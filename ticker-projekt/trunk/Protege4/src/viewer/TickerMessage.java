package viewer;

import java.awt.Color;
import java.awt.Paint;

import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.reasoner.Node;

public class TickerMessage implements TickerNode {

	private final Node<OWLNamedIndividual> message;
	private final int time;

	public TickerMessage(Node<OWLNamedIndividual> message, int time) {
		this.message = message;
		this.time = time;
	}

	@Override
	public String getRenderName() {
		String minute = Integer.toString(time);
		minute = minute.length() < 2 ? "0" + minute : minute;
		return "Minute " + minute;
	}

	public OWLNamedIndividual getRepresentative() {
		return this.message.getRepresentativeElement();
	}

	@Override
	public Paint getRenderPaint() {
		return Color.YELLOW;
	}
}
