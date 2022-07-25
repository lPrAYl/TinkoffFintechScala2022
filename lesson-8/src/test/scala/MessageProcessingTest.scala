import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import java.util.concurrent.atomic.AtomicInteger

class MessageProcessingTest extends AnyFlatSpec with Matchers {

  behavior of "ThreadPool"

  it should "increment counter in ThreadPool" in {
    val tp = new ThreadPool(4)
    val counter = new AtomicInteger(0)

    tp.execute(() => counter.incrementAndGet())
    Thread.sleep(1000)
    counter.get shouldBe 1
  }
}
