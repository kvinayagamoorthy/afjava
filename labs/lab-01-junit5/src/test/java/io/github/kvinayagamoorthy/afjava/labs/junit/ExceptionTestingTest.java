package io.github.kvinayagamoorthy.afjava.labs.junit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Testing that code throws. {@code new LockoutRegistry(0)} is rejected, but
 * {@code new LockoutRegistry(5)} is valid and does NOT throw. Both tests below wrongly claim that
 * 5 is rejected. Only one of them notices.
 */
class ExceptionTestingTest {

  @Test
  @DisplayName("bad: try/catch without fail() passes even though nothing was thrown")
  void tryCatchPassesSilently() {
    try {
      new LockoutRegistry(5);
    } catch (IllegalArgumentException e) {
      assertEquals("maxFailures must be at least 1: 5", e.getMessage());
    }
    // No exception, so the catch block never ran and no assertion executed. Still green.
  }

  @Test
  @Tag("on-purpose-failure")
  @DisplayName("good: assertThrows fails when nothing is thrown")
  void assertThrowsNoticesMissingException() {
    assertThrows(IllegalArgumentException.class, () -> new LockoutRegistry(5));
  }

  @Test
  @DisplayName("good: assertThrows returns the exception, so its message can be checked")
  void assertThrowsReturnsException() {
    IllegalArgumentException thrown =
        assertThrows(IllegalArgumentException.class, () -> new LockoutRegistry(0));
    assertEquals("maxFailures must be at least 1: 0", thrown.getMessage());
  }
}
