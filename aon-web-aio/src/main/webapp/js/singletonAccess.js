// singletonAccess.js
// cargar $module.js que lo implemente como ESM (module) en tu HTML.
// Control de pestaña única mediante BroadcastChannel con overlay, icono "warning",
// botón "Cerrar pestaña" (tries best-effort) y botón "Usar aquí" (toma liderazgo).

export function initSingletonAccess(userOptions = {}) {
  if (typeof window === 'undefined') return;
  if (window.__KMS_SINGLETON_INITED__) return; // evitar doble init al importar varias veces
  window.__KMS_SINGLETON_INITED__ = true;

  const options = {
    channelName: 'kms-single-tab',
    leaderElectDelay: 300,
    // Textos
    title: 'Sesión ya activa',
    msg1: 'Esta aplicación ya está abierta en otra pestaña de este navegador.',
    msg2: 'Puedes <b>cerrar esta pestaña</b> o <b>usar aquí</b> cerrando la pestaña activa.',
    btnClose: 'Cerrar pestaña',
    btnUseHere: 'Usar aquí',
    ...userOptions,
  };

  if (!('BroadcastChannel' in window)) return;

  const ch = new BroadcastChannel(options.channelName);
  const myId = `${Date.now()}-${Math.random().toString(36).slice(2)}`;
  let isLeader = false;
  let overlayShown = false;

  // ===== utilidades =====
  function ensureMaterialSymbols() {
    if (!document.querySelector('link[href*="Material+Symbols+Outlined"]')) {
      const link = document.createElement('link');
      link.rel = 'stylesheet';
      link.href = 'https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined';
      document.head.appendChild(link);
    }
  }

  function injectBaseStyles() {
    if (document.getElementById('kms-singleton-styles')) return;
    const style = document.createElement('style');
    style.id = 'kms-singleton-styles';
    style.textContent = `
      .kms-singleton-overlay{position:fixed;inset:0;display:flex;align-items:center;justify-content:center;background:rgba(0,0,0,.6);z-index:2147483647}
      .kms-singleton-card{background:#fff;max-width:620px;margin:16px;padding:24px;border-radius:16px;box-shadow:0 10px 40px rgba(0,0,0,.2);font:16px/1.45 system-ui,-apple-system,Segoe UI,Roboto,Ubuntu,Cantarell}
      .kms-singleton-card h1{margin:0 0 8px;font-size:20px;display:flex;align-items:center;gap:6px}
      .kms-singleton-card p{margin:0 0 12px}
      .kms-singleton-actions{display:flex;gap:8px;justify-content:flex-end;margin-top:8px;flex-wrap:wrap}
      .kms-btn{border:2px solid transparent;border-radius:10px;padding:10px 14px;cursor:pointer;background:#fff}
      .kms-btn-red{border-color:#d32f2f;color:#d32f2f}.kms-btn-red:hover{background:#ffebee}
      .kms-btn-green{border-color:#2e7d32;color:#2e7d32}.kms-btn-green:hover{background:#e8f5e9}
      .material-symbols-outlined{font-variation-settings:'FILL' 0,'wght' 400,'GRAD' 0,'opsz' 24}
      .kms-ico{font-size:24px;line-height:1;vertical-align:middle}
      .kms-ico-warning{color:#f57c00}
    `;
    document.head.appendChild(style);
  }

  // Intenta cerrar esta pestaña; si no puede, la inutiliza.
  function tryForceCloseWindow() {
    window.close();                         // 1) cierre estándar (solo si se abrió por script)
    window.open('', '_self'); window.close(); // 2) reemplazo del contexto y cierre
    setTimeout(() => {
      // 3) último recurso: dejar la pestaña inutilizada
      document.documentElement.innerHTML = '';
      location.replace('about:blank');
    }, 50);
  }

  // Overlay para pestaña secundaria (no líder)
  function showSecondaryOverlay() {
    if (overlayShown) return;
    overlayShown = true;
    ensureMaterialSymbols();
    injectBaseStyles();

    const overlay = document.createElement('div');
    overlay.className = 'kms-singleton-overlay';
    overlay.innerHTML = `
      <div class="kms-singleton-card" role="dialog" aria-live="assertive" aria-modal="true">
        <h1>
          <span class="material-symbols-outlined kms-ico kms-ico-warning">warning</span>
          ${options.title}
        </h1>
        <p>${options.msg1}</p>
        <p>${options.msg2}</p>
        <div class="kms-singleton-actions">
          <button id="kmsSingletonClose" class="kms-btn kms-btn-red">${options.btnClose}</button>
          <button id="kmsUseHere" class="kms-btn kms-btn-green">${options.btnUseHere}</button>
        </div>
      </div>
    `;
    document.body.appendChild(overlay);

    document.getElementById('kmsSingletonClose')?.addEventListener('click', tryForceCloseWindow);

    document.getElementById('kmsUseHere')?.addEventListener('click', () => {
      // Reclama liderazgo: la otra pestaña debe auto-cerrarse/bloquearse
      ch.postMessage({ type: 'takeover-request', from: myId });
      isLeader = true; // optimista
      document.querySelector('.kms-singleton-overlay')?.remove();
      overlayShown = false;
    });
  }

  // Overlay para pestaña reemplazada (era líder y cede la sesión)
  function showReplacedOverlay() {
    if (overlayShown) return;
    overlayShown = true;
    injectBaseStyles();

    const overlay = document.createElement('div');
    overlay.className = 'kms-singleton-overlay';
    overlay.innerHTML = `
      <div class="kms-singleton-card" role="dialog" aria-live="assertive" aria-modal="true">
        <h1>Has sido reemplazado</h1>
        <p>La sesión continúa en otra pestaña. Esta pestaña se cerrará. Si el navegador lo impide, quedará bloqueada.</p>
        <div class="kms-singleton-actions">
          <button id="kmsReplacedClose" class="kms-btn kms-btn-red">Cerrar pestaña</button>
        </div>
      </div>
    `;
    document.body.appendChild(overlay);

    document.getElementById('kmsReplacedClose')?.addEventListener('click', tryForceCloseWindow);
    // Intento inmediato:
    tryForceCloseWindow();
  }

  // ===== Mensajería entre pestañas =====
  ch.onmessage = (e) => {
    const msg = e?.data;
    if (!msg || !msg.type) return;

    if (msg.type === 'ping') {
      if (isLeader) ch.postMessage({ type: 'pong', from: myId });
    }
    if (msg.type === 'pong') {
      // ya hay líder → bloqueo esta
      showSecondaryOverlay();
    }
    if (msg.type === 'become-leader') {
      if (!isLeader && msg.from !== myId) showSecondaryOverlay();
    }
    if (msg.type === 'takeover-request') {
      if (isLeader && msg.from !== myId) {
        isLeader = false;
        showReplacedOverlay();
        ch.postMessage({ type: 'handover-ack', from: myId });
      }
    }
  };

  // ===== Elección inicial de liderazgo =====
  ch.postMessage({ type: 'ping', from: myId });
  setTimeout(() => {
    if (!overlayShown) {
      isLeader = true;
      ch.postMessage({ type: 'become-leader', from: myId });
    }
  }, options.leaderElectDelay);
}

// Auto-init al importar (import side-effect)
if (typeof window !== 'undefined' && !window.__KMS_SINGLETON_INITED__) {
  initSingletonAccess();
}

export default initSingletonAccess;
