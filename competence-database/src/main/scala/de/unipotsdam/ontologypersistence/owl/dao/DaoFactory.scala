package de.unipotsdam.ontologypersistence.owl.dao

import de.unipotsdam.ontologypersistence.owl.access.OntologyManager

object DaoFactory {
  def getAbstractEvidenceDao(comp: OntologyManager, identifier: String): AbstractEvidenceLink = {
    return new AbstractEvidenceLink(comp, identifier).getFullDao
  }

}