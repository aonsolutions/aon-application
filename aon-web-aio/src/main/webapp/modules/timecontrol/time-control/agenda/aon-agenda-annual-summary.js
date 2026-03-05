import { AonElement } from "../../../../components/AonElement.js";
import { getTaskHolder } from "../../../../services/taskHolderService.js";
import { getTaskHolderContactEvents, getTaskHolderTimeControl } from "../../../../services/timeControlService.js";
import { AonDateUtils } from "../../../utils/AonDateUtils.js";

export class AonAgendaAnnualSummary extends AonElement {

	_taskHolder = null;
	_year = new Date().getFullYear();
	_events = new Map(); // "YYYY-MM-DD" → { dayData, details }
	_festivesContract = new Map(); // "YYYY-MM-DD" → festivo
	_daysTypeContract = new Map(); // "YYYY-MM-DD" → tipo de día
	_workingDays = [];
	_workingDaysHours = [];
	_annualHolidaysTotal = 0;

	// Sources que tienen horas asociadas
	_sourcesWithHours = new Set(['EFFECITVE_DAYS', 'PAID_LEAVE', 'PARTIALITY', 'IT']);

	async connectedCallback() {
		await this.buildShell();
	}

	async buildShell() {
		this.innerHTML = `
      <header class="annual-header">
        <div class="header-row">
          <div class="year-nav">
            <button class="year-btn prev-year" aria-label="Año anterior">&#8249;</button>
            <span class="year-label"></span>
            <button class="year-btn next-year" aria-label="Año siguiente">&#8250;</button>
          </div>
          <span class="annual-hours"></span>
        </div>
        <div class="header-row header-row-right">
          <span class="today-hours"></span>
          <span class="annual-holidays"></span>
        </div>
      </header>
      <div class="annual-scroll"></div>
    `;

		this._yearLabel = this.querySelector('.year-label');
		this._annualHours = this.querySelector('.annual-hours');
		this._annualHolidays = this.querySelector('.annual-holidays');
		this._todayHours = this.querySelector('.today-hours');
		this._scrollEl = this.querySelector('.annual-scroll');

		const userTaskHolder = await getTaskHolder();
		if (userTaskHolder) {
			this._taskHolder = userTaskHolder.id;
		}

		this.attachHeaderListeners();
		this.attachSwipeListeners();
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

	// ---- API PÚBLICA ----

	async loadYear(year) {
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
			group: 'DAY',
			period: 'personalized',
			startDate: startDateStr,
			endDate: endDateStr,
			active: true,
			name: 'Personalizado',
			value: 'personalized',
			taskHolderId: this._taskHolder,
			event: 'click',
			search: ''
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

	// ---- Procesado de datos ----

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
					date,
					dateKey,
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

	// ---- Renderizado ----

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

		el.innerHTML = `
      <div class="annual-month-header">
        <span class="annual-month-name">${monthName.toUpperCase()}</span>
      </div>
      <div class="annual-month-pills">
        ${this.renderAllPills(stats)}
      </div>
    `;

		return el;
	}

	calculateMonthStats(firstDay, lastDay) {
		const msPerHour = 1000 * 60 * 60;
		let workedMs = 0;
		let expectedMs = 0;
		let workedDays = 0;

		// Map: source → { count, workedMs, expectedMs }
		const bySource = new Map();
		const festivesOfMonth = [];

		for (let d = new Date(firstDay); d <= lastDay; d.setDate(d.getDate() + 1)) {
			const day = new Date(d);
			const dateKey = AonDateUtils.format(day, 'YYYY-MM-DD');
			const dayOfWeek = day.getDay();

			const hoursForDay = this._workingDaysHours.length === 7
				? (this._workingDaysHours[dayOfWeek] || 0) : 0;
			const dayExpectedMs = hoursForDay * msPerHour;

			const eventData = this._events.get(dateKey);
			let dayWorkedMs = eventData ? Number(eventData.dayData.time || 0) : 0;

			const dayType = this._daysTypeContract.get(dateKey);
			if (dayType?.source) {
				const src = dayType.source;

				// IT y PAID_LEAVE cuentan como horas esperadas a efectos de trabajado
				if (src === 'IT' || src === 'PAID_LEAVE') {
					dayWorkedMs = dayExpectedMs;
				}

				if (!bySource.has(src)) {
					bySource.set(src, { count: 0, workedMs: 0, expectedMs: 0 });
				}
				const entry = bySource.get(src);
				entry.count++;
				entry.expectedMs += dayExpectedMs;
				// Solo acumulamos horas en bySource para sources que las tienen
				if (this._sourcesWithHours.has(src)) {
					entry.workedMs += dayWorkedMs;
				}
			}

			const festive = this._festivesContract.get(dateKey);
			if (festive) festivesOfMonth.push(festive);

			// Días trabajados: días con tiempo registrado y sin source especial
			if (dayWorkedMs > 0) workedDays++;

			workedMs += dayWorkedMs;
			expectedMs += dayExpectedMs;
		}

		return { workedMs, expectedMs, workedDays, bySource, festivesOfMonth };
	}

	renderAllPills({ workedMs, expectedMs, workedDays, bySource, festivesOfMonth }) {
		const pills = [];

		// ── Píldora principal: Trabajados ──
		const workedStr = this.msToHoursMinutes(workedMs);
		const diffMs = workedMs - expectedMs;
		const diffStr = this.formatDiffTime(diffMs);
		const diffClass = diffMs < 0 ? 'negative' : 'positive';
		pills.push(`
      <span class="day-type-pill pill-worked">
        <span class="pill-left">Trabajados: <strong>${workedDays} día${workedDays !== 1 ? 's' : ''}</strong></span>
        <span class="pill-right">${workedStr} h / <span class="day-diff-hours ${diffClass}">${diffStr} h</span></span>
      </span>
    `);

		// ── Píldora por cada SOURCE presente ──
		const sourceOrder = [
			'EFFECITVE_DAYS', 'PAID_LEAVE', 'IT', 'HOLIDAYS',
			'INACTIVITY', 'ABSENCE', 'STRIKE', 'ERE',
			'ERE_FZA', 'ERE_FZA_EXO', 'PARTIALITY'
		];

		for (const src of sourceOrder) {
			if (!bySource.has(src)) continue;
			const { count, workedMs: srcWorkedMs, expectedMs: srcExpectedMs } = bySource.get(src);
			const label = this.getSourceLabel(src);
			const hasHours = this._sourcesWithHours.has(src);
			const cssClass = src.toLowerCase().replace(/_/g, '-');

			let rightHtml = '';
			if (hasHours) {
				const srcWorked = this.msToHoursMinutes(srcWorkedMs);
				const srcDiffMs = srcWorkedMs - srcExpectedMs;
				const srcDiff = this.formatDiffTime(srcDiffMs);
				const srcDiffCls = srcDiffMs < 0 ? 'negative' : 'positive';
				rightHtml = `${srcWorked} h / <span class="day-diff-hours ${srcDiffCls}">${srcDiff} h</span>`;
			}

			pills.push(`
        <span class="day-type-pill ${cssClass}">
          <span class="pill-left">${label}: <strong>${count} día${count !== 1 ? 's' : ''}</strong></span>
          ${rightHtml ? `<span class="pill-right">${rightHtml}</span>` : ''}
        </span>
      `);
		}

		// ── Festivos ──
		if (festivesOfMonth.length > 0) {
			const count = festivesOfMonth.length;
			pills.push(`
        <span class="day-type-pill festive">
          <span class="pill-left">Festivos: <strong>${count} día${count !== 1 ? 's' : ''}</strong></span>
        </span>
      `);
		}

		return pills.join('');
	}

	// ---- Totales anuales para la cabecera ----

	updateAnnualHeader() {
		const msPerHour = 1000 * 60 * 60;
		const todayKey = AonDateUtils.format(new Date(), 'YYYY-MM-DD');
		let totalWorkedMs = 0;
		let totalExpectedMs = 0;
		let totalHolidays = 0;

		const firstDay = new Date(this._year, 0, 1);
		const lastDay = new Date(this._year, 11, 31);

		for (let d = new Date(firstDay); d <= lastDay; d.setDate(d.getDate() + 1)) {
			const day = new Date(d);
			const dateKey = AonDateUtils.format(day, 'YYYY-MM-DD');
			const dayOfWeek = day.getDay();

			const hoursForDay = this._workingDaysHours.length === 7
				? (this._workingDaysHours[dayOfWeek] || 0) : 0;
			const dayExpectedMs = hoursForDay * msPerHour;

			const eventData = this._events.get(dateKey);
			let dayWorkedMs = eventData ? Number(eventData.dayData.time || 0) : 0;

			const dayType = this._daysTypeContract.get(dateKey);
			if (dayType?.source === 'IT' || dayType?.source === 'PAID_LEAVE') {
				dayWorkedMs = dayExpectedMs;
			}
			if (dayType?.source === 'HOLIDAYS') totalHolidays++;

			totalWorkedMs += dayWorkedMs;
			totalExpectedMs += dayExpectedMs;
		}

		// Totales anuales
		const workedStr = this.msToHoursMinutes(totalWorkedMs);
		const diffMs = totalWorkedMs - totalExpectedMs;
		const diffStr = this.formatDiffTime(diffMs);
		const diffClass = diffMs < 0 ? 'negative' : 'positive';

		this._annualHours.innerHTML =
			`Año: ${workedStr} h / <span class="day-diff-hours ${diffClass}">${diffStr} h</span>`;
		this._annualHolidays.textContent =
			`Vacaciones: ${totalHolidays} / ${this._annualHolidaysTotal} días`;

		// Hoy
		const todayEvent = this._events.get(todayKey);
		const todayWorkedMs = todayEvent ? Number(todayEvent.dayData.time || 0) : 0;
		const todayDow = new Date().getDay();
		const todayExpectedMs = this._workingDaysHours.length === 7
			? (this._workingDaysHours[todayDow] || 0) * msPerHour : 0;
		const todayDiffMs = todayWorkedMs - todayExpectedMs;
		const todayDiffCls = todayDiffMs < 0 ? 'negative' : 'positive';

		this._todayHours.innerHTML =
			`Hoy: ${this.msToHoursMinutes(todayWorkedMs)} h / <span class="day-diff-hours ${todayDiffCls}">${this.formatDiffTime(todayDiffMs)} h</span>`;
	}

	// ---- Helpers ----

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
		//const seconds = totalSeconds % 60;

		return `${this.formatThousands(hours)}:${String(minutes).padStart(2, '0')}`;
	}
	
	formatThousands(n) {
	    return n.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ".");
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