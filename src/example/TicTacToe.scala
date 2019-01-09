package example

import com.google.ortools.sat._
import com.google.ortools.sat.CpModelImplicits._

import scala.reflect.ClassTag

object TicTacToe {
  System.loadLibrary("jniortools")

  def main(args: Array[String]): Unit = {
    implicit val model = new CpModel

    implicit def everything2Array[A : ClassTag](s: TraversableOnce[A]): Array[A] = s.toArray[A] // everything can ba an array if we're breve enough

    val squares = Seq.tabulate(9)(i => "s" + i boolVar)

    squares.sliding(3, 3).foreach(model.addLinearSum(_, 1, 2))
    squares.sliding(3, 3).toSeq.transpose.foreach(model.addLinearSum(_, 1, 2))
    model.addLinearSum(squares.sliding(4, 4).map(_.head), 1, 2)
    model.addLinearSum(squares.drop(2).sliding(2, 2).map(_.head).take(3), 1, 2)
//    model.addLinearSumEqual(squares, 4)
    model.minimizeSum(squares)

    println(model.model())

    val solver = new CpSolver()
    solver.solve(model) match {
      case CpSolverStatus.FEASIBLE | CpSolverStatus.OPTIMAL =>
        println(squares.map(solver.value).sliding(3, 3).map(_.mkString(" ")).mkString("\n"))
      case status =>
        println(status)
    }
  }
}
