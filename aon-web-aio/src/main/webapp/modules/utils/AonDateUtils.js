import { MSG } from "../../environments/environments.js";
import { DAYS, DAYS_ABR, MONTHS, MONTHS_ABR } from "../../models/enums.js";
import { addZero } from "../../services/utils.js";

export const AonDateUtils = {
	/**
	 *
	 * @param {Date} date
	 * @return {String} dd/MM/yyyy
	*/
	formatDate: function(d, format) {
		format = format || 'dd/MM/yyyy';
		if (d !== undefined) {
			const date = new Date(d);
			const day = addZero(date.getDate(), 2);
			const month = addZero(date.getMonth() + 1, 2);
			const year = date.getFullYear();

			switch (format) {
				case 'dd-MM-yyyy':
					return day + "-" + month + '-' + year;
				case 'yyyy-MM-dd':
					return year + "-" + month + '-' + day;
				case 'yyyy/MM/dd':
					return year + "/" + month + '/' + day;
				default:
					return day + "/" + month + '/' + year;
			}
		} else {
			// No tiene fecha
			return "";
		}
	},
	/**
	 *
	 * @param {Date} date
	 * @return {String} H:m
	*/
	setTime: function(date) {
		const newDate = new Date(date);
		const hour = addZero(newDate.getHours(), 2);
		const min = addZero(newDate.getMinutes(), 2);
		return hour + ":" + min;
	},
	/**
	 *
	 * @param {Date} date
	 * @return {String} dd-MM-yyyy
	*/
	setDate: function(date) {
		return this.formatDate(date);
	},
	/**
	 *
	 * @param {Date} date
	 * @return {String} yyyy-MM-dd
	*/
	formatDateOrigin: function(d) {
		let date = new Date(d);
		const day = addZero(date.getDate(), 2);
		const month = addZero(date.getMonth() + 1, 2);
		const year = date.getFullYear();
		return year + "-" + month + "-" + day;
	},
	/**
	 *
	 * @param {Date} date
	 * @return {String} dd-MM-yyyy H:m
	*/
	setDateTimestamp: function(d) {
		const date = new Date(d);
		return this.formatDate(date) + " " + this.setTime(date);
	},
	/**
	 *
	 * @param {Date} date
	 * @return {String} DAY OR NULL
	*/
	lastThreeDayStr: function(d) {
		const date = new Date(d);
		const now = new Date();
		let day = null;
		if (date.getFullYear() === now.getFullYear() && date.getMonth() === now.getMonth()) {
			if (date.getDate() === now.addDay(-1).getDate()) day = MSG.YESTERDAY;
			else if (date.getDate() === new Date().getDate()) day = MSG.TODAY;
			else if (date.getDate() === new Date().addDay(1).getDate()) day = MSG.TOMORROW;
		}
		return day;
	},
	/**
	 *
	 * @param {Date} date
	 * @return {Date} date first day year
	*/
	getYearFirstDay: function(d) {
		return new Date(d.getFullYear(), 0, 1);
	},
	/**
	 * 
	 * @param {Date} date
	 * @return {Date} date last day year
	*/
	getYearLastDay: function(d) {
		return new Date(d.getFullYear(), 11, 31);
	},
	/**
	 * 
	 * @param {Date} date
	 * @return {Date} date first day month
	*/
	getMonthFirstDay: function(d) {
		return new Date(d.getFullYear(), d.getMonth(), 1);
	},
	/**
	 * 
	 * @param {Date} date
	 * @return {String} date first day month
	*/
	getMonthFirstDayFormat: function(d) {
		return this.formatDate(this.getMonthFirstDay(d), 'yyyy-MM-dd');
	},
	/**
	 * 
	 * @param {Date} date
	 * @return {Date} date last day month
	*/
	getMonthLastDay: function(d) {
		return new Date(d.getFullYear(), d.getMonth() + 1, 0);
	},
	/**
	 * 
	 * @param {Date} date
	 * @return {String} date last day month
	 */
	getMonthLastDayFormat: function(d) {
		return this.formatDate(this.getMonthLastDay(d), 'yyyy-MM-dd');
	},
	/**
	 * 
	 * @param {Date} date
	 * @return {Date} date last day of last month
	*/
	getLastMonthLastDay: function(d) {
		return new Date(d.getFullYear(), d.getMonth(), 0);
	},
	/**
	 * @param {Date} date
	 * @return {String} date last day of last month
	 */
	getLastMonthLastDayFormat: function(d) {
		return this.formatDate(this.getLastMonthLastDay(d), 'yyyy-MM-dd');
	},
	/**
	 * 
	 * @param {Date} date
	 * @return {Date} date first day of last month
	*/
	getLastMonthFirstDay: function(d) {
		return new Date(d.getFullYear(), d.getMonth() - 1, 1);
	},
	/**
	 * @param {Date} date
	 * @return {String} date first day of last month
	 */
	getLastMonthFirstDayFormat: function(d) {
		return this.formatDate(this.getLastMonthFirstDay(d), 'yyyy-MM-dd');
	},
	
	/**
	 *
	 * @param {Date} date
	 * @return {String} DAY OR NULL
	*/
	getDayStr: function(date) {
		return this.lastThreeDayStr(date) || DAYS[date.getDay()];
	},
	/**
	 *
	 * @param {Date} d
	 * @return {String} DAY, dd-MM-yyyy
	*/
	setDateTpDay: function(d) {
		const date = new Date(d);
		return this.getDayStr(date) + ", " + this.formatDate(date);
	},
	/**
	 *
	 * @param {Date} date
	 * @return {String} DAY, dd-MM-yyyy H:m
	*/
	setDateTimestampDay: function(d) {
		return this.setDateTpDay(new Date(d)) + " " + this.setTime(new Date(d));
	},
	/**
	 *
	 * @param {Date} date
	 * @return {String} DAY, dd de mm de yyyy
	*/
	setFullDate: function(d) {
		const date = new Date(d);
		const dayText = this.getDayStr(date);
		const monthText = MONTHS[date.getMonth()];
		return `${dayText}, ${date.getDate()} de ${monthText} de ${date.getFullYear()}`;
	},
	/**
	 *
	 * @param {Date} date
	 * @return {String} MONTH yyyy
	*/
	getDayMonth: function(d) {
		const date = new Date(d);
		const day = addZero(date.getDate(), 2);
		const month = MONTHS[date.getMonth()];
		return day + " " + month;
	},
	/**
	 *
	 * @param {Date} date
	 * @return {String} MONTH yyyy
	*/
	getDayMonthAbr: function(d) {
		const date = new Date(d);
		const day = addZero(date.getDate(), 2);
		const month = MONTHS_ABR[date.getMonth()];
		return day + " " + month;
	},
	/**
	 *
	 * @param {Date} date
	 * @return {String} dd-MONTH OR OTHER YEAR dd-mm-yyyy
	*/
	getDayMonthOrFull: function(d) {
		const date = new Date(d);
		const now = new Date();

		if (date.getFullYear() === now.getFullYear()) {
			return this.lastThreeDayStr(date) || this.getDayMonth(date);
		}

		return this.formatDate(date);
	},
	/**
	 *
	 * @param {Date} date
	 * @return {String} dd-MONTH OR OTHER YEAR dd-mm-yyyy
	*/
	getDayMonthOrFullShort: function(d) {
		const date = new Date(d);
		const now = new Date();

		if (date.getFullYear() === now.getFullYear()) {
			return this.lastThreeDayStr(date) || this.getDayMonthAbr(date);
		}

		return this.formatDate(date);
	},
	/**
	 *
	 * @param {Date} date
	 * @return {String} MONTH. yyyy
	*/
	getMonthYear: function(date) {
		const d = new Date(date);
		const month = MONTHS[d.getMonth()];
		return month + ". " + d.getFullYear();
	},
	/**
	 *
	 * @param {Date} date
	 * @return {String} H:M:S
	*/
	timeParser: function(time) {
		let msecPerMinute = 1000 * 60;
		let msecPerHour = msecPerMinute * 60;

		// Calcular las horas , minutos y segundos
		let hours = Math.floor(time / msecPerHour);
		time = time - hours * msecPerHour;

		let minutes = Math.floor(time / msecPerMinute);
		time = time - minutes * msecPerMinute;

		let seconds = Math.floor(time / 1000);
		return addZero(hours, 2) + ":" + addZero(minutes, 2) + ":" + addZero(seconds, 2);
	},
	timeParserShort: function(time) {
		let msecPerMinute = 1000 * 60;
		let msecPerHour = msecPerMinute * 60;

		// Calcular las horas , minutos y segundos
		let hours = Math.floor(time / msecPerHour);
		time = time - hours * msecPerHour;

		let minutes = Math.floor(time / msecPerMinute);
		time = time - minutes * msecPerMinute;

		let seconds = Math.floor(time / 1000);
		return hours + ":" + addZero(minutes, 2) + ":" + addZero(seconds, 2);
	},
	parse: function(dateStr) {
		dateStr = dateStr.replaceAll('"', '');
		let dateParse = Date.parse(dateStr);
		if (isNaN(dateParse)) {
			dateParse = Date.parse(dateStr.substring(0, 10))
		}
		if (isNaN(dateParse) && (dateStr.includes('/') || dateStr.includes('-'))) {
			let dateArr = dateStr.includes('/') ? dateStr.split('/') : dateStr.split('-');
			let a = dateArr[0].length === 4
				? addZero(dateArr[2], 2) : addZero(dateArr[0], 2);
			let b = addZero(dateArr[2], 2);
			let c = dateArr[0].length === 4
				? dateArr[0] : dateArr[2].substring(0, 4);
			dateStr = a + '/' + b + '/' + c;
			dateParse = Date.parse(dateStr);
		}
		return new Date(dateParse);
	},
	/**
	 *
	 * @param {Date} date
	 * @return {String} H:M
	*/
	timeParserHHMM: function(time) {
		let date = new Date();
		date.setTime(time);
		// Extraer horas y minutos
		let hours = date.getHours();
		let minutes = date.getMinutes();

		// Agregar un 0 al inicio si es necesario para formato de dos dígitos
		hours = hours < 10 ? '0' + hours : hours;
		minutes = minutes < 10 ? '0' + minutes : minutes;

		// Retornar en formato hh:mm
		return `${hours}:${minutes}`;
	},

	isAfterOrEqual: (date1, date2) => {
		const d1 = new Date(date1.getFullYear(), date1.getMonth(), date1.getDate());
		const d2 = new Date(date2.getFullYear(), date2.getMonth(), date2.getDate());
		return d1 >= d2;
	},

	/**
	 *
	 * @param {Date} date
	 * @return {Date} Lunes de la semana
	 */
	startOfWeek: function(d) {
		const date = new Date(d);
		const day = date.getDay() || 7; // Domingo = 7
		if (day !== 1) {
			date.setDate(date.getDate() - (day - 1));
		}
		date.setHours(0, 0, 0, 0);
		return date;
	},

	/**
	 *
	 * @param {Date} date
	 * @return {String} DAY (LUN, MAR...)
	 */
	dayName: function(d) {
		const date = new Date(d);
		return DAYS[date.getDay()];
	},

	/**
	 *
	 * @param {Date} date
	 * @return {String} MONTH
	 */
	monthName: function(d) {
		const date = new Date(d);
		return MONTHS[date.getMonth()];
	},

	/**
	 *
	 * @param {Date} date
	 * @param {String} format
	 * @return {String}
	 */
	format: function(d, format) {
		if (!d) return "";

		const date = new Date(d);
		const day = addZero(date.getDate(), 2);
		const month = addZero(date.getMonth() + 1, 2);
		const year = date.getFullYear();

		switch (format) {
			case "YYYY-MM-DD":
				return `${year}-${month}-${day}`;
			case "DD/MM/YYYY":
				return `${day}/${month}/${year}`;
			default:
				return this.formatDate(date);
		}
	},

	/**
	 *
	 * @param {Date|Number} date
	 * @return {String} hh:mm
	 */
	hour: function(d) {
		const date = new Date(d);
		const h = addZero(date.getHours(), 2);
		const m = addZero(date.getMinutes(), 2);
		return `${h}:${m}`;
	},

	/**
   * Formatea el nombre visible de un periodo para mostrarlo en la UI.
   *
   * Casos:
   *  - Un solo día (hoy/ayer/mañana): "Hoy Mar. 10/03"
   *  - Rango de días:                 "Mes actual (01 mar / 31 mar)"
   *
   * @param {Object} period  - Objeto periodo de getPeriod()
   * @param {Date}   [filterStart] - Solo para "personalized"
   * @param {Date}   [filterEnd]   - Solo para "personalized"
   * @return {String}
   */
	getPeriodName: function(period, filterStart, filterEnd) {

		// Elegimos las fechas: si es personalizado usamos las del filtro,
		// si no, las que trae el propio periodo.
		const start = new Date(period.value === "personalized" ? filterStart : period.startDate);
		const end = new Date(period.value === "personalized" ? filterEnd : period.endDate);

		// ¿Es un periodo de un solo día?
		// Comparamos solo año/mes/día, ignorando la hora.
		const isSingleDay =
			start.getFullYear() === end.getFullYear() &&
			start.getMonth() === end.getMonth() &&
			start.getDate() === end.getDate();

		if (isSingleDay) {
			// lastThreeDayStr devuelve "Hoy", "Ayer" o "Mañana" si aplica, o null.
			const relativeLabel = this.lastThreeDayStr(start);

			// DAYS es el array de nombres de día (Lun, Mar, Mié…).
			// .getDay() devuelve 0=Dom, 1=Lun… por eso usamos el array.
			const dayAbbr = DAYS_ABR[start.getDay()]; // ej.: "Mar"

			// dd/mm  — sin año porque estamos en el año actual
			const day = addZero(start.getDate(), 2);
			const month = addZero(start.getMonth() + 1, 2);
			const ddmm = `${day}/${month}`;

			// Si tenemos etiqueta relativa: "Hoy Mar. 10/03"
			// Si no (por si acaso): "Mar. 10/03"
			return relativeLabel
				? `${relativeLabel} ${dayAbbr}. ${ddmm}`
				: `${dayAbbr}. ${ddmm}`;
		}

		// --- Rango de varios días ---
		// Usamos getDayMonthOrFull que ya tienes: devuelve "10 marzo" o "10/03/2024"
		// según si es el año actual o no. Así funciona también para años anteriores.
		const startStr = this.getDayMonthAbr(start); // ej.: "01 marzo"
		const endStr = this.getDayMonthAbr(end);   // ej.: "31 marzo"
		
		return `${period.name} (${startStr} / ${endStr})`;
	},

}