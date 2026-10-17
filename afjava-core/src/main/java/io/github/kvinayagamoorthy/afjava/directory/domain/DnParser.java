package io.github.kvinayagamoorthy.afjava.directory.domain;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Single-use cursor over a DN string. Reads {@code type=value} pairs separated by commas.
 *
 * <p>Kept separate from {@link DistinguishedName} so the record stays a plain value and each
 * parsing rule lives in one small method. Positions in error messages are 0-based offsets into
 * the original text.
 */
final class DnParser {

  /** Characters that must be escaped inside a value (RFC 4514 section 2.4). */
  private static final String MUST_ESCAPE = "\"+;<>";

  /** Characters allowed after a backslash, besides two hex digits. */
  private static final String ESCAPABLE = "\"+,;<>\\#= ";

  private final String text;
  private int pos;

  DnParser(String text) {
    this.text = text;
  }

  List<Rdn> parse() {
    List<Rdn> rdns = new ArrayList<>();
    if (text.isBlank()) {
      return rdns;
    }
    rdns.add(readRdn());
    while (pos < text.length()) {
      pos++; // consume ','
      rdns.add(readRdn());
    }
    return rdns;
  }

  private Rdn readRdn() {
    int start = pos;
    if (text.substring(pos).isBlank() || text.charAt(pos) == ',') {
      throw error("empty RDN", start);
    }
    String type = readType();
    String value = readValue();
    if (value.isEmpty()) {
      throw error("empty value in RDN starting", start);
    }
    return new Rdn(type, value);
  }

  /** Reads up to and including the '='; the type itself may not contain escapes. */
  private String readType() {
    int start = pos;
    while (pos < text.length()) {
      char c = text.charAt(pos);
      if (c == '=') {
        String type = text.substring(start, pos).strip();
        pos++;
        if (type.isEmpty()) {
          throw error("missing attribute type", start);
        }
        return type;
      }
      if (c == ',') {
        break;
      }
      pos++;
    }
    throw error("missing '=' in RDN starting", start);
  }

  /**
   * Reads up to (not including) the next unescaped ',' or end of text.
   *
   * <p>Unescaped leading and trailing spaces are not part of the value; escaped spaces ({@code \ }
   * or {@code \20}) are. {@code significantLength} marks the end of the last character that must
   * be kept, so trailing unescaped spaces can be cut off at the end.
   */
  private String readValue() {
    StringBuilder value = new StringBuilder();
    ByteArrayOutputStream pendingBytes = new ByteArrayOutputStream();
    int hexRunStart = pos;
    int significantLength = 0;
    while (pos < text.length() && text.charAt(pos) != ',') {
      char c = text.charAt(pos);
      if (c == '\\' && isHexPair(pos + 1)) {
        if (pendingBytes.size() == 0) {
          hexRunStart = pos;
        }
        pendingBytes.write(Integer.parseInt(text, pos + 1, pos + 3, 16));
        pos += 3;
        continue;
      }
      if (flushUtf8(pendingBytes, value, hexRunStart)) {
        significantLength = value.length();
      }
      if (c == '\\') {
        value.append(readEscapedChar());
        significantLength = value.length();
      } else if (MUST_ESCAPE.indexOf(c) >= 0) {
        throw error(unescapedMessage(c), pos);
      } else if (c == ' ') {
        if (value.length() > 0) {
          value.append(c);
        }
        pos++;
      } else {
        value.append(c);
        significantLength = value.length();
        pos++;
      }
    }
    if (flushUtf8(pendingBytes, value, hexRunStart)) {
      significantLength = value.length();
    }
    return value.substring(0, significantLength);
  }

  private char readEscapedChar() {
    int escapeAt = pos;
    pos++; // consume '\'
    if (pos >= text.length()) {
      throw error("dangling '\\' at end of DN", escapeAt);
    }
    char c = text.charAt(pos);
    if (ESCAPABLE.indexOf(c) < 0) {
      throw error("invalid escape '\\" + c + "'", escapeAt);
    }
    pos++;
    return c;
  }

  private boolean isHexPair(int at) {
    return at + 1 < text.length()
        && Character.digit(text.charAt(at), 16) >= 0
        && Character.digit(text.charAt(at + 1), 16) >= 0;
  }

  /** Decodes buffered hex-escaped bytes as UTF-8 into {@code value}; returns true if any. */
  private boolean flushUtf8(
      ByteArrayOutputStream pendingBytes, StringBuilder value, int hexRunStart) {
    if (pendingBytes.size() == 0) {
      return false;
    }
    try {
      value.append(
          StandardCharsets.UTF_8
              .newDecoder()
              .onMalformedInput(CodingErrorAction.REPORT)
              .onUnmappableCharacter(CodingErrorAction.REPORT)
              .decode(ByteBuffer.wrap(pendingBytes.toByteArray())));
    } catch (CharacterCodingException e) {
      throw error("hex escapes are not valid UTF-8", hexRunStart);
    }
    pendingBytes.reset();
    return true;
  }

  private static String unescapedMessage(char c) {
    if (c == '+') {
      return "multi-valued RDN ('+') is not supported";
    }
    return "unescaped '" + c + "' in value";
  }

  private IllegalArgumentException error(String message, int at) {
    return new IllegalArgumentException(message + " at position " + at + ": \"" + text + "\"");
  }
}
