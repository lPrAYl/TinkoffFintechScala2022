package ru.tinkoff.coursework

import enumeratum.{CirceEnum, Enum, EnumEntry}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

sealed trait CarStatus extends EnumEntry

object CarStatus extends Enum[CarStatus] with CirceEnum[CarStatus] {

  implicit val jsonDecoder: Decoder[CarStatus] = deriveDecoder
  implicit val jsonEncoder: Encoder[CarStatus] = deriveEncoder

  case object Free extends CarStatus

  case object Busy extends CarStatus

  case object LowFuelLevel extends CarStatus

  override val values: IndexedSeq[CarStatus] = findValues


}
