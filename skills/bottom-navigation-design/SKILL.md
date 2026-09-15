---
name: bottom-navigation-design
description: Design, build, or review a mobile bottom navigation bar (iOS tab bar / Android bottom navigation). Use when the user asks for a bottom nav, tab bar, or mobile navigation UI; lists 3-6 top-level app destinations; or asks to audit, fix, or polish an existing bottom nav for usability, tap targets, active states, contrast, or visual quality. Covers destination selection, sizing and safe-area spacing, thumb reach, active/inactive states, icons, labels, color, badges, separation from content, and micro-interactions.
metadata:
  version: "1.0"
  source_video: "Top UI/UX Design Tips - How to Design a Great Bottom Mobile Navigation Bar"
  source_url: "https://www.youtube.com/watch?v=wLJ40GV2XEc"
  source_author: "uxpeak"
  derived_from: "Distilled from the video's transcript; platform metrics cross-checked against Apple HIG and Material Design 3."
---

# Bottom Navigation Design

## When to use this skill

Use when any of these are true:

- The user wants a bottom navigation bar / tab bar designed, built, or restyled.
- The user names 3–6 top-level destinations and asks how to navigate between them.
- The user asks to review, audit, critique, or fix an existing bottom nav.
- The user is picking icons, labels, sizes, colors, badges, or active states for a bottom nav.

**Do not use for:** deep navigation hierarchies (use a nav drawer or nested stacks), fewer than 3 destinations (use top tabs or a single screen), more than 6 destinations (bottom nav is the wrong pattern), or desktop/web-only layouts.

## The core principle

Bottom navigation is not "just buttons at the bottom." It is the **backbone of the app's usability**: it declares the app's top-level structure and is one of the most tapped areas in the entire product. Every pixel in it is prime real estate — so every icon, label, and interaction must be deliberate.

Fundamentals come first. A beautiful animation on a badly structured bar is still a bad bar.

## Workflow

Work these steps in order. Do not skip to styling.

### 1. Pick the destinations (most important step)

Score every candidate destination on **frequency of use** × **importance to the core job**. Only the top 3–5 survive.

**Good candidates:** home / main feed / dashboard, search / discover, add / create, messages / notifications (if social is core), profile / saved / history.

**Bad candidates — never put these in bottom nav:**
- Help, FAQ → belongs in profile or a side menu
- Log out → nobody logs out frequently
- Legal pages (privacy, terms) → nobody needs these at their fingertips
- Settings → usually profile, unless it's a top-2 action
- Back / forward buttons, logos → these belong to **top** navigation

Putting top-nav elements at the bottom violates **Jacob's Law**: users spend most of their time in other apps, so they expect your app to work the way the ones they already know work. Violating that familiar pattern costs you more than the convenience gains.

**The one exception worth making:** a central call-to-action (Create, Post, Order). Center it, make it prominent — central placement is easy to reach on large phones, and it converts. A raised/contrasting CTA in the middle is a feature, not clutter.

### 2. Know the users before drawing anything

Audience determines the details:

| Audience | Icon vs. label decision |
|---|---|
| Younger, tech-savvy | Icon-only can work (minimalist) |
| Older or less app-confident | **Always** show text labels — labels build confidence |
| Mixed / unknown | Show labels. Default to clarity. |

Also ask: what devices do they use? What problem are they solving? This sets tap-target and label decisions later.

### 3. Build the spec

Fix the geometry before styling. See **[references/metrics.md](references/metrics.md)** for the full numbers (iOS vs. Android, safe area, tap targets, sizes).

Non-negotiables:
- **3–5 tabs**, absolute max 6. More tabs = smaller targets + choice paralysis.
- **Tap target ≥ 44×44 px** (iOS) / **≥ 48×48 dp** (Android). This is thumb-sized, not arbitrary — and it's an accessibility requirement, not a nicety. A 24×24 icon *inside* a 44+ target is fine; a 24×24 tappable area is not.
- **Respect the safe area.** Never modify, hide, or overlap the home indicator. If the bar is cramped against it, users trigger "go home" when trying to tap a tab.

### 4. Design the visual layer

| Area | Rule |
|---|---|
| **Icons** | Familiar and simple. Magnifying glass for search — not binoculars. Recognition beats cleverness. |
| **Icon style** | One consistent style across all tabs (all outline, or all filled). Mixing styles is "showing up to a black-tie event in beachwear." Match **complexity** too — a minimal icon next a detailed one breaks harmony. |
| **Selected state** | Allowed (and encouraged) to switch outline → filled. That single exception is the point of the rule. |
| **Labels** | Short, single-line, one word where possible. Two-line labels bloat the bar and throw off layout balance. |
| **Color** | Stick to the brand palette. **Do not give each tab its own color** — it turns navigation into a color-guessing game and steals attention from content. |
| **Chrome color** | Keep navigation neutral (white / gray / dark). Reserve bright and primary colors for key actions on the main screen. Keep top and bottom navigation in the same palette so the app feels cohesive. |
| **Clutter** | No boxes, pills, or outlines around tabs (except a deliberate active indicator). Visual noise helps no one. |

### 5. Separate the bar from the content — the mistake pros make

**This is the single most common professional failure: not separating bottom nav from the main content.** Pick at least one:

1. A **1px border** or soft hairline across the top of the bar — subtle, clear.
2. A **different background color** than the content (e.g. white content, light gray bar) — creates depth.
3. A **soft shadow** above the bar — makes it feel elevated and floating.

Keep shadows subtle. A big harsh shadow drags the eye away from the content and looks cheap.

### 6. Get states and contrast right

**Active vs. inactive must differ by at least two signals.** Changing only the text color is not enough — users then take longer to figure out where they are. Combine:

- icon outline → filled, **plus** color change, **plus** bolder/darker label

Two changes minimum (e.g. icon color + bolder label). Keeping the icon outlined and only changing color is acceptable **as long as** a second signal accompanies it.

**Inactive does not mean invisible.** Don't use a drastically different low-contrast color. Instead, **slightly reduce opacity** — it keeps the palette cohesive while still distinguishing states.

Accessibility gate:
- **3:1** minimum contrast for icons and UI components (WCAG 1.4.11 Non-text Contrast)
- **4.5:1** minimum for label text (WCAG 1.4.3)
- Verify with a contrast checker (e.g. WebAIM) — never eyeball it

### 7. Badges — sparingly

Badges signal "something new here." Rules:

- Small enough to stay subtle, large enough to notice
- **Top-right corner** of the icon — where the eye already goes
- Add a thin outline around the badge for a polished, separated look
- If numeric, the number must be legible at a glance (no tiny or over-stylized fonts)
- Color must contrast with both bar and icon, while still fitting the app
- **Only for essential notifications.** Badging everything causes notification fatigue and kills the feature.

### 8. Add micro-interactions last

With the fundamentals solid, add the layer that makes the bar *feel* great:

1. **Tap feedback** — instant visual response: color change, slight scale-up, or ripple.
2. **Switch feedback** — don't snap. Slide an underline, or animate the icon into place.
3. **Screen transitions** — soft fade or directional slide so screens feel connected, not teleported.

Keep transitions in the **150–300 ms** range and honor reduced-motion settings. Never sacrifice usability for a creative layout — a stunning bar that confuses people is a bad bar.

## Output format

When designing, produce a **nav spec** before writing code:

```markdown
### Destinations
| # | Icon | Label | Route | Why it earned a slot |
|---|------|-------|-------|----------------------|

### Geometry
- Platform(s): / Tabs: / Icon: / Label: / Tap target: / Bar height: / Safe-area inset:

### Tokens
- Bar background: / Separator: / Active icon: / Active label: / Inactive icon+label (opacity): / Badge:

### States
- Active: [2+ signals]  /  Inactive: [opacity]  /  Pressed: [feedback]

### Interactions
- Tap: / Switch: / Screen transition:

### Verification
- [ ] Contrast: icon ≥3:1, text ≥4.5:1  [ ] Tap targets ≥44/48  [ ] Safe area respected
- [ ] Tested on 2–3 device widths  [ ] Tested on a real device
```

## Audit mode

When asked to review an existing bottom nav, work through **[references/audit-checklist.md](references/audit-checklist.md)** and report as a table:

`| Rule | Status (pass / warn / fail) | What's wrong | Fix |`

Lead with the failures that hurt usability most: wrong destinations, >5 tabs, undersized tap targets, safe-area overlap, and the missing content separator. Pattern-match against **[references/anti-patterns.md](references/anti-patterns.md)**.

## Definition of done

Do not call a bottom nav finished until all of these hold:

- [ ] 3–5 destinations, each one a genuine top-level destination
- [ ] No top-nav artifacts (back, forward, logo) and no low-frequency items (help, legal, logout)
- [ ] Tap targets ≥44 px (iOS) / ≥48 dp (Android)
- [ ] Home indicator respected; bar sits in the safe area
- [ ] Active state differs from inactive by ≥2 visual signals
- [ ] Inactive states still meet 3:1 (icons) / 4.5:1 (text) contrast
- [ ] One icon style and one complexity level throughout
- [ ] Labels short, single-line, present for non-expert audiences
- [ ] Bar visually separated from content (hairline, background shift, or soft shadow)
- [ ] Colors neutral/brand-consistent; no per-tab rainbow
- [ ] Badges used only for essential notifications
- [ ] Tap + switch feedback implemented, transitions ≤300 ms, reduced-motion honored
- [ ] Checked on 2–3 device widths **and** on a real device

## References

- **[references/metrics.md](references/metrics.md)** — sizes, spacing, safe area, platform specs (iOS tab bar vs. Material 3 navigation bar)
- **[references/audit-checklist.md](references/audit-checklist.md)** — scored review checklist for existing bars
- **[references/anti-patterns.md](references/anti-patterns.md)** — named failure modes with before/after fixes
- **[references/todo-app-example.md](references/todo-app-example.md)** — worked example applied to a task/todo app

## Attribution

Distilled from the uxpeak video *"Top UI/UX Design Tips — How to Design a Great Bottom Mobile Navigation Bar"* ([youtube.com/watch?v=wLJ40GV2XEc](https://www.youtube.com/watch?v=wLJ40GV2XEc)). Platform metrics were cross-checked against Apple's Human Interface Guidelines and Material Design 3; always re-verify against the current spec for your target platform.
