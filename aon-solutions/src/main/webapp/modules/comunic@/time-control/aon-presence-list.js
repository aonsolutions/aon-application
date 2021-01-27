import { AonElement } from "../../../components/AonElement.js";
import {
  getTimeControlList,
  getTaskHolderTimeControl,
} from "../../../services/service.js";
import {
  serializeForm,
  setDateTimestamp,
  setValueName,
  timePaser,
} from "../../../services/utils.js";
import "../../../components/aon-table.js";
import "../../../components/aon-mobile-list.js";
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
  }

  paintView() {
    if (this.isMobile())
      this.innerHTML = ` <aon-mobile-list id='${this.TABLE_ID}' />`;
    else this.innerHTML = ` <aon-table id='${this.TABLE_ID}' />`;
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

    // if (this.isMobile()) {
    //     let floatButton = this.getElement(`${this.aonComunicaEl.id}FloatSpan`);
    //     if (!floatButton) {
    //         this.aonComunicaEl.addFloatOption({
    //             id: 'AddAlta',
    //             name: 'addalta',
    //             icon: 'add'
    //         }, () => this.openDialogFilter());
    //     }
    // } else {
    this.aonComunicaEl.addToolbarOption("Filter", "tune", () =>
      this.openDialogFilter()
    );
    // }
  }

  openDialogFilter() {
    let d = document.getElementById(this.aonComunicaEl.DIALOG);
    d.clear();
    if (!this.isMobile()) d.width = "400px";
    d.setTitle("Filtros");
    let htmlContent = `
        <form id="filterForm">
          <aon-select name="group" id="group" title="Agrupar"></aon-select>
          <aon-date name="startDate" id="startDate" title="Fecha inicio"></aon-date>
          <aon-date name="endDate" id="endDate" title="Fecha fin"></aon-date>
        </form>
      `;
    d.setContentHTML(htmlContent);
    d.addAcceptAction(() => this.sendFilter());
    d.open();

    this.getElement("group").options = JSON.stringify(this.getGroups());

    if (this.filter) {
      for (const property in this.filter) setValueName(property, this.filter[property]);
    }
  }

  sendFilter() {
    const form = this.getElement(`filterForm`);
    this.filter = serializeForm(form);
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
            title: `${res.name}`,
            subtitle: `${res.location} <div style="float: right;">${res.date} <div class="timeControl ${res.status}" style="float: right;margin-left: 10px; margin-top: 3px;"></div></div> `,
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
    let data = [];
    try {
      const datos = await getTimeControlList(this.filter);
      datos.map(
        ({
          date,
          department,
          name,
          last_name1,
          status,
          location,
          taskHolderId,
          time,
        }) => {
          const lettersName = `${name.substr(0, 1)}${last_name1.substr(0, 1)}`;
          let lettersHtml = `<div class="profile-letters">${lettersName}</div>`;
          //status = out, in, pause
          let newStatus = status.toLowerCase();
          const obj = {
            lettersHtml,
            status: newStatus,
            name: `${name} ${last_name1}`,
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

    return data;
  }

  async aonEvent({ target: el }, { taskHolderId }) {
    let id = "aonEvent";
    this.aonComunicaEl.startLoader();
    this.aonComunicaEl.setContentHTML(
      `<aon-event-list id="${id}"></aon-event-list>`
    );
    try {
      let resp = await getTaskHolderTimeControl({ taskHolderId });
      const aonEvent = this.getElement(id);
      if (resp && aonEvent) {
        aonEvent.data = resp;
      }
    } catch (error) {
      console.log(error);
    }
    this.aonComunicaEl.stopLoader();
  }

  getGroups() {
    return [
      {
        name: "DIA",
        value: "DAY",
      },
      {
        name: "SEMANA",
        value: "WEEK",
      },
      {
        name: "MES",
        value: "MONTH",
      },
      {
        name: "AÑO",
        value: "YEAR",
      },
    ];
  }
}
window.customElements.define("aon-presence-list", AonPresenceList);
