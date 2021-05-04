import { AonElement } from "../../../components/AonElement.js";
import { getPeriod, getStatus, getTimeControlList, getTimeControlExcel } from "../../../services/service.js";
import { isEmptyObject, setDateTimestamp, setDateTimestampDay, setValueName, sortBy, waitEl } from "../../../services/utils.js";
import { iconAddLocation, PRESENCE_FILTER, SigninSidenav, SIGNIN_VIEWS } from "../signinEnums.js";
import { dateCustomDayHour, StringTwoLetters, timeHour } from "./utils.js";
import "../../../components/aon-table.js";
import "../../../components/aon-mobile-list.js";
import "../../../components/aon-filter.js";
import { EVENT, MSG } from "../../../environments/environments.js";
// import { AonSwitch } from "../../../components/aon-switch.js";

export class AonPresenceList extends AonElement {
  TABLE_ID;
  static get observedAttributes() {
    return ["filter"];
  }

  get filter() {
    return JSON.parse(this.getAttribute("filter"));
  }

  set filter(filter) {
    this.setAttribute("filter", JSON.stringify(filter));
  }

  get id() {
    return this.getAttribute("id");
  }

  set id(id) {
    this.setAttribute("id", id);
  }

  attributeChangedCallback(name, oldValue, newValue) {
    if ("filter" === name) this.getTable();
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
  }

  paintView() {
    let innerHTML =  `<aon-filter id="${this.id}Filter" title="Filtros"></aon-filter>`;
    if (this.isMobile()) {
      innerHTML = innerHTML + ` <aon-mobile-list id='${this.TABLE_ID}' />`;
    } else {
      innerHTML = innerHTML + `<aon-table id='${this.TABLE_ID}' />`;
    }

    this.innerHTML = innerHTML;
  }

  buildToolbar() {
    this.applicationEl.removeToolbarOptions();
    const filterEl = this.getElement(`${this.id}Filter`);
    this.applicationEl.addToolbarOption2(SigninSidenav.FILTER, (e) => filterEl.openFilter());
    this.applicationEl.addToolbarOption2(SigninSidenav.EXCEL, (e) => this.getTimeControlExcel()
   );
  }


  async buildFilter() {
    let aonFilter = this.getElement(`${this.id}Filter`);
    aonFilter.setInputs(PRESENCE_FILTER);

    // let filterFormEl = aonFilter.getFormEl();
    // let aonSwitch = new AonSwitch();
    // aonSwitch.name = "linked";
    // aonSwitch.title = "Usuarios vinculados";
    // aonSwitch.checked = true;
    // filterFormEl.appendChild(aonSwitch);

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
      let filter = null;
      try {filter = {...this.applicationParenEl._filter};} catch (error) {}

      const  datos = await getTimeControlList(filter);
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
                nameLocation = `<aon-icon-button id="iconLocation" icon="${iconAddLocation}" noHover="true"></aon-icon-button>`;
              }
              const obj = {
                lettersHtml,
                textStatus: textStatus.name,
                status: newStatus,
                name: `${name}`,
                duration: timeHour(Number(time)),
                last_date,
                coordinates,
                last_location,
                nameLocation,
                taskHolderId,
              };
              data.push(obj);
            }
          }
        );
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
