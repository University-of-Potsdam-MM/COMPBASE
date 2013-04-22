package algorithm.phase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.model.RemoveAxiom;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import util.OntologyException;
import util.ReasonableOntology;
import util.ResultingTimeable;
import util.Stop;
import util.TickerTimeInterval;
import util.Timeable;
import algorithm.phase.shared.OWLUtil;

public class TickerMergePhase implements Timeable {

	private static final int[] MINUTE_MODIFIER = { 0, -1, 1 };

	private final ReasonableOntology ontology;

	public TickerMergePhase(ReasonableOntology ontology) {
		this.ontology = ontology;
	}

	@Override
	public void time() {
		// Alle TimeIntervals der beiden Ticker in je einer Liste, sortiert nach
		// Minuten
		Map<Integer, Collection<TickerTimeInterval>>[] timeIntervals = this
				.sortTimeIntervalsByTicker();
		// Derzeit werden nur zwei Ticker unterstützt, das lässt sich aber
		// leicht ändern
		if (timeIntervals.length < 2)
			throw new OntologyException(
					"Es werden mindestens zwei Ticker benötigt");
		Map<Integer, Collection<TickerTimeInterval>> sourceMap = timeIntervals[0];
		Map<Integer, Collection<TickerTimeInterval>> targetMap = timeIntervals[1];
		// Merge-Vorgang nach Minuten
		// Eine Minute davor und danach berücksichtigen
		// Nach jedem Schritt Konsistenz sicherstellen
		MinuteDependentIntervals delegate = new NearIntervals(targetMap);
		mergeSourceWithTarget(sourceMap, delegate);

		// Alle noch nicht verknüpften Tickermeldungen schrittweise verknüpfen
		// Nach jedem Schritt Konsistenz sicherstellen
		delegate = new AllValues(targetMap);
		mergeSourceWithTarget(sourceMap, delegate);
	}

	private void mergeSourceWithTarget(
			Map<Integer, Collection<TickerTimeInterval>> sourceMap,
			MinuteDependentIntervals minuteDelegate) {
		// Durch alle Minuten der Quelle durchgehen
		for (Integer key : sourceMap.keySet()) {
			if (key == null)
				continue;
			Collection<TickerTimeInterval> sourceTimeIntervals = sourceMap
					.get(key);
			Iterator<TickerTimeInterval> sourceIterator = sourceTimeIntervals
					.iterator();
			// Durch alle TimeIntervals der Minute durchgehen
			while (sourceIterator.hasNext()) {
				TickerTimeInterval sourceTimeInterval = sourceIterator.next();
				boolean foundTarget = false;
				// Alle passenden Ziel-TimeIntervals durchgehen
				for (Collection<TickerTimeInterval> observedIntervals : minuteDelegate
						.getDependentIntervals(key)) {
					if (foundTarget)
						break;
					// Verknüpfung von Quell-TimeInterval und Ziel-TimeIntervals
					// versuchen
					foundTarget = mergeSourceWithTargetCollection(
							sourceTimeInterval, foundTarget, observedIntervals);
				}

				if (foundTarget)
					sourceIterator.remove();
			}
		}
	}

	private boolean mergeSourceWithTargetCollection(
			TickerTimeInterval sourceTimeInterval, boolean foundTarget,
			Collection<TickerTimeInterval> observedIntervals) {
		for (TickerTimeInterval targetInterval : observedIntervals) {
			String message = "Prüfe " + sourceTimeInterval.getInterval()
					+ " und " + targetInterval.getInterval();
			ApplyAndCheck apply = new ApplyAndCheck(sourceTimeInterval,
					targetInterval, ontology);
			foundTarget = Stop.watch(message, apply);

			if (!foundTarget) {
				Stop.watch("Ungleich", new DoNothing());
			} else {
				Stop.watch("Gleich", new DoNothing());
				observedIntervals.remove(targetInterval);
				break;
			}
		}
		return foundTarget;
	}

	private Map<Integer, Collection<TickerTimeInterval>>[] sortTimeIntervalsByTicker() {
		OWLReasoner reasoner = this.ontology.getReasoner();
		OWLDataFactory dataFactory = this.ontology.getDataFactory();
		PrefixManager prefixManager = this.ontology.getPrefixManager();

		OWLClass tickerClass = dataFactory.getOWLClass("Ticker", prefixManager);
		OWLClass timeIntervalClass = dataFactory.getOWLClass("TimeInterval",
				prefixManager);
		OWLObjectProperty hasPublished = dataFactory.getOWLObjectProperty(
				"hasPublished", prefixManager);

		NodeSet<OWLNamedIndividual> tickerNodeSet = reasoner.getInstances(
				tickerClass, false);
		int tickerNodeCount = 0;
		Iterator<Node<OWLNamedIndividual>> tickerIterator = tickerNodeSet
				.iterator();
		while (tickerIterator.hasNext()) {
			tickerIterator.next();
			tickerNodeCount++;
		}

		@SuppressWarnings("unchecked")
		Map<Integer, Collection<TickerTimeInterval>>[] result = new Map[tickerNodeCount];
		for (int count = 0; count < tickerNodeCount; count++)
			result[count] = new HashMap<Integer, Collection<TickerTimeInterval>>();

		int currentTicker = 0;
		for (Node<OWLNamedIndividual> tickerNode : tickerNodeSet) {
			OWLNamedIndividual tickerRepresentative = tickerNode
					.getRepresentativeElement();
			NodeSet<OWLNamedIndividual> publishedNodeSet = reasoner
					.getObjectPropertyValues(tickerRepresentative, hasPublished);
			for (Node<OWLNamedIndividual> publishedNode : publishedNodeSet) {
				OWLNamedIndividual publishedRepresentative = publishedNode
						.getRepresentativeElement();
				if (!reasoner.getTypes(publishedRepresentative, false)
						.containsEntity(timeIntervalClass))
					continue;
				// Wir haben es mit einem TimeInterval zu tun
				int minute = OWLUtil.extractMinute(publishedRepresentative,
						dataFactory, prefixManager, reasoner);
				// Passende Collection raussuchen und Element hinzufügen
				Collection<TickerTimeInterval> minuteCollection;
				if (result[currentTicker].containsKey(minute))
					minuteCollection = result[currentTicker].get(minute);
				else {
					minuteCollection = new ArrayList<TickerTimeInterval>();
					result[currentTicker].put(minute, minuteCollection);
				}
				minuteCollection.add(new TickerTimeInterval(
						publishedRepresentative, dataFactory));
			}
			currentTicker++;
		}

		return result;
	}

	private interface MinuteDependentIntervals {
		public Iterable<Collection<TickerTimeInterval>> getDependentIntervals(
				int minute);
	}

	private class NearIntervals implements MinuteDependentIntervals {
		private final Map<Integer, Collection<TickerTimeInterval>> contentMap;

		public NearIntervals(
				Map<Integer, Collection<TickerTimeInterval>> contentMap) {
			this.contentMap = contentMap;
		}

		@Override
		public Iterable<Collection<TickerTimeInterval>> getDependentIntervals(
				int minute) {
			Collection<Collection<TickerTimeInterval>> values = new ArrayList<Collection<TickerTimeInterval>>();
			for (int count = 0; count < MINUTE_MODIFIER.length; count++) {
				int observedMinute = minute + MINUTE_MODIFIER[count];
				if (!contentMap.containsKey(observedMinute))
					continue;
				values.add(contentMap.get(observedMinute));
			}
			return values;
		}
	}

	private class AllValues implements MinuteDependentIntervals {
		private final Map<Integer, Collection<TickerTimeInterval>> contentMap;

		public AllValues(Map<Integer, Collection<TickerTimeInterval>> contentMap) {
			this.contentMap = contentMap;
		}

		@Override
		public Iterable<Collection<TickerTimeInterval>> getDependentIntervals(
				int minute) {
			return this.contentMap.values();
		}
	}

	private class ApplyAndCheck implements ResultingTimeable<Boolean> {
		private final TickerTimeInterval sourceTimeInterval;
		private final TickerTimeInterval targetInterval;
		private final ReasonableOntology ontology;

		private boolean foundTarget;

		public ApplyAndCheck(TickerTimeInterval sourceTimeInterval,
				TickerTimeInterval targetInterval, ReasonableOntology ontology) {
			this.sourceTimeInterval = sourceTimeInterval;
			this.targetInterval = targetInterval;
			this.ontology = ontology;
		}

		@Override
		public void time() {
			OWLAxiom sameAxiom = sourceTimeInterval
					.createSameAxiom(targetInterval);
			this.addAxiom(sameAxiom);

			if (!this.ontology.isConsistent()) {
				this.removeAxiom(sameAxiom);
				this.foundTarget = false;
			} else {
				this.foundTarget = true;
			}
		}

		private void addAxiom(OWLAxiom axiom) {
			AddAxiom add = new AddAxiom(this.ontology.getOntology(), axiom);
			this.ontology.getOntologyManager().applyChange(add);
		}

		private void removeAxiom(OWLAxiom axiom) {
			RemoveAxiom remove = new RemoveAxiom(this.ontology.getOntology(),
					axiom);
			this.ontology.getOntologyManager().applyChange(remove);
		}

		@Override
		public Boolean getResult() {
			return this.foundTarget;
		}
	}

	private class DoNothing implements Timeable {

		@Override
		public void time() {
		}
	}
}
