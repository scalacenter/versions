package ch.epfl.scala.version

/**
  * separation of possible combinations
  * @param major the major version number
  * @param minor the minor version number
  * @param patch the path version number
  * @param preRelease the pre release name
  * @param metadata the release metadata
  */
case class SemanticVersion(
    major: Long,
    minor: Long,
    patch: Long,
    preRelease: Option[PreRelease] = None,
    metadata: Option[String] = None
) extends Ordered[SemanticVersion] {

  override def toString: String = {
    val preReleasePart = preRelease.map {
      case Milestone(d) => "M" + d.toString
      case ReleaseCandidate(d) => "RC" + d.toString
      case OtherPreRelease(v) => v.toString
    }.map("-" + _).getOrElse("")

    val metadataPart = metadata.map("+" + _).getOrElse("")

    major + "." + minor + "." + patch + preReleasePart + metadataPart
  }

  private final val cmp = implicitly[Ordering[(Long, Long, Long)]]

  override def compare(that: SemanticVersion): Int = {
    val v1 = this
    val v2 = that

    def tupled(v: SemanticVersion) = (v.major, v.minor, v.patch)
    val tv1 = tupled(v1)
    val tv2 = tupled(v2)

    // Milestone < Release Candidate < Released
    if (cmp.equiv(tv1, tv2)) PreRelease.compare(v1.preRelease, v2.preRelease)
    else cmp.compare(tv1, tv2)
  }
}

object SemanticVersion {
  import fastparse.all._
  import fastparse.core.Parsed

  implicit def ordering = new Ordering[SemanticVersion] {
    def compare(v1: SemanticVersion, v2: SemanticVersion): Int = v1.compare(v2)
  }

  val Parser = {
    val Major = Number
    val MinorP = ("." ~ Number)
    val PatchP = ("." ~ Number)

    // http://semver.org/#spec-item-9
    val PreRelease: P[PreRelease] =
      "-" ~ (
        (("M" | "m") ~ &(Digit) ~ Number).map(n => Milestone(n)) |
          (("R" | "r") ~ ("C" | "c") ~ &(Digit) ~ Number).map(n => ReleaseCandidate(n)) |
          (Digit | Alpha | "." | "-").rep.!.map(s => OtherPreRelease(s))
      )

    // http://semver.org/#spec-item-10
    val MetaData = "+" ~ AnyChar.rep.!

    (Major ~ MinorP ~ PatchP ~ PreRelease.? ~ MetaData.?).map {
      case (major, minor, patch, preRelease, metadata) =>
        SemanticVersion(major, minor, patch, preRelease, metadata)
    }
  }
  private val FullParser = Start ~ Parser ~ End
  def parse(version: String): Option[SemanticVersion] = apply(version)
  def apply(version: String): Option[SemanticVersion] = {
    FullParser.parse(version) match {
      case Parsed.Success(v, _) => Some(v)
      case _ => None
    }
  }
}
