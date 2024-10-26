package com.google.ortools.scala

import com.google.ortools.Loader
import com.google.ortools.sat.*

class MagicSquareSat(val size: Int = 7) {
  val model = new CpModel()

  val square: Array[Array[LinearArgument]] = Array.tabulate(size, size)((i, j) => model.newIntVar(1, size * size, s"($i,$j)"))

  model.addAllDifferent(square.flatten)

  val sum: Int = size * (size * size + 1) / 2
  // Sum on rows and columns.
  square.foreach(row => model.addEquality(LinearExpr.sum(row), sum))
  square.transpose.foreach(col => model.addEquality(LinearExpr.sum(col), sum))
  // Sum on diagonals
  model.addEquality(LinearExpr.sum(square.indices.map(i => square(i)(i)).toArray), sum)
  model.addEquality(LinearExpr.sum(square.indices.map(i => square(i)(size - 1 - i)).toArray), sum)

  def solve(): Unit = {
    val solver = new CpSolver()
    solver.solve(model) match {
      case CpSolverStatus.FEASIBLE | CpSolverStatus.OPTIMAL =>
        println("found a solution!")
        println(square.map(_.map(solver.value).map(" " * 4 + _).map(_.takeRight((size * size).toString.length)).mkString(" ")).mkString("\n"))
      case status =>
        println(s"status = $status")
    }
  }
}

object MagicSquareSat {
  def main(args: Array[String]): Unit = {
    Loader.loadNativeLibraries()
    Iterator.from(1).foreach { i =>
      new MagicSquareSat(i).solve()
    }
  }
}
