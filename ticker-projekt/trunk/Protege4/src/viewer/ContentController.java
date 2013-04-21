package viewer;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.semanticweb.HermiT.Reasoner.ReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

import algorithm.phase.shared.OWLUtil;
import editor.CommandInterpreter;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraDistance;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.util.EdgeType;

public class ContentController {

	private final OWLOntologyManager ontologyManager;
	private final OWLOntology ontology;
	private final OWLDataFactory dataFactory;
	private final PrefixManager prefixManager;
	private final OWLReasoner reasoner;
	private final Map<OWLNamedIndividual, TimeInterval> timeIntervalMap;

	public ContentController(File ontologyFile)
			throws OWLOntologyCreationException {
		ontologyManager = OWLManager.createOWLOntologyManager();
		ontology = ontologyManager
				.loadOntologyFromOntologyDocument(ontologyFile);
		dataFactory = ontologyManager.getOWLDataFactory();
		prefixManager = new DefaultPrefixManager(CommandInterpreter.DEFAULT_IRI);

		System.out.println(new Date() + " Reasoner erstellen");
		this.reasoner = new ReasonerFactory().createReasoner(ontology);

		this.timeIntervalMap = new HashMap<OWLNamedIndividual, TimeInterval>();
	}

	public DirectedGraph<TickerNode, TickerConnection> readOntology() {
		DirectedGraph<TickerNode, TickerConnection> graph = new DirectedSparseGraph<TickerNode, TickerConnection>();

		for (Ticker ticker : this.readTickers()) {
			graph.addVertex(ticker);
			for (TickerMessage message : this.readTickerMessages(ticker)) {
				graph.addEdge(new TickerConnection("hasPublished"), ticker,
						message, EdgeType.DIRECTED);
				for (TimeInterval interval : this.readTimeIntervals(message)) {
					graph.addEdge(new TickerConnection("hasPublished"),
							message, interval, EdgeType.DIRECTED);
				}
			}
		}

		DirectedGraph<TimeInterval, TickerConnection> beforeGraph = this
				.createBeforeConnections();
		for (TickerConnection connection : beforeGraph.getEdges()) {
			TimeInterval source = beforeGraph.getSource(connection);
			TimeInterval target = beforeGraph.getDest(connection);
			EdgeType type = beforeGraph.getEdgeType(connection);
			graph.addEdge(connection, source, target, type);
		}

		return graph;
	}

	private DirectedGraph<TimeInterval, TickerConnection> createBeforeConnections() {
		System.out.println(new Date() + " Before-Connections sortieren");
		DirectedGraph<TimeInterval, TickerConnection> graph = new DirectedSparseGraph<TimeInterval, TickerConnection>();

		// Zuordnung Minute->TimeIntervals erstellen
		Map<Integer, Collection<TimeInterval>> intervalMinuteMap = new HashMap<Integer, Collection<TimeInterval>>();
		Set<TimeInterval> timeIntervalSet = new HashSet<TimeInterval>(
				this.timeIntervalMap.values());
		for (TimeInterval interval : timeIntervalSet) {
			int minute = OWLUtil.extractMinute(interval.getRepresentative(),
					dataFactory, prefixManager, reasoner);
			Collection<TimeInterval> timeIntervalsInMinute = null;
			if (intervalMinuteMap.containsKey(minute))
				timeIntervalsInMinute = intervalMinuteMap.get(minute);
			else {
				timeIntervalsInMinute = new ArrayList<TimeInterval>();
				intervalMinuteMap.put(minute, timeIntervalsInMinute);
			}
			timeIntervalsInMinute.add(interval);
		}

		// Graph erstellen
		Integer[] tempKeys = intervalMinuteMap.keySet().toArray(new Integer[0]);
		int[] keys = new int[tempKeys.length];
		for (int count = 0; count < keys.length; count++)
			keys[count] = tempKeys[count];
		Arrays.sort(keys);
		for (int count = 0; count < keys.length; count++) {
			Collection<TimeInterval> currents = intervalMinuteMap
					.get(keys[count]);
			for (TimeInterval current : currents) {
				for (int count2 = count + 1; count2 < keys.length; count2++) {
					Collection<TimeInterval> targets = intervalMinuteMap
							.get(keys[count2]);
					for (TimeInterval target : targets)
						graph.addEdge(new TickerConnection(new String()),
								current, target, EdgeType.DIRECTED);
				}
			}
		}

		// TODO das Problem gleicher Kanten in einer Minute lösen

		// Überflüssige (transitive) Kanten entfernen
		Collection<TickerConnection> connections = new ArrayList<TickerConnection>(
				graph.getEdges());
		Iterator<TickerConnection> iterator = connections.iterator();
		while (iterator.hasNext()) {
			TickerConnection connection = iterator.next();
			TimeInterval source = graph.getSource(connection);
			TimeInterval target = graph.getDest(connection);
			EdgeType type = graph.getEdgeType(connection);

			graph.removeEdge(connection);
			Number distance = new DijkstraDistance<TimeInterval, TickerConnection>(
					graph).getDistance(source, target);
			if (distance != null)
				iterator.remove();
			else
				graph.addEdge(connection, source, target, type);
		}

		return graph;
	}

	private Iterable<TimeInterval> readTimeIntervals(TickerMessage message) {
		System.out.println(new Date() + " TimeIntervals lesen ("
				+ message.getRenderName() + ")");
		Collection<TimeInterval> result = new ArrayList<TimeInterval>();

		OWLClass gameActionClass = dataFactory.getOWLClass("GameAction",
				prefixManager);
		OWLClass gameActionSliceClass = dataFactory.getOWLClass(
				"GameActionSlice", prefixManager);
		OWLObjectProperty hasPublished = dataFactory.getOWLObjectProperty(
				"hasPublished", prefixManager);
		OWLObjectProperty tsTimeIntervalOf = dataFactory.getOWLObjectProperty(
				"tsTimeIntervalOf", prefixManager);
		OWLObjectProperty tsTimeSliceOf = dataFactory.getOWLObjectProperty(
				"tsTimeSliceOf", prefixManager);

		OWLNamedIndividual messageIndividual = message.getRepresentative();
		for (Node<OWLNamedIndividual> timeInterval : reasoner
				.getObjectPropertyValues(messageIndividual, hasPublished)) {
			boolean foundMapElement = false;
			for (OWLNamedIndividual representative : timeInterval) {
				if (!this.timeIntervalMap.containsKey(representative))
					continue;
				result.add(this.timeIntervalMap.get(representative));
				foundMapElement = true;
				break;
			}
			if (foundMapElement)
				continue;

			OWLClass event = null;
			boolean foundEvent = false;
			for (Node<OWLNamedIndividual> timeSlice : reasoner
					.getObjectPropertyValues(
							timeInterval.getRepresentativeElement(),
							tsTimeIntervalOf)) {
				if (foundEvent)
					break;
				if (!reasoner.getTypes(timeSlice.getRepresentativeElement(),
						false).containsEntity(gameActionSliceClass))
					continue;
				// Wir haben es mit einer GameActionSlice zu tun
				foundEvent = true;
				NodeSet<OWLNamedIndividual> eventIndividuals = reasoner
						.getObjectPropertyValues(
								timeSlice.getRepresentativeElement(),
								tsTimeSliceOf);
				@SuppressWarnings("unchecked")
				Node<OWLNamedIndividual> eventIndividual = eventIndividuals
						.getNodes().toArray(new Node[1])[0];
				for (Node<OWLClass> type : reasoner.getTypes(
						eventIndividual.getRepresentativeElement(), true)) {
					OWLClass representative = type.getRepresentativeElement();
					if (!reasoner.getSuperClasses(representative, false)
							.containsEntity(gameActionClass))
						continue;
					event = representative;
					break;
				}
			}

			TimeInterval interval = new TimeInterval(event,
					timeInterval.getRepresentativeElement());
			for (OWLNamedIndividual representative : timeInterval)
				this.timeIntervalMap.put(representative, interval);
			result.add(interval);
		}

		return result;
	}

	private Iterable<TickerMessage> readTickerMessages(Ticker ticker) {
		System.out.println(new Date() + " TickerMessages lesen ("
				+ ticker.getRenderName() + ")");
		Collection<TickerMessage> result = new ArrayList<TickerMessage>();

		OWLClass tickerMessageClass = dataFactory.getOWLClass("TickerMessage",
				prefixManager);
		OWLObjectProperty hasPublished = dataFactory.getOWLObjectProperty(
				"hasPublished", prefixManager);

		OWLNamedIndividual tickerIndividual = ticker.getRepresentative();
		for (Node<OWLNamedIndividual> tickerMessage : reasoner
				.getObjectPropertyValues(tickerIndividual, hasPublished)) {
			OWLNamedIndividual representative = tickerMessage
					.getRepresentativeElement();
			if (!reasoner.getTypes(representative, false).containsEntity(
					tickerMessageClass))
				continue;
			// Wir haben es mit einer TickerMessage zu tun
			int minute = OWLUtil.extractMinute(representative, dataFactory,
					prefixManager, reasoner);

			result.add(new TickerMessage(tickerMessage, minute));
		}

		return result;
	}

	private Iterable<Ticker> readTickers() {
		System.out.println(new Date() + " Ticker lesen");
		Collection<Ticker> result = new ArrayList<Ticker>();

		OWLClass tickerClass = dataFactory.getOWLClass("Ticker", prefixManager);
		for (Node<OWLNamedIndividual> ticker : reasoner.getInstances(
				tickerClass, false))
			result.add(new Ticker(ticker));

		return result;
	}
}
