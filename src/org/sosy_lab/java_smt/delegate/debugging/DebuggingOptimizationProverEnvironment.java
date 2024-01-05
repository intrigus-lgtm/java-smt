// This file is part of JavaSMT,
// an API wrapper for a collection of SMT solvers:
// https://github.com/sosy-lab/java-smt
//
// SPDX-FileCopyrightText: 2024 Dirk Beyer <https://www.sosy-lab.org>
//
// SPDX-License-Identifier: Apache-2.0

package org.sosy_lab.java_smt.delegate.debugging;

import java.util.Optional;
import java.util.Set;
import org.sosy_lab.common.rationals.Rational;
import org.sosy_lab.java_smt.api.Formula;
import org.sosy_lab.java_smt.api.OptimizationProverEnvironment;
import org.sosy_lab.java_smt.api.SolverException;

public class DebuggingOptimizationProverEnvironment extends DebuggingBasicProverEnvironment<Void>
    implements OptimizationProverEnvironment {
  private final OptimizationProverEnvironment delegate;

  public DebuggingOptimizationProverEnvironment(
      OptimizationProverEnvironment pDelegate, Set<Formula> pLocalFormulas) {
    super(pDelegate, pLocalFormulas);
    delegate = pDelegate;
  }

  @Override
  public int maximize(Formula objective) {
    assertThreadLocal();
    assertFormulaInContext(objective);
    return delegate.maximize(objective);
  }

  @Override
  public int minimize(Formula objective) {
    assertThreadLocal();
    assertFormulaInContext(objective);
    return delegate.maximize(objective);
  }

  @Override
  public OptStatus check() throws InterruptedException, SolverException {
    assertThreadLocal();
    return delegate.check();
  }

  @Override
  public Optional<Rational> upper(int handle, Rational epsilon) {
    assertThreadLocal();
    return delegate.upper(handle, epsilon);
  }

  @Override
  public Optional<Rational> lower(int handle, Rational epsilon) {
    assertThreadLocal();
    return delegate.lower(handle, epsilon);
  }
}