# Koki ni maya

Abstract motion graphics for romantic connection.

Two ethereal ribbons of light — one warm peach, one soft lavender — dance, twist, and intertwine in slow motion against a deep velvet background. They braid into an abstract heart, hold it with a soft heartbeat, then dissolve into stardust that spells out **Koki ni maya**.

---

## Run the animation

```bash
node tools/server.mjs
# open http://localhost:3000
```

**Controls** (hover to reveal):
| Key | Action |
|---|---|
| `Space` | play / pause |
| `← →` | step ±0.5 s |
| `R` | restart |
| `F` | toggle fullscreen |

The scrub bar at the bottom lets you drag to any moment in the 26.5-second piece.

---

## Timeline

| Time | Moment |
|------|--------|
| 0 s | Deep velvet. Fade in begins. |
| 1 s | Two ribbons fully present — free braided dance. |
| 7 – 11 s | The ribbons flow into a heart. Staggered landing; the tips arrive last. |
| 11 – 15 s | The heart holds. A soft lub-dub heartbeat breathes it. |
| 15 – 20 s | The ribbons dissolve into stardust. Dust flies along bezier arcs and spells the name. |
| 20 – 24 s | **Koki ni maya** — the name breathes in the glow. |
| 24 – 27 s | Fade back to velvet. |

---

## Render stills offline

```bash
# render keyframes into /tmp/frames
node tools/render.mjs --t 0,5,10.35,15,20,24 --w 2400 --h 1350 --out /tmp/frames
```

Pre-rendered key art lives in [`assets/`](assets/):
- `keyart-heart.png` — the heart at t ≈ 10.35 s
- `keyart-text.png` — the name at t ≈ 21 s

---

## How it works

The whole piece is a **pure function of time** — no accumulated state, every frame is independently renderable. The engine runs both in the browser (requestAnimationFrame) and in Node via `@napi-rs/canvas`.

**Techniques used:**
- **Glass ribbon texture mapping** — a cross-section texture baked for every twist phase (rim highlight, specular core, subsurface bleed, thin-film fringe) is sampled per quad segment, so highlights sweep along the silk as the ribbon turns.
- **Depth-sorted segments** — the two ribbons are sorted per-frame so they genuinely weave in front of / behind each other. Front-facing segments punch a hole in the glow layer.
- **Hand-rolled bloom** — luminous things are drawn into a low-resolution glow layer, which is then upscaled back additively (no shadow API, no CSS filters).
- **Deterministic noise** — a 3D value-noise + FBM for organic, reproducible turbulence (Mulberry32 seed, `noise3`, `fbm3`).
- **Motion smear** — a couple of slightly-earlier time-offset copies give a silky, 60fps feel even at lower frame rates.
- **Stardust particles** — 2300 particles born on the heart outline, flying along bezier arcs to their glyph point in the name, ranked left-to-right so the name is *written* by the cloud.
- **Cached velvet backdrop** — gradient, fold bands, warm pool, vignette, and grain are baked once on resize.

---

## Font

**Cormorant Garamond** — used for the stardust lettering, licensed under the  
[SIL Open Font License 1.1](src/fonts/OFL.txt).  
Source: [github.com/CatharsisFonts/Cormorant](https://github.com/CatharsisFonts/Cormorant)

---

Built for Maya & Koki.