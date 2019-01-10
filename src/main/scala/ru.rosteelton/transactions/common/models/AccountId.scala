package ru.rosteelton.transactions.common.models

import io.circe.{Decoder, Encoder}

final case class AccountId(value: String) extends AnyVal

object AccountId {
  implicit val accountIdEncoder: Encoder[AccountId] = Encoder[String].contramap(_.value)
  implicit val accountIdDecoder: Decoder[AccountId] = Decoder[String].map(AccountId(_))
}
