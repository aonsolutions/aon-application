import { AonElement } from "../../../../components/AonElement.js";
import { setDateTimestamp, timePaser } from "../../../../services/utils.js";
import "../../../../components/aon-table.js";
import "../../../../components/aon-mobile-list.js";
import {
  getGroups,
  getStatus,
  getTaskHolderTimeControl,
} from "../../../../services/service.js";

import "./aon-event-detail-list.js";
import { StringTwoLetters } from "../utils.js";

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
    if ("filter" === name) {
      this.data = { ...this.data, ...this.filter };
    } else if ("data" == name && newValue) {
      this.getTable();
    }
  }

  constructor() {
    super();
    this.aonSigninEl = this.getElement("aonSignin");
    this.aonSigninToolbar = this.getElement(
      `${this.aonSigninEl.id}Toolbar`
    );
    this.id = this.id || "aonEvent";
    this.TABLE_ID = this.id + "Table";
  }

  connectedCallback() {
    this.paintView();
    this.build();
    this.buildToolbar();
  }

  disconnectedCallback() {}

  buildToolbar() {
    this.aonSigninEl.removeToolbarOptions();
    this.buildFilter();
    const filterEl = this.getElement(`${this.id}Filter`);
    this.aonSigninEl.addToolbarOption("Filter", "tune", (e) =>
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
    let groupEl = this.getElement("group");
    groupEl.options = JSON.stringify(await getGroups());
    if (this.data && this.data.group) {
      groupEl.value = this.data.group;
    }
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
    this.aonSigninToolbar.setAttribute("option", "Eventos");
    this.aonSigninEl.startLoader();
    if (this.isMobile()) {
      await this.getTableMobile();
    } else {
      await this.getTableDesk();
    }
    this.aonSigninEl.stopLoader();
  }

  async build() {}

  async getTableDesk() {
    const aonTable = this.getElement(this.TABLE_ID);
    if (aonTable) {
      aonTable.removeColumns();
      aonTable.addColumn("", "string", "lettersHtml", "5%");
      aonTable.addColumn("Nombre", "string", "name", "35%");
      aonTable.addColumn("Estado", "", "status", "15%");
      aonTable.addColumn("Duración", "", "duration", "5%");
      aonTable.addColumn("Fecha", "date", "dateParse", "20%");
      aonTable.addColumn("Ubicación", "string", "textCoordinates", "20%");
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
            subtitle: `(${res.textStatus}) ${res.textCoordinates} <div style="float: right;">${res.dateParse}</div> `,
          };
          aonTable.addLi(options, idx, (el) => this.addEventAdd(el, res));
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
      let resp = await getTaskHolderTimeControl(this.data);
      resp.map(
        async (r) => {
          const name = r.task_holder.name;
          const lettersName = StringTwoLetters(name);
          const newStatus = r.status.toLowerCase();
          const textStatus = await getStatus(newStatus);
          const obj = {
            ...r,
            letters_name: lettersName,
            textStatus: textStatus.name,
            status: newStatus,
            name: `${name}`,
            textCoordinates: undefined,
            dateParse: setDateTimestamp(r.last_date),
            duration: timePaser(Number(r.time)),
          };
          data.push(obj);
        }
      );
    } catch (e) {
      console.log(e);
    }
    this.aonSigninEl.stopLoader();
    return data;
  }

  addEventAdd(el, data) {
    let id = "aonEventDetailList";
    this.aonSigninEl.setContentHTML(
      `<aon-event-detail-list id="${id}"></aon-event-detail-list>`
    );
    const aonEventEl = this.getElement(id);
    if (data && aonEventEl) {
      aonEventEl.data = data;
    }
  }
}
window.customElements.define("aon-event-list", AonEventList);
