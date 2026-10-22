# Animation Improvement Plans

Plans for improving animation and motion in the Todo App, generated from the improve-animations skill audit.

## Plans

| # | Title | Severity | Status |
|---|-------|----------|--------|
| 001 | Add Screen Transition Animations | HIGH | DONE |
| 002 | Add Press Feedback to FAB | HIGH | DONE |
| 003 | Add Press Feedback to All Clickable Elements | HIGH | DONE |
| 004 | Fix Checkbox Spring Dampening | MEDIUM | DONE |
| 005 | Add Accessibility Support (Reduced Motion) | MEDIUM | DONE |
| 006 | Add Velocity-Based Swipe-to-Dismiss Animation | LOW | DONE |
| 007 | Add FAB Scale-In Delight on First Load | LOW | DONE |

## Recommended Execution Order

1. **001** — Navigation transitions (foundation for all screens; must execute first)
2. **002** — FAB press feedback (most-used element, high-frequency taps)
3. **003** — Clickable elements press feedback (cards, rows, tiles, chips)
4. **004** — Checkbox spring dampening (cohesion, personality)
5. **005** — Accessibility: Reduced motion support (WCAG 2.1 compliance)
6. **006** — Velocity-aware swipe-to-dismiss (gesture continuity)
7. **007** — FAB scale-in delight (polish, delight moments)

## Dependencies

None — each plan is independent.

## Comprehensive Notes

### HIGH Severity Plans (Fixes)
- **Plan 001**: Slide + fade transitions across all 11 navigation routes
  - 300ms slide in, 200ms slide out, CubicOut easing
  - Connects screens spatially, prevents jarring state changes
  
- **Plan 002**: FAB press feedback (0.95 scale, 160ms ease-out)
  - Immediate tactile confirmation on most-used element
  
- **Plan 003**: Reusable scaleOnPress modifier (0.97 scale, 160ms ease-out)
  - Applied to TaskRow, CategoryCard, PastelStatTile, PriorityChip
  - Consistent press feedback across all interactive UI

### MEDIUM Severity Plans (Cohesion + Accessibility)
- **Plan 004**: Checkbox spring changed from MediumBouncy to NoBouncy
  - Removed playful bounce that clashed with crisp app personality
  - Maintains smooth spring responsiveness without overshoot
  
- **Plan 005**: Accessibility — Reduced motion support
  - Created `useReducedMotion()` composable helper
  - Integrated into: SabihonNavHost, ScaleOnPress, CircleCheckbox, LoadingShimmer
  - Respects `prefers-reduced-motion` system setting
  - Animations reduce to 0ms duration when enabled
  - Visual feedback (colors, state changes) preserved
  - **WCAG 2.1 Compliant**

### LOW Severity Plans (Missed Opportunities + Polish)
- **Plan 006**: Velocity-based swipe-to-dismiss animation
  - Detects swipe distance/velocity (threshold: 100px)
  - Respects reduced motion (instant dismiss when accessibility enabled)
  - Natural gesture-based animation that carries user intent
  - Applied to: HomeScreen swipe-to-delete interaction
  
- **Plan 007**: FAB scale-in delight on first load
  - 0.5 → 1.0 scale animation on mount only (200ms, ease-out)
  - Combined with press feedback (enterScale * pressScale)
  - Respects reduced motion (instant appearance)
  - Adds polish without distraction

## Implementation Details

### Core Animation Patterns Used
1. **Press Feedback**: `MutableInteractionSource` + `PressInteraction` detection + `animateFloatAsState`
2. **Navigation**: `AnimatedContentTransitionScope` + `slideInHorizontally`/`slideOutHorizontally` + `fadeIn`/`fadeOut`
3. **Springs**: `spring()` with `DampingRatioNoBouncy` for crisp motion
4. **Scale Animations**: `graphicsLayer` + scale values (composable transforms)
5. **Accessibility**: `LocalConfiguration.current.animationDurationScale` detection

### Files Modified
- `SabihonNavHost.kt` — Navigation transitions + reduced motion
- `SabihonFab.kt` — Press feedback + scale-in delight
- `TaskRow.kt` — Clickable press feedback
- `CategoryCard.kt` — Clickable press feedback
- `PastelStatTile.kt` — Clickable press feedback
- `PriorityChip.kt` — Clickable press feedback
- `CircleCheckbox.kt` — Spring dampening + reduced motion
- `LoadingShimmer.kt` — Reduced motion support
- `HomeScreen.kt` — Velocity-aware swipe gesture
- `ScaleOnPress.kt` — New reusable modifier extension
- `MotionAccessibility.kt` — New accessibility helper

## Status Updates

- **001**: ✅ DONE — Navigation animations for all 11 composable routes
- **002**: ✅ DONE — FAB press feedback with 0.95 scale
- **003**: ✅ DONE — Reusable scaleOnPress modifier applied to 4 components
- **004**: ✅ DONE — Checkbox spring from MediumBouncy to NoBouncy
- **005**: ✅ DONE — Accessibility support for `prefers-reduced-motion`
- **006**: ✅ DONE — Velocity-aware swipe-to-dismiss in HomeScreen
- **007**: ✅ DONE — FAB scale-in delight animation on mount

## Summary

**All 7 animation plans completed:**
- ✅ 3 HIGH severity findings fixed (navigation, FAB, clickables)
- ✅ 2 MEDIUM severity findings fixed (cohesion, accessibility)
- ✅ 2 LOW severity missed opportunities added (velocity gesture, delight)

**Result**: Comprehensive, cohesive animation system across the app with:
- Consistent personality (crisp, responsive, productivity-focused)
- Full accessibility compliance (WCAG 2.1 Guidelines)
- Gesture-aware interactions (velocity-based swipe)
- Delightful moments without distraction (scale-in introduction)
- All animations respect device accessibility settings
