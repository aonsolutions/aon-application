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
import { SigninSidenav, PRESENCE_FILTER, SIGNIN_VIEWS, iconAddLocation } from "../../signinEnums.js";
import { ToolbarType } from "../../../../models/enums.js";
import { dateCustomDayHour } from "../utils.js";
import "../../../../components/aon-table.js";
import "../../../../components/aon-mobile-list.js";
import "../../../../components/aon-filter.js";
import { EVENT, MSG } from "../../../../environments/environments.js";

import * as ACTION from '../../../actions.js';

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
      this.getTable();
    }
  }

  constructor() {
    super();
  }

  connectedCallback() {
    this.initialize();
    this.build();
  }

  initialize() {
    this.id = this.id || SIGNIN_VIEWS.AON_EVENT_DETAIL_LIST;
    this.TABLE_ID = this.id + "Table";
    this.TOOLBAR = this.id + "Toolbar";
    this.applicationEl = this.getApplication();
    this.applicationParentEl = this.getApplicationParent();
    this.applicationEl.addToolbarTitle("Detalle");
    this.applicationParentEl.periodSideNavDisplay(true);
  }

  disconnectedCallback() {
    if (this.applicationEl) this.applicationEl.removeFloatOption();
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
    this.applicationEl.removeToolbarOptions();
    if(!this.applicationParentEl.isEmployee()){
      this.applicationEl.addToolbarOption2(SigninSidenav.ADD, (e) => this.aonEvent());
    }

    this.applicationEl.addToolbarOption2(SigninSidenav.FILTER, (e) =>
      this.getElement(`${this.id}Filter`).openFilter()
    );
  }

  buildToolbarMobile() {
    const filterEl = this.getElement(`${this.id}Filter`);
    let toolbarEl = this.getElement(this.TOOLBAR);
    this.applicationEl.removeToolbarOptions();
    toolbarEl.removeButtons();
    if(!this.applicationParentEl.isEmployee()){
      this.applicationEl.addFloatOption(SigninSidenav.ADD, () => this.aonEvent() );
    }
    toolbarEl.addButton2(ACTION.BACK, () => this.back());
    this.applicationEl.addToolbarOption2(SigninSidenav.FILTER, (e) =>
      filterEl.openFilter()
    );
  }

  async buildFilter() {
    let aonFilter = this.getElement(`${this.id}Filter`);
    aonFilter.setInputs(PRESENCE_FILTER);
    aonFilter.addEventListener(EVENT.APPLY_FILTER, ({ detail }) => {
      if (detail) this.applicationParentEl.setDataFilter(detail);
    });

    let periodEl = this.getElement("period");
    periodEl.options = JSON.stringify(getPeriod());

    periodEl.addEventListener(EVENT.CHANGE, ({ detail }) => {
      if (detail) {
        const { startDate, endDate } = detail;
        setValueName("startDate", startDate);
        setValueName("endDate", endDate);
      }
    });

    this.getElement("startDate").addEventListener(EVENT.CHANGE, (ev) => {
      periodEl.value = "personalized";
    });
    this.getElement("endDate").addEventListener(EVENT.CHANGE, (ev) => {
      periodEl.value = "personalized";
    });
  }

  async getTable() {
    this.applicationEl.startLoader();
    if (this.isMobile()) {
      await this.getTableMobile();
    } else {
      await this.getTableDesk();
    }
    this.applicationEl.stopLoader();
    this.applicationParentEl.changeFilter();
  }

  async getTableDesk() {
    const aonTable = this.getElement(this.TABLE_ID);
    if (aonTable) {
      aonTable.removeColumns();
      aonTable.addColumnIcon("arrow_back", "string", "lettersHtml", "6%", ()=>this.back());
      aonTable.addColumn(MSG.STATUS, "string", "textStatus", "10%");
      aonTable.addColumn(MSG.DATE, "date", "dateParse", "20%");
      aonTable.addColumn(MSG.LOCATION, "string", "nameLocation", "30%");
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
          let newDate = dateCustomDayHour(res.date);
          if(newDate){res.dateParse = newDate;}
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
    let data = [];
    try {
      let filter = null;
      try {
        filter = { ...this.applicationParentEl._filter, ...this.applicationParentEl.DATE_TMP };
      } catch (error) {}

      const datos = await getTimeControlDetail(filter);
      if (datos) {
        await sortBy(datos, "date", "asc").map(async (resp) => {
          removeEmpty(resp);
          const newStatus = resp.status.toLowerCase();
          const status = await getStatus(newStatus);
          const textStatus = status.name;
          const lettersHtml = `<div class="profile-letters ${newStatus}">${textStatus.substr(0,1)}</div>`;
          let nameLocation = "";
          if (resp.location && resp.location.name) {
            nameLocation = resp.location.name;
          } else if (resp.coordinates) {
            nameLocation = `<aon-icon-button id="iconLocation" icon="${iconAddLocation}" noHover="true"></aon-icon-button>`;
          }
          const obj = {
            ...resp,
            lettersHtml,
            textStatus,
            status: newStatus,
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

  async paintName(){
    const taskHolder = this.applicationParentEl.TASK_HOLDER;
    if(taskHolder) {
      if(this.isMobile()) await this.paintNameMobile(taskHolder);
      else this.applicationEl.addTitleToolSection(taskHolder.name);
    }
  }

  async paintNameMobile(taskHolder){
    let iconPhone = "";
    if(!this.applicationParentEl.isEmployee()) {
      const auth = await this.applicationParentEl.getAuth({task_holder: taskHolder.id});
      if(auth && auth.phone){
        let color = "black";
        if(taskHolder.status && "in"===taskHolder.status) color = "green";
        iconPhone = `<a href="tel:+34${auth.phone}"><aon-icon-button id="iconPhone" icon="phone_in_talk" noHover="true" color="${color}"></aon-icon-button></a>`;
      }
    }
    this.getElement(this.TOOLBAR).title = `${taskHolder.name}${iconPhone}`;

    let elIcon = this.getElement('iconPhone');
    if(elIcon){
      let buttonEl = elIcon.querySelector('button');
      buttonEl.style.top = "4px";
      buttonEl.querySelector('i').style.fontSize="20px";
    } 
  }

  aonEvent(el, data) {
    if (el && iconAddLocation === el.target.textContent) {
      this.applicationParentEl.showView(SIGNIN_VIEWS.AON_LOCATION_ADD, {coordinates:data.coordinates});
    } else {
      this.applicationParentEl.showView(SIGNIN_VIEWS.AON_EVENT_ADD, data);
    }
  }

  back() {
    this.applicationParentEl.showView(SIGNIN_VIEWS.AON_EVENT_LIST, undefined, true);
  }
}
window.customElements.define("aon-event-detail-list", AonEventDetailList);
