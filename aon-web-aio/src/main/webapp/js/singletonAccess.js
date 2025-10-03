// singletonAccess.js
// cargar $module.js que lo implemente como ESM (module) en tu HTML.
// Control de pestaña única mediante BroadcastChannel con overlay, icono "warning",
// botón "Cerrar pestaña" (tries best-effort) y botón "Usar aquí" (toma liderazgo).


export function initSingletonAccess(userOptions = {}) {
  if (typeof window === 'undefined') return;
  if (window.__AON_SINGLETON_INITED__) return; // evitar doble init al importar varias veces
  window.__AON_SINGLETON_INITED__ = true;

  const options = {
    channelName: 'aon-single-tab',
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

    const overlay = document.createElement('div');
    overlay.className = 'aonDialog aonSingletonOverlay';
    overlay.innerHTML = `
      <div class="aonDialogContent aonSingletonCard" role="dialog" aria-live="assertive" aria-modal="true">
        <h1>
          <span class="material-symbols-outlined aonMaterialSymbolsOutlined aonSingletonIco aonSingletonIcoWarning">warning</span>
          ${options.title}
        </h1>
        <p>${options.msg1}</p>
        <p>${options.msg2}</p>
        <div class="aonSingletonActions">
          <button id="aonSingletonClose" class="aonButton aonSingletonBtnWarning">${options.btnClose}</button>
          <button id="aonUseHere" class="aonButton">${options.btnUseHere}</button>
        </div>
      </div>
    `;
    document.body.appendChild(overlay);

    document.getElementById('aonSingletonClose')?.addEventListener('click', tryForceCloseWindow);

    document.getElementById('aonUseHere')?.addEventListener('click', () => {
      // Reclama liderazgo: la otra pestaña debe auto-cerrarse/bloquearse
      ch.postMessage({ type: 'takeover-request', from: myId });
      isLeader = true; // optimista
      document.querySelector('.aonSingletonOverlay')?.remove();
      overlayShown = false;
    });
  }

  // Overlay para pestaña reemplazada (era líder y cede la sesión)
  function showReplacedOverlay() {
    if (overlayShown) return;
    overlayShown = true;

    const overlay = document.createElement('div');
    overlay.className = 'aonDialog aonSingletonOverlay';
    overlay.innerHTML = `
      <div class="aonDialogContent aonSingletonCard" role="dialog" aria-live="assertive" aria-modal="true">
        <h1>
		<span class="material-symbols-outlined aonMaterialSymbolsOutlined aonSingletonIco aonSingletonIcoWarning">warning</span>
		Has sido reemplazado
		</h1>
        <p>La sesión continúa en otra pestaña.</p>
        <div class="aonSingletonActions">
          <button id="aonReplacedClose" class="aonButton aonSingletonBtnWarning">${options.btnClose}</button>
		  <button id="aonUseHere" class="aonButton">${options.btnUseHere}</button>
        </div>
      </div>
    `;
    document.body.appendChild(overlay);

	document.getElementById('aonUseHere')?.addEventListener('click', () => {
	  // Reclama liderazgo: la otra pestaña debe auto-cerrarse/bloquearse
	  ch.postMessage({ type: 'takeover-request', from: myId });
	  isLeader = true; // optimista
	  favicon.href = window.__AON_SINGLETON_FAVICON__;
	  window.location?.reload();
	  document.querySelector('.aonSingletonOverlay')?.remove();
	  overlayShown = false;
	});

	document.getElementById('aonReplacedClose')?.addEventListener('click', tryForceCloseWindow);
	
	const favicon = document.querySelector("link[rel~='icon']");
	if ( favicon ) {
		window.__AON_SINGLETON_FAVICON__ = favicon.href;
		favicon.href = "assets/img/evaluation/regular.png";
	}
    
	// Intento inmediato:
    // tryForceCloseWindow();
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
if (typeof window !== 'undefined' && !window.__AON_SINGLETON_INITED__) {
  initSingletonAccess();
}

export default initSingletonAccess;
