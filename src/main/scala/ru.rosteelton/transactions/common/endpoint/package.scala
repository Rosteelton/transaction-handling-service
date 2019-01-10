package ru.rosteelton.transactions.common

import cats.effect.Effect
import cats.implicits._
import io.circe.syntax._
import org.http4s.Response
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl

package object endpoint {
  implicit class EndpointOpsEither[F[_]: Effect, R](value: F[Either[R, Unit]]) extends Http4sDsl[F] {
    def toResponse: F[Response[F]] = value.flatMap(e => Ok(ApiResponse.fromEither(e).asJson))
  }

  implicit class EndpointOpsUnit[F[_]: Effect, R](value: F[Unit]) extends Http4sDsl[F] {
    def toResponse: F[Response[F]] = value.flatMap(_ => Ok((ApiResponse.Success: ApiResponse).asJson))
  }
}
