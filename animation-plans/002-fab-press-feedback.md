# 002 — Add Press Feedback to FAB

- **Status**: TODO
- **Commit**: 71e06f8
- **Severity**: HIGH
- **Category**: Press feedback, Purpose & frequency
- **Estimated scope**: 1 file, small

## Problem

The FAB (Floating Action Button) in `SabihonFab.kt` has **no press feedback**. When users tap the primary action button, nothing happens until the click callback fires — no scale change, no color feedback. For an element tapped 100+ times per day, this lack of tactile confirmation makes the app feel unresponsive.

```kotlin
// app/src/main/java/com/sabihon/todo/core/ui/components/SabihonFab.kt:20-35 (current)
FloatingActionButton(
    onClick = onClick,
    modifier = modifier.size(56.dp),
    shape = CircleShape,
    containerColor = AccentBlue,
    contentColor = Color.White,
    elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp)
) {
    Icon(
        imageVector = Icons.Default.Add,
        contentDescription = contentDescription
    )
}
```

No interaction state — the button looks identical during press, before release.

## Target

The FAB should scale down to **0.95** on press with a **160ms ease-out** animation, then scale back to 1.0 when released. This gives immediate tactile feedback without being distracting.

```kotlin
// Target behavior
FloatingActionButton(
    onClick = onClick,
    modifier = modifier
        .size(56.dp)
        .animateContentSize(animationSpec = tween(160, easing = CubicOut))
        .scaleOnPress(0.95f, duration = 160),
    shape = CircleShape,
    // ... rest of props
)
```

The exact implementation uses Compose's `pointerInput` + `animateFloatAsState` to detect press state and animate the scale.

## Repo conventions to follow

- Animation specs use `tween()` with duration in milliseconds
- Easing uses `CubicOut` for responsive UI animations (from audit)
- Spring animations reserved for bounce feedback (like CircleCheckbox)
- Button animations follow the pattern: detect press state → animate scale → release

## Steps

1. **Open** `app/src/main/java/com/sabihon/todo/core/ui/components/SabihonFab.kt`

2. **Add imports** for press detection and animation:
   ```kotlin
   import androidx.compose.animation.core.animateFloatAsState
   import androidx.compose.animation.core.CubicOut
   import androidx.compose.animation.core.tween
   import androidx.compose.foundation.interaction.MutableInteractionSource
   import androidx.compose.foundation.interaction.PressInteraction
   import androidx.compose.runtime.mutableStateOf
   import androidx.compose.runtime.remember
   import androidx.compose.runtime.getValue
   import androidx.compose.ui.graphics.graphicsLayer
   ```

3. **Inside the `SabihonFab` composable**, create state to track press:
   ```kotlin
   val interactionSource = remember { MutableInteractionSource() }
   var isPressed by remember { mutableStateOf(false) }
   
   // Detect press events
   LaunchedEffect(interactionSource) {
       interactionSource.interactions.collect { interaction ->
           when (interaction) {
               is PressInteraction.Press -> isPressed = true
               is PressInteraction.Release -> isPressed = false
               is PressInteraction.Cancel -> isPressed = false
           }
       }
   }
   
   val scale by animateFloatAsState(
       targetValue = if (isPressed) 0.95f else 1.0f,
       animationSpec = tween(160, easing = CubicOut),
       label = "fabScale"
   )
   ```

4. **Add `interactionSource` to FloatingActionButton** and apply scale transform:
   ```kotlin
   FloatingActionButton(
       onClick = onClick,
       modifier = modifier
           .size(56.dp)
           .graphicsLayer { scaleX = scale; scaleY = scale },
       shape = CircleShape,
       containerColor = AccentBlue,
       contentColor = Color.White,
       elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp),
       interactionSource = interactionSource
   ) {
       Icon(
           imageVector = Icons.Default.Add,
           contentDescription = contentDescription
       )
   }
   ```

## Boundaries

- Do NOT change the FAB's size, color, or shape — only add press animation.
- Do NOT add new dependencies — all APIs are already in Compose.
- Do NOT change any calling code in HomeScreen, CategoriesScreen, etc.

## Verification

- **Mechanical**: Code should compile without errors.
- **Feel check**:
  - Launch the app → tap FAB and hold
  - Verify the button scales down smoothly (0.95 scale)
  - Release → button scales back to 1.0
  - In DevTools Animations panel (set to 10% speed), confirm scale animation is smooth with no jumps
  - Tap rapidly — each press should trigger a fresh scale animation
  - On a real device, verify feedback is immediate and not delayed
- **Done when**: Every FAB tap produces consistent press feedback with 160ms ease-out animation.