import { AonElement } from "../components/AonElement.js";
import { rootPanel } from "../services/gwtLoader.js";
import { getReader } from "../services/utils.js";
import {
  uploadFileDocumental,
  insertInvoice,
  actionMobile,
} from "../services/service.js";
import { Invoice } from "./invoice/Invoice.js";
import "../components/aon-icon-button.js";
import "./comunic@/aon-comunica.js";
import "./documental/aon-documental.js";
import "./signin/aon-signin.js";
import "./invoice/aon-invoice-panel.js";
import "../components/aon-dialog-menu.js";

export class AonMobileMenu extends AonElement {
  CAMERA_INPUT;
  TYPE_IMG;
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
    this.CAMERA_INPUT = this.id + "CameraInput";
  }

  connectedCallback() {
    this.build();
  }

  build() {
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
					id="aonMobileMenuCameraInput"
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
    this.TYPE_IMG = type;
    const isApp = await actionMobile({ action: "camera" });
    if (!isApp) this.getElement("aonMobileMenuCameraInput").click();
  }

  async changeImage({ target }) {
    const {
      files: [file],
    } = target;
    const archivo = await getReader(file).catch((e) => null);
    if (archivo) this.sendImage(archivo);
  }

  sendImage(fileObj) {
    switch (this.TYPE_IMG) {
      case "document":
        this.attachDocument(fileObj);
        break;
      case "invoice":
        this.attachInvoice(fileObj);
        break;
      case "solicitud":
        break;
      default:
        break;
    }
  }

  async attachDocument(file) {
    this.loading(true);
    const data = {
      ...file,
      contentName: file.name,
      contentSize: file.size,
    };
    const result = await uploadFileDocumental(data).catch((e) => null);
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

  async sendTokenFCM(token) {
    console.log("tokenFCM>", typeof token, token);
  }

  loading(load) {
    let block = load ? "block" : "none";
    let aonEl = document.querySelector("#aonMobileMenuLoading");
    aonEl.style.display = block;
  }
}

window.customElements.define("aon-mobile-menu", AonMobileMenu);
