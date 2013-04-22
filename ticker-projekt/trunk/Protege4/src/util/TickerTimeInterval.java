package util;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

public class TickerTimeInterval {

	private final OWLDataFactory dataFactory;
	private final OWLNamedIndividual interval;

	public TickerTimeInterval(OWLNamedIndividual interval,
			OWLDataFactory dataFactory) {
		this.interval = interval;
		this.dataFactory = dataFactory;
	}

	public OWLAxiom createSameAxiom(TickerTimeInterval targetInterval) {
		OWLAxiom sameAxiom = this.dataFactory.getOWLSameIndividualAxiom(
				this.interval, targetInterval.getInterval());
		return sameAxiom;
	}

	public OWLIndividual getInterval() {
		return this.interval;
	}
}
