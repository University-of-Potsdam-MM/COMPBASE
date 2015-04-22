package uzuzjmd.competence.owl.dao

import uzuzjmd.competence.owl.access.CompOntologyManager

class UniversityContext(comp: CompOntologyManager) extends CourseContext(comp, "university") {
  def toStrinz: String = {
    return "university"
  }

}