package com.google.ortools.scala

import com.google.ortools.Loader
import com.google.ortools.sat.*
import com.google.ortools.sat.CpModelImplicits.*

class MagicSquareSatWithImplicits(val size: Int = 7) {
  implicit val model: CpModel = new CpModel()

  val square: Seq[Seq[LinearArgument]] = Seq.tabulate(size, size)((i, j) => s"($i,$j)" in 1 to size*size)

  model.addAllDifferent(square.flatten.toArray)

  val sum: Long = size * (size * size + 1) / 2
  // Sum on rows and columns.
  square.foreach(_.sumExpr === sum)
  square.transpose.map(_.sumExpr === sum)

  square.indices.map(i => square(i)(i)).sumExpr === sum
  square.indices.map(i => square(i)(size - 1 - i)).sumExpr === sum

  model.minimize(Seq(square.head.head, square.last.last, square.head.last, square.last.head).sumExpr)

  def solve(): Unit = {
    val solver = new CpSolver()
    solver.solve(model) match {
      case status @ (CpSolverStatus.FEASIBLE | CpSolverStatus.OPTIMAL) =>
        println(s"found a${if ("aeiou".contains(status.toString.toLowerCase.head)) "n" else ""} ${status.toString.toLowerCase} solution!")
        println(square.map(_.map(solver.value).map(" " * 4 + _).map(_.takeRight((size * size).toString.length)).mkString(" ")).mkString("\n"))
      case status =>
        println(s"status = $status")
    }
  }
}

object MagicSquareSatWithImplicits {
  def main(args: Array[String]): Unit = {
    Loader.loadNativeLibraries()
    (1 to 7).foreach { i =>
      new MagicSquareSatWithImplicits(i).solve()
    }
  }
}
