package uzuzjmd.competence.owl.dao

import uzuzjmd.competence.owl.ontology.CompOntClass
import uzuzjmd.competence.owl.access.CompOntologyManager
import uzuzjmd.competence.owl.access.CompOntologyAccess
import uzuzjmd.competence.owl.dao.exceptions.IndividualNotFoundException
import uzuzjmd.competence.owl.ontology.CompObjectProperties

case class EvidenceActivity(comp: CompOntologyManager, val url: String, val printableName: String = null) extends CompetenceOntologyDao(comp, CompOntClass.EvidenceActivity, url) {

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

    try {
      val hasPrintableName = getDataField(PRINTABLENAME) != null;
      val hasURL = getDataField(URL) != null;
      return new EvidenceActivity(comp, getDataField(URL), getDataField(PRINTABLENAME))
    } catch {
      case e: IndividualNotFoundException =>
        return this
    }

  }

  def addCompetenceFor(dao: Competence) {
    createEdgeWith(CompObjectProperties.ActivityForComptence, dao);
  }
}