import { AonElement } from "../../../../components/AonElement.js";

export class AonCalendarMenu extends AonElement {
	
	constructor() {
		super();
		this._isOpen = false;
		this._onTodayClick = null;
		this._initialized = false;
	}
	
	connectedCallback() {
		// Inicializar inmediatamente si no se ha hecho
		if (!this._initialized) {
			this._initialized = true;
			this.initializeComponent();
		}
	}
	
	initializeComponent() {
		// Crear el HTML del componente
		this.innerHTML = `
			<div class="calendar-menu-container">
				<!-- Botón flotante principal -->
				<button class="calendar-fab" aria-label="Menú de calendario">
					<svg class="calendar-icon" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
						<rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
						<line x1="16" y1="2" x2="16" y2="6"></line>
						<line x1="8" y1="2" x2="8" y2="6"></line>
						<line x1="3" y1="10" x2="21" y2="10"></line>
					</svg>
				</button>
				
				<!-- Menú desplegable -->
				<div class="calendar-menu-popup">
					<button class="calendar-menu-option disabled" data-action="request" disabled>
						<svg class="menu-icon" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
							<path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
							<polyline points="14 2 14 8 20 8"></polyline>
							<line x1="16" y1="13" x2="8" y2="13"></line>
							<line x1="16" y1="17" x2="8" y2="17"></line>
							<polyline points="10 9 9 9 8 9"></polyline>
						</svg>
						<span>Solicitud</span>
					</button>
					
					<button class="calendar-menu-option disabled" data-action="yearly" disabled>
						<svg class="menu-icon" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
							<rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
							<line x1="16" y1="2" x2="16" y2="6"></line>
							<line x1="8" y1="2" x2="8" y2="6"></line>
							<line x1="3" y1="10" x2="21" y2="10"></line>
							<rect x="7" y="12" width="3" height="3"></rect>
							<rect x="14" y="12" width="3" height="3"></rect>
						</svg>
						<span>Vista Anual</span>
					</button>
					
					<button class="calendar-menu-option disabled" data-action="monthly" disabled>
						<svg class="menu-icon" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
							<rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
							<line x1="16" y1="2" x2="16" y2="6"></line>
							<line x1="8" y1="2" x2="8" y2="6"></line>
							<line x1="3" y1="10" x2="21" y2="10"></line>
						</svg>
						<span>Vista Mensual</span>
					</button>
					
					<button class="calendar-menu-option" data-action="today">
						<svg class="menu-icon" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
							<circle cx="12" cy="12" r="10"></circle>
							<polyline points="12 6 12 12 16 14"></polyline>
						</svg>
						<span>Hoy</span>
					</button>
					
				</div>
				
				<!-- Botón de cerrar (X) -->
				<button class="calendar-close-btn" aria-label="Cerrar menú">
					<svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
						<line x1="18" y1="6" x2="6" y2="18"></line>
						<line x1="6" y1="6" x2="18" y2="18"></line>
					</svg>
				</button>
			</div>
		`;
		
		// Obtener referencias a elementos
		this.fabButton = this.querySelector('.calendar-fab');
		this.menuPopup = this.querySelector('.calendar-menu-popup');
		this.closeButton = this.querySelector('.calendar-close-btn');
		this.menuOptions = this.querySelectorAll('.calendar-menu-option:not(.disabled)');
		
		// Configurar event listeners
		this.setupEventListeners();
	
	}
	
	setupEventListeners() {
		if (!this.fabButton || !this.closeButton) {
			console.error('AonCalendarMenu: No se encontraron los elementos necesarios');
			return;
		}
		
		// Click en el botón principal
		this.fabButton.addEventListener('click', (e) => {
			e.stopPropagation();
			this.toggleMenu();
		});
		
		// Click en el botón de cerrar
		this.closeButton.addEventListener('click', (e) => {
			e.stopPropagation();
			this.closeMenu();
		});
		
		// Click en opciones del menú
		this.menuOptions.forEach(option => {
			option.addEventListener('click', (e) => {
				e.stopPropagation();
				const action = option.dataset.action;
				this.handleMenuAction(action);
			});
		});
		
		// Cerrar menú al hacer click fuera
		document.addEventListener('click', (e) => {
			if (this._isOpen && !this.contains(e.target)) {
				this.closeMenu();
			}
		});
	}
	
	toggleMenu() {
		if (this._isOpen) {
			this.closeMenu();
		} else {
			this.openMenu();
		}
	}
	
	openMenu() {
		this._isOpen = true;
		this.classList.add('menu-open');
	}
	
	closeMenu() {
		this._isOpen = false;
		this.classList.remove('menu-open');
	}
	
	handleMenuAction(action) {
		switch(action) {
			case 'today':
				this.closeMenu();
				if (this._onTodayClick) {
					this._onTodayClick();
				}
				break;
			case 'monthly':
				// TODO: Implementar vista mensual
				console.log('Vista mensual - Por implementar');
				break;
			case 'yearly':
				// TODO: Implementar vista anual
				console.log('Vista anual - Por implementar');
				break;
			case 'request':
				// TODO: Implementar solicitud
				console.log('Solicitud - Por implementar');
				break;
		}
	}
	
	// Método público para establecer el callback de "Hoy"
	setOnTodayClick(callback) {
		this._onTodayClick = callback;
	}
}

// Registrar el componente
if (!customElements.get("aon-calendar-menu")) {
	window.customElements.define("aon-calendar-menu", AonCalendarMenu);
	console.log('AonCalendarMenu: Componente registrado');
}