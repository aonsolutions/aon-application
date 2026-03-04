import { AonElement } from "../../../../components/AonElement.js";
import { getTaskHolder } from "../../../../services/taskHolderService.js";
import { getTaskHolderContactEvents, getTaskHolderTimeControl } from "../../../../services/timeControlService.js";
import { AonDateUtils } from "../../../utils/AonDateUtils.js";
import { timeHourShort } from ".././utils.js";

export class AonAgendaAnnualSummary extends AonElement {

  _taskHolder       = null;
  _year             = new Date().getFullYear();
  _events           = new Map(); // "YYYY-MM-DD" → { dayData, details }
  _festivesContract = new Map(); // "YYYY-MM-DD" → festivo
  _daysTypeContract = new Map(); // "YYYY-MM-DD" → tipo de día
  _workingDays      = [];
  _workingDaysHours = [];

  async connectedCallback() {
    await this.buildShell();
  }

  async buildShell() {
    this.innerHTML = `
      <header class="annual-header">
        <div class="header-left">
          <div class="year-nav">
            <button class="year-btn prev-year" aria-label="Año anterior">&#8249;</button>
            <span class="year-label"></span>
            <button class="year-btn next-year" aria-label="Año siguiente">&#8250;</button>
          </div>
        </div>
        <div class="header-right">
          <div class="annual-totals">
            <span class="annual-hours"></span>
          </div>
          <span class="annual-holidays"></span>
        </div>
      </header>
      <div class="annual-scroll"></div>
    `;

    this._yearLabel      = this.querySelector('.year-label');
    this._annualHours    = this.querySelector('.annual-hours');
    this._annualHolidays = this.querySelector('.annual-holidays');
    this._scrollEl       = this.querySelector('.annual-scroll');

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
    // Guardamos dónde empezó el dedo al tocar la pantalla
    let startX  = 0;
    let startY  = 0;
    // Flag para saber si ya decidimos ignorar este gesto
    // (por ejemplo porque el usuario está haciendo scroll vertical)
    let decided = false;

    this.addEventListener('touchstart', e => {
      // Solo nos interesa un dedo; si hay más, ignoramos
      if (e.touches.length !== 1) return;
      startX  = e.touches[0].clientX;
      startY  = e.touches[0].clientY;
      decided = false; // reiniciamos la decisión en cada nuevo toque
    }, { passive: true });

    this.addEventListener('touchmove', e => {
      if (decided) return; // ya decidimos en un movimiento anterior de este gesto

      const dx = e.touches[0].clientX - startX; // cuánto se movió en horizontal
      const dy = e.touches[0].clientY - startY; // cuánto se movió en vertical

      // Si el movimiento es demasiado pequeño, esperamos más datos
      if (Math.abs(dx) < 8 && Math.abs(dy) < 8) return;

      // Si el gesto es más vertical que horizontal → es scroll, no swipe
      // Usamos ratio 2:1: el horizontal debe doblar al vertical
      if (Math.abs(dy) > Math.abs(dx) / 2) {
        decided = true; // marcamos como "ignorado" para no recalcular
        return;
      }

      // Es claramente horizontal → bloqueamos el scroll para que no "baile"
      e.preventDefault();
      decided = true;
    }, { passive: false }); // passive:false necesario para poder llamar preventDefault

    this.addEventListener('touchend', e => {
      if (e.changedTouches.length !== 1) return;

      const dx = e.changedTouches[0].clientX - startX;
      const dy = e.changedTouches[0].clientY - startY;

      // Mínimo 60px horizontales y ratio 2:1 para confirmar el swipe
      const isHorizontalSwipe = Math.abs(dx) > 60 && Math.abs(dx) > Math.abs(dy) * 2;
      if (!isHorizontalSwipe) return;

      if (dx < 0) {
        // Deslizó hacia la izquierda → año siguiente
        this.loadYear(this._year + 1);
      } else {
        // Deslizó hacia la derecha → año anterior
        this.loadYear(this._year - 1);
      }
    }, { passive: true });
  }

  // ---- API PÚBLICA ----

  async loadYear(year) {
    if (!this._taskHolder) return;

    this._year = year;
    this._yearLabel.textContent = year;

    // Limpiar datos del año anterior
    this._events.clear();
    this._festivesContract.clear();
    this._daysTypeContract.clear();
    this._workingDays      = [];
    this._workingDaysHours = [];
    this._scrollEl.innerHTML = '';

    // Mostrar el loader global (el mismo que usa aon-agenda-all-days)
    this.startLoading();

    const startDateStr = AonDateUtils.format(new Date(year, 0, 1),  'YYYY-MM-DD');
    const endDateStr   = AonDateUtils.format(new Date(year, 11, 31), 'YYYY-MM-DD');

    const filter = {
      group:        'DAY',
      period:       'personalized',
      startDate:    startDateStr,
      endDate:      endDateStr,
      active:       true,
      name:         'Personalizado',
      value:        'personalized',
      taskHolderId: this._taskHolder,
      event:        'click',
      search:       ''
    };

    try {
      const [datos, contractDatos] = await Promise.all([
        getTaskHolderTimeControl(filter),
        getTaskHolderContactEvents(filter)
      ]);

      this.processEvents(datos);
      this.processContractDaysType(contractDatos.daysType);
      this.processContractFestives(contractDatos.festives);
      this._workingDays      = contractDatos.workingDays      || [];
      this._workingDaysHours = contractDatos.workingDaysHours || [];

      this.renderYear();
      this.updateAnnualHeader();

    } catch (err) {
      console.error('❌ Error cargando resumen anual:', err);
    } finally {
      // Siempre quitamos el loader, aunque haya fallado
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
      const date    = new Date(dayData.start_date);
      const dateKey = AonDateUtils.format(date, 'YYYY-MM-DD');
      if (!this._festivesContract.has(dateKey)) {
        this._festivesContract.set(dateKey, {
          date,
          dateKey,
          description: dayData.description,
          source:      dayData.source
        });
      }
    });
  }

  processContractDaysType(datos) {
    if (!Array.isArray(datos)) return;
    datos.forEach(dayData => {
      const start = new Date(dayData.start_date);
      const end   = new Date(dayData.end_date);
      start.setHours(0, 0, 0, 0);
      end.setHours(0, 0, 0, 0);

      for (let cur = new Date(start); cur <= end; cur.setDate(cur.getDate() + 1)) {
        const dateKey = AonDateUtils.format(cur, 'YYYY-MM-DD');
        if (!this._daysTypeContract.has(dateKey)) {
          this._daysTypeContract.set(dateKey, {
            description: dayData.description,
            source:      dayData.source
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
    const lastDay  = new Date(year, monthIndex + 1, 0);

    const monthName = AonDateUtils.monthName(firstDay);
    const { workedMs, expectedMs, dayTypeSummary, festivesOfMonth } =
      this.calculateMonthStats(firstDay, lastDay);

    const workedStr = timeHourShort(workedMs);
    const diffMs    = workedMs - expectedMs;
    const diffStr   = this.formatDiffTime(diffMs);
    const diffClass = diffMs < 0 ? 'negative' : 'positive';

    const dayTypePillsHtml  = this.renderDayTypePills(dayTypeSummary);
    const festivesPillsHtml = this.renderFestivePills(festivesOfMonth);

    const el = document.createElement('section');
    const today = new Date();
    const isCurrentMonth = year === today.getFullYear() && monthIndex === today.getMonth();
    el.className = `annual-month${isCurrentMonth ? ' current' : ''}`;
    el.dataset.month = monthIndex;

    el.innerHTML = `
      <div class="annual-month-header">
        <span class="annual-month-name">${monthName.toUpperCase()}</span>
        <div class="annual-month-stats">
          <span class="annual-month-hours">
            ${workedStr} h /
            <span class="day-diff-hours ${diffClass}">${diffStr} h</span>
          </span>
        </div>
      </div>
      <div class="annual-month-pills">
        ${dayTypePillsHtml}
        ${festivesPillsHtml}
      </div>
    `;

    return el;
  }
  
  calculateMonthStats(firstDay, lastDay) {
    const msPerHour = 1000 * 60 * 60;
    let workedMs   = 0;
    let expectedMs = 0;

    const dayTypeSummary  = new Map();
    const festivesOfMonth = [];

    for (let d = new Date(firstDay); d <= lastDay; d.setDate(d.getDate() + 1)) {
      const day       = new Date(d);
      const dateKey   = AonDateUtils.format(day, 'YYYY-MM-DD');
      const dayOfWeek = day.getDay();

      const hoursForDay   = this._workingDaysHours.length === 7
        ? (this._workingDaysHours[dayOfWeek] || 0) : 0;
      const dayExpectedMs = hoursForDay * msPerHour;

      const eventData = this._events.get(dateKey);
      let dayWorkedMs = eventData ? Number(eventData.dayData.time || 0) : 0;

      const dayType = this._daysTypeContract.get(dateKey);
      if (dayType?.source) {
		if(dayType?.source === "IT" || dayType?.source === "PAID_LEAVE")
        	dayWorkedMs = dayExpectedMs;
        	
        if (!dayTypeSummary.has(dayType.source)) {
          dayTypeSummary.set(dayType.source, {
            count: 0,
            label: this.getSourceLabel(dayType.source)
          });
        }
        dayTypeSummary.get(dayType.source).count++;
      }

      const festive = this._festivesContract.get(dateKey);
      if (festive) {
        festivesOfMonth.push(festive);
      }

      workedMs   += dayWorkedMs;
      expectedMs += dayExpectedMs;
    }

    return { workedMs, expectedMs, dayTypeSummary, festivesOfMonth };
  }

  renderDayTypePills(dayTypeSummary) {
    if (dayTypeSummary.size === 0) return '';
    return Array.from(dayTypeSummary.entries()).map(([source, { count, label }]) => `
      <span class="day-type-pill ${source.toLowerCase()}">
        ${label}: ${count} día${count !== 1 ? 's' : ''}
      </span>
    `).join('');
  }

  renderFestivePills(festivesOfMonth) {
    if (!festivesOfMonth || festivesOfMonth.length === 0) return '';
    const sorted = [...festivesOfMonth].sort((a, b) => a.date - b.date);
    return sorted.map(f => `
      <span class="day-type-pill festive">
        ${f.date.getDate()} · ${f.description}
      </span>
    `).join('');
  }

  // ---- Totales anuales para la cabecera ----

  updateAnnualHeader() {
    const msPerHour = 1000 * 60 * 60;
    let totalWorkedMs   = 0;
    let totalExpectedMs = 0;
    let totalHolidays   = 0;

    const firstDay = new Date(this._year, 0, 1);
    const lastDay  = new Date(this._year, 11, 31);

    for (let d = new Date(firstDay); d <= lastDay; d.setDate(d.getDate() + 1)) {
      const day       = new Date(d);
      const dateKey   = AonDateUtils.format(day, 'YYYY-MM-DD');
      const dayOfWeek = day.getDay();

      const hoursForDay   = this._workingDaysHours.length === 7
        ? (this._workingDaysHours[dayOfWeek] || 0) : 0;
      const dayExpectedMs = hoursForDay * msPerHour;

      const eventData = this._events.get(dateKey);
      let dayWorkedMs = eventData ? Number(eventData.dayData.time || 0) : 0;

      const dayType = this._daysTypeContract.get(dateKey);
      if (dayType?.source && (dayType?.source === "IT" || dayType?.source === "PAID_LEAVE") ) {
        dayWorkedMs = dayExpectedMs;
        if (dayType.source === 'HOLIDAYS') totalHolidays++;
      }

      totalWorkedMs   += dayWorkedMs;
      totalExpectedMs += dayExpectedMs;
    }

    const workedStr = timeHourShort(totalWorkedMs);
    const diffMs    = totalWorkedMs - totalExpectedMs;
    const diffStr   = this.formatDiffTime(diffMs);
    const diffClass = diffMs < 0 ? 'negative' : 'positive';

    this._annualHours.innerHTML =
      `Año: ${workedStr} h / <span class="day-diff-hours ${diffClass}">${diffStr} h</span>`;

    this._annualHolidays.textContent =
      `Vacaciones: ${totalHolidays} día${totalHolidays !== 1 ? 's' : ''}`;
  }

  // ---- Helpers ----

  getSourceLabel(source) {
    const labels = {
      HOLIDAYS:       'Vacaciones',
      EFFECITVE_DAYS: 'Día Efectivo',
      INACTIVITY:     'Inactividad',
      ABSENCE:        'Ausencia',
      STRIKE:         'Huelga',
      ERE:            'ERE',
      ERE_FZA:        'ERE F.M.',
      ERE_FZA_EXO:    'ERE F.M. Exo.',
      PAID_LEAVE:     'Perm. Retribuido',
      PARTIALITY:     'Parcialidad',
      IT:             'IT',
    };
    return labels[source] || source;
  }

  formatDiffTime(ms) {
    const sign   = ms < 0 ? '-' : '+';
    const absStr = timeHourShort(Math.abs(ms));
    return `${sign}${absStr}`;
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