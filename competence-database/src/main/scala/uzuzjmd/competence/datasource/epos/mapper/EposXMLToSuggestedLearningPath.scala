package uzuzjmd.competence.datasource.epos.mapper

import uzuzjmd.competence.datasource.epos.filter.LevelFilter
import uzuzjmd.competence.persistence.dao.{Catchword, Competence, LearningProjectTemplate}
import uzuzjmd.competence.shared.{DESCRIPTORSETType, DESCRIPTORType}

import scala.collection.JavaConverters._
import scala.collection.mutable

/**
  * Generates learning paths implied by levels in the epos xml format
  */
object EposXMLToSuggestedLearningPath {

  def convertLevelsToOWLRelations(descriptorSetType: java.util.List[DESCRIPTORSETType]) {
    descriptorSetType.asScala.foreach(x => convertLevelsToOWLRelations2(x.getDESCRIPTOR().asScala.toList))
    descriptorSetType.asScala.foreach(x => createTemplateAssociation(x))
  }

  def convertLevelsAndLearningGoalToTemplate(descriptorSetType: java.util.List[DESCRIPTORSETType]) {
    descriptorSetType.asScala.foreach(x => createTemplateAssociation(x))
  }

  def createTemplateAssociation(x: DESCRIPTORSETType) {
    val templateName = x.getNAME()
    val competences: mutable.Buffer[Competence] = x.getDESCRIPTOR().asScala.map(EposXML2FilteredCSVCompetence.descriptorSetType2Id).map(x => new Competence(x, false).persist()).map(x => x.asInstanceOf[Competence])
    competences.foreach(competence => competence.addCatchword(new Catchword(EposXML2FilteredCSVCompetence.identifier2Definition(competence.getDefinition))));
    val learningProjectTemplate = new LearningProjectTemplate(templateName, competences.toList.asJava)
    learningProjectTemplate.persistMore()
  }

  def convertLevelsToOWLRelations2(descriptorSetType: List[DESCRIPTORType]) {
    val result = descriptorSetType.map(x => (x.getCOMPETENCE(), x.getLEVEL())).distinct.combinations(2)
    result.foreach(x => convertLevelToOWLRelations3((x.head._1, x.head._2), (x.tail.head._1, x.tail.head._2)))
  }

  def convertLevelToOWLRelations3(domain: (String, String), range: (String, String)) {
    if (LevelFilter.filterSuggestedLevels(domain._2, range._2) && domain._1.equals(range._1)) {
      //println("create link " + domain + " " + range);
      val domainId = EposXML2FilteredCSVCompetence.descriptorSetPair2Id(domain);
      val rangeID = EposXML2FilteredCSVCompetence.descriptorSetPair2Id(range);
      val domainCompetence = new Competence(domainId, false)
      domainCompetence.persist()
      val rangeCompetence = new Competence(rangeID, false)
      rangeCompetence.persist()
      rangeCompetence.addSuggestedCompetenceRequirement(domainCompetence)
    }
  }
}