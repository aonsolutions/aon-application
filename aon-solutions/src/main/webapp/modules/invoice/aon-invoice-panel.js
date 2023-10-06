import { AonElement } from '../../components/AonElement.js';
import { insertInvoice, mobileAction, MOBILE_ACTION, getDomainUserRoles, selfconta, downloadInvoiceExcel, getInvoice } from '../../services/service.js';

import { Invoice } from './Invoice.js';
import { DomainUserRoles } from '../../models/DomainUserRoles.js';

import { AonInvoice } from './aon-invoice.js';
import { AonMobileInvoice } from './aon-mobile-invoice.js';

import './aon-invoice-list.js';
import './aon-mobile-invoice-list.js';
import './aon-invoice-print.js';

import '../../components/aon-application.js';
import '../../components/aon-dialog-menu.js';

import { MSG, MATERIAL_ICONS, CONSTANT, EVENT, TAG } from '../../environments/environments.js';
import { downscaleImage } from '../../services/compressImg.js';
import { getReader } from '../../services/utils.js';
import * as ACTION from '../actions.js';
import { AonProductList } from '../product/aon-product-list.js';
import * as OPTION from './InvoiceOptions.js';
import { AonInvoicePrint } from './aon-invoice-print.js';
import { AonMobileProductList } from '../product/aon-mobile-product-list.js';
import Apps from '../../services/app.js';
import { AonProduct } from '../product/aon-product.js';
import { AonCustomerList } from '../registry/customer/aon-customer-list.js';
import { AonSupplierList } from '../registry/supplier/aon-supplier-list.js';
import { AonCreditorList } from '../registry/creditor/aon-creditor-list.js';
import { AonMobileSupplierList } from '../registry/supplier/aon-mobile-supplier-list.js';
import { AonMobileCreditorList } from '../registry/creditor/aon-mobile-creditor-list.js';
import { AonMobileCustomerList } from '../registry/customer/aon-mobile-customer-list.js';
import { AonCustomer } from '../registry/customer/aon-customer.js';
import { AonSupplier } from '../registry/supplier/aon-supplier.js';
import { AonCreditor } from '../registry/creditor/aon-creditor.js';
import * as GWT from '../../gwt/gwt.js';
import { SigninSidenav } from '../timecontrol/signinEnums.js';
import { AonInvestList } from '../product/aon-invest-list.js';
import { AonInvest } from '../product/aon-invest.js';
import { AonSelect } from '../../components/aon-select.js';

export class AonInvoicePanel extends AonElement {

	dur;
	selectedOption;
	filter;

	INVOICE;
	INPUT_FILE;
	INPUT_CAMERA;
	PRODUCT_LIST;
	CUSTOMER_LIST;
	SUPPLIER_LIST;
	CREDITOR_LIST;
	INVEST_LIST;

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
		this.PRODUCT_LIST = this.INVOICE + 'ProductList';
		this.CUSTOMER_LIST = this.INVOICE + 'CustomerList';
		this.SUPPLIER_LIST = this.INVOICE + 'SupplierList';
		this.CREDITOR_LIST = this.INVOICE + 'CreditorList';
		this.INVEST_LIST = this.INVOICE + 'InvestList';

		this.status = this.status || CONSTANT.INBOX;
		this.filter = {
			status: this.status || CONSTANT.INBOX,
			page: 0,
			per_page: 50
		}
		this.option = this.option || (CONSTANT.REJECTED === this.status
			? OPTION.RAWDOC_REJECT : OPTION.RAWDOC_INBOX); 
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
		input.addEventListener(EVENT.CHANGE, () => this.preview(input.files));

		this.getElement(this.INPUT_CAMERA).addEventListener(EVENT.CHANGE,  ({target}) => this.preview(target.files));

		aonInvoice.addEventListener(EVENT.AON_APPLICATION_DROP, (e) => this.preview(e.detail));

		if(this.isMobile()) {
			aonInvoice.addFloatOption(ACTION.ADD_INVOICE, () => this.addInvoice());
		}
		this.buildToolbarOptions();
		
		this.appendChild(input);
		this.buildSidenavOptions();
		if(this.invoice && this.invoice.type){
			this.aonInvoice(this.invoice.type, this.invoice);
		} else if(this.value){
			this.aonInvoiceById(this.value);
		} else 
			this.selectOption(this.option);
	}

	buildToolbarOptions(){
		let toolbar = this.getElement(aonInvoice.TOOLBAR);
		toolbar.removeButtons();
		if(!this.isMobile()) {
			if(this.selectedOption && (OPTION.PRODUCT.id === this.selectedOption.id)){
				this.getApplication().addToolbarOption('Add', 'add', () => this.addProduct());
			} else if(this.selectedOption && (OPTION.EXPENSES.id === this.selectedOption.id)){
				this.getApplication().addToolbarOption('Add', 'add', () => this.addExpense());
			} else if(this.selectedOption && (OPTION.REGISTRY_CUSTOMER.id === this.selectedOption.id)){
				this.getApplication().addToolbarOption('Add', 'add', () => this.addCustomer());
			} else if(this.selectedOption && (OPTION.REGISTRY_SUPPLIER.id === this.selectedOption.id)){
				this.getApplication().addToolbarOption('Add', 'add', () => this.addSupplier());
			} else if(this.selectedOption && (OPTION.REGISTRY_CREDITOR.id === this.selectedOption.id)){
				this.getApplication().addToolbarOption('Add', 'add', () => this.addCreditor());
			} else if(this.selectedOption && (OPTION.INVEST.id === this.selectedOption.id)){
				this.getApplication().addToolbarOption('Add', 'add', () => this.addInvest());
			} else {
				this.getApplication().addToolbarOption('Add', 'add', () => this.addInvoice());
				this.getApplication().addToolbarOption('Upload', 'file_upload', () => this.addInvoiceFile());
				if(this.selectedOption && (OPTION.INVOICE_ISSUED.id === this.selectedOption.id 
					|| OPTION.INVOICE_RECEIVED.id === this.selectedOption.id 
					|| OPTION.INVOICE_TICKET.id === this.selectedOption.id)){
						this.getApplication().addToolbarOption2(SigninSidenav.EXCEL, () => this.downloadInvoiceExcel());
					}
			}
		}
		const btnSearch = this.getApplication().addSearchOption();
		let searchFn = (event) => this.search(event.detail);
		btnSearch.addEventListener(EVENT.SEARCH, searchFn);
	}

	downloadInvoiceExcel() {
		let aonInvoiceTable = document.getElementById('aonInvoiceTable');
		
		let data = {
			domainId: localStorage.getItem('aon_domain_id'),
			domainName: localStorage.getItem('aon_domain_name'),
			domainLogin: localStorage.getItem('aon_domain_login'),
			ids: aonInvoiceTable.selected.map(r => r.id),
			description: this.getFilter().description,
			status: this.getFilter().status,
			type: this.getFilter().type,
			from: this.getFilter().from,
			to: this.getFilter().to

		};
		let json = btoa(JSON.stringify(data));
		downloadInvoiceExcel(json);
	}

 	buildSidenavOptions() {
		if(this.isMobile()){
			this.getApplication().addMobileSidenavHeader(Apps.INVOICE);
		}

		this.getApplication().addEventListener(EVENT.SELECT_OPTION, (e) => {
			this.selectOption(e.detail);
		})
		this.buildRawdocOptions();
		this.buildOfferOptions();
		if(this.getDur().isInvoicePortal() || this.getDur().isInvoiceManager()){
			this.buildInvoiceOptions();
			this.buildSettingOptions();
		}
	}

	buildRawdocOptions() {
		let pendingOptions = [ 
			OPTION.RAWDOC_INBOX,
			OPTION.RAWDOC_REJECT, 
			OPTION.RAWDOC_DRAFT
		];
		let data = {
			id: MSG.PENDING_DOCUMENTS,
			title: MSG.PENDING_DOCUMENTS,
			name: MSG.PENDING_DOCUMENTS,
			color: Apps.INVOICE.color
		}
		this.getApplication().addSidenavOptions2(data, pendingOptions);
	}

	buildInvoiceOptions() {
		let invoiceOptions = [
			OPTION.INVOICE_ISSUED,
			OPTION.INVOICE_RECEIVED, 
			OPTION.INVOICE_TICKET
		];
		let data = {
			id: MSG.INVOICES,
			title: MSG.INVOICES,
			name: MSG.INVOICES,
			color: Apps.INVOICE.color
		}
		
		this.getApplication().addSidenavOptions2(data, invoiceOptions);
	}

	buildOfferOptions() {
		if(this.getDur().isAlpha() && (this.getDur().isInvoicePortal() || this.getDur().isInvoiceManager())){
			let budgetOptions = [ OPTION.OFFER ];
			let data = {
				id: MSG.BUDGETS,
				title: MSG.BUDGETS,
				name:  MSG.BUDGETS,
				color: Apps.INVOICE.color
			}
			this.getApplication().addSidenavOptions2(data, budgetOptions);
		}
	}

	buildSettingOptions() {
		let settingOptions = [];
		if(!this.isMobile()) {
			settingOptions = [ OPTION.REGISTRY, OPTION.CONCEPTS, OPTION.CHARGES_PAYMENTS, OPTION.VAT_PANEL, OPTION.RETENTION_PANEL ];
		} else settingOptions = [ OPTION.REGISTRY, OPTION.PRODUCT ];

		let data = {
			id: MSG.MANAGEMENT,
			title: MSG.MANAGEMENT,
			name:  MSG.MANAGEMENT,
			color: Apps.INVOICE.color
		}
		this.getApplication().addSidenavOptions2(data, settingOptions);
	}

	add(){

	}

	search(value) {
		if(this.selectedOption && OPTION.REGISTRY_CREDITOR.id === this.selectedOption.id) {
			this.aonCreditorList({page:1, perPage:50, value});
		} else if(this.selectedOption && OPTION.REGISTRY_SUPPLIER.id === this.selectedOption.id){
			this.aonSupplierList({page:1, perPage:50, value});
		} else if(this.selectedOption && OPTION.REGISTRY_CUSTOMER.id === this.selectedOption.id){
			this.aonCustomerList({page:1, perPage:50, value});
		} else if(this.selectedOption && OPTION.PRODUCT.id === this.selectedOption.id){
			this.aonProductList({expense: false, value});
		} else if(this.selectedOption && OPTION.EXPENSES.id === this.selectedOption.id){
			this.aonProductList({expense: true, value});
		} else if(this.selectedOption && OPTION.INVEST.id === this.selectedOption.id){
			this.aonInvestList({value});
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

	aonCustomerList(filter) {
		let customerList = this.getElement(this.CUSTOMER_LIST);
		if(customerList) {
			customerList.setFilter(filter);
		} else {
			customerList =  this.isMobile() ? new AonMobileCustomerList() : new AonCustomerList();
			customerList.id = this.CUSTOMER_LIST;	
			customerList.filter = filter;
			aonInvoice.setContent(customerList);
		}
	}

	aonSupplierList(filter) {
		let supplierList = this.getElement(this.SUPPLIER_LIST);
		if(supplierList) {
			supplierList.setFilter(filter);
		} else {
			supplierList = this.isMobile() ? new AonMobileSupplierList() : new AonSupplierList();
			supplierList.id = this.SUPPLIER_LIST;	
			supplierList.filter = filter;
			aonInvoice.setContent(supplierList);
		}
	}


	aonCreditorList(filter) {
		let creditorList = this.getElement(this.CREDITOR_LIST);
		if(creditorList) {
			creditorList.setFilter(filter);
		} else {
			creditorList = this.isMobile() ? new AonMobileCreditorList() : new AonCreditorList();
			creditorList.id = this.CREDITOR_LIST;	
			creditorList.filter = filter;
			aonInvoice.setContent(creditorList);
		}
	}

	aonProductList(filter) {
		let productList = this.getElement(this.PRODUCT_LIST);
		if(productList) {
			productList.setFilter(filter);
		} else {
			productList = this.isMobile() ? new AonMobileProductList() : new AonProductList();
			productList.id = this.PRODUCT_LIST;	
			productList.filter = filter;
			aonInvoice.setContent(productList);
		}
	}

	aonInvestList(filter) {
		let investList = this.getElement(this.INVEST_LIST);
		if(investList) {
			investList.setFilter(filter);
		} else {
			investList = new AonInvestList();
			investList.id = this.INVEST_LIST;	
			investList.filter = filter;
			aonInvoice.setContent(investList);
		}
	}

	addCustomer() {
		let aonCustomer = new AonCustomer();
		aonCustomer.id = this.id + 'Customer';
		aonCustomer.setCustomer();
		this.getApplication().setContent(aonCustomer);	
	}

	addSupplier() {
		let aonSupplier = new AonSupplier();
		aonSupplier.id = this.id + 'Supplier';
		aonSupplier.setSupplier();
		this.getApplication().setContent(aonSupplier);	
	}
	
	addCreditor() {
		let aonCreditor = new AonCreditor();
		aonCreditor.id = this.id + 'Creditor';
		aonCreditor.setCreditor();
		this.getApplication().setContent(aonCreditor);	
	}

	addProduct() {
		let aonProduct = new AonProduct();
		aonProduct.id = this.id + 'Product';
		this.getApplication().setContent(aonProduct);	
	}

	addExpense() {
		let aonProduct = new AonProduct();
		aonProduct.id = this.id + 'Expense';
		aonProduct.expense = true;
		this.getApplication().setContent(aonProduct);	
	}

	addInvest() {
		let aonInvest = new AonInvest();
		aonInvest.id = this.id + 'Invest';
		this.getApplication().setContent(aonInvest);	
	}

	addInvoice() {
		let ayudat = this.getDur().isSelfconta();
		let aonInvoice = this.getElement('aonInvoice');
		let aonInvoiceToolbar = this.getElement(aonInvoice.TOOLBAR);
		let button = this.isMobile()
			?	this.getElement('aonInvoiceAddInvoiceButton')
			: this.getElement(aonInvoiceToolbar.TOOL_SECTION + 'AddButton');

		let height = window.innerHeight;
		let top  = button.getBoundingClientRect().top;
		const left = button.getBoundingClientRect().left;

		if((height - top) < (height / 2)) {
				top = top - (ayudat ? 205 : 170);
		}

		let d = document.getElementById('aonDialogAddOption');

		const newEmitida = {
			name: 'Emitidas',
			title:"Emitidas",
			icon: 'unarchive',
			permission: true,
			backgroundColor: "#4472C4",
			fn: () => this.aonInvoice('emitida')
		  };

		  const newRecibidas = {
			name: 'Recibidas',
			title:"Recibidas",
			icon: 'archive',
			permission: true,
			backgroundColor: "#4472C4",
			fn : () => this.aonInvoice('recibida')
		  };

		  const newTicket = {
			name: "Tickets / Justificantes",
			title: "Tickets / Justificantes",
			icon: 'receipt',
			permission: true,
			backgroundColor: "#4472C4",
			fn :  () => this.aonInvoice('ticket')
		  };
	  
		let options = [newEmitida, newRecibidas, newTicket];
		if(ayudat) {
			let importSelfconta = {
				name: 'Importación Selfconta',
				title: 'Importación Selfconta',
				icon: 'import_export',
				permission: ayudat,
				backgroundColor: "#4472C4",
				fn: () => this.importSelfconta()
			}
			options.push(importSelfconta);
		}

		if(this.isMobile()) {
			let uploadFile = {
				name: MSG.UPLOAD_FILE,
				title: MSG.UPLOAD_FILE,
				icon: MATERIAL_ICONS.FILE_UPLOAD,
				permission: true,
				backgroundColor: "#4472C4",
				fn :  () => this.addInvoiceFile()
			}

			options.push(uploadFile);

			let openCamera = {
				name: 'Camara',
				title: 'Camara',
				icon: 'camera_alt',
				permission: true,
				backgroundColor: "#4472C4",
				fn :  () => this.openCamera()
			}
			options.push(openCamera);
		}
		d.setMenuOptions(options, top, left);
		d.open();
	}
	
	importSelfconta() {
		let div = this.createElement(TAG.DIV);
		div.id = "aonInvoiceSelfcontaDiv"; 

		let div2 = this.createElement(TAG.DIV);
		div2.id = "aonInvoiceSelfcontaDiv2"; 
		div2.innerHTML = "¿Desea importar las facturas?"
		div.appendChild(div2);

		let yearSelect = new AonSelect();
		yearSelect.id = "aonInvoiceSelfcontaYear";
		yearSelect.title = MSG.YEAR;
		yearSelect.options = JSON.stringify([
			{name:'2023', value:2023},
			{name:'2022', value:2022},
			{name:'2021', value:2021},
			{name:'2020', value:2020},
			{name:'2019', value:2019},
			{name:'2018', value:2018}
		]);
		div.appendChild(yearSelect);

		let aonApplication = this.getApplication();
		let d = aonApplication.getDialog();
		d.clear();
		if(!this.isMobile()) d.width = '400px';
		d.setTitle("Importar");
		d.setContent(div);
		d.addAcceptAction(async() => {
			aonApplication.startLoading();
			try {
				await selfconta(yearSelect.value);
				this.showToast({
					type: 'success',
					message: 'Datos Importados. Revisa las facturas rechazadas.'
				});
			} catch (error) {
				this.showToast(error);
			}
			aonApplication.stopLoading();
		});
		d.open();
	}

	async openCamera() {
		const isApp = await mobileAction({ action: MOBILE_ACTION.CAMERA, id: this.INPUT_CAMERA, selector: 'aon-invoice-panel' });
		if (!isApp) this.getElement(this.INPUT_CAMERA).click();
	}

	async receiveAppImage(file) {
		if (file.contentType.indexOf("image") >= 0) {
		  //compress 500kB / file, 500kb, quality default 0.9, maxResolution 1280
		  const f = await downscaleImage(file, undefined, undefined, undefined);
		  const data = {
				file:f,
				invoice: new Invoice().setType('recibida')
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
				invoice: new Invoice().setType('recibida')
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
		if(this.isMobile() && aonInvoice.TOOLBAR) {
			let toolbar = this.getElement(aonInvoice.TOOLBAR);
			toolbar.removeButtons();
		}
		let component = this.isMobile() ? new AonMobileInvoice() : new AonInvoice();
		component.setType(type);
		component.setInvoice(invoice);

		aonInvoice.setContent(component);
	}

	aonInvoiceById(id) {
		getInvoice(id).then(invoice => this.aonInvoice(invoice.type, invoice)).catch(error =>this.showToast(error));
	}

	selectOption(option) {
		if(option) {
			let toolbar = this.getElement(aonInvoice.TOOLBAR);
			toolbar.option = option.name;
			this.selectedOption = option;
			this.buildToolbarOptions();
			switch(option.id){
				case OPTION.CREATE_INVOICE_ISSUED.id:
					this.aonInvoice('emitida');
					break;
				case OPTION.CREATE_INVOICE_RECEIVED.id:
					this.aonInvoice('recibida');
					break;
				case OPTION.CREATE_INVOICE_TICKET.id:
					this.aonInvoice('ticket');
					break;
				
			case OPTION.RAWDOC_INBOX.id:
				this.aonInvoiceList({status: CONSTANT.INBOX});
				break;
			case OPTION.RAWDOC_INBOX_ISSUED.id:
				this.aonInvoiceList({status: CONSTANT.INBOX, type: 'emitida'});
				break;
			case OPTION.RAWDOC_INBOX_RECEIVED.id:
				this.aonInvoiceList({status: CONSTANT.INBOX, type: 'recibida'});
				break;
			case OPTION.RAWDOC_INBOX_TICKET.id:
				this.aonInvoiceList({status: CONSTANT.INBOX, type: 'ticket'});
				break;
			case OPTION.RAWDOC_REJECT.id:
				this.aonInvoiceList({status: CONSTANT.REJECTED});
				break;
			case OPTION.RAWDOC_DRAFT.id:
				this.aonInvoiceList({status: CONSTANT.DRAFT});
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
				this.aonCustomerList();
				// GWT.load(GWT.CUSTOMER, this.getApplication().CONTENT);
				break;
			case OPTION.REGISTRY_SUPPLIER.id:
				this.aonSupplierList();
				//GWT.load(GWT.SUPPLIER, this.getApplication().CONTENT);
				break;
			case OPTION.REGISTRY_CREDITOR.id:
				this.aonCreditorList();
				//GWT.load(GWT.CREDITOR, this.getApplication().CONTENT);
				break;
			case OPTION.OFFER.id:
				break;
			case OPTION.CONCEPTS.id:
				break;
			case OPTION.PRODUCT.id:
				this.aonProductList({expense: false});
				break;
			case OPTION.EXPENSES.id:
				this.aonProductList({expense: true});
				break;
			case OPTION.INVEST.id:
				this.aonInvestList({});
				break;
			case OPTION.CONFIGURATION_PRINT.id:
				this.aonInvoicePrint();
				break;
			case OPTION.CONFIGURATION_SII_TBAI.id:
				break;
			case OPTION.REGISTRY.id:
				break;
			case OPTION.CHARGES_PAYMENTS.id:
				GWT.load(GWT.FINANCE, this.getApplication().CONTENT);
				break;
			case OPTION.VAT_PANEL.id:
				GWT.load(GWT.VAT_REPORT, this.getApplication().CONTENT);
				break;
			case OPTION.RETENTION_PANEL.id:
				GWT.load(GWT.IRPF_REPORT, this.getApplication().CONTENT);
				break;
			default:
				this.aonInvoiceList({status: CONSTANT.INBOX});
				break;
			}
		}
	}

}

window.customElements.define('aon-invoice-panel', AonInvoicePanel);
