package io.github.kvinayagamoorthy.afjava.labs.junit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * INTENTIONALLY FLAWED. Each test is correct on its own, but {@link StaticLockoutRegistry} keeps
 * state in a static map, so failures recorded by one test are still there for the next. Whether
 * the class passes depends on the order JUnit picks. Run with a random method order and
 * different seeds to watch it flip between PASS and FAIL.
 */
@Tag("order-dependent")
class SharedStateTest {

  @Test
  void newAccountIsNotLocked() {
    assertEquals(0, StaticLockoutRegistry.failureCount("cn=admin,o=acme"));
    assertFalse(StaticLockoutRegistry.isLocked("cn=admin,o=acme"));
  }

  @Test
  void twoFailuresDoNotLock() {
    StaticLockoutRegistry.failedBind("cn=admin,o=acme");
    StaticLockoutRegistry.failedBind("cn=admin,o=acme");
    assertFalse(StaticLockoutRegistry.isLocked("cn=admin,o=acme"));
  }

  @Test
  void threeFailuresLock() {
    StaticLockoutRegistry.failedBind("cn=admin,o=acme");
    StaticLockoutRegistry.failedBind("cn=admin,o=acme");
    StaticLockoutRegistry.failedBind("cn=admin,o=acme");
    assertTrue(StaticLockoutRegistry.isLocked("cn=admin,o=acme"));
  }
}
