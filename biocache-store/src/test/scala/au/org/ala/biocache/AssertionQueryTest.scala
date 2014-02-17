package au.org.ala.biocache

import org.junit.Ignore
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite

@Ignore
class ValidationRuleTest extends FunSuite {

  test("Test retrieve list"){
    println("Testing ")
    val aqDao:ValidationRuleDAO = Config.getInstance(classOf[ValidationRuleDAO]).asInstanceOf[ValidationRuleDAO]
    def listOfRules = aqDao.list
    println ("List of rules retrieved: " + listOfRules.size)
  }
}
