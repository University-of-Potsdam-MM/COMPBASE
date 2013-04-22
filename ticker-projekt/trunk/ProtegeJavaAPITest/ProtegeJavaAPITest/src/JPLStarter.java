import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Hashtable;

import jpl.Atom;
import jpl.Query;
import jpl.Term;
import jpl.Variable;

public class JPLStarter {

	public static void main(String[] args) {
		String pathToPrologBin = args[0];

		// Alle Bibliotheken laden
		File binDir = new File(pathToPrologBin);
		File[] binDirArray = binDir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File arg0) {
				return arg0.getName().endsWith(".dll");
			}
		});

		// Bibliotheken in Liste kopieren
		ArrayList<File> binDirContent = new ArrayList<File>(binDirArray.length);
		for (File file : binDirArray) {
			binDirContent.add(file);
		}

		// Alle Bibliotheken, die sich laden lassen, nacheinander laden
		boolean loadedLib;
		do {
			loadedLib = false;
			for (File file : binDirContent) {
				try {
					System.load(file.getAbsolutePath());
					binDirContent.remove(file);
					loadedLib = true;

					System.out.print("Bibliothek " + file.getName()
							+ " geladen, ");
					break;
				} catch (UnsatisfiedLinkError e) {
				}
			}

			System.out.println(binDirContent.size() + " Bibliotheken zu laden");
		} while (loadedLib == true && !binDirContent.isEmpty());

		// Bibliotheken, die nicht geladen werden konnten, ausgeben
		if (!binDirContent.isEmpty()) {
			System.out
					.println("Konnte nicht alle Bibliotheken-Abhaengigkeiten aufloesen");
			System.out.println("Nicht geladene Bibliotheken:");
			for (File file : binDirContent) {
				System.out.println("- " + file.getName());
			}
		}

		Query loadScript = new Query("consult", new Term[] { new Atom(
				"prolog-scripts/inheritance_example.pl") });
		Hashtable[] result = loadScript.allSolutions();
		boolean success = result.length > 0;

		System.out.println("Script geladen: " + (success ? "ja" : "nein"));
		if (!success)
			return;

		Variable X = new Variable("X");
		Query allDescendents = new Query("descendent_of", new Term[] {
				new Atom("mary"), X });
//		Query allDescendents = new Query("descendent_of('mary',X)");
		Hashtable[] solutions = allDescendents.allSolutions();
		
		for (Hashtable solution : solutions) {
			Term term = (Term) solution.get("X");
			System.out.println("Nachfolger von Mary: " + term);
		} 
		
		Query assertSomething = new Query("assert(hello(X,X))");
		boolean wasSucess = assertSomething.allSolutions().length > 0;
		
		
		System.out.println("War Erfolg: " + wasSucess);
		
		Query allHellos = new Query("hello", new Term[] {
				new Atom("klamm"), new Atom("klamm") });
		solutions = allHellos.allSolutions();
		
		System.out.println("L�sungen: " + solutions.length);
//		for (Hashtable solution : solutions) {
//			Term term = (Term) solution.get("X");
//			System.out.println("Hello meint: " + term);
//		} 
	}
}
