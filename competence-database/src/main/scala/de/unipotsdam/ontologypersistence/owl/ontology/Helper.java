package de.unipotsdam.ontologypersistence.owl.ontology;

import de.unipotsdam.ontologypersistence.owl.access.OntologyManager;

public class Helper {
	public OntologyManager getOntologyManagerInit() {
		OntologyManager compOntologyManager = new OntologyManager();
		compOntologyManager.begin();
		return compOntologyManager;
	}
}
