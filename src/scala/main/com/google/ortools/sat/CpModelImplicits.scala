package com.google.ortools.sat

import com.google.ortools.util.Domain

import scala.annotation.targetName
import scala.language.implicitConversions


object CpModelImplicits {
  final class IncompleteRange[A](f: Int => A) {
    def to(end: Int): A = f(end)
  }

  implicit class VarMaker(name: String)(implicit model: CpModel) {
    def in(r: Range): IntVar = model.newIntVar(r.min, r.max, name)
    def in(start: Int): IncompleteRange[IntVar] = IncompleteRange(end => model.newIntVar(start, end, name))
    def boolVar: BoolVar = model.newBoolVar(name)
  }

  implicit class RichCpModel(model: CpModel) {
    // methods that can be used to make the model available as given in a context,
    // without polluting with givens e.g. model.add(foo === bar)
    def add(f: CpModel ?=> Constraint): Constraint =
      f(using model)

    def addAll(f: CpModel ?=> Seq[Constraint]): Seq[Constraint] =
      f(using model)

    def addAll(f: Seq[CpModel ?=> Constraint]): Seq[Constraint] = {
      given CpModel = model
      f.map(x => x)
    }
  }

  // conversions from Scala objects to sat solver Domain
  implicit def range2Domain(r: Range): Domain =
    if (r.step.abs == 1)
      new Domain(r.min, r.max)
    else
      Domain.fromValues(r.map(_.toLong).toArray[Long])

  implicit def seq2Domain[A <: Int | Long : Numeric](seq: Seq[A]): Domain =
    Domain.fromValues(seq.map(summon[Numeric[A]].toLong).toArray[Long])

  implicit class RichLinearArgument(self: LinearArgument) {
    @targetName("plus")
    def +(that: LinearArgument): LinearExpr =
      LinearExpr.sum(Array(self, that))
  }

  implicit class RichLinearExprSeq(self: Seq[LinearArgument]) {
    def sumExpr: LinearExpr = LinearExpr.sum(self.toArray[LinearArgument])

    @targetName("setProductIsEqualTo")
    def prod_===(target: LinearArgument)(implicit model: CpModel): Constraint =
      model.addMultiplicationEquality(target, self.toArray[LinearArgument])
  }

  implicit def int2LinearExpr(i: Int): LinearExpr = LinearExpr.constant(i)
  implicit def long2LinearExpr(l: Long): LinearExpr = LinearExpr.constant(l)

  implicit class LinearArgumentInModel(lhs: LinearArgument)(implicit model: CpModel) {
    def in(domain: Domain): Constraint =
      model.addLinearExpressionInDomain(lhs, domain)
    @targetName("setEqualTo")
    def ===(rhs: Long): Constraint =
      model.addEquality(lhs, rhs)
    @targetName("setEqualTo")
    def ===(rhs: LinearArgument): Constraint =
      model.addEquality(lhs, rhs)
    @targetName("setLessOrEqual")
    def <=(rhs: LinearArgument): Constraint =
      model.addLessOrEqual(lhs, rhs)
    @targetName("setLessThan")
    def <(rhs: LinearArgument): Constraint =
      model.addLessThan(lhs, rhs)
    @targetName("setGreaterOrEqual")
    def >=(rhs: LinearArgument): Constraint =
      model.addGreaterOrEqual(lhs, rhs)
    @targetName("setGreaterThan")
    def >(rhs: LinearArgument): Constraint =
      model.addGreaterThan(lhs, rhs)
  }

}
