package ru.rosteelton.transactions.common.endpoint

import io.circe.{ Encoder, Json }

sealed trait ApiResponse
object ApiResponse {
  case object Success extends ApiResponse
  case class Failure(reason: String) extends ApiResponse

  implicit val apiResponseEncoder: Encoder[ApiResponse] = Encoder.instance {
    case Success         => Json.obj("success" -> Json.fromBoolean(true))
    case Failure(reason) => Json.obj("success" -> Json.fromBoolean(false), "reason" -> Json.fromString(reason))
  }

  def fromEither[R](either: Either[R, Unit]): ApiResponse =
    either.fold(r => ApiResponse.Failure(r.toString), _ => ApiResponse.Success)
}
