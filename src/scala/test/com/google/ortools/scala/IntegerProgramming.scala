package com.google.ortools.scala

import com.google.ortools.Loader
import com.google.ortools.linearsolver.MPSolver
// Copyright 2010-2018 Google LLC
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0

// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

import com.google.ortools.linearsolver.LinearSolverImplicits._

/** Integer programming example that shows how to use the API. */
object IntegerProgramming {
  private def runIntegerProgrammingExample(solverType: String): Unit = {
    val solver = MPSolver.createSolver(solverType)
    if (solver == null) {
      println("Could not create solver " + solverType)
      return
    }
    // x1 and x2 are integer non-negative variables.
    val x1 = solver.makeNonNegIntVar("x1")
    val x2 = solver.makeNonNegIntVar("x2")

    solver.minimize(x1 + 2 * x2)

    solver.addConstraint(2 * x2 + 3 * x1 >= 17)

    val resultStatus = solver.solve
    // Check that the problem has an optimal solution.
    if (resultStatus != MPSolver.ResultStatus.OPTIMAL) {
      System.err.println("The problem does not have an optimal solution!")
      return
    }
    // Verify that the solution satisfies all constraints (when using solvers
    // others than GLOP_LINEAR_PROGRAMMING, this is highly recommended!).
    if (!solver.verifySolution(/*tolerance=*/ 1e-7, /*logErrors=*/ true)) {
      System.err.println("The solution returned by the solver violated the" + " problem constraints by at least 1e-7")
      return
    }
    println("Problem solved in " + solver.wallTime + " milliseconds")
    // The objective value of the solution.
    println("Optimal objective value = " + solver.objective.value)
    // The value of each variable in the solution.
    println("x1 = " + x1.solutionValue)
    println("x2 = " + x2.solutionValue)
    println("Advanced usage:")
    println("Problem solved in " + solver.nodes + " branch-and-bound nodes")
  }

  def main(args: Array[String]): Unit = {
    Loader.loadNativeLibraries()
    println("---- Integer programming example with SCIP (recommended) ----")
    runIntegerProgrammingExample("SCIP_MIXED_INTEGER_PROGRAMMING")
    println("---- Integer programming example with CBC ----")
    runIntegerProgrammingExample("CBC_MIXED_INTEGER_PROGRAMMING")
    println("---- Integer programming example with SCIP ----")
    runIntegerProgrammingExample("SCIP")
  }

}
