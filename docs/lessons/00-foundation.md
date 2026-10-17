# Lesson 00 — Foundation: Maven Wrapper, parent POM, javac lint, `qa/check.sh`

| | |
|---|---|
| **Status** | ✅ |
| **Version** | Maven 3.9.16 (wrapper 3.3.4), JDK 21.0.12, maven-compiler-plugin 3.16.0 |
| **Phase** | Foundation |
| **Python analog** | `check_sanity.sh`, `requirements.txt` pinning, `python -W error` |
| **Works on** | source (javac) |
| **Lab module** | `labs/lab-00-javac-lint` |

## 0. Try it yourself

Run from the repo root, in order. Every command and result below was run and checked in
this repo.

### 0.1 Lab: see it fail, then see it pass

| # | Command | Expect | Look at |
|---|---|---|---|
| 1 | `./mvnw -Plabs -pl labs/lab-00-javac-lint -am clean compile` | **PASS** with 12 `[WARNING]` lines | Labs only report: `afjava.failOnWarning=false` from `labs/pom.xml` |
| 2 | `./mvnw -Dafjava.failOnWarning=true -Plabs -pl labs/lab-00-javac-lint -am clean compile` | **FAIL**: `warnings found and -Werror specified` | Same code, gate switched on from the command line |
| 3 | `javac -Xlint:all -Werror -d "$(mktemp -d)" labs/lab-00-javac-lint/src/main/java/io/github/kvinayagamoorthy/afjava/labs/javaclint/LintGoodExamples.java; echo "exit=$?"` | **PASS**: `exit=0`, no output | Fixed versions are clean |

`-pl <module>` builds just that module; `-am` ("also make") also builds what it depends on
(here the parent POMs). `-D` on the command line overrides a `<properties>` value from any POM.

### 0.2 Plain javac, no Maven: fastest loop (under 1 s)

Set `F` once in your shell, then run the rest:

```bash
F=labs/lab-00-javac-lint/src/main/java/io/github/kvinayagamoorthy/afjava/labs/javaclint/LintBadExamples.java
```

| # | Command | Expect | Look at |
|---|---|---|---|
| 4 | `javac -Xlint:all -d "$(mktemp -d)" $F` | `12 warnings`, exit 0 | Each warning names its category: `[rawtypes]`, `[fallthrough]`, ... |
| 5 | `javac -Xlint:all,-rawtypes,-unchecked -d "$(mktemp -d)" $F` | `7 warnings` | Turning off categories: `all` then subtract with `-<key>` |
| 6 | `javac -Xlint:all -Werror -d "$(mktemp -d)" $F; echo "exit=$?"` | **FAIL**: `1 error`, `11 warnings`, `exit=1` | **11, not 12.** See 0.3 |
| 7 | `javac --help-lint` | List of every lint key | |

### 0.3 Surprise: `-Werror` hides a warning

Compare the warning lists with and without `-Werror`:

```bash
diff <(javac -Xlint:all -d "$(mktemp -d)" $F 2>&1 | grep 'warning:') \
     <(javac -Xlint:all -Werror -d "$(mktemp -d)" $F 2>&1 | grep 'warning:')
```

Real output:

```text
12d11
< .../LintBadExamples.java:27: warning: [fallthrough] possible fall-through into case
```

With `-Werror`, javac stopped before it reported the fall-through warning. A file containing
*only* the fall-through bug does report it under `-Werror`:

```text
Ft.java:7: warning: [fallthrough] possible fall-through into case
      case 1:
      ^
error: warnings found and -Werror specified
1 error
1 warning
```

So with the gate on, fixing warnings can uncover new ones. Don't assume the first failure
list is complete; rerun after each round of fixes.

### 0.4 Break the core, watch `qa/check.sh` catch it

| # | Command | Expect |
|---|---|---|
| 8 | Run the `sed` command below the table | Adds a raw-type method to `Rdn` |
| 9 | `./qa/check.sh` | **FAIL**: `Rdn.java:[31,19] found raw type: java.util.List` |
| 10 | `git checkout -- afjava-core/src/main/java/io/github/kvinayagamoorthy/afjava/directory/domain/Rdn.java` | Undo |
| 11 | `./qa/check.sh` | **PASS**: `ALL CHECKS PASSED` |

Command for step 8:

```bash
sed -i 's|^  @Override|  static java.util.List raw() { return new java.util.ArrayList(); }\n\n  @Override|' \
  afjava-core/src/main/java/io/github/kvinayagamoorthy/afjava/directory/domain/Rdn.java
```

### 0.5 See the flow: which Maven steps ran, in which order

```bash
./mvnw -B clean verify | grep -E -- '--- |Building afjava'
```

Real output:

```text
[INFO] Building afjava :: parent 0.1.0-SNAPSHOT                           [1/2]
[INFO] --- clean:3.5.0:clean (default-clean) @ afjava-parent ---
[INFO] Building afjava :: core 0.1.0-SNAPSHOT                             [2/2]
[INFO] --- clean:3.5.0:clean (default-clean) @ afjava-core ---
[INFO] --- resources:3.5.0:resources (default-resources) @ afjava-core ---
[INFO] --- compiler:3.16.0:compile (default-compile) @ afjava-core ---
[INFO] --- resources:3.5.0:testResources (default-testResources) @ afjava-core ---
[INFO] --- compiler:3.16.0:testCompile (default-testCompile) @ afjava-core ---
[INFO] --- surefire:3.6.0:test (default-test) @ afjava-core ---
[INFO] --- jar:3.5.1:jar (default-jar) @ afjava-core ---
```

Format: `plugin:version:goal (execution-id) @ module`. The versions are the ones pinned in the
parent POM. Every later tool appears in this list as a new line at a specific phase; this is
the way to see *where* in the build a tool runs.

### 0.6 Poke a class interactively with jshell (no test needed)

```bash
./mvnw -q compile
jshell --class-path afjava-core/target/classes
```

Then type, one line at a time:

```java
import io.github.kvinayagamoorthy.afjava.directory.domain.Rdn;
new Rdn("CN", "admin")
new Rdn("cn", "admin").equals(new Rdn("CN", "admin"))
new Rdn("1cn", "admin")
new Rdn("cn", "")
/exit
```

Real output:

```text
jshell> $2 ==> cn=admin
jshell> $3 ==> true
jshell> |  Exception java.lang.IllegalArgumentException: invalid attribute type: '1cn'
|        at Rdn.<init> (Rdn.java:23)
|        at (#4:1)
jshell> |  Exception java.lang.IllegalArgumentException: empty value for attribute type: cn
|        at Rdn.<init> (Rdn.java:26)
|        at (#5:1)
```

Tip: lines piped on stdin, as above, echo each value. Running a script file
(`jshell -q file.jsh`) shows only exceptions, not the values.

You can see the data (`cn=admin`, type lower-cased), the state (`equals` is true because the
record compares normalised components), and where validation fails (`Rdn.java:23`). Python
analog: `python -i` with the package on `PYTHONPATH`.

## 1. What it catches and why it matters

Before adding any external tool, the compiler itself is the cheapest static analyzer you
have. `javac -Xlint:all` turns on every warning category (raw types, unchecked generics,
fall-through, deprecated-for-removal APIs, missing `serialVersionUID`, constant division by
zero, ...). With `-Werror` (Maven: `failOnWarning`) these become build failures: zero
extra runtime, zero extra dependencies, caught at the earliest possible moment.

The rest of the lesson is plumbing every later lesson depends on:

| Piece | Why |
|---|---|
| Maven Wrapper (`./mvnw`) | Same Maven version on every machine and CI; no "works with my Maven" |
| Project-scoped `.mvn/settings.xml` | Repo builds from Maven Central regardless of `~/.m2/settings.xml` (corporate mirror, VPN) |
| Parent POM with pinned plugin versions | Maven's implicit plugin versions change between Maven releases → non-reproducible builds |
| `afjava-core` vs `labs/` split | Main build always green; flawed code lives in labs (see D002) |
| `qa/check.sh` | One command, PASS/FAIL + timing per step, only the findings on failure |

## 2. How it works

- **Maven reactor:** parent POM (`packaging=pom`) lists modules; `./mvnw verify` builds them in
  dependency order. Children inherit `<properties>` and `<pluginManagement>`.
- **`pluginManagement` vs `plugins`:** `pluginManagement` only *configures* a plugin (version,
  settings) for whoever uses it; `plugins` actually *binds* it to the build. Core plugins
  (compiler, surefire, jar...) are bound implicitly by `packaging=jar`, so managing them is enough.
- **`maven.compiler.release=21`:** unlike `source`/`target`, `--release` also checks you only
  call JDK APIs that exist in Java 21. (C analogy: compiling against the right headers, not just
  the right `-std=`.)
- **`.mvn/maven.config`:** extra CLI args applied to every Maven run in this repo. Holds
  `--settings ${maven.multiModuleProjectDirectory}/.mvn/settings.xml`. One token per line;
  the variable makes it work from sub-module directories too.
- **`distributionType=only-script`:** wrapper is a shell script that downloads Maven on first
  use; no `maven-wrapper.jar` binary in git.
- **Property-driven gate:** compiler config uses `<failOnWarning>${afjava.failOnWarning}</failOnWarning>`;
  parent sets `true`, `labs/pom.xml` sets `false`. One switch, inherited by every lab.

What javac lint **cannot** see: anything requiring data-flow or cross-method reasoning
(nulls, resource leaks, wrong `equals`, ...). That is what Error Prone, NullAway and SpotBugs add.

## 3. Setup

Parent `pom.xml` (excerpt):

```xml
<properties>
  <maven.compiler.release>21</maven.compiler.release>
  <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
  <project.build.outputTimestamp>2026-10-05T00:00:00Z</project.build.outputTimestamp>
  <afjava.failOnWarning>true</afjava.failOnWarning>
  <maven-compiler-plugin.version>3.16.0</maven-compiler-plugin.version>
  <!-- ... every core plugin pinned ... -->
</properties>

<pluginManagement>
  <plugins>
    <plugin>
      <groupId>org.apache.maven.plugins</groupId>
      <artifactId>maven-compiler-plugin</artifactId>
      <version>${maven-compiler-plugin.version}</version>
      <configuration>
        <compilerArgs><arg>-Xlint:all</arg></compilerArgs>
        <showWarnings>true</showWarnings>
        <failOnWarning>${afjava.failOnWarning}</failOnWarning>
      </configuration>
    </plugin>
  </plugins>
</pluginManagement>

<profiles>
  <profile>
    <id>labs</id>
    <modules><module>labs</module></modules>
  </profile>
</profiles>
```

`.mvn/maven.config`:

```text
--settings
${maven.multiModuleProjectDirectory}/.mvn/settings.xml
```

Wrapper generated once with `mvn -N wrapper:wrapper -Dmaven=3.9.16`.

Finding stable plugin versions: Maven Central `maven-metadata.xml` `<release>` currently returns
**4.x betas** (built for Maven 4). For Maven 3.9 pick the newest non-beta 3.x:

```bash
curl -s https://repo.maven.apache.org/maven2/org/apache/maven/plugins/maven-compiler-plugin/maven-metadata.xml \
  | grep -o '<version>[^<]*' | cut -d'>' -f2 | grep -Ev 'alpha|beta|-M[0-9]|^4' | sort -V | tail -1
```

## 4. Lab: make it fire

`labs/lab-00-javac-lint/.../LintBadExamples.java` has one method per lint category:

```java
public List<String> rawTypes() {
  List names = new ArrayList();
  names.add("cn=admin");
  names.add(42); // compiles: raw type accepts anything
  return names;
}

public int fallthrough(int scope) {
  int cost = 0;
  switch (scope) {
    case 0:
      cost += 1;          // falls into case 1
    case 1:
      cost += 10;
      break;
    default:
      cost += 100;
  }
  return cost;
}

public Integer deprecated() { return new Integer(7); }
public String redundantCast(String dn) { return (String) dn; }
public String staticViaInstance(Thread t) { return t.currentThread().getName(); }
public static class Entry implements Serializable { private String dn = ""; /* ... */ }
public int divZero() { return 1 / 0; }
public boolean empty(boolean flag) { if (flag); return flag; }
```

Real output, `./mvnw -Plabs -pl labs/lab-00-javac-lint -am clean compile` (paths shortened to `.../`):

```text
[WARNING] .../LintBadExamples.java:[15,5] found raw type: java.util.List
[WARNING] .../LintBadExamples.java:[15,22] found raw type: java.util.ArrayList
[WARNING] .../LintBadExamples.java:[16,14] unchecked call to add(E) as a member of the raw type java.util.List
[WARNING] .../LintBadExamples.java:[17,14] unchecked call to add(E) as a member of the raw type java.util.List
[WARNING] .../LintBadExamples.java:[18,12] unchecked conversion
[WARNING] .../LintBadExamples.java:[38,12] Integer(int) in java.lang.Integer has been deprecated and marked for removal
[WARNING] .../LintBadExamples.java:[43,12] redundant cast to java.lang.String
[WARNING] .../LintBadExamples.java:[48,13] static method should be qualified by type name, java.lang.Thread, instead of by an expression
[WARNING] .../LintBadExamples.java:[52,17] serializable class io.github.kvinayagamoorthy.afjava.labs.javaclint.LintBadExamples.Entry has no definition of serialVersionUID
[WARNING] .../LintBadExamples.java:[62,16] division by zero
[WARNING] .../LintBadExamples.java:[67,14] empty statement after if
[WARNING] .../LintBadExamples.java:[27,7] possible fall-through into case
[INFO] BUILD SUCCESS
```

Labs report but do not fail. Same lab with the gate forced on
(`-Dafjava.failOnWarning=true`):

```text
[ERROR] COMPILATION ERROR :
[ERROR] .../LintBadExamples.java: warnings found and -Werror specified
[INFO] BUILD FAILURE
```

Note `names.add(42)`: no warning on that line specifically. The raw type already switched off
generic checking; the type error surfaces only at runtime as `ClassCastException` in some
distant caller. That is why `rawtypes` must be a gate, not a suggestion.

## 5. Fix

`LintGoodExamples.java` (zero warnings):

| Bad | Good | Why |
|---|---|---|
| `List names = new ArrayList()` | `List<String> names = new ArrayList<>()` | `add(42)` becomes a compile error |
| `switch` with `case:` + missing `break` | `return switch (scope) { case 0 -> 11; ... };` | Arrow form cannot fall through; switch is an expression; compiler checks exhaustiveness for enums/sealed types |
| `new Integer(7)` | `Integer.valueOf(7)` | Removed API; `valueOf` uses the cache |
| `(String) dn` | `dn` | Noise hides intent |
| `t.currentThread()` | `Thread.currentThread()` | Reader thinks it's about `t`; it isn't |
| no `serialVersionUID` | `@Serial private static final long serialVersionUID = 1L;` | `@Serial` makes javac verify the declaration's signature |
| `1 / 0` | validate divisor | — |
| `if (flag);` | always brace if-bodies | Stray `;` cannot become the whole body |

## 6. Tuning and false positives

- **Knobs:** `-Xlint:all` then subtract with `-Xlint:-<key>` (e.g. `-Xlint:all,-serial`).
  `javac --help-lint` lists every key.
- **Legit suppression** (`LintSuppression.java`): generic array creation needs an unchecked cast.
  Suppress on the *single local variable*, with a comment saying why it's safe:

  ```java
  // Safe: the array never escapes as T[] to callers; only single elements are returned.
  @SuppressWarnings("unchecked")
  T[] created = (T[]) new Object[capacity];
  this.items = created;
  ```

  Never at method or class level: that silences future, unrelated warnings too.
- **Rules disabled:** none.

## 7. Cost and gate decision

| Metric | Value |
|---|---|
| Time added to `./qa/check.sh` | 0 (part of compile); whole check ≈ 1.5 s for core |
| Noise on clean core | 0 findings |
| Gate | **fail** (`failOnWarning=true`) |
| Tier | T1 |

Reasoning: free, no false positives met, earliest possible feedback.

`./qa/check.sh` failure output (raw type injected into `Rdn.java`): shows exactly where, no Maven boilerplate:

```text
[build: compile + javac lint]
[WARNING] COMPILATION WARNING :
[WARNING] afjava-core/src/main/java/.../Rdn.java:[31,19] found raw type: java.util.List
[WARNING] afjava-core/src/main/java/.../Rdn.java:[31,53] found raw type: java.util.ArrayList
[ERROR] COMPILATION ERROR :
[ERROR] afjava-core/src/main/java/.../Rdn.java: warnings found and -Werror specified
[ERROR] Failed to execute goal org.apache.maven.plugins:maven-compiler-plugin:3.16.0:compile (default-compile) on project afjava-core: Compilation failure
FAIL: build: compile + javac lint (1215 ms)

Summary
  build: compile + javac lint              FAIL     1215 ms
  total                                             1215 ms
CHECKS FAILED
```

The first version used `mvn -q`; on failure it printed only "warnings found and -Werror
specified", with no line numbers. Quiet mode suppresses the `[WARNING]` lines that hold the
actual finding. Fixed by dropping `-q` and filtering instead.

## 8. Overlap with other tools

javac lint ⊂ Error Prone (lesson 08), which runs *inside* javac and adds ~500 checks. Keep
`-Xlint:all` anyway: free and authoritative for language-level issues.

## 9. Gotchas

- `.mvn/maven.config` needs **one token per line**: `-s .mvn/settings.xml` on one line fails
  with `The specified user settings file does not exist: .../ .mvn/settings.xml` (leading space).
- `~/.m2/settings.xml` with a `<mirrorOf>central</mirrorOf>` mirror silently routes *all*
  downloads through that mirror; unreachable mirror → `No plugin found for prefix 'wrapper'`.
- `maven-metadata.xml` `<release>` ≠ latest stable for Maven 3 (see §3).
- `@SuppressWarnings("PMD")` or other unknown keys compile silently: javac ignores strings it
  doesn't know. Typos in suppressions don't warn.

## 10. Cheat sheet

```bash
# See it fail (lab with gate forced on)
./mvnw -Dafjava.failOnWarning=true -Plabs -pl labs/lab-00-javac-lint -am clean compile

# See it pass / report only (lab default)
./mvnw -Plabs -pl labs/lab-00-javac-lint -am clean compile

# Fastest loop: javac directly on one file
javac -Xlint:all -d "$(mktemp -d)" <File.java>             # report
javac -Xlint:all -Werror -d "$(mktemp -d)" <File.java>     # gate
javac -Xlint:all,-rawtypes -d "$(mktemp -d)" <File.java>   # minus one category
javac --help-lint                                           # list lint keys

# Whole repo
./qa/check.sh                       # fast gates
./qa/check.sh --labs                # + build labs (warnings expected, must not fail)
./mvnw verify                       # plain build, core only
./mvnw -Dafjava.failOnWarning=false compile   # temporarily report-only

# See the flow: plugin goals in execution order
./mvnw -B clean verify | grep -E -- '--- |Building afjava'

# Poke core classes interactively
./mvnw -q compile && jshell --class-path afjava-core/target/classes
```

## Done criteria

- [x] "Try it yourself" experiments written, each run here, fail-first, with expected output
- [x] Read: defect class, mechanism, Python analog written up
- [x] Lab module created; tool fires; real output captured above
- [x] Findings fixed; tool green on lab "good" code
- [x] At least one knob tuned and one suppression demonstrated
- [x] Wired into parent POM + `afjava-core` + `qa/check.sh`
- [x] Runtime and noise measured
- [x] Cheat sheet complete
- [x] TRACKER.md + DECISIONS.md updated; committed
