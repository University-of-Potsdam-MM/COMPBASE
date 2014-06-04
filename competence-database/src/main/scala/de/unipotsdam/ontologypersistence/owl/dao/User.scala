package de.unipotsdam.ontologypersistence.owl.dao

import de.unipotsdam.ontologypersistence.owl.access.OntologyManager
import de.unipotsdam.ontologypersistence.owl.ontology.OntClasses
import de.unipotsdam.ontologypersistence.owl.ontology.OntObjectProperties
import de.unipotsdam.ontologypersistence.owl.queries.OntologyQueries

case class User(comp: OntologyManager, val name: String, val role: Role = null, val courseContext: CourseContext = null, val readableName: String = null) extends CompetenceOntologyDao(comp, OntClasses.User, name) {

  def NAME = "name"

  @Override
  protected def deleteMore() {
    role.deleteEdge(OntObjectProperties.RoleOf, this)
  }

  @Override
  protected def persistMore() {
    val thisIndividual = createIndividual
    if (courseContext != null) {
      createEdgeWith(OntObjectProperties.belongsToCourseContext, courseContext)
    }
    if (role != null) {
      role.createEdgeWith(OntObjectProperties.RoleOf, this)
    }
    if (readableName != null) {
      addDataField(NAME, readableName)
    } else {
      addDataField(NAME, identifierBeforeParsing)
    }
  }

  @Override
  def getFullDao(): User = {
    val teacherRole = new TeacherRole(comp)
    val queries = new OntologyQueries(comp.getM())
    val courseContext2 = getAssociatedStandardDaosAsRange(OntObjectProperties.belongsToCourseContext, classOf[CourseContext]).map(x => x.getFullDao.asInstanceOf[CourseContext]).head;
    val readableName = getDataField(NAME)
    if (hasEdge(teacherRole, OntObjectProperties.RoleOf)) {
      return new User(comp, name, teacherRole, courseContext2, readableName)
    } else {
      return new User(comp, name, new StudentRole(comp), courseContext2, readableName)
    }
  }

  def getAssociatedLinks(): List[AbstractEvidenceLink] = {
    return getAssociatedStandardDaosAsRange(OntObjectProperties.UserOfLink, classOf[AbstractEvidenceLink])
  }
}