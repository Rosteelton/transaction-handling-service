package ru.rosteelton.transactions.common.pickler

import java.time.Instant

object DefaultPickler {
  implicit val instantPickler: boopickle.Pickler[Instant] =
    boopickle.DefaultBasic.longPickler.xmap(Instant.ofEpochMilli)(_.toEpochMilli)
}
