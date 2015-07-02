package uzuzjmd.competence.service.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import uzuzjmd.competence.mapper.gui.HierarchieChangesToOnt;
import uzuzjmd.competence.mapper.gui.Ont2CompetenceGraph;
import uzuzjmd.competence.mapper.gui.Ont2CompetenceLinkMap;
import uzuzjmd.competence.mapper.gui.Ont2ProgressMap;
import uzuzjmd.competence.owl.access.CompOntologyManager;
import uzuzjmd.competence.owl.dao.AbstractEvidenceLink;
import uzuzjmd.competence.owl.dao.Catchword;
import uzuzjmd.competence.owl.dao.Comment;
import uzuzjmd.competence.owl.dao.Competence;
import uzuzjmd.competence.owl.dao.CourseContext;
import uzuzjmd.competence.owl.dao.DaoFactory;
import uzuzjmd.competence.owl.dao.EvidenceActivity;
import uzuzjmd.competence.owl.dao.Operator;
import uzuzjmd.competence.owl.dao.Role;
import uzuzjmd.competence.owl.dao.StudentRole;
import uzuzjmd.competence.owl.dao.TeacherRole;
import uzuzjmd.competence.owl.dao.User;
import uzuzjmd.competence.owl.ontology.CompObjectProperties;
import uzuzjmd.competence.owl.validation.CompetenceGraphValidator;
import uzuzjmd.competence.rcd.generated.Rdceo;
import uzuzjmd.competence.service.CompetenceServiceImpl;
import uzuzjmd.competence.service.rest.client.dto.CompetenceLinksMap;
import uzuzjmd.competence.service.rest.client.dto.Graph;
import uzuzjmd.competence.service.rest.client.dto.HierarchieChangeSet;
import uzuzjmd.competence.service.rest.client.dto.ProgressMap;

/**
 * Root resource (exposed at "competences" path)
 */
@Path("/competences/json")
public class CompetenceServiceRestJSON extends CompetenceOntologyInterface {

	/**
	 * Lists all competences in the RDCEO Standard Format
	 * 
	 * @return List of competences in RDCEO
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/all")
	public List<Rdceo> getRdceo() {
		System.out.println("Competences queried (rest)");
		// return "Got it!";
		CompetenceServiceImpl competenceServiceImpl = new CompetenceServiceImpl();
		return new ArrayList<Rdceo>(Arrays.asList(competenceServiceImpl.getCompetences()));
	}

	/**
	 * use /updateHierarchie2 instead
	 * 
	 * @param changes
	 * @return
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/updateHierarchie")
	@Deprecated
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateHierarchie(@QueryParam("changes") List<String> changes) {

		HierarchieChangeSet changeSet = new HierarchieChangeSet().convertListToModel(changes);

		CompOntologyManager comp = initManagerInCriticalMode();
		HierarchieChangesToOnt.convertChanges(comp, changeSet);
		comp.close();

		return Response.ok("updated taxonomy").build();
	}

	/**
	 * updates the competence hierarchy
	 * 
	 * @param changes
	 *            of type HierarchieChangeObject @see updateHierarchie2
	 * 
	 * @return
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/updateHierarchie2")
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateHierarchie2(@QueryParam("changes") HierarchieChangeSet changes) {

		CompOntologyManager comp = initManagerInCriticalMode();
		HierarchieChangesToOnt.convertChanges(comp, changes);
		comp.close();

		return Response.ok("updated taxonomy").build();
	}

	/**
	 * This is an example for the format needed for updating the hierarchie
	 * 
	 * @param changes
	 * @return
	 */
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/updateHierarchie2/example")
	@Produces(MediaType.APPLICATION_JSON)
	public HierarchieChangeSet updateHierarchieExample(@QueryParam("changes") HierarchieChangeSet changes) {
		return changes;
	}

	/**
	 * Link the competences to a course context.
	 * 
	 * This allows for selecting competences for a given context so that the
	 * application can deal with a subset of the competence database.
	 * 
	 * @param course
	 *            (the id of the course) or any name. Prefered id format is
	 *            number.
	 * @param compulsory
	 *            (optional) indicates whether the competence is compulsory for
	 *            the context in terms of passing the course.
	 * @param competences
	 *            the competences linked to the course
	 * 
	 * @param requirements
	 *            a plain text string explaining why this competences are
	 *            necessary for the course
	 * @return
	 */
	@Consumes(MediaType.APPLICATION_JSON)
	@POST
	@Path("/coursecontext/create/{course}/{compulsory}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response linkCompetencesToCourseContextJson(@PathParam("course") String course, @PathParam("compulsory") String compulsory,
			@QueryParam(value = "competences") final List<String> competences, @QueryParam(value = "requirements") String requirements) {

		Boolean compulsoryBoolean = RestUtil.convertCompulsory(compulsory);
		CompetenceServiceWrapper.linkCompetencesToCourse(course, competences, compulsoryBoolean, requirements);
		return Response.ok("competences linked to course").build();
	}

	/**
	 * Deletes the course context.
	 * 
	 * All competences linked to this context will be removed from it. This
	 * should be used as a companion with coursecontext/create
	 * 
	 * @param course
	 * @return
	 */
	@Consumes(MediaType.APPLICATION_JSON)
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/coursecontext/delete/{course}")
	public Response deleteCourseContextJSON(@PathParam("course") String course) {
		CompetenceServiceWrapper.delete(course);
		return Response.ok("competences deleted from course:" + course).build();
	}

	/**
	 * Get the description of requirements for the course.
	 * 
	 * The requirement string specifying why this subset of competences was
	 * selected for the course is returned.
	 * 
	 * @param course
	 *            the context of the competences
	 * @return the requirement string
	 */
	@Produces(MediaType.TEXT_PLAIN)
	@GET
	@Path("/coursecontext/requirements/{course}")
	public String getRequirements(@PathParam("course") String course) {
		String result = CompetenceServiceWrapper.getRequirements(course);
		return result;
	}

	/**
	 * Returns all the competences linked to a course context. It is deprecated
	 * /coursecontext/selected should be used.
	 * 
	 * @param course
	 * @return
	 */
	@Produces(MediaType.APPLICATION_JSON)
	@GET
	@Path("/selected/{course}")
	@Deprecated
	public String[] getSelected(@PathParam("course") String course) {
		return CompetenceServiceWrapper.getSelected(course);
	}

	/**
	 * Get competences linked to (course) context.
	 * 
	 * Returns all the competences linked to a course context.
	 * 
	 * @param course
	 * @return
	 */
	@Produces(MediaType.APPLICATION_JSON)
	@GET
	@Path("/coursecontext/selected/{course}")
	public String[] getSelected2(@PathParam("course") String course) {
		return CompetenceServiceWrapper.getSelected(course);
	}

	/**
	 * Creates an evidence as a proof that competences have been acquired by the
	 * user by certain activities
	 * 
	 * @param course
	 *            (the context of the acquirement)
	 * @param creator
	 *            the user who created the link
	 * @param role
	 *            the role of the user who created the link (can be either
	 *            "teacher" or "student")
	 * @param linkedUser
	 *            the user who has acquired the competences
	 * @param competences
	 *            the competences acquired
	 * @param evidences
	 *            the activities that stand as evidences
	 * @return
	 */
	@Consumes(MediaType.APPLICATION_JSON)
	@POST
	@Path("/link/create/{course}/{creator}/{role}/{linkedUser}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response linkCompetencesToUserJson(@PathParam("course") String course, @PathParam("creator") String creator, @PathParam("role") String role, @PathParam("linkedUser") String linkedUser,
			@QueryParam(value = "competences") List<String> competences, @QueryParam(value = "evidences") List<String> evidences) {

		CompOntologyManager compOntologyManager = initManagerInCriticalMode();
		Role creatorRole = convertRole(role, compOntologyManager);
		for (String evidence : evidences) {

			if (!evidences.contains(",")) {
				throw new WebApplicationException(new Exception("Evidences must be in Format: [Tool or Origin identifier],[link]"));
			}
			for (String competence : competences) {
				CourseContext courseContext = new CourseContext(compOntologyManager, course);
				User creatorUser = new User(compOntologyManager, creator, creatorRole, courseContext, creator);
				User linkedUserUser = new User(compOntologyManager, linkedUser, new StudentRole(compOntologyManager), courseContext, linkedUser);
				scala.collection.immutable.List<Comment> comments = null;
				EvidenceActivity evidenceActivity = new EvidenceActivity(compOntologyManager, evidence.split(",")[0], evidence.split(",")[1]);
				Competence competenceDao = new Competence(compOntologyManager, competence, null, null);
				AbstractEvidenceLink abstractEvidenceLink = new AbstractEvidenceLink(compOntologyManager, (evidence + competence), creatorUser, linkedUserUser, courseContext, comments,
						evidenceActivity, Long.valueOf(System.currentTimeMillis()), Boolean.valueOf(false), competenceDao);
				abstractEvidenceLink.persist();
			}
		}
		closeManagerInCriticalMode(compOntologyManager);
		return Response.ok("competences linked to evidences").build();
	}

	/**
	 * Add a comment to an evidence link
	 * 
	 * Have a look at @see linkCompetencesToUserJson in order to better
	 * understand the model of a evidence link.
	 * 
	 * 
	 * @param linkId
	 *            the id of the link
	 * @param user
	 *            the user who creates the comment
	 * @param text
	 *            the text of the comment
	 * @param courseContext
	 *            the course context the comment is created in
	 * @param role
	 *            the role of the user
	 * @return
	 */
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@POST
	@Path("/link/comment/{linkId}/{user}/{courseContext}/{role}")
	public Response commentCompetence(@PathParam("linkId") String linkId, @PathParam("user") String user, @QueryParam("text") String text, @PathParam("courseContext") String courseContext,
			@PathParam("role") String role) {

		createUserIfNotExists(user, courseContext, role);

		createComment(linkId, user, text, courseContext, role);
		return Response.ok("link commented").build();
	}

	private void createComment(String linkId, String user, String text, String courseContext, String role) {
		CompOntologyManager compOntologyManager = initManagerInCriticalMode();

		Role creatorRole2 = convertRole(role, compOntologyManager);
		CourseContext coursecontext2 = new CourseContext(compOntologyManager, courseContext);
		User creator2 = new User(compOntologyManager, user, creatorRole2, coursecontext2, user);
		AbstractEvidenceLink abstractEvidenceLink = DaoFactory.getAbstractEvidenceDao(compOntologyManager, linkId);
		Comment comment = new Comment(compOntologyManager, text, creator2, System.currentTimeMillis(), text);
		comment.persist();
		abstractEvidenceLink.linkComment(comment);

		closeManagerInCriticalMode(compOntologyManager);
	}

	private void createUserIfNotExists(String user, String courseContext, String role) {
		CompOntologyManager comp = initManagerInCriticalMode();
		Role creatorRole = convertRole(role, comp);
		creatorRole.persist(false);
		CourseContext coursecontext = new CourseContext(comp, courseContext);
		coursecontext.persist();
		User creator = new User(comp, user, creatorRole, coursecontext, user);
		creator.persist();
		comp.close();
	}

	private Role convertRole(String role, CompOntologyManager comp) {
		Role creatorRole = null;
		if (role.equals("student")) {
			creatorRole = new StudentRole(comp);
		} else {
			creatorRole = new TeacherRole(comp);
		}
		return creatorRole;
	}

	/**
	 * Validate an evidence link.
	 * 
	 * Have a look at {@link linkCompetencesToUserJson} for the nature of the
	 * evidence link.
	 * 
	 * This should only be done by teacher role (which should be checked in the
	 * frontend)
	 * 
	 * 
	 * @param linkId
	 * @return
	 */
	@Consumes(MediaType.APPLICATION_JSON)
	@POST
	@Path("/link/validate/{linkId}")
	public Response validateLink(@PathParam("linkId") String linkId) {
		Boolean isvalid = true;
		return handleLinkValidation(linkId, isvalid);
	}

	/**
	 * Invalidate a link
	 * 
	 * (this should only be done by teacher role (which should be checked in the
	 * frontend)
	 * 
	 * @param linkId
	 * @return
	 */
	@Consumes(MediaType.APPLICATION_JSON)
	@POST
	@Path("/link/invalidate/{linkId}")
	public Response invalidateLink(@PathParam("linkId") String linkId) {
		Boolean isvalid = false;
		return handleLinkValidation(linkId, isvalid);
	}

	/**
	 * Delete an evidence link
	 * 
	 * @param linkId
	 *            the id of the link to be deleted
	 * @return
	 */
	@Consumes(MediaType.APPLICATION_JSON)
	@POST
	@Path("/link/delete/{linkId}")
	public Response deleteLink(@PathParam("linkId") String linkId) {
		CompOntologyManager manager = initManagerInCriticalMode();
		AbstractEvidenceLink abstractEvidenceLink = new AbstractEvidenceLink(manager, linkId, null, null, null, null, null, null, null, null);
		abstractEvidenceLink.delete();
		manager.close();
		return Response.ok("link deleted").build();
	}

	/**
	 * Deletes one or more competences
	 * 
	 * @param competences
	 * @return
	 */
	@Consumes(MediaType.APPLICATION_JSON)
	@POST
	@Path("/competence/delete")
	public Response deleteCompetence(@QueryParam("competences") List<String> competences) {
		CompOntologyManager manager = initManagerInCriticalMode();

		System.out.println("deleting competences" + competences);

		for (String string : competences) {
			Competence toDelete = new Competence(manager, string, string, null);
			toDelete.delete();
		}

		manager.close();
		return Response.ok("competences deleted").build();
	}

	/**
	 * Deletes competences and all their subcompetences
	 * 
	 * @param competences
	 *            the competences to be deleted
	 * @return
	 */
	@Consumes(MediaType.APPLICATION_JSON)
	@POST
	@Path("/competence/deleteTree")
	public Response deleteCompetenceTree(@QueryParam("competences") List<String> competences) {
		CompOntologyManager manager = initManagerInCriticalMode();

		System.out.println("deleting competences" + competences);

		for (String string : competences) {
			Competence toDelete = new Competence(manager, string, string, null);
			toDelete.deleteTree();
		}

		manager.close();
		return Response.ok("competences deleted").build();
	}

	/**
	 * returns a map competence->evidences
	 * 
	 * Returns all the evidences for a user in a form that they can be presented
	 * 
	 * @param user
	 *            the user who has acquired the competences
	 * @return
	 */
	@GET
	@Path("/link/overview/{user}")
	@Produces(MediaType.APPLICATION_JSON)
	public CompetenceLinksMap getCompetenceLinksMap(@PathParam("user") String user) {
		CompOntologyManager comp = new CompOntologyManager();
		Ont2CompetenceLinkMap competenceLinkMap = new Ont2CompetenceLinkMap(comp, user);
		CompetenceLinksMap map = competenceLinkMap.getCompetenceLinkMap();
		// for (List<CompetenceLinksView> linkCollections :
		// map.getMapUserCompetenceLinks().values()) {
		// Collections.sort(linkCollections);
		// }
		return map;
	}

	/**
	 * Shows overview of the progress a user has made in a course
	 * 
	 * @param course
	 *            the course the overview is generated for
	 * @param selectedCompetences
	 *            a filter: the percentate of acquired competences is calculated
	 *            taking into account the competences visible to the user
	 * @return
	 */
	@GET
	@Path("/link/progress/{course}")
	@Produces(MediaType.APPLICATION_JSON)
	public ProgressMap getProgressM(@PathParam("course") String course, @QueryParam("competences") List<String> selectedCompetences) {

		ProgressMap result = null;
		CompOntologyManager comp = initManagerInCriticalMode();
		Ont2ProgressMap map = new Ont2ProgressMap(comp, course, selectedCompetences);
		result = map.getProgressMap();
		closeManagerInCriticalMode(comp);
		return result;
	}

	/**
	 * This creates a "prerquisite" relation between the
	 * selectedCompetences->linkedCompetences
	 * 
	 * @param course
	 *            the course context the link is created in (may be "university"
	 *            for global context")
	 * @param linkedCompetence
	 *            the pre competences
	 * @param selectedCompetences
	 *            the post competences
	 */
	@Consumes(MediaType.APPLICATION_JSON)
	@POST
	@Path("/prerequisite/create/{course}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response createPrerequisite(@PathParam("course") String course, @QueryParam("linkedCompetence") String linkedCompetence, @QueryParam("selectedCompetences") List<String> selectedCompetences) {
		CompOntologyManager compOntologyManager = initManagerInCriticalMode();
		compOntologyManager.startReasoning();
		compOntologyManager.switchOffDebugg();
		Competence competence = new Competence(compOntologyManager, linkedCompetence, null, null);
		for (String string : selectedCompetences) {
			Competence preCompetence = new Competence(compOntologyManager, string, null, null);
			competence.addRequiredCompetence(preCompetence);
		}
		compOntologyManager.close();
		return Response.ok("prerequisite created").build();
	}

	/**
	 * Deletes the "prerequisite" link between the competences
	 * 
	 * @param course
	 * 
	 *            the course context the link is created in (may be "university"
	 *            for global context")
	 * @param linkedCompetence
	 *            the pre competences
	 * @param selectedCompetences
	 *            the post competences
	 * @return
	 */
	@Consumes(MediaType.APPLICATION_JSON)
	@POST
	@Path("/prerequisite/delete/{course}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deletePrerequisite(@PathParam("course") String course, @QueryParam("linkedCompetence") String linkedCompetence, @QueryParam("competences") List<String> selectedCompetences) {
		CompOntologyManager compOntologyManager = initManagerInCriticalMode();
		compOntologyManager.startReasoning();
		compOntologyManager.switchOffDebugg();
		Competence competence = new Competence(compOntologyManager, linkedCompetence, null, null);
		if (selectedCompetences == null || selectedCompetences.isEmpty()) {
			selectedCompetences = new LinkedList<String>();
			for (String string : competence.getRequiredCompetencesAsArray()) {
				selectedCompetences.add(string);
			}
		}
		for (String string : selectedCompetences) {
			Competence preCompetence = new Competence(compOntologyManager, string, null, null);
			competence.addNotRequiredCompetence(preCompetence);
		}
		compOntologyManager.close();
		return Response.ok("prerequisite deleted").build();
	}

	/**
	 * returns the whole prerequisite graph (given filter)
	 * 
	 * @param selectedCompetences
	 *            the competences that selected that filter the graph
	 * @return
	 */
	@Consumes(MediaType.APPLICATION_JSON)
	@GET
	@Path("/prerequisite/graph/{course}")
	@Produces(MediaType.APPLICATION_JSON)
	public Graph getPrerequisiteGraph(@QueryParam("selectedCompetences") List<String> selectedCompetences, @PathParam("course") String course) {
		CompOntologyManager comp = new CompOntologyManager();
		Ont2CompetenceGraph mapper = new Ont2CompetenceGraph(comp, selectedCompetences, course);
		return mapper.getCompetenceGraph();
	}

	/**
	 * gets the prerequisites for the given competence
	 * 
	 * @param forCompetence
	 * @return
	 */
	@Consumes(MediaType.APPLICATION_JSON)
	@GET
	@Path("/prerequisite/required/{course}")
	@Produces(MediaType.APPLICATION_JSON)
	public String[] getRequiredCompetences(@QueryParam("competence") String forCompetence) {
		CompOntologyManager compOntologyManager = initManagerInCriticalMode();
		compOntologyManager.startReasoning();
		compOntologyManager.switchOffDebugg();
		Competence competence = new Competence(compOntologyManager, forCompetence, null, null);
		String[] requiredCompetences = competence.getRequiredCompetencesAsArray();
		compOntologyManager.close();
		return requiredCompetences;
	}

	/**
	 * GET the operator for a given competence
	 * 
	 * @param forCompetence
	 * @return
	 */
	@Consumes(MediaType.APPLICATION_JSON)
	@GET
	@Path("/operator")
	public String getOperatorForCompetence(@QueryParam("competence") String forCompetence) {
		CompOntologyManager comp = new CompOntologyManager();
		comp.begin();

		Competence competence = new Competence(comp, forCompetence, forCompetence, null);
		scala.collection.immutable.List<Operator> operators = competence.getAssociatedSingletonDaosAsDomain(CompObjectProperties.OperatorOf, Operator.class);

		String result = "";
		if (!operators.isEmpty()) {
			result = operators.head().getDefinition();
		}

		comp.close();
		return result;
	}

	/**
	 * Get all the catchwords for a given competence
	 * 
	 * @param forCompetence
	 * @return
	 */
	@Consumes(MediaType.APPLICATION_JSON)
	@GET
	@Path("/catchwords")
	public String getCatchwordsForCompetence(@QueryParam("competence") String forCompetence) {
		CompOntologyManager comp = new CompOntologyManager();
		comp.begin();

		Competence competence = new Competence(comp, forCompetence, forCompetence, null);
		List<Catchword> catchwords = competence.getCatchwordsAsJava();

		String result = "";
		if (!catchwords.isEmpty()) {
			for (Catchword catchword : catchwords) {
				result += catchword.getDefinition() + ",";
			}
			result = result.substring(0, result.length() - 1);
		}

		comp.close();
		return result;
	}

	/**
	 * add a competence to the model
	 * 
	 * @param forCompetence
	 * @param operator
	 * @param catchwords
	 * @param superCompetences
	 * @param subCompetences
	 * @return
	 */
	@Consumes(MediaType.APPLICATION_JSON)
	@POST
	@Path("/addOne")
	public Response addCompetenceToModel(@QueryParam("competence") String forCompetence, @QueryParam("operator") String operator, @QueryParam("catchwords") List<String> catchwords,
			@QueryParam("superCompetences") List<String> superCompetences, @QueryParam("subCompetences") List<String> subCompetences) {
		CompOntologyManager compOntologyManager = initManagerInCriticalMode();
		String resultMessage = addCompetence(forCompetence, operator, catchwords, superCompetences, subCompetences, compOntologyManager);
		closeManagerInCriticalMode(compOntologyManager);

		return Response.ok(resultMessage).build();
	}

	/**
	 * Edit competence metadata
	 * 
	 * (competence text may not be changed without changes in the hierarchy
	 * following)
	 * 
	 * @param forCompetence
	 * @param operator
	 * @param catchwords
	 * @param superCompetences
	 * @param subCompetences
	 * @param orgininalCompetence
	 * @return
	 */
	@Consumes(MediaType.APPLICATION_JSON)
	@POST
	@Path("/editOne")
	public Response editCompetenceToModel(@QueryParam("competence") String forCompetence, @QueryParam("operator") String operator, @QueryParam("catchwords") List<String> catchwords,
			@QueryParam("superCompetences") List<String> superCompetences, @QueryParam("subCompetences") List<String> subCompetences, @QueryParam("originalCompetence") String orgininalCompetence) {

		CompOntologyManager compOntologyManager = initManagerInCriticalMode();
		Competence original = new Competence(compOntologyManager, orgininalCompetence, orgininalCompetence, null);
		original.delete();
		String resultMessage = addCompetence(forCompetence, operator, catchwords, superCompetences, subCompetences, compOntologyManager);
		closeManagerInCriticalMode(compOntologyManager);

		return Response.ok(resultMessage).build();
	}

	private String addCompetence(String forCompetence, String operator, List<String> catchwords, List<String> superCompetences, List<String> subCompetences, CompOntologyManager compOntologyManager) {

		Competence addedCompetence = new Competence(compOntologyManager, forCompetence, forCompetence, null);
		List<Competence> superCompetencesTyped = new LinkedList<Competence>();
		for (String competence : superCompetences) {
			Competence superCompetence = new Competence(compOntologyManager, competence, competence, null);
			superCompetencesTyped.add(superCompetence);
		}
		List<Competence> subCompetencesTyped = new LinkedList<Competence>();
		for (String competence : subCompetences) {
			Competence subCompetence = new Competence(compOntologyManager, competence, competence, null);
			subCompetencesTyped.add(subCompetence);
		}

		CompetenceGraphValidator competenceGraphValidator = new CompetenceGraphValidator(compOntologyManager, addedCompetence, superCompetencesTyped, subCompetencesTyped);

		if (competenceGraphValidator.isValid()) {
			addedCompetence.persist(true);
			for (String catchwordItem : catchwords) {
				Catchword catchword = new Catchword(compOntologyManager, catchwordItem, catchwordItem);
				catchword.persist(true);
				catchword.createEdgeWith(CompObjectProperties.CatchwordOf, addedCompetence);

			}
			Operator operatorDAO = new Operator(compOntologyManager, operator, operator);
			operatorDAO.persist(true);
			operatorDAO.createEdgeWith(CompObjectProperties.OperatorOf, addedCompetence);
			// new Catchword(compOntologyManager, )

			// addedCompetence.createEdgeWith(, edgeType);
			// CompetenceInstance competenceRoot = new
			// CompetenceInstance(compOntologyManager);
			// addedCompetence.addSuperClass(competenceRoot);
			for (Competence subCompetence : subCompetencesTyped) {
				subCompetence.addSuperCompetence(addedCompetence);
			}

			for (Competence superCompetence : superCompetencesTyped) {
				addedCompetence.addSuperCompetence(superCompetence);
			}

		}
		String resultMessage = competenceGraphValidator.getExplanationPath();

		return resultMessage;
	}

	private Response handleLinkValidation(String linkId, Boolean isvalid) {
		CompOntologyManager compOntologyManager = initManagerInCriticalMode();

		AbstractEvidenceLink abstractEvidenceLink = DaoFactory.getAbstractEvidenceDao(compOntologyManager, linkId);
		abstractEvidenceLink.addDataField(abstractEvidenceLink.ISVALIDATED(), isvalid);

		compOntologyManager.close();

		return Response.ok("link updated").build();
	}

}