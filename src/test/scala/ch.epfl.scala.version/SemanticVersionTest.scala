package ch.epfl.scala.version

import org.scalatest.FunSuite

class SemanticVersionTests extends FunSuite {
  test("ordering") {
    val versions = List(
      "1.0.1",
      "1.0.1-M1",
      "1.0.1-M2",
      "1.0.1-RC2",
      "1.1.1",
      "1.0.1-BLABLA",
      "1.0.1-RC1"
    )

    assert(order(versions) == List(
      SemanticVersion(1, 1, 1),
      SemanticVersion(1, 0, 1),
      SemanticVersion(1, 0, 1, Some(ReleaseCandidate(2))),
      SemanticVersion(1, 0, 1, Some(ReleaseCandidate(1))),
      SemanticVersion(1, 0, 1, Some(Milestone(2))),
      SemanticVersion(1, 0, 1, Some(Milestone(1))),
      SemanticVersion(1, 0, 1, Some(OtherPreRelease("BLABLA")))
    ))
  }

  test("full") {
    assert(full("1") == None)
    assert(full("1.2") == None)
    assert(full("1.2.3") == Some("1.2.3"))
    assert(full("1.2.3.4") == None)
    assert(full("1.2.3-RC5") == Some("1.2.3-RC5"))
    assert(full("1.2.3-M6") == Some("1.2.3-M6"))
    assert(full("1.1.1-xyz") == Some("1.1.1-xyz"))
    assert(full("1.1.1+some.meta~data") == Some("1.1.1+some.meta~data"))
    assert(full("13e7afa9c1817d45b2989e545b2e9ead21d00cef") == None)
    assert(full("v1") == None)
  }

  test("parsing") {
    assert(parse("1") == None)
    assert(parse("1.2") == None)
    assert(parse("1.2.3") == Some(SemanticVersion(1, 2, 3)))
    assert(parse("1.2.3.4") == None)
    assert(parse("1.2.3-RC5") == Some(SemanticVersion(1, 2, 3, Some(ReleaseCandidate(5)))))
    assert(parse("1.2.3-M6") == Some(SemanticVersion(1, 2, 3, Some(Milestone(6)))))
    assert(parse("1.1.1-xyz") == Some(SemanticVersion(1, 1, 1, Some(OtherPreRelease("xyz")))))
    assert(parse("1.1.1+some.meta~data") == Some(SemanticVersion(1, 1, 1, None, Some("some.meta~data"))))
    assert(parse("13e7afa9c1817d45b2989e545b2e9ead21d00cef") == None)
    assert(parse("v1") == None)
  }

  private def Descending[T: Ordering] = implicitly[Ordering[T]].reverse
  private def parse(v: String): Option[SemanticVersion] = SemanticVersion(v)
  def order(versions: List[String]): List[SemanticVersion] = versions.flatMap(parse).sorted(Descending[SemanticVersion])
  private def full(v: String): Option[String] = SemanticVersion(v).map(_.toString)
}
