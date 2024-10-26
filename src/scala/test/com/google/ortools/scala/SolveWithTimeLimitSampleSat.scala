package example

import com.google.ortools.Loader
import com.google.ortools.sat.CpSolverStatus
import com.google.ortools.sat.CpModel
import com.google.ortools.sat.CpSolver


/** Solves a problem with a time limit. */
object SolveWithTimeLimitSampleSat {
  @throws[Exception]
  def main(args: Array[String]): Unit = { // Create the model.
    Loader.loadNativeLibraries()
    val model = new CpModel
    // Create the variables.
    val numVals = 3
    val x = model.newIntVar(0, numVals - 1, "x")
    val y = model.newIntVar(0, numVals - 1, "y")
    val z = model.newIntVar(0, numVals - 1, "z")
    // Create the constraint.
    model.addDifferent(x, y)
    // Create a solver and solve the model.
    val solver = new CpSolver
    solver.getParameters.setMaxTimeInSeconds(10.0)
    val status = solver.solve(model)
    if (status == CpSolverStatus.FEASIBLE || status == CpSolverStatus.OPTIMAL) {
      System.out.println("x = " + solver.value(x))
      System.out.println("y = " + solver.value(y))
      System.out.println("z = " + solver.value(z))
    }
  }

}