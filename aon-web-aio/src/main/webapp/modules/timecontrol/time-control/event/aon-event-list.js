import { AonElement } from "../../../../components/AonElement.js";
import { setValueName, sortBy, isEmptyObject } from "../../../../services/utils.js";
import { setAttributes } from "../../../../services/utilsComponents.js";
import { getPeriod, getStatus, getTaskHolderTimeControl } from "../../../../services/service.js";
import { ToolbarType } from "../../../../models/enums.js";
import { EVENT_LIST_FILTER, SigninSidenav, SIGNIN_VIEWS } from "../../signinEnums.js";
import { firstLetters, timeHour} from "../utils.js";
import { CONSTANT, CSS, EVENT, MSG, TAG } from "../../../../environments/environments.js";
import * as ACTION from '../../../actions.js';
import { AonMobileList } from "../../../../components/aon-mobile-list.js";
import { AonTable } from "../../../../components/aon-table.js";
import { AonToolbar } from "../../../../components/aon-toolbar.js";
import { AonIconButton } from "../../../../components/aon-icon-button.js";
import { AonDateUtils } from "../../../utils/AonDateUtils.js";

export class AonEventList extends AonElement {
  TABLE_ID;
  FN_FILTER;
  static get observedAttributes() {
    return [];
  }

  get id() {
    return this.getAttribute(CONSTANT.ID);
  }

  set id(id) {
    this.setAttribute(CONSTANT.ID, id);
  }

  attributeChangedCallback(name, oldValue, newValue) {}

  constructor() {
    super();
  }

  connectedCallback() {
    this.initialize();
    this.build();
  }

  disconnectedCallback() {
    this.applicationParentEl.removeEventListener("filterParent", this.FN_FILTER);
  }

  initialize(){
    this.id = this.id || SIGNIN_VIEWS.AON_EVENT_LIST;
    this.TABLE_ID = this.id + "Table";
    this.TOOLBAR = this.id + "Toolbar";

    this.applicationEl = this.getApplication();
    this.applicationParentEl = this.getApplicationParent();

    this.applicationEl.addToolbarTitle("Resumen");

    this.FN_FILTER = ()=> {
      this.getTable();
    }
  }

   build() {
    this.paintView();
    this.buildToolbar();
    this.getTable();

    this.applicationParentEl.addEventListener("filterParent", this.FN_FILTER);
  }


  paintView() {
    if (this.isMobile()) {
      let aonToolbar =  new AonToolbar();
      aonToolbar.id = this.TOOLBAR;
      aonToolbar.type = ToolbarType.SECONDARY;
      this.appendChild(aonToolbar);
    } 
    
    let aonTable = this.isMobile() ? new AonMobileList() : new AonTable();
    aonTable.id = this.TABLE_ID;
    
    let div = this.createElement(TAG.DIV);

    if(this.isMobile()) {
      div.className = CSS.AON_MOBILE_SUB_CONTENT;
    }
  
    div.appendChild(aonTable);
    this.appendChild(div);
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
  
  buildToolbar(){
    this.applicationEl.removeToolbarOptions();
    if(this.isMobile()){
      let toolbarEl = this.getElement(this.TOOLBAR);
      toolbarEl.removeButtons();
      if(!this.applicationParentEl.isEmployee()){
        toolbarEl.addButton2(ACTION.BACK, () => this.back());
      }
    }

    if(!this.applicationParentEl.isEmployee()){
      if(this.isMobile()){
        this.applicationEl.addFloatOption(SigninSidenav.ADD, () =>this.aonEventAdd());
      } else {
        this.applicationEl.addToolbarOption2(SigninSidenav.ADD, () =>this.aonEventAdd());
      }
    }

    this.buildToolbarSearch();
  }

  buildToolbarSearch(){
    let btnSearch = this.applicationEl.addSearchOption();
    btnSearch.disabled = true;
    const searchValueFn = ({detail})=>{
      if(detail) this.applicationParentEl.setDataFilter(detail);
    }
    btnSearch.addEventListener(EVENT.SEARCH_NEW, searchValueFn);
    btnSearch.buildOptionsFilter(EVENT_LIST_FILTER);//INPUTS
    this.searchValueDefault();
  }

  async searchValueDefault(){

    let groupEl = this.getElement('group');
    groupEl.options = JSON.stringify(this.getGroups());

    let periodEl = this.getElement("period");
    periodEl.options = JSON.stringify(getPeriod());
    periodEl.addEventListener(EVENT.CHANGE, ({detail}) => {
      if(detail){
        const {startDate, endDate} = detail;
        setValueName('startDate', startDate);
        setValueName('endDate', endDate);
      }
    });

    this.getElement("startDate").addEventListener(EVENT.CHANGE,()=>periodEl.value = "personalized");
    this.getElement("endDate").addEventListener(EVENT.CHANGE,()=>periodEl.value = "personalized");
  }


  async getTableDesk() {
    const aonTable = this.getElement(this.TABLE_ID);
    if (aonTable) {
      aonTable.removeColumns();
      const iconBack = !this.applicationParentEl.isEmployee() ? "arrow_back" : "";
      aonTable.addColumnIcon({title:MSG.BACK, name:iconBack, type:"string", id:"lettersHtml", width:"6%"}, ()=>this.back());
      aonTable.addColumn(MSG.DATE, "date", "dateParse", "30%");
      aonTable.addColumn(MSG.DURATION, "string", "durationParse", "10%");
      aonTable.addColumn(MSG.LAST_LOCATION, "string", "nameLocation", "20%");
      try {
        const resp = await this.getData();
        aonTable.removeRows();
        resp.map((res) => {
          let tr = aonTable.addRow(
            {
              ...res,
              status: res.textStatus
            },
            (el) => this.aonEvent(el, res)
          );
          tr.id = "aonTimeControlRow";
        });
      } catch (e) {
        console.log("error",e);
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
    try {
        const group = await this.getGroupValue();
        const isMobile = this.isMobile();
        let filter = null;
        try {filter = {...this.applicationParentEl._filter};} catch (error) {}
        let datos = await getTaskHolderTimeControl(filter);
        if(datos){
          datos = datos.filter(dt => new Date(dt.start_date) <= new Date());
          sortBy(datos, 'start_date', 'asc').map((r) => {
              const newStatus = r.status.toLowerCase();
              const textStatus = getStatus(newStatus);
              const numbDate   = this.getTimeNumber(group.value, r.start_date);
              const lettersHtml = `<div id = "aonTimeControlTableDiv" class="profile-letters ${ numbDate ? "font": ""} ${newStatus}">${group.name.substr(0,1)+numbDate}</div>`;

              const nameLocation = r.last_location && r.last_location.name ? r.last_location.name : "";

              let dateParse = r.dateParse = firstLetters(AonDateUtils.setFullDate(r.start_date));
              if(isMobile){
                const groupV  = r.group;
                dateParse =  groupV && groupV.indexOf("DAY")>=0 ? AonDateUtils.formatDate(r.start_date)+" - "+ AonDateUtils.formatDate(r.end_date) : firstLetters(AonDateUtils.setDateTpDay(r.start_date))
              }

              data.push({
                ...r,
                lettersHtml,
                dateParse,
                nameLocation,
                textStatus: textStatus.name,
                status: newStatus,
                durationParse: timeHour(Number(r.time))
              });
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
      const groups = this.getGroups(groupEl.value);
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

  getGroups(data) {
    let jsonValues = [
      {
        name: "Día",
        value: "DAY",
      },
      {
        name: "Semana",
        value: "WEEK",
      },
      {
        name: MSG.MONTH,
        value: "MONTH",
      },
      {
        name: MSG.YEAR,
        value: "YEAR",
      },
    ];
  
    if (data) jsonValues = jsonValues.find((f) => f.value.indexOf(data) >= 0);
    return jsonValues;
  };

  async paintName(){
    const taskHolder = this.applicationParentEl.TASK_HOLDER;
    if(taskHolder) {
      if(this.isMobile()) await this.paintNameMobile(taskHolder);
      else this.applicationEl.addTitleToolSection(taskHolder.name);
    }
  }

  async paintNameMobile(taskHolder){
    let div = this.createElement(TAG.DIV);
    let divName = this.createElement(TAG.DIV);
    divName.style.whiteSpace = "nowrap";
    divName.style.textOverflow = "ellipsis";
    divName.style.overflow = "hidden";
    divName.style.marginRight = "14px";
    divName.innerText = taskHolder.name;
    div.appendChild(divName);

    if(!this.applicationParentEl.isEmployee()) {
      const auth = await this.applicationParentEl.getAuth({task_holder: taskHolder.id});
      if(auth && auth.phone){
        let color = "black";
        if(taskHolder.status && "in"===taskHolder.status) color = "green";
        let aEl = this.createElement(TAG.A);
        aEl.href = `tel:+34${auth.phone}`;
        aEl.style.position = "absolute";
        aEl.style.top = "-1px";
        aEl.style.right = "4px";
        let aib = setAttributes(new AonIconButton(),{
          id:"iconPhone",
          icon:"phone_in_talk",
          noHover: "true",
          color
        })
        aEl.appendChild(aib);
        div.appendChild(aEl);   
      }
    }
    this.getElement(this.TOOLBAR).title = div.outerHTML;
  }

  aonEvent({}, data) {
    let newData = data;
    const parent = this.applicationParentEl;
    if(newData.start_date) parent.DATE_TMP = {...parent.DATE_TMP, startDate:AonDateUtils.formatDateOrigin(newData.start_date)};
    if(newData.end_date) parent.DATE_TMP = {...parent.DATE_TMP, endDate:AonDateUtils.formatDateOrigin(newData.end_date)};
    parent.showView(SIGNIN_VIEWS.AON_EVENT_DETAIL_LIST, newData);
  }

  aonEventAdd(){
    this.applicationParentEl.showView(SIGNIN_VIEWS.AON_EVENT_ADD, {date: new Date()});
  }  

  back(){
    this.applicationParentEl.showView(SIGNIN_VIEWS.AON_PRESENCE_LIST);
  }
}
window.customElements.define("aon-event-list", AonEventList);
