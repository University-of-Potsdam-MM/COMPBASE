package uzuzjmd.competence.config;

/**
 * contains the main configuration for the project which are defined in
 * evidenceserver.properties
 */
public class MagicStrings {

	// public static final String PREFIX =
	// "http://www.uzuzjmd.de/proof-of-concept.owl#";
	public static final String PREFIX = "http://comp#"; // better don't change,
														// might be hardcoded
														// somewhere
	public static final String ROOTPATH = PropUtil
			.getProp("rootPath");
	public static final String LOG4JXMLPATH = PropUtil
			.getRelativeOrAbsoluteFileProp(
					"log4jlocationRelative",
					"log4jlocation");
	public static final String TDBLocationPath = PropUtil
			.getRelativeOrAbsoluteFileProp(
					"tdblocationRelative", "tdblocation");
	// public static final String ONTOLOGYFORMAT = "TURTLE";
	public static final String ONTOLOGYFORMAT = "RDF/XML";
	// public static final String ONTOLOGYFORMAT = "RDF/XML-ABBREV";
	public static final String ONTOLOGYFILE = PropUtil
			.getRelativeFileProp("ontologyFile");
	// public static String ONTOLOGYFILE = null;
	public static final String SERVICEENDPOINT = "http://localhost:8081/WS/Competence";
	public static final String EVIDENCESERVICEENDPOINT = "http://localhost:8082/WS/Competence/Evidence";

	public static final String SINGLETONPREFIX = "I";
	public static final String CSVLOCATION = PropUtil
			.getRelativeFileProp("csvFile");
	public static final String MOODLEURL = PropUtil
			.getProp("moodleURL");
	public static final String webapplicationPath = PropUtil
			.getProp("webapplicationPath");
	public static final String RESTURLCompetence = "http://localhost:8084";
	public static final String ICONPATHMOODLE = "icons/WindowsIcons-master/WindowsPhone/svg";
	public static final String ICONPATHGWT = "icons/WindowsIcons-master/WindowsPhone/svg";
	public static final String USERICONPATH = "http://icons.iconarchive.com/icons/artua/dragon-soft/16/User-icon.png";
	public static final String EPOSLocation = PropUtil
			.getRelativeFileProp("eposfile");
	public static final String GERMANMODELLOCATION = "germanPCFG.ser.gz";
	public static final String thesaurusLogin = PropUtil
			.getProp("thesaurusLogin");
	public static final String thesaurusPassword = PropUtil
			.getProp("thesaurusPassword");
	public static final String thesaurusDatabaseName = PropUtil
			.getProp("thesaurusDatabaseName");
	public static final String thesaurusDatabaseUrl = PropUtil
			.getProp("thesaurusDatabaseUrl");
	public static final Boolean WRITEDEBUGRDF = PropUtil
			.getProp("writeDebugRDF").equals("true");
	public static final String NEO4JURL = PropUtil
			.getProp("neo4jURL");
	public static final Boolean neo4jEnabled = PropUtil
			.getProp("neo4jEnabled").equals("true");


}