package uzuzjmd.competence.mapper.rest.read

import java.util.LinkedList

import scala.collection.JavaConverters.asScalaBufferConverter

import uzuzjmd.competence.owl.access.CompOntologyAccessScala
import uzuzjmd.competence.owl.access.CompOntologyManager
import uzuzjmd.competence.owl.access.TDBREADTransactional
import uzuzjmd.competence.owl.ontology.CompObjectProperties
import uzuzjmd.competence.owl.queries.CompetenceQueries

/**
 * @author dehne
 */
object Ont2SelectedCompetencesForCourse extends TDBREADTransactional[String, Array[String]] {
  def convert(changes: String): Array[String] = {
    execute(convertHelper _, changes)
  }

  def convertHelper(comp: CompOntologyManager, changes: String): Array[String] = {
    val queries = new CompetenceQueries(comp.getM());
    val result = new LinkedList[String]();
    val competenceIndividuals = queries.getRelatedIndividualsDomainGiven(changes, CompObjectProperties.CourseContextOf);
    val it = competenceIndividuals.iterator()
    while (it.hasNext) {
      val competenceIndividual = it.next();
      val competenceClass = competenceIndividual.getOntClass(true);
      result.add(CompOntologyAccessScala.getDefinitionString(competenceClass, comp));
    }
    return result.asScala.toArray
  }

}