// This file is part of JavaSMT,
// an API wrapper for a collection of SMT solvers:
// https://github.com/sosy-lab/java-smt
//
// SPDX-FileCopyrightText: 2023 Dirk Beyer <https://www.sosy-lab.org>
//
// SPDX-License-Identifier: Apache-2.0

package org.sosy_lab.java_smt.solvers.cvc5;

import com.google.common.base.Preconditions;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import io.github.cvc5.Kind;
import io.github.cvc5.Solver;
import io.github.cvc5.Term;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.sosy_lab.common.ShutdownNotifier;
import org.sosy_lab.java_smt.api.BooleanFormula;
import org.sosy_lab.java_smt.api.Formula;
import org.sosy_lab.java_smt.api.FormulaManager;
import org.sosy_lab.java_smt.api.InterpolatingProverEnvironment;
import org.sosy_lab.java_smt.api.SolverContext.ProverOptions;
import org.sosy_lab.java_smt.api.SolverException;

public class CVC5InterpolatingProver extends CVC5AbstractProver<Term>
    implements InterpolatingProverEnvironment<Term> {

  private final FormulaManager mgr;
  private final Set<ProverOptions> solverOptions;
  private final int seed;

  CVC5InterpolatingProver(
      CVC5FormulaCreator pFormulaCreator,
      ShutdownNotifier pShutdownNotifier,
      @SuppressWarnings("unused") int randomSeed,
      Set<ProverOptions> pOptions,
      FormulaManager pMgr) {
    super(pFormulaCreator, pShutdownNotifier, randomSeed, pOptions, pMgr);
    mgr = pMgr;
    solverOptions = pOptions;
    seed = randomSeed;
  }

  /**
   * Sets the same solver Options of the Original Solver to the separate solvertoSet, except for
   * produce-interpolants which is set here. From CVC5AbstractProver Line 66
   */
  @Override
  protected void setSolverOptions(int randomSeed, Set<ProverOptions> pOptions, Solver pSolver) {
    super.setSolverOptions(randomSeed, pOptions, pSolver);
    pSolver.setOption("produce-interpolants", "true");
  }

  @Override
  public Term addConstraint(BooleanFormula pConstraint) throws InterruptedException {
    Preconditions.checkState(!closed);
    Term t = creator.extractInfo(pConstraint);

    super.addConstraint(pConstraint);
    return t; // t is not returned in the Abstract Class
  }

  @Override
  public BooleanFormula getInterpolant(Collection<Term> pFormulasOfA)
      throws SolverException, InterruptedException {
    Preconditions.checkState(!closed);

    if (pFormulasOfA.isEmpty()) { // Catch trivial case
      return mgr.getBooleanFormulaManager().makeBoolean(true);
    }

    // formulasOfB := assertedFormulas - pFormulasOfA
    Set<Term> formulasOfB =
        assertedFormulas.stream()
            .flatMap(c -> c.stream())
            .filter(n -> !pFormulasOfA.contains(n))
            .collect(ImmutableSet.toImmutableSet());

    if (formulasOfB.isEmpty()) { // Catch trivial case
      return mgr.getBooleanFormulaManager().makeBoolean(false);
    }

    // fit the Input to work with getCVC5Interpolation

    ImmutableList<Collection<Term>> formAAsList = ImmutableList.of(pFormulasOfA);
    ImmutableList<Collection<Term>> formBAsList = ImmutableList.of(formulasOfB);

    Term itp = getCVC5Interpolation(formAAsList, formBAsList);

    return creator.encapsulateBoolean(itp);
  }

  @Override
  public List<BooleanFormula> getSeqInterpolants(
      List<? extends Collection<Term>> partitionedFormulas)
      throws SolverException, InterruptedException {
    Preconditions.checkArgument(
        !partitionedFormulas.isEmpty(), "at least one partition should be available.");

    List<BooleanFormula> itps = Stream.of(new BooleanFormula[] {}).collect(Collectors.toList());

    if (partitionedFormulas.size() == 1) {
      return itps;
    }
    itps.add(
        getInterpolant(ImmutableList.copyOf(Iterables.concat(partitionedFormulas.subList(0, 1)))));
    Integer n = partitionedFormulas.size();
    for (int i = 2; i < n; i++) {
      Term preInterpol = creator.extractInfo(itps.get(i - 2));
      Term currFormulaInterpol =
          getCVC5Interpolation(
              ImmutableList.of(ImmutableSet.of(preInterpol), partitionedFormulas.get(i - 1)),
              ImmutableList.of(
                  ImmutableList.copyOf(Iterables.concat(partitionedFormulas.subList(i, n)))));
      itps.add(creator.encapsulateBoolean(currFormulaInterpol));
    }

    return itps;
  }

  // Experimental!
  @Override
  public List<BooleanFormula> getTreeInterpolants(
      List<? extends Collection<Term>> pPartitionedFormulas, int[] pStartOfSubTree)
      throws SolverException, InterruptedException {
    Preconditions.checkState(!closed);
    assert InterpolatingProverEnvironment.checkTreeStructure(
        pPartitionedFormulas.size(), pStartOfSubTree);

    // Generate every Interpolation Pair
    ImmutableList<ImmutableList<ImmutableList<Collection<Term>>>> interpolationPairs =
        getTreeInterpolationPairs(pPartitionedFormulas, pStartOfSubTree);

    final List<Term> itps = Stream.of(new Term[] {}).collect(Collectors.toList());
    try { // Interpolate every Interpolation Pair
      for (int i = 0; i < interpolationPairs.size(); i++) {
        itps.add(
            getCVC5Interpolation(
                ImmutableList.copyOf(interpolationPairs.get(i).get(0)),
                ImmutableList.copyOf(interpolationPairs.get(i).get(1))));
      }
    } catch (UnsupportedOperationException e) {
      if (e.getMessage() != null) {
        throw new SolverException(e.getMessage(), e);
      } else {
        throw e;
      }
    }

    final List<BooleanFormula> result =
        Stream.of(new BooleanFormula[] {}).collect(Collectors.toList());
    for (Term itp : itps) {
      result.add(creator.encapsulateBoolean(itp));
    }
    assert result.size() == pStartOfSubTree.length - 1;
    return result;
  }

  /**
   * Interpolates a Tuple of Interpolants according to Craig-Interpolation using CVC5-Interpolation.
   *
   * <p>CVC5's getInterpolant: There is a Model, such that the Interpolant I, with A -> I = True
   * (1CVC5) and I -> B = True (2CVC5).
   *
   * <p>Craig Interpolation: There is a Model, such that the Interpolant psi, with phi- -> psi =
   * True (1Craig), not(psi /\ phi+) = True (2Craig).
   *
   * <p>With A/phi- current set of assertions and B/phi+ Formulas to interpolate.
   *
   * <p>CVC5 -> Craig Interpolation:
   *
   * <p>(1CVC5) <=> (1Craig) due to subst of A with phi- and reflexivity.
   *
   * <p>(2CVC5) <=> I -> B = True <=> (not I) \/ B = True <=> not (I /\ (not B)) = True <=> (2Craig)
   * due to subst of (not B) with phi+ and reflexivity.
   *
   * <p>Hence, CVC5's Interpolation produces an equivalent Interpolation to Craig Interpolation, if
   * B is negated during CVC5 Interpolation.
   *
   * @param pFormAAsList current Set of Assertions A
   * @param pFormBAsList Formulas to Interpolate B
   * @return Interpolation of the Interpolation Pair following the definition of
   *     Craig-Interpolation.
   */
  private Term getCVC5Interpolation(
      ImmutableList<Collection<Term>> pFormAAsList, ImmutableList<Collection<Term>> pFormBAsList) {

    // Uses a separate Solver instance to leave the original solver-context unmodified
    Solver interpolationSolver = new Solver();

    setSolverOptions(seed, solverOptions, interpolationSolver);

    interpolationSolver.resetAssertions();

    Term interpolA = buildConjunctionOfFormulasOverList(pFormAAsList, interpolationSolver);

    Term interpolB = buildConjunctionOfFormulasOverList(pFormBAsList, interpolationSolver);

    interpolationSolver.assertFormula(interpolA);

    Term interpolant =
        interpolationSolver.getInterpolant(interpolationSolver.mkTerm(Kind.NOT, interpolB));

    ImmutableMap<String, Formula> interpolantSymbols =
        mgr.extractVariablesAndUFs(creator.encapsulateBoolean(interpolant));
    ImmutableMap<String, Formula> interpolASymbols =
        mgr.extractVariablesAndUFs(creator.encapsulateBoolean(interpolA));
    ImmutableMap<String, Formula> interpolBSymbols =
        mgr.extractVariablesAndUFs(creator.encapsulateBoolean(interpolB));
    boolean interpolSymbolMatch = true;
    for (String key : interpolantSymbols.keySet()) {
      if (!(interpolASymbols.containsKey(key) || interpolBSymbols.containsKey(key))) {
        interpolSymbolMatch = false;
      }
    }

    Preconditions.checkArgument(interpolSymbolMatch, "Interpolant contains Symbols not in A or B.");

    interpolationSolver.deletePointer();

    return interpolant;
  }

  /**
   * Turns a List of Collections of Formulas to a Single Conjunction of the Formulas e.g.:
   * [[A,B],[C]] -> A/\B/\C.
   *
   * @param setOfFormulasToConcat List of Collections of formulas
   * @param usingSolver the CVC5 Solver Instance to use
   * @return concatenated Formulas with AND as CVC5 Term
   */
  private Term buildConjunctionOfFormulasOverList(
      ImmutableList<Collection<Term>> setOfFormulasToConcat, Solver usingSolver) {
    Term concatTerm =
        buildConjunctionOfFormulas(
            FluentIterable.concat(setOfFormulasToConcat).toSet(), usingSolver);

    return concatTerm;
  }

  /**
   * Turns a collection of Formulas to a Single Conjunction of the Formulas e.g.: [A,B,C] ->
   * A/\B/\C.
   *
   * @param formulas collection of formulas
   * @param usingSolver the CVC5 Solver Instance to use
   * @return concatenated Formulas with AND as CVC5 Term
   */
  private Term buildConjunctionOfFormulas(Collection<Term> formulas, Solver usingSolver) {
    Preconditions.checkState(!closed);
    Preconditions.checkArgument(!formulas.isEmpty());

    switch (formulas.size()) {
      case 0:
        return usingSolver.mkBoolean(true);
      case 1:
        return Iterables.getOnlyElement(formulas);
      default:
        return usingSolver.mkTerm(Kind.AND, formulas.toArray(new Term[0]));
    }
  }

  /**
   * Generates Interpolation Pairs for Tree Interpolation. Experimental!
   *
   * @param pPartitionedFormulas of formulas
   * @param pStartOfSubTree The start of the subtree containing the formula at this index as root.
   * @return An List of Interpolation Pairs (as Tuple (a List)) containing Lists of Collection of
   *     Terms
   */
  @SuppressWarnings("unchecked")
  private ImmutableList<ImmutableList<ImmutableList<Collection<Term>>>> getTreeInterpolationPairs(
      List<? extends Collection<Term>> pPartitionedFormulas, int[] pStartOfSubTree) {
    // First Interpolation Pair
    ImmutableList<Integer> leafes = ImmutableList.of(pStartOfSubTree[0]);
    // current generated LHS of Tuple
    ImmutableList<Collection<Term>> currA = ImmutableList.of(pPartitionedFormulas.get(0));
    // current generated RHS of Tuple
    ImmutableList<Collection<Term>> currB =
        ImmutableList.copyOf(pPartitionedFormulas.subList(1, pPartitionedFormulas.size()));
    // current generated Interpolation Tuple
    ImmutableList<ImmutableList<Collection<Term>>> betweenRes = ImmutableList.of(currA, currB);
    ImmutableList<ImmutableList<ImmutableList<Collection<Term>>>> result =
        ImmutableList.of(betweenRes);
    // iterate through Tree structure
    for (int i = 1; i < pStartOfSubTree.length - 1; i++) {
      // if the leave does not change, continue like Sequential Interpolation
      if (pStartOfSubTree[i] == pStartOfSubTree[i - 1]) {
        currA =
            Stream.concat(currA.stream(), ImmutableList.of(pPartitionedFormulas.get(i)).stream())
                .collect(ImmutableList.toImmutableList());
        currB = currB.subList(1, currB.size());
        betweenRes = ImmutableList.of(currA, currB);
      } else {
        // if the leave for the node already existed, rebuild the lists, split at the node
        if (leafes.contains(pStartOfSubTree[i])) {
          currA = new ImmutableList.Builder<Collection<Term>>().build();
          currB = new ImmutableList.Builder<Collection<Term>>().build();
          for (int j = 0; j < pStartOfSubTree.length; j++) {
            if (j <= i) {
              currA =
                  Stream.concat(
                          currA.stream(), ImmutableList.of(pPartitionedFormulas.get(j)).stream())
                      .collect(ImmutableList.toImmutableList());
            } else {
              currB =
                  Stream.concat(
                          currA.stream(), ImmutableList.of(pPartitionedFormulas.get(j)).stream())
                      .collect(ImmutableList.toImmutableList());
            }
          }
          betweenRes = ImmutableList.of(currA, currB);
          // rebuild currA from beginning with new node
        } else {
          currB =
              Stream.concat(currB.stream(), currA.stream())
                  .collect(ImmutableList.toImmutableList());
          currA = new ImmutableList.Builder<Collection<Term>>().build();
          currA =
              Stream.concat(currA.stream(), ImmutableList.of(pPartitionedFormulas.get(i)).stream())
                  .collect(ImmutableList.toImmutableList());
          currB = currB.subList(1, currB.size());
          betweenRes = ImmutableList.of(currA, currB);
          leafes =
              Stream.concat(leafes.stream(), ImmutableList.of(pStartOfSubTree[i]).stream())
                  .collect(ImmutableList.toImmutableList());
        }
      }
      result =
          Stream.concat(result.stream(), ImmutableList.of(betweenRes).stream())
              .collect(ImmutableList.toImmutableList());
    }
    assert result.size() == pStartOfSubTree.length - 1;
    return result;
  }
}
