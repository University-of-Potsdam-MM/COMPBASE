package uzuzjmd.competence.tests

import scala.collection.JavaConverters.seqAsJavaListConverter
import org.junit.AfterClass
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import uzuzjmd.competence.mapper.gui.Ont2CompetenceTree
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
import uzuzjmd.competence.mapper.gui.Ont2CompetenceLinkMap
import uzuzjmd.competence.owl.dao.AbstractEvidenceLink
import uzuzjmd.competence.mapper.gui.Ont2ProgressMap
import uzuzjmd.competence.owl.dao.CourseContext
import uzuzjmd.competence.owl.dao.SelectedLearningProjectTemplate
import uzuzjmd.competence.owl.dao.LearningProjectTemplate
import uzuzjmd.competence.owl.dao.CompetenceInstance
import uzuzjmd.competence.owl.dao.Competence
import uzuzjmd.competence.owl.access.CompOntologyAccess
import org.apache.log4j.Level
import com.hp.hpl.jena.rdf.model.InfModel
import com.hp.hpl.jena.rdf.model.ModelFactory
import uzuzjmd.competence.owl.dao.Competence
import uzuzjmd.competence.main.CompetenceImporter
import uzuzjmd.competence.main.EposImporter
import uzuzjmd.competence.owl.access.MagicStrings
import uzuzjmd.competence.service.rest.CompetenceOntologyInterface
import uzuzjmd.competence.owl.dao.AbstractEvidenceLink
import uzuzjmd.competence.owl.dao.EvidenceActivity
import uzuzjmd.competence.owl.dao.AbstractEvidenceLink
import scala.collection.JavaConverters._

@RunWith(classOf[JUnitRunner])
class CoreTests2 extends FunSuite with ShouldMatchers {

  def init() {
    
       // change this, if you want to really reset the database
    CompFileUtil.deleteTDB()

    val compOntManag = new CompOntologyManager()

    compOntManag.begin()
    compOntManag.getM().validate()
    compOntManag.close()

    CompetenceImporter.convertCSVArray();
    compOntManag.begin()
    compOntManag.getM().validate()
    compOntManag.close()

    EposImporter.importEpos()
    compOntManag.begin()
    compOntManag.getM().validate()
    compOntManag.close()

    compOntManag.begin()
    val fileUtil = new CompFileUtil(compOntManag.getM())
    fileUtil.writeOntologyout()
    compOntManag.close()
    
  }

  test("if two evidence links are created with different evidences with same user and competence") {
    //init()
    
    MagicStrings.TDBLocationPath="C:/dev/scalaworkspace/Wissensmodellierung/competence-database/tdb2";
    
    //basic data
    val interface = new CompetenceOntologyInterface
    val compOntManag = interface.initManagerInCriticalMode
    val studentRole = new StudentRole(compOntManag)
    val coursecontext = new CourseContext(compOntManag, "2")
    val userstudent = new User(compOntManag, "Hendrik", studentRole, coursecontext, "Hendrik")
    val competence = new Competence(compOntManag, "Hörverstehen A2")
    competence.persist(false)

    // first evidence
    val evidenceActivity = new EvidenceActivity(compOntManag, "http://testest", "meine testaktivitat")
    val link = new AbstractEvidenceLink(compOntManag, null, userstudent, userstudent, coursecontext, evidenceActivity, System.currentTimeMillis(), false, competence)
    link.persist
    link.exists should not be false
    val linkId1 = link.getId
    val evidenceActivity1Id = evidenceActivity.getId

    // second evidence
    val evidenceActivity2 = new EvidenceActivity(compOntManag, "http://testest2", "meine testaktivitat2")
    val link2 = new AbstractEvidenceLink(compOntManag, null, userstudent, userstudent, coursecontext, evidenceActivity2, System.currentTimeMillis(), false, competence)
    link2.persist
    link2.exists should not be false
    val linkId2 = link2.getId
    val evidenceActivity2Id = evidenceActivity2.getId
    
    interface.closeManagerInCriticalMode(compOntManag)
    
    // start getting the map
    val compOntManag2 = new CompOntologyManager   
    compOntManag2.begin()

    val competenceMapCalculator = new Ont2CompetenceLinkMap(compOntManag2, userstudent.name)
    val map = competenceMapCalculator.getCompetenceLinkMap()
    
    val competenceString = competence.getDefinition()
    
    println("Kompetenz linked is: "+ competenceString)
    // both evidences should be returned
    (map.getMapUserCompetenceLinks.get(competenceString).size() >= 2) should not be false
    
    val results = map.getMapUserCompetenceLinks.get(competence.getDefinition())
    println(results.size() + " evidences linked")
    results.asScala.foreach(x=>println(x.getEvidenceUrl + x.getEvidenceTitel))

    compOntManag2.close()
    
    val compOntManager3 = interface.initManagerInCriticalMode()
    // reset evidence1
    
    val link1Copy = new AbstractEvidenceLink(compOntManager3, linkId1)
    link1Copy.delete
    val evidenceActivityCopy = new EvidenceActivity(compOntManager3, evidenceActivity1Id)
    evidenceActivityCopy.delete
    evidenceActivityCopy.exists should not be true

    // reset evidence2
    val link2Copy = new AbstractEvidenceLink(compOntManager3, linkId2)
    link2Copy.delete
    val evidenceActivityCopy2 = new EvidenceActivity(compOntManager3, evidenceActivity2Id)
    evidenceActivityCopy2.delete
    evidenceActivityCopy2.exists should not be true
    
    interface.closeManagerInCriticalMode(compOntManager3)
    showResult
    

  }

  private def showResult() {
    val compOntManag = new CompOntologyManager()
    compOntManag.begin()
    val fileUtil = new CompFileUtil(compOntManag.getM())
    fileUtil.writeOntologyout()
    compOntManag.close()
  }

  private def createAbstract(compOntManag: CompOntologyManager, userstudent: User): AbstractEvidenceLink = {
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
    val link = new AbstractEvidenceLink(compOntManag, null, user, userstudent, coursecontext, evidenceActivity, System.currentTimeMillis(), false, competence, (comment :: comment2 :: Nil))
    link.persist
    comment.hasEdge(userstudent, CompObjectProperties.UserOfComment) should not be false
    comment2.hasEdge(userstudent, CompObjectProperties.UserOfComment) should not be false
    return link
  }

  //  test("A non-empty list should not be empty") {
  //    List(1, 2, 3) should not be ('empty)
  //    List("fee", "fie", "foe", "fum") should not be ('empty)
  //
  //    val assessment = "schlecht"
  //    val enumMap = List("gar nicht" -> 0, "schlecht" -> 1, "mittel" -> 2, "gut" -> 3)
  //    val map = enumMap.toMap.get(assessment).get
  //    println(map);
  //
  //  }

}
