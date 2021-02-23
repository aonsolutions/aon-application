import { AonElement } from "../../../../components/AonElement.js";
import {
  removeEmpty,
  setDateTimestamp,
  setValueName,
  sortBy,
} from "../../../../services/utils.js";
import {
  getStatus,
  getPeriod,
  getTimeControlDetail,
} from "../../../../services/service.js";

import { AonEventList } from "./aon-event-list.js";
import { ToolbarType } from "../../../../models/enums.js";
import { UserAction } from "../../../user/userEnums.js";
import { AonEventAdd } from "./aon-event-add.js";
import "../../../../components/aon-table.js";
import "../../../../components/aon-mobile-list.js";
import "../../../../components/aon-filter.js";


export class AonEventDetailList extends AonElement {
  TABLE_ID;
  TASK_HOLDER;
  DATE_TASK;
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
    if (value) this.setAttribute("data", JSON.stringify(value));
  }

  get id() {
    return this.getAttribute("id");
  }

  set id(id) {
    this.setAttribute("id", id);
  }

  attributeChangedCallback(name, oldValue, newValue) {
    if ("filter" === name) {
      this.initialize();
      this.DATE_TASK = null;
      this.getTable();
    }
  }

  constructor() {
    super();
  }

  connectedCallback() {
    this.initialize();
    this.aonSigninEl.addToolbarTitle("Detalle");
    this.build();
  }

  initialize() {
    this.id = this.id || "aonEventDetail";
    this.TABLE_ID = this.id + "Table";
    this.TOOLBAR = this.id + "Toolbar";
    this.aonSigninEl = this.getElement("aonSignin");
    this.aonSigninParentEl = this.aonSigninEl.getParent();
    this.aonSigninParentEl.periodSideNavDisplay(true);
  }

  disconnectedCallback() {
    if (this.aonSigninEl) this.aonSigninEl.removeFloatOption();
  }

  async build() {
    this.paintView();
    if (this.isMobile()) {
      this.buildToolbarMobile();
    } else {
      this.buildToolbarDesk();
    }
    await this.buildFilter();
    await this.getTable();
  }

  paintView() {
    let innerHTML = `<aon-filter id="${this.id}Filter" title="Filtros"></aon-filter>`;
    const aonToolbar = this.isMobile()
      ? `<aon-toolbar id="${this.TOOLBAR}" type="${ToolbarType.SECONDARY}"> </aon-toolbar>`
      : "";
    if (this.isMobile()) {
      innerHTML = innerHTML + ` <aon-mobile-list id='${this.TABLE_ID}' />`;
    } else {
      innerHTML = innerHTML + ` <aon-table id='${this.TABLE_ID}' />`;
    }

    this.innerHTML = aonToolbar + innerHTML;
  }

  buildToolbarDesk() {
    this.aonSigninEl.removeToolbarOptions();
    this.aonSigninEl.addToolbarOption("Save", "add", (e) => this.aonEvent());
    this.aonSigninEl.addToolbarOption("Filter", "tune", (e) =>
      this.getElement(`${this.id}Filter`).openFilter()
    );
  }

  buildToolbarMobile() {
    const filterEl = this.getElement(`${this.id}Filter`);
    let toolbarEl = this.getElement(this.TOOLBAR);
    this.aonSigninEl.removeToolbarOptions();
    toolbarEl.removeButtons();

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
    toolbarEl.addButton2(UserAction.BACK, () => this.back());
    this.aonSigninEl.addToolbarOption("Filter", "tune", (e) =>
      filterEl.openFilter()
    );
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
    aonFilter.addEventListener("applyFilter", ({ detail }) => {
      if (detail) this.aonSigninParentEl.setDataFilter(detail);
    });

    let periodEl = this.getElement("period");
    periodEl.options = JSON.stringify(await getPeriod());

    periodEl.addEventListener("change", ({ detail }) => {
      if (detail) {
        const { startDate, endDate } = detail;
        setValueName("startDate", startDate);
        setValueName("endDate", endDate);
      }
    });

    this.getElement("startDate").addEventListener("change", (ev) => {
      periodEl.value = "personalized";
    });
    this.getElement("endDate").addEventListener("change", (ev) => {
      periodEl.value = "personalized";
    });
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

  async getTableDesk() {
    const aonTable = this.getElement(this.TABLE_ID);
    if (aonTable) {
      aonTable.removeColumns();
      aonTable.addColumnIcon("arrow_back", "string", "lettersHtml", "6%", ()=>this.back());
      aonTable.addColumn("Estado", "string", "textStatus", "10%");
      aonTable.addColumn("Fecha", "date", "dateParse", "20%");
      aonTable.addColumn("Ubicación", "string", "nameLocation", "30%");
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
      try {
        const resp = await this.getData();
        aonTable.removeAllLi();
        resp.map((res, idx) => {
          let options = {
            paddingTopTitle: "5px",
            iconHtmlCustom: `${res.lettersHtml} <span style="padding-top: 5px;float: right;color: rgba(0,0,0,.54);">${res.dateParse}</span>`,
            title: `${res.textStatus}`,
            subtitle: `<span style="float: right;">${res.nameLocation}</span>`,
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
      try {
        filter = { ...this.aonSigninParentEl._filter, ...this.DATE_TASK };
      } catch (error) {}

      const datos = await getTimeControlDetail(filter);
      if (datos) {
        sortBy(datos, "date", "desc").forEach(async (resp) => {
          removeEmpty(resp);
          const name = resp.task_holder.name;
          if (!this.TASK_HOLDER) this.TASK_HOLDER = resp.task_holder;
          const newStatus = resp.status.toLowerCase();
          const status = await getStatus(newStatus);
          const textStatus = status.name;
          const lettersHtml = `<div class="profile-letters ${newStatus}">${textStatus.substr(0,1)}</div>`;
          let nameLocation = undefined;
          if (resp.location && resp.location.name) {
            nameLocation = resp.location.name;
          } else if (resp.coordinates) {
            nameLocation = `<aon-icon-button id="iconLocation" icon="add_location" noHover="true"></aon-icon-button>`;
          }
          const obj = {
            ...resp,
            lettersHtml,
            textStatus,
            status: newStatus,
            name: `${name}`,
            nameLocation,
            dateParse: setDateTimestamp(resp.date),
          };
          data.push(obj);
        });
      }
    } catch (error) {
      console.log(error);
    }
    this.paintName();
    return data;
  }

  paintName(){
    if(this.TASK_HOLDER) {
      if(this.isMobile()) this.getElement(this.TOOLBAR).title = this.TASK_HOLDER.name;
      else this.aonSigninEl.addTitleToolSection(this.TASK_HOLDER.name);
    }
  }

  aonEvent(el, data) {
    if (el && "add_location" === el.target.textContent) {
      this.aonSigninEl.getParent().openLocationAdd(el, data);
    } else {
      let id = "aonEventAdd";
      let aonEventAdd = new AonEventAdd();
      aonEventAdd.id = id;
      if (!data && this.TASK_HOLDER) {
        data = { task_holder: this.TASK_HOLDER, name: this.TASK_HOLDER.name };
      }
      if (data) aonEventAdd.data = data;
      this.aonSigninEl.setContent(aonEventAdd);
    }
  }

  back() {
    let aonEventList = new AonEventList();
    aonEventList.filter = true;
    this.aonSigninEl.setContent(aonEventList);
  }
}
window.customElements.define("aon-event-detail-list", AonEventDetailList);
