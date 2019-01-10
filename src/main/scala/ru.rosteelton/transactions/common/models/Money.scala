package ru.rosteelton.transactions.common.models

import cats.kernel.Monoid
import io.circe.{ Decoder, Encoder }

final case class Money(value: BigDecimal) extends AnyVal {
  def +(amount: Money): Money = Money(value + amount.value)
  def -(amount: Money): Money = Money(value - amount.value)
}
object Money {
  def zero: Money = Money(0)

  implicit def moneyMonoid: Monoid[Money] = new Monoid[Money] {
    def empty: Money = Money.zero
    def combine(x: Money, y: Money): Money = Money(x.value + y.value)
  }

  implicit val moneyEncoder: Encoder[Money] = Encoder[String].contramap(_.value.toString())
  implicit val moneyDecoder: Decoder[Money] = Decoder[String].map(m => Money(BigDecimal(m)))
}
