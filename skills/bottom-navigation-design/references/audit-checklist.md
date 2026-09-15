# Bottom navigation audit checklist

Score each row: **pass** / **warn** / **fail**. Anything that fails in *Structure* or *Reach* is a usability bug, not a style preference — fix it before touching visuals.

## 1. Structure

- [ ] 3–5 tabs (6 is the hard ceiling, and only with a strong reason)
- [ ] Every tab is a genuine **top-level destination**, not a sub-screen
- [ ] Each tab earned its slot on frequency × importance
- [ ] No low-frequency items: help, FAQ, legal, privacy, terms, logout, settings-as-a-tab
- [ ] No top-nav artifacts: back, forward, up, logo, page titles (Jacob's Law)
- [ ] If there's a central CTA, it's a true primary action and it's visually distinct
- [ ] Bottom nav is the right pattern at all (≥3 destinations, shallow hierarchy)

## 2. Reach & ergonomics

- [ ] Tap target ≥44 × 44 pt (iOS) / ≥48 × 48 dp (Android) — measure the *hit area*, not the icon
- [ ] No tab is cramped or overlapping its neighbour
- [ ] Home indicator / gesture area respected — no interactive element inside it
- [ ] Bar sits in the safe area with breathing room below the last row
- [ ] Reachable one-handed on the largest supported device
- [ ] Central CTA (if any) stays within thumb reach on large screens

## 3. Labels & copy

- [ ] Labels are short and single-line — nothing wraps to two lines
- [ ] Labels fit at the smallest supported device width and at max text scale
- [ ] Labels present for non-expert / older audiences; icon-only only for a tech-savvy audience
- [ ] No truncation, no ellipsis, no abbreviation that needs decoding
- [ ] Consistent casing and grammar across tabs

## 4. Icons

- [ ] Each icon is instantly recognizable (magnifying glass for search, not binoculars)
- [ ] One icon style throughout (all outline or all filled)
- [ ] Consistent **complexity** — no minimal icon sitting next to a detailed one
- [ ] Consistent optical weight and stroke width
- [ ] Each icon maps unambiguously to one destination
- [ ] Selected state uses the filled variant (the one sanctioned exception)

## 5. States

- [ ] Active differs from inactive by **≥2** visual signals (fill + color, or color + bolder label)
- [ ] Not text-color-only — that's too weak for users to locate themselves
- [ ] Pressed/active/pressed-and-active states all defined
- [ ] Inactive is de-emphasized without becoming invisible
- [ ] Tab switch is instant-feeling (feedback ≤100 ms)

## 6. Color & contrast

- [ ] Icon/indicator contrast ≥**3:1** against the bar
- [ ] Label text contrast ≥**4.5:1**
- [ ] Inactive states use **reduced opacity**, not a washed-out color — and still pass the ratios above
- [ ] No per-tab rainbow; no "every tab its own color"
- [ ] Chrome colors are neutral (white / gray / dark) so content keeps the spotlight
- [ ] Bright/primary colors reserved for key actions on the main screen
- [ ] Top and bottom navigation share one palette
- [ ] Verified with a contrast checker, in both light and dark mode

## 7. Separation from content

- [ ] Bar is separated from content by **at least one** of: hairline/1px border, contrasting background, or soft shadow
- [ ] Shadow (if used) is subtle — not a heavy drop shadow
- [ ] Content never appears to bleed into the navigation
- [ ] Scrollable content doesn't visually collide with the bar

## 8. Badges

- [ ] Used only for essential notifications, not every minor update
- [ ] Placed top-right of the icon
- [ ] Small but noticeable; has separation from the icon (outline/ring)
- [ ] Numeric counts remain legible at a glance
- [ ] Badge color contrasts with both bar and icon
- [ ] Clear/empty state defined and tidy

## 9. Motion & feedback

- [ ] Tap feedback exists (color shift, scale, or ripple)
- [ ] Tab switch is animated — sliding indicator or animated icon, not a hard snap
- [ ] Screen transitions are connected (fade or directional slide), not a teleport
- [ ] All motion ≤300 ms
- [ ] Reduced-motion setting honored
- [ ] Nothing animates at the cost of usability

## 10. Craft

- [ ] No boxes/pills/outlines around inactive tabs
- [ ] Bar is clean and minimal; no decorative noise
- [ ] Verified on 2–3 device widths
- [ ] Verified on a **real device**, one-handed
- [ ] Verified in dark mode, with large text, and with reduced motion
- [ ] Creative layout (if any) was validated for usability, not just approved for looks

## Reporting

Summarize as:

```
| # | Rule | Status | What's wrong | Fix |
```

Then order the fixes by impact: **structure → reach → states/contrast → separation → motion → polish.** Say plainly when a bar needs restructuring rather than restyling — repainting a bad information architecture doesn't fix it.
