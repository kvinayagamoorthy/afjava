# Program theory: afjava-core

Peter Naur: the program's real asset is the theory of how it maps onto the world, why it is
shaped this way, and what is deliberately left out. Code and tests are evidence of that theory.
This file holds the theory of the sample domain. Update it with every domain change.

Format per concept: **world rule → code**, **invariants**, **boundary** (out of scope, on
purpose), **proof** (tests or experiments that show it holds).

## The world being modelled

An LDAP directory (eDirectory, OpenLDAP, Active Directory) stores entries in a tree. Every
entry has a Distinguished Name (DN) such as `cn=admin,ou=sales,o=acme`: read right to left it
is the path from the top of the tree (`o=acme`) down to the entry (`cn=admin`). Programs pass
DNs around as strings, typed by people, written in config files and LDIF, and returned by
servers. The string form is specified by RFC 4514; attribute-name syntax by RFC 4512.

## Rdn: one step in the path

- **World rule → code:** an RDN is `type=value`, e.g. `cn=admin` →
  `io.github.kvinayagamoorthy.afjava.directory.domain.Rdn` (record).
- **Invariants:**
  - Type matches RFC 4512 `descr` (`[A-Za-z][A-Za-z0-9-]*`) and is stored lower-case, because
    LDAP attribute names are case-insensitive. So `CN=admin` equals `cn=admin`.
  - Value is non-empty and stored **unescaped** (the real characters, e.g. `Smith, John`).
  - `toString()` is the RFC 4514 form: escapes `" + , ; < > \` anywhere, `#` or space at the
    start, space at the end, NUL as `\00`.
- **Boundary:**
  - Value comparison is exact: `cn=Admin` ≠ `cn=admin`. Real servers compare values with the
    attribute's matching rule from the schema (usually case-insensitive for `cn`); there is no
    schema here.
  - Numeric OID types (`2.5.4.3=admin`) are rejected.
  - Multi-valued RDNs (`cn=a+sn=b`) are not modelled: an `Rdn` holds exactly one pair.
- **Proof:** `RdnTest` (lesson 01). Break-it experiment: removing the comma from the escape set
  fails `RdnTest.toStringEscapes` and the DN round-trip test.

## DistinguishedName: the whole path

- **World rule → code:** a DN is a sequence of RDNs, leftmost = the entry itself →
  `DistinguishedName` (record holding `List<Rdn>`); parsing in package-private `DnParser`.
- **Invariants:**
  - Immutable: the RDN list is copied on construction.
  - `rdns().get(0)` is the entry's own RDN; `parent()` drops it. The empty DN is the root
    (`DistinguishedName.ROOT`), which has no parent.
  - `isDescendantOf(a)` is true only when `a`'s RDNs are a proper suffix: what a subtree search
    based at `a` would return, excluding `a` itself.
  - Round trip: `parse(dn.toString()).equals(dn)` for every DN.
  - Parse errors are `IllegalArgumentException` whose message names the 0-based position and
    quotes the input, so a person can find the mistake in what they typed.
- **Accepted input (lenient where people are sloppy, strict where meaning would change):**
  - Unescaped spaces around `,` and `=` are dropped (`cn=admin, ou=sales` is common in configs).
    Escaped spaces (`\ `, `\20`) are kept.
  - Escapes: `\` + one of `" + , ; < > \ # = space`, or `\` + two hex digits. Consecutive hex
    bytes are decoded as UTF-8 (`J\C3\BCrgen` → `Jürgen`); invalid UTF-8 is rejected.
  - Unescaped `" + ; < >` inside a value are rejected; `+` gets its own message because it
    means "multi-valued RDN", which is out of scope.
- **Boundary:** `#`-prefixed BER hex values, LDAPv2 forms (`;` separators, quoted values), and
  schema-aware equality are not supported.
- **Why the parser is a separate class:** the record stays a plain value; the parser is a
  single-use cursor (`text`, `pos`) whose state is easy to follow one small method at a time.
- **Proof:** `DistinguishedNameTest` nested groups `parsing`, `escaping` (incl. round trip),
  `hierarchy`, `rejection`. Break-it experiment: changing `extra > 0` to `extra >= 0` in
  `isDescendantOf` fails the "not self" rows.
