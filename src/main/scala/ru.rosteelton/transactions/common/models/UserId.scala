package ru.rosteelton.transactions.common.models

import io.circe.{Decoder, Encoder}

final case class UserId(value: String) extends AnyVal
object UserId {
  implicit val encoder: Encoder[UserId] = Encoder[String].contramap(_.value)
  implicit val decoder: Decoder[UserId] = Decoder[String].map(UserId(_))
}

