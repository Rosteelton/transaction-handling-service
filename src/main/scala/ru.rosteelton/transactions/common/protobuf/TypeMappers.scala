package ru.rosteelton.transactions.common.protobuf

import java.time.Instant

import ru.rosteelton.transactions.common.models.Money
import scalapb.TypeMapper
import shapeless._

trait AnyValTypeMapper {
  implicit def anyValTypeMapper[V, U](implicit ev: V <:< AnyVal,
                                      V: Unwrapped.Aux[V, U]): TypeMapper[U, V] = {
    val _ = ev
    TypeMapper[U, V](V.wrap)(V.unwrap)
  }
}

trait CaseClassTypeMapper {

  implicit def caseClassTypeMapper[A, B, Repr <: HList](
    implicit aGen: Generic.Aux[A, Repr],
    bGen: Generic.Aux[B, Repr]
  ): TypeMapper[A, B] =
    TypeMapper { x: A =>
      bGen.from(aGen.to(x))
    } { x =>
      aGen.from(bGen.to(x))
    }
}

trait BaseTypeMapper {
  implicit val instant: TypeMapper[Long, Instant] =
    TypeMapper[Long, Instant](Instant.ofEpochMilli)(_.toEpochMilli)

  implicit val money: TypeMapper[String, Money] =
    TypeMapper[String, Money](str => Money(BigDecimal(str)))(_.value.toString())
}

object TypeMappers extends BaseTypeMapper with AnyValTypeMapper with CaseClassTypeMapper