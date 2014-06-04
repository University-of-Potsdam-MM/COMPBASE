package de.unipotsdam.ontologypersistence.owl.access;

public class OntologyManagerFactory {
	public static OntologyManager startManager() {
		OntologyManager compOntologyManager = new OntologyManager();
		compOntologyManager.begin();
		compOntologyManager.getM().enterCriticalSection(false);
		return compOntologyManager;
	}
}
