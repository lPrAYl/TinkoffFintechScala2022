import java.util.UUID

import org.scalacheck._

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext
import java.util.concurrent.Executors

class SocialNetRemoteSpec extends Properties("SocialNetApiSpec") {
  val ec = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(32))

  import Prop.forAll
  import Gen._

  implicit val genSmallInteger: Arbitrary[Int] = Arbitrary(Gen.choose(0, 4))
  implicit val genString: Arbitrary[String] = Arbitrary(Gen.alphaLowerStr)
  implicit val genUUID: Arbitrary[UUID] = Arbitrary(Gen.uuid)

  property("send") = forAll { (user: String, text: String, ret: Either[String, UUID]) =>
    val emulator = SocialNetRemoteEmulator(
      List(SendCall(ret, text, user))
    )
    val testee = SocialNetApi(emulator)(ec)

    ret == Await.result(testee.send(user, text), 1.second) && emulator.calls().isEmpty
  }

  property("like") = forAll { (id: UUID, ret: Either[String, Boolean]) =>
    val emulator = SocialNetRemoteEmulator(
      List(LikeCall(ret, id))
    )
    val testee = SocialNetApi(emulator)(ec)

    ret == Await.result(testee.like(id), 1.second) && emulator.calls().isEmpty
  }

  property("unlike") = forAll { (id: UUID, ret: Either[String, Boolean]) =>
    val emulator = SocialNetRemoteEmulator(
      List(UnlikeCall(ret, id))
    )
    val testee = SocialNetApi(emulator)(ec)

    ret == Await.result(testee.unlike(id), 1.second) && emulator.calls().isEmpty
  }

  def interleave[T](xs: List[T], ys: List[T], step: Int): List[T] = {
    val iter = xs.grouped(step)
    val yIter = ys.iterator
    val coll = iter.zip(yIter).flatMap {
      case (xs, y) => xs :+ y
    }
    (coll ++ iter.flatten ++ yIter).toList
  }

  property("get") = forAll { (fails: Set[(UUID, String)], succs: Set[(UUID, String, String, Int)]) =>
    val emulator = SocialNetRemoteEmulator(
      fails.map { case (id, err) =>
        GetCall(Left(err), id)
      }.toList ++
      succs.map { case (id, user, text, likeCount) =>
        GetCall(Right((id, user, text, likeCount)), id)
      }.toList
    )

    val idList = fails.map(_._1) ++ succs.map(_._1)

    val testee = SocialNetApi(emulator)(ec)

    val actual = Await.result(testee.get(idList), 1.second)
    val expected = (fails, succs.map((Post.apply _).tupled))

    expected == actual && emulator.calls().isEmpty
  }

  property("likeSeq") = forAll { (res: Either[(Set[(UUID, Int)], Set[(UUID, String)]), Set[(UUID, Boolean)]]) =>
    //    println(s"\n\n\nIN A $res")
    res match {
      case Left((succs, errs)) if errs.nonEmpty =>
        val arg = succs.map(_._1) ++ errs.map(_._1)
        val emulator = SocialNetRemoteEmulator(
          succs.map(x => LikeCall(Right(true), x._1)).toList ++
            errs.map(x => LikeCall(Left(x._2), x._1)).toList ++
            succs.toList.flatMap { case (id, tries) =>
              (1 to tries).map(x => UnlikeCall(Left(s"err unlike $id try $x"), id)).toList :+
                UnlikeCall(Right(true), id)
            }
        )
        val testee = SocialNetApi(emulator)(ec)
        val expected = Left(errs.map(x => x._1 -> x._2))
        val actual = Await.result(testee.likeSeq(arg), 1.second)
        val callsLeft = emulator.calls()

        //        println(s"expected fail: $expected")
        //        println(s"actual fail:   $actual")
        //        println(s"calls left: ${callsLeft} ${callsLeft.isEmpty}")

        expected == actual && callsLeft.isEmpty
      case Right(res) =>
        val emulator = SocialNetRemoteEmulator(
          res.map(x => LikeCall(Right(x._2), x._1)).toList
        )
        val testee = SocialNetApi(emulator)(ec)
        val expected = Right(res)
        val actual = Await.result(testee.likeSeq(res.map(_._1)), 1.second)

        // val callsLeft = emulator.calls()
        //        println(s"expected succ: $expected")
        //        println(s"actual succ:   $actual"
        //        println(s"calls left: ${callsLeft} ${callsLeft.isEmpty}")

        expected == actual && emulator.calls().isEmpty

      case _ =>
        true
    }
  }
}