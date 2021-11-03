import { AonElement } from "../components/AonElement.js";
import { AonIconButton } from "../components/aon-icon-button.js";
import {DomainUserRoles} from '../models/DomainUserRoles.js';
import {AonDialogMenu} from "../components/aon-dialog-menu.js";
import { waitEl } from "../services/utils.js";
import { MOBILE_ACTION, mobileAction, closeSession, getDomainUserRoles } from "../services/service.js";
import { CONSTANT, EVENT, MATERIAL_ICONS, TAG } from '../environments/environments.js';
import * as LS from '../services/localStorageService.js';
import { AonNotification } from "./notification/aon-notification.js";
import { AonApps } from "./aon-apps.js";
import { AonNotificationIcon } from "./notification/aon-notification-icon.js";
// import { AonMobileProfile } from "./user/aon-mobile-profile.js";
import { uploadInvoice, uploadInvoices } from "./invoice/InvoiceUtils.js";
import { uploadDocuments } from "./documental/DocumentalUtils.js";
import { AonDialog } from "../components/aon-dialog.js";

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
      this.resize();
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
    this.INPUT_INVOICE_FILE = this.id + 'InputInvoiceFile';
    this.INPUT_DOCUMENT_FILE = this.id + 'InputDocumentFile';
    this.INPUT_CAMERA = this.id + 'InputCamera';
  }

  build() {
    this.innerHTML = `
      <input id='${this.INPUT_INVOICE_FILE}' style='display:none;' type='file' name='file' multiple>
      <input id='${this.INPUT_DOCUMENT_FILE}' style='display:none;' type='file' name='file' multiple>
      <input id='${this.INPUT_CAMERA}' type='file' accept='image/*' capture='camera' hidden />
    `;
    let inputInvoiceFile = this.getElement(this.INPUT_INVOICE_FILE);
		inputInvoiceFile.addEventListener('change', ({target}) => uploadInvoices(inputInvoiceFile, target.files));

    let inputDocumentFile = this.getElement(this.INPUT_DOCUMENT_FILE);
    inputDocumentFile.addEventListener('change', ({target}) => uploadDocuments(inputDocumentFile, target.files, this.getDur()));
  	
    let inputCamera = this.getElement(this.INPUT_CAMERA);
    inputCamera.addEventListener('change',  ({target}) => uploadInvoices(inputCamera, target.files));

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

  resize() {
    let n = (window.innerWidth / 5 - 40) / 2;
    
    this.getElement('aonMobileMenuHome').style.marginLeft = n;
    this.getElement('aonMobileMenuHome').style.marginRight = n;

    this.getElement('aonMobileMenuApps').style.marginLeft = n;
    this.getElement('aonMobileMenuApps').style.marginRight = n;

    this.getElement('aonMobileMenuAdd').style.marginLeft = n;
    this.getElement('aonMobileMenuAdd').style.marginRight = n;

    this.getElement('aonMobileMenuNotification').style.marginLeft = n;
    this.getElement('aonMobileMenuNotification').style.marginRight = n;

    this.getElement('aonMobileMenuExit').style.marginLeft = n;
    this.getElement('aonMobileMenuExit').style.marginRight = n;
    

    let n1 = (window.innerWidth / 2) - 95;
    this.getElement('proba').style.left = n1 + 'px';

    let n2 = (window.innerWidth / 2) - 45;
    this.getElement('proba2').style.left = n2 + 'px';

    let n3 = (window.innerWidth / 2) + 5;
    this.getElement('proba3').style.left = n3 + 'px';

    let n4 = (window.innerWidth / 2) +55;
    this.getElement('proba4').style.left = n4 + 'px';
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
      this.newButtons();
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

    let btnAdd = this.addMenuButton({
      icon: MATERIAL_ICONS.ADD,
      name: 'Add',
      color: 'white',
      background: '#002469',
      fn: () => {
        if(btnAdd.icon)
          btnAdd.icon = MATERIAL_ICONS.CLOSE;
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
    let div = this.getElement('probaDiv') || this.createElement(TAG.DIV);
    div.id = 'probaDiv';
    div.className = 'aonDialog';
    div.style.backgroundColor = 'transparent';
    div.style.display = 'none';
    div.addEventListener(EVENT.CLICK, () => {
      div.style.display = 'none';
      let btnAdd = this.getElement("aonMobileMenuAddButton");
      if(btnAdd.icon)
        btnAdd.icon = MATERIAL_ICONS.ADD;
    });
    this.appendChild(div);

    let proba = this.getElement('proba') || new  AonIconButton();
    proba.id = 'proba';
    proba.icon = 'receipt';
    proba.color = 'white';
    proba.noHover = true;
    proba.background = this.getDur().isInvoice() ? '#002469' : '#bbb';
    proba.style.position = 'absolute'
    proba.style.bottom = '65px';
    proba.style.opacity = '0.75';
    proba.style.transitionDuration = '2000ms';
    let n = (window.innerWidth / 2) - 95;
    proba.style.left = n + 'px';
    if(this.getDur().isInvoice()) 
      proba.addEventListener(EVENT.CLICK, () => this.addInvoiceFile());
    div.appendChild(proba);

    let proba2 = this.getElement('proba2') || new  AonIconButton();
    proba2.id = 'proba2';
    proba2.icon = 'description';
    proba2.color = 'white';
    proba2.noHover = true;
    proba2.background = this.getDur().isDocumental() ? '#002469' : '#bbb';
    proba2.style.position = 'absolute'
    proba2.style.bottom = '65px';
    proba2.style.opacity = '0.75';
    proba2.style.transitionDuration = '2000ms';
    let n2 = (window.innerWidth / 2) - 45;
    proba2.style.left = n2 + 'px';
    if(this.getDur().isDocumental())
      proba2.addEventListener(EVENT.CLICK, () => this.addDocumentFile());
    div.appendChild(proba2);


    let proba3 = this.getElement('proba3') || new  AonIconButton();
    proba3.id = 'proba3';
    proba3.icon = 'message';
    proba3.color = 'white';
    proba3.noHover = true;
    proba3.background = '#002469';
    proba3.style.position = 'absolute'
    proba3.style.bottom = '65px';
    proba3.style.opacity = '0.75';
    proba3.style.transitionDuration = '2000ms';
    let n3 = (window.innerWidth / 2) + 5;
    proba3.style.left = n3 + 'px';
    proba3.addEventListener(EVENT.CLICK, () => {
      alert('En Desarrollo');
    });
    div.appendChild(proba3);

    let proba4 = this.getElement('proba4') || new  AonIconButton();
    proba4.id = 'proba4';
    proba4.icon = 'photo_camera';
    proba4.color = 'white';
    proba4.noHover = true;
    proba4.background = this.getDur().isInvoice() ? '#002469' : '#bbb';;
    proba4.style.position = 'absolute'
    proba4.style.bottom = '65px';
    proba4.style.opacity = '0.75';
    proba4.style.transitionDuration = '2000ms';
    let n4 = (window.innerWidth / 2) +55;
    proba4.style.left = n4 + 'px';
    if(this.getDur().isInvoice()) 
      proba4.addEventListener(EVENT.CLICK, () => this.openCamera());
    div.appendChild(proba4);
  }

  add() {
    const dialog = this.getElement('probaDiv');
    if(dialog) 
      dialog.style.display = 'block';
    if(LS.getCompany()) {
      let aonHeader = this.getElement("aonHeader");
      aonHeader.companyIn();
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

  // user() {
  //   if(LS.getCompany()) {
  //     let aonHeader = this.getElement("aonHeader");
  //     aonHeader.companyIn();
  //   }
  //   this.rootPanel(new AonMobileProfile());
  // }

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


	async openCamera() {
		const isApp = await mobileAction({ action: MOBILE_ACTION.CAMERA, id: this.INPUT_CAMERA, selector: 'aon-new-mobile-menu' });
		if (!isApp) this.getElement(this.INPUT_CAMERA).click();
	}

  async receiveAppImage(file) {
    uploadInvoice(file);
	}


  addDocumentFile() {
    if(LS.getDomainName()) 
      this.getElement(this.INPUT_DOCUMENT_FILE).click();
    else alert("Selecciona un empresa");
	}

  addMessenger() {
    alert('En Desarrollo');
  }

	addInvoiceFile() {
		this.getElement(this.INPUT_INVOICE_FILE).click();
	}
}

window.customElements.define("aon-new-mobile-menu", AonNewMobileMenu);
