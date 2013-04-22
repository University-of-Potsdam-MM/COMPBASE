package editor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

public class CommandParser {

	private static final String SPLIT_MARKER = "---";

	private final OWLDataFactory dataFactory;
	private final PrefixManager prefixManager;
	private final OWLReasoner reasoner;

	private static CommandParser currentSingleton;

	public static CommandParser getSingleton() {
		return currentSingleton;
	}

	public static void setSingleton(CommandParser parser) {
		currentSingleton = parser;
	}

	public CommandParser(OWLDataFactory dataFactory,
			PrefixManager prefixManager, OWLReasoner reasoner) {
		this.dataFactory = dataFactory;
		this.prefixManager = prefixManager;
		this.reasoner = reasoner;
	}

	public Iterable<TimeIntervalEvent> processAsTimeIntervalEvents(
			String[] commands, int minute) {
		Collection<TimeIntervalEvent> events = new ArrayList<TimeIntervalEvent>();

		Iterable<String[]> splittedCommands = this.splitByMarker(commands);
		for (String[] command : splittedCommands) {
			OWLClass event = dataFactory.getOWLClass(command[0], prefixManager);
			ensureExists(event);

			Collection<ObjectConnection> connections = new ArrayList<ObjectConnection>();
			for (int count = 1; count < command.length; count++) {
				String[] objectConnection = command[count].split(" ");

				OWLObjectProperty connection = dataFactory
						.getOWLObjectProperty(objectConnection[0],
								prefixManager);
				ensureExists(connection);

				OWLNamedIndividual subject = dataFactory.getOWLNamedIndividual(
						objectConnection[1], prefixManager);
				ensureExists(subject);

				connections.add(new ObjectConnection(connection, subject));
			}

			events.add(new TimeIntervalEvent(event, connections, minute));
		}

		return events;
	}

	private Iterable<String[]> splitByMarker(String[] commands) {
		if (commands.length == 0)
			return new ArrayList<String[]>();

		Collection<String[]> splittedCommands = new ArrayList<String[]>();
		String[] processedCommands = Arrays.copyOf(commands,
				commands.length + 1);
		processedCommands[commands.length] = SPLIT_MARKER;

		int currentStart = 0;
		for (int count = 0; count < processedCommands.length; count++) {
			if (!processedCommands[count].equals(SPLIT_MARKER))
				continue;
			String[] split = Arrays.copyOfRange(processedCommands,
					currentStart, count);
			splittedCommands.add(split);
			currentStart = count + 1;
		}

		return splittedCommands;
	}

	public void ensureExists(OWLClass className) {
		if (!exists(className))
			throw new NotExistentException(className);
	}

	public void ensureExists(OWLNamedIndividual individual) {
		if (!exists(individual))
			throw new NotExistentException(individual);
	}

	public void ensureExists(OWLObjectProperty property) {
		if (!exists(property))
			throw new NotExistentException(property);
	}

	public boolean exists(OWLClass className) {
		OWLClass thingClass = dataFactory.getOWLThing();
		return reasoner.getSubClasses(thingClass, false).getFlattened()
				.contains(className);
	}

	public boolean exists(OWLNamedIndividual individual) {
		OWLClass thingClass = dataFactory.getOWLThing();
		return reasoner.getInstances(thingClass, false).getFlattened()
				.contains(individual);
	}

	public boolean exists(OWLObjectProperty property) {
		OWLObjectProperty topProperty = dataFactory.getOWLTopObjectProperty();
		return reasoner.getSubObjectProperties(topProperty, false)
				.getFlattened().contains(property);
	}

	public Set<OWLNamedIndividual> getTickers() {
		OWLClass tickerClass = dataFactory.getOWLClass("Ticker", prefixManager);
		return this.reasoner.getInstances(tickerClass, false).getFlattened();
	}
}
