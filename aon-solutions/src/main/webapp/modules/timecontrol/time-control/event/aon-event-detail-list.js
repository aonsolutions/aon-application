import { AonElement } from "../../../../components/AonElement.js";
import { isEmptyObject, removeEmpty, setValueName, sortBy} from "../../../../services/utils.js";
import { setAttributes} from "../../../../services/utilsComponents.js";
import { getStatus, getPeriod, getTimeControlDetail } from "../../../../services/service.js";
import { SigninSidenav, PRESENCE_FILTER, SIGNIN_VIEWS, iconAddLocation } from "../../signinEnums.js";
import { ToolbarType } from "../../../../models/enums.js";
import { dateCustomDayHour } from "../utils.js";
import { CONSTANT, CSS, EVENT, MSG, TAG } from "../../../../environments/environments.js";
import * as ACTION from '../../../actions.js';
import { AonFilter } from "../../../../components/aon-filter.js";
import { AonToolbar } from "../../../../components/aon-toolbar.js";
import { AonMobileList } from "../../../../components/aon-mobile-list.js";
import { AonTable } from "../../../../components/aon-table.js";
import { AonIconButton } from "../../../../components/aon-icon-button.js";
import { AonDateUtils } from "../../../utils/AonDateUtils.js";


export class AonEventDetailList extends AonElement {
  TABLE_ID;
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
    if (this.applicationEl) this.applicationEl.removeFloatOption();
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


  build() {

    this.paintView();
    
    this.buildToolbar();

    this.getTable();
    
    this.applicationParentEl.addEventListener("filterParent",()=> {
      this.getTable();
    });
  }

  paintView() {
    let aonFilter = new AonFilter();
    aonFilter.id = this.id+"Filter";
    aonFilter.title = MSG.FILTERS;
    this.appendChild(aonFilter);
    
    if (this.isMobile()) {
      let aonToolbar =  new AonToolbar();
      aonToolbar.id = this.TOOLBAR;
      aonToolbar.type = ToolbarType.SECONDARY;
      this.appendChild(aonToolbar);
    } 

    let aonTable = this.isMobile() ? new AonMobileList() : new AonTable();
    aonTable.id = this.TABLE_ID;
    let div = this.createElement(TAG.DIV);
    if(this.isMobile()) div.className = CSS.AON_MOBILE_SUB_CONTENT;
    div.appendChild(aonTable);
    this.appendChild(div);
  }

  buildToolbar() {
    this.applicationEl.removeToolbarOptions();
  
    if(this.isMobile()){
      let toolbarEl = this.getElement(this.TOOLBAR);
      toolbarEl.removeButtons();
      toolbarEl.addButton2(ACTION.BACK, () => this.back());
    }

    if(!this.applicationParentEl.isEmployee()){
      if(this.isMobile()){
        this.applicationEl.addFloatOption(SigninSidenav.ADD, () => this.aonEvent() );
      } else {
        this.applicationEl.addToolbarOption2(SigninSidenav.ADD, () => this.aonEvent());
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
    btnSearch.buildOptionsFilter(PRESENCE_FILTER);//INPUTS
    this.searchValueDefault();
  }

  async searchValueDefault(){

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
      try {filter = { ...this.applicationParentEl._filter, ...this.applicationParentEl.DATE_TMP };} catch (error) {}

      const datos = await getTimeControlDetail(filter);
      if (datos) {
        sortBy(datos, "date", "asc").map((resp) => {
          removeEmpty(resp);
          const newStatus = resp.status.toLowerCase();
          const status = getStatus(newStatus);
          const textStatus = status.name;
          const lettersHtml = `<div class="profile-letters ${newStatus}">${textStatus.substr(0,1)}</div>`;
    
          let nameLocation = "";
          if (resp.location && resp.location.name) {
            nameLocation = resp.location.name;
          } else if(!isEmptyObject(resp.coordinates)) {
            let aib = setAttributes(new AonIconButton(),{id: "iconLocation", noHover: "true", icon: iconAddLocation});
            nameLocation = aib.outerHTML;
          }

          data.push({
            ...resp,
            lettersHtml,
            textStatus,
            status: newStatus,
            nameLocation,
            dateParse: AonDateUtils.setDateTimestamp(resp.date),
          });
          
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

  aonEvent(el, data) {
    if (el && iconAddLocation === el.target.textContent) {
      this.applicationParentEl.showView(SIGNIN_VIEWS.AON_LOCATION_ADD, {coordinates:data.coordinates});
    } else {
      this.applicationParentEl.showView(SIGNIN_VIEWS.AON_EVENT_ADD, data);
    }
  }

  back() {
    this.applicationParentEl.showView(SIGNIN_VIEWS.AON_EVENT_LIST);
  }

}
window.customElements.define("aon-event-detail-list", AonEventDetailList);
