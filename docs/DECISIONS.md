# Decisions

Short ADR-style log. One entry per decision about tools, gates, configs or layout. Newest at
bottom. Format: context → decision → consequence.

## D001 — Maven over Gradle (2026-10-05)

- **Context:** Maven 3.9 installed, Gradle not. Most enterprise Java (incl. OpenText projects)
  is Maven. Every tool in the plan has a first-class Maven plugin.
- **Decision:** Maven with Maven Wrapper; JDK 21.
- **Consequence:** All snippets in lessons are Maven. Gradle equivalents can be noted in passing.

## D002 — `afjava-core` + `labs/` split (2026-10-05)

- **Context:** Need code that makes tools fire, but main build must stay green.
- **Decision:** `afjava-core` has all gates on and must stay green. Each tool gets
  `labs/lab-NN-<tool>` with intentionally flawed code, excluded from the default build.
- **Consequence:** `./qa/check.sh` never fails because of lab code. Labs run via `-Plabs` or `-pl`.

## D003 — Mini identity directory as sample domain (2026-10-05)

- **Context:** Sample code must be realistic enough for every tool to have something to catch.
- **Decision:** DN parser, LDAP filter builder, users/groups, password policy, XML import,
  repository with in-memory + LDAP implementations.
- **Consequence:** find-sec-bugs gets LDAP injection + XXE; Testcontainers gets OpenLDAP; PIT
  gets parser edge cases; ArchUnit gets layers.
