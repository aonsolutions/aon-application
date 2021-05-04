import { AonElement } from "../components/AonElement.js";
import { AonIconButton } from "../components/aon-icon-button.js";
import {DomainUserRoles} from '../models/DomainUserRoles.js';
import { rootPanel } from "../services/gwtLoader.js";
import {AonDialogMenu} from "../components/aon-dialog-menu.js";
import { waitEl } from "../services/utils.js";
import {
  getDomainUserRoles,
  closeSession
} from "../services/service.js";

import { EVENT, MATERIAL_ICONS, MSG } from '../environments/environments.js';
import {MobileMenuApps, DOCUMENTAL, TIMECONTROL, INVOICE, COMUNICA, MESSENGER,
   PAYROLL, ACCOUNTING, FISCAL} from "../services/app.js"

import "./documental/aon-documental.js";
import "./signin/aon-signin.js";
import "./invoice/aon-invoice-panel.js";
import "./laboral/aon-laboral.js";
import "./fiscal/aon-fiscal.js";
import "./messenger/aon-messenger.js";


export class AonMobileMenu extends AonElement {

  TYPE_IMG;
  dur;

  get id() {
    return this.getAttribute("id");
  }

  set id(id) {
    this.setAttribute("id", id);
  }

  get company() {
    return this.getAttribute("company");
  }

  set company(company) {
    this.setAttribute("company", company);
  }

  get user() {
    return this.getAttribute("user");
  }

  set user(user) {
    this.setAttribute("user", user);
  }

  constructor() {
    super();
  }

  eventListener(){
    window.addEventListener(EVENT.USER_AUTH, ()=>{
      this.build();
		});
    window.addEventListener(EVENT.RESIZE, () => {
      this.reload();
    });

  }

  connectedCallback() {
    this.initialize();
    this.eventListener();
  }

  initialize() {
    this.id = this.id || 'aonMobileMenu';
  }


  build() {
    const id = this.id + 'Sidenav';
    const sidEl = this.getElement(id);
    if(sidEl)sidEl.remove();
    let div = document.createElement('div');
    div.id = id;
    div.className = 'aonMobileMenu';
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
      fn: () => rootPanel('<aon-mobile-desktop id="aonDesktop"></aon-mobile-desktop>')
    });

    let apps = [];
    let options = [];
    MobileMenuApps.forEach((app, i) => {
      if(this.isApp(app) && this.getAppInfo(app)) {
        apps.push(this.getAppInfo(app));
      }
    });
    
    apps.forEach((app, i) => {
      if(count >= 4 && apps.length > 4) {
        options.push(app);
      } else {
        this.addMenuButton(app);
      }
      count++;
    });

    if(apps.length > 4){
      this.addMenuButton({
        name: 'More',
        icon: 'more_horiz',
        fn: ({ target }) => {
          let top = target.getBoundingClientRect().top;
          const left = target.getBoundingClientRect().left;
          const height = window.innerHeight;
          if (height - top < height / 2) top = top - (options.length * 35);
          let d = this.getElement(this.id + 'dialogMenu');
          d.setMenuOptions(options, top, left);
          d.open();
        }
      });
    }

    if(count <= 4) {
      this.addMenuButton({
        name: 'Configuration',
        icon: 'settings',
        fn: () => rootPanel('<aon-configuration id="aon-configuration"></aon-configuration>')
      });
      count++;
    }

    if(count <= 4) {
      this.addMenuButton({
        name: 'Help',
        icon: 'help_outline',
        fn: () => rootPanel('<iframe height="100%" width="100%" src="https://faqs.aonsolutions.es/"></iframe>')
      });
      count++;
    }

    if(count <= 4) {
      this.addMenuButton({
        name: 'CloseSession',
        icon: 'input',
        fn: () => closeSession()
      });
    }
  }

  addMenuButton(app) {
    let menu = this.getElement(`${this.id}Sidenav`);
    let n = (window.innerWidth / 5 - 40) / 2;
    let span = document.createElement('span');
    span.id = this.id + app.name;
    span.style.top = '10px';
    span.style.position = 'relative';
    span.style.marginLeft = n;
    if(menu.childNodes && menu.childNodes.length < 5){
      span.style.marginRight = n;
    }
    menu.appendChild(span);

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

  loading(load) {
    let block = load ? "block" : "none";
    let aonEl = document.querySelector("#aonMobileMenuLoading");
    aonEl.style.display = block;
  }


  isApp(app) {
    const dur = this.getDur();
    let permission = false;
    if(dur){
      switch(app.app){
        case DOCUMENTAL.app:
          permission = dur.isDocumental();
          break;
        case COMUNICA.app:
          permission = (dur.isComunicaManager() || dur.isComunicaPortal() ) && !dur.isPayroll()
          break;
        case PAYROLL.app:
          permission = dur.isPayroll();
          break;
        case TIMECONTROL.app:
          permission = dur.isTimecontrol();
          break;
        case INVOICE.app:
          permission = dur.isInvoice();
          break;
        case MESSENGER.app:
          permission = dur.isMessenger();
          break;
        case FISCAL.app:
          permission = dur.isFiscal();
          break;
        case ACCOUNTING.app:
          permission = dur.isAccounting();
          break;
      }
    }
    return permission;
  }

  getAppInfo(app) {
    if(DOCUMENTAL.app === app.app)
      return {
        name: app.title,
        icon: MATERIAL_ICONS.FOLDER,
        fn: () => rootPanel("<aon-documental></aon-documental>")
      };
    else if(COMUNICA.app === app.app)
      return {
        name: app.title,
        aonIcon: "aon_seg_social",
        fn: () =>rootPanel(`<aon-laboral title="${MSG.COMUNICA}"></aon-laboral>`)
      };
    else if(PAYROLL.app === app.app)
      return {
        name: app.title,
        aonIcon: "aon_seg_social",
        fn: () => rootPanel(`<aon-laboral title="${MSG.PAYROLL}"></aon-laboral>`),
      };
    else if(TIMECONTROL.app === app.app)
      return {
        name: app.title,
        icon: "alarm_on",
        fn: () => rootPanel("<aon-signin></aon-signin>")
      };
    else if(INVOICE.app === app.app)
      return {
        name: app.title,
        icon: "receipt",
        fn: () => rootPanel('<aon-invoice-panel></aon-invoice-panel>')
      };
    else if(MESSENGER.app === app.app)
      return {
        name: "Solicitudes",
        icon: "message",
        fn: () => this.isBeta() ? rootPanel(`<aon-messenger></aon-messenger>`) :  alert('en desarrollo')
      };
    else if(FISCAL.app === app.app)
      return {
        name: "Fiscal",
        icon: "receipt",
        fn: () => rootPanel('<aon-fiscal></aon-fiscal>')
      };
    else if(ACCOUNTING.app === app.app)
      return {
        name: "Contabilidad",
        icon: MATERIAL_ICONS.ACCOUNT_BALANCE,
        fn: () =>  rootPanel('<aon-accounting></aon-accounting>')
      };
    return undefined;
  }
}

window.customElements.define("aon-mobile-menu", AonMobileMenu);
