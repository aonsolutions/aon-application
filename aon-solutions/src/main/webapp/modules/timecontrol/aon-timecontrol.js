import { AonElement } from "../../components/AonElement.js";

import {AonPresenceList} from "./time-control/aon-presence-list.js";
import { domainId, getAuthNoCache, getDomainUserRoles, getPeriod, getTaskHoldersUser, getTastHolders } from "../../services/service.js";
import {  isEmptyObject, setValueName } from "../../services/utils.js";
import { AonLocationAdd } from "./time-control/location/aon-location-add.js";
import { AonLocationList } from "./time-control/location/aon-location-list.js";
import { SigninSidenav, SIGNIN_VIEWS } from "./signinEnums.js";
import { DomainUserRoles } from "../../models/DomainUserRoles.js";
import { AonEventList } from "./time-control/event/aon-event-list.js";
import { AonEventDetailList } from "./time-control/event/aon-event-detail-list.js";
import { AonEventAdd } from "./time-control/event/aon-event-add.js";
import { AonApplication } from "../../components/aon-application.js";
import { MSG } from "../../environments/environments.js";
import Apps from "../../services/app.js";

import '../../css/aon.css';

export class AonTimecontrol extends AonElement {
  AON_SIGNIN;
  TASK_HOLDER;
  TASK_HOLDER_ENTERPRISE;
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
    this.TASK_HOLDER_ENTERPRISE = [];
  }

  getDur() {
    return this.dur;
  }

  build() {
    this.filterInit();
    this.paintView();
    this.buildToolbar();
    this.showView(SIGNIN_VIEWS.AON_PRESENCE_LIST);
  }

  filterInit(){
    const periodEnums = SigninSidenav.PERIOD;
    const period = getPeriod(this.isEmployee() ? periodEnums.THIS_WEEK.id : periodEnums.TODAY.id);
    this._filter = { 
      group:"DAY",
      period: period.value,
      startDate: period.startDate,
      endDate: period.endDate,
      active:true
    }
  }
  paintView() {
    this.createApplication(this.AON_SIGNIN, MSG.TIMECONTROL, new AonApplication());
    this.applicationEl = this.getApplication();
  }
  
  buildToolbar() {
    if(this.isMobile()){
      this.applicationEl.addMobileSidenavHeader(Apps.TIMECONTROL);
    }
    const options = [
      {
        ...SigninSidenav.PRESENCE,
        fn: () => this.showView(SIGNIN_VIEWS.AON_PRESENCE_LIST)
      },
      {
        ...SigninSidenav.LOCATION,
        fn: () =>this.showView(SIGNIN_VIEWS.AON_LOCATION_LIST)
      }
    ];

    if( this.isEmployee()) {
      delete options[1];
    } 

    this.applicationEl.addSidenavOptions(MSG.TIMECONTROL, options);

    const {TODAY, YESTERDAY, THIS_WEEK, LAST_WEEK, THIS_MONTH}  = SigninSidenav.PERIOD;
    const options2 = [
      {
        ...TODAY,
        fn: () => this.setDataFilter({period:TODAY.id})
      },
      {
        ...YESTERDAY,
        fn: () =>this.setDataFilter({period:YESTERDAY.id})
      },
      {
        ...THIS_WEEK,
        fn: () =>this.setDataFilter({period:THIS_WEEK.id})
      },
      {
        ...LAST_WEEK,
        fn: () =>this.setDataFilter({period:LAST_WEEK.id})
      },
      {
        ...THIS_MONTH,
        fn: () => this.setDataFilter({period:THIS_MONTH.id})
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
      this.dispatchEvent(new CustomEvent("filterParent",{filter:this._filter}));
    } catch (error) {
      console.log(error);
    }
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

      if(data && data.reload){
        this.TASK_HOLDER = null;
      }

      // if(!this.getElement(view)){
        switch(view){
          case SIGNIN_VIEWS.AON_PRESENCE_LIST:
              aonView = new AonPresenceList();
            break;
          case SIGNIN_VIEWS.AON_EVENT_LIST:
            aonView = new AonEventList();
            if(data && data.taskHolderId){
              this.TASK_HOLDER = {id: data.taskHolderId, name: data.name};
            } else {
              this.TASK_HOLDER = await this.getTaskHolder().catch(()=>null);
            }
            if(data && data.status && !isEmptyObject(this.TASK_HOLDER)) {this.TASK_HOLDER.status = data.status;}
            this._filter.taskHolderId = this.TASK_HOLDER.id;
            break;
          case SIGNIN_VIEWS.AON_EVENT_DETAIL_LIST:
              aonView = new AonEventDetailList();
            break;
          case SIGNIN_VIEWS.AON_EVENT_ADD:
            aonView = new AonEventAdd();
            if (data) { 
              aonView.data = data;
            } else if (this.TASK_HOLDER) {
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
        if(aonView){
          aonView.id = view;
          if(filter) aonView.filter = filter;
          this.applicationEl.setContent(aonView);
        }
      // }
      
      resolve(true);
    });
  }

  async getAuth({task_holder}){
    let auth = this.AUTHS.find(d => d.task_holder === task_holder);
    if(!auth){
      const resp = await getAuthNoCache({task_holder, reload:true}).catch(()=>null);
      auth = {...resp, task_holder}
      this.AUTHS.push(auth);
    } 
    return auth;
  }
  
  async getTaskHolder(){
    if(isEmptyObject(this.TASK_HOLDER)){
      const result = await getTaskHoldersUser({reload:true});
      this.TASK_HOLDER = result.find(({domain_id})=> domain_id === parseInt(domainId()));
    }
    return this.TASK_HOLDER;
  }
  
  async getTaskHoldersEnterprise(){
		if(this.TASK_HOLDER_ENTERPRISE.length<=0){
			const ths = await getTastHolders();
			this.TASK_HOLDER_ENTERPRISE = ths.map( c=> ({...c, value: c.id})  );
		}
		return this.TASK_HOLDER_ENTERPRISE;
	}
  
  isEmployee(){
    return !this.getDur().isTimecontrolManager() && !this.getDur().isTimecontrolPortal();
  }

}
window.customElements.define("aon-timecontrol", AonTimecontrol);
