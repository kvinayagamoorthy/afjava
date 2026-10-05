package io.github.kvinayagamoorthy.afjava.labs.javaclint;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * INTENTIONALLY FLAWED. Each method triggers one javac -Xlint category.
 * Do not fix; see LintGoodExamples for the corrected versions.
 */
public class LintBadExamples {

  /** [rawtypes] + [unchecked]: raw List loses type safety; heap pollution goes unnoticed. */
  public List<String> rawTypes() {
    List names = new ArrayList();
    names.add("cn=admin");
    names.add(42); // compiles: raw type accepts anything
    return names;
  }

  /** [fallthrough]: missing break silently runs the next case. */
  public int fallthrough(int scope) {
    int cost = 0;
    switch (scope) {
      case 0:
        cost += 1;
      case 1:
        cost += 10;
        break;
      default:
        cost += 100;
    }
    return cost;
  }

  /** [deprecation] + [removal]: Integer(int) constructor is deprecated for removal. */
  public Integer deprecated() {
    return new Integer(7);
  }

  /** [cast]: redundant cast hides intent. */
  public String redundantCast(String dn) {
    return (String) dn;
  }

  /** [static]: static method called through an instance. */
  public String staticViaInstance(Thread t) {
    return t.currentThread().getName();
  }

  /** [serial]: Serializable class without serialVersionUID. */
  public static class Entry implements Serializable {
    private String dn = "";

    public String dn() {
      return dn;
    }
  }

  /** [divzero]: constant division by zero. */
  public int divZero() {
    return 1 / 0;
  }

  /** [empty]: empty statement after if. */
  public boolean empty(boolean flag) {
    if (flag);
    return flag;
  }
}
