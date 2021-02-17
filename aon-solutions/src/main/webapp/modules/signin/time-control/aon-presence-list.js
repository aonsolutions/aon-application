import { AonElement } from "../../../components/AonElement.js";
import { getPeriod, getStatus, getTimeControlList } from "../../../services/service.js";
import { isEmptyObject, setDateTimestamp, setDateTimestampDay, setValueName, sortBy, timeHour } from "../../../services/utils.js";
import "../../../components/aon-table.js";
import "../../../components/aon-mobile-list.js";
import "../../../components/aon-filter.js";
import {AonEventList} from "./event/aon-event-list.js";

import { StringTwoLetters } from "./utils.js";

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
    if ("filter" === name){}
  }

  constructor() {
    super();
    this.id = this.id || "aonPresenceList";
    this.TABLE_ID = this.id + "Table";
    this.aonSigninEl = this.getElement("aonSignin");
    this.aonSigninEl.addToolbarTitle("Presencia");
    // console.log(atob(localStorage.getItem(this.id)));
  }

  connectedCallback() {
    this.build();
  }
  
  disconnectedCallback() {
    // localStorage.setItem(this.id, btoa(this.innerHTML))
  }

  async build(){
    this.paintView();
    window.addEventListener('filterAonSignin', ({detail})=>{
      if(detail && detail.period) setValueName('period', detail.period);
      this.getTable();
    })
    await this.buildToolbar();
    await this.getTable();
  }

  paintView() {
    let innerHTML = `<aon-filter id="${this.id}Filter" title="Filtros"></aon-filter>`;
    if (this.isMobile()) {
      innerHTML = innerHTML + ` <aon-mobile-list id='${this.TABLE_ID}' />`;
    } else {
      innerHTML = innerHTML + `<aon-table id='${this.TABLE_ID}' />`;
    }

    this.innerHTML = innerHTML;
  }

  async buildToolbar() {
    this.aonSigninEl.removeToolbarOptions();
    await this.buildFilter();
    const filterEl = this.getElement(`${this.id}Filter`);
    this.aonSigninEl.addToolbarOption("Filter", "tune", (e) =>
      filterEl.openFilter()
    );
  }

  async getTable() {
    this.aonSigninEl.startLoader();
    if (this.isMobile()) await this.getTableMobile();
    else await this.getTableDesk();
    this.aonSigninEl.stopLoader();
  }

  async buildFilter() {
    let aonFilter = this.getElement(`${this.id}Filter`);
    let inputs = [
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
    aonFilter.addEventListener("applyFilter", () => {
      this.getTable();
    });

    await this.getOptionsPeriod();
  }

  async getOptionsPeriod() {
    let periodEl = this.getElement("period");
    periodEl.options = JSON.stringify(await getPeriod());
    this.eventListenerFilter();
  }

  eventListenerFilter(){
    let periodEl = this.getElement("period");
    periodEl.addEventListener('change', ({detail}) => {
      setValueName('startDate', detail.startDate);
      setValueName('endDate', detail.endDate);
    });

    this.getElement("startDate").addEventListener("change",(ev)=>{
      periodEl.value = "personalized";
    });
    this.getElement("endDate").addEventListener("change",(ev)=>{
      periodEl.value = "personalized";
    });

    periodEl.value = "today";
  }

  async getTableDesk() {
    const aonTable = this.getElement(this.TABLE_ID);
    if (aonTable) {
      aonTable.removeColumns();
      aonTable.addColumn("", "string", "lettersHtml", "6%");
      aonTable.addColumn("Nombre", "string", "name", "34%");
      aonTable.addColumn("Estado", "", "textStatus", "15%");
      aonTable.addColumn("Fecha", "date", "dateParse", "20%");
      aonTable.addColumn("Duración", "", "duration", "5%");
      aonTable.addColumn("Ubicación", "string", "nameLocation", "20%");
      try {
        const resp = await this.getData();
        aonTable.removeRows();
        resp.map((res) => {
          res.dateParse = setDateTimestampDay(res.last_date);
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
      aonTable.createAonDialog();
      try {
        const resp = await this.getData();
        aonTable.removeAllLi();
        resp.map((res, idx) => {
          res.dateParse = setDateTimestamp(res.last_date);
          let options = {
            iconHtmlCustom: `${res.lettersHtml}`,
            title: `${res.name}`,
            subtitle: `(${res.duration}) ${res.dateParse} <span style="float: right;">${res.nameLocation}</span> `,
          };
          if (res.contractType) options.option = this.getOptions(res);
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
      const filtros = this.getElement(`${this.id}Filter`).getValues();
      const datos = await getTimeControlList(filtros);
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
              let nameLocation = undefined;
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
    this.aonSigninEl.stopLoader();
    return data;
  }

  aonEvent({ target }, data) {
    if ("add_location" === target.textContent) {
      this.aonSigninEl.getParent().openLocationAdd(undefined, data);
    } else {
      let aonList= new AonEventList();
      aonList.id = "aonEventList";
      const filtros = this.getElement(`${this.id}Filter`).getValues();
      aonList.data = { ...data, ...filtros};
      this.aonSigninEl.setContent(aonList);
    }
  }
}

window.customElements.define("aon-presence-list", AonPresenceList);
