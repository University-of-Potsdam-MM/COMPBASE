package uzuzjmd.competence.owl.dao

import uzuzjmd.competence.owl.access.CompOntologyManager
import uzuzjmd.competence.owl.ontology.CompOntClass
import uzuzjmd.competence.owl.ontology.CompObjectProperties
import uzuzjmd.competence.owl.access.CompOntologyAccess
import uzuzjmd.competence.owl.queries.CompetenceQueries
import uzuzjmd.scalahacks.ScalaHacks
import com.hp.hpl.jena.ontology.Individual

case class User(comp: CompOntologyManager, val name: String, val role: Role = null, val courseContext: CourseContext = null) extends CompetenceOntologyDao(comp, CompOntClass.User, name) {

  def NAME = "name"

  addDataField(NAME, name)

  @Override
  protected def deleteMore() {
    role.deleteEdge(CompObjectProperties.RoleOf, this)
  }

  @Override
  protected def persistMore() {
    val thisIndividual = createIndividual
    if (courseContext != null) {
      createEdgeWith(CompObjectProperties.belongsToCourseContext, courseContext)
    }
    if (role != null) {
      role.createEdgeWith(CompObjectProperties.RoleOf, this)
    }
    if (name != null) {
      addDataField(NAME, name)
    }
  }

  @Override
  def getFullDao(): User = {
    val teacherRole = new TeacherRole(comp)
    val queries = new CompetenceQueries(comp.getM())
    val courseContext2 = getAssociatedStandardDaosAsRange(CompObjectProperties.belongsToCourseContext, classOf[CourseContext]).map(x => x.getFullDao.asInstanceOf[CourseContext]).head;
    //    val courseContext2 = associatedContexts match {
    //      case null => null
    //      case Nil => null
    //      case default => default.head
    //    }
    if (hasEdge(teacherRole, CompObjectProperties.RoleOf)) {
      return new User(comp, name, teacherRole, courseContext2)
    } else {
      return new User(comp, name, new StudentRole(comp), courseContext2)
    }
  }

  def getAssociatedLinks(): List[AbstractEvidenceLink] = {
    return getAssociatedStandardDaosAsRange(CompObjectProperties.UserOfLink, classOf[AbstractEvidenceLink])
  }
}