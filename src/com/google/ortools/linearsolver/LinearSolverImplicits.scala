package com.google.ortools.linearsolver

import scala.language.implicitConversions

object LinearSolverImplicits {
  final val infinity = MPSolver.infinity

  implicit def mpVar2Linear(mPVariable: MPVariable): Linear = prod(mPVariable, 1)

  private def prod(mpv: MPVariable, d: Double) = Linear(Seq((mpv, d)))

  case class Constraint(lb: Double, linear: Linear, ub: Double) {
    def <=(ub: Double) = Constraint(lb, linear, math.min(this.ub, ub))

    override def toString: String =
      (if (lb > -infinity) lb + " <= " else "") +
        linear.toString.drop(7).dropRight(1) +
        (if (ub < infinity) " <= " + ub else "")
  }

  implicit class LowerBoundDouble(lb: Double) {
    def <=(linear: Linear) = Constraint(lb, linear, infinity)
  }

  case class Linear(list: Seq[(MPVariable, Double)]) {
    def +(other: Linear): Linear = Linear(list ++ other.list)
    def -(other: Linear): Linear = this + Linear(other.list.map { case (mpv, d) => (mpv, -d) })
    def *(scale: Double): Linear = Linear(list.map { case (mpv, d) => (mpv, d * scale) })
    def <=(ub: Double) = Constraint(-infinity, this, ub)
    def >=(lb: Double) = Constraint(lb, this, infinity)

    override def toString: String = "Linear(" + list.map { case (mpv, d) => mpv.name + " * " + d }.mkString(" + ") + ")"
  }

  implicit class Prefactor(d: Double) {
    def *(mPVariable: MPVariable): Linear = prod(mPVariable, d)
    def *(linear: Linear): Linear = linear * d
  }

  implicit class RichMPSolver(val solver: MPSolver) {
    def addConstraint(c: Constraint, name: String = null): MPConstraint = {
      val cons = if (name == null) solver.makeConstraint() else solver.makeConstraint(name)
      cons.setBounds(c.lb, c.ub)
      c.linear.list.foreach { case (mpv, d) =>
        cons.setCoefficient(mpv, d)
      }
      cons
    }

    def maximize(linear: Linear): MPObjective = {
      val obj = solver.objective()
      linear.list.foreach { case (mpv, d) =>
        obj.setCoefficient(mpv, d)
      }
      obj.setMaximization()
      obj
    }

    def minimize(linear: Linear): MPObjective = {
      val obj = solver.objective()
      linear.list.foreach { case (mpv, d) =>
        obj.setCoefficient(mpv, d)
      }
      obj.setMinimization()
      obj
    }

    def makeNumVar(name: String): MPVariable = solver.makeNumVar(-infinity, infinity, name)
    def makeIntVar(name: String): MPVariable = solver.makeIntVar(-infinity, infinity, name)
    def makeNonNegNumVar(name: String): MPVariable = solver.makeNumVar(0, infinity, name)
    def makeNonNegIntVar(name: String): MPVariable = solver.makeIntVar(0, infinity, name)
  }

}
