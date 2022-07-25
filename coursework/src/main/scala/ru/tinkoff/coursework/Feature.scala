package ru.tinkoff.coursework

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import ru.tinkoff.coursework.logic.Point

final case class Feature(carModel: String, number: String, fuelLevel: Int, position: Point) {

}

object Feature {
  implicit val jsonDecoder: Decoder[Feature] = deriveDecoder
  implicit val jsonEncoder: Encoder[Feature] = deriveEncoder
}
