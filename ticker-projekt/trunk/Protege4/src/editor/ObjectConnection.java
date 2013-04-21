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

public class ObjectConnection {

	private final OWLObjectProperty connection;
	private final OWLIndividual subject;

	public ObjectConnection(OWLObjectProperty connection, OWLIndividual subject) {
		this.connection = connection;
		this.subject = subject;
	}

	public Set<OWLAxiom> applyTo(OWLDataFactory dataFactory,
			PrefixManager prefixManager, OWLNamedIndividual event,
			OWLNamedIndividual timeIntervalIndividual) {
		Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
		// SubjectSlice erstellen
		String sliceName = UUID.randomUUID().toString();
		OWLNamedIndividual slice = dataFactory.getOWLNamedIndividual(sliceName,
				prefixManager);
		OWLClass sliceClass = dataFactory.getOWLClass("TimeSlice",
				prefixManager);
		OWLAxiom sliceAxiom = dataFactory.getOWLClassAssertionAxiom(sliceClass,
				slice);
		axioms.add(sliceAxiom);
		// Subject und SubjectSlice verbinden
		OWLObjectProperty tsTimeSliceOf = dataFactory.getOWLObjectProperty(
				"tsTimeSliceOf", prefixManager);
		OWLAxiom timeSliceOfAxiom = dataFactory
				.getOWLObjectPropertyAssertionAxiom(tsTimeSliceOf, slice,
						subject);
		axioms.add(timeSliceOfAxiom);
		// SubjectSlice und TimeInterval verbinden
		OWLObjectProperty timeInterval = dataFactory.getOWLObjectProperty(
				"tsTimeInterval", prefixManager);
		OWLAxiom timeIntervalAxiom = dataFactory
				.getOWLObjectPropertyAssertionAxiom(timeInterval, slice,
						timeIntervalIndividual);
		axioms.add(timeIntervalAxiom);
		// SubjectSlice und Event verbinden
		OWLAxiom connectionAxiom = dataFactory
				.getOWLObjectPropertyAssertionAxiom(connection, event, slice);
		axioms.add(connectionAxiom);
		return axioms;
	}
}
