import { AonElement } from "../components/AonElement.js";
import { AonIconButton } from "../components/aon-icon-button.js";
import {DomainUserRoles} from '../models/DomainUserRoles.js';
import {AonDialogMenu} from "../components/aon-dialog-menu.js";
import { getReader, waitEl } from "../services/utils.js";
import { normalizeImageFile, PDF_OR_IMAGE_ACCEPT } from "../services/imageFileService.js";
import { MOBILE_ACTION, mobileAction, closeSession, getDomainUserRoles, uploadFileDocumental } from "../services/service.js";
import { CONSTANT, EVENT, MATERIAL_ICONS, MSG, TAG } from '../environments/environments.js';
import * as LS from '../services/localStorageService.js';
import * as UA from '../services/userAgentService.js';

import { AonNotification } from "./notification/aon-notification.js";
import { AonApps } from "./aon-apps.js";
import { AonNotificationIcon } from "./notification/aon-notification-icon.js";
import { generateJobId, uploadInvoices } from "./invoice/InvoiceUtils.js";
import { uploadDocuments } from "./documental/DocumentalUtils.js";
import { AonDialog } from "../components/aon-dialog.js";
import { AonInvoicePanel } from "./invoice/aon-invoice-panel.js";
import { AonMessenger } from "./messenger/aon-messenger.js";
import { TASK_SOURCE } from "./messenger/MessengerEnums.js";
import { AonDocumental } from "./documental/aon-documental.js";
import { AonWarehouse } from "./warehouse/aon-warehouse.js";
import { openCamera, openBarcode } from "../services/actionService.js";
import { AonImageEditor } from "../components/aon-image-editor.js";

import * as WAREHOUSE_OPTION from './warehouse/WarehouseOptions.js';
import { AonUploadToast } from "../components/aon-upload-toast.js";
import { AonMobileHome } from "./home/aon-mobile-home.js";
import { AonMobileDesktop } from "./company/aon-mobile-desktop.js";
import { AonMobileProfile } from "./user/aon-mobile-profile.js";
import { AonNewInput } from "../components/aon-new-input.js";
import { AonSelect } from "../components/aon-select.js";
import { createSelect } from "../components/CreateComponent.js";

export class AonMobileMenu extends AonElement {

  dur;
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
    this.innerHTML = `
      <input id='${this.INPUT_INVOICE_FILE}' style='display:none;' type='file' name='file' accept='${PDF_OR_IMAGE_ACCEPT}' multiple>
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
          // El HEIC hay que pasarlo a JPEG antes del editor: Cropper tampoco sabe pintarlo.
          normalizeImageFile(files[0])
            .then(file => getReader(file))
            .then(file => this.buildInvoiceImageEditor(file))
            .catch(()=>null);
      }
    });

    const id = this.id + 'Sidenav';
    const sidEl = this.getElement(id);
    if(sidEl)sidEl.remove();
    let div = this.createElement(TAG.DIV);
    div.id = id;
    div.className = 'aonMobileMenu';
    if(this.isIosSab()) {
      div.style.height = '4rem'; 
    }
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

    // this.addMenuButton({
    //   name: 'Apps',
    //   icon: 'apps',
    //   fn: () => this.apps()
    // });

   this.addMenuButton({
      icon: MATERIAL_ICONS.ADD,
      name: 'Add',
      color: 'white',
      background: '#0f172a',
      fn: () => {
        this.add();
      }
    });

    // this.addMenuButton({
    //   name: 'Notification',
    //   icon: 'notifications',
    //   fn: () => this.notification()
    // });

    // this.addMenuButton({
    //   name: 'Exit',
    //   icon: MATERIAL_ICONS.LOGOUT,
    //   fn: () => this.closeSession()
    // });

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
      this.rootPanel(new AonMobileHome());
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

  user() {
  	const aonMobileProfile = new AonMobileProfile();
		aonMobileProfile.id = CONSTANT.AON_MOBILE_PROFILE;
	  this.rootPanel(aonMobileProfile);
  }

  newButtons() {
    let dialog = this.getDialogMenu();
    dialog.clear();

    dialog.setTitle(MSG.QUICK_ACCESS_TITLE);
    dialog.open();

    const isInvoice = this.getDur().isInvoice();
    const isMessenger = this.getDur().isMessenger();
    const isDocumentalManager = this.getDur().isDocumentalPortal() || this.getDur().isDocumentalManager();
    const isUdapa = this.getDur().getDomain().getName().includes("udapa") || this.getDur().getDomain().getName().includes("paturpat");


    const newInvoice = {
      title: MSG.NEW_INVOICE,
      icon: CONSTANT.ADD,
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
    };

    const uploadInvoice = {
      title: MSG.UPLOAD_INVOICE,
      icon: CONSTANT.UPLOAD,
      permission: isInvoice,
      backgroundColor: "#4472C4",
      fn :  (ev) => {
        if(isInvoice){
          dialog.close();
          this.addInvoiceFile();
        }
      }
    };

    const photoInvoice = {
      title: MSG.PHOTO_INVOICE,
      icon: 'photo_camera',
      permission: isInvoice,
      backgroundColor: "#4472C4",
      fn :  () => {
        if(isInvoice){
          dialog.close();
          if(UA.isApp()) {
            this.SELECTED = "invoice";
            let ionicData = { action: MOBILE_ACTION.CAMERA, id: this.INPUT_CAMERA, selector: 'aon-new-mobile-menu' };
            openCamera(ionicData, (result) => {
              this.buildInvoiceImageEditor(result);
            });
          } else this.openCamera("invoice");
        }
      }
    };

    const newMessenger = {
      title: MSG.NEW_REQUEST,
      icon: CONSTANT.ADD,
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
    };

    const uploadDocument = {
      icon: 'upload',
      title: MSG.UPLOAD_DOCUMENT,
      permission: isDocumentalManager,
      backgroundColor: "#6986BB",
      fn :  () => {
        if(isDocumentalManager){
          dialog.close();
          this.addDocumentFile();
        }
      }
    }

    const photoDocument = {
      title: MSG.PHOTO_DOCUMENT,
      icon: 'photo_camera',
      permission: isDocumentalManager,
      backgroundColor: "#6986BB",
      fn :  () => {
        if(isDocumentalManager) {
          dialog.close();
          if(UA.isApp()) {
            this.SELECTED = "documental";
            let ionicData = { action: MOBILE_ACTION.CAMERA, id: this.INPUT_CAMERA, selector: 'aon-new-mobile-menu' };
            openCamera(ionicData, (result) => {
              this.buildInvoiceImageEditor(result);
            });
          } else this.openCamera("documental");
        }
      }
    };

    const newPackaging = {
      title: MSG.NEW_PACKAGING,
      icon: 'add',
      permission: isUdapa,
      backgroundColor: "#002469",
      fn :  () => {
        if(isUdapa){
          dialog.close();
          let aonComponent = new AonWarehouse();
          aonComponent.setOption(WAREHOUSE_OPTION.PACKAGING);
          this.rootPanel(aonComponent);
        }
      }
    };

    const deliveryPreparation = {
      title: MSG.DELIVERY_PREPARATION,
      icon: MATERIAL_ICONS.QR_CODE_SCANNER,
      permission: isUdapa,
      backgroundColor: "#002469",
      fn :  () => {
        if(isUdapa){
          dialog.close();
          this.openBarcode();
        }
      }
    };

    const searchPackage = {
      title: MSG.SEARCH_PACKAGES,
      icon: MATERIAL_ICONS.QR_CODE_SCANNER,
      permission: isUdapa,
      backgroundColor: "#002469",
      fn :  () => {
        if(isUdapa){
          dialog.close();
          let aonComponent = new AonWarehouse();
          aonComponent.setOption(WAREHOUSE_OPTION.PACKAGE);
          this.rootPanel(aonComponent);
        }
      }
    };
    
    let buttons = [newInvoice, uploadInvoice, photoInvoice, newMessenger, uploadDocument, photoDocument];
    if(isUdapa) {
      buttons = [newInvoice, uploadInvoice, photoInvoice, newPackaging, deliveryPreparation, searchPackage];
    }
    dialog.addButtons(buttons);
  }

  openBarcode() {
    let ionicData = { action: MOBILE_ACTION.BARCODE, selector: TAG.AON_MOBILE_MENU };
    if(UA.isAndroidApp()) {
      openBarcode(ionicData, (result) => this.setBarcodeAction(result.code));
    } else mobileAction(ionicData);
	}

	setBarcodeData(barcodeStr) {
		try {
			if(typeof barcodeStr === 'string') {
				barcodeStr = JSON.parse(barcodeStr);
			}

			const {text, format, cancelled} = barcodeStr;
			if(!cancelled) {
        this.setBarcodeAction(text);
			}
		} catch (error) {
			this.showError(error);
		}
	}

  setBarcodeAction(code) {
    console.log("delivery: " + code);

    let aonComponent = new  AonWarehouse();
    let option = WAREHOUSE_OPTION.DELIVERY;
    option.delivery = code;
    aonComponent.setOption(option);
    this.rootPanel(aonComponent);
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
    d.setTitle(MSG.CLOSE_SESSION);
    d.setContentHTML(`Estás seguro de cerrar sesión`);
    d.addAcceptAction(() => closeSession());
    d.open();
  }

	async openCamera(type) {
    this.SELECTED = type;
    if(this.isBeta()) this.getElement(this.INPUT_CAMERA).click();
    else {
      const isApp = await mobileAction({ action: MOBILE_ACTION.CAMERA, id: this.INPUT_CAMERA, selector: 'aon-new-mobile-menu' });
		  if (!isApp) this.getElement(this.INPUT_CAMERA).click();
    }
	}

  receiveAppImage(file) {
    if(this.SELECTED == "documental"){
      this.saveDocumentFile(file);
    } else this.buildInvoiceImageEditor(file);
	}

  buildInvoiceImageEditor(file) {
    let editor = new AonImageEditor();
    editor.setImage(`data:${file.contentType || 'image/jpeg'};base64,` + file.content);
    editor.addEventListener(EVENT.CROPPER, (e) => {
      let uploadToast = this.getElement('aonUploadToast');
		  if(!uploadToast){ 
			  uploadToast = new AonUploadToast();
	  		this.appendChild(uploadToast);
  		}
		  let data = { uploaded: 0, prefix: 'CM' };
      uploadToast.setJobId(generateJobId()); 
			uploadToast.addFile(this.SELECTED, e.detail, data);

      this.home();
		});
    this.rootPanel(editor); 
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
if(!window.customElements.get(TAG.AON_MOBILE_MENU)){
  window.customElements.define(TAG.AON_MOBILE_MENU, AonMobileMenu);
}

