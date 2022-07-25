import java.util.concurrent.LinkedBlockingQueue

class ThreadPool(numThreads: Int) {
  type Task = () => Unit
  private lazy val blockingQueue = new LinkedBlockingQueue[Task]()

  def execute(task: Task): Unit = blockingQueue.put(task)

  def workRoutine() = while (true) blockingQueue.take()()

  val threads = Seq.tabulate(numThreads) { _ =>
    new Thread {
      override def run() = workRoutine()
    }
  }
  threads.foreach(_.setDaemon(true))
  threads.foreach(_.start())
}

object MessageProcessing {
  def main(args: Array[String]): Unit = {
  }
}
