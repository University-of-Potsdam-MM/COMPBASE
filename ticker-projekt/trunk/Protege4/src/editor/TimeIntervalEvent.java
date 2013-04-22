package editor;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.PrefixManager;

public class TimeIntervalEvent {

	private final OWLClass event;
	private final Iterable<ObjectConnection> connections;
	private final int minute;

	private OWLNamedIndividual timeIntervalIndividual;

	public TimeIntervalEvent(OWLClass event,
			Iterable<ObjectConnection> connections, int minute) {
		this.event = event;
		this.connections = connections;
		this.minute = minute;
	}

	public Set<OWLAxiom> applyTo(OWLDataFactory dataFactory,
			PrefixManager prefixManager) {
		Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
		// Event-Individuum erstellen
		String individualName = "Min"
				+ (this.minute < 10 ? "0" + this.minute : this.minute) + "_"
				+ UUID.randomUUID();
		OWLNamedIndividual eventIndividual = dataFactory.getOWLNamedIndividual(
				individualName, prefixManager);
		OWLAxiom eventAxiom = dataFactory.getOWLClassAssertionAxiom(event,
				eventIndividual);
		axioms.add(eventAxiom);
		// Event-TimeSlice erstellen
		OWLClass timeSliceClass = dataFactory.getOWLClass("TimeSlice",
				prefixManager);
		String timeSliceName = "EventSlice_" + UUID.randomUUID();
		OWLNamedIndividual eventSliceIndividual = dataFactory
				.getOWLNamedIndividual(timeSliceName, prefixManager);
		OWLAxiom timeSliceAxiom = dataFactory.getOWLClassAssertionAxiom(
				timeSliceClass, eventSliceIndividual);
		axioms.add(timeSliceAxiom);
		// Event und EventSlice verbinden
		OWLObjectProperty tsTimeSliceOf = dataFactory.getOWLObjectProperty(
				"tsTimeSliceOf", prefixManager);
		OWLAxiom timeSliceOfAxiom = dataFactory
				.getOWLObjectPropertyAssertionAxiom(tsTimeSliceOf,
						eventSliceIndividual, eventIndividual);
		axioms.add(timeSliceOfAxiom);
		// TimeInterval erstellen
		OWLClass timeIntervalClass = dataFactory.getOWLClass("TimeInterval",
				prefixManager);
		String timeIntervalName = "Min"
				+ (this.minute < 10 ? "0" + this.minute : this.minute) + "_"
				+ UUID.randomUUID();
		timeIntervalIndividual = dataFactory.getOWLNamedIndividual(
				timeIntervalName, prefixManager);
		OWLAxiom timeIntervalAxiom = dataFactory.getOWLClassAssertionAxiom(
				timeIntervalClass, timeIntervalIndividual);
		axioms.add(timeIntervalAxiom);
		// TimeInterval und EventSlice verbinden
		OWLObjectProperty tsTimeInterval = dataFactory.getOWLObjectProperty(
				"tsTimeInterval", prefixManager);
		OWLAxiom tsTimeIntervalAxiom = dataFactory
				.getOWLObjectPropertyAssertionAxiom(tsTimeInterval,
						eventSliceIndividual, timeIntervalIndividual);
		axioms.add(tsTimeIntervalAxiom);
		// Connections erstellen
		for (ObjectConnection connection : this.connections) {
			Set<OWLAxiom> connectionAxioms = connection
					.applyTo(dataFactory, prefixManager, eventSliceIndividual,
							timeIntervalIndividual);
			axioms.addAll(connectionAxioms);
		}
		return axioms;
	}

	public OWLIndividual getTimeInterval() {
		return this.timeIntervalIndividual;
	}
}
