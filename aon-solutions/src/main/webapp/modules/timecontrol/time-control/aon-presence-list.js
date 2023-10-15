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

export class AonPresenceList extends AonElement {
  TABLE_ID;
  searchFilter;
  _list;
  FN_FILTER;
  static get observedAttributes() {
    return [];
  }

  get id() {
    return this.getAttribute(CONSTANT.ID);
  }

  set id(id) {
    this.setAttribute(CONSTANT.ID, id);
  }

  attributeChangedCallback(name, oldValue, newValue) {}

  constructor() {
    super();
  }

  connectedCallback() {
    this.initialize();
    this.build();
  }
  
  disconnectedCallback() {
    this.applicationParentEl.removeEventListener("filterParent", this.FN_FILTER);
  }

  initialize(){
    this.id = this.id || SIGNIN_VIEWS.AON_PRESENCE_LIST;
    this.TABLE_ID = this.id + "Table";
    this._list = [];

    this.applicationEl = this.getApplication();
    this.applicationParentEl = this.getApplicationParent();

    this.applicationEl.addToolbarTitle("Presencia");
    this.applicationParentEl.periodSideNavDisplay(true);

    this.FN_FILTER = ()=> {
      this._list = [];
      this.getTable();
    }

  }
  
  build(){
    this.paintView();
    this.buildToolbar();
    this.getTable();
    
    this.applicationParentEl.addEventListener("filterParent", this.FN_FILTER);
  }

  paintView() {
    let aonTable = this.isMobile() ? new AonMobileList() : new AonTable();
    aonTable.id = this.TABLE_ID;
    aonTable.setApp(TIMECONTROL)
    this.appendChild(aonTable);
  }

  buildToolbar() {
    this.applicationEl.removeToolbarOptions();
    
    if(!this.applicationParentEl.isEmployee()){
      if(this.isMobile()){
        this.applicationEl.addFloatOption(SigninSidenav.ADD, () => this.aonEventAdd() );
      } else {
        this.applicationEl.addToolbarOption2(SigninSidenav.ADD, () => this.aonEventAdd());
      }
    }

    if(!this.isMobile()) {
      this.applicationEl.addToolbarOption2(SigninSidenav.MORE, ({target}) => this.dialogReport(target));
    }

    this.buildToolbarSearch();
    
  }

  buildToolbarSearch(){
    let btnSearch = this.applicationEl.addSearchOption();
    
    let timeOut = null;

    btnSearch.addEventListener(EVENT.SEARCH_NEW, ({detail})=>{
      clearTimeout(timeOut);
      timeOut = setTimeout(() => {
        this._list = [];
        this.searchFilter = detail.search;
        this.applicationParentEl.setDataFilter({
          active: detail.active,
          search:detail.search,
          period: detail.period,
          startDate: detail.startDate,
          endDate: detail.endDate
        });
      });
    });

    let inputsFilter = [
      ...PRESENCE_FILTER,
      {
        type: CONSTANT.HTML_ELEMENT,
        element: new AonSwitch(),
        id: "aonSwitchFilter",
        name:"active",
        title:"Usuarios activos",
        checked:true
      }
    ];

    btnSearch.buildOptionsFilter(inputsFilter);//INPUTS

    this.searchValueDefault();
  }

  searchValueDefault(){
    let periodEl = this.getElement("period");
    if(periodEl){
      periodEl.setOptions(getPeriod());
      periodEl.addEventListener(EVENT.CHANGE, ({detail}) => {
        if(detail){
          const {startDate, endDate} = detail;
          setValueName('startDate', startDate);
          setValueName('endDate', endDate);
        }
      });
    }

    this.getElement("startDate").addEventListener(EVENT.CHANGE,()=>periodEl.value = "personalized");
    this.getElement("endDate").addEventListener(EVENT.CHANGE,()=>periodEl.value = "personalized");
  }

  async getTable() {
    this.applicationEl = await waitEl("#aonSignin");
    this.applicationEl.startLoader();
    if (this.isMobile()) await this.getTableMobile();
    else await this.getTableDesk();
    this.applicationEl.stopLoader();
    this.applicationParentEl.changeFilter();
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
          let lastStatus = res.last_date ? `${res.textStatus} ${AonDateUtils.setDateTimestampDay(res.last_date)}` : null;
          res.lastStatus = lastStatus;
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
          let subtitle = null;
          if(res.last_date){
            const dateParse = dateCustomDayHour(res.last_date) || AonDateUtils.setDateTimestamp(res.last_date);
            subtitle = `${dateParse} <span style="float: right;">${res.nameLocation}</span> `;
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

  dialogReport(button){
		const left = button.getBoundingClientRect().left;
    let top  = button.getBoundingClientRect().top;
    if(this.isMobile()) top = top - 50;

    let options = [{
      name: "Registro de jornada",
      aonIcon: 'aon_excel',
      permission:true,
      backgroundColor: Apps.TIMECONTROL.color,
      fn: () => modalReport(this.applicationEl, this, "excel")
    }, {
      name: "Plantilla fichajes",
      aonIcon: 'aon_pdf',
      permission:true,
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
      if(this._list.length){
        data = this._list;
      } else {
        let filter = null;
        try {filter = {...this.applicationParentEl._filter};} catch (error) {}
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
              const newStatus = status.toLowerCase();
              const lettersName = StringTwoLetters(name);
              
              const div = this.createElement(TAG.DIV);
              div.classList.add("profile-letters", newStatus);
              div.innerText = lettersName;

              const lettersHtml = div.outerHTML;
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
          );
          this._list = data;
          if(this.searchFilter) data = this.filterSearch(["name", "nameLocation"], data);
        }
      }
    } catch (e) { console.log(e); }
    return data;
  }

	async getTimeControlExcel(startYear) {
		this.applicationEl.startLoading();
		try {
      let {active} = this.applicationParentEl._filter;
			await getTimeControlExcel({startDate:startYear+"-01-01", endDate:startYear+"-12-31", active}); 
		} catch (error) {
      this.showToast(error);
		}
		this.applicationEl.stopLoading();
	}

	async getTimeControlPdf(startDate) {
		this.applicationEl.startLoading();
		try {
			await getTimeControlPdf({startDate:startDate}); 
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
    const parent = this.applicationParentEl;
    if (iconAddLocation === target.textContent) {
      parent.showView(SIGNIN_VIEWS.AON_LOCATION_ADD, data);
    } else {
      parent.showView(SIGNIN_VIEWS.AON_EVENT_LIST, data);
    }
  }
  
  aonEventAdd(){
    this.applicationParentEl.showView(SIGNIN_VIEWS.AON_EVENT_ADD, {date: new Date(), reload:true});
  }  
}

window.customElements.define("aon-presence-list", AonPresenceList);
