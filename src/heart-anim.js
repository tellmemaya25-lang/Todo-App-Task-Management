/**
 * "Koki ni maya" — abstract motion graphics for romantic connection.
 *
 * Two ethereal ribbons of light — one warm peach, one soft lavender — dance,
 * twist and intertwine in slow motion against a deep velvet background. They
 * braid into an abstract heart, hold it with a soft heartbeat, then dissolve
 * into stardust that spells out "Koki ni maya".
 *
 * Design notes
 * ------------
 *  - The whole piece is a *pure function of time*: no accumulated state, so
 *    every frame can be rendered on its own (scrubbing, offline export, video).
 *  - Only the 2D canvas API is used, and only features available both in
 *    browsers and in Node through @napi-rs/canvas (no ctx.filter, no shadow).
 *  - Glow is a hand-rolled two-tap bloom: luminous things are drawn into a
 *    low-resolution "glow" layer which is then upscaled back additively.
 *  - Ribbons are texture-mapped quads: a cross-section texture baked for every
 *    twist phase of a glass ribbon (rim highlight, specular core, subsurface
 *    bleed, thin-film fringe) is sampled per segment, so the highlight sweeps
 *    along the ribbon as it turns.
 *  - Segments are depth sorted, and near segments punch a hole in the glow
 *    layer, so the two ribbons genuinely weave in front of / behind each other.
 */

export const VERSION = '1.0.0';

export const TAU = Math.PI * 2;

/* ------------------------------------------------------------------ *
 * math helpers
 * ------------------------------------------------------------------ */

export const clamp = (v, a, b) => (v < a ? a : v > b ? b : v);
export const lerp = (a, b, t) => a + (b - a) * t;
export const fract = (v) => v - Math.floor(v);

export function smoothstep(edge0, edge1, x) {
  const t = clamp((x - edge0) / (edge1 - edge0 || 1e-6), 0, 1);
  return t * t * (3 - 2 * t);
}

export const easeInOut = (t) => {
  t = clamp(t, 0, 1);
  return t < 0.5 ? 4 * t * t * t : 1 - Math.pow(-2 * t + 2, 3) / 2;
};
export const easeOut = (t) => 1 - Math.pow(1 - clamp(t, 0, 1), 3);

export function makeRng(seed) {
  let a = (seed | 0) >>> 0;
  return function rng() {
    a = (a + 0x6d2b79f5) >>> 0;
    let t = a;
    t = Math.imul(t ^ (t >>> 15), t | 1);
    t ^= t + Math.imul(t ^ (t >>> 7), t | 61);
    return ((t ^ (t >>> 14)) >>> 0) / 4294967296;
  };
}

/* 3D value noise ---------------------------------------------------- */

function hash3(ix, iy, iz, seed) {
  let h = Math.imul(ix | 0, 374761393) ^ Math.imul(iy | 0, 668265263) ^
    Math.imul(iz | 0, 1442695041) ^ Math.imul(seed | 0, 971);
  h = Math.imul(h ^ (h >>> 13), 1274126177);
  h ^= h >>> 16;
  return (h >>> 0) / 4294967296;
}

export function noise3(x, y, z, seed = 0) {
  const ix = Math.floor(x), iy = Math.floor(y), iz = Math.floor(z);
  const fx = x - ix, fy = y - iy, fz = z - iz;
  const ux = fx * fx * (3 - 2 * fx);
  const uy = fy * fy * (3 - 2 * fy);
  const uz = fz * fz * (3 - 2 * fz);
  const c000 = hash3(ix, iy, iz, seed), c100 = hash3(ix + 1, iy, iz, seed);
  const c010 = hash3(ix, iy + 1, iz, seed), c110 = hash3(ix + 1, iy + 1, iz, seed);
  const c001 = hash3(ix, iy, iz + 1, seed), c101 = hash3(ix + 1, iy, iz + 1, seed);
  const c011 = hash3(ix, iy + 1, iz + 1, seed), c111 = hash3(ix + 1, iy + 1, iz + 1, seed);
  const x00 = lerp(c000, c100, ux), x10 = lerp(c010, c110, ux);
  const x01 = lerp(c001, c101, ux), x11 = lerp(c011, c111, ux);
  return lerp(lerp(x00, x10, uy), lerp(x01, x11, uy), uz);
}

/** signed fractal noise, roughly [-1, 1] */
export function fbm3(x, y, z, seed = 0, octaves = 2) {
  let sum = 0, amp = 1, norm = 0, f = 1;
  for (let o = 0; o < octaves; o++) {
    sum += amp * (noise3(x * f, y * f, z * f, seed + o * 131) * 2 - 1);
    norm += amp;
    amp *= 0.5;
    f *= 2.03;
  }
  return sum / norm;
}

/* ------------------------------------------------------------------ *
 * canvas factory (browser by default, injectable for node/offscreen)
 * ------------------------------------------------------------------ */

let canvasFactory = null;

export function setCanvasFactory(fn) {
  canvasFactory = fn;
}

function makeCanvas(w, h) {
  if (canvasFactory) return canvasFactory(w, h);
  if (typeof document !== 'undefined' && document.createElement) {
    const c = document.createElement('canvas');
    c.width = w; c.height = h;
    return c;
  }
  throw new Error('heart-anim: no canvas implementation available (call setCanvasFactory)');
}

/* ------------------------------------------------------------------ *
 * timeline — the whole piece is a pure function of t (seconds)
 * ------------------------------------------------------------------ */

export const TIMELINE = {
  duration: 26.5,
  fadeIn: 0.9,
  danceIn: 1.1,       // ribbons fully present
  heartStart: 7.3,    // begin flowing into the heart
  heartFormed: 10.6,  // heart fully formed
  heartRelease: 14.7, // the heart begins to give way
  dustStart: 15.4,    // ribbons are dust from here on
  textFormed: 20.3,   // the name is fully spelled by the stardust
  textHold: 24.1,     // the name breathes until here
  fadeOut: 25.3,      // fade back to velvet
  end: 26.5,
};

export function heartAmount(t) {
  const up = smoothstep(TIMELINE.heartStart, TIMELINE.heartFormed, t);
  const down = 1 - smoothstep(TIMELINE.heartRelease, TIMELINE.dustStart, t);
  return clamp(Math.min(up, down), 0, 1);
}

export function ribbonAlpha(t) {
  const inA = smoothstep(0.04, TIMELINE.danceIn, t);
  const outA = 1 - smoothstep(TIMELINE.heartRelease + 0.15, TIMELINE.dustStart + 0.15, t);
  return clamp(inA * outA, 0, 1);
}

export function dustAmount(t) {
  return smoothstep(TIMELINE.heartRelease + 0.3, TIMELINE.dustStart + 0.1, t);
}

/** double thump per cycle — lub-dub */
export function heartbeat(t, period = 1.25) {
  const p = fract(t / period) * period;
  const a = Math.exp(-Math.pow(p / 0.085, 2));
  const b = 0.55 * Math.exp(-Math.pow((p - 0.27) / 0.105, 2));
  return clamp(a + b, 0, 1.35);
}

/* ------------------------------------------------------------------ *
 * palette
 * ------------------------------------------------------------------ */

export const PALETTE = {
  peach: [255, 176, 130],
  peachHot: [255, 236, 214],
  peachCool: [255, 146, 172],
  lavender: [178, 154, 255],
  lavenderHot: [238, 232, 255],
  lavenderCool: [138, 188, 255],
  white: [255, 250, 246],
};

const rgba = (c, a) => `rgba(${c[0] | 0},${c[1] | 0},${c[2] | 0},${a})`;

/* ------------------------------------------------------------------ *
 * baked textures
 * ------------------------------------------------------------------ */

/**
 * A glass ribbon cross-section baked across every twist phase.
 *   x = across the ribbon width,  y = twist phase (0..TAU)
 */
export function makeRibbonTexture(base, hot, cool, opts = {}) {
  const CW = opts.width || 96;
  const CH = opts.height || 192;
  const canvas = makeCanvas(CW, CH);
  const ctx = canvas.getContext('2d');
  const img = ctx.createImageData(CW, CH);
  const d = img.data;

  for (let y = 0; y < CH; y++) {
    const phase = (y / CH) * TAU;
    const faceOn = Math.cos(phase);
    const edgeOn = Math.abs(Math.sin(phase));
    const core = Math.sin(phase) * 0.5;
    const coreW = 0.075 + 0.055 * Math.abs(faceOn);

    for (let x = 0; x < CW; x++) {
      const s = ((x + 0.5) / CW) * 2 - 1;
      const as = Math.abs(s);

      // glass body — denser towards the middle, dimmer when edge-on
      const thickness = Math.pow(Math.max(0, 1 - as * as), 0.72);
      let R = base[0] * 0.5, G = base[1] * 0.5, B = base[2] * 0.5;
      let A = (0.20 + 0.40 * thickness) * (0.70 + 0.30 * (0.5 + 0.5 * faceOn));

      // thin-film fringe: cool on one edge, warm on the other
      const fr = lerp(cool[0], hot[0], clamp(s * 0.5 + 0.5, 0, 1));
      const fg = lerp(cool[1], hot[1], clamp(s * 0.5 + 0.5, 0, 1));
      const fb = lerp(cool[2], hot[2], clamp(s * 0.5 + 0.5, 0, 1));

      const rim = Math.exp(-Math.pow((as - 0.94) / 0.06, 2));
      const rimA = clamp(rim * (0.20 + 0.70 * edgeOn), 0, 1);
      const inner = clamp(Math.exp(-Math.pow((as - 0.78) / 0.17, 2)) * 0.30, 0, 1);
      const ss = clamp(Math.exp(-Math.pow((s - core * 0.85) / 0.30, 2)) * 0.55, 0, 1);
      const sp = clamp(Math.exp(-Math.pow((s - core) / coreW, 2)) * (0.45 + 0.55 * (0.5 + 0.5 * faceOn)), 0, 1);
      const dark = Math.exp(-Math.pow((s + 0.14) / 0.36, 2)) * (0.20 - 0.10 * edgeOn);

      const over = (cr, cg, cb, ca) => {
        R = R * (1 - ca) + cr * ca;
        G = G * (1 - ca) + cg * ca;
        B = B * (1 - ca) + cb * ca;
        A = A + (1 - A) * ca;
      };
      over(base[0], base[1], base[2], inner);
      over(fr, fg, fb, rimA);
      over(lerp(base[0], hot[0], 0.5), lerp(base[1], hot[1], 0.5), lerp(base[2], hot[2], 0.5), ss);
      R *= 1 - dark; G *= 1 - dark; B *= 1 - dark;
      over(lerp(hot[0], 255, 0.45), lerp(hot[1], 255, 0.45), lerp(hot[2], 255, 0.45), sp);

      const i = (y * CW + x) * 4;
      d[i] = clamp(R, 0, 255);
      d[i + 1] = clamp(G, 0, 255);
      d[i + 2] = clamp(B, 0, 255);
      d[i + 3] = clamp(A, 0, 1) * 255;
    }
  }
  ctx.putImageData(img, 0, 0);
  return canvas;
}

/** wide soft additive band used for the ribbon's halo */
export function makeGlowBand(color, width = 96, height = 2) {
  const canvas = makeCanvas(width, height);
  const ctx = canvas.getContext('2d');
  const img = ctx.createImageData(width, height);
  const d = img.data;
  for (let y = 0; y < height; y++) {
    for (let x = 0; x < width; x++) {
      const s = ((x + 0.5) / width) * 2 - 1;
      const v = Math.pow(Math.max(0, 1 - Math.abs(s)), 1.7);
      const i = (y * width + x) * 4;
      d[i] = color[0]; d[i + 1] = color[1]; d[i + 2] = color[2];
      d[i + 3] = v * 255;
    }
  }
  ctx.putImageData(img, 0, 0);
  return canvas;
}

/** soft round dust sprite */
export function makeDustSprite(color, size = 32) {
  const canvas = makeCanvas(size, size);
  const ctx = canvas.getContext('2d');
  const img = ctx.createImageData(size, size);
  const d = img.data;
  const c = (size - 1) / 2;
  for (let y = 0; y < size; y++) {
    for (let x = 0; x < size; x++) {
      const dx = (x - c) / c, dy = (y - c) / c;
      const r = Math.sqrt(dx * dx + dy * dy);
      const core = Math.exp(-Math.pow(r / 0.30, 2));
      const halo = Math.exp(-Math.pow(r / 0.80, 2)) * 0.30;
      const a = clamp(core + halo, 0, 1);
      const i = (y * size + x) * 4;
      d[i] = color[0]; d[i + 1] = color[1]; d[i + 2] = color[2];
      d[i + 3] = a * 255;
    }
  }
  ctx.putImageData(img, 0, 0);
  return canvas;
}

/* ------------------------------------------------------------------ *
 * curves
 * ------------------------------------------------------------------ */

/** classic heart curve (x in [-16,16], y in [-17,12]) */
function heartCurve(th, out) {
  const s = Math.sin(th);
  out[0] = 16 * s * s * s;
  out[1] = 13 * Math.cos(th) - 5 * Math.cos(2 * th) - 2 * Math.cos(3 * th) - Math.cos(4 * th);
  return out;
}

/**
 * Free dance, in world space (x in [-1,1], y in [-1/aspect, 1/aspect], z ~ [-0.5,0.5]).
 * Both ribbons ride a slowly undulating spine and orbit it in opposite phase,
 * so they braid around each other and keep swapping depth.
 */
export function freePoint(r, u, t, out) {
  const ph = r === 0 ? 0 : 0.62;
  const dir = r === 0 ? 1 : -1;

  const sx =
    0.24 * Math.sin(TAU * 0.50 * u + 0.40 * t + ph * 1.7) +
    0.30 * fbm3(u * 1.5, t * 0.13, ph * 3.1, 11);
  const sy = (u - 0.5) * 1.62 + 0.10 * Math.sin(TAU * 0.82 * u + 0.52 * t + ph);
  const sz = 0.13 * Math.sin(TAU * 0.61 * u + 0.55 * t + ph * 2.3);

  const radius = 0.29 * Math.pow(Math.sin(Math.PI * u), 0.6) * (r === 0 ? 1 : 0.93);
  const ang =
    dir * TAU * 1.06 * u + 0.52 * t + (r === 0 ? 0 : Math.PI) +
    0.55 * fbm3(u * 1.1, t * 0.09, ph, 27);

  out[0] = sx + radius * Math.cos(ang) + 0.045 * fbm3(u * 3.1, t * 0.22, 3.3 + ph, 41);
  out[1] = sy + 0.30 * radius * Math.sin(ang + 0.9) + 0.035 * fbm3(u * 2.7, t * 0.19, 5.1 + ph, 53);
  out[2] = sz + radius * Math.sin(ang) + 0.07 * fbm3(u * 2.2, t * 0.16, 7.7 + ph, 67);
  return out;
}

/** the heart outline, in world space, for ribbon r's half */
function heartPoint(r, u, t, k, hs, out) {
  const over = 0.12;
  const th = r === 0
    ? lerp(-over * Math.PI, (1 + over) * Math.PI, u)
    : lerp((1 - over) * Math.PI, (2 + over) * Math.PI, u);

  heartCurve(th, tmpCurve);
  const beat = heartbeat(t);
  const breathe = 1 + 0.017 * beat;

  let wx = tmpCurve[0] * hs * breathe;
  let wy = (-(tmpCurve[1] + 2.6) * hs - 0.015) * breathe;
  let wz =
    0.045 * Math.sin(TAU * 1.25 * u + 0.5 + r * 2.1) * (r === 0 ? 1 : -1) +
    0.018 * Math.sin(0.42 * t + r * 1.4) +
    0.012 * Math.sin(TAU * 2.2 * u + 1.1);

  // while forming, the ribbon bows outward a little before it settles
  const bow = Math.sin(Math.PI * clamp(k, 0, 1)) * 0.26;
  wx += -Math.cos(th) * bow * (r === 0 ? 0.30 : -0.30) * hs * 6;
  wy += Math.sin(th) * bow * (r === 0 ? 0.30 : -0.30) * hs * 6;

  out[0] = wx; out[1] = wy; out[2] = wz;
  return out;
}
const tmpCurve = [0, 0];

/* ------------------------------------------------------------------ *
 * scene
 * ------------------------------------------------------------------ */

const DEFAULTS = {
  text: 'Koki ni maya',
  fontFamily: '"Cormorant Garamond", "Times New Roman", Georgia, serif',
  fontStyle: 'italic',
  fontWeight: 500,
  tracking: 0.055,
  textWidthRatio: 0.72,
  dpr: 1,
  segments: 168,
  particleCount: 2300,
  emberCount: 64,
  seed: 1402,
  glowScale: 0.25,
  haloScale: 0.07,
  occluder: true,
  smear: true,
  grain: 0.05,
  width: null,
  height: null,
};

export function createScene(canvas, options = {}) {
  return new Scene(canvas, options);
}

class Scene {
  constructor(canvas, options = {}) {
    this.canvas = canvas;
    this.opts = Object.assign({}, DEFAULTS, options);
    this.ctx = canvas.getContext('2d');

    this.textures = {
      ribbon: [
        makeRibbonTexture(PALETTE.peach, PALETTE.peachHot, PALETTE.peachCool),
        makeRibbonTexture(PALETTE.lavender, PALETTE.lavenderHot, PALETTE.lavenderCool),
      ],
      glow: [makeGlowBand(PALETTE.peach), makeGlowBand(PALETTE.lavender)],
      dust: [
        makeDustSprite(PALETTE.peachHot),
        makeDustSprite(PALETTE.lavenderHot),
        makeDustSprite(PALETTE.white),
      ],
    };

    this.layers = {};
    this.time = 0;
    this.resize();
  }

  /* ---------------- layout ---------------- */

  resize(width, height) {
    const o = this.opts;
    const dpr = o.dpr || 1;
    this.W = width || o.width || this.canvas.clientWidth || this.canvas.width / dpr || 1280;
    this.H = height || o.height || this.canvas.clientHeight || this.canvas.height / dpr || 720;
    this.dpr = dpr;

    this.canvas.width = Math.max(2, Math.round(this.W * dpr));
    this.canvas.height = Math.max(2, Math.round(this.H * dpr));

    this.aspect = this.W / this.H;
    this.scale = this.W / 2;          // world x -> css px
    this.cx = this.W / 2;
    this.cy = this.H / 2;
    this.worldHalfH = 1 / this.aspect;
    this.cam = 3.2;

    // the heart fits the frame with air to spare
    this.heartScale = Math.min(0.60 / 16, (this.worldHalfH * 0.80) / 14.6);

    this._makeLayers();
    this._makeBackground();
    this._sampleText();
  }

  _makeLayers() {
    const mk = (w, h) => {
      const c = makeCanvas(Math.max(2, Math.round(w)), Math.max(2, Math.round(h)));
      return { canvas: c, ctx: c.getContext('2d'), w: c.width, h: c.height };
    };
    const L = this.layers;
    L.glow = mk(this.W * this.opts.glowScale, this.H * this.opts.glowScale);
    L.halo = mk(this.W * this.opts.haloScale, this.H * this.opts.haloScale);
    L.occ = mk(this.W * this.opts.glowScale, this.H * this.opts.glowScale);
    L.bg = mk(this.W * this.dpr, this.H * this.dpr);
  }

  /** baked velvet backdrop: gradient, folds, pool of light, vignette, grain */
  _makeBackground() {
    const { ctx: c, w, h } = this.layers.bg;
    const rng = makeRng(this.opts.seed + 7);
    const W = w / this.dpr, H = h / this.dpr;

    c.setTransform(this.dpr, 0, 0, this.dpr, 0, 0);
    c.clearRect(0, 0, W, H);
    c.globalCompositeOperation = 'source-over';

    const base = c.createLinearGradient(0, 0, 0, H);
    base.addColorStop(0, '#0a0612');
    base.addColorStop(0.45, '#150a16');
    base.addColorStop(1, '#060309');
    c.fillStyle = base;
    c.fillRect(0, 0, W, H);

    // velvet folds
    const cx = W / 2, cy = H / 2;
    const diag = Math.hypot(W, H);
    c.globalCompositeOperation = 'lighter';
    for (let i = 0; i < 9; i++) {
      const ang = -0.5 + rng() * 1.0 + (i % 2 ? 0.45 : -0.4);
      const pos = (i / 8 - 0.5) * diag * 1.1;
      const strength = 0.05 + rng() * 0.13;
      const spread = diag * (0.22 + rng() * 0.32);
      const col = rng() < 0.55 ? [104, 44, 68] : [62, 42, 104];
      const nx = -Math.sin(ang), ny = Math.cos(ang);
      const gx = cx + Math.cos(ang) * pos, gy = cy + Math.sin(ang) * pos;
      const grad = c.createLinearGradient(gx - nx * spread, gy - ny * spread, gx + nx * spread, gy + ny * spread);
      grad.addColorStop(0, rgba(col, 0));
      grad.addColorStop(0.5, rgba(col, strength));
      grad.addColorStop(1, rgba(col, 0));
      c.save();
      c.translate(gx, gy);
      c.rotate(ang);
      c.fillStyle = grad;
      c.fillRect(-diag, -spread, diag * 2, spread * 2);
      c.restore();
    }
    c.globalCompositeOperation = 'source-over';

    const pool = c.createRadialGradient(cx, cy * 0.94, 0, cx, cy * 0.94, diag * 0.5);
    pool.addColorStop(0, 'rgba(74,30,48,0.55)');
    pool.addColorStop(0.5, 'rgba(42,17,34,0.26)');
    pool.addColorStop(1, 'rgba(0,0,0,0)');
    c.fillStyle = pool;
    c.fillRect(0, 0, W, H);

    const vig = c.createRadialGradient(cx, cy, Math.min(W, H) * 0.20, cx, cy, diag * 0.60);
    vig.addColorStop(0, 'rgba(0,0,0,0)');
    vig.addColorStop(0.62, 'rgba(0,0,0,0.34)');
    vig.addColorStop(1, 'rgba(0,0,0,0.88)');
    c.fillStyle = vig;
    c.fillRect(0, 0, W, H);

    // velvet grain
    const n = 128;
    const tile = makeCanvas(n, n);
    const tctx = tile.getContext('2d');
    const im = tctx.createImageData(n, n);
    for (let i = 0; i < n * n; i++) {
      const v = 6 + rng() * 20;
      im.data[i * 4] = v;
      im.data[i * 4 + 1] = v * 0.94;
      im.data[i * 4 + 2] = v * 1.12;
      im.data[i * 4 + 3] = 255;
    }
    tctx.putImageData(im, 0, 0);
    c.globalAlpha = this.opts.grain;
    c.globalCompositeOperation = 'lighter';
    c.fillStyle = c.createPattern(tile, 'repeat');
    c.fillRect(0, 0, W, H);
    c.globalAlpha = 1;
    c.globalCompositeOperation = 'source-over';
  }

  /* ---------------- text ---------------- */

  _font(size) {
    const o = this.opts;
    return `${o.fontStyle} ${o.fontWeight} ${size}px ${o.fontFamily}`;
  }

  _measureTracked(c, text, tracking) {
    let w = 0;
    for (const ch of text) {
      w += c.measureText(ch).width + tracking;
    }
    return Math.max(0, w - tracking);
  }

  _drawTracked(c, text, x, y, tracking) {
    let px = x;
    for (const ch of text) {
      c.fillText(ch, px, y);
      px += c.measureText(ch).width + tracking;
    }
  }

  /**
   * Rasterise the name, then turn glyph coverage into an evenly scattered list
   * of stardust targets ordered left to right (so the dust can *write* it).
   */
  _sampleText() {
    const o = this.opts;
    const targetW = this.W * o.textWidthRatio;
    const probe = makeCanvas(8, 8).getContext('2d');

    let size = 200;
    for (let i = 0; i < 8; i++) {
      probe.font = this._font(size);
      const w = this._measureTracked(probe, o.text, size * o.tracking);
      if (!isFinite(w) || w <= 1) break;
      size *= targetW / w;
    }
    size = clamp(size, 10, 4000);
    this.textSize = size;
    const tracking = size * o.tracking;

    const pad = Math.ceil(size * 0.5);
    const maskW = Math.ceil(targetW + pad * 2);
    const maskH = Math.ceil(size * 2.1);
    const mask = makeCanvas(maskW, maskH);
    const mctx = mask.getContext('2d');
    mctx.font = this._font(size);
    mctx.textAlign = 'left';
    mctx.textBaseline = 'alphabetic';
    mctx.fillStyle = '#fff';
    const textW = this._measureTracked(mctx, o.text, tracking);
    const baseline = maskH * 0.70;
    this._drawTracked(mctx, o.text, (maskW - textW) / 2, baseline, tracking);

    this.textBox = {
      x: (this.W - maskW) / 2,
      y: this.H / 2 - baseline - size * 0.16,
      w: maskW,
      h: maskH,
    };

    const data = mctx.getImageData(0, 0, maskW, maskH).data;
    const step = clamp(Math.round(size / 46), 2, 8);
    const raw = [];
    for (let y = 0; y < maskH; y += step) {
      for (let x = 0; x < maskW; x += step) {
        if (data[(y * maskW + x) * 4 + 3] > 108) raw.push(x, y);
      }
    }
    const n = raw.length / 2;

    // rank order left -> right, a touch of noise so it is not a hard edge
    const rng = makeRng(o.seed + 31);
    const idx = new Array(n);
    for (let i = 0; i < n; i++) idx[i] = i;
    idx.sort((p, q) => (raw[p * 2] + (rng() - 0.5) * step * 3) - (raw[q * 2] + (rng() - 0.5) * step * 3));

    const xs = new Float32Array(n), ys = new Float32Array(n);
    for (let i = 0; i < n; i++) {
      xs[i] = raw[idx[i] * 2];
      ys[i] = raw[idx[i] * 2 + 1];
    }
    this.textTargets = { xs, ys, count: n, step };
    if (n < 4) this.textTargets = this._fallbackTargets(size);
  }

  _fallbackTargets(size) {
    const m = 1200;
    const xs = new Float32Array(m), ys = new Float32Array(m);
    const cols = 40, rows = m / cols;
    for (let i = 0; i < m; i++) {
      xs[i] = ((i % cols) / (cols - 1)) * this.W * this.opts.textWidthRatio;
      ys[i] = (Math.floor(i / cols) / (rows - 1)) * size * 1.2;
    }
    return { xs, ys, count: m, step: 1 };
  }

  /* ---------------- geometry ---------------- */

  _project(wx, wy, wz, out) {
    const k = this.cam / (this.cam - wz);
    out[0] = this.cx + wx * this.scale * k;
    out[1] = this.cy + wy * this.scale * k;
    out[2] = wz;
    out[3] = k;
    return out;
  }

  /** ribbon point clouds for a given time */
  _ribbons(t) {
    const N = this.opts.segments;
    const k0 = heartAmount(t);
    const rA = ribbonAlpha(t);
    const res = [];

    for (let r = 0; r < 2; r++) {
      const pts = new Array(N + 1);
      const fw = [0, 0, 0], hw = [0, 0, 0], pr = [0, 0, 0, 0];
      for (let i = 0; i <= N; i++) {
        const u = i / N;
        freePoint(r, u, t, fw);
        let wx = fw[0], wy = fw[1], wz = fw[2];
        if (k0 > 0.0005) {
          const delay = 0.40 * Math.abs(2 * u - 1);
          const ki = easeInOut(clamp((k0 - delay) / (1 - delay), 0, 1));
          heartPoint(r, u, t, ki, this.heartScale, hw);
          wx = lerp(wx, hw[0], ki);
          wy = lerp(wy, hw[1], ki);
          wz = lerp(wz, hw[2], ki);
          const sw = Math.sin(Math.PI * ki) * 0.09 * k0 * (r === 0 ? 1 : -1);
          const dx = wx, dy = wy;
          wx += -dy * sw;
          wy += dx * sw * 0.5;
        }
        this._project(wx, wy, wz, pr);
        const taper = Math.pow(Math.sin(Math.PI * Math.pow(clamp(u, 0.0001, 0.9999), 0.92)), 0.55);
        pts[i] = {
          x: pr[0], y: pr[1], z: pr[2], k: pr[3], u,
          halfW: (0.060 + r * 0.004) * taper * this.scale * pr[3] * rA,
        };
      }
      res.push(pts);
    }
    return res;
  }

  /** depth sorted segments, with all the shading data the passes need */
  _segments(ribbons, t) {
    const segs = [];
    for (let r = 0; r < 2; r++) {
      const pts = ribbons[r];
      for (let i = 0; i < pts.length - 1; i++) {
        const a = pts[i], b = pts[i + 1];
        const dx = b.x - a.x, dy = b.y - a.y;
        const len = Math.hypot(dx, dy);
        if (len < 1e-4) continue;
        const u = (a.u + b.u) * 0.5;

        // the ribbon's twist: it turns as it braids, so the highlight sweeps
        const twist =
          (r === 0 ? 1 : -1) * TAU * 1.02 * u + 0.52 * t +
          (r === 0 ? 0 : Math.PI) + 0.5 * fbm3(u * 1.1, t * 0.09, r * 0.62, 27);
        const faceW = 0.34 + 0.66 * Math.abs(Math.cos(twist));

        const tip = smoothstep(0, 0.10, u) * (1 - smoothstep(0.90, 1, u));
        const bright =
          (0.62 + 0.38 * noise3(u * 3.4, t * 0.25, r * 2.7, 91)) *
          (0.35 + 0.65 * tip);

        segs.push({
          r, a, b, len,
          tx: dx / len, ty: dy / len,
          z: (a.z + b.z) * 0.5,
          halfW: ((a.halfW + b.halfW) * 0.5) * faceW,
          phase: fract(twist / TAU) * 0.995,
          bright,
        });
      }
    }
    segs.sort((p, q) => p.z - q.z);
    return segs;
  }

  /* ---------------- stardust ---------------- */

  /**
   * Every particle is born on the heart outline and flies along a bezier arc to
   * a point of the name. Particles are handed out in rank order, so the name is
   * written left to right by a travelling cloud of dust.
   */
  _particles(t) {
    const o = this.opts;
    const st = TIMELINE;
    const targets = this.textTargets;
    const count = o.particleCount;
    const rng = makeRng(o.seed + 101);

    // the ribbon at the moment it dissolves = the seed cloud
    const seedT = clamp(st.heartRelease + 0.5, 0, st.dustStart);
    const seed = this._ribbons(seedT);
    const list = new Array(count);

    for (let i = 0; i < count; i++) {
      const r = rng() < 0.5 ? 0 : 1;
      const pts = seed[r];
      const f = rng() * (pts.length - 1);
      const i0 = Math.floor(f), fr = f - i0;
      const p0 = pts[i0], p1 = pts[Math.min(pts.length - 1, i0 + 1)];
      const sx = lerp(p0.x, p1.x, fr) + (rng() - 0.5) * 9;
      const sy = lerp(p0.y, p1.y, fr) + (rng() - 0.5) * 9;

      const ti = Math.min(targets.count - 1, Math.floor((i / count) * targets.count));
      const tx = this.textBox.x + targets.xs[ti];
      const ty = this.textBox.y + targets.ys[ti];

      // graceful arc through the frame
      const mx = (sx + tx) / 2, my = (sy + ty) / 2;
      let px = -(ty - sy), py = tx - sx;
      const pl = Math.hypot(px, py) || 1;
      const bow = (rng() * 2 - 1) * (0.06 + rng() * 0.24) * Math.min(this.W, this.H);
      const ctrlX = mx + (px / pl) * bow;
      const ctrlY = my + (py / pl) * bow - this.H * (0.02 + rng() * 0.06);

      const rank = i / count;
      const delay = st.dustStart - 0.15 + Math.pow(rank, 0.85) * 2.5 + rng() * 0.55;
      const white = rng() < 0.16;

      list[i] = {
        r, sx, sy, tx, ty, cx: ctrlX, cy: ctrlY,
        delay,
        dur: 1.5 + rng() * 1.5,
        size: 1.1 + rng() * 2.0 + (white ? 1.0 : 0),
        tw: rng() * TAU,
        twS: 0.5 + rng() * 1.6,
        g: clamp((tx - this.textBox.x) / this.textBox.w, 0, 1),
        white,
        jx: (rng() - 0.5) * 1.2,
        jy: (rng() - 0.5) * 1.2,
        ph: rng() * 100,
        alpha: 0.55 + rng() * 0.45,
      };
    }
    return list;
  }

  _particleState(p, t) {
    const k = clamp((t - p.delay) / p.dur, 0, 1);
    const e = easeInOut(k);
    const mt = 1 - e;
    let x = mt * mt * p.sx + 2 * mt * e * p.cx + e * e * p.tx;
    let y = mt * mt * p.sy + 2 * mt * e * p.cy + e * e * p.ty;
    // a whisper of idle drift while it waits / once it has arrived
    const idle = (1 - k) * 4 + 1.1;
    x += Math.sin(0.55 * t + p.ph) * idle + p.jx * k;
    y += Math.cos(0.47 * t + p.ph * 1.3) * idle * 0.8 + p.jy * k;
    return { x, y, k };
  }

  /* ---------------- segment drawing ---------------- */

  /**
   * Draw a segment list as texture mapped quads. The cross-section texture is
   * sampled per segment at its twist phase, so highlights travel along the silk.
   */
  _drawSegments(ctx, segs, textures, opt = {}) {
    const alpha = opt.alpha === undefined ? 1 : opt.alpha;
    if (alpha <= 0.003) return;
    const widthScale = opt.widthScale || 1;
    const minHalf = opt.minHalf || 1.2;
    const prevOp = ctx.globalCompositeOperation;
    const prevA = ctx.globalAlpha;
    ctx.globalCompositeOperation = opt.composite || 'lighter';

    for (let s = 0; s < segs.length; s++) {
      const sg = segs[s];
      const hw = sg.halfW * widthScale;
      if (hw < minHalf) continue;
      const tex = textures[sg.r];
      const tW = tex.width, tH = tex.height;
      const a = sg.a, b = sg.b;
      const ex = sg.tx * 0.6, ey = sg.ty * 0.6;       // hide segment seams
      const x0 = a.x - ex, y0 = a.y - ey;
      const x1 = b.x + ex, y1 = b.y + ey;
      const nx = -sg.ty, ny = sg.tx;
      const sy = sg.phase * (tH - 2);

      ctx.globalAlpha = clamp(alpha * sg.bright, 0, 1);
      ctx.setTransform(
        (nx * 2 * hw) / tW, (ny * 2 * hw) / tW,
        x1 - x0, y1 - y0,
        x0 - nx * hw, y0 - ny * hw
      );
      ctx.drawImage(tex, 0, sy, tW, 2, 0, 0, tW, 1);
    }
    ctx.setTransform(1, 0, 0, 1, 0, 0);
    ctx.globalAlpha = prevA;
    ctx.globalCompositeOperation = prevOp;
  }

  /** silhouettes of the ribbons that are in front of the others */
  _drawOccluders(ctx, segs) {
    ctx.globalCompositeOperation = 'source-over';
    ctx.fillStyle = '#000';
    for (let s = 0; s < segs.length; s++) {
      const sg = segs[s];
      const amt = smoothstep(0.03, 0.26, sg.z);
      if (amt <= 0.01) continue;
      const hw = sg.halfW * 0.80;
      if (hw < 1.2) continue;
      const nx = -sg.ty, ny = sg.tx;
      const a = sg.a, b = sg.b;
      const x0 = a.x - sg.tx * 0.6, y0 = a.y - sg.ty * 0.6;
      const x1 = b.x + sg.tx * 0.6, y1 = b.y + sg.ty * 0.6;
      ctx.globalAlpha = amt * 0.9;
      ctx.beginPath();
      ctx.moveTo(x0 - nx * hw, y0 - ny * hw);
      ctx.lineTo(x1 - nx * hw, y1 - ny * hw);
      ctx.lineTo(x1 + nx * hw, y1 + ny * hw);
      ctx.lineTo(x0 + nx * hw, y0 + ny * hw);
      ctx.closePath();
      ctx.fill();
    }
    ctx.globalAlpha = 1;
  }

  /* ---------------- particles / embers ---------------- */

  _drawDust(ctx, list, t, amount, scale = 1, alphaScale = 1) {
    const st = TIMELINE;
    const tex = this.textures.dust;
    const globalOut = 1 - smoothstep(st.textHold + 0.2, st.fadeOut + 0.6, t);
    const prevOp = ctx.globalCompositeOperation;
    ctx.globalCompositeOperation = 'lighter';
    for (let i = 0; i < list.length; i++) {
      const p = list[i];
      const rise = smoothstep(p.delay - 0.55, p.delay + 0.05, t);
      if (rise <= 0.01) continue;
      const { x, y, k } = this._particleState(p, t);
      const twinkle = 0.62 + 0.38 * Math.sin(t * p.twS * 2 + p.tw);
      const a = amount * globalOut * rise * p.alpha * twinkle * (0.55 + 0.45 * k) * alphaScale;
      if (a <= 0.01) continue;
      const s = p.size * scale * (1 + 0.25 * (1 - k));
      ctx.globalAlpha = clamp(a, 0, 1);
      const sprite = p.white ? tex[2] : tex[p.r];
      ctx.drawImage(sprite, x - s, y - s, s * 2, s * 2);
    }
    ctx.globalAlpha = 1;
    ctx.globalCompositeOperation = prevOp;
  }

  _drawEmbers(ctx, t, alpha, scale = 1, sizeMul = 1) {
    const n = this.opts.emberCount;
    const rng = makeRng(this.opts.seed + 3);
    const prevOp = ctx.globalCompositeOperation;
    ctx.globalCompositeOperation = 'lighter';
    for (let i = 0; i < n; i++) {
      const x0 = rng() * this.W;
      const y0 = rng() * this.H;
      const spd = 6 + rng() * 26;
      const ph = rng() * TAU;
      const size = (1.2 + rng() * 2.6) * sizeMul;
      const col = rng();
      let y = (y0 - t * spd) % this.H;
      if (y < 0) y += this.H;
      const x = x0 + Math.sin(t * 0.13 + ph) * this.W * 0.03;
      const edge = smoothstep(0, 0.10, y / this.H) * (1 - smoothstep(0.90, 1, y / this.H));
      const tw = 0.45 + 0.55 * (0.5 + 0.5 * Math.sin(t * (0.5 + rng()) + ph));
      const a = alpha * edge * tw * 0.5;
      if (a <= 0.01) continue;
      ctx.globalAlpha = clamp(a, 0, 1);
      const sprite = this.textures.dust[col < 0.5 ? 0 : 1];
      const s = size * scale;
      ctx.drawImage(sprite, x - s, y - s, s * 2, s * 2);
    }
    ctx.globalAlpha = 1;
    ctx.globalCompositeOperation = prevOp;
  }

  /* ---------------- main render ---------------- */

  render(t) {
    const ctx = this.ctx;
    const L = this.layers;
    const st = TIMELINE;
    const o = this.opts;
    this.time = t;

    const fadeIn = smoothstep(0, st.fadeIn, t);
    const fadeOut = 1 - smoothstep(st.fadeOut, st.end, t);
    const fade = clamp(fadeIn * fadeOut, 0, 1);

    const rA = ribbonAlpha(t);
    const heartA = heartAmount(t);
    const dustA = dustAmount(t);
    const tA = smoothstep(st.dustStart + 0.1, st.textFormed, t) * (1 - smoothstep(st.textHold + 0.1, st.fadeOut + 0.5, t));
    const beat = heartbeat(t);

    /* 1 — velvet backdrop ---------------------------------------------- */
    ctx.setTransform(1, 0, 0, 1, 0, 0);
    ctx.globalCompositeOperation = 'source-over';
    ctx.globalAlpha = 1;
    ctx.drawImage(L.bg.canvas, 0, 0, this.W, this.H);

    /* 2 — the pool of light reacting to the heart / the name ----------- */
    const auraAmt = (0.30 + 0.70 * heartA) * fade * (0.55 + 0.45 * rA) + tA * 0.5;
    if (auraAmt > 0.01) {
      const baseR = Math.min(this.W, this.H) * (0.52 - 0.06 * heartA);
      const r = baseR * (1 + 0.025 * beat) * (1 + 0.02 * Math.sin(t * 0.4));
      const grad = ctx.createRadialGradient(this.W * 0.5, this.H * 0.5, 0, this.W * 0.5, this.H * 0.5, r * 1.7);
      grad.addColorStop(0, `rgba(255,152,140,${(0.05 * auraAmt * (1 + 0.3 * beat)).toFixed(4)})`);
      grad.addColorStop(0.40, `rgba(168,116,208,${(0.042 * auraAmt).toFixed(4)})`);
      grad.addColorStop(1, 'rgba(0,0,0,0)');
      ctx.globalCompositeOperation = 'lighter';
      ctx.fillStyle = grad;
      ctx.fillRect(0, 0, this.W, this.H);
    }

    /* 3 — glow layer --------------------------------------------------- */
    const { glow, occ, halo } = L;
    const g = glow.ctx;
    g.setTransform(1, 0, 0, 1, 0, 0);
    g.globalCompositeOperation = 'source-over';
    g.globalAlpha = 1;
    g.clearRect(0, 0, glow.w, glow.h);
    g.setTransform(glow.w / this.W, 0, 0, glow.w / this.W, 0, 0);

    const smearDt = 0.030;
    const smearSteps = o.smear ? 2 : 0;
    const segCache = [];
    for (let s = smearSteps; s >= 0; s--) {
      const tt = t - s * smearDt;
      if (tt < 0) continue;
      const ribbons = s === 0 ? this._ribbons(t) : this._ribbons(tt);
      const segs = this._segments(ribbons, tt);
      segCache.push({ s, segs });
      const a = (s === 0 ? 0.95 : 0.34 / s) * rA * fade;
      this._drawSegments(g, segs, this.textures.glow, {
        alpha: a, widthScale: s === 0 ? 3.4 : 4.6, composite: 'lighter', minHalf: 0.8,
      });
    }
    const segsNow = segCache[segCache.length - 1].segs;

    if (rA > 0.01) this._drawEmbers(g, t, 0.5 * fade, 3.0);
    if (dustA > 0.01) {
      const list = this._particles(t);
      this._drawDust(g, list, t, dustA * fade, 1.6, 0.55);
      this._drawDust(ctx, list, t, dustA * fade, 1.0, 1.0);   // crisp cores
    }

    /* 4 — ribbons in front punch a hole in the glow -------------------- */
    if (o.occluder && rA > 0.02) {
      const oc = occ.ctx;
      oc.setTransform(1, 0, 0, 1, 0, 0);
      oc.clearRect(0, 0, occ.w, occ.h);
      oc.setTransform(occ.w / this.W, 0, 0, occ.w / this.W, 0, 0);
      oc.globalAlpha = 1;
      this._drawOccluders(oc, segsNow);
      g.globalCompositeOperation = 'destination-out';
      g.setTransform(1, 0, 0, 1, 0, 0);
      g.globalAlpha = clamp(rA * 1.4, 0, 1);
      g.drawImage(occ.canvas, 0, 0);
      g.globalAlpha = 1;
    }

    /* 5 — bloom it back into the frame --------------------------------- */
    g.setTransform(1, 0, 0, 1, 0, 0);
    const hc = halo.ctx;
    hc.setTransform(1, 0, 0, 1, 0, 0);
    hc.globalCompositeOperation = 'source-over';
    hc.clearRect(0, 0, halo.w, halo.h);
    hc.drawImage(glow.canvas, 0, 0, halo.w, halo.h);

    ctx.setTransform(1, 0, 0, 1, 0, 0);
    ctx.globalCompositeOperation = 'lighter';
    const squish = 0.0025 * this.W;
    ctx.globalAlpha = 0.62;
    ctx.drawImage(halo.canvas, -squish, 0, this.W + squish * 2, this.H);
    ctx.drawImage(halo.canvas, squish * 0.5, -this.H * 0.002, this.W, this.H * 1.004);
    ctx.globalAlpha = 0.80;
    ctx.drawImage(glow.canvas, 0, 0, this.W, this.H);
    ctx.globalAlpha = 1;

    /* 6 — crisp glass bodies, depth sorted so they weave ---------------- */
    if (rA > 0.02) {
      const cut = 0.004 + 0.5 * (1 - rA);
      this._drawSegments(ctx, segsNow, this.textures.ribbon, {
        alpha: (1 - cut) * 0.92 * fade, composite: 'source-over', widthScale: 1, minHalf: 1.2,
      });
      this._drawSegments(ctx, segsNow, this.textures.ribbon, {
        alpha: 0.42 * fade, composite: 'lighter', widthScale: 0.92, minHalf: 1.2,
      });
    }

    if (rA > 0.01) this._drawEmbers(ctx, t, 0.65 * fade, 1.0, 1.0);

    /* 7 — fade in / out ------------------------------------------------- */
    const black = 1 - fade;
    if (black > 0.001) {
      ctx.globalCompositeOperation = 'source-over';
      ctx.globalAlpha = clamp(black, 0, 1);
      ctx.fillStyle = '#000';
      ctx.fillRect(0, 0, this.W, this.H);
      ctx.globalAlpha = 1;
    }
  }
}
