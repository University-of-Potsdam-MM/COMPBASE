package algorithm.phase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import util.OntologyException;
import util.ReasonableOntology;
import util.ResultingTimeable;
import util.Stop;
import util.TickerSplitOntology;
import util.Timeable;

public class ConnectionPhase implements ResultingTimeable<TickerSplitOntology> {

	private TickerSplitOntology ontology;

	public ConnectionPhase(ReasonableOntology ontology) {
		this.ontology = new TickerSplitOntology(ontology);
	}

	@Override
	public void time() {
		// Spielstart / -ende suchen und verknüpfen
		Stop.watch("Spielstart / -ende suchen und verknüpfen",
				new ConnectStartEndPoints(ontology));

		// Konsistenz sichern
		if (!ontology.isConsistent())
			throw new OntologyException("Ontologie inkonsistent");
	}

	@Override
	public TickerSplitOntology getResult() {
		return this.ontology;
	}

	private class ConnectStartEndPoints implements Timeable {
		private static final String ANPFIFF = "AnpfiffErsteHalbzeit";
		private static final String ABPFIFF = "AbpfiffZweiteHalbzeit";
		private static final String ENDE_ERSTE_HALBZEIT = "AbpfiffErsteHalbzeit";
		private static final String ANFANG_ZWEITE_HALBZEIT = "AnpfiffZweiteHalbzeit";

		private ReasonableOntology ontology;

		public ConnectStartEndPoints(ReasonableOntology ontology) {
			this.ontology = ontology;
		}

		@Override
		public void time() {
			PrefixManager prefixManager = this.ontology.getPrefixManager();
			OWLDataFactory dataFactory = this.ontology.getDataFactory();

			OWLObjectProperty tsHasTimeSlice = dataFactory
					.getOWLObjectProperty("tsHasTimeSlice", prefixManager);
			OWLObjectProperty tsTimeInterval = dataFactory
					.getOWLObjectProperty("tsTimeInterval", prefixManager);

			OWLClass anpfiff = dataFactory.getOWLClass(ANPFIFF, prefixManager);
			OWLClass abpfiff = dataFactory.getOWLClass(ABPFIFF, prefixManager);
			OWLClass halbzeitende = dataFactory.getOWLClass(
					ENDE_ERSTE_HALBZEIT, prefixManager);
			OWLClass halbzeitanfang = dataFactory.getOWLClass(
					ANFANG_ZWEITE_HALBZEIT, prefixManager);

			// Spielstarts suchen
			connectGivenInstances(anpfiff, tsHasTimeSlice, tsTimeInterval,
					dataFactory);

			// Spielenden suchen
			connectGivenInstances(abpfiff, tsHasTimeSlice, tsTimeInterval,
					dataFactory);

			// Halbzeitende suchen
			connectGivenInstances(halbzeitende, tsHasTimeSlice, tsTimeInterval,
					dataFactory);

			// Halbzeitanfang suchen
			connectGivenInstances(halbzeitanfang, tsHasTimeSlice,
					tsTimeInterval, dataFactory);

			// Tore suchen
			connectEqualGoalScores(dataFactory);
		}

		private void connectEqualGoalScores(OWLDataFactory dataFactory) {
			OWLReasoner reasoner = this.ontology.getReasoner();
			PrefixManager prefixManager = this.ontology.getPrefixManager();
			OWLClass goalClass = dataFactory.getOWLClass(
					"SuccessfulShotOnGoal", this.ontology.getPrefixManager());
			OWLDataProperty newScore = dataFactory.getOWLDataProperty(
					"newScore", prefixManager);

			Map<String, OWLNamedIndividual> scoreMap = new HashMap<String, OWLNamedIndividual>();
			NodeSet<OWLNamedIndividual> goals = reasoner.getInstances(
					goalClass, false);
			for (Node<OWLNamedIndividual> goal : goals) {
				OWLNamedIndividual representative = goal
						.getRepresentativeElement();
				Set<OWLLiteral> scoreValues = reasoner.getDataPropertyValues(
						representative, newScore);
				if (scoreValues.size() > 0) {
					OWLLiteral score = scoreValues.toArray(new OWLLiteral[0])[0];
					String scoreString = score.toString();

					if (scoreMap.containsKey(scoreString)) {
						// Falls der Wert schon existiert -> Individuen
						// verknüpfen
						OWLNamedIndividual same = scoreMap.get(scoreString);
						OWLAxiom sameAxiom = dataFactory
								.getOWLSameIndividualAxiom(same, representative);
						this.ontology.getOntologyManager().addAxiom(
								this.ontology.getOntology(), sameAxiom);
					} else {
						// Andernfalls: Individuum hinzufügen
						scoreMap.put(scoreString, representative);
					}
				}
			}
		}

		private void connectGivenInstances(OWLClass individualsClass,
				OWLObjectProperty tsHasTimeSlice,
				OWLObjectProperty tsTimeInterval, OWLDataFactory dataFactory) {
			OWLReasoner reasoner = this.ontology.getReasoner();

			Collection<OWLNamedIndividual> sameTimeIntervals = new ArrayList<OWLNamedIndividual>();
			NodeSet<OWLNamedIndividual> individuals = reasoner.getInstances(
					individualsClass, false);
			for (Node<OWLNamedIndividual> individual : individuals) {
				NodeSet<OWLNamedIndividual> timeSliceIndividuals = reasoner
						.getObjectPropertyValues(
								individual.getRepresentativeElement(),
								tsHasTimeSlice);
				for (Node<OWLNamedIndividual> timeSliceIndividual : timeSliceIndividuals) {
					NodeSet<OWLNamedIndividual> timeIntervalIndividuals = reasoner
							.getObjectPropertyValues(timeSliceIndividual
									.getRepresentativeElement(), tsTimeInterval);
					for (Node<OWLNamedIndividual> timeIntervalIndividual : timeIntervalIndividuals)
						sameTimeIntervals.add(timeIntervalIndividual
								.getRepresentativeElement());
				}
			}

			OWLAxiom axiom = dataFactory
					.getOWLSameIndividualAxiom(new HashSet<OWLNamedIndividual>(
							sameTimeIntervals));
			AddAxiom addAxiom = new AddAxiom(ontology.getOntology(), axiom);
			ontology.getOntologyManager().applyChange(addAxiom);
		}
	}
}
