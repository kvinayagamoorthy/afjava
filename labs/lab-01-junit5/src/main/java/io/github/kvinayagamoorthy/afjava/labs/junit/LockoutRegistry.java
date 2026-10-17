package io.github.kvinayagamoorthy.afjava.labs.junit;

import java.util.HashMap;
import java.util.Map;

/**
 * Locks an account after too many consecutive failed binds, like an LDAP server's intruder
 * detection. A successful bind resets the counter. Each registry instance has its own state.
 */
public final class LockoutRegistry {

  private final int maxFailures;
  private final Map<String, Integer> failures = new HashMap<>();

  /** Creates a registry that locks an account after {@code maxFailures} consecutive failures. */
  public LockoutRegistry(int maxFailures) {
    if (maxFailures < 1) {
      throw new IllegalArgumentException("maxFailures must be at least 1: " + maxFailures);
    }
    this.maxFailures = maxFailures;
  }

  /** Records a failed bind for {@code dn}. */
  public void failedBind(String dn) {
    failures.merge(dn, 1, Integer::sum);
  }

  /** Records a successful bind for {@code dn}, which resets its failure count. */
  public void successfulBind(String dn) {
    failures.remove(dn);
  }

  /** Returns the consecutive failure count for {@code dn}. */
  public int failureCount(String dn) {
    return failures.getOrDefault(dn, 0);
  }

  /** Returns true once {@code dn} has reached the failure limit. */
  public boolean isLocked(String dn) {
    return failureCount(dn) >= maxFailures;
  }
}
