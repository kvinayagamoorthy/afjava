# Lesson 01 — JUnit (Jupiter API, JUnit 6)

| | |
|---|---|
| **Status** | ✅ |
| **Version** | JUnit 6.1.3 (BOM), Surefire 3.6.0, maven-surefire-junit5-tree-reporter 2.0.0 |
| **Phase** | Tests |
| **Python analog** | pytest (and `unittest` for the instance-per-test model) |
| **Works on** | runtime (executes your code) |
| **Lab module** | `labs/lab-01-junit5` |

**JUnit 5 or 6?** The tool list says JUnit 5. The current release is **JUnit 6** (6.0 came out
in 2025). JUnit 6 keeps the JUnit 5 "Jupiter" programming model, so `@Test`, `@BeforeEach`,
`@ParameterizedTest` and `Assertions` are the same. The changes: one version number for all
JUnit artifacts, Java 17 minimum, and some old APIs removed. Everything here applies to JUnit 5
code too. See D008.

## 1. Try it yourself

Run from the repo root. Failing runs come first. All commands below were run here.

### 1.1 See assertion failures, then see the lab pass

| # | Command | Expect | Look at |
|---|---|---|---|
| 1 | `./mvnw -Plabs -pl labs/lab-01-junit5 -am verify -Dlab.excludedGroups= -Dgroups=on-purpose-failure` | **FAIL**: `Tests run: 6, Failures: 6` | The `[ERROR]` lines under `Failures:`. Which message tells you most without opening the code? (section 2.1) |
| 2 | `./mvnw -Plabs -pl labs/lab-01-junit5 -am verify` | **PASS**: `Tests run: 20, Failures: 0, Errors: 0, Skipped: 1` | The tree: each test with its display name and data; `[??]` = skipped |

`-Dlab.excludedGroups=` (empty) switches off the lab's default exclusion of the
`on-purpose-failure` and `order-dependent` tags. `-Dgroups=X` runs only tests tagged `X`.

### 1.2 See the flow: lifecycle order and test instances

| # | Command | Expect | Look at |
|---|---|---|---|
| 3 | `./mvnw -Plabs -pl labs/lab-01-junit5 -am verify -Dtest='LifecycleOrderTest,PerClassLifecycleTest'` | **PASS**, 4 tests | `[flow]` lines: a new `instance=` number for each test. `[per-class]` lines: same instance, state carried over |

### 1.3 See shared state break tests, depending on order

| # | Command | Expect |
|---|---|---|
| 4 | `./mvnw -Plabs -pl labs/lab-01-junit5 -am test -Dlab.excludedGroups= -Dgroups=order-dependent '-Djunit.jupiter.testmethod.order.default=org.junit.jupiter.api.MethodOrderer$Random' -Djunit.jupiter.execution.order.random.seed=1` | **FAIL**: `twoFailuresDoNotLock` |
| 5 | Same, with `seed=5` | **PASS**: all three |
| 6 | Same, with `seed=2` | **FAIL**: two tests |

Same code, same tests; only the order changes. (Section 2.3.)

### 1.4 Break the core, watch the tests catch it

Commit or stash your own changes first: the undo command is `git checkout`, which discards
uncommitted edits in that file.

| # | Command | Expect |
|---|---|---|
| 7 | `sed -i 's/+,;<>/+;<>/' afjava-core/src/main/java/io/github/kvinayagamoorthy/afjava/directory/domain/Rdn.java` | Comma no longer escaped in `Rdn.toString()` |
| 8 | `./qa/check.sh` | **FAIL**: `Tests run: 67, Failures: 1, Errors: 1` (section 2.4) |
| 9 | `git checkout -- afjava-core/src/main/java/io/github/kvinayagamoorthy/afjava/directory/domain/Rdn.java` | Undo |
| 10 | `sed -i 's/return extra > 0 \&\&/return extra >= 0 \&\&/' afjava-core/src/main/java/io/github/kvinayagamoorthy/afjava/directory/domain/DistinguishedName.java` | A DN now counts as its own descendant |
| 11 | `./qa/check.sh` | **FAIL**: `isDescendantOf(...)[4] expected: <false> but was: <true>` and `rootIsNoDescendant` |
| 12 | `git checkout -- afjava-core/src/main/java/io/github/kvinayagamoorthy/afjava/directory/domain/DistinguishedName.java` then `./qa/check.sh` | Undo; **PASS**, `Tests run: 67` |

### 1.5 Select what runs (fast loops)

| # | Command | Runs | Time |
|---|---|---|---|
| 13 | `./mvnw -o -q test -pl afjava-core -Dtest=RdnTest` | one class | ≈ 3.1 s |
| 14 | `./mvnw -Plabs -pl labs/lab-01-junit5 -am test '-Dtest=LockoutTableTest#lockingByTable'` | one method, all 5 rows | |
| 15 | `./mvnw -Plabs -pl labs/lab-01-junit5 -am test -Dgroups=unit` | 1 test | |
| 16 | `./mvnw -Plabs -pl labs/lab-01-junit5 -am test -Dgroups=component` | the slow one, 2 s | ≈ 5.4 s |

`-o` = offline: no remote repository checks, a little faster once dependencies are downloaded.

### 1.6 Poke the DN parser interactively

```bash
./mvnw -q compile
jshell --class-path afjava-core/target/classes
```

```java
import io.github.kvinayagamoorthy.afjava.directory.domain.*;
DistinguishedName.parse("CN=admin, OU=sales , o=acme")
DistinguishedName.parse("cn=Smith\\, John,o=acme").rdns().get(0).value()
DistinguishedName.parse("cn=J\\C3\\BCrgen,o=acme").rdns().get(0).value()
DistinguishedName.parse("cn=admin,ou=sales,o=acme").parent()
DistinguishedName.parse("cn=a+sn=b,o=acme")
DistinguishedName.parse("cn=admin,")
```

Real output (jshell needs `\\` inside Java strings for one `\`):

```text
$2 ==> cn=admin,ou=sales,o=acme
$3 ==> "Smith, John"
$4 ==> "Jürgen"
$5 ==> Optional[ou=sales,o=acme]
|  Exception java.lang.IllegalArgumentException: multi-valued RDN ('+') is not supported at position 4: "cn=a+sn=b,o=acme"
|  Exception java.lang.IllegalArgumentException: empty RDN at position 9: "cn=admin,"
```

Try your own DNs from eDirectory or IDM configs and see what the parser makes of them.

## 2. What you saw

### 2.1 Assertion messages compared (experiment 1)

All five tests make the same mistake: they expect lockout after 2 failed binds, but the limit
is 3. Real output:

```text
[ERROR]   AssertionMessagesTest.assertTrueHidesValues:26 expected: <true> but was: <false>
[ERROR]   AssertionMessagesTest.swappedArguments:34 expected: <2> but was: <3>
[ERROR]   AssertionMessagesTest.expectedFirstWithMessage:42 failures after two bad binds ==> expected: <3> but was: <2>
[ERROR]   AssertionMessagesTest.sequentialAssertsStopEarly:50 failure count ==> expected: <3> but was: <2>
[ERROR]   AssertionMessagesTest.assertAllReportsEverything:59 account cn=admin after two bad binds (2 failures)
	org.opentest4j.AssertionFailedError: failure count ==> expected: <3> but was: <2>
	org.opentest4j.AssertionFailedError: locked ==> expected: <true> but was: <false>
[ERROR]   ExceptionTestingTest.assertThrowsNoticesMissingException:32 Expected java.lang.IllegalArgumentException to be thrown, but nothing was thrown.
```

| Test | Code | What the message tells you |
|---|---|---|
| `assertTrue` | `assertTrue(registry.failureCount("cn=admin") == 3)` | Nothing: `expected <true> but was <false>`. Which values? Unknown |
| swapped | `assertEquals(actual, 3)` | **Wrong**: says the code returned 3 and you expected 2. Sends you debugging the wrong side |
| good | `assertEquals(3, actual, "failures after two bad binds")` | What, expected, actual |
| sequential | two `assertEquals` | Only the first failure; the `locked` check never ran |
| `assertAll` | `assertAll("...", () -> ..., () -> ...)` | Every failed check at once: `(2 failures)` |

Rule: **expected first, actual second**, as in `assertEquals(expected, actual)`. pytest
rewrites `assert a == b` to show both sides, so Python users never had to remember this.
JUnit doesn't do that.

### 2.2 Lifecycle and instances (experiment 3)

Real output:

```text
[flow] @BeforeAll        (static, once per class)
[flow]   instance=1  constructor
[flow]   instance=1  @BeforeEach      registry created, failures(cn=admin)=0
[flow]   instance=1  @Test twoFailuresDoNotLock  failures(cn=admin)=2
[flow]   instance=1  @AfterEach
[flow]   instance=2  constructor
[flow]   instance=2  @BeforeEach      registry created, failures(cn=admin)=0
[flow]   instance=2  @Test threeFailuresLock     failures(cn=admin)=3
[flow]   instance=2  @AfterEach
[flow] @AfterAll         (static, once per class); instances created: 2
```

```text
[per-class] @BeforeAll on instance 1676827075
[per-class] first  on instance 1676827075 failures=2
[per-class] second on instance 1676827075 failures=3
```

- **Default (`PER_METHOD`):** JUnit creates a **new instance of the test class for every test
  method**. Fields start fresh each time. `@BeforeAll`/`@AfterAll` must therefore be `static`:
  no instance exists yet. Python `unittest.TestCase` works the same way; pytest gets the same
  isolation through function-scoped fixtures.
- **`@TestInstance(PER_CLASS)`:** one instance for the whole class. Fields are shared, so the
  second test sees the first test's 2 failures. `@BeforeAll` can be non-static. Useful for
  expensive setup; dangerous because tests can now affect each other.

### 2.3 Shared state + order = flaky tests (experiments 4–6)

`StaticLockoutRegistry` keeps counters in a `static` map: one per JVM, shared by every test.
Each test is right on its own, but they leave failures behind for the next one. Real output for
eight seeds:

```text
seed=1  [OK] newAccountIsNotLocked [OK] threeFailuresLock [XX] twoFailuresDoNotLock
seed=2  [OK] threeFailuresLock [XX] twoFailuresDoNotLock [XX] newAccountIsNotLocked
seed=3  [OK] twoFailuresDoNotLock [XX] newAccountIsNotLocked [OK] threeFailuresLock
seed=4  [OK] twoFailuresDoNotLock [XX] newAccountIsNotLocked [OK] threeFailuresLock
seed=5  [OK] newAccountIsNotLocked [OK] twoFailuresDoNotLock [OK] threeFailuresLock
seed=6  [OK] threeFailuresLock [XX] twoFailuresDoNotLock [XX] newAccountIsNotLocked
seed=7  [OK] twoFailuresDoNotLock [OK] threeFailuresLock [XX] newAccountIsNotLocked
seed=8  [OK] twoFailuresDoNotLock [OK] threeFailuresLock [XX] newAccountIsNotLocked
```

Only one order in eight passes. JUnit's default method order is deterministic but deliberately
unspecified, so a suite like this can stay green for months, then break when someone adds,
renames or reorders a test. Shuffling the order on every run brings that failure forward to
today, and the seed makes it reproducible. That is why `qa/check.sh` now shuffles (2.6).

Fix: no mutable static state. `LockoutRegistry` keeps its map in an instance field and each
test builds its own registry.

### 2.4 Break-it on core: Failure vs Error (experiment 8)

With the comma no longer escaped, `./qa/check.sh` (seed 42) printed:

```text
[INFO]       +-- [XX] round trip: parse(dn.toString()) equals dn[2] cn=Smith, John,o=acme - 0.011 s
[INFO]    +-- [XX] toString escapes DN syntax characters (RFC 4514 section 2.4)[2] value "Smith, John" is written as "cn=Smith\\, John" - 0.005 s
[ERROR] Failures:
[ERROR]   RdnTest.toStringEscapes:85 expected: <cn=Smith\, John> but was: <cn=Smith, John>
[ERROR] Errors:
[ERROR]   DistinguishedNameTest.roundTrip(DistinguishedName)[2] » IllegalArgument missing '=' in RDN starting at position 9: "cn=Smith, John,o=acme"
[ERROR] Tests run: 67, Failures: 1, Errors: 1, Skipped: 0
FAIL: build: javac lint + unit tests (3991 ms)
```

- **Failure** = an assertion was false (`RdnTest`: wrong string).
- **Error** = the test threw something it didn't expect (round trip: re-parsing the unescaped
  string treats ` John` as a second RDN with no `=`).

The round-trip test shows the real-world consequence. A DN for "Smith, John" written without
escaping no longer names the same entry; an LDAP server would reject it or, worse, find a
different entry.

The `[XX]` lines come from the tree reporter; they show the failing row's data, which the
`[ERROR]` summary leaves out (`toStringEscapes:85` doesn't say which of the 12 rows failed).

### 2.5 Exception tests that cannot fail

`ExceptionTestingTest.tryCatchPassesSilently` claims `new LockoutRegistry(5)` throws. It doesn't,
yet the test passes in experiment 2: the `catch` block never runs, so no assertion runs. The
`assertThrows` version of the same wrong claim fails (experiment 1: "Expected
java.lang.IllegalArgumentException to be thrown, but nothing was thrown."). A test that can't
fail proves nothing. PIT (lesson 13) hunts for exactly these.

### 2.6 Surprises

- **Disabling Surefire's console reporter hides test stdout.** The tree reporter's docs suggest
  `<consoleOutputReporter><disable>true</disable></consoleOutputReporter>`. With it, every
  `[flow]` line vanished. Removed: seeing the flow matters more than a tidier console.
- **JUnit 6 quotes String arguments in display names.** `name = "\"{0}\" -> {1}"` printed
  `""admin,o=acme""`. Plain `{0}` already prints `"admin,o=acme"`. CSV values are Strings, so
  even numbers show quoted: `limit "3", "0" failures`.
- **First run of the 67 new core tests was green.** A test you've never seen fail might not
  be testing anything, so experiments 7–12 break the code on purpose to watch them fail.
- **Text blocks strip trailing spaces.** The CSV row `'trail ' | cn=trail\\ ` lost its trailing
  space and failed to compile (`illegal escape character`); the expected value has to be quoted:
  `'cn=trail\\ '`.

## 3. Theory of the tool (Naur)

| Question | Answer |
|---|---|
| **World mapping:** what real failure does it prevent? | Behaviour regressions: a change breaks a rule that used to hold (comma escaping, "not its own descendant"), and nobody notices until a customer's DN stops resolving. Tests run the rule against concrete cases on every build (experiments 7–12). |
| **Current theory:** why is it shaped this way? | *Instance per test* gives isolation by default (2.2). *Annotations, not inheritance* (`@Test` on any method, no `TestCase` base class) so tests are plain classes. *Platform vs engine*: the JUnit Platform launches tests and Jupiter is one engine on it, so Surefire, IDEs and other engines plug in. *Unspecified default order*, to discourage tests that depend on order (2.3). *Assertions as static methods*, expected first, which is where the swap trap comes from (2.1). |
| **Similarity:** closest known tool | pytest: `@ParameterizedTest`+`@CsvSource` ≈ `@pytest.mark.parametrize`; `@BeforeEach` ≈ function fixture; `@BeforeAll` ≈ class/module fixture; `@Tag` + `-Dgroups` ≈ markers + `-m`; `-Dtest=Class#method` ≈ `-k`/node id; `@Nested` ≈ test classes grouping; `@TestFactory` ≈ generating parametrize ids at collection time. Differences: no assert rewriting (2.1); JUnit runs inside Maven's lifecycle, not standalone; random order needs configuration (pytest needs the `pytest-randomly` plugin). |
| **Boundary:** what it cannot catch | Only the cases someone wrote down. It can't show the tests are strong enough (2.5; PIT, lesson 13), what code they never touch (JaCoCo, lesson 04), or bugs in code paths that need real infrastructure (Testcontainers, lesson 14). |
| **Proof:** which experiment showed it working | 8 and 11: a one-character change in `Rdn` or `DistinguishedName` turned the build red with the exact rule and data row. |

## 4. How it works

```text
mvn verify
  └─ surefire:test (phase "test")                  forks a JVM with test classpath
       └─ JUnit Platform Launcher                   discovers tests, applies filters (tags, -Dtest)
            └─ Jupiter engine                       creates instances, runs lifecycle, assertions
                 └─ events → Surefire reporters     console tree, target/surefire-reports/*.xml
```

- Surefire picks up classes named `*Test`, `Test*`, `*Tests`, `*TestCase` under `src/test/java`.
- `-D` user properties reach the forked test JVM as system properties; JUnit reads its
  configuration parameters (`junit.jupiter.*`) from there. That is how `check.sh` sets the
  random order.
- Reports: `target/surefire-reports/TEST-*.xml` (JUnit XML, read by CI servers) and `*.txt`.

See the flow yourself: experiment 3 prints it; `./mvnw -B verify | grep -- '--- '` shows
`surefire:3.6.0:test` running after `testCompile`.

## 5. Setup

Parent `pom.xml`:

```xml
<properties>
  <junit.version>6.1.3</junit.version>
  <surefire-tree-reporter.version>2.0.0</surefire-tree-reporter.version>
</properties>

<dependencyManagement>
  <dependencies>
    <dependency>                         <!-- BOM: one version for all JUnit artifacts -->
      <groupId>org.junit</groupId>
      <artifactId>junit-bom</artifactId>
      <version>${junit.version}</version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>
  </dependencies>
</dependencyManagement>

<!-- in pluginManagement -->
<plugin>
  <artifactId>maven-surefire-plugin</artifactId>
  <version>${maven-surefire-plugin.version}</version>
  <dependencies>
    <dependency>
      <groupId>me.fabriciorby</groupId>
      <artifactId>maven-surefire-junit5-tree-reporter</artifactId>
      <version>${surefire-tree-reporter.version}</version>
    </dependency>
  </dependencies>
  <configuration>
    <reportFormat>plain</reportFormat>
    <statelessTestsetInfoReporter
        implementation="org.apache.maven.plugin.surefire.extensions.junit5.JUnit5StatelessTestsetInfoTreeReporter"/>
  </configuration>
</plugin>
```

Module `pom.xml` (no version: the BOM supplies it):

```xml
<dependency>
  <groupId>org.junit.jupiter</groupId>
  <artifactId>junit-jupiter</artifactId>
  <scope>test</scope>
</dependency>
```

`junit-jupiter` pulls in `-api`, `-params` and `-engine`. A **BOM** ("bill of materials") is a
POM that only pins versions; importing it is like a constraints file in pip
(`pip install -c constraints.txt`).

## 6. Tuning

- **Random order everywhere** (`qa/check.sh`): class and method order shuffled with a printed
  seed; `AFJAVA_TEST_SEED=<n> ./qa/check.sh` replays an order. Classes that need a fixed order
  say so explicitly (`@TestMethodOrder(OrderAnnotation.class)` in `PerClassLifecycleTest`).
- **Tags in labs:** `lab.excludedGroups` property, default `on-purpose-failure,order-dependent`.
- **Display names carry the data:** `@DisplayName` states the rule, `@ParameterizedTest(name=...)`
  shows the row. The tree report then reads like a specification.
- **Suppression equivalent:** `@Disabled("reason")` shows up as skipped (`[??]`) with its reason.
  Use sparingly; a skipped test is a decision to postpone, so give the reason.

## 7. Cost and gate decision

| Metric | Value |
|---|---|
| Time added to `./qa/check.sh` | build step 1.5 s → 4.2 s (+2.7 s: Surefire fork + JUnit start; the 67 tests themselves ≈ 0.3 s) |
| Fastest feedback command | `./mvnw -o -q test -pl afjava-core -Dtest=RdnTest` ≈ 3.1 s; jshell for poking: instant |
| Noise | 0 false failures: 6 random-order runs of core (seeds 11, 222, 3333, 4444, 5555, 31337) all green |
| Gate | **fail** |
| Tier | T1 |

## 8. Overlap with other tools

JUnit is the runner others build on. AssertJ (02) replaces the assertions with readable ones.
Mockito (03) fakes collaborators inside JUnit tests. JaCoCo (04) measures what JUnit runs. PIT
(13) mutates code and reruns JUnit tests to see whether they notice. ArchUnit (12) rules are
JUnit tests.

## 9. Gotchas

- `assertEquals(expected, actual)`: order matters for the message (2.1).
- `try { ... } catch` without `fail()` passes silently (2.5). Use `assertThrows`.
- Static mutable state leaks between tests (2.3). So can `PER_CLASS` fields (2.2).
- `-DexcludedGroups=...` is ignored when the POM sets `<excludedGroups>` explicitly (as the
  lab does); the POM value wins. The lab uses its own property for that reason.
- Tests need not be `public` in Jupiter (package-private is idiomatic).
- `@CsvSource` text blocks: trailing spaces are stripped; quote values with `'...'`.
- `git checkout -- <file>` as an undo also throws away *your* uncommitted edits in that file.
  Commit before break-it experiments.

## 10. Cheat sheet

```bash
# see it fail (lab, on-purpose failures)
./mvnw -Plabs -pl labs/lab-01-junit5 -am verify -Dlab.excludedGroups= -Dgroups=on-purpose-failure
# see it pass (lab default)
./mvnw -Plabs -pl labs/lab-01-junit5 -am verify
# flaky-by-order demo, pick a seed
./mvnw -Plabs -pl labs/lab-01-junit5 -am test -Dlab.excludedGroups= -Dgroups=order-dependent \
  '-Djunit.jupiter.testmethod.order.default=org.junit.jupiter.api.MethodOrderer$Random' \
  -Djunit.jupiter.execution.order.random.seed=1

./mvnw -o -q test -pl afjava-core -Dtest=RdnTest                  # one class
./mvnw test -pl afjava-core '-Dtest=DistinguishedNameTest$Hierarchy'  # one @Nested group
./mvnw test -Dtest='LockoutTableTest#lockingByTable' ...          # one method
./mvnw test -Dgroups=unit                                         # by tag
./mvnw verify -DskipTests                                         # skip temporarily
AFJAVA_TEST_SEED=42 ./qa/check.sh                                 # replay a random order
ls afjava-core/target/surefire-reports/                           # XML + txt reports
```

## Done criteria

- [x] "Try it yourself" experiments written, each run here, fail-first, with expected output
- [x] Lab module created; tool fires; real output captured
- [x] Findings fixed; tool green on lab "good" code
- [x] State/flow of the tool made visible (lifecycle prints, tree report, seeds)
- [x] Theory of the tool: five questions answered, each tied to an experiment
- [x] Domain code changed? `docs/THEORY.md` updated (world rule, invariant, boundary, proof)
- [x] Mechanism and Python analog written up
- [x] At least one knob tuned (random order, display names) and one suppression (`@Disabled`) demonstrated
- [x] Wired into parent POM + `afjava-core` + `qa/check.sh`
- [x] Runtime and noise measured
- [x] Cheat sheet complete
- [x] TRACKER.md + DECISIONS.md updated; committed
