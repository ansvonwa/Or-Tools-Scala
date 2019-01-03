# Or-Tools-Scala

[Googles or-tools](https://github.com/google/or-tools/) have [Java-bindings](https://developers.google.com/optimization/introduction/java), so you can use them in Scala.

This project just simplifies the usage.

## Example
Now, you can write
```scala
val x1 = solver.makeNonNegVar("x1")
val x2 = solver.makeNonNegVar("x2")
val x3 = solver.makeNonNegVar("x3")

solver.maximize(10 * x1 + 6 * x2 + 4 * x3)

val c0 = solver.addConstraint(x1 + x2 + x3 <= 100)
val c1 = solver.addConstraint(x1 * 10 + x2 * 4 + x3 * 5 <= 600)
val c2 = solver.addConstraint(2 * x1 + 2 * x2 + 6 * x3 <= 300) // note that the order does not matter
```
instead of the java-version
```java
double infinity = MPSolver.infinity();
// x1, x2 and x3 are continuous non-negative variables.
MPVariable x1 = solver.makeNumVar(0.0, infinity, "x1");
MPVariable x2 = solver.makeNumVar(0.0, infinity, "x2");
MPVariable x3 = solver.makeNumVar(0.0, infinity, "x3");

// Maximize 10 * x1 + 6 * x2 + 4 * x3.
MPObjective objective = solver.objective();
objective.setCoefficient(x1, 10);
objective.setCoefficient(x2, 6);
objective.setCoefficient(x3, 4);
objective.setMaximization();

// x1 + x2 + x3 <= 100.
MPConstraint c0 = solver.makeConstraint(-infinity, 100.0);
c0.setCoefficient(x1, 1);
c0.setCoefficient(x2, 1);
c0.setCoefficient(x3, 1);

// 10 * x1 + 4 * x2 + 5 * x3 <= 600.
MPConstraint c1 = solver.makeConstraint(-infinity, 600.0);
c1.setCoefficient(x1, 10);
c1.setCoefficient(x2, 4);
c1.setCoefficient(x3, 5);

// 2 * x1 + 2 * x2 + 6 * x3 <= 300.
MPConstraint c2 = solver.makeConstraint(-infinity, 300.0);
c2.setCoefficient(x1, 2);
c2.setCoefficient(x2, 2);
c2.setCoefficient(x3, 6);
```

Compare [Scala](https://github.com/ansvonwa/Or-Tools-Scala/blob/master/src/example/LinearProgramming.scala) and [Java](https://github.com/google/or-tools/blob/master/examples/java/LinearProgramming.java) version!

## Getting Started
`clone`, copy lib folder from or-tools into the project, import in intellij and run


