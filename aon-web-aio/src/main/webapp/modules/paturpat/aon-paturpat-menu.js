import { AonElement } from "../../components/AonElement.js";
import { AonIconButton } from "../../components/aon-icon-button.js";
import { AonDialogMenu } from "../../components/aon-dialog-menu.js";
import { waitEl } from "../../services/utils.js";
import { CONSTANT, EVENT, MATERIAL_ICONS, TAG } from '../../environments/environments.js';

import { AonNotificationIcon } from "../notification/aon-notification-icon.js";
import { AonMobileHome } from "../home/aon-mobile-home.js";
import { AonMobileProfile } from "../user/aon-mobile-profile.js";

import * as LS from '../../services/localStorageService.js';
import { AonPaturpatHome } from "./aon-paturpat-home.js";

export class AonPaturpatMenu extends AonElement {

  SELECTED;
  DIALOG_MENU;

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
    this.INPUT_INVOICE_FILE = this.id + 'InputInvoiceFile';
    this.INPUT_DOCUMENT_FILE = this.id + 'InputDocumentFile';
    this.INPUT_CAMERA = this.id + 'InputCamera';
    this.SELECTED = "invoice";
    this.DIALOG_MENU = this.id + 'DialogMenu';
  }

  build() {
    const id = this.id + 'Sidenav';
    const sidEl = this.getElement(id);
    if(sidEl)sidEl.remove();
    let div = this.createElement(TAG.DIV);
    div.id = id;
    div.className = 'aonMobileMenu';
    this.appendChild(div);
    let dialogMenu = new AonDialogMenu();
    dialogMenu.id = this.DIALOG_MENU;
    this.appendChild(dialogMenu);

    this.buildMenu();
  }

  getDialogMenu() {
    return this.getElement(this.DIALOG_MENU);
  }

  setMarginById(id, value){
    let elem = this.getElement(id);
    elem.style.marginLeft = value;
    elem.style.marginRight = value;
  }

  reload() {
    waitEl(`#${this.id}Sidenav`).then(async(menu)=>{
        menu.innerHTML = '';
        this.buildMenu();
     });
  }

  async buildMenu(){
    await waitEl(`#${this.id}Sidenav`);

    this.addMenuButton({
      name: 'Home',
      icon: 'home',
      fn: () => this.home()
    });

    this.addMenuButton({
      name: 'User',
      icon: MATERIAL_ICONS.PERSON,
      fn: () => this.user()
    });
  }

  addMenuButton(app) {
    const idSpan = this.id + app.name;
    let span = this.getElement(idSpan);
    if(!span){
      span = this.createElement(TAG.SPAN);
      let menu = this.getElement(`${this.id}Sidenav`);
      let n = (window.innerWidth / 3 - 40) / 2;
      span.id = idSpan;
      menu.appendChild(span);

      if(app.icon === MATERIAL_ICONS.NOTIFICATIONS){
        span.appendChild(new AonNotificationIcon());
      } else {
        let button = new AonIconButton();
        button.id = span.id + 'Button';
        if(app.icon) button.icon = app.icon;
        if(app.aonIcon) {
          button.aonIcon = app.aonIcon;
        }
        button.addEventListener(EVENT.CLICK, app.fn);
        if(app.background) button.background = app.background;
        if(app.color) button.color = app.color;
        button.noHover = true;
        span.appendChild(button);
        button.getButton().style.color = '#005f2c';
        if(app.aonIcon) {
          this.getElement(button.BUTTON).style.bottom = '5px';
          this.getElement(button.AON_ICON).size = "20";
        }
        return button;
      }
    }
    return null;
  }

  loading(load) {
    let block = load ? "block" : "none";
    let aonEl = document.querySelector("#aonMobileMenuLoading");
    aonEl.style.display = block;
  }

  home() {
    const rootPanel = this.getElement("mobileRootPanel");
    rootPanel.innerHTML = '';
    rootPanel.appendChild(new AonPaturpatHome());
  }

  user() {
    const rootPanel = this.getElement("mobileRootPanel");
    rootPanel.innerHTML = '';
  	const aonMobileProfile = new AonMobileProfile();
		aonMobileProfile.id = CONSTANT.AON_MOBILE_PROFILE;
	  rootPanel.appendChild(aonMobileProfile);
  }
}
if(!window.customElements.get(TAG.AON_PATURPAT_MENU)){
  window.customElements.define(TAG.AON_PATURPAT_MENU, AonPaturpatMenu);
}

