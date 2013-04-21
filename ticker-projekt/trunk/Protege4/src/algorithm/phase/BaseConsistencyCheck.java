package algorithm.phase;

import java.util.Set;

import org.semanticweb.HermiT.Reasoner.ReasonerFactory;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import util.OntologyException;
import util.ReasonableOntology;
import util.ResultingTimeable;
import util.Stop;
import util.Timeable;
import algorithm.phase.shared.CheckReasoningConsistency;

public class BaseConsistencyCheck implements Timeable {
	private final OWLOntology ontology;
	private final OWLDataFactory dataFactory;
	private final PrefixManager prefixManager;
	private ReasonableOntology reasonableOntology;
	private OWLOntologyManager ontologyManager;

	public BaseConsistencyCheck(OWLOntology ontology,
			OWLDataFactory dataFactory, PrefixManager prefixManager,
			OWLOntologyManager manager) {
		this.ontology = ontology;
		this.dataFactory = dataFactory;
		this.prefixManager = prefixManager;
		this.ontologyManager = manager;
	}

	@Override
	public void time() {
		// Reasoner erstellen
		OWLReasoner reasoner = Stop.watch("Reasoner erstellen",
				new CreateReasoner(ontology, ontologyManager));

		// Resultat erstellen
		CheckReasoningConsistency checkReasoningConsistency = new CheckReasoningConsistency(
				reasoner);
		this.reasonableOntology = new ReasonableOntology(ontology, reasoner,
				this.prefixManager, this.dataFactory, this.ontologyManager,
				checkReasoningConsistency);

		// Initiales Reasoning anwerfen
		EnsureReasoningConsistency checkConsistency = new EnsureReasoningConsistency(
				checkReasoningConsistency);
		Stop.watch("Initiale Konsistenz überprüfen", checkConsistency);

		// TimeInterval- / TimeSlice-Consistency
		CheckTimeIntervals checkTimeIntervals = new CheckTimeIntervals(
				reasoner, dataFactory, prefixManager);
		Stop.watch("TimeInterval- / TimeSlice-Consistency überprüfen",
				checkTimeIntervals);

		// Ticker- / Publisher-Consistency?
	}

	public ReasonableOntology getOntology() {
		return this.reasonableOntology;
	}

	private class CreateReasoner implements ResultingTimeable<OWLReasoner> {
		private OWLOntology ontology;
		private OWLReasoner reasoner;
		private OWLOntologyManager ontologyManager;

		public CreateReasoner(OWLOntology ontology,
				OWLOntologyManager ontologyManager) {
			this.ontology = ontology;
			this.ontologyManager = ontologyManager;
		}

		@Override
		public void time() {
			// PelletReasoner reasoner = PelletReasonerFactory.getInstance()
			// .createNonBufferingReasoner(ontology);
			OWLReasoner reasoner = new ReasonerFactory()
					.createNonBufferingReasoner(ontology);
			this.reasoner = reasoner;
		}

		@Override
		public OWLReasoner getResult() {
			return reasoner;
		}
	}

	private class EnsureReasoningConsistency implements Timeable {
		private CheckReasoningConsistency checkReasoningConsistency;

		public EnsureReasoningConsistency(
				CheckReasoningConsistency checkReasoningConsistency) {
			this.checkReasoningConsistency = checkReasoningConsistency;
		}

		@Override
		public void time() {
			Stop.watch("Konsistenzsicherung", checkReasoningConsistency);
			if (!this.checkReasoningConsistency.getResult())
				throw new OntologyException("Ontologie ist nicht konsistent");
		}
	}

	private class CheckTimeIntervals implements Timeable {
		private OWLReasoner reasoner;
		private OWLDataFactory dataFactory;
		private PrefixManager prefixManager;

		public CheckTimeIntervals(OWLReasoner reasoner,
				OWLDataFactory dataFactory, PrefixManager prefixManager) {
			this.reasoner = reasoner;
			this.dataFactory = dataFactory;
			this.prefixManager = prefixManager;
		}

		@Override
		public void time() {
			OWLClass timeInterval = dataFactory.getOWLClass("TimeInterval",
					prefixManager);
			OWLClass gameActionSlice = dataFactory.getOWLClass(
					"GameActionSlice", prefixManager);
			OWLClass ticker = dataFactory.getOWLClass("Ticker", prefixManager);
			OWLClass timeSlice = dataFactory.getOWLClass("TimeSlice",
					prefixManager);

			OWLObjectProperty tsMinute = dataFactory.getOWLObjectProperty(
					"tsMinute", prefixManager);

			OWLObjectProperty tsTimeIntervalOf = dataFactory
					.getOWLObjectProperty("tsTimeIntervalOf", prefixManager);
			OWLObjectProperty publishedBy = dataFactory.getOWLObjectProperty(
					"publishedBy", prefixManager);
			OWLObjectProperty tsTimeInterval = dataFactory
					.getOWLObjectProperty("tsTimeInterval", prefixManager);

			Set<OWLNamedIndividual> gameActionSliceIndividuals = reasoner
					.getInstances(gameActionSlice, false).getFlattened();
			Set<OWLNamedIndividual> tickerIndividuals = reasoner.getInstances(
					ticker, false).getFlattened();

			NodeSet<OWLNamedIndividual> timeSlices = reasoner.getInstances(
					timeSlice, true);
			for (Node<OWLNamedIndividual> timeSliceNode : timeSlices) {
				OWLNamedIndividual timeSliceInstance = timeSliceNode
						.getRepresentativeElement();
				Set<OWLNamedIndividual> timeIntervals = reasoner
						.getObjectPropertyValues(timeSliceInstance,
								tsTimeInterval).getFlattened();
				if (timeIntervals.size() == 0)
					throw new OntologyException(
							"Kein TimeInterval zugeordnet bei "
									+ timeSliceInstance);
			}

			NodeSet<OWLNamedIndividual> timeIntervalIndividuals = reasoner
					.getInstances(timeInterval, true);
			// Durch alle TimeInterval-Individuen durchgehen
			for (Node<OWLNamedIndividual> timeIntervalNode : timeIntervalIndividuals) {
				OWLNamedIndividual representative = timeIntervalNode
						.getRepresentativeElement();

				checkHasEventTime(representative, tsMinute);
				checkHasGameActionSlice(representative, tsTimeIntervalOf,
						gameActionSliceIndividuals);
				checkHasOneTicker(representative, publishedBy,
						tickerIndividuals);
			}
		}

		private void checkHasOneTicker(OWLNamedIndividual representative,
				OWLObjectProperty publishedBy,
				Set<OWLNamedIndividual> tickerIndividuals) {
			// Ist ein Ticker zugeordnet?
			NodeSet<OWLNamedIndividual> publishers = reasoner
					.getObjectPropertyValues(representative, publishedBy);
			int tickerCounter = 0;
			for (Node<OWLNamedIndividual> publisherNode : publishers) {
				OWLNamedIndividual publisher = publisherNode
						.getRepresentativeElement();
				if (tickerIndividuals.contains(publisher))
					tickerCounter++;
			}

			if (tickerCounter == 0)
				throw new OntologyException("Kein Ticker zugeordnet bei "
						+ representative);
			if (tickerCounter > 1)
				throw new OntologyException(
						"Mehr als ein Ticker zugeordnet bei " + representative);
		}

		private void checkHasGameActionSlice(OWLNamedIndividual representative,
				OWLObjectProperty tsTimeIntervalOf,
				Set<OWLNamedIndividual> gameActionSliceIndividuals) {
			// Ist eine GameActionSlice zugeordnet?
			NodeSet<OWLNamedIndividual> timeSlices = reasoner
					.getObjectPropertyValues(representative, tsTimeIntervalOf);
			boolean hasGameActionSlice = false;
			for (Node<OWLNamedIndividual> timeSliceNode : timeSlices) {
				OWLNamedIndividual timeSlice = timeSliceNode
						.getRepresentativeElement();
				hasGameActionSlice |= gameActionSliceIndividuals
						.contains(timeSlice);
			}

			if (!hasGameActionSlice)
				throw new OntologyException("Keine GameAction zugeordnet bei "
						+ representative);
		}

		private void checkHasEventTime(OWLNamedIndividual representative,
				OWLObjectProperty tsMinute) {
			// Ist eine Zeit zugeordnet?
			Set<OWLNamedIndividual> eventTimes = reasoner
					.getObjectPropertyValues(representative, tsMinute)
					.getFlattened();

			if (eventTimes.size() != 1)
				throw new OntologyException("Keine Zeit zugeordnet bei "
						+ representative);
		}
	}
}
