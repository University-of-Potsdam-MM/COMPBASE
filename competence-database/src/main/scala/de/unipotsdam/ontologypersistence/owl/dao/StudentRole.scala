package de.unipotsdam.ontologypersistence.owl.dao

import de.unipotsdam.ontologypersistence.owl.access.OntologyManager
import de.unipotsdam.ontologypersistence.owl.ontology.OntClasses

class StudentRole(comp: OntologyManager) extends Role(comp, OntClasses.StudentRole) {

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