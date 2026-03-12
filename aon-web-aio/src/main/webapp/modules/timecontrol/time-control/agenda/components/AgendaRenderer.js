import { AonDateUtils } from "../../../../utils/AonDateUtils.js";
import { timeHourShort } from "../../utils.js";

/**
 * AgendaRenderer
 *
 * Responsabilidad única: construir elementos DOM a partir de datos.
 * No carga nada, no calcula horas de mes/semana — solo renderiza.
 */
export class AgendaRenderer {

	constructor(dataService, hoursCalculator) {
		this.data  = dataService;
		this.hours = hoursCalculator;
	}

	/* ---------------- SEMANA ---------------- */

	renderWeek(week) {
		const el    = document.createElement("section");
		el.className     = "week";
		el.dataset.offset = week.offset;

		const today = new Date();
		today.setHours(0, 0, 0, 0);

		week.days.forEach(day => {
			el.appendChild(this._renderDay(day, today));
		});

		return el;
	}

	/* ---------------- DÍA ---------------- */

	_renderDay(day, today) {
		const dayEl   = document.createElement("div");
		dayEl.className = "day";

		const dayDate = new Date(day.date);
		dayDate.setHours(0, 0, 0, 0);
		const dayOfWeek = dayDate.getDay();

		// Hoy
		if (dayDate.getTime() === today.getTime()) dayEl.classList.add("today");

		// Fin de semana
		if (this.data.workingDays.length > 0) {
			if (this.data.workingDays[dayOfWeek] === 1) dayEl.classList.add("weekend");
		} else if (dayOfWeek === 0 || dayOfWeek === 6) {
			dayEl.classList.add("weekend");
		}

		const dayTypeContract   = this.data.daysTypeContract.get(day.key);
		const dayContractFestive = this.data.festivesContract.get(day.key);
		const dayEventData       = this.data.events.get(day.key);

		// Horas trabajadas y esperadas
		const { workedTime, expectedTime } = this.hours.getDayHours(day.key, dayOfWeek);
		const totalHours = timeHourShort(workedTime);
		const diffHours  = this.hours.formatDiffTime(workedTime - expectedTime);

		// HTML del contenido del día
		let eventsHtml = '<div class="events">';
		if (dayTypeContract && dayTypeContract.source !== 'HOLIDAYS') {
			eventsHtml += this._renderDaysTypeEvent(dayTypeContract);
		}
		if (dayEventData) {
			eventsHtml += this._renderDayEvents(dayEventData.details);
		}
		eventsHtml += '</div>';

		// Festivo / vacaciones junto al número de día
		let dayFestiveHtml = '';
		if (dayContractFestive) {
			dayFestiveHtml = this._renderContractFestive(dayContractFestive);
		} else if (dayTypeContract?.source === "HOLIDAYS") {
			dayFestiveHtml = this._renderContractHoliday();
		}

		// Clase de color del número de día
		const dayNumberClass = dayContractFestive
			? 'festive'
			: (dayTypeContract ? dayTypeContract.source.toLowerCase() : '');

		// Mostrar horas solo si el día ya pasó y tiene horas, o si es no laborable con horas
		const showHours = (today >= dayDate && workedTime > 0)
			|| (workedTime > 0 && this.data.workingDays[dayOfWeek] === 0);

		const dayNameShort = AonDateUtils.dayName(day.date).substring(0, 3) + '.';

		dayEl.innerHTML = `
			<div class="day-header">
				<div class="day-header-left">
					<span class="day-name">${dayNameShort}</span>
					<span class="day-number ${dayNumberClass}">${day.date.getDate()}</span>
					${dayFestiveHtml}
				</div>
				${showHours ? `
				<div class="day-header-right">
					<span class="total-hours">
						${totalHours} h / <span class="day-diff-hours ${diffHours.includes('-') ? 'negative' : 'positive'}">${diffHours} h</span>
					</span>
				</div>` : ''}
			</div>
			<div class="day-content">${eventsHtml}</div>
		`;

		return dayEl;
	}

	/* ---------------- EVENTOS DE FICHAJE ---------------- */

	_renderDayEvents(events) {
		if (!events?.length) return '';

		const sorted = [...events].sort((a, b) => a.date - b.date);
		const tramos = this._groupEventsIntoTramos(sorted);

		return tramos.map(tramo => {
			const start = new Date(tramo.start.date);
			const end   = new Date(tramo.end.date);

			const startStr = `${String(start.getHours()).padStart(2,'0')}:${String(start.getMinutes()).padStart(2,'0')}`;
			const endStr   = `${String(end.getHours()).padStart(2,'0')}:${String(end.getMinutes()).padStart(2,'0')}`;

			const statusMap = { in: 'in', out: 'out', pause: 'pause', current: 'current' };
			const statusClass = statusMap[tramo.start.status] || '';
			const reason = tramo.start.reasonValue || tramo.start.reason || '';

			return `
				<div class="event ${statusClass}">
					<span class="time">${startStr} - ${endStr}</span>
					<span class="reason">${reason}</span>
				</div>`;
		}).join('');
	}

	/* ---------------- EVENTOS DE CONTRATO ---------------- */

	_renderContractFestive(event) {
		return event
			? `<div class="event"><span class="reason festive">${event.description}</span></div>`
			: '';
	}

	_renderContractHoliday() {
		return `<div class="event"><span class="reason holiday">Vacaciones</span></div>`;
	}

	_renderDaysTypeEvent(event) {
		if (!event) return '';
		return `
			<div class="event ${event.source.toLowerCase()}">
				<span class="reason">${this._parseDayTypeDescription(event)}</span>
			</div>`;
	}

	_parseDayTypeDescription(event) {
		switch (event.source) {
			case 'HOLIDAYS':       return 'Vacaciones';
			case 'EFFECITVE_DAYS': return 'Día Efectivo';
			case 'INACTIVITY':     return 'Inactividad: ' + event.description;
			case 'ABSENCE':        return 'Ausencia. '    + this._parsePartiality(event.description);
			case 'STRIKE':         return 'Huelga. '      + this._parsePartiality(event.description);
			case 'ERE':            return 'ERE. '         + this._parsePartiality(event.description);
			case 'ERE_FZA':        return 'ERE Fuerza Mayor. '             + this._parsePartiality(event.description);
			case 'ERE_FZA_EXO':    return 'ERE Fuerza Mayor Exonerado. '   + this._parsePartiality(event.description);
			case 'PAID_LEAVE':     return 'Perm. Retribuido: ' + this._parsePaidLeave(event.description);
			case 'PARTIALITY':     return this._parsePartiality(event.description);
			case 'IT':             return event.description;
			default:               return 'Desconocido';
		}
	}

	_parsePartiality(description) {
		return `Parcialidad: ${description * 100}%`;
	}

	_parsePaidLeave(description) {
		const [, frase, numero] = description.match(/\/\*inherit\*\/(.*?)\/\*\*\/([\d.]+)/);
		return `${frase}. Parcialidad: ${numero * 100}%`;
	}

	/* ---------------- AGRUPACIÓN EN TRAMOS ---------------- */

	_groupEventsIntoTramos(events) {
		const tramos = [];
		let i = 0;

		while (i < events.length) {
			const cur = events[i];

			if (cur.status === 'in') {
				// Avanzar hasta el último IN consecutivo
				let startIndex = i;
				while (i + 1 < events.length && events[i + 1].status === 'in') {
					i++; startIndex = i;
				}
				const startEvent = events[startIndex];
				let endIndex = startIndex + 1;

				if (endIndex < events.length) {
					const endEvent = events[endIndex];
					tramos.push({ start: startEvent, end: endEvent });

					if (endEvent.status === 'pause' && endIndex + 1 < events.length) {
						tramos.push({ start: endEvent, end: events[endIndex + 1] });
					}
					i = endIndex + 1;
				} else {
					tramos.push({ start: { ...startEvent, status: 'current' }, end: this._nowEvent() });
					i = startIndex + 1;
				}

			} else if (cur.status === 'pause') {
				if (i + 1 < events.length) {
					tramos.push({ start: cur, end: events[i + 1] });
					i += 2;
				} else { i++; }

			} else {
				i++;
			}
		}

		// Tramo final si el último evento es PAUSE
		const last = events[events.length - 1];
		if (last?.status === 'pause') {
			tramos.push({ start: { ...last, status: 'current' }, end: this._nowEvent() });
		}

		return tramos;
	}

	_nowEvent() {
		const now = Date.now();
		return { status: 'current', date: now, creation_date: now, cause: 'DEFAULT' };
	}
}