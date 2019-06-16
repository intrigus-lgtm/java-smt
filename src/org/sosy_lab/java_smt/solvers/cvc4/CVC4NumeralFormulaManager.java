/*
 *  JavaSMT is an API wrapper for a collection of SMT solvers.
 *  This file is part of JavaSMT.
 *
 *  Copyright (C) 2007-2019  Dirk Beyer
 *  All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.sosy_lab.java_smt.solvers.cvc4;

import static edu.nyu.acsys.CVC4.Kind.DIVISION;
import static edu.nyu.acsys.CVC4.Kind.INTS_DIVISION;
import static edu.nyu.acsys.CVC4.Kind.INTS_MODULUS;
import static edu.nyu.acsys.CVC4.Kind.ITE;
import static edu.nyu.acsys.CVC4.Kind.MINUS;
import static edu.nyu.acsys.CVC4.Kind.MULT;
import static edu.nyu.acsys.CVC4.Kind.PLUS;

import com.google.common.collect.ImmutableSet;
import edu.nyu.acsys.CVC4.Expr;
import edu.nyu.acsys.CVC4.ExprManager;
import edu.nyu.acsys.CVC4.Kind;
import edu.nyu.acsys.CVC4.Rational;
import edu.nyu.acsys.CVC4.Type;
import edu.nyu.acsys.CVC4.vectorExpr;
import java.math.BigInteger;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.sosy_lab.java_smt.api.NumeralFormula;
import org.sosy_lab.java_smt.basicimpl.AbstractNumeralFormulaManager;

public abstract class CVC4NumeralFormulaManager<
        ParamFormulaType extends NumeralFormula, ResultFormulaType extends NumeralFormula>
    extends AbstractNumeralFormulaManager<
        Expr, Type, ExprManager, ParamFormulaType, ResultFormulaType, Expr> {

  /** Operators for arithmetic functions that return a numeric value. */
  private static final ImmutableSet<Kind> NUMERIC_FUNCTIONS =
      ImmutableSet.of(PLUS, MINUS, MULT, DIVISION, INTS_DIVISION, INTS_MODULUS);

  protected final ExprManager exprManager;

  CVC4NumeralFormulaManager(CVC4FormulaCreator pCreator, NonLinearArithmetic pNonLinearArithmetic) {
    super(pCreator, pNonLinearArithmetic);
    exprManager = pCreator.getEnv();
  }

  protected abstract Type getNumeralType();

  @Override
  public boolean isNumeral(Expr pVal) {
    return (pVal.getType().isInteger() || pVal.getType().isReal()) && pVal.isConst();
  }

  /**
   * Check whether the current term is numeric and the value of a term is determined by only
   * numerals, i.e. no variable is contained. This method should check as precisely as possible the
   * situations in which CVC4 supports arithmetic operations like multiplications.
   *
   * <p>Example: TRUE for "1", "2+3", "ite(x,2,3) and FALSE for "x", "x+2", "ite(1=2,x,0)"
   */
  boolean consistsOfNumerals(Expr val) {
    Set<Expr> finished = new HashSet<>();
    Deque<Expr> waitlist = new ArrayDeque<>();
    waitlist.add(val);
    while (!waitlist.isEmpty()) {
      Expr e = waitlist.pop();
      if (!finished.add(e)) {
        continue;
      }
      if (isNumeral(e)) {
        // true, skip and check others
      } else if (NUMERIC_FUNCTIONS.contains(e.getKind())) {
        for (Expr param : e) {
          waitlist.add(param);
        }
      } else if (ITE.equals(e.getKind())) {
        // ignore condition, just use the if- and then-case
        waitlist.add(e.getChild(1));
        waitlist.add(e.getChild(2));
      } else {
        return false;
      }
    }
    return true;
  }

  @Override
  protected Expr makeNumberImpl(long i) {
    return exprManager.mkConst(new Rational(i));
  }

  @Override
  protected Expr makeNumberImpl(BigInteger pI) {
    return makeNumberImpl(pI.toString());
  }

  @Override
  protected Expr makeNumberImpl(String pI) {
    return exprManager.mkConst(new Rational(pI));
  }

  @Override
  protected Expr makeVariableImpl(String varName) {
    Type type = getNumeralType();
    return getFormulaCreator().makeVariable(type, varName);
  }

  @Override
  public Expr divide(Expr pParam1, Expr pParam2) {
    return exprManager.mkExpr(Kind.INTS_DIVISION, pParam1, pParam2);
  }

  @Override
  public Expr multiply(Expr pParam1, Expr pParam2) {
    if (consistsOfNumerals(pParam1) || consistsOfNumerals(pParam2)) {
      return exprManager.mkExpr(Kind.MULT, pParam1, pParam2);
    } else {
      return super.multiply(pParam1, pParam2);
    }
  }

  @Override
  public Expr modulo(Expr pParam1, Expr pParam2) {
    return exprManager.mkExpr(Kind.INTS_MODULUS, pParam1, pParam2);
  }

  @Override
  public Expr negate(Expr pParam1) {
    return exprManager.mkExpr(Kind.UMINUS, pParam1);
  }

  @Override
  public Expr add(Expr pParam1, Expr pParam2) {
    return exprManager.mkExpr(Kind.PLUS, pParam1, pParam2);
  }

  @Override
  public Expr subtract(Expr pParam1, Expr pParam2) {
    return exprManager.mkExpr(Kind.MINUS, pParam1, pParam2);
  }

  @Override
  public Expr equal(Expr pParam1, Expr pParam2) {
    return exprManager.mkExpr(Kind.EQUAL, pParam1, pParam2);
  }

  @Override
  public Expr greaterThan(Expr pParam1, Expr pParam2) {
    return exprManager.mkExpr(Kind.GT, pParam1, pParam2);
  }

  @Override
  public Expr greaterOrEquals(Expr pParam1, Expr pParam2) {
    return exprManager.mkExpr(Kind.GEQ, pParam1, pParam2);
  }

  @Override
  public Expr lessThan(Expr pParam1, Expr pParam2) {
    return exprManager.mkExpr(Kind.LT, pParam1, pParam2);
  }

  @Override
  public Expr lessOrEquals(Expr pParam1, Expr pParam2) {
    return exprManager.mkExpr(Kind.LEQ, pParam1, pParam2);
  }

  @Override
  protected Expr distinctImpl(List<Expr> pParam) {
    vectorExpr param = new vectorExpr();
    pParam.forEach(param::add);
    return exprManager.mkExpr(Kind.DISTINCT, param);
  }
}
