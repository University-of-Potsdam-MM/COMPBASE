package uzuzjmd.competence.mapper.gui

import scala.collection.JavaConverters._
import uzuzjmd.competence.owl.access.CompOntologyManager
import uzuzjmd.competence.owl.ontology.CompOntClass
import uzuzjmd.competence.owl.access.MagicStrings
import com.hp.hpl.jena.ontology.OntProperty
import scala.collection.mutable.Buffer
import uzuzjmd.competence.service.rest.dto.CompetenceXMLTree
import uzuzjmd.competence.service.rest.dto.OperatorXMLTree
import uzuzjmd.competence.service.rest.dto.CatchwordXMLTree

/**
 * Diese Klasse mappt die Kompetenzen auf einen Baum, der in GWT-anzeigbar ist
 */
class Ont2CompetenceTree(ontologyManager: CompOntologyManager) {
  def getComptenceTree(): java.util.List[CompetenceXMLTree] = {
    ontologyManager.begin()

    val subCompetences = getSubClassesOfCompetence()
    val iProperty = ontologyManager.getM.getOntProperty(MagicStrings.PREFIX + "definition")
    val definitions = subCompetences.map(x => x.getPropertyValue(iProperty))
    val result = definitions.filterNot(_ == null).map(x => new CompetenceXMLTree(x.asNode().getLiteralValue().toString(), "Kompetenz", "", null));
    ontologyManager.close()

    // todo implement
    return result.toList.asJava
  }

  def getSubClassesOfCompetence(): Buffer[com.hp.hpl.jena.ontology.OntClass] = {
    val competenceClass = ontologyManager.getUtil().getClass(CompOntClass.Competence);
    val competences = competenceClass.listSubClasses().toList().asScala
    return competences
  }

  def getOperatorXMLTree(): java.util.List[OperatorXMLTree] = {

    return null
  }

  def getCatchwordXMLTree(): java.util.List[CatchwordXMLTree] = {
    return null
  }

}