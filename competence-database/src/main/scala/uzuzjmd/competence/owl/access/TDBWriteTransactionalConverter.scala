package uzuzjmd.competence.owl.access

/**
 * @author dehne
 */
trait TDBWriteTransactionalConverter[X] extends TDBWriteTransactional[X] {
  def convert(changes: X) {
    execute(convertHelper _, changes)
  }

  def convertHelper(comp: CompOntologyManager, changes: X) {
    doWrite(comp, changes)
  }

  def doWrite(comp: CompOntologyManager, changes: X)
}