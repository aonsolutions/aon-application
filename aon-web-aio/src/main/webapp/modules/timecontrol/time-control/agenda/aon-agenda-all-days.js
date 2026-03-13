import { AonElement } from "../../../../components/AonElement.js";
import { getTaskHolder } from "../../../../services/taskHolderService.js";
import { AonDateUtils } from "../../../utils/AonDateUtils.js";
import { AgendaHoursCalculator } from "./components/AgendaHoursCalculator.js";
import { AgendaRenderer } from "./components/AgendaRenderer.js";
import { AgendaDataService } from "./components/AgendaDataService.js";

export class AonAgendaAllDays extends AonElement {

	// Control de meses cargados (límites para el scroll infinito)
	_firstLoadedMonth = null;
	_lastLoadedMonth = null;

	// Flags de control de scroll
	_initialLoadComplete = false;
	_scrollLocked = false;
	_lastScrollPosition = 0;

	// Botón flotante "Hoy"
	_todayBtnEnabled = false;

	async connectedCallback() {
		this._initState();
		await this._build();
		this._renderInitial();
		this._attachEventListeners();

		await this._loadInitialEvents();

		setTimeout(() => this.scrollToToday(), 200);
		setTimeout(() => {
			this._initialLoadComplete = true;
			console.log('✅ Scroll infinito activado');
		}, 1000);
	}

	/* ═══════════════════════════════════════════════
	   ESTADO E INICIALIZACIÓN
	═══════════════════════════════════════════════ */

	_initState() {
		const today = new Date();
		this.baseDate = AonDateUtils.startOfWeek(today);
		this.weekHeight = 0;
		this.visibleWeeks = new Map(); // offset → { element, week }
		this.renderRange = 6;
	}

	async _build() {
		this.innerHTML = `
			<header>
				<div class="header-left">
					<div class="header-top"><span class="month"></span></div>
					<div class="header-bottom">
						<span class="week-label">Sem.</span>
						<span class="range"></span>
					</div>
				</div>
				<div class="header-right">
					<span class="month-hours"></span>
					<span class="week-hours"></span>
				</div>
			</header>
			<div class="scroll"></div>
		`;

		this.headerMonth = this.querySelector(".month");
		this.headerMonthHours = this.querySelector(".month-hours");
		this.headerRange = this.querySelector(".range");
		this.headerWeekHours = this.querySelector(".week-hours");
		this.scrollEl = this.querySelector(".scroll");

		// Botón flotante "Hoy"
		this.todayBtn = document.createElement('button');
		this.todayBtn.className = 'today-floating-btn';
		this.todayBtn.textContent = '↓ Hoy';
		this.todayBtn.style.display = 'none';
		this.todayBtn.addEventListener('click', () => this.scrollToToday(true));
		this.insertBefore(this.todayBtn, this.scrollEl);

		// Cargar el taskHolder e instanciar los servicios
		const userTaskHolder = await getTaskHolder();
		const taskHolderId = userTaskHolder?.id || null;

		this._data = new AgendaDataService(taskHolderId);
		this._hours = new AgendaHoursCalculator(this._data);
		this._renderer = new AgendaRenderer(this._data, this._hours);

		if (userTaskHolder) {
			this._taskHolderName = userTaskHolder.name;
		}
	}

	/* ═══════════════════════════════════════════════
	   CARGA DE EVENTOS
	═══════════════════════════════════════════════ */

	async _loadInitialEvents() {
		if (!this._data.taskHolder) return;

		const today = new Date();
		const prevMonth = new Date(today.getFullYear(), today.getMonth() - 1, 1);
		const nextMonth = new Date(today.getFullYear(), today.getMonth() + 1, 1);

		this._firstLoadedMonth = prevMonth;
		this._lastLoadedMonth = nextMonth;

		await this._loadMonthEvents(prevMonth);
		await this._loadMonthEvents(today);
		await this._loadMonthEvents(nextMonth);
	}

	async _loadMonthEvents(date) {
		if (!this._data.taskHolder || this._data.isLoading) return;

		this.startLoading();
		try {
			const loaded = await this._data.loadMonth(date);
			if (!loaded) return; // ya estaba cargado

			const startDate = new Date(date.getFullYear(), date.getMonth(), 1);
			this._addWeeksForMonth(startDate);
			this._refreshWeeks();
		} catch (err) {
			console.error(`❌ Error cargando mes:`, err);
		} finally {
			this.stopLoading();
		}
	}

	async loadPreviousMonth() {
		if (!this._firstLoadedMonth) return;

		const prevMonth = new Date(this._firstLoadedMonth.getFullYear(), this._firstLoadedMonth.getMonth() - 1, 1);
		const key = this._data.monthKey(prevMonth);
		if (this._data.loadedMonths.has(key)) return;

		const beforeHeight = this.scrollEl.scrollHeight;
		const beforeScroll = this.scrollEl.scrollTop;

		await this._loadMonthEvents(prevMonth);
		this._firstLoadedMonth = prevMonth;

		requestAnimationFrame(() => {
			const diff = this.scrollEl.scrollHeight - beforeHeight;
			this.scrollEl.scrollTop = beforeScroll + diff;
		});
	}

	async loadNextMonth() {
		if (!this._lastLoadedMonth) return;

		const nextMonth = new Date(this._lastLoadedMonth.getFullYear(), this._lastLoadedMonth.getMonth() + 1, 1);
		const key = this._data.monthKey(nextMonth);
		if (this._data.loadedMonths.has(key)) return;

		await this._loadMonthEvents(nextMonth);
		this._lastLoadedMonth = nextMonth;
	}

	/* ═══════════════════════════════════════════════
	   GESTIÓN DE SEMANAS EN EL DOM
	═══════════════════════════════════════════════ */

	_addWeeksForMonth(monthStartDate) {
		const firstDay = new Date(monthStartDate.getFullYear(), monthStartDate.getMonth(), 1);
		const lastDay = new Date(monthStartDate.getFullYear(), monthStartDate.getMonth() + 1, 0);
		const firstWeekStart = AonDateUtils.startOfWeek(firstDay);
		const lastWeekStart = AonDateUtils.startOfWeek(lastDay);
		const msPerWeek = 1000 * 60 * 60 * 24 * 7;

		const firstOffset = Math.round((firstWeekStart - this.baseDate) / msPerWeek);
		const lastOffset = Math.round((lastWeekStart - this.baseDate) / msPerWeek);

		for (let offset = firstOffset; offset <= lastOffset; offset++) {
			if (!this.visibleWeeks.has(offset)) this._mountWeek(offset);
		}
	}

	_mountWeek(offset) {
		if (this.visibleWeeks.has(offset)) return;

		const week = this._createWeek(offset);
		const weekEl = this._renderer.renderWeek(week);
		this.visibleWeeks.set(offset, { element: weekEl, week });

		// Insertar en la posición DOM correcta (orden cronológico)
		const sortedOffsets = Array.from(this.visibleWeeks.keys()).sort((a, b) => a - b);
		const idx = sortedOffsets.indexOf(offset);

		if (sortedOffsets.length === 1) {
			this.scrollEl.appendChild(weekEl);
		} else if (idx === 0) {
			this.scrollEl.insertBefore(weekEl, this.scrollEl.firstChild);
		} else if (idx === sortedOffsets.length - 1) {
			this.scrollEl.appendChild(weekEl);
		} else {
			const nextData = this.visibleWeeks.get(sortedOffsets[idx + 1]);
			this.scrollEl.insertBefore(weekEl, nextData?.element ?? null);
		}
	}

	_createWeek(offset) {
		const start = new Date(this.baseDate);
		start.setDate(start.getDate() + offset * 7);

		const days = Array.from({ length: 7 }, (_, i) => {
			const date = new Date(start);
			date.setDate(start.getDate() + i);
			return { date, key: AonDateUtils.format(date, "YYYY-MM-DD") };
		});

		return { offset, start, days };
	}

	_refreshWeeks() {
		const scrollTop = this.scrollEl.scrollTop;

		this.visibleWeeks.forEach((data, offset) => {
			const newEl = this._renderer.renderWeek(data.week);
			data.element.replaceWith(newEl);
			this.visibleWeeks.set(offset, { element: newEl, week: data.week });
		});

		this.scrollEl.scrollTop = scrollTop;
	}

	_renderInitial() {
		for (let i = -this.renderRange; i <= 4; i++) this._mountWeek(i);
		setTimeout(() => this._measureWeekHeight(), 100);
	}

	_measureWeekHeight() {
		const first = this.scrollEl.querySelector(".week");
		if (first) this.weekHeight = first.offsetHeight;
	}

	/* ═══════════════════════════════════════════════
	   SCROLL Y NAVEGACIÓN
	═══════════════════════════════════════════════ */

	scrollToToday(smooth = false) {
		let todayEl = null;
		this.visibleWeeks.forEach(({ element }) => {
			if (!todayEl) todayEl = element.querySelector('.day.today');
		});

		const target = todayEl ?? this.visibleWeeks.get(0)?.element;
		target?.scrollIntoView({ behavior: smooth ? 'smooth' : 'auto', block: 'center' });

		setTimeout(() => this._updateHeader(), 500);
	}

	/*
	goToDate(date) {
		const targetStart = AonDateUtils.startOfWeek(date);
		const msPerWeek = 1000 * 60 * 60 * 24 * 7;
		const offset = Math.floor((targetStart - this.baseDate) / msPerWeek);

		this._mountWeek(offset);

		requestAnimationFrame(() => {
			const weekData = this.visibleWeeks.get(offset);
			if (!weekData) return;

			const targetKey = AonDateUtils.format(date, 'YYYY-MM-DD');
			const dayEls = weekData.element.querySelectorAll('.day');
			let targetDayEl = null;

			dayEls.forEach((dayEl, i) => {
				const d = new Date(targetStart);
				d.setDate(d.getDate() + i);
				console.log('targetKey', targetKey, AonDateUtils.format(d, 'YYYY-MM-DD') === targetKey);
				if (AonDateUtils.format(d, 'YYYY-MM-DD') === targetKey) targetDayEl = dayEl;
				console.log('targetDayEl', targetDayEl);
			});

			(targetDayEl ?? weekData.element).scrollIntoView({ behavior: 'smooth', block: 'start' });
		});
	}
	*/

	goToDate(date) {
		const targetStart = AonDateUtils.startOfWeek(date);

		// Calculamos el offset en DÍAS usando UTC para evitar que el cambio
		// de hora de verano (DST) haga que una semana "dure" 167h en vez de 168h
		// y el Math.floor redondee al offset incorrecto.
		const msPerDay = 1000 * 60 * 60 * 24;
		const diffDays = Math.round((targetStart - this.baseDate) / msPerDay);
		const offset = Math.floor(diffDays / 7);

		// Math.round en vez de Math.floor para los días: si el DST roba 1h,
		// 6.958 días se redondea a 7 en vez de truncarse a 6.

		this._mountWeek(offset);

		requestAnimationFrame(() => {
			const weekData = this.visibleWeeks.get(offset);
			if (!weekData) return;

			const targetKey = AonDateUtils.format(date, 'YYYY-MM-DD');
			const dayEls = Array.from(weekData.element.querySelectorAll('.day'));

			const targetDayEl = dayEls.find((dayEl, i) => {
				const d = new Date(targetStart);
				d.setDate(d.getDate() + i);
				return AonDateUtils.format(d, 'YYYY-MM-DD') === targetKey;
			});

			(targetDayEl ?? weekData.element).scrollIntoView({ behavior: 'smooth', block: 'start' });
		});
	}

	async goToMonth(year, month) {
		const target = new Date(year, month, 1);
		const monthKey = `${year}-${String(month + 1).padStart(2, '0')}`;

		if (!this._data.loadedMonths.has(monthKey)) {
			await this._loadMonthEvents(target);
			if (!this._firstLoadedMonth || target < this._firstLoadedMonth) this._firstLoadedMonth = target;
			if (!this._lastLoadedMonth || target > this._lastLoadedMonth) this._lastLoadedMonth = target;
		}

		this.goToDate(target);
	}

	/* ═══════════════════════════════════════════════
	   SCROLL INFINITO
	═══════════════════════════════════════════════ */

	_attachEventListeners() {
		let checkTimeout;

		this.scrollEl.addEventListener("scroll", (e) => {
			this._checkTodayVisible();

			if (this._scrollLocked) {
				e.preventDefault();
				this.scrollEl.scrollTop = this._lastScrollPosition;
				return;
			}

			this._lastScrollPosition = this.scrollEl.scrollTop;
			this._updateHeader();
			this._checkLoadMore();

			clearTimeout(checkTimeout);
			checkTimeout = setTimeout(() => this._checkLoadMonthsOnScroll(), 50);
		}, { passive: false });

		setTimeout(() => this._measureWeekHeight(), 100);
	}

	_checkLoadMore() {
		const { scrollTop, scrollHeight, clientHeight } = this.scrollEl;
		const distanceFromBottom = scrollHeight - scrollTop - clientHeight;

		if (distanceFromBottom < clientHeight * 2) {
			const maxOffset = Math.max(...this.visibleWeeks.keys());
			if (maxOffset < 4) {
				for (let i = maxOffset + 1; i <= Math.min(maxOffset + 10, 4); i++) {
					this._mountWeek(i);
				}
			}
		}
	}

	async _checkLoadMonthsOnScroll() {
		if (!this._initialLoadComplete || this._data.isLoading || this._scrollLocked) return;

		const { scrollTop, scrollHeight, clientHeight } = this.scrollEl;
		const threshold = clientHeight * 0.2;

		if (scrollTop < threshold) await this.loadPreviousMonth();
		if (scrollHeight - scrollTop - clientHeight < threshold) await this.loadNextMonth();
	}

	/* ═══════════════════════════════════════════════
	   HEADER
	═══════════════════════════════════════════════ */

	_updateHeader() {
		if (!this.weekHeight) this._measureWeekHeight();

		const scrollMiddle = this.scrollEl.scrollTop + this.scrollEl.clientHeight / 2;
		let centerWeek = null, minDist = Infinity;

		this.visibleWeeks.forEach(({ element, week: w }) => {
			const mid = element.offsetTop + element.offsetHeight / 2;
			const dist = Math.abs(mid - scrollMiddle);
			if (dist < minDist) { minDist = dist; centerWeek = w; }
		});

		if (!centerWeek) return;

		const monthName = AonDateUtils.monthName(centerWeek.start);
		const year = centerWeek.start.getFullYear().toString().slice(-2);
		this.headerMonth.textContent = `${monthName.toUpperCase()} ${year}`;

		this.headerMonthHours.innerHTML = `Mes: ${this._hours.calculateMonthHours(centerWeek.start)}`;
		this.headerWeekHours.innerHTML = `Sem.: ${this._hours.calculateWeekHours(centerWeek)}`;

		const [first, last] = [centerWeek.days[0].date, centerWeek.days[6].date];
		const fmt = d => `${String(d.getDate()).padStart(2, '0')}/${String(d.getMonth() + 1).padStart(2, '0')}`;
		this.headerRange.textContent = `${fmt(first)} – ${fmt(last)}`;
	}

	/* ═══════════════════════════════════════════════
	   BOTÓN FLOTANTE "HOY"
	═══════════════════════════════════════════════ */

	enableTodayButton() {
		this._todayBtnEnabled = true;
		this._checkTodayVisible();
	}

	_checkTodayVisible() {
		if (!this._todayBtnEnabled) return;

		const todayEl = this.scrollEl.querySelector('.day.today');
		if (!todayEl) { this.todayBtn.style.display = ''; return; }

		const scrollRect = this.scrollEl.getBoundingClientRect();
		const todayRect = todayEl.getBoundingClientRect();

		const isVisible = todayRect.top >= scrollRect.top && todayRect.bottom <= scrollRect.bottom;
		if (isVisible) { this.todayBtn.style.display = 'none'; return; }

		this.todayBtn.textContent = todayRect.top > scrollRect.bottom ? '↓ Hoy' : '↑ Hoy';
		this.todayBtn.style.display = '';
	}

	/* ═══════════════════════════════════════════════
	   API PÚBLICA
	═══════════════════════════════════════════════ */

	goToToday(smooth) { this.scrollToToday(smooth); }

	async reload() {
		this._initialLoadComplete = false;
		this._data.clear();

		await this._loadInitialEvents();
		this.scrollToToday();

		setTimeout(() => {
			this._initialLoadComplete = true;
		}, 1500);
	}

	startLoading() { this.getElement('aonModuleLoader')?.startLoading(); }
	stopLoading() { this.getElement('aonModuleLoader')?.stopLoading(); }
}

window.customElements.define("aon-agenda-all-days", AonAgendaAllDays);