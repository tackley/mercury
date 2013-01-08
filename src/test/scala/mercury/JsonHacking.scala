package mercury

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FlatSpec

import spray.json._

class JsonHacking extends FlatSpec with ShouldMatchers {

  import spray.json.DefaultJsonProtocol._

  "json library" should "do what i want" in {
    val source = """{ "some": "JSON source" }"""
    val jsonAst = source.asJson

    println(jsonAst)

    val jsonAst2 = List(1, 2, 3).toJson
    println(jsonAst2)

    println(Map("aaa" -> "aval", "bbb" -> "bval").toJson)
  }

  case class MyClass(a: String, b: Option[String])

  it should "do case classes" in {

    implicit val colorFormat = jsonFormat2(MyClass)

    val my = MyClass("a", Some("b"))

    println(my.toJson)
  }
}
