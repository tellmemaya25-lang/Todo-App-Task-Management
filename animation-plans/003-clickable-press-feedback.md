# 003 — Add Press Feedback to All Clickable Elements

- **Status**: TODO
- **Commit**: 71e06f8
- **Severity**: HIGH
- **Category**: Press feedback, Purpose & frequency
- **Estimated scope**: 5 files, medium

## Problem

Multiple frequently-clicked UI elements have **no press feedback**: TaskRow, CategoryCard, PastelStatTile, and PriorityChip all use bare `.clickable {}` modifiers with no scale or visual response during press. Users get no confirmation that their tap registered, making the app feel sluggish and unresponsive.

Affected files:
- `TaskRow.kt:54` — `.clickable()` on task rows (tapped 100+ times/day)
- `CategoryCard.kt:54` — `.clickable()` on category cards
- `PastelStatTile.kt:53` — `.clickable()` on stat tiles
- `PriorityChip.kt:60` — `.clickable()` on priority selection chips

```kotlin
// Example from TaskRow.kt:54 (current)
modifier = modifier
    .fillMaxWidth()
    .clickable(enabled = onClick != null) { onClick?.invoke() }
    .padding(horizontal = 20.dp, vertical = 12.dp)
    .animateContentSize(),
```

No scale, no press indication.

## Target

All clickable elements should use a **scale-on-press** helper that:
- Scales to **0.97** on press (subtle, not as aggressive as FAB's 0.95)
- Uses **160ms ease-out** animation
- Works with `.clickable()` via `MutableInteractionSource`
- Is reusable across all components

Target pattern:
```kotlin
modifier = modifier
    .fillMaxWidth()
    .scaleOnPress(scale = 0.97f)
    .clickable(enabled = onClick != null, interactionSource = interactionSource) { onClick?.invoke() }
    .padding(horizontal = 20.dp, vertical = 12.dp)
```

## Repo conventions to follow

- Create a reusable `Modifier.scaleOnPress()` extension in a new file: `core/ui/extensions/ScaleOnPress.kt`
- Animation timing: 160ms tween with CubicOut easing (same as FAB)
- Scale value: 0.97 for cards/rows/chips, 0.95 for primary actions (FAB already uses 0.95)
- Use `MutableInteractionSource` pattern (same as FAB implementation)

## Steps

1. **Create** `app/src/main/java/com/sabihon/todo/core/ui/extensions/ScaleOnPress.kt`:
   ```kotlin
   package com.sabihon.todo.core.ui.extensions

   import androidx.compose.animation.core.animateFloatAsState
   import androidx.compose.animation.core.CubicOut
   import androidx.compose.animation.core.tween
   import androidx.compose.foundation.interaction.InteractionSource
   import androidx.compose.foundation.interaction.PressInteraction
   import androidx.compose.material3.LocalContentColor
   import androidx.compose.runtime.LaunchedEffect
   import androidx.compose.runtime.getValue
   import androidx.compose.runtime.mutableStateOf
   import androidx.compose.runtime.remember
   import androidx.compose.runtime.setValue
   import androidx.compose.ui.Modifier
   import androidx.compose.ui.graphics.graphicsLayer

   fun Modifier.scaleOnPress(
       scale: Float = 0.97f,
       interactionSource: InteractionSource
   ): Modifier = composed {
       var isPressed by remember { mutableStateOf(false) }

       LaunchedEffect(interactionSource) {
           interactionSource.interactions.collect { interaction ->
               when (interaction) {
                   is PressInteraction.Press -> isPressed = true
                   is PressInteraction.Release -> isPressed = false
                   is PressInteraction.Cancel -> isPressed = false
               }
           }
       }

       val animatedScale by animateFloatAsState(
           targetValue = if (isPressed) scale else 1.0f,
           animationSpec = tween(160, easing = CubicOut),
           label = "pressScale"
       )

       graphicsLayer { scaleX = animatedScale; scaleY = animatedScale }
   }
   ```

2. **Update TaskRow.kt**:
   - Add import: `import com.sabihon.todo.core.ui.extensions.scaleOnPress`
   - Change `.clickable()` call to include `interactionSource`:
   ```kotlin
   val interactionSource = remember { MutableInteractionSource() }
   modifier = modifier
       .fillMaxWidth()
       .scaleOnPress(scale = 0.97f, interactionSource = interactionSource)
       .clickable(
           enabled = onClick != null,
           interactionSource = interactionSource,
           indication = ripple()
       ) { onClick?.invoke() }
       .padding(horizontal = 20.dp, vertical = 12.dp)
       .animateContentSize()
   ```

3. **Update CategoryCard.kt**:
   - Add import: `import androidx.compose.foundation.interaction.MutableInteractionSource`
   - Add import: `import com.sabihon.todo.core.ui.extensions.scaleOnPress`
   - Add import: `import androidx.compose.material.ripple.ripple`
   - Create `interactionSource` in composable
   - Apply `.scaleOnPress()` before `.clickable()`

4. **Update PastelStatTile.kt**:
   - Same pattern as CategoryCard

5. **Update PriorityChip.kt**:
   - Same pattern as CategoryCard

## Boundaries

- Do NOT change the structure or layout of any components — only add animation to existing `.clickable()` calls
- Do NOT modify FAB (Plan 002 is already done)
- Do NOT add new dependencies — use built-in Compose APIs only
- Do NOT change component sizes, colors, or shapes

## Verification

- **Mechanical**: Code should compile without errors.
- **Feel check**:
  - Launch app → tap any task row, category card, or stat tile
  - Verify smooth scale-down (0.97) on press, scale-up on release
  - Rapid taps should trigger fresh animations each time
  - In DevTools (10% speed), confirm animations are smooth with no jank
  - Test on real device — all interactive elements should feel responsive and consistent
  - Swipe interactions (like swipe-to-dismiss) should not interfere with press feedback
- **Done when**: Every clickable element (tasks, categories, stat tiles, chips) provides consistent press feedback with 160ms scale animation.