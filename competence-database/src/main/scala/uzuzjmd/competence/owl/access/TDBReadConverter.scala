package uzuzjmd.competence.owl.access

/**
 * @author dehne
 *
 * Utility Class to wrap calling the execute function with helper function
 */
trait TDBReadConverter[I, O] extends TDBREADTransactional[I, O] {

  def convert(changes: I): O = {
    return execute(convertHelper _, changes)
  }

  def convertHelper(comp: CompOntologyManager, changes: I): O = {
    return doRead(comp, changes)
  }

  def doRead(comp: CompOntologyManager, changes: I): O

}