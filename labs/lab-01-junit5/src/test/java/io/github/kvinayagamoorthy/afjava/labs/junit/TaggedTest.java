package io.github.kvinayagamoorthy.afjava.labs.junit;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Tags select which tests run, like pytest markers ({@code -m}). The "component" test simulates
 * a slow call to a real LDAP server; select or skip it from the command line with
 * {@code -Dgroups=component} or {@code -DexcludedGroups=component}.
 */
class TaggedTest {

  @Test
  @Tag("unit")
  @DisplayName("unit: in-memory, milliseconds")
  void fastUnitTest() {
    LockoutRegistry registry = new LockoutRegistry(1);
    registry.failedBind("cn=admin,o=acme");
    assertTrue(registry.isLocked("cn=admin,o=acme"));
  }

  @Test
  @Tag("component")
  @DisplayName("component: pretends to bind against a real LDAP server (2 s)")
  void slowComponentTest() throws InterruptedException {
    Thread.sleep(2000);
    assertTrue(true);
  }

  @Test
  @Disabled("password history is not implemented yet")
  @DisplayName("disabled: shows up as skipped, with its reason")
  void passwordHistory() {}
}
