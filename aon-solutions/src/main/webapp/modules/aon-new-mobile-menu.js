import { AonElement } from "../components/AonElement.js";
import { AonIconButton } from "../components/aon-icon-button.js";
import {DomainUserRoles} from '../models/DomainUserRoles.js';
import {AonDialogMenu} from "../components/aon-dialog-menu.js";
import { waitEl } from "../services/utils.js";
import { getDomainUserRoles } from "../services/service.js";

import { CONSTANT, EVENT, TAG } from '../environments/environments.js';
import * as LS from '../services/localStorageService.js';
import { AonNotification } from "./notification/aon-notification.js";
import { AonConfiguration } from "./configuration/aon-configuration.js";
import { AonApps } from "./aon-apps.js";
import { AonNotificationIcon } from "./notification/aon-notification-icon.js";

export class AonNewMobileMenu extends AonElement {

  dur;

  get id() {
    return this.getAttribute(CONSTANT.ID);
  }

  set id(id) {
    this.setAttribute(CONSTANT.ID, id);
  }

  constructor() {
    super();
  }

  connectedCallback() {
    this.initialize();
    this.eventListener();
  }

  eventListener(){
    this.build();
    window.addEventListener(EVENT.RESIZE, () => {
      this.reload();
    });

    if(this.android()){
      const height = window.innerHeight;
      window.onresize = ({target})=> {
        let bottom = "59px";
        let display = "block";
        let application = this.getApplication();
        if((target.innerHeight +15) < height){
          display = "none";
          bottom = "1px";
        }
        if(application && application.getContent())
          application.getContent().style.bottom = bottom;
        this.style.display = display;
      }
    }
  }

  initialize() {
    this.id = this.id || 'aonMobileMenu';
  }

  build() {
    const id = this.id + 'Sidenav';
    const sidEl = this.getElement(id);
    if(sidEl)sidEl.remove();
    let div = this.createElement(TAG.DIV);
    div.id = id;
    div.className = 'aonMobileMenu';
    if(this.isSab()) {
      div.style.marginBottom = '10px'; 
    }
    this.appendChild(div);
    let dialogMenu = new AonDialogMenu();
    dialogMenu.id = this.id + 'dialogMenu';
    this.appendChild(dialogMenu);

    this.buildMenu();
  }

  reload() {
    waitEl(`#${this.id}Sidenav`).then(async(menu)=>{
        const r = await getDomainUserRoles({});
        this.dur = new DomainUserRoles(r);
        menu.innerHTML = '';
        this.buildMenu();
     });
  }

  getDur() {
    return this.dur;
  }

  async buildMenu(){
    await waitEl(`#${this.id}Sidenav`);
    let count = 1;
    this.addMenuButton({
      name: 'Home',
      icon: 'home',
      fn: () => this.home()
    });

    this.addMenuButton({
      name: 'Apps',
      icon: 'apps',
      fn: () => this.apps()
    });

    this.addMenuButton({
      name: 'Notification',
      icon: 'notifications',
      fn: () => this.notification()
    });

    this.addMenuButton({
      name: 'User',
      icon: 'person',
      fn: () => this.user()
    });
  }

  addMenuButton(app) {
    const idSpan = this.id + app.name;
    let span = this.getElement(idSpan);
    if(!span){
      span = this.createElement(TAG.SPAN);
      let menu = this.getElement(`${this.id}Sidenav`);
      let n = (window.innerWidth / 4 - 40) / 2;
      span.id = idSpan;
      span.style.top = '10px';
      span.style.position = 'relative';
      span.style.marginLeft = n;
      if(menu.childNodes && menu.childNodes.length < 5){
        span.style.marginRight = n;
      }
      menu.appendChild(span);
      
      if(app.icon === 'notifications'){
        span.appendChild(new AonNotificationIcon());
      } else {
        let button = new AonIconButton();
        button.id = span.id + 'Button';
        if(app.icon) button.icon = app.icon;
        if(app.aonIcon) {
          button.aonIcon = app.aonIcon;
        }
        button.addEventListener(EVENT.CLICK, app.fn);
        span.appendChild(button);
        if(app.aonIcon) {
        this.getElement(button.BUTTON).style.bottom = '5px';
        this.getElement(button.AON_ICON).size = "20";
        }
      }
    }
  }

  loading(load) {
    let block = load ? "block" : "none";
    let aonEl = document.querySelector("#aonMobileMenuLoading");
    aonEl.style.display = block;
  }

  home() {
    if(LS.getCompany()) {
      let aonHeader = this.getElement("aonHeader");
      aonHeader.companyIn();
      this.rootPanelHtml('<aon-mobile-desktop id="aonDesktop"></aon-mobile-desktop>');
    } else {
      let aonHeader = this.getElement("aonHeader");
      aonHeader.companyOut();
      this.rootPanelHtml('<aon-mobile-parent id="aonParent"></aon-mobile-parent>');
    }
  }

  apps() {
    if(LS.getCompany()) {
      let aonHeader = this.getElement("aonHeader");
      aonHeader.companyIn();
      this.rootPanel(new AonApps());
    } else {
      alert("selecciona una empresa.")
    }
  }

  notification() {
    if(LS.getCompany()) {
      let aonHeader = this.getElement("aonHeader");
      aonHeader.companyIn();
      this.rootPanel(new AonNotification());
    } else {
      alert("selecciona una empresa.")
    }
  }

  user() {
    if(LS.getCompany()) {
      let aonHeader = this.getElement("aonHeader");
      aonHeader.companyIn();
    
    }
    this.rootPanel(new AonConfiguration());
  }
}

window.customElements.define("aon-new-mobile-menu", AonNewMobileMenu);
