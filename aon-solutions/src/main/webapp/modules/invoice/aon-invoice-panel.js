import { AonElement } from '../../components/AonElement.js';
import { AonApplication } from '../../components/aon-application.js';
import { insertInvoice, deleteInvoices, actionMobile, getDomainUserRoles, selfconta } from '../../services/service.js';
import { Invoice } from './Invoice.js';
import { DomainUserRoles } from '../../models/DomainUserRoles.js';

import { AonNewInvoice } from './aon-new-invoice.js';
import { AonMobileNewInvoice } from './aon-mobile-new-invoice.js';

import './aon-invoice.js';
import './aon-mobile-invoice.js';
import './aon-invoice-list.js';
import './aon-mobile-invoice-list.js';
import './aon-invoice-print.js';

import '../../components/aon-application.js';
import '../../components/aon-dialog-menu.js';

import { MSG, MATERIAL_ICONS, CONSTANT, EVENT } from '../../environments/environments.js';
import { downscaleImage } from '../../services/compressImg.js';
import { getReader } from '../../services/utils.js';
import * as ACTION from '../actions.js';
import * as GWT from "../../gwt/gwt.js";
import { AonProductList } from '../product/aon-product-list.js';
import * as OPTION from './InvoiceOptions.js';
import { AonInvoicePrint } from './aon-invoice-print.js';
import { AonToolbar } from '../../components/aon-toolbar.js';
import { AonMobileProductList } from '../product/aon-mobile-product-list.js';

export class AonInvoicePanel extends AonElement {

	dur;
	selectedOption;
	filter;

	INVOICE;
	INPUT_FILE;
	INPUT_CAMERA;
	PRODUCT_LIST;

	get id() {
		return this.getAttribute(CONSTANT.ID);
	}

	set id(id) {
		this.setAttribute(CONSTANT.ID, id);
	}

	get status() {
		return this.getAttribute(CONSTANT.STATUS);
	}

	set status(status) {
		this.setAttribute(CONSTANT.STATUS, status);
	}

	constructor () {
		super();
	}

	connectedCallback () {
		this.initialize();
		this.innerHTML = `
			<aon-application id='${this.INVOICE}' title='${MSG.BILLING}' drag_and_drop='true'></aon-application>
			<aon-dialog-menu id='aonDialogAddOption'> </aon-dialog-menu>
			<input id='${this.INPUT_FILE}' style='display:none;' type='file' name='file' multiple>
			<input id='${this.INPUT_CAMERA}' type='file' accept='image/*' capture='camera' hidden />
		`;
		getDomainUserRoles({}).then(r => {
			this.dur = new DomainUserRoles(r);
			this.build();
		});
	}

	initialize() {
		this.INVOICE = 'aonInvoice';
		this.INPUT_FILE = this.INVOICE + 'InputFile';
		this.INPUT_CAMERA = 'aonMobileMenuCameraInput';// this.INVOICE + 'InputCamera';
		this.PRODUCT_LIST = this.id + 'ProductList';
		this.status = this.status || CONSTANT.INBOX;
		this.filter = {
			status: this.status || CONSTANT.INBOX,
			page: 0,
			per_page: 50
		}
		this.option = CONSTANT.REFUSED === this.status
			? OPTION.RAWDOC_REJECT : OPTION.RAWDOC_INBOX; 
	}

	getDur(){
		return this.dur;
	}

	getFilter() {
		return this.filter;
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
			aonInvoice.addFloatOption(ACTION.ADD_INVOICE, () => this.addInvoice());
		}
		this.buildToolbarOptions();
		
		this.appendChild(input);
		this.buildSidenavOptions();
		this.selectOption(this.option);
	}

	buildToolbarOptions(){
		let toolbar = this.getElement(aonInvoice.TOOLBAR);
		toolbar.removeButtons();
		if(!this.isMobile()) {
			if(this.selectedOption && OPTION.PRODUCT.id === this.selectedOption.id){
				// TODO
			} else {
				this.getApplication().addToolbarOption('Add', 'add', () => this.addInvoice());
				this.getApplication().addToolbarOption('Upload', 'file_upload', () => this.addInvoiceFile());
			}
		}
		this.getApplication().addSearchOption();
		let searchFn = (event) => this.search(event.detail);
		this.getApplication().removeEventListener(EVENT.SEARCH, searchFn, true);
		this.getApplication().addEventListener(EVENT.SEARCH, searchFn);
	}

 	buildSidenavOptions() {
		this.getApplication().addEventListener(EVENT.SELECT_OPTION, (e) => {
			this.selectOption(e.detail);
		})
		this.buildRawdocOptions();
		this.buildOfferOptions();
		if(this.getDur().isInvoicePortal() || this.getDur().isInvoiceManager()){
			this.buildInvoiceOptions();
			this.buildRegistryOptions();
			this.buildSettingOptions();
		}
	}

	buildRawdocOptions() {
		let pendingOptions = [ 
			OPTION.RAWDOC_INBOX,
			OPTION.RAWDOC_REJECT, 
			OPTION.RAWDOC_DRAFT
		];
		this.getApplication().addSidenavOptions(MSG.PENDING_DOCUMENTS.toUpperCase(), pendingOptions);
	}

	buildInvoiceOptions() {
		let invoiceOptions = [
			OPTION.INVOICE_ISSUED,
			OPTION.INVOICE_RECEIVED, 
			OPTION.INVOICE_TICKET
		];
			
		this.getApplication().addSidenavOptions(MSG.INVOICES.toUpperCase(), invoiceOptions);
	}

	buildOfferOptions() {
		if(this.getDur().isAlpha() && (this.getDur().isInvoicePortal() || this.getDur().isInvoiceManager())){
			let budgetOptions = [ OPTION.OFFER ];
			this.getApplication().addSidenavOptions(MSG.BUDGETS.toUpperCase(), budgetOptions);
		}
	}

	buildRegistryOptions() {
		if(!this.isMobile()) {
			let contactOptions = [
				OPTION.REGISTRY_CUSTOMER,
				OPTION.REGISTRY_SUPPLIER,
				OPTION.REGISTRY_CREDITOR
			];
			this.getApplication().addSidenavOptions(MSG.HOLDERS.toUpperCase(), contactOptions);
		}
	}

	buildSettingOptions() {
		let settingOptions = [ OPTION.CONFIGURATION_PRINT, OPTION.PRODUCT ];

		if(this.getDur().isAlpha()){
			// settingOptions.push(OPTION.PRODUCT);
			settingOptions.push(OPTION.CONFIGURATION_SII_TBAI);
		}
		this.getApplication().addSidenavOptions(MSG.SETTING.toUpperCase(), settingOptions);
	}

	add(){

	}

	search(value) {
		if(this.selectedOption && OPTION.PRODUCT.id === this.selectedOption.id){
			this.aonProductList({value});
		} else {
			if(this.filter.description !== value) {
				this.filter.description = value;
				this.aonInvoiceList();
			}
		}
	}

	aonInvoiceList(filter) {
		filter = filter || this.filter;
		this.filter = filter;
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
		this.getApplication().setContent(new AonInvoicePrint());
	}

	aonProductList(filter) {
		let productList = this.getElement(this.PRODUCT_LIST);
		if(productList) {
			productList.setFilter(filter);
		} else {
			productList = this.isMobile() ? new AonMobileProductList() : new AonProductList();
			productList.id = this.PRODUCT_LIST;	
			aonInvoice.setContent(productList);
		}
	}

	addInvoice() {
		let ayudat = localStorage.getItem('aon_domain_name').includes('ayudat');

		let aonInvoice = this.getElement('aonInvoice');
		let aonInvoiceToolbar = this.getElement(aonInvoice.TOOLBAR);
		let button = this.isMobile()
			?	this.getElement('aonInvoiceAddInvoiceButton')
			: this.getElement(aonInvoiceToolbar.TOOL_SECTION + 'AddButton');

		let height = window.innerHeight;
		let top  = button.getBoundingClientRect().top;
		const left = button.getBoundingClientRect().left;

		if((height - top) < (height / 2)) {
				top = top - (ayudat ? 170 : 135);
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
		if(ayudat) {
			options.push({
					name: 'Importación Selfconta',
					icon: 'import_export',
					fn: () => {
						let aonApplication = this.getApplication();
						aonApplication.confirmDialog("Importar", "Desea importar las facturas?", async() => {
							aonApplication.startLoading();
							try {
								await selfconta();
								this.showToast({
									type: 'success',
									message: 'Datos Importados. Revisa las facturas rechazadas.'
								});
							} catch (error) {
								this.showToast(error);
							}
							aonApplication.stopLoading();
						});
					}
				});
		}

		if(this.isMobile()) {
			options.push({
				name: 'Camara',
				icon: 'camera_alt',
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
		this.getElement(this.INPUT_FILE).click();
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
		let aonInvoice = this.getApplication();
		if(this.getDur().isAlpha() || type === 'emitida' 
				|| (invoice && invoice.status === 'pending') 
				|| (invoice && invoice.status === 'scored')
				|| (invoice && invoice.status === 'accounting')){
			let ni = this.isMobile() ? new AonMobileNewInvoice() : new AonNewInvoice();
			ni.setInvoice(invoice);
			aonInvoice.setContent(ni);
		} else if(this.isMobile()) {
			aonInvoice.setContentHTML(invoice
				? `<aon-mobile-invoice invoice='${JSON.stringify(invoice)}'> </aon-mobile-invoice>`
				: `<aon-mobile-invoice type="${type}"> </aon-mobile-invoice>`);
		} else {
			aonInvoice.setContentHTML(invoice
				? `<aon-invoice invoice='${JSON.stringify(invoice).replaceAll("'", "")}'> </aon-invoice>`
				: `<aon-invoice type="${type}"> </aon-invoice>`);
		}
	}

	selectOption(option) {
		if(option) {
			let toolbar = this.getElement(aonInvoice.TOOLBAR);
			toolbar.option = option.name;
			this.selectedOption = option;
			this.buildToolbarOptions();
			switch(option.id){
			case OPTION.RAWDOC_INBOX.id:
				this.aonInvoiceList({status: CONSTANT.INBOX});
				break;
			case OPTION.RAWDOC_REJECT.id:
				this.aonInvoiceList({status: CONSTANT.REFUSED});
				break;
			case OPTION.RAWDOC_DRAFT.id:
				this.aonInvoiceList({status: CONSTANT.TRASH});
				break;
			case OPTION.INVOICE_ISSUED.id:
				this.aonInvoiceList({status:'accounting', type:'sales', page:1, per_page: 50});
				break;
			case OPTION.INVOICE_RECEIVED.id:
				this.aonInvoiceList({status:'accounting', type:'purchase,expenses', page:1, per_page: 50});
				break;
			case OPTION.INVOICE_TICKET.id:
				this.aonInvoiceList({status:'accounting', type:'ticket', page:1, per_page: 50});
				break;
			case OPTION.REGISTRY_CUSTOMER.id:
				GWT.load(GWT.CUSTOMER, this.getApplication().CONTENT);
				break;
			case OPTION.REGISTRY_SUPPLIER.id:
				GWT.load(GWT.SUPPLIER, this.getApplication().CONTENT);
				break;
			case OPTION.REGISTRY_CREDITOR.id:
				GWT.load(GWT.CREDITOR, this.getApplication().CONTENT);
				break;
			case OPTION.OFFER.id:
				break;
			case OPTION.PRODUCT.id:
				this.aonProductList();
				break;
			case OPTION.CONFIGURATION_PRINT.id:
				this.aonInvoicePrint();
				break;
			case OPTION.CONFIGURATION_SII_TBAI.id:
				break;
			default:
				this.aonInvoiceList({status: CONSTANT.INBOX});
				break;
			}
		}
	}

}

window.customElements.define('aon-invoice-panel', AonInvoicePanel);
