package de.unipotsdam.ontologypersistence.owl.access;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;

public class OntResult {
	private Individual individual;

	private OntClass ontclass;

	public OntResult(Individual individual, OntClass ontclass) {
		super();
		this.individual = individual;
		this.ontclass = ontclass;
	}

	public Individual getIndividual() {
		return individual;
	}

	public OntClass getOntclass() {
		return ontclass;
	}
	public void setIndividual(Individual individual) {
		this.individual = individual;
	}

	public void setOntclass(OntClass ontclass) {
		this.ontclass = ontclass;
	}
}
