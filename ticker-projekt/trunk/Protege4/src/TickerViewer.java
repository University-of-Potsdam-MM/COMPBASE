import java.awt.Dimension;
import java.awt.Paint;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

import org.apache.commons.collections15.Transformer;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import viewer.ContentController;
import viewer.TickerConnection;
import viewer.TickerNode;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.PickingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.PluggableGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.TranslatingGraphMousePlugin;

public class TickerViewer extends JFrame {

	private static final long serialVersionUID = -870786568926715547L;

	public static void main(String[] args) throws OWLOntologyCreationException {
		JFileChooser chooser = new JFileChooser();
		if (args.length > 0) {
			File defaultFile = new File(args[0]);
			chooser.setSelectedFile(defaultFile);
		}

		if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			File ontology = chooser.getSelectedFile();
			new TickerViewer(ontology);
		}
	}

	private final ContentController controller;

	private TickerViewer(File loadFile) throws OWLOntologyCreationException {
		this.controller = new ContentController(loadFile);

		Graph<TickerNode, TickerConnection> graph = controller.readOntology();
		Layout<TickerNode, TickerConnection> layout = new ISOMLayout<TickerNode, TickerConnection>(
				graph);

		layout.setSize(new Dimension(1024, 768));
		VisualizationViewer<TickerNode, TickerConnection> server = new VisualizationViewer<TickerNode, TickerConnection>(
				layout);
		setRenderOptions(server.getRenderContext());

		PluggableGraphMouse mouse = new PluggableGraphMouse();
		mouse.add(new TranslatingGraphMousePlugin(MouseEvent.BUTTON3_MASK));
		mouse.add(new ScalingGraphMousePlugin(new CrossoverScalingControl(), 0,
				1.1f, 0.9f));
		mouse.add(new PickingGraphMousePlugin<TickerNode, TickerConnection>(
				MouseEvent.BUTTON1_MASK, MouseEvent.BUTTON1_MASK));
		server.setGraphMouse(mouse);

		this.getContentPane().add(server);

		this.setExtendedState(MAXIMIZED_BOTH);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setVisible(true);
	}

	private static void setRenderOptions(
			RenderContext<TickerNode, TickerConnection> context) {
		context.setVertexLabelTransformer(new Transformer<TickerNode, String>() {
			@Override
			public String transform(TickerNode arg0) {
				return arg0.getRenderName();
			}
		});
		context.setVertexFillPaintTransformer(new Transformer<TickerNode, Paint>() {
			@Override
			public Paint transform(TickerNode arg0) {
				return arg0.getRenderPaint();
			}
		});
	}
}
