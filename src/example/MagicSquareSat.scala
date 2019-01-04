package example

import com.google.ortools.sat._

class MagicSquareSat(val size: Int = 7) {
  val model = new CpModel()

  val square: Array[Array[IntVar]] = Array.tabulate(size, size)((i, j) => model.newIntVar(1, size * size, s"($i,$j)"))

  model.addAllDifferent(square.flatten)

  val sum: Int = size * (size * size + 1) / 2
  // Sum on rows and columns.
  square.foreach(row => model.addLinearSumEqual(row, sum))
  square.transpose.foreach(col => model.addLinearSumEqual(col, sum))
  // Sum on diagonals
  model.addLinearSumEqual(square.indices.map(i => square(i)(i)).toArray, sum)
  model.addLinearSumEqual(square.indices.map(i => square(i)(size - 1 - i)).toArray, sum)

  def solve(): Unit = {
    val solver = new CpSolver()
    solver.solve(model) match {
      case CpSolverStatus.FEASIBLE | CpSolverStatus.OPTIMAL =>
        println("found a solution!")
        println(square.map(_.map(solver.value).map(" " * 4 + _).map(_.takeRight((size * size).toString.length)).mkString(" ")).mkString("\n"))
      case status => // FIXME: the solver crashes (sometimes?) if the model is infeasible
        println(s"status = $status")
    }
  }
}

object MagicSquareSat {
  System.loadLibrary("jniortools")

  def main(args: Array[String]): Unit = {
    Stream.from(1).foreach { i =>
      new MagicSquareSat(i).solve()
    }
  }
}
