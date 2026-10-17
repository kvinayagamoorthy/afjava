# CLAUDE.md — afjava

Learning repo: hands-on with the Java quality toolchain, one tool at a time. Owner is an
experienced C/C++/Python engineer (identity/LDAP background), newer to Java tooling. Explain Java
specifics; skip generic programming basics.

## Start of every session

1. Read `docs/TRACKER.md` → **Current focus**. That is the resume point.
2. Read the current lesson doc in `docs/lessons/` → unchecked *Done criteria* = remaining work.
3. Run `./qa/check.sh` to confirm the tree is green before changing anything.

## End of every session / lesson step

- Update `docs/TRACKER.md`: Current focus, lesson row (status, version, gate, tier, time
  added), and append one row to the Session log.
- Log any gate/config/rule decision in `docs/DECISIONS.md`.
- Tick completed boxes in the lesson doc.

## How to teach (read docs/LEARNING-PRINCIPLES.md)

Owner learns from concrete things first (Bret Victor: data, state, flow, time, concrete before
abstract; Peter Senge: learn by acting and seeing consequences within the learning horizon).
- Every lesson opens with **"Try it yourself"**: copy-paste commands run from repo root, each
  with expected PASS/FAIL + the key output line, what to look at, and an undo command if it
  edits tracked files. **See it fail first, then see it pass.** Run every command before
  documenting it.
- Explain mechanism and config only *after* the experiments.
- Make invisible state/flow visible: jshell sessions, lifecycle prints, reports, traces,
  `diff <(before) <(after)`. Offer the fastest feedback loop for each experiment.
- Use meaningful identity data (`cn=admin,ou=sales,o=acme`), never foo/bar.
- **Theory building (Naur):** use the `programming-as-theory-building` skill for every code
  change (answer world mapping / current theory / similarity / boundary / proof before
  coding). Keep `docs/THEORY.md` current for `afjava-core`. Each lesson has a "Theory of the
  tool" section. End non-trivial work with Theory / Changed / Verified / Risk.
- Turn surprises into experiments. Simulate long-horizon payoffs (CVEs, old secrets, weak
  tests) inside the lab so the consequence is visible now.

## Rules

- **One tool at a time.** Do not wire in tools from later lessons early.
- **After every code change run `./qa/check.sh`** and fix issues before calling work done.
- `afjava-core/` must always pass every wired gate. Never weaken a gate to get green —
  fix the code, or record a narrow suppression with a reason in `docs/DECISIONS.md`.
- `labs/lab-NN-*/` code is **intentionally flawed**. Do not "fix" lab bad-code examples
  unless the lesson step is the fix step; keep bad and good variants side by side.
- **Never invent tool output.** Lesson docs quote real output captured by running the tool here.
- Pin every tool/library version as a property in the parent `pom.xml`; record it in the tracker.
- Use the Maven Wrapper (`./mvnw`), not system `mvn`.
- Measure, don't guess: time added to `./qa/check.sh` gets recorded per tool.
- Lesson docs follow `docs/lessons/_TEMPLATE.md`. Include a Python analog where one exists
  (owner's Python setup: ruff, pylint, mypy, bandit, radon, cohesion, tach, vulture, pytest).

## Layout

- `pom.xml` — parent: versions, pluginManagement, profiles (`labs`; `full` added in later lessons)
- `afjava-core/` — sample identity-directory domain, always green
- `labs/` — per-tool sandboxes (excluded from default build)
- `config/` — shared tool configs (checkstyle, pmd, spotbugs, dependency-check, gitleaks)
- `qa/check.sh` — sanity runner, PASS/FAIL + timing per step; `--full` for slow tier
- `docs/LEARNING-PRINCIPLES.md` — how lessons are designed (concrete first, immediate feedback, Naur theory)
- `docs/THEORY.md` — program theory of the sample domain: world rule → code, invariants, boundaries, proof
- `docs/00-plan.md` — plan and rationale; `docs/TRACKER.md` — status; `docs/DECISIONS.md` — ADR log

## Tech

JDK 21, Maven 3.9.16 via `./mvnw`. Docker available for Testcontainers.
`.mvn/maven.config` forces project-scoped `.mvn/settings.xml` (Maven Central, no corporate
mirror) — don't remove it. groupId `io.github.kvinayagamoorthy`, base package
`io.github.kvinayagamoorthy.afjava`. Labs get `afjava.failOnWarning=false` from `labs/pom.xml`.
Pick plugin versions from Maven Central: newest non-beta 3.x (`<release>` may be a Maven-4 beta).
