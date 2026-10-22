# 005 — Add Accessibility Support for Reduced Motion

- **Status**: TODO
- **Commit**: 71e06f8
- **Severity**: MEDIUM
- **Category**: Accessibility
- **Estimated scope**: 4 files, medium

## Problem

The app has **no `prefers-reduced-motion` handling**. Users who've enabled "Reduce motion" in their device settings are still exposed to all animations — scale feedback, screen transitions, checkbox springs, load shimmer. This violates WCAG 2.1 guideline 2.3.3 (Animation from Interactions) and creates discomfort for users with motion sensitivity or vestibular disorders.

Affected animations:
- Navigation transitions (Plan 001) — 300ms slide + fade on every screen swap
- Press feedback (Plans 002–003) — scale animations on every tap
- Checkbox spring (Plan 004) — scale on completion
- Loading shimmer (LoadingShimmer.kt) — infinite horizontal translate

## Target

Detect `prefers-reduced-motion` and reduce motion gracefully:
- **Entrance animations**: Replace with instant visibility (no slide, fade-only or instant)
- **Press feedback**: Reduce scale from 0.95/0.97 to 1.0 (instant tactile feedback only)
- **Checkbox spring**: Reduce to instant scale (no animation)
- **Load shimmer**: Replace infinite loop with static shimmer or remove animation

Core pattern:
```kotlin
@Composable
fun useReducedMotion(): Boolean {
    val animationDuration = LocalConfiguration.current.animationDurationScale
    return animationDuration == 0f
}

// Usage
val isReducedMotion = useReducedMotion()
val actualDuration = if (isReducedMotion) 0 else 300
```

## Repo conventions to follow

- Create `core/ui/extensions/MotionAccessibility.kt` with a reusable `useReducedMotion()` composable
- All animation specs: check reduced motion and set duration to 0 when true
- Maintain visual feedback (colors, opacity, scale endpoints) — only reduce timing

## Steps

1. **Create** `app/src/main/java/com/sabihon/todo/core/ui/extensions/MotionAccessibility.kt`:
   ```kotlin
   package com.sabihon.todo.core.ui.extensions

   import androidx.compose.runtime.Composable
   import androidx.compose.ui.platform.LocalConfiguration

   @Composable
   fun useReducedMotion(): Boolean {
       val animationDuration = LocalConfiguration.current.animationDurationScale
       return animationDuration == 0f
   }
   ```

2. **Update SabihonNavHost.kt**:
   - Add import: `import com.sabihon.todo.core.ui.extensions.useReducedMotion`
   - Inside `SabihonNavHost` composable, add:
     ```kotlin
     val isReducedMotion = useReducedMotion()
     ```
   - For all transition animations, change duration:
     ```kotlin
     // Before
     tween(300, easing = CubicOut)
     
     // After
     tween(if (isReducedMotion) 0 else 300, easing = CubicOut)
     ```
   - Apply to all `slideInHorizontally`, `slideOutHorizontally`, `fadeIn`, `fadeOut` specs

3. **Update ScaleOnPress.kt**:
   - Add import: `import com.sabihon.todo.core.ui.extensions.useReducedMotion`
   - Inside the composed modifier, add:
     ```kotlin
     val isReducedMotion = useReducedMotion()
     ```
   - Update tween:
     ```kotlin
     tween(if (isReducedMotion) 0 else 160, easing = CubicOut)
     ```

4. **Update CircleCheckbox.kt**:
   - Add import: `import com.sabihon.todo.core.ui.extensions.useReducedMotion`
   - Inside composable, add:
     ```kotlin
     val isReducedMotion = useReducedMotion()
     ```
   - Update spring to skip when reduced motion:
     ```kotlin
     animationSpec = if (isReducedMotion)
         tween(0)
     else
         spring(
             dampingRatio = Spring.DampingRatioNoBouncy,
             stiffness = Spring.StiffnessLow
         )
     ```

5. **Update LoadingShimmer.kt**:
   - Add import: `import com.sabihon.todo.core.ui.extensions.useReducedMotion`
   - Inside composable, add:
     ```kotlin
     val isReducedMotion = useReducedMotion()
     ```
   - Update infinite animation:
     ```kotlin
     animationSpec = if (isReducedMotion)
         infiniteRepeatable(
             animation = tween(0),
             repeatMode = RepeatMode.Restart
         )
     else
         infiniteRepeatable(
             animation = tween(durationMillis = 1200),
             repeatMode = RepeatMode.Restart
         )
     ```
   - Alternatively, just skip rendering shimmer when reduced motion is on

## Boundaries

- Do NOT remove animations entirely — reduced motion users still get visual feedback (colors, opacity changes)
- Do NOT change the final state of any animation (scale endpoints, color targets)
- Do NOT break interactivity when reduced motion is enabled
- Do NOT add new dependencies

## Verification

- **Mechanical**: Code compiles without errors.
- **Feel check**:
  - **Without reduced motion**: Navigate screens, tap elements — all animations work normally
  - **With reduced motion enabled** (device settings or emulator):
    - Navigate screens → transitions are instant (no slide, fade only or instant)
    - Tap task to complete → no scale animation, checkbox still changes state instantly
    - Tap FAB, cards → no press scale animation, but buttons still respond
    - Loading shimmer → either instant or removed entirely
  - Visual feedback (colors changing, state changes) still occurs instantly
  - Verify on a real device with reduced motion enabled in Settings → Accessibility
- **Done when**: All animations respect `prefers-reduced-motion` and reduce timing to 0 without removing visual feedback.