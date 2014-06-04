package de.unipotsdam.ontologypersistence.owl.dao

import de.unipotsdam.ontologypersistence.owl.access.OntologyAccessScala
import de.unipotsdam.ontologypersistence.owl.access.OntologyManager
import de.unipotsdam.ontologypersistence.owl.ontology.OntClasses
import de.unipotsdam.ontologypersistence.owl.ontology.OntObjectProperties

case class CourseContext(comp: OntologyManager, var name: String) extends CompetenceOntologyDao(comp, OntClasses.CourseContext, OntologyAccessScala.convertMoodleIdToName(name)) {
  name = OntologyAccessScala.convertMoodleIdToName(name)

  @Override
  protected def deleteMore() {
    //TODO
  }

  @Override
  protected def persistMore() {

  }

  def getFullDao(): CompetenceOntologyDao = {
    return this
  }

  def getLinkedCompetences(): List[Competence] = {
    return getAssociatedSingletonDaosAsRange(OntObjectProperties.CourseContextOf, classOf[Competence])
  }

  def getLinkedUser(): List[User] = {
    return getAssociatedStandardDaosAsDomain(OntObjectProperties.belongsToCourseContext, classOf[User])
  }

  def getMoodleId(): java.lang.Integer = {
    return Integer.parseInt(name.replace("n", ""))
  }

}