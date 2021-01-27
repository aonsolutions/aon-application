import { AonElement } from "../../../components/AonElement.js";
import { getGroups, getTimeControlList } from "../../../services/service.js";
import { setDateTimestamp, timePaser } from "../../../services/utils.js";
import "../../../components/aon-table.js";
import "../../../components/aon-mobile-list.js";
import "../../../components/aon-filter.js";
import "./aon-event-list.js";

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
    if ("filter" === name) this.build();
  }

  constructor() {
    super();
    this.aonComunicaEl = this.getElement("aonComunica");
    this.aonComunicaToolbar = this.getElement("aonComunicaToolbar");
    this.TABLE_ID = this.id + "Table";
  }

  connectedCallback() {
    this.paintView();
    this.build();
    this.buildToolbar();
    this.eventListener();
  }
  eventListener() {}

  paintView() {
    let innerHTML = `<aon-filter id="${this.id}Filter" title="Filtros"></aon-filter>`;
    if (this.isMobile()) {
      innerHTML = innerHTML + ` <aon-mobile-list id='${this.TABLE_ID}' />`;
    } else {
      innerHTML = innerHTML + `<aon-table id='${this.TABLE_ID}' />`;
    }

    this.innerHTML = innerHTML;
  }

  async build() {
    this.aonComunicaToolbar.setAttribute("option", "Presencia");
    this.aonComunicaEl.startLoader();
    if (this.isMobile()) await this.getTableMobile();
    else await this.getTableDesk();
    this.aonComunicaEl.stopLoader();
  }

  buildToolbar() {
    this.aonComunicaEl.removeToolbarOptions();
    this.buildFilter();
    const filterEl = this.getElement(`${this.id}Filter`);
    this.aonComunicaEl.addToolbarOption("Filter", "tune", (e) =>
      filterEl.openFilter()
    );
  }

  buildFilter() {
    let aonFilter = this.getElement(`${this.id}Filter`);
    let inputs = [
      {
        type: "select",
        id: "group",
        name: "group",
        title: "Agrupar por",
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
    aonFilter.addEventListener("applyFilter", ({ detail }) => {
      if (detail) this.filter = detail;
    });
    this.getOptionsGroup();
  }

  async getOptionsGroup() {
    this.getElement("group").options = JSON.stringify(await getGroups());
  }

  async getTableDesk() {
    const aonTable = this.getElement(this.TABLE_ID);
    if (aonTable) {
      aonTable.removeColumns();
      aonTable.addColumn("", "string", "lettersHtml", "5%");
      aonTable.addColumn("", "string", "statusHtml", "5%");
      aonTable.addColumn("Nombre", "string", "name", "40%");
      aonTable.addColumn("Duración", "string", "duration", "5%");
      aonTable.addColumn("Fecha", "date", "date", "20%");
      aonTable.addColumn("Ubicación", "string", "location", "25%");
      try {
        const resp = await this.getData();
        aonTable.removeRows();
        resp.map((res) => {
          let statusHtml = `<div class="timeControl ${res.status}"></div>`;
          aonTable.addRow(
            {
              ...res,
              statusHtml,
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
            title: `${res.name} <div style="font-size: 14px;float: right; color: rgba(0,0,0,.54);">${res.duration}<div style="float: right;margin-left: 15px;"></div>`,
            subtitle: `${res.date} <div style="float: right;">${res.location} <div class="timeControl ${res.status}" style="float: right;margin-left: 4px; margin-top: -8%;"></div></div> `,
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
    this.aonComunicaEl.startLoader();
    let data = [];
    try {
      const datos = await getTimeControlList(this.filter);
      datos.map(
        ({
          time,
          in_date:date,
          status,
          location,
          task_holder:{
            id:taskHolderId,
            name
          }
        }) => {
          const nameArray = name.split(" ");
          const lettersName = `${nameArray[0].substr(0, 1)}${nameArray[1].substr(0, 1)}`;
          const lettersHtml = `<div class="profile-letters">${lettersName}</div>`;
          const newStatus = status.toLowerCase();
          const obj = {
            lettersHtml,
            status: newStatus,
            name: `${name}`,
            date: setDateTimestamp(date),
            duration: timePaser(Number(time)),
            location,
            taskHolderId,
          };
          data.push(obj);
        }
      );
    } catch (e) {
      console.log(e);
    }
    this.aonComunicaEl.stopLoader();
    return data;
  }

  async aonEvent({ target: el }, {taskHolderId}) {
    let id = "aonEventList";
    this.aonComunicaEl.setContentHTML(
      `<aon-event-list id="${id}"></aon-event-list>`
    );
    const aonEventEl = this.getElement(id);
    if (aonEventEl) {
      aonEventEl.data = { taskHolderId };
    }
  }
}
window.customElements.define("aon-presence-list", AonPresenceList);
