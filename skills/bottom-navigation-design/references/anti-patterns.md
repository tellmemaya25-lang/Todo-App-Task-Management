# Anti-patterns: named failure modes

Each entry: what it looks like, why it hurts, and the fix. Map issues found in a review to these names so feedback is concrete and repeatable.

---

### 1. The junk drawer

**Looks like:** Help, Settings, Log out, Privacy Policy, and Terms all sitting in the bottom bar.

**Why it fails:** Bottom nav is the most valuable surface in the app. Spending it on things nobody taps means the things people *do* tap get crowded out.

**Fix:** Keep only top-level destinations. Help/FAQ → profile or side menu. Log out → profile. Legal → footer of profile/settings.

---

### 2. Backwards nav

**Looks like:** A back arrow, a forward chevron, or the app logo in the bottom bar.

**Why it fails:** Violates Jacob's Law — users already know back and logos live at the *top*. Putting them at the bottom makes the app feel subtly broken.

**Fix:** Back/forward/logo stay in top navigation. Bottom bar holds destinations only.

---

### 3. Seven tabs of doom

**Looks like:** 6–8 tabs crammed edge to edge, each icon barely 20 px wide.

**Why it fails:** Choice paralysis plus mis-taps. Every added tab shrinks the tap area of all the others.

**Fix:** Cut to 3–5 by frequency × importance. Demote the rest into a "More" destination, a side menu, or a search entry point.

---

### 4. The pixel hunt

**Looks like:** A 24 × 24 px icon that is also the entire tappable area.

**Why it fails:** Thumbs are ~44 px+. Small targets cause mis-taps and exclude users with limited dexterity.

**Fix:** Keep the 24 px icon *visually*, but expand the hit area to ≥44 × 44 pt / 48 × 48 dp.

---

### 5. Home-indicator crush

**Looks like:** Tabs sitting flush against the bottom edge, overlapping the home indicator.

**Why it fails:** Users swipe-to-home when they meant to tap a tab, and the bar feels broken and unfinished — especially one-handed.

**Fix:** Respect the safe area. Put the bar above the home indicator; extend only the background into the inset, never the controls.

---

### 6. Where am I?

**Looks like:** The active tab differs from the others only by slightly darker label text; the icon is identical.

**Why it fails:** Users have to actively hunt for their location, and every tap feels uncertain.

**Fix:** Use ≥2 signals — filled icon + color change, or color change + bolder/darker label.

---

### 7. Ghost tabs

**Looks like:** Inactive icons in ultra-light gray that nearly vanish, or vice versa — inactive icons so dark they read as active.

**Why it fails:** Too faint fails accessibility (and fails in sunlight); too strong destroys the active/inactive distinction.

**Fix:** Slightly reduce opacity instead of switching colors. Verify: icons ≥3:1, text ≥4.5:1.

---

### 8. Mixed metaphor

**Looks like:** Three outline icons and two filled icons at rest, or one hyper-detailed glyph next to a minimal one.

**Why it fails:** Reads as unfinished — "a black-tie event with someone in beachwear." It also dilutes the meaning of the filled selected state.

**Fix:** One style, one stroke weight, one level of detail. Reserve fill for selected.

---

### 9. The rainbow bar

**Looks like:** Home is blue, search green, add orange, profile purple.

**Why it fails:** Navigation becomes a color-matching game and pulls attention off the content. It also wrecks brand recognition.

**Fix:** Neutral chrome; one accent for the active state; brand palette only.

---

### 10. Novelty icons

**Looks like:** Binoculars for search, an abstract swirl for profile, a custom glyph nobody has seen before.

**Why it fails:** If users must decode the icon, the destination is effectively hidden from them.

**Fix:** Use the conventional glyph. Magnifying glass for search, person for profile, house for home. Clarity over originality.

---

### 11. War and peace labels

**Looks like:** "Discover New Content" wrapping to two lines under a tiny icon.

**Why it fails:** Two-line labels balloon the bar height and clutter the layout.

**Fix:** One word. "Search". "Saved". "Profile". Truncate the *destination*, not the layout.

---

### 12. The melting bar  ⭐ *the one pros get wrong*

**Looks like:** Content scrolls right into the navigation with no boundary — same background, no border, no shadow.

**Why it fails:** Users can't tell where content ends and controls begin. The bar feels like part of the page rather than a persistent control.

**Fix:** Add separation — a 1px hairline, a contrasting background (white content / light gray bar), or a soft elevation shadow. Keep the shadow subtle.

---

### 13. Heavy metal shadow

**Looks like:** A large, dark, diffuse drop shadow above the bar.

**Why it fails:** Overcorrecting #12. A harsh shadow shouts and cheapens the design.

**Fix:** Tight, low-opacity, small-radius shadow — or just use a hairline.

---

### 14. Boxed in

**Looks like:** Every tab sitting in its own outlined box or pill, even when inactive.

**Why it fails:** Pure visual noise; it makes the bar look like a toolbar from 2009.

**Fix:** Bare icon + label. Reserve any container shape for the active indicator only.

---

### 15. Badge spam

**Looks like:** Every tab carries a red dot; counts in the hundreds on three tabs at once.

**Why it fails:** Notification fatigue. When everything is urgent, users stop looking at all of it.

**Fix:** Badge only what's essential. Cap counts ("9+"). Clear on view.

---

### 16. The dead tap

**Looks like:** Tap a tab and it changes instantly with zero feedback — no ripple, no scale, no color shift.

**Why it fails:** It feels like a broken light switch with no click. The app reads as static and unresponsive.

**Fix:** Immediate tap feedback (≤100 ms), then animate the switch.

---

### 17. Teleport transition

**Looks like:** Content hard-cuts between tabs with no transition.

**Why it fails:** Users lose their sense of place; screens feel disconnected.

**Fix:** Fade or directional slide, 200–300 ms, reduced-motion aware.

---

### 18. Beautiful and unusable

**Looks like:** A stunning custom-shaped bar with non-standard icon arrangement that testers can't navigate on first try.

**Why it fails:** Aesthetics won a battle they shouldn't have been in.

**Fix:** Creativity is allowed — after usability passes. Validate with a real tap-through before shipping the flourish.
