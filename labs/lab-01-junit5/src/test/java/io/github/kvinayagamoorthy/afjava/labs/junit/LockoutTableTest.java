package io.github.kvinayagamoorthy.afjava.labs.junit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Two ways to turn a table of concrete cases into tests. Each row shows up as its own test in
 * the report, with its data in the name.
 */
class LockoutTableTest {

  /** Each row: limit, failed binds, expected locked. */
  @ParameterizedTest(name = "limit {0}, {1} failures -> locked={2}")
  @CsvSource({
    "3, 0, false",
    "3, 2, false",
    "3, 3, true",
    "3, 4, true",
    "1, 1, true",
  })
  @DisplayName("parameterized: rows fixed at compile time")
  void lockingByTable(int limit, int failedBinds, boolean expectedLocked) {
    LockoutRegistry registry = new LockoutRegistry(limit);
    for (int i = 0; i < failedBinds; i++) {
      registry.failedBind("cn=admin,o=acme");
    }
    assertEquals(expectedLocked, registry.isLocked("cn=admin,o=acme"));
  }

  /** Generates one test per failure count from 0 to 5, computed at run time. */
  @TestFactory
  @DisplayName("dynamic: rows generated at run time")
  Stream<DynamicTest> lockingByGeneratedRows() {
    int limit = 3;
    return IntStream.rangeClosed(0, 5)
        .mapToObj(
            failedBinds ->
                dynamicTest(
                    failedBinds + " failures -> locked=" + (failedBinds >= limit),
                    () -> {
                      LockoutRegistry registry = new LockoutRegistry(limit);
                      for (int i = 0; i < failedBinds; i++) {
                        registry.failedBind("cn=admin,o=acme");
                      }
                      assertEquals(failedBinds >= limit, registry.isLocked("cn=admin,o=acme"));
                    }));
  }
}
