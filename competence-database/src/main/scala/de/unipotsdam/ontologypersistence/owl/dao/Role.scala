package de.unipotsdam.ontologypersistence.owl.dao

import de.unipotsdam.ontologypersistence.owl.access.OntologyManager
import de.unipotsdam.ontologypersistence.owl.ontology.OntClasses

abstract class Role(ontManager: OntologyManager, compOntClass: OntClasses) extends CompetenceOntologySingletonDao(ontManager, compOntClass) {

}