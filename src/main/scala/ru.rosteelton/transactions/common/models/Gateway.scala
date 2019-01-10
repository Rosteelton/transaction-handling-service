package ru.rosteelton.transactions.common.models

sealed trait Gateway extends Product with Serializable
object Gateway {
  case class UserAccount(accountId: AccountId) extends Gateway
  case class CompanyAccount(inn: Inn, kpp: Kpp) extends Gateway
}