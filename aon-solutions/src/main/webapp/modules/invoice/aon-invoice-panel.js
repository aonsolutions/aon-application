import {AonElement} from '../../components/AonElement.js';
import {AonApplication} from '../../components/aon-application.js';
import {insertInvoice, deleteInvoices, actionMobile} from '../../services/service.js';
import {Invoice} from './Invoice.js';
import {InvoiceAction} from './invoiceEnums.js';

import './aon-invoice.js';
import './aon-mobile-invoice.js';
import './aon-invoice-list.js';
import './aon-mobile-invoice-list.js';
import './aon-invoice-print.js';

import '../../components/aon-application.js';
import '../../components/aon-dialog-menu.js';

import * as MSG from "../../environments/msg.js";
import { downscaleImage } from '../../services/compressImg.js';
import { getReader } from '../../services/utils.js';

export class AonInvoicePanel extends AonElement {

	selected;
	_filter;

	INVOICE;
	INPUT_FILE;
	INPUT_CAMERA;

	get status() {
		return this.getAttribute('status');
	}

	set status(status) {
		this.setAttribute('status', status);
	}

	constructor () {
		super();
	}


	connectedCallback () {
		this.initialize();
		this.innerHTML = `
			<aon-application id='${this.INVOICE}' title='${MSG.AON_MSG_INVOICES}' drag_and_drop='true'></aon-application>
			<aon-dialog-menu id='aonDialogAddOption'> </aon-dialog-menu>
			<input id='${this.INPUT_FILE}' style='display:none;' type='file' name='file' multiple>
			<input id='${this.INPUT_CAMERA}' type='file' accept='image/*' capture='camera' hidden />
		`;
		this.build();
	}

	initialize() {
		this.INVOICE = 'aonInvoice';
		this.INPUT_FILE = this.INVOICE + 'InputFile';
		this.INPUT_CAMERA = 'aonMobileMenuCameraInput';// this.INVOICE + 'InputCamera';
		this._filter = {
			status: 'inbox',
			page: 0,
			per_page: 50
		};
	}

  build(){
		let aonInvoice = this.getElement(this.INVOICE);

		let input = this.getElement(this.INPUT_FILE);
		input.addEventListener('change', () => this.preview(input.files));
		
		this.getElement(this.INPUT_CAMERA).addEventListener('change',  ({target}) => this.preview(target.files));

		aonInvoice.addEventListener('drop', (event) => {
			if(event && event.dataTransfer && event.dataTransfer.files){
				this.preview(event.dataTransfer.files);
			}
		});

		if(this.isMobile()) {
			aonInvoice.addFloatOption(InvoiceAction.ADD_INVOICE, () => this.addInvoice());
		} else {
			aonInvoice.addToolbarOption('Add', 'add', () => this.addInvoice());
			aonInvoice.addToolbarOption('Upload', 'file_upload', () => this.addInvoiceFile());

			aonInvoice.addSearchOption();
      aonInvoice.addEventListener('search', (event) => this.search(event.detail));
		}

		this.appendChild(input);

		let pendingOptions = [
			{
				name: MSG.AON_MSG_INBOX,
				icon: 'inbox',
				fn: () => this.aonInvoiceList({status:'inbox'})
			},
			{
				name: MSG.AON_MSG_REJECTEDS,
				icon: 'report',
				fn: () => this.aonInvoiceList({status:'refused'})
			},
			{
				name: MSG.AON_MSG_TRASH,
				icon: 'delete',
				fn: () => this.aonInvoiceList({status:'trash'})
			}
		];
		aonInvoice.addSidenavOptions(MSG.AON_MSG_PENDINGS.toUpperCase(), pendingOptions);

		let accountingOptions = [
			{
				name: MSG.AON_MSG_ISSUEDS,
				icon: 'unarchive',
				fn: () => this.aonInvoiceList({status:'accounting', type:'sales', page:1, per_page: 50})
			},
			{
				name: MSG.AON_MSG_RECEIVEDS,
				icon: 'archive',
				fn: () => this.aonInvoiceList({status:'accounting', type:'purchase,expenses', page:1, per_page: 50})
			},
			{
				name: MSG.AON_MSG_TICKETS,
				icon: 'receipt',
				fn: () => this.aonInvoiceList({status:'accounting', type:'ticket', page:1, per_page: 50})
			}
		];
		aonInvoice.addSidenavOptions(MSG.AON_MSG_ACCOUNTEDS.toUpperCase(), accountingOptions);

		let settingOptions = [
			{
				name: MSG.AON_MSG_PRINTING_INVOICES,
				icon: 'print',
				fn: () => {this.aonInvoicePrint()}
			}
		];
		aonInvoice.addSidenavOptions(MSG.AON_MSG_SETTING.toUpperCase(), settingOptions);

		let filter = {status:'inbox'};

		if(this.hasAttribute('status')) {
			filter = {status: this.getAttribute('status')};
		}

		if(!this.isMobile()){
			let toolbar = this.getElement(aonInvoice.TOOLBAR);
			if(filter.status === 'inbox') toolbar.setAttribute('option', MSG.AON_MSG_INBOX);
			else if(filter.status === 'refused') toolbar.setAttribute('option', MSG.AON_MSG_REJECTEDS);
		}
		this.aonInvoiceList(filter);
	}

	search(value) {
		this._filter.description = value;
		this.aonInvoiceList();
	}

	aonInvoiceList(filter) {
		filter = filter || this._filter;
		this._filter = filter;
		let invoiceList = document.getElementById('aonInvoiceList');
		if(invoiceList) {
			invoiceList.setFilter(filter);
			invoiceList.init();
		} else {
			let aonInvoice = document.getElementById('aonInvoice');
			if(this.isMobile()) {
				aonInvoice.setContentHTML(filter
					? `<aon-mobile-invoice-list id="aonInvoiceList" filter='${JSON.stringify(filter)}'></aon-mobile-invoice-list>`
					: `<aon-mobile-invoice-list id="aonInvoiceList"></aon-mobile-invoice-list>`);
			}  else {
				aonInvoice.setContentHTML(filter
					? `<aon-invoice-list id="aonInvoiceList" filter='${JSON.stringify(filter)}'></aon-invoice-list>`
					: `<aon-invoice-list id="aonInvoiceList"></aon-invoice-list>`);
			}
		}
	}

	aonInvoicePrint() {
		aonInvoice.setContentHTML(`<aon-invoice-print></aon-invoice-print>`)
	}

	addInvoice() {
		let aonInvoice = this.getElement('aonInvoice');
		let aonInvoiceToolbar = this.getElement(aonInvoice.TOOLBAR);
		let button = this.isMobile()
			?	this.getElement('aonInvoiceAddInvoiceButton')
			: this.getElement(aonInvoiceToolbar.TOOL_SECTION + 'AddButton');

		let height = window.innerHeight;
		let top  = button.getBoundingClientRect().top;
		const left = button.getBoundingClientRect().left;

		if((height - top) < (height / 2)) {
				top = top - 135;
		}

		let d = document.getElementById('aonDialogAddOption');

		let options = [{
				name: 'Emitidas',
				icon: 'unarchive',
				fn: () => this.aonInvoice('emitida')
			}, {
				name: 'Recibidas',
				icon: 'archive',
				fn: () => this.aonInvoice('recibida')
			}, {
				name: 'Tickets/Justificantes',
				icon: 'receipt',
				fn: () => this.aonInvoice('ticket')
			}];
		if(this.isMobile()) {
			options.push({
				name: 'Camara',
				icon: 'camera',
				fn: () => this.openCamera()
			});
		}
		d.setMenuOptions(options, top, left);
		d.open();
	}

	async openCamera() {
		const isApp = await actionMobile({ action: "camera", id: this.INPUT_CAMERA, selector: 'aon-invoice-panel' });
		console.log('aon-invoice-panel');
		if (!isApp) this.getElement(this.INPUT_CAMERA).click();
	}

	async receiveAppImage(file) {
		if (file.contentType.indexOf("image") >= 0) {
		  //compress 500kB / file, 500kb, quality default 0.9, maxResolution 1280
		  const f = await downscaleImage(file, undefined, undefined, undefined);
		  const data = {
				file:f,
				invoice: new Invoice('recibida')
		   };
		   let aonInvoice = document.getElementById('aonInvoice');
		   aonInvoice.startLoader();
		   await insertInvoice(data);
		   this.aonInvoiceList({status:'inbox'})
		   aonInvoice.stopLoader();
		}
	}

	addInvoiceFile() {
		let el = this.getElement(this.INPUT_FILE);
		el.click();
	}

	preview(files) {
		for(let i = 0; i < files.length; i++) {
			getReader(files[i]).then(f=>{
				this.attach(f);
			});
		}
	}

	async attach(file){
		if (file) {
			const data = {
				file,
				invoice: new Invoice('recibida')
			};
			if(this.isMobile()) {
				if (data.file.contentType.indexOf("image") >= 0) {
					//compress 500kB / file, 500kb, quality default 0.9, maxResolution 1280
					data.file = await downscaleImage(data.file, undefined, undefined, undefined);
		    	}
			}
			let aonInvoice = document.getElementById('aonInvoice');
			aonInvoice.startLoader();
			insertInvoice(data).then((r) => {
 				this.aonInvoiceList({status:'inbox'})
 				aonInvoice.stopLoader();
				//this.getInvoice().id = r.id;
			});
		}
	}

	aonInvoice(type, invoice) {
		let aonInvoice = document.getElementById('aonInvoice');
		if(this.isMobile()) {
			aonInvoice.setContentHTML(invoice
				? `<aon-mobile-invoice invoice='${JSON.stringify(invoice)}'> </aon-mobile-invoice>`
				: `<aon-mobile-invoice type="${type}"> </aon-mobile-invoice>`);
		} else {
			aonInvoice.setContentHTML(invoice
				? `<aon-invoice invoice='${JSON.stringify(invoice)}'> </aon-invoice>`
				: `<aon-invoice type="${type}"> </aon-invoice>`);
		}
	}

}

window.customElements.define('aon-invoice-panel', AonInvoicePanel);
