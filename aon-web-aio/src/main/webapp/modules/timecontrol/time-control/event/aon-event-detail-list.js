import { AonElement } from "../../../../components/AonElement.js";
import { isEmptyObject, removeEmpty, setValueName, sortBy } from "../../../../services/utils.js";
import { setAttributes } from "../../../../services/utilsComponents.js";
import { getStatus, getPeriod, getTaskHolderTimeControl } from "../../../../services/service.js";
import { SigninSidenav, PRESENCE_FILTER, SIGNIN_VIEWS, iconAddLocation } from "../../signinEnums.js";
import { ToolbarType } from "../../../../models/enums.js";
import { dateCustomDayHour } from "../utils.js";
import { CONSTANT, CSS, EVENT, MATERIAL_ICONS, MSG, TAG } from "../../../../environments/environments.js";
import * as ACTION from '../../../actions.js';
import { AonToolbar } from "../../../../components/aon-toolbar.js";
import { AonMobileList } from "../../../../components/aon-mobile-list.js";
import { AonTable } from "../../../../components/aon-table.js";
import { AonIconButton } from "../../../../components/aon-icon-button.js";
import { AonDateUtils } from "../../../utils/AonDateUtils.js";
import * as LS from '../../../../services/localStorageService.js';


export class AonEventDetailList extends AonElement {
	TABLE_ID;

	_eventDatailListFilter;
	_filter;
	_taskHolder

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
		this.id = this.id || SIGNIN_VIEWS.AON_EVENT_DETAIL_LIST;
		this.TABLE_ID = this.id + "Table";
		this.TOOLBAR = this.id + "Toolbar";

		this.applicationEl = this.getApplication();
		this.applicationParentEl = this.getApplicationParent();

		this.applicationEl.addToolbarTitle("Detalle");

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
			search: '',
			taskHolderiId: undefined
		}
	}

	build() {
		// Join default filter with presence current filter
		if (this._eventDatailListFilter) {
			this._filter = { ...this._filter, ...this._eventDatailListFilter };
		}

		this.paintView();
		this.buildToolbar();
		this.getTable();
	}

	paintView() {
		if (this.isMobile()) {
			let aonToolbar = new AonToolbar();
			aonToolbar.id = this.TOOLBAR;
			aonToolbar.type = ToolbarType.SECONDARY;
			this.appendChild(aonToolbar);
		}

		let aonTable = this.isMobile() ? new AonMobileList() : new AonTable();
		aonTable.id = this.TABLE_ID;

		let div = this.createElement(TAG.DIV);
		div.appendChild(aonTable);
		this.appendChild(div);
	}

	buildToolbar() {
		this.applicationEl.removeToolbarOptions();

		if (this.isMobile()) {
			let toolbarEl = this.getElement(this.TOOLBAR);
			toolbarEl.removeButtons();
			toolbarEl.addButton2(ACTION.BACK, () => this.back());
		}

		if (!this.applicationParentEl.isEmployee()) {
			if (this.isMobile()) {
				this.applicationEl.addFloatOption(SigninSidenav.ADD, () => this.aonEvent());
			} else {
				this.applicationEl.addToolbarOption2(SigninSidenav.ADD, () => this.aonEvent());
			}
		}

		this.buildToolbarSearch();
	}


	buildToolbarSearch() {
		let btnSearch = this.applicationEl.addSearchOption(LS.isFutureTheme());

		let timeOut = null;

		btnSearch.addEventListener(EVENT.SEARCH_NEW, ({ detail }) => {
			clearTimeout(timeOut);
			timeOut = setTimeout(() => {
				this.searchFilter = detail.search;

				let newFilter = {
					period: detail.period,
					startDate: detail.startDate,
					endDate: detail.endDate,
				}

				if (newFilter && newFilter.period) {
					newFilter = { ...newFilter, ...getPeriod(newFilter.period) };
				}

				this._filter = { ...this._filter, ...newFilter };

				this.getTable();

			});
		});

		btnSearch.addEventListener(EVENT.RESET_FILTER, ({ detail }) => {
			clearTimeout(timeOut);
			timeOut = setTimeout(() => {
				this.searchFilter = '';

				this.filterInit();

				let searchInput = this.getElement('aonSigninToolbarHeaderToolSectionSearchSearchInput');
				if (searchInput) searchInput.value = '';

				setValueName('period', this._filter.period);
				setValueName('startDate', this._filter.startDate);
				setValueName('endDate', this._filter.endDate);

				this.getTable();
			});
		});


		btnSearch.buildOptionsFilter(PRESENCE_FILTER);
		this.searchValueDefault();
	}

	async searchValueDefault() {

		let periodEl = this.getElement("period");
		periodEl.options = JSON.stringify(getPeriod());
		periodEl.addEventListener(EVENT.CHANGE, ({ detail }) => {
			if (detail) {
				const { startDate, endDate } = detail;
				setValueName('startDate', startDate);
				setValueName('endDate', endDate);
			}
		});

		this.getElement("startDate").addEventListener(EVENT.CHANGE, () => periodEl.value = "personalized");
		this.getElement("endDate").addEventListener(EVENT.CHANGE, () => periodEl.value = "personalized");

		setValueName('period', this._filter.period);
		setValueName('startDate', this._filter.startDate);
		setValueName('endDate', this._filter.endDate);
	}


	async getTable() {
		this.applicationEl.startLoader();
		if (this.isMobile()) {
			await this.getTableMobile();
		} else {
			await this.getTableDesk();
		}
		this.applicationEl.stopLoader();
	}

	async getTableDesk() {
		const aonTable = this.getElement(this.TABLE_ID);
		if (aonTable) {
			aonTable.removeColumns();
			aonTable.addColumnIcon({ title: MSG.BACK, name: MATERIAL_ICONS.ARROW_BACK, type: "string", id: "lettersHtml", width: "6%" }, () => this.back());
			aonTable.addColumn(MSG.STATUS, "string", "textStatus", "10%");
			aonTable.addColumn("Tipo", "string", "reason", "15%");
			aonTable.addColumn(MSG.DATE, "date", "dateParse", "20%");
			aonTable.addColumn(MSG.LOCATION, "string", "nameLocation", "30%");
			try {
				const resp = await this.getData();
				aonTable.removeRows();
				resp.map((res) => {
					aonTable.addRow(
						{
							...res,
							status: res.textStatus,
						},
						(el) => this.aonEvent(el, res)
					);
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
					let newDate = dateCustomDayHour(res.date);
					if (newDate) { res.dateParse = newDate; }

					let subtitle = `${res.reason && res.reason.length > 0 ? res.reason : ''} <span style="float: right;">${res.nameLocation}</span> `;

					let options = {
						paddingTopTitle: "5px",
						iconHtmlCustom: `${res.lettersHtml} <span style="padding-top: 5px;float: right;color: rgba(0,0,0,.54);">${res.dateParse}</span>`,
						title: `${res.textStatus}`,
						subtitle
					};
					aonTable.addLi(options, idx, (el) => this.aonEvent(el, res));
				});
			} catch (e) {
				console.log(e);
			}
		}
	}

	async getData() {
		let data = [];
		try {
			this._filter.taskHolderId = this._taskHolder.id;

			const datos = await getTaskHolderTimeControl(this._filter);

			//console.log('--------- Aon Event Detail List ---------', this._filter, datos);

			if (datos) {
				sortBy(datos, "start_date", "asc").map((resp) => {
					removeEmpty(resp);

					sortBy(resp.detail, "date", "asc").map((detail) => {
						removeEmpty(detail);

						const newStatus = detail.status.toLowerCase();
						const status = getStatus(newStatus);
						const textStatus = status.name;
						const lettersHtml = `<div class="profile-letters ${newStatus}">${textStatus.substr(0, 1)}</div>`;

						let locationName;
						if (detail.location && detail.location.name) {
							locationName = detail.location.name;
						}
	
						let reason = '';
						if (detail && detail.length > 0) {
						  const last = [...detail].reverse().find(item => item.status === 'in');
						  reason = last ? last.comments : '';
						}

						data.push({
							...detail,
							lettersHtml,
							textStatus,
							status: newStatus,
							nameLocation: locationName || reason,
							dateParse: AonDateUtils.setDateTimestamp(detail.date),
							reason
						});

					});

				});
			}
		} catch (error) {
			console.log(error);
		}
		this.paintName();
		return data;
	}

	async paintName() {
		const taskHolder = this.applicationParentEl.TASK_HOLDER;
		if (taskHolder) {
			if (this.isMobile()) await this.paintNameMobile(taskHolder);
			else this.applicationEl.addTitleToolSection(taskHolder.name);
		}
	}

	async paintNameMobile(taskHolder) {
		let div = this.createElement(TAG.DIV);
		let divName = this.createElement(TAG.DIV);
		divName.style.whiteSpace = "nowrap";
		divName.style.textOverflow = "ellipsis";
		divName.style.overflow = "hidden";
		divName.style.marginRight = "14px";
		divName.innerText = taskHolder.name;
		div.appendChild(divName);
		if (!this.applicationParentEl.isEmployee()) {
			const auth = await this.applicationParentEl.getAuth({ task_holder: taskHolder.id });
			if (auth && auth.phone) {
				let color = "black";
				if (taskHolder.status && "in" === taskHolder.status) color = "green";
				let aEl = this.createElement(TAG.A);
				aEl.href = `tel:+34${auth.phone}`;
				aEl.style.position = "absolute";
				aEl.style.top = "-1px";
				aEl.style.right = "4px";
				let aib = setAttributes(new AonIconButton(), {
					id: "iconPhone",
					icon: "phone_in_talk",
					noHover: "true",
					color
				})
				aEl.appendChild(aib);
				div.appendChild(aEl);
			}
		}
		this.getElement(this.TOOLBAR).title = div.outerHTML;
	}

	aonEvent(el, data) {
		if (el && iconAddLocation === el.target.textContent) {
			this.applicationParentEl.showView(SIGNIN_VIEWS.AON_LOCATION_ADD, { coordinates: data.coordinates });
		} else {
			this.applicationParentEl.showView(SIGNIN_VIEWS.AON_EVENT_ADD, data, this._eventDatailListFilter);
		}
	}

	back() {
		this.applicationParentEl.showView(SIGNIN_VIEWS.AON_EVENT_LIST, undefined, this._filter);
	}

}
window.customElements.define("aon-event-detail-list", AonEventDetailList);
