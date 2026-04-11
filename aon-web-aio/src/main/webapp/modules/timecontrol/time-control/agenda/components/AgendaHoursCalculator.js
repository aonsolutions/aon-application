import { AonDateUtils } from "../../../../utils/AonDateUtils.js";
import { timeHourShort } from "../../utils.js";

/**
 * AgendaHoursCalculator
 *
 * Responsabilidad única: calcular horas trabajadas, esperadas
 * y diferencias — tanto por día, semana o mes.
 *
 * No sabe nada del DOM. Recibe los mapas de datos y devuelve strings HTML.
 */
export class AgendaHoursCalculator {

	constructor(dataService) {
		// Recibe referencia al servicio de datos para leer los mapas
		this.data = dataService;
	}

	/* ---------------- DIFF FORMAT ---------------- */

	formatDiffTime(ms) {
		const sign    = ms < 0 ? "-" : "+";
		const absTime = Math.abs(ms);
		return `${sign}${timeHourShort(absTime)}`;
	}

	/* ---------------- HORAS DE UN DÍA ---------------- */

	/**
	 * Devuelve { workedTime, expectedTime } en milisegundos para un día concreto,
	 * aplicando las reglas de vacaciones, IT y permisos retribuidos.
	 */
	getDayHours(dateKey, dayOfWeek) {
		const msPerHour = 1000 * 60 * 60;
		
		// Los festivos de contrato no cuentan
		//if (this.data.festivesContract.has(dateKey)) return { workedTime: 0, expectedTime: 0 };

		const hoursForDay  = this.data.workingDaysHours.length === 7
			? (this.data.workingDaysHours[dayOfWeek] || 0) : 0;
		const expectedTime = hoursForDay * msPerHour;

		const dayEventData = this.data.events.get(dateKey);
		let workedTime     = dayEventData ? Number(dayEventData.dayData.time || 0) : 0;
		
		// Los festivos de contrato no cuentan
		if (this.data.festivesContract.has(dateKey)) return { workedTime, expectedTime: 0 };

		const dayTypeContract = this.data.daysTypeContract.get(dateKey);
		if (dayTypeContract && (
			dayTypeContract.source === "IT" ||
			dayTypeContract.source === "PAID_LEAVE" ||
			dayTypeContract.source === "HOLIDAYS"
		)) {
			workedTime = expectedTime;
		}

		return { workedTime, expectedTime };
	}

	/* ---------------- HORAS DE LA SEMANA ---------------- */

	calculateWeekHours(week) {
		if (!week?.days?.length) return this._emptyHoursHtml();

		let totalWorked = 0, totalExpected = 0;

		week.days.forEach(dayObj => {
			const day = new Date(dayObj.date);
			day.setHours(0, 0, 0, 0);
			const { workedTime, expectedTime } = this.getDayHours(dayObj.key, day.getDay());
			totalWorked   += workedTime;
			totalExpected += expectedTime;
		});

		return this._hoursHtml(totalWorked, totalExpected);
	}

	/* ---------------- HORAS DEL MES ---------------- */

	calculateMonthHours(centerDate) {
		if (!centerDate) return this._emptyHoursHtml();

		const start = new Date(centerDate.getFullYear(), centerDate.getMonth(), 1);
		const end   = new Date(centerDate.getFullYear(), centerDate.getMonth() + 1, 0);

		let totalWorked = 0, totalExpected = 0;

		for (let d = new Date(start); d <= end; d.setDate(d.getDate() + 1)) {
			const day     = new Date(d);
			day.setHours(0, 0, 0, 0);
			const dateKey = AonDateUtils.format(day, "YYYY-MM-DD");
			const dow     = day.getDay();

			// Los festivos de contrato no cuentan
			//if (this.data.festivesContract.has(dateKey)) continue;

			const { workedTime, expectedTime } = this.getDayHours(dateKey, dow);
			totalWorked   += workedTime;
			totalExpected += expectedTime;
		}

		return this._hoursHtml(totalWorked, totalExpected);
	}

	/* ---------------- HELPERS HTML ---------------- */

	_hoursHtml(worked, expected) {
		const workedStr = timeHourShort(worked);
		const diffStr   = this.formatDiffTime(worked - expected);
		const diffClass = (worked - expected) < 0 ? 'negative' : 'positive';
		return `${workedStr} h / <span class="day-diff-hours ${diffClass}">${diffStr} h</span>`;
	}

	_emptyHoursHtml() {
		return `0:00 h / <span class="day-diff-hours positive">+0:00 h</span>`;
	}
}