package example

import com.google.ortools.Loader
import com.google.ortools.sat.*
import com.google.ortools.sat.CpModelImplicits.*

import scala.language.implicitConversions
import scala.reflect.ClassTag

object Sudoku {
  def main(args: Array[String]): Unit = {
    Loader.loadNativeLibraries()
    implicit val model: CpModel = new CpModel

    implicit def everything2Array[A : ClassTag](s: Seq[A]): Array[A] = s.toArray[A] // everything can be an array if we're brave enough

    val squares = Seq.tabulate(9, 9)((i, j) => s"($i,$j)" in 1 to 9)
    squares.foreach(model.addAllDifferent(_))
    squares.transpose.foreach(model.addAllDifferent(_))
    squares.map(_.sliding(3, 3)).sliding(3, 3).map(_.map(_.toSeq)).flatMap(_.transpose.map(_.flatten)) // blocks
      .foreach(model.addAllDifferent(_))

    val solver = new CpSolver
    solver.solve(model) match {
      case CpSolverStatus.FEASIBLE | CpSolverStatus.OPTIMAL =>
//        println(squares.map(_.map(solver.value).mkString(" ")).mkString("\n"))
        println(squares.map(_.map(solver.value).sliding(3, 3).map(_.mkString(" ")).mkString(" | ")).sliding(3, 3).map(_.mkString("\n")).mkString("\n------+-------+------\n"))
      case status =>
        println(status)
    }
  }
}
