package uzuzjmd.competence.mapper.rest.read

import uzuzjmd.competence.owl.access.TDBReadConverter
import uzuzjmd.competence.shared.dto.Activity
import uzuzjmd.competence.owl.access.CompOntologyManager
import uzuzjmd.competence.owl.dao.Competence
import uzuzjmd.competence.owl.dao.EvidenceActivity
import scala.collection.JavaConversions._

/**
 * @author dehne
 */
object Ont2ActivitiesForCompetence extends TDBReadConverter[String, java.util.List[Activity]] {
  override def doRead(comp: CompOntologyManager, changes: String): java.util.List[Activity] = {
    val competence = new Competence(comp, changes)
    return competence.getActivitiesFor().map(x => new Activity(x.url, x.printableName)).toList
  }

}