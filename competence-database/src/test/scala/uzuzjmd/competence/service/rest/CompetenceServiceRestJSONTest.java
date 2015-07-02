package uzuzjmd.competence.service.rest;

import static org.junit.Assert.assertNotSame;

import java.util.LinkedList;

import javax.ws.rs.core.MediaType;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class CompetenceServiceRestJSONTest {

	private static Thread serverThread;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		// serverThread = new Thread(new Runnable() {
		// @Override
		// public void run() {
		// MagicStrings.TDBLocationPath =
		// "C:/dev/scalaworkspace/Wissensmodellierung/competence-database/tdb2";
		// try {
		// RestServer.startServer();
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }
		// });

		// serverThread.start();

		// warm up database
		Client client2 = Client.create();
		// WebResource webResource2 =
		// client2.resource("http://localhost:8084/competences/json/catchwords/?competence=CS+students+adada+in+heterogeneous+groups");
		WebResource webResource2 = client2.resource("http://localhost:8080/competence-servlet/competence/competences/xml/competencetree/university/all/nocache");

		ClientResponse response2 = webResource2.accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		if (response2.getStatus() != 200) {
			throw new RuntimeException("Failed : HTTP error code : " + response2.getStatus());
		}
		client2.destroy();

	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		System.out.println("finished without problems");
	}

	@Test
	public void testGetCatchwordsForCompetenceSingle() throws InterruptedException {
		getCatchwords();
	}

	@Test
	public void testGetCatchwordsForCompetence100() throws InterruptedException {
		Runnable catchwordrunable = new Runnable() {
			@Override
			public void run() {
				getCatchwords();
			}
		};

		benchmark1000(catchwordrunable);

	}

	private void benchmark1000(Runnable catchwordrunable) throws InterruptedException {
		LinkedList<Thread> threadsStarted = new LinkedList<Thread>();
		for (int i = 0; i < 1000; i++) {
			Thread t = new Thread(catchwordrunable);
			threadsStarted.add(t);
			t.start();
		}
		for (Thread thread : threadsStarted) {
			thread.join();
		}
	}

	private void getCatchwords() {
		try {
			Client client = Client.create();
			WebResource webResource = client.resource("http://localhost:8080/competence-servlet/competence/competences/xml/competencetree/university/all/nocache");
			ClientResponse response = webResource.accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
			if (response.getStatus() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
			}
			String output = response.getEntity(String.class);
			assertNotSame(output, "");
			client.destroy();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testGetRdceo() {

	}

	@Test
	public void testUpdateHierarchie() {

	}

	@Test
	public void testUpdateHierarchieExample() {

	}

	@Test
	public void testLinkCompetencesToCourseContextJson() {

	}

	@Test
	public void testDeleteCourseContextJSON() {

	}

	@Test
	public void testGetRequirements() {

	}

	@Test
	public void testGetSelected() {

	}

	@Test
	public void testLinkCompetencesToUserJson() {

	}

	@Test
	public void testCommentCompetence() {

	}

	@Test
	public void testValidateLink() {

	}

	@Test
	public void testInvalidateLink() {

	}

	@Test
	public void testDeleteLink() {

	}

	@Test
	public void testDeleteCompetence() {

	}

	@Test
	public void testDeleteCompetenceTree() {

	}

	@Test
	public void testGetCompetenceLinksMap() {

	}

	@Test
	public void testGetProgressM() {

	}

	@Test
	public void testCreatePrerequisite() {

	}

	@Test
	public void testDeletePrerequisite() {

	}

	@Test
	public void testGetPrerequisiteGraph() {

	}

	@Test
	public void testGetRequiredCompetences() {

	}

	@Test
	public void testGetOperatorForCompetence() {

	}

	@Test
	public void testAddCompetenceToModel() {

	}

	@Test
	public void testEditCompetenceToModel() {

	}

}
