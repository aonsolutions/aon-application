import { AonElement } from "../../../../components/AonElement.js";
import { isEmptyObject, setDateTimestamp, sortBy } from "../../../../services/utils.js";
import {
  getStatus,
} from "../../../../services/service.js";

import { StringTwoLetters } from "../utils.js";

import "../../../../components/aon-table.js";
import "../../../../components/aon-mobile-list.js";
import "./aon-event-add.js";

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
    this.id = this.id || "aonEvent";
    this.TABLE_ID = this.id + "Table";
  }

  connectedCallback() {
    this.paintView();
    this.build();
    this.buildToolbar();
    this.eventListener();
  }

  disconnectedCallback() {
    if (this.aonSigninEl) this.aonSigninEl.removeFloatOption();
  }

  eventListener(){}

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
          () => this.aonEvent()
        );
      }
    } else {
      this.aonSigninEl.addToolbarOption("Add", "add", () =>
        this.aonEvent()
      );
      this.aonSigninEl.addToolbarOption("Previus", "arrow_back", (e) => this.back());
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
    this.aonSigninEl.addToolbarTitle("Detalle");
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
      aonTable.addColumn("Ubicación", "string", "nameLocation", "20%");
      try {
        const resp = await this.getData();
        aonTable.removeRows();
        resp.map((res) => {
          aonTable.addRow(
            {
              ...res,
              status: res.textStatus,
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
            title: `${res.name} <div style="float: right;">${res.textStatus}</div>`,
            subtitle: `<div style="float: right;">${res.nameLocation} ${res.dateParse}</div> `,
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
    if (this.data && this.data.detail) {
      const details = sortBy(this.data.detail, 'date', 'desc');
      details.forEach(
        async (resp) => {
          const name = resp.task_holder.name;
          const lettersName = StringTwoLetters(name);
          const newStatus = resp.status.toLowerCase();
          const lettersHtml = `<div class="profile-letters ${newStatus}">${lettersName}</div>`;
          const textStatus = await getStatus(newStatus);
          let nameLocation = undefined;
          if (resp.location && resp.location.name) {
            nameLocation = resp.location.name;
          } else if(!isEmptyObject(resp.coordinates)) {
            nameLocation = `<aon-icon-button id="iconLocation" icon="add_location" noHover="true"></aon-icon-button>`;
          }
          const obj = {
            ...resp,
            lettersHtml,
            textStatus: textStatus.name,
            status: newStatus,
            name: `${name}`,
            nameLocation,
            dateParse: setDateTimestamp(resp.date)
          };
          data.push(obj);
        }
      );
    }
    return data;
  }

  aonEvent(el, data) {
    if(el && "add_location" === el.target.textContent){
      this.aonSigninEl.getParent().openLocationAdd(el, data);
    } else {
      let id = "aonEventAdd";
      this.aonSigninEl.setContentHTML(
        `<aon-event-add id="${id}"></aon-event-add>`
      );
      const aonEventEl = this.getElement(id);
      if (!data && this.data)  data = this.data;
      aonEventEl.data = data;
    }
  }

  back(){
    this.aonSigninEl.setContentHTML(
     `<aon-presence-list></aon-presence-list>` 
    );
  }
}
window.customElements.define("aon-event-detail-list", AonEventDetailList);
