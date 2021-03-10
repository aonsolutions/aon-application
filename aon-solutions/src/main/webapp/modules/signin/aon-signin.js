import { AonElement } from "../../components/AonElement.js";

import {AonPresenceList} from "./time-control/aon-presence-list.js";
import { getDomainUserRoles, getPeriod, getTaskHolders } from "../../services/service.js";
import { formatDateOrigin, isEmptyObject, setValueName } from "../../services/utils.js";
import { AonLocationAdd } from "./time-control/location/aon-location-add.js";
import { AonLocationList } from "./time-control/location/aon-location-list.js";
import { SigninSidenav } from "./signinEnums.js";
import { DomainUserRoles } from "../../models/DomainUserRoles.js";
import { AonEventList } from "./time-control/event/aon-event-list.js";
import { AonEventDetailList } from "./time-control/event/aon-event-detail-list.js";
import { AonEventAdd } from "./time-control/event/aon-event-add.js";
import "../../components/aon-application.js";


export class AonSignin extends AonElement {
  AON_SIGNIN;
  TASK_HOLDER;
  _filter;
  _roles;
  constructor() {
    super();
  }

  disconnectedCallback() {}
  connectedCallback() {
    this.initialize();
    getDomainUserRoles({reload:true}).then(r=>{
      this._roles = new DomainUserRoles(r);
      this.build();
    })

  }

  initialize(){
    this.AON_SIGNIN = "aonSignin";
    this._filter = { 
      period: "today",
      startDate: formatDateOrigin(new Date()),
      endDate: formatDateOrigin(new Date())
    };
  }

  async build() {
    this.paintView();
    this.buildToolbar();
    this.showView("aonPresenceList");
  }

  paintView() {
    this.innerHTML = `
			<aon-application id="${this.AON_SIGNIN}" title="Control Horario"></aon-application>
    `;
    this.aonSigninEl = this.getApplication();
  }
  
  buildToolbar() {
    const options = [
      {
        ...SigninSidenav.PRESENCE,
        fn: () => this.showView("aonPresenceList")
      },
      {
        ...SigninSidenav.LOCATION,
        fn: () =>{
          this.aonSigninEl.setContent(new AonLocationList());
          this.periodSideNavDisplay(false);
        }

      },
    ];

    if( this.isEmployee()) {
      delete options[1];
    } 

    this.aonSigninEl.addSidenavOptions("Control horario", options);

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
    
    this.aonSigninEl.addSidenavOptions("Período", options2);

  }

  periodSideNavDisplay(b){
    let eleSideNav = this.getElement(`${this.aonSigninEl.SIDENAV}Período`);
    if(eleSideNav) eleSideNav.style.display = b ? "block": "none";
  }

  async setDataFilter(data){
    try {
      if(data && data.period){
        data = {...data, ...await getPeriod(data.period)};
      } 
      this._filter = {...this._filter, ...data};
      this.aonSigninEl.getChild().filter = true;
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
      if("aonPresenceList" === view && this.isEmployee()) {
        const taskHolder = await this.getTaskHolder().catch(e=>null);
        if(taskHolder){
          data = {taskHolderId:taskHolder.id};
        }
        view = "aonEventList";
      } else if( "aonLocationAdd" === view && this.isEmployee()){
       resolve(true);
       return;
      }
  
      if(!this.getElement(view)){
        switch(view){
          case "aonPresenceList":
              aonView = new AonPresenceList();
          break;
          case "aonEventList":
            aonView = new AonEventList();
            if(data && data.taskHolderId) this._filter.taskHolderId = data.taskHolderId;
          break;
          case "aonLocationAdd":
              aonView = new AonLocationAdd();
              if(data){
                if(data.coordinates && data.coordinates.latitude && data.coordinates.longitude){
                  data = {...data, latitude:data.coordinates.latitude, longitude: data.coordinates.longitude}
                  aonView.data = {...data, latitude:data.coordinates.latitude, longitude: data.coordinates.longitude};
                }
                if(data.add){
                  aonView.add = data.add;
                }
              } 
          break;
          case "aonEventDetailList":
              aonView = new AonEventDetailList();
              if(data){
                const startDate = formatDateOrigin(data.start_date);
                aonView.DATE_TASK = {startDate, endDate:startDate};
              }
          break;
          case "aonEventAdd":
            aonView = new AonEventAdd();
            if (data) {
              aonView.data = data;
            }
          break;
        }
        aonView.id = view;
        if(filter) aonView.filter = filter;
        this.aonSigninEl.setContent(aonView);
      }
      
      resolve(true);
    });
  }

  async getTaskHolder(){
    if(isEmptyObject(this.TASK_HOLDER)){
      const domain_id = parseInt(localStorage.getItem("aon_domain_id"));
      const result = await getTaskHolders({reload:true});
      this.TASK_HOLDER = result.find(r=> r.domain_id === domain_id);
    }
    return this.TASK_HOLDER;
  }
  
  isEmployee(){
    return !this._roles.isTimecontrolManager() && !this._roles.isTimecontrolPortal();
  }

}
window.customElements.define("aon-signin", AonSignin);
