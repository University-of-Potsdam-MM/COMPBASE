package de.unipotsdam.ontologypersistence.owl.dao

import de.unipotsdam.ontologypersistence.owl.access.OntologyManager
import de.unipotsdam.ontologypersistence.owl.ontology.OntClasses

case class EvidenceActivity(ontManager: OntologyManager, val url: String, val printableName: String = null) extends CompetenceOntologyDao(ontManager, OntClasses.EvidenceActivity, url) {

  def URL = "url"
  def PRINTABLENAME = "printableName"

  @Override
  protected def deleteMore() {
    //TODO
  }

  @Override
  protected def persistMore() {
    addDataField(URL, url)
    addDataField(PRINTABLENAME, printableName)
  }

  def getFullDao(): EvidenceActivity = {
    if (getDataField(PRINTABLENAME) != null && getDataField(URL) != null) {
      return new EvidenceActivity(ontManager, getDataField(PRINTABLENAME), getDataField(URL))
    } else {
      return this;
    }
  }
}