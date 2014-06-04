package de.unipotsdam.ontologypersistence.owl.reasoning;

import com.hp.hpl.jena.rdf.listeners.ChangedListener;
import com.hp.hpl.jena.rdf.model.Statement;

import de.unipotsdam.ontologypersistence.owl.access.OntologyManager;

public class ModelChangeListener extends ChangedListener {

	private SimpleRulesReasoner rulesReasoner;
	private OntologyManager manager;

	public ModelChangeListener(SimpleRulesReasoner rulesReasoner, OntologyManager manager) {
		this.rulesReasoner = rulesReasoner;
		this.manager = manager;
	}

	@Override
	public void addedStatement(Statement s) {
		super.addedStatement(s);
		rulesReasoner.reason();
	}
}
