# CLAUDE.md — afjava

Learning repo: hands-on with the Java quality toolchain, one tool at a time. Owner is an
experienced C/C++/Python engineer (identity/LDAP background), newer to Java tooling. Explain Java
specifics; skip generic programming basics.

## Start of every session

1. Read `docs/TRACKER.md` → **Current focus**. That is the resume point.
2. Read the current lesson doc in `docs/lessons/` → unchecked *Done criteria* = remaining work.
3. Run `./qa/check.sh` (once it exists) to confirm the tree is green before changing anything.

## End of every session / lesson step

- Update `docs/TRACKER.md`: Current focus, lesson row (status, version, gate, tier, time
  added), and append one row to the Session log.
- Log any gate/config/rule decision in `docs/DECISIONS.md`.
- Tick completed boxes in the lesson doc.

## Rules

- **One tool at a time.** Do not wire in tools from later lessons early.
- **After every code change run `./qa/check.sh`** and fix issues before calling work done.
- `afjava-core/` must always pass every wired gate. Never weaken a gate to get green —
  fix the code, or record a narrow suppression with a reason in `docs/DECISIONS.md`.
- `labs/lab-NN-*/` code is **intentionally flawed**. Do not "fix" lab bad-code examples
  unless the lesson step is the fix step; keep bad and good variants side by side.
- **Never invent tool output.** Lesson docs quote real output captured by running the tool here.
- Pin every tool/library version as a property in the parent `pom.xml`; record it in the tracker.
- Use the Maven Wrapper (`./mvnw`), not system `mvn`, once it exists.
- Measure, don't guess: time added to `./qa/check.sh` gets recorded per tool.
- Lesson docs follow `docs/lessons/_TEMPLATE.md`. Include a Python analog where one exists
  (owner's Python setup: ruff, pylint, mypy, bandit, radon, cohesion, tach, vulture, pytest).

## Layout

- `pom.xml` — parent: versions, pluginManagement, profiles (`labs`, `full`)
- `afjava-core/` — sample identity-directory domain, always green
- `labs/` — per-tool sandboxes (excluded from default build)
- `config/` — shared tool configs (checkstyle, pmd, spotbugs, dependency-check, gitleaks)
- `qa/check.sh` — sanity runner, PASS/FAIL + timing per step; `--full` for slow tier
- `docs/00-plan.md` — plan and rationale; `docs/TRACKER.md` — status; `docs/DECISIONS.md` — ADR log

## Tech

JDK 21, Maven 3.9 (wrapper). Docker available for Testcontainers.
