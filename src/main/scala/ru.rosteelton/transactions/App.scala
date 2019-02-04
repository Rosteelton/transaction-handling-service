package ru.rosteelton.transactions

import cats.effect.ExitCode
import monix.eval.{ Task, TaskApp }
import monix.execution.Scheduler
import cats.implicits._

object App extends TaskApp {
  implicit val implScheduler: Scheduler = scheduler

  def run(args: List[String]): Task[ExitCode] =
    new AppZ[Task].program.as(ExitCode.Success)
}
