package uzuzjmd.competence.mapper.rest.write

import uzuzjmd.competence.shared.dto.CompetenceForActivityData
import uzuzjmd.competence.owl.access.CompOntologyManager
import uzuzjmd.competence.owl.access.TDBWriteTransactionalConverter
import uzuzjmd.competence.owl.dao.Competence
import uzuzjmd.competence.shared.dto.Activity
import uzuzjmd.competence.owl.dao.EvidenceActivity
import scala.collection.JavaConverters.asScalaBufferConverter

/**
 * @author dehne
 */
object ActivityForCompetence2Ont extends TDBWriteTransactionalConverter[CompetenceForActivityData] {

  override def doWrite(comp: CompOntologyManager, changes: CompetenceForActivityData) {

    val competence = new Competence(comp, changes.getCompetence)
    competence.persist(false)

    val activity = new EvidenceActivity(comp, changes.getActivity.getUrl, changes.getActivity.getShortname)
    activity.persist()

    activity.addCompetenceFor(competence)
  }

}