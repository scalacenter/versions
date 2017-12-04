package ch.epfl.scala.version

import fastparse.all._

object PreRelease {
  private final val lcmp = implicitly[Ordering[Long]]
  private final val scmp = implicitly[Ordering[String]]
  private final val cmp = implicitly[Ordering[(Long, Long, Option[Long], Option[Long])]]

  def compare(pr1: Option[PreRelease], pr2: Option[PreRelease]): Int = {
    val LT = -1
    val GT = 1
    val EQ = 0

    // format: off
    (pr1, pr2) match {
      case (None, None)                                               => EQ
      case (None, Some(_))                                            => GT
      case (Some(_), None)                                            => LT
      case (Some(ReleaseCandidate(rc1)), Some(ReleaseCandidate(rc2))) => lcmp.compare(rc1, rc2)
      case (Some(ReleaseCandidate(_))  , Some(Milestone(_)))          => GT
      case (Some(Milestone(_))         , Some(ReleaseCandidate(_)))   => LT
      case (Some(Milestone(m1))        , Some(Milestone(m2)))         => lcmp.compare(m1, m2)
      case (Some(OtherPreRelease(pr1)) , Some(OtherPreRelease(pr2)))  => scmp.compare(pr1, pr2)
      case (Some(OtherPreRelease(_))   , Some(Milestone(_)))          => LT
      case (Some(OtherPreRelease(_))   , Some(ReleaseCandidate(_)))   => LT
      case (Some(_)                    , Some(OtherPreRelease(_)))    => GT
      case _                                                          => EQ
    }
    // format: on
  }

  // http://semver.org/#spec-item-9
  val Parser: P[PreRelease] =
    "-" ~ (
      (("M" | "m") ~ &(Digit) ~ Number).map(n => Milestone(n)) |
        (("R" | "r") ~ ("C" | "c") ~ &(Digit) ~ Number).map(n => ReleaseCandidate(n)) |
        (Digit | Alpha | "." | "-").rep.!.map(s => OtherPreRelease(s))
    )
}

sealed trait PreRelease
case class ReleaseCandidate(rc: Long) extends PreRelease
case class Milestone(m: Long) extends PreRelease
case class OtherPreRelease(o: String) extends PreRelease
