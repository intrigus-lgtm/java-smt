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

package org.sosy_lab.java_smt.solvers.apron;

import java.util.Collection;
import java.util.List;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.sosy_lab.java_smt.api.SolverException;
import org.sosy_lab.java_smt.basicimpl.AbstractEvaluator;
import org.sosy_lab.java_smt.basicimpl.AbstractProver;
import org.sosy_lab.java_smt.basicimpl.FormulaCreator;
import org.sosy_lab.java_smt.solvers.apron.types.ApronFormulaType;
import org.sosy_lab.java_smt.solvers.apron.types.ApronNode;
import org.sosy_lab.java_smt.solvers.apron.types.ApronNode.ApronConstraint;

public class ApronEvaluator extends AbstractEvaluator<ApronNode, ApronFormulaType, ApronModel> {

  private final List<Collection<ApronConstraint>> assertedFormulas;

  private final ApronTheoremProver theoremProver;
  protected ApronEvaluator(
      AbstractProver<?> pProver,
      FormulaCreator<ApronNode,ApronFormulaType, ApronModel, Long> creator) {
    super(pProver, creator);
    ApronTheoremProver prover = (ApronTheoremProver) pProver;
    this.assertedFormulas = prover.getAssertedFormulas();
    this.theoremProver = prover;
  }

  @Override
  protected @Nullable ApronNode evalImpl(ApronNode formula) {
    try {
      ApronModel apronModel = (ApronModel) theoremProver.getModel();
      return apronModel.getValue(formula);
    }catch (SolverException e){
      throw new RuntimeException(e);
    }
  }
}