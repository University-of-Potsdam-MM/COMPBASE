package uzuzjmd.competence.service.rest;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import uzuzjmd.competence.mapper.gui.Ont2SuggestedCompetenceGrid;
import uzuzjmd.competence.mapper.gui.ReflectiveAssessmentHolder2Ont;
import uzuzjmd.competence.owl.access.CompOntologyManager;
import uzuzjmd.competence.owl.dao.CourseContext;
import uzuzjmd.competence.owl.dao.LearningProjectTemplate;
import uzuzjmd.competence.owl.dao.SelectedLearningProjectTemplate;
import uzuzjmd.competence.owl.dao.TeacherRole;
import uzuzjmd.competence.owl.dao.User;
import uzuzjmd.competence.owl.ontology.CompOntClass;
import uzuzjmd.competence.rcd.generated.Rdceo;
import uzuzjmd.competence.service.CompetenceServiceImpl;
import uzuzjmd.competence.service.rest.dto.CatchwordXMLTree;
import uzuzjmd.competence.service.rest.dto.CompetenceXMLTree;
import uzuzjmd.competence.service.rest.dto.OperatorXMLTree;
import uzuzjmd.competence.shared.ReflectiveAssessmentsListHolder;
import uzuzjmd.competence.shared.StringList;
import uzuzjmd.competence.shared.SuggestedCompetenceColumn;
import uzuzjmd.competence.shared.SuggestedCompetenceGrid;
import uzuzjmd.competence.shared.SuggestedCompetenceRow;

/**
 * Root resource (exposed at "competences" path)
 */
@Path("/competences/xml")
public class CompetenceServiceRestXML extends CompetenceOntologyInterface {

	/**
	 * new competences can be added via this function. They must be encoded as
	 * Rdceo see somewhere...
	 * 
	 * @param input
	 */
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	@Path("/add")
	public void addRcdeo(Rdceo input) {
		System.out.println("Competences inserted");
		CompetenceServiceImpl competenceServiceImpl = new CompetenceServiceImpl();
		competenceServiceImpl.insertCompetence(input);
	}

	@Produces(MediaType.APPLICATION_XML)
	@GET
	@Path("/competencetree/university/{compulsory}/{cache}")
	public Response getCompetenceTreeForUniversity(@PathParam("compulsory") String compulsory, @PathParam("cache") String cache,
			@QueryParam(value = "selectedCatchwords") List<String> selectedCatchwords, @QueryParam(value = "selectedOperators") List<String> selectedOperators) {

		CompetenceXMLTree[] result = null;
		if (compulsory.equals("all")) {
			result = CompetenceServiceWrapper.getCompetenceTreeForCourse(selectedCatchwords, selectedOperators, "university", null);
		} else {
			Boolean compulsoryBoolean = RestUtil.convertCompulsory(compulsory);
			result = CompetenceServiceWrapper.getCompetenceTreeForCourse(selectedCatchwords, selectedOperators, "university", compulsoryBoolean);
		}

		Response response = RestUtil.buildCachedResponse(result, cache.equals("cache"));
		return response;
	}

	@Produces(MediaType.APPLICATION_XML)
	@GET
	@Path("/competencetree/course/{compulsory}/{cache}/{course}")
	public Response getCompetenceTreeForCourseWithoutFilter(@PathParam("course") String course, @PathParam("compulsory") String compulsory, @PathParam("cache") String cache,
			@QueryParam(value = "selectedCatchwords") List<String> selectedCatchwords, @QueryParam(value = "selectedOperators") List<String> selectedOperators) {

		CompetenceXMLTree[] result = null;
		if (compulsory.equals("all")) {
			result = CompetenceServiceWrapper.getCompetenceTreeForCourseNoFilter(selectedCatchwords, selectedOperators, course, null);
		} else {
			Boolean compulsoryBoolean = RestUtil.convertCompulsory(compulsory);
			result = CompetenceServiceWrapper.getCompetenceTreeForCourseNoFilter(selectedCatchwords, selectedOperators, course, compulsoryBoolean);
		}

		Response response = RestUtil.buildCachedResponse(result, cache.equals("cache"));
		return response;
	}

	/**
	 * Get the GUI operator tree
	 * 
	 * @param course
	 * @param cache
	 * @param selectedCatchwords
	 * @param selectedOperators
	 * @return
	 */
	@Produces(MediaType.APPLICATION_XML)
	@GET
	@Path("/operatortree/university/{cache}")
	public Response getOperatorTreeForUniversity(@PathParam("cache") String cache, @QueryParam(value = "selectedCatchwords") List<String> selectedCatchwords,
			@QueryParam(value = "selectedOperators") List<String> selectedOperators) {

		OperatorXMLTree[] result = CompetenceServiceWrapper.getOperatorTree(selectedCatchwords, selectedOperators, "university");
		Response response = RestUtil.buildCachedResponse(result, cache.equals("cached"));
		return response;
	}

	/**
	 * Get the GUI operator tree
	 * 
	 * @param course
	 * @param cache
	 * @param selectedCatchwords
	 * @param selectedOperators
	 * @return
	 */
	@Produces(MediaType.APPLICATION_XML)
	@GET
	@Path("/operatortree/course/{cache}/{course}")
	public Response getOperatorTree(@PathParam("course") String course, @PathParam("cache") String cache, @QueryParam(value = "selectedCatchwords") List<String> selectedCatchwords,
			@QueryParam(value = "selectedOperators") List<String> selectedOperators) {

		OperatorXMLTree[] result = CompetenceServiceWrapper.getOperatorTree(selectedCatchwords, selectedOperators, course);

		Response response = RestUtil.buildCachedResponse(result, cache.equals("cached"));
		return response;
	}

	/**
	 * Get the GUI Catchword Tree
	 * 
	 * @param course
	 * @param cache
	 * @param selectedCatchwords
	 * @param selectedOperators
	 * @return
	 */
	@Produces(MediaType.APPLICATION_XML)
	@GET
	@Path("/catchwordtree/{course}/{cache}")
	public Response getCatchwordTree(@PathParam("course") String course, @PathParam("cache") String cache, @QueryParam(value = "selectedCatchwords") List<String> selectedCatchwords,
			@QueryParam(value = "selectedOperators") List<String> selectedOperators) {
		CatchwordXMLTree[] result = CompetenceServiceWrapper.getCatchwordTree(selectedCatchwords, selectedOperators, course);
		Response response = RestUtil.buildCachedResponse(result, cache.equals("cached"));
		return response;
	}

	@Produces(MediaType.APPLICATION_XML)
	@GET
	@Path("/learningtemplates")
	public Response getAllLearningTemplates() {
		List<String> learningTemplates = new LinkedList<String>();
		// TODO implement getting the learning Templates from the
		CompOntologyManager compOntologyManager = new CompOntologyManager();
		compOntologyManager.begin();

		// OntClass learningProjectTemplateClass =
		// compOntologyManager.getUtil().getOntClassForString(CompOntClass.LearningProjectTemplate.name());
		// ExtendedIterator<? extends OntResource> instances =
		// learningProjectTemplateClass.listInstances();
		// while (instances.hasNext()) {
		// OntResource res = instances.next();
		// OntProperty iProperty =
		// compOntologyManager.getM().getOntProperty(MagicStrings.PREFIX +
		// "definition");
		// RDFNode value = res.getPropertyValue(iProperty);
		// if (value == null) {
		// } else {
		// learningTemplates.add(value.asNode().getLiteralValue().toString());
		// }
		// }

		List<String> result = compOntologyManager.getUtil().getAllInstanceDefinitions(CompOntClass.LearningProjectTemplate);
		learningTemplates.addAll(result);

		compOntologyManager.close();
		Response response = RestUtil.buildCachedResponse(new StringList(learningTemplates), true);
		return response;
	}

	@Produces(MediaType.APPLICATION_XML)
	@POST
	@Path("/learningtemplates/add")
	public Response addLearningTemplateSelection(@QueryParam(value = "userId") String userName, @QueryParam(value = "groupId") String groupId,
			@QueryParam(value = "selectedTemplate") String selectedTemplate) {

		CompOntologyManager compOntologyManager = initManagerInCriticalMode();
		CourseContext context = new CourseContext(compOntologyManager, groupId);
		User user = new User(compOntologyManager, userName, new TeacherRole(compOntologyManager), context, userName);
		SelectedLearningProjectTemplate selected = new SelectedLearningProjectTemplate(compOntologyManager, user, context, null, null);
		selected.persist();
		LearningProjectTemplate learningTemplate = new LearningProjectTemplate(compOntologyManager, selectedTemplate, null, selectedTemplate);
		selected.addAssociatedTemplate(learningTemplate);
		closeManagerInCriticalMode(compOntologyManager);
		return Response.ok("templateSelection updated").build();
	}

	@Produces(MediaType.APPLICATION_XML)
	@GET
	@Path("/learningtemplates/selected")
	public Response getSelectedLearningTemplates(@QueryParam(value = "userId") String userName, @QueryParam(value = "groupId") String groupId) {
		CompOntologyManager compOntologyManager = initManagerInCriticalMode();
		CourseContext context = new CourseContext(compOntologyManager, groupId);
		User user = new User(compOntologyManager, userName, new TeacherRole(compOntologyManager), context, userName);
		SelectedLearningProjectTemplate selected = new SelectedLearningProjectTemplate(compOntologyManager, user, context, null, null);
		StringList result = selected.getAssociatedTemplatesAsStringList();
		closeManagerInCriticalMode(compOntologyManager);
		return RestUtil.buildCachedResponse(result, false);
	}

	@Produces(MediaType.APPLICATION_XML)
	@POST
	@Path("/learningtemplates/delete")
	public Response deleteSelectedLearningTemplate(@QueryParam(value = "userId") String userName, @QueryParam(value = "groupId") String groupId,
			@QueryParam(value = "selectedTemplate") String selectedTemplate) {
		CompOntologyManager compOntologyManager = initManagerInCriticalMode();
		CourseContext context = new CourseContext(compOntologyManager, groupId);
		User user = new User(compOntologyManager, userName, new TeacherRole(compOntologyManager), context, userName);
		SelectedLearningProjectTemplate selected = new SelectedLearningProjectTemplate(compOntologyManager, user, context, null, null);
		LearningProjectTemplate learningTemplate = new LearningProjectTemplate(compOntologyManager, selectedTemplate, null, selectedTemplate);
		selected.removeAssociatedTemplate(learningTemplate);
		closeManagerInCriticalMode(compOntologyManager);
		return Response.ok("templateSelection updated after delete").build();
	}

	@Produces(MediaType.APPLICATION_XML)
	@GET
	@Path("learningtemplates/gridview")
	public Response getGridView(@QueryParam(value = "userId") String userName, @QueryParam(value = "groupId") String groupId, @QueryParam(value = "selectedTemplate") String selectedTemplate) {
		if (selectedTemplate.equals("test")) {
			return dummyGridResult();

		}
		CompOntologyManager compOntologyManager = new CompOntologyManager();
		compOntologyManager.begin();
		CourseContext context = new CourseContext(compOntologyManager, groupId);
		User user = new User(compOntologyManager, userName, new TeacherRole(compOntologyManager), context, userName);
		LearningProjectTemplate learningTemplate = new LearningProjectTemplate(compOntologyManager, selectedTemplate, null, null);
		if (!learningTemplate.exists()) {
			return dummyGridResult();
		}
		SuggestedCompetenceGrid result = Ont2SuggestedCompetenceGrid.convertToTwoDimensionalGrid(compOntologyManager, learningTemplate, user);
		compOntologyManager.close();
		return RestUtil.buildCachedResponse(result, false);
	}

	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_XML)
	@POST
	@Path("learningtemplates/gridview/update")
	public Response updateGridView(@QueryParam(value = "userId") String userName, @QueryParam(value = "groupId") String groupId, ReflectiveAssessmentsListHolder reflectiveAssessmentHolder) {
		CompOntologyManager compOntologyManager = initManagerInCriticalMode();
		CourseContext context = new CourseContext(compOntologyManager, groupId);

		User user = new User(compOntologyManager, userName, new TeacherRole(compOntologyManager), context, userName);
		ReflectiveAssessmentHolder2Ont.convert(compOntologyManager, user, context, reflectiveAssessmentHolder);
		closeManagerInCriticalMode(compOntologyManager);
		return Response.ok("reflexion updated").build();
	}

	private Response dummyGridResult() {
		SuggestedCompetenceGrid result = new SuggestedCompetenceGrid();
		SuggestedCompetenceRow row = new SuggestedCompetenceRow();
		SuggestedCompetenceColumn column = new SuggestedCompetenceColumn();
		column.setProgressInPercent(33);
		column.setTestOutput("deimudday is here");
		row.setSuggestedCompetenceColumns(Collections.singletonList(column));
		result.setSuggestedCompetenceRows(Collections.singletonList(row));
		return RestUtil.buildCachedResponse(result, false);
	}
}