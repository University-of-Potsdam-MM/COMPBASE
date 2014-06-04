package de.unipotsdam.ontologypersistence.owl.dao

import de.unipotsdam.ontologypersistence.owl.access.OntologyManager

object DaoFactory {
  def getAbstractEvidenceDao(ontManager: OntologyManager, identifier: String): AbstractEvidenceLink = {
    return new AbstractEvidenceLink(ontManager, identifier).getFullDao
  }

}