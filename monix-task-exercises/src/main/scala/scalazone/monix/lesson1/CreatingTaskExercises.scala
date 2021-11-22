package scalazone.monix.lesson1

import monix.eval.Task
import monix.execution.exceptions.DummyException

import scala.util.Try

/** Run with
  * sbt "monix-task-exercises/runMain scalazone.monix.lesson1.CreatingTaskExercises"
  */
object CreatingTaskExercises extends App {

  /** Exercise 1
    *
    * Create a Task, that after execution will print "Hello!"
    */
  val helloTask: Task[Unit] = Task.eval{
    println("hello")
  }

  /** Exercise 2
    *
    * Create a Task that wraps already computed Integer
    */
  val intTask: Task[Int] = Task(1 + 1)

  /** Exercise 3
    *
    * Create a Task that fails with `monix.execution.exceptions.DummyException`
    */
  val failedTask = Task.raiseError(new monix.execution.exceptions.DummyException)

  /** Exercise 4
    *
    * Write `fromTry` method that will create a `Task`
    * from `Try`
    */
  def fromTry[A](t: Try[A]): Task[A] = Task.from(t)
}
