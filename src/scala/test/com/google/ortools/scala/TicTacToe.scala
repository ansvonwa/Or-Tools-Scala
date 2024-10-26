package example

import com.google.ortools.Loader
import com.google.ortools.sat.*
import com.google.ortools.sat.CpModelImplicits.*

object TicTacToe {
  def main(args: Array[String]): Unit = {
    Loader.loadNativeLibraries()
    implicit val model: CpModel = new CpModel

    val squares = Seq.tabulate(9)(i => ("s" + i).boolVar: LinearArgument)

    // rows and cols
    squares.sliding(3, 3)
      .foreach(_.reduce(_ + _) in (1 to 2))
    squares.sliding(3, 3).toSeq.transpose
      .foreach(_.reduce(_ + _) in (1 to 2))
    // diagonals
    squares.sliding(4, 4).map(_.head).reduce(_ + _) in (1 to 2)
    squares.drop(2).sliding(2, 2).map(_.head).reduce(_ + _) in (1 to 2)

//    model.addLinearSumEqual(squares, 4)
    model.minimize(squares.reduce(_ + _))

    println(model.model())

    val solver = new CpSolver()
    solver.solve(model) match {
      case CpSolverStatus.FEASIBLE | CpSolverStatus.OPTIMAL =>
        println("Tic-Tac-Toe with minimal number of 1s to form a draw:")
        println(squares.map(solver.value).sliding(3, 3).map(_.mkString(" ")).mkString("\n"))
      case status =>
        println(status)
    }
  }
}
