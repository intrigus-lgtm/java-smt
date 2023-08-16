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

public class ExpressionDoubleMap extends java.util.AbstractMap<Expression, Double> {
  private transient long swigCPtr;
  protected transient boolean swigCMemOwn;

  protected ExpressionDoubleMap(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(ExpressionDoubleMap obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected static long swigRelease(ExpressionDoubleMap obj) {
    long ptr = 0;
    if (obj != null) {
      if (!obj.swigCMemOwn)
        throw new RuntimeException("Cannot release ownership as memory is not owned");
      ptr = obj.swigCPtr;
      obj.swigCMemOwn = false;
      obj.delete();
    }
    return ptr;
  }

  @SuppressWarnings("deprecation")
  protected void finalize1() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        drealJNI.delete_ExpressionDoubleMap(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  @Override
  public int size() {
    return sizeImpl();
  }

  @Override
  public boolean containsKey(java.lang.Object key) {
    if (!(key instanceof Expression)) {
      return false;
    }

    return containsImpl((Expression) key);
  }

  @Override
  public Double get(java.lang.Object key) {
    if (!(key instanceof Expression)) {
      return null;
    }

    Iterator itr = find((Expression) key);
    if (itr.isNot(end())) {
      return itr.getValue();
    }

    return null;
  }

  @Override
  public Double put(Expression key, Double value) {
    Iterator itr = find(key);
    if (itr.isNot(end())) {
      Double oldValue = itr.getValue();
      itr.setValue(value);
      return oldValue;
    } else {
      putUnchecked(key, value);
      return null;
    }
  }

  @Override
  public Double remove(java.lang.Object key) {
    if (!(key instanceof Expression)) {
      return null;
    }

    Iterator itr = find((Expression) key);
    if (itr.isNot(end())) {
      Double oldValue = itr.getValue();
      removeUnchecked(itr);
      return oldValue;
    } else {
      return null;
    }
  }

  @Override
  public java.util.Set<Entry<Expression, Double>> entrySet() {
    java.util.Set<Entry<Expression, Double>> setToReturn =
        new java.util.HashSet<Entry<Expression, Double>>();

    Iterator itr = begin();
    final Iterator end = end();
    while (itr.isNot(end)) {
      setToReturn.add(
          new Entry<Expression, Double>() {
            private Iterator iterator;

            private Entry<Expression, Double> init(Iterator iterator) {
              this.iterator = iterator;
              return this;
            }

            @Override
            public Expression getKey() {
              return iterator.getKey();
            }

            @Override
            public Double getValue() {
              return iterator.getValue();
            }

            @Override
            public Double setValue(Double newValue) {
              Double oldValue = iterator.getValue();
              iterator.setValue(newValue);
              return oldValue;
            }
          }.init(itr));
      itr = itr.getNextUnchecked();
    }

    return setToReturn;
  }

  public ExpressionDoubleMap() {
    this(drealJNI.new_ExpressionDoubleMap__SWIG_0(), true);
  }

  public ExpressionDoubleMap(ExpressionDoubleMap other) {
    this(drealJNI.new_ExpressionDoubleMap__SWIG_1(ExpressionDoubleMap.getCPtr(other), other), true);
  }

  protected static class Iterator {
    private transient long swigCPtr;
    protected transient boolean swigCMemOwn;

    protected Iterator(long cPtr, boolean cMemoryOwn) {
      swigCMemOwn = cMemoryOwn;
      swigCPtr = cPtr;
    }

    protected static long getCPtr(Iterator obj) {
      return (obj == null) ? 0 : obj.swigCPtr;
    }

    protected static long swigRelease(Iterator obj) {
      long ptr = 0;
      if (obj != null) {
        if (!obj.swigCMemOwn)
          throw new RuntimeException("Cannot release ownership as memory is not owned");
        ptr = obj.swigCPtr;
        obj.swigCMemOwn = false;
        obj.delete();
      }
      return ptr;
    }

    @SuppressWarnings("deprecation")
    protected void finalize1() {
      delete();
    }

    public synchronized void delete() {
      if (swigCPtr != 0) {
        if (swigCMemOwn) {
          swigCMemOwn = false;
          drealJNI.delete_ExpressionDoubleMap_Iterator(swigCPtr);
        }
        swigCPtr = 0;
      }
    }

    private ExpressionDoubleMap.Iterator getNextUnchecked() {
      return new ExpressionDoubleMap.Iterator(
          drealJNI.ExpressionDoubleMap_Iterator_getNextUnchecked(swigCPtr, this), true);
    }

    private boolean isNot(ExpressionDoubleMap.Iterator other) {
      return drealJNI.ExpressionDoubleMap_Iterator_isNot(
          swigCPtr, this, ExpressionDoubleMap.Iterator.getCPtr(other), other);
    }

    private Expression getKey() {
      return new Expression(drealJNI.ExpressionDoubleMap_Iterator_getKey(swigCPtr, this), true);
    }

    private double getValue() {
      return drealJNI.ExpressionDoubleMap_Iterator_getValue(swigCPtr, this);
    }

    private void setValue(double newValue) {
      drealJNI.ExpressionDoubleMap_Iterator_setValue(swigCPtr, this, newValue);
    }
  }

  @Override
  public boolean isEmpty() {
    return drealJNI.ExpressionDoubleMap_isEmpty(swigCPtr, this);
  }

  @Override
  public void clear() {
    drealJNI.ExpressionDoubleMap_clear(swigCPtr, this);
  }

  private ExpressionDoubleMap.Iterator find(Expression key) {
    return new ExpressionDoubleMap.Iterator(
        drealJNI.ExpressionDoubleMap_find(swigCPtr, this, Expression.getCPtr(key), key), true);
  }

  private ExpressionDoubleMap.Iterator begin() {
    return new ExpressionDoubleMap.Iterator(
        drealJNI.ExpressionDoubleMap_begin(swigCPtr, this), true);
  }

  private ExpressionDoubleMap.Iterator end() {
    return new ExpressionDoubleMap.Iterator(drealJNI.ExpressionDoubleMap_end(swigCPtr, this), true);
  }

  private int sizeImpl() {
    return drealJNI.ExpressionDoubleMap_sizeImpl(swigCPtr, this);
  }

  private boolean containsImpl(Expression key) {
    return drealJNI.ExpressionDoubleMap_containsImpl(swigCPtr, this, Expression.getCPtr(key), key);
  }

  private void putUnchecked(Expression key, double value) {
    drealJNI.ExpressionDoubleMap_putUnchecked(swigCPtr, this, Expression.getCPtr(key), key, value);
  }

  private void removeUnchecked(ExpressionDoubleMap.Iterator itr) {
    drealJNI.ExpressionDoubleMap_removeUnchecked(
        swigCPtr, this, ExpressionDoubleMap.Iterator.getCPtr(itr), itr);
  }
}
