"""Post-process the icon font produced by scripts/build-icon-font.js.

Run automatically by that script after the static woff2 is built.
Requires python3 with fontTools and brotli (pip install fonttools brotli).

1. Fixes left side bearings: svg2ttf writes lsb=0 for every glyph, which
   makes renderers shift each icon left by its xMin, leaving a gap on the
   right. Each lsb is set to the glyph's xMin so icons render where their
   outlines say they are (centered in the em box).

2. Adds a variable 'wght' axis (100-700, default 400). The light/bold
   masters are synthesized by offsetting every outline point along its
   local outward normal, which thins/thickens the stroke while keeping
   point structure identical (a requirement for gvar interpolation).
   Offsets are calibrated to Material Symbols Outlined stroke widths
   (28/80/126 units @960upm for wght 100/400/700), scaled to 1000upm.
"""
import math
import sys
from fontTools.ttLib import TTFont, newTable
from fontTools.ttLib.tables._f_v_a_r import Axis
from fontTools.ttLib.tables.TupleVariation import TupleVariation

LIGHT_OFFSET = -27.1  # per-side stroke offset at wght 100
BOLD_OFFSET = 24.0    # per-side stroke offset at wght 700


def contour_points(glyph):
    """Split glyph coordinates into per-contour lists of (x, y)."""
    coords = list(glyph.coordinates)
    contours, start = [], 0
    for end in glyph.endPtsOfContours:
        contours.append(coords[start:end + 1])
        start = end + 1
    return contours


def shoelace(pts):
    a = 0.0
    for i, (x0, y0) in enumerate(pts):
        x1, y1 = pts[(i + 1) % len(pts)]
        a += x0 * y1 - x1 * y0
    return a / 2.0


def offset_contour(pts, d, outward_sign):
    """Offset each point along its averaged normal.

    outward_sign flips the normal so positive d always thickens ink,
    based on the dominant contour orientation of the glyph.
    """
    n = len(pts)
    out = []
    for i in range(n):
        px, py = pts[(i - 1) % n]
        qx, qy = pts[(i + 1) % n]
        tx, ty = qx - px, qy - py
        length = math.hypot(tx, ty)
        if length == 0:
            out.append(pts[i])
            continue
        tx, ty = tx / length, ty / length
        # left-of-travel normal; outward_sign corrects global convention
        nx, ny = -ty * outward_sign, tx * outward_sign
        x, y = pts[i]
        out.append((x + nx * d, y + ny * d))
    return out


def make_deltas(glyph, d):
    contours = contour_points(glyph)
    # dominant (largest |area|) contour is assumed to be an ink-outer
    # boundary; pick the sign that makes it expand for positive d
    dominant = max(contours, key=lambda c: abs(shoelace(c)))
    # for a CW contour (shoelace < 0, y-up), left-of-travel points outward
    outward_sign = 1 if shoelace(dominant) < 0 else -1
    deltas = []
    for c in contours:
        moved = offset_contour(c, d, outward_sign)
        deltas.extend(
            (round(mx - x), round(my - y))
            for (x, y), (mx, my) in zip(c, moved)
        )
    deltas.extend([(0, 0)] * 4)  # phantom points: metrics unchanged
    return deltas


def main(path):
    font = TTFont(path)
    if 'fvar' in font:
        print(f'{path} already has an fvar table, skipping')
        return

    glyf = font['glyf']
    hmtx = font['hmtx']

    fixed = 0
    for name in font.getGlyphOrder():
        glyph = glyf[name]
        if glyph.numberOfContours <= 0:
            continue
        # bboxes from a transformed-glyf woff2 are not loaded until save time
        glyph.recalcBounds(glyf)
        advance, lsb = hmtx[name]
        if lsb != glyph.xMin:
            hmtx[name] = (advance, glyph.xMin)
            fixed += 1
    print(f'Fixed left side bearing on {fixed} glyphs')

    fvar = newTable('fvar')
    axis = Axis()
    axis.axisTag = 'wght'
    axis.minValue, axis.defaultValue, axis.maxValue = 100.0, 400.0, 700.0
    axis.flags = 0
    axis.axisNameID = font['name'].addName('Weight', minNameID=255)
    fvar.axes = [axis]
    fvar.instances = []
    font['fvar'] = fvar

    gvar = newTable('gvar')
    gvar.version, gvar.reserved = 1, 0
    gvar.variations = {}
    count = 0
    for name in font.getGlyphOrder():
        glyph = glyf[name]
        if glyph.numberOfContours <= 0:  # empty or composite
            continue
        gvar.variations[name] = [
            TupleVariation({'wght': (-1.0, -1.0, 0.0)}, make_deltas(glyph, LIGHT_OFFSET)),
            TupleVariation({'wght': (0.0, 1.0, 1.0)}, make_deltas(glyph, BOLD_OFFSET)),
        ]
        count += 1
    font['gvar'] = gvar

    font.flavor = 'woff2'
    font.save(path)
    print(f'Added wght axis (100-700) to {path} ({count} glyphs)')


if __name__ == '__main__':
    if len(sys.argv) != 2:
        sys.exit(f'Usage: python3 {sys.argv[0]} <font.woff2>')
    main(sys.argv[1])
