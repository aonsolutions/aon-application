import { AonElement } from "../../components/AonElement.js";

import { AonPresenceList} from "./time-control/aon-presence-list.js";
import { getAuthNoCache, getDomainUserRoles, getPeriod, getTaskHoldersUser, getTastHolders, getTimeControl } from "../../services/service.js";
import { isEmptyObject, setValueName } from "../../services/utils.js";
import { AonLocationAdd } from "./time-control/location/aon-location-add.js";
import { AonLocationList } from "./time-control/location/aon-location-list.js";
import { SigninSidenav, SIGNIN_VIEWS } from "./signinEnums.js";
import { DomainUserRoles } from "../../models/DomainUserRoles.js";
import { AonEventList } from "./time-control/event/aon-event-list.js";
import { AonEventDetailList } from "./time-control/event/aon-event-detail-list.js";
import { AonEventAdd } from "./time-control/event/aon-event-add.js";
import { AonApplication } from "../../components/aon-application.js";
import { MSG,TAG,CSS } from "../../environments/environments.js";
import Apps from "../../services/app.js";
import * as LS from '../../services/localStorageService.js';
import * as OPTIONS from './TimecontrolOptions.js';
import 'aoncss';
import { AonSign } from "./aon-sign.js";
import { AonStatistics } from "./time-control/statistics/aon-statistics.js";
import { getPosition } from "../../services/maps.js";

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
    if(this.isMobile())
      this.showView(SIGNIN_VIEWS.AON_STATISTICS);
    else
      this.showView(SIGNIN_VIEWS.AON_PRESENCE_LIST);
      
	// Set selected sidebar  
    let aonSigninSidenavpresence = this.getElement('aonSigninSidenavpresence');
    aonSigninSidenavpresence.classList.add('aonAppMenuSidenavListSelected');
    
    let aonSigninSidenavtoday = this.getElement('aonSigninSidenavtoday');
    aonSigninSidenavtoday.classList.add('aonAppMenuSidenavListSelected');
    
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

    let data = OPTIONS.TIMECONTROL;
    if(this.isEmployee()) data.options = [OPTIONS.PRESENCE];
    
    this.applicationEl.addSidenavOptions3(data);

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
    
    let data2 = {
			id: "Periodo",
			title: "Periodo",
      name: "Periodo",
      app: Apps.TIMECONTROL,
      options: options2
		}
    
    this.applicationEl.addSidenavOptions3(data2);

    if(this.getDur().isTimecontrol() && LS.isNewTheme() && !this.isMobile()) {
			getTimeControl().then(r => {
		    let data3 = {
          id: "signing",
          title: MSG.SIGNING.toUpperCase(),
          name: MSG.SIGNING.toUpperCase(),
          app: Apps.TIMECONTROL
        }
    
        let aonSign = new AonSign();
        this.applicationEl.addSidenavWidget2(data3, aonSign);
				aonSign.buildSignin(r);
        let aonHeader = this.getElement('aonHeader');
        aonHeader.timeControlStatus(r);
			});
		}
  }

  periodSideNavDisplay(b){
    let eleSideNav = this.getElement(`${this.applicationEl.SIDENAV}Período`);
    if(eleSideNav) eleSideNav.style.display = b ? "block": "none";
  }

  async setDataFilter(data){
	console.log('setDataFilter', data);
    try {
      this.showView(SIGNIN_VIEWS.AON_PRESENCE_LIST);
      
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
      
      this.applicationEl.removeToolbarOptions();

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
          case SIGNIN_VIEWS.AON_STATISTICS:
             this.buildTimeControl();
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
      this.TASK_HOLDER = result.find(({domain_id})=> domain_id === parseInt(LS.getDomainId()));
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
  
  async buildTimeControl() {
      getPosition().then(console.log).catch(console.error);

      const r = await getTimeControl();

      let div3 = this.getElement(this.TIMECONTROL_SIGN) || this.createElement(TAG.DIV);
      div3.id = this.TIMECONTROL_SIGN;
      if(this.isMobile()){
        div3.style.borderTop = '1px solid #ddd';
        this.clearElement(div3);
        div3.appendChild(this.createTitleTime());
      }

      let staticsDiv = this.createElement(TAG.DIV);
      staticsDiv.style.height = "15rem";
      staticsDiv.style.minWidth = "10rem";
      staticsDiv.style.maxWidth = "20rem";
      staticsDiv.style.margin = "0 auto";

      staticsDiv.appendChild(new AonStatistics());

      div3.appendChild(staticsDiv);
      let aonSign = new AonSign();
      aonSign.setTimeControl(r);
      div3.appendChild(aonSign);
      this.applicationEl.setContent(div3);
      if(this.isMobile()){
        const {TODAY, YESTERDAY, THIS_WEEK, THIS_MONTH}  = SigninSidenav.PERIOD;
        let div = this.getElement("aonSigninContent");
        const apps = [
          {title: "Hoy", fn: () => this.setDataFilter({period:TODAY.id})},
          {title: "Ayer", fn: () => this.setDataFilter({period:YESTERDAY.id})},
          {title: "Semana actual", fn: () => this.setDataFilter({period:THIS_WEEK.id})},
          {title: "Mes actual", fn: () => this.setDataFilter({period:THIS_MONTH.id})}
        ]
        div.appendChild(this.createApps(apps));
      }
  }

  createApps(apps) {
    let ul = this.createElement(TAG.UL);
    ul.id = "aonMobileAppSelection";
    ul.classList.add(CSS.AON_UL, CSS.AON_LIST_GROUP);

    apps.forEach(({ title, fn }) => {
        let li = this.createElement(TAG.LI);
        li.id = "aonMobileTcApp" + title;
        li.classList.add(CSS.AON_LIST_GROUP_ITEM, CSS.AON_APP_LI, "fixLi");
        li.style.borderRight = "0px";
        li.style.borderLeft = "0px";
        li.style.cursor = "pointer";

        li.addEventListener("click", fn);

        let span = this.createElement(TAG.SPAN);
        span.style.margin = "20px";

        let icon = this.createElement(TAG.SPAN); 
        icon.classList.add(CSS.MATERIAL_SYMBOLS_OUTLINED);
        icon.id = "aonMobileSelectionIcon" + title;
        icon.innerHTML = "today"; 
        icon.style.backgroundColor = "var(--aonTimecontrol)";
        icon.style.color = "white";
        icon.style.fontVariationSettings = "'FILL' 0, 'wght' 300, 'GRAD' 0, 'opsz' 24";
        icon.style.borderRadius = "5px";
        icon.style.width = "32px";
        icon.style.height = "32px";

        let span2 = this.createElement(TAG.SPAN);
        span2.id = "aonMobileTcTitle" + title;
        span2.className = "aonAppTitle";
        span2.innerHTML = title;

        span.appendChild(icon);
        span.appendChild(span2);
        li.appendChild(span);
        ul.appendChild(li);
    });

    return ul;
  }

  createTitleTime(){
    let div = this.createElement(TAG.DIV);
    div.className = CSS.AON_SIDENAV_TITLE;
    div.innerHTML = 'CONTROL HORARIO';
    div.style.textAlign = "left";
    div.style.marginLeft = "0";
    div.style.paddingLeft = "10";
    return div;
  }

  isEmployee() {
    return !this.getDur().isTimecontrolManager() && !this.getDur().isTimecontrolPortal();
  }

}
window.customElements.define("aon-timecontrol", AonTimecontrol);
