package uzuzjmd.competence.service.rest

import com.google.common.collect.Lists
import org.junit.Assert._
import org.junit.{After, Before, BeforeClass, Test}
import uzuzjmd.competence.config.{MagicStrings, Logging}
import uzuzjmd.competence.mapper.rest.read.{Ont2SuggestedCompetenceGrid, Ont2CompetenceGraph, Ont2CompetenceTree}
import uzuzjmd.competence.mapper.rest.write._
import uzuzjmd.competence.persistence.abstractlayer.WriteTransactional
import uzuzjmd.competence.persistence.dao._
import uzuzjmd.competence.persistence.ontology.Edge
import uzuzjmd.competence.service.rest.dto._
import uzuzjmd.competence.shared.{SuggestedCompetenceGrid, ReflectiveAssessment, Assessment, ReflectiveAssessmentsListHolder}
import uzuzjmd.competence.shared.dto.{HierarchyChange, HierarchyChangeSet}

import scala.collection.JavaConverters._

/**
  * Created by dehne on 14.12.2015.
  */
object CompetenceServiceImplTest {
  @BeforeClass
  @throws(classOf[Exception])
  def setUpBeforeClass {
    //TestCommons.setup
  }
}

/**
  * TEST CLASS for the JSON interface
  */
class CompetenceServiceImplTest extends WriteTransactional[Any] with Logging {
  private var jsonService: CompetenceServiceImpl = null

  val assertEmptyDatabse = true;

  logger.info("using databaseendpoint:" + MagicStrings.NEO4JURL + "\n")

  @Before
  @throws(classOf[Exception])
  def setUp {
    this.jsonService = new CompetenceServiceImpl
  }

  @After
  @throws(classOf[Exception])
  def tearDown {
  }

 

  @Test
  @throws(classOf[Exception])
  def testUpdateHierarchy {
    val competenceA: String = "I know how to program hierarchies"
    val competenceB: String = "I know how to program"
    val competenceC: String = "I know little"
    val changes: HierarchyChangeSet = new HierarchyChangeSet
    val hierarchyChange: HierarchyChange = new HierarchyChange(competenceB, competenceC, competenceA)
    changes.setElements(Lists.newArrayList(hierarchyChange))
    this.jsonService.updateHierarchie2(changes)

    // validate
    updateHierarchyAssertions(changes)

    // clean
    cleanUpdateHierarchyAssertions(changes)
  }

  def updateHierarchyAssertions( changes: HierarchyChangeSet): Unit = {
    val hierarchyChange = changes.getElements.get(0)
    val competenceADAO = new Competence(hierarchyChange.getNodeSelected)
    val competenceBDAO = new Competence(hierarchyChange.getNewClass)
    val competenceCDAO = new Competence(hierarchyChange.getOldClass)
    assertTrue(competenceADAO.isSubClassOf(competenceBDAO))
    assertFalse(competenceADAO.isSubClassOf(competenceCDAO))
  }

  def cleanUpdateHierarchyAssertions( changes: HierarchyChangeSet): Unit = {
    val hierarchyChange = changes.getElements.get(0)
    val competenceADAO = new Competence(hierarchyChange.getNodeSelected)
    val competenceBDAO = new Competence(hierarchyChange.getNewClass)
    val competenceCDAO = new Competence(hierarchyChange.getOldClass)
    competenceADAO.delete()
    competenceBDAO.delete()
    competenceCDAO.delete()
  }

  @Test
  @throws(classOf[Exception])
  def testLinkCompetencesToCourseContext: Unit = {
    // TODO write
  }

  @Test
  @throws(classOf[Exception])
  def testCreateUser: Unit = {
    // TODO write
  }

  @Test
  @throws(classOf[Exception])
  def testDeleteCourseContext: Unit = {
    assertTrue(true) // has been tested in create User
  }

  @Test
  @throws(classOf[Exception])
  def testGetRequirements: Unit = {
    // TODO implement (not prio)
  }

  @Test
  @throws(classOf[Exception])
  def testGetSelected: Unit = {
    // not testing deprecated method
  }

  @Test
  @throws(classOf[Exception])
  def testGetSelected2: Unit = {
    testGetSelectedCreateContexts
    val course = "university"
    val result = Ont2CompetenceTree.getCompetenceTree(new CompetenceTreeFilterData(course))
    assertFalse(result.isEmpty)
    testGetSelectedDeleteContexts
  }

  def testGetSelectedCreateContexts(): Unit = {
    val competence = new Competence( "TestKompetenz")
    competence.persist
  }

  def testGetSelectedDeleteContexts(): Unit = {
    val competence = new Competence( "TestKompetenz")
    competence.delete()
  }

  @Test
  @throws(classOf[Exception])
  def testLinkCompetencesToUser: Unit ={
    assertTrue(true)
  }


  @Test
  def testUpdateGridView : Unit = {
    val competence1 = new Competence("java1", null, (new Catchword("java") :: Nil).asJava);
    competence1.persist()

    val competence2 = new Competence("java2", null, (new Catchword("java") :: Nil).asJava);
    competence2.persist()

    val user = new uzuzjmd.competence.persistence.dao.User("Julian")
    user.persist()

    val courseContext = new CourseContext("university")
    courseContext.persist()

    val myTestLearningProject= new LearningProjectTemplate("TestLernprojekt")
    myTestLearningProject.persist()
    myTestLearningProject.addCompetenceToProject(competence1)
    myTestLearningProject.addCompetenceToProject(competence2)

    val holder = new ReflectiveAssessmentsListHolder
    holder.setSuggestedMetaCompetence("java")
    holder.setAssessment(new Assessment)
    val reflectiveAssessment1 = new ReflectiveAssessment
    reflectiveAssessment1.setIsLearningGoal(false)
    reflectiveAssessment1.setAssessment("gut")
    reflectiveAssessment1.setCompetenceDescription("java2")

    val reflectiveAssessment2 = new ReflectiveAssessment
    reflectiveAssessment2.setIsLearningGoal(false)
    reflectiveAssessment2.setAssessment("schlecht")
    reflectiveAssessment2.setCompetenceDescription("java1")

    val assessmentList = reflectiveAssessment1 :: reflectiveAssessment2 :: Nil
    holder.setReflectiveAssessmentList(assessmentList.asJava)

    val assessmentChangeData = new ReflectiveAssessmentChangeData("Julian", "university", holder)
    ReflectiveAssessmentHolder2Ont.convert(assessmentChangeData)

    val data: LearningTemplateData = new LearningTemplateData(user.getId, courseContext.getId, "TestLernprojekt")
    val result: SuggestedCompetenceGrid = Ont2SuggestedCompetenceGrid.convert(data)

    val index = result.getSuggestedCompetenceRows.get(0).getSuggestedCompetenceColumns.get(0).getProgressInPercent;
    printf("index is" + index)
    assertNotNull(result)
    assertTrue(index > 0)
  }

  @Test
  @throws(classOf[Exception])
  def testCommentCompetence: Unit = {
    // TODO write
  }

  @Test
  @throws(classOf[Exception])
  def testValidateLink: Unit = {
    val linkId = setupValidationContext
    val isValid = true
    HandleLinkValidationInOnt.convert(new LinkValidationData(linkId, isValid))
    validateValidation
    deleteValidationContext
  }


  @Test
  @throws(classOf[Exception])
  def testInvalidateLink: Unit = {
    val linkId = setupValidationContext
    val isValid = false
    HandleLinkValidationInOnt.convert(new LinkValidationData(linkId, isValid))
    validateInValidation
    deleteValidationContext
  }


  def validateValidation = {
    // TODO write
  }

  def validateInValidation = {
    // TODO write
  }

  def setupValidationContext: String = {
    // TODO write
    return "false"
  }

  def deleteValidationContext(): Unit = {
    // TODO write
  }


  @Test
  @throws(classOf[Exception])
  def testDeleteLink: Unit = {
      assertTrue(true); // has been used in
  }

  @Test
  @throws(classOf[Exception])
  def testDeleteCompetence: Unit = {
    if (assertEmptyDatabse) {
      testDeleteCompetenceSetup
      testDeleteCompetenceDo
      testDeleteCompetenceAssertions
    }
  }

  def testDeleteCompetenceDo: Unit = {
    val competenceA: String = "I know how to program hierarchies"
    val competenceB: String = "I know how to program"
    val competenceC: String = "I know little"
    val competences = competenceA :: competenceB :: competenceC :: Nil
    DeleteCompetenceInOnt.convert(competences.asJava);
  }

  def testDeleteCompetenceSetup(): Unit = {
    val competenceA: String = "I know how to program hierarchies"
    val competenceB: String = "I know how to program"
    val competenceC: String = "I know little"
    val competenceADao = new Competence( competenceA);
    competenceADao.persist
    val competenceBDao = new Competence( competenceB);
    competenceBDao.persist
    val competenceCDao = new Competence( competenceC);
    competenceCDao.persist
  }

  def testDeleteCompetenceAssertions(): Unit = {
    val competenceA: String = "I know how to program hierarchies"
    val competenceB: String = "I know how to program"
    val competenceC: String = "I know little"
    val competenceADao = new Competence( competenceA);
    assertFalse(competenceADao.exists())
    val competenceBDao = new Competence( competenceB);
    assertFalse(competenceBDao.exists())
    val competenceCDao = new Competence( competenceC);
    assertFalse(competenceCDao.exists())
  }

  @Test
  @throws(classOf[Exception])
  def testDeleteCompetenceTree: Unit = {
    if(assertEmptyDatabse) {
      testDeleteCompetenceSetup
      val competenceA: String = "I know how to program hierarchies"
      val competenceB: String = "I know how to program"
      val competenceC: String = "I know little"
      val competences = competenceA :: competenceB :: competenceC :: Nil
      DeleteCompetenceTreeInOnt.convert(competences.asJava)
      testDeleteCompetenceTreeValidation
    }
  }

  def testDeleteCompetenceTreeSetup(): Unit = {
    val competenceA: String = "I know how to program hierarchies"
    val competenceB: String = "I know how to program"
    val competenceC: String = "I know little"
    val competenceADao = new Competence( competenceA);
    competenceADao.persist
    val competenceBDao = new Competence( competenceB);
    competenceBDao.persist
    val competenceCDao = new Competence( competenceC);
    competenceCDao.persist
    competenceADao.addSuperCompetence(competenceBDao)
    competenceBDao.addSuperCompetence(competenceCDao)
  }

  def testDeleteCompetenceTreeValidation(): Unit = {
    val competenceA: String = "I know how to program hierarchies"
    val competenceB: String = "I know how to program"
    val competenceC: String = "I know little"
    val competenceADao = new Competence( competenceA);
    val competenceBDao = new Competence( competenceB);
    val competenceCDao = new Competence( competenceC);
    assertFalse(competenceADao.exists())
    assertFalse(competenceBDao.exists())
    assertFalse(competenceCDao.exists())
  }


  @Test
  @throws(classOf[Exception])
  def testGetCompetenceLinksMap: Unit = {
    // TODO write
  }

  @Test
  @throws(classOf[Exception])
  def testGetProgressM: Unit = {
    // TODO write
  }



  @Test
  @throws(classOf[Exception])
  def testCreatePrerequisite: Unit = {
    val competenceA: String = "I know how to program hierarchies"
    val competenceB: String = "I know how to program"
    val competenceC: String = "I know little"
    val competences = (competenceB :: competenceC :: Nil).asJava
    val course = new CourseContext( "university")
    testCreatePrerequisiteTestSetup
    CreatePrerequisiteInOnt.convert(new PrerequisiteData(course.getId, competenceA, competences))
    testCreatePrerequisiteAssertions
    if(assertEmptyDatabse) {
      testDeleteCompetencePrerequisiteCleanup
    }
  }

  def testDeleteCompetencePrerequisiteCleanup(): Unit = {
    val competenceA: String = "I know how to program hierarchies"
    val competenceB: String = "I know how to program"
    val competenceC: String = "I know little"
    val competenceADao = new Competence( competenceA);
    competenceADao.delete()
    val competenceBDao = new Competence( competenceB);
    competenceBDao.delete()
    val competenceCDao = new Competence( competenceC);
    competenceCDao.delete()

    assertFalse(competenceADao.exists())
    assertFalse(competenceBDao.exists())
    assertFalse(competenceCDao.exists())
  }

  def testCreatePrerequisiteAssertions(): Unit = {
    val competenceA: String = "I know how to program hierarchies"
    val competenceB: String = "I know how to program"
    val competenceC: String = "I know little"
    val competenceADao = new Competence(competenceA);

    val tmpResult1 = competenceADao.getRequiredCompetences().asScala.map(x=>x.getDefinition())
    assertTrue(tmpResult1.contains(competenceB))
    assertTrue(tmpResult1.contains(competenceC))
  }

  def testCreatePrerequisiteTestSetup(): Unit = {
    val course = new CourseContext( "university")
    course.persist()
    val competenceA: String = "I know how to program hierarchies"
    val competenceB: String = "I know how to program"
    val competenceC: String = "I know little"

    val competenceADao = new Competence( competenceA);
    competenceADao.persist
    val competenceBDao = new Competence( competenceB);
    competenceBDao.persist
    val competenceCDao = new Competence( competenceC);
    competenceCDao.persist

  }

  @Test
  @throws(classOf[Exception])
  def testDeletePrerequisite: Unit = {
    if(assertEmptyDatabse) {
      testCreatePrerequisiteTestSetup
      val competenceA: String = "I know how to program hierarchies"
      val competenceB: String = "I know how to program"
      val competenceC: String = "I know little"
      val competences = (competenceB :: competenceC :: Nil).asJava
      val course = new CourseContext("university")
      DeletePrerequisiteInOnt.convert(new PrerequisiteData(course.getId(), competenceA, competences))
      testDeletePrerequisiteAssertions //assertions
    }
  }

  def testDeletePrerequisiteAssertions(): Unit = {
    val competenceA: String = "I know how to program hierarchies"
    val competenceB: String = "I know how to program"
    val competenceC: String = "I know little"

    val competenceADao = new Competence(competenceA);
    val competenceBDao = new Competence(competenceB);
    val competenceCDao = new Competence(competenceC);
    assertFalse(competenceADao.getRequiredCompetences().contains(competenceBDao))
    assertFalse(competenceADao.getRequiredCompetences().contains(competenceCDao))
    competenceADao.delete()
    competenceBDao.delete()
    competenceCDao.delete()

    assertFalse(competenceADao.exists())
    assertFalse(competenceBDao.exists())
    assertFalse(competenceCDao.exists())
  }




  @Test
  @throws(classOf[Exception])
  def testGetPrerequisiteGraph: Unit = {
    testCreatePrerequisiteTestSetup
    val competenceA: String = "I know how to program hierarchies"
    val competenceB: String = "I know how to program"
    val competenceC: String = "I know little"
    val competences = (competenceB :: competenceC :: Nil).asJava
    val course = new CourseContext( "university")
    testCreatePrerequisiteTestSetup
    CreatePrerequisiteInOnt.convert(new PrerequisiteData(course.getId, competenceA, competences))
    val changes: GraphFilterData = new GraphFilterData("university", competenceA);
    val result = Ont2CompetenceGraph.convert(changes)
    assertNotNull(result)
    assertFalse(result.triples.isEmpty)
  }

  @throws(classOf[Exception])
  def testGetRequiredCompetences: Unit = {
    // implicitly tested
    assert(true)
  }

  @Test
  @throws(classOf[Exception])
  def testGetOperatorForCompetence: Unit = {
    testCreateOperatorPrerequisites
    testCreateOperatorValidations
    testCreateOperatorCleanUp
  }



  def testCreateOperatorPrerequisites() : Unit = {
    val competenceA : String = "I know how to program"
    val operator = "know"
    val catchwords = "know" :: "how" :: "I" :: "to":: Nil;
    val data: CompetenceData = new CompetenceData(operator, catchwords.asJava, null, null, null, competenceA)
    Competence2Ont.convert(data)
  }

  def testCreateOperatorValidations() : Unit = {
    val competenceA : String = "I know how to program"
    val operator = "know"
    val competenceDAO = new Competence( competenceA)
    val operatorDAO = new Operator( operator)
    assertTrue(competenceDAO.hasEdge(operatorDAO, Edge.OperatorOf))
  }

  def testCreateOperatorCleanUp() : Unit = {
    val competenceA : String = "I know how to program"
    val operator = "know"
    val competenceDAO = new Competence( competenceA)
    val operatorDAO = new Operator( operator)
    competenceDAO.delete()
    operatorDAO.delete()
  }

  @Test
  @throws(classOf[Exception])
  def testGetCatchwordsForCompetence: Unit = {
    testCreateOperatorPrerequisites
    testGetCatchwordsValidation
    testDeleteCatchwords
  }

  def testGetCatchwordsValidation(): Unit = {
    val competenceA : String = "I know how to program"
    val competenceDAO = new Competence( competenceA)
    assertNotNull(competenceDAO.getCatchwords())
  }

  def testDeleteCatchwords(): Unit = {
    val competenceA : String = "I know how to program"
    val competenceDAO = new Competence( competenceA)
    competenceDAO.getCatchwords().asScala.foreach(_.delete())
    competenceDAO.delete()
  }

  @Test
  @throws(classOf[Exception])
  def testAddCompetenceToModel: Unit = {
    // tested above
    assert(true)
  }

  @Test
  @throws(classOf[Exception])
  def testEditCompetenceToModel {
  }

  @Test
  @throws(classOf[Exception])
  def testCreateSuggestedCourseForCompetence {
  }

  @Test
  @throws(classOf[Exception])
  def testCreateSuggestedActivityForCompetence {
  }

  @Test
  @throws(classOf[Exception])
  def testDeleteSuggestedCourseForCompetence {
  }

  @Test
  @throws(classOf[Exception])
  def testDeleteSuggestedActivityForCompetence {
  }
}