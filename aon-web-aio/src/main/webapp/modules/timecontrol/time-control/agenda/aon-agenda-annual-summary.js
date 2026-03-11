import { AonElement } from "../../../../components/AonElement.js";
import { getTaskHolder } from "../../../../services/taskHolderService.js";
import { getTaskHolderContactEvents, getTaskHolderTimeControl } from "../../../../services/timeControlService.js";
import { AonDateUtils } from "../../../utils/AonDateUtils.js";

export class AonAgendaAnnualSummary extends AonElement {

	_taskHolder = null;
	_year = new Date().getFullYear();
	_events = new Map();            // "YYYY-MM-DD" → { dayData, details }
	_festivesContract = new Map();  // "YYYY-MM-DD" → festivo de contrato
	_daysTypeContract = new Map();  // "YYYY-MM-DD" → tipo de día (HOLIDAYS, IT, etc.)
	_workingDays = [];
	_workingDaysHours = [];
	_annualHolidaysTotal = 0;

	// Sources que tienen horas asociadas (para píldoras)
	_sourcesWithHours = new Set(['EFFECITVE_DAYS', 'PAID_LEAVE', 'PARTIALITY', 'IT', 'HOLIDAYS']);

	async connectedCallback() {
		// Creamos una promesa y guardamos su función "resolve"
		// para poder dispararla manualmente cuando queramos.
		// Es como preparar un pistolín de salida: lo armamos ahora,
		// pero lo disparamos cuando el componente esté listo.
		this._readyResolve = null;
		this.ready = new Promise(resolve => {
			this._readyResolve = resolve;
		});

		await this.buildShell();

		// ¡Ya estamos listos! Disparamos la señal.
		this._readyResolve();
	}

	async buildShell() {
		// ── Nueva distribución de cabecera ──────────────────────────────────
		// Fila superior: Año (izq) | Vacaciones disfrutadas / restantes (drch)
		// Fila inferior: Totales anuales (izq) | Hoy dd/MM horas / diff (drch)
		this.innerHTML = `
      <header>
        <div class="header-left">
          <div class="header-top">
            <button class="year-btn prev-year" aria-label="Año anterior">&#8249;</button>
            <span class="year-label"></span>
            <button class="year-btn next-year" aria-label="Año siguiente">&#8250;</button>
          </div>
          <span class="header-bottom" id="annual-hours"></span>
        </div>
        <div class="header-right">
          <span class="annual-holidays"></span>
          <span class="today-hours"></span>
        </div>
      </header>
      <div class="annual-scroll"></div>
    `;

		this._yearLabel = this.querySelector('.year-label');
		this._annualHours = this.querySelector('#annual-hours');
		this._annualHolidays = this.querySelector('.annual-holidays');
		this._todayHours = this.querySelector('.today-hours');
		this._scrollEl = this.querySelector('.annual-scroll');

		const userTaskHolder = await getTaskHolder();
		if (userTaskHolder) {
			this._taskHolder = userTaskHolder.id;
		}

		this.attachHeaderListeners();
		this.attachSwipeListeners();

		this._scrollEl.addEventListener('click', e => {
			const monthEl = e.target.closest('.annual-month');
			if (!monthEl) return;
			const month = parseInt(monthEl.dataset.month, 10);
			const year = parseInt(monthEl.dataset.year, 10);
			this.dispatchEvent(new CustomEvent('annual-month-click', {
				bubbles: true,
				composed: true,
				detail: { year, month }   // month: 0-11
			}));
		});
	}

	attachHeaderListeners() {
		this.querySelector('.prev-year').addEventListener('click', () => {
			this.loadYear(this._year - 1);
		});
		this.querySelector('.next-year').addEventListener('click', () => {
			this.loadYear(this._year + 1);
		});
	}

	attachSwipeListeners() {
		let startX = 0;
		let startY = 0;
		let decided = false;

		this.addEventListener('touchstart', e => {
			if (e.touches.length !== 1) return;
			startX = e.touches[0].clientX;
			startY = e.touches[0].clientY;
			decided = false;
		}, { passive: true });

		this.addEventListener('touchmove', e => {
			if (decided) return;
			const dx = e.touches[0].clientX - startX;
			const dy = e.touches[0].clientY - startY;
			if (Math.abs(dx) < 8 && Math.abs(dy) < 8) return;
			if (Math.abs(dy) > Math.abs(dx) / 2) { decided = true; return; }
			e.preventDefault();
			decided = true;
		}, { passive: false });

		this.addEventListener('touchend', e => {
			if (e.changedTouches.length !== 1) return;
			const dx = e.changedTouches[0].clientX - startX;
			const dy = e.changedTouches[0].clientY - startY;
			if (Math.abs(dx) <= 60 || Math.abs(dx) <= Math.abs(dy) * 2) return;
			this.loadYear(dx < 0 ? this._year + 1 : this._year - 1);
		}, { passive: true });
	}

	// ── API PÚBLICA ──────────────────────────────────────────────────────────

	async loadYear(year) {
		console.log("loadYear Summary", year, this._taskHolder);

		if (!this._taskHolder) return;

		this._year = year;
		this._yearLabel.textContent = year;

		this._events.clear();
		this._festivesContract.clear();
		this._daysTypeContract.clear();
		this._workingDays = [];
		this._workingDaysHours = [];
		this._scrollEl.innerHTML = '';

		this.startLoading();

		const startDateStr = AonDateUtils.format(new Date(year, 0, 1), 'YYYY-MM-DD');
		const endDateStr = AonDateUtils.format(new Date(year, 11, 31), 'YYYY-MM-DD');

		const filter = {
			group: 'DAY', period: 'personalized',
			startDate: startDateStr, endDate: endDateStr,
			active: true, name: 'Personalizado', value: 'personalized',
			taskHolderId: this._taskHolder, event: 'click', search: ''
		};

		try {
			const [datos, contractDatos] = await Promise.all([
				getTaskHolderTimeControl(filter),
				getTaskHolderContactEvents(filter)
			]);

			this.processEvents(datos);
			this.processContractDaysType(contractDatos.daysType);
			this.processContractFestives(contractDatos.festives);
			this._workingDays = contractDatos.workingDays || [];
			this._workingDaysHours = contractDatos.workingDaysHours || [];
			this._annualHolidaysTotal = contractDatos.annualHolidays || 0;

			this.renderYear();
			this.updateAnnualHeader();

		} catch (err) {
			console.error('❌ Error cargando resumen anual:', err);
		} finally {
			this.stopLoading();
		}
	}

	goToCurrentMonth(smooth) {
		const currentMonth = new Date().getMonth();
		const monthEl = this._scrollEl.querySelector(`[data-month="${currentMonth}"]`);
		if (monthEl) {
			monthEl.scrollIntoView({ behavior: smooth ? 'smooth' : 'auto', block: 'center' });
		}
	}

	// ── PROCESADO DE DATOS ───────────────────────────────────────────────────

	processEvents(datos) {
		if (!Array.isArray(datos)) return;
		datos.forEach(dayData => {
			const dateKey = AonDateUtils.format(new Date(dayData.start_date), 'YYYY-MM-DD');
			if (!this._events.has(dateKey)) {
				this._events.set(dateKey, {
					dayData: { time: dayData.time || 0, status: dayData.status },
					details: dayData.detail || []
				});
			}
		});
	}

	processContractFestives(datos) {
		if (!Array.isArray(datos)) return;
		datos.forEach(dayData => {
			const date = new Date(dayData.start_date);
			const dateKey = AonDateUtils.format(date, 'YYYY-MM-DD');
			if (!this._festivesContract.has(dateKey)) {
				this._festivesContract.set(dateKey, {
					date, dateKey,
					description: dayData.description,
					source: dayData.source
				});
			}
		});
	}

	processContractDaysType(datos) {
		if (!Array.isArray(datos)) return;
		datos.forEach(dayData => {
			const start = new Date(dayData.start_date);
			const end = new Date(dayData.end_date);
			start.setHours(0, 0, 0, 0);
			end.setHours(0, 0, 0, 0);
			for (let cur = new Date(start); cur <= end; cur.setDate(cur.getDate() + 1)) {
				const dateKey = AonDateUtils.format(cur, 'YYYY-MM-DD');
				if (!this._daysTypeContract.has(dateKey)) {
					this._daysTypeContract.set(dateKey, {
						description: dayData.description,
						source: dayData.source
					});
				}
			}
		});
	}

	// ── LÓGICA CENTRAL DE HORAS ──────────────────────────────────────────────
	//
	// Esta función es el corazón de toda la nueva lógica.
	// Dado un día concreto, devuelve cuántas horas "cuentan" para ese día.
	//
	// Reglas:
	//   1. Si es festivo de contrato → 0 ms (los festivos no cuentan nunca)
	//   2. Si tiene source HOLIDAYS, PAID_LEAVE o IT
	//      → horas esperadas del contrato + horas fichadas ese día (si las hay)
	//   3. Cualquier otro día → solo horas fichadas
	//
	// Devuelve un objeto { workedMs, expectedMs } donde:
	//   - workedMs   = lo que "ha trabajado" (ya con las reglas aplicadas)
	//   - expectedMs = lo que se esperaba que trabajase ese día
	//
	getDayWorkedMs(dateKey, dayOfWeek) {
		const msPerHour = 1000 * 60 * 60;

		// Horas esperadas según el contrato para ese día de la semana
		const hoursForDay = this._workingDaysHours.length === 7
			? (this._workingDaysHours[dayOfWeek] || 0) : 0;
		const expectedMs = hoursForDay * msPerHour;

		// Horas reales fichadas ese día (0 si no hay ningún fichaje)
		const eventData = this._events.get(dateKey);
		const fichadasMs = eventData ? Number(eventData.dayData.time || 0) : 0;

		// Regla 1: festivo de contrato → todo a 0
		const festive = this._festivesContract.get(dateKey);
		if (festive) {
			return { workedMs: 0, expectedMs: 0 };
		}

		// Regla 2: HOLIDAYS, PAID_LEAVE o IT → esperadas + fichadas
		const dayType = this._daysTypeContract.get(dateKey);
		const src = dayType?.source;
		if (src === 'HOLIDAYS' || src === 'PAID_LEAVE' || src === 'IT') {
			return { workedMs: expectedMs + fichadasMs, expectedMs };
		}

		// Regla 3: día normal → solo fichadas
		return { workedMs: fichadasMs, expectedMs };
	}

	// ── RENDERIZADO ──────────────────────────────────────────────────────────

	renderYear() {
		const fragment = document.createDocumentFragment();
		for (let m = 0; m < 12; m++) {
			fragment.appendChild(this.renderMonth(this._year, m));
		}
		this._scrollEl.appendChild(fragment);
	}

	renderMonth(year, monthIndex) {
		const firstDay = new Date(year, monthIndex, 1);
		const lastDay = new Date(year, monthIndex + 1, 0);
		const monthName = AonDateUtils.monthName(firstDay);
		const stats = this.calculateMonthStats(firstDay, lastDay);

		const el = document.createElement('section');
		const today = new Date();
		const isCurrentMonth = year === today.getFullYear() && monthIndex === today.getMonth();
		el.className = `annual-month${isCurrentMonth ? ' current' : ''}`;
		el.dataset.month = monthIndex;
		el.dataset.year = year;

		// Horas del mes en la cabecera del mes (derecha)
		const workedStr = this.msToHoursMinutes(stats.workedMs);
		const expectedStr = this.msToHoursMinutes(stats.expectedMs);
		const diffMs = stats.workedMs - stats.expectedMs;
		const diffStr = this.formatDiffTime(diffMs);
		const diffClass = diffMs < 0 ? 'negative' : 'positive';

		el.innerHTML = `
      <div class="annual-month-header">
      	<div class="annual-month-day">
        	<span class="annual-month-name">${monthName.toUpperCase()}</span>
        	<span class="annual-month-total-hours">${expectedStr}</span>
        </div>
        <span class="annual-month-hours">
          ${workedStr} h / <span class="day-diff-hours ${diffClass}">${diffStr} h</span>
        </span>
      </div>
      <div class="annual-month-pills">
        ${this.renderAllPills(stats)}
      </div>
    `;

		return el;
	}

	// Calcula estadísticas de un mes completo.
	// Usa getDayWorkedMs para aplicar la lógica centralizada.
	calculateMonthStats(firstDay, lastDay) {
		let workedMs = 0;
		let expectedMs = 0;
		let workedDays = 0;

		// Mapa: source → { count, workedMs, expectedMs }
		const bySource = new Map();
		const festivesOfMonth = [];

		for (let d = new Date(firstDay); d <= lastDay; d.setDate(d.getDate() + 1)) {
			const day = new Date(d);
			const dateKey = AonDateUtils.format(day, 'YYYY-MM-DD');
			const dow = day.getDay();

			const { workedMs: dayWorked, expectedMs: dayExpected } = this.getDayWorkedMs(dateKey, dow);

			workedMs += dayWorked;
			expectedMs += dayExpected;
			if (dayWorked > 0) workedDays++;

			// Acumular por source para las píldoras
			const dayType = this._daysTypeContract.get(dateKey);
			if (dayType?.source) {
				const src = dayType.source;
				if (!bySource.has(src)) bySource.set(src, { count: 0, workedMs: 0, expectedMs: 0 });
				const entry = bySource.get(src);
				entry.count++;
				entry.expectedMs += dayExpected;
				if (this._sourcesWithHours.has(src)) entry.workedMs += dayWorked;
			}

			const festive = this._festivesContract.get(dateKey);
			if (festive) festivesOfMonth.push(festive);
		}

		return { workedMs, expectedMs, workedDays, bySource, festivesOfMonth };
	}

	renderAllPills({ workedMs, expectedMs, workedDays, bySource, festivesOfMonth }) {
		const pills = [];

		// ── Píldora por cada SOURCE presente ──
		const sourceOrder = [
			'EFFECITVE_DAYS', 'PAID_LEAVE', 'IT', 'HOLIDAYS',
			'INACTIVITY', 'ABSENCE', 'STRIKE', 'ERE',
			'ERE_FZA', 'ERE_FZA_EXO', 'PARTIALITY'
		];

		// ── Píldora principal: días trabajados + horas totales ──ﬁ
		const workedStr = this.msToHoursMinutes(workedMs);
		const diffMs = workedMs - expectedMs;
		const diffStr = this.formatDiffTime(diffMs);
		const diffClass = diffMs < 0 ? 'negative' : 'positive';

		let workedMsCopy = workedMs;
		let workedDaysCopy = workedDays;
		for (const src of sourceOrder) {
			if (!bySource.has(src)) continue;
			const { count, workedMs: srcWorked, expectedMs: srcExpected } = bySource.get(src);
			const hasHours = this._sourcesWithHours.has(src);
			if (!hasHours) continue;
			workedMsCopy -= srcWorked;
			workedDaysCopy -= count;
		}
		let workedCopyStr = this.msToHoursMinutes(workedMsCopy);

		if (workedDays > 0)
			pills.push(`
	      <span class="day-type-pill pill-worked">
	        <span class="pill-left"><strong>${workedDaysCopy} día${workedDaysCopy !== 1 ? 's' : ''}</strong> - Trabajados</span>
	        <span class="pill-right">${workedCopyStr} h</span>
	      </span>
	    `);

		for (const src of sourceOrder) {
			if (!bySource.has(src)) continue;
			const { count, workedMs: srcWorked, expectedMs: srcExpected } = bySource.get(src);
			const label = this.getSourceLabel(src);
			const hasHours = this._sourcesWithHours.has(src);
			const cssClass = src.toLowerCase().replace(/_/g, '-');

			let rightHtml = '';
			if (hasHours) {
				const srcWorkedStr = this.msToHoursMinutes(srcWorked);
				const srcDiffMs = srcWorked - srcExpected;
				const srcDiff = this.formatDiffTime(srcDiffMs);
				const srcDiffCls = srcDiffMs < 0 ? 'negative' : 'positive';
				rightHtml = `${srcWorkedStr} h`;
				// rightHtml = `${srcWorkedStr} h / <span class="day-diff-hours ${srcDiffCls}">${srcDiff} h</span>`;
			}

			pills.push(`
        <span class="day-type-pill ${cssClass}">
          <span class="pill-left"><strong>${count} día${count !== 1 ? 's' : ''}</strong> - ${label}</span>
          ${rightHtml ? `<span class="pill-right">${rightHtml}</span>` : ''}
        </span>
      `);
		}

		// ── Festivos de contrato ──
		if (festivesOfMonth.length > 0) {
			pills.push(`
        <span class="day-type-pill festive">
          <span class="pill-left"><strong>${festivesOfMonth.length} día${festivesOfMonth.length !== 1 ? 's' : ''}</strong> - Festivos</span>
        </span>
      `);
		}

		return pills.join('');
	}

	// ── CABECERA ANUAL ───────────────────────────────────────────────────────
	updateAnnualHeader() {
		const today = new Date();
		today.setHours(0, 0, 0, 0);

		let totalWorkedMs = 0;
		let totalExpectedMs = 0;
		let totalHolidays = 0;

		// Acumulados solo hasta hoy (inclusive)
		let uptodayWorkedMs = 0;
		let uptodayExpectedMs = 0;

		const firstDay = new Date(this._year, 0, 1);
		const lastDay = new Date(this._year, 11, 31);

		for (let d = new Date(firstDay); d <= lastDay; d.setDate(d.getDate() + 1)) {
			const day = new Date(d);
			const dateKey = AonDateUtils.format(day, 'YYYY-MM-DD');
			const dow = day.getDay();

			const { workedMs, expectedMs } = this.getDayWorkedMs(dateKey, dow);

			totalWorkedMs += workedMs;
			totalExpectedMs += expectedMs;

			// Contar vacaciones (HOLIDAYS)
			const dayType = this._daysTypeContract.get(dateKey);
			if (dayType?.source === 'HOLIDAYS') totalHolidays++;

			// Acumular "hasta hoy" solo si el día ya ha pasado (o es hoy)
			if (day <= today) {
				uptodayWorkedMs += workedMs;
				uptodayExpectedMs += expectedMs;
			}
		}

		// ── Totales anuales ──
		const totalExpectedStr = this.msToHoursMinutes(totalExpectedMs);

		// ── Vacaciones: disfrutadas / restantes ──
		const remaining = this._annualHolidaysTotal - totalHolidays;
		const remSign = remaining < 0 ? '' : '+'; // si es negativo ya lleva el '-'
		this._annualHolidays.innerHTML =
			`Vacaciones: ${totalHolidays} / <span class="day-diff-hours ${remaining < 0 ? 'negative' : 'positive'}">${remSign}${remaining} días</span>`;

		this._annualHours.innerHTML =
			`Cal.: ${totalExpectedStr} h - ${totalHolidays + remaining} d Vac.`;

		// ── Hoy: fecha + horas trabajadas hasta hoy / diferencia hasta hoy ──
		const uptodayWorkedStr = this.msToHoursMinutes(uptodayWorkedMs);
		const uptodayDiffMs = uptodayWorkedMs - uptodayExpectedMs;
		const uptodayDiffStr = this.formatDiffTime(uptodayDiffMs);
		const uptodayDiffClass = uptodayDiffMs < 0 ? 'negative' : 'positive';

		this._todayHours.innerHTML =
			`Acum.: ${uptodayWorkedStr} h / <span class="day-diff-hours ${uptodayDiffClass}">${uptodayDiffStr} h</span>`;
	}

	// ── HELPERS ──────────────────────────────────────────────────────────────

	getSourceLabel(source) {
		const labels = {
			HOLIDAYS: 'Vacaciones',
			EFFECITVE_DAYS: 'Día Efectivo',
			INACTIVITY: 'Inactividad',
			ABSENCE: 'Ausencia',
			STRIKE: 'Huelga',
			ERE: 'ERE',
			ERE_FZA: 'ERE F.M.',
			ERE_FZA_EXO: 'ERE F.M. Exo.',
			PAID_LEAVE: 'Perm. Retribuido',
			PARTIALITY: 'Parcialidad',
			IT: 'IT',
		};
		return labels[source] || source;
	}

	formatDiffTime(ms) {
		const sign = ms < 0 ? '-' : '+';
		const absStr = this.msToHoursMinutes(Math.abs(ms));
		return `${sign}${absStr}`;
	}

	msToHoursMinutes(ms) {
		const totalSeconds = Math.floor(ms / 1000);
		const hours = Math.floor(totalSeconds / 3600);
		const minutes = Math.floor((totalSeconds % 3600) / 60);
		return `${this.formatThousands(hours)}:${String(minutes).padStart(2, '0')}`;
	}

	formatThousands(n) {
		return n.toString().replace(/\B(?=(\d{3})+(?!\d))/g, '.');
	}

	startLoading() {
		const el = this.getElement('aonModuleLoader');
		if (el) el.startLoading();
	}

	stopLoading() {
		const el = this.getElement('aonModuleLoader');
		if (el) el.stopLoading();
	}
}

window.customElements.define('aon-agenda-annual-summary', AonAgendaAnnualSummary);