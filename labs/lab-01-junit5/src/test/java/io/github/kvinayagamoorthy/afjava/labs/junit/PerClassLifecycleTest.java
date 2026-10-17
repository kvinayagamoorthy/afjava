package io.github.kvinayagamoorthy.afjava.labs.junit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * Same registry, but {@code PER_CLASS}: one instance for all tests, so the field is shared and
 * the second test sees the first test's failures. Order is pinned with {@code @Order} to make
 * that visible. Compare the printed counts with {@link LifecycleOrderTest}.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PerClassLifecycleTest {

  private LockoutRegistry registry;

  @BeforeAll
  void createOnce() { // non-static: allowed only with PER_CLASS
    registry = new LockoutRegistry(3);
    System.out.println("[per-class] @BeforeAll on instance " + System.identityHashCode(this));
  }

  @Test
  @Order(1)
  void firstTestFailsTwice() {
    registry.failedBind("cn=admin");
    registry.failedBind("cn=admin");
    System.out.println("[per-class] first  on instance " + System.identityHashCode(this)
        + " failures=" + registry.failureCount("cn=admin"));
    assertEquals(2, registry.failureCount("cn=admin"));
  }

  @Test
  @Order(2)
  void secondTestSeesTheFirstTestsState() {
    registry.failedBind("cn=admin");
    System.out.println("[per-class] second on instance " + System.identityHashCode(this)
        + " failures=" + registry.failureCount("cn=admin"));
    assertEquals(3, registry.failureCount("cn=admin")); // 2 leaked in from the first test
  }
}
