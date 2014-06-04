package de.unipotsdam.ontologypersistence.owl.dao

import de.unipotsdam.ontologypersistence.owl.access.OntologyManager
import de.unipotsdam.ontologypersistence.owl.ontology.OntClasses

class CompetenceInstance(ontManager: OntologyManager) extends CompetenceOntologySingletonDao(ontManager, OntClasses.Competence) {
  @Override
  protected def persistMore() {
  }

  @Override
  def getFullDao(): Dao = {
    return this
  }
}