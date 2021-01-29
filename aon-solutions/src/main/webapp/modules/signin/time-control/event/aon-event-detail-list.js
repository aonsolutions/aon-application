import { AonElement } from "../../../../components/AonElement.js";
import { setDateTimestamp, timePaser } from "../../../../services/utils.js";
import "../../../../components/aon-table.js";
import "../../../../components/aon-mobile-list.js";
import {
  getGroups,
  getStatus,
  getTaskHolderTimeControl,
} from "../../../../services/service.js";

import "./aon-event-add.js";
import { StringTwoLetters } from "../utils.js";

export class AonEventDetailList extends AonElement {
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

  disconnectedCallback() {
    if (this.aonSigninEl) this.aonSigninEl.removeFloatOption();
  }

  buildToolbar() {
    this.aonSigninEl.removeToolbarOptions();

    if (this.isMobile()) {
      let floatButton = this.getElement(`${this.aonSigninEl.id}FloatSpan`);
      if (!floatButton) {
        this.aonSigninEl.addFloatOption(
          {
            id: "AddEvent",
            name: "addevent",
            icon: "add",
          },
          () => this.addEventAdd()
        );
      }
    } else {
      this.aonSigninEl.addToolbarOption("Add", "add", () =>
        this.addEventAdd()
      );
    }
  }

  paintView() {
    let innerHTML = ``;
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
      aonTable.addColumn("Fecha", "date", "dateParse", "25%");
      aonTable.addColumn("Ubicación", "string", "location", "20%");
      try {
        const resp = await this.getData();
        aonTable.removeRows();
        resp.map((res) => {
          aonTable.addRow(
            {
              ...res,
              status: res.textStatus,
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
            title: `${res.name} <div style="float: right;">${res.textStatus}</div>`,
            subtitle: `${res.location} <div style="float: right;">${res.dateParse}</div> `,
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
    if (this.data && this.data.detail) {
      this.data.detail.forEach(
        async ({ date, status, location, task_holder }) => {
          const name = task_holder.name;
          const lettersName = StringTwoLetters(name);
          const lettersHtml = `<div class="profile-letters">${lettersName}</div>`;
          const newStatus = status.toLowerCase();
          const textStatus = await getStatus(newStatus);
          const obj = {
            lettersHtml,
            textStatus: textStatus.name,
            status: newStatus,
            name: `${name}`,
            date: setDateTimestamp(date),
            location: 'LOCATION'
          };
          data.push(obj);
        }
      );
    }
    return data;
  }

  addEventAdd(el, data) {
    let id = "aonEventAdd";
    this.aonSigninEl.setContentHTML(
      `<aon-event-add id="${id}"></aon-event-add>`
    );
    const aonEventEl = this.getElement(id);
    if (!data) {
      data = this.data;
    }
    aonEventEl.data = data;
  }
}
window.customElements.define("aon-event-detail-list", AonEventDetailList);
