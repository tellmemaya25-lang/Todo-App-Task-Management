# 007 — Add FAB Scale-In Delight Animation on First Load

- **Status**: TODO
- **Commit**: 71e06f8
- **Severity**: LOW
- **Category**: Missed opportunities, Delight
- **Estimated scope**: 2 files, small

## Problem

The FAB appears instantly when the app loads or when screens transition to views with a floating action button. There's no introduction animation. For a productivity app, this is a minor miss — the FAB could have a brief scale-in animation (0.5 → 1.0) to signal its presence and add a touch of delight without being distracting.

```kotlin
// app/src/main/java/com/sabihon/todo/core/ui/components/SabihonFab.kt:42-59 (current)
FloatingActionButton(
    onClick = onClick,
    modifier = modifier
        .size(56.dp)
        .graphicsLayer { scaleX = scale; scaleY = scale },
    // ... rest of props
) {
    Icon(...)
}
```

The FAB appears fully scaled immediately on screen load.

## Target

Add a brief **scale-in animation** when the FAB first appears:
- **Start scale**: 0.5 (half size)
- **End scale**: 1.0 (full size)
- **Duration**: 200ms
- **Easing**: ease-out (responsive feel)
- **Trigger**: Only on first mount, not on every recomposition
- **Respect accessibility**: Skip animation when reduced motion is enabled

Pattern:
```kotlin
var isMounted by remember { mutableStateOf(false) }
LaunchedEffect(Unit) {
    isMounted = true
}

val enterScale by animateFloatAsState(
    targetValue = if (isMounted) 1.0f else 0.5f,
    animationSpec = if (isReducedMotion) tween(0) else tween(200, easing = CubicOut),
    label = "fabEnter"
)

// Apply to FAB
modifier = modifier
    .size(56.dp)
    .graphicsLayer { scaleX = enterScale * scale; scaleY = enterScale * scale }
```

## Repo conventions to follow

- Use `LaunchedEffect(Unit)` to animate on mount only
- Combine `enterScale * scale` to preserve existing press feedback
- Duration: 200ms (from audit standards for UI animations)
- Easing: CubicOut (responsive, matches other entrance animations)
- Respect reduced motion (0ms duration when accessibility enabled)

## Steps

1. **Open** `app/src/main/java/com/sabihon/todo/core/ui/components/SabihonFab.kt`

2. **Add imports**:
   ```kotlin
   import androidx.compose.runtime.LaunchedEffect
   import androidx.compose.runtime.mutableStateOf
   import androidx.compose.animation.core.CubicOut
   import androidx.compose.animation.core.tween
   ```

3. **Inside SabihonFab composable**, add state and entry animation after the press-feedback scale:
   ```kotlin
   var isMounted by remember { mutableStateOf(false) }
   val isReducedMotion = androidx.compose.ui.platform.LocalConfiguration.current.animationDurationScale == 0f

   LaunchedEffect(Unit) {
       isMounted = true
   }

   val enterScale by animateFloatAsState(
       targetValue = if (isMounted) 1.0f else 0.5f,
       animationSpec = if (isReducedMotion) tween(0) else tween(200, easing = CubicOut),
       label = "fabEnter"
   )
   ```

4. **Update the modifier** to combine entry and press scales:
   ```kotlin
   FloatingActionButton(
       onClick = onClick,
       modifier = modifier
           .size(56.dp)
           .graphicsLayer { scaleX = enterScale * scale; scaleY = enterScale * scale },
       // ... rest unchanged
   ) {
       Icon(...)
   }
   ```

## Boundaries

- Do NOT change FAB size, color, shape, or elevation
- Do NOT affect press feedback (scale multiplied, not replaced)
- Do NOT add new dependencies
- Do NOT animate on every recomposition — only on first mount

## Verification

- **Mechanical**: Code compiles without errors.
- **Feel check**:
  - Launch app → FAB scales in smoothly from 0.5 to 1.0 over 200ms
  - Tap FAB during scale-in → press feedback combines with scale (smooth interaction)
  - Navigate to screens with FAB → consistent scale-in animation
  - In reduced-motion mode → FAB appears instantly (no animation)
  - On fast devices, animation completes before user can interact
- **Done when**: FAB has a delightful, brief scale-in on first appearance, and respects accessibility.