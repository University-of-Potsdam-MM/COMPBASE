package editor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.PrefixManager;

import algorithm.phase.shared.OWLUtil;

public class Ticker {

	private final OWLNamedIndividual tickerIndividual;
	private final Iterable<TimeIntervalEvent> events;
	private int minute;
	private String tickerName;

	public Ticker(OWLNamedIndividual tickerIndividual,
			Iterable<TimeIntervalEvent> events, int minute, String tickerName) {
		this.tickerIndividual = tickerIndividual;
		this.events = events;
		this.minute = minute;
		this.tickerName = tickerName;
	}

	public Set<OWLAxiom> applyTo(OWLDataFactory dataFactory,
			PrefixManager prefixManager) {
		Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
		// TickerMessage erstellen
		String tickerMessageName = tickerName + "_" + "Min" + minute + "_"
				+ UUID.randomUUID();
		OWLNamedIndividual tickerMessage = dataFactory.getOWLNamedIndividual(
				tickerMessageName, prefixManager);
		OWLClass tickerMessageClass = dataFactory.getOWLClass("TickerMessage",
				prefixManager);
		OWLAxiom tickerMessageAxiom = dataFactory.getOWLClassAssertionAxiom(
				tickerMessageClass, tickerMessage);
		axioms.add(tickerMessageAxiom);
		// TickerMessage mit Zeitpunkten verknüpfen
		OWLAxiom eventTimeConnection = OWLUtil.createMinuteConnectionAxiom(
				tickerMessage, minute, dataFactory, prefixManager);
		axioms.add(eventTimeConnection);
		// TickerMessage mit TimeIntervals verknüpfen
		OWLObjectProperty hasPublished = dataFactory.getOWLObjectProperty(
				"hasPublished", prefixManager);
		for (TimeIntervalEvent interval : this.events) {
			OWLAxiom axiom = dataFactory.getOWLObjectPropertyAssertionAxiom(
					hasPublished, tickerMessage, interval.getTimeInterval());
			axioms.add(axiom);
		}
		// Before-After-Beziehungen zwischen Zeitpunkten erstellen
		List<TimeIntervalEvent> eventCollection = new ArrayList<TimeIntervalEvent>();
		for (TimeIntervalEvent event : this.events)
			eventCollection.add(event);
		if (eventCollection.size() > 1) {
			OWLObjectProperty tsBefore = dataFactory.getOWLObjectProperty(
					"tsBefore", prefixManager);
			TimeIntervalEvent current = eventCollection.get(0);
			for (int count = 1; count < eventCollection.size(); count++) {
				OWLAxiom beforeAxiom = dataFactory
						.getOWLObjectPropertyAssertionAxiom(tsBefore,
								current.getTimeInterval(),
								eventCollection.get(count).getTimeInterval());
				axioms.add(beforeAxiom);
				current = eventCollection.get(count);
			}
		}
		// Ticker mit TickerMessage verknüpfen
		OWLAxiom tickerAxiom = dataFactory.getOWLObjectPropertyAssertionAxiom(
				hasPublished, this.tickerIndividual, tickerMessage);
		axioms.add(tickerAxiom);
		return axioms;
	}
}
