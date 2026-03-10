import { AonElement } from "../../../../components/AonElement.js";
import { waitEl } from "../../../../services/utils.js";
import { TASK_SOURCE } from "../../../messenger/MessengerEnums.js";
import { AonMessenger } from "../../../messenger/aon-messenger.js";

// Importamos los dos widgets de vista
import "./aon-agenda-all-days.js";
import "./aon-agenda-annual-summary.js";
import "./aon-calendar-menu.js";

/**
 * COMPONENTE PADRE: aon-calendar-container
 *
 * Es el "director de orquesta". Su único trabajo es:
 *   1. Mostrar la vista correcta (agenda o resumen anual)
 *   2. Escuchar los eventos del menú y cambiar de vista cuando corresponda
 *   3. Pasar al menú información sobre qué vista está activa,
 *      para que el menú pueda mostrar/ocultar las opciones correctas.
 */
export class AonCalendarContainer extends AonElement {

	// La vista activa al arrancar es siempre la agenda
	_currentView = 'agenda'; // 'agenda' | 'annual'

	constructor() {
		super();
		this.ready = new Promise(resolve => {
			this._readyResolve = resolve;
		});
	}

	connectedCallback() {
		this.build();
		this.attachEventListeners();
		// Avisamos de que el contenedor ya está listo
		this._readyResolve();
	}

	build() {
		this.innerHTML = `
      <!-- Vista 1: la agenda de días -->
      <aon-agenda-all-days id="agendaView"></aon-agenda-all-days>

      <!-- Vista 2: el resumen anual (oculto al inicio) -->
      <aon-agenda-annual-summary id="annualView" style="display:none;"></aon-agenda-annual-summary>

      <!-- El menú flotante -->
      <aon-calendar-menu id="calendarMenu"></aon-calendar-menu>
    `;

		// Guardamos referencias a cada parte para no tener que buscarlas cada vez
		this._agendaView = this.querySelector('#agendaView');
		this._annualView = this.querySelector('#annualView');
		this._menu = this.querySelector('#calendarMenu');
	}

	attachEventListeners() {
		/**
		 * Escuchamos los eventos que lanza el menú.
		 * El menú no sabe nada del contenedor; solo lanza eventos.
		 * El contenedor los escucha y decide qué hacer.
		 *
		 * Es como un mando a distancia: el botón no sabe qué tele tiene delante,
		 * solo emite una señal. La tele la recibe y reacciona.
		 */

		// "Ir a la agenda"
		this.addEventListener('calendar-show-agenda', () => {
			this.showView('agenda');
		});

		// "Ir al resumen anual"
		this.addEventListener('calendar-show-annual', () => {
			this.showView('annual');
		});

		// "Ir a hoy" → se lo delegamos a la agenda
		this.addEventListener('calendar-go-today', () => {
			if (this._agendaView && typeof this._agendaView.goToToday === 'function') {
				this._agendaView.goToToday(true);
			}
		});

		// "Ir al mes actual" → se lo delegamos al resumen anual
		this.addEventListener('calendar-go-current-month', () => {
			if (this._annualView && typeof this._annualView.goToCurrentMonth === 'function') {
				this._annualView.goToCurrentMonth(true);
			}
		});

		// El usuario quiere modificar un fichaje
		this.addEventListener('calendar-fichaje', () => {
			const aonComponent = new AonMessenger();
			aonComponent.data = { source: TASK_SOURCE.QUERY };
			this.rootPanel(aonComponent);
			// Añadimos el tercer parámetro: la vista desde la que se llamó
			this._setupMessenger(aonComponent, 'request', '3', this._currentView);
		});

		this.addEventListener('calendar-vacaciones', () => {
			const aonComponent = new AonMessenger();
			aonComponent.data = { source: TASK_SOURCE.QUERY };
			this.rootPanel(aonComponent);
			this._setupMessenger(aonComponent, 'request', '1', this._currentView);
		});
	}

	/**
	 * showView(viewName)
	 *
	 * Muestra la vista solicitada y oculta la otra.
	 * También le dice al menú qué vista está activa,
	 * para que pueda ajustar sus opciones.
	 */
	showView(viewName) {
		this._currentView = viewName;

		if (viewName === 'agenda') {
			this._agendaView.style.display = '';      // mostrar
			this._annualView.style.display = 'none';  // ocultar

			// Cuando se muestra el resumen, decirle qué año cargar
			// (por defecto el año actual; el componente lo gestiona internamente)
			if (typeof this._agendaView.reload === 'function') {
				this._agendaView.reload();
			}

		} else {
			this._agendaView.style.display = 'none';  // ocultar
			this._annualView.style.display = '';      // mostrar

			// Cuando se muestra el resumen, decirle qué año cargar
			// (por defecto el año actual; el componente lo gestiona internamente)
			if (typeof this._annualView.loadYear === 'function') {
				this._annualView.loadYear(new Date().getFullYear());
			}
		}

		// Informamos al menú de la vista activa para que adapte sus botones
		if (this._menu && typeof this._menu.setActiveView === 'function') {
			this._menu.setActiveView(viewName);
		}
	}

	// Método privado reutilizable para los dos casos.
	// Espera a que AonMessenger esté listo y luego rellena los campos.
	async _setupMessenger(component, sourceTaskValue, processTypeValue, originView) {

		// Guardamos referencia a la aplicación AHORA, antes de que setContent
		// reemplace el contenedor. Después del setContent del messenger,
		// "this" seguirá vivo en memoria pero ya no estará en el DOM.
		component.onBack = async () => {
			// 1. Creamos un nuevo AonCalendarContainer y lo pintamos
			const calendar = new AonCalendarContainer();
			this.rootPanel(calendar);

			// 2. Esperamos a que el contenedor esté listo (connectedCallback terminado)
			await calendar.ready;

			// 3. Ahora sí, mostramos la vista desde la que se abrió el messenger
			calendar.showView(originView);
		};

		await component.ready;

		const sourceTask = component.querySelector('#sourceTask');
		if (sourceTask) {
			sourceTask.value = sourceTaskValue;
			sourceTask.dispatchEvent(new Event('change', { bubbles: true }));
		}

		await this._waitForElement(component, '#processType');

		const processType = component.querySelector('#processType');
		if (processType) {
			processType.value = processTypeValue;
			processType.dispatchEvent(new Event('change', { bubbles: true }));
		}
	}

	// Helper: devuelve una promesa que se resuelve cuando el selector
	// aparece dentro de 'root'. Si ya existe, se resuelve inmediatamente.
	_waitForElement(root, selector) {
		return new Promise(resolve => {

			// Si el elemento ya existe, no hace falta esperar
			if (root.querySelector(selector)) {
				resolve();
				return;
			}

			// Si no existe aún, montamos el vigilante
			const observer = new MutationObserver(() => {
				if (root.querySelector(selector)) {
					observer.disconnect(); // apagamos el vigilante, ya no lo necesitamos
					resolve();
				}
			});

			// Le decimos al vigilante que observe cambios en el subárbol completo
			observer.observe(root, { childList: true, subtree: true });
		});
	}
}

window.customElements.define('aon-calendar-container', AonCalendarContainer);