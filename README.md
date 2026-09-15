# Todo-App-Task-Management

## Agent skills

| Skill | Path | What it does |
|---|---|---|
| `bottom-navigation-design` | [`skills/bottom-navigation-design/`](skills/bottom-navigation-design/SKILL.md) | Designs, builds, or audits a mobile bottom navigation bar (iOS tab bar / Android bottom navigation). |

### How to use it

The repo ships one skill: **`bottom-navigation-design`**, in [`skills/bottom-navigation-design/SKILL.md`](skills/bottom-navigation-design/SKILL.md). It's built in the open **Agent Skills** format (`SKILL.md` with `name` + `description` frontmatter), so agents auto-discover it once it's in a directory they scan.

**1. Let your agent pick it up automatically.** Skills are loaded from the directory your agent scans. For Claude Code projects that's `.claude/skills/`, so link it in once:

```bash
mkdir -p .claude/skills
ln -s ../../skills/bottom-navigation-design .claude/skills/bottom-navigation-design
```

For personal use across all projects, link it into `~/.claude/skills/` instead. Other agents read from `skills/` at the repo root directly, or you can copy/symlink into their own skills directory.

**2. Or invoke it explicitly** — no install needed. Just point the agent at it:

> "Use `skills/bottom-navigation-design/SKILL.md` to design a bottom nav for this todo app."

**3. Or paste it into any chat agent** as a system/context prompt. The file is self-contained; the `references/` files are optional depth.

### Example prompts

- "Design the bottom navigation for this todo app." → produces a full nav spec (destinations, geometry, tokens, states, interactions, verification).
- "Audit the bottom nav in `src/components/NavBar.tsx`." → returns a `| Rule | Status | What's wrong | Fix |` table.
- "We have 7 tabs — what should we cut?" → runs the destination scoring and gives a recommendation.
- "Why does my bottom nav feel cramped on iPhone SE?" → checks tab count, tap targets, safe area, and label wrapping.

### Skill contents

| File | Purpose |
|---|---|
| `SKILL.md` | Core workflow, hard rules, output format, audit mode, definition of done |
| `references/metrics.md` | Sizes, spacing, safe area, contrast, motion — iOS vs. Material 3 |
| `references/audit-checklist.md` | 10-section scored checklist for reviewing an existing bar |
| `references/anti-patterns.md` | 18 named failure modes with before/after fixes |
| `references/todo-app-example.md` | Worked example applied to a task/todo app |

Derived from the uxpeak video *"Top UI/UX Design Tips — How to Design a Great Bottom Mobile Navigation Bar"* ([watch](https://www.youtube.com/watch?v=wLJ40GV2XEc)); platform metrics cross-checked against Apple HIG and Material Design 3.
