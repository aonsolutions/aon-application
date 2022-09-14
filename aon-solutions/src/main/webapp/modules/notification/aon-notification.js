import { AonElement } from "../../components/AonElement.js";
import { AonNotificationDesk } from "./aon-notification-desk.js";
import { AonNotificationMobile } from "./aon-notification-mobile.js";

export class AonNotification extends AonElement {

 
  constructor() {
    super();
  }

  connectedCallback() {
    this.initialize();
  }

  initialize() {
    const element = this.isMobile() ?  new AonNotificationMobile() : new AonNotificationDesk();
 
    this.rootPanel(element);
  }
}
window.customElements.define("aon-notification", AonNotification);
