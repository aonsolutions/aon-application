import { getTaskHolderContactEvents, getTaskHolderTimeControl } from "../../../../../services/timeControlService.js";
import { AonDateUtils } from "../../../../utils/AonDateUtils.js";

/**
 * AgendaDataService
 *
 * Responsabilidad única: cargar datos del servidor y procesarlos
 * en las estructuras Map que la agenda necesita.
 *
 * No sabe nada del DOM. Recibe y devuelve datos.
 */
export class AgendaDataService {

	constructor(taskHolder) {
		this.taskHolder = taskHolder;

		// Mapas de datos — la clase principal los leerá directamente
		this.events           = new Map(); // "YYYY-MM-DD" → { dayData, details }
		this.festivesContract = new Map(); // "YYYY-MM-DD" → festivo
		this.daysTypeContract = new Map(); // "YYYY-MM-DD" → tipo de día
		this.workingDays      = [];
		this.workingDaysHours = [];

		// Control de qué meses están cargados o cargándose
		this.loadedMonths  = new Set();
		this.loadingMonths = new Set();
		this.isLoading     = false;
	}

	/* ---------------- CARGA ---------------- */

	buildFilter(startDate, endDate) {
		return {
			group: "DAY",
			period: "personalized",
			startDate: AonDateUtils.format(startDate, "YYYY-MM-DD"),
			endDate:   AonDateUtils.format(endDate,   "YYYY-MM-DD"),
			active: true,
			name: "Personalizado",
			value: "personalized",
			taskHolderId: this.taskHolder,
			event: "click",
			search: ""
		};
	}

	monthKey(date) {
		return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}`;
	}

	/**
	 * Carga un mes concreto. Devuelve true si lo cargó, false si ya estaba.
	 * Lanza excepción si hay error de red.
	 */
	async loadMonth(date) {
		const key = this.monthKey(date);
		if (this.loadedMonths.has(key) || this.loadingMonths.has(key)) return false;

		this.loadingMonths.add(key);
		this.isLoading = true;

		const startDate = new Date(date.getFullYear(), date.getMonth(), 1);
		const endDate   = new Date(date.getFullYear(), date.getMonth() + 1, 0);
		const filter    = this.buildFilter(startDate, endDate);

		try {
			const [datos, contractDatos] = await Promise.all([
				getTaskHolderTimeControl(filter),
				getTaskHolderContactEvents(filter)
			]);

			this._processEvents(datos);
			this._processDaysType(contractDatos.daysType);
			this._processFestives(contractDatos.festives);
			this.workingDays      = contractDatos.workingDays      || [];
			this.workingDaysHours = contractDatos.workingDaysHours || [];

			this.loadedMonths.add(key);
			return true;
		} finally {
			this.loadingMonths.delete(key);
			this.isLoading = false;
		}
	}

	/* ---------------- PROCESADO ---------------- */

	_processEvents(datos) {
		if (!Array.isArray(datos)) return;
		datos.forEach(d => {
			const key = AonDateUtils.format(new Date(d.start_date), "YYYY-MM-DD");
			if (!this.events.has(key)) {
				this.events.set(key, {
					dayData: { start_date: d.start_date, end_date: d.end_date, time: d.time || 0, status: d.status },
					details: d.detail || []
				});
			}
		});
	}

	_processFestives(datos) {
		if (!Array.isArray(datos)) return;
		datos.forEach(d => {
			const key = AonDateUtils.format(new Date(d.start_date), "YYYY-MM-DD");
			if (!this.festivesContract.has(key)) {
				this.festivesContract.set(key, {
					start_date: d.start_date, end_date: d.end_date,
					description: d.description, source: d.source
				});
			}
		});
	}

	_processDaysType(datos) {
		if (!Array.isArray(datos)) return;
		datos.forEach(d => {
			const start = new Date(d.start_date);
			const end   = new Date(d.end_date);
			start.setHours(0, 0, 0, 0);
			end.setHours(0, 0, 0, 0);
			for (let cur = new Date(start); cur <= end; cur.setDate(cur.getDate() + 1)) {
				const key = AonDateUtils.format(cur, "YYYY-MM-DD");
				if (!this.daysTypeContract.has(key)) {
					this.daysTypeContract.set(key, { description: d.description, source: d.source });
				}
			}
		});
	}

	/* ---------------- RESET ---------------- */

	clear() {
		this.events.clear();
		this.festivesContract.clear();
		this.daysTypeContract.clear();
		this.loadedMonths.clear();
		this.loadingMonths.clear();
		this.workingDays      = [];
		this.workingDaysHours = [];
		this.isLoading        = false;
	}
}