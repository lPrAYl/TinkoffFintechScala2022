package ru.tinkoff.coursework.logic

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import ru.tinkoff.coursework.Car

import scala.concurrent.Future

case class Point(x: Int, y: Int)

object Point {
  implicit val jsonDecoder: Decoder[Point] = deriveDecoder
  implicit val jsonEncoder: Encoder[Point] = deriveEncoder
}


trait CarsharingService {

  /** Возвращает ближайший к клиенту автомобиль
   *
   * @param clientCoordinates координаты клиента
   * @return
   */
  def carSearch(clientCoordinates: Point): Future[Option[Car]]

  /** Бронирует автомобиль
   *
   * @param userName имя игрока
   * @return забронированный автомобиль
   */
  def carBooking(userName: String): Future[Car]

  /** Попытка угадать букву в загаданном слове
   *
   * @param id    идентификатор автобомобиля
   * @return состояние автомобиля
   */
  def carState(id: Long): Future[Car]

}
