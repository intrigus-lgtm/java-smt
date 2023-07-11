// This file is part of JavaSMT,
// an API wrapper for a collection of SMT solvers:
// https://github.com/sosy-lab/java-smt
//
// SPDX-FileCopyrightText: 2021 Dirk Beyer <https://www.sosy-lab.org>
//
// SPDX-License-Identifier: Apache-2.0

package org.sosy_lab.java_smt.solvers.cvc5;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;

import com.google.common.base.Preconditions;
import io.github.cvc5.CVC5ApiException;
import io.github.cvc5.Kind;
import io.github.cvc5.Op;
import io.github.cvc5.Result;
import io.github.cvc5.RoundingMode;
import io.github.cvc5.Solver;
import io.github.cvc5.Sort;
import io.github.cvc5.Term;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.After;
import org.junit.AssumptionViolatedException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.sosy_lab.common.NativeLibraries;

/*
 * Please note that CVC5 does not have a native variable cache!
 * Each variable created is a new one with a new internal id, even if they are named the same.
 * As a result, checking equality on 2 formulas that are build with new variables
 *  that are named the same results in false!
 * Additionally, CVC5 only supports quantifier elimination for LIA and LRA.
 * However, it might run endlessly in some cases if you try quantifier elimination on array
 * theories!
 */
public class CVC5NativeAPITest {

  private static final String INVALID_GETVALUE_STRING_SAT =
      "Cannot get value unless after a SAT or UNKNOWN response.";

  private static final String INVALID_TERM_BOUND_VAR =
      "Cannot process term .* with free variables: .*";

  private static final String INVALID_MODEL_STRING =
      "Cannot get model unless after a SAT or UNKNOWN response.";

  @BeforeClass
  public static void loadCVC5() {
    try {
      CVC5SolverContext.loadLibrary(NativeLibraries::loadLibrary);
    } catch (UnsatisfiedLinkError e) {
      throw new AssumptionViolatedException("CVC5 is not available", e);
    }
  }

  private Term x;
  private Term array;
  private Term aAtxEq0;
  private Term aAtxEq1;

  private Solver solver;

  @Before
  public void init() throws CVC5ApiException {
    solver = createEnvironment();
  }

  private static Solver createEnvironment() throws CVC5ApiException {
    Solver newSolver = new Solver();
    newSolver.setLogic("ALL");

    // options
    newSolver.setOption("incremental", "true");
    newSolver.setOption("produce-models", "true");
    newSolver.setOption("finite-model-find", "true");
    newSolver.setOption("sets-ext", "true");
    newSolver.setOption("output-language", "smtlib2");
    newSolver.setOption("strings-exp", "true");

    return newSolver;
  }

  @After
  public void freeEnvironment() {
    solver.deletePointer();
  }

  /*
   * Check how to get types/values etc. from constants, variables etc. in CVC5.
   * You can get the values of constants via toString()
   * and the name of variables via toString().
   * One can use getOp() on a Term to get its operator.
   * This operator can be used to create the same Term again with the same arguments.
   * The Ids match.
   */
  @Test
  public void checkGetValueAndType() throws CVC5ApiException {
    // Constant values (NOT Kind,CONSTANT!)
    assertThat(solver.mkBoolean(false).isBooleanValue()).isTrue();
    assertThat(solver.mkInteger(0).isIntegerValue()).isTrue();
    assertThat(solver.mkInteger(999).isIntegerValue()).isTrue();
    assertThat(solver.mkInteger(-1).isIntegerValue()).isTrue();
    assertThat(solver.mkInteger("0").isIntegerValue()).isTrue();
    assertThat(solver.mkString("").isStringValue()).isTrue();
    // Note: toString on String values does not equal the value!!
    assertThat(solver.mkString("").toString()).isNotEqualTo("");
    assertThat(solver.mkString("").getStringValue()).isEqualTo("");
    // Variables (named const, because thats not confusing....)
    // Variables (Consts) return false if checked for value!
    assertThat(solver.mkConst(solver.getBooleanSort()).isBooleanValue()).isFalse();
    assertThat(solver.mkConst(solver.getIntegerSort()).isIntegerValue()).isFalse();
    // To check for variables we have to check for value and type
    assertThat(solver.mkConst(solver.getBooleanSort()).getSort().isBoolean()).isTrue();

    // Test consts (variables). Consts are always false when checked for isTypedValue(), if you try
    // getTypedValue() on it anyway an exception is raised. This persists after sat. The only way of
    // checking and geting the values is via Kind.CONSTANT, type = sort and getValue()
    Term intVar = solver.mkConst(solver.getIntegerSort(), "int_const");
    assertThat(intVar.isIntegerValue()).isFalse();
    assertThat(intVar.getSort().isInteger()).isTrue();
    Exception e = assertThrows(io.github.cvc5.CVC5ApiException.class, intVar::getIntegerValue);
    assertThat(e.toString())
        .contains(
            "Invalid argument 'int_const' for '*d_node', expected Term to be an integer value when"
                + " calling getIntegerValue()");
    // Build a formula such that is has a value, assert and check sat and then check again
    Term equality = solver.mkTerm(Kind.EQUAL, intVar, solver.mkInteger(1));
    solver.assertFormula(equality);
    // Is sat, no need to check
    solver.checkSat();
    assertThat(intVar.isIntegerValue()).isFalse();
    assertThat(intVar.getSort().isInteger()).isTrue();
    assertThat(intVar.getKind()).isEqualTo(Kind.CONSTANT);
    assertThat(intVar.getKind()).isNotEqualTo(Kind.VARIABLE);
    assertThat(solver.getValue(intVar).toString()).isEqualTo("1");
    // Op test
    assertThat(equality.getOp().toString()).isEqualTo("EQUAL");
    assertThat(
            solver.mkTerm(equality.getOp(), intVar, solver.mkInteger(1)).getId()
                == equality.getId())
        .isTrue();
    // Note that variables (Kind.VARIABLES) are bound variables!
    assertThat(solver.mkVar(solver.getIntegerSort()).getKind()).isEqualTo(Kind.VARIABLE);
    assertThat(solver.mkVar(solver.getIntegerSort()).getKind()).isNotEqualTo(Kind.CONSTANT);
    // Uf return sort is codomain
    // Uf unapplied are CONSTANT
    Sort intToBoolSort = solver.mkFunctionSort(solver.getIntegerSort(), solver.getBooleanSort());
    assertThat(intToBoolSort.getFunctionCodomainSort().isBoolean()).isTrue();
    Term uf1 = solver.mkConst(intToBoolSort);
    assertThat(uf1.getKind()).isNotEqualTo(Kind.VARIABLE);
    assertThat(uf1.getKind()).isEqualTo(Kind.CONSTANT);
    assertThat(uf1.getKind()).isNotEqualTo(Kind.APPLY_UF);
    assertThat(intToBoolSort.isFunction()).isTrue();
    assertThat(uf1.getSort().isFunction()).isTrue();
    // arity 1
    assertThat(uf1.getSort().getFunctionArity()).isEqualTo(1);
    // apply the uf, the kind is now APPLY_UF
    Term appliedUf1 = solver.mkTerm(Kind.APPLY_UF, new Term[] {uf1, intVar});
    assertThat(appliedUf1.getKind()).isNotEqualTo(Kind.VARIABLE);
    assertThat(appliedUf1.getKind()).isNotEqualTo(Kind.CONSTANT);
    assertThat(appliedUf1.getKind()).isEqualTo(Kind.APPLY_UF);
    assertThat(appliedUf1.getSort().isFunction()).isFalse();
    // The ufs sort is always the returntype
    assertThat(appliedUf1.getSort()).isEqualTo(solver.getBooleanSort());
    assertThat(appliedUf1.getNumChildren()).isEqualTo(2);
    // The first child is the UF
    assertThat(appliedUf1.getChild(0).getSort()).isEqualTo(intToBoolSort);
    // The second child onwards are the arguments
    assertThat(appliedUf1.getChild(1).getSort()).isEqualTo(solver.getIntegerSort());
  }

  /*
   *  Try to convert real -> int -> bv -> fp; which fails at the fp step.
   *  Use Kind.FLOATINGPOINT_TO_FP_REAL instead!
   */
  @Test
  public void checkFPConversion() throws CVC5ApiException {
    Term oneFourth = solver.mkReal("1/4");
    Term intOneFourth = solver.mkTerm(Kind.TO_INTEGER, oneFourth);
    Term bvOneFourth = solver.mkTerm(solver.mkOp(Kind.INT_TO_BITVECTOR, 32), intOneFourth);

    Exception e =
        assertThrows(
            io.github.cvc5.CVC5ApiException.class,
            () -> solver.mkFloatingPoint(8, 24, bvOneFourth));
    assertThat(e.toString())
        .contains(
            "Invalid argument '((_ int2bv 32) (to_int (/ 1 4)))' for 'val', expected bit-vector"
                + " constant");
  }

  @Test
  public void checkSimpleUnsat() {
    solver.assertFormula(solver.mkBoolean(false));
    Result satCheck = solver.checkSat();
    assertThat(satCheck.isUnsat()).isTrue();
  }

  @Test
  public void checkSimpleSat() {
    solver.assertFormula(solver.mkBoolean(true));
    Result satCheck = solver.checkSat();
    assertThat(satCheck.isSat()).isTrue();
  }

  @Test
  public void checkSimpleEqualitySat() {
    Term one = solver.mkInteger(1);
    Term assertion = solver.mkTerm(Kind.EQUAL, one, one);
    solver.assertFormula(assertion);
    Result satCheck = solver.checkSat();
    assertThat(satCheck.isSat()).isTrue();
  }

  @Test
  public void checkSimpleEqualityUnsat() {
    Term zero = solver.mkInteger(0);
    Term one = solver.mkInteger(1);
    Term assertion = solver.mkTerm(Kind.EQUAL, zero, one);
    solver.assertFormula(assertion);
    Result satCheck = solver.checkSat();
    assertThat(satCheck.isSat()).isFalse();
  }

  @Test
  public void checkSimpleInequalityUnsat() {
    Term one = solver.mkInteger(1);
    Term assertion = solver.mkTerm(Kind.NOT, solver.mkTerm(Kind.EQUAL, one, one));
    solver.assertFormula(assertion);
    Result satCheck = solver.checkSat();
    assertThat(satCheck.isSat()).isFalse();
  }

  @Test
  public void checkSimpleInequalitySat() {
    Term zero = solver.mkInteger(0);
    Term one = solver.mkInteger(1);
    Term assertion = solver.mkTerm(Kind.NOT, solver.mkTerm(Kind.EQUAL, zero, one));
    solver.assertFormula(assertion);
    Result satCheck = solver.checkSat();
    assertThat(satCheck.isSat()).isTrue();
  }

  @Test
  public void checkSimpleLIAEqualitySat() {
    Term one = solver.mkInteger(1);
    Term two = solver.mkInteger(2);
    Term assertion = solver.mkTerm(Kind.EQUAL, solver.mkTerm(Kind.ADD, one, one), two);
    solver.assertFormula(assertion);
    Result satCheck = solver.checkSat();
    assertThat(satCheck.isSat()).isTrue();
  }

  @Test
  public void checkSimpleLIAEqualityUnsat() {
    Term one = solver.mkInteger(1);
    Term assertion = solver.mkTerm(Kind.EQUAL, solver.mkTerm(Kind.ADD, one, one), one);
    solver.assertFormula(assertion);
    Result satCheck = solver.checkSat();
    assertThat(satCheck.isSat()).isFalse();
  }

  @Test
  public void checkSimpleLIASat() {
    // x + y = 4 AND x * y = 4
    Term four = solver.mkInteger(4);
    Term varX = solver.mkConst(solver.getIntegerSort(), "x");
    Term varY = solver.mkConst(solver.getIntegerSort(), "y");
    Term assertion1 = solver.mkTerm(Kind.EQUAL, solver.mkTerm(Kind.MULT, varX, varY), four);
    Term assertion2 = solver.mkTerm(Kind.EQUAL, solver.mkTerm(Kind.ADD, varX, varY), four);
    solver.assertFormula(assertion1);
    solver.assertFormula(assertion2);
    Result satCheck = solver.checkSat();
    assertThat(satCheck.isSat()).isTrue();
    assertThat(getInt(varX) + getInt(varY)).isEqualTo(4);
    assertThat(getInt(varX) * getInt(varY)).isEqualTo(4);
  }

  /** Helper to get to int values faster. */
  private int getInt(Term cvc5Term) {
    String string = solver.getValue(cvc5Term).toString();
    return Integer.parseInt(string);
  }

  @Test
  public void checkSimpleLIAUnsat() {
    // x + y = 1 AND x * y = 1
    Term one = solver.mkInteger(1);
    Term varX = solver.mkConst(solver.getIntegerSort(), "x");
    Term varY = solver.mkConst(solver.getIntegerSort(), "y");
    Term assertion1 = solver.mkTerm(Kind.EQUAL, solver.mkTerm(Kind.MULT, varX, varY), one);
    Term assertion2 = solver.mkTerm(Kind.EQUAL, solver.mkTerm(Kind.ADD, varX, varY), one);
    solver.assertFormula(assertion1);
    solver.assertFormula(assertion2);
    Result satCheck = solver.checkSat();
    assertThat(satCheck.isSat()).isFalse();
  }

  @Test
  public void checkLIAModel() {
    // 1 + 2 = var
    // it follows that var = 3
    Term one = solver.mkInteger(1);
    Term two = solver.mkInteger(2);
    Term var = solver.mkConst(solver.getIntegerSort());
    Term assertion = solver.mkTerm(Kind.EQUAL, solver.mkTerm(Kind.ADD, one, two), var);
    solver.assertFormula(assertion);
    Result result = solver.checkSat();
    assertThat(result.isSat()).isTrue();
    Term assertionValue = solver.getValue(assertion);
    assertThat(assertionValue.toString()).isEqualTo("true");
    assertThat(solver.getValue(var).toString()).isEqualTo("3");
  }

  @Test
  public void checkSimpleLIRAUnsat2() {
    // x + y = 4 AND x * y = 4
    Term threeHalf = solver.mkReal(3, 2);
    Term varX = solver.mkConst(solver.getRealSort(), "x");
    Term varY = solver.mkConst(solver.getRealSort(), "y");
    Term assertion1 = solver.mkTerm(Kind.EQUAL, solver.mkTerm(Kind.MULT, varX, varY), threeHalf);
    Term assertion2 = solver.mkTerm(Kind.EQUAL, solver.mkTerm(Kind.ADD, varX, varY), threeHalf);
    solver.assertFormula(assertion1);
    solver.assertFormula(assertion2);
    Result satCheck = solver.checkSat();
    assertThat(satCheck.isSat()).isFalse();
  }

  @Test
  public void checkSimpleLIRASat() {
    // x + y = 8/5 AND x > 0 AND y > 0 AND x < 8/5 AND y < 8/5
    Term zero = solver.mkReal(0);
    Term eightFifth = solver.mkReal(8, 5);
    Term varX = solver.mkConst(solver.getRealSort(), "x");
    Term varY = solver.mkConst(solver.getRealSort(), "y");
    Term assertion1 = solver.mkTerm(Kind.GT, varX, zero);
    Term assertion2 = solver.mkTerm(Kind.GT, varY, zero);
    Term assertion3 = solver.mkTerm(Kind.LT, varX, eightFifth);
    Term assertion4 = solver.mkTerm(Kind.LT, varY, eightFifth);
    Term assertion5 = solver.mkTerm(Kind.EQUAL, solver.mkTerm(Kind.ADD, varX, varY), eightFifth);
    solver.assertFormula(assertion1);
    solver.assertFormula(assertion2);
    solver.assertFormula(assertion3);
    solver.assertFormula(assertion4);
    solver.assertFormula(assertion5);
    Result satCheck = solver.checkSat();
    assertThat(satCheck.isSat()).isTrue();
  }

  /** Real uses the same operators as int (plain plus, mult etc.). */
  @Test
  public void checkSimpleLRASat() {
    // x * y = 8/5 AND x < 4/5
    Term fourFifth = solver.mkReal(4, 5);
    Term eightFifth = solver.mkReal(8, 5);
    Term varX = solver.mkConst(solver.getRealSort(), "x");
    Term varY = solver.mkConst(solver.getRealSort(), "y");

    Term assertion1 = solver.mkTerm(Kind.EQUAL, solver.mkTerm(Kind.MULT, varX, varY), eightFifth);
    Term assertion2 = solver.mkTerm(Kind.LT, varX, fourFifth);

    solver.assertFormula(assertion1);
    solver.assertFormula(assertion2);
    Result satCheck = solver.checkSat();
    assertThat(satCheck.isSat()).isTrue();
  }

  /** Exponents may only be natural number constants. */
  @Test
  public void checkSimplePow() {
    // x ^ 2 = 4 AND x ^ 3 = 8
    Term two = solver.mkReal(2);
    Term three = solver.mkReal(3);
    Term four = solver.mkReal(4);
    Term eight = solver.mkReal(8);
    Term varX = solver.mkConst(solver.getRealSort(), "x");
    Term assertion1 = solver.mkTerm(Kind.EQUAL, solver.mkTerm(Kind.POW, varX, two), four);
    Term assertion2 = solver.mkTerm(Kind.EQUAL, solver.mkTerm(Kind.POW, varX, three), eight);
    solver.assertFormula(assertion1);
    solver.assertFormula(assertion2);
    Result satCheck = solver.checkSat();
    assertThat(satCheck.isSat()).isTrue();
  }

  // TODO: schreibe test von fp variable nach real bzw. umgekehrt. ALso fp formel -> real formel

  @Test
  public void checkSimpleFPSat() throws CVC5ApiException {
    // x * y = 1/4
    Term rmTerm = solver.mkRoundingMode(RoundingMode.ROUND_NEAREST_TIES_TO_AWAY);
    Op mkRealOp = solver.mkOp(Kind.FLOATINGPOINT_TO_FP_FROM_REAL, 8, 24);
    Term oneFourth = solver.mkTerm(mkRealOp, rmTerm, solver.mkReal(1, 4));

    Term varX = solver.mkConst(solver.mkFloatingPointSort(8, 24), "x");
    Term varY = solver.mkConst(solver.mkFloatingPointSort(8, 24), "y");
    Term assertion1 =
        solver.mkTerm(
            Kind.FLOATINGPOINT_EQ,
            solver.mkTerm(Kind.FLOATINGPOINT_MULT, rmTerm, varX, varY),
            oneFourth);

    solver.assertFormula(assertion1);
    Result satCheck = solver.checkSat();
    assertThat(satCheck.isSat()).isTrue();
  }

  @Test
  public void checkSimpleFPUnsat() throws CVC5ApiException {
    // x * y = 1/4 AND x > 0 AND y < 0
    Term rmTerm = solver.mkRoundingMode(RoundingMode.ROUND_NEAREST_TIES_TO_AWAY);
    Op mkRealOp = solver.mkOp(Kind.FLOATINGPOINT_TO_FP_FROM_REAL, 8, 24);
    Term oneFourth = solver.mkTerm(mkRealOp, rmTerm, solver.mkReal(1, 4));
    Term zero = solver.mkTerm(mkRealOp, rmTerm, solver.mkReal(0));

    Term varX = solver.mkConst(solver.mkFloatingPointSort(8, 24), "x");
    Term varY = solver.mkConst(solver.mkFloatingPointSort(8, 24), "y");
    Term assertion1 =
        solver.mkTerm(
            Kind.FLOATINGPOINT_EQ,
            solver.mkTerm(Kind.FLOATINGPOINT_MULT, rmTerm, varX, varY),
            oneFourth);
    Term assertion2 = solver.mkTerm(Kind.FLOATINGPOINT_GT, varX, zero);
    Term assertion3 = solver.mkTerm(Kind.FLOATINGPOINT_LT, varY, zero);
    solver.assertFormula(assertion1);
    solver.assertFormula(assertion2);
    solver.assertFormula(assertion3);
    Result satCheck = solver.checkSat();
    assertThat(satCheck.isSat()).isFalse();
  }

  @Test
  public void checkSimpleLRAUnsat() {
    // x + y = x * y AND x - 1 = 0
    Term zero = solver.mkInteger(0);
    Term one = solver.mkInteger(1);
    Term varX = solver.mkConst(solver.getRealSort(), "x");
    Term varY = solver.mkConst(solver.getRealSort(), "y");
    Term assertion1 =
        solver.mkTerm(
            Kind.EQUAL, solver.mkTerm(Kind.MULT, varX, varY), solver.mkTerm(Kind.ADD, varX, varY));
    Term assertion2 =
        solver.mkTerm(
            Kind.EQUAL,
            solver.mkTerm(Kind.SUB, varX, solver.mkTerm(Kind.TO_REAL, one)),
            solver.mkTerm(Kind.TO_REAL, zero));
    solver.assertFormula(assertion1);
    solver.assertFormula(assertion2);
    Result satCheck = solver.checkSat();
    assertThat(satCheck.isSat()).isFalse();
  }

  @Test
  public void checkSimpleLRAUnsat2() {
    // x + y = 3/2 AND x * y = 3/2
    Term threeHalf = solver.mkReal(3, 2);
    Term varX = solver.mkConst(solver.getRealSort(), "x");
    Term varY = solver.mkConst(solver.getRealSort(), "y");
    Term assertion1 = solver.mkTerm(Kind.EQUAL, solver.mkTerm(Kind.MULT, varX, varY), threeHalf);
    Term assertion2 = solver.mkTerm(Kind.EQUAL, solver.mkTerm(Kind.ADD, varX, varY), threeHalf);
    solver.assertFormula(assertion1);
    solver.assertFormula(assertion2);
    Result satCheck = solver.checkSat();
    assertThat(satCheck.isSat()).isFalse();
  }

  @Test
  public void checkSimpleIncrementalSolving() throws CVC5ApiException {
    // x + y = 3/2 AND x * y = 3/2 (AND x - 1 = 0)
    Term zero = solver.mkInteger(0);
    Term one = solver.mkInteger(1);
    Term threeHalf = solver.mkReal(3, 2);
    Term varX = solver.mkConst(solver.getRealSort(), "x");
    Term varY = solver.mkConst(solver.getRealSort(), "y");
    // this alone is SAT
    Term assertion1 =
        solver.mkTerm(
            Kind.EQUAL, solver.mkTerm(Kind.MULT, varX, varY), solver.mkTerm(Kind.ADD, varX, varY));
    // both 2 and 3 make it UNSAT (either one)
    Term assertion2 = solver.mkTerm(Kind.EQUAL, solver.mkTerm(Kind.ADD, varX, varY), threeHalf);
    Term assertion3 =
        solver.mkTerm(
            Kind.EQUAL,
            solver.mkTerm(Kind.SUB, varX, solver.mkTerm(Kind.TO_REAL, one)),
            solver.mkTerm(Kind.TO_REAL, zero));
    solver.push();
    solver.assertFormula(assertion1);
    Result satCheck = solver.checkSat();
    assertThat(satCheck.isSat()).isTrue();
    solver.push();
    solver.assertFormula(assertion2);
    satCheck = solver.checkSat();
    assertThat(satCheck.isSat()).isFalse();
    solver.pop();
    satCheck = solver.checkSat();
    assertThat(satCheck.isSat()).isTrue();
    solver.push();
    solver.assertFormula(assertion3);
    satCheck = solver.checkSat();
    assertThat(satCheck.isSat()).isFalse();
    solver.pop();
    satCheck = solver.checkSat();
    assertThat(satCheck.isSat()).isTrue();
  }

  /** Note that model and getValue are seperate! */
  @Test
  public void checkInvalidModelGetValue() {
    Term assertion = solver.mkBoolean(false);
    solver.assertFormula(assertion);
    Result result = solver.checkSat();
    assertThat(result.isSat()).isFalse();
    Exception e =
        assertThrows(
            io.github.cvc5.CVC5ApiRecoverableException.class, () -> solver.getValue(assertion));
    assertThat(e.toString()).contains(INVALID_GETVALUE_STRING_SAT);
  }

  /** The getModel() call needs an array of sorts and terms. */
  @Test
  public void checkGetModelUnsat() {
    Term assertion = solver.mkBoolean(false);
    solver.assertFormula(assertion);
    Sort[] sorts = new Sort[] {solver.getBooleanSort()};
    Term[] terms = new Term[] {assertion};
    Result result = solver.checkSat();
    assertThat(result.isSat()).isFalse();

    Exception e =
        assertThrows(
            io.github.cvc5.CVC5ApiRecoverableException.class, () -> solver.getModel(sorts, terms));
    assertThat(e.toString()).contains(INVALID_MODEL_STRING);
  }

  /**
   * The getModel() call needs an array of sorts and terms. This tests invalid sort parameters.
   * Sort: The list of uninterpreted sorts that should be printed in the model. Vars: The list of
   * free constants that should be printed in the model. A subset of these may be printed based on
   * isModelCoreSymbol.
   */
  @Test
  public void checkGetModelSatInvalidSort() {
    Term assertion = solver.mkBoolean(true);
    solver.assertFormula(assertion);
    Sort[] sorts = new Sort[] {solver.getBooleanSort()};
    Term[] terms = new Term[] {assertion};
    Result result = solver.checkSat();
    assertThat(result.isSat()).isTrue();
    Exception e =
        assertThrows(
            io.github.cvc5.CVC5ApiRecoverableException.class, () -> solver.getModel(sorts, terms));
    assertThat(e.toString()).contains("Expecting an uninterpreted sort as argument to getModel.");
  }

  /** Same as checkGetModelSatInvalidSort but with invalid term. */
  @Test
  public void checkGetModelSatInvalidTerm() {
    Term assertion = solver.mkBoolean(true);
    solver.assertFormula(assertion);
    Sort[] sorts = new Sort[] {};
    Term[] terms = new Term[] {assertion};
    Result result = solver.checkSat();
    assertThat(result.isSat()).isTrue();
    Exception e =
        assertThrows(
            io.github.cvc5.CVC5ApiRecoverableException.class, () -> solver.getModel(sorts, terms));
    assertThat(e.toString()).contains("Expecting a free constant as argument to getModel.");
  }

  @Test
  public void checkGetModelSat() {
    Term assertion = solver.mkConst(solver.getBooleanSort());
    solver.assertFormula(assertion);
    Sort[] sorts = new Sort[] {};
    Term[] terms = new Term[] {assertion};
    Result result = solver.checkSat();
    assertThat(result.isSat()).isTrue();
    String model = solver.getModel(sorts, terms);
    // The toString of vars (consts) is the internal variable id
    assertThat(model).contains("(\n" + "(define-fun " + assertion + " () Bool true)\n" + ")");
  }

  /**
   * The getModel() call needs an array of sorts and terms. This tests what happens if you put empty
   * arrays into it.
   */
  @Test
  public void checkInvalidGetModel() {
    Term assertion = solver.mkBoolean(false);
    solver.assertFormula(assertion);
    Result result = solver.checkSat();
    assertThat(result.isSat()).isFalse();
    Sort[] sorts = new Sort[1];
    Term[] terms = new Term[1];
    assertThrows(NullPointerException.class, () -> solver.getModel(sorts, terms));
  }

  /** It does not matter if you take an int or array or bv here, all result in the same error. */
  @Test
  public void checkInvalidTypeOperationsAssert() throws CVC5ApiException {
    Sort bvSort = solver.mkBitVectorSort(16);
    Term bvVar = solver.mkConst(bvSort, "bla");
    Term assertion = solver.mkTerm(Kind.BITVECTOR_AND, bvVar, bvVar);

    Exception e =
        assertThrows(io.github.cvc5.CVC5ApiException.class, () -> solver.assertFormula(assertion));
    assertThat(e.toString()).contains("Expected term with sort Bool");
  }

  /** It does not matter if you take an int or array or bv here, all result in the same error. */
  @Test
  public void checkInvalidTypeOperationsCheckSat() throws CVC5ApiException {
    Sort bvSort = solver.mkBitVectorSort(16);
    Term bvVar = solver.mkConst(bvSort);
    Term intVar = solver.mkConst(solver.getIntegerSort());
    Term arrayVar =
        solver.mkConst(solver.mkArraySort(solver.getIntegerSort(), solver.getIntegerSort()));

    Exception e =
        assertThrows(
            io.github.cvc5.CVC5ApiException.class, () -> solver.mkTerm(Kind.AND, bvVar, bvVar));
    assertThat(e.toString()).contains("expecting a Boolean subexpression");

    e =
        assertThrows(
            io.github.cvc5.CVC5ApiException.class, () -> solver.mkTerm(Kind.AND, intVar, intVar));
    assertThat(e.toString()).contains("expecting a Boolean subexpression");

    e =
        assertThrows(
            io.github.cvc5.CVC5ApiException.class,
            () -> solver.mkTerm(Kind.AND, arrayVar, arrayVar));
    assertThat(e.toString()).contains("expecting a Boolean subexpression");
  }

  @Test
  public void checkBvInvalidZeroWidthAssertion() {
    Exception e =
        assertThrows(io.github.cvc5.CVC5ApiException.class, () -> solver.mkBitVector(0, 1));
    assertThat(e.toString()).contains("Invalid argument '0' for 'size', expected a bit-width > 0");
  }

  @Test
  public void checkBvInvalidNegativeWidthCheckAssertion() {
    Exception e =
        assertThrows(io.github.cvc5.CVC5ApiException.class, () -> solver.mkBitVector(-1, 1));
    assertThat(e.toString()).contains("Expected size '-1' to be non negative.");
  }

  @Test
  public void checkSimpleBvEqualitySat() throws CVC5ApiException {
    // 1 + 0 = 1 with bitvectors
    Term bvOne = solver.mkBitVector(16, 1);
    Term bvZero = solver.mkBitVector(16, 0);
    Term assertion =
        solver.mkTerm(Kind.EQUAL, solver.mkTerm(Kind.BITVECTOR_ADD, bvZero, bvOne), bvOne);
    solver.assertFormula(assertion);
    Result satCheck = solver.checkSat();
    assertThat(satCheck.isSat()).isTrue();
  }

  @Test
  public void checkSimpleBvEqualityUnsat() throws CVC5ApiException {
    // 0 + 1 = 2 UNSAT with bitvectors
    Term bvZero = solver.mkBitVector(16, 0);
    Term bvOne = solver.mkBitVector(16, 1);
    Term bvTwo = solver.mkBitVector(16, 2);
    Term assertion =
        solver.mkTerm(Kind.EQUAL, solver.mkTerm(Kind.BITVECTOR_ADD, bvZero, bvOne), bvTwo);
    solver.assertFormula(assertion);
    Result satCheck = solver.checkSat();
    assertThat(satCheck.isSat()).isFalse();
  }

  @Test
  public void checkSimpleBvUnsat() throws CVC5ApiException {
    // var + 1 = 0 & var < max bitvector & var > 0; both < and > signed
    // Because of bitvector nature its UNSAT now

    Term bvVar = solver.mkConst(solver.mkBitVectorSort(16), "bvVar");
    Term bvOne = solver.mkBitVector(16, 1);
    Term bvZero = solver.mkBitVector(16, 0);
    Term assertion1 =
        solver.mkTerm(Kind.EQUAL, solver.mkTerm(Kind.BITVECTOR_ADD, bvVar, bvOne), bvZero);
    // mkMaxSigned(16);
    Term assertion2 = solver.mkTerm(Kind.BITVECTOR_SLT, bvVar, makeMaxCVC5Bitvector(16, true));
    Term assertion3 = solver.mkTerm(Kind.BITVECTOR_SGT, bvVar, bvZero);
    solver.assertFormula(assertion1);
    solver.assertFormula(assertion2);
    solver.assertFormula(assertion3);
    Result satCheck = solver.checkSat();
    assertThat(satCheck.isSat()).isFalse();
  }

  @Ignore
  public void checkBvDistinct() throws CVC5ApiException {
    Sort bvSort = solver.mkBitVectorSort(6);
    List<Term> bvs = new ArrayList<>();
    for (int i = 0; i < 64; i++) {
      bvs.add(solver.mkConst(bvSort, "a" + i + "_"));
    }

    // TODO: this got worse in the 1.0.0 release and now this runs endlessly as well, check in later
    // version again.
    Term distinct2 = solver.mkTerm(Kind.DISTINCT, bvs.toArray(new Term[0]));
    solver.assertFormula(distinct2);
    assertThat(solver.checkSat().isSat()).isTrue();
    solver.resetAssertions();

    // TODO: The following runs endlessly; recheck for new versions!
    /*
      bvs.add(solver.mkConst(bvSort, "b" + "_"));
      Term distinct3 = solver.mkTerm(Kind.DISTINCT, bvs.toArray(new Term[0]));
      solver.assertFormula(distinct3);
      assertThat(solver.checkSat().isSat()).isFalse();
    */
  }

  /*
   * CVC5 fails some easy quantifier tests.
   */
  @Test
  public void checkQuantifierExistsIncomplete() {
    // (not exists x . not b[x] = 0) AND (b[123] = 0) is SAT

    setupArrayQuant();
    Term zero = solver.mkInteger(0);

    Term xBound = solver.mkVar(solver.getIntegerSort(), "x");
    Term quantifiedVars = solver.mkTerm(Kind.VARIABLE_LIST, xBound);
    Term aAtxEq0s = aAtxEq0.substitute(x, xBound);
    Term exists = solver.mkTerm(Kind.EXISTS, quantifiedVars, solver.mkTerm(Kind.NOT, aAtxEq0s));
    Term notExists = solver.mkTerm(Kind.NOT, exists);

    Term select123 = solver.mkTerm(Kind.SELECT, array, solver.mkInteger(123));
    Term selectEq0 = solver.mkTerm(Kind.EQUAL, select123, zero);
    Term assertion = solver.mkTerm(Kind.AND, notExists, selectEq0);

    // CVC5 does not allow non quantifier formulas as the top most formula
    Exception e =
        assertThrows(io.github.cvc5.CVC5ApiException.class, () -> solver.assertFormula(assertion));
    assertThat(e.getMessage().strip()).matches(INVALID_TERM_BOUND_VAR);
  }

  @Test
  public void checkQuantifierEliminationLIA() {
    // build formula: (forall x . ((x < 5) | (7 < x + y)))
    // quantifier-free equivalent: (2 < y) or (>= y 3)
    setupArrayQuant();

    Term three = solver.mkInteger(3);
    Term five = solver.mkInteger(5);
    Term seven = solver.mkInteger(7);

    Term y = solver.mkConst(solver.getIntegerSort(), "y");

    Term first = solver.mkTerm(Kind.LT, x, five);
    Term second = solver.mkTerm(Kind.LT, seven, solver.mkTerm(Kind.ADD, x, y));
    Term body = solver.mkTerm(Kind.OR, first, second);

    Term xBound = solver.mkVar(solver.getIntegerSort(), "xBound");
    Term quantifiedVars = solver.mkTerm(Kind.VARIABLE_LIST, xBound);

    Term bodySubst = body.substitute(x, xBound);
    Term assertion = solver.mkTerm(Kind.FORALL, quantifiedVars, bodySubst);

    Term result = solver.getQuantifierElimination(assertion);

    Term resultCheck = solver.mkTerm(Kind.GEQ, y, three);
    assertThat(result.toString()).isEqualTo(resultCheck.toString());
  }

  @Test
  public void checkQuantifierAndModelWithUf() throws CVC5ApiException {
    Term var = solver.mkConst(solver.getIntegerSort(), "var");
    // start with a normal, free variable!
    Term boundVar = solver.mkConst(solver.getIntegerSort(), "boundVar");
    Term varIsOne = solver.mkTerm(Kind.EQUAL, var, solver.mkInteger(4));
    // try not to use 0 as this is the default value for CVC5 models
    Term boundVarIsTwo = solver.mkTerm(Kind.EQUAL, boundVar, solver.mkInteger(2));
    Term boundVarIsThree = solver.mkTerm(Kind.EQUAL, boundVar, solver.mkInteger(3));

    String func = "func";
    Sort intSort = solver.getIntegerSort();

    Sort ufSort = solver.mkFunctionSort(intSort, intSort);
    Term uf = solver.mkConst(ufSort, func);
    Term funcAtBoundVar = solver.mkTerm(Kind.APPLY_UF, uf, boundVar);

    Term body =
        solver.mkTerm(Kind.AND, boundVarIsTwo, solver.mkTerm(Kind.EQUAL, var, funcAtBoundVar));

    // This is the bound variable used for boundVar
    Term boundVarBound = solver.mkVar(solver.getIntegerSort(), "boundVar");
    Term quantifiedVars = solver.mkTerm(Kind.VARIABLE_LIST, boundVarBound);
    // Subst all boundVar variables with the bound version
    Term bodySubst = body.substitute(boundVar, boundVarBound);
    Term quantFormula = solver.mkTerm(Kind.EXISTS, quantifiedVars, bodySubst);

    // var = 4 & boundVar = 3 & exists boundVar . ( boundVar = 2 & f(boundVar) = var )
    Term overallFormula = solver.mkTerm(Kind.AND, varIsOne, boundVarIsThree, quantFormula);

    solver.assertFormula(overallFormula);

    Result satCheck = solver.checkSat();

    // SAT
    assertThat(satCheck.isSat()).isTrue();

    // check Model
    // var = 4 & boundVar = 3 & exists boundVar . ( boundVar = 2 & f(2) = 4 )
    // It seems like CVC5 can't return quantified variables,
    // therefore we can't get a value for the uf!
    assertThat(solver.getValue(var).toString()).isEqualTo("4");
    assertThat(solver.getValue(boundVar).toString()).isEqualTo("3");
    // funcAtBoundVar and body do not have boundVars in them!
    assertThat(solver.getValue(funcAtBoundVar).toString()).isEqualTo("4");
    assertThat(solver.getValue(body).toString()).isEqualTo("false");
    // The function is a applied uf
    assertThat(funcAtBoundVar.getKind()).isEqualTo(Kind.APPLY_UF);
    assertThat(funcAtBoundVar.getSort()).isEqualTo(solver.getIntegerSort());
    assertThat(funcAtBoundVar.hasSymbol()).isFalse();
    assertThat(solver.getValue(funcAtBoundVar).toString()).isEqualTo("4");
    // The function has 2 children, 1st is the function, 2nd is the argument
    assertThat(funcAtBoundVar.getNumChildren()).isEqualTo(2);
    assertThat(funcAtBoundVar.toString()).isEqualTo("(func boundVar)");
    assertThat(funcAtBoundVar.getChild(0).toString()).isEqualTo("func");
    assertThat(funcAtBoundVar.getChild(1).toString()).isEqualTo("boundVar");
    // Now the same function within the body with the bound var substituted
    // A quantifier has 2 children, the second is the body
    assertThat(quantFormula.getNumChildren()).isEqualTo(2);
    // The body is the AND formula from above, the right child is var = func
    // The right child of var = func is the func
    Term funcModel = quantFormula.getChild(1).getChild(1).getChild(1);
    // This too is a applied uf
    assertThat(funcModel.getKind()).isEqualTo(Kind.APPLY_UF);
    // This should have the same SMTLIB2 string as the declaration
    assertThat(funcModel.toString()).isEqualTo(funcAtBoundVar.toString());
    // But the argument should be a bound var
    // You can not get a value for the entire function Term as it contains a bound var! (see below)
    assertThat(funcModel.getNumChildren()).isEqualTo(2);
    assertThat(funcModel.getChild(0).hasSymbol()).isTrue();
    assertThat(funcModel.getChild(0).getSymbol()).isEqualTo("func");
    // For some reason the function in an UF is CONSTANT type after a SAT call but if you try to get
    // the value it changes and is no longer the same as before, but a
    // LAMBDA Kind with the argument (in some internal string representation + its type) and the
    // result. You can get the result as the second child (child 1)
    assertThat(funcModel.getChild(0).getKind()).isEqualTo(Kind.CONSTANT);
    // Without getValue the Kind and num of children is fine
    assertThat(funcModel.getChild(0).getNumChildren()).isEqualTo(0);
    // The Sort is the function sort (which is the lambda)
    assertThat(funcModel.getChild(0).getSort()).isEqualTo(funcAtBoundVar.getChild(0).getSort());
    assertThat(solver.getValue(funcModel.getChild(0)).getNumChildren()).isEqualTo(2);
    assertThat(solver.getValue(funcModel.getChild(0)).getKind()).isEqualTo(Kind.LAMBDA);
    assertThat(solver.getValue(funcModel.getChild(0)).toString())
        .isEqualTo("(lambda ((_arg_1 Int)) 4)");

    assertThat(solver.getValue(funcModel.getChild(0)).getChild(1).toString()).isEqualTo("4");
    // The function parameter is fine
    assertThat(funcModel.getChild(1).toString()).isEqualTo("boundVar");
    // Now it is a VARIABLE (bound variables in CVC5)
    assertThat(funcModel.getChild(1).getKind()).isEqualTo(Kind.VARIABLE);

    // CVC5 does not allow the usage of getValue() on bound vars!
    Exception e =
        assertThrows(io.github.cvc5.CVC5ApiException.class, () -> solver.getValue(boundVarBound));
    assertThat(e.getMessage().strip()).matches(INVALID_TERM_BOUND_VAR);
    e = assertThrows(io.github.cvc5.CVC5ApiException.class, () -> solver.getValue(bodySubst));
    assertThat(e.getMessage().strip()).matches(INVALID_TERM_BOUND_VAR);
  }

  /** CVC5 does not support Array quantifier elimination. This would run endlessly! */
  @Ignore
  @Test
  public void checkArrayQuantElim() {
    setupArrayQuant();
    Term body = solver.mkTerm(Kind.OR, aAtxEq0, aAtxEq1);

    Term xBound = solver.mkVar(solver.getIntegerSort(), "x_b");
    Term quantifiedVars = solver.mkTerm(Kind.VARIABLE_LIST, xBound);
    Term bodySubst = body.substitute(x, xBound);
    Term assertion = solver.mkTerm(Kind.FORALL, quantifiedVars, bodySubst);

    Term result = solver.getQuantifierElimination(assertion);
    String resultString =
        "(forall ((x_b Int)) (let ((_let_0 (select a x_b))) (or (= _let_0 0) (= _let_0 1))) )";
    assertThat(result.toString()).isEqualTo(resultString);
  }

  /** CVC5 does support Bv quantifier elim.! */
  @Test
  public void checkQuantifierEliminationBV() throws CVC5ApiException {
    // build formula: exists y : bv[2]. x * y = 1
    // quantifier-free equivalent: x = 1 | x = 3
    // or extract_0_0 x = 1

    // Note from CVC5: a witness expression; first parameter is a BOUND_VAR_LIST, second is the
    // witness body"

    int width = 2;

    Term xBv = solver.mkConst(solver.mkBitVectorSort(width), "x_bv");
    Term yBv = solver.mkConst(solver.mkBitVectorSort(width), "y_bv");
    Term mult = solver.mkTerm(Kind.BITVECTOR_MULT, xBv, yBv);
    Term body = solver.mkTerm(Kind.EQUAL, mult, solver.mkBitVector(2, 1));

    Term xBound = solver.mkVar(solver.mkBitVectorSort(width), "y_bv");
    Term quantifiedVars = solver.mkTerm(Kind.VARIABLE_LIST, xBound);
    Term bodySubst = body.substitute(yBv, xBound);
    Term assertion = solver.mkTerm(Kind.EXISTS, quantifiedVars, bodySubst);

    Term quantElim = solver.getQuantifierElimination(assertion);

    assertThat(quantElim.toString())
        .isEqualTo(
            "(= (bvmul x_bv (witness ((x0 (_ BitVec 2))) (or (= (bvmul x_bv x0) #b01) (not (="
                + " (concat #b0 ((_ extract 0 0) (bvor x_bv (bvneg x_bv)))) #b01))))) #b01)");

    // TODO: formely you could get a better result Term by using getValue(). But now getValue() only
    // works after SAT since 1.0.0 and then getValue() prints trivial statements like false.
  }

  @Test
  public void checkArraySat() {
    // ((x = 123) & (select(arr, 5) = 123)) => ((select(arr, 5) = x) & (x = 123))
    Term five = solver.mkInteger(5);
    Term oneTwoThree = solver.mkInteger(123);

    Term xInt = solver.mkConst(solver.getIntegerSort(), "x_int");

    Sort arraySort = solver.mkArraySort(solver.getIntegerSort(), solver.getIntegerSort());
    Term arr = solver.mkConst(arraySort, "arr");

    Term xEq123 = solver.mkTerm(Kind.EQUAL, xInt, oneTwoThree);
    Term selAat5Eq123 =
        solver.mkTerm(Kind.EQUAL, solver.mkTerm(Kind.SELECT, arr, five), oneTwoThree);
    Term selAat5EqX = solver.mkTerm(Kind.EQUAL, solver.mkTerm(Kind.SELECT, arr, five), xInt);

    Term leftAnd = solver.mkTerm(Kind.AND, xEq123, selAat5Eq123);
    Term rightAnd = solver.mkTerm(Kind.AND, xEq123, selAat5EqX);
    Term impl = solver.mkTerm(Kind.IMPLIES, leftAnd, rightAnd);

    solver.assertFormula(impl);
    Result satCheck = solver.checkSat();
    assertThat(satCheck.isSat()).isTrue();
  }

  @Test
  public void checkArrayUnsat() {
    // (x = 123) & (select(arr, 5) = 123) & (select(arr, 5) != x)
    Term five = solver.mkInteger(5);
    Term oneTwoThree = solver.mkInteger(123);

    Term xInt = solver.mkConst(solver.getIntegerSort(), "x_int");

    Sort arraySort = solver.mkArraySort(solver.getIntegerSort(), solver.getIntegerSort());
    Term arr = solver.mkConst(arraySort, "arr");

    Term xEq123 = solver.mkTerm(Kind.EQUAL, xInt, oneTwoThree);
    Term selAat5Eq123 =
        solver.mkTerm(Kind.EQUAL, solver.mkTerm(Kind.SELECT, arr, five), oneTwoThree);
    Term selAat5NotEqX =
        solver.mkTerm(
            Kind.NOT, solver.mkTerm(Kind.EQUAL, solver.mkTerm(Kind.SELECT, arr, five), xInt));

    Term assertion = solver.mkTerm(Kind.AND, xEq123, selAat5Eq123, selAat5NotEqX);

    solver.assertFormula(assertion);
    Result satCheck = solver.checkSat();
    assertThat(satCheck.isSat()).isFalse();
  }

  @Test
  public void checkUnsatCore() {
    solver.setOption("produce-unsat-cores", "true");
    solver.setOption("produce-proofs", "true");
    // (a & b) & (not(a OR b))
    // Enable UNSAT Core first!
    solver.setOption("produce-unsat-cores", "true");

    Sort boolSort = solver.getBooleanSort();
    Term a = solver.mkConst(boolSort, "a");
    Term b = solver.mkConst(boolSort, "b");

    Term aAndb = solver.mkTerm(Kind.AND, a, b);
    Term notaOrb = solver.mkTerm(Kind.NOT, solver.mkTerm(Kind.OR, a, b));

    solver.assertFormula(aAndb);
    solver.assertFormula(notaOrb);
    Result satCheck = solver.checkSat();
    assertThat(satCheck.isSat()).isFalse();

    Term[] unsatCore = solver.getUnsatCore();

    // UnsatCores are iterable
    for (Term e : unsatCore) {
      assertThat(e.toString()).isIn(Arrays.asList("(not (or a b))", "(and a b)"));
    }
  }

  @Test
  public void checkCustomTypesAndUFs() {
    // 0 <= f(x)
    // 0 <= f(y)
    // f(x) + f(y) <= 1
    // not p(0)
    // p(f(y))
    Term zero = solver.mkInteger(0);
    Term one = solver.mkInteger(1);

    Sort boolSort = solver.getBooleanSort();
    Sort intSort = solver.getIntegerSort();

    // You may use custom sorts just like bool or int
    Sort mySort = solver.mkParamSort("f");
    // Sort for UFs later
    Sort mySortToInt = solver.mkFunctionSort(mySort, intSort);
    Sort intToBool = solver.mkFunctionSort(intSort, boolSort);

    Term xTyped = solver.mkConst(mySort, "x");
    Term yTyped = solver.mkConst(mySort, "y");

    // declare UFs
    Term f = solver.mkConst(mySortToInt, "f");
    Term p = solver.mkConst(intToBool, "p");

    // Apply UFs
    Term fx = solver.mkTerm(Kind.APPLY_UF, f, xTyped);
    Term fy = solver.mkTerm(Kind.APPLY_UF, f, yTyped);
    Term sum = solver.mkTerm(Kind.ADD, fx, fy);
    Term p0 = solver.mkTerm(Kind.APPLY_UF, p, zero);
    Term pfy = solver.mkTerm(Kind.APPLY_UF, p, fy);

    // Make some assumptions
    Term assumptions1 =
        solver.mkTerm(
            Kind.AND,
            solver.mkTerm(Kind.LEQ, zero, fx),
            solver.mkTerm(Kind.LEQ, zero, fy),
            solver.mkTerm(Kind.LEQ, sum, one));

    Term assumptions2 = solver.mkTerm(Kind.AND, p0.notTerm(), pfy);

    solver.assertFormula(assumptions1);
    solver.assertFormula(assumptions2);
    Result satCheck = solver.checkSat();
    assertThat(satCheck.isSat()).isTrue();
  }

  @Test
  public void checkBooleanUFDeclaration() {
    Sort boolSort = solver.getBooleanSort();
    Sort intSort = solver.getIntegerSort();

    // arg is bool, return is int
    Sort ufSort = solver.mkFunctionSort(boolSort, intSort);
    Term uf = solver.mkConst(ufSort, "fun_bi");
    Term ufTrue = solver.mkTerm(Kind.APPLY_UF, uf, solver.mkTrue());
    Term ufFalse = solver.mkTerm(Kind.APPLY_UF, uf, solver.mkFalse());

    Term assumptions = solver.mkTerm(Kind.NOT, solver.mkTerm(Kind.EQUAL, ufTrue, ufFalse));

    solver.assertFormula(assumptions);
    Result satCheck = solver.checkSat();
    assertThat(satCheck.isSat()).isTrue();
  }

  @Test
  public void checkLIAUfsUnsat() {
    // 0 <= f(x)
    // 0 <= f(y)
    // f(x) + f(y) = x
    // f(x) + f(y) = y
    // f(x) = x + 1
    // f(y) = y - 1
    Term zero = solver.mkInteger(0);
    Term one = solver.mkInteger(1);

    Sort intSort = solver.getIntegerSort();

    // Sort for UFs later
    Sort intToInt = solver.mkFunctionSort(intSort, intSort);

    Term xInt = solver.mkConst(intSort, "x");
    Term yInt = solver.mkConst(intSort, "y");

    // declare UFs
    Term f = solver.mkConst(intToInt, "f");

    // Apply UFs
    Term fx = solver.mkTerm(Kind.APPLY_UF, f, xInt);
    Term fy = solver.mkTerm(Kind.APPLY_UF, f, yInt);
    Term plus = solver.mkTerm(Kind.ADD, fx, fy);

    // Make some assumptions
    Term assumptions1 =
        solver.mkTerm(
            Kind.AND,
            solver.mkTerm(Kind.LEQ, zero, fx),
            solver.mkTerm(Kind.EQUAL, plus, xInt),
            solver.mkTerm(Kind.LEQ, zero, fy));

    Term assumptions2 =
        solver.mkTerm(
            Kind.AND,
            solver.mkTerm(Kind.EQUAL, fx, solver.mkTerm(Kind.ADD, xInt, one)),
            solver.mkTerm(Kind.EQUAL, fy, solver.mkTerm(Kind.SUB, yInt, one)),
            solver.mkTerm(Kind.EQUAL, plus, yInt));

    solver.assertFormula(assumptions1);
    solver.assertFormula(assumptions2);
    Result satCheck = solver.checkSat();
    assertThat(satCheck.isSat()).isFalse();
  }

  @Test
  public void checkLIAUfsSat() {
    // f(x) = x + 1
    // f(y) = y - 1
    // x = y -> f(x) + f(y) = x AND f(x) + f(y) = y
    Term one = solver.mkInteger(1);

    Sort intSort = solver.getIntegerSort();

    // Sort for UFs later
    Sort intToInt = solver.mkFunctionSort(intSort, intSort);

    Term xInt = solver.mkConst(intSort, "x");
    Term yInt = solver.mkConst(intSort, "y");

    // declare UFs
    Term f = solver.mkConst(intToInt, "f");

    // Apply UFs
    Term fx = solver.mkTerm(Kind.APPLY_UF, f, xInt);
    Term fy = solver.mkTerm(Kind.APPLY_UF, f, yInt);
    Term plus = solver.mkTerm(Kind.ADD, fx, fy);

    Term plusEqx = solver.mkTerm(Kind.EQUAL, plus, xInt);
    Term plusEqy = solver.mkTerm(Kind.EQUAL, plus, yInt);
    Term xEqy = solver.mkTerm(Kind.EQUAL, yInt, xInt);
    Term xEqyImplplusEqxAndy =
        solver.mkTerm(Kind.IMPLIES, xEqy, solver.mkTerm(Kind.AND, plusEqx, plusEqy));

    Term assumptions =
        solver.mkTerm(
            Kind.AND,
            solver.mkTerm(Kind.EQUAL, fx, solver.mkTerm(Kind.ADD, xInt, one)),
            solver.mkTerm(Kind.EQUAL, fy, solver.mkTerm(Kind.SUB, yInt, one)),
            xEqyImplplusEqxAndy);

    solver.assertFormula(assumptions);
    Result satCheck = solver.checkSat();
    assertThat(satCheck.isSat()).isTrue();
    assertThat(solver.getValue(fx).toString()).isEqualTo("0");
  }

  @Test
  public void checkStringCompare() {
    Term var1 = solver.mkConst(solver.getStringSort(), "0");
    Term var2 = solver.mkConst(solver.getStringSort(), "1");

    Term f =
        solver
            .mkTerm(Kind.STRING_LEQ, var1, var2)
            .andTerm(solver.mkTerm(Kind.STRING_LEQ, var2, var1));

    // Thats no problem
    solver.assertFormula(f);
    assertThat(solver.checkSat().isSat()).isTrue();

    // implying that 1 <= 2 & 2 <= 1 -> 1 = 2 however runs indefinitely
    /*
    Term implication = f.notTerm().orTerm(solver.mkTerm(Kind.EQUAL, var2, var1));
    solver.assertFormula(implication.notTerm());
    assertThat(solver.checkSat().isUnsat()).isTrue();
    */
  }

  /** Sets up array and quantifier based formulas for tests. */
  private void setupArrayQuant() {
    Term zero = solver.mkInteger(0);
    Term one = solver.mkInteger(1);

    x = solver.mkVar(solver.getIntegerSort(), "x");

    Sort arraySort = solver.mkArraySort(solver.getIntegerSort(), solver.getIntegerSort());
    array = solver.mkVar(arraySort, "a");

    aAtxEq0 = solver.mkTerm(Kind.EQUAL, solver.mkTerm(Kind.SELECT, array, x), zero);
    aAtxEq1 = solver.mkTerm(Kind.EQUAL, solver.mkTerm(Kind.SELECT, array, x), one);
  }

  /**
   * For some reason CVC5 does not provide API to create max (or min) size signed/unsigned
   * bitvectors.
   *
   * @param width of the bitvector term.
   * @param signed true if signed. false for unsigned.
   * @return Max size bitvector term.
   */
  private Term makeMaxCVC5Bitvector(int width, boolean signed) throws CVC5ApiException {
    String bitvecString;
    if (signed) {
      bitvecString = String.valueOf(new char[width - 1]).replace("\0", "1");
      bitvecString = "0" + bitvecString;
    } else {
      bitvecString = String.valueOf(new char[width]).replace("\0", "1");
    }
    return solver.mkBitVector(width, bitvecString, 2);
  }

  @Test
  public void termAccessAfterModelClosed() throws CVC5ApiException {
    Solver secondSolver = createEnvironment();
    Term v = solver.mkConst(solver.getIntegerSort(), "v");
    Term one = solver.mkInteger(1);
    Term eq = solver.mkTerm(Kind.EQUAL, v, one); // v==1

    secondSolver.assertFormula(eq);
    Result result = secondSolver.checkSat();
    assertThat(result.isSat()).isTrue();

    Term valueV = secondSolver.getValue(v);
    Preconditions.checkNotNull(valueV);

    System.out.println(valueV);
  }

  @Test
  public void checkCVC5InterpolationMethod() throws CVC5ApiException {
    solver.setOption("produce-interpolants", "true");

    Term x = solver.mkConst(solver.getIntegerSort(), "x");
    Term y = solver.mkConst(solver.getIntegerSort(), "y");
    Term z = solver.mkConst(solver.getIntegerSort(), "z");

    Term f1 = solver.mkTerm(Kind.EQUAL, y, solver.mkTerm(Kind.MULT, solver.mkInteger(2), x));
    Term f2 =
        solver.mkTerm(
            Kind.EQUAL,
            y,
            solver.mkTerm(
                Kind.ADD,
                solver.mkInteger(1),
                solver.mkTerm(Kind.MULT, z, solver.mkInteger(2))));
    System.out.println(f1.toString());
    System.out.println(f2.toString());
    // Term interpol = solver.mkTerm(Kind.AND, f1, solver.mkTerm(Kind.NOT, f2));
    // Term interpol = solver.mkTerm(Kind.AND, f1, f2);
    // System.out.println(interpol.toString());
    solver.assertFormula(f1);
    Term interpolation = solver.getInterpolant(f2);
    Result result = solver.checkSat();
    System.out.println(result.toString());
    // Term interpolation = solver.getInterpolant(interpol);
    System.out.println(interpolation.toString());
    // solver.assertFormula(interpol);
    // Result result = solver.checkSat();
    // System.out.println(result.toString());

  }

  @Test
  public void checkCVC5InterpolationMethod02() throws CVC5ApiException {
    solver.setOption("produce-interpolants", "true");
    solver.setOption("interpolants-mode", "default");
    solver.setOption("check-interpolants", "true");
    Term x = solver.mkConst(solver.getIntegerSort(), "x");
    Term y = solver.mkConst(solver.getIntegerSort(), "y");

    Term ip0 = solver.mkTerm(Kind.GT, x, y);
    Term ip1 = solver.mkTerm(Kind.EQUAL, x, solver.mkInteger(0));
    Term ip2 = solver.mkTerm(Kind.GT, y, solver.mkInteger(0));

    Term smtInterpolInterpolation = solver.mkTerm(Kind.LEQ, y, solver.mkInteger(-1));

    System.out.println("ip0 " + ip0.toString());
    System.out.println("ip1 " + ip1.toString());
    System.out.println("ip2 " + ip2.toString());

    solver.assertFormula(ip0);
    solver.assertFormula(ip1);

    solver.checkSat();
    Term interpolation = solver.getInterpolant(solver.mkTerm(Kind.NOT, ip2));
    // (<= y (+ x (- 1))), (<= y (- 1)) interpolation by SMTInterpol
    System.out.println("CVC5 Interpolation1 " + interpolation.toString());
    System.out.println("CVC5 Interpolation1 " + smtInterpolInterpolation.toString());
    System.out.println(solver.checkSat().toString());
    Term[] assertions = solver.getAssertions();
    for (int i = 0; i < assertions.length; i++) {
      System.out.println(assertions[i]);
    }
    solver.resetAssertions();
    System.out.println("len of Assert " + solver.getAssertions().length);
    solver.assertFormula(
        solver
            .mkTerm(Kind.NOT, solver.mkTerm(Kind.EQUAL, interpolation, smtInterpolInterpolation)));
    Result result = solver.checkSat();

    System.out.println(result.toString());
    assertThat(result.isUnsat()).isTrue();

    solver.resetAssertions();
    // <= y (+ x (- 1)))
    smtInterpolInterpolation =
        solver.mkTerm(Kind.LEQ, y, solver.mkTerm(Kind.ADD, x, solver.mkInteger(-1)));
    solver.assertFormula(ip0);
    // solver.assertFormula(ip1);
    // solver.assertFormula(ip2);
    solver.checkSat();
    interpolation =
        solver.getInterpolant(solver.mkTerm(Kind.NOT, solver.mkTerm(Kind.AND, ip1, ip2)));
    // interpolation =
    // solver.getInterpolant(solver.mkTerm(Kind.AND, ip1, ip2));
    // interpolation = solver.getInterpolant(solver.mkTerm(Kind.NOT, ip0));
    System.out.println("CVC5 Interpolation 2 " + interpolation.toString());
    System.out.println("SMTInterpol Interpolation 2 " + smtInterpolInterpolation.toString());
    solver.resetAssertions();
    solver.assertFormula(
        solver
            .mkTerm(Kind.NOT, solver.mkTerm(Kind.EQUAL, interpolation, smtInterpolInterpolation)));
    result = solver.checkSat();
    assertThat(result.isUnsat()).isTrue();
  }

  @Test
  public void checkCVC5InterpolationMethod03() throws CVC5ApiException {
    solver.setOption("produce-interpolants", "true");
    Term x = solver.mkConst(solver.getIntegerSort(), "x");
    Term y = solver.mkConst(solver.getIntegerSort(), "y");

    Term ip0 = solver.mkTerm(Kind.GT, x, y);
    Term ip1 = solver.mkTerm(Kind.EQUAL, x, solver.mkInteger(0));
    Term ip2 = solver.mkTerm(Kind.GT, y, solver.mkInteger(0));

    Term a = ip0;
    Term b = solver.mkTerm(Kind.AND, ip1, ip2);

    // <= y (+ x (- 1))) generated by SmtInterpol
    Term smtInterpolInterpolation =
        solver.mkTerm(Kind.LEQ, y, solver.mkTerm(Kind.ADD, x, solver.mkInteger(-1)));
    solver.assertFormula(a);
    solver.checkSat();
    Term interpolation =
        solver.getInterpolant(solver.mkTerm(Kind.NOT, b));
    System.out.println("CVC5 Interpolation " + interpolation.toString());
    System.out.println("SMTInterpol Interpolation " + smtInterpolInterpolation.toString());
    solver.resetAssertions();
    Term craig1 = solver.mkTerm(Kind.IMPLIES, a, interpolation);
    Term craig2 =
        solver
            .mkTerm(Kind.EQUAL, solver.mkTerm(Kind.AND, interpolation, b), solver.mkBoolean(false));
    solver.assertFormula(craig1);
    solver.assertFormula(craig2);

    Result result = solver.checkSat();

    assertThat(result.isSat()).isTrue();

    solver.resetAssertions();
    solver.assertFormula(solver.mkTerm(Kind.NOT, solver.mkTerm(Kind.AND, craig1, craig2)));

    result = solver.checkSat();
    assertThat(result.isUnsat()).isTrue();
  }

  @Test
  public void checkCVC5InterpolationMethod04() throws CVC5ApiException {
    solver.setOption("produce-interpolants", "true");
    Term x = solver.mkConst(solver.getIntegerSort(), "x");
    Term y = solver.mkConst(solver.getIntegerSort(), "y");

    Term ip0 = solver.mkTerm(Kind.GT, x, y);
    Term ip1 = solver.mkTerm(Kind.EQUAL, x, solver.mkInteger(0));
    Term ip2 = solver.mkTerm(Kind.GT, y, solver.mkInteger(0));

    Term a = ip0;
    Term b = solver.mkTerm(Kind.AND, ip1, ip2);

    Term interpolation = interpolateAndCheck(solver, a, b);
    System.out.println(interpolation.toString());
  }

  private Term interpolateAndCheck(Solver solver, Term interpolantA, Term interpolantB) {
    // solver.setOption("produce-interpolants", "true");
    solver.assertFormula(interpolantA);
    Term interpolation = solver.getInterpolant(solver.mkTerm(Kind.NOT, interpolantB));

    solver.resetAssertions();
    Term cvc51 = solver.mkTerm(Kind.IMPLIES, interpolantA, interpolation);
    Term cvc52 = solver.mkTerm(Kind.IMPLIES, interpolation, solver.mkTerm(Kind.NOT, interpolantB));

    solver.assertFormula(cvc51);
    solver.assertFormula(cvc52);
    if (solver.checkSat().isUnsat()) {
      System.out.println("Does not satisfy CVC5 Interpolation Definition");
      return null;
    }

    solver.resetAssertions();
    solver.assertFormula(solver.mkTerm(Kind.NOT, solver.mkTerm(Kind.AND, cvc51, cvc52)));
    if (solver.checkSat().isSat()) {
      System.out.println("Does not satisfy generally CVC5 Interpolation Definition");
      return null;
    }

    solver.resetAssertions();
    Term craig1 = solver.mkTerm(Kind.IMPLIES, interpolantA, interpolation);
    Term craig2 =
        solver.mkTerm(
            Kind.EQUAL,
            solver.mkTerm(Kind.AND, interpolation, interpolantB),
            solver.mkBoolean(false));
    solver.assertFormula(craig1);
    solver.assertFormula(craig2);
    if (solver.checkSat().isUnsat()) {
      System.out.println("Does not satisfy Craig Interpolation Definition");
      return null;
    }
    solver.resetAssertions();
    solver.assertFormula(solver.mkTerm(Kind.NOT, solver.mkTerm(Kind.AND, craig1, craig2)));
    if (solver.checkSat().isSat()) {
      System.out.println("Does not satisfy generally Craig Interpolation Definition");
      return null;
    }

    return interpolation;
  }

  @Test
  public void testSimpleInterpolation() throws CVC5ApiException {
    // Out of Interpolation tests
    // IntegerFormula x = imgr.makeVariable("x");
    // IntegerFormula y = imgr.makeVariable("y");
    // IntegerFormula z = imgr.makeVariable("z");
    // BooleanFormula f1 = imgr.equal(y, imgr.multiply(imgr.makeNumber(2), x));
    // BooleanFormula f2 =
    // imgr.equal(y, imgr.add(imgr.makeNumber(1), imgr.multiply(z, imgr.makeNumber(2))));
    // prover.push(f1);
    // T id2 = prover.push(f2);
    // boolean check = prover.isUnsat();
    // assertWithMessage("formulas must be contradicting").that(check).isTrue();
    // prover.getInterpolant(ImmutableList.of(id2));
    solver.setOption("produce-interpolants", "true");
    Term x = solver.mkConst(solver.getIntegerSort(), "x");
    Term y = solver.mkConst(solver.getIntegerSort(), "y");
    Term z = solver.mkConst(solver.getIntegerSort(), "z");
    Term f1 = solver.mkTerm(Kind.EQUAL, y, solver.mkTerm(Kind.MULT, solver.mkInteger(2), x));
    Term f2 =
        solver.mkTerm(
            Kind.EQUAL,
            y,
            solver.mkTerm(
                Kind.ADD,
                solver.mkInteger(1),
                solver.mkTerm(Kind.MULT, z, solver.mkInteger(2))));
    // solver.assertFormula(f1);
    // solver.assertFormula(f2);
    Term i1 = interpolateAndCheck(solver, f1, f2);
    System.out.println(i1.toString());
  }

}
