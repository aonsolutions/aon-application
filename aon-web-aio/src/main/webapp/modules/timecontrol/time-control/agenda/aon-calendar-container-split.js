import { AonElement } from "../../../../components/AonElement.js";
import "./aon-agenda-all-days.js";
import "./aon-agenda-annual-summary.js";

export class AonCalendarContainerSplit extends AonElement {

	async connectedCallback() {
		this.build();
		await this.loadViews();  // esperamos a que todo esté listo
	}

	build() {
		this.innerHTML = `
      <!-- Los dos widgets, uno al lado del otro -->
      <div class="split-wrapper">

        <!-- Panel izquierdo: la agenda de días -->
        <div class="split-panel split-panel--left">
          <aon-agenda-all-days id="agendaView"></aon-agenda-all-days>
        </div>

        <!-- Divisor visual entre los dos paneles -->
        <div class="split-divider"></div>

        <!-- Panel derecho: el resumen anual -->
        <div class="split-panel split-panel--right">
          <aon-agenda-annual-summary id="annualView"></aon-agenda-annual-summary>
        </div>

      </div>
    `;

		// Guardamos referencias para usarlas en loadViews()
		this._agendaView = this.querySelector('#agendaView');
		this._annualView = this.querySelector('#annualView');
	}

	async loadViews() {
		// Esperamos a que el componente termine su buildShell
		// (que incluye el await getTaskHolder interno)
		await this._annualView.ready;

		// Ahora sí, el taskHolder ya está cargado y podemos pedir el año
		this._annualView.loadYear(new Date().getFullYear());
	}
}

// Registramos el nuevo elemento con un nombre distinto al original
window.customElements.define('aon-calendar-container-split', AonCalendarContainerSplit);