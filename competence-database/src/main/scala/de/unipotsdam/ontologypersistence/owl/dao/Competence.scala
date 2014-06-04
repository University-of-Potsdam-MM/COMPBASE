package de.unipotsdam.ontologypersistence.owl.dao

import de.unipotsdam.ontologypersistence.owl.access.OntologyManager
import de.unipotsdam.ontologypersistence.owl.ontology.OntClasses
import de.unipotsdam.ontologypersistence.owl.ontology.OntObjectProperties

class Competence(compManager: OntologyManager, identifier: String, val definition: String = null, val compulsory: java.lang.Boolean = null) extends CompetenceOntologySingletonDao(compManager, OntClasses.Competence, identifier) {

  def DEFINITION = "definition"
  def COMPULSORY = "compulsory"

  @Override
  protected def persistMore() {
    val competenceRoot = new CompetenceInstance(comp)
    persist(false).getOntclass().addSuperClass(competenceRoot.persist(false).getOntclass())
  }

  @Override
  def getFullDao(): Competence = {
    return new Competence(compManager, identifier, getDataField(DEFINITION), getDataFieldBoolean(COMPULSORY))
  }

  def addRequiredCompetence(competence: Competence) {
    deleteEdgeWith(competence, OntObjectProperties.NotPrerequisiteOf)
    createEdgeWith(competence, OntObjectProperties.PrerequisiteOf)
  }

  def addNotRequiredCompetence(competence: Competence) {
    deleteEdgeWith(competence, OntObjectProperties.PrerequisiteOf)
    createEdgeWith(competence, OntObjectProperties.NotPrerequisiteOf)
  }

  def getRequiredCompetences(): List[Competence] = {
    return getAssociatedSingletonDaosAsDomain(OntObjectProperties.PrerequisiteOf, classOf[Competence])
  }

  def getRequiredCompetencesAsArray(): Array[String] = {
    return getRequiredCompetences().map(x => x.getDataField(x.definition)).toArray;
  }

  def isLinkedAsRequired(): Boolean = {
    return !(getRequiredCompetences.isEmpty && getAssociatedSingletonDaosAsRange(OntObjectProperties.PrerequisiteOf, classOf[Competence]).isEmpty);
  }

}