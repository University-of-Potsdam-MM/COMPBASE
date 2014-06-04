package de.unipotsdam.ontologypersistence.owl.access;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.io.FileUtils;

import com.hp.hpl.jena.rdf.model.Model;

/**
 * Eine hilfsklasse, um eine Ontologie-Modell als eine Datei auszugeben
 * 
 * @author julian
 * 
 */
public class FileUtil {

	private Model m;

	public FileUtil(Model m) {
		this.m = m;
	}

	// /**
	// * reads relativ from src dir
	// *
	// * @param m
	// * @param path
	// */
	// public void readFileOntology(String path) {
	// m.read(this.getClass().getResourceAsStream("/" + path),
	// MagicStrings.PREFIX);
	// }

	/**
	 * 
	 * @param m
	 * @throws IOException
	 */
	public void writeOntologyout(Model m) throws IOException {
		writeOut(m);
	}

	private void writeOut(Model m) {
		OutputStream out = null;
		try {
			// // XML format - long and verbose
			out = new FileOutputStream(MagicStrings.ONTOLOGYFILE);
			// String preamble =
			// "<?xml version=\"1.0\"?>\r\n\r\n\r\n<!DOCTYPE rdf:RDF [\r\n    <!ENTITY owl \"http://www.w3.org/2002/07/owl#\" >\r\n    <!ENTITY xsd \"http://www.w3.org/2001/XMLSchema#\" >\r\n    <!ENTITY rdfs \"http://www.w3.org/2000/01/rdf-schema#\" >\r\n    <!ENTITY rdf \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" >\r\n]>\r\n";
			// out.write(preamble.getBytes());

//			m.write(System.out, MagicStrings.ONTOLOGYFORMAT);
			m.write(out, MagicStrings.ONTOLOGYFORMAT);

			System.out.println("Written Ontology to "
					+ MagicStrings.ONTOLOGYFILE);

			// RDFWriter w = m.getWriter("RDF/XML");
			// w.setProperty("allowBadURIs", "true");
			// w.setProperty("showXMLDeclaration", "true");
			// w.setProperty("tab", "1");
			// w.write(m, out, MagicStrings.PREFIX);

			// OR Turtle format - compact and more readable
			// use this variant if you're not sure which to use!
			// out = new FileWriter("mymodel.ttl");
			// m.write(out, "Turtle");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException ignore) {
					System.out.println(ignore);
					ignore.printStackTrace();
				}
			}
		}
	}

	/**
	 * 
	 * @param m
	 * @throws IOException
	 */
	public void writeOntologyout() throws IOException {
		writeOut(m);
	}

	public static void deleteTDB() {
		String fileName = MagicStrings.TDBLocationPath;
		// A File object to represent the filename
		try {
			FileUtils.deleteDirectory(new File(fileName));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
