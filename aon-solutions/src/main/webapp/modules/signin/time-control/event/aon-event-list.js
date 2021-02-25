import { AonElement } from "../../../../components/AonElement.js";
import { setFullDate, setValueName, sortBy, timeHour,  setDateTpDay, firstLetters } from "../../../../services/utils.js";
import {
  getGroups,
  getPeriod,
  getStatus,
  getTaskHolderTimeControl,
} from "../../../../services/service.js";
import { ToolbarType } from "../../../../models/enums.js";
import { UserAction } from "../../../user/userEnums.js";
import { EventListFilterInput, SigninSidenav } from "../../signinEnums.js";
import "../../../../components/aon-table.js";
import "../../../../components/aon-mobile-list.js";
import "../../../../components/aon-filter.js";


export class AonEventList extends AonElement {
  TABLE_ID;
  TASK_HOLDER;
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
    if ("filter" === name) this.getTable();
  }

  constructor() {
    super();
    this.id = this.id || "aonEventList";
    this.TABLE_ID = this.id + "Table";
    this.TOOLBAR = this.id + "Toolbar";
    this.aonSigninEl = this.getElement("aonSignin");
    this.aonSigninParentEl = this.aonSigninEl.getParent();
    this.aonSigninParentEl.periodSideNavDisplay(true);
  }

  connectedCallback() {
    this.aonSigninEl.addToolbarTitle("Resumen");
    this.build();
  }

  disconnectedCallback() {}

  async build() {
    this.paintView();
    if(this.isMobile()){
      this.buildToolbarMobile();
    } else {
      this.buildToolbarDesk();
    }

    await this.buildFilter();
    await this.getTable();

  }


  paintView() {
    let innerHTML = `<aon-filter id="${this.id}Filter" title="Filtros"></aon-filter>`;
    const aonToolbar = this.isMobile() ? `<aon-toolbar id="${this.TOOLBAR}" type="${ToolbarType.SECONDARY}"> </aon-toolbar>` :'';
    if (this.isMobile()) {
      innerHTML = innerHTML + ` <aon-mobile-list id='${this.TABLE_ID}' />`;
    } else {
      innerHTML = innerHTML + ` <aon-table id='${this.TABLE_ID}' />`;
    }

    this.innerHTML = aonToolbar + innerHTML;
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


  buildToolbarDesk() {
    this.aonSigninEl.removeToolbarOptions();
    const filterEl =  this.getElement(`${this.id}Filter`);
    this.aonSigninEl.addToolbarOption2(SigninSidenav.FILTER, (e) => filterEl.openFilter());
  }
  buildToolbarMobile(){
    this.aonSigninEl.removeToolbarOptions();
    let toolbarEl = this.getElement(this.TOOLBAR);
    const filterEl =  this.getElement(`${this.id}Filter`);
    toolbarEl.removeButtons();
    if(!this.aonSigninParentEl.isEmployee()){
		  toolbarEl.addButton2(UserAction.BACK, () => this.back());
    }
    this.aonSigninEl.addToolbarOption2(SigninSidenav.FILTER, (e) => filterEl.openFilter());
  }

  async buildFilter() {
    let aonFilter = this.getElement(`${this.id}Filter`);
    aonFilter.setInputs(EventListFilterInput);
    aonFilter.addEventListener("applyFilter", ({detail}) => {
      if(detail) this.aonSigninParentEl.setDataFilter(detail);
    });

    let groupEl = this.getElement('group');
    groupEl.options = JSON.stringify(await getGroups());

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


  async getTableDesk() {
    const aonTable = this.getElement(this.TABLE_ID);
    if (aonTable) {
      aonTable.removeColumns();
      let iconBack = "";
      if(!this.aonSigninParentEl.isEmployee()){
        iconBack = "arrow_back";
      }
      aonTable.addColumnIcon(iconBack, "string", "lettersHtml", "6%", ()=>this.back());
      aonTable.addColumn("Fecha", "date", "dateParse", "40%");
      aonTable.addColumn("Duración", "", "duration", "30%");
      try {
        const resp = await this.getData();
        aonTable.removeRows();
        resp.map((res) => {
          res.dateParse = firstLetters(setFullDate(res.start_date));
          aonTable.addRow(
            {
              ...res,
              status: res.textStatus
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
          res.dateParse = firstLetters(setDateTpDay(res.start_date));
          let options = {
            paddingTopTitle: "5px",
            iconHtmlCustom: `${res.lettersHtml} <span style="padding-top: 5px;float: right;color: rgba(0,0,0,.54);">${res.duration}</span>`,
            title: `${res.dateParse}`,
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
    let group = await this.getGroupValue();
    try {
      let filter = null;
      try {filter = {...this.aonSigninParentEl._filter};} catch (error) {}
      let datos = await getTaskHolderTimeControl(filter);
      if(datos){
        sortBy(datos, 'start_date', 'asc').map(
          async (r) => {
            if(!this.TASK_HOLDER)  this.TASK_HOLDER = r.task_holder;
            const name = this.TASK_HOLDER.name;
            const newStatus = r.status.toLowerCase();
            const textStatus = await getStatus(newStatus);
            const numbDate =   this.getTimeNumber(group.value, r.start_date);
            const lettersHtml = `<div class="profile-letters ${ numbDate ? "font": ""} out">${group.name.substr(0,1)+numbDate}</div>`;
            const obj = {
              ...r,
              lettersHtml,
              textStatus: textStatus.name,
              status: newStatus,
              name: `${name}`,
              last_location: r.last_location,
              duration: timeHour(Number(r.time)),
            };
            data.push(obj);
          }
        );
      }
    } catch (e) {
      console.log(e);
    }
    this.aonSigninEl.stopLoader();
    this.paintName();
    return data;
  }

  async getGroupValue(){
    let groupEl = this.getElement('group');
    let gv = {value:"DAY", name:"DIA"};
    if(groupEl && groupEl.value){
      const groups = await getGroups(groupEl.value);
      if(groups) gv = groups;
    }
    return gv;
  }

  getTimeNumber(gv, date){
    const dt = new Date(date);
    let v = "";
    switch(gv){
      case "WEEK":
        v = dt.getWeekNumber();
      break;
      case "MONTH":
        v = dt.getMonth() + 1;
      break;
    }
    return v;
  }

  paintName(){
    if(this.TASK_HOLDER) {
      if(this.isMobile()) this.getElement(this.TOOLBAR).title = this.TASK_HOLDER.name;
      else this.aonSigninEl.addTitleToolSection(this.TASK_HOLDER.name);
    }
  }

  aonEvent({target}, data) {
    if("add_location" === target.textContent){
      this.aonSigninParentEl.showView("aonLocationAdd", data);
    } else {
      this.aonSigninParentEl.showView("aonEventDetailList", data);
    }
  }

  back(){
    this.aonSigninParentEl.showView("aonPresenceList", undefined, true);
  }
}
window.customElements.define("aon-event-list", AonEventList);
