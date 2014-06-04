package de.unipotsdam.ontologypersistence.owl.dao

import de.unipotsdam.ontologypersistence.owl.ontology.OntClasses
import de.unipotsdam.ontologypersistence.owl.access.OntologyManager

class TeacherRole(comp: OntologyManager) extends Role(comp, OntClasses.TeacherRole) {
  @Override
  protected def persistMore() {
    val role = new RoleInstance(comp)
    role.persist(false)
    addSuperClass(role)
  }

  @Override
  def getFullDao(): Dao = {
    return this
  }
}