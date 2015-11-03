package uzuzjmd.competence.owl.dao

import uzuzjmd.competence.owl.access.CompOntologyManager
import uzuzjmd.competence.owl.ontology.CompObjectProperties
import uzuzjmd.competence.owl.ontology.CompOntClass
import com.hp.hpl.jena.rdf.model.Property
import uzuzjmd.competence.owl.access.CompOntologyAccess
import com.hp.hpl.jena.rdf.model.Statement
import com.hp.hpl.jena.ontology.OntClass
import scala.collection.JavaConverters._
import uzuzjmd.competence.owl.dao.exceptions.DataFieldNotInitializedException
import uzuzjmd.competence.owl.access.CompOntologyAccessScala

class Competence(compManager: CompOntologyManager, identifierlocal: String, val definition: String = null, val compulsory: java.lang.Boolean = null) extends CompetenceOntologySingletonDao(compManager, CompOntClass.Competence, identifierlocal) {

  def COMPULSORY = "compulsory"

  @Override
  protected def persistMore() {
    val competenceRoot = new CompetenceInstance(comp)
    val ontClass = persist(false).getOntclass()
    ontClass.addSuperClass(competenceRoot.persist(true).getOntclass())
    if (definition != null) {
      addDataField(DEFINITION, definition)
      //      compManager.getUtil().createOntClassForString(definition, definition)
    } else {
      addDataField(DEFINITION, identifierlocal)
    }
    if (compulsory == null) {
      addDataField(COMPULSORY, new java.lang.Boolean(false))
    } else {
      addDataField(COMPULSORY, compulsory)
    }
  }

  @Override
  def getFullDao(): Competence = {
    val definition2 = getDefinition
    return new Competence(compManager, identifier, definition2, getDataFieldBoolean(COMPULSORY))
  }

  //  def getIdentifier(): String = {
  //    return identifier
  //  }

  def addSuggestedCompetenceRequirement(competence: Competence) {
    createEdgeWith(competence, CompObjectProperties.SuggestedCompetencePrerequisiteOf)
  }

  def getSuggestedCompetenceRequirements(): List[Competence] = {
    return getAssociatedSingletonDaosAsDomain(CompObjectProperties.SuggestedCompetencePrerequisiteOf, classOf[Competence])
  }

  def addRequiredCompetence(competence: Competence) {
    deleteEdgeWith(competence, CompObjectProperties.NotPrerequisiteOf)
    createEdgeWith(competence, CompObjectProperties.PrerequisiteOf)
  }

  def addNotRequiredCompetence(competence: Competence) {
    deleteEdgeWith(competence, CompObjectProperties.PrerequisiteOf)
    createEdgeWith(competence, CompObjectProperties.NotPrerequisiteOf)
  }

  def getCatchwords(): List[Catchword] = {
    val result = getAssociatedSingletonDaosAsDomain(CompObjectProperties.CatchwordOf, classOf[Catchword])
    //val result = getAssociatedSingletonDaosAsRange(CompObjectProperties.CatchwordOfInverse, classOf[Catchword])
    return result
  }

  def getRequiredCompetences(): List[Competence] = {
    return getAssociatedSingletonDaosAsDomain(CompObjectProperties.PrerequisiteOf, classOf[Competence])
  }

  def getRequiredCompetencesAsArray(): Array[String] = {
    return getRequiredCompetences().map(x => x.getDataField(x.DEFINITION)).toArray;
  }

  def isLinkedAsRequired(): Boolean = {
    return !(getRequiredCompetences.isEmpty && getAssociatedSingletonDaosAsRange(CompObjectProperties.PrerequisiteOf, classOf[Competence]).isEmpty);
  }

  def isAllowed(): Boolean = {
    return getAssociatedSingletonDaosAsDomain(CompObjectProperties.PrerequisiteOf, classOf[Competence]).isEmpty
  }

  def addSuperCompetence(superCompetence: Competence): Competence = {
    persist(false).getOntclass().addSuperClass(superCompetence.persist(true).getOntclass())
    persist(false)
    return this
  }

  def removeSuperCompetence(superCompetence: Competence): Competence = {
    persist(false).getOntclass().removeSuperClass(superCompetence.persist(true).getOntclass())
    persist(false)
    return this
  }

  def toStrinz(): String = {
    return getDefinition()
  }

  def getAssessment(user: User): SelfAssessment = {
    val result1 = getAssociatedStandardDaosAsDomain(CompObjectProperties.AssessmentOfCompetence, classOf[SelfAssessment])
    val result = result1.filter(x => x.hasEdge(CompObjectProperties.AssessmentOfUser, user))
    if (result.isEmpty) {
      return new SelfAssessment(compManager, CompOntologyAccessScala.createIdentifierForAssessment(user, this), this, user, 0, false)
    }
    return result.head
  }

  def getShortestPathToSubCompetence(subCompetence: Competence): java.util.List[Competence] = {
    return compManager.getUtil().getShortestSubClassPath(subCompetence.toOntClass, toOntClass).asScala.map(x => new Competence(compManager, x, null, null)).asJava
  }

  def getCatchwordsAsJava(): java.util.List[Catchword] = {
    return getCatchwords.asJava
  }

  def addCatchword(dao: Catchword) {
    dao.persist(true)
    dao.createEdgeWith(CompObjectProperties.CatchwordOf, this)
  }

  def addLearningTemplate(learningTemplate: LearningProjectTemplate) {
    createEdgeWith(learningTemplate, CompObjectProperties.LearningProjectTemplateOf)
  }

  def getOperators(): List[Operator] = {
    return getAssociatedSingletonDaosAsDomain(CompObjectProperties.OperatorOf, classOf[Operator]);
  }

  def addCourseContext(course: CourseContext) {
    createEdgeWith(course, CompObjectProperties.CourseContextOf)
    addSuperCompetencesToCourse(this, course)
  }

  def getActivitiesFor(): List[EvidenceActivity] = {
    return getAssociatedStandardDaosAsDomain(CompObjectProperties.ActivityForComptence, classOf[EvidenceActivity])
  }

  private def addSuperCompetencesToCourse(competence: Competence, course: CourseContext) {
    if (competence.hasSuperClass) {
      val superCompetences = competence.listSuperClasses(classOf[Competence]).filterNot { x => x.identifier.equals("Competence") }.filterNot { x => x.identifier.equals("Resource") }
      superCompetences.foreach { x => x.createEdgeWith(course, CompObjectProperties.CourseContextOf) }
      superCompetences.foreach { x => addSuperCompetencesToCourse(x, course) }
    }
  }

}