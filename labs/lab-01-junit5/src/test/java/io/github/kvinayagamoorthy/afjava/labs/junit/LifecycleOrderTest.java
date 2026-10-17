package io.github.kvinayagamoorthy.afjava.labs.junit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Prints the order in which JUnit calls lifecycle methods, and which test-class instance each
 * call runs on. Run it and read the console: every {@code @Test} gets a fresh instance (a new
 * {@code instance=} number), so a field set in one test is never seen by another.
 */
class LifecycleOrderTest {

  private static int instancesCreated;

  private final int instance;
  private LockoutRegistry registry;

  LifecycleOrderTest() {
    instancesCreated++;
    instance = instancesCreated;
    log("constructor");
  }

  @BeforeAll
  static void beforeAll() {
    System.out.println("[flow] @BeforeAll        (static, once per class)");
  }

  @BeforeEach
  void beforeEach() {
    registry = new LockoutRegistry(3);
    log("@BeforeEach      registry created, failures(cn=admin)=" + registry.failureCount("cn=admin"));
  }

  @Test
  void twoFailuresDoNotLock() {
    registry.failedBind("cn=admin");
    registry.failedBind("cn=admin");
    log("@Test twoFailuresDoNotLock  failures(cn=admin)=" + registry.failureCount("cn=admin"));
    assertEquals(false, registry.isLocked("cn=admin"));
  }

  @Test
  void threeFailuresLock() {
    registry.failedBind("cn=admin");
    registry.failedBind("cn=admin");
    registry.failedBind("cn=admin");
    log("@Test threeFailuresLock     failures(cn=admin)=" + registry.failureCount("cn=admin"));
    assertEquals(true, registry.isLocked("cn=admin"));
  }

  @AfterEach
  void afterEach() {
    log("@AfterEach");
  }

  @AfterAll
  static void afterAll() {
    System.out.println("[flow] @AfterAll         (static, once per class); instances created: "
        + instancesCreated);
  }

  private void log(String event) {
    System.out.println("[flow]   instance=" + instance + "  " + event);
  }
}
