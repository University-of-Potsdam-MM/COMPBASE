package de.unipotsdam.ontologypersistence.owl.dao

import de.unipotsdam.ontologypersistence.owl.access.OntologyManager
import de.unipotsdam.ontologypersistence.owl.ontology.OntClasses
import de.unipotsdam.ontologypersistence.owl.ontology.OntObjectProperties

case class Comment(comp: OntologyManager, val text: String, val creator: User = null, val created: java.lang.Long = null, val textReadable : String = null) extends CompetenceOntologyDao(comp, OntClasses.Comment, text) {

  def DATECRATED = "datecreated"
  def TEXT = "text"

  @Override
  protected def deleteMore() {
    //TODO

  }

  @Override
  protected def persistMore() {
    creator.persist
    createEdgeWith(creator, OntObjectProperties.UserOfComment)    
    addDataField(TEXT, identifierBeforeParsing)
    if (created == null) {
    	addDataField(DATECRATED, System.currentTimeMillis(): java.lang.Long);
    } else {
      addDataField(DATECRATED, created);
    }
  }

  def getFullDao(): Comment = {
    val creatorNew = getAssociatedStandardDaosAsDomain(OntObjectProperties.UserOfComment, classOf[User]).head
    val textNew = getDataField(TEXT)
    val dateCreatedNew = getDataFieldLong(DATECRATED)
    return new Comment(comp, text, creatorNew, dateCreatedNew, textNew)
  }
}