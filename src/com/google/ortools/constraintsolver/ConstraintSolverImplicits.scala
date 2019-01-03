package com.google.ortools.constraintsolver

import scala.language.implicitConversions

object ConstraintSolverImplicits {

  implicit def long2RichIntExpr(l: Long)(implicit solver: Solver): IntExpr = solver.makeIntConst(l)
  implicit def int2RichIntExpr(l: Int)(implicit solver: Solver): IntExpr = solver.makeIntConst(l)

  def True(implicit solver: Solver): Constraint = solver.makeTrueConstraint()
  def False(implicit solver: Solver): Constraint = solver.makeFalseConstraint()

  implicit class RichIntExpr(lhs: IntExpr)(implicit solver: Solver) {
    def +(rhs: IntExpr): IntExpr = solver.makeSum(lhs, rhs)
    def -(rhs: IntExpr): IntExpr = solver.makeDifference(lhs, rhs)
    def unary_- : IntExpr = solver.makeOpposite(lhs)
    def *(rhs: IntExpr): IntExpr = solver.makeProd(lhs, rhs)
    def /(rhs: IntExpr): IntExpr = solver.makeDiv(lhs, rhs)
    def %(rhs: IntExpr): IntExpr = solver.MakeModulo(lhs, rhs)

    def abs: IntExpr = solver.makeAbs(lhs)
    def squared: IntExpr = solver.makeSquare(lhs)

    def **(exponent: Long): IntExpr = solver.MakePower(lhs, exponent)

    def min(rhs: IntExpr): IntExpr = solver.makeMin(lhs, rhs)
    def max(rhs: IntExpr): IntExpr = solver.makeMax(lhs, rhs)

    def ===(rhs: IntExpr): Constraint = solver.makeEquality(lhs, rhs)
    def !==(rhs: IntExpr): Constraint = solver.makeNonEquality(lhs, rhs)

    def <(rhs: IntExpr): Constraint = solver.makeLess(lhs, rhs)
    def <=(rhs: IntExpr): Constraint = solver.makeLessOrEqual(lhs, rhs)
    def >(rhs: IntExpr): Constraint = solver.makeGreater(lhs, rhs)
    def >=(rhs: IntExpr): Constraint = solver.makeGreaterOrEqual(lhs, rhs)
  }

}
