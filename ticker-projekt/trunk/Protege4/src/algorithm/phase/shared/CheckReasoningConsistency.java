package algorithm.phase.shared;

import org.semanticweb.owlapi.reasoner.OWLReasoner;

import util.ResultingTimeable;

public class CheckReasoningConsistency implements ResultingTimeable<Boolean> {
	private OWLReasoner reasoner;
	private boolean isConsistent;

	public CheckReasoningConsistency(OWLReasoner reasoner) {
		this.reasoner = reasoner;
	}

	@Override
	public void time() {
		// reasoner.precomputeInferences();
		this.isConsistent = reasoner.isConsistent();
	}

	@Override
	public Boolean getResult() {
		return this.isConsistent;
	}
}
