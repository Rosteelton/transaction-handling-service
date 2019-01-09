package ru.rosteelton.transactions.common.endpoint

import io.circe.Encoder
import io.circe.derivation.deriveEncoder

final case class ApiResponse(success: Boolean, reason: Option[String])

object ApiResponse {
  implicit val apiResponseEncoder: Encoder[ApiResponse] = deriveEncoder[ApiResponse]
}
