package com.google.ortools.sat

import com.google.ortools.sat.{Constraint, CpModel, IntVar}

import scala.collection.SeqLike

object CpModelImplicits {
  trait ConstraintDescription {
    def addConstraintTo(implicit model: CpModel): Constraint
  }
  def long2IntVar[A <: ConstraintDescription](long: Long, f: IntVar => A): NeedsModelToEvaluateToConstraint[A] =
    NeedsModelToEvaluateToConstraint(model => f(model.newConstant(long)))

  case class NeedsModelToEvaluateToConstraint[CD <: ConstraintDescription](toCD: CpModel => ConstraintDescription) extends ConstraintDescription {
    def addConstraintTo(implicit model: CpModel): Constraint = toCD(model) addConstraintTo model
  }

  trait EqualityOperation {
    private[sat] def addToModelEqualTo(model: CpModel, rhs: IntVar): Constraint
    def ===(rhs: IntVar) = Equality(this, rhs)
//    def ===(rhs: Long) = NeedsModelToEvaluateToConstraint(model => Equality(this, model.newConstant(rhs)))
    def ===(rhs: Long): NeedsModelToEvaluateToConstraint[Equality[_]] = long2IntVar(rhs, ===)
  }
  case class Equality[Op <: EqualityOperation](op: Op, rhs: IntVar) extends ConstraintDescription {
    def addConstraintTo(implicit model: CpModel): Constraint = op.addToModelEqualTo(model, rhs)
  }

  trait InRangeOperation {
    self =>
    private[sat] def addToModelInRange(model: CpModel, rhs: Range): Constraint
    def ϵ(rhs: Range): InRange[this.type] = InRange(this, rhs)
    def in(rhs: Range): InRange[this.type] = InRange(this, rhs)
    def ϵ(start: Int): AnyRef {def to(end: Int): InRange[self.type]} = new AnyRef {
      def to(end: Int): InRange[self.type] = self.in(start to end)
    }
    def in(start: Int): IncompleteRange[InRange[this.type]] = IncompleteRange((end: Int) => InRange(this, start to end))
  }
  case class InRange[Op <: InRangeOperation](op: Op, rhs: Range) extends ConstraintDescription {
    def addConstraintTo(implicit model: CpModel): Constraint = op.addToModelInRange(model, rhs)
  }

  trait LessOrEqualOperation {
    private[sat] def addToModelLessOrEqual(model: CpModel, rhs: IntVar): Constraint
    def <=(rhs: IntVar) = LessOrEqual(this, rhs)
    def <=(rhs: Long): NeedsModelToEvaluateToConstraint[LessOrEqual[_]] = long2IntVar(rhs, <=)
  }
  case class LessOrEqual[Op <: LessOrEqualOperation](op: Op, rhs: IntVar) extends ConstraintDescription {
    override def addConstraintTo(implicit model: CpModel): Constraint = op.addToModelLessOrEqual(model, rhs)
  }

  trait GreaterOrEqualOperation {
    private[sat] def addToModelGreaterOrEqual(model: CpModel, rhs: IntVar): Constraint
    def >=(rhs: IntVar) = GreaterOrEqual(this, rhs)
    def >=(rhs: Long): NeedsModelToEvaluateToConstraint[GreaterOrEqual[_]] = long2IntVar(rhs, >=)
  }
  case class GreaterOrEqual[Op <: GreaterOrEqualOperation](op: Op, rhs: IntVar) extends ConstraintDescription {
    override def addConstraintTo(implicit model: CpModel): Constraint = op.addToModelGreaterOrEqual(model, rhs)
  }

  implicit class SimpleIntVar(lhs: IntVar) extends EqualityOperation with InRangeOperation with LessOrEqualOperation with GreaterOrEqualOperation {
    override private[sat] def addToModelEqualTo(model: CpModel, rhs: IntVar): Constraint = model.addEquality(lhs, rhs)
    override private[sat] def addToModelInRange(model: CpModel, rhs: Range) = {
      model.addGreaterOrEqual(lhs, rhs.min)
      model.addLessOrEqual(lhs, rhs.max)
      null // prevent returning only one of the constraints
    }
    override private[sat] def addToModelLessOrEqual(model: CpModel, rhs: IntVar) = model.addLessOrEqual(lhs, rhs)
    override private[sat] def addToModelGreaterOrEqual(model: CpModel, rhs: IntVar) = model.addLessOrEqual(rhs, lhs)
  }
  case class Product(seq: Seq[IntVar]) extends EqualityOperation {
    override def addToModelEqualTo(model: CpModel, rhs: IntVar): Constraint = model.addMultiplicationEquality(rhs, seq.toArray)
  }
  def Σ(seq: Seq[IntVar]) = Sum(seq)
  case class Sum(seq: Seq[IntVar]) extends EqualityOperation with InRangeOperation {
    override def addToModelEqualTo(model: CpModel, rhs: IntVar): Constraint = ??? // model.addLinearSumEqual(seq.toArray, rhs)
    override def addToModelInRange(model: CpModel, rhs: Range): Constraint = ??? // model.addLinearSum(seq.toArray, rhs.min, rhs.max)
  }
  case class Modulo(a: IntVar, b: IntVar) extends EqualityOperation {
    override def addToModelEqualTo(model: CpModel, rhs: IntVar): Constraint = model.addModuloEquality(a, rhs, b) // TODO check order
  }

  implicit class IntVarSeqOps[A <: Seq[IntVar]](a: A) {
    def mkSum = Sum(a)
    def prod = Product(a)
  }

  implicit class IntVarOps(a: IntVar) {
    def % (b: IntVar) = Modulo(a, b)
  }

  case class IncompleteRange[A](f: Int => A) {
    def to(end: Int): A = f(end)
  }

  implicit class VarMaker(name: String)(implicit model: CpModel) {
    def in(r: Range): IntVar = model.newIntVar(r.min, r.max, name)
    def in(start: Int): IncompleteRange[IntVar] = IncompleteRange(end => model.newIntVar(start, end, name))
    def boolVar: IntVar = model.newBoolVar(name)
  }

  implicit class RichCpModel(model: CpModel) {
    def add(conDesc: ConstraintDescription): Constraint = {
      conDesc.addConstraintTo(model)
    }

    def addAll(conDescs: Seq[ConstraintDescription], more: Seq[ConstraintDescription]*): Unit = {
      conDescs.foreach(add)
      more.foreach(_.foreach(add))
    }

    def add(conDesc: ConstraintDescription, more: ConstraintDescription*): Unit = {
      add(conDesc)
      more.foreach(add)
    }
  }

}
