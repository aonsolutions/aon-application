
export const API_URL = 'ms/api';
export const API_URL_BIDOQ = "https://mispapeles.es/api/v2/index.php";

// Pinned to a fixed pdf.js version served from a CDN that also hosts the WASM
// image decoders (jbig2/ccitt/openjpeg). The previous rolling gh-pages build
// (mozilla.github.io/pdf.js/legacy) upgraded to v6 -- which decodes CCITTFax /
// JBIG2 image masks via WASM -- but does not host the wasm/ directory, so those
// images (e.g. the black text on scanned invoices) silently failed to render.
export const PDFJS_URL = "https://cdn.jsdelivr.net/npm/pdfjs-dist@6.1.200/";
export const PDFJS_PDF_URL = `${PDFJS_URL}legacy/build/pdf.mjs`;
export const PDFJS_WORKER_URL = `${PDFJS_URL}legacy/build/pdf.worker.mjs`;
export const PDFJS_VIEWER_STYLESHEET_URL = `${PDFJS_URL}legacy/web/pdf_viewer.css`;
export const PDFJS_WASM_URL = `${PDFJS_URL}wasm/`;

//export const URL_PDF_VIEWER = "https://sig.aonsolutions.org/html/build/pdf.js";

//----PROD
export const SIG_URL = "https://aonsolutions.org";
export const SIG_DOMAIN_NAME = "sig.aonsolutions.org";
export const SIG_DOMAIN_ID = 5;
export const PRO_URL = "https://aon.solutions";
export const PRO_TEST_URL = "http://localhost:8080";

//FB
export const VAPIDKEY_FB = "BCH91WxACVIpylkYRMj3xSpIfrzjz7Ixnctcj25BPMBZKSbGeKjJFIdaRsJGQ3F-SXVFGD0cr4outWLCFwemIkE";
export const CONFIG_FB   = {
    apiKey: "AIzaSyDeUk7hHabedCzkA7qBoHRu43VC7N9CJOs",
    authDomain: "aon-solutions-d69a4.firebaseapp.com",
    databaseURL: "https://aon-solutions-d69a4.firebaseio.com",
    projectId: "aon-solutions-d69a4",
    storageBucket: "aon-solutions-d69a4.appspot.com",
    messagingSenderId: "292041697338",
    appId: "1:292041697338:web:81dbe6e044074cbc58ed03",
    measurementId: "G-7MQDKLET0Y",
};


import * as MSG from './msg.js';
import * as EVENT from './aonEvent.js';
import * as TAG from './aonTag.js';
import * as CSS from './css.js';
import * as COLORS from './colors.js';
import * as MATERIAL_ICONS from './materialIcons.js';
import * as AON_ICONS from './aonIcons.js';
import * as AON_SYMBOLS from './aonSymbols.js';
import * as CONSTANT from './constants.js';
import * as API from './aonApi.js';
import * as APPPARAMS from './appParams.js';

export {MSG, EVENT, TAG, CSS, COLORS, MATERIAL_ICONS, AON_ICONS, AON_SYMBOLS, CONSTANT, API, APPPARAMS};
