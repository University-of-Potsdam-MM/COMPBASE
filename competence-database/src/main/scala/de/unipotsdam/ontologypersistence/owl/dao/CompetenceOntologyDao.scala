package de.unipotsdam.ontologypersistence.owl.dao

import com.hp.hpl.jena.ontology.Individual
import com.hp.hpl.jena.rdf.model.Property
import com.hp.hpl.jena.rdf.model.Statement

import de.unipotsdam.ontologypersistence.owl.access.OntologyAccess
import de.unipotsdam.ontologypersistence.owl.access.OntologyManager
import de.unipotsdam.ontologypersistence.owl.ontology.OntClasses

abstract class CompetenceOntologyDao(ontManager: OntologyManager, ontologyClass: OntClasses, val identifier: String) extends Dao(ontManager) {
  val util = ontManager.getUtil()
  val identifierBeforeParsing = identifier
  val encodedstring = identifier.trim().replaceAll("[^a-zA-ZäöüÄÖÜß1-9]", "_").replaceAll("[\u0000-\u001f]", "").replaceAll("\\.", "__").replaceAll("[\n\r]", "").replaceAll("[\n]", "").replaceAll("_", "");

  @Override
  def getPropertyPair(key: String): (Property, Statement) = {
    val literal = ontManager.getOntModel().createProperty(OntologyAccess.encode(key));
    val prop: Statement = createIndividual.getProperty(literal);
    return (literal, prop)
  }

  def persist(): Individual = {
    val result = createIndividual
    persistMore()
    return result
  }

  def delete {
    if (exists) {
      deleteMore()
      val individual = util.getIndividualForString(encodedstring)
      individual.remove()
    }
  }

  protected def deleteMore()

  def exists(): Boolean = {
    val result = util.getIndividualForString(encodedstring)
    return result != null
  }

  def createIndividual: Individual = {
    val ontClass = util.createOntClass(ontologyClass)

    return util.createIndividualForString(ontClass, encodedstring)
  }

  def getId: String = {
    return encodedstring;
  }

}