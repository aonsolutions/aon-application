import { AonElement } from "../../components/AonElement.js";
import "../../components/aon-toast.js";
import "../../components/aon-application.js";
import "./time-control/aon-presence-list.js";
import "./time-control/location/aon-location-list.js";

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
			<aon-application id="${this.AON_SIGNIN}" title="Control de Horario"></aon-application>
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
    this.aonSigninEl.addSidenavOptions("Control de horario", options);
  }

  paintViewPresenceList(){
    this.aonSigninEl.setContentHTML(
      `<aon-presence-list></aon-presence-list>`
    );
	}
}
window.customElements.define("aon-signin", AonSignin);
