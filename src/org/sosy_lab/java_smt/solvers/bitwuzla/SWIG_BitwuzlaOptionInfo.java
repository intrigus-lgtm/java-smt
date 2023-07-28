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

package org.sosy_lab.java_smt.solvers.bitwuzla;

/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 4.0.2
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */


public class SWIG_BitwuzlaOptionInfo {
  private transient long swigCPtr;
  protected transient boolean swigCMemOwn;

  protected SWIG_BitwuzlaOptionInfo(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(SWIG_BitwuzlaOptionInfo obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  @SuppressWarnings("deprecation")
  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        BitwuzlaJNI.delete_BitwuzlaOptionInfo(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public void setOpt(SWIG_BitwuzlaOption value) {
    BitwuzlaJNI.BitwuzlaOptionInfo_opt_set(swigCPtr, this, value.swigValue());
  }

  public SWIG_BitwuzlaOption getOpt() {
    return SWIG_BitwuzlaOption.swigToEnum(BitwuzlaJNI.BitwuzlaOptionInfo_opt_get(swigCPtr, this));
  }

  public void setShrt(String value) {
    BitwuzlaJNI.BitwuzlaOptionInfo_shrt_set(swigCPtr, this, value);
  }

  public String getShrt() {
    return BitwuzlaJNI.BitwuzlaOptionInfo_shrt_get(swigCPtr, this);
  }

  public void setLng(String value) {
    BitwuzlaJNI.BitwuzlaOptionInfo_lng_set(swigCPtr, this, value);
  }

  public String getLng() {
    return BitwuzlaJNI.BitwuzlaOptionInfo_lng_get(swigCPtr, this);
  }

  public void setDesc(String value) {
    BitwuzlaJNI.BitwuzlaOptionInfo_desc_set(swigCPtr, this, value);
  }

  public String getDesc() {
    return BitwuzlaJNI.BitwuzlaOptionInfo_desc_get(swigCPtr, this);
  }

  public void setIs_numeric(boolean value) {
    BitwuzlaJNI.BitwuzlaOptionInfo_is_numeric_set(swigCPtr, this, value);
  }

  public boolean getIs_numeric() {
    return BitwuzlaJNI.BitwuzlaOptionInfo_is_numeric_get(swigCPtr, this);
  }

  public SWIG_BitwuzlaOptionInfo() {
    this(BitwuzlaJNI.new_BitwuzlaOptionInfo(), true);
  }

}

