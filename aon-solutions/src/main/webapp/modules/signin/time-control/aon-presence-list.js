import { AonElement } from "../../../components/AonElement.js";
import { getGroups, getTimeControlList } from "../../../services/service.js";
import { setDateTimestamp, timePaser } from "../../../services/utils.js";
import "../../../components/aon-table.js";
import "../../../components/aon-mobile-list.js";
import "../../../components/aon-filter.js";
import "./event/aon-event-list.js";
import { StringTwoLetters } from "./utils.js";

export class AonPresenceList extends AonElement {
  TABLE_ID;
  GROUP_DEFAULT;
  DIALOG;
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
    this.id = this.id || 'aonPresenceList';
    this.TABLE_ID = this.id + "Table";
    this.GROUP_DEFAULT = "DAY";
    this.DIALOG = this.id+'Dialog';
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
    innerHTML = innerHTML + `<aon-dialog id="${this.DIALOG}"> </aon-dialog>`;
    if (this.isMobile()) {
      innerHTML = innerHTML + ` <aon-mobile-list id='${this.TABLE_ID}' />`;
    } else {
      innerHTML = innerHTML + `<aon-table id='${this.TABLE_ID}' />`;
    }

    this.innerHTML = innerHTML;
  }

  async build() {
    this.aonSigninEl = this.getElement("aonSignin");
    this.aonSigninToolbar = this.getElement(`${this.aonSigninEl.id}Toolbar`);
    this.aonSigninToolbar.setAttribute("option", "Presencia");

    this.aonSigninEl.startLoader();
    if (this.isMobile()) await this.getTableMobile();
    else await this.getTableDesk();
    this.aonSigninEl.stopLoader();
  }

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
      if (detail) {
        this.filter = detail;
        if (this.filter.group) {
          this.GROUP_DEFAULT = this.filter.group;
        }
      }
    });
    this.getOptionsGroup();
  }

  async getOptionsGroup() {
    let groupEl = this.getElement("group");
    groupEl.options = JSON.stringify(await getGroups());
    groupEl.value = this.GROUP_DEFAULT;
  }

  async getTableDesk() {
    const aonTable = this.getElement(this.TABLE_ID);
    if (aonTable) {
      aonTable.removeColumns();
      aonTable.addColumn("", "string", "lettersHtml", "5%");
      aonTable.addColumn("", "string", "statusHtml", "5%");
      aonTable.addColumn("Nombre", "string", "name", "40%");
      aonTable.addColumn("Duración", "string", "duration", "5%");
      aonTable.addColumn("Fecha", "date", "last_date", "20%");
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
            (el) =>this.eventClick(el,res) 
          );
        });
      } catch (e) {
        console.log(e);
      }
    }
  }

  eventClick(el, res){
    console.log(el.target);
    // if("LOCATION"===el.target.textContent){
      let d = this.getElement(this.DIALOG);
            
      const latitude = 42.8649459;
      const longitude = -2.6961458;
      const iframeMaps = `
      <iframe src="https://maps.google.es/maps?q=${latitude},${longitude}&z=17&output=embed"  frameborder="0" style="border:0;height: 400px;width: 100%;"></iframe>
      `;
      d.setContentHTML(iframeMaps);
      d.open();
    // } else {
    //   this.aonEvent(el, res);
    // }
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
            subtitle: `${res.last_date} <div style="float: right;">${res.location} <div class="timeControl ${res.status}" style="float: right;margin-left: 4px; margin-top: -8%;"></div></div> `,
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
      const datos = await getTimeControlList(this.filter);
      datos.map(
        ({
          time,
          last_date,
          status,
          location,
          task_holder: { id: taskHolderId, name },
        }) => {
          const lettersName = StringTwoLetters(name);
          const lettersHtml = `<div class="profile-letters">${lettersName}</div>`;
          const newStatus = status.toLowerCase();
          const obj = {
            lettersHtml,
            status: newStatus,
            name: `${name}`,
            last_date: setDateTimestamp(last_date),
            duration: timePaser(Number(time)),
            location: undefined,
            taskHolderId,
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

  async aonEvent({ target: el }, { taskHolderId }) {
    let id = "aonEventList";
    this.aonSigninEl.setContentHTML(
      `<aon-event-list id="${id}"></aon-event-list>`
    );
    const aonEventEl = this.getElement(id);
    if (aonEventEl) {
      let obj = { taskHolderId, group: this.GROUP_DEFAULT };
      aonEventEl.data = obj;
    }
  }
}
window.customElements.define("aon-presence-list", AonPresenceList);
