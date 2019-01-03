package example

import com.google.ortools.constraintsolver.ConstraintSolverParameters
import com.google.ortools.constraintsolver.Solver
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

import com.google.ortools.constraintsolver.ConstraintSolverImplicits._

/** Sample showing how to model using the constraint programming solver. */
object RabbitsPheasants {

  /**
    * Solves the rabbits + pheasants problem. We are seeing 20 heads and 56 legs. How many rabbits and
    * how many pheasants are we thus seeing?
    */
  private def solve(traceSearch: Boolean): Unit = {
    val parameters = ConstraintSolverParameters.newBuilder.mergeFrom(Solver.defaultSolverParameters).setTraceSearch(traceSearch).build
    implicit val solver = new Solver("RabbitsPheasants", parameters)
    val rabbits = solver.makeIntVar(0, 100, "rabbits")
    val pheasants = solver.makeIntVar(0, 100, "pheasants")
    solver.addConstraint(rabbits + pheasants === 20)
    solver.addConstraint(rabbits * 4 + pheasants * 2 === 56)
    val db = solver.makePhase(rabbits, pheasants, Solver.CHOOSE_FIRST_UNBOUND, Solver.ASSIGN_MIN_VALUE)
    solver.newSearch(db)
    solver.nextSolution
    println(rabbits.toString)
    println(pheasants.toString)
    solver.endSearch()
  }

  @throws[Exception]
  def main(args: Array[String]): Unit = {
    val traceSearch = args.length > 0 && args(1) == "--trace"
    RabbitsPheasants.solve(traceSearch)
  }

  try System.loadLibrary("jniortools")

}
