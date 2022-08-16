import { AonElement } from "../components/AonElement.js";
import { AonIconButton } from "../components/aon-icon-button.js";
import {DomainUserRoles} from '../models/DomainUserRoles.js';
import {AonDialogMenu} from "../components/aon-dialog-menu.js";
import {AonDialogMobile} from "../components/aon-dialog-mobile.js";
import { waitEl } from "../services/utils.js";
import { MOBILE_ACTION, mobileAction, closeSession, getDomainUserRoles, uploadFileDocumental } from "../services/service.js";
import { CONSTANT, EVENT, MATERIAL_ICONS, TAG } from '../environments/environments.js';
import * as LS from '../services/localStorageService.js';
import { AonNotification } from "./notification/aon-notification.js";
import { AonApps } from "./aon-apps.js";
import { AonNotificationIcon } from "./notification/aon-notification-icon.js";
import { uploadInvoice, uploadInvoices } from "./invoice/InvoiceUtils.js";
import { uploadDocuments } from "./documental/DocumentalUtils.js";
import { AonDialog } from "../components/aon-dialog.js";
import { AonInvoicePanel } from "./invoice/aon-invoice-panel.js";
import { AonMessenger } from "./messenger/aon-messenger.js";
import { TASK_SOURCE } from "./messenger/MessengerEnums.js";
import { AonDocumental } from "./documental/aon-documental.js";

export class AonNewMobileMenu extends AonElement {

  dur;
  SELECTED;
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
  }

  build() {
    this.innerHTML = `
      <input id='${this.INPUT_INVOICE_FILE}' style='display:none;' type='file' name='file' multiple>
      <input id='${this.INPUT_DOCUMENT_FILE}' style='display:none;' type='file' name='file' multiple>
      <input id='${this.INPUT_CAMERA}' type='file' accept='image/*' capture='camera' hidden />
    `;
    let inputInvoiceFile = this.getElement(this.INPUT_INVOICE_FILE);
		inputInvoiceFile.addEventListener(EVENT.CHANGE, ({target}) => {
      uploadInvoices(inputInvoiceFile, target.files).then(invoices =>  {
        if(invoices && invoices.length>0) 
          this.goInvoice(invoices[0]);
      });
    });

    let inputDocumentFile = this.getElement(this.INPUT_DOCUMENT_FILE);
    inputDocumentFile.addEventListener(EVENT.CHANGE, ({target}) => {
      uploadDocuments(inputDocumentFile, target.files, this.getDur());
    });
  	
    let inputCamera = this.getElement(this.INPUT_CAMERA);
    inputCamera.addEventListener(EVENT.CHANGE,  ({target}) =>{
      const files = target.files;
      if(this.SELECTED == "documental"){
        this.saveDocumentFile(files[0]);
      } else {
        uploadInvoices(inputCamera, files).then(invoices => {
          if(invoices && invoices.length>0) 
            this.goInvoice(invoices[0]);
        });
      }
    });

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

  setMarginById(id, value){
    let elem = this.getElement(id);
    elem.style.marginLeft = value;
    elem.style.marginRight = value;
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
  
    getDomainUserRoles({}).then(r => {
      this.dur = new DomainUserRoles(r);
    });


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
      icon: MATERIAL_ICONS.ADD,
      name: 'Add',
      color: 'white',
      background: '#002469',
      fn: () => {
        // if(LS.getCompany() && btnAdd.icon)
        //   btnAdd.icon = MATERIAL_ICONS.CLOSE;
        this.add();
      }
    });

    this.addMenuButton({
      name: 'Notification',
      icon: 'notifications',
      fn: () => this.notification()
    });

    // this.addMenuButton({
    //   name: 'User',
    //   icon: 'person',
    //   fn: () => this.user()
    // });

    this.addMenuButton({
      name: 'Exit',
      icon: 'input',
      fn: () => this.closeSession()
    });
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

  newButtons() {
    let dialogId = "menuMobile";
    let dialog = this.getElement(dialogId) ||  new AonDialogMobile(); 
    dialog.id = dialogId;
    this.clearElement(dialog);
    this.appendChild(dialog);
    dialog.clear();

    dialog.setTitle("Acceso Rápido");
    dialog.open();

    const isInvoice = this.getDur().isInvoice();
    const isMessenger = this.getDur().isMessenger();
    const isDocumentalManager = this.getDur().isDocumentalPortal() || this.getDur().isDocumentalManager();

    let buttons = [
      {
        title:"Nueva factura",
        icon: 'add',
        permission: isInvoice,
        backgroundColor: "#4472C4",
        fn :  () => {
          if(isInvoice){
            dialog.close();
            let aonComponent = new AonInvoicePanel();
            aonComponent.invoice = {type:"emitida"};
            this.rootPanel(aonComponent);
          }
        }
      },
      {
        title:"Subir factura",
        icon: 'upload',
        permission: isInvoice,
        backgroundColor: "#4472C4",
        fn :  (ev) => {
          if(isInvoice){
            dialog.close();
            ev.preventDefault();
            this.addInvoiceFile();
          }
        }
      },
      {
        title:"Foto factura",
        icon: 'photo_camera',
        permission: isInvoice,
        backgroundColor: "#4472C4",
        fn :  () => {
          if(isInvoice){
            dialog.close();
            this.openCamera("invoice");
          }
        }
      },
      {
        title:"Nueva solicitud",
        icon: 'add',
        permission: isMessenger,
        backgroundColor: "#1fd8b9",
        fn :  () => {
          if(isMessenger){
            dialog.close();
            let aonComponent = new AonMessenger();	
            aonComponent.data = {source:TASK_SOURCE.QUERY};
            this.rootPanel(aonComponent);
          }
        }
      },
      {
        icon: 'upload',
        title:"Subir documento",
        permission: isDocumentalManager,
        backgroundColor: "#6986BB",
        fn :  () => {
          if(isDocumentalManager){
            dialog.close();
            this.addDocumentFile();
          }
        }
      },
      {
        title:"Foto documento",
        icon: 'photo_camera',
        permission: isDocumentalManager,
        backgroundColor: "#6986BB",
        fn :  () => {
          if(isDocumentalManager){
            dialog.close();
            this.openCamera("documental");
          }
        }
      },
    ];

    dialog.addButtons(buttons);
  }


  add() {
    if(LS.getCompany()) {
      getDomainUserRoles({}).then(r => {
        this.dur = new DomainUserRoles(r);
        this.newButtons();
        let aonHeader = this.getElement("aonHeader");
        aonHeader.companyIn();
      });
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

  goInvoice(invoice){
    if(invoice){
      let aonComponent = new AonInvoicePanel();
      aonComponent.invoice = invoice;
      this.rootPanel(aonComponent);
    }
  }

  goDocumental(file){
    if(file && file.id){
      let aonComponent = new AonDocumental();
      aonComponent.value = file.id;
      this.rootPanel(aonComponent);
    }
  }

  closeSession() {
    const idDialog = "dialogCloseSesion";
    let d = this.getElement(idDialog);
    if(!d){
      d = new AonDialog();
      d.id = idDialog;
      this.appendChild(d);
    }
    d.clear();
    if(!this.isMobile()) d.width = '400px';
    d.setTitle('Cerrar Sesión');
    d.setContentHTML(`Estás seguro de cerrar sesión`);
    d.addAcceptAction(() => closeSession());
    d.open();
  }

	async openCamera(type) {
    this.SELECTED = type;
		const isApp = await mobileAction({ action: MOBILE_ACTION.CAMERA, id: this.INPUT_CAMERA, selector: 'aon-new-mobile-menu' });
		if (!isApp) this.getElement(this.INPUT_CAMERA).click();
	}

  async openBarcode() {
    await mobileAction({ action: MOBILE_ACTION.BARCODE, selector: 'aon-new-mobile-menu' });
	}

  receiveAppImage(file) {
    if(this.SELECTED == "documental"){
      this.saveDocumentFile(file);
    } else {
      uploadInvoice(file).then(f=>{
        this.goInvoice(f);
        this.showMessage("Factura registrada");
      });
    }
	}

  setBarcodeData(barcodeStr) {
    const barcode = JSON.parse(barcodeStr);
    alert("test: "+ JSON.stringify(barcode))
	}

  saveDocumentFile(file){
    let type = this.getDur().isDocumentalManager() || this.getDur().isDocumentalPortal()  ? 'enterprise' : 'employee';
    const data = {
      ...file,
      type,
      contentName: file.name,
      contentSize: file.size
    };
    uploadFileDocumental(data).then((f)=>{
      this.goDocumental(f);
      this.showMessage("Documento registrado");
    })
  }

  addDocumentFile() {
    if(LS.getDomainName()) 
      this.getElement(this.INPUT_DOCUMENT_FILE).click();
    else alert("Selecciona un empresa");
	}

	addInvoiceFile() {
		this.getElement(this.INPUT_INVOICE_FILE).click();
	}
}

window.customElements.define("aon-new-mobile-menu", AonNewMobileMenu);
