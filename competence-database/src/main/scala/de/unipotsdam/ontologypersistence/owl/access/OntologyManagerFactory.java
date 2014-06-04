package de.unipotsdam.ontologypersistence.owl.access;

public class OntologyManagerFactory {
	public static OntologyManager startManager() {
		OntologyManager ontManager = new OntologyManager();
		ontManager.begin();
		ontManager.getOntModel().enterCriticalSection(false);
		return ontManager;
	}
}
