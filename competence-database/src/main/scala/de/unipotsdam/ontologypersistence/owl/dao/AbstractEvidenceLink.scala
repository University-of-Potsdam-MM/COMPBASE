package de.unipotsdam.ontologypersistence.owl.dao

import de.unipotsdam.ontologypersistence.owl.access.OntologyManager
import de.unipotsdam.ontologypersistence.owl.ontology.OntClasses
import de.unipotsdam.ontologypersistence.owl.ontology.OntObjectProperties

case class AbstractEvidenceLink(
  val comp: OntologyManager,
  val identifier2: String,
  val creator: User = null,
  val linkedUser: User = null,
  val courseContexts: CourseContext = null,
  val comments: List[Comment] = null,
  val evidenceActivity: EvidenceActivity = null,
  val dateCreated: java.lang.Long = null,
  val isValidated: java.lang.Boolean = null,
  val competence: Competence = null) extends CompetenceOntologyDao(comp, OntClasses.AbstractEvidenceLink, identifier2) {

  def CREATED = "datecreated"
  def ISVALIDATED = "isValidated"

  @Override
  protected def deleteMore() {
    if (comments != null) {
      comments.foreach(x => x.delete)
    }
  }

  @Override
  protected def persistMore() {
    if (comments != null) {
      linkComments(comments)
    }
    linkedUser.persist
    createEdgeWith(linkedUser, OntObjectProperties.UserOfLink)
    creator.persist
    createEdgeWith(OntObjectProperties.createdBy, creator)
    courseContexts.persist
    createEdgeWith(OntObjectProperties.LinkOfCourseContext, courseContexts)
    evidenceActivity.persist
    createEdgeWith(evidenceActivity, OntObjectProperties.ActivityOf)
    competence.persist(false)
    createEdgeWith(OntObjectProperties.linksCompetence, competence)
    addDataField(CREATED, dateCreated)
    addDataField(ISVALIDATED, isValidated)
  }

  def linkComments(comments: List[Comment]) {
    comments.foreach(linkComment)
  }

  def linkComment(comment: Comment) {
    comment.persist
    createEdgeWith(comment, OntObjectProperties.CommentOf)
  }

  @Override
  def getFullDao(): AbstractEvidenceLink = {
    val comments = getAssociatedStandardDaosAsDomain(OntObjectProperties.CommentOf, classOf[Comment])
    val creator = getAssociatedStandardDaosAsRange(OntObjectProperties.createdBy, classOf[User]).head
    val courseContext = getAssociatedStandardDaosAsRange(OntObjectProperties.LinkOfCourseContext, classOf[CourseContext])
    val evidenceActivity = getAssociatedStandardDaosAsDomain(OntObjectProperties.ActivityOf, classOf[EvidenceActivity])
    return new AbstractEvidenceLink(comp, identifier, creator.getFullDao, null, null, comments, null, getDataFieldLong(CREATED), getDataFieldBoolean(ISVALIDATED), null)
  }

  def getAllLinkedUsers(): List[User] = {
    return getAssociatedStandardDaosAsDomain(OntObjectProperties.UserOfLink, classOf[User]).map(x => x.getFullDao.asInstanceOf[User])
  }

  def getAllActivities(): List[EvidenceActivity] = {
    return getAssociatedStandardDaosAsDomain(OntObjectProperties.ActivityOf, classOf[EvidenceActivity]).map(x => x.getFullDao.asInstanceOf[EvidenceActivity])
  }

  def getAllCourseContexts(): List[CourseContext] = {
    return getAssociatedStandardDaosAsRange(OntObjectProperties.LinkOfCourseContext, classOf[CourseContext]).map(x => x.getFullDao.asInstanceOf[CourseContext])
  }

  def getAllLinkedCompetences(): List[Competence] = {
    return getAssociatedSingletonDaosAsRange(OntObjectProperties.linksCompetence, classOf[Competence]).map(x => x.getFullDao.asInstanceOf[Competence])
  }

}