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
import { SigninSidenav, PRESENCE_FILTER, SIGNIN_VIEWS } from "../../signinEnums.js";
import { ToolbarType } from "../../../../models/enums.js";
import { UserAction } from "../../../user/userEnums.js";
import { dateCustomDayHour } from "../utils.js";
import "../../../../components/aon-table.js";
import "../../../../components/aon-mobile-list.js";
import "../../../../components/aon-filter.js";


export class AonEventDetailList extends AonElement {
  TABLE_ID;
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
    this.build();
  }

  initialize() {
    this.id = this.id || "aonEventDetail";
    this.TABLE_ID = this.id + "Table";
    this.TOOLBAR = this.id + "Toolbar";
    this.aonSigninEl = this.getApplication();
    this.aonSigninEl.addToolbarTitle("Detalle");
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
    if(!this.aonSigninParentEl.isEmployee()){
      this.aonSigninEl.addToolbarOption2(SigninSidenav.ADD, (e) => this.aonEvent());
    }

    this.aonSigninEl.addToolbarOption2(SigninSidenav.FILTER, (e) =>
      this.getElement(`${this.id}Filter`).openFilter()
    );
  }

  buildToolbarMobile() {
    const filterEl = this.getElement(`${this.id}Filter`);
    let toolbarEl = this.getElement(this.TOOLBAR);
    this.aonSigninEl.removeToolbarOptions();
    toolbarEl.removeButtons();
    if(!this.aonSigninParentEl.isEmployee()){
      this.aonSigninEl.addFloatOption(SigninSidenav.ADD, () => this.aonEvent() );
    }
    toolbarEl.addButton2(UserAction.BACK, () => this.back());
    this.aonSigninEl.addToolbarOption2(SigninSidenav.FILTER, (e) =>
      filterEl.openFilter()
    );
  }

  async buildFilter() {
    let aonFilter = this.getElement(`${this.id}Filter`);
    aonFilter.setInputs(PRESENCE_FILTER);
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
        filter = { ...this.aonSigninParentEl._filter, ...this.DATE_TASK };
      } catch (error) {}

      const datos = await getTimeControlDetail(filter);
      if (datos) {
        await sortBy(datos, "date", "desc").map(async (resp) => {
          removeEmpty(resp);
          const newStatus = resp.status.toLowerCase();
          const status = await getStatus(newStatus);
          const textStatus = status.name;
          const lettersHtml = `<div class="profile-letters ${newStatus}">${textStatus.substr(0,1)}</div>`;
          let nameLocation = "";
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
    const taskHolder = this.aonSigninParentEl.TASK_HOLDER;
    if(taskHolder) {
      if(this.isMobile()) await this.paintNameMobile(taskHolder);
      else this.aonSigninEl.addTitleToolSection(taskHolder.name);
    }
  }

  async paintNameMobile(taskHolder){
    let iconPhone = "";
    if(!this.aonSigninParentEl.isEmployee()) {
      const auth = await this.aonSigninParentEl.getAuth({task_holder: taskHolder.id});
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
    if (el && "add_location" === el.target.textContent) {
      this.aonSigninParentEl.showView(SIGNIN_VIEWS.AON_LOCATION_ADD, {coordinates:data.coordinates});
    } else {
      this.aonSigninParentEl.showView(SIGNIN_VIEWS.AON_EVENT_ADD, data);
    }
  }

  back() {
    this.aonSigninParentEl.showView(SIGNIN_VIEWS.AON_EVENT_LIST, undefined, true);
  }
}
window.customElements.define("aon-event-detail-list", AonEventDetailList);
