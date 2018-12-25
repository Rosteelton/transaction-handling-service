package ru.rosteelton.transactions.common.models

import cats.kernel.Monoid

final case class Money(value: BigDecimal) extends AnyVal

object Money {
  def zero: Money = Money(0)

  implicit def monoid: Monoid[Money] = new Monoid[Money] {
    def empty: Money = Money.zero
    def combine(x: Money, y: Money): Money = Money(x.value + y.value)
  }
}
