package io.github.kvinayagamoorthy.afjava.directory.domain;

import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Relative Distinguished Name: one {@code type=value} component of a DN, e.g. {@code cn=admin}.
 *
 * <p>Single-valued only. Attribute type follows the RFC 4512 {@code descr} form (letter, then
 * letters, digits or hyphens) and is normalised to lower case, as LDAP attribute names are
 * case-insensitive. Value escaping arrives with the DN parser.
 */
public record Rdn(String type, String value) {

  private static final Pattern DESCR = Pattern.compile("[A-Za-z][A-Za-z0-9-]*");

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

  @Override
  public String toString() {
    return type + "=" + value;
  }
}
