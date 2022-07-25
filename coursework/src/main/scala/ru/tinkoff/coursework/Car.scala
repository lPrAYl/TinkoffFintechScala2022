package ru.tinkoff.coursework

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

import java.util.UUID

case class Car(id: UUID, carFeature: Feature, status: CarStatus)

object Car {
  implicit val jsonDecoder: Decoder[Car] = deriveDecoder
  implicit val jsonEncoder: Encoder[Car] = deriveEncoder
}
