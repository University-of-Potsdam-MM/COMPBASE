package util;

import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import algorithm.phase.shared.CheckReasoningConsistency;

public class ReasonableOntology {

	private CheckReasoningConsistency checkReasoningConsistency;
	private OWLOntology ontology;
	private OWLReasoner reasoner;
	private PrefixManager prefixManager;
	private OWLDataFactory dataFactory;

	public ReasonableOntology(OWLOntology ontology, OWLReasoner reasoner,
			PrefixManager prefixManager, OWLDataFactory dataFactory,
			OWLOntologyManager ontologyManager,
			CheckReasoningConsistency checkReasoningConsistency) {
		this.ontology = ontology;
		this.reasoner = reasoner;
		this.prefixManager = prefixManager;
		this.dataFactory = dataFactory;
		this.checkReasoningConsistency = checkReasoningConsistency;
	}

	public ReasonableOntology(ReasonableOntology ontology) {
		this(ontology.getOntology(), ontology.getReasoner(), ontology
				.getPrefixManager(), ontology.getDataFactory(), ontology
				.getOntologyManager(), ontology.getConsistencyChecker());
	}

	public boolean isConsistent() {
		Stop.watch("Konsistenzüberprüfung", checkReasoningConsistency);
		return checkReasoningConsistency.getResult();
	}

	public OWLOntology getOntology() {
		return this.ontology;
	}

	public CheckReasoningConsistency getConsistencyChecker() {
		return this.checkReasoningConsistency;
	}

	public OWLReasoner getReasoner() {
		return this.reasoner;
	}

	public PrefixManager getPrefixManager() {
		return this.prefixManager;
	}

	public OWLDataFactory getDataFactory() {
		return this.dataFactory;
	}

	public OWLOntologyManager getOntologyManager() {
		return this.ontology.getOWLOntologyManager();
	}
}
