# Learning principles

How lessons, labs and sample code in this repo are designed. Anyone, human or agent, who writes
a lesson, lab or demo here follows this document.

## Sources

**Bret Victor, "Inventing on Principle" (2012).** Creators need an immediate connection to what
they create. When you make a change, you should see its effect at once, not after a long
compile/deploy/inspect cycle.

**Bret Victor, "Learnable Programming" (2012).** A learner should be able to see what the
program is doing, not imagine it. The ideas used here:

| Idea | Meaning for this repo |
|---|---|
| **Data** | Show real values, not descriptions of values. |
| **State** | Make the state at each step visible. |
| **Flow** | Make the order of execution visible. |
| **Time** | Show how things change across steps and runs; keep the feedback loop short. |
| **Concrete first, then abstract** | Start with one concrete example you can poke. Generalise only after seeing several. |
| **Create by reacting** | Change one thing, see the consequence, change again. |
| **Show comparisons** | Put bad next to good, before next to after, so the difference is visible. |

**Peter Senge, "The Fifth Discipline".**

> The most powerful learning comes from direct experience — through direct trial and error —
> through taking action and seeing the consequences of that action; then taking a new and
> different action. We each have a "learning horizon", a breadth of vision in time and space
> within which we assess our effectiveness. When our actions have consequences beyond our
> learning horizon, it becomes impossible to learn from direct experience. Cycles are
> particularly hard to see, and thus learn from, if they last longer than a year or two.

Quality tools are a perfect example of the learning-horizon problem: their payoff (a bug that
never reaches a customer, a CVE that is never shipped) usually lies months or years beyond the
horizon. So each lesson has to **pull the consequence inside the horizon**: plant the defect,
run the tool, watch it get caught, within seconds.

## Owner's learning style

The owner needs to see **concrete things first, a lot of them**, before forming abstractions.
Explanations of mechanism, configuration theory and general rules come *after* hands-on
experiments, never before.

## Rules for lessons, labs and demos

1. **Every lesson starts with "Try it yourself".** A numbered list of copy-paste commands, run
   from the repo root, each with:
   - the **expected result** (PASS or FAIL, plus the key output line to look for),
   - **what to look at** (file, line, report path, number),
   - an **undo command** if the experiment modifies tracked files (`git checkout -- <path>`).
   Prefer experiments that change nothing tracked: `-D` properties, `-pl` on a lab, scratch copies.
2. **See it fail, then see it pass.** For every gate: one command that makes it fail, and one that
   makes it pass. The failing run comes first.
3. **Concrete before abstract.** Lesson order: try → observe → explain the mechanism →
   generalise into config. A rule or config option is introduced only after it has been seen
   firing on concrete code.
4. **Real data only.** Sample data is identity data that means something (`cn=admin,ou=sales,o=acme`),
   not `foo`/`bar`. Output shown in docs is captured by running the command here; never invented.
5. **Make state and flow visible.** Where a tool has invisible behaviour, add a way to see it:
   print lifecycle order (JUnit), show recorded interactions (Mockito), show the mutant that
   survived and its diff (PIT), open the coloured coverage report (JaCoCo), print the Maven
   phases and plugin goals that ran.
6. **Interactive poking.** Offer `jshell --class-path <module>/target/classes` sessions and
   single-file `java File.java` programs so a class can be tried without writing a test first.
7. **One change, one rerun.** "Break it" experiments change exactly one thing, so cause and
   effect stay connected. Show the comparison with `diff <(before) <(after)` when useful.
8. **Short loops, measured.** Show the fastest command for each experiment (single module
   `-pl`, offline `-o`, direct `javac`). Record seconds. Feedback beyond ~10 s gets a faster
   alternative or a note on why not.
9. **Pull long-horizon consequences in.** If a tool's value normally appears late (dependency
   CVEs, secrets in old commits, weak tests), simulate it now: pin an old vulnerable library in a
   lab, commit a fake secret to a throwaway branch, plant a bug the tests miss.
10. **Surprises are lessons.** When a tool behaves unexpectedly (e.g. `-Werror` hiding the
    fall-through warning until the others are fixed), turn it into an experiment, not a footnote.
