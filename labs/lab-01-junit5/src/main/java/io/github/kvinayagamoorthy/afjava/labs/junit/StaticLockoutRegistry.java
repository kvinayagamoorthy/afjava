package io.github.kvinayagamoorthy.afjava.labs.junit;

import java.util.HashMap;
import java.util.Map;

/**
 * INTENTIONALLY FLAWED. Same behaviour as {@link LockoutRegistry}, but the counters live in a
 * static map shared by the whole JVM. Tests that use it leak state into each other, so their
 * result depends on the order they run in. See {@code SharedStateTest}.
 */
public final class StaticLockoutRegistry {

  private static final int MAX_FAILURES = 3;
  private static final Map<String, Integer> FAILURES = new HashMap<>();

  private StaticLockoutRegistry() {}

  /** Records a failed bind for {@code dn}. */
  public static void failedBind(String dn) {
    FAILURES.merge(dn, 1, Integer::sum);
  }

  /** Returns the consecutive failure count for {@code dn}. */
  public static int failureCount(String dn) {
    return FAILURES.getOrDefault(dn, 0);
  }

  /** Returns true once {@code dn} has reached the failure limit. */
  public static boolean isLocked(String dn) {
    return failureCount(dn) >= MAX_FAILURES;
  }
}
