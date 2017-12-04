package ch.epfl.scala.version

import org.scalatest.FunSuite

class VersionTests extends FunSuite {
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
      Version(1, 1, Some(1)),
      Version(1, 0, Some(1)),
      Version(1, 0, Some(1), None, Some(ReleaseCandidate(2))),
      Version(1, 0, Some(1), None, Some(ReleaseCandidate(1))),
      Version(1, 0, Some(1), None, Some(Milestone(2))),
      Version(1, 0, Some(1), None, Some(Milestone(1))),
      Version(1, 0, Some(1), None, Some(OtherPreRelease("BLABLA")))
    ))
  }

  test("binary") {
    assert(binary("1") == Some("1.0"))
    assert(binary("1.2") == Some("1.2"))
    assert(binary("1.2.3") == Some("1.2"))
    assert(binary("1.2.3.4") == Some("1.2"))
    assert(binary("1.2.3-RC5") == Some("1.2.3-RC5"))
    assert(binary("1.2.3-M6") == Some("1.2.3-M6"))
    assert(binary("1.1.1-xyz") == Some("1.1.1-xyz"))
    assert(binary("1.1.1+some.meta~data") == Some("1.1"))
    assert(binary("13e7afa9c1817d45b2989e545b2e9ead21d00cef") == None)
    assert(binary("v1") == Some("1.0"))
  }

  test("full") {
    assert(full("1") == Some("1.0"))
    assert(full("1.2") == Some("1.2"))
    assert(full("1.2.3") == Some("1.2.3"))
    assert(full("1.2.3.4") == Some("1.2.3.4"))
    assert(full("1.2.3-RC5") == Some("1.2.3-RC5"))
    assert(full("1.2.3-M6") == Some("1.2.3-M6"))
    assert(full("1.1.1-xyz") == Some("1.1.1-xyz"))
    assert(full("1.1.1+some.meta~data") == Some("1.1.1+some.meta~data"))
    assert(full("13e7afa9c1817d45b2989e545b2e9ead21d00cef") == None)
    assert(full("v1") == Some("1.0"))    
  }

  test("parsing") {
    assert(parse("1") == Some(Version(1)))
    assert(parse("1.2") == Some(Version(1, 2)))
    assert(parse("1.2.3") == Some(Version(1, 2, Some(3))))
    assert(parse("1.2.3.4") == Some(Version(1, 2, Some(3), Some(4))))
    assert(parse("1.2.3-RC5") == Some(Version(1, 2, Some(3), None, Some(ReleaseCandidate(5)))))
    assert(parse("1.2.3-M6") == Some(Version(1, 2, Some(3), None, Some(Milestone(6)))))
    assert(parse("1.1.1-xyz") == Some(Version(1, 1, Some(1), None, Some(OtherPreRelease("xyz")))))
    assert(parse("1.1.1+some.meta~data") == Some(Version(major = 1, minor = 1, patch = Some(1), patch2 = None, preRelease = None, metadata = Some("some.meta~data"))))
    assert(parse("13e7afa9c1817d45b2989e545b2e9ead21d00cef") == None)
    assert(parse("v1") == Some(Version(1)))
  }

  private def Descending[T: Ordering] = implicitly[Ordering[T]].reverse
  private def parse(v: String): Option[Version] = Version(v)
  private def order(versions: List[String]): List[Version] = versions.flatMap(parse).sorted(Descending[Version])
  private def binary(v: String): Option[String] = Version(v).map(_.binary.toString)
  private def full(v: String): Option[String] = Version(v).map(_.toString)
}
