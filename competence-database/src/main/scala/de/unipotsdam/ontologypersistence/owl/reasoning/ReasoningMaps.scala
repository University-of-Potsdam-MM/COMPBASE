package de.unipotsdam.ontologypersistence.owl.reasoning

import de.unipotsdam.ontologypersistence.owl.ontology.OntClasses
import de.unipotsdam.ontologypersistence.owl.ontology.OntObjectProperties
import de.unipotsdam.ontologypersistence.owl.access.MagicStrings

object ReasoningMaps {
  /**
   * returns (?a objectProperty ?b)
   */
  def op2reasonableString(objectProperty: OntObjectProperties): String = {
    return "(?a " + MagicStrings.PREFIX + objectProperty.name() + " ?b)";
  }

  /**
   * returns (?c rdfs:subClassOf className)
   */
  def op2reasonableString(className: OntClasses): String = {
    return "(?c rdfs:subClassOf " + MagicStrings.PREFIX + className.name() + ")";
  }

}