import {AonElement} from '../components/AonElement.js';
import {rootPanel} from '../services/gwtLoader.js';

import '../components/aon-icon.js'

import {getReader} from '../services/utils.js'

import { uploadFile } from '../services/fileService.js'

export class AonMobileMenu extends AonElement {
	CAMERA_INPUT;
	get id() {
		return this.getAttribute('id');
	}

	set id(id) {
		this.setAttribute('id', id);
	}

	get opened() {
		return this.getAttribute('opened');
	}

	set opened(opened) {
		this.setAttribute('opened', opened);
	}

	get company() {
		return this.getAttribute('company');
	}

	set company(company) {
		this.setAttribute('company', company);
	}

	get user() {
		return this.getAttribute('user');
	}

	set user(user) {
		this.setAttribute('user', user);
	}

	constructor () {
		super();
		this.CAMERA_INPUT =  this.getAttribute('id') + 'CameraInput';
	}

	connectedCallback () {
		this.build();
  	}

	build() {
		this.innerHTML = `
			<div id="aonMobileMenuSidenav" class="aonMobileMenuSidenav">
				<span id="aonMobileMenuHome"  style="top: 10px; position: relative;">
					<aon-icon-button id="aonMobileMenuHomeButton" icon="home"></aon-icon-button>
				</span>

				<span id="aonMobileMenuDocumental"  style="top: 10px; position: relative;">
					<aon-icon-button id="aonMobileMenuDocumentalButton" icon="attach_file"></aon-icon-button>
				</span>

				<span id="aonMobileMenuAdd"  style="top: 10px; position: relative;">
					<aon-icon-button id="aonMobileMenuAddButton" icon="add"></aon-icon-button>
				</span>

				<span id="aonMobileMenuTime" style="top: 10px; position: relative;">
					<aon-icon-button id="aonMobileMenuTimeButton" icon="access_time"></aon-icon-button>
				</span>

				<span id="aonMobileMenuInvoice"  style="top: 10px; position: relative;">
					<aon-icon-button id="aonMobileMenuInvoiceButton" icon="receipt"></aon-icon-button>
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
		`;
		let rp = this.getElement('rootPanel');
		let aonMenuSidenav = this.getElement('aonMobileMenuSidenav');

		if(this.getAttribute('opened')) {
			rp.className = 'rootMobilePanel';
			aonMenuSidenav.style.height = '60px';
			rp.style.marginBottom = '60px';
		} else {
			rp.className = 'rootPanel';
			aonMenuSidenav.style.height = '0px';
			rp.style.marginBottom = '0px';
		}
		let n = ((window.innerWidth / 6) - 40) / 2;

		let aonMobileMenuHome = this.getElement('aonMobileMenuHome');
		aonMobileMenuHome.style.marginRight = n;

		let aonMobileMenuDocumental = this.getElement('aonMobileMenuDocumental');
		aonMobileMenuDocumental.style.marginLeft = n;
		aonMobileMenuDocumental.style.marginRight = n;

		let aonMobileMenuAdd = this.getElement('aonMobileMenuAdd');
		aonMobileMenuAdd.style.marginLeft = n;
		aonMobileMenuAdd.style.marginRight = n;

		let aonMobileMenuTime = this.getElement('aonMobileMenuTime');
		aonMobileMenuTime.style.marginLeft = n;
		aonMobileMenuTime.style.marginRight = n;

		let aonMobileMenuInvoice = this.getElement('aonMobileMenuInvoice');
		aonMobileMenuInvoice.style.marginLeft = n;
		aonMobileMenuInvoice.style.marginRight = n;


		let aonMobileMenuInvoiceButton = this.getElement('aonMobileMenuInvoiceButton');
		aonMobileMenuInvoiceButton.addEventListener('click', () => {
			rootPanel('<aon-invoice-panel></aon-invoice-panel>');
		});

		let aonMobileMenuCameraButton = this.getElement('aonMobileMenuCameraButton');
		aonMobileMenuCameraButton.addEventListener('click',() => this.openCamera());

		this.getElement(this.CAMERA_INPUT).addEventListener('change', (ev)=> this.sendImage(ev) );
	}


	open() {
		let aonMenuSidenav = this.getElement('aonMobileMenuSidenav');
		let rootPanel = this.getElement('rootPanel');
		rootPanel.className = 'rootMobilePanel';
		aonMenuSidenav.style.height = '60px';
		rootPanel.style.marginBottom = '60px';
		this.setAttribute('opened', true);
	}

	close() {
		let aonMenuSidenav = this.getElement('aonMobileMenuSidenav');
		let rootPanel = this.getElement('rootPanel');
		rootPanel.className = 'rootPanel';
		aonMenuSidenav.style.height = '0px';
		rootPanel.style.marginBottom = '0px';

		this.removeAttribute('opened');
	}


	openCamera(){
	    if(!webkit.messageHandlers.cordova_iab) throw "Cordova IAB postMessage API not found!";
	    webkit.messageHandlers.cordova_iab.postMessage(JSON.stringify({action:"camera"}));
	}

	async sendImage({target}){
		try {
			const {files: [file]} = target;
			const archivo = await getReader(file);
			const resp = await uploadFile({ file: archivo });
			console.log("archivo guardado!", resp);
		} catch (error) {
			console.log(error);
		}
	}
}

window.customElements.define('aon-mobile-menu', AonMobileMenu);
