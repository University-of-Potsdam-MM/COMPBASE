package uzuzjmd.competence.mapper.gui

import uzuzjmd.competence.owl.ontology.CompObjectProperties
import uzuzjmd.competence.owl.access.CompOntologyManager
import uzuzjmd.competence.gui.client.shared.dto.HierarchieChangeSet
import scala.collection.JavaConverters._
import uzuzjmd.competence.gui.client.shared.dto.HierarchieChange
import uzuzjmd.competence.owl.dao.Competence

object HierarchieChangesToOnt {
  def convertChanges(comp: CompOntologyManager, changes: HierarchieChangeSet) {
    changes.getElements().asScala.foreach(convertChange(comp, _))
  }

  def convertChange(comp: CompOntologyManager, change: HierarchieChange) {
    val selectedCompetence = new Competence(comp, change.getNodeSelected(), change.getNodeSelected())
    val oldSuperClass = new Competence(comp, change.getOldClass(), change.getOldClass())
    val newSuperClass = new Competence(comp, change.getNewClass(), change.getNewClass())

    selectedCompetence.addSuperCompetence(newSuperClass)
    selectedCompetence.removeSuperCompetence(oldSuperClass)
  }
}