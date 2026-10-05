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

## D004 — Project-scoped Maven settings (2026-10-05)

- **Context:** `~/.m2/settings.xml` mirrors `central` to a corporate Artifactory that is
  unreachable off VPN. This is a personal/public repo.
- **Decision:** `.mvn/maven.config` passes `--settings .mvn/settings.xml` (minimal, no mirrors).
- **Consequence:** Builds resolve from Maven Central anywhere. Global settings untouched.
  Local repository `~/.m2/repository` still shared.

## D005 — javac `-Xlint:all` + `failOnWarning` as first gate (2026-10-05)

- **Context:** Compiler warnings are free and have no false positives so far.
- **Decision:** Parent POM enables all lint categories and fails on any warning, controlled by
  property `afjava.failOnWarning` (labs set it `false`).
- **Consequence:** Suppressions must be narrow (`@SuppressWarnings` on a local variable) with a
  comment saying why.

## D006 — `qa/check.sh`: no `mvn -q`, filter findings instead (2026-10-05)

- **Context:** `-q` hides `[WARNING]` lines, so a lint failure printed no file/line.
- **Decision:** Capture full log; on failure print `[ERROR]/[WARNING]` lines minus Maven help
  boilerplate. One Maven invocation per step for clear per-tool PASS/FAIL.
- **Consequence:** ~1 s JVM startup per step. Revisit (single `verify` or mvnd) if T1 grows past
  budget.
