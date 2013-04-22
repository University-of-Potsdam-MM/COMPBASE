package algorithm.phase.shared;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

public class OWLUtil {

	public static int extractMinute(OWLNamedIndividual timeInterval,
			OWLDataFactory dataFactory, PrefixManager prefixManager,
			OWLReasoner reasoner) {
		OWLObjectProperty tsMinute = dataFactory.getOWLObjectProperty(
				"tsMinute", prefixManager);
		OWLDataProperty tsEventTime = dataFactory.getOWLDataProperty(
				"tsEventTime", prefixManager);

		Set<OWLNamedIndividual> minuteProperties = reasoner
				.getObjectPropertyValues(timeInterval, tsMinute).getFlattened();
		OWLNamedIndividual minuteProperty = minuteProperties
				.toArray(new OWLNamedIndividual[1])[0];

		Set<OWLLiteral> minuteDatas = reasoner.getDataPropertyValues(
				minuteProperty, tsEventTime);
		OWLLiteral minuteData = minuteDatas.toArray(new OWLLiteral[1])[0];

		return minuteData.parseInteger();
	}

	public static OWLAxiom createMinuteConnectionAxiom(
			OWLNamedIndividual timeInterval, int minute,
			OWLDataFactory dataFactory, PrefixManager prefixManager) {
		OWLObjectProperty tsMinute = dataFactory.getOWLObjectProperty(
				"tsMinute", prefixManager);
		OWLNamedIndividual minuteIndividual = dataFactory
				.getOWLNamedIndividual(Integer.toString(minute), prefixManager);

		return dataFactory.getOWLObjectPropertyAssertionAxiom(tsMinute,
				timeInterval, minuteIndividual);
	}
}
