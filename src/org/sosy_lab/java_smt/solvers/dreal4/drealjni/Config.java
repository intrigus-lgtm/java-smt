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

public class Config {
  private transient long swigCPtr;
  protected transient boolean swigCMemOwn;

  protected Config(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(Config obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected static long swigRelease(Config obj) {
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
        drealJNI.delete_Config(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public Config() {
    this(drealJNI.new_Config__SWIG_0(), true);
  }

  public Config(Config arg0) {
    this(drealJNI.new_Config__SWIG_1(Config.getCPtr(arg0), arg0), true);
  }

  public Config AssignOperator(Config arg0) {
    return new Config(
        drealJNI.Config_AssignOperator__SWIG_0(swigCPtr, this, Config.getCPtr(arg0), arg0), false);
  }

  public double precision() {
    return drealJNI.Config_precision(swigCPtr, this);
  }

  public OptionValueDouble mutable_precision() {
    return new OptionValueDouble(drealJNI.Config_mutable_precision(swigCPtr, this), false);
  }

  public void mutable_precision(double value) {
    drealJNI.Config_mutable_precision_0(value, Config.getCPtr(this));
  }

  public boolean produce_models() {
    return drealJNI.Config_produce_models(swigCPtr, this);
  }

  public OptionValueBool mutable_produce_models() {
    return new OptionValueBool(drealJNI.Config_mutable_produce_models(swigCPtr, this), false);
  }

  public void mutable_produce_models(boolean bool) {
    drealJNI.Config_mutable_produce_models_0(bool, Config.getCPtr(this));
  }

  public boolean use_polytope() {
    return drealJNI.Config_use_polytope(swigCPtr, this);
  }

  public OptionValueBool mutable_use_polytope() {
    return new OptionValueBool(drealJNI.Config_mutable_use_polytope(swigCPtr, this), false);
  }

  public void mutable_use_polytope(boolean bool) {
    drealJNI.Config_mutable_use_polytope_0(bool, Config.getCPtr(this));
  }

  public boolean use_polytope_in_forall() {
    return drealJNI.Config_use_polytope_in_forall(swigCPtr, this);
  }

  public OptionValueBool mutable_use_polytope_in_forall() {
    return new OptionValueBool(
        drealJNI.Config_mutable_use_polytope_in_forall(swigCPtr, this), false);
  }

  public void mutable_use_polytope_in_forall(boolean bool) {
    drealJNI.Config_mutable_use_polytope_in_forall_0(bool, Config.getCPtr(this));
  }

  public boolean use_worklist_fixpoint() {
    return drealJNI.Config_use_worklist_fixpoint(swigCPtr, this);
  }

  public OptionValueBool mutable_use_worklist_fixpoint() {
    return new OptionValueBool(
        drealJNI.Config_mutable_use_worklist_fixpoint(swigCPtr, this), false);
  }

  public void mutable_use_worklist_fixpoint(boolean bool) {
    drealJNI.Config_mutable_use_worklist_fixpoint_0(bool, Config.getCPtr(this));
  }

  public boolean use_local_optimization() {
    return drealJNI.Config_use_local_optimization(swigCPtr, this);
  }

  public OptionValueBool mutable_use_local_optimization() {
    return new OptionValueBool(
        drealJNI.Config_mutable_use_local_optimization(swigCPtr, this), false);
  }

  public void mutable_use_local_optimization(boolean bool) {
    drealJNI.Config_mutable_use_local_optimization_0(bool, Config.getCPtr(this));
  }

  public boolean dump_theory_literals() {
    return drealJNI.Config_dump_theory_literals(swigCPtr, this);
  }

  public OptionValueBool mutable_dump_theory_literals() {
    return new OptionValueBool(drealJNI.Config_mutable_dump_theory_literals(swigCPtr, this), false);
  }

  public void mutable_dump_theory_literals(boolean bool) {
    drealJNI.Config_mutable_dump_theory_literals_0(bool, Config.getCPtr(this));
  }

  public int number_of_jobs() {
    return drealJNI.Config_number_of_jobs(swigCPtr, this);
  }

  public OptionValueInt mutable_number_of_jobs() {
    return new OptionValueInt(drealJNI.Config_mutable_number_of_jobs(swigCPtr, this), false);
  }

  public void mutable_number_of_jobs(int i) {
    drealJNI.Config_mutable_number_of_jobs_0(i, Config.getCPtr(this));
  }

  public boolean stack_left_box_first() {
    return drealJNI.Config_stack_left_box_first(swigCPtr, this);
  }

  public OptionValueBool mutable_stack_left_box_first() {
    return new OptionValueBool(drealJNI.Config_mutable_stack_left_box_first(swigCPtr, this), false);
  }

  public void mutable_stack_left_box_first(boolean bool) {
    drealJNI.Config_mutable_stack_left_box_first_0(bool, Config.getCPtr(this));
  }

  public
  SWIGTYPE_p_std__functionT_int_fdreal__Box_const_R_DynamicBitset_const_R_dreal__Box_p_dreal__Box_pF_t
      brancher() {
    return new SWIGTYPE_p_std__functionT_int_fdreal__Box_const_R_DynamicBitset_const_R_dreal__Box_p_dreal__Box_pF_t(
        drealJNI.Config_brancher(swigCPtr, this), false);
  }

  public
  SWIGTYPE_p_dreal__OptionValueT_std__functionT_int_fdreal__Box_const_R_DynamicBitset_const_R_dreal__Box_p_dreal__Box_pF_t_t
      mutable_brancher() {
    return new SWIGTYPE_p_dreal__OptionValueT_std__functionT_int_fdreal__Box_const_R_DynamicBitset_const_R_dreal__Box_p_dreal__Box_pF_t_t(
        drealJNI.Config_mutable_brancher(swigCPtr, this), false);
  }

  public double nlopt_ftol_rel() {
    return drealJNI.Config_nlopt_ftol_rel(swigCPtr, this);
  }

  public OptionValueDouble mutable_nlopt_ftol_rel() {
    return new OptionValueDouble(drealJNI.Config_mutable_nlopt_ftol_rel(swigCPtr, this), false);
  }

  public void mutable_nlopt_ftol_rel(double value) {
    drealJNI.Config_mutable_nlopt_ftol_rel_0(value, Config.getCPtr(this));
  }

  public double nlopt_ftol_abs() {
    return drealJNI.Config_nlopt_ftol_abs(swigCPtr, this);
  }

  public OptionValueDouble mutable_nlopt_ftol_abs() {
    return new OptionValueDouble(drealJNI.Config_mutable_nlopt_ftol_abs(swigCPtr, this), false);
  }

  public void mutable_nlopt_ftol_abs(double value) {
    drealJNI.Config_mutable_nlopt_ftol_abs_0(value, Config.getCPtr(this));
  }

  public int nlopt_maxeval() {
    return drealJNI.Config_nlopt_maxeval(swigCPtr, this);
  }

  public OptionValueInt mutable_nlopt_maxeval() {
    return new OptionValueInt(drealJNI.Config_mutable_nlopt_maxeval(swigCPtr, this), false);
  }

  public void mutable_nlopt_maxeval(int i) {
    drealJNI.Config_mutable_nlopt_maxeval_0(i, Config.getCPtr(this));
  }

  public double nlopt_maxtime() {
    return drealJNI.Config_nlopt_maxtime(swigCPtr, this);
  }

  public OptionValueDouble mutable_nlopt_maxtime() {
    return new OptionValueDouble(drealJNI.Config_mutable_nlopt_maxtime(swigCPtr, this), false);
  }

  public void mutable_nlopt_maxtime(double value) {
    drealJNI.Config_mutable_nlopt_maxtime_0(value, Config.getCPtr(this));
  }

  public Config.SatDefaultPhase sat_default_phase() {
    return Config.SatDefaultPhase.swigToEnum(drealJNI.Config_sat_default_phase(swigCPtr, this));
  }

  public SWIGTYPE_p_dreal__OptionValueT_dreal__Config__SatDefaultPhase_t
      mutable_sat_default_phase() {
    return new SWIGTYPE_p_dreal__OptionValueT_dreal__Config__SatDefaultPhase_t(
        drealJNI.Config_mutable_sat_default_phase(swigCPtr, this), false);
  }

  public long random_seed() {
    return drealJNI.Config_random_seed(swigCPtr, this);
  }

  public OptionValueUnsignedInt mutable_random_seed() {
    return new OptionValueUnsignedInt(drealJNI.Config_mutable_random_seed(swigCPtr, this), false);
  }

  public void mutable_random_seed(long seed) {
    if (seed < 0) {
      throw new IllegalArgumentException("Seed must be greater than zero");
    }
    drealJNI.Config_mutable_random_seed_0(seed, Config.getCPtr(this));
  }

  public boolean smtlib2_compliant() {
    return drealJNI.Config_smtlib2_compliant(swigCPtr, this);
  }

  public OptionValueBool mutable_smtlib2_compliant() {
    return new OptionValueBool(drealJNI.Config_mutable_smtlib2_compliant(swigCPtr, this), false);
  }

  public void mutable_smtlib2_compliant(boolean bool) {
    drealJNI.Config_mutable_smtlib2_compliant_0(bool, Config.getCPtr(this));
  }

  public static double getKDefaultPrecision() {
    return drealJNI.Config_kDefaultPrecision_get();
  }

  public static double getKDefaultNloptFtolRel() {
    return drealJNI.Config_kDefaultNloptFtolRel_get();
  }

  public static double getKDefaultNloptFtolAbs() {
    return drealJNI.Config_kDefaultNloptFtolAbs_get();
  }

  public static int getKDefaultNloptMaxEval() {
    return drealJNI.Config_kDefaultNloptMaxEval_get();
  }

  public static double getKDefaultNloptMaxTime() {
    return drealJNI.Config_kDefaultNloptMaxTime_get();
  }

  public static final class SatDefaultPhase {
    public static final Config.SatDefaultPhase False =
        new Config.SatDefaultPhase("False", drealJNI.Config_SatDefaultPhase_False_get());
    public static final Config.SatDefaultPhase True =
        new Config.SatDefaultPhase("True", drealJNI.Config_SatDefaultPhase_True_get());
    public static final Config.SatDefaultPhase JeroslowWang =
        new Config.SatDefaultPhase(
            "JeroslowWang", drealJNI.Config_SatDefaultPhase_JeroslowWang_get());
    public static final Config.SatDefaultPhase RandomInitialPhase =
        new Config.SatDefaultPhase(
            "RandomInitialPhase", drealJNI.Config_SatDefaultPhase_RandomInitialPhase_get());

    public final int swigValue() {
      return swigValue;
    }

    @Override
    public String toString() {
      return swigName;
    }

    public static SatDefaultPhase swigToEnum(int swigValue) {
      if (swigValue < swigValues.length
          && swigValue >= 0
          && swigValues[swigValue].swigValue == swigValue) return swigValues[swigValue];
      for (int i = 0; i < swigValues.length; i++)
        if (swigValues[i].swigValue == swigValue) return swigValues[i];
      throw new IllegalArgumentException(
          "No enum " + SatDefaultPhase.class + " with value " + swigValue);
    }

    @SuppressWarnings("unused")
    private SatDefaultPhase(String swigName) {
      this.swigName = swigName;
      this.swigValue = swigNext++;
    }

    @SuppressWarnings("StaticAssignmentInConstructor")
    private SatDefaultPhase(String swigName, int swigValue) {
      this.swigName = swigName;
      this.swigValue = swigValue;
      swigNext = swigValue + 1;
    }

    @SuppressWarnings({"unused", "StaticAssignmentInConstructor"})
    private SatDefaultPhase(String swigName, SatDefaultPhase swigEnum) {
      this.swigName = swigName;
      this.swigValue = swigEnum.swigValue;
      swigNext = this.swigValue + 1;
    }

    private static SatDefaultPhase[] swigValues = {False, True, JeroslowWang, RandomInitialPhase};

    @SuppressWarnings("unused")
    private static int swigNext = 0;

    private final int swigValue;
    private final String swigName;
  }
}
