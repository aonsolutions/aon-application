import { AonElement } from "../../../../components/AonElement.js";
import { getTaskHolder } from "../../../../services/taskHolderService.js";
import { getTaskHolderContactEvents, getTaskHolderTimeControl } from "../../../../services/timeControlService.js";
import { AonDateUtils } from "../../../utils/AonDateUtils.js";
import { timeHourShort } from ".././utils.js";

export class AonAgendaAllDays extends AonElement {

	_taskHolder;
	_taskHolderName;
	_events = new Map(); // key: "YYYY-MM-DD" => value: array de eventos
	_festivesContract = new Map(); // key: "YYYY-MM-DD" => value: array de eventos
	_daysTypeContract = new Map(); // key: "YYYY-MM-DD" => value: array de eventos
	_workingDays = []; // array dias laborables
	_workingDaysHours = []; // array dias laborables
	_eventsContract = new Map(); // key: "YYYY-MM-DD" => value: array de eventos
	_loadedMonths = new Set(); // Para trackear qué meses ya se han cargado
	_loadingMonths = new Set(); // Para trackear qué meses están cargándose AHORA
	_isLoading = false; // Flag para evitar múltiples cargas simultáneas
	_initialLoadComplete = false; // Flag para evitar cargas durante scroll inicial
	_scrollLocked = false; // Flag para bloquear scroll durante carga
	_lastScrollPosition = 0; // Última posición válida del scroll
	_firstLoadedMonth = null; // Primer mes cargado (Date)
	_lastLoadedMonth = null; // Último mes cargado (Date)

	async connectedCallback() {
		this.initState();
		await this.build();
		this.renderInitial();
		this.attachEventListeners();

		// Cargar eventos del mes actual inicialmente
		await this.loadInitialEvents();

		// DESPUÉS de cargar los eventos, hacer scroll al día de hoy
		setTimeout(() => {
			this.scrollToToday();
		}, 200);

		// Activar checkLoadMonthsOnScroll después de 1 segundo
		setTimeout(() => {
			this._initialLoadComplete = true;
			console.log('✅ Scroll infinito activado');
		}, 1000);
	}

	/* ---------------- STATE ---------------- */
	initState() {
		// Fecha base: inicio de la semana actual
		const today = new Date();
		this.baseDate = AonDateUtils.startOfWeek(today);

		// Control de virtualización
		this.weekHeight = 0;
		this.visibleWeeks = new Map(); // offset => {element, week}

		// Rango de renderizado inicial reducido - solo 6 semanas hacia atrás (aprox 1.5 meses)
		this.renderRange = 6;
	}

	/* ---------------- BUILD ---------------- */
	async build() {
		this.innerHTML = `
	      <header>
	        <div class="header-left">
	          <div class="header-top">
	            <span class="month"></span>
	          </div>
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

		let userTaskHolder = await getTaskHolder();
		if (userTaskHolder) {
			this._taskHolder = userTaskHolder.id;
			this._taskHolderName = userTaskHolder.name;
		}
	}

	/* ---------------- LOAD EVENTS ---------------- */
	async loadInitialEvents() {
		if (!this._taskHolder) return;

		// Cargar 3 meses: anterior, actual y siguiente
		const today = new Date();

		const prevMonth = new Date(today);
		prevMonth.setMonth(prevMonth.getMonth() - 1);

		const nextMonth = new Date(today);
		nextMonth.setMonth(nextMonth.getMonth() + 1);

		// Establecer los límites ANTES de cargar
		this._firstLoadedMonth = new Date(prevMonth.getFullYear(), prevMonth.getMonth(), 1);
		this._lastLoadedMonth = new Date(nextMonth.getFullYear(), nextMonth.getMonth(), 1);

		console.log(`🚀 Cargando 3 meses: ${AonDateUtils.format(this._firstLoadedMonth, 'YYYY-MM')} a ${AonDateUtils.format(this._lastLoadedMonth, 'YYYY-MM')}`);

		await this.loadMonthEvents(prevMonth); // Mes anterior
		await this.loadMonthEvents(today); // Mes actual
		await this.loadMonthEvents(nextMonth); // Mes siguiente
	}

	async loadMonthEvents(date) {
		if (!this._taskHolder || this._isLoading) return;

		// Generar clave del mes (YYYY-MM)
		const monthKey = `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}`;

		// Si ya se cargó o se está cargando este mes, salir
		if (this._loadedMonths.has(monthKey) || this._loadingMonths.has(monthKey)) {
			return;
		}

		// Marcar como "cargándose"
		this._loadingMonths.add(monthKey);

		this._isLoading = true;
		this.startLoading();

		// Calcular primer y último día del mes
		const startDate = new Date(date.getFullYear(), date.getMonth(), 1);
		const endDate = new Date(date.getFullYear(), date.getMonth() + 1, 0);

		// Formatear fechas para el filtro
		const startDateStr = AonDateUtils.format(startDate, "YYYY-MM-DD");
		const endDateStr = AonDateUtils.format(endDate, "YYYY-MM-DD");

		console.log(`📥 Cargando ${monthKey} (${startDateStr} a ${endDateStr})`);

		// Construir filtro
		const filter = {
			"group": "DAY",
			"period": "personalized",
			"startDate": startDateStr,
			"endDate": endDateStr,
			"active": true,
			"name": "Personalizado",
			"value": "personalized",
			"taskHolderId": this._taskHolder,
			"event": "click",
			"search": ""
		};

		try {
			const datos = await getTaskHolderTimeControl(filter);
			const contractDatos = await getTaskHolderContactEvents(filter);

			console.log(`✅ ${monthKey}: ${datos.length} eventos`);
			console.log(datos);

			console.log(`✅ ${monthKey}: ${contractDatos.length} eventos contrato`);
			console.log(contractDatos);

			// Procesar y guardar eventos por fecha
			this.processEvents(datos);

			// Procesar y guardar festivos por fecha
			this.processContractDaysType(contractDatos.daysType);

			// Procesar y guardar festivos por fecha
			this.processContractFestives(contractDatos.festives);

			// Guardar dias laborables / no laborables semana
			this._workingDays = contractDatos.workingDays;
			this._workingDaysHours = contractDatos.workingDaysHours;

			// Procesar y guardar eventos por fecha
			//this.processContractEvents(contractDatos.events);

			// Marcar mes como cargado
			this._loadedMonths.add(monthKey);

			// AGREGAR LAS SEMANAS DEL MES AL DOM
			this.addWeeksForMonth(startDate);

			// Re-renderizar las semanas visibles con los eventos
			this.refreshWeeks();
		} catch (error) {
			console.error(`❌ Error en ${monthKey}:`, error);
		} finally {
			// Quitar de "cargándose"
			this._loadingMonths.delete(monthKey);
			this._isLoading = false;
			this.stopLoading();
		}
	}

	/* ---------------- AGREGAR SEMANAS DEL MES ---------------- */
	addWeeksForMonth(monthStartDate) {
		// Calcular primer y último día del mes
		const firstDay = new Date(monthStartDate.getFullYear(), monthStartDate.getMonth(), 1);
		const lastDay = new Date(monthStartDate.getFullYear(), monthStartDate.getMonth() + 1, 0);

		// Calcular primera y última semana que contienen días de este mes
		const firstWeekStart = AonDateUtils.startOfWeek(firstDay);
		const lastWeekStart = AonDateUtils.startOfWeek(lastDay);

		// Calcular offsets de esas semanas
		const firstOffset = Math.floor((firstWeekStart - this.baseDate) / (1000 * 60 * 60 * 24 * 7));
		const lastOffset = Math.floor((lastWeekStart - this.baseDate) / (1000 * 60 * 60 * 24 * 7));

		console.log(`  📅 Agregando semanas ${firstOffset} a ${lastOffset}`);

		// Montar todas las semanas del mes
		for (let offset = firstOffset; offset <= lastOffset; offset++) {
			if (!this.visibleWeeks.has(offset)) {
				this.mountWeek(offset);
			}
		}
	}

	/* ---------------- PROCESS EVENTS ---------------- */
	processEvents(datos) {
		if (!datos || !Array.isArray(datos)) return;

		datos.forEach(dayData => {
			// Convertir start_date (timestamp en milisegundos) a fecha
			const date = new Date(dayData.start_date);
			const dateKey = AonDateUtils.format(date, "YYYY-MM-DD");

			let time = dayData.time || 0;
			const details = dayData.detail || [];

			// Solo guardar si no existe ya (para no sobrescribir)
			if (!this._events.has(dateKey)) {
				this._events.set(dateKey, {
					dayData: {
						start_date: dayData.start_date,
						end_date: dayData.end_date,
						time,
						status: dayData.status
					},
					details
				});
			}
		});
	}

	/* ---------------- PROCESS CONTRACT FESTIVES ---------------- */
	processContractFestives(datos) {
		if (!datos || !Array.isArray(datos)) return;

		datos.forEach(dayData => {
			// Convertir start_date (timestamp en milisegundos) a fecha
			const date = new Date(dayData.start_date);
			const dateKey = AonDateUtils.format(date, "YYYY-MM-DD");

			// Solo guardar si no existe ya (para no sobrescribir)

			if (!this._festivesContract.has(dateKey)) {
				this._festivesContract.set(dateKey, {
					start_date: dayData.start_date,
					end_date: dayData.end_date,
					description: dayData.description,
					source: dayData.source
				});
			}
		});
	}

	/* ---------------- PROCESS CONTRACT DAYS TYPE ---------------- */
	processContractDaysType(datos) {
		if (!datos || !Array.isArray(datos)) return;

		datos.forEach(dayData => {
			const start = new Date(dayData.start_date);
			const end = new Date(dayData.end_date);

			// Normalizar horas para evitar saltos raros
			start.setHours(0, 0, 0, 0);
			end.setHours(0, 0, 0, 0);

			// Iterar día a día
			for (
				let current = new Date(start);
				current <= end;
				current.setDate(current.getDate() + 1)
			) {
				const dateKey = AonDateUtils.format(current, "YYYY-MM-DD");

				// No sobrescribir si ya existe
				if (!this._daysTypeContract.has(dateKey)) {
					this._daysTypeContract.set(dateKey, {
						start_date: current,
						end_date: current,
						description: dayData.description,
						source: dayData.source
					});
				}
			}
		});
	}

	/* ---------------- PROCESS CONTRACT EVENTS ---------------- */
	processContractEvents(datos) {
		if (!datos || !Array.isArray(datos)) return;

		datos.forEach(dayData => {
			// Convertir start_date (timestamp en milisegundos) a fecha
			const date = new Date(dayData.start_date);
			const dateKey = AonDateUtils.format(date, "YYYY-MM-DD");

			// Solo guardar si no existe ya (para no sobrescribir)

			if (!this._eventsContract.has(dateKey)) {
				this._eventsContract.set(dateKey, {
					start_date: dayData.start_date,
					end_date: dayData.end_date,
					description: dayData.description,
					source: dayData.source
				});
			}
		});
	}

	/* ---------------- REFRESH WEEKS ---------------- */
	refreshWeeks() {
		// Guardar posición de scroll
		const currentScrollTop = this.scrollEl.scrollTop;

		console.log(`🔄 Actualizando ${this.visibleWeeks.size} semanas con eventos`);

		// Re-renderizar cada semana visible in-place
		this.visibleWeeks.forEach((data, offset) => {
			const newWeekEl = this.renderWeek(data.week);

			// Reemplazar el elemento antiguo con el nuevo (mantiene posición en DOM)
			data.element.replaceWith(newWeekEl);

			// Actualizar la referencia
			this.visibleWeeks.set(offset, { element: newWeekEl, week: data.week });
		});

		// Restaurar posición de scroll
		this.scrollEl.scrollTop = currentScrollTop;
	}

	/* ---------------- INITIAL RENDER ---------------- */
	renderInitial() {
		// Renderizar semanas en orden ASCENDENTE (de más antiguo a más reciente)
		// Las semanas se añaden en orden: -52, -51, ..., -1, 0, 1, 2, 3, 4
		// Esto hace que el scroll funcione correctamente:
		// - Arriba: pasado (offset negativo)
		// - Abajo: futuro (offset positivo)

		// Primero las semanas pasadas (de -52 a -1)
		for (let i = -this.renderRange; i <= -1; i++) {
			this.mountWeek(i);
		}

		// Luego la semana actual (0)
		this.mountWeek(0);

		// Finalmente las semanas futuras (de 1 a 4)
		for (let i = 1; i <= 4; i++) {
			this.mountWeek(i);
		}

		// Medir altura después de renderizar
		setTimeout(() => {
			this.measureWeekHeight();
		}, 100);
	}

	/* ---------------- SCROLL TO TODAY ---------------- */
	scrollToToday(smooth) {
		const today = new Date();
		today.setHours(0, 0, 0, 0);

		// Buscar el día de hoy en todas las semanas renderizadas
		let todayElement = null;

		this.visibleWeeks.forEach((data, offset) => {
			if (todayElement) return; // Ya lo encontramos

			const dayElements = data.element.querySelectorAll('.day');
			dayElements.forEach(dayEl => {
				if (todayElement) return; // Ya lo encontramos

				// Verificar si este día es hoy comparando con la clase 'today'
				if (dayEl.classList.contains('today')) {
					todayElement = dayEl;
				}
			});
		});

		// Hacer scroll al día de hoy CENTRADO en el viewport
		if (todayElement) {
			todayElement.scrollIntoView({ behavior: smooth ? 'smooth' : 'auto', block: 'center' });
			console.log('Scrolled to today (centered) - auto');
		} else {
			console.warn('Today element not found');
			// Si no encontramos el día de hoy, hacer scroll a la semana actual
			const currentWeekData = this.visibleWeeks.get(0);
			if (currentWeekData) {
				currentWeekData.element.scrollIntoView({ behavior: smooth ? 'smooth' : 'auto', block: 'center' });
			}
		}

		// Actualizar header después del scroll
		setTimeout(() => {
			this.updateHeader();
		}, 500);
	}

	/* ---------------- WEEK MANAGEMENT ---------------- */
	mountWeek(offset) {
		if (this.visibleWeeks.has(offset)) return;

		const week = this.createWeek(offset);
		const weekEl = this.renderWeek(week);

		this.visibleWeeks.set(offset, { element: weekEl, week });

		// Insertar en la posición correcta según offset
		// Offsets negativos (pasado) van al principio
		// Offsets positivos (futuro) van al final

		if (this.visibleWeeks.size === 1) {
			// Primera semana, añadir directamente
			this.scrollEl.appendChild(weekEl);
		} else {
			// Encontrar dónde insertar según el offset
			let inserted = false;

			// Obtener todos los offsets ordenados
			const sortedOffsets = Array.from(this.visibleWeeks.keys()).sort((a, b) => a - b);
			const thisIndex = sortedOffsets.indexOf(offset);

			if (thisIndex === 0) {
				// Es el offset más pequeño, insertar al principio
				this.scrollEl.insertBefore(weekEl, this.scrollEl.firstChild);
				inserted = true;
			} else if (thisIndex === sortedOffsets.length - 1) {
				// Es el offset más grande, insertar al final
				this.scrollEl.appendChild(weekEl);
				inserted = true;
			} else {
				// Insertar entre dos elementos
				const nextOffset = sortedOffsets[thisIndex + 1];
				const nextData = this.visibleWeeks.get(nextOffset);
				if (nextData && nextData.element) {
					this.scrollEl.insertBefore(weekEl, nextData.element);
					inserted = true;
				}
			}

			if (!inserted) {
				// Fallback: añadir al final
				this.scrollEl.appendChild(weekEl);
			}
		}
	}

	unmountWeek(offset) {
		const data = this.visibleWeeks.get(offset);
		if (data) {
			data.element.remove();
			this.visibleWeeks.delete(offset);
		}
	}

	createWeek(offset) {
		// Calcular fecha de inicio de esta semana
		const start = new Date(this.baseDate);
		start.setDate(start.getDate() + offset * 7);

		// Generar los 7 días
		const days = [];
		for (let i = 0; i < 7; i++) {
			const date = new Date(start);
			date.setDate(start.getDate() + i);
			const key = AonDateUtils.format(date, "YYYY-MM-DD");
			days.push({ date, key });
		}

		return { offset, start, days };
	}

	/* ---------------- RENDER ---------------- */
	renderWeek(week) {
		const el = document.createElement("section");
		el.className = "week";
		el.dataset.offset = week.offset;

		const today = new Date();
		today.setHours(0, 0, 0, 0);

		// Ahora los días van en orden normal (lunes a domingo)
		// ya que el scroll va de antiguo (arriba) a reciente (abajo)
		week.days.forEach(day => {
			const dayEl = document.createElement("div");
			dayEl.className = "day";

			// Marcar día actual
			const dayDate = new Date(day.date);
			dayDate.setHours(0, 0, 0, 0);
			if (dayDate.getTime() === today.getTime()) {
				dayEl.classList.add("today");
			}

			// Marcar fin de semana (sábado = 6, domingo = 0) si no existe _workingDays
			const dayOfWeek = dayDate.getDay();
			if (this._workingDays.length > 0) {
				if (this._workingDays[dayOfWeek] === 1)
					dayEl.classList.add("weekend");
			} else if (dayOfWeek === 0 || dayOfWeek === 6) {
				dayEl.classList.add("weekend");
			}

			// Obtener eventos del día
			const dayTypeContract = this._daysTypeContract.get(day.key);

			// Mostrar todos los tipos de días
			let daysTypeHtml = '<div class="events">';

			if (dayTypeContract && !dayTypeContract.source === 'HOLIDAYS') {
				daysTypeHtml += this.renderDaysTypeEvents(dayTypeContract);
			}

			// Mostrar todos los días, tengan eventos o no
			const dayEventData = this._events.get(day.key);

			let totalHours = "0:00";
			let workedTime = 0;
			let eventsHtml = '';

			if (dayEventData) {
				workedTime = Number(dayEventData.dayData.time);
				totalHours = timeHourShort(Number(dayEventData.dayData.time));
				eventsHtml = this.renderDayEvents(dayEventData.details);
			}

			// Horas esperadas (Double[]) → convertir a minutos
			let expectedTime = 0;
			const msecPerMinute = 1000 * 60;
			const msecPerHour = msecPerMinute * 60;
			if (this._workingDaysHours && this._workingDaysHours.length === 7) {
				expectedTime = (this._workingDaysHours[dayOfWeek] || 0) * msecPerHour;
			}

			const dayContractFestive = this._festivesContract.get(day.key);

			// Vacaciones, permisos retribuidos, ITs (habra que filtrar dayTypeContract.source si queremos quitar alguno )
			if (dayTypeContract && (dayTypeContract.source === "IT" || dayTypeContract.source === "PAID_LEAVE" || dayTypeContract.source === "HOLIDAYS")) {
				// Si el día es laborable (_workingDays[dayOfWeek] === 0) y HOLIDAYS → sumar horas esperadas
				if (this._workingDays && this._workingDays[dayOfWeek] === 0) {
					totalHours = timeHourShort(expectedTime);
					workedTime = expectedTime;
				} else {
					totalHours = timeHourShort(expectedTime);
					workedTime = expectedTime;
				}
			}

			daysTypeHtml += eventsHtml;
			daysTypeHtml += '</div>';

			// Obtener eventos contrato del día
			let dayFestiveHtml = '';

			if (dayContractFestive) {
				dayFestiveHtml = this.renderContractFestive(dayContractFestive);
				//expectedTime = 0;
			} else if (dayTypeContract && dayTypeContract.source === "HOLIDAYS") {
				dayFestiveHtml = this.renderContractHoliday();
			}

			// Diferencia en minutos
			const diffTime = workedTime - expectedTime;
			const diffHours = this.formatDiffTime(diffTime);

			// Formatear nombre del día (3 primeras letras + punto)
			const dayNameFull = AonDateUtils.dayName(day.date);
			const dayNameShort = dayNameFull.substring(0, 3) + '.';

			let dayHtml =
				`<div class="day-header">`;

			dayHtml +=
				`<div class="day-header-left">
				            <span class="day-name">${dayNameShort}</span>
				            <span class="day-number ${dayContractFestive ? 'festive' : (dayTypeContract ? dayTypeContract.source.toLowerCase() : '')}">${day.date.getDate()}</span>
				            ${dayFestiveHtml}
				          </div>`;

			if ((today >= dayDate && workedTime > 0) || (workedTime > 0 && this._workingDays[dayOfWeek] === 0))
				dayHtml +=
					`<div class="day-header-right">
					        <span class="total-hours">${totalHours} h / <span class="day-diff-hours ${diffHours.includes('-') ? 'negative' : 'positive'}">${diffHours} h</span></span>
					      </div>`;

			dayHtml +=
				`</div>
			        <div class="day-content">
			          ${daysTypeHtml}
			        </div>`;

			dayEl.innerHTML = dayHtml;

			el.appendChild(dayEl);
		});

		return el;
	}

	formatDiffTime = (time) => {
		const sign = time < 0 ? "-" : "+";
		const absTime = Math.abs(time);

		let formatted = timeHourShort(absTime);
		return `${sign}${formatted}`;
	};

	/* ---------------- RENDER EVENTS ---------------- */
	renderDayEvents(events) {
		if (!events || events.length === 0) {
			return '';
		}

		// Ordenar eventos por fecha (hora)
		const sortedEvents = [...events].sort((a, b) => a.date - b.date);

		// Agrupar eventos en tramos
		const tramos = this.groupEventsIntoTramos(sortedEvents);

		return `
			${tramos.map(tramo => {
			// Formatear horas de inicio y fin
			const startDate = new Date(tramo.start.date);
			const endDate = new Date(tramo.end.date);

			const startHours = String(startDate.getHours()).padStart(2, '0');
			const startMinutes = String(startDate.getMinutes()).padStart(2, '0');
			const startTimeStr = `${startHours}:${startMinutes}`;

			const endHours = String(endDate.getHours()).padStart(2, '0');
			const endMinutes = String(endDate.getMinutes()).padStart(2, '0');
			const endTimeStr = `${endHours}:${endMinutes}`;

			// Determinar clase CSS según el status del evento de inicio
			let statusClass = '';
			if (tramo.start.status === 'in') {
				statusClass = 'in';
			} else if (tramo.start.status === 'out') {
				statusClass = 'out';
			} else if (tramo.start.status === 'pause') {
				statusClass = 'pause';
			} else if (tramo.start.status === 'current') {
				statusClass = 'current';
			}

			// Usar el reasonValue del evento de inicio
			const reason = tramo.start.reasonValue || tramo.start.reason || '';

			return `
					<div class="event ${statusClass}">
						<span class="time">${startTimeStr} - ${endTimeStr}</span>
						<span class="reason">${reason}</span>
					</div>
				`;
		}).join('')}
		`;
	}

	/* ---------------- RENDER CONTRACT EVENTS ---------------- */
	renderContractFestive(event) {
		if (!event) return '';

		return `
			<div class="event">
				<span class="reason festive">${event.description}</span>
			</div>
		`;
	}
	
	renderContractHoliday() {
		return `
			<div class="event">
				<span class="reason holiday">Vacaciones</span>
			</div>
		`;
	}

	/* ---------------- RENDER CONTRACT EVENTS ---------------- */
	renderDaysTypeEvents(event) {
		if (!event) return '';

		return `
				<div class="event ${event.source.toLowerCase()}">
					<span class="reason">${this.parseDayTypeDescription(event)}</span>
				</div>
		`;
	}

	parseDayTypeDescription(event) {
		switch (event.source) {
			case 'HOLIDAYS':
				return 'Vacaciones';
			case 'EFFECITVE_DAYS':
				return 'Día Efectivo';
			case 'INACTIVITY':
				return 'Inactividad: ' + event.description; 
			case 'ABSENCE':
				return 'Ausencia. ' + this.parsePartialityDescription(event.description);
			case 'STRIKE':
				return 'Huelga. ' + this.parsePartialityDescription(event.description);
			case 'ERE':
				return 'ERE. ' + this.parsePartialityDescription(event.description);
			case 'ERE_FZA':
				return 'ERE Fuerza Mayor. ' + this.parsePartialityDescription(event.description);
			case 'ERE_FZA_EXO':
				return 'ERE Fuerza Mayor Exonerado. ' + this.parsePartialityDescription(event.description);
			case 'PAID_LEAVE':
				return 'Perm. Retribuido: ' + this.parsePaidLeaveDescription(event.description); // SI se suma
			case 'PARTIALITY':
				return this.parsePartialityDescription(event.description);
			case 'IT':
				return event.description; // SI SE SUMAN
			default:
				return 'Desconocido'
		}
	}

	parsePartialityDescription(description) {
		return `Parcialidad: ${description * 100}%`;
	}

	parsePaidLeaveDescription(description) {
		let regex = /\/\*inherit\*\/(.*?)\/\*\*\/([\d.]+)/;
		let [, frase, numero] = description.match(regex);
		return `${frase}. Parcialidad: ${numero * 100}%`;
	}



	/* ---------------- RENDER CONTRACT EVENTS ---------------- */
	renderDayContractEvents(event) {
		if (!event) {
			return '';
		}

		return `
			<div class="events">
				<div class="event ${event.source.toLowerCase()}">
					<svg class="calendar-icon" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
						<rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
						<line x1="16" y1="2" x2="16" y2="6"></line>
						<line x1="8" y1="2" x2="8" y2="6"></line>
						<line x1="3" y1="10" x2="21" y2="10"></line>
					</svg>
					<span class="reason">${event.description}</span>
				</div>
			</div>
		`;
	}

	/* ---------------- GROUP EVENTS INTO TRAMOS ---------------- */
	groupEventsIntoTramos(events) {
		const tramos = [];
		let i = 0;

		while (i < events.length) {
			const currentEvent = events[i];

			/* -------------------- CASO: IN -------------------- */
			if (currentEvent.status === 'in') {

				let startIndex = i;
				let nextIndex = i + 1;

				while (nextIndex < events.length && events[nextIndex].status === 'in') {
					startIndex = nextIndex;
					nextIndex++;
				}

				const startEvent = events[startIndex];

				let endIndex = startIndex + 1;
				while (endIndex < events.length && events[endIndex].status === 'in') {
					endIndex++;
				}

				if (endIndex < events.length) {
					const endEvent = events[endIndex];

					tramos.push({ start: startEvent, end: endEvent });

					if (endEvent.status === 'pause') {
						const afterPauseIndex = endIndex + 1;

						if (afterPauseIndex < events.length) {
							tramos.push({
								start: endEvent,
								end: events[afterPauseIndex]
							});
						}

						i = endIndex + 1;
					} else {
						i = endIndex + 1;
					}

				} else {
					// Último evento = IN → tramo current
					tramos.push({
						start: { ...startEvent, status: 'current' },
						end: this.createNowEndEvent()
					});

					i = startIndex + 1;
				}
			}
			/* -------------------- CASO: PAUSE -------------------- */
			else if (currentEvent.status === 'pause') {
				const nextIndex = i + 1;

				if (nextIndex < events.length) {
					tramos.push({
						start: currentEvent,
						end: events[nextIndex]
					});
					i = nextIndex + 1;
				} else {
					i++;
				}
			}
			/* -------------------- CASO: OUT -------------------- */
			else if (currentEvent.status === 'out') {
				i++;
			}
			else {
				i++;
			}
		}

		/* -------------------- TRAMO FINAL CURRENT -------------------- */
		const last = events[events.length - 1];

		// SOLO si el último es PAUSE (no IN)
		if (last.status === 'pause') {
			tramos.push({
				start: { ...last, status: 'current' },
				end: this.createNowEndEvent()
			});
		}
		return tramos;
	}

	createNowEndEvent() {
		const now = Date.now();
		return {
			status: 'current',
			date: now,
			creation_date: now,
			cause: 'DEFAULT'
		};
	}


	/* ---------------- EVENT LISTENERS ---------------- */
	attachEventListeners() {
		let checkTimeout;

		this.scrollEl.addEventListener("scroll", (e) => {
			// Si el scroll está bloqueado, prevenir y restaurar posición
			if (this._scrollLocked) {
				e.preventDefault();
				this.scrollEl.scrollTop = this._lastScrollPosition;
				return;
			}

			// Guardar la posición actual como válida
			this._lastScrollPosition = this.scrollEl.scrollTop;

			this.handleScroll();

			// Debounce para checkLoadMonthsOnScroll
			clearTimeout(checkTimeout);
			checkTimeout = setTimeout(() => {
				this.checkLoadMonthsOnScroll();
			}, 50); // Aumentado a 50ms para evitar triggers múltiples
		}, { passive: false }); // Cambiado a false para permitir preventDefault

		// Medir altura después de un pequeño delay
		setTimeout(() => {
			this.measureWeekHeight();
		}, 100);
	}

	handleScroll() {
		this.updateHeader();
		this.checkLoadMore();
		//this.checkLoadMonthsOnScroll();
	}

	async checkLoadMore() {
		const scrollTop = this.scrollEl.scrollTop;
		const scrollHeight = this.scrollEl.scrollHeight;
		const clientHeight = this.scrollEl.clientHeight;

		const scrollBottom = scrollTop + clientHeight;
		const distanceFromBottom = scrollHeight - scrollBottom;

		// DESACTIVADO: checkLoadMore hacia el pasado
		// Ahora loadPreviousMonth() maneja la carga hacia atrás

		// Si estamos cerca del bottom (futuro), cargar más semanas futuras
		if (distanceFromBottom < clientHeight * 2) {
			const maxOffset = Math.max(...this.visibleWeeks.keys());
			// Limitar a máximo 4 semanas en el futuro
			if (maxOffset < 4) {
				for (let i = maxOffset + 1; i <= Math.min(maxOffset + 10, 4); i++) {
					this.mountWeek(i);
				}
			}
		}
	}

	/* ---------------- LOAD MONTHS ON SCROLL ---------------- */
	async checkLoadMonthsOnScroll() {
		// No cargar meses adicionales hasta que la carga inicial esté completa
		if (!this._initialLoadComplete) return;
		if (this._isLoading) return;
		if (this._scrollLocked) return;

		const scrollTop = this.scrollEl.scrollTop;
		const scrollHeight = this.scrollEl.scrollHeight;
		const clientHeight = this.scrollEl.clientHeight;

		const scrollBottom = scrollTop + clientHeight;
		const distanceFromTop = scrollTop;
		const distanceFromBottom = scrollHeight - scrollBottom;

		// Threshold dinámico: 20% del viewport (ej: 126px en 630px)
		const threshold = clientHeight * 0.2;

		// Cargar mes anterior si estamos cerca del inicio
		if (distanceFromTop < threshold) {
			console.log(`🔍 Cerca del inicio: ${Math.round(distanceFromTop)}px < ${Math.round(threshold)}px`);
			await this.loadPreviousMonth();
		}

		// Cargar mes siguiente si estamos cerca del final
		if (distanceFromBottom < threshold) {
			console.log(`🔍 Cerca del final: ${Math.round(distanceFromBottom)}px < ${Math.round(threshold)}px`);
			await this.loadNextMonth();
		}
	}

	async loadPreviousMonth() {
		if (!this._firstLoadedMonth) {
			console.log('⚠️ loadPreviousMonth: _firstLoadedMonth no definido');
			return;
		}

		// Calcular el mes anterior al primer mes cargado
		const prevMonth = new Date(this._firstLoadedMonth);
		prevMonth.setMonth(prevMonth.getMonth() - 1);

		const monthKey = `${prevMonth.getFullYear()}-${String(prevMonth.getMonth() + 1).padStart(2, '0')}`;

		// Si ya está cargado, no hacer nada
		if (this._loadedMonths.has(monthKey)) {
			console.log(`⏭️ loadPreviousMonth: ${monthKey} ya cargado`);
			return;
		}

		console.log(`⬆️ loadPreviousMonth: Iniciando carga de ${monthKey}`);
		console.log(`   _firstLoadedMonth actual: ${AonDateUtils.format(this._firstLoadedMonth, 'YYYY-MM-DD')}`);

		// Guardar posición de scroll
		const beforeHeight = this.scrollEl.scrollHeight;
		const beforeScroll = this.scrollEl.scrollTop;

		// Cargar el mes (esto bloqueará automáticamente con startLoading)
		await this.loadMonthEvents(prevMonth);

		// Actualizar el límite
		this._firstLoadedMonth = new Date(prevMonth.getFullYear(), prevMonth.getMonth(), 1);
		console.log(`   _firstLoadedMonth nuevo: ${AonDateUtils.format(this._firstLoadedMonth, 'YYYY-MM-DD')}`);

		// Ajustar scroll para mantener posición visual
		requestAnimationFrame(() => {
			const afterHeight = this.scrollEl.scrollHeight;
			const heightDiff = afterHeight - beforeHeight;
			this.scrollEl.scrollTop = beforeScroll + heightDiff;
			console.log(`   Scroll ajustado: ${beforeScroll} + ${heightDiff} = ${this.scrollEl.scrollTop}`);
		});
	}

	async loadNextMonth() {
		if (!this._lastLoadedMonth) return;

		// Calcular el mes siguiente al último mes cargado
		const nextMonth = new Date(this._lastLoadedMonth);
		nextMonth.setMonth(nextMonth.getMonth() + 1);

		const monthKey = `${nextMonth.getFullYear()}-${String(nextMonth.getMonth() + 1).padStart(2, '0')}`;

		// Si ya está cargado, no hacer nada
		if (this._loadedMonths.has(monthKey)) {
			return;
		}

		console.log(`⬇️ Cargando siguiente: ${monthKey}`);

		// Cargar el mes (esto bloqueará automáticamente con startLoading)
		await this.loadMonthEvents(nextMonth);

		// Actualizar el límite
		this._lastLoadedMonth = new Date(nextMonth.getFullYear(), nextMonth.getMonth(), 1);
	}

	measureWeekHeight() {
		const firstWeek = this.scrollEl.querySelector(".week");
		if (firstWeek) {
			this.weekHeight = firstWeek.offsetHeight;
		}
	}

	/* ---------------- HEADER ---------------- */
	updateHeader() {
		if (!this.weekHeight || this.weekHeight === 0) {
			this.measureWeekHeight();
		}

		const scrollTop = this.scrollEl.scrollTop;
		const scrollMiddle = scrollTop + (this.scrollEl.clientHeight / 2);

		// Encontrar la semana visible más cercana al centro
		let centerWeek = null;
		let minDistance = Infinity;

		this.visibleWeeks.forEach((data, offset) => {
			const weekTop = data.element.offsetTop;
			const weekMiddle = weekTop + (data.element.offsetHeight / 2);
			const distance = Math.abs(weekMiddle - scrollMiddle);

			if (distance < minDistance) {
				minDistance = distance;
				centerWeek = data.week;
			}
		});

		if (!centerWeek) return;

		// Actualizar mes con año (últimos 2 dígitos)
		const monthName = AonDateUtils.monthName(centerWeek.start);
		const year = centerWeek.start.getFullYear().toString().slice(-2);
		this.headerMonth.textContent = `${monthName.toUpperCase()} ${year}`;

		// Calcular horas totales del mes (trabajadas / esperadas)
		const monthHours = this.calculateMonthHours(centerWeek.start);
		this.headerMonthHours.innerHTML = `Mes: ${monthHours}`;

		// Actualizar rango: del día X al día Y
		const firstDay = centerWeek.days[0].date;
		const lastDay = centerWeek.days[6].date;

		const firstDayNum = String(firstDay.getDate()).padStart(2, '0');
		const lastDayNum = String(lastDay.getDate()).padStart(2, '0');

		const firstDayMonthNum = String(firstDay.getMonth() + 1).padStart(2, '0');
		const lastDayMontNum = String(lastDay.getMonth() + 1).padStart(2, '0');

		this.headerRange.textContent = `${firstDayNum}/${firstDayMonthNum} – ${lastDayNum}/${lastDayMontNum}`;

		// Calcular horas totales de la semana (trabajadas / esperadas)
		const weekHours = this.calculateWeekHours(centerWeek);
		this.headerWeekHours.innerHTML = `Sem.: ${weekHours}`;
	}

	/* ---------------- CALCULATE HOURS ---------------- */
	calculateMonthHours(centerDate) {
		if (!centerDate) return `0:00 h / <span class="day-diff-hours positive">+ 0:00 h</span>`;

		const year = centerDate.getFullYear();
		const month = centerDate.getMonth();

		const start = new Date(year, month, 1);
		const end = new Date(year, month + 1, 0);

		let totalWorked = 0;
		let totalExpected = 0;

		for (let d = new Date(start); d <= end; d.setDate(d.getDate() + 1)) {
			const day = new Date(d);
			day.setHours(0, 0, 0, 0);

			const dateKey = AonDateUtils.format(day, "YYYY-MM-DD");
			const dayOfWeek = day.getDay();

			// Horas trabajadas
			const dayEventData = this._events.get(dateKey);
			let workedTime = dayEventData ? Number(dayEventData.dayData.time || 0) : 0;

			// Horas esperadas base
			const hoursForDay = (this._workingDaysHours && this._workingDaysHours.length === 7)
				? (this._workingDaysHours[dayOfWeek] || 0)
				: 0;
			let expectedTime = hoursForDay * 60 * 60 * 1000;

			const dayTypeContract = this._daysTypeContract.get(dateKey);

			// Vacaciones, permisos retribuidos, ITs (habra que filtrar dayTypeContract.source si queremos quitar alguno )
			if (dayTypeContract && (dayTypeContract.source === "IT" || dayTypeContract.source === "PAID_LEAVE" || dayTypeContract.source === "HOLIDAYS")) {
				// Si el día es laborable (_workingDays[dayOfWeek] === 0) y HOLIDAYS → sumar horas esperadas
				if (this._workingDays && this._workingDays[dayOfWeek] !== 0) {
					//expectedTime = hoursForDay * 60 * 60 * 1000;
					workedTime = hoursForDay * 60 * 60 * 1000;
				} else {
					// expectedTime = 0;
					workedTime = hoursForDay * 60 * 60 * 1000;
				}
			}

			totalWorked += workedTime;
			totalExpected += expectedTime;
		}

		const workedStr = timeHourShort(totalWorked);

		// Diferencia en minutos
		const diffTime = totalWorked - totalExpected;
		const diffHours = this.formatDiffTime(diffTime);

		return `${workedStr} h / <span class="day-diff-hours ${diffHours.includes('-') ? 'negative' : 'positive'}">${diffHours} h</span>`;
	}

	calculateWeekHours(week) {
		if (!week || !week.days || week.days.length === 0) {
			return `0:00 h <span class="day-diff-hours">( + 0:00 h )</span>`;
		}

		let totalWorked = 0;
		let totalExpected = 0;

		week.days.forEach(dayObj => {
			const day = new Date(dayObj.date);
			day.setHours(0, 0, 0, 0);

			const dateKey = dayObj.key;
			const dayOfWeek = day.getDay();

			// Horas trabajadas
			const dayEventData = this._events.get(dateKey);
			let workedTime = dayEventData ? Number(dayEventData.dayData.time || 0) : 0;

			// Horas esperadas base
			const hoursForDay = (this._workingDaysHours && this._workingDaysHours.length === 7)
				? (this._workingDaysHours[dayOfWeek] || 0)
				: 0;
			let expectedTime = hoursForDay * 60 * 60 * 1000;

			const dayTypeContract = this._daysTypeContract.get(dateKey);

			// Vacaciones, permisos retribuidos, ITs (habra que filtrar dayTypeContract.source si queremos quitar alguno )
			if (dayTypeContract && (dayTypeContract.source === "IT" || dayTypeContract.source === "PAID_LEAVE" || dayTypeContract.source === "HOLIDAYS")) {
				// NUEVA REGLA:
				// Si el día es laborable (_workingDays[dayOfWeek] === 0) y HOLIDAYS → sumar horas esperadas
				if (this._workingDays && this._workingDays[dayOfWeek] !== 0) {
					//expectedTime = hoursForDay * 60 * 60 * 1000;
					workedTime = hoursForDay * 60 * 60 * 1000;
				} else {
					//expectedTime = 0;
					workedTime = hoursForDay * 60 * 60 * 1000;
				}
			}

			totalWorked += workedTime;
			totalExpected += expectedTime;
		});

		const workedStr = timeHourShort(totalWorked);

		// Diferencia en minutos
		const diffTime = totalWorked - totalExpected;
		const diffHours = this.formatDiffTime(diffTime);

		return `${workedStr} h / <span class="day-diff-hours ${diffHours.includes('-') ? 'negative' : 'positive'}">${diffHours} h</span>`;
	}

	/* ---------------- PUBLIC API ---------------- */
	goToToday(smooth) {
		this.scrollToToday(smooth);
	}

	goToDate(date) {
		const targetStart = AonDateUtils.startOfWeek(date);
		const diffMs = targetStart - this.baseDate;
		const diffDays = Math.floor(diffMs / (1000 * 60 * 60 * 24));
		const offset = Math.floor(diffDays / 7);

		// Asegurarse de que la semana está renderizada
		this.mountWeek(offset);

		// Buscar el elemento
		const weekData = this.visibleWeeks.get(offset);
		if (weekData) {
			weekData.element.scrollIntoView({ behavior: 'auto', block: 'start' });
		}
	}

	/* ---------------- RELOAD DATA ---------------- */
	async reload() {
		// Resetear flag de carga inicial
		this._initialLoadComplete = false;

		// Limpiar datos cargados
		this._events.clear();
		this._eventsContract.clear();
		this._loadedMonths.clear();

		// Recargar eventos iniciales
		await this.loadInitialEvents();
		
		this.scrollToToday();

		// Marcar como completo nuevamente
		setTimeout(() => {
			this._initialLoadComplete = true;
			console.log('Recarga completa - checkLoadMonthsOnScroll activo');
		}, 1500); // Aumentado para consistencia
	}

	startLoading() {
		let el = this.getElement('aonModuleLoader');
		if (el) el.startLoading();
	}

	stopLoading() {
		let el = this.getElement('aonModuleLoader');
		if (el) el.stopLoading();
	}
}

window.customElements.define("aon-agenda-all-days", AonAgendaAllDays);