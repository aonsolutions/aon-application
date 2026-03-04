import { AonElement } from "../../../../components/AonElement.js";

/**
 * COMPONENTE MENÚ: aon-calendar-menu
 *
 * Cambios respecto a la versión anterior:
 *   - Ya no necesita recibir callbacks directamente (setOnTodayClick).
 *     Ahora lanza CustomEvents que el padre (aon-calendar-container) escucha.
 *   - Las opciones "Hoy" y "Mes Actual" se muestran/ocultan
 *     según la vista activa, gracias al método público setActiveView().
 *   - El texto "Vista Anual" pasa a llamarse "Resumen Anual".
 *   - El texto "Vista Mensual" pasa a llamarse "Agenda".
 */
export class AonCalendarMenu extends AonElement {

  constructor() {
    super();
    this._isOpen       = false;
    this._activeView   = 'agenda'; // 'agenda' | 'annual'
    this._initialized  = false;
  }

  connectedCallback() {
    if (!this._initialized) {
      this._initialized = true;
      this.initializeComponent();
    }
  }

  initializeComponent() {
    this.innerHTML = `
      <div class="calendar-menu-container">

        <!-- Botón flotante principal (el icono de calendario) -->
        <button class="calendar-fab" aria-label="Menú de calendario">
          <svg class="calendar-icon" width="24" height="24" viewBox="0 0 24 24"
               fill="none" stroke="currentColor" stroke-width="2"
               stroke-linecap="round" stroke-linejoin="round">
            <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
            <line x1="16" y1="2" x2="16" y2="6"></line>
            <line x1="8"  y1="2" x2="8"  y2="6"></line>
            <line x1="3"  y1="10" x2="21" y2="10"></line>
          </svg>
        </button>

        <!-- Menú desplegable -->
        <div class="calendar-menu-popup">

          <!-- Solicitud (deshabilitado por ahora) -->
          <button class="calendar-menu-option disabled" data-action="request" disabled>
            <svg class="menu-icon" width="20" height="20" viewBox="0 0 24 24"
                 fill="none" stroke="currentColor" stroke-width="2"
                 stroke-linecap="round" stroke-linejoin="round">
              <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
              <polyline points="14 2 14 8 20 8"></polyline>
              <line x1="16" y1="13" x2="8" y2="13"></line>
              <line x1="16" y1="17" x2="8" y2="17"></line>
              <polyline points="10 9 9 9 8 9"></polyline>
            </svg>
            <span>Solicitud</span>
          </button>

          <!-- Resumen Anual -->
          <button class="calendar-menu-option" data-action="annual">
            <svg class="menu-icon" width="20" height="20" viewBox="0 0 24 24"
                 fill="none" stroke="currentColor" stroke-width="2"
                 stroke-linecap="round" stroke-linejoin="round">
              <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
              <line x1="16" y1="2" x2="16" y2="6"></line>
              <line x1="8"  y1="2" x2="8"  y2="6"></line>
              <line x1="3"  y1="10" x2="21" y2="10"></line>
              <rect x="7"  y="12" width="3" height="3"></rect>
              <rect x="14" y="12" width="3" height="3"></rect>
            </svg>
            <span>Resumen Anual</span>
          </button>

          <!-- Agenda (volver a la vista de días) -->
          <button class="calendar-menu-option" data-action="agenda">
            <svg class="menu-icon" width="20" height="20" viewBox="0 0 24 24"
                 fill="none" stroke="currentColor" stroke-width="2"
                 stroke-linecap="round" stroke-linejoin="round">
              <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
              <line x1="16" y1="2" x2="16" y2="6"></line>
              <line x1="8"  y1="2" x2="8"  y2="6"></line>
              <line x1="3"  y1="10" x2="21" y2="10"></line>
            </svg>
            <span>Agenda</span>
          </button>

          <!-- Hoy: solo visible cuando estamos en la agenda -->
          <button class="calendar-menu-option" data-action="today" data-view="agenda">
            <svg class="menu-icon" width="20" height="20" viewBox="0 0 24 24"
                 fill="none" stroke="currentColor" stroke-width="2"
                 stroke-linecap="round" stroke-linejoin="round">
              <circle cx="12" cy="12" r="10"></circle>
              <polyline points="12 6 12 12 16 14"></polyline>
            </svg>
            <span>Hoy</span>
          </button>

          <!-- Mes Actual: solo visible cuando estamos en el resumen anual -->
          <button class="calendar-menu-option" data-action="current-month" data-view="annual">
            <svg class="menu-icon" width="20" height="20" viewBox="0 0 24 24"
                 fill="none" stroke="currentColor" stroke-width="2"
                 stroke-linecap="round" stroke-linejoin="round">
              <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
              <line x1="16" y1="2" x2="16" y2="6"></line>
              <line x1="8"  y1="2" x2="8"  y2="6"></line>
              <line x1="3"  y1="10" x2="21" y2="10"></line>
              <circle cx="12" cy="15" r="2" fill="currentColor"></circle>
            </svg>
            <span>Mes Actual</span>
          </button>

        </div>

        <!-- Botón de cerrar (X) -->
        <button class="calendar-close-btn" aria-label="Cerrar menú">
          <svg width="24" height="24" viewBox="0 0 24 24"
               fill="none" stroke="currentColor" stroke-width="2"
               stroke-linecap="round" stroke-linejoin="round">
            <line x1="18" y1="6"  x2="6"  y2="18"></line>
            <line x1="6"  y1="6"  x2="18" y2="18"></line>
          </svg>
        </button>

      </div>
    `;

    this.fabButton  = this.querySelector('.calendar-fab');
    this.menuPopup  = this.querySelector('.calendar-menu-popup');
    this.closeButton = this.querySelector('.calendar-close-btn');

    this.setupEventListeners();

    // Aplicar el estado inicial (agenda activa por defecto)
    this.setActiveView(this._activeView);
  }

  setupEventListeners() {
    // Abrir/cerrar el menú al pulsar el botón flotante
    this.fabButton.addEventListener('click', e => {
      e.stopPropagation();
      this.toggleMenu();
    });

    // Cerrar con la X
    this.closeButton.addEventListener('click', e => {
      e.stopPropagation();
      this.closeMenu();
    });

    // Cerrar al hacer click fuera del menú
    document.addEventListener('click', e => {
      if (this._isOpen && !this.contains(e.target)) {
        this.closeMenu();
      }
    });

    // Manejar clicks en opciones del menú
    // Usamos delegación de eventos: un solo listener en el popup
    // que detecta en qué botón se hizo click gracias a data-action.
    this.menuPopup.addEventListener('click', e => {
      const btn = e.target.closest('[data-action]');
      if (!btn || btn.disabled) return;
      e.stopPropagation();
      this.handleMenuAction(btn.dataset.action);
    });
  }

  handleMenuAction(action) {
    this.closeMenu();

    /**
     * En lugar de llamar directamente a funciones de otros componentes,
     * lanzamos un CustomEvent que "sube" por el árbol del DOM.
     *
     * bubbles: true → el evento sube al padre, abuelo, etc.
     * composed: true → cruza los límites de Shadow DOM (por si acaso)
     *
     * El componente padre (aon-calendar-container) lo escucha y reacciona.
     */
    const eventMap = {
      'annual':        'calendar-show-annual',
      'agenda':        'calendar-show-agenda',
      'today':         'calendar-go-today',
      'current-month': 'calendar-go-current-month',
    };

    const eventName = eventMap[action];
    if (eventName) {
      this.dispatchEvent(new CustomEvent(eventName, { bubbles: true, composed: true }));
    }
  }

  /**
   * setActiveView(viewName)
   *
   * El padre llama a este método para decirle al menú
   * qué vista está activa. El menú usa esa info para
   * mostrar u ocultar las opciones contextuales.
   *
   * data-view="agenda"  → solo visible cuando la agenda está activa
   * data-view="annual"  → solo visible cuando el resumen anual está activo
   * Sin data-view        → siempre visible
   */
  setActiveView(viewName) {
    this._activeView = viewName;

    // Marcamos el botón activo visualmente (clase 'active')
    this.querySelectorAll('[data-action]').forEach(btn => {
      const btnView = btn.dataset.action; // 'agenda' o 'annual'

      // Resaltar el botón de la vista activa
      btn.classList.toggle('active', btnView === viewName);

      // Mostrar/ocultar opciones contextuales (data-view)
      const restrictedTo = btn.dataset.view; // 'agenda', 'annual' o undefined
      if (restrictedTo) {
        // Esta opción solo se muestra para una vista concreta
        btn.style.display = restrictedTo === viewName ? '' : 'none';
      }
    });
  }

  toggleMenu() { this._isOpen ? this.closeMenu() : this.openMenu(); }
  openMenu()   { this._isOpen = true;  this.classList.add('menu-open'); }
  closeMenu()  { this._isOpen = false; this.classList.remove('menu-open'); }

  // Mantenemos este método por compatibilidad con código antiguo
  setOnTodayClick(callback) {
    this._onTodayClick = callback;
  }
}

if (!customElements.get('aon-calendar-menu')) {
  window.customElements.define('aon-calendar-menu', AonCalendarMenu);
}