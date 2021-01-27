import { AonElement } from "../../../components/AonElement.js";
import { setDateTimestamp, timePaser } from "../../../services/utils.js";
import "../../../components/aon-table.js";
import "../../../components/aon-mobile-list.js";
import {
  getGroups,
  getStatus,
  getTaskHolderTimeControl,
} from "../../../services/service.js";

import "./aon-event-add.js";

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
    else if ("data" == name && newValue) {
      this.edit(this.data);
    }
  }

  constructor() {
    super();
    this.aonComunicaEl = this.getElement("aonComunica");
    this.aonComunicaToolbar = this.getElement("aonComunicaToolbar");
    this.id = this.id || "aonEvent";
    this.TABLE_ID = this.id + "Table";
  }

  connectedCallback() {
    this.paintView();
    this.build();
    this.buildToolbar();
  }

  disconnectedCallback() {
    if (this.aonComunicaEl) this.aonComunicaEl.removeFloatOption();
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
    this.aonComunicaToolbar.setAttribute("option", "Eventos");
    this.aonComunicaEl.startLoader();
    if (this.isMobile()) await this.getTableMobile();
    else await this.getTableDesk();
    this.aonComunicaEl.stopLoader();
  }

  async build() {
    if (this.isMobile()) {
      let floatButton = this.getElement(`${aonComunica.id}FloatSpan`);
      if (!floatButton) {
        aonComunica.addFloatOption(
          {
            id: "AddEvent",
            name: "addevent",
            icon: "add",
          },
          () => this.addEventAdd()
        );
      }
    } else {
      aonComunica.addToolbarOption("Add", "add", () => this.addEventAdd());
    }
  }

  async getTableDesk() {
    const aonTable = this.getElement(this.TABLE_ID);
    if (aonTable) {
      aonTable.removeColumns();
      aonTable.addColumn("", "string", "lettersHtml", "5%");
      aonTable.addColumn("Nombre", "string", "name", "35%");
      aonTable.addColumn("Estado", "", "status", "15%");
      aonTable.addColumn("Duración", "", "duration", "5%");
      aonTable.addColumn("Fecha", "date", "dateParse", "20%");
      aonTable.addColumn("Ubicación", "string", "location", "20%");
      try {
        const resp = await this.getData();
        aonTable.removeRows();
        resp.map((res) => {
          let lettersHtml = `<div class="profile-letters">${res.letters_name}</div>`;
          aonTable.addRow(
            {
              ...res,
              status: res.textStatus,
              lettersHtml,
            },
            (el) => this.addEventAdd(el, res)
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
            title: `${res.name} <div style="float: right;">${res.duration}</div>`,
            subtitle: `(${res.textStatus}) ${res.location} <div style="float: right;">${res.dateParse}</div> `,
          };
          if (res.contractType) options.option = this.getOptions(res);
          aonTable.addLi(options, idx, (el) => this.addEventAdd(el, res));
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
      let resp = await getTaskHolderTimeControl(this.data);
      resp.map(
        async ({
          time,
          in_date: date,
          status,
          location,
          task_holder: { id: taskHolderId, name },
        }) => {
          const nameArray = name.split(" ");
          const lettersName = `${nameArray[0].substr(0,1)}${nameArray[1].substr(0,1)}`;
          const newStatus = status.toLowerCase();
          const textStatus = await getStatus(newStatus);
          const obj = {
            letters_name: lettersName,
            textStatus: textStatus.name,
            status: newStatus,
            name: `${name}`,
            date,
            dateParse: setDateTimestamp(date),
            location,
            duration: timePaser(Number(time)),
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

  edit(data) {
    this.getTable();
  }

  addEventAdd(el, data) {
    let id = "aonEventAdd";
    this.aonComunicaEl.setContentHTML(
      `<aon-event-add id="${id}"></aon-event-add>`
    );
    const aonEventEl = this.getElement(id);
    if (data && aonEventEl) {
      aonEventEl.data = data;
    }
  }
}
window.customElements.define("aon-event-list", AonEventList);
