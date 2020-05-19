package ru.rosteelton.transactions

import cats.effect.{ ExitCode, Resource }
import monix.eval.{ Task, TaskApp }
import monix.execution.Scheduler
import cats.implicits._

object App extends TaskApp {

  implicit val implScheduler: Scheduler = scheduler

  def run(args: List[String]): Task[ExitCode] = {
    val app = new AppZ[Task]
    app.createResources.flatMap(r => Resource.liftF(app.program(r))).use(_ => Task.never).as(ExitCode.Success)
  }
}
