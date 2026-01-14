import { AonElement } from "../../../components/AonElement.js";
import { getTaskHoldersUser } from "../../../services/taskHolderService.js";
import { getTaskHolderTimeControl } from "../../../services/timeControlService.js";
import { AonDateUtils } from "../../utils/AonDateUtils.js";
import { timeHour } from "./utils.js";

export class AonAgendaAllDays extends AonElement {
	
	_taskHolder;
	_taskHolderName;
	_events = new Map(); // key: "YYYY-MM-DD" => value: array de eventos
	
	async connectedCallback() {
		this.initState();
		await this.build();
		this.renderInitial();
		this.attachEventListeners();
		
		// Cargar eventos si existe taskHolder
		await this.loadEvents();
	}

	/* ---------------- STATE ---------------- */
	initState() {
		// Fecha base: inicio de la semana actual
		const today = new Date();
		this.baseDate = AonDateUtils.startOfWeek(today);

		// Control de virtualización
		this.weekHeight = 0;
		this.visibleWeeks = new Map(); // offset => {element, week}

		// Rango de renderizado
		this.renderRange = 52; // Total de semanas a renderizar hacia atrás
	}

	/* ---------------- BUILD ---------------- */
	async build() {
		this.innerHTML = `
	      <header>
	        <div class="header-left">
	          <div class="month"></div>
	          <div class="range"></div>
	        </div>
	        <div class="task-holder-name"></div>
	      </header>
	      <div class="scroll"></div>
	    `;
	    
		this.headerMonth = this.querySelector(".month");
		this.headerRange = this.querySelector(".range");
		this.taskHolderNameEl = this.querySelector(".task-holder-name");
		this.scrollEl = this.querySelector(".scroll");

		let userTaskHolders = await getTaskHoldersUser();
		if (userTaskHolders.length > 0) {
			//console.log('TaskHolder', userTaskHolders[0]);
			this._taskHolder = userTaskHolders[0].id;
			this._taskHolderName = userTaskHolders[0].name;
			this.taskHolderNameEl.textContent = this._taskHolderName;
		}
	}

	/* ---------------- LOAD EVENTS ---------------- */
	async loadEvents() {
		if (!this._taskHolder) return;
		
		// Calcular fechas: desde 1 año atrás hasta 1 mes adelante
		const today = new Date();
		
		const startDate = new Date(today);
		startDate.setFullYear(startDate.getFullYear() - 1); // 1 año atrás desde hoy
		
		const endDate = new Date(today);
		endDate.setMonth(endDate.getMonth() + 1); // 1 mes adelante desde hoy
		
		// Formatear fechas para el filtro
		const startDateStr = AonDateUtils.format(startDate, "YYYY-MM-DD");
		const endDateStr = AonDateUtils.format(endDate, "YYYY-MM-DD");
		
		console.log("Rango de fechas:", startDateStr, "hasta", endDateStr);
		
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
			//console.log("Eventos cargados:", datos);
			
			// Procesar y guardar eventos por fecha
			this.processEvents(datos);
			
			// Re-renderizar las semanas visibles con los eventos
			this.refreshWeeks();
		} catch (error) {
			console.error("Error cargando eventos:", error);
		}
	}
	
	/* ---------------- PROCESS EVENTS ---------------- */
	processEvents(datos) {
		// Limpiar eventos anteriores
		this._events.clear();
		
		if (!datos || !Array.isArray(datos)) return;
		
		datos.forEach(dayData => {
			// Convertir start_date (timestamp en milisegundos) a fecha
			const date = new Date(dayData.start_date);
			const dateKey = AonDateUtils.format(date, "YYYY-MM-DD");
			
			// Guardar el día completo con sus detalles (incluso si no tiene detalles)
			this._events.set(dateKey, {
				dayData: {
					start_date: dayData.start_date,
					end_date: dayData.end_date,
					time: dayData.time, // Tiempo total del día
					status: dayData.status
				},
				details: dayData.detail || []
			});
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
		// Renderizar semanas en orden DESCENDENTE (de más reciente a más antigua)
		// Las semanas se añaden en orden: 4, 3, 2, 1, 0, -1, -2, ..., -52
		// Esto hace que el scroll funcione correctamente:
		// - Arriba: futuro (offset positivo)
		// - Abajo: pasado (offset negativo)
		
		// Primero renderizar las semanas futuras (de 4 a 1)
		for (let i = 4; i >= 1; i--) {
			this.mountWeek(i);
		}
		
		// Luego la semana actual (0)
		this.mountWeek(0);
		
		// Finalmente las semanas pasadas (de -1 a -52)
		for (let i = -1; i >= -this.renderRange; i--) {
			this.mountWeek(i);
		}

		// Esperar a que el DOM se actualice
		requestAnimationFrame(() => {
			this.measureWeekHeight();

			// Scroll a la semana actual (offset 0)
			// Buscar el elemento de la semana 0
			const currentWeekData = this.visibleWeeks.get(0);
			if (currentWeekData) {
				currentWeekData.element.scrollIntoView({ behavior: 'auto', block: 'start' });
			}

			// Forzar actualización del header
			this.updateHeader();
		});
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

		// INVERTIR el orden de los días dentro de la semana
		// Para que vayan de más reciente (domingo) a más antiguo (lunes)
		const reversedDays = [...week.days].reverse();

		reversedDays.forEach(day => {
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
			
			if (eventsHtml === '') {
				dayEl.innerHTML = `
			        <div class="day-header">
			          <div class="day-header-left">
			            <span class="day-name">${AonDateUtils.dayName(day.date)}</span>
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
			            <span class="day-name">${AonDateUtils.dayName(day.date)}</span>
			            <span class="day-number">${day.date.getDate()}</span>
			          </div>
			          <div class="day-header-right">
			            <span class="total-hours">H. Totales: ${totalHours}</span>
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
		
		return `<div class="events">
			${sortedEvents.map(event => {
				// Formatear hora
				const eventDate = new Date(event.date);
				const hours = String(eventDate.getHours()).padStart(2, '0');
				const minutes = String(eventDate.getMinutes()).padStart(2, '0');
				const timeStr = `${hours}:${minutes}`;
				
				// Determinar clase CSS según el status
				let statusClass = '';
				if (event.status === 'in') {
					statusClass = 'in';
				} else if (event.status === 'out') {
					statusClass = 'out';
				} else if (event.status === 'pause') {
					statusClass = 'pause';
				}
				
				return `
					<div class="event ${statusClass}">
						<span class="time">${timeStr}</span>
						<span class="reason">${event.reasonValue || event.reason || ''}</span>
					</div>
				`;
			}).join('')}
		</div>`;
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
	}

	async checkLoadMore() {
		const scrollTop = this.scrollEl.scrollTop;
		const scrollHeight = this.scrollEl.scrollHeight;
		const clientHeight = this.scrollEl.clientHeight;

		const scrollBottom = scrollTop + clientHeight;
		const distanceFromTop = scrollTop;
		const distanceFromBottom = scrollHeight - scrollBottom;

		// Si estamos cerca del top (futuro), cargar más semanas futuras
		if (distanceFromTop < clientHeight * 2) {
			const maxOffset = Math.max(...this.visibleWeeks.keys());
			// Limitar a máximo 4 semanas en el futuro
			if (maxOffset < 4) {
				// Añadir semanas hacia el futuro al principio
				for (let i = maxOffset + 1; i <= Math.min(maxOffset + 4, 4); i++) {
					if (!this.visibleWeeks.has(i)) {
						const week = this.createWeek(i);
						const weekEl = this.renderWeek(week);
						this.visibleWeeks.set(i, { element: weekEl, week });
						// Insertar al principio
						this.scrollEl.insertBefore(weekEl, this.scrollEl.firstChild);
					}
				}
			}
		}

		// Si estamos cerca del bottom (pasado), cargar más semanas pasadas
		if (distanceFromBottom < clientHeight * 2) {
			const minOffset = Math.min(...this.visibleWeeks.keys());
			// Cargar 10 semanas más hacia el pasado
			for (let i = minOffset - 1; i >= minOffset - 10; i--) {
				this.mountWeek(i);
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
		const year = centerWeek.start.getFullYear().toString().slice(-2); // Últimos 2 dígitos
		this.headerMonth.textContent = `${monthName.toUpperCase()} ${year}`;

		// Actualizar rango: del día X al día Y
		const firstDay = centerWeek.days[0].date;
		const lastDay = centerWeek.days[6].date;

		const firstDayNum = firstDay.getDate();
		const lastDayNum = lastDay.getDate();

		// Formato: "2–8"
		this.headerRange.textContent = `${firstDayNum}–${lastDayNum}`;
	}

	/* ---------------- PUBLIC API ---------------- */
	goToToday() {
		const currentWeekData = this.visibleWeeks.get(0);
		if (currentWeekData) {
			currentWeekData.element.scrollIntoView({ behavior: 'smooth', block: 'start' });
		}
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
			weekData.element.scrollIntoView({ behavior: 'smooth', block: 'start' });
		}
	}
	
	/* ---------------- RELOAD DATA ---------------- */
	async reload() {
		// Método público para recargar todos los eventos
		await this.loadEvents();
	}
}

window.customElements.define("aon-agenda-all-days", AonAgendaAllDays);