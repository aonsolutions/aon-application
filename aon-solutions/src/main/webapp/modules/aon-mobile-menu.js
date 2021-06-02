import { AonElement } from "../components/AonElement.js";
import { AonIconButton } from "../components/aon-icon-button.js";
import {DomainUserRoles} from '../models/DomainUserRoles.js';
import {AonDialogMenu} from "../components/aon-dialog-menu.js";
import { waitEl } from "../services/utils.js";
import { getDomainUserRoles, closeSession } from "../services/service.js";
import { AonDocumentalAyudat } from "./documental/ayudat/aon-documental-ayudat.js";
import { AonDocumental } from "./documental/aon-documental.js";
import { AonMessenger } from "./messenger/aon-messenger.js";
import { AonAccounting } from "./accounting/aon-accounting.js";
import { AonFiscal } from  "./fiscal/aon-fiscal.js";
import { AonInvoicePanel } from "./invoice/aon-invoice-panel.js";
import { AonSignin } from "./signin/aon-signin.js";
import { AonLaboral } from "./laboral/aon-laboral.js";
import { CONSTANT, EVENT, MATERIAL_ICONS, MSG, TAG } from '../environments/environments.js';
import { MobileMenuApps, DOCUMENTAL, TIMECONTROL, INVOICE, COMUNICA, MESSENGER, PAYROLL, ACCOUNTING, FISCAL } from "../services/app.js";

export class AonMobileMenu extends AonElement {

  TYPE_IMG;
  dur;

  get id() {
    return this.getAttribute(CONSTANT.ID);
  }

  set id(id) {
    this.setAttribute(CONSTANT.ID, id);
  }

  get company() {
    return this.getAttribute(CONSTANT.COMPANY);
  }

  set company(company) {
    this.setAttribute(CONSTANT.COMPANY, company);
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

  connectedCallback() {
    this.initialize();
    this.eventListener();
  }

  eventListener(){
    this.build();
    window.addEventListener(EVENT.RESIZE, () => {
      this.reload();
    });

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
      fn: () => this.rootPanelHtml('<aon-mobile-desktop id="aonDesktop"></aon-mobile-desktop>')
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
        fn: () => this.rootPanelHtml('<aon-configuration id="aon-configuration"></aon-configuration>')
      });
      count++;
    }

    if(count <= 4) {
      this.addMenuButton({
        name: 'Help',
        icon: 'help_outline',
        fn: () => this.rootPanelHtml('<iframe height="100%" width="100%" src="https://faqs.aonsolutions.es/"></iframe>')
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
    const idSpan = this.id + app.name;
    let span = this.getElement(idSpan);
    if(!span){
      span = this.createElement(TAG.SPAN);
      let menu = this.getElement(`${this.id}Sidenav`);
      let n = (window.innerWidth / 5 - 40) / 2;
      span.id = idSpan;
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
        fn: () => {
          const aonDocumental = this.getDur().isBidoq() ? new AonDocumentalAyudat() : new AonDocumental();
				  this.rootPanel(aonDocumental);
        }
      };
    else if(COMUNICA.app === app.app)
      return {
        name: app.title,
        aonIcon: "aon_seg_social",
        fn: () =>{
          const aonLaboral = new AonLaboral();
          aonLaboral.title = MSG.COMUNICA;
          this.rootPanel(aonLaboral);
        }
      };
    else if(PAYROLL.app === app.app)
      return {
        name: app.title,
        aonIcon: "aon_seg_social",
        fn: () => {
          const aonLaboral   = new AonLaboral();
          aonLaboral.title = MSG.PAYROLL;
          this.rootPanel(aonLaboral);
        }
      };
    else if(TIMECONTROL.app === app.app)
      return {
        name: app.title,
        icon: "alarm_on",
        fn: () => this.rootPanel(new AonSignin())
      };
    else if(INVOICE.app === app.app)
      return {
        name: app.title,
        icon: "receipt",
        fn: () => this.rootPanel(new AonInvoicePanel())
      };
    else if(MESSENGER.app === app.app)
      return {
        name: "Solicitudes",
        icon: "message",
        fn: () => this.isBeta() ? this.rootPanel(new AonMessenger()) :  alert('en desarrollo')
      };
    else if(FISCAL.app === app.app)
      return {
        name: "Fiscal",
        icon: "receipt",
        fn: () => this.rootPanel(new AonFiscal())
      };
    else if(ACCOUNTING.app === app.app)
      return {
        name: "Contabilidad",
        icon: MATERIAL_ICONS.ACCOUNT_BALANCE,
        fn: () => this.rootPanel(new AonAccounting())
      };
    return undefined;
  }
}

window.customElements.define("aon-mobile-menu", AonMobileMenu);
