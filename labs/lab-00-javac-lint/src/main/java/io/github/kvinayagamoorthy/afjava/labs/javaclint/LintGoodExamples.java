package io.github.kvinayagamoorthy.afjava.labs.javaclint;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/** Corrected versions of {@link LintBadExamples}; compiles with zero -Xlint:all warnings. */
public class LintGoodExamples {

  /** Parameterised type: adding 42 is now a compile error, not a runtime surprise. */
  public List<String> rawTypes() {
    List<String> names = new ArrayList<>();
    names.add("cn=admin");
    return names;
  }

  /** Arrow-form switch: no fallthrough possible; switch is an expression. */
  public int fallthrough(int scope) {
    return switch (scope) {
      case 0 -> 11;
      case 1 -> 10;
      default -> 100;
    };
  }

  /** Factory method uses the Integer cache instead of the removed constructor. */
  public Integer deprecated() {
    return Integer.valueOf(7);
  }

  /** No cast needed. */
  public String redundantCast(String dn) {
    return dn;
  }

  /** Static method called on the class. */
  public String staticViaInstance() {
    return Thread.currentThread().getName();
  }

  /** Explicit serialVersionUID, marked with @Serial so javac checks the declaration. */
  public static class Entry implements Serializable {
    @Serial private static final long serialVersionUID = 1L;

    private String dn = "";

    public String dn() {
      return dn;
    }
  }

  /** Real code would validate the divisor; constant zero was the bug. */
  public int divZero(int divisor) {
    if (divisor == 0) {
      throw new IllegalArgumentException("divisor must be non-zero");
    }
    return 1 / divisor;
  }

  /** Always brace if-bodies; a stray ';' then cannot become the whole body. */
  public boolean empty(boolean flag) {
    if (flag) {
      System.out.println("flag set");
    }
    return flag;
  }
}
