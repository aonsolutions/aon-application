import { AonElement } from "../../../../components/AonElement.js";
import { formatDateOrigin, isEmptyObject, setDateTimestamp, setValueName, sortBy, timeHour } from "../../../../services/utils.js";
import {
  getGroups,
  getPeriod,
  getStatus,
  getTaskHolderTimeControl,
} from "../../../../services/service.js";
import { StringTwoLetters } from "../utils.js";
import { AonPresenceList } from "../aon-presence-list.js";
import {AonEventDetailList} from "./aon-event-detail-list.js";
import "../../../../components/aon-table.js";
import "../../../../components/aon-mobile-list.js";
import "../../../../components/aon-filter.js";


export class AonEventList extends AonElement {
  TABLE_ID;
  static get observedAttributes() {
    return ["filter", "data"];
  }

  get filter() {
    return JSON.parse(this.getAttribute("filter"));
  }

  set filter(filter) {
    this.setAttribute("filter", JSON.stringify(filter));
  }

  get data() {
    return JSON.parse(this.getAttribute("data"));
  }

  set data(value) {
    this.setAttribute("data", JSON.stringify(value));
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
    this.id = this.id || "aonEventList";
    this.TABLE_ID = this.id + "Table";
    this.aonSigninEl = this.getElement("aonSignin");
    this.aonSigninParentEl = this.aonSigninEl.getParent();
    this.aonSigninParentEl.periodSideNavDisplay(true);
  }

  connectedCallback() {
    this.aonSigninEl.addToolbarTitle("Resumen");
    this.build();
  }

  disconnectedCallback() {}

  async build() {
    this.paintView();
    this.buildToolbar();
    await this.buildFilter();
    await this.getTable();
  }


  paintView() {
    let innerHTML = `<aon-filter id="${this.id}Filter" title="Filtros"></aon-filter>`;
    if (this.isMobile()) {
      innerHTML = innerHTML + ` <aon-mobile-list id='${this.TABLE_ID}' />`;
    } else {
      innerHTML = innerHTML + ` <aon-table id='${this.TABLE_ID}' />`;
    }

    this.innerHTML = innerHTML;
  }

  async getTable() {
    this.aonSigninEl.startLoader();
    if (this.isMobile()) {
      await this.getTableMobile();
    } else {
      await this.getTableDesk();
    }
    this.aonSigninEl.stopLoader();
    this.aonSigninParentEl.changeFilter();
  }


  buildToolbar() {
    this.aonSigninEl.removeToolbarOptions();
    const filterEl = this.getElement(`${this.id}Filter`);
    this.aonSigninEl.addToolbarOption("Filter", "tune", (e) =>
      filterEl.openFilter()
    );
    // this.aonSigninEl.addToolbarOption("Previus", "arrow_back", (e) => this.back());
  }

  async buildFilter() {
    let aonFilter = this.getElement(`${this.id}Filter`);
    let inputs = [
      {
        type: "select",
        id: "group",
        name: "group",
        title: "Agrupar por ",
      },
      {
        type: "select",
        id: "period",
        name: "period",
        title: "Período",
      },
      {
        type: "date",
        name: "startDate",
        id: "startDate",
        title: "Desde",
      },
      {
        type: "date",
        name: "endDate",
        id: "endDate",
        title: "Hasta",
      },
    ];
    aonFilter.setInputs(inputs);
    aonFilter.addEventListener("applyFilter", ({detail}) => {
      if(detail) this.aonSigninParentEl.setDataFilter(detail);
    });

    let groupEl = this.getElement('group')
    groupEl.options = JSON.stringify(await getGroups());

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


  async getTableDesk() {
    const aonTable = this.getElement(this.TABLE_ID);
    if (aonTable) {
      aonTable.removeColumns();
      aonTable.addBack((e)=>this.back());
      aonTable.addColumn("", "string", "lettersHtml", "6%");
      aonTable.addColumn("Nombre", "string", "name", "34%");
      aonTable.addColumn("Estado", "", "status", "15%");
      aonTable.addColumn("Fecha", "date", "dateParse", "20%");
      aonTable.addColumn("Duración", "", "duration", "5%");
      aonTable.addColumn("Ubicación", "string", "nameLocation", "20%");
      try {
        const resp = await this.getData();
        aonTable.removeRows();
        resp.map((res) => {
          aonTable.addRow(
            {
              ...res,
              status: res.textStatus
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
      aonTable.createAonDialog();
      try {
        const resp = await this.getData();
        aonTable.removeAllLi();
        resp.map((res, idx) => {
          let options = {
            iconHtmlCustom: `${res.lettersHtml}`,
            title: `${res.name}  <span style="float: right;">(${res.duration})</span>`,
            subtitle: `(${res.textStatus}) ${res.dateParse} <span style="float: right;">${res.nameLocation}</span>`,
          };
          aonTable.addLi(options, idx, (el) => this.aonEvent(el, res));
        });
      } catch (e) {
        console.log(e);
      }
    }
  }

  async getData() {
    this.aonSigninEl.startLoader();
    let data = [];
    try {
      let filter = null;
      try {filter = {...this.aonSigninParentEl._filter};} catch (error) {}
      let datos = await getTaskHolderTimeControl(filter);
      if(datos){
        sortBy(datos, 'last_date', 'desc').map(
          async (r) => {
            const name = r.task_holder.name;
            const newStatus = r.status.toLowerCase();
            const lettersName = StringTwoLetters(name);
            const lettersHtml = `<div class="profile-letters ${newStatus}">${lettersName}</div>`;
            const textStatus = await getStatus(newStatus);
            let nameLocation = undefined;
            if (r.last_location && r.last_location.name) {
              nameLocation = r.last_location.name;
            } else if(!isEmptyObject(r.coordinates)) {
              nameLocation = `<aon-icon-button id="iconLocation" icon="add_location" noHover="true"></aon-icon-button>`;
            }
            const obj = {
              ...r,
              letters_name: lettersName,
              textStatus: textStatus.name,
              lettersHtml,
              status: newStatus,
              name: `${name}`,
              last_location: r.last_location,
              nameLocation,
              dateParse: setDateTimestamp(r.last_date),
              duration: timeHour(Number(r.time)),
            };
            data.push(obj);
          }
        );
      }
    } catch (e) {
      console.log(e);
    }
    this.aonSigninEl.stopLoader();
    return data;
  }

  aonEvent({target}, data) {
    // if(data){
    //   const startDateTask = formatDateOrigin(data.last_date);
    //   this.aonSigninParentEl.setDataFilter({startDateTask, endDateTask:startDateTask});
    // }
    if("add_location" === target.textContent){
      this.aonSigninParentEl.openLocationAdd(undefined, data);
    } else {
      let aonEventDetail = new AonEventDetailList();
      aonEventDetail.id = "aonEventDetailList";
      if(data){
        const startDate = formatDateOrigin(data.last_date);
        aonEventDetail.DATE_TASK = {startDate, endDate:startDate};
      }
      this.aonSigninEl.setContent(aonEventDetail);
    }
  }

  back(){
    let aonPresenceList = new AonPresenceList();
    aonPresenceList.filter = true;
    this.aonSigninEl.setContent(aonPresenceList);
  }
}
window.customElements.define("aon-event-list", AonEventList);
