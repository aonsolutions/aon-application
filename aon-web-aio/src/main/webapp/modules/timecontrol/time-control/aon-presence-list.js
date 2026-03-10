import { AonElement } from "../../../components/AonElement.js";
import { getPeriod, getStatus, getTimeControlList, getTimeControlExcel, getTimeControlPdf } from "../../../services/service.js";
import { isEmptyObject, setValueName, sortBy, waitEl } from "../../../services/utils.js";
import { setAttributes } from "../../../services/utilsComponents.js";
import { iconAddLocation, PRESENCE_FILTER, SigninSidenav, SIGNIN_VIEWS } from "../signinEnums.js";
import { dateCustomDayHour, modalReport, StringTwoLetters, timeHour } from "./utils.js";
import { CONSTANT, EVENT, MSG, TAG } from "../../../environments/environments.js";
import { AonMobileList } from "../../../components/aon-mobile-list.js";
import { AonTable } from "../../../components/aon-table.js";
import { AonIconButton } from "../../../components/aon-icon-button.js";
import { AonSwitch } from "../../../components/aon-switch.js";
import { AonDateUtils } from "../../utils/AonDateUtils.js";
import Apps, { TIMECONTROL } from "../../../services/app.js";
import * as LS from '../../../services/localStorageService.js';

export class AonPresenceList extends AonElement {
	TABLE_ID;

	searchFilter;
	_list;

	_timeControlFilter;
	_filter;

	static get observedAttributes() {
		return [];
	}

	get id() {
		return this.getAttribute(CONSTANT.ID);
	}

	set id(id) {
		this.setAttribute(CONSTANT.ID, id);
	}

	constructor() {
		super();
	}

	connectedCallback() {
		this.initialize();
		this.build();
	}

	initialize() {
		this.id = this.id || SIGNIN_VIEWS.AON_PRESENCE_LIST;
		this.TABLE_ID = this.id + "Table";
		this._list = [];

		this.applicationEl = this.getApplication();
		this.applicationParentEl = this.getApplicationParent();

		this.applicationEl.addToolbarTitle("Presencia");

		this.filterInit();
	}

	filterInit() {
		const periodEnums = SigninSidenav.PERIOD;
		const period = getPeriod(this.applicationParentEl.isEmployee() ? periodEnums.THIS_WEEK.id : periodEnums.TODAY.id);
		this._filter = {
			group: "DAY",
			period: period.value,
			startDate: period.startDate,
			endDate: period.endDate,
			active: true,
			withData: true,
			search: ''
		}
	}

	build() {
		if (this._timeControlFilter) {
			this._filter = { ...this._filter, ...this._timeControlFilter };
		}

		this.paintView();
		this.buildToolbar();
		this.getTable();
	}

	paintView() {
		let aonTable = this.isMobile() ? new AonMobileList() : new AonTable();
		aonTable.id = this.TABLE_ID;
		this.appendChild(aonTable);
	}

	buildToolbar() {
		this.applicationEl.removeToolbarOptions();

		if (!this.applicationParentEl.isEmployee()) {
			if (this.isMobile()) {
				this.applicationEl.addFloatOption(SigninSidenav.ADD, () => this.aonEventAdd());
			} else {
				this.applicationEl.addToolbarOption2(SigninSidenav.ADD, () => this.aonEventAdd());
			}
		}

		if (!this.isMobile()) {
			this.applicationEl.addToolbarOption2(SigninSidenav.REPORT, () => modalReport(this.applicationEl, this, "excel"));
		}

		this.buildToolbarSearch();

	}

	buildToolbarSearch() {
		let btnSearch = this.applicationEl.addSearchOption(LS.isFutureTheme());

		let timeOut = null;

		btnSearch.addEventListener(EVENT.SEARCH_NEW, ({ detail }) => {
			clearTimeout(timeOut);
			timeOut = setTimeout(() => {
				this._list = [];
				this.searchFilter = detail.search;

				let newFilter = {
					active: detail.active === 'true',
					search: detail.search,
					period: detail.period,
					startDate: detail.startDate,
					endDate: detail.endDate,
					withData: detail.withData === 'true'
				}
				if (newFilter && newFilter.period) {
					newFilter = { ...newFilter, ...getPeriod(newFilter.period) };
				}

				this._filter = { ...this._filter, ...newFilter };

				this._list = [];
				this.getTable();

			});
		});

		btnSearch.addEventListener(EVENT.RESET_FILTER, ({ detail }) => {
			clearTimeout(timeOut);
			timeOut = setTimeout(() => {

				this._list = [];
				this.searchFilter = '';

				this.filterInit();

				let searchInput = this.getElement('aonSigninToolbarHeaderToolSectionSearchSearchInput');
				if (searchInput) searchInput.value = '';

				setValueName('period', this._filter.period);
				setValueName('startDate', this._filter.startDate);
				setValueName('endDate', this._filter.endDate);

				let aonSwitchFilter = this.getElement('aonSwitchFilter');
				if (aonSwitchFilter) aonSwitchFilter.checked = true;
				let aonWithDataSwitchFilter = this.getElement('aonWithDataSwitchFilter');
				if (aonWithDataSwitchFilter) aonWithDataSwitchFilter.checked = true;

				this._list = [];
				this.getTable();
			});
		});

		// Search Inputs

		let inputsFilter = [
			...PRESENCE_FILTER,
			{
				type: CONSTANT.HTML_ELEMENT,
				element: new AonSwitch(),
				id: "aonSwitchFilter",
				name: "active",
				title: "Operarios activos",
				checked: this._filter?.active
			},
			{
				type: CONSTANT.HTML_ELEMENT,
				element: new AonSwitch(),
				id: "aonWithDataSwitchFilter",
				name: "withData",
				title: "Con Datos",
				checked: this._filter?.withData
			}
		];

		btnSearch.buildOptionsFilter(inputsFilter);

		this.searchValueDefault();
	}

	searchValueDefault() {
		let periodEl = this.getElement("period");

		if (periodEl) {
			periodEl.setOptions(getPeriod());
			periodEl.addEventListener(EVENT.CHANGE, ({ detail }) => {
				if (detail) {
					const { startDate, endDate } = detail;
					setValueName('startDate', startDate);
					setValueName('endDate', endDate);
				}
			});
		}

		this.getElement("startDate").addEventListener(EVENT.CHANGE, () => periodEl.value = "personalized");
		this.getElement("endDate").addEventListener(EVENT.CHANGE, () => periodEl.value = "personalized");

		setValueName('period', this._filter.period);
		setValueName('startDate', this._filter.startDate);
		setValueName('endDate', this._filter.endDate);
	}

	async getTable() {
		this.applicationEl = await waitEl("#aonSignin");
		this.applicationEl.startLoader();
		if (this.isMobile()) await this.getTableMobile();
		else await this.getTableDesk();
		this.applicationEl.stopLoader();
	}

	async getTableDesk() {
		const aonTable = this.getElement(this.TABLE_ID);

		if (aonTable) {
			aonTable.removeColumns();
			aonTable.addColumn("", "string", "lettersHtml", "5%");
			aonTable.addColumn(MSG.NAME, "string", "name", "28%");
			aonTable.addColumn(MSG.PERIOD, "string", "periodName", "25%");
			aonTable.addColumn(MSG.DURATION, "", "duration", "5%");

			aonTable.addColumn("Ult. Estado", "", "status", "12%");
			aonTable.addColumn("Motivo", "string", "reason", "15%");
			aonTable.addColumn("Ult. Ubicación", "string", "nameLocation", "15%");
			try {
				const resp = await this.getData();
				aonTable.removeRows();
				resp.map((res) => {
					let lastStatus = res.last_date ? `${res.textStatus} ${AonDateUtils.setDateTimestampDay(res.last_date)}` : null;
					res.lastStatus = lastStatus;
					if (res.status == "in") {
						let durationMs = this.timeStringToMs(res.duration);
						let elapsedMs = new Date().getTime() - res.last_date;
						durationMs += elapsedMs; // Sumar los milisegundos transcurridos a la duración
						res.duration = this.msToTimeString(durationMs); // Convertir de vuelta a "minutos:segundos"
					}
					// alert(JSON.stringify(res));
					let tr = aonTable.addRow(res, (el) => this.aonEvent(el, res));
					tr.id = "aonTimeControlRow";
				});
			} catch (e) {
				console.log(e);
			}
		}
	}

	async getTableMobile() {
		const aonTable = this.getElement(this.TABLE_ID);
		if (aonTable) {
			try {
				const resp = await this.getData();
				aonTable.removeAllLi();
				resp.map((res, idx) => {
					let subtitle = null;
					if (res.last_date) {
						const dateParse = dateCustomDayHour(res.last_date) || AonDateUtils.setDateTimestamp(res.last_date);
						subtitle = `${res.reason && res.reason.length > 0 ? (res.reason + ' - ') : ''} ${dateParse} <span style="float: right;">${res.nameLocation}</span> `;
					}
					let options = {
						iconHtmlCustom: `${res.lettersHtml} <span style="float: right;color: rgba(0,0,0,.54);">${res.duration}</span>`,
						title: `${res.name}`,
						subtitle
					};
					aonTable.addLi(options, idx, (el) => this.aonEvent(el, res));
				});
			} catch (e) {
				console.log(e);
			}
		}
	}

	dialogReport(button) {
		const left = button.getBoundingClientRect().left;
		let top = button.getBoundingClientRect().top;
		if (this.isMobile()) top = top - 50;

		let options = [{
			name: "Registro de jornada",
			aonIcon: 'excel',
			permission: true,
			backgroundColor: Apps.TIMECONTROL.color,
			fn: () => modalReport(this.applicationEl, this, "excel")
		}, {
			name: "Plantilla fichajes",
			aonIcon: 'aon_pdf',
			permission: true,
			backgroundColor: Apps.TIMECONTROL.color,
			fn: () => modalReport(this.applicationEl, this, "pdf")
		}];

		const d = this.applicationEl.getOptionDialog();
		d.setMenuOptions(options, top, left);
		d.open();
	}

	async getData() {
		let data = [];
		try {
			if (this._list.length) {
				data = this._list;
			} else {
				const datos = await getTimeControlList(this._filter);
				console.log('--------- Aon Presence List ---------', this._filter, datos);
				if (datos) {
					sortBy(datos, 'last_date', 'desc').map(({
						time,
						last_date,
						status,
						coordinates,
						last_location,
						task_holder: { id: taskHolderId, name },
						detail
					}) => {
						const newStatus = status.toLowerCase();
						const lettersName = StringTwoLetters(name);

						const div = this.createElement(TAG.DIV);
						div.id = "aonTimeControlTableDiv";
						div.classList.add("profile-letters", newStatus);
						div.innerText = lettersName;


						const lettersHtml = div.outerHTML;
						const { name: textStatus } = getStatus(newStatus);
						let nameLocation = "";
						if (last_location && last_location.name) {
							nameLocation = last_location.name;
						} else if (!isEmptyObject(coordinates)) {
							let aib = setAttributes(new AonIconButton(), { id: "iconLocation", noHover: "true", icon: iconAddLocation });
							nameLocation = aib.outerHTML;
						}

						let reason = detail && detail.length > 0 ? detail[detail.length - 1].reasonValue : '';

						let period = getPeriod(this._filter.period);
						let statusString = newStatus == 'in' ? 'Entrada' : newStatus == 'pause' ? 'Pausa' : 'Salida';
						let lastDateString = this.msToDateHourMinute(last_date);

						data.push({
							lettersHtml,
							name,
							periodName: period.value == "personalized"
								? (`${period.name} (${AonDateUtils.getDayMonthOrFull(this._filter.startDate)} / ${AonDateUtils.getDayMonthOrFull(this._filter.endDate)})`)
								: period.value == "today" || period.value == "yesterday" ? period.name : (`${period.name} (${AonDateUtils.getDayMonthOrFull(period.startDate)} / ${AonDateUtils.getDayMonthOrFull(period.endDate)})`),

							duration: this.msToHoursMinutes(time),

							status: lastDateString,
							reason,
							nameLocation,

							last_date,
							coordinates,
							last_location,
							taskHolderId,
							textStatus,

						});
					}
					);

					this._list = data;
					if (this.searchFilter) data = this.filterSearch(["name", "nameLocation"], data);
				}
			}
		} catch (e) { console.log(e); }
		return data;
	}

	async getTimeControlExcel(startYear) {
		this.applicationEl.startLoading();
		try {
			let { active } = this._filter;
			await getTimeControlExcel({ startDate: startYear + "-01-01", endDate: startYear + "-12-31", active });
		} catch (error) {
			this.showToast(error);
		}
		this.appcationEl.stopLoading();
	}

	async getTimeControlPdf(startDate) {
		this.applicationEl.startLoading();
		try {
			await getTimeControlPdf({ startDate: startDate });
		} catch (error) {
			this.showToast(error);
		}
		this.applicationEl.stopLoading();
	}

	search() {
		this._list = this.filterSearch(["name", "nameLocation"], this._list);
		this.getTable();
	}

	filterSearch(keys, lists) {
		let list = [];
		if (this.searchFilter && lists.length) {
			list = lists.filter((lt) => keys.some(key => lt[key] && lt[key].toString().toLowerCase().includes(this.searchFilter.toLowerCase())));
		}
		return list;
	}

	aonEvent({ target }, data) {
		const parent = this.applicationParentEl;
		if (iconAddLocation === target.textContent) {
			parent.showView(SIGNIN_VIEWS.AON_LOCATION_ADD, data);
		} else {
			parent.showView(SIGNIN_VIEWS.AON_EVENT_LIST, data, this._filter);
		}
	}

	aonEventAdd() {
		this.applicationParentEl.showView(SIGNIN_VIEWS.AON_EVENT_ADD, { date: new Date(), reload: true });
	}

	timeStringToMs(duration) {
		if (duration.length === 5) {
			duration = "00:" + duration; // Añadir "00:" al principio para representar las horas
		}
		const parts = duration.split(":"); // Dividir la cadena en minutos y segundos
		const hours = parseInt(parts[0], 10);
		const minutes = parseInt(parts[0], 10); // Obtener los minutos
		const seconds = parseInt(parts[1], 10); // Obtener los segundos
		return (minutes * 60 + seconds) * 1000; // Convertir todo a milisegundos
	}

	formatThousands(n) {
		console.log('formatThousands', n, n.toString().replace(/\B(?=(\d{3})+(?!\d))/g, "."));
		return n.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ".");
	}

	msToTimeString(ms) {
		const totalSeconds = Math.floor(ms / 1000);
		const hours = Math.floor(totalSeconds / 3600);
		const minutes = Math.floor((totalSeconds % 3600) / 60);
		const seconds = totalSeconds % 60;
		// Formatear con dos dígitos para horas, minutos y segundos
		return `${this.formatThousands(hours)}:${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`;
	}

	msToHoursMinutes(ms) {
		const totalSeconds = Math.floor(ms / 1000);
		const hours = Math.floor(totalSeconds / 3600);
		const minutes = Math.floor((totalSeconds % 3600) / 60);
		const seconds = totalSeconds % 60;

		return `${this.formatThousands(hours)}:${String(minutes).padStart(2, '0')}`;
	}

	msToDateHourMinute(ms) {
		const date = new Date(ms);

		const dd = String(date.getDate()).padStart(2, '0');
		const mm = String(date.getMonth() + 1).padStart(2, '0');
		const hh = String(date.getHours()).padStart(2, '0');
		const min = String(date.getMinutes()).padStart(2, '0');

		return `${dd}/${mm} ${hh}:${min}`;
	}

}



window.customElements.define("aon-presence-list", AonPresenceList);
