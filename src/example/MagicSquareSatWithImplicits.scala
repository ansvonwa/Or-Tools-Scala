package example

import com.google.ortools.sat._
import com.google.ortools.sat.CpModelImplicits._

class MagicSquareSatWithImplicits(val size: Int = 7) {
  implicit val model = new CpModel()

  val square: Seq[Seq[IntVar]] = Seq.tabulate(size, size)((i, j) => s"($i,$j)" in 1 to size*size)

  model.addAllDifferent(square.flatten.toArray)

  val sum: Int = size * (size * size + 1) / 2
  // Sum on rows and columns.
  model.addAll(
    square.map(Sum(_) === sum),
    square.transpose.map(Sum(_) === sum),
  )
  model.add(
    Sum(square.indices.map(i => square(i)(i))) === sum,
    Sum(square.indices.map(i => square(i)(size - 1 - i))) === sum,
  )
//  model.minimizeSum(Array(square.head.head, square.last.last, square.head.last, square.last.head))

  def solve(): Unit = {
    val solver = new CpSolver()
    solver.solve(model) match {
      case status @ (CpSolverStatus.FEASIBLE | CpSolverStatus.OPTIMAL) =>
        println(s"found a${if ("aeiou".contains(status.toString.toLowerCase.head)) "n" else ""} ${status.toString.toLowerCase} solution!")
        println(square.map(_.map(solver.value).map(" " * 4 + _).map(_.takeRight((size * size).toString.length)).mkString(" ")).mkString("\n"))
      case status => // FIXME: the solver crashes (sometimes?) if the model is infeasible
        println(s"status = $status")
    }
  }
}

object MagicSquareSatWithImplicits {
  System.loadLibrary("jniortools")

  def main(args: Array[String]): Unit = {
    new MagicSquareSatWithImplicits(5).solve()
  }
}
