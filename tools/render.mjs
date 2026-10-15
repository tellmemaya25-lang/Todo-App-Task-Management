/**
 * Offline render harness — renders frames of the "Koki ni maya" scene with a
 * Node canvas implementation so the visuals can be iterated without a browser
 * (and so stills can be exported).
 *
 *   node tools/render.mjs --t 0,6,12,16,20,24 --w 1600 --h 900 --out /tmp/frames
 *
 * The canvas backend is looked up in this order:
 *   1. $HEART_CANVAS_MODULE
 *   2. @napi-rs/canvas resolved normally (if you npm i it here)
 *   3. /home/user/.tools/node_modules/@napi-rs/canvas  (sandbox scratch install)
 */

import { writeFileSync, mkdirSync, existsSync, readdirSync } from 'node:fs';
import { createRequire } from 'node:module';
import { dirname, join, resolve } from 'node:path';
import { fileURLToPath, pathToFileURL } from 'node:url';

const __dirname = dirname(fileURLToPath(import.meta.url));
const ROOT = resolve(__dirname, '..');
const require = createRequire(import.meta.url);

function loadCanvas() {
  const candidates = [];
  if (process.env.HEART_CANVAS_MODULE) candidates.push(process.env.HEART_CANVAS_MODULE);
  candidates.push('@napi-rs/canvas', '/home/user/.tools/node_modules/@napi-rs/canvas');
  for (const c of candidates) {
    try {
      return { mod: require(c), from: c };
    } catch (e) {
      /* keep looking */
    }
  }
  throw new Error('no canvas backend found; npm i @napi-rs/canvas or set HEART_CANVAS_MODULE');
}

const { mod: canvasMod, from } = loadCanvas();
const { createCanvas, GlobalFonts } = canvasMod;

const mod = await import(pathToFileURL(join(ROOT, 'src/heart-anim.js')).href);
const { createScene, setCanvasFactory, TIMELINE } = mod;

setCanvasFactory((w, h) => createCanvas(w, h));

/* fonts ------------------------------------------------------------- */
const fontDir = join(ROOT, 'src/fonts');
const FONT_FAMILY = 'Cormorant Garamond';
let fontLoaded = false;
if (existsSync(fontDir)) {
  const files = readdirSync(fontDir).filter((f) => f.endsWith('.woff2') || f.endsWith('.ttf'));
  // heaviest weight last so it wins for the family
  const order = ['400-normal', '400-italic', '500-italic', '600-italic'];
  files.sort((a, b) => order.findIndex((o) => a.includes(o)) - order.findIndex((o) => b.includes(o)));
  for (const f of files) {
    try {
      GlobalFonts.registerFromPath(join(fontDir, f), FONT_FAMILY);
      fontLoaded = true;
    } catch (e) {
      console.warn('font failed:', f, e.message);
    }
  }
}

/* args -------------------------------------------------------------- */
const args = process.argv.slice(2);
const opt = (name, dflt) => {
  const i = args.indexOf('--' + name);
  return i >= 0 && args[i + 1] ? args[i + 1] : dflt;
};
const flag = (name) => args.includes('--' + name);

const W = parseInt(opt('w', '1600'), 10);
const H = parseInt(opt('h', '900'), 10);
const DPR = parseInt(opt('dpr', '1'), 10);
const OUT = opt('out', '/tmp/heart');
const times = opt('t', '0,3,6,9,12,15,17,19,21,23,25')
  .split(',').map((s) => parseFloat(s.trim())).filter((v) => !isNaN(v));

mkdirSync(OUT, { recursive: true });

const canvas = createCanvas(W, H);
const scene = createScene(canvas, {
  width: W,
  height: H,
  dpr: DPR,
  fontFamily: `"${FONT_FAMILY}", "Times New Roman", Georgia, serif`,
  dpr2: undefined,
});

const t0 = Date.now();
const report = [];
for (const t of times) {
  const s = Date.now();
  scene.render(t);
  const buf = canvas.toBuffer('image/png');
  const name = `frame_${t.toFixed(2).replace('.', '_')}.png`;
  writeFileSync(join(OUT, name), buf);
  report.push({ t, file: join(OUT, name), ms: Date.now() - s, kb: Math.round(buf.length / 1024) });
}
const total = Date.now() - t0;

console.log(JSON.stringify({
  canvasBackend: from,
  fonts: fontLoaded ? FONT_FAMILY : 'MISSING (fallback serif used)',
  size: `${W}x${H}@${DPR}`,
  textSizePx: Math.round(scene.textSize),
  textTargets: scene.textTargets.count,
  timeline: TIMELINE,
  totalMs: total,
  frames: report,
}, null, 2));
