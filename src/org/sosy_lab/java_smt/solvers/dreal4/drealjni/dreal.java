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

public class dreal {
  public static SWIGTYPE_p_std__ostream InsertOperator(SWIGTYPE_p_std__ostream os, Variable var) {
    return new SWIGTYPE_p_std__ostream(
        drealJNI.InsertOperator__SWIG_0(
            SWIGTYPE_p_std__ostream.getCPtr(os), Variable.getCPtr(var), var),
        false);
  }

  public static SWIGTYPE_p_std__ostream InsertOperator(
      SWIGTYPE_p_std__ostream os, Variable.Type type) {
    return new SWIGTYPE_p_std__ostream(
        drealJNI.InsertOperator__SWIG_1(SWIGTYPE_p_std__ostream.getCPtr(os), type.swigValue()),
        false);
  }

  public static boolean Equal(Variables vars1, Variables vars2) {
    return drealJNI.Equal__SWIG_0(Variables.getCPtr(vars1), vars1, Variables.getCPtr(vars2), vars2);
  }

  public static boolean Less(Variables vars1, Variables vars2) {
    return drealJNI.Less__SWIG_0(Variables.getCPtr(vars1), vars1, Variables.getCPtr(vars2), vars2);
  }

  public static SWIGTYPE_p_std__ostream InsertOperator(
      SWIGTYPE_p_std__ostream arg0, Variables vars) {
    return new SWIGTYPE_p_std__ostream(
        drealJNI.InsertOperator__SWIG_2(
            SWIGTYPE_p_std__ostream.getCPtr(arg0), Variables.getCPtr(vars), vars),
        false);
  }

  public static Variables intersect(Variables vars1, Variables vars2) {
    return new Variables(
        drealJNI.intersect(Variables.getCPtr(vars1), vars1, Variables.getCPtr(vars2), vars2), true);
  }

  public static Variables AddEqual(Variables vars1, Variables vars2) {
    return new Variables(
        drealJNI.AddEqual__SWIG_0(Variables.getCPtr(vars1), vars1, Variables.getCPtr(vars2), vars2),
        true);
  }

  public static Variables AddEqual(Variables vars, Variable var) {
    return new Variables(
        drealJNI.AddEqual__SWIG_1(Variables.getCPtr(vars), vars, Variable.getCPtr(var), var), true);
  }

  public static Variables Add(Variables vars1, Variables vars2) {
    return new Variables(
        drealJNI.Add__SWIG_0(Variables.getCPtr(vars1), vars1, Variables.getCPtr(vars2), vars2),
        true);
  }

  public static Variables Add(Variables vars, Variable var) {
    return new Variables(
        drealJNI.Add__SWIG_1(Variables.getCPtr(vars), vars, Variable.getCPtr(var), var), true);
  }

  public static Variables Add(Variable var, Variables vars) {
    return new Variables(
        drealJNI.Add__SWIG_2(Variable.getCPtr(var), var, Variables.getCPtr(vars), vars), true);
  }

  public static Variables SubstractEqual(Variables vars1, Variables vars2) {
    return new Variables(
        drealJNI.SubstractEqual__SWIG_0(
            Variables.getCPtr(vars1), vars1, Variables.getCPtr(vars2), vars2),
        true);
  }

  public static Variables SubstractEqual(Variables vars, Variable var) {
    return new Variables(
        drealJNI.SubstractEqual__SWIG_1(Variables.getCPtr(vars), vars, Variable.getCPtr(var), var),
        true);
  }

  public static Variables Substract(Variables vars1, Variables vars2) {
    return new Variables(
        drealJNI.Substract__SWIG_0(
            Variables.getCPtr(vars1), vars1, Variables.getCPtr(vars2), vars2),
        true);
  }

  public static Variables Substract(Variables vars, Variable var) {
    return new Variables(
        drealJNI.Substract__SWIG_1(Variables.getCPtr(vars), vars, Variable.getCPtr(var), var),
        true);
  }

  public static SWIGTYPE_p_std__ostream InsertOperator(
      SWIGTYPE_p_std__ostream os, Environment env) {
    return new SWIGTYPE_p_std__ostream(
        drealJNI.InsertOperator__SWIG_3(
            SWIGTYPE_p_std__ostream.getCPtr(os), Environment.getCPtr(env), env),
        false);
  }

  public static boolean Less(ExpressionKind k1, ExpressionKind k2) {
    return drealJNI.Less__SWIG_1(k1.swigValue(), k2.swigValue());
  }

  public static Expression Add(Expression lhs, Expression rhs) {
    return new Expression(
        drealJNI.Add__SWIG_3(Expression.getCPtr(lhs), lhs, Expression.getCPtr(rhs), rhs), true);
  }

  public static Expression AddEqual(Expression lhs, Expression rhs) {
    return new Expression(
        drealJNI.AddEqual__SWIG_2(Expression.getCPtr(lhs), lhs, Expression.getCPtr(rhs), rhs),
        false);
  }

  public static Expression Substract(Expression lhs, Expression rhs) {
    return new Expression(
        drealJNI.Substract__SWIG_2(Expression.getCPtr(lhs), lhs, Expression.getCPtr(rhs), rhs),
        true);
  }

  public static Expression SubstractEqual(Expression lhs, Expression rhs) {
    return new Expression(
        drealJNI.SubstractEqual__SWIG_2(Expression.getCPtr(lhs), lhs, Expression.getCPtr(rhs), rhs),
        false);
  }

  public static Expression Add(Expression e) {
    return new Expression(drealJNI.Add__SWIG_7(Expression.getCPtr(e), e), true);
  }

  public static Expression Substract(Expression e) {
    return new Expression(drealJNI.Substract__SWIG_6(Expression.getCPtr(e), e), true);
  }

  public static Expression Multiply(Expression lhs, Expression rhs) {
    return new Expression(
        drealJNI.Multiply__SWIG_0(Expression.getCPtr(lhs), lhs, Expression.getCPtr(rhs), rhs),
        true);
  }

  public static Expression MultiplyEqual(Expression lhs, Expression rhs) {
    return new Expression(
        drealJNI.MultiplyEqual(Expression.getCPtr(lhs), lhs, Expression.getCPtr(rhs), rhs), false);
  }

  public static Expression Divide(Expression lhs, Expression rhs) {

    return new Expression(
        drealJNI.Divide(Expression.getCPtr(lhs), lhs, Expression.getCPtr(rhs), rhs), true);
  }

  public static Expression DivideEqual(Expression lhs, Expression rhs) {
    return new Expression(
        drealJNI.DivideEqual(Expression.getCPtr(lhs), lhs, Expression.getCPtr(rhs), rhs), false);
  }

  public static Expression real_constant(double lb, double ub, boolean use_lb_as_representative) {
    return new Expression(drealJNI.real_constant(lb, ub, use_lb_as_representative), true);
  }

  public static Expression log(Expression e) {
    return new Expression(drealJNI.log(Expression.getCPtr(e), e), true);
  }

  public static Expression abs(Expression e) {
    return new Expression(drealJNI.abs(Expression.getCPtr(e), e), true);
  }

  public static Expression exp(Expression e) {
    return new Expression(drealJNI.exp(Expression.getCPtr(e), e), true);
  }

  public static Expression sqrt(Expression e) {
    return new Expression(drealJNI.sqrt(Expression.getCPtr(e), e), true);
  }

  public static Expression pow(Expression e1, Expression e2) {
    return new Expression(
        drealJNI.pow(Expression.getCPtr(e1), e1, Expression.getCPtr(e2), e2), true);
  }

  public static Expression sin(Expression e) {
    return new Expression(drealJNI.sin(Expression.getCPtr(e), e), true);
  }

  public static Expression cos(Expression e) {
    return new Expression(drealJNI.cos(Expression.getCPtr(e), e), true);
  }

  public static Expression tan(Expression e) {
    return new Expression(drealJNI.tan(Expression.getCPtr(e), e), true);
  }

  public static Expression asin(Expression e) {
    return new Expression(drealJNI.asin(Expression.getCPtr(e), e), true);
  }

  public static Expression acos(Expression e) {
    return new Expression(drealJNI.acos(Expression.getCPtr(e), e), true);
  }

  public static Expression atan(Expression e) {
    return new Expression(drealJNI.atan(Expression.getCPtr(e), e), true);
  }

  public static Expression atan2(Expression e1, Expression e2) {
    return new Expression(
        drealJNI.atan2(Expression.getCPtr(e1), e1, Expression.getCPtr(e2), e2), true);
  }

  public static Expression sinh(Expression e) {
    return new Expression(drealJNI.sinh(Expression.getCPtr(e), e), true);
  }

  public static Expression cosh(Expression e) {
    return new Expression(drealJNI.cosh(Expression.getCPtr(e), e), true);
  }

  public static Expression tanh(Expression e) {
    return new Expression(drealJNI.tanh(Expression.getCPtr(e), e), true);
  }

  public static Expression min(Expression e1, Expression e2) {
    return new Expression(
        drealJNI.min(Expression.getCPtr(e1), e1, Expression.getCPtr(e2), e2), true);
  }

  public static Expression max(Expression e1, Expression e2) {
    return new Expression(
        drealJNI.max(Expression.getCPtr(e1), e1, Expression.getCPtr(e2), e2), true);
  }

  public static Expression if_then_else(Formula f_cond, Expression e_then, Expression e_else) {
    return new Expression(
        drealJNI.if_then_else(
            Formula.getCPtr(f_cond),
            f_cond,
            Expression.getCPtr(e_then),
            e_then,
            Expression.getCPtr(e_else),
            e_else),
        true);
  }

  public static Expression uninterpreted_function(String name, Variables vars) {
    return new Expression(
        drealJNI.uninterpreted_function(name, Variables.getCPtr(vars), vars), true);
  }

  public static SWIGTYPE_p_std__ostream InsertOperator(SWIGTYPE_p_std__ostream os, Expression e) {
    return new SWIGTYPE_p_std__ostream(
        drealJNI.InsertOperator__SWIG_4(
            SWIGTYPE_p_std__ostream.getCPtr(os), Expression.getCPtr(e), e),
        false);
  }

  public static boolean is_constant(Expression e) {
    return drealJNI.is_constant__SWIG_0(Expression.getCPtr(e), e);
  }

  public static boolean is_real_constant(Expression e) {
    return drealJNI.is_real_constant(Expression.getCPtr(e), e);
  }

  public static boolean is_variable(Expression e) {
    return drealJNI.is_variable__SWIG_0(Expression.getCPtr(e), e);
  }

  public static boolean is_addition(Expression e) {
    return drealJNI.is_addition(Expression.getCPtr(e), e);
  }

  public static boolean is_multiplication(Expression e) {
    return drealJNI.is_multiplication(Expression.getCPtr(e), e);
  }

  public static boolean is_division(Expression e) {
    return drealJNI.is_division(Expression.getCPtr(e), e);
  }

  public static boolean is_log(Expression e) {
    return drealJNI.is_log(Expression.getCPtr(e), e);
  }

  public static boolean is_abs(Expression e) {
    return drealJNI.is_abs(Expression.getCPtr(e), e);
  }

  public static boolean is_exp(Expression e) {
    return drealJNI.is_exp(Expression.getCPtr(e), e);
  }

  public static boolean is_sqrt(Expression e) {
    return drealJNI.is_sqrt(Expression.getCPtr(e), e);
  }

  public static boolean is_pow(Expression e) {
    return drealJNI.is_pow(Expression.getCPtr(e), e);
  }

  public static boolean is_sin(Expression e) {
    return drealJNI.is_sin(Expression.getCPtr(e), e);
  }

  public static boolean is_cos(Expression e) {
    return drealJNI.is_cos(Expression.getCPtr(e), e);
  }

  public static boolean is_tan(Expression e) {
    return drealJNI.is_tan(Expression.getCPtr(e), e);
  }

  public static boolean is_asin(Expression e) {
    return drealJNI.is_asin(Expression.getCPtr(e), e);
  }

  public static boolean is_acos(Expression e) {
    return drealJNI.is_acos(Expression.getCPtr(e), e);
  }

  public static boolean is_atan(Expression e) {
    return drealJNI.is_atan(Expression.getCPtr(e), e);
  }

  public static boolean is_atan2(Expression e) {
    return drealJNI.is_atan2(Expression.getCPtr(e), e);
  }

  public static boolean is_sinh(Expression e) {
    return drealJNI.is_sinh(Expression.getCPtr(e), e);
  }

  public static boolean is_cosh(Expression e) {
    return drealJNI.is_cosh(Expression.getCPtr(e), e);
  }

  public static boolean is_tanh(Expression e) {
    return drealJNI.is_tanh(Expression.getCPtr(e), e);
  }

  public static boolean is_min(Expression e) {
    return drealJNI.is_min(Expression.getCPtr(e), e);
  }

  public static boolean is_max(Expression e) {
    return drealJNI.is_max(Expression.getCPtr(e), e);
  }

  public static boolean is_if_then_else(Expression e) {
    return drealJNI.is_if_then_else(Expression.getCPtr(e), e);
  }

  public static boolean is_uninterpreted_function(Expression e) {
    return drealJNI.is_uninterpreted_function(Expression.getCPtr(e), e);
  }

  public static Expression Sum(ExpressionVector expressions) {
    return new Expression(drealJNI.Sum(ExpressionVector.getCPtr(expressions), expressions), true);
  }

  public static Expression Prod(ExpressionVector expressions) {
    return new Expression(drealJNI.Prod(ExpressionVector.getCPtr(expressions), expressions), true);
  }

  public static void swap(Expression a, Expression b) {
    drealJNI.swap(Expression.getCPtr(a), a, Expression.getCPtr(b), b);
  }

  public static boolean is_constant(Expression e, double v) {
    return drealJNI.is_constant__SWIG_1(Expression.getCPtr(e), e, v);
  }

  public static boolean is_zero(Expression e) {
    return drealJNI.is_zero(Expression.getCPtr(e), e);
  }

  public static boolean is_one(Expression e) {
    return drealJNI.is_one(Expression.getCPtr(e), e);
  }

  public static boolean is_neg_one(Expression e) {
    return drealJNI.is_neg_one(Expression.getCPtr(e), e);
  }

  public static boolean is_two(Expression e) {
    return drealJNI.is_two(Expression.getCPtr(e), e);
  }

  public static boolean is_nan(Expression e) {
    return drealJNI.is_nan(Expression.getCPtr(e), e);
  }

  public static double get_constant_value(Expression e) {
    return drealJNI.get_constant_value(Expression.getCPtr(e), e);
  }

  public static double get_lb_of_real_constant(Expression e) {
    return drealJNI.get_lb_of_real_constant(Expression.getCPtr(e), e);
  }

  public static double get_ub_of_real_constant(Expression e) {
    return drealJNI.get_ub_of_real_constant(Expression.getCPtr(e), e);
  }

  public static Variable get_variable(Expression e) {
    return new Variable(drealJNI.get_variable__SWIG_0(Expression.getCPtr(e), e), false);
  }

  public static Expression get_argument(Expression e) {
    return new Expression(drealJNI.get_argument(Expression.getCPtr(e), e), false);
  }

  public static Expression get_first_argument(Expression e) {
    return new Expression(drealJNI.get_first_argument(Expression.getCPtr(e), e), false);
  }

  public static Expression get_second_argument(Expression e) {
    return new Expression(drealJNI.get_second_argument(Expression.getCPtr(e), e), false);
  }

  public static double get_constant_in_addition(Expression e) {
    return drealJNI.get_constant_in_addition(Expression.getCPtr(e), e);
  }

  public static ExpressionDoubleMap get_expr_to_coeff_map_in_addition(Expression e) {
    return new ExpressionDoubleMap(
        drealJNI.get_expr_to_coeff_map_in_addition(Expression.getCPtr(e), e), false);
  }

  public static double get_constant_in_multiplication(Expression e) {
    return drealJNI.get_constant_in_multiplication(Expression.getCPtr(e), e);
  }

  public static ExpressionExpressionMap get_base_to_exponent_map_in_multiplication(Expression e) {
    return new ExpressionExpressionMap(
        drealJNI.get_base_to_exponent_map_in_multiplication(Expression.getCPtr(e), e), false);
  }

  public static Formula get_conditional_formula(Expression e) {
    return new Formula(drealJNI.get_conditional_formula(Expression.getCPtr(e), e), false);
  }

  public static Expression get_then_expression(Expression e) {
    return new Expression(drealJNI.get_then_expression(Expression.getCPtr(e), e), false);
  }

  public static Expression get_else_expression(Expression e) {
    return new Expression(drealJNI.get_else_expression(Expression.getCPtr(e), e), false);
  }

  public static String get_uninterpreted_function_name(Expression e) {
    return drealJNI.get_uninterpreted_function_name(Expression.getCPtr(e), e);
  }

  public static Expression Add(Variable var) {
    return new Expression(drealJNI.Add__SWIG_8(Variable.getCPtr(var), var), true);
  }

  public static Expression Substract(Variable var) {
    return new Expression(drealJNI.Substract__SWIG_8(Variable.getCPtr(var), var), true);
  }

  public static boolean Less(FormulaKind k1, FormulaKind k2) {
    return drealJNI.Less__SWIG_2(k1.swigValue(), k2.swigValue());
  }

  public static SWIGTYPE_p_std__ostream InsertOperator(SWIGTYPE_p_std__ostream os, Formula f) {
    return new SWIGTYPE_p_std__ostream(
        drealJNI.InsertOperator__SWIG_5(SWIGTYPE_p_std__ostream.getCPtr(os), Formula.getCPtr(f), f),
        false);
  }

  public static boolean is_false(Formula f) {
    return drealJNI.is_false(Formula.getCPtr(f), f);
  }

  public static boolean is_true(Formula f) {
    return drealJNI.is_true(Formula.getCPtr(f), f);
  }

  public static boolean is_variable(Formula f) {
    return drealJNI.is_variable__SWIG_1(Formula.getCPtr(f), f);
  }

  public static boolean is_equal_to(Formula f) {
    return drealJNI.is_equal_to(Formula.getCPtr(f), f);
  }

  public static boolean is_not_equal_to(Formula f) {
    return drealJNI.is_not_equal_to(Formula.getCPtr(f), f);
  }

  public static boolean is_greater_than(Formula f) {
    return drealJNI.is_greater_than(Formula.getCPtr(f), f);
  }

  public static boolean is_greater_than_or_equal_to(Formula f) {
    return drealJNI.is_greater_than_or_equal_to(Formula.getCPtr(f), f);
  }

  public static boolean is_less_than(Formula f) {
    return drealJNI.is_less_than(Formula.getCPtr(f), f);
  }

  public static boolean is_less_than_or_equal_to(Formula f) {
    return drealJNI.is_less_than_or_equal_to(Formula.getCPtr(f), f);
  }

  public static boolean is_relational(Formula f) {
    return drealJNI.is_relational(Formula.getCPtr(f), f);
  }

  public static boolean is_conjunction(Formula f) {
    return drealJNI.is_conjunction(Formula.getCPtr(f), f);
  }

  public static boolean is_disjunction(Formula f) {
    return drealJNI.is_disjunction(Formula.getCPtr(f), f);
  }

  public static boolean is_negation(Formula f) {
    return drealJNI.is_negation(Formula.getCPtr(f), f);
  }

  public static boolean is_forall(Formula f) {
    return drealJNI.is_forall(Formula.getCPtr(f), f);
  }

  public static Formula Not(Formula f) {
    return new Formula(drealJNI.Not__SWIG_0(Formula.getCPtr(f), f), true);
  }

  public static Formula Equal(Expression e1, Expression e2) {
    return new Formula(
        drealJNI.Equal__SWIG_1(Expression.getCPtr(e1), e1, Expression.getCPtr(e2), e2), true);
  }

  public static Formula NotEqual(Expression e1, Expression e2) {
    return new Formula(
        drealJNI.NotEqual__SWIG_0(Expression.getCPtr(e1), e1, Expression.getCPtr(e2), e2), true);
  }

  public static Formula Less(Expression e1, Expression e2) {
    return new Formula(
        drealJNI.Less__SWIG_3(Expression.getCPtr(e1), e1, Expression.getCPtr(e2), e2), true);
  }

  public static Formula LessEqual(Expression e1, Expression e2) {
    return new Formula(
        drealJNI.LessEqual(Expression.getCPtr(e1), e1, Expression.getCPtr(e2), e2), true);
  }

  public static Formula Grater(Expression e1, Expression e2) {
    return new Formula(
        drealJNI.Grater(Expression.getCPtr(e1), e1, Expression.getCPtr(e2), e2), true);
  }

  public static Formula GraterEqual(Expression e1, Expression e2) {
    return new Formula(
        drealJNI.GraterEqual(Expression.getCPtr(e1), e1, Expression.getCPtr(e2), e2), true);
  }

  public static Formula forall(Variables vars, Formula f) {
    return new Formula(drealJNI.forall(Variables.getCPtr(vars), vars, Formula.getCPtr(f), f), true);
  }

  public static Formula And(Formula f1, Formula f2) {
    return new Formula(
        drealJNI.And__SWIG_0(Formula.getCPtr(f1), f1, Formula.getCPtr(f2), f2), true);
  }

  public static Formula And(Variable v, Formula f) {
    return new Formula(drealJNI.And__SWIG_4(Variable.getCPtr(v), v, Formula.getCPtr(f), f), true);
  }

  public static Formula And(Formula f, Variable v) {
    return new Formula(drealJNI.And__SWIG_6(Formula.getCPtr(f), f, Variable.getCPtr(v), v), true);
  }

  public static Formula And(Variable v1, Variable v2) {
    return new Formula(
        drealJNI.And__SWIG_8(Variable.getCPtr(v1), v1, Variable.getCPtr(v2), v2), true);
  }

  public static Formula Or(Formula f1, Formula f2) {
    return new Formula(drealJNI.Or__SWIG_0(Formula.getCPtr(f1), f1, Formula.getCPtr(f2), f2), true);
  }

  public static Formula Or(Variable v, Formula f) {
    return new Formula(drealJNI.Or__SWIG_4(Variable.getCPtr(v), v, Formula.getCPtr(f), f), true);
  }

  public static Formula Or(Formula f, Variable v) {
    return new Formula(drealJNI.Or__SWIG_6(Formula.getCPtr(f), f, Variable.getCPtr(v), v), true);
  }

  public static Formula Or(Variable v1, Variable v2) {
    return new Formula(
        drealJNI.Or__SWIG_8(Variable.getCPtr(v1), v1, Variable.getCPtr(v2), v2), true);
  }

  public static Formula Not(Variable v) {
    return new Formula(drealJNI.Not__SWIG_1(Variable.getCPtr(v), v), true);
  }

  public static Formula Equal(Variable v1, Variable v2) {
    return new Formula(
        drealJNI.Equal__SWIG_2(Variable.getCPtr(v1), v1, Variable.getCPtr(v2), v2), true);
  }

  public static Formula Equal(Formula f1, Formula f2) {
    return new Formula(
        drealJNI.Equal__SWIG_3(Formula.getCPtr(f1), f1, Formula.getCPtr(f2), f2), true);
  }

  public static Formula Equal(Variable v, Formula f) {
    return new Formula(drealJNI.Equal__SWIG_4(Variable.getCPtr(v), v, Formula.getCPtr(f), f), true);
  }

  public static Formula Equal(Formula f, Variable v) {
    return new Formula(drealJNI.Equal__SWIG_5(Formula.getCPtr(f), f, Variable.getCPtr(v), v), true);
  }

  public static Formula NotEqual(Variable v1, Variable v2) {
    return new Formula(
        drealJNI.NotEqual__SWIG_1(Variable.getCPtr(v1), v1, Variable.getCPtr(v2), v2), true);
  }

  public static Formula NotEqual(Formula f1, Formula f2) {
    return new Formula(
        drealJNI.NotEqual__SWIG_2(Formula.getCPtr(f1), f1, Formula.getCPtr(f2), f2), true);
  }

  public static Formula NotEqual(Variable v, Formula f) {
    return new Formula(
        drealJNI.NotEqual__SWIG_3(Variable.getCPtr(v), v, Formula.getCPtr(f), f), true);
  }

  public static Formula NotEqual(Formula f, Variable v) {
    return new Formula(
        drealJNI.NotEqual__SWIG_4(Formula.getCPtr(f), f, Variable.getCPtr(v), v), true);
  }

  public static boolean is_nary(Formula f) {
    return drealJNI.is_nary(Formula.getCPtr(f), f);
  }

  public static Variable get_variable(Formula f) {
    return new Variable(drealJNI.get_variable__SWIG_1(Formula.getCPtr(f), f), false);
  }

  public static Expression get_lhs_expression(Formula f) {
    return new Expression(drealJNI.get_lhs_expression(Formula.getCPtr(f), f), false);
  }

  public static Expression get_rhs_expression(Formula f) {
    return new Expression(drealJNI.get_rhs_expression(Formula.getCPtr(f), f), false);
  }

  public static FormulaSet get_operands(Formula f) {
    return new FormulaSet(drealJNI.get_operands(Formula.getCPtr(f), f), false);
  }

  public static Formula get_operand(Formula f) {
    return new Formula(drealJNI.get_operand(Formula.getCPtr(f), f), false);
  }

  public static Variables get_quantified_variables(Formula f) {
    return new Variables(drealJNI.get_quantified_variables(Formula.getCPtr(f), f), false);
  }

  public static Formula get_quantified_formula(Formula f) {
    return new Formula(drealJNI.get_quantified_formula(Formula.getCPtr(f), f), false);
  }

  public static Formula logic_and(Formula f1, Formula f2) {
    return new Formula(drealJNI.logic_and(Formula.getCPtr(f1), f1, Formula.getCPtr(f2), f2), true);
  }

  public static Formula imply(Formula f1, Formula f2) {
    return new Formula(
        drealJNI.imply__SWIG_0(Formula.getCPtr(f1), f1, Formula.getCPtr(f2), f2), true);
  }

  public static Formula imply(Variable v, Formula f) {
    return new Formula(drealJNI.imply__SWIG_1(Variable.getCPtr(v), v, Formula.getCPtr(f), f), true);
  }

  public static Formula imply(Formula f, Variable v) {
    return new Formula(drealJNI.imply__SWIG_2(Formula.getCPtr(f), f, Variable.getCPtr(v), v), true);
  }

  public static Formula imply(Variable v1, Variable v2) {
    return new Formula(
        drealJNI.imply__SWIG_3(Variable.getCPtr(v1), v1, Variable.getCPtr(v2), v2), true);
  }

  public static Formula iff(Formula f1, Formula f2) {
    return new Formula(
        drealJNI.iff__SWIG_0(Formula.getCPtr(f1), f1, Formula.getCPtr(f2), f2), true);
  }

  public static Formula iff(Variable v, Formula f) {
    return new Formula(drealJNI.iff__SWIG_1(Variable.getCPtr(v), v, Formula.getCPtr(f), f), true);
  }

  public static Formula iff(Formula f, Variable v) {
    return new Formula(drealJNI.iff__SWIG_2(Formula.getCPtr(f), f, Variable.getCPtr(v), v), true);
  }

  public static Formula iff(Variable v1, Variable v2) {
    return new Formula(
        drealJNI.iff__SWIG_3(Variable.getCPtr(v1), v1, Variable.getCPtr(v2), v2), true);
  }

  public static FormulaSet map(
      FormulaSet formulas,
      SWIGTYPE_p_std__functionT_dreal__drake__symbolic__Formula_fdreal__drake__symbolic__Formula_const_RF_t
          func) {
    return new FormulaSet(
        drealJNI.map(
            FormulaSet.getCPtr(formulas),
            formulas,
            SWIGTYPE_p_std__functionT_dreal__drake__symbolic__Formula_fdreal__drake__symbolic__Formula_const_RF_t
                .getCPtr(func)),
        true);
  }

  public static boolean is_atomic(Formula f) {
    return drealJNI.is_atomic(Formula.getCPtr(f), f);
  }

  public static boolean is_clause(Formula f) {
    return drealJNI.is_clause(Formula.getCPtr(f), f);
  }

  public static FormulaSet get_clauses(Formula f) {
    return new FormulaSet(drealJNI.get_clauses(Formula.getCPtr(f), f), true);
  }

  public static boolean is_cnf(Formula f) {
    return drealJNI.is_cnf(Formula.getCPtr(f), f);
  }

  public static boolean HaveIntersection(Variables variables1, Variables variables2) {
    return drealJNI.HaveIntersection(
        Variables.getCPtr(variables1), variables1, Variables.getCPtr(variables2), variables2);
  }

  public static Formula DeltaStrengthen(Formula f, double delta) {
    return new Formula(drealJNI.DeltaStrengthen(Formula.getCPtr(f), f, delta), true);
  }

  public static Formula DeltaWeaken(Formula f, double delta) {
    return new Formula(drealJNI.DeltaWeaken(Formula.getCPtr(f), f, delta), true);
  }

  public static boolean IsDifferentiable(Formula f) {
    return drealJNI.IsDifferentiable__SWIG_0(Formula.getCPtr(f), f);
  }

  public static boolean IsDifferentiable(Expression e) {
    return drealJNI.IsDifferentiable__SWIG_1(Expression.getCPtr(e), e);
  }

  public static Formula make_conjunction(FormulaVector formulas) {
    return new Formula(drealJNI.make_conjunction(FormulaVector.getCPtr(formulas), formulas), true);
  }

  public static Formula make_disjunction(FormulaVector formulas) {
    return new Formula(drealJNI.make_disjunction(FormulaVector.getCPtr(formulas), formulas), true);
  }

  public static VariableVector CreateVector(String prefix, int size, Variable.Type type) {
    return new VariableVector(drealJNI.CreateVector__SWIG_0(prefix, size, type.swigValue()), true);
  }

  public static VariableVector CreateVector(String prefix, int size) {
    return new VariableVector(drealJNI.CreateVector__SWIG_1(prefix, size), true);
  }

  public static RelationalOperator Not(RelationalOperator op) {
    return RelationalOperator.swigToEnum(drealJNI.Not(op.swigValue()));
  }

  public static SWIGTYPE_p_std__ostream InsertOperator(
      SWIGTYPE_p_std__ostream os, RelationalOperator op) {
    return new SWIGTYPE_p_std__ostream(
        drealJNI.InsertOperator__SWIG_6(SWIGTYPE_p_std__ostream.getCPtr(os), op.swigValue()),
        false);
  }

  public static Logic parse_logic(String s) {
    return Logic.swigToEnum(drealJNI.parse_logic(s));
  }

  public static SWIGTYPE_p_std__ostream InsertOperator(SWIGTYPE_p_std__ostream os, Logic logic) {
    return new SWIGTYPE_p_std__ostream(
        drealJNI.InsertOperator__SWIG_7(SWIGTYPE_p_std__ostream.getCPtr(os), logic.swigValue()),
        false);
  }

  public static SWIGTYPE_p_std__ostream InsertOperator(SWIGTYPE_p_std__ostream os, Box box) {
    return new SWIGTYPE_p_std__ostream(
        drealJNI.InsertOperator__SWIG_8(SWIGTYPE_p_std__ostream.getCPtr(os), Box.getCPtr(box), box),
        false);
  }

  public static boolean Equal(Box b1, Box b2) {
    return drealJNI.Equal(Box.getCPtr(b1), b1, Box.getCPtr(b2), b2);
  }

  public static boolean NotEqual(Box b1, Box b2) {
    return drealJNI.NotEqual(Box.getCPtr(b1), b1, Box.getCPtr(b2), b2);
  }

  public static SWIGTYPE_p_std__ostream DisplayDiff(
      SWIGTYPE_p_std__ostream os,
      VariableVector variables,
      SWIGTYPE_p_ibex__IntervalVector old_iv,
      SWIGTYPE_p_ibex__IntervalVector new_iv) {
    return new SWIGTYPE_p_std__ostream(
        drealJNI.DisplayDiff(
            SWIGTYPE_p_std__ostream.getCPtr(os),
            VariableVector.getCPtr(variables),
            variables,
            SWIGTYPE_p_ibex__IntervalVector.getCPtr(old_iv),
            SWIGTYPE_p_ibex__IntervalVector.getCPtr(new_iv)),
        false);
  }

  public static SWIGTYPE_p_std__ostream InsertOperator(
      SWIGTYPE_p_std__ostream os, Config.SatDefaultPhase sat_default_phase) {
    return new SWIGTYPE_p_std__ostream(
        drealJNI.InsertOperator__SWIG_9(
            SWIGTYPE_p_std__ostream.getCPtr(os), sat_default_phase.swigValue()),
        false);
  }

  public static SWIGTYPE_p_std__ostream InsertOperator(SWIGTYPE_p_std__ostream os, Config config) {
    return new SWIGTYPE_p_std__ostream(
        drealJNI.InsertOperator__SWIG_22(
            SWIGTYPE_p_std__ostream.getCPtr(os), Config.getCPtr(config), config),
        false);
  }

  public static boolean CheckSatisfiability(Formula f, double delta, Box box) {
    return drealJNI.CheckSatisfiability__SWIG_0(
        Formula.getCPtr(f), f, delta, Box.getCPtr(box), box);
  }

  public static boolean CheckSatisfiability(Formula f, Config config, Box box) {
    return drealJNI.CheckSatisfiability__SWIG_1(
        Formula.getCPtr(f), f, Config.getCPtr(config), config, Box.getCPtr(box), box);
  }

  public static boolean Minimize(Expression objective, Formula constraint, double delta, Box box) {
    return drealJNI.Minimize__SWIG_0(
        Expression.getCPtr(objective),
        objective,
        Formula.getCPtr(constraint),
        constraint,
        delta,
        Box.getCPtr(box),
        box);
  }

  public static boolean Minimize(Expression objective, Formula constraint, Config config, Box box) {
    return drealJNI.Minimize__SWIG_1(
        Expression.getCPtr(objective),
        objective,
        Formula.getCPtr(constraint),
        constraint,
        Config.getCPtr(config),
        config,
        Box.getCPtr(box),
        box);
  }

  // self written
  public static boolean CheckSatisfiability(Formula f) {
    return drealJNI.CheckSatisfiability__SWIG_2(Formula.getCPtr(f));
  }

  /**
   * This function reads the result of one variable from the model(Box). It should be known what
   * variable is called, to save the String with the associated variable.
   *
   * @param box to read the values of the variables
   * @param var to get the value associated with the variable of the box
   * @return String with the value, value is the lower-bound or ENTIRE or EMPTY
   */
  public static String getResult(Box box, Variable var) {
    // returns EMPTY or lower-bound; upperbound as String
    String result = drealJNI.getResult(Box.getCPtr(box), Variable.getCPtr(var));
    if (result.equals("EMPTY")) {
      return "EMPTY";
    }
    String[] bounds = result.split("; ", -1);
    if (bounds[0].equals(bounds[1])) {
      return bounds[0];
      // Probably not needed, because it is already evaluated to True e.g. x * 1 == x is
      // evaluated to True and variable x does not exist in result anymore
    } else if (bounds[0].equals("-inf") && bounds[1].equals("inf")) {
      return "ENTIRE";
    } else if (bounds[0].equals("-inf") || bounds[1].equals("inf")) {
      if (bounds[0].equals("-inf")) {
        return bounds[1];
      } else {
        return bounds[0];
      }
    } else {
      return bounds[0];
    }
  }

  // Zum testen

  public static boolean has_variable(Box box, Variable var) {
    return drealJNI.Box_has_variable_0(Box.getCPtr(box), Variable.getCPtr(var));
  }

  public static boolean CheckSatisfiabilityTest(Formula f, double delta, Box box) {
    System.out.println("Aufruf von Main und jetzt wird drealJNI und dann wrapper aufgerufen");
    return drealJNI.CheckSatisfiability__SWIG_3(Formula.getCPtr(f), delta, Box.getCPtr(box));
  }

  public static void Test() {
    drealJNI.Context_Test();
  }
  ;

  public static void TestCheckSat() {
    drealJNI.Context_TestCheckSat();
  }

  public static void satCheck() {
    drealJNI.satCheck();
  }
}
