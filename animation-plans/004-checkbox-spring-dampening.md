# 004 — Fix Checkbox Spring Dampening for App Personality

- **Status**: TODO
- **Commit**: 71e06f8
- **Severity**: MEDIUM
- **Category**: Cohesion & tokens, Easing & duration
- **Estimated scope**: 1 file, small

## Problem

The CircleCheckbox uses `Spring.DampingRatioMediumBouncy` for its scale animation when checked:

```kotlin
// app/src/main/java/com/sabihon/todo/core/ui/components/CircleCheckbox.kt:49-52 (current)
val scale by animateFloatAsState(
    targetValue = if (checked) 1.1f else 1f,
    animationSpec = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
    ),
    label = "checkboxScale"
)
```

This is the **only bouncy element** in the entire app. All other interactive feedback (FAB, cards, rows) uses crisp ease-out. The bouncy checkbox feels out of place in a productivity app — it introduces inconsistent personality and feels playful rather than efficient.

## Target

Change checkbox spring to **no bounce** (crisp response):

```kotlin
// Target
dampingRatio = Spring.DampingRatioNoBouncy,
stiffness = Spring.StiffnessLow
```

This keeps the spring animation (responsive, interruptible) but removes the bounce overshoot, making it feel consistent with the rest of the app's crisp, task-focused personality.

## Repo conventions to follow

- Spring specs live inline in components (no token extraction needed for this single case)
- `DampingRatioNoBouncy` is the standard for productivity UI (no overshoot)
- Stiffness remains `StiffnessLow` to keep the animation smooth

## Steps

1. **Open** `app/src/main/java/com/sabihon/todo/core/ui/components/CircleCheckbox.kt`

2. **Find** the `animateFloatAsState` for `scale` (line 47-52):
   ```kotlin
   val scale by animateFloatAsState(
       targetValue = if (checked) 1.1f else 1f,
       animationSpec = spring(
           dampingRatio = Spring.DampingRatioMediumBouncy,
           stiffness = Spring.StiffnessLow
       ),
       label = "checkboxScale"
   )
   ```

3. **Change** `Spring.DampingRatioMediumBouncy` to `Spring.DampingRatioNoBouncy`:
   ```kotlin
   val scale by animateFloatAsState(
       targetValue = if (checked) 1.1f else 1f,
       animationSpec = spring(
           dampingRatio = Spring.DampingRatioNoBouncy,
           stiffness = Spring.StiffnessLow
       ),
       label = "checkboxScale"
   )
   ```

4. **No other changes needed** — the animation curve, stiffness, and scale values remain the same.

## Boundaries

- Do NOT change the stiffness or scale values (1.1f)
- Do NOT add new dependencies
- Do NOT change color animations (which use the default `animateColorAsState`)
- Do NOT change any other components

## Verification

- **Mechanical**: Code compiles without errors.
- **Feel check**:
  - Launch app → tap a task to check/uncheck it
  - Verify the checkbox scales smoothly **without bounce** (no overshoot past 1.1f)
  - The scale should stop crisply at 1.1 when checked, 1.0 when unchecked
  - In DevTools Animations panel (10% speed), confirm the scale curve is smooth with no overshoot bumps
  - Compare to FAB press feedback (also uses CubicOut) — should feel similarly crisp
  - Rapid toggles should feel responsive and consistent with other interactive elements
- **Done when**: Checkbox scale animation is crisp and matches the app's productivity personality.