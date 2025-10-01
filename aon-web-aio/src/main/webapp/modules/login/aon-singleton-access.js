// Control de accesos duplicados (singleton) en el navegador
// cargar header.js como ESM (module) en tu HTML.
import { initSingletonAccess } from '../../js/singletonAccess.js';
import { MSG, CONSTANT, AON_ICONS, CSS, EVENT, MATERIAL_ICONS, TAG } from '../../environments/environments.js';

initSingletonAccess(); // opcional: puedes pasar textos/ajustes personalizados

// ... resto del archivo igual ...


export class AonSingletonAccess extends HTMLElement {
  constructor() {
    super();
    this.user = null;
    this.member = null;
    this.mode = 'application';
  }

  connectedCallback() {
    if (this._mounted) return;
    this._mounted = true;

    this.loadDataFromStorage();
    this.render();
    this.bindActions();
    this.injectFavicon();
    this.updateHomeButtonVisibility();

    if (this.mode === 'console') {
      this.populateMembers();
    }
  }

  updateHomeButtonVisibility() {
    const homeButton = this.querySelector('#btnHome');
    if (!homeButton) return;

    const isAdmin = this.user && (this.user.account_type === 'root' || this.user.member === null);
    
    if (isAdmin) {
      const isHomePage = window.location.pathname === '/' || window.location.pathname.endsWith('/index.html');
      homeButton.style.display = isHomePage ? 'none' : 'inline-flex';
    } else {
      homeButton.style.display = 'none';
    }
  }

  injectFavicon() {
    if (document.querySelector('link[rel="icon"]')) return;
    const logoEl = this.querySelector('.logo');
    if (!logoEl || !logoEl.src) return;
    
    try {
        const logoUrl = new URL(logoEl.src);
        const faviconPath = `${logoUrl.pathname.substring(0, logoUrl.pathname.lastIndexOf('/'))}/favicon.ico`;
        
        const link = document.createElement('link');
        link.rel = 'icon';
        link.type = 'image/x-icon';
        link.href = faviconPath;
        document.head.appendChild(link);
    } catch (e) {
        console.error("Error setting favicon path", e);
    }
  }

  loadDataFromStorage() {
    try {
      this.user = JSON.parse(localStorage.getItem('kms_user'));
      this.member = JSON.parse(localStorage.getItem('kms_member'));
      this.mode = localStorage.getItem('kms_mode') || 'application';

      if (this.user && !localStorage.getItem('kms_mode')) {
        if (this.user.member) {
          this.mode = 'application';
          localStorage.setItem('kms_mode', 'application');
        } else {
          this.mode = 'console';
          localStorage.setItem('kms_mode', 'console');
        }
      }
    } catch (e) {
      console.error("Failed to parse user/member data from localStorage", e);
      localStorage.clear();
      window.location.href = '/security/login/login.html';
    }
  }

  render() {
    const logoPath = this.getAttribute('logo') || '../../resources/KMS%20Logo.png';
    const isConsoleMode = this.mode === 'console';
    const centerHtml = isConsoleMode ? `<select id="memberSelector" class="member-selector"><option value="">Cargando miembros...</option></select>` : `<span class="header-title">Gestor del conocimiento</span>`;
    const rightText = isConsoleMode ? 'CONSOLA de Administración' : (this.member ? this.member.name : '');
    const rightHtml = `
      <span class="member-name">${rightText}</span>
      <button class="icon-btn" id="btnHome" title="Inicio"><span class="material-symbols-outlined">home</span></button>
      <button class="icon-btn" id="btnSettings" title="Ajustes"><span class="material-symbols-outlined">settings</span></button>
      <button class="icon-btn" id="btnLogout" title="Cerrar sesión"><span class="material-symbols-outlined">logout</span></button>`;
    this.innerHTML = `
      <header class="kms-header">
        <div class="left"><img class="logo" src="${logoPath}" alt="KMS"></div>
        <div class="center">${centerHtml}</div>
        <div class="right">${rightHtml}</div>
      </header>`;
  }

  bindActions() {
    this.querySelector('#btnHome')?.addEventListener('click', () => location.href = '/');
    this.querySelector('#btnLogout')?.addEventListener('click', () => {
        localStorage.clear();
        location.href = '/security/login/login.html';
    });
    this.querySelector('#btnSettings')?.addEventListener('click', () => showDevelopmentModal());
    
    // --- INICIO DE LA CORRECCIÓN (2. Deselección) ---
    this.querySelector('#memberSelector')?.addEventListener('change', (e) => {
        const memberId = e.target.value;
        if (memberId) {
            this.updateSessionMember(memberId);
        } else {
            // Si se selecciona la opción vacía, se limpia el storage y se recarga.
            localStorage.removeItem('kms_member');
            window.location.reload(); 
        }
    });
    // --- FIN DE LA CORRECCIÓN ---

    document.addEventListener('kms:member-change-request', (e) => {
      if (e.detail && e.detail.memberId) {
        this.updateSessionMember(e.detail.memberId);
      }
    });
  }

  async populateMembers() {
    const selector = this.querySelector('#memberSelector');
    if (!selector) return;
    try {
      const res = await fetch('/api/v1/members');
      if (!res.ok) throw new Error(`API Error: ${res.status}`);
      const membersData = await res.json();
      
      const members = membersData.data || [];

      selector.innerHTML = `<option value="">— Selecciona un miembro —</option>`;
      members.forEach(member => {
        const option = document.createElement('option');
        option.value = member.id;
        option.textContent = member.name;
        selector.appendChild(option);
      });

      // Esta lógica ahora funcionará porque this.member (cargado de localStorage)
      // tendrá la estructura correcta.
      if (this.member && this.member.id) {
        selector.value = this.member.id;
      }
    } catch (error) {
      console.error("Error populating members:", error);
      selector.innerHTML = `<option value="">Error al cargar</option>`;
    }
  }

  // --- INICIO DE LA CORRECCIÓN (1. Selección Persistente) ---
  async updateSessionMember(memberId) {
    try {
      const res = await fetch(`/api/v1/members/${memberId}`);
      if (!res.ok) throw new Error(`API Error: ${res.status}`);
      const memberResponse = await res.json();

      // Asumimos que la API puede devolver el objeto directamente o envuelto en 'data'.
      // Este código maneja ambos casos.
      const member = memberResponse.data || memberResponse;
      
      localStorage.setItem('kms_member', JSON.stringify(member));
      document.dispatchEvent(new CustomEvent('kms:member-changed', { detail: { member } }));
      window.location.reload();
    } catch (error) {
      console.error("Failed to update session member:", error);
      alert("No se pudo actualizar la sesión.");
    }
  }
  // --- FIN DE LA CORRECCIÓN ---
}

if(!window.customElements.get(TAG.AON_SINGLETON_ACCESS)){
	window.customElements.define(TAG.AON_SINGLETON_ACCESS, AonSingletonAccess);
}

(function ensureHeaderCss() {
  const href = '/components/header/header.css';
  if (!document.head.querySelector(`link[rel="stylesheet"][href*="${href}"]`)) {
    const link = document.createElement('link');
    link.rel = 'stylesheet';
    link.href = href;
    document.head.appendChild(link);
  }
})();
