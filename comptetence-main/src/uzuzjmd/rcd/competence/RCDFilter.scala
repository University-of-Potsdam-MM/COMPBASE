package uzuzjmd.rcd.competence

import uzuzjmd.owl.competence.ontology.CompObjectProperties

object RCDFilter {
  /**
   * // Titel, Statementname, Statementtext bzw.
   *  _1 KompetenzIndividual _2 (Title), ObjectProperty, _3 Individual related to that ObjectProperty i.e. OperatorIndividual to OperatorOf
   */
  type CompetenceTriple = (String, String, String)
  /**
   *   suboperatortriples: x._1 competence, x._2 operator,  x._3 suboperator,
   */
  type OperatorTriple = (String, String, String)
  type CompetenceFilter = CompetenceTriple => Boolean

  def isObjectPropertyTriple(triple: CompetenceTriple): Boolean = {
    CompObjectProperties.values.map(x => x.name()).contains(triple._2)
  }

  def isSubClassTriple(triple: CompetenceTriple): Boolean = {
    CompObjectProperties.SubCompetenceOf.equals(CompObjectProperties.valueOf(triple._2))
  }

  def isSubOperatorTriple(triple: CompetenceTriple): Boolean = {
    CompObjectProperties.SubOperatorOf.equals(CompObjectProperties.valueOf(triple._2))
  }

  def isMetaCatchwordOfTriple(triple: CompetenceTriple): Boolean = {
    CompObjectProperties.MetaCatchwordOf.equals(CompObjectProperties.valueOf(triple._2))
  }

  def isDescriptionElementOfTriple(triple: CompetenceTriple): Boolean = {
    CompObjectProperties.DescriptionElementOf.equals(CompObjectProperties.valueOf(triple._2))
  }

  def isTripleWithBlanc(triple: CompetenceTriple): Boolean = {
    triple match {
      //      case (null, egal, egal2) => return false
      //      case (egal, null, egal2) => return false
      //      case (egal, egal2, null) => return false
      case ("", egal, egal2) => return true
      case (egal, "", egal2) => return true
      case (egal, egal2, "") => return true
      case (egal, egal2, egal3) => return false
    }
  }

}