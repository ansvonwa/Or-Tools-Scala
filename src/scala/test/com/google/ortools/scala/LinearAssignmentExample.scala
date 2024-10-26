package example

import com.google.ortools.Loader
import com.google.ortools.graph.LinearSumAssignment
// Copyright 2010-2017 Google
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


/**
  * Test assignment on a 4x4 matrix. Example taken from
  * http://www.ee.oulu.fi/~mpa/matreng/eem1_2-1.htm with kCost[0][1] modified so the optimum solution
  * is unique.
  */
object LinearAssignmentExample {
  private def runAssignmentOn4x4Matrix(): Unit = {
    val numSources = 4
    val numTargets = 4
    val cost = Array(
      Array(90, 76, 75, 80),
      Array(35, 85, 55, 65),
      Array(125, 95, 90, 105),
      Array(45, 110, 95, 115),
    )
    val expectedCost = cost(0)(3) + cost(1)(2) + cost(2)(1) + cost(3)(0)
    val assignment = new LinearSumAssignment
    (0 until numSources).foreach(source =>
      (0 until numTargets).foreach(target =>
        assignment.addArcWithCost(source, target, cost(source)(target))))
    if (assignment.solve == LinearSumAssignment.Status.OPTIMAL) {
      println("Total cost = " + assignment.getOptimalCost + "/" + expectedCost)

      (0 until assignment.getNumNodes).foreach(node =>
        println("Left node " + node + " assigned to right node " + assignment.getRightMate(node) + " with cost " + assignment.getAssignmentCost(node)))
    }
    else println("No solution found.")
  }

  @throws[Exception]
  def main(args: Array[String]): Unit = {
    Loader.loadNativeLibraries()
    LinearAssignmentExample.runAssignmentOn4x4Matrix()
  }

}
