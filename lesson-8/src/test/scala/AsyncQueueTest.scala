import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers

class AsyncQueueTest extends AsyncFlatSpec with Matchers {
  behavior of "AsyncQueue"

  it should "return second item" in {
    val queue = new AsyncQueue[Int]()

    queue.take()
    queue.add(1)
    queue.add(2)
    queue.take().map(result => result shouldBe 2)
  }

  it should "return first item" in {
    val queue = new AsyncQueue[Int]()

    queue.add(1)
    queue.add(2)
    queue.take().map(result => result shouldBe 1)
  }
}
