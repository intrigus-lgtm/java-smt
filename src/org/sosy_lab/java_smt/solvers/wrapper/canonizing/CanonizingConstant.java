/*
 *  JavaSMT is an API wrapper for a collection of SMT solvers.
 *  This file is part of JavaSMT.
 *
 *  Copyright (C) 2007-2018  Dirk Beyer
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
package org.sosy_lab.java_smt.solvers.wrapper.canonizing;

import java.math.BigInteger;
import org.sosy_lab.java_smt.api.BooleanFormula;
import org.sosy_lab.java_smt.api.Formula;
import org.sosy_lab.java_smt.api.FormulaManager;
import org.sosy_lab.java_smt.api.FormulaType;
import org.sosy_lab.java_smt.solvers.wrapper.strategy.CanonizingStrategy;

public class CanonizingConstant implements CanonizingFormula {

  private static final long serialVersionUID = 1L;
  private transient FormulaManager mgr;
  private Object value;
  private FormulaType<?> type;

  private Integer hashCode = null;
  private transient Formula translated = null;

  private transient CanonizingFormula canonized = null;

  public static final CanonizingConstant getInstance(
      FormulaManager pMgr, Object pValue, FormulaType<?> pType) {
    if (pType.isBooleanType()) {
      return new CanonizingBooleanConstant(pMgr, pValue, pType);
    } else {
      return new CanonizingConstant(pMgr, pValue, pType);
    }
  }

  private CanonizingConstant(FormulaManager pMgr, Object pValue, FormulaType<?> pType) {
    mgr = pMgr;
    value = pValue;
    type = pType;
  }

  public Object getValue() {
    return value;
  }

  @Override
  public FormulaType<?> getType() {
    return type;
  }

  @Override
  public CanonizingFormula copy() {
    CanonizingFormula copy = new CanonizingConstant(mgr, value, type);

    return copy;
  }

  @Override
  public Formula toFormula(FormulaManager pMgr) {
    if (translated != null) {
      return translated;
    }

    if (type.isIntegerType()) {
      translated = pMgr.getIntegerFormulaManager().makeNumber(value.toString());
    } else if (type.isBitvectorType()) {
      translated =
          pMgr.getBitvectorFormulaManager()
              .makeBitvector(
                  ((FormulaType.BitvectorType) type).getSize(), new BigInteger(value.toString()));
    } else if (type.isFloatingPointType()) {
      if (value != null) {
        translated =
            pMgr.getFloatingPointFormulaManager()
                .makeNumber(value.toString(), (FormulaType.FloatingPointType) type);
      } else {
        translated =
            pMgr.getFloatingPointFormulaManager().makeNaN((FormulaType.FloatingPointType) type);
      }
    } else if (type.isBooleanType()) {
      translated =
          pMgr.getBooleanFormulaManager().makeBoolean(Boolean.getBoolean(value.toString()));
    }

    return translated;
  }

  @Override
  public CanonizingFormula canonize(CanonizingStrategy pStrategy, CanonizingFormulaStore pCaller) {
    if (canonized == null) {
      canonized = copy();
    }
    return canonized;
  }

  @Override
  public FormulaManager getFormulaManager() {
    return mgr;
  }

  @Override
  public void toString(StringBuilder pBuilder) {
    pBuilder.append(value);
  }

  @Override
  public String toString() {
    return value.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof CanonizingConstant)) {
      return false;
    }
    CanonizingConstant other = (CanonizingConstant) o;
    return type.equals(other.type) && value.equals(other.value);
  }

  @Override
  public int hashCode() {
    int prime = 37;
    int result = 1;
    if (hashCode == null) {
      result = prime * result + type.hashCode();
      result = prime * result + value.hashCode();
      hashCode = result;
    }
    return hashCode;
  }

  static final class CanonizingBooleanConstant extends CanonizingConstant
      implements BooleanFormula {

    private static final long serialVersionUID = 1L;

    private CanonizingBooleanConstant(FormulaManager pMgr, Object pValue, FormulaType<?> pType) {
      super(pMgr, pValue, pType);
    }
  }
}
