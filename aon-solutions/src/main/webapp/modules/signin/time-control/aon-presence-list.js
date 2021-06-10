import { AonElement } from "../../../components/AonElement.js";
import { getPeriod, getStatus, getTimeControlList, getTimeControlExcel } from "../../../services/service.js";
import { isEmptyObject, setAttributes, setDateTimestamp, setDateTimestampDay, setValueName, sortBy, waitEl } from "../../../services/utils.js";
import { iconAddLocation, PRESENCE_FILTER, SigninSidenav, SIGNIN_VIEWS } from "../signinEnums.js";
import { dateCustomDayHour, StringTwoLetters, timeHour } from "./utils.js";
import { CONSTANT, EVENT, MSG } from "../../../environments/environments.js";
import { AonMobileList } from "../../../components/aon-mobile-list.js";
import { AonTable } from "../../../components/aon-table.js";
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
    this._list = [];
  }
  
  async build(){
    this.paintView();
    this.buildToolbar();
    await this.getTable();
  }

  paintView() {
    let aonTable = this.isMobile() ? new AonMobileList() : new AonTable();
    aonTable.id = this.TABLE_ID;
    this.appendChild(aonTable);
  }

  buildToolbar() {
    this.applicationEl.removeToolbarOptions();
    if(!this.isMobile()) this.applicationEl.addToolbarOption2(SigninSidenav.EXCEL, () => this.getTimeControlExcel());
    this.buildToolbarSearch();
    this.searchValueDefault();
  }

  buildToolbarSearch(){
    let btnSearch = this.applicationEl.addSearchOption();
    
    this.applicationEl.addEventListener(EVENT.SEARCH, ({detail}) => {
      this.searchFilter = detail;
      this.search();
    });

    btnSearch.addEventListener(EVENT.SEARCH_VALUE, ({detail})=>{
      this._list = [];
      if(detail) this.applicationParenEl.setDataFilter(detail);
    });

    let arrayNewFilter = PRESENCE_FILTER;
    arrayNewFilter.push({
      type: CONSTANT.HTML_ELEMENT,
      element: new AonSwitch(),
      id: "aonSwitchFilter",
      name:"linked",
      title:"Usuarios activos",
      checked:true,
      disabled:true
    })
    
    btnSearch.buildOptionsFilter(arrayNewFilter);//INPUTS
  }

  searchValueDefault(){
    let periodEl = this.getElement("period");
    periodEl.options = JSON.stringify(getPeriod());
    periodEl.addEventListener(EVENT.CHANGE, ({detail}) => {
      if(detail){
        const {startDate, endDate} = detail;
        setValueName('startDate', startDate);
        setValueName('endDate', endDate);
      }
    });

    this.getElement("startDate").addEventListener(EVENT.CHANGE,()=>periodEl.value = "personalized");
    this.getElement("endDate").addEventListener(EVENT.CHANGE,()=>periodEl.value = "personalized");
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
      if(this._list.length){
        data = this._list;
      } else {
        let filter = null;
        try {filter = {...this.applicationParenEl._filter};} catch (error) {}
        const datos = await getTimeControlList(filter);
        if (datos) {
          sortBy(datos, 'last_date', 'desc').map(({
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
                const {name:textStatus} = getStatus(newStatus);
                let nameLocation = "";
                if (last_location && last_location.name) {
                  nameLocation = last_location.name;
                } else if(!isEmptyObject(coordinates)) {
                  let aib = setAttributes(new AonIconButton(),{id: "iconLocation", noHover: "true", icon: iconAddLocation});
                  nameLocation = aib.outerHTML;
                }
                data.push({
                  name,
                  lettersHtml,
                  last_date,
                  coordinates,
                  last_location,
                  nameLocation,
                  taskHolderId,
                  textStatus,
                  status: newStatus,
                  duration: timeHour(Number(time)),
                });
              }
            }
          );
          this._list = data;
          if(this.searchFilter) data = this.filterSearch(["name", "nameLocation"], data);
        }
      }
    } catch (e) { console.log(e); }
    return data;
  }

	async getTimeControlExcel() {
		this.applicationEl.startLoading();
		try {
      let startYear = new Date().getFullYear();
      let {startDate} = this.applicationParenEl._filter;
			if(startDate) {startYear = new Date(startDate).getFullYear();} 
			await getTimeControlExcel({startDate:startYear+"-01-01", endDate:startYear+"-12-31"}); 
		} catch (error) {
      this.showToast(error);
		}
		this.applicationEl.stopLoading();
	}

  search(){
    this._list = this.filterSearch(["name", "nameLocation"], this._list);
    this.getTable();
  }

  filterSearch(keys, lists){
    let list = [];
    if(this.searchFilter && lists.length){
      list = lists.filter((lt)=> keys.some(key=>lt[key] && lt[key].toString().toLowerCase().includes(this.searchFilter.toLowerCase())));
    }
    return list;
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
