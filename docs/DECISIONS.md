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

## D007 — Lessons are experiment-first (2026-10-17)

- **Context:** Owner learns from concrete, immediate feedback (Bret Victor, "Inventing on
  Principle" and "Learnable Programming"; Peter Senge's learning horizon). Lesson 00 at first
  lacked the command to *see* the gate fail; the owner had to work it out.
- **Decision:** `docs/LEARNING-PRINCIPLES.md` added. Every lesson opens with "Try it yourself":
  copy-paste commands, fail first then pass, expected key output, undo commands. Mechanism
  comes after the experiments. Template reordered accordingly.
- **Consequence:** Each experiment command is run before it is documented. Lesson 00
  retrofitted (11 experiments, incl. the `-Werror` hidden-warning surprise and jshell).

## D008 — JUnit 6 instead of JUnit 5 (2026-10-17)

- **Context:** Plan said JUnit 5. Current release on Maven Central is 6.1.3. JUnit 6 keeps the
  Jupiter API, unifies version numbers, needs Java 17+ (we use 21).
- **Decision:** `junit-bom` 6.1.3. Lessons teach the Jupiter API, valid for JUnit 5 code too.
- **Consequence:** One version property for every JUnit artifact.

## D009 — Surefire tree reporter, test stdout kept (2026-10-17)

- **Context:** Default Surefire output shows only class totals; the data rows of parameterized
  tests are invisible. Seeing the data is a learning principle.
- **Decision:** Third-party `me.fabriciorby:maven-surefire-junit5-tree-reporter` 2.0.0 as a
  Surefire plugin dependency. Its suggested `consoleOutputReporter disable` is **not** used,
  because it hid test stdout (lifecycle flow prints).
- **Consequence:** One extra third-party build dependency (to be covered by
  Dependency-Check in lesson 15). `check.sh` prints `[XX]` rows on failure.

## D010 — Random test order with a printed seed (2026-10-17)

- **Context:** Lab 01 showed static shared state passing in 1 of 8 orders. JUnit's default order
  is fixed but unspecified, so order-dependence can hide for months.
- **Decision:** `qa/check.sh` shuffles class and method order on every run and prints the seed;
  `AFJAVA_TEST_SEED=<n>` replays it. Tests needing an order declare `@TestMethodOrder`.
- **Consequence:** Hidden coupling between tests surfaces early and reproducibly.

## D011 — Labs are self-contained (2026-10-17)

- **Context:** `--labs` rebuilt and retested core (11.8 s total).
- **Decision:** Labs don't depend on `afjava-core`; the labs step uses `-pl '!afjava-core'`.
- **Consequence:** Each lab shows its tool in isolation; labs step no longer duplicates core.
  Revisit if a later lab (e.g. ArchUnit, PIT) needs core classes.

## D012 — Program theory written down (Naur) (2026-10-17)

- **Context:** Owner wants the theory behind what we build, not just the code.
  `programming-as-theory-building` skill available.
- **Decision:** Skill used for every code change. `docs/THEORY.md` records the domain theory
  (world rule → code, invariants, boundary, proof). Each lesson has a "Theory of the tool"
  table with the five questions, tied to experiments.
- **Consequence:** Domain changes are not done until THEORY.md says why.
