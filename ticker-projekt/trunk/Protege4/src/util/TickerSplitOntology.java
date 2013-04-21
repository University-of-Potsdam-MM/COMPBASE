package util;

import java.util.ArrayList;
import java.util.Collection;

public class TickerSplitOntology extends ReasonableOntology {

	private Collection<Ticker> tickers;

	public TickerSplitOntology(ReasonableOntology ontology) {
		super(ontology);
		this.tickers = new ArrayList<Ticker>();
	}

	public void addTicker(Ticker ticker) {
		this.tickers.add(ticker);
	}

	public Iterable<Ticker> getTickers() {
		return this.tickers;
	}
}
