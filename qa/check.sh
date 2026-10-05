#!/usr/bin/env bash
#
# check.sh — afjava sanity runner. Run after every change.
#
# Usage:
#   ./qa/check.sh            fast tier (T1): every change
#   ./qa/check.sh --full     T1 + slow tier (T2): before push
#   ./qa/check.sh --labs     also build lab modules (keeps them compiling)
#
# Each step prints PASS/FAIL with elapsed time; a summary table follows.
# Exit code is non-zero if any step failed. New tools add a step here as their lesson wires them in.

set -u

RED='\033[0;31m'
GREEN='\033[0;32m'
CYAN='\033[0;36m'
BOLD='\033[1m'
NC='\033[0m'

REPO_ROOT="$(cd "$(dirname "$(readlink -f "$0")")/.." && pwd)"
cd "$REPO_ROOT" || exit 2

run_full=0
run_labs=0
for arg in "$@"; do
    case "$arg" in
        --full) run_full=1 ;;
        --labs) run_labs=1 ;;
        -h|--help) sed -n '3,11p' "$0"; exit 0 ;;
        *) echo "Unknown option: $arg" >&2; exit 2 ;;
    esac
done

# -B batch mode (no colour codes in logs), -ntp no download progress noise.
MVN=(./mvnw -B -ntp)

summary_file=$(mktemp)
log_dir=$(mktemp -d)
trap 'rm -f "$summary_file"; rm -rf "$log_dir"' EXIT
overall=0

now_ms() { echo $(( $(date +%s%N) / 1000000 )); }

# findings <log>: ERROR/WARNING lines minus Maven's generic help boilerplate.
findings() {
    grep -E '^\[(ERROR|WARNING)\]' "$1" \
        | grep -vE 'Help 1|-e switch|-X switch|resume the build|mvn <args>|For more information|cwiki\.apache|^\[ERROR\] *$' \
        | sed "s#${REPO_ROOT}/##"
}

# run_step <name> <command...>
# Captures output; on failure prints only the findings (full log if none matched).
run_step() {
    local name="$1"; shift
    local log="$log_dir/$(echo "$name" | tr -c 'A-Za-z0-9' '_').log"
    echo ""
    echo -e "${CYAN}${BOLD}[${name}]${NC}"
    local start; start=$(now_ms)
    "$@" > "$log" 2>&1
    local rc=$?
    local elapsed=$(( $(now_ms) - start ))
    if [ "$rc" -eq 0 ]; then
        echo -e "${GREEN}PASS${NC}: ${name} (${elapsed} ms)"
    else
        findings "$log" || cat "$log"
        echo -e "${RED}FAIL${NC}: ${name} (${elapsed} ms)"
        overall=1
    fi
    printf '%s\t%s\t%s\n' "$name" "$rc" "$elapsed" >> "$summary_file"
}

# ── T1: fast tier ─────────────────────────────────────────────────────────────
# compile (javac -Xlint:all -Werror) + package for every default-build module.
run_step "build: compile + javac lint" "${MVN[@]}" clean verify

# ── T2: slow tier ─────────────────────────────────────────────────────────────
if [ "$run_full" -eq 1 ]; then
    :   # PIT, Testcontainers, Checker Framework arrive in later lessons.
fi

# ── Labs: intentionally flawed code must still build (warnings expected) ─────
if [ "$run_labs" -eq 1 ]; then
    run_step "labs: build" "${MVN[@]}" -Plabs clean verify
fi

# ── Summary ───────────────────────────────────────────────────────────────────
echo ""
echo -e "${BOLD}Summary${NC}"
total=0
while IFS=$'\t' read -r name rc elapsed; do
    total=$(( total + elapsed ))
    if [ "$rc" -eq 0 ]; then status="${GREEN}PASS${NC}"; else status="${RED}FAIL${NC}"; fi
    printf '  %-40s %b %8s ms\n' "$name" "$status" "$elapsed"
done < "$summary_file"
printf '  %-40s %13s ms\n' "total" "$total"

if [ "$overall" -eq 0 ]; then
    echo -e "${GREEN}${BOLD}ALL CHECKS PASSED${NC}"
else
    echo -e "${RED}${BOLD}CHECKS FAILED${NC}"
fi
exit "$overall"
