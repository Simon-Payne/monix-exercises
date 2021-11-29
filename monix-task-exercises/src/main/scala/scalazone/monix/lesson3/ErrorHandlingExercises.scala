package scalazone.monix.lesson3

import monix.eval.Task

import scala.concurrent.duration.FiniteDuration

/** Run with
  * {{{
  *   sbt "monix-task-exercises/runMain scalazone.monix.lesson3.ErrorHandlingExercises"
  * }}}
  *
  * Test with
  * {{{
  *   sbt "monix-task-exercises/testOnly *ErrorHandlingExercisesTests"
  * }}}
  */
object ErrorHandlingExercises extends App {

  /** Exercise 1
    *
    * Use `onErrorHandle` to fallback to `default` in case the `Task` has an error.
    */
  def ex1[A](task: Task[A], default: A): Task[A] = task.onErrorHandle(_ => default)

  /** Exercise 2
    *
    * Write a method that will recover with `default`,
    * but only if the exception is `DummyException`
    */
  def ex2[A](task: Task[A], default: A): Task[A] = task.onErrorHandle {
    case d: monix.execution.exceptions.DummyException => default
    case t => throw t
  }

  /** Exercise 3
    *
    * Use `redeem` to construct a `Task` that will
    * multiply value of `task` by 10 if it's successful but
    * it will fallback to 20 if there was an error.
    */
  def ex3(task: Task[Int]): Task[Int] = task.redeem(_ => 20, _ * 10)

  /** Exercise 4
    *
    * Write a method which handle all errors in a Task end exposes them as `Left` in `Either`.
    *
    * There is a function called `attempt` which does it but try to implement
    * it on your own from combinators that we have learned.
    */
  import monix.execution.Scheduler.Implicits.global
  def ex4[A](task: Task[A]): Task[Either[Throwable, A]] = task.redeem(Left(_), Right(_))

  /** Exercise 5
    *
    * Write a recursive function which will retry Task up to `maxRetries`
    * with exponential backoff between retries.
    *
    * Tip: Check `Task.sleep` or `Task#delayExecution` for delaying execution of a `Task`.
    * Note that unlike `Thread.sleep`, `Task.sleep` is non-blocking!
    */
  def ex5[A](source: Task[A], maxRetries: Int, firstDelay: FiniteDuration): Task[A] = {
    import concurrent.duration.durationToPair

    source.redeemWith(_ match {
      case ex =>
        if (maxRetries >= 0) {
          val d = firstDelay._1
          val newDelay = FiniteDuration(d * d, firstDelay._2)
          ex5(source.delayExecution(firstDelay), maxRetries - 1, newDelay)
        }
        else throw ex
    },{
      case v => Task(v)
    })
  }
}
