# afjava — Learning Plan

Antifragile Java: get hands-on with the Java quality toolchain, **one tool at a time**, until
each tool's purpose, output, noise level, cost and gate decision are understood from experience
rather than from reading about it.

Status lives in [TRACKER.md](TRACKER.md). This file is the *what* and *why*; the tracker is the
*where are we*.

---

## 1. Goals

1. For every tool: know **what defect class it catches**, see it fire on real code, fix the
   finding, tune false positives, and decide where it belongs in the feedback loop.
2. End with a **reusable project template** (parent POM + configs + `qa/check.sh` + CI) that
   can be dropped into any new Java project at OpenText or elsewhere.
3. Keep the main code base **always green** — every lesson ends with the new gate wired in and
   `./qa/check.sh` passing. Same discipline as scim-tools `check_sanity.sh`.

## 2. Guiding principles

| Principle | Consequence for this repo |
|---|---|
| Developer attention is the costly resource | Fast gates run on every change; slow gates are tiered (§6). Noisy rules get tuned or removed, never ignored. |
| Shift left | Prefer compile-time (Error Prone, NullAway) over bytecode (SpotBugs) over tests over QA. Each lesson records *how early* its tool catches its defect class. |
| Antifragile (Taleb) | Tools that stress code with variation — mutation testing, property-based testing, fuzzing — are first-class, not extras. Every bug found later should become a new rule or test (system gains from stressors). |
| Skin in the game | A gate that fails the build is real; a report nobody reads is not. Each lesson ends with an explicit gate decision: **fail**, **warn**, or **drop**. |
| No fabricated knowledge | Lesson docs quote **real captured tool output** from this repo, not invented examples. |

## 3. Tech baseline

- **JDK 21** (installed: OpenJDK 21.0.12), **Maven 3.9.x** via Maven Wrapper (`./mvnw`).
- Docker/Podman available → Testcontainers is feasible locally.
- Tool versions pinned in the parent `pom.xml` `<properties>`; picked as latest stable at the
  time each lesson starts and recorded in the tracker.

## 4. Repo layout (target)

```
afjava/
├── CLAUDE.md                 # Agent working rules
├── README.md
├── pom.xml                   # Parent POM: versions, pluginManagement, profiles
├── mvnw, .mvn/               # Maven Wrapper (reproducible Maven version)
├── config/                   # Shared tool configs
│   ├── checkstyle/checkstyle.xml
│   ├── pmd/ruleset.xml
│   ├── spotbugs/exclude.xml
│   ├── dependency-check/suppressions.xml
│   └── gitleaks/gitleaks.toml
├── afjava-core/              # Sample domain. ALL gates ON. Must always be green.
├── labs/                     # One sandbox module per tool. Intentionally flawed code.
│   └── lab-NN-<tool>/        # Excluded from default build; run with -Plabs or -pl
├── qa/
│   └── check.sh              # Sanity runner: PASS/FAIL + timing per step (like check_sanity.sh)
└── docs/
    ├── 00-plan.md            # This file
    ├── TRACKER.md            # Status + resume point + session log
    ├── DECISIONS.md          # Short ADR-style log of gate/config decisions
    └── lessons/
        ├── _TEMPLATE.md
        └── NN-<tool>.md      # Written during each lesson
```

**Why two kinds of modules:**
- `afjava-core` — realistic code that must pass every gate. Shows the *steady state*.
- `labs/lab-NN-*` — deliberately bad code that makes the tool fire. Shows *what the tool
  catches*. Labs never break the main build.

### Sample domain: mini identity directory

Chosen to match LDAP / eDirectory / IDM background and to give every tool something real to bite:

| Domain piece | Gives tools something to catch |
|---|---|
| `DistinguishedName` parser (`cn=x,ou=y,o=z`, escaping) | Edge cases → PIT mutants, property tests, null handling |
| LDAP filter builder/parser | LDAP injection → find-sec-bugs; complexity → Checkstyle/PMD |
| `User`, `Group`, membership | Records, immutability → Error Prone; cohesion → PMD GodClass |
| Password policy evaluator | Branch-heavy → JaCoCo branch coverage, PIT, cognitive complexity |
| XML import of users (DirXML-ish) | XXE → find-sec-bugs |
| `DirectoryRepository` (in-memory + LDAP impl) | Mockito at boundary; Testcontainers OpenLDAP; ArchUnit layering |
| Layers: `domain` → `service` → `adapter` | ArchUnit rules (like tach) |

## 5. Lesson sequence

Order rationale: build the **safety net first** (tests), then **cheap fast gates** (format,
style), then **deeper bug finders** (compile time → bytecode), then **security**, then
**architecture**, then **test effectiveness**, then **integration**.

| # | Tool | Phase | Python analog | Defect class | Size |
|---|---|---|---|---|---|
| 00 | Maven Wrapper, parent POM, `qa/check.sh` | Foundation | `check_sanity.sh` | — | S |
| 01 | JUnit 5 (Jupiter) | Tests | pytest | Behaviour regressions | M |
| 02 | AssertJ | Tests | pytest asserts | Unreadable failures | S |
| 03 | Mockito | Tests | unittest.mock | Untestable boundaries | M |
| 04 | JaCoCo | Tests | coverage.py | Untested code | S |
| 05 | Spotless (google-java-format) | Format | ruff format | Style churn in diffs | S |
| 06 | Checkstyle | Style | pylint (style), radon cc | Conventions, complexity, size | M |
| 07 | PMD + CPD | Style/Design | pylint, radon, cohesion, vulture | Design smells, dead code, cognitive complexity, copy-paste | M |
| 08 | Error Prone | Compile-time bugs | mypy / pylint errors | Common Java bug patterns | M |
| 09 | NullAway (+ JSpecify) | Compile-time types | mypy `Optional` | NullPointerException | M |
| 10 | SpotBugs | Bytecode bugs | — | Bug patterns in compiled code | M |
| 11 | Find Security Bugs | Security | bandit | Injection, XXE, weak crypto | M |
| 12 | ArchUnit | Architecture | tach | Layer/dependency violations, cycles | M |
| 13 | PIT (pitest) | Test effectiveness | mutmut | Tests that don't really test | M |
| 14 | Testcontainers | Component tests | component tests w/ live server | Integration bugs (real OpenLDAP) | L |
| 15 | OWASP Dependency-Check | Supply chain | pip-audit | Known CVEs in dependencies | S |
| 16 | gitleaks | Secrets | detect-secrets | Committed secrets (fast, pre-commit) | S |
| 17 | TruffleHog | Secrets | — | Verified live secrets, full history | S |
| 18 | Checker Framework | Pluggable types | mypy (strict) | Nullness, tainting, resource leaks, format strings | L |
| 19 | Integration | Pipeline | pre-commit + check_sanity | Gate tiering, git hooks, Claude Code hook, GitHub Actions | M |
| 20 | Capstone + template | Playbook | — | Reusable starter + one-page playbook | M |

Checker Framework sits late on purpose: after NullAway, so the two can be compared on the same
code (speed vs soundness), and because it is the heaviest to adopt.

### Backlog (optional, after core 00–20)

| Tool | Why it is interesting |
|---|---|
| **jqwik** (property-based testing) | Antifragile: random inputs find edge cases you didn't imagine (Hypothesis analog) |
| **Jazzer** (coverage-guided fuzzing) | Stress parsers (DN, LDAP filter) with mutation-driven input |
| Maven Enforcer | Ban dependencies, require JDK/Maven version, dependency convergence |
| `dependency:analyze` | Unused / undeclared deps (vulture for the POM) |
| OpenRewrite | Automated large-scale refactoring and migrations |
| CycloneDX | SBOM generation |
| Modernizer / jdeps / JPMS | Legacy API use; module boundaries enforced by the compiler |
| japicmp / Revapi | Binary/API compatibility between releases |
| Renovate / Dependabot | Automated dependency updates |
| JMH | Micro-benchmarks done right |

## 6. Gate tiering (target end state)

Attention budget drives where each tool runs. Numbers are targets; real timings get recorded
per lesson.

| Tier | When | Budget | Tools |
|---|---|---|---|
| T0 pre-commit | `git commit` | < 10 s | Spotless check, gitleaks (staged) |
| T1 fast check | every change (`./qa/check.sh`) | < 90 s | compile w/ Error Prone + NullAway, Checkstyle, PMD/CPD, SpotBugs + FSB, unit tests, ArchUnit, JaCoCo gate |
| T2 full check | before push (`./qa/check.sh --full`) | minutes | T1 + PIT (incremental), Testcontainers component tests, Checker Framework |
| T3 scheduled / CI | nightly or CI | any | OWASP Dependency-Check, TruffleHog full history, full PIT |

## 7. Per-lesson workflow (Definition of Done)

Every lesson follows [lessons/_TEMPLATE.md](lessons/_TEMPLATE.md):

1. **Read** — what it catches, how it works (source / bytecode / runtime), Python analog.
2. **Lab** — create `labs/lab-NN-<tool>` with flawed code; run tool; capture real output.
3. **Fix** — fix findings; see the tool go green.
4. **Tune** — try config knobs, suppressions, one false positive and how to silence it narrowly.
5. **Wire** — add tool to parent POM + `afjava-core` + `qa/check.sh` with a gate decision.
6. **Measure** — record runtime added to `./qa/check.sh` and noise level.
7. **Write** — finish `docs/lessons/NN-<tool>.md` incl. cheat sheet.
8. **Track** — update TRACKER.md (status, versions, timings, next step); log decisions in
   DECISIONS.md; commit.

A lesson is ✅ only when all eight are done and `./qa/check.sh` is green.

## 8. Resume protocol

To pick up any time (human or agent):

1. Open [TRACKER.md](TRACKER.md) → **Current focus** section says exact lesson and next step.
2. Open that lesson's doc → unchecked boxes in its *Done criteria* show what remains.
3. Run `./qa/check.sh` to confirm the tree is green before continuing.
