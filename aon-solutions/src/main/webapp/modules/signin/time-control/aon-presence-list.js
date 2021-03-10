import { AonElement } from "../../../components/AonElement.js";
import { getPeriod, getStatus, getTimeControlList } from "../../../services/service.js";
import { isEmptyObject, setDateTimestamp, setDateTimestampDay, setValueName, sortBy, waitEl } from "../../../services/utils.js";
import { PresenceFilterInput, SigninSidenav } from "../signinEnums.js";
import { dateCustomDayHour, StringTwoLetters, timeHour } from "./utils.js";
import "../../../components/aon-table.js";
import "../../../components/aon-mobile-list.js";
import "../../../components/aon-filter.js";

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
    this.id = this.id || "aonPresenceList";
    this.TABLE_ID = this.id + "Table";
    this.aonSigninEl = this.getApplication();
    this.aonSigninParentEl = this.aonSigninEl.getParent();
    this.aonSigninEl.addToolbarTitle("Presencia");
    this.aonSigninParentEl.periodSideNavDisplay(true);
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
    this.aonSigninEl.removeToolbarOptions();
    const filterEl = this.getElement(`${this.id}Filter`);
    this.aonSigninEl.addToolbarOption2(SigninSidenav.FILTER, (e) =>
      filterEl.openFilter()
    );
  }


  async buildFilter() {
    let aonFilter = this.getElement(`${this.id}Filter`);
    aonFilter.setInputs(PresenceFilterInput);
    aonFilter.addEventListener("applyFilter", ({detail}) => {
      if(detail) this.aonSigninParentEl.setDataFilter(detail);
    });

    let periodEl = this.getElement("period");
    periodEl.options = JSON.stringify(await getPeriod());

    periodEl.addEventListener('change', ({detail}) => {
      if(detail){
        const {startDate, endDate} = detail;
        setValueName('startDate', startDate);
        setValueName('endDate', endDate);
      }
    });

    this.getElement("startDate").addEventListener("change",(ev)=>{
      periodEl.value = "personalized";
    });
    this.getElement("endDate").addEventListener("change",(ev)=>{
      periodEl.value = "personalized";
    });
  }

  async getTable() {
    this.aonSigninEl = await waitEl("#aonSignin");
    this.aonSigninEl.startLoader();
    if (this.isMobile()) await this.getTableMobile();
    else await this.getTableDesk();
    this.aonSigninEl.stopLoader();
    this.aonSigninParentEl.changeFilter();
  }



  async getTableDesk() {
    const aonTable = this.getElement(this.TABLE_ID);
    if (aonTable) {
      aonTable.removeColumns();
      aonTable.addColumn("", "string", "lettersHtml", "6%");
      aonTable.addColumn("Nombre", "string", "name", "34%");
      aonTable.addColumn("Último estado", "", "lastStatus", "35%");
      aonTable.addColumn("Duración", "", "duration", "5%");
      aonTable.addColumn("Última ubicación", "string", "nameLocation", "20%");
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
      try {filter = {...this.aonSigninParentEl._filter};} catch (error) {}

      const datos = await getTimeControlList(filter);
      if (datos) {
        sortBy(datos, 'last_date', 'desc').map(
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
              let nameLocation = "";
              const textStatus = await getStatus(newStatus);
              if (last_location && last_location.name) {
                nameLocation = last_location.name;
              } else if(!isEmptyObject(coordinates)) {
                nameLocation = `<aon-icon-button id="iconLocation" icon="add_location" noHover="true"></aon-icon-button>`;
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

  aonEvent({ target }, data) {
    const parent = this.aonSigninParentEl;
    if ("add_location" === target.textContent) {
      parent.showView("aonLocationAdd", data);
    } else {
      parent.showView("aonEventList", data, true);
    }
  }
}

window.customElements.define("aon-presence-list", AonPresenceList);
