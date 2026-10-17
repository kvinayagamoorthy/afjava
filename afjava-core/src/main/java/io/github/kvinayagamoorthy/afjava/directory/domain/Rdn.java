package io.github.kvinayagamoorthy.afjava.directory.domain;

import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Relative Distinguished Name: one {@code type=value} component of a DN, e.g. {@code cn=admin}.
 *
 * <p>Single-valued only. Attribute type follows the RFC 4512 {@code descr} form (letter, then
 * letters, digits or hyphens) and is normalised to lower case, as LDAP attribute names are
 * case-insensitive. The value is held unescaped (the real characters) and compared exactly;
 * schema-aware matching such as case-insensitive {@code cn} is out of scope.
 *
 * <p>{@link #toString()} produces the RFC 4514 string form, escaping characters that would
 * otherwise be read as DN syntax.
 */
public record Rdn(String type, String value) {

  private static final Pattern DESCR = Pattern.compile("[A-Za-z][A-Za-z0-9-]*");

  /** Characters RFC 4514 section 2.4 requires to be escaped anywhere in a value. */
  private static final String ALWAYS_ESCAPED = "\"+,;<>\\";

  /** Validates and normalises the components. */
  public Rdn {
    Objects.requireNonNull(type, "type");
    Objects.requireNonNull(value, "value");
    if (!DESCR.matcher(type).matches()) {
      throw new IllegalArgumentException("invalid attribute type: '" + type + "'");
    }
    if (value.isEmpty()) {
      throw new IllegalArgumentException("empty value for attribute type: " + type);
    }
    type = type.toLowerCase(Locale.ROOT);
  }

  /** Returns the RFC 4514 string form, e.g. {@code cn=Smith\, John}. */
  @Override
  public String toString() {
    return type + "=" + escape(value);
  }

  private static String escape(String raw) {
    StringBuilder escaped = new StringBuilder(raw.length() + 8);
    int last = raw.length() - 1;
    for (int i = 0; i <= last; i++) {
      char c = raw.charAt(i);
      boolean leadingSpecial = i == 0 && (c == ' ' || c == '#');
      boolean trailingSpace = i == last && c == ' ';
      if (c == '\0') {
        escaped.append("\\00");
      } else if (ALWAYS_ESCAPED.indexOf(c) >= 0 || leadingSpecial || trailingSpace) {
        escaped.append('\\').append(c);
      } else {
        escaped.append(c);
      }
    }
    return escaped.toString();
  }
}
