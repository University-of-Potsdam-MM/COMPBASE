package de.unipotsdam.ontologypersistence.owl.dao

import com.hp.hpl.jena.ontology.Individual
import com.hp.hpl.jena.rdf.model.Property
import com.hp.hpl.jena.rdf.model.Statement

import de.unipotsdam.ontologypersistence.owl.access.MagicStrings
import de.unipotsdam.ontologypersistence.owl.access.OntResult
import de.unipotsdam.ontologypersistence.owl.access.OntologyAccess
import de.unipotsdam.ontologypersistence.owl.access.OntologyManager
import de.unipotsdam.ontologypersistence.owl.ontology.OntClasses

abstract case class CompetenceOntologySingletonDao(ontManager: OntologyManager, val compOntClass: OntClasses, val identifier: String = null) extends Dao(ontManager) {
  val util = ontManager.getUtil()

  def persist(more: Boolean): OntResult = {
    var result: OntResult = null
    if (identifier == null) {
      result = util.accessSingletonResourceWithClass(compOntClass)
    } else {
      result = util.accessSingletonResource(identifier)
    }
    if (more) {
      persistMore
    }
    return result
  }

  /**
   * needs this override, because the definition is not placed at the level of the individual but the corresponding class
   */
  @Override
  def getPropertyPair(key: String): (Property, Statement) = {
    val literal = ontManager.getM().createProperty(OntologyAccess.encode(key));
    val prop: Statement = persist(false).getOntclass().getProperty(literal);
    return (literal, prop)
  }

  def createIndividual: Individual = {
    return persist(false).getIndividual()
  }

  def exists(): Boolean = {
    return util.getIndividualForString(MagicStrings.SINGLETONPREFIX + compOntClass.name()) != null
  }

  def getId: String = {
    return createIndividual.getLocalName()
  }
}