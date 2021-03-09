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

import {MobileMenuApps, DOCUMENTAL, TIMECONTROL, INVOICE, COMUNICA, MESSENGER,
   PAYROLL} from "../services/app.js"

import "./comunic@/aon-comunica.js";
import "./documental/aon-documental.js";
import "./signin/aon-signin.js";
import "./invoice/aon-invoice-panel.js";
import "./laboral/aon-laboral.js";

export class AonMobileMenu extends AonElement {

  // CAMERA_INPUT;
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
    this.eventListener();
  }

  eventListener(){
    window.addEventListener('userAuth', ()=>{
      this.build();
			this.reload();
		});
  }

  connectedCallback() {
    this.id = this.id || 'aonMobileMenu';
    // this.CAMERA_INPUT = this.id + "CameraInput";
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
      };
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
          if (height - top < height / 2) top = top - 80;
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
    if(menu.childNodes.length < 5){
      span.style.marginRight = n;
    }
    menu.appendChild(span);

    let button = new AonIconButton();
    button.id = span.id + 'Button';
    if(app.icon) button.icon = app.icon;
    if(app.aonIcon) {
      button.aonIcon = app.aonIcon;
    }
    button.addEventListener("click", app.fn);
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
    if(DOCUMENTAL.app === app.app)
      return this.getDur().isDocumental();
    else if(PAYROLL.app === app.app)
      return this.getDur().isPayroll();
    else if(COMUNICA.app === app.app)
      return this.getDur().isComunica();
    else if(TIMECONTROL.app === app.app)
      return this.getDur().isTimecontrol();
    else if(INVOICE.app === app.app)
      return this.getDur().isInvoice();
    else if(MESSENGER.app === app.app)
      return this.getDur().isMessenger();
    else return false;
  }

  getAppInfo(app) {
    if(DOCUMENTAL.app === app.app)
      return {
        name: app.title,
        icon: "snippet_folder",
        fn: () => rootPanel("<aon-signin></aon-signin>")
      };
    else if(PAYROLL.app === app.app)
      return {
        name: app.title,
        aonIcon: "aon_seg_social",
        fn: () => rootPanel("<aon-laboral></aon-laboral>"),
      };
    else if(COMUNICA.app === app.app)
      return {
        name: app.title,
        icon: "alternate_email",
        fn: () => rootPanel("<aon-comunica></aon-comunica>")
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
        fn: () => alert('en desarrollo')
      };
    return undefined;
  }
}

window.customElements.define("aon-mobile-menu", AonMobileMenu);
