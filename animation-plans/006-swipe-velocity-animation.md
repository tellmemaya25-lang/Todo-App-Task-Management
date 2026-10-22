# 006 — Add Velocity-Based Swipe-to-Dismiss Animation

- **Status**: TODO
- **Commit**: 71e06f8
- **Severity**: LOW
- **Category**: Interruptibility, Missed opportunities
- **Estimated scope**: 1 file, medium

## Problem

The swipe-to-dismiss interaction in `HomeScreen.kt` uses `SwipeToDismissBox` with default animations. When users swipe tasks to delete or complete them, the state snaps instantly after release — there's no momentum-based animation that carries the swipe gesture velocity. This feels disconnected and loses the sense of physical motion the user initiated.

```kotlin
// app/src/main/java/com/sabihon/todo/ui/home/HomeScreen.kt:228 (current)
SwipeToDismissBox(
    state = dismissState,
    backgroundContent = { /* ... */ },
    modifier = Modifier.animateContentSize()
) {
    Box {
        TaskRow(...)
        DropdownMenu(...)
    }
}
```

The box animates size but doesn't carry velocity from the swipe gesture into the exit animation.

## Target

Enhance swipe-to-dismiss to:
- **Detect swipe velocity** on gesture release
- **Dismiss with momentum** if velocity exceeds threshold (~0.11 px/ms or ~110 dp/second)
- **Animate exit smoothly** using the velocity to inform animation duration
- **Preserve reduced-motion** by using instant dismissal when accessibility setting is enabled

Pattern:
```kotlin
val dismissState = rememberSwipeToDismissBoxState(
    confirmValueChange = { dismissValue ->
        if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
            // Calculate velocity (distance / elapsed time)
            val velocityDp = abs(dismissState.currentValue) / dismissState.progress
            if (velocityDp > 110.dp.value || isReducedMotion) {
                // Dismiss with momentum or instantly
                viewModel.onAction(HomeAction.DeleteTask(task))
                true // Dismiss
            } else {
                false // Snap back
            }
        } else {
            false
        }
    }
)
```

## Repo conventions to follow

- Use `SwipeToDismissBox` built-in velocity tracking (via `dismissState.targetValue`)
- Animation duration scales with velocity (faster swipe = shorter duration)
- Minimum velocity threshold: 110 dp/second (standard for Material motion)
- Respect reduced motion (instant dismiss when accessibility enabled)

## Steps

1. **Open** `app/src/main/java/com/sabihon/todo/ui/home/HomeScreen.kt`

2. **Add imports**:
   ```kotlin
   import com.sabihon.todo.core.ui.extensions.useReducedMotion
   import kotlin.math.abs
   ```

3. **Inside HomeScreen composable**, add:
   ```kotlin
   val isReducedMotion = useReducedMotion()
   ```

4. **Find the `rememberSwipeToDismissBoxState`** (around line 223) and update:
   ```kotlin
   val dismissState = rememberSwipeToDismissBoxState(
       confirmValueChange = { dismissValue ->
           when (dismissValue) {
               SwipeToDismissBoxValue.StartToEnd -> {
                   viewModel.onAction(HomeAction.ToggleComplete(task.id, !task.isCompleted))
                   false
               }
               SwipeToDismissBoxValue.EndToStart -> {
                   // Velocity-based dismissal
                   val shouldDismiss = if (isReducedMotion) {
                       true // Instant dismiss for accessibility
                   } else {
                       // Check if swipe was fast enough
                       abs(dismissState.offset.value) > 100f // threshold in pixels
                   }
                   if (shouldDismiss) {
                       viewModel.onAction(HomeAction.DeleteTask(task))
                       true // Dismiss
                   } else {
                       false // Snap back
                   }
               }
               else -> false
           }
       }
   )
   ```

5. **Ensure SwipeToDismissBox still animates** with `animateContentSize()`:
   ```kotlin
   SwipeToDismissBox(
       state = dismissState,
       backgroundContent = { /* ... */ },
       modifier = Modifier.animateContentSize()
   ) { /* ... */ }
   ```

## Boundaries

- Do NOT modify the background content or colors
- Do NOT change the swipe direction (left = delete, right = complete)
- Do NOT add new dependencies — use built-in Compose APIs
- Do NOT change the ViewModel actions

## Verification

- **Mechanical**: Code compiles without errors.
- **Feel check**:
  - Launch app → swipe a task slowly to the left — should snap back if released early
  - Swipe a task quickly/fully to the left → should dismiss smoothly
  - Swipe to delete → item exits the list with momentum
  - In reduced-motion mode → swipes dismiss/complete instantly without animation overshoot
  - Rapid swipes should feel responsive and natural
- **Done when**: Swipe gestures carry momentum into the exit animation, and reduced motion is respected.