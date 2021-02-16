import { AonElement } from "../../../components/AonElement.js";
import { getGroups, getTimeControlList } from "../../../services/service.js";
import { isEmptyObject, setDateTimestamp, sortBy, timePaser } from "../../../services/utils.js";
import "../../../components/aon-table.js";
import "../../../components/aon-mobile-list.js";
import "../../../components/aon-filter.js";
import "./event/aon-event-list.js";
import { StringTwoLetters } from "./utils.js";

export class AonPresenceList extends AonElement {
  TABLE_ID;
  GROUP_DEFAULT;
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
    this.id = this.id || "aonPresenceList";
    this.TABLE_ID = this.id + "Table";
    this.GROUP_DEFAULT = "DAY";

    // console.log(atob(localStorage.getItem(this.id)));
  }

  connectedCallback() {
    this.paintView();
    this.build();
    this.buildToolbar();
    this.eventListener();
  }
  
  disconnectedCallback() {
    // localStorage.setItem(this.id, btoa(this.innerHTML))
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
    this.aonSigninEl = this.getElement("aonSignin");
    this.aonSigninEl.addToolbarTitle("Presencia");
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
      aonTable.addColumn("", "string", "lettersHtml", "6%");
      aonTable.addColumn("Nombre", "string", "name", "44%");
      aonTable.addColumn("Duración", "string", "duration", "5%");
      aonTable.addColumn("Fecha", "date", "dateParse", "20%");
      aonTable.addColumn("Ubicación", "string", "nameLocation", "25%");
      try {
        const resp = await this.getData();
        aonTable.removeRows();
        resp.map((res) => {
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
          let options = {
            iconHtmlCustom: `${res.lettersHtml}`,
            title: `${res.name} <div style="font-size: 14px;float: right; color: rgba(0,0,0,.54);">${res.duration}<div style="float: right;"></div>`,
            subtitle: `${res.dateParse} <div style="float: right;">${res.nameLocation}</div> `,
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
      if (datos) {
        sortBy(datos, 'last_date', 'desc').map(
          ({
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
              if (last_location && last_location.name) {
                nameLocation = last_location.name;
              } else if(!isEmptyObject(coordinates)) {
                nameLocation = `<aon-icon-button id="iconLocation" icon="add_location" noHover="true"></aon-icon-button>`;
              }
              const obj = {
                lettersHtml,
                status: newStatus,
                name: `${name}`,
                dateParse: setDateTimestamp(last_date),
                duration: timePaser(Number(time)),
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
      let id = "aonEventList";
      this.aonSigninEl.setContentHTML(
        `<aon-event-list id="${id}"></aon-event-list>`
      );
      const aonEventEl = this.getElement(id);
      if (aonEventEl) {
        aonEventEl.data = { ...data, group: this.GROUP_DEFAULT };
      }
    }
  }
}
window.customElements.define("aon-presence-list", AonPresenceList);
