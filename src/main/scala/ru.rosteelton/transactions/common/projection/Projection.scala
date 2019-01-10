package ru.rosteelton.transactions.common.projection

import aecor.data.Folded

trait Projection[F[_], E, S] {
  def fetchVersionAndState(event: E): F[(Long, Option[S])]
  def saveNewVersion(s: S, version: Long): F[Unit]
  def applyEvent(s: Option[S])(event: E): Folded[S]
}
