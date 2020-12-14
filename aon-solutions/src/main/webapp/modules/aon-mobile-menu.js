import {AonElement} from '../components/AonElement.js';
import {rootPanel} from '../services/gwtLoader.js';

import '../components/aon-icon-button.js';
import './comunic@/aon-comunica.js';
import './documental/aon-documental.js';
import './messenger/aon-messenger.js';
import './invoice/aon-invoice-panel.js';

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
		this.CAMERA_INPUT =  this.id + 'CameraInput';
	}

	connectedCallback () {
		this.build();
  	}

	build() {
		this.innerHTML = `
			<div id="aonMobileMenuSidenav" class="aonMobileMenu">

				<span id="aonMobileMenuDocumental"  style="top: 10px; position: relative;">
					<aon-icon-button id="aonMobileMenuDocumentalButton" icon="snippet_folder"></aon-icon-button>
				</span>

				<span id="aonMobileMenuAdd"  style="top: 10px; position: relative;">
					<aon-icon-button id="aonMobileMenuAddButton" icon="assignment"></aon-icon-button>
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
		`;
		let rp = this.getElement('rootPanel');
		let aonMenuSidenav = this.getElement('aonMobileMenuSidenav');

		let n = ((window.innerWidth / 5) - 40) / 2;

		let aonMobileMenuDocumental = this.getElement('aonMobileMenuDocumental');
		aonMobileMenuDocumental.style.marginRight = n;
		aonMobileMenuDocumental.addEventListener('click', () => {
			rootPanel('<aon-documental></aon-documental>');
		});

		let aonMobileMenuAdd = this.getElement('aonMobileMenuAdd');
		aonMobileMenuAdd.style.marginLeft = n;
		aonMobileMenuAdd.style.marginRight = n;
		aonMobileMenuAdd.addEventListener('click', () => {
			rootPanel('<aon-messenger></aon-messenger>');
		});

		let aonMobileMenuInvoice = this.getElement('aonMobileMenuInvoice');
		aonMobileMenuInvoice.style.marginLeft = n;
		aonMobileMenuInvoice.style.marginRight = n;

		let aonMobileMenuInvoiceButton = this.getElement('aonMobileMenuInvoiceButton');
		aonMobileMenuInvoiceButton.addEventListener('click', () => {
			rootPanel('<aon-invoice-panel></aon-invoice-panel>');
		});

		let aonMobileMenuComunica = this.getElement('aonMobileMenuComunica');
		aonMobileMenuComunica.style.marginLeft = n;
		aonMobileMenuComunica.style.marginRight = n;
		aonMobileMenuComunica.addEventListener('click', () => {
			rootPanel('<aon-comunica></aon-comunica>');
		});

		let aonMobileMenuCamera = this.getElement('aonMobileMenuCamera');
		aonMobileMenuCamera.style.marginLeft = n;

		let aonMobileMenuCameraButton = this.getElement('aonMobileMenuCameraButton');
		aonMobileMenuCameraButton.addEventListener('click',() => this.openCamera());

		this.getElement(this.CAMERA_INPUT).addEventListener('change', (ev)=> this.sendImage(ev) );
	}

	openCamera(){
		if("undefined" === typeof webkit) {
			this.getElement('aonMobileMenuCameraInput').click();
			return false;
		}
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
