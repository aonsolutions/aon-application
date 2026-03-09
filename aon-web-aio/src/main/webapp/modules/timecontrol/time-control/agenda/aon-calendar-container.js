import { AonElement } from "../../../../components/AonElement.js";

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

  connectedCallback() {
    this.build();
    this.attachEventListeners();
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
    this._agendaView  = this.querySelector('#agendaView');
    this._annualView  = this.querySelector('#annualView');
    this._menu        = this.querySelector('#calendarMenu');
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
      this._agendaView.style.display  = '';      // mostrar
      this._annualView.style.display  = 'none';  // ocultar
      
      // Cuando se muestra el resumen, decirle qué año cargar
      // (por defecto el año actual; el componente lo gestiona internamente)
      if (typeof this._agendaView.reload === 'function') {
        this._agendaView.reload();
      }
      
    } else {
      this._agendaView.style.display  = 'none';  // ocultar
      this._annualView.style.display  = '';      // mostrar

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
}

window.customElements.define('aon-calendar-container', AonCalendarContainer);