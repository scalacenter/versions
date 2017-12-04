package ch.epfl.scala.version

/**
  * separation of possible combinations
  * @param major the major version number
  * @param minor the minor version number
  * @param patch the path version number
  * @param patch2 the path version number (to support a.b.c.d)
  * @param preRelease the pre release name
  * @param metadata the release metadata
  */
case class Version(
    major: Long,
    minor: Long = 0,
    patch: Option[Long] = None,
    patch2: Option[Long] = None,
    preRelease: Option[PreRelease] = None,
    metadata: Option[String] = None
) extends Ordered[Version] {

  override def toString: String = {
    val patchPart = patch.map("." + _).getOrElse("")
    val patch2Part = patch2.map("." + _).getOrElse("")

    val preReleasePart = preRelease.map {
      case Milestone(d) => "M" + d.toString
      case ReleaseCandidate(d) => "RC" + d.toString
      case OtherPreRelease(v) => v.toString
    }.map("-" + _).getOrElse("")

    val metadataPart = metadata.map("+" + _).getOrElse("")

    major + "." + minor + patchPart + patch2Part + preReleasePart + metadataPart
  }

  def binary: Version =
    if (preRelease.nonEmpty) this
    else forceBinary

  def forceBinary: Version = Version(major, minor)

  private final val cmp = implicitly[Ordering[(Long, Long, Option[Long], Option[Long])]]

  override def compare(that: Version): Int = {
    val v1 = this
    val v2 = that

    def tupled(v: Version) = (v.major, v.minor, v.patch, v.patch2)
    val tv1 = tupled(v1)
    val tv2 = tupled(v2)

    // Milestone < Release Candidate < Released
    if (cmp.equiv(tv1, tv2)) PreRelease.compare(v1.preRelease, v2.preRelease)
    else cmp.compare(tv1, tv2)
  }
}

object Version {
  import fastparse.all._
  import fastparse.core.Parsed

  implicit def ordering = new Ordering[Version] {
    def compare(v1: Version, v2: Version): Int = v1.compare(v2)
  }

  val Parser = {
    val Major = Number
    val MinorP = ("." ~ Number).?.map(_.getOrElse(0L))
    val PatchP = ("." ~ Number).?
    val Patch2P = ("." ~ Number).?
    val MetaData = "+" ~ AnyChar.rep.!

    ("v".? ~ Major ~ MinorP ~ PatchP ~ Patch2P ~ PreRelease.Parser.? ~ MetaData.?).map {
      case (major, minor, patch, patch2, preRelease, metadata) =>
        Version(major, minor, patch, patch2, preRelease, metadata)
    }
  }
  private val FullParser = Start ~ Parser ~ End
  def parse(version: String): Option[Version] = apply(version)
  def apply(version: String): Option[Version] = {
    FullParser.parse(version) match {
      case Parsed.Success(v, _) => Some(v)
      case _ => None
    }
  }
}
