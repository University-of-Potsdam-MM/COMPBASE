package de.unipotsdam.ontologypersistence.owl.access

object OntologyAccessScala {
  def getDefinitionString(subclass: com.hp.hpl.jena.ontology.OntClass, ontologyManager: OntologyManager): String = {
    if (getPropertyString(subclass, "definition", ontologyManager) != null) {
      return (getPropertyString(subclass, "definition", ontologyManager).toString().replaceAll("[\n\r]", "")).replaceAll("[\n]", "")
    } else {
      return ""
    }
  }
  private def getPropertyString(subclass: com.hp.hpl.jena.ontology.OntClass, propertyName: String, ontologyManager: OntologyManager): Object = {
    val iProperty = ontologyManager.getOntModel.getOntProperty(MagicStrings.PREFIX + propertyName)
    val value = subclass.getPropertyValue(iProperty)
    if (value == null) { return null }
    else { return value.asNode().getLiteralValue() }
  }

  def convertMoodleIdToName(name: String): String = {
    if (name.startsWith("1") || name.startsWith("2") ||
      name.startsWith("3") || name.startsWith("4") ||
      name.startsWith("5") || name.startsWith("6") ||
      name.startsWith("7")
      || name.startsWith("8") || name.startsWith("9")) {
      return "n" + name
    } else {
      return name;
    }
  }
}