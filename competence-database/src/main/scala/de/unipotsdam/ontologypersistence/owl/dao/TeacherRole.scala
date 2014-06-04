package de.unipotsdam.ontologypersistence.owl.dao

import de.unipotsdam.ontologypersistence.owl.ontology.OntClasses
import de.unipotsdam.ontologypersistence.owl.access.OntologyManager

class TeacherRole(ontManager: OntologyManager) extends Role(ontManager, OntClasses.TeacherRole) {
  @Override
  protected def persistMore() {
    val role = new RoleInstance(ontManager)
    role.persist(false)
    addSuperClass(role)
  }

  @Override
  def getFullDao(): Dao = {
    return this
  }
}