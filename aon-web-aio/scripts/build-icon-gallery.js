/**
 * Builds a browsable gallery for the Aon Symbols Outlined icon font — a local
 * equivalent of fonts.google.com/icons.
 *
 * Usage: npm run build:icon-gallery
 *
 * Output: docs/aon-symbols.html — one self-contained file (the woff2 is inlined
 * as a data URI), so it works opened straight from disk, served from anywhere,
 * or copied into src/main/webapp/ to be reachable at /aon-symbols.html.
 *
 * The icon names and their codepoints are read from scripts/build-icon-font.js,
 * the single source of truth: codepoint = 0xE900 + position in ICONS. Re-run
 * this after every `npm run build:icons` so the gallery cannot drift.
 *
 * Flags:
 *   --fragment          emit a body fragment (no <!doctype>/<html>/<head>) —
 *                       what the Artifact publisher expects
 *   --out <path>        write somewhere other than the default
 */
const fs = require('fs');
const path = require('path');

const ROOT = path.join(__dirname, '..');
const WEBAPP = path.join(ROOT, 'src', 'main', 'webapp');
const FONT_FILE = path.join(WEBAPP, 'fonts', 'aon-symbols-outlined.woff2');
const FONT_SCRIPT = path.join(__dirname, 'build-icon-font.js');
const TEMPLATE = path.join(__dirname, 'icon-gallery.template.html');
const DEFAULT_OUT = path.join(ROOT, 'docs', 'aon-symbols.html');

// Same mapping build-icon-font.js uses for the aonSymbols.js constants.
function constName(icon) {
    return icon
        .replace(/([a-z0-9])([A-Z])/g, '$1_$2')
        .replace(/[-\s]/g, '_')
        .toUpperCase();
}

function readIcons() {
    const src = fs.readFileSync(FONT_SCRIPT, 'utf8');
    const block = /const ICONS = \[([\s\S]*?)\n\];/.exec(src);
    if (!block) {
        throw new Error(`Could not find the ICONS array in ${FONT_SCRIPT}`);
    }
    const names = block[1].match(/'([^']+)'/g).map(s => s.slice(1, -1));
    if (!names.length) {
        throw new Error('ICONS array parsed but empty');
    }
    return names;
}

function main() {
    const argv = process.argv.slice(2);
    const fragment = argv.includes('--fragment');
    const outIdx = argv.indexOf('--out');
    const out = outIdx > -1 ? path.resolve(argv[outIdx + 1]) : DEFAULT_OUT;

    const icons = readIcons();
    const font = fs.readFileSync(FONT_FILE);
    const template = fs.readFileSync(TEMPLATE, 'utf8');

    const missing = icons.filter(n => !fs.existsSync(path.join(WEBAPP, 'icons', `${n}.svg`)));
    if (missing.length) {
        console.warn(`Warning: ${missing.length} icon(s) have no SVG source: ${missing.join(', ')}`);
    }

    const data = JSON.stringify(icons.map(n => [n, constName(n)]));
    const body = template
        .replace('{{FONT_B64}}', font.toString('base64'))
        .replace('{{ICON_DATA}}', data)
        .replace(/\{\{COUNT\}\}/g, String(icons.length))
        .replace(/\{\{FONT_KB\}\}/g, String(Math.round(font.length / 1024)));

    const parts = body.split(/<!--@HEAD-->\n|<!--@BODY-->\n/);
    if (parts.length !== 3) {
        throw new Error('Template must contain exactly one <!--@HEAD--> and one <!--@BODY--> marker');
    }
    const head = parts[1];
    const markup = parts[2];

    const html = fragment
        ? head + markup
        : '<!doctype html>\n<html lang="en">\n<head>\n'
            + '<meta charset="utf-8">\n'
            + '<meta name="viewport" content="width=device-width, initial-scale=1">\n'
            + '<meta name="color-scheme" content="light dark">\n'
            + head
            + '</head>\n<body>\n'
            + markup
            + '</body>\n</html>\n';

    fs.mkdirSync(path.dirname(out), { recursive: true });
    fs.writeFileSync(out, html);
    console.log(
        `Wrote ${path.relative(process.cwd(), out)} `
        + `(${icons.length} icons, ${Math.round(html.length / 1024)} KB${fragment ? ', fragment' : ''})`
    );
}

main();
