package ru.rosteelton.transactions.common.models

import io.circe.{ Decoder, Encoder }

final case class TransactionId(value: String) extends AnyVal

object TransactionId {
  implicit val transactionIdEncoder: Encoder[TransactionId] = Encoder[String].contramap(_.value)
  implicit val transactionIdDecoder: Decoder[TransactionId] = Decoder[String].map(TransactionId(_))
}
