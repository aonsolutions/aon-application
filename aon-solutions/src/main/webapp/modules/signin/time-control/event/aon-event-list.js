import { AonElement } from "../../../../components/AonElement.js";
import { setFullDate, setValueName, sortBy, setDateTpDay, formatDateOrigin, formatDate, isEmptyObject } from "../../../../services/utils.js";
import {
  getGroups,
  getPeriod,
  getStatus,
  getTaskHolderTimeControl,
} from "../../../../services/service.js";
import { ToolbarType } from "../../../../models/enums.js";
import { EVENT_LIST_FILTER, SigninSidenav, SIGNIN_VIEWS } from "../../signinEnums.js";
import { firstLetters, timeHour} from "../utils.js";
import "../../../../components/aon-table.js";
import "../../../../components/aon-mobile-list.js";
import "../../../../components/aon-filter.js";

import * as ACTION from '../../../actions.js';
import { MSG } from "../../../../environments/environments.js";

export class AonEventList extends AonElement {
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
    if ("filter" === name) this.getTable();
  }

  constructor() {
    super();
    this.id = this.id || SIGNIN_VIEWS.AON_EVENT_LIST;
    this.TABLE_ID = this.id + "Table";
    this.TOOLBAR = this.id + "Toolbar";
    this.applicationEl = this.getApplication();
    this.applicationParentEl = this.getApplicationParent();
    this.applicationParentEl.periodSideNavDisplay(true);
  }

  connectedCallback() {
    this.applicationEl.addToolbarTitle("Resumen");
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
    this.applicationEl.startLoader();
    if (this.isMobile()) {
      await this.getTableMobile();
    } else {
      await this.getTableDesk();
    }
    this.applicationEl.stopLoader();
    this.applicationParentEl.changeFilter();
  }


  buildToolbarDesk() {
    this.applicationEl.removeToolbarOptions();
    const filterEl =  this.getElement(`${this.id}Filter`);
    this.applicationEl.addToolbarOption2(SigninSidenav.FILTER, (e) => filterEl.openFilter());
  }
  
  buildToolbarMobile(){
    this.applicationEl.removeToolbarOptions();
    let toolbarEl = this.getElement(this.TOOLBAR);
    const filterEl =  this.getElement(`${this.id}Filter`);
    toolbarEl.removeButtons();
    if(!this.applicationParentEl.isEmployee()){
		  toolbarEl.addButton2(ACTION.BACK, () => this.back());
    }
    this.applicationEl.addToolbarOption2(SigninSidenav.FILTER, (e) => filterEl.openFilter());
  }

  async buildFilter() {
    let aonFilter = this.getElement(`${this.id}Filter`);
    aonFilter.setInputs(EVENT_LIST_FILTER);
    aonFilter.addEventListener("applyFilter", ({detail}) => {
      if(detail) this.applicationParentEl.setDataFilter(detail);
    });

    let groupEl = this.getElement('group');
    groupEl.options = JSON.stringify(await getGroups());

    let periodEl = this.getElement("period");
    periodEl.options = JSON.stringify(getPeriod());

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
      if(!this.applicationParentEl.isEmployee()){
        iconBack = "arrow_back";
      }
      aonTable.addColumnIcon(iconBack, "string", "lettersHtml", "6%", ()=>this.back());
      aonTable.addColumn(MSG.DATE, "date", "dateParse", "40%");
      aonTable.addColumn(MSG.DURATION, "", "duration", "30%");
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
        let idxTotal = 0;
        let timeTotal = 0;
        resp.map((res, idx) => {
          const group = res.group;
          if(group && "DAY"!==group) {
            res.dateParse = formatDate(res.start_date)+" - "+ formatDate(res.end_date);
          } else {
            res.dateParse = firstLetters(setDateTpDay(res.start_date));
          }
          if(res.status && res.status.indexOf("in")>=0 && res.in_date){
            res.time = (new Date().getTime() - res.in_date)  + res.time;
            res.durationParse = timeHour(Number(res.time));
          }
          const options = {
            paddingTopTitle: "5px",
            iconHtmlCustom: `${res.lettersHtml} <span style="padding-top: 5px;float: right;color: rgba(0,0,0,.54);">${res.durationParse}</span>`,
            title: `${res.dateParse}`,
          };
          aonTable.addLi(options, idx, (el) => this.aonEvent(el, res));
          idxTotal = idx;
          timeTotal = timeTotal + res.time;
        });
        //---------TIME TOTAL
        if(timeTotal>0) this.addTotalMobile(aonTable, idxTotal+1, timeTotal);
      } catch (e) {
        console.log(e);
      }
    }
  }

  addTotalMobile(aonTable, idxTotal, timeTotal){
      const filter = !isEmptyObject(this.applicationParentEl._filter) ? this.applicationParentEl._filter : null;
      if(filter && "DAY"===filter.group && "this_week"===filter.period ){
        aonTable.addLi({
          iconHtmlCustom: `<span style="padding-top: 5px;float: right;color: rgba(0,0,0,.54);">${timeHour(Number(timeTotal))}</span>`,
          title: `<span style="font-weight: 500;padding-right: 10px;padding-top: 5px;float: right;">TOTAL DE HORAS</span>`,
        }, idxTotal, (el) =>{});
      }
  }

  async getData() {
    let data = [];
    let group = await this.getGroupValue();
    try {
      let filter = null;
      try {filter = {...this.applicationParentEl._filter};} catch (error) {}
      let datos = await getTaskHolderTimeControl(filter);
      if(datos){
        datos = datos.filter(dt => new Date(dt.start_date) <= new Date());
        await sortBy(datos, 'start_date', 'asc').map(
          async (r) => {
            const newStatus = r.status.toLowerCase();
            const textStatus = await getStatus(newStatus);
            const numbDate =   this.getTimeNumber(group.value, r.start_date);
            const lettersHtml = `<div class="profile-letters ${ numbDate ? "font": ""} out">${group.name.substr(0,1)+numbDate}</div>`;
            const obj = {
              ...r,
              lettersHtml,
              textStatus: textStatus.name,
              status: newStatus,
              last_location: r.last_location,
              durationParse: timeHour(Number(r.time)),
            };
            data.push(obj);
          }
        );
      }
    } catch (e) {
      console.log(e);
    }
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

  aonEvent({target}, data) {
    let newData = data;
    const parent = this.applicationParentEl;
    if(newData.start_date) parent.DATE_TMP = {...parent.DATE_TMP, startDate:formatDateOrigin(newData.start_date)};
    if(newData.end_date) parent.DATE_TMP = {...parent.DATE_TMP, endDate:formatDateOrigin(newData.end_date)};
    parent.showView(SIGNIN_VIEWS.AON_EVENT_DETAIL_LIST, newData);
  }

  back(){
    this.applicationParentEl.showView(SIGNIN_VIEWS.AON_PRESENCE_LIST, undefined, true);
  }
}
window.customElements.define("aon-event-list", AonEventList);
