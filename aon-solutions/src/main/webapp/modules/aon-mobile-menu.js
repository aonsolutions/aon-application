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

import "./comunic@/aon-comunica.js";
import "./documental/aon-documental.js";
import "./signin/aon-signin.js";
import "./invoice/aon-invoice-panel.js";


export class AonMobileMenu extends AonElement {

  CAMERA_INPUT;
  TYPE_IMG;

  _roles;

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
    this.CAMERA_INPUT = this.id + "CameraInput";
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
      this._roles = new DomainUserRoles(r);
      menu.innerHTML = '';
      this.buildMenu();
     });
  }

  async buildMenu(){
    await waitEl(`#${this.id}Sidenav`); 
    let count = 1;

    this.addMenuButton('Home', 'home', () =>
      rootPanel('<aon-mobile-desktop id="aonDesktop"></aon-mobile-desktop>')
    );

    if(this._roles && this._roles.isDocumental()) {
      count++;
      this.addMenuButton('Documental', 'snippet_folder', () =>
        rootPanel("<aon-documental></aon-documental>")
      );
    }

    if(this._roles && this._roles.isTimecontrol()) {
      count++;
      this.addMenuButton('Timecontrol', 'alarm_on', () =>
        rootPanel("<aon-signin></aon-signin>")
      );
    }

    if(this._roles && this._roles.isInvoice()) {
      count++;
      this.addMenuButton('Invoice', 'receipt', () =>
        rootPanel("<aon-invoice-panel></aon-invoice-panel>")
      );
    }

    if(this._roles && this._roles.isComunica() && this._roles.isMessenger() && count === 4){
      count++;
      this.addMenuButton('More', 'more_horiz', ({ target }) => {
        let top = target.getBoundingClientRect().top;
        const left = target.getBoundingClientRect().left;
        const height = window.innerHeight;

        if (height - top < height / 2) top = top - 80;

        let d = this.getElement(this.id + 'dialogMenu');

        let options = [
          {
            name: "Comunica",
            icon: "alternate_email",
            fn: () => rootPanel("<aon-comunica></aon-comunica>"),
          },
          {
            name: "Solicitudes",
            icon: "message",
            fn: () => alert('en desarrollo')
          }
        ];
        d.setMenuOptions(options, top, left);
        d.open();
      });
    } else {
      if(this._roles && this._roles.isComunica()) {
        count++;
        this.addMenuButton('Comunica', 'alternate_email', () =>
          rootPanel("<aon-comunica></aon-comunica>")
        );
      }

      if(this._roles && this._roles.isMessenger()) {
        count++;
        this.addMenuButton('Messenger', 'message', () =>
          alert('en desarrollo')
        );
      }
    }
    if(count <= 4) {
      count++;
      this.addMenuButton('Configuration', 'settings', () =>
        rootPanel('<aon-configuration id="aon-configuration"></aon-configuration>')
      );
    }

    if(count <= 4) {
      count++;
      this.addMenuButton('Help', 'help_outline', () =>
        rootPanel('<iframe height="100%" width="100%" src="https://faqs.aonsolutions.es/"></iframe>')
      );
    }

    if(count <= 4) {
      count++;
      this.addMenuButton('CloseSession', 'input', () => closeSession());
    }
  }

  addMenuButton(name, icon, action) {
    let menu = this.getElement(`${this.id}Sidenav`);
    let n = (window.innerWidth / 5 - 40) / 2;
    let span = document.createElement('span');
    span.id = this.id + name;
    span.style.top = '10px';
    span.style.position = 'relative';
    span.style.marginLeft = n;
    if(menu.childNodes.length < 5){
      span.style.marginRight = n;
    }
    menu.appendChild(span);

    let button = new AonIconButton();
    button.id = span.id + 'Button';
    button.icon = icon;
    button.addEventListener("click", action);
    span.appendChild(button);
  }

  loading(load) {
    let block = load ? "block" : "none";
    let aonEl = document.querySelector("#aonMobileMenuLoading");
    aonEl.style.display = block;
  }
}

window.customElements.define("aon-mobile-menu", AonMobileMenu);
