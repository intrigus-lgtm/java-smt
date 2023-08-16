/*
 *  JavaSMT is an API wrapper for a collection of SMT solvers.
 *  This file is part of JavaSMT.
 *
 *  Copyright (C) 2007-2016  Dirk Beyer
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

/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (https://www.swig.org).
 * Version 4.1.1
 *
 * Do not make changes to this file unless you know what you are doing - modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */
package org.sosy_lab.java_smt.solvers.dreal4.drealjni;

public final class FormulaKind {
  public static final FormulaKind False = new FormulaKind("False");
  public static final FormulaKind True = new FormulaKind("True");
  public static final FormulaKind Var = new FormulaKind("Var");
  public static final FormulaKind Eq = new FormulaKind("Eq");
  public static final FormulaKind Neq = new FormulaKind("Neq");
  public static final FormulaKind Gt = new FormulaKind("Gt");
  public static final FormulaKind Geq = new FormulaKind("Geq");
  public static final FormulaKind Lt = new FormulaKind("Lt");
  public static final FormulaKind Leq = new FormulaKind("Leq");
  public static final FormulaKind And = new FormulaKind("And");
  public static final FormulaKind Or = new FormulaKind("Or");
  public static final FormulaKind Not = new FormulaKind("Not");
  public static final FormulaKind Forall = new FormulaKind("Forall");

  public final int swigValue() {
    return swigValue;
  }

  @Override
  public String toString() {
    return swigName;
  }

  public static FormulaKind swigToEnum(int swigValue) {
    if (swigValue < swigValues.length
        && swigValue >= 0
        && swigValues[swigValue].swigValue == swigValue) return swigValues[swigValue];
    for (int i = 0; i < swigValues.length; i++)
      if (swigValues[i].swigValue == swigValue) return swigValues[i];
    throw new IllegalArgumentException("No enum " + FormulaKind.class + " with value " + swigValue);
  }

  private FormulaKind(String swigName) {
    this.swigName = swigName;
    this.swigValue = swigNext++;
  }

  @SuppressWarnings({"unused", "StaticAssignmentInConstructor"})
  private FormulaKind(String swigName, int swigValue) {
    this.swigName = swigName;
    this.swigValue = swigValue;
    swigNext = swigValue + 1;
  }

  @SuppressWarnings({"unused", "StaticAssignmentInConstructor"})
  private FormulaKind(String swigName, FormulaKind swigEnum) {
    this.swigName = swigName;
    this.swigValue = swigEnum.swigValue;
    swigNext = this.swigValue + 1;
  }

  private static FormulaKind[] swigValues = {
    False, True, Var, Eq, Neq, Gt, Geq, Lt, Leq, And, Or, Not, Forall
  };
  private static int swigNext = 0;
  private final int swigValue;
  private final String swigName;
}
