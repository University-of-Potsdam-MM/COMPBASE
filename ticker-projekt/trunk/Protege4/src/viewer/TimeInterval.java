package viewer;

import java.awt.Color;
import java.awt.Paint;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import editor.CommandInterpreter;

public class TimeInterval implements TickerNode {

	private final OWLClass eventClass;
	private final OWLNamedIndividual representative;

	public TimeInterval(OWLClass eventClass, OWLNamedIndividual representative) {
		this.eventClass = eventClass;
		this.representative = representative;
	}

	public OWLNamedIndividual getRepresentative() {
		return this.representative;
	}

	@Override
	public String getRenderName() {
		return CommandInterpreter.getRenderName(eventClass);
	}

	@Override
	public Paint getRenderPaint() {
		return Color.GREEN;
	}
}
