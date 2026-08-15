#!/usr/bin/env python3
"""Check that every ```java fence in the lessons is a complete Java compilation unit.

The rule (see AUTHORING.md > "Code fences"): a reader must be able to copy any java
fence into a file without getting a wall of red. That means no bare statements and no
bare class members at fence top level -- every fence opens with a type declaration.

Two tiers, both checked here:

  * every fence          must PARSE as a compilation unit. Unresolved symbols are fine
                         (an excerpt of Application legitimately references Status), but
                         syntax errors are not.
  * *Example / *Demo     must additionally COMPILE and RUN clean with no classpath, so a
                         standalone teaching example is genuinely standalone.

Usage:  python3 lessons/tools/check-snippets.py [path ...]
Exit code 1 if any fence fails.
"""

import re
import subprocess
import sys
import tempfile
from pathlib import Path

FENCE = re.compile(r"^```java(?P<info>[^\n]*)\n(?P<body>.*?)^```$", re.M | re.S)

TYPE_DECL = re.compile(
    r"^\s*(?:@\w+\s+)*(?:public\s+|final\s+|abstract\s+|sealed\s+|non-sealed\s+)*"
    r"(?P<kind>class|interface|enum|record)\s+(?P<name>\w+)",
    re.M,
)

# javac messages that mean "this is not well-formed Java", as opposed to
# "this is well-formed but references something I can't see from here".
SYNTAX_MARKERS = (
    "class, interface, enum, or record expected",
    "illegal start of expression",
    "illegal start of type",
    "reached end of file while parsing",
    "<identifier> expected",
    "not a statement",
    "invalid method declaration",
    "';' expected",
    "')' expected",
    "'{' expected",
    "class expected",
    "unclosed string literal",
    "unnamed classes are a preview feature",
    "implicitly declared classes are a preview feature",
)


def line_of(text: str, index: int) -> int:
    return text.count("\n", 0, index) + 1


def type_name(body: str):
    """Return the name of the first top-level type declaration, or None."""
    m = TYPE_DECL.search(body)
    return m.group("name") if m else None


def check_fence(md: Path, lineno: int, body: str, workdir: Path):
    """Return a list of failure strings for one fence."""
    where = f"{md}:{lineno}"

    name = type_name(body)
    if name is None:
        first = body.strip().splitlines()[0][:70] if body.strip() else "(empty)"
        return [
            f"{where}: no top-level type declaration -- fence starts with a bare "
            f"statement or member. First line: {first!r}"
        ]

    src = workdir / f"{name}.java"
    src.write_text(body)

    out = workdir / "out"
    out.mkdir(exist_ok=True)
    proc = subprocess.run(
        ["javac", "-nowarn", "-d", str(out), str(src)], capture_output=True, text=True
    )
    stderr = proc.stderr

    syntax_hits = [
        ln for ln in stderr.splitlines() if any(mark in ln for mark in SYNTAX_MARKERS)
    ]
    if syntax_hits:
        return [f"{where}: syntax error in fence:\n    " + "\n    ".join(syntax_hits[:4])]

    # Standalone examples must fully compile and run.
    if name.endswith(("Example", "Demo")):
        if proc.returncode != 0:
            errs = [ln for ln in stderr.splitlines() if ": error:" in ln]
            return [
                f"{where}: {name} is a standalone example but does not compile on its "
                f"own:\n    " + "\n    ".join(errs[:4])
            ]
        if "static void main(" not in body:
            return []
        run = subprocess.run(
            ["java", "-cp", str(out), name], capture_output=True, text=True, timeout=30
        )
        if run.returncode != 0:
            head = run.stderr.strip().splitlines()
            return [
                f"{where}: {name} compiles but crashes when run:\n    "
                + (head[0][:120] if head else "(no stderr)")
            ]
    return []


def main(argv):
    roots = [Path(a) for a in argv[1:]] or [Path("lessons")]
    files = []
    for root in roots:
        files.extend(sorted(root.rglob("*.md")) if root.is_dir() else [root])

    all_failures = []
    checked = 0
    with tempfile.TemporaryDirectory() as td:
        workdir = Path(td)
        for md in files:
            text = md.read_text()
            for m in FENCE.finditer(text):
                checked += 1
                all_failures.extend(
                    check_fence(md, line_of(text, m.start()), m.group("body"), workdir)
                )

    for f in all_failures:
        print("FAIL " + f)
    print(f"\n{checked} java fences checked, {len(all_failures)} failing.")
    return 1 if all_failures else 0


if __name__ == "__main__":
    sys.exit(main(sys.argv))
