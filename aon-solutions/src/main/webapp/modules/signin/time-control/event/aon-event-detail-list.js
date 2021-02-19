import { AonElement } from "../../../../components/AonElement.js";
import { isEmptyObject, setDateTimestamp, setValueName, sortBy } from "../../../../services/utils.js";
import {
  getStatus,
  getPeriod,
  getTimeControlDetail
} from "../../../../services/service.js";

import { StringTwoLetters } from "../utils.js";
import { AonEventList } from "./aon-event-list.js";
import "../../../../components/aon-table.js";
import "../../../../components/aon-mobile-list.js";
import "../../../../components/aon-filter.js";
import "./aon-event-add.js";

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
    if(value) this.setAttribute("data", JSON.stringify(value));
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

  initialize(){
    this.id = this.id || "aonEvent";
    this.TABLE_ID = this.id + "Table";
    this.aonSigninEl = this.getElement("aonSignin");
    this.aonSigninParentEl = this.aonSigninEl.getParent();
    this.aonSigninParentEl.periodSideNavDisplay(true);
  }

  disconnectedCallback() {
    if (this.aonSigninEl) this.aonSigninEl.removeFloatOption();
  }

  async build() {
    this.paintView();
    this.buildToolbar();
    await this.buildFilter();
    await this.getTable();
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
          () => this.aonEvent()
        );
      }
    } else {
      const filterEl = this.getElement(`${this.id}Filter`);
      this.aonSigninEl.addToolbarOption("Filter", "tune", (e) =>
        filterEl.openFilter()
      );
      // this.aonSigninEl.addToolbarOption("Previus", "arrow_back", (e) => this.back());
      this.aonSigninEl.addToolbarOption("Add", "add", () =>
      this.aonEvent()
    );
    }
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
    aonFilter.addEventListener("applyFilter", ({detail}) => {
      if(detail) this.aonSigninParentEl.setDataFilter(detail);
    });

    let periodEl = this.getElement("period");
    periodEl.options = JSON.stringify(await getPeriod());

    periodEl.addEventListener('change', ({detail}) => {
      if(detail){
        const {startDate, endDate} = detail;
        setValueName('startDate', startDate);
        setValueName('endDate', endDate);
      }
    });

    this.getElement("startDate").addEventListener("change",(ev)=>{
      periodEl.value = "personalized";
    });
    this.getElement("endDate").addEventListener("change",(ev)=>{
      periodEl.value = "personalized";
    });
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
      aonTable.addBack((e)=>this.back());
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
            title: `${res.name} (${res.textStatus})`,
            subtitle: `${res.dateParse}<span style="float: right;">${res.nameLocation}</span> `,
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
        try { filter = {...this.aonSigninParentEl._filter, ...this.DATE_TASK};} catch (error) {}

        const datos = await getTimeControlDetail(filter);
        if(datos){
          sortBy(datos, 'date', 'desc').forEach(
            async (resp) => {
              const name = resp.task_holder.name;
              if(!this.TASK_HOLDER) this.TASK_HOLDER =  resp.task_holder;
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
    } catch (error) {
      console.log(error);
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
      if (!data && this.TASK_HOLDER){
        data = {task_holder: this.TASK_HOLDER, name: this.TASK_HOLDER.name}; 
      }
      if(data) aonEventEl.data = data;
    }
  }

  back() {
    let aonEventList = new AonEventList();
    aonEventList.filter = true;
    this.aonSigninEl.setContent(aonEventList);
  }
}
window.customElements.define("aon-event-detail-list", AonEventDetailList);
