package de.unipotsdam.ontologypersistence.owl.dao

import com.hp.hpl.jena.ontology.Individual
import com.hp.hpl.jena.rdf.model.Property
import com.hp.hpl.jena.rdf.model.Statement

import de.unipotsdam.ontologypersistence.owl.access.OntologyAccess
import de.unipotsdam.ontologypersistence.owl.access.OntologyManager
import de.unipotsdam.ontologypersistence.owl.ontology.OntObjectProperties
import de.unipotsdam.ontologypersistence.owl.queries.OntologyQueries
import uzuzjmd.scalahacks.ScalaHacks
import uzuzjmd.scalahacks.ScalaHacksInScala

abstract class Dao(comp: OntologyManager) {
  def createIndividual: Individual;
  def getId: String;
  def getPropertyPair(key: String): (Property, Statement)
  protected def persistMore()

  def getFullDao(): Dao

  def exists(): Boolean;

  def createEdgeWith(edgeType: OntObjectProperties, range: Dao) {
    val domainIndividual = createIndividual
    val rangeIndividual = range.createIndividual
    comp.getUtil().createObjectPropertyWithIndividual(domainIndividual, rangeIndividual, edgeType)
  }

  def createEdgeWith(domain: Dao, edgeType: OntObjectProperties) {
    val domainIndividual = domain.createIndividual
    val rangeIndividual = createIndividual
    comp.getUtil().createObjectPropertyWithIndividual(domainIndividual, rangeIndividual, edgeType)
  }

  def deleteEdgeWith(domain: Dao, edgeType: OntObjectProperties) {
    val domainIndividual = domain.createIndividual
    val rangeIndividual = createIndividual
    comp.getUtil().deleteObjectPropertyWithIndividual(domainIndividual, rangeIndividual, edgeType)
  }

  def deleteEdge(edgeType: OntObjectProperties, range: Dao) {
    val domainIndividual = createIndividual
    val rangeIndividual = range.createIndividual
    comp.getUtil().deleteObjectPropertyWithIndividual(domainIndividual, rangeIndividual, edgeType)
  }

  def hasEdge(edgeType: OntObjectProperties, range: Dao): Boolean = {
    if (this.exists && range.exists) {
      val domainIndividual = createIndividual
      val rangeIndividual = range.createIndividual
      val result = comp.getUtil().existsObjectPropertyWithIndividual(domainIndividual, rangeIndividual, edgeType)
      return result
    } else { return false }
  }

  def hasEdge(domain: Dao, edgeType: OntObjectProperties): Boolean = {
    if (this.exists && domain.exists) {
      val domainIndividual = domain.createIndividual
      val rangeIndividual = createIndividual
      val result = comp.getUtil().existsObjectPropertyWithIndividual(domainIndividual, rangeIndividual, edgeType)
      return result
    } else { return false }
  }

  def addSuperClass(superClass: CompetenceOntologySingletonDao) {
    if (superClass != null) {
      val ontSuperClass = superClass.persist(true).getOntclass()
      createIndividual.getOntClass().addSuperClass(ontSuperClass)
    }
  }

  def addDataField(key: String, value: Object) {
    deleteDataField(key)
    val literal = comp.getM().createProperty(OntologyAccess.encode(key));
    //    createIndividual.removeAll(literal)
    createIndividual.addLiteral(literal, value);
  }
  def getDataField(key: String): String = {
    val tmpResult = getPropertyPair(key)
    if (tmpResult._2 == null) {
      return null;
    }
    return tmpResult._2.asTriple().getObject().getLiteralLexicalForm();
  }

  def getDataFieldBoolean(key: String): java.lang.Boolean = {
    val tmpResult = getPropertyPair(key)
    if (tmpResult._2 == null) {
      return null;
    }
    return tmpResult._2.getBoolean();
  }

  def getDataFieldLong(key: String): java.lang.Long = {
    val tmpResult = getPropertyPair(key)
    if (tmpResult._2 == null) {
      return null;
    }
    return tmpResult._2.getLong();
  }

  def getDataFieldInt(key: String): java.lang.Integer = {
    val tmpResult = getPropertyPair(key)
    if (tmpResult._2 == null) {
      return null;
    }
    return tmpResult._2.getInt();
  }

  def deleteDataField(key: String) = {
    val tmpResult = getPropertyPair(key)
    if (tmpResult._2 != null) {
      comp.getM().remove(tmpResult._2);
    }
  }

  def hasDataField(key: String): Boolean = {
    return getDataField(key) != null
  }

  private def getAssociatedIndividuals(edgeType: OntObjectProperties, range: Dao): List[Individual] = {
    val hacks = new ScalaHacks;
    val individualDummy = hacks.getIndividualArray()
    val queries = new OntologyQueries(comp.getM())
    return queries.getRelatedIndividuals(edgeType, range.getId).toArray(individualDummy).toList
  }

  private def getAssociatedIndividuals(domain: Dao, edgeType: OntObjectProperties): List[Individual] = {
    val hacks = new ScalaHacks;
    val individualDummy = hacks.getIndividualArray()
    val queries = new OntologyQueries(comp.getM())
    return queries.getRelatedIndividualsDomainGiven(domain.getId, edgeType).toArray(individualDummy).toList
  }

  /**
   * assumes that alle dao classes have a constructor available that only takes the ontclass and the manager
   */
  protected def getAssociatedStandardDaosAsDomain[T <: CompetenceOntologyDao](edgeType: OntObjectProperties, clazz: java.lang.Class[T]): List[T] = {
    val assocIndividuals = getAssociatedIndividuals(edgeType, this)
    val result = assocIndividuals.map(x => ScalaHacksInScala.instantiateDao(clazz)(comp, x.getLocalName()).asInstanceOf[T]).map(x => x.getFullDao)
    return result.asInstanceOf[List[T]]
  }

  /**
   * assumes that alle dao classes have a constructor available that only takes the ontclass and the manager
   */
  protected def getAssociatedSingletonDaosAsDomain[T <: CompetenceOntologySingletonDao](edgeType: OntObjectProperties, clazz: java.lang.Class[T]): List[T] = {
    val assocIndividuals = getAssociatedIndividuals(edgeType, this)
    val result = assocIndividuals.map(x => ScalaHacksInScala.instantiateDao(clazz)(comp, x.getOntClass().getLocalName()).asInstanceOf[T]).map(x => x.getFullDao)
    return result.asInstanceOf[List[T]]
  }

  /**
   * assumes that alle dao classes have a constructor available that only thakes the ontclass and the manager
   */
  protected def getAssociatedStandardDaosAsRange[T <: CompetenceOntologyDao](edgeType: OntObjectProperties, clazz: java.lang.Class[T]): List[T] = {
    val ontClasses = getAssociatedIndividuals(this, edgeType)
    val result = ontClasses.map(x => ScalaHacksInScala.instantiateDao(clazz)(comp, x.getLocalName()).asInstanceOf[T]).map(x => x.getFullDao)
    return result.asInstanceOf[List[T]]
  }

  /**
   * assumes that alle dao classes have a constructor available that only thakes the ontclass and the manager
   */
  protected def getAssociatedSingletonDaosAsRange[T <: CompetenceOntologySingletonDao](edgeType: OntObjectProperties, clazz: java.lang.Class[T]): List[T] = {
    val ontClasses = getAssociatedIndividuals(this, edgeType)
    val result = ontClasses.map(x => ScalaHacksInScala.instantiateDao(clazz)(comp, x.getOntClass().getLocalName()).asInstanceOf[T]).map(x => x.getFullDao)
    return result.asInstanceOf[List[T]]
  }

}