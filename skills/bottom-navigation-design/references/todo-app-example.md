# Worked example: bottom nav for a task/todo app

How the rules land on this repo's subject matter. Use it as a reference for tone and depth, not as a mandate — run the destination scoring for your actual product.

## Step 1 — Destination scoring

Candidate destinations for a todo app, scored on frequency × importance:

| Candidate | Frequency | Importance | Verdict |
|---|---|---|---|
| Tasks / Today (home) | Very high | Core — the whole app | ✅ Tab 1 |
| Search | High | High — "where did I put that task" | ✅ Tab 2 |
| Add task | Very high | Core action | ✅ Center CTA |
| Lists / Projects | High | High — how tasks are organized | ✅ Tab 4 |
| Profile / Account | Medium | Medium — settings, sync, logout | ✅ Tab 5 |
| Calendar / Upcoming | Medium | High | ⚠️ Competes with Lists — pick one, not both |
| Insights / Stats | Low | Low | ❌ Move into Profile or a home-screen widget |
| Filters / Sort | High (within a screen) | — | ❌ Not a destination — it's a control on the Tasks screen |
| Archive / Completed | Low | Low | ❌ Nested under Lists or Profile |
| Settings | Low | Medium | ❌ Under Profile |
| Help / FAQ | Very low | Low | ❌ Under Profile |
| Log out | Near zero | — | ❌ Under Profile |

**Result — 4 tabs + 1 center CTA (5 slots):**

```
[ Tasks ]  [ Search ]  [  ⊕  ]  [ Lists ]  [ Profile ]
```

Icons: `checklist` (or house) · `magnifyingglass` · `plus.circle.fill` · `folder` / `list.bullet` · `person`

All labels shown. A task app is a utility used by a broad age range, often in a hurry — icon-only would cost comprehension for zero benefit.

## Step 2 — Geometry

| Property | Value |
|---|---|
| Tabs | 4 + center CTA |
| Icon | 24 dp/pt; CTA icon 28–32 for emphasis |
| Label | 12 sp/pt active, 10–11 inactive — or one fixed size with weight change |
| Tap target | 48 × 48 dp (Android) / 44 × 44 pt (iOS) minimum, CTA ≥56 |
| Bar height | 80 dp (M3) / 49 pt + safe area (iOS) |
| Safe area | `padding-bottom: env(safe-area-inset-bottom)`; controls stay above it |

## Step 3 — Tokens

| Token | Value |
|---|---|
| Bar background | `surfaceContainer` (light: near-white gray; dark: elevated dark gray) |
| Separator | 1px hairline top border, or 3 dp elevation shadow — **pick one** |
| Active icon | `primary` |
| Active label | `primary`, weight 600 |
| Inactive icon + label | `onSurfaceVariant` at ~70% opacity |
| Active indicator | `secondaryContainer` pill (optional) |
| CTA | `primary` container, `onPrimary` glyph |
| Badge | Error/red, white numeral, 1px ring in bar background color |

## Step 4 — States

- **Active:** filled icon + `primary` color + label at weight 600 → three signals.
- **Inactive:** outline icon, `onSurfaceVariant` at 70% opacity, regular weight.
- **Pressed:** 8% state-layer overlay, icon scales to 0.92 then releases (≤100 ms).
- **Badge:** due-today count on Tasks, overdue count on Lists. Cleared the moment the screen is viewed. Nothing else earns a badge — "3 lists exist" is not a notification.

## Step 5 — Interactions

1. **Tap:** scale-down + state-layer flash, ~80 ms. Light haptic on iOS.
2. **Switch:** active indicator pill slides to the new tab over ~200 ms (emphasized easing); icon cross-fades outline → filled.
3. **Screen transition:** 220 ms cross-fade + 8 px directional slide. Horizontal position encodes the tab order, so the motion reinforces the structure.
4. **Add task:** the CTA opens a bottom sheet rather than pushing a screen — it's an interruptible action, and a sheet keeps the bar (and the user's context) visible.
5. **Reduced motion:** drop to instant state changes; no slide, no scale.

## Step 6 — Verification

- [ ] 5 slots used; nothing low-frequency present
- [ ] Tappable on a small phone one-handed; CTA reachable on a large phone
- [ ] Home indicator clear on a Face ID device
- [ ] Labels don't wrap at 320 pt width with the largest text setting
- [ ] Icon contrast ≥3:1 and label ≥4.5:1 in light **and** dark mode
- [ ] Tasks badge clears on view; no other tab carries a badge
- [ ] Bar visibly separated from the scrolling task list
- [ ] Reduce Motion honored
