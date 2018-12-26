package ru.rosteelton.transactions.common.models

import cats.kernel.Monoid

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
}
