package ch.epfl.scala

import fastparse.all._

package object version {
  private[version] val Alpha = (CharIn('a' to 'z') | CharIn('A' to 'Z')).!
  private[version] val Digit = CharIn('0' to '9').!
  private[version] val Number = Digit.rep(1).!.map(_.toLong)
}