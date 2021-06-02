import { AonElement } from "../../../components/AonElement.js";
import { getPeriod, getStatus, getTimeControlList, getTimeControlExcel } from "../../../services/service.js";
import { isEmptyObject, setAttributes, setDateTimestamp, setDateTimestampDay, setValueName, sortBy, waitEl } from "../../../services/utils.js";
import { iconAddLocation, PRESENCE_FILTER, SigninSidenav, SIGNIN_VIEWS } from "../signinEnums.js";
import { dateCustomDayHour, StringTwoLetters, timeHour } from "./utils.js";
import { CONSTANT, EVENT, MSG } from "../../../environments/environments.js";
import { AonMobileList } from "../../../components/aon-mobile-list.js";
import { AonTable } from "../../../components/aon-table.js";
import { AonFilter } from "../../../components/aon-filter.js";
import { AonIconButton } from "../../../components/aon-icon-button.js";
import { AonSwitch } from "../../../components/aon-switch.js";

export class AonPresenceList extends AonElement {
  TABLE_ID;
  searchFilter;
  _list;
  static get observedAttributes() {
    return [CONSTANT.FILTER];
  }

  get filter() {
    return JSON.parse(this.getAttribute(CONSTANT.FILTER));
  }

  set filter(filter) {
    this.setAttribute(CONSTANT.FILTER, JSON.stringify(filter));
  }

  get id() {
    return this.getAttribute(CONSTANT.ID);
  }

  set id(id) {
    this.setAttribute(CONSTANT.ID, id);
  }

  attributeChangedCallback(name, oldValue, newValue) {
    if (CONSTANT.FILTER === name) this.getTable();
  }

  constructor() {
    super();
  }

  connectedCallback() {
    this.initialize();
    this.build();
  }

  initialize(){
    this.id = this.id || SIGNIN_VIEWS.AON_PRESENCE_LIST;
    this.TABLE_ID = this.id + "Table";
    this.applicationEl = this.getApplication();
    this.applicationParenEl = this.getApplicationParent();
    this.applicationEl.addToolbarTitle("Presencia");
    this.applicationParenEl.periodSideNavDisplay(true);
  }
  
  async build(){
    this.paintView();
    this.buildToolbar();
    await this.buildFilter();
    await this.getTable();
    // document.querySelector("aon-search").buildOptionsFilter(PRESENCE_FILTER);
  }

  paintView() {
    let aonFilter = new AonFilter();
    aonFilter.id = this.id+"Filter";
    aonFilter.title = MSG.FILTERS;
    this.appendChild(aonFilter);

    let aonTable = this.isMobile() ? new AonMobileList() : new AonTable();
    aonTable.id = this.TABLE_ID;
    this.appendChild(aonTable);
  }

  buildToolbar() {
    this.applicationEl.removeToolbarOptions();
    const filterEl = this.getElement(`${this.id}Filter`);
    this.applicationEl.addToolbarOption2(SigninSidenav.FILTER, () => filterEl.openFilter());
    this.applicationEl.addToolbarOption2(SigninSidenav.EXCEL, () => this.getTimeControlExcel());
    this.applicationEl.addSearchOption();
    this.applicationEl.addEventListener(EVENT.SEARCH, ({detail}) => this.search(detail));
  }


  async buildFilter() {
    let aonFilter = this.getElement(`${this.id}Filter`);
    aonFilter.setInputs(PRESENCE_FILTER);

    let filterFormEl = aonFilter.getFormEl();
    let aonSwitch = new AonSwitch();
    aonSwitch.name = "linked";
    aonSwitch.title = "Usuarios activos";
    aonSwitch.checked = true;
    aonSwitch.disabled = true;
    filterFormEl.appendChild(aonSwitch);

    aonFilter.addEventListener(EVENT.APPLY_FILTER, ({detail}) => {
      if(detail) this.applicationParenEl.setDataFilter(detail);
    });

    let periodEl = this.getElement("period");
    periodEl.options = JSON.stringify(getPeriod());

    periodEl.addEventListener(EVENT.CHANGE, ({detail}) => {
      if(detail){
        const {startDate, endDate} = detail;
        setValueName('startDate', startDate);
        setValueName('endDate', endDate);
      }
    });

    this.getElement("startDate").addEventListener(EVENT.CHANGE,()=>{
      periodEl.value = "personalized";
    });
    this.getElement("endDate").addEventListener(EVENT.CHANGE,()=>{
      periodEl.value = "personalized";
    });
  }

  async getTable() {
    this.applicationEl = await waitEl("#aonSignin");
    this.applicationEl.startLoader();
    if (this.isMobile()) await this.getTableMobile();
    else await this.getTableDesk();
    this.applicationEl.stopLoader();
    this.applicationParenEl.changeFilter();
  }

  async getTableDesk() {
    const aonTable = this.getElement(this.TABLE_ID);
    if (aonTable) {
      aonTable.removeColumns();
      aonTable.addColumn("", "string", "lettersHtml", "6%");
      aonTable.addColumn(MSG.NAME, "string", "name", "34%");
      aonTable.addColumn(MSG.LAST_STATUS, "", "lastStatus", "35%");
      aonTable.addColumn(MSG.DURATION, "", "duration", "5%");
      aonTable.addColumn(MSG.LAST_LOCATION, "string", "nameLocation", "20%");
      try {
        const resp = await this.getData();
        aonTable.removeRows();
        resp.map((res) => {
          res.lastStatus = `${res.textStatus} ${setDateTimestampDay(res.last_date)}`
          aonTable.addRow(res, (el) => this.aonEvent(el, res));
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
          let dateParse = dateCustomDayHour(res.last_date) || setDateTimestamp(res.last_date);

          let options = {
            iconHtmlCustom: `${res.lettersHtml} <span style="float: right;color: rgba(0,0,0,.54);">${res.duration}</span>`,
            title: `${res.name}`,
            subtitle: `${dateParse} <span style="float: right;">${res.nameLocation}</span> `,
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
      if(this.searchFilter && !isEmptyObject(this._list)){
        data = this._list.filter(({name, nameLocation})=> this.includeSearch(name) ||  this.includeSearch(nameLocation));
      } else {
        let filter = null;
        try {filter = {...this.applicationParenEl._filter};} catch (error) {}
        const datos = await getTimeControlList(filter);
        if (datos) {
          await sortBy(datos, 'last_date', 'desc').map(
            async ({
              time,
              last_date,
              status,
              coordinates,
              last_location,
              task_holder: { id: taskHolderId, name },
            }) => {
              if (last_date) {
                const newStatus = status.toLowerCase();
                const lettersName = StringTwoLetters(name);
                const lettersHtml = `<div class="profile-letters ${newStatus}">${lettersName}</div>`;
                const textStatus = await getStatus(newStatus);
                let nameLocation = "";
                if (last_location && last_location.name) {
                  nameLocation = last_location.name;
                } else if(!isEmptyObject(coordinates)) {
                  let aib = setAttributes(new AonIconButton(),{id: "iconLocation", noHover: "true", icon: iconAddLocation});
                  nameLocation = aib.outerHTML;
                }
                const obj = {
                  name,
                  lettersHtml,
                  last_date,
                  coordinates,
                  last_location,
                  nameLocation,
                  taskHolderId,
                  textStatus: textStatus.name,
                  status: newStatus,
                  duration: timeHour(Number(time)),
                };
                data.push(obj);
              }
            }
          );
          this._list = data;
        }
      }
    } catch (e) {
      console.log(e);
    }
    return data;
  }

	async getTimeControlExcel() {
		this.applicationEl.startLoading();
		try {
      let startYear = new Date().getFullYear();
      let {startDate} = this.applicationParenEl._filter;
			if(startDate) {startYear = new Date(startDate).getFullYear();} 
      startDate = startYear+"-01-01"; 
      const endDate = startYear+"-12-31"; 
			await getTimeControlExcel({startDate, endDate}); 
		} catch (error) {
      this.showToast(error);
		}
		this.applicationEl.stopLoading();
	}

  search(detail){
    this.searchFilter = detail;
    this.getTable();
  }

  includeSearch(str){
    return this.searchFilter && str && str.toLowerCase().includes(this.searchFilter.toLowerCase());
  }

  aonEvent({ target }, data) {
    const parent = this.applicationParenEl;
    if (iconAddLocation === target.textContent) {
      parent.showView(SIGNIN_VIEWS.AON_LOCATION_ADD, data);
    } else {
      parent.showView(SIGNIN_VIEWS.AON_EVENT_LIST, data, true);
    }
  }
}

window.customElements.define("aon-presence-list", AonPresenceList);
