import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextPane;

import layout.TableLayout;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import editor.CommandInterpreter;
import editor.NotExistentException;

public class CommandUI extends JFrame {

	private static final long serialVersionUID = -6999831017026900792L;

	public static void main(String[] args) throws OWLOntologyCreationException {
		JFileChooser chooser = new JFileChooser();
		if (args.length > 0) {
			File defaultFile = new File(args[0]);
			chooser.setSelectedFile(defaultFile);
		}

		if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			File ontology = chooser.getSelectedFile();
			new CommandUI(ontology);
		}
	}

	private final CommandInterpreter controller;

	private final JTextArea commandArea;
	private final JTextPane minutePane;
	private final JComboBox ticketBox;

	public CommandUI(File ontology) throws OWLOntologyCreationException {
		this.controller = new CommandInterpreter(ontology);

		double[][] layout = new double[][] {
				{ 8, TableLayout.FILL, 8, 200, 8 },
				{ 8, TableLayout.PREFERRED, 8, TableLayout.PREFERRED, 8,
						TableLayout.PREFERRED, 8, TableLayout.FILL, 8,
						TableLayout.PREFERRED, 8 } };
		this.setLayout(new TableLayout(layout));

		JPanel commandPanel = new JPanel();
		commandPanel.setBorder(BorderFactory.createTitledBorder("Befehle"));
		commandPanel.setLayout(new BorderLayout());
		this.add(commandPanel, "1, 1, 1, 9");
		this.commandArea = new JTextArea();
		commandPanel.add(commandArea, BorderLayout.CENTER);

		JPanel tickerPanel = new JPanel();
		tickerPanel.setBorder(BorderFactory.createTitledBorder("Ticker"));
		tickerPanel.setLayout(new BorderLayout());
		this.add(tickerPanel, "3, 1");
		this.ticketBox = new JComboBox();
		tickerPanel.add(ticketBox, BorderLayout.CENTER);

		for (String tickerName : controller.getTickerNames())
			ticketBox.addItem(tickerName);

		JPanel minutePanel = new JPanel();
		minutePanel.setBorder(BorderFactory.createTitledBorder("Minute"));
		minutePanel.setLayout(new BorderLayout());
		this.add(minutePanel, "3, 3");
		this.minutePane = new JTextPane();
		minutePanel.add(minutePane, BorderLayout.CENTER);

		JButton addButton = new JButton();
		addButton.setText("Hinzufügen");
		this.add(addButton, "3, 5");
		addButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				CommandUI.this.addCommands();
			}
		});

		JButton saveButton = new JButton();
		saveButton.setText("Ontologie speichern");
		this.add(saveButton, "3, 9");
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				CommandUI.this.saveOntology();
			}
		});

		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setSize(700, 500);
		this.setVisible(true);
	}

	private void addCommands() {
		String[] commands = this.commandArea.getText().split("\n");
		int minute = Integer.parseInt(this.minutePane.getText());
		String tickerName = this.ticketBox.getSelectedItem().toString();

		try {
			this.controller.interpret(commands, minute, tickerName);
		} catch (NotExistentException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(),
					"Fehler beim Hinzufügen", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void saveOntology() {
		try {
			this.controller.saveOntology();
		} catch (OWLOntologyStorageException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this,
					"Während des Speichervorganges ist ein Fehler aufgetreten: "
							+ e, "Fehler beim Speichern",
					JOptionPane.ERROR_MESSAGE);
		}
	}
}
