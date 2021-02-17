import { AonElement } from "../../components/AonElement.js";
import "../../components/aon-toast.js";
import "../../components/aon-application.js";
import {AonPresenceList} from "./time-control/aon-presence-list.js";
import "./time-control/location/aon-location-list.js";
import "./time-control/location/aon-location-add.js";

export class AonSignin extends AonElement {
  AON_SIGNIN;

  constructor() {
    super();
    this.AON_SIGNIN = "aonSignin";
  }

  connectedCallback() {
    this.paintView();
    this.build();
  }
  disconnectedCallback() {}
  paintView() {
    this.innerHTML = `
			<aon-toast id="${this.AON_SIGNIN}Toast"></aon-toast>
			<aon-application id="${this.AON_SIGNIN}" title="Control Horario"></aon-application>
    `;
  }
  build() {
    this.aonSigninEl = this.getElement("aonSignin");
    this.buildToolbar();
    this.paintViewPresenceList();
  }

  buildToolbar() {
    const options = [
      {
        name: "Presencia",
        icon: "account_box",
        fn: () => this.paintViewPresenceList()
      },
      {
        name: "Ubicaciones",
        icon: "location_on",
        fn: () =>
          this.aonSigninEl.setContentHTML(
            `<aon-location-list id="aonLocationList"></aon-location-list>`
          ),
      },
    ];
    
    this.aonSigninEl.addSidenavOptions("Control horario", options);

    const options2 = [
      {
        icon: 'today',
        name: "Hoy",
        fn: () => this.filterAonSignin({period:"today"})
      },
      {
        icon: 'today',
        name: "Ayer",
        fn: () => this.filterAonSignin({period:"yesterday"})
      },
      {
        icon: 'today',
        name: "Ésta semana",
        fn: () => this.filterAonSignin({period: "this_week"})
      },
      {
        icon: 'today',
        name: "Éste mes",
        fn: () => this.filterAonSignin({period: "this_month"})
      }
    ];
    this.aonSigninEl.addSidenavOptions("Período", options2);
  }

  async paintViewPresenceList(){
    let aonList = new AonPresenceList();
    aonList.id = "aonPresenceList";
    this.aonSigninEl.setContent(aonList);
  }

  filterAonSignin(data){
    window.dispatchEvent(new CustomEvent('filterAonSignin', { detail: data }));
  }

  openLocationAdd(el, data) {
    let id = "aonLocationAdd";
    this.aonSigninEl.setContentHTML(`<aon-location-add id="${id}"></aon-location-add>`);
    const {coordinates} = data;
    const aonEventEl = this.getElement(id);
    if(aonEventEl && coordinates && coordinates.latitude && coordinates.longitude) {
      aonEventEl.data = { ...data, latitude:coordinates.latitude, longitude: coordinates.longitude};
    }
  }
}
window.customElements.define("aon-signin", AonSignin);
