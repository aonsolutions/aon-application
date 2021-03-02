import { AonElement } from "../components/AonElement.js";
import { AonIconButton } from "../components/aon-icon-button.js";
import {DomainUserRoles} from '../models/DomainUserRoles.js';
import { rootPanel } from "../services/gwtLoader.js";
// import { getReader } from "../services/utils.js";
import {
  uploadFileDocumental,
  insertInvoice,
  // actionMobile,
  getDomainUserRoles,
  closeSession
} from "../services/service.js";
import { Invoice } from "./invoice/Invoice.js";

import "./comunic@/aon-comunica.js";
import "./documental/aon-documental.js";
import "./signin/aon-signin.js";
import "./invoice/aon-invoice-panel.js";
import {AonDialogMenu} from "../components/aon-dialog-menu.js";
import { waitEl } from "../services/utils.js";
// import { downscaleImage } from "../services/compressImg.js";

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
    console.log("connect aon-mobile-menu");
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
      console.log("reload menu");
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


  buildOld() {
    this.innerHTML = `
			<div id="aonMobileMenuSidenav" class="aonMobileMenu">

				<span id="aonMobileMenuDocumental"  style="top: 10px; position: relative;">
					<aon-icon-button id="aonMobileMenuDocumentalButton" icon="snippet_folder"></aon-icon-button>
				</span>

				<span id="aonMobileMenuAdd"  style="top: 10px; position: relative;">
					<aon-icon-button id="aonMobileMenuAddButton" icon="alarm_on"></aon-icon-button>
				</span>

				<span id="aonMobileMenuInvoice"  style="top: 10px; position: relative;">
					<aon-icon-button id="aonMobileMenuInvoiceButton" icon="receipt"></aon-icon-button>
				</span>

				<span id="aonMobileMenuComunica" style="top: 10px; position: relative;">
					<aon-icon-button id="aonMobileMenuComunicaButton" icon="alternate_email"></aon-icon-button>
				</span>

				<span id="aonMobileMenuCamera"  style="top: 10px; position: relative; color:red;">
					<aon-icon-button id="aonMobileMenuCameraButton" icon="camera_alt"></aon-icon-button>
				</span>
				<input
					type="file"
					accept="image/*"
					capture="camera"
					id="${this.CAMERA_INPUT}"
					hidden
				  />
			</div>
			<aon-dialog-menu id="aonMobileMenuComunicaaonDialogAddOption" ></aon-dialog-menu>
			<div id="aonMobileMenuLoading" class="aonLoading"></div>
		`;
    // let rp = this.getElement('rootPanel');
    // let aonMenuSidenav = this.getElement('aonMobileMenuSidenav');

    let n = (window.innerWidth / 5 - 40) / 2;

    let aonMobileMenuDocumental = this.getElement("aonMobileMenuDocumental");
    aonMobileMenuDocumental.style.marginRight = n;
    aonMobileMenuDocumental.addEventListener("click", () => {
      rootPanel("<aon-documental></aon-documental>");
    });

    let aonMobileMenuAdd = this.getElement("aonMobileMenuAdd");
    aonMobileMenuAdd.style.marginLeft = n;
    aonMobileMenuAdd.style.marginRight = n;
    aonMobileMenuAdd.addEventListener("click", () => {
      rootPanel("<aon-signin></aon-signin>");
    });

    let aonMobileMenuInvoice = this.getElement("aonMobileMenuInvoice");
    aonMobileMenuInvoice.style.marginLeft = n;
    aonMobileMenuInvoice.style.marginRight = n;

    let aonMobileMenuInvoiceButton = this.getElement(
      "aonMobileMenuInvoiceButton"
    );
    aonMobileMenuInvoiceButton.addEventListener("click", () => {
      rootPanel("<aon-invoice-panel></aon-invoice-panel>");
    });

    let aonMobileMenuComunica = this.getElement("aonMobileMenuComunica");
    aonMobileMenuComunica.style.marginLeft = n;
    aonMobileMenuComunica.style.marginRight = n;
    aonMobileMenuComunica.addEventListener("click", () => {
      rootPanel("<aon-comunica></aon-comunica>");
    });

    let aonMobileMenuCamera = this.getElement("aonMobileMenuCamera");
    aonMobileMenuCamera.style.marginLeft = n;

    let aonMobileMenuCameraButton = this.getElement(
      "aonMobileMenuCameraButton"
    );
    aonMobileMenuCameraButton.addEventListener("click", ({ target }) => {
      let top = target.getBoundingClientRect().top;
      const left = target.getBoundingClientRect().left;
      const height = window.innerHeight;

      if (height - top < height / 2) top = top - 120;

      let d = this.getElement(`${aonMobileMenuComunica.id}aonDialogAddOption`);

      let options = [
        {
          name: "Documental",
          icon: "snippet_folder",
          fn: () => this.openCamera("document"),
        },
        {
          name: "Solicitudes",
          icon: "assignment",
          fn: () => this.openCamera("solicitud"),
        },
        {
          name: "Facturas",
          icon: "receipt",
          fn: () => this.openCamera("invoice"),
        },
      ];
      d.setMenuOptions(options, top, left);
      d.open();
    });

    this.getElement(this.CAMERA_INPUT).addEventListener("change", (ev) =>
      this.changeImage(ev)
    );
  }

  async openCamera(type) {
    // this.TYPE_IMG = type;
    // const isApp = await actionMobile({ action: "camera" });
    // if (!isApp) this.getElement(this.CAMERA_INPUT).click();
  }

  async changeImage({ target }) {
    // const {
    //   files: [file],
    // } = target;
    // const archivo = await getReader(file).catch((e) => null);
    // if (archivo) this.sendImage(archivo);
  }

  async sendImage(file) {
    // if (file.contentType.indexOf("image") >= 0) {
    //   //compress 500kB / file, 500kb, quality default 0.9, maxResolution 1280
    //   file = await downscaleImage(file, undefined, undefined, undefined);
    // }

    // switch (this.TYPE_IMG) {
    //   case "document":
    //     this.attachDocument(file);
    //     break;
    //   case "invoice":
    //     this.attachInvoice(file);
    //     break;
    //   case "solicitud":
    //     break;
    //   default:
    //     break;
    // }
  }

  async attachDocument(file) {
    this.loading(true);
    const data = {
      ...file,
      contentName: file.name,
      contentSize: file.size,
    };
    await uploadFileDocumental(data).catch((e) => null);
    await rootPanel(`<aon-documental />`);
    this.loading(false);
  }

  async attachInvoice(file) {
    this.loading(true);
    const data = {
      file,
      invoice: new Invoice("recibida"),
    };
    const result = await insertInvoice(data).catch((e) => null);
    await rootPanel(`<aon-invoice-panel />`);
    if (result) {
      let iEl = document.querySelector(`aon-invoice-panel`);
      if (iEl) iEl.aonInvoice(result.type, result);
    }
    this.loading(false);
  }

  loading(load) {
    let block = load ? "block" : "none";
    let aonEl = document.querySelector("#aonMobileMenuLoading");
    aonEl.style.display = block;
  }
}

window.customElements.define("aon-mobile-menu", AonMobileMenu);
