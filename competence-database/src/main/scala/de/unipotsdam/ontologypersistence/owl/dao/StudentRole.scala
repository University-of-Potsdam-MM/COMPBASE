package de.unipotsdam.ontologypersistence.owl.dao

import de.unipotsdam.ontologypersistence.owl.access.OntologyManager
import de.unipotsdam.ontologypersistence.owl.ontology.OntClasses

class StudentRole(ontManager: OntologyManager) extends Role(ontManager, OntClasses.StudentRole) {

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