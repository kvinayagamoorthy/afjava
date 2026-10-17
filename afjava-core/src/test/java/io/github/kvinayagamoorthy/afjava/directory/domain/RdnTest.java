package io.github.kvinayagamoorthy.afjava.directory.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("Rdn")
class RdnTest {

  @Test
  @DisplayName("attribute type is case-insensitive: CN=admin equals cn=admin")
  void typeIsNormalisedToLowerCase() {
    Rdn upper = new Rdn("CN", "admin");

    assertEquals("cn", upper.type());
    assertEquals(new Rdn("cn", "admin"), upper);
  }

  @Test
  @DisplayName("value is compared exactly: cn=Admin differs from cn=admin")
  void valueIsComparedExactly() {
    assertNotEquals(new Rdn("cn", "admin"), new Rdn("cn", "Admin"));
  }

  @ParameterizedTest(name = "type {0} is rejected")
  @ValueSource(strings = {"", "1cn", "c n", "cn=", "_cn", "2.5.4.3"})
  @DisplayName("attribute type must be an RFC 4512 descr")
  void invalidTypeIsRejected(String type) {
    IllegalArgumentException thrown =
        assertThrows(IllegalArgumentException.class, () -> new Rdn(type, "admin"));

    assertTrue(thrown.getMessage().contains("invalid attribute type"), thrown.getMessage());
  }

  @Test
  @DisplayName("empty value is rejected")
  void emptyValueIsRejected() {
    IllegalArgumentException thrown =
        assertThrows(IllegalArgumentException.class, () -> new Rdn("cn", ""));

    assertEquals("empty value for attribute type: cn", thrown.getMessage());
  }

  @Test
  @DisplayName("null type or value is rejected with the parameter name")
  void nullsAreRejected() {
    NullPointerException nullType =
        assertThrows(NullPointerException.class, () -> new Rdn(null, "admin"));
    NullPointerException nullValue =
        assertThrows(NullPointerException.class, () -> new Rdn("cn", null));

    assertEquals("type", nullType.getMessage());
    assertEquals("value", nullValue.getMessage());
  }

  /** Each row: raw value, expected RFC 4514 string form. Pipe-delimited to avoid CSV commas. */
  @ParameterizedTest(name = "[{index}] value {0} is written as {1}")
  @CsvSource(
      delimiter = '|',
      quoteCharacter = '\'',
      textBlock =
          """
          admin            | cn=admin
          Smith, John      | cn=Smith\\, John
          a+b              | cn=a\\+b
          say "hi"         | cn=say \\"hi\\"
          <x>;y            | cn=\\<x\\>\\;y
          back\\slash      | cn=back\\\\slash
          '#1'             | cn=\\#1
          '1#'             | cn=1#
          ' lead'          | cn=\\ lead
          'trail '         | 'cn=trail\\ '
          'in side'        | cn=in side
          a=b              | cn=a=b
          """)
  @DisplayName("toString escapes DN syntax characters (RFC 4514 section 2.4)")
  void toStringEscapes(String value, String expected) {
    assertEquals(expected, new Rdn("cn", value).toString());
  }

  @Test
  @DisplayName("NUL character is written as \\00")
  void nulIsHexEscaped() {
    assertEquals("cn=a\\00b", new Rdn("cn", "a\0b").toString());
  }
}
