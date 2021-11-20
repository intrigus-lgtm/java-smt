// This file is part of JavaSMT,
// an API wrapper for a collection of SMT solvers:
// https://github.com/sosy-lab/java-smt
//
// SPDX-FileCopyrightText: 2021 Dirk Beyer <https://www.sosy-lab.org>
//
// SPDX-License-Identifier: Apache-2.0

package org.sosy_lab.java_smt.test;

import static com.google.common.truth.TruthJUnit.assume;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.sosy_lab.java_smt.SolverContextFactory.Solvers;
import org.sosy_lab.java_smt.api.BooleanFormula;
import org.sosy_lab.java_smt.api.NumeralFormula.IntegerFormula;
import org.sosy_lab.java_smt.api.RegexFormula;
import org.sosy_lab.java_smt.api.SolverException;
import org.sosy_lab.java_smt.api.StringFormula;

@SuppressWarnings("ConstantConditions")
@SuppressFBWarnings(value = "DLS_DEAD_LOCAL_STORE", justification = "test code")
@RunWith(Parameterized.class)
public class StringFormulaManagerTest extends SolverBasedTest0 {

  private StringFormula hello;
  private RegexFormula a2z;

  @Parameters(name = "{0}")
  public static Object[] getAllSolvers() {
    return Solvers.values();
  }

  @Parameter public Solvers solverUnderTest;

  @Override
  protected Solvers solverToUse() {
    return solverUnderTest;
  }

  @Before
  public void setup() {
    requireStrings();
    hello = smgr.makeString("hello");
    a2z = smgr.range('a', 'z');
  }

  // Utility methods

  private void assertEqual(IntegerFormula num1, IntegerFormula num2)
      throws SolverException, InterruptedException {
    assertThatFormula(imgr.equal(num1, num2)).isTautological();
  }

  private void assertDistinct(IntegerFormula num1, IntegerFormula num2)
      throws SolverException, InterruptedException {
    assertThatFormula(imgr.distinct(List.of(num1, num2))).isTautological();
  }

  private void assertEqual(StringFormula str1, StringFormula str2)
      throws SolverException, InterruptedException {
    assertThatFormula(smgr.equal(str1, str2)).isTautological();
  }

  private void assertDistinct(StringFormula str1, StringFormula str2)
      throws SolverException, InterruptedException {
    assertThatFormula(smgr.equal(str1, str2)).isUnsatisfiable();
  }

  // Tests

  @Test
  public void testRegexAll() throws SolverException, InterruptedException {
    RegexFormula regex = smgr.all();
    assertThatFormula(smgr.in(hello, regex)).isSatisfiable();
  }

  @Test
  public void testRegexAll3() throws SolverException, InterruptedException {
    // This is not ALL_CHAR! This matches ".*" literally!
    RegexFormula regex = smgr.makeRegex(".*");
    assertThatFormula(smgr.in(hello, regex)).isUnsatisfiable();
    assertThatFormula(smgr.in(smgr.makeString(".*"), regex)).isSatisfiable();
  }

  @Test
  public void testStringRegex2() throws SolverException, InterruptedException {
    RegexFormula regex = smgr.concat(smgr.closure(a2z), smgr.makeRegex("ll"), smgr.closure(a2z));
    assertThatFormula(smgr.in(hello, regex)).isSatisfiable();
  }

  @Test
  public void testStringRegex3() throws SolverException, InterruptedException {
    RegexFormula regex = smgr.makeRegex(".*ll.*");
    assertThatFormula(smgr.in(hello, regex)).isUnsatisfiable();
  }

  @Test
  public void testEmptyRegex() throws SolverException, InterruptedException {
    RegexFormula regex = smgr.none();
    assertThatFormula(smgr.in(hello, regex)).isUnsatisfiable();
  }

  @Test
  public void testStringConcat() throws SolverException, InterruptedException {
    StringFormula str1 = smgr.makeString("hello");
    StringFormula str2 = smgr.makeString("world");
    StringFormula concat = smgr.concat(str1, str2);
    StringFormula complete = smgr.makeString("helloworld");

    assertEqual(concat, complete);
  }

  @Test
  public void testStringConcatEmpty() throws SolverException, InterruptedException {
    StringFormula empty = smgr.makeString("");

    assertEqual(empty, smgr.concat(ImmutableList.of()));
    assertEqual(empty, smgr.concat(empty));
    assertEqual(empty, smgr.concat(empty, empty));
    assertEqual(empty, smgr.concat(ImmutableList.of(empty, empty, empty, empty)));
  }

  @Test
  public void testStringPrefixSuffixConcat() throws SolverException, InterruptedException {
    // check whether "prefix + suffix == concat"
    StringFormula prefix = smgr.makeVariable("prefix");
    StringFormula suffix = smgr.makeVariable("suffix");
    StringFormula concat = smgr.makeVariable("concat");

    assertThatFormula(
            bmgr.and(
                smgr.prefix(prefix, concat),
                smgr.suffix(suffix, concat),
                imgr.equal(
                    smgr.length(concat), imgr.add(smgr.length(prefix), smgr.length(suffix)))))
        .implies(smgr.equal(concat, smgr.concat(prefix, suffix)));
  }

  @Test
  public void testStringPrefixSuffix() throws SolverException, InterruptedException {
    // check whether "prefix == suffix iff equal length"
    StringFormula prefix = smgr.makeVariable("prefix");
    StringFormula suffix = smgr.makeVariable("suffix");

    assertThatFormula(bmgr.and(smgr.prefix(prefix, suffix), smgr.suffix(suffix, prefix)))
        .implies(smgr.equal(prefix, suffix));
    assertThatFormula(
            bmgr.and(
                smgr.prefix(prefix, suffix), imgr.equal(smgr.length(prefix), smgr.length(suffix))))
        .implies(smgr.equal(prefix, suffix));
    assertThatFormula(
            bmgr.and(
                smgr.suffix(suffix, prefix), imgr.equal(smgr.length(prefix), smgr.length(suffix))))
        .implies(smgr.equal(prefix, suffix));
  }

  @Test
  public void testStringToIntConversion() throws SolverException, InterruptedException {
    IntegerFormula ten = imgr.makeNumber(10);
    StringFormula zeroStr = smgr.makeString("0");

    for (int i = 0; i < 100; i += 7) {
      StringFormula str = smgr.makeString(Integer.toString(i));
      IntegerFormula num = imgr.makeNumber(i);
      IntegerFormula numInc = imgr.makeNumber(i + 1);

      assertEqual(str, smgr.toStringFormula(num));
      assertDistinct(str, smgr.toStringFormula(numInc));
      assertDistinct(str, smgr.toStringFormula(imgr.add(num, numInc)));

      assertEqual(num, smgr.toIntegerFormula(str));
      assertDistinct(numInc, smgr.toIntegerFormula(str));
      assertEqual(imgr.multiply(num, ten), smgr.toIntegerFormula(smgr.concat(str, zeroStr)));
      assertDistinct(imgr.multiply(numInc, ten), smgr.toIntegerFormula(smgr.concat(str, zeroStr)));

      assertEqual(num, smgr.toIntegerFormula(smgr.toStringFormula(num)));
      assertEqual(numInc, smgr.toIntegerFormula(smgr.toStringFormula(numInc)));
    }
  }

  @Test
  public void testStringToIntConversionCornerCases() throws SolverException, InterruptedException {
    assertEqual(imgr.makeNumber(-1), smgr.toIntegerFormula(smgr.makeString("-1")));
    assertEqual(imgr.makeNumber(-1), smgr.toIntegerFormula(smgr.makeString("-12")));
    assertEqual(imgr.makeNumber(-1), smgr.toIntegerFormula(smgr.makeString("-123")));
    assertEqual(imgr.makeNumber(-1), smgr.toIntegerFormula(smgr.makeString("-1234")));

    assertDistinct(imgr.makeNumber(-12), smgr.toIntegerFormula(smgr.makeString("-1")));
    assertDistinct(imgr.makeNumber(-12), smgr.toIntegerFormula(smgr.makeString("-12")));
    assertDistinct(imgr.makeNumber(-12), smgr.toIntegerFormula(smgr.makeString("-123")));
    assertDistinct(imgr.makeNumber(-12), smgr.toIntegerFormula(smgr.makeString("-1234")));

    assertEqual(imgr.makeNumber(-1), smgr.toIntegerFormula(smgr.makeString("")));
    assertEqual(imgr.makeNumber(-1), smgr.toIntegerFormula(smgr.makeString("a")));
    assertEqual(imgr.makeNumber(-1), smgr.toIntegerFormula(smgr.makeString("1a")));
    assertEqual(imgr.makeNumber(-1), smgr.toIntegerFormula(smgr.makeString("a1")));

    assertDistinct(imgr.makeNumber(-12), smgr.toIntegerFormula(smgr.makeString("")));
    assertDistinct(imgr.makeNumber(-12), smgr.toIntegerFormula(smgr.makeString("a")));
    assertDistinct(imgr.makeNumber(-12), smgr.toIntegerFormula(smgr.makeString("1a")));
    assertDistinct(imgr.makeNumber(-12), smgr.toIntegerFormula(smgr.makeString("a1")));
  }

  @Test
  public void testIntToStringConversionCornerCases() throws SolverException, InterruptedException {
    assertEqual(smgr.makeString("123"), smgr.toStringFormula(imgr.makeNumber(123)));
    assertEqual(smgr.makeString("1"), smgr.toStringFormula(imgr.makeNumber(1)));
    assertEqual(smgr.makeString("0"), smgr.toStringFormula(imgr.makeNumber(0)));
    assertEqual(smgr.makeString(""), smgr.toStringFormula(imgr.makeNumber(-1)));
    assertEqual(smgr.makeString(""), smgr.toStringFormula(imgr.makeNumber(-123)));

    assertDistinct(smgr.makeString("1"), smgr.toStringFormula(imgr.makeNumber(-1)));
  }

  @Test
  public void testStringLength() throws SolverException, InterruptedException {
    assertEqual(imgr.makeNumber(0), smgr.length(smgr.makeString("")));
    assertEqual(imgr.makeNumber(1), smgr.length(smgr.makeString("a")));
    assertEqual(imgr.makeNumber(2), smgr.length(smgr.makeString("aa")));
    assertEqual(imgr.makeNumber(9), smgr.length(smgr.makeString("aaabbbccc")));

    assertDistinct(imgr.makeNumber(5), smgr.length(smgr.makeString("")));
    assertDistinct(imgr.makeNumber(5), smgr.length(smgr.makeString("a")));
    assertDistinct(imgr.makeNumber(5), smgr.length(smgr.makeString("aa")));
    assertDistinct(imgr.makeNumber(5), smgr.length(smgr.makeString("aaabbbcc")));
  }

  @Test
  public void testStringLengthWithVariable() throws SolverException, InterruptedException {
    StringFormula var = smgr.makeVariable("var");

    assertThatFormula(imgr.equal(imgr.makeNumber(0), smgr.length(var)))
        .implies(smgr.equal(var, smgr.makeString("")));

    assertThatFormula(
            bmgr.and(
                imgr.equal(imgr.makeNumber(5), smgr.length(var)),
                smgr.prefix(smgr.makeString("aba"), var),
                smgr.suffix(smgr.makeString("aba"), var)))
        .implies(smgr.equal(smgr.makeVariable("var"), smgr.makeString("ababa")));

    assertThatFormula(
            bmgr.and(
                imgr.equal(imgr.makeNumber(4), smgr.length(var)),
                smgr.prefix(smgr.makeString("aba"), var),
                smgr.suffix(smgr.makeString("aba"), var)))
        .isUnsatisfiable();

    assertThatFormula(
            bmgr.and(
                imgr.equal(imgr.makeNumber(4), smgr.length(var)),
                smgr.prefix(smgr.makeString("ab"), var),
                smgr.suffix(smgr.makeString("ba"), var),
                smgr.equal(smgr.makeString("c"), smgr.charAt(var, imgr.makeNumber(3)))))
        .isUnsatisfiable();

    assertThatFormula(
            bmgr.and(
                imgr.equal(imgr.makeNumber(5), smgr.length(var)),
                smgr.prefix(smgr.makeString("ab"), var),
                smgr.suffix(smgr.makeString("ba"), var),
                smgr.equal(smgr.makeString("c"), smgr.charAt(var, imgr.makeNumber(3)))))
        .implies(smgr.equal(smgr.makeVariable("var"), smgr.makeString("abcba")));
  }

  @Test
  public void testStringLengthPositiv() throws SolverException, InterruptedException {
    assertThatFormula(imgr.lessOrEquals(imgr.makeNumber(0), smgr.length(smgr.makeVariable("x"))))
        .isTautological();
    assertThatFormula(imgr.greaterThan(imgr.makeNumber(0), smgr.length(smgr.makeVariable("x"))))
        .isUnsatisfiable();
  }

  @Test
  public void testStringCompare() throws SolverException, InterruptedException {
    StringFormula var1 = smgr.makeVariable("0");
    StringFormula var2 = smgr.makeVariable("1");
    assertThatFormula(bmgr.and(smgr.lessOrEquals(var1, var2), smgr.greaterOrEquals(var1, var2)))
        .implies(smgr.equal(var1, var2));
    assertThatFormula(smgr.equal(var1, var2))
        .implies(bmgr.and(smgr.lessOrEquals(var1, var2), smgr.greaterOrEquals(var1, var2)));
  }

  /** Test const Strings = String variables + prefix and suffix constraints. */
  @Test
  public void testConstStringEqStringVar() throws SolverException, InterruptedException {
    String string1 = "";
    String string2 = "a";
    String string3 = "ab";
    String string4 = "abcdefghijklmnopqrstuvwxyz";
    StringFormula string1c = smgr.makeString(string1);
    StringFormula string2c = smgr.makeString(string2);
    StringFormula string3c = smgr.makeString(string3);
    StringFormula string4c = smgr.makeString(string4);
    StringFormula string1v = smgr.makeVariable("string1v");
    StringFormula string2v = smgr.makeVariable("string1v");
    StringFormula string3v = smgr.makeVariable("string1v");
    StringFormula string4v = smgr.makeVariable("string1v");

    BooleanFormula formula =
        bmgr.and(
            smgr.equal(string1c, string1v),
            smgr.equal(string2c, string2v),
            smgr.equal(string3c, string3v),
            smgr.equal(string4c, string4v));

    BooleanFormula string1PrefixFormula =
        bmgr.and(
            smgr.prefix(string1c, string1v),
            bmgr.not(smgr.prefix(string2c, string1v)),
            bmgr.not(smgr.prefix(string3c, string1v)),
            bmgr.not(smgr.prefix(string4c, string1v)));

    BooleanFormula string2PrefixFormula =
        bmgr.and(
            smgr.prefix(string1c, string2v),
            smgr.prefix(string2c, string2v),
            bmgr.not(smgr.prefix(string3c, string2v)),
            bmgr.not(smgr.prefix(string4c, string2v)));

    BooleanFormula string3PrefixFormula =
        bmgr.and(
            smgr.prefix(string1c, string3v),
            smgr.prefix(string2c, string3v),
            smgr.prefix(string3c, string3v),
            bmgr.not(smgr.prefix(string4c, string3v)));

    BooleanFormula string4PrefixFormula =
        bmgr.and(
            smgr.prefix(string1c, string4v),
            smgr.prefix(string2c, string4v),
            smgr.prefix(string3c, string4v),
            smgr.prefix(string4c, string4v));

    BooleanFormula string1SuffixFormula =
        bmgr.and(
            smgr.suffix(string1c, string1v),
            bmgr.not(smgr.suffix(string2c, string1v)),
            bmgr.not(smgr.suffix(string3c, string1v)),
            bmgr.not(smgr.suffix(string4c, string1v)));

    BooleanFormula string2SuffixFormula =
        bmgr.and(
            smgr.suffix(string1c, string2v),
            bmgr.not(smgr.suffix(string3c, string2v)),
            bmgr.not(smgr.suffix(string4c, string2v)));

    BooleanFormula string3SuffixFormula =
        bmgr.and(
            smgr.suffix(string1c, string3v),
            smgr.suffix(string3c, string3v),
            bmgr.not(smgr.suffix(string4c, string3v)));

    BooleanFormula string4SuffixFormula =
        bmgr.and(smgr.suffix(string1c, string4v), smgr.suffix(string4c, string4v));

    assertThatFormula(bmgr.and(formula))
        .implies(
            bmgr.and(
                string1PrefixFormula,
                string2PrefixFormula,
                string3PrefixFormula,
                string4PrefixFormula,
                string1SuffixFormula,
                string2SuffixFormula,
                string3SuffixFormula,
                string4SuffixFormula));
  }

  /** Test String variables with negative length (UNSAT). */
  @Test
  public void testStringVariableLengthNegative() throws SolverException, InterruptedException {
    StringFormula stringVariable1 = smgr.makeVariable("zeroLength");
    StringFormula stringVariable2 = smgr.makeVariable("negLength");

    // SAT + UNSAT Formula -> UNSAT
    assertThatFormula(
            bmgr.and(
                imgr.equal(smgr.length(stringVariable1), imgr.makeNumber(0)),
                imgr.equal(smgr.length(stringVariable2), imgr.makeNumber(-100))))
        .isUnsatisfiable();
    // UNSAT below
    assertThatFormula(imgr.equal(smgr.length(stringVariable2), imgr.makeNumber(-1)))
        .isUnsatisfiable();
    assertThatFormula(imgr.equal(smgr.length(stringVariable2), imgr.makeNumber(-100)))
        .isUnsatisfiable();
  }

  /**
   * Test String formulas with inequalities in the negative range.
   *
   * <p>-10000 < stringVariable length < 0 -> UNSAT
   *
   * <p>-10000 < stringVariable length < -1 -> UNSAT
   *
   * <p>-10000 <= stringVariable length <= -1 -> UNSAT
   *
   * <p>-10000 <= stringVariable length <= 0 AND stringVariable != "" -> UNSAT
   *
   * <p>-10000 <= stringVariable length <= 0 -> SAT implies stringVariable = ""
   */
  @Test
  public void testStringLengthInequalityNegativeRange()
      throws SolverException, InterruptedException {
    StringFormula stringVariable = smgr.makeVariable("stringVariable");
    IntegerFormula stringVariableLength = smgr.length(stringVariable);
    IntegerFormula minusTenThousand = imgr.makeNumber(-10000);
    IntegerFormula minusOne = imgr.makeNumber(-1);
    IntegerFormula zero = imgr.makeNumber(0);

    // -10000 < stringVariable length < 0 -> UNSAT
    assertThatFormula(
            bmgr.and(
                imgr.lessThan(minusTenThousand, stringVariableLength),
                imgr.lessThan(stringVariableLength, zero)))
        .isUnsatisfiable();
    // -10000 < stringVariable length < -1 -> UNSAT
    assertThatFormula(
            bmgr.and(
                imgr.lessThan(minusTenThousand, stringVariableLength),
                imgr.lessThan(stringVariableLength, minusOne)))
        .isUnsatisfiable();
    // -10000 <= stringVariable length <= -1 -> UNSAT
    assertThatFormula(
            bmgr.and(
                imgr.lessOrEquals(minusTenThousand, stringVariableLength),
                imgr.lessOrEquals(stringVariableLength, minusOne)))
        .isUnsatisfiable();
    // -10000 <= stringVariable length <= 0 AND stringVariable != "" -> UNSAT
    assertThatFormula(
            bmgr.and(
                imgr.lessOrEquals(minusTenThousand, stringVariableLength),
                imgr.lessOrEquals(stringVariableLength, zero),
                bmgr.not(smgr.equal(stringVariable, smgr.makeString("")))))
        .isUnsatisfiable();
    // -10000 <= stringVariable length <= 0 -> SAT implies stringVariable = ""
    assertThatFormula(
            bmgr.and(
                imgr.lessOrEquals(minusTenThousand, stringVariableLength),
                imgr.lessOrEquals(stringVariableLength, zero)))
        .implies(smgr.equal(stringVariable, smgr.makeString("")));
  }

  /**
   * Test String formulas with inequalities in the negative range.
   *
   * <p>0 < stringVariable length < 1 -> UNSAT
   *
   * <p>0 < stringVariable length < 2 -> SAT
   *
   * <p>0 <= stringVariable length < 1 -> SAT implies stringVariable = ""
   *
   * <p>1 < stringVariable length < 3 -> SAT implies stringVariable length = 2
   */
  @Test
  public void testStringLengthInequalityPositiveRange()
      throws SolverException, InterruptedException {
    StringFormula stringVariable = smgr.makeVariable("stringVariable");
    IntegerFormula stringVariableLength = smgr.length(stringVariable);
    IntegerFormula three = imgr.makeNumber(3);
    IntegerFormula two = imgr.makeNumber(2);
    IntegerFormula one = imgr.makeNumber(1);
    IntegerFormula zero = imgr.makeNumber(0);

    // 0 < stringVariable length < 1 -> UNSAT
    assertThatFormula(
            bmgr.and(
                imgr.lessThan(zero, stringVariableLength),
                imgr.lessThan(stringVariableLength, one)))
        .isUnsatisfiable();
    // 0 < stringVariable length < 2 -> SAT
    assertThatFormula(
            bmgr.and(
                imgr.lessThan(zero, stringVariableLength),
                imgr.lessThan(stringVariableLength, two)))
        .isSatisfiable();
    // 0 <= stringVariable length < 1 -> SAT implies stringVariable = ""
    assertThatFormula(
            bmgr.and(
                imgr.lessOrEquals(zero, stringVariableLength),
                imgr.lessThan(stringVariableLength, one)))
        .implies(smgr.equal(stringVariable, smgr.makeString("")));
    // 1 < stringVariable length < 3 -> SAT implies stringVariable length = 2
    assertThatFormula(
            bmgr.and(
                imgr.lessThan(one, stringVariableLength),
                imgr.lessThan(stringVariableLength, three)))
        .implies(imgr.equal(smgr.length(stringVariable), two));
  }

  /** Test simple String lexicographic ordering (< <= > >=) for constant Strings. */
  @Test
  public void testSimpleConstStringLexicographicOrdering()
      throws SolverException, InterruptedException {

    String[] ws = {
      "",
      "0",
      "1",
      "10",
      "a",
      "b",
      "A",
      "B",
      "aa",
      "Aa",
      "aA",
      "AA",
      "ab",
      "aB",
      "Ab",
      "AB",
      "ac",
      "bb",
      "aaa",
      "Aaa",
      "aAa",
      "aAA",
      "aab",
      "aaabbb",
      "bbbccc",
      "abcde",
      "abdde",
      "abcdf",
      "abchurrdurr",
      "abcdefaaaaa",
    };
    Arrays.sort(ws); // lexicographic ordering
    List<StringFormula> words = FluentIterable.from(ws).transform(w -> smgr.makeString(w)).toList();

    for (int i = 1; i < words.size(); i++) {
      StringFormula word1 = words.get(i - 1);
      StringFormula word2 = words.get(i);

      assertThatFormula(smgr.lessThan(word1, word1)).isUnsatisfiable();
      assertThatFormula(smgr.lessOrEquals(word1, word1)).isSatisfiable();
      assertThatFormula(smgr.greaterOrEquals(word1, word1)).isSatisfiable();

      assertThatFormula(smgr.lessThan(word1, word2)).isSatisfiable();
      assertThatFormula(smgr.lessOrEquals(word1, word2)).isSatisfiable();
      assertThatFormula(smgr.greaterThan(word1, word2)).isUnsatisfiable();
      assertThatFormula(smgr.greaterOrEquals(word1, word2)).isUnsatisfiable();
    }
  }

  /** Test simple String lexicographic ordering (< <= > >=) for String variables. */
  @Test
  public void testSimpleStringVariableLexicographicOrdering()
      throws SolverException, InterruptedException {
    StringFormula a = smgr.makeString("a");
    StringFormula b = smgr.makeString("b");
    StringFormula ab = smgr.makeString("ab");
    StringFormula abc = smgr.makeString("abc");
    StringFormula abd = smgr.makeString("abd");
    StringFormula abe = smgr.makeString("abe");
    StringFormula abaab = smgr.makeString("abaab");
    StringFormula abbab = smgr.makeString("abbab");
    StringFormula abcab = smgr.makeString("abcab");
    StringFormula stringVariable = smgr.makeVariable("stringVariable");

    assertThatFormula(
            bmgr.and(
                smgr.lessThan(a, stringVariable),
                smgr.lessThan(stringVariable, b),
                imgr.equal(imgr.makeNumber(0), smgr.length(stringVariable))))
        .implies(smgr.equal(stringVariable, smgr.makeString("")));

    assertThatFormula(
            bmgr.and(
                smgr.lessOrEquals(a, stringVariable),
                smgr.lessThan(stringVariable, b),
                imgr.equal(imgr.makeNumber(1), smgr.length(stringVariable))))
        .implies(smgr.equal(stringVariable, smgr.makeString("a")));

    assertThatFormula(
            bmgr.and(
                smgr.lessThan(a, stringVariable),
                smgr.lessOrEquals(stringVariable, b),
                imgr.equal(imgr.makeNumber(1), smgr.length(stringVariable))))
        .implies(smgr.equal(stringVariable, b));

    assertThatFormula(
            bmgr.and(
                smgr.lessOrEquals(abc, stringVariable),
                smgr.lessThan(stringVariable, abd),
                imgr.equal(imgr.makeNumber(3), smgr.length(stringVariable))))
        .implies(smgr.equal(stringVariable, abc));

    assertThatFormula(
            bmgr.and(
                smgr.lessThan(abc, stringVariable),
                smgr.lessThan(stringVariable, abe),
                imgr.equal(imgr.makeNumber(3), smgr.length(stringVariable))))
        .implies(smgr.equal(stringVariable, abd));

    assertThatFormula(
            bmgr.and(
                smgr.lessThan(abc, stringVariable),
                smgr.lessOrEquals(stringVariable, abd),
                imgr.equal(imgr.makeNumber(3), smgr.length(stringVariable))))
        .implies(smgr.equal(stringVariable, abd));

    assertThatFormula(
            bmgr.and(
                smgr.lessThan(abaab, stringVariable),
                smgr.lessThan(stringVariable, abcab),
                smgr.prefix(ab, stringVariable),
                smgr.suffix(ab, stringVariable),
                imgr.equal(imgr.makeNumber(5), smgr.length(stringVariable))))
        .implies(smgr.equal(stringVariable, abbab));
  }

  /** Takeaway: invalid positions always refer to the empty string! */
  @Test
  public void testCharAtWithConstString() throws SolverException, InterruptedException {
    StringFormula empty = smgr.makeString("");
    StringFormula a = smgr.makeString("a");
    StringFormula b = smgr.makeString("b");
    StringFormula ab = smgr.makeString("ab");

    assertEqual(smgr.charAt(empty, imgr.makeNumber(1)), empty);
    assertEqual(smgr.charAt(empty, imgr.makeNumber(0)), empty);
    assertEqual(smgr.charAt(empty, imgr.makeNumber(-1)), empty);

    assertDistinct(smgr.charAt(a, imgr.makeNumber(-1)), a);
    assertEqual(smgr.charAt(a, imgr.makeNumber(-1)), empty);
    assertEqual(smgr.charAt(a, imgr.makeNumber(0)), a);
    assertDistinct(smgr.charAt(a, imgr.makeNumber(1)), a);
    assertEqual(smgr.charAt(a, imgr.makeNumber(1)), empty);
    assertDistinct(smgr.charAt(a, imgr.makeNumber(2)), a);
    assertEqual(smgr.charAt(a, imgr.makeNumber(2)), empty);

    assertEqual(smgr.charAt(ab, imgr.makeNumber(0)), a);
    assertEqual(smgr.charAt(ab, imgr.makeNumber(1)), b);
    assertDistinct(smgr.charAt(ab, imgr.makeNumber(0)), b);
    assertDistinct(smgr.charAt(ab, imgr.makeNumber(1)), a);
  }

  /**
   * Test escapecharacter treatment. Escape characters are treated as a single char! Example:
   * "a\u1234T" has "a" at position 0, "\u1234" at position 1 and "T" at position 2
   *
   * <p>SMTLIB2 uses an escape sequence for the numerals of the sort: {1234}.
   */
  @Test
  public void testCharAtWithSpecialCharacters() throws SolverException, InterruptedException {
    assume()
        .withMessage("Solver %s does only support 2 byte unicode", solverToUse())
        .that(solverToUse())
        .isNotEqualTo(Solvers.Z3);

    StringFormula num1 = smgr.makeString("1");
    StringFormula u = smgr.makeString("u");
    StringFormula curlyOpen = smgr.makeString("\u007B");
    StringFormula curlyClose = smgr.makeString("\u007D");
    StringFormula u1234WOEscape = smgr.makeString("u1234");
    StringFormula au1234WOEscape = smgr.makeString("au1234");
    // Java needs a double {{ as the first one is needed as an escape char for the second, this is a
    // workaround
    String workaround = "au\u007B1234\u007D";
    StringFormula au1234WOEscapeCurly = smgr.makeString(workaround);
    StringFormula backSlash = smgr.makeString("\\");
    StringFormula a = smgr.makeString("a");
    StringFormula b = smgr.makeString("b");
    StringFormula u1234 = smgr.makeString("\\u{1234}");
    StringFormula au1234b = smgr.makeString("a\\u{1234}b");
    StringFormula stringVariable = smgr.makeVariable("stringVariable");

    // Javas backslash (double written) is just 1 char
    assertThatFormula(imgr.equal(smgr.length(backSlash), imgr.makeNumber(1))).isSatisfiable();

    assertThatFormula(smgr.equal(smgr.charAt(au1234b, imgr.makeNumber(0)), stringVariable))
        .implies(smgr.equal(stringVariable, a));

    // It seems like CVC4 sees the backslash as its own char!
    assertThatFormula(smgr.equal(smgr.charAt(au1234b, imgr.makeNumber(1)), stringVariable))
        .implies(smgr.equal(stringVariable, u1234));

    assertThatFormula(smgr.equal(smgr.charAt(au1234b, imgr.makeNumber(2)), stringVariable))
        .implies(smgr.equal(stringVariable, b));

    assertThatFormula(
            bmgr.and(
                smgr.equal(smgr.charAt(u1234WOEscape, imgr.makeNumber(0)), u),
                smgr.equal(smgr.charAt(u1234WOEscape, imgr.makeNumber(1)), num1)))
        .isSatisfiable();

    assertThatFormula(
            bmgr.and(
                smgr.equal(smgr.charAt(au1234WOEscape, imgr.makeNumber(0)), a),
                smgr.equal(smgr.charAt(au1234WOEscape, imgr.makeNumber(1)), u),
                smgr.equal(smgr.charAt(au1234WOEscape, imgr.makeNumber(2)), num1)))
        .isSatisfiable();

    assertThatFormula(
            bmgr.and(
                smgr.equal(smgr.charAt(au1234WOEscapeCurly, imgr.makeNumber(0)), a),
                smgr.equal(smgr.charAt(au1234WOEscapeCurly, imgr.makeNumber(1)), u),
                smgr.equal(smgr.charAt(au1234WOEscapeCurly, imgr.makeNumber(2)), curlyOpen),
                smgr.equal(smgr.charAt(au1234WOEscapeCurly, imgr.makeNumber(7)), curlyClose)))
        .isSatisfiable();

    // Check that the unicode is not treated as seperate chars
    assertThatFormula(
            bmgr.and(
                smgr.equal(smgr.charAt(u1234, imgr.makeNumber(0)), smgr.makeString("\\")),
                smgr.equal(smgr.charAt(u1234, imgr.makeNumber(1)), u),
                smgr.equal(smgr.charAt(u1234, imgr.makeNumber(2)), num1)))
        .isUnsatisfiable();
  }

  /**
   * Same as {@link #testCharAtWithSpecialCharacters} but only with 2 Byte special chars as Z3 only
   * supports those.
   */
  @Test
  public void testCharAtWithSpecialCharacters2Byte() throws SolverException, InterruptedException {
    StringFormula num7 = smgr.makeString("7");
    StringFormula u = smgr.makeString("u");
    StringFormula curlyOpen2BUnicode = smgr.makeString("\\u{7B}");
    StringFormula curlyClose2BUnicode = smgr.makeString("\\u{7D}");
    StringFormula acurlyClose2BUnicodeb = smgr.makeString("a\\u{7D}b");
    // Java needs a double {{ as the first one is needed as a escape char for the second, this is a
    // workaround
    String workaround = "au\u007B7B\u007D";
    StringFormula acurlyOpen2BUnicodeWOEscapeCurly = smgr.makeString(workaround);
    // StringFormula backSlash = smgr.makeString("\\");
    StringFormula a = smgr.makeString("a");
    StringFormula b = smgr.makeString("b");
    StringFormula stringVariable = smgr.makeVariable("stringVariable");

    // Curly braces unicode is treated as 1 char
    assertThatFormula(imgr.equal(smgr.length(curlyOpen2BUnicode), imgr.makeNumber(1)))
        .isSatisfiable();
    assertThatFormula(imgr.equal(smgr.length(curlyClose2BUnicode), imgr.makeNumber(1)))
        .isSatisfiable();

    // check a}b
    assertThatFormula(
            smgr.equal(smgr.charAt(acurlyClose2BUnicodeb, imgr.makeNumber(0)), stringVariable))
        .implies(smgr.equal(stringVariable, a));

    assertThatFormula(
            smgr.equal(smgr.charAt(acurlyClose2BUnicodeb, imgr.makeNumber(1)), stringVariable))
        .implies(smgr.equal(stringVariable, curlyClose2BUnicode));

    assertThatFormula(
            smgr.equal(smgr.charAt(acurlyClose2BUnicodeb, imgr.makeNumber(2)), stringVariable))
        .implies(smgr.equal(stringVariable, b));

    // Check the unescaped version (missing backslash)
    assertThatFormula(
            bmgr.and(
                smgr.equal(smgr.charAt(acurlyOpen2BUnicodeWOEscapeCurly, imgr.makeNumber(0)), a),
                smgr.equal(smgr.charAt(acurlyOpen2BUnicodeWOEscapeCurly, imgr.makeNumber(1)), u),
                smgr.equal(
                    smgr.charAt(acurlyOpen2BUnicodeWOEscapeCurly, imgr.makeNumber(2)),
                    curlyOpen2BUnicode),
                smgr.equal(smgr.charAt(acurlyOpen2BUnicodeWOEscapeCurly, imgr.makeNumber(3)), num7),
                smgr.equal(
                    smgr.charAt(acurlyOpen2BUnicodeWOEscapeCurly, imgr.makeNumber(4)),
                    smgr.makeString("B")),
                smgr.equal(
                    smgr.charAt(acurlyOpen2BUnicodeWOEscapeCurly, imgr.makeNumber(5)),
                    curlyClose2BUnicode)))
        .isSatisfiable();
  }

  @Test
  public void testCharAtWithStringVariable() throws SolverException, InterruptedException {
    StringFormula a = smgr.makeString("a");
    StringFormula b = smgr.makeString("b");
    StringFormula ab = smgr.makeString("ab");
    StringFormula aa = smgr.makeString("aa");
    StringFormula abc = smgr.makeString("abc");
    StringFormula aabc = smgr.makeString("aabc");
    StringFormula abcb = smgr.makeString("abcb");
    StringFormula stringVariable = smgr.makeVariable("stringVariable");

    assertThatFormula(smgr.equal(smgr.charAt(ab, imgr.makeNumber(0)), stringVariable))
        .implies(smgr.equal(stringVariable, a));

    assertThatFormula(smgr.equal(smgr.charAt(ab, imgr.makeNumber(1)), stringVariable))
        .implies(smgr.equal(stringVariable, b));

    assertThatFormula(
            bmgr.and(
                smgr.equal(smgr.charAt(ab, imgr.makeNumber(0)), stringVariable),
                smgr.equal(smgr.charAt(ab, imgr.makeNumber(1)), stringVariable)))
        .isUnsatisfiable();

    assertThatFormula(
            bmgr.and(
                smgr.equal(smgr.charAt(aa, imgr.makeNumber(0)), stringVariable),
                smgr.equal(smgr.charAt(aa, imgr.makeNumber(1)), stringVariable)))
        .implies(smgr.equal(stringVariable, a));

    assertThatFormula(
            bmgr.and(
                smgr.equal(smgr.charAt(stringVariable, imgr.makeNumber(0)), a),
                smgr.equal(smgr.charAt(stringVariable, imgr.makeNumber(1)), b),
                imgr.equal(imgr.makeNumber(2), smgr.length(stringVariable))))
        .implies(smgr.equal(stringVariable, ab));

    assertThatFormula(
            bmgr.and(
                smgr.equal(smgr.charAt(stringVariable, imgr.makeNumber(0)), a),
                smgr.equal(smgr.charAt(stringVariable, imgr.makeNumber(2)), b),
                imgr.equal(imgr.makeNumber(4), smgr.length(stringVariable)),
                smgr.suffix(abc, stringVariable)))
        .implies(smgr.equal(stringVariable, aabc));

    assertThatFormula(
            bmgr.and(
                smgr.equal(smgr.charAt(stringVariable, imgr.makeNumber(0)), a),
                smgr.equal(smgr.charAt(stringVariable, imgr.makeNumber(3)), b),
                imgr.equal(imgr.makeNumber(4), smgr.length(stringVariable)),
                smgr.suffix(abc, stringVariable)))
        .implies(smgr.equal(stringVariable, abcb));

    assertThatFormula(
            bmgr.and(
                smgr.equal(smgr.charAt(stringVariable, imgr.makeNumber(0)), a),
                smgr.equal(smgr.charAt(stringVariable, imgr.makeNumber(3)), b),
                imgr.equal(imgr.makeNumber(4), smgr.length(stringVariable)),
                smgr.prefix(abc, stringVariable)))
        .implies(smgr.equal(stringVariable, abcb));
  }

  @Test
  public void testConstStringContains() throws SolverException, InterruptedException {
    StringFormula empty = smgr.makeString("");
    StringFormula a = smgr.makeString("a");
    StringFormula aUppercase = smgr.makeString("A");
    StringFormula bUppercase = smgr.makeString("B");
    StringFormula b = smgr.makeString("b");
    StringFormula bbbbbb = smgr.makeString("bbbbbb");
    StringFormula bbbbbbb = smgr.makeString("bbbbbbb");
    StringFormula abbbbbb = smgr.makeString("abbbbbb");
    StringFormula aaaaaaaB = smgr.makeString("aaaaaaaB");
    StringFormula abcAndSoOn = smgr.makeString("abcdefghijklmnopqrstuVwxyz");
    StringFormula curlyOpen2BUnicode = smgr.makeString("\\u{7B}");
    StringFormula curlyClose2BUnicode = smgr.makeString("\\u{7D}");
    StringFormula multipleCurlys2BUnicode = smgr.makeString("\\u{7B}\\u{7D}\\u{7B}\\u{7B}");
    StringFormula curlyClose2BUnicodeEncased = smgr.makeString("blabla\\u{7D}bla");

    assertThatFormula(smgr.contains(empty, empty)).isSatisfiable();
    assertThatFormula(smgr.contains(empty, a)).isUnsatisfiable();
    assertThatFormula(smgr.contains(a, empty)).isSatisfiable();
    assertThatFormula(smgr.contains(a, a)).isSatisfiable();
    assertThatFormula(smgr.contains(a, aUppercase)).isUnsatisfiable();
    assertThatFormula(smgr.contains(aUppercase, a)).isUnsatisfiable();
    assertThatFormula(smgr.contains(a, b)).isUnsatisfiable();
    assertThatFormula(smgr.contains(b, b)).isSatisfiable();
    assertThatFormula(smgr.contains(abbbbbb, a)).isSatisfiable();
    assertThatFormula(smgr.contains(abbbbbb, b)).isSatisfiable();
    assertThatFormula(smgr.contains(abbbbbb, bbbbbb)).isSatisfiable();
    assertThatFormula(smgr.contains(abbbbbb, bbbbbbb)).isUnsatisfiable();
    assertThatFormula(smgr.contains(abbbbbb, aUppercase)).isUnsatisfiable();
    assertThatFormula(smgr.contains(abbbbbb, aUppercase)).isUnsatisfiable();
    assertThatFormula(smgr.contains(aaaaaaaB, a)).isSatisfiable();
    assertThatFormula(smgr.contains(aaaaaaaB, b)).isUnsatisfiable();
    assertThatFormula(smgr.contains(aaaaaaaB, bUppercase)).isSatisfiable();
    assertThatFormula(smgr.contains(aaaaaaaB, curlyOpen2BUnicode)).isUnsatisfiable();
    assertThatFormula(smgr.contains(abcAndSoOn, smgr.makeString("xyz"))).isSatisfiable();
    assertThatFormula(smgr.contains(abcAndSoOn, smgr.makeString("Vwxyz"))).isSatisfiable();
    assertThatFormula(smgr.contains(abcAndSoOn, smgr.makeString("Vwxyza"))).isUnsatisfiable();
    assertThatFormula(smgr.contains(abcAndSoOn, smgr.makeString("t Vwxyz"))).isUnsatisfiable();
    assertThatFormula(smgr.contains(multipleCurlys2BUnicode, curlyOpen2BUnicode)).isSatisfiable();
    assertThatFormula(smgr.contains(multipleCurlys2BUnicode, curlyClose2BUnicode)).isSatisfiable();
    assertThatFormula(smgr.contains(curlyClose2BUnicodeEncased, curlyClose2BUnicode))
        .isSatisfiable();
  }

  @Test
  public void testStringVariableContains() throws SolverException, InterruptedException {
    StringFormula var1 = smgr.makeVariable("var1");
    StringFormula var2 = smgr.makeVariable("var2");

    StringFormula empty = smgr.makeString("");
    StringFormula bUppercase = smgr.makeString("B");
    StringFormula ab = smgr.makeString("ab");
    StringFormula bbbbbb = smgr.makeString("bbbbbb");
    StringFormula abbbbbb = smgr.makeString("abbbbbb");
    StringFormula curlyOpen2BUnicode = smgr.makeString("\\u{7B}");
    StringFormula curlyClose2BUnicode = smgr.makeString("\\u{7D}");

    assertThatFormula(
            bmgr.and(smgr.contains(var1, empty), imgr.equal(imgr.makeNumber(0), smgr.length(var1))))
        .implies(smgr.equal(var1, empty));

    assertThatFormula(bmgr.and(smgr.contains(var1, var2), smgr.contains(var2, var1)))
        .implies(smgr.equal(var1, var2));

    // Unicode is treated as 1 char. So \\u{7B} is treated as { and the B inside is not contained!
    assertThatFormula(
            bmgr.and(
                smgr.contains(var1, curlyOpen2BUnicode),
                smgr.contains(var1, bUppercase),
                imgr.equal(imgr.makeNumber(1), smgr.length(var1))))
        .isUnsatisfiable();
    // Same goes for the curly brackets used as escape sequence
    assertThatFormula(
            bmgr.and(
                smgr.contains(var1, curlyOpen2BUnicode),
                smgr.contains(var1, curlyClose2BUnicode),
                imgr.equal(imgr.makeNumber(1), smgr.length(var1))))
        .isUnsatisfiable();

    assertThatFormula(
            bmgr.and(
                smgr.contains(var1, bbbbbb),
                smgr.contains(var1, ab),
                imgr.equal(imgr.makeNumber(7), smgr.length(var1))))
        .implies(smgr.equal(var1, abbbbbb));
  }

  @Test
  public void testStringContainsOtherVariable() throws SolverException, InterruptedException {
    assume()
        .withMessage("Solver %s runs endlessly on this task", solverToUse())
        .that(solverToUse())
        .isNotEqualTo(Solvers.Z3);

    StringFormula var1 = smgr.makeVariable("var1");
    StringFormula var2 = smgr.makeVariable("var2");

    StringFormula abUppercase = smgr.makeString("AB");
    StringFormula ab = smgr.makeString("ab");

    assertThatFormula(
            bmgr.and(
                smgr.contains(var1, ab),
                smgr.contains(var2, abUppercase),
                smgr.contains(var1, var2)))
        .implies(smgr.contains(var1, abUppercase));
  }

  @Test
  public void testConstStringIndexOf() throws SolverException, InterruptedException {
    StringFormula empty = smgr.makeString("");
    StringFormula a = smgr.makeString("a");
    StringFormula aUppercase = smgr.makeString("A");
    StringFormula b = smgr.makeString("b");
    StringFormula ab = smgr.makeString("ab");
    StringFormula bbbbbb = smgr.makeString("bbbbbb");
    StringFormula bbbbbbb = smgr.makeString("bbbbbbb");
    StringFormula abbbbbb = smgr.makeString("abbbbbb");
    StringFormula abcAndSoOn = smgr.makeString("abcdefghijklmnopqrstuVwxyz");
    StringFormula curlyOpen2BUnicode = smgr.makeString("\\u{7B}");
    StringFormula curlyClose2BUnicode = smgr.makeString("\\u{7D}");
    StringFormula multipleCurlys2BUnicode = smgr.makeString("\\u{7B}\\u{7D}\\u{7B}\\u{7B}");
    // Z3 transforms this into {}, but CVC4 does not! CVC4 is on the side of the SMTLIB2 standard as
    // far as i can see.
    StringFormula curlys2BUnicodeWOEscape = smgr.makeString("\\u7B\\u7D");

    IntegerFormula zero = imgr.makeNumber(0);

    assertEqual(smgr.indexOf(empty, empty, zero), zero);
    assertEqual(smgr.indexOf(a, empty, zero), zero);
    assertEqual(smgr.indexOf(a, a, zero), zero);
    assertEqual(smgr.indexOf(a, aUppercase, zero), imgr.makeNumber(-1));
    assertEqual(smgr.indexOf(abbbbbb, a, zero), zero);
    assertEqual(smgr.indexOf(abbbbbb, b, zero), imgr.makeNumber(1));
    assertEqual(smgr.indexOf(abbbbbb, ab, zero), zero);
    assertEqual(smgr.indexOf(abbbbbb, bbbbbb, zero), imgr.makeNumber(1));
    assertEqual(smgr.indexOf(abbbbbb, bbbbbbb, zero), imgr.makeNumber(-1));
    assertEqual(smgr.indexOf(abbbbbb, smgr.makeString("c"), zero), imgr.makeNumber(-1));
    assertEqual(smgr.indexOf(abcAndSoOn, smgr.makeString("z"), zero), imgr.makeNumber(25));
    assertEqual(smgr.indexOf(abcAndSoOn, smgr.makeString("V"), zero), imgr.makeNumber(21));
    assertEqual(smgr.indexOf(abcAndSoOn, smgr.makeString("v"), zero), imgr.makeNumber(-1));
    assertEqual(smgr.indexOf(multipleCurlys2BUnicode, curlyOpen2BUnicode, zero), zero);
    assertEqual(
        smgr.indexOf(multipleCurlys2BUnicode, curlyClose2BUnicode, zero), imgr.makeNumber(1));

    // TODO: Z3 and CVC4 handle this differently!
    // assertEqual(smgr.indexOf(multipleCurlys2BUnicode, curlys2BUnicodeWOEscape, zero), zero);

    assertEqual(
        smgr.indexOf(multipleCurlys2BUnicode, curlys2BUnicodeWOEscape, imgr.makeNumber(1)),
        imgr.makeNumber(-1));
    assertEqual(
        smgr.indexOf(multipleCurlys2BUnicode, smgr.makeString("B"), zero), imgr.makeNumber(-1));
  }

  @Test
  public void testStringVariableIndexOf() throws SolverException, InterruptedException {
    StringFormula var1 = smgr.makeVariable("var1");
    StringFormula var2 = smgr.makeVariable("var2");
    IntegerFormula intVar = imgr.makeVariable("intVar");

    StringFormula empty = smgr.makeString("");
    StringFormula curlyOpen2BUnicode = smgr.makeString("\\u{7B}");

    IntegerFormula zero = imgr.makeNumber(0);

    // If the index of var2 is not -1, it is contained in var1.
    assertThatFormula(
            bmgr.and(
                bmgr.not(imgr.equal(intVar, imgr.makeNumber(-1))),
                imgr.equal(intVar, smgr.indexOf(var1, var2, zero))))
        .implies(smgr.contains(var1, var2));

    // If the index is less than 0 (only -1 possible) it is not contained.
    assertThatFormula(
            bmgr.and(
                imgr.equal(intVar, smgr.indexOf(var1, var2, zero)), imgr.lessThan(intVar, zero)))
        .implies(bmgr.not(smgr.contains(var1, var2)));

    // If the index of var2 in var is >= 0 and vice versa, both contain each other.
    assertThatFormula(
            bmgr.and(
                imgr.greaterOrEquals(smgr.indexOf(var1, var2, zero), zero),
                imgr.greaterOrEquals(smgr.indexOf(var2, var1, zero), zero)))
        .implies(bmgr.and(smgr.contains(var1, var2), smgr.contains(var2, var1)));

    // If the are indices equal and one is >= 0 and the strings are not "", both are contained in
    // each other and the chars at the position must be the same.
    assertThatFormula(
            bmgr.and(
                imgr.equal(smgr.indexOf(var1, var2, zero), smgr.indexOf(var2, var1, zero)),
                imgr.greaterOrEquals(smgr.indexOf(var1, var2, zero), zero),
                bmgr.not(smgr.equal(empty, smgr.charAt(var1, smgr.indexOf(var1, var2, zero))))))
        .implies(
            bmgr.and(
                smgr.contains(var1, var2),
                smgr.contains(var2, var1),
                smgr.equal(
                    smgr.charAt(var1, smgr.indexOf(var2, var1, zero)),
                    smgr.charAt(var1, smgr.indexOf(var1, var2, zero)))));

    // If a String contains {, but not B, the index of B must be -1. (unicode of { contains B)
    assertThatFormula(
            bmgr.and(
                smgr.contains(var1, curlyOpen2BUnicode),
                bmgr.not(smgr.contains(var1, smgr.makeString("B")))))
        .implies(
            bmgr.and(
                imgr.greaterOrEquals(smgr.indexOf(var1, curlyOpen2BUnicode, zero), zero),
                imgr.equal(imgr.makeNumber(-1), smgr.indexOf(var1, smgr.makeString("B"), zero))));
  }

  @Test
  public void testStringIndexOfWithSubStrings() throws SolverException, InterruptedException {
    assume()
        .withMessage("Solver %s runs endlessly on this task", solverToUse())
        .that(solverToUse())
        .isNotEqualTo(Solvers.Z3);

    StringFormula var1 = smgr.makeVariable("var1");

    IntegerFormula zero = imgr.makeNumber(0);

    // If the index of the string abba is 0, the index of the string bba is 1, and b is 1, and ba is
    // 2
    assertThatFormula(imgr.equal(zero, smgr.indexOf(var1, smgr.makeString("abba"), zero)))
        .implies(
            bmgr.and(
                smgr.contains(var1, smgr.makeString("abba")),
                imgr.equal(imgr.makeNumber(1), smgr.indexOf(var1, smgr.makeString("bba"), zero)),
                imgr.equal(imgr.makeNumber(1), smgr.indexOf(var1, smgr.makeString("b"), zero)),
                imgr.equal(imgr.makeNumber(2), smgr.indexOf(var1, smgr.makeString("ba"), zero))));
  }

  @Test
  public void testStringPrefixImpliesPrefixIndexOf() throws SolverException, InterruptedException {
    assume()
        .withMessage("Solver %s runs endlessly on this task", solverToUse())
        .that(solverToUse())
        .isNoneOf(Solvers.Z3, Solvers.CVC4);

    StringFormula var1 = smgr.makeVariable("var1");
    StringFormula var2 = smgr.makeVariable("var2");

    IntegerFormula zero = imgr.makeNumber(0);

    // If a prefix (var2) is non empty, the length of the string (var1) has to be larger or equal to
    // the prefix
    // and the chars have to be the same for the lenth of the prefix, meaning the indexOf the prefix
    // must be 0 in the string.
    assertThatFormula(bmgr.and(imgr.greaterThan(smgr.length(var2), zero), smgr.prefix(var2, var1)))
        .implies(
            bmgr.and(
                smgr.contains(var1, var2),
                imgr.greaterOrEquals(smgr.length(var1), smgr.length(var2)),
                imgr.equal(zero, smgr.indexOf(var1, var2, zero))));
  }

  @SuppressWarnings("unused")
  @Test
  public void testSubString() throws SolverException, InterruptedException {
    StringFormula empty = smgr.makeString("");
    StringFormula a = smgr.makeString("a");
    StringFormula aUppercase = smgr.makeString("A");
    StringFormula bUppercase = smgr.makeString("B");
    StringFormula b = smgr.makeString("b");
    StringFormula bbbbbb = smgr.makeString("bbbbbb");
    StringFormula bbbbbbb = smgr.makeString("bbbbbbb");
    StringFormula abbbbbb = smgr.makeString("abbbbbb");
    StringFormula aaaaaaaB = smgr.makeString("aaaaaaaB");
    StringFormula abcAndSoOn = smgr.makeString("abcdefghijklmnopqrstuVwxyz");
    StringFormula curlyOpen2BUnicode = smgr.makeString("\\u{7B}");
    StringFormula curlyClose2BUnicode = smgr.makeString("\\u{7D}");
    StringFormula multipleCurlys2BUnicode = smgr.makeString("\\u{7B}\\u{7D}\\u{7B}\\u{7B}");
    StringFormula curlyClose2BUnicodeEncased = smgr.makeString("blabla\\u{7D}bla");
    StringFormula curlys2BUnicodeWOEscape = smgr.makeString("\\u007B\\u007D");

    IntegerFormula zero = imgr.makeNumber(0);
    IntegerFormula one = imgr.makeNumber(1);
    IntegerFormula two = imgr.makeNumber(2);

    assertEqual(smgr.substring(empty, zero, zero), empty);
    // TODO: more
  }

  @Test
  public void testStringSubstringOutOfBounds() throws SolverException, InterruptedException {
    StringFormula bbbbbb = smgr.makeString("bbbbbb");
    StringFormula abbbbbb = smgr.makeString("abbbbbb");

    StringFormula multipleCurlys2BUnicode = smgr.makeString("\\u{7B}\\u{7D}\\u{7B}\\u{7B}");
    StringFormula multipleCurlys2BUnicodeFromIndex1 = smgr.makeString("\\u{7D}\\u{7B}\\u{7B}");

    assertEqual(smgr.substring(abbbbbb, imgr.makeNumber(1), imgr.makeNumber(10000)), bbbbbb);
    assertEqual(
        smgr.substring(multipleCurlys2BUnicode, imgr.makeNumber(1), imgr.makeNumber(10000)),
        multipleCurlys2BUnicodeFromIndex1);
  }

  @Test
  public void testStringReplace() {
    // TODO
  }

  @Test
  public void testStringReplaceAll() {
    // TODO
  }

  @Test
  public void testStringConcatWUnicode() throws SolverException, InterruptedException {
    StringFormula backslash = smgr.makeString("\\");
    StringFormula u = smgr.makeString("u");
    StringFormula curlyOpen = smgr.makeString("\\u{7B}");
    StringFormula sevenB = smgr.makeString("7B");
    StringFormula curlyClose = smgr.makeString("\\u{7D}");
    StringFormula concat = smgr.concat(backslash, u, curlyOpen, sevenB, curlyClose);
    StringFormula complete = smgr.makeString("\\u{7B}");

    // Concatting parts of unicode does not result in the unicode char!
    assertDistinct(concat, complete);
  }

  @Test
  public void testStringSimpleRegex() {
    // TODO
  }
}
