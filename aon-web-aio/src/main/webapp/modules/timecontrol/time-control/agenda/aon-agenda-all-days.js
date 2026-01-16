import { AonElement } from "../../../../components/AonElement.js";
import { getTaskHoldersUser } from "../../../../services/taskHolderService.js";
import { getTaskHolderTimeControl } from "../../../../services/timeControlService.js";
import { AonDateUtils } from "../../../utils/AonDateUtils.js";
import { timeHour } from ".././utils.js";
import { AonCalendarMenu } from "./aon-calendar-menu.js";


export class AonAgendaAllDays extends AonElement {
	
	_taskHolder;
	_taskHolderName;
	_events = new Map(); // key: "YYYY-MM-DD" => value: array de eventos
	_loadedMonths = new Set(); // Para trackear qué meses ya se han cargado
	_isLoading = false; // Flag para evitar múltiples cargas simultáneas
	_initialLoadComplete = false; // Flag para evitar cargas durante scroll inicial
	
	async connectedCallback() {
		this.initState();
		await this.build();
		this.renderInitial();
		this.attachEventListeners();
		
		// Configurar el menú calendario
    	this.setupCalendarMenu();
		
		// Cargar eventos del mes actual inicialmente
		await this.loadInitialEvents();
		
		// DESPUÉS de cargar los eventos, hacer scroll al día de hoy
		setTimeout(() => {
			this.scrollToToday();
		}, 200);
		
		// Activar checkLoadMonthsOnScroll después de 2 segundos para estar seguros
		setTimeout(() => {
			this._initialLoadComplete = true;
			console.log('Carga inicial completa - checkLoadMonthsOnScroll ahora activo');
		}, 2000);
	}
	
	setupCalendarMenu() {
	    setTimeout(() => {
	        if (this.aonCalendarMenu && typeof this.aonCalendarMenu.setOnTodayClick === 'function') {
	            this.aonCalendarMenu.setOnTodayClick(() => {
	                this.goToToday(true);
	            });
	        }
	    }, 200);
	}

	/* ---------------- STATE ---------------- */
	initState() {
		// Fecha base: inicio de la semana actual
		const today = new Date();
		this.baseDate = AonDateUtils.startOfWeek(today);

		// Control de virtualización
		this.weekHeight = 0;
		this.visibleWeeks = new Map(); // offset => {element, week}

		// Rango de renderizado inicial reducido - solo 8 semanas hacia atrás (aprox 2 meses)
		this.renderRange = 8;
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
	
		let userTaskHolders = await getTaskHoldersUser();
		if (userTaskHolders.length > 0) {
			this._taskHolder = userTaskHolders[0].id;
			this._taskHolderName = userTaskHolders[0].name;
		}
		
		this.aonCalendarMenu = new AonCalendarMenu();
		this.aonCalendarMenu.id = 'aonCalendarMenu';
	    this.appendChild(this.aonCalendarMenu);
	    
}

	/* ---------------- LOAD EVENTS ---------------- */
	async loadInitialEvents() {
		if (!this._taskHolder) return;
		
		// Cargar mes actual + 1 mes anterior + 1 mes siguiente
		const today = new Date();
		await this.loadMonthEvents(today); // Mes actual
		
		const prevMonth = new Date(today);
		prevMonth.setMonth(prevMonth.getMonth() - 1);
		await this.loadMonthEvents(prevMonth); // Mes anterior
		
		const nextMonth = new Date(today);
		nextMonth.setMonth(nextMonth.getMonth() + 1);
		await this.loadMonthEvents(nextMonth); // Mes siguiente
	}
	
	async loadMonthEvents(date) {
		if (!this._taskHolder || this._isLoading) return;
		
		// Generar clave del mes (YYYY-MM)
		const monthKey = `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}`;
		
		// Si ya se cargó este mes, salir
		if (this._loadedMonths.has(monthKey)) {
			return;
		}
		
		this._isLoading = true;
		this.startLoading();
		
		// Calcular primer y último día del mes
		const startDate = new Date(date.getFullYear(), date.getMonth(), 1);
		const endDate = new Date(date.getFullYear(), date.getMonth() + 1, 0);
		
		// Formatear fechas para el filtro
		const startDateStr = AonDateUtils.format(startDate, "YYYY-MM-DD");
		const endDateStr = AonDateUtils.format(endDate, "YYYY-MM-DD");
		
		console.log(`Cargando mes: ${monthKey} (${startDateStr} hasta ${endDateStr})`);
		
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
			console.log(`Eventos cargados para ${monthKey}:`, datos.length);
			
			// Procesar y guardar eventos por fecha
			this.processEvents(datos);
			
			// Marcar mes como cargado
			this._loadedMonths.add(monthKey);
			
			// Re-renderizar las semanas visibles con los eventos
			this.refreshWeeks();
		} catch (error) {
			console.error(`Error cargando eventos del mes ${monthKey}:`, error);
		} finally {
			this._isLoading = false;
			this.stopLoading();
		}
	}
	
	/* ---------------- PROCESS EVENTS ---------------- */
	processEvents(datos) {
		if (!datos || !Array.isArray(datos)) return;
		
		datos.forEach(dayData => {
			// Convertir start_date (timestamp en milisegundos) a fecha
			const date = new Date(dayData.start_date);
			const dateKey = AonDateUtils.format(date, "YYYY-MM-DD");
			
			// Solo guardar si no existe ya (para no sobrescribir)
			if (!this._events.has(dateKey)) {
				this._events.set(dateKey, {
					dayData: {
						start_date: dayData.start_date,
						end_date: dayData.end_date,
						time: dayData.time,
						status: dayData.status
					},
					details: dayData.detail || []
				});
			}
		});
	}
	
	/* ---------------- REFRESH WEEKS ---------------- */
	refreshWeeks() {
		// Re-renderizar todas las semanas visibles con los nuevos eventos
		this.visibleWeeks.forEach((data, offset) => {
			const newWeekEl = this.renderWeek(data.week);
			data.element.replaceWith(newWeekEl);
			this.visibleWeeks.set(offset, { element: newWeekEl, week: data.week });
		});
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
		const todayKey = AonDateUtils.format(today, "YYYY-MM-DD");
		
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
		}, 600);
	}

	/* ---------------- WEEK MANAGEMENT ---------------- */
	mountWeek(offset) {
		if (this.visibleWeeks.has(offset)) return;

		const week = this.createWeek(offset);
		const weekEl = this.renderWeek(week);

		this.visibleWeeks.set(offset, { element: weekEl, week });

		// Añadir al DOM siempre al final (orden descendente)
		this.scrollEl.appendChild(weekEl);
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
			
			// Marcar fin de semana (sábado = 6, domingo = 0)
			const dayOfWeek = dayDate.getDay();
			if (dayOfWeek === 0 || dayOfWeek === 6) {
				dayEl.classList.add("weekend");
			}

			// Obtener eventos del día
			const dayEventData = this._events.get(day.key);
			
			// Mostrar todos los días, tengan eventos o no
			let totalHours = "0h 0m";
			let eventsHtml = '';
			
			if (dayEventData) {
				totalHours = timeHour(Number(dayEventData.dayData.time));
				eventsHtml = this.renderDayEvents(dayEventData.details);
			}
			
			// Formatear nombre del día (3 primeras letras + punto)
			const dayNameFull = AonDateUtils.dayName(day.date);
			const dayNameShort = dayNameFull.substring(0, 3) + '.';
			
			if (eventsHtml === '') {
				dayEl.innerHTML = `
			        <div class="day-header">
			          <div class="day-header-left">
			            <span class="day-name">${dayNameShort}</span>
			            <span class="day-number">${day.date.getDate()}</span>
			          </div>
			        </div>
			        <div class="day-content">
			          ${eventsHtml}
			        </div>
			      `;
			} else {
				dayEl.innerHTML = `
			        <div class="day-header">
			          <div class="day-header-left">
			            <span class="day-name">${dayNameShort}</span>
			            <span class="day-number">${day.date.getDate()}</span>
			          </div>
			          <div class="day-header-right">
			            <span class="total-hours">${totalHours} h</span>
			          </div>
			        </div>
			        <div class="day-content">
			          ${eventsHtml}
			        </div>
			      `;	
			}
		      
			el.appendChild(dayEl);
		});

		return el;
	}
	
	/* ---------------- RENDER EVENTS ---------------- */
	renderDayEvents(events) {
		if (!events || events.length === 0) {
			return '';
		}
		
		// Ordenar eventos por fecha (hora)
		const sortedEvents = [...events].sort((a, b) => a.date - b.date);
		
		// Agrupar eventos en tramos
		const tramos = this.groupEventsIntoTramos(sortedEvents);
		
		return `<div class="events">
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
		</div>`;
	}
	
	/* ---------------- GROUP EVENTS INTO TRAMOS ---------------- */
	groupEventsIntoTramos(events) {
		const tramos = [];
		let i = 0;
		
		while (i < events.length) {
			const currentEvent = events[i];
			
			// Solo procesar eventos que son 'in' o 'pause'
			if (currentEvent.status === 'in') {
				// Verificar si hay múltiples 'in' seguidos, quedarnos con el más tardío
				let startIndex = i;
				let nextIndex = i + 1;
				
				while (nextIndex < events.length && events[nextIndex].status === 'in') {
					startIndex = nextIndex;
					nextIndex++;
				}
				
				const startEvent = events[startIndex];
				
				// Buscar el siguiente evento que sea 'out' o 'pause'
				let endIndex = startIndex + 1;
				while (endIndex < events.length && events[endIndex].status === 'in') {
					endIndex++;
				}
				
				if (endIndex < events.length) {
					const endEvent = events[endIndex];
					
					// Crear tramo in → pause/out
					tramos.push({
						start: startEvent,
						end: endEvent
					});
					
					// Si el endEvent es 'pause', crear otro tramo desde 'pause' hasta el siguiente evento
					if (endEvent.status === 'pause') {
						let afterPauseIndex = endIndex + 1;
						if (afterPauseIndex < events.length) {
							// Buscar el siguiente out o in
							const afterPauseEvent = events[afterPauseIndex];
							
							tramos.push({
								start: endEvent,
								end: afterPauseEvent
							});
							
							i = afterPauseIndex + 1;
						} else {
							// Pause sin evento siguiente
							i = endIndex + 1;
						}
					} else {
						// Era un 'out', continuar desde ahí
						i = endIndex + 1;
					}
				} else {
					// No hay más eventos, el tramo queda abierto
					i = startIndex + 1;
				}
			}
			// Si es 'pause' sin un 'in' previo en este tramo
			else if (currentEvent.status === 'pause') {
				let nextIndex = i + 1;
				if (nextIndex < events.length) {
					tramos.push({
						start: currentEvent,
						end: events[nextIndex]
					});
					i = nextIndex + 1;
				} else {
					// Pause sin evento siguiente
					i++;
				}
			}
			// Si es 'out', simplemente avanzar (no nos interesa)
			else {
				i++;
			}
		}
		
		return tramos;
	}

	/* ---------------- EVENT LISTENERS ---------------- */
	attachEventListeners() {
		this.scrollEl.addEventListener("scroll", () => {
			this.handleScroll();
		}, { passive: true });

		// Medir altura después de un pequeño delay
		setTimeout(() => {
			this.measureWeekHeight();
		}, 100);
	}

	handleScroll() {
		this.updateHeader();
		this.checkLoadMore();
		this.checkLoadMonthsOnScroll();
	}

	async checkLoadMore() {
		const scrollTop = this.scrollEl.scrollTop;
		const scrollHeight = this.scrollEl.scrollHeight;
		const clientHeight = this.scrollEl.clientHeight;

		const scrollBottom = scrollTop + clientHeight;
		const distanceFromTop = scrollTop;
		const distanceFromBottom = scrollHeight - scrollBottom;

		// Si estamos cerca del top (pasado), cargar más semanas pasadas
		if (distanceFromTop < clientHeight * 2) {
			const minOffset = Math.min(...this.visibleWeeks.keys());
			// Cargar 10 semanas más hacia el pasado al principio
			for (let i = minOffset - 10; i < minOffset; i++) {
				if (!this.visibleWeeks.has(i)) {
					const week = this.createWeek(i);
					const weekEl = this.renderWeek(week);
					this.visibleWeeks.set(i, { element: weekEl, week });
					// Insertar al principio
					this.scrollEl.insertBefore(weekEl, this.scrollEl.firstChild);
				}
			}
		}

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
		
		const scrollTop = this.scrollEl.scrollTop;
		const scrollHeight = this.scrollEl.scrollHeight;
		const clientHeight = this.scrollEl.clientHeight;
		const scrollBottom = scrollTop + clientHeight;
		
		// Obtener las semanas visibles en el viewport
		const visibleWeeks = [];
		this.visibleWeeks.forEach((data, offset) => {
			const weekTop = data.element.offsetTop;
			const weekBottom = weekTop + data.element.offsetHeight;
			
			// Si la semana está visible en el viewport
			if (weekBottom >= scrollTop && weekTop <= scrollBottom) {
				visibleWeeks.push({ week: data.week, top: weekTop, offset });
			}
		});
		
		if (visibleWeeks.length === 0) return;
		
		// Ordenar semanas visibles por posición (top más pequeño = más arriba)
		visibleWeeks.sort((a, b) => a.top - b.top);
		
		// Cargar meses de las semanas visibles
		const monthsToLoad = new Set();
		visibleWeeks.forEach(({ week }) => {
			week.days.forEach(day => {
				const monthKey = `${day.date.getFullYear()}-${String(day.date.getMonth() + 1).padStart(2, '0')}`;
				if (!this._loadedMonths.has(monthKey)) {
					monthsToLoad.add(monthKey);
				}
			});
		});
		
		// Cargar los meses que faltan de las semanas visibles
		for (const monthKey of monthsToLoad) {
			const [year, month] = monthKey.split('-');
			const date = new Date(parseInt(year), parseInt(month) - 1, 1);
			await this.loadMonthEvents(date);
		}
		
		// Precargar UN mes adyacente SOLO si estamos muy cerca del borde
		const distanceFromTop = scrollTop;
		const distanceFromBottom = scrollHeight - scrollBottom;
		
		// Precarga hacia el pasado (arriba): usar la PRIMERA semana visible
		if (distanceFromTop < clientHeight) {
			const topVisibleWeek = visibleWeeks[0].week;
			const topDate = topVisibleWeek.start;
			
			// Cargar el mes anterior solo si no está cargado
			const prevMonth = new Date(topDate);
			prevMonth.setMonth(prevMonth.getMonth() - 1);
			const prevMonthKey = `${prevMonth.getFullYear()}-${String(prevMonth.getMonth() + 1).padStart(2, '0')}`;
			
			if (!this._loadedMonths.has(prevMonthKey)) {
				await this.loadMonthEvents(prevMonth);
			}
		}
		
		// Precarga hacia el futuro (abajo): usar la ÚLTIMA semana visible
		if (distanceFromBottom < clientHeight) {
			const bottomVisibleWeek = visibleWeeks[visibleWeeks.length - 1].week;
			const bottomDate = bottomVisibleWeek.start;
			
			// Cargar el mes siguiente solo si no está cargado
			const nextMonth = new Date(bottomDate);
			nextMonth.setMonth(nextMonth.getMonth() + 1);
			const nextMonthKey = `${nextMonth.getFullYear()}-${String(nextMonth.getMonth() + 1).padStart(2, '0')}`;
			
			if (!this._loadedMonths.has(nextMonthKey)) {
				await this.loadMonthEvents(nextMonth);
			}
		}
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

		// Calcular horas totales del mes
		const monthHours = this.calculateMonthHours(centerWeek.start);
		this.headerMonthHours.textContent = `Mes: ${monthHours} h`;

		// Actualizar rango: del día X al día Y
		const firstDay = centerWeek.days[0].date;
		const lastDay = centerWeek.days[6].date;

		const firstDayNum = String(firstDay.getDate() + 1).padStart(2, '0');
		const lastDayNum = String(lastDay.getDate() + 1).padStart(2, '0');
		
		const firstDayMonthNum = String(firstDay.getMonth() + 1).padStart(2, '0');
		const lastDayMontNum = String(lastDay.getMonth() + 1).padStart(2, '0');

		this.headerRange.textContent = `${firstDayNum}/${firstDayMonthNum} – ${lastDayNum}/${lastDayMontNum}`;
		
		// Calcular horas totales de la semana
		const weekHours = this.calculateWeekHours(centerWeek);
		this.headerWeekHours.textContent = `Sem.: ${weekHours} h`;
	}
	
	/* ---------------- CALCULATE HOURS ---------------- */
	calculateMonthHours(date) {
		const monthKey = `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}`;
		let totalMilliseconds = 0;
		
		// Sumar horas de todos los días del mes
		this._events.forEach((eventData, dateKey) => {
			if (dateKey.startsWith(monthKey)) {
				totalMilliseconds += Number(eventData.dayData.time) || 0;
			}
		});
		
		return timeHour(totalMilliseconds);
	}
	
	calculateWeekHours(week) {
		let totalMilliseconds = 0;
		
		// Sumar horas de todos los días de la semana
		week.days.forEach(day => {
			const eventData = this._events.get(day.key);
			if (eventData) {
				totalMilliseconds += Number(eventData.dayData.time) || 0;
			}
		});
		
		return timeHour(totalMilliseconds);
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
		this._loadedMonths.clear();
		
		// Recargar eventos iniciales
		await this.loadInitialEvents();
		
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