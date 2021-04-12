import { AonElement } from "../../components/AonElement.js";

import {AonPresenceList} from "./time-control/aon-presence-list.js";
import { getAuth, getDomainUserRoles, getPeriod, getTaskHolders, getWeekDayObj } from "../../services/service.js";
import { formatDateOrigin, isEmptyObject, setValueName } from "../../services/utils.js";
import { AonLocationAdd } from "./time-control/location/aon-location-add.js";
import { AonLocationList } from "./time-control/location/aon-location-list.js";
import { SigninSidenav, SIGNIN_VIEWS } from "./signinEnums.js";
import { DomainUserRoles } from "../../models/DomainUserRoles.js";
import { AonEventList } from "./time-control/event/aon-event-list.js";
import { AonEventDetailList } from "./time-control/event/aon-event-detail-list.js";
import { AonEventAdd } from "./time-control/event/aon-event-add.js";
import "../../components/aon-application.js";


export class AonSignin extends AonElement {
  AON_SIGNIN;
  TASK_HOLDER;
  DATE_TMP;
  AUTHS;
  _filter;
  dur;
  constructor() {
    super();
  }

  disconnectedCallback() {}
  connectedCallback() {
    this.initialize();
    getDomainUserRoles({reload:true}).then(r=>{
      this.dur = new DomainUserRoles(r);
      this.build();
    })

  }

  initialize(){
    this.DATE_TMP = null;
    this.AON_SIGNIN = SIGNIN_VIEWS.AON_SIGNIN;
    this.AUTHS=[];
  }

  getDur() {
    return this.dur;
  }

  async build() {
    this.filterInit();
    this.paintView();
    this.buildToolbar();
    this.showView(SIGNIN_VIEWS.AON_PRESENCE_LIST);
  }

  filterInit(){
    if(this.isEmployee()){
      const weekDayObj = getWeekDayObj();
      this._filter = { 
        period: "this_week",
        group:"DAY",
        startDate: formatDateOrigin( new Date().setDate(weekDayObj.dayWeekFirst) ),
        endDate: formatDateOrigin( new Date().setDate(weekDayObj.dayWeekLast) )
      };
    } else {
      this._filter = { 
        period: "today",
        group:"DAY",
        startDate: formatDateOrigin(new Date()),
        endDate: formatDateOrigin(new Date())
      };
    }
  }
  paintView() {
    this.innerHTML = `
			<aon-application id="${this.AON_SIGNIN}" title="Control Horario"></aon-application>
    `;
    this.applicationEl = this.getApplication();
  }
  
  buildToolbar() {
    const options = [
      {
        ...SigninSidenav.PRESENCE,
        fn: () => this.showView( SIGNIN_VIEWS.AON_PRESENCE_LIST)
      },
      {
        ...SigninSidenav.LOCATION,
        fn: () =>this.showView(SIGNIN_VIEWS.AON_LOCATION_LIST)
      },
    ];

    if( this.isEmployee()) {
      delete options[1];
    } 

    this.applicationEl.addSidenavOptions("Control horario", options);

    const options2 = [
      {
        ...SigninSidenav.PERIOD.TODAY,
        fn: () => this.setDataFilter({period:"today"})
      },
      {
        ...SigninSidenav.PERIOD.YESTERDAY,
        fn: () =>this.setDataFilter({period:"yesterday"})
      },
      {
        ...SigninSidenav.PERIOD.THIS_WEEK,
        fn: () =>this.setDataFilter({period:"this_week"})
      },
      {
        ...SigninSidenav.PERIOD.THIS_MONTH,
        fn: (e) => this.setDataFilter({period:"this_month"})
      }
    ];
    
    this.applicationEl.addSidenavOptions("Período", options2);

  }

  periodSideNavDisplay(b){
    let eleSideNav = this.getElement(`${this.applicationEl.SIDENAV}Período`);
    if(eleSideNav) eleSideNav.style.display = b ? "block": "none";
  }

  async setDataFilter(data){
    try {
      this.DATE_TMP =  null;
      if(data && data.period){
        data = {...data, ...getPeriod(data.period)};
      } 
      this._filter = {...this._filter, ...data};
      this.applicationEl.getChild().filter = true;
    } catch (error) {}
  }

  changeFilter(){
    try {
      const filterParent = this._filter;
      for(const obj in filterParent){
        setValueName(obj, filterParent[obj]);
      }
    } catch (error) {}
  }

  showView(view, data, filter = undefined){
    return new Promise(async(resolve)=>{
      let aonView = undefined;

      if(this.isEmployee()){
        if(SIGNIN_VIEWS.AON_PRESENCE_LIST === view) {
          view = SIGNIN_VIEWS.AON_EVENT_LIST;
        } else if( SIGNIN_VIEWS.AON_LOCATION_ADD === view){ resolve(true);return; }
      }

      if(!this.getElement(view)){
        switch(view){
          case SIGNIN_VIEWS.AON_PRESENCE_LIST:
              aonView = new AonPresenceList();
            break;
          case SIGNIN_VIEWS.AON_EVENT_LIST:
            aonView = new AonEventList();
            if(data && data.taskHolderId){
              this.TASK_HOLDER = {id: data.taskHolderId, name: data.name};
            } else {
              this.TASK_HOLDER = await this.getTaskHolder().catch(e=>null);
            }
            if(data && data.status && !isEmptyObject(this.TASK_HOLDER)) {this.TASK_HOLDER.status = data.status;}
            this._filter.taskHolderId = this.TASK_HOLDER.id;
            break;
          case SIGNIN_VIEWS.AON_EVENT_DETAIL_LIST:
              aonView = new AonEventDetailList();
              if(data){
                // const startDate = data.startDate;
                // let endDate = data.endDate || startDate;
                // aonView.DATE_TASK = {startDate, endDate};
              }
            break;
          case SIGNIN_VIEWS.AON_EVENT_ADD:
            aonView = new AonEventAdd();
            if (data) { aonView.data = data;} 
            else if (this.TASK_HOLDER) {
              aonView.data = {task_holder: this.TASK_HOLDER};
            }
            break;
          case SIGNIN_VIEWS.AON_LOCATION_LIST:
              aonView = new AonLocationList();
              this.periodSideNavDisplay(false);
            break;
          case SIGNIN_VIEWS.AON_LOCATION_ADD:
            aonView = new AonLocationAdd();
            if(data){
              if(data.coordinates && data.coordinates.latitude && data.coordinates.longitude){
                data = {...data, latitude:data.coordinates.latitude, longitude: data.coordinates.longitude}
                aonView.data = {...data, latitude:data.coordinates.latitude, longitude: data.coordinates.longitude};
              }
              if(data.add){ aonView.add = data.add;}
            } 
            break;
        }
        aonView.id = view;
        if(filter) aonView.filter = filter;
        this.applicationEl.setContent(aonView);
      }
      
      resolve(true);
    });
  }

  async getAuth({task_holder}){
    let auth = this.AUTHS.find(d => d.task_holder === task_holder);
    if(!auth){
      auth = await getAuth({task_holder}).catch(e=>null);
      this.AUTHS.push({...auth, task_holder});
    } 
    return auth;
  }
  
  async getTaskHolder(){
    if(isEmptyObject(this.TASK_HOLDER)){
      const domainId = parseInt(localStorage.getItem("aon_domain_id"));
      const result = await getTaskHolders({reload:true});
      this.TASK_HOLDER = result.find(({domain_id})=> domain_id === domainId);
    }
    return this.TASK_HOLDER;
  }
  
  
  isEmployee(){
    return !this.getDur().isTimecontrolManager() && !this.getDur().isTimecontrolPortal();
  }

}
window.customElements.define("aon-signin", AonSignin);
