package io.github.kvinayagamoorthy.afjava.labs.junit;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * INTENTIONALLY FAILING. Every test here fails, to show what the failure message looks like.
 * The registry has a limit of 3, but each test wrongly expects the account to lock after 2.
 * Compare how much each message tells you without opening the code.
 */
@Tag("on-purpose-failure")
class AssertionMessagesTest {

  private final LockoutRegistry registry = new LockoutRegistry(3);

  @Test
  @DisplayName("1 bad: assertTrue hides the values")
  void assertTrueHidesValues() {
    registry.failedBind("cn=admin");
    registry.failedBind("cn=admin");
    assertTrue(registry.failureCount("cn=admin") == 3);
  }

  @Test
  @DisplayName("2 bad: expected and actual swapped")
  void swappedArguments() {
    registry.failedBind("cn=admin");
    registry.failedBind("cn=admin");
    assertEquals(registry.failureCount("cn=admin"), 3); // (actual, expected): wrong order
  }

  @Test
  @DisplayName("3 good: expected first, actual second, plus a message")
  void expectedFirstWithMessage() {
    registry.failedBind("cn=admin");
    registry.failedBind("cn=admin");
    assertEquals(3, registry.failureCount("cn=admin"), "failures after two bad binds");
  }

  @Test
  @DisplayName("4 bad: sequential asserts stop at the first failure")
  void sequentialAssertsStopEarly() {
    registry.failedBind("cn=admin");
    registry.failedBind("cn=admin");
    assertEquals(3, registry.failureCount("cn=admin"), "failure count");
    assertEquals(true, registry.isLocked("cn=admin"), "locked"); // never reached
  }

  @Test
  @DisplayName("5 good: assertAll reports every failed check at once")
  void assertAllReportsEverything() {
    registry.failedBind("cn=admin");
    registry.failedBind("cn=admin");
    assertAll(
        "account cn=admin after two bad binds",
        () -> assertEquals(3, registry.failureCount("cn=admin"), "failure count"),
        () -> assertEquals(true, registry.isLocked("cn=admin"), "locked"));
  }
}
