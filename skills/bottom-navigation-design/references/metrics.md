# Metrics & platform specs

Starting defaults. The video is explicit that these are **guidelines, not rigid rules** — adapt per device and verify against the current platform spec before shipping.

## Baseline numbers (from the video)

| Element | Value | Notes |
|---|---|---|
| Icon size | **24 px/dp** | Large enough to recognize, small enough not to dominate |
| Label size | **10–12 px/dp** | Below 10 causes accessibility problems; above 12 creates clutter |
| Tap target | **≥44 × 44** | Based on average thumb size |
| Home indicator area | **~34 pt** | Never cover, modify, or hide it |
| Tab count | **3–5** (max 6) | Fewer tabs = more breathing room + fewer mis-taps |

## iOS (tab bar)

| Element | Value |
|---|---|
| Tab bar height | **49 pt** |
| Home indicator inset | **34 pt** (devices with Face ID) |
| Total bottom chrome | **49 + 34 = 83 pt** |
| Tab bar icon | ~25 × 25 pt for circular glyphs, ~23 × 23 pt for square glyphs (regular size class); ~18 / 17 pt in compact |
| Minimum touch target | **44 × 44 pt** |

Icons: use SF Symbols; the filled variant is the idiomatic selected state.

## Android / Material 3 (navigation bar)

| Element | Value |
|---|---|
| Container height | **80 dp** (M3 Expressive also offers a shorter ~64 dp variant) |
| Icon | **24 dp**, top-aligned |
| Label | `labelMedium` (~12 sp) below the icon |
| Active indicator | Pill, ~**32 dp** tall (~56–64 dp wide depending on implementation/version) |
| Elevation | **3 dp** |
| Destinations | **3–5** |
| Minimum touch target | **48 × 48 dp** |

Tokens: container `surfaceContainer`, active icon `primary`, active label `secondary`, inactive icon/label `onSurfaceVariant`, active indicator `secondaryContainer`.

## Spacing & layout

- Design for **2–3 device widths** (small, standard, large) — a bulky bar eats screen space on small phones and makes the app feel cramped.
- Keep the bar as compact as the content allows; it should never overshadow the main content.
- Distribute items evenly across the full width; keep horizontal padding symmetric.
- On larger devices, center a CTA so it stays within thumb reach.

## Safe area

- The bar sits **above** the home indicator, with enough spacing that a thumb aiming at a tab doesn't trigger the system gesture.
- Extend only the bar's **background** into the safe area if you want edge-to-edge color — never the interactive items.
- Web/CSS: `padding-bottom: env(safe-area-inset-bottom)` on the bar.
- iOS: read `safeAreaInsets.bottom`; Android: handle window insets / `navigationBarsPadding()`.

## Contrast & accessibility

| Target | Requirement |
|---|---|
| Icons / UI components | **≥3:1** (WCAG 1.4.11) |
| Label text (normal size) | **≥4.5:1** (WCAG 1.4.3) |
| Label text (large, ≥18.66px bold / 24px) | ≥3:1 |

To signal inactive state, **reduce opacity** rather than switching to a low-contrast color — then verify the reduced-opacity result still clears the ratios above.

Check with the WebAIM Contrast Checker or an equivalent tool. Don't eyeball it.

## Motion

| Interaction | Suggested |
|---|---|
| Tap feedback | 50–100 ms |
| Tab switch / indicator slide | 150–250 ms |
| Screen transition (fade / slide) | 200–300 ms |

Use standard easing (ease-out for entrances, emphasized for the active indicator). Nothing should exceed ~300 ms — long transitions make the app feel slow. Always honor `prefers-reduced-motion` (iOS: "Reduce Motion"; Android: "Remove animations") by collapsing to an instant state change.

## Verification

- [ ] Tested at the smallest supported width and the largest
- [ ] Tested on a real device (simulators miss thumb reach, safe-area edge cases, and haptics)
- [ ] Tested one-handed
- [ ] Tested in dark mode
- [ ] Tested with increased text size / display scaling
- [ ] Tested with reduced motion enabled
