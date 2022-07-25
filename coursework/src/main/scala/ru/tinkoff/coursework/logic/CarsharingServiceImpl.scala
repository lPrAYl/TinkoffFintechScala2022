package ru.tinkoff.coursework.logic

import ru.tinkoff.coursework.Car

import scala.concurrent.{ExecutionContext, Future}

trait CarsharingStorage

class CarsharingServiceImpl(carsharingStorage: CarsharingStorage)
                           (implicit ec: ExecutionContext) extends CarsharingService {
  override def carSearch(clientCoordinates: Point): Future[Option[Car]] = ???

  override def carBooking(userName: String): Future[Car] = ???

  override def carState(id: Long): Future[Car] = ???

}
