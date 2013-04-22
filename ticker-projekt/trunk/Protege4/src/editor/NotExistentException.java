package editor;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;

public class NotExistentException extends RuntimeException {

	private static final long serialVersionUID = -5419306194579070006L;

	public NotExistentException(OWLClass className) {
		super("Klasse " + className + " existiert nicht");
	}

	public NotExistentException(OWLNamedIndividual individual) {
		super("Individuum " + individual + " existiert nicht");
	}

	public NotExistentException(OWLObjectProperty property) {
		super("ObjectProperty " + property + " existiert nicht");
	}
}
