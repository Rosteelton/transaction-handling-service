package ru.rosteelton.transactions

import cats.effect.ConcurrentEffect
import com.typesafe.config.{Config, ConfigFactory}
import pureconfig.generic.auto._
import pureconfig.loadConfigOrThrow
import ru.rosteelton.transactions.config.AppConfig


class AppZ[F[_]](implicit F: ConcurrentEffect[F]) {

  val config = ConfigFactory.load()





  def program: F[Unit] = {





    for {
      appConfig <- F.delay(loadConfigOrThrow[AppConfig](config))
    }

  }
}
