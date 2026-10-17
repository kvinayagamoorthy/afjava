package io.github.kvinayagamoorthy.afjava.directory.domain;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Distinguished Name: the full name of a directory entry, e.g. {@code cn=admin,ou=sales,o=acme}.
 *
 * <p>Held as a list of {@link Rdn}s, leftmost first, exactly as written in the string form. The
 * leftmost RDN names the entry itself; the remaining RDNs are the DN of its parent container.
 * The empty DN names the root of the tree.
 *
 * <p>Two DNs are equal when their RDNs are equal pairwise (type case-insensitive, value exact).
 */
public record DistinguishedName(List<Rdn> rdns) {

  /** The root of the directory tree: zero RDNs, string form {@code ""}. */
  public static final DistinguishedName ROOT = new DistinguishedName(List.of());

  /** Defensively copies the RDN list so the DN is immutable. */
  public DistinguishedName {
    rdns = List.copyOf(Objects.requireNonNull(rdns, "rdns"));
  }

  /**
   * Parses the RFC 4514 string form.
   *
   * <p>Accepts backslash escapes ({@code \,} and {@code \2C} hex, UTF-8 decoded) and tolerates
   * unescaped spaces around {@code ,} and {@code =}, as commonly written by people.
   *
   * @throws IllegalArgumentException if the text is not a valid DN; the message names the
   *     position of the problem
   */
  public static DistinguishedName parse(String text) {
    Objects.requireNonNull(text, "text");
    return new DistinguishedName(new DnParser(text).parse());
  }

  /** Returns true for the root DN. */
  public boolean isRoot() {
    return rdns.isEmpty();
  }

  /** Returns the DN of the containing entry, or empty for the root. */
  public Optional<DistinguishedName> parent() {
    if (isRoot()) {
      return Optional.empty();
    }
    return Optional.of(new DistinguishedName(rdns.subList(1, rdns.size())));
  }

  /**
   * Returns true if this entry lies strictly below {@code ancestor} in the tree, i.e. a subtree
   * search based at {@code ancestor} would find it. A DN is not its own descendant.
   */
  public boolean isDescendantOf(DistinguishedName ancestor) {
    Objects.requireNonNull(ancestor, "ancestor");
    int extra = rdns.size() - ancestor.rdns.size();
    return extra > 0 && rdns.subList(extra, rdns.size()).equals(ancestor.rdns);
  }

  /** Returns the RFC 4514 string form, e.g. {@code cn=admin,ou=sales,o=acme}. */
  @Override
  public String toString() {
    return rdns.stream().map(Rdn::toString).collect(Collectors.joining(","));
  }
}
