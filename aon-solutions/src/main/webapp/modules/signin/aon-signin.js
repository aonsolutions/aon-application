import { AonElement } from "../../components/AonElement.js";

import {AonPresenceList} from "./time-control/aon-presence-list.js";
import { getPeriod } from "../../services/service.js";
import { formatDateOrigin, setValueName } from "../../services/utils.js";
import { AonLocationAdd } from "./time-control/location/aon-location-add.js";
import { AonLocationList } from "./time-control/location/aon-location-list.js";
import { SigninSidenav } from "./signinEnums.js";
import "../../components/aon-toast.js";
import "../../components/aon-application.js";

export class AonSignin extends AonElement {
  AON_SIGNIN;
  _filter;

  constructor() {
    super();
  }

  disconnectedCallback() {}
  connectedCallback() {
    this.initialize();
    this.build();
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
    this.paintViewPresenceList();
  }

  paintView() {
    this.innerHTML = `
			<aon-toast id="${this.AON_SIGNIN}Toast"></aon-toast>
			<aon-application id="${this.AON_SIGNIN}" title="Control Horario"></aon-application>
    `;
    this.aonSigninEl = this.getElement(this.AON_SIGNIN);
  }
  
  buildToolbar() {
    const options = [
      {
        ...SigninSidenav.PRESENCE,
        fn: () => this.paintViewPresenceList()
      },
      {
        ...SigninSidenav.LOCATION,
        fn: () =>{
          this.aonSigninEl.setContent(new AonLocationList());
          this.periodSideNavDisplay(false);
        }

      },
    ];
    
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

  async paintViewPresenceList(){
    const id = "aonPresenceList";
    if(this.getElement(id)==null){
      let aonList = new AonPresenceList();
      aonList.id = id;
      this.aonSigninEl.setContent(aonList);
    }
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

  openLocationAdd(el, data) {
    const {coordinates} = data;
    let aonLocationAdd = new AonLocationAdd();
    aonLocationAdd.id = "aonLocationAdd";
    if(coordinates && coordinates.latitude && coordinates.longitude) {
      aonLocationAdd.data = {latitude:coordinates.latitude, longitude: coordinates.longitude};
    }
    this.aonSigninEl.setContent(aonLocationAdd);
  }
}
window.customElements.define("aon-signin", AonSignin);
