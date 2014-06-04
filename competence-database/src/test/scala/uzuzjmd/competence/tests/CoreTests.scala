package uzuzjmd.competence.tests

import scala.collection.JavaConverters.seqAsJavaListConverter
import org.junit.AfterClass
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import uzuzjmd.competence.owl.access.CompFileUtil
import uzuzjmd.competence.owl.access.CompOntologyManager
import uzuzjmd.competence.owl.dao.StudentRole
import uzuzjmd.competence.owl.dao.TeacherRole
import org.scalatest.junit.JUnitRunner
import org.specs2.specification.After
import org.specs2.mutable.After
import uzuzjmd.competence.owl.dao.User
import uzuzjmd.competence.owl.ontology.CompObjectProperties
import uzuzjmd.competence.owl.ontology.CompOntClass
import uzuzjmd.competence.owl.dao.Role
import uzuzjmd.competence.owl.dao.Comment
import uzuzjmd.competence.owl.dao.EvidenceActivity
import uzuzjmd.competence.owl.dao.AbstractEvidenceLink
import uzuzjmd.competence.owl.dao.StudentRole
import uzuzjmd.competence.owl.dao.CourseContext
import uzuzjmd.competence.owl.dao.TeacherRole
import uzuzjmd.competence.owl.dao.Competence
import uzuzjmd.competence.owl.dao.AbstractEvidenceLink
import uzuzjmd.competence.owl.dao.CourseContext

@RunWith(classOf[JUnitRunner])
class CoreTests extends FunSuite with ShouldMatchers {

  test("if a user is persisted, the course context should be acessable") {

    val compOntManag = new CompOntologyManager()
    compOntManag.begin()

    val teacherRole = new TeacherRole(compOntManag)
    val coursecontext = new CourseContext(compOntManag, "2")
    coursecontext.persist
    val user = new User(compOntManag, "me", teacherRole, coursecontext, "Julian Dehne")
    user.persist()
    val user2 = new User(compOntManag, "me")
    val fullUser = user2.getFullDao
    fullUser.equals(user) should not be false
    user.delete
    coursecontext.delete
    compOntManag.close()
  }

  test("the singletondao should persist without error") {
    val compOntManag = new CompOntologyManager()
    compOntManag.begin()
    val studentRole = new StudentRole(compOntManag)
    studentRole.persist(true)
    studentRole.persist(false).getIndividual() should not be null
    studentRole.persist(false).getOntclass() should not be null
    val teacherRole = new TeacherRole(compOntManag)
    teacherRole.persist(true)
    teacherRole.persist(false).getIndividual() should not be null
    teacherRole.persist(false).getOntclass() should not be null
    compOntManag.close()
    showResult
  }

  test("the regular dao should persist without error") {
    val compOntManag = new CompOntologyManager()
    compOntManag.begin()
    val teacherRole = new TeacherRole(compOntManag)
    val coursecontext = new CourseContext(compOntManag, "2")
    val user = new User(compOntManag, "me", teacherRole, coursecontext, "me")
    user.persist
    user.exists should not be false
    user.delete
    user.exists should not be true
    compOntManag.close()
    showResult
  }

  test("if a dao is linked the link should exist") {
    val compOntManag = new CompOntologyManager()
    compOntManag.begin()
    val teacherRole = new TeacherRole(compOntManag)
    val coursecontext = new CourseContext(compOntManag, "2")
    val user = new User(compOntManag, "me", teacherRole, coursecontext, "me")
    user.persist()
    user.exists should not be false
    teacherRole.hasEdge(CompObjectProperties.RoleOf, user) should not be false
    user.delete
    user.exists should not be true
    teacherRole.hasEdge(CompObjectProperties.RoleOf, user) should not be true
    user.exists should not be true
    user.persist
    teacherRole.hasEdge(CompObjectProperties.RoleOf, user) should not be false
    teacherRole.deleteEdge(CompObjectProperties.RoleOf, user)
    user.exists should not be false
    teacherRole.hasEdge(CompObjectProperties.RoleOf, user) should not be true
    compOntManag.close()
    showResult
  }

  test("if a comment is persisted it should have its datafields in place") {
    val compOntManag = new CompOntologyManager()
    compOntManag.begin()
    val testkommentar = "mein testkommentar"
    val teacherRole = new TeacherRole(compOntManag)
    val coursecontext = new CourseContext(compOntManag, "2")
    val user = new User(compOntManag, "me", teacherRole, coursecontext, "me")
    val comment = new Comment(compOntManag, testkommentar, user, System.currentTimeMillis())
    comment.persist
    comment.exists should not be false
    val testkommentar2 = comment.getDataField(comment.TEXT)
    comment.getDataField(comment.TEXT).equals(testkommentar) should not be false
    (comment.getDataField(comment.DATECRATED) != null) should not be false
    comment.deleteDataField(comment.TEXT)
    (comment.getDataField(comment.TEXT) != null) should not be true
    comment.hasDataField(comment.TEXT) should not be true
    compOntManag.close()
    showResult
  }

  test("if a evidence link is created this should not cause errors") {
    val compOntManag = new CompOntologyManager()
    compOntManag.begin()
    val studentRole = new StudentRole(compOntManag)
    val coursecontext = new CourseContext(compOntManag, "2")
    val userstudent = new User(compOntManag, "student meäää 10AA", studentRole, coursecontext, "student meäää 10AA")
    val testkommentar = "mein testkommentar"
    val comment = new Comment(compOntManag, testkommentar, userstudent, System.currentTimeMillis())
    val testkommentar2 = "mein testkommentar2"
    val comment2 = new Comment(compOntManag, testkommentar2, userstudent, System.currentTimeMillis())
    val teacherRole = new TeacherRole(compOntManag)
    val user = new User(compOntManag, "me", teacherRole, coursecontext)

    val competence = new Competence(compOntManag, "Die Lehramtsanwärter kooperieren mit Kolleginnen und Kollegen bei der  Erarbeitung von Beratung/Empfehlung")
    val evidenceActivity = new EvidenceActivity(compOntManag, "http://testest", "meine testaktivitat")
    val link = new AbstractEvidenceLink(compOntManag, user.name + evidenceActivity.printableName, user, userstudent, coursecontext, (comment :: comment2 :: Nil), evidenceActivity, System.currentTimeMillis(), false, competence)
    link.persist
    link.exists should not be false
    link.delete
    evidenceActivity.delete
    evidenceActivity.exists should not be true
    compOntManag.close()
    showResult
  }

  test("if a string is given the identified full dao should be returnable") {
    val compOntManag = new CompOntologyManager()
    compOntManag.begin()
    val linkId = "hellolinkId"
    val studentRole = new StudentRole(compOntManag)
    val coursecontext = new CourseContext(compOntManag, "2")
    val userstudent = new User(compOntManag, "student meäää 10AA", studentRole, coursecontext, "student meäää 10AA")
    val link = createAbstract(compOntManag, linkId, userstudent)

    // now getting it by example
    val exampleLink = new AbstractEvidenceLink(compOntManag, linkId)
    val fullExampleLink = exampleLink.getFullDao

    fullExampleLink.creator should not be null
    fullExampleLink.getAllActivities should not be ('empty)
    fullExampleLink.getAllCourseContexts should not be ('empty)
    fullExampleLink.getAllLinkedUsers should not be ('empty)
    val linkedUser = fullExampleLink.getAllLinkedUsers.head
    //    linkedUser.equals(userstudent) should not be false
    link.delete

    compOntManag.close()
    showResult
  }

  test("if a requires b and b requires c, a should require c") {
    val compOntManag = new CompOntologyManager()
    compOntManag.begin()
    compOntManag.getM().enterCriticalSection(false);
    compOntManag.startReasoning();
    val competenceA = new Competence(compOntManag, "Die Lehramtsanwärter kooperieren mit Kolleginnen und Kollegen bei der  Erarbeitung von Beratung/Empfehlung")
    val competenceB = new Competence(compOntManag, "Die Lehramtsanwärter erkennen Entwicklungsstände, Lernpotentiale, Lernhindernisseund Lernfortschritte")
    val competenceC = new Competence(compOntManag, "Die Lehramtsanwärter beschreiben den Lernstand der SuS und ihren eigenen Wissensstand.")
    competenceC.addRequiredCompetence(competenceB)
    competenceB.addRequiredCompetence(competenceA)
    competenceC.hasEdge(competenceA, CompObjectProperties.PrerequisiteOf) should not be false
    compOntManag.close()
    showResult
  }

  test("testing the rules") {
    val compOntManag = new CompOntologyManager()
    compOntManag.begin()
    compOntManag.getM().enterCriticalSection(false);
    compOntManag.startReasoning();
    compOntManag.switchOffDebugg();
    
    val courseContext = new CourseContext(compOntManag, "n2");            
    val competenceA = new Competence(compOntManag, "Die Lehramtsanwärter kooperieren mit Kolleginnen und Kollegen bei der  Erarbeitung von Beratung/Empfehlung")
    courseContext.createEdgeWith(CompObjectProperties.CourseContextOf, competenceA)
    val competenceB = new Competence(compOntManag, "Die Lehramtsanwärter erkennen Entwicklungsstände, Lernpotentiale, Lernhindernisseund Lernfortschritte")
    courseContext.createEdgeWith(CompObjectProperties.CourseContextOf, competenceB)
    val competenceC = new Competence(compOntManag, "Die Lehramtsanwärter beschreiben den Lernstand der SuS und ihren eigenen Wissensstand.")
    courseContext.createEdgeWith(CompObjectProperties.CourseContextOf, competenceB)
    competenceC.addRequiredCompetence(competenceB)
    competenceB.addRequiredCompetence(competenceA)
    // testing basic transition
    competenceC.hasEdge(competenceA, CompObjectProperties.PrerequisiteOf) should not be false

    // testing not transition 1
    competenceB.addNotRequiredCompetence(competenceA)
    competenceC.hasEdge(competenceA, CompObjectProperties.PrerequisiteOf) should not be true

    // testing basic transition
    competenceB.addRequiredCompetence(competenceA)
    competenceC.hasEdge(competenceA, CompObjectProperties.PrerequisiteOf) should not be false

    // testing not transition 2
    competenceC.addNotRequiredCompetence(competenceB)
    competenceB.hasEdge(competenceA, CompObjectProperties.PrerequisiteOf) should not be true

    // testing basic transition
    competenceC.addRequiredCompetence(competenceB)
    competenceB.addRequiredCompetence(competenceA)
    competenceC.hasEdge(competenceA, CompObjectProperties.PrerequisiteOf) should not be false

    // testing the create link transition
    val linkId = "hellolinkId"
    val studentRole = new StudentRole(compOntManag)
    val coursecontext = new CourseContext(compOntManag, "2")
    val userstudent = new User(compOntManag, "student meäää 10AA", studentRole, coursecontext, "student meäää 10AA")
    val link = createAbstract(compOntManag, linkId, userstudent)

    compOntManag.close()
    showResult
  }

  private def showResult() {
    val compOntManag = new CompOntologyManager()
    compOntManag.begin()
    val fileUtil = new CompFileUtil(compOntManag.getM())
    fileUtil.writeOntologyout()
    compOntManag.close()
  }

  private def createAbstract(compOntManag: CompOntologyManager, linkId: String, userstudent: User): AbstractEvidenceLink = {
    val coursecontext = new CourseContext(compOntManag, "2")
    val teacherRole = new TeacherRole(compOntManag)
    val user = new User(compOntManag, "me", teacherRole, coursecontext, "me")
    user.persist
    val user2 = new User(compOntManag, "me")
    val fullUser = user2.getFullDao
    fullUser.role.equals(teacherRole) should not be false
    // and now a more complicated example    
    val testkommentar = "mein testkommentar"
    val comment = new Comment(compOntManag, testkommentar, userstudent, System.currentTimeMillis())
    val testkommentar2 = "mein testkommentar2"
    val comment2 = new Comment(compOntManag, testkommentar2, userstudent, System.currentTimeMillis())

    val evidenceActivity = new EvidenceActivity(compOntManag, "http://testest", "meine testaktivitat")
    val competence = new Competence(compOntManag, "Die Lehramtsanwärter kooperieren mit Kolleginnen und Kollegen bei der  Erarbeitung von Beratung/Empfehlung")
    competence.createEdgeWith(coursecontext, CompObjectProperties.CourseContextOf)
    val link = new AbstractEvidenceLink(compOntManag, linkId, user, userstudent, coursecontext, (comment :: comment2 :: Nil), evidenceActivity, System.currentTimeMillis(), false, competence)
    link.persist
    comment.hasEdge(userstudent, CompObjectProperties.UserOfComment) should not be false
    comment2.hasEdge(userstudent, CompObjectProperties.UserOfComment) should not be false
    return link
  }

  //  test("A non-empty list should not be empty") {
  //    List(1, 2, 3) should not be ('empty)
  //    List("fee", "fie", "foe", "fum") should not be ('empty)
  //  }
  //
  //  test("A list's length should equal the number of elements it contains") {
  //    List() should have length (0)
  //    List(1, 2) should have length (2)
  //    List("fee", "fie", "foe", "fum") should have length (4)
  //  }
}

