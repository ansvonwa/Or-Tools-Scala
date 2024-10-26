package com.google.ortools.scala

import com.google.ortools.Loader
import com.google.ortools.linearsolver.MPSolver
import com.google.ortools.linearsolver.LinearSolverImplicits.*

/**
 *  Linear programming example that shows how to use the API.
 *  1:1 translation of the java example https://github.com/google/or-tools/blob/stable/examples/java/LinearProgramming.java
 */
object LinearProgramming {
  private def runLinearProgrammingExample(solverType: String, printModel: Boolean): Unit = {
    val solver = MPSolver.createSolver(solverType)
    if (solver == null) {
      println("Could not create solver " + solverType)
      return
    }

    // x1, x2 and x3 are continuous non-negative variables.
    val x1 = solver.makeNonNegNumVar("x1")
    val x2 = solver.makeNonNegNumVar("x2")
    val x3 = solver.makeNonNegNumVar("x3")

    solver.maximize(10 * x1 + 6 * x2 + 4 * x3)

    val c0 = solver.addConstraint(x1 + x2 + x3 <= 100)
    val c1 = solver.addConstraint(x1 * 10 + x2 * 4 + x3 * 5 <= 600)
    val c2 = solver.addConstraint(2 * x1 + 2 * x2 + 6 * x3 <= 300)

    println("Number of variables = " + solver.numVariables)
    println("Number of constraints = " + solver.numConstraints)

    if (printModel) {
      val model = solver.exportModelAsLpFormat()
      println(model)
    }

    val resultStatus = solver.solve()

    // Check that the problem has an optimal solution.
    if (resultStatus != MPSolver.ResultStatus.OPTIMAL) {
      System.err.println("The problem does not have an optimal solution!")
      return
    }

    // Verify that the solution satisfies all constraints (when using solvers
    // others than GLOP_LINEAR_PROGRAMMING, this is highly recommended!).
    if (!solver.verifySolution(/*tolerance=*/ 1e-7, /*logErrors=*/ true)) {
      System.err.println("The solution returned by the solver violated the" +
        " problem constraints by at least 1e-7")
      return
    }

    println("Problem solved in " + solver.wallTime + " milliseconds")

    // The objective value of the solution.
    println("Optimal objective value = " + solver.objective.value)

    // The value of each variable in the solution.
    println("x1 = " + x1.solutionValue)
    println("x2 = " + x2.solutionValue)
    println("x3 = " + x3.solutionValue)

    val activities = solver.computeConstraintActivities

    println("Advanced usage:")
    println("Problem solved in " + solver.iterations + " iterations")
    println("x1: reduced cost = " + x1.reducedCost)
    println("x2: reduced cost = " + x2.reducedCost)
    println("x3: reduced cost = " + x3.reducedCost)
    println("c0: dual value = " + c0.dualValue)
    println("    activity = " + activities(c0.index))
    println("c1: dual value = " + c1.dualValue)
    println("    activity = " + activities(c1.index))
    println("c2: dual value = " + c2.dualValue)
    println("    activity = " + activities(c2.index))
  }

  def main(args: Array[String]): Unit = {
    Loader.loadNativeLibraries()
    println("---- Linear programming example with GLOP (recommended) ----")
    runLinearProgrammingExample("GLOP_LINEAR_PROGRAMMING", true)
    println("---- Linear programming example with CLP ----")
    runLinearProgrammingExample("CLP_LINEAR_PROGRAMMING", false)
//    println("---- Linear programming example with SCIP ----")
//    runLinearProgrammingExample("SCIP", false)
//    println("---- Linear programming example with XPRESS ----")
//    runLinearProgrammingExample("XPRESS_LINEAR_PROGRAMMING", false)
  }

}
