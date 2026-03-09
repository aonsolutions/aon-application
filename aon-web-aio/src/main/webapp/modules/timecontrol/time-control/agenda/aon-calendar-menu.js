import { AonElement } from "../../../../components/AonElement.js";

export class AonCalendarMenu extends AonElement {

	constructor() {
		super();
		this._isOpen = false;
		this._activeView = 'agenda'; // 'agenda' | 'annual'
		this._initialized = false;
		this._incidenciaOpen = false;
		this._incidenciaChildren = null;
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

        <!-- Botón flotante principal (FAB) -->
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

          <!-- ── INCIDENCIA (acordeón padre) ── -->
          <button class="calendar-menu-option incidencia-toggle" data-action="incidencia-toggle">
            <svg class="menu-icon" width="20" height="20" viewBox="0 0 24 24"
                 fill="none" stroke="currentColor" stroke-width="2"
                 stroke-linecap="round" stroke-linejoin="round">
              <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
              <polyline points="14 2 14 8 20 8"></polyline>
              <line x1="16" y1="13" x2="8" y2="13"></line>
              <line x1="16" y1="17" x2="8" y2="17"></line>
            </svg>
            <span>Solicitud</span>
            <!-- Flecha que rota cuando el acordeón se abre -->
            <svg class="incidencia-arrow" width="16" height="16" viewBox="0 0 24 24"
                 fill="none" stroke="currentColor" stroke-width="2"
                 stroke-linecap="round" stroke-linejoin="round"
                 style="margin-left: auto; transition: transform 0.25s ease;">
              <polyline points="6 9 12 15 18 9"></polyline>
            </svg>
          </button>

          <!-- ── Hijos del acordeón (ocultos por defecto) ── -->
          <div class="incidencia-children">
            <button class="calendar-menu-option incidencia-child" data-action="fichaje">
              <svg class="menu-icon" width="20" height="20" viewBox="0 0 24 24"
                 fill="none" stroke="currentColor" stroke-width="2"
                 stroke-linecap="round" stroke-linejoin="round">
              <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
              <polyline points="14 2 14 8 20 8"></polyline>
              <line x1="16" y1="13" x2="8" y2="13"></line>
              <line x1="16" y1="17" x2="8" y2="17"></line>
            </svg>
              <span>Modificación Fichaje</span>
            </button>
            <button class="calendar-menu-option incidencia-child" data-action="vacaciones">
              <svg class="menu-icon" width="20" height="20" viewBox="0 0 24 24"
                 fill="none" stroke="currentColor" stroke-width="2"
                 stroke-linecap="round" stroke-linejoin="round">
              <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
              <polyline points="14 2 14 8 20 8"></polyline>
              <line x1="16" y1="13" x2="8" y2="13"></line>
              <line x1="16" y1="17" x2="8" y2="17"></line>
            </svg>
              <span>Solicitud Vacaciones</span>
            </button>
          </div>

          <!-- Resumen Anual -->
          <button class="calendar-menu-option" data-action="annual" data-view="agenda">
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

          <!-- Agenda -->
          <button class="calendar-menu-option" data-action="agenda" data-view="annual">
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

		this.fabButton = this.querySelector('.calendar-fab');
		this.menuPopup = this.querySelector('.calendar-menu-popup');
		this.closeButton = this.querySelector('.calendar-close-btn');
		this._incidenciaChildren = this.querySelector('.incidencia-children');

		this.setupEventListeners();
		this.setActiveView(this._activeView);
	}

	setupEventListeners() {
		// Abrir/cerrar el menú al pulsar el FAB
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

		// Delegación de eventos: un solo listener para todas las opciones
		this.menuPopup.addEventListener('click', e => {
			const btn = e.target.closest('[data-action]');
			if (!btn || btn.disabled) return;
			e.stopPropagation();
			this.handleMenuAction(btn.dataset.action);
		});
	}

	handleMenuAction(action) {
		// El acordeón no cierra el menú, solo expande/colapsa los hijos
		if (action === 'incidencia-toggle') {
			this.toggleIncidencia();
			return;
		}

		// Cualquier otra acción cierra el menú y lanza su evento
		this.closeMenu();

		const eventMap = {
			'annual': 'calendar-show-annual',
			'agenda': 'calendar-show-agenda',
			'today': 'calendar-go-today',
			'current-month': 'calendar-go-current-month',
			'fichaje': 'calendar-fichaje',
			'vacaciones': 'calendar-vacaciones',
		};

		const eventName = eventMap[action];
		if (eventName) {
			this.dispatchEvent(new CustomEvent(eventName, { bubbles: true, composed: true }));
		}
	}

	toggleIncidencia() {
		this._incidenciaOpen = !this._incidenciaOpen;

		const children = this._incidenciaChildren;
		const arrow = this.querySelector('.incidencia-arrow');

		if (this._incidenciaOpen) {
			// scrollHeight = altura real del contenido sin restricciones
			children.style.maxHeight = children.scrollHeight + 'px';
			if (arrow) arrow.style.transform = 'rotate(180deg)';
		} else {
			children.style.maxHeight = '0px';
			if (arrow) arrow.style.transform = 'rotate(0deg)';
		}
	}

	setActiveView(viewName) {
		this._activeView = viewName;

		this.querySelectorAll('[data-action]').forEach(btn => {
			const restrictedTo = btn.dataset.view; // 'agenda', 'annual' o undefined
			if (restrictedTo) {
				btn.style.display = restrictedTo === viewName ? '' : 'none';
			}
		});
	}

	toggleMenu() { this._isOpen ? this.closeMenu() : this.openMenu(); }

	openMenu() {
		this._isOpen = true;
		this.classList.add('menu-open');
	}

	closeMenu() {
		this._isOpen = false;
		this.classList.remove('menu-open');

		// Colapsar el acordeón al cerrar el menú
		this._incidenciaOpen = false;
		if (this._incidenciaChildren) {
			this._incidenciaChildren.style.maxHeight = '0px';
		}
		const arrow = this.querySelector('.incidencia-arrow');
		if (arrow) arrow.style.transform = 'rotate(0deg)';
	}

	// Mantenemos por compatibilidad con código antiguo
	setOnTodayClick(callback) {
		this._onTodayClick = callback;
	}
}

if (!customElements.get('aon-calendar-menu')) {
	window.customElements.define('aon-calendar-menu', AonCalendarMenu);
}