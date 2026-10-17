package io.github.kvinayagamoorthy.afjava.directory.domain;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("DistinguishedName")
class DistinguishedNameTest {

  private static final DistinguishedName ACME = DistinguishedName.parse("o=acme");
  private static final DistinguishedName SALES = DistinguishedName.parse("ou=sales,o=acme");
  private static final DistinguishedName ADMIN =
      DistinguishedName.parse("cn=admin,ou=sales,o=acme");

  @Nested
  @DisplayName("parsing")
  class Parsing {

    @Test
    @DisplayName("cn=admin,ou=sales,o=acme has three RDNs, leftmost first")
    void rdnsAreLeftmostFirst() {
      assertEquals(
          List.of(new Rdn("cn", "admin"), new Rdn("ou", "sales"), new Rdn("o", "acme")),
          ADMIN.rdns());
    }

    @Test
    @DisplayName("empty string is the root DN")
    void emptyStringIsRoot() {
      DistinguishedName root = DistinguishedName.parse("");

      assertAll(
          () -> assertTrue(root.isRoot()),
          () -> assertEquals(DistinguishedName.ROOT, root),
          () -> assertEquals("", root.toString()));
    }

    /** Each row: as typed by a person, normalised string form. */
    @ParameterizedTest(name = "{0} normalises to {1}")
    @CsvSource(
        delimiter = '|',
        quoteCharacter = '\'',
        textBlock =
            """
            cn=admin,ou=sales,o=acme       | cn=admin,ou=sales,o=acme
            CN=admin,OU=sales,O=acme       | cn=admin,ou=sales,o=acme
            cn=admin, ou=sales, o=acme     | cn=admin,ou=sales,o=acme
            'cn = admin , ou = sales'      | cn=admin,ou=sales
            'cn=Admin User,o=acme'         | cn=Admin User,o=acme
            """)
    @DisplayName("types are lower-cased and unescaped spaces around , and = are dropped")
    void normalisesTypesAndSeparatorSpaces(String typed, String normalised) {
      assertEquals(normalised, DistinguishedName.parse(typed).toString());
    }
  }

  @Nested
  @DisplayName("escaping")
  class Escaping {

    /** Each row: DN text, expected value of the leftmost RDN (shown in brackets). */
    @ParameterizedTest(name = "{0} -> leftmost value [{1}]")
    @CsvSource(
        delimiter = '|',
        quoteCharacter = '\'',
        textBlock =
            """
            cn=Smith\\, John,o=acme           | Smith, John
            cn=Smith\\2C John,o=acme          | Smith, John
            cn=J\\C3\\BCrgen,o=acme           | Jürgen
            cn=\\E5\\BC\\A0\\E4\\BC\\9F,o=acme  | 张伟
            cn=a\\+b,o=acme                   | a+b
            cn=\\#1,o=acme                    | '#1'
            'cn=\\ padded\\ ,o=acme'          | ' padded '
            'cn=\\20x\\20,o=acme'             | ' x '
            cn=back\\\\slash,o=acme           | back\\slash
            """)
    @DisplayName("backslash and hex escapes are decoded (hex as UTF-8)")
    void escapesAreDecoded(String text, String expectedValue) {
      assertEquals(expectedValue, DistinguishedName.parse(text).rdns().get(0).value());
    }

    static Stream<DistinguishedName> awkwardNames() {
      return Stream.of(
          ADMIN,
          new DistinguishedName(List.of(new Rdn("cn", "Smith, John"), new Rdn("o", "acme"))),
          new DistinguishedName(List.of(new Rdn("cn", " lead and trail "), new Rdn("o", "x"))),
          new DistinguishedName(List.of(new Rdn("cn", "#1+<2>;\"3\"\\"), new Rdn("o", "x"))),
          new DistinguishedName(List.of(new Rdn("cn", "Jürgen"), new Rdn("o", "acme"))));
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("awkwardNames")
    @DisplayName("round trip: parse(dn.toString()) equals dn")
    void roundTrip(DistinguishedName dn) {
      assertEquals(dn, DistinguishedName.parse(dn.toString()));
    }
  }

  @Nested
  @DisplayName("hierarchy")
  class Hierarchy {

    @Test
    @DisplayName("parent of cn=admin,ou=sales,o=acme is ou=sales,o=acme")
    void parentDropsLeftmostRdn() {
      assertEquals(Optional.of(SALES), ADMIN.parent());
    }

    @Test
    @DisplayName("root has no parent")
    void rootHasNoParent() {
      assertEquals(Optional.empty(), DistinguishedName.ROOT.parent());
    }

    @Test
    @DisplayName("walking parent() from cn=admin reaches the root in three steps")
    void parentChainEndsAtRoot() {
      DistinguishedName top = ADMIN.parent().flatMap(DistinguishedName::parent).orElseThrow();

      assertEquals(ACME, top);
      assertEquals(Optional.of(DistinguishedName.ROOT), top.parent());
    }

    static Stream<Arguments> descendantCases() {
      return Stream.of(
          Arguments.of(ADMIN, SALES, true),
          Arguments.of(ADMIN, ACME, true),
          Arguments.of(ADMIN, DistinguishedName.ROOT, true),
          Arguments.of(ADMIN, ADMIN, false),
          Arguments.of(SALES, ADMIN, false),
          Arguments.of(ADMIN, DistinguishedName.parse("ou=hr,o=acme"), false),
          Arguments.of(DistinguishedName.parse("cn=admin,o=sales"), SALES, false));
    }

    @ParameterizedTest(name = "{0} below {1}: {2}")
    @MethodSource("descendantCases")
    @DisplayName("isDescendantOf matches subtree search semantics (not self)")
    void isDescendantOf(DistinguishedName dn, DistinguishedName ancestor, boolean expected) {
      assertEquals(expected, dn.isDescendantOf(ancestor));
    }

    @Test
    @DisplayName("root is not a descendant of anything, not even itself")
    void rootIsNoDescendant() {
      assertFalse(DistinguishedName.ROOT.isDescendantOf(DistinguishedName.ROOT));
    }
  }

  @Nested
  @DisplayName("rejection")
  class Rejection {

    /** Each row: invalid DN text, expected message prefix including position. */
    @ParameterizedTest(name = "{0} -> {1}")
    @CsvSource(
        delimiter = '|',
        quoteCharacter = '\'',
        textBlock =
            """
            admin,o=acme          | missing '=' in RDN starting at position 0
            cn=admin,,o=acme      | empty RDN at position 9
            cn=admin,             | empty RDN at position 9
            =admin                | missing attribute type at position 0
            cn=,o=acme            | empty value in RDN starting at position 0
            cn=a+sn=b,o=acme      | multi-valued RDN ('+') is not supported at position 4
            cn=a;b                | unescaped ';' in value at position 4
            cn=a\\q               | invalid escape '\\q' at position 4
            cn=a\\                | dangling '\\' at end of DN at position 4
            cn=a\\FF\\FE,o=acme    | hex escapes are not valid UTF-8 at position 4
            1cn=admin             | invalid attribute type: '1cn'
            """)
    @DisplayName("invalid DNs fail with a message that names the position")
    void invalidDnIsRejected(String text, String expectedMessageStart) {
      IllegalArgumentException thrown =
          assertThrows(IllegalArgumentException.class, () -> DistinguishedName.parse(text));

      assertTrue(
          thrown.getMessage().startsWith(expectedMessageStart),
          () -> "message was: " + thrown.getMessage());
    }

    @Test
    @DisplayName("null text is rejected")
    void nullIsRejected() {
      assertThrows(NullPointerException.class, () -> DistinguishedName.parse(null));
    }
  }
}
