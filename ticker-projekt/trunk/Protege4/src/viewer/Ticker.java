package viewer;

import java.awt.Color;
import java.awt.Paint;

import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.reasoner.Node;

import editor.CommandInterpreter;

public class Ticker implements TickerNode {

	private final Node<OWLNamedIndividual> ticker;

	public Ticker(Node<OWLNamedIndividual> ticker) {
		this.ticker = ticker;
	}

	@Override
	public String getRenderName() {
		return CommandInterpreter.getRenderName(ticker
				.getRepresentativeElement());
	}

	public OWLNamedIndividual getRepresentative() {
		return this.ticker.getRepresentativeElement();
	}

	@Override
	public Paint getRenderPaint() {
		return Color.RED;
	}
}
