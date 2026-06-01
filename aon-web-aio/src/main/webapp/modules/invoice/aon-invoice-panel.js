import { AonElement } from "../../components/AonElement.js";
import {
	insertInvoice, mobileAction, MOBILE_ACTION, selfconta, downloadInvoiceExcel,
	getBidoqToOCR, getBidoqToOCRCount, getInvoice, getInvoiceCount, getRawdocCount, invoiceDuplicateFix, saveInvoiceClosing, downloadRegistryExcel,
	checkBidoq,
	getCompanyActivities,
	getInvoiceConfiguration,
	fixInvoice
} from "../../services/service.js";
import { Invoice } from "./Invoice.js";
import { AonInvoice } from "./aon-invoice.js";
import { AonMobileInvoice } from "./aon-mobile-invoice.js";
import { MSG, MATERIAL_ICONS, CONSTANT, EVENT, TAG } from "../../environments/environments.js";
import { downscaleImage } from "../../services/compressImg.js";
import { AonProductList } from "../product/aon-product-list.js";
import { AonMobileProductList } from "../product/aon-mobile-product-list.js";
import { Apps, INVOICE } from "../../services/app.js";
import { AonProduct } from "../product/aon-product.js";
import { AonCustomerList } from "../registry/customer/aon-customer-list.js";
import { AonSupplierList } from "../registry/supplier/aon-supplier-list.js";
import { AonCreditorList } from "../registry/creditor/aon-creditor-list.js";
import { AonMobileSupplierList } from "../registry/supplier/aon-mobile-supplier-list.js";
import { AonMobileCreditorList } from "../registry/creditor/aon-mobile-creditor-list.js";
import { AonMobileCustomerList } from "../registry/customer/aon-mobile-customer-list.js";
import { AonCustomer } from "../registry/customer/aon-customer.js";
import { AonSupplier } from "../registry/supplier/aon-supplier.js";
import { AonCreditor } from "../registry/creditor/aon-creditor.js";
import { AonInvestList } from "../product/aon-invest-list.js";
import { AonInvest } from "../product/aon-invest.js";
import { AonSelect } from "../../components/aon-select.js";
import { AonUploadToast } from "../../components/aon-upload-toast.js";
import { AonInvoiceList } from "./aon-invoice-list.js";
import { AonMobileInvoiceList } from "./aon-mobile-invoice-list.js";
import { AonInvoiceHome } from "./aon-invoice-home.js";

import { FiscalUtils } from "../fiscal/FiscalUtils.js";

import * as ACTION from "../actions.js";
import * as OPTION from "./InvoiceOptions.js";
import * as LS from "../../services/localStorageService.js";
import * as GWT from '../../gwt/gwt.js';

import "./aon-invoice-print.js";
import "../../components/aon-application.js";
import "../../components/aon-dialog-menu.js";

import { getCounter, addCounter, clearCounter } from "./InvoiceCounter.js";
import { AonFutureTax } from "../fiscal/tax/aon-future-tax.js";
import { generateJobId } from "./InvoiceUtils.js";
import { getReader } from "../../services/utils.js";
import { AonImageEditor } from "../../components/aon-image-editor.js";
import { createSelect } from "../../components/CreateComponent.js";
import { AonInvoiceClosingList } from "./aon-invoice-closing-list.js";
import { AonIncomeList } from "./aon-income-list.js";
import { AonExpenseList } from "./aon-expense-list.js";
import { AonInvoiceProcessing } from "./aon-invoice-processing.js";
import { AonDialog } from "../../components/aon-dialog.js";
import { InvoiceCommunicationConfiguration } from "../../models/InvoiceCommunicationConfiguration.js";
import { isValid } from "../../services/documentUtils.js";
import { AonIncome } from "./aon-income.js";
import { Income } from "./Income.js";
import { Expense } from "./Expense.js";
import { AonExpense } from "./aon-expense.js";

export class AonInvoicePanel extends AonElement {
	selectedOption;
	filter;
	invofoxFilter;
	counterActive;

	INVOICE;
	INPUT_FILE;
	INPUT_CAMERA;
	PRODUCT_LIST;
	CUSTOMER_LIST;
	SUPPLIER_LIST;
	CREDITOR_LIST;
	INVEST_LIST;
	CLOSING_INVOICE_LIST;

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

	constructor() {
		super();
	}

	async connectedCallback() {
		this.initialize();
		this.innerHTML = `
			<aon-application id='${this.INVOICE}' title='${MSG.BILLING}' drag_and_drop='true'></aon-application>
			<aon-dialog-menu id='aonDialogAddOption'> </aon-dialog-menu>
			<input id='${this.INPUT_FILE}' style='display:none;' type='file' name='file' multiple>
			<input id='${this.INPUT_CAMERA}' type='file' accept='image/*' capture='camera' hidden />
		`;
		
		fixInvoice().then(() => { console.log("fixInvoice completed"); }).catch(() => { console.log("fixInvoice failed"); });

		await this.getInvoiceConfiguration();
		this.buildDur().then((r) => {
			this.build();
		});
		
	}

	initialize() {
		this.INVOICE = "aonInvoice";
		this.INPUT_FILE = this.INVOICE + "InputFile";
		this.INPUT_CAMERA = "aonMobileMenuCameraInput"; // this.INVOICE + 'InputCamera';
		this.PRODUCT_LIST = this.INVOICE + "ProductList";
		this.CUSTOMER_LIST = this.INVOICE + "CustomerList";
		this.SUPPLIER_LIST = this.INVOICE + "SupplierList";
		this.CREDITOR_LIST = this.INVOICE + "CreditorList";
		this.INVEST_LIST = this.INVOICE + "InvestList";
		this.CLOSING_INVOICE_LIST = this.INVOICE + "ClosingInvoiceList";

		this.status = this.status || CONSTANT.INBOX;
		this.filter = {
			status: this.status || CONSTANT.INBOX,
			page: 0,
			per_page: 50,
		};
		this.counterActive = true;

		this.option =
			this.option ||
			(CONSTANT.REJECTED === this.status
				? OPTION.RAWDOC_REJECT
				: OPTION.RAWDOC_INBOX);
	}

	getFilter() {
		return this.filter;
	}

	build() {
		this.checkConfiguration();

		let aonInvoice = this.getElement(this.INVOICE);

		let input = this.getElement(this.INPUT_FILE);
		input.addEventListener(EVENT.CHANGE, () =>
			this.upload(input.files)
		);

		let inputCamera = this.getElement(this.INPUT_CAMERA);
		inputCamera.addEventListener(EVENT.CHANGE, ({ target }) =>
			this.uploadCamera(target.files)
		);

		aonInvoice.addEventListener(EVENT.AON_APPLICATION_DROP, (e) =>
			this.upload(e.detail)
		);
		this.buildInvoiceHomeToolbarOptions();
		this.buildSidenavOptions();
		if (this.invoice && this.invoice.type) {
			this.aonInvoice(this.invoice.type, this.invoice);
		} else if (this.value) {
			this.aonInvoiceById(this.value);
		} else this.aonInvoiceHome();

		this.dispatchEvent(new CustomEvent(EVENT.BUILD, { panel: this }));
	}

	checkConfiguration() {		
		if(!this.getDur().hasScopes()) {
			this.getApplication().showMessageError("El usuario no tiene ámbitos asignados. Por favor, contacte con el administrador del dominio.");
			return false;
		}
		if(!this.config || !this.config.company || !this.config.company.document || !this.config.communication){
			this.getApplication().showMessageError("La configuración de facturación no está completa. Por favor, revise la configuración.");
			return false;
		} 
		if(!isValid(this.config.company.document)) {
			this.getApplication().showMessageError("La configuración de facturación no está completa. El NIF/CIF de la empresa no es válido.");
			return false;
		}
		// if(isPersonaFisica(this.config.company.document) && !(this.config.company.person || this.config.person)) {
		// 	this.getApplication().showMessageError("La configuración de facturación no está completa. Es obligatorio rellenar todos los datos de la persona física.");
		// 	return false;
		// }
		return true;
	}

	async getInvoiceConfiguration() {
		this.config = await getInvoiceConfiguration();
		this.icc = new InvoiceCommunicationConfiguration(this.config.communication);
	}
	buildEmptyToolbarOptions() {
		this.clearToolbar();
	}

	async buildInvoiceHomeToolbarOptions() {
		this.clearToolbar();
		if (!this.isMobile()) {
			this.getApplication().addToolbarOption2(ACTION.ADD_INVOICE, () => this.addInvoice());
			this.getApplication().addToolbarOption2(ACTION.REFRESH, () => this.refreshInvoicePanel());
			if (this.getDur().isOcr() || this.getDur().isInvofox())
				this.getApplication().addToolbarOption2(ACTION.UPLOAD_FILE, () => this.addInvoiceFile());
			// BIDOQ INTEGRATION
			if (this.getDur().isBidoq() && this.getDur().isInvoiceManager()) {
				this.getApplication().addToolbarOption2(ACTION.BIDOQ_IMPORT, () => this.importBidoqDocumentsToAon());
			}
		} else {
			this.getApplication().removeFloatOption();
			this.getApplication().addFloatOption(ACTION.ADD_INVOICE, () => this.addInvoice());
		}
	}



	buildInvoiceToolbarOptions(acceptedInvoices, processing) {
		this.clearToolbar();
		if (!this.isMobile()) {
			this.getApplication().addToolbarOption2(ACTION.ADD_INVOICE, () => this.addInvoice());
			this.getApplication().addToolbarOption2(ACTION.REFRESH, () => this.refreshInvoicePanel());
			if (this.getDur().isOcr() || this.getDur().isInvofox())
				this.getApplication().addToolbarOption2(ACTION.UPLOAD_FILE, () => this.addInvoiceFile());
			if (acceptedInvoices) this.getApplication().addToolbarOption2(ACTION.DOWNLOAD_EXCEL_INVOICE, () => this.downloadInvoiceExcel());
			if (this.isConsole()) this.getApplication().addToolbarOption('FIX', 'healing', () => invoiceDuplicateFix());
		} else {
			this.getApplication().removeFloatOption();
			this.getApplication().addFloatOption(ACTION.ADD_INVOICE, () => this.addInvoice());
		}
		this.buildToolbarSearchOption(acceptedInvoices);
	}

	buildProductToolbarOptions() {
		this.clearToolbar();
		if (!this.isMobile()) {
			this.getApplication().addToolbarOption2(ACTION.ADD_PRODUCT, () => this.addProduct());
		}
		// TODO ACTIVAR CUANDO ESTE LA OPCIÓN DE AÑADIR PRODUCTO EN EL MÓVIL
		// else {
		//   this.getApplication().removeFloatOption();
		//   this.getApplication().addFloatOption(ACTION.ADD_PRODUCT, () => this.addProduct());
		// }

		this.buildToolbarSearchOption();
	}

	buildExpenseToolbarOptions() {
		this.clearToolbar();
		if (!this.isMobile()) {
			this.getApplication().addToolbarOption2(ACTION.ADD_EXPENSE, () => this.addExpense());
		}
		// TODO ACTIVAR CUANDO ESTE LA OPCIÓN DE AÑADIR GASTO EN EL MÓVIL
		// else {
		//   this.getApplication().removeFloatOption();
		//   this.getApplication().addFloatOption(ACTION.ADD_EXPENSE, () => this.addExpense());
		// }
		this.buildToolbarSearchOption();
	}

	buildInvestToolbarOptions() {
		this.clearToolbar();
		if (!this.isMobile()) {
			this.getApplication().addToolbarOption2(ACTION.ADD_INVEST_ASSET, () => this.addInvest());
		}
		// TODO ACTIVAR CUANDO ESTE LA OPCIÓN DE AÑADIR BIEN AFECTO EN EL MÓVIL
		// else {
		//   this.getApplication().removeFloatOption();
		//   this.getApplication().addFloatOption(ACTION.ADD_INVEST_ASSET, () => this.addInvest());
		// }

		this.buildToolbarSearchOption();
	}

	buildClosingInvoiceToolbarOptions() {
		this.clearToolbar();
	}

	buildCustomerToolbarOptions() {
		this.clearToolbar();
		if (!this.isMobile()) {
			this.getApplication().addToolbarOption2(ACTION.ADD_CUSTOMER, () => this.addCustomer());
		}
		const isUdapa = this.getDur().getDomain().getName().includes("udapa") || this.getDur().getDomain().getName().includes("paturpat");
		if (isUdapa) this.getApplication().addToolbarOption2(ACTION.DOWNLOAD_EXCEL, () => this.downloadRegistryExcel('customer'));

		// TODO ACTIVAR CUANDO ESTE LA OPCIÓN DE AÑADIR CLIENTE EN EL MÓVIL
		// else {
		//   this.getApplication().removeFloatOption();
		//   this.getApplication().addFloatOption(ACTION.ADD_CUSTOMER, () => this.addCustomer());
		// }
		this.buildToolbarSearchOption();
	}

	buildSupplierToolbarOptions() {
		this.clearToolbar();
		if (!this.isMobile()) {
			this.getApplication().addToolbarOption2(ACTION.ADD_SUPPLIER, () => this.addSupplier());
		}
		const isUdapa = this.getDur().getDomain().getName().includes("udapa") || this.getDur().getDomain().getName().includes("paturpat");
		if (isUdapa) this.getApplication().addToolbarOption2(ACTION.DOWNLOAD_EXCEL, () => this.downloadRegistryExcel('supplier'));
		// TODO ACTIVAR CUANDO ESTE LA OPCIÓN DE AÑADIR PROVEEDOR EN EL MÓVIL
		// else {
		//   this.getApplication().removeFloatOption();
		//   this.getApplication().addFloatOption(ACTION.ADD_SUPPLIER, () => this.addSupplier());
		// }
		this.buildToolbarSearchOption();
	}

	buildCreditorToolbarOptions() {
		this.clearToolbar();
		if (!this.isMobile()) {
			this.getApplication().addToolbarOption2(ACTION.ADD_CREDITOR, () => this.addCreditor());
		}
		const isUdapa = this.getDur().getDomain().getName().includes("udapa") || this.getDur().getDomain().getName().includes("paturpat");
		if (isUdapa) this.getApplication().addToolbarOption2(ACTION.DOWNLOAD_EXCEL, () => this.downloadRegistryExcel('creditor'));
		// TODO ACTIVAR CUANDO ESTE LA OPCIÓN DE AÑADIR ACREEDOR EN EL MÓVIL
		// else {
		//   this.getApplication().removeFloatOption();
		//   this.getApplication().addFloatOption(ACTION.ADD_CREDITOR, () => this.addCreditor());
		// }
		this.buildToolbarSearchOption();
	}

	buildIncomeToolbarOptions() {
		this.clearToolbar();
	}

	buildExpenseToolbarOptions() {
		this.clearToolbar();
	}

	clearToolbar() {
		let aonInvoice = this.getApplication();
		let toolbar = this.getElement(aonInvoice.TOOLBAR);
		toolbar.removeButtons();
	}

	buildToolbarSearchOption(acceptedInvoices) {
		const btnSearch = this.getApplication().addSearchOption(!this.isMobile());
		let searchFn = (event) => this.search(event.detail);
		btnSearch.addEventListener(EVENT.SEARCH_NEW, searchFn);
		if (acceptedInvoices) {
			btnSearch.buildOptionsFilter(OPTION.INVOICE_SEARCH_OPTIONS);

			this.getElement('recorded').setOptions([
				{ name: "-", value: undefined },
				{ name: MSG.PENDING, value: "PENDING" },
				{ name: MSG.ACCOUNTED, value: "SCORED" },
			]);
		}
	}

	downloadInvoiceExcel() {
		if(!this.checkConfiguration()) return;
		let aonInvoiceTable = document.getElementById("aonInvoiceTable");

		let data = {
			domainId: localStorage.getItem("aon_domain_id"),
			domainName: localStorage.getItem("aon_domain_name"),
			domainLogin: localStorage.getItem("aon_domain_login"),
			ids: aonInvoiceTable.selected.map((r) => r.id),
			description: this.getFilter().description,
			status: this.getFilter().status,
			type: this.getFilter().type,
			from: this.getFilter().from,
			to: this.getFilter().to,
		};
		let json = btoa(JSON.stringify(data));
		downloadInvoiceExcel(json);
	}

	buildSidenavOptions() {
		if (this.isMobile()) {
			this.getApplication().addMobileSidenavHeader(Apps.INVOICE);
		}

		this.getApplication().addEventListener(EVENT.SELECT_OPTION, (e) => {
			this.selectOption(e.detail);
		});

		OPTION.getOptions(this.getDur().isTrial()).forEach((option) => {
			option.app = INVOICE;
			this.getApplication().addSidenavOptions3(option);
		});

		OPTION.getFutureOptions(this.getDur().isTrial()).forEach((option) => {
			option.app = INVOICE;
			this.getApplication().addSidenavOptions3(option);
		});

		this.buildCounter();
	}

	buildCounter() {
		if (this.counterActive) {
			this.counterActive = false;
			clearCounter();
			this.invoiceCounter();
		}
	}

	invoiceCounter() {
		getRawdocCount({}).then((r) => {
			this.counterActive = true;

			// FACTURAS EMITIDAS 
			if (r.invoice && r.invoice.emitida && r.invoice.emitida > 0) {
				addCounter(OPTION.INVOICE_ISSUED_BETA, r.invoice.emitida);
				addCounter(OPTION.FUTURE_INVOICE_ISSUED_BETA, r.invoice.emitida);
			}
			this.updateCounterSpan(OPTION.INVOICE_ISSUED_BETA);
			this.updateCounterSpan(OPTION.FUTURE_INVOICE_ISSUED_BETA);

			// FACTURAS RECIBIDAS
			if (r.invoice && r.invoice.recibida && r.invoice.recibida > 0) {
				addCounter(OPTION.INVOICE_RECEIVED_BETA, r.invoice.recibida);
				addCounter(OPTION.FUTURE_INVOICE_RECEIVED_BETA, r.invoice.recibida);
			}
			this.updateCounterSpan(OPTION.INVOICE_RECEIVED_BETA);
			this.updateCounterSpan(OPTION.FUTURE_INVOICE_RECEIVED_BETA);

			// FACTURAS SIMPLIFICADAS / TICKETS
			if (r.invoice && r.invoice.ticket && r.invoice.ticket > 0) {
				addCounter(OPTION.INVOICE_TICKET, r.invoice.ticket);
				addCounter(OPTION.FUTURE_INVOICE_TICKET, r.invoice.ticket);
			}
			this.updateCounterSpan(OPTION.INVOICE_TICKET);
			this.updateCounterSpan(OPTION.FUTURE_INVOICE_TICKET);

			// if (r && r.rawdoc && r.rawdoc.inbox && r.rawdoc.inbox.count && r.rawdoc.inbox.count > 0) {
			//   addCounter(OPTION.INVOICE_PENDINGS, r.rawdoc.inbox.count);
			// }
			// this.updateCounterSpan(OPTION.INVOICE_PENDINGS);

			// BORRADOR/PROFORMAS
			if (r && r.rawdoc && r.rawdoc.inbox && r.rawdoc.inbox.OUTPUT && r.rawdoc.inbox.OUTPUT > 0) {
				addCounter(OPTION.PROFORMA_INVOICES, r.rawdoc.inbox.OUTPUT);
				addCounter(OPTION.FUTURE_PROFORMA_INVOICES, r.rawdoc.inbox.OUTPUT);
			}
			this.updateCounterSpan(OPTION.PROFORMA_INVOICES);
			this.updateCounterSpan(OPTION.FUTURE_PROFORMA_INVOICES);

			// BORRADOR FACTURAS RECIBIDAS
			if (r && r.rawdoc && r.rawdoc.inbox && r.rawdoc.inbox.INPUT && r.rawdoc.inbox.INPUT > 0) {
				addCounter(OPTION.RAWDOC_INBOX_RECEIVED_NEW, r.rawdoc.inbox.INPUT);
				addCounter(OPTION.FUTURE_RAWDOC_INBOX_RECEIVED_NEW, r.rawdoc.inbox.INPUT);
			}
			this.updateCounterSpan(OPTION.RAWDOC_INBOX_RECEIVED_NEW);
			this.updateCounterSpan(OPTION.FUTURE_RAWDOC_INBOX_RECEIVED_NEW);

			// BORRADOR FACTURAS SIMPLIFICADAS / TICKETS
			if (r && r.rawdoc && r.rawdoc.inbox && r.rawdoc.inbox.TICKET && r.rawdoc.inbox.TICKET > 0) {
				addCounter(OPTION.RAWDOC_INBOX_TICKET_NEW, r.rawdoc.inbox.TICKET);
				addCounter(OPTION.FUTURE_RAWDOC_INBOX_TICKET_NEW, r.rawdoc.inbox.TICKET);
			}
			this.updateCounterSpan(OPTION.RAWDOC_INBOX_TICKET_NEW);
			this.updateCounterSpan(OPTION.FUTURE_RAWDOC_INBOX_TICKET_NEW);

			// EN TRAMITE
			if (r && r.rawdoc && r.rawdoc.processing && r.rawdoc.processing.count && r.rawdoc.processing.count > 0) {
				addCounter(OPTION.RAWDOC_PROCESSING, r.rawdoc.processing.count);
				addCounter(OPTION.FUTURE_RAWDOC_PROCESSING, r.rawdoc.processing.count);
			}
			if (r && r.rawdoc && r.rawdoc.processed && r.rawdoc.processed.count && r.rawdoc.processed.count > 0) {
				addCounter(OPTION.RAWDOC_PROCESSING, r.rawdoc.processed.count);
				addCounter(OPTION.FUTURE_RAWDOC_PROCESSING, r.rawdoc.processed.count);
			}
			this.updateCounterSpan(OPTION.RAWDOC_PROCESSING);
			this.updateCounterSpan(OPTION.FUTURE_RAWDOC_PROCESSING);
			
			// A REVISAR
			if (r && r.rawdoc && r.rawdoc.rejected && r.rawdoc.rejected.count && r.rawdoc.rejected.count > 0) {
				addCounter(OPTION.RAWDOC_REJECT, r.rawdoc.rejected.count);
				addCounter(OPTION.FUTURE_RAWDOC_REJECT, r.rawdoc.rejected.count);
			}
			this.updateCounterSpan(OPTION.RAWDOC_REJECT);
			this.updateCounterSpan(OPTION.FUTURE_RAWDOC_REJECT);

			// PAPELERA
			if (r && r.rawdoc && r.rawdoc.trash && r.rawdoc.trash.count && r.rawdoc.trash.count > 0) {
				addCounter(OPTION.RAWDOC_TRASH, r.rawdoc.trash.count);
				addCounter(OPTION.FUTURE_RAWDOC_TRASH, r.rawdoc.trash.count);
			}
			this.updateCounterSpan(OPTION.RAWDOC_TRASH);
			this.updateCounterSpan(OPTION.FUTURE_RAWDOC_TRASH);

			this.updateCounterHome();
		});
	}

	updateCounterSpan(option) {
		let span = document.getElementById("aonMenuItemSpan" + option.id);
		if (span) {
			let count = getCounter()[option.id];
			span.innerHTML = count > 0 ? option.name + " (" + count + ")" : option.name;
			if (count > 0) span.style.fontWeight = "bold";
		} else setTimeout(this.updateCounterSpan, 100, option);
	}

	updateCounterHome() {
		// FACTURAS

		let issued = getCounter()[OPTION.INVOICE_ISSUED_BETA.id] || 0;
		let invoiceIssuedNumber = this.getElement("invoiceIssuedNumber");
		if (invoiceIssuedNumber) invoiceIssuedNumber.innerHTML = issued;

		let received = getCounter()[OPTION.INVOICE_RECEIVED_BETA.id] || 0;
		let invoiceReceivedNumber = this.getElement("invoiceReceivedNumber");
		if (invoiceReceivedNumber) invoiceReceivedNumber.innerHTML = received;

		let ticket = getCounter()[OPTION.INVOICE_TICKET.id] || 0;
		let invoiceTicketNumber = this.getElement("invoiceTicketNumber");
		if (invoiceTicketNumber) invoiceTicketNumber.innerHTML = ticket;

		// BORRADORES

		let pendingIssuedCounter = getCounter()[OPTION.PROFORMA_INVOICES.id] || 0;
		let pendingIssuedNumber = this.getElement("pendingIssuedNumber");
		if (pendingIssuedNumber) pendingIssuedNumber.innerHTML = pendingIssuedCounter;

		let pendingReceivedCounter = getCounter()[OPTION.RAWDOC_INBOX_RECEIVED_NEW.id] || 0;
		let pendingReceivedNumber = this.getElement("pendingReceivedNumber");
		if (pendingReceivedNumber) pendingReceivedNumber.innerHTML = pendingReceivedCounter;

		let pendingTicketCounter = getCounter()[OPTION.RAWDOC_INBOX_TICKET_NEW.id] || 0;
		let pendingTicketNumber = this.getElement("pendingTicketNumber");
		if (pendingTicketNumber) pendingTicketNumber.innerHTML = pendingTicketCounter;

		// PENDIENTES

		let processingCounter = getCounter()[OPTION.RAWDOC_PROCESSING.id] || 0;
		let processingNumber = this.getElement("processingNumber");
		if (processingNumber) processingNumber.innerHTML = processingCounter;

		let rejectedCounter = getCounter()[OPTION.RAWDOC_REJECT.id] || 0;
		let rejectedNumber = this.getElement("rejectedNumber");
		if (rejectedNumber) rejectedNumber.innerHTML = rejectedCounter;

		let trash = getCounter()[OPTION.RAWDOC_TRASH.id] || 0;
		let trashNumber = this.getElement("trashNumber");
		if (trashNumber) trashNumber.innerHTML = trash;
	}

	search(detail) {
		let value = detail.search;
		if (this.selectedOption && OPTION.REGISTRY_CREDITOR.id === this.selectedOption.id) {
			this.aonCreditorList({ page: 1, perPage: 50, value });
		} else if (this.selectedOption && OPTION.REGISTRY_SUPPLIER.id === this.selectedOption.id) {
			this.aonSupplierList({ page: 1, perPage: 50, value });
		} else if (this.selectedOption && OPTION.REGISTRY_CUSTOMER.id === this.selectedOption.id) {
			this.aonCustomerList({ page: 1, perPage: 50, value });
		} else if (this.selectedOption && OPTION.PRODUCT.id === this.selectedOption.id) {
			this.aonProductList({ expense: false, value });
		} else if (this.selectedOption && OPTION.EXPENSES.id === this.selectedOption.id) {
			this.aonProductList({ expense: true, value });
		} else if (this.selectedOption && OPTION.INVEST.id === this.selectedOption.id) {
			this.aonInvestList({ value });
		} else {
			this.filter.description = value;
			if (detail.recorded) this.filter.recorded = detail.recorded;
			this.filter.from = detail.startDate;
			this.filter.to = detail.endDate;
			this.filter.page = 1;
			this.filter.perPage = 50;
			this.aonInvoiceList(this.filter, this.invofoxFilter);
		}
	}


	aonInvoiceProcessing() {
		if(!this.checkConfiguration()) return;
		this.getApplication().setContent(new AonInvoiceProcessing());
	}


	aonInvoiceList(filter, invofoxFilter) {
		if(!this.checkConfiguration()) return;
		this.filter = filter;
		this.invofoxFilter = invofoxFilter;
		let table = this.isMobile()
			? new AonMobileInvoiceList()
			: new AonInvoiceList();
		table.id = "aonInvoiceList";
		table.setIcc(this.icc);
		table.setFilter(this.filter);
		table.invofoxFilter = this.invofoxFilter;
		this.getApplication().setContent(table);
		this.getApplication().buildDragAndDrop(true);
	}


	aonNewIncome(){
		let inc = new AonIncome( new Income() );
		this.getApplication().setContent(inc);
	}

	aonIncome() {
		if(!this.checkConfiguration()) return;
		let table = new AonIncomeList();
		this.getApplication().setContent(table);
	}

	aonNewExpense(){
		let inc = new AonExpense( new Expense() );
		this.getApplication().setContent(inc);
	}

	aonExpense() {
		if(!this.checkConfiguration()) return;
		let table = new AonExpenseList();
		this.getApplication().setContent(table);
	}

	aonCustomerList(filter) {
		if(!this.checkConfiguration()) return;
		let customerList = this.getElement(this.CUSTOMER_LIST);
		if (customerList) {
			customerList.setFilter(filter);
		} else {
			customerList = this.isMobile()
				? new AonMobileCustomerList()
				: new AonCustomerList();
			customerList.id = this.CUSTOMER_LIST;
			customerList.filter = filter;
			this.getApplication().setContent(customerList);
		}
	}

	aonSupplierList(filter) {
		if(!this.checkConfiguration()) return;
		let supplierList = this.getElement(this.SUPPLIER_LIST);
		if (supplierList) {
			supplierList.setFilter(filter);
		} else {
			supplierList = this.isMobile()
				? new AonMobileSupplierList()
				: new AonSupplierList();
			supplierList.id = this.SUPPLIER_LIST;
			supplierList.filter = filter;
			this.getApplication().setContent(supplierList);
		}
	}

	aonCreditorList(filter) {
		if(!this.checkConfiguration()) return;
		let creditorList = this.getElement(this.CREDITOR_LIST);
		if (creditorList) {
			creditorList.setFilter(filter);
		} else {
			creditorList = this.isMobile()
				? new AonMobileCreditorList()
				: new AonCreditorList();
			creditorList.id = this.CREDITOR_LIST;
			creditorList.filter = filter;
			this.getApplication().setContent(creditorList);
		}
	}

	aonProductList(filter) {
		if(!this.checkConfiguration()) return;
		let productList = this.getElement(this.PRODUCT_LIST);
		if (productList) {
			productList.setFilter(filter);
		} else {
			productList = this.isMobile()
				? new AonMobileProductList()
				: new AonProductList();
			productList.id = this.PRODUCT_LIST;
			productList.filter = filter;
			this.getApplication().setContent(productList);
		}
	}

	aonInvestList(filter) {
		if(!this.checkConfiguration()) return;
		let investList = this.getElement(this.INVEST_LIST);
		if (investList) {
			investList.setFilter(filter);
		} else {
			investList = new AonInvestList();
			investList.id = this.INVEST_LIST;
			investList.filter = filter;
			this.getApplication().setContent(investList);
		}
	}

	aonClosingInvoiceList(filter) {
		let closingInvoiceList = new AonInvoiceClosingList();
		closingInvoiceList.id = this.CLOSING_INVOICE_LIST;
		this.getApplication().setContent(closingInvoiceList);
	}

	addCustomer() {
		if(!this.checkConfiguration()) return;
		let aonCustomer = new AonCustomer();
		aonCustomer.id = this.id + "Customer";
		this.getApplication().setContent(aonCustomer);
	}

	addSupplier() {
		if(!this.checkConfiguration()) return;
		let aonSupplier = new AonSupplier();
		aonSupplier.id = this.id + "Supplier";
		aonSupplier.setSupplier();
		this.getApplication().setContent(aonSupplier);
	}

	addCreditor() {
		if(!this.checkConfiguration()) return;
		let aonCreditor = new AonCreditor();
		aonCreditor.id = this.id + "Creditor";
		aonCreditor.setCreditor();
		this.getApplication().setContent(aonCreditor);
	}

	addProduct() {
		if(!this.checkConfiguration()) return;
		let aonProduct = new AonProduct();
		aonProduct.id = this.id + "Product";
		this.getApplication().setContent(aonProduct);
	}

	addExpense() {
		if(!this.checkConfiguration()) return;
		let aonProduct = new AonProduct();
		aonProduct.id = this.id + "Expense";
		aonProduct.expense = true;
		this.getApplication().setContent(aonProduct);
	}

	addInvest() {
		if(!this.checkConfiguration()) return;
		let aonInvest = new AonInvest();
		aonInvest.id = this.id + "Invest";
		this.getApplication().setContent(aonInvest);
	}

	async addInvoice() {
		if(!this.checkConfiguration()) return;
		// Check trial limit
		const result = await getInvoiceCount();
		
		if (this.getDur().isTrial() && result.invoiceCount >= this.getDur().getTrialValue()) {
			this.getApplication().confirmDialog(
				"Límite alcanzado",
				"Ha alcanzado el límite de prueba del módulo de facturación. Para poder registrar nuevas facturas debe ampliar su plan actual. ¿Desea navegar a los planes disponibles?",
				async () => {
					GWT.iLoad(GWT.PRODUCT_CATALOGUE_MODULE);
				}
			);
			return;
		}
		
		let ayudat = this.getDur().isSelfconta();
		let aonInvoice = this.getApplication();
		let aonInvoiceToolbar = this.getElement(aonInvoice.TOOLBAR);
		let button = this.isMobile()
			? this.getElement("aonInvoiceAddInvoiceButton")
			: this.getElement(aonInvoiceToolbar.TOOL_SECTION + ACTION.ADD_INVOICE.id + "Button");

		let height = window.innerHeight;
		let top = button.getBoundingClientRect().top;
		const left = button.getBoundingClientRect().left;

		if (height - top < height / 2) {
			top = top - (ayudat ? 205 : 170);
		}

		let d = document.getElementById("aonDialogAddOption");

		let options = OPTION.getNewOptions();
		if (ayudat) {
			let importSelfconta = {
				name: "Importación Selfconta",
				title: "Importación Selfconta",
				icon: "import_export",
				permission: ayudat,
				backgroundColor: "#4472C4",
				fn: () => this.importSelfconta(),
			};
			options.push(importSelfconta);
		}

		if (this.isMobile()) {
			let uploadFile = {
				name: MSG.UPLOAD_FILE,
				title: MSG.UPLOAD_FILE,
				icon: MATERIAL_ICONS.FILE_UPLOAD,
				permission: true,
				backgroundColor: "#4472C4",
				fn: () => this.addInvoiceFile(),
			};

			options.push(uploadFile);

			let openCamera = {
				name: "Camara",
				title: "Camara",
				icon: "camera_alt",
				permission: true,
				backgroundColor: "#4472C4",
				fn: () => {
					if (UA.isApp()) {
						let ionicData = { action: MOBILE_ACTION.CAMERA, id: this.INPUT_CAMERA, selector: 'aon-invoice-panel' };
						openCamera(ionicData, (result) => {
							this.buildInvoiceImageEditor(result);
						});
					} else this.openCamera();
				}
			};
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
		div2.innerHTML = "¿Desea importar las facturas?";
		div.appendChild(div2);

		let yearSelect = new AonSelect();
		yearSelect.id = "aonInvoiceSelfcontaYear";
		yearSelect.title = MSG.YEAR;
		yearSelect.options = JSON.stringify([
			{ name: "2024", value: 2024 },
			{ name: "2023", value: 2023 },
			{ name: "2022", value: 2022 },
			{ name: "2021", value: 2021 },
			{ name: "2020", value: 2020 },
			{ name: "2019", value: 2019 },
			{ name: "2018", value: 2018 },
		]);
		div.appendChild(yearSelect);

		let aonApplication = this.getApplication();
		let d = aonApplication.getDialog();
		d.clear();
		if (!this.isMobile()) d.width = "400px";
		d.setTitle(MSG.IMPORT);
		d.setContent(div);
		d.addAcceptAction(async () => {
			aonApplication.startLoading();
			try {
				await selfconta(yearSelect.value);
				this.showToast({
					type: "success",
					message: "Datos Importados. Revisa las facturas rechazadas.",
				});
			} catch (error) {
				this.showToast(error);
			}
			aonApplication.stopLoading();
		});
		d.open();
	}

	async openCamera() {
		const isApp = await mobileAction({
			action: MOBILE_ACTION.CAMERA,
			id: this.INPUT_CAMERA,
			selector: "aon-invoice-panel",
		});
		if (!isApp) this.getElement(this.INPUT_CAMERA).click();
	}

	async receiveAppImage(file) {
		if (file.contentType.indexOf("image") >= 0) {
			//compress 500kB / file, 500kb, quality default 0.9, maxResolution 1280
			const f = await downscaleImage(file, undefined, undefined, undefined);
			const data = {
				file: f,
				invoice: new Invoice().setType("recibida"),
			};
			let aonInvoice = document.getElementById("aonInvoice");
			aonInvoice.startLoader();
			await insertInvoice(data);
			this.aonInvoiceList({ status: "inbox" });
			aonInvoice.stopLoader();
		}
	}

	closingInvoice() {
		let closing = this.createDiv();
		let closingPeriod = createSelect("aonInvoiceClosingPeriod", MSG.PERIOD);
		closingPeriod.options = JSON.stringify([
			{ name: MSG.JANUARY, value: "01" }, { name: MSG.FEBRUARY, value: "02" }, { name: MSG.MARCH, value: "03" },
			{ name: MSG.APRIL, value: "04" }, { name: MSG.MAY, value: "05" }, { name: MSG.JUNE, value: "06" },
			{ name: MSG.JULY, value: "07" }, { name: MSG.AUGUST, value: "08" }, { name: MSG.SEPTEMBER, value: "09" },
			{ name: MSG.OCTOBER, value: "10" }, { name: MSG.NOVEMBER, value: "11" }, { name: MSG.DECEMBER, value: "12" },
			{ name: "1 Trimestre", value: "1T" }, { name: "2 Trimestre", value: "2T" }, { name: "3 Trimestre", value: "3T" },
			{ name: "4 Trimestre", value: "4T" }
		]);
		closing.appendChild(closingPeriod)
		let closingYear = createSelect("aonInvoiceClosingYear", MSG.YEAR);
		closingYear.options = JSON.stringify([{ name: "2024", value: 2024 }]);
		closing.appendChild(closingYear);

		let d = this.getApplication().getDialog();
		d.clear();
		if (!this.isMobile()) d.width = '400px';
		d.setTitle(MSG.CLOSE_RECEIVED_INVOICES);
		d.setContent(closing);
		d.addAcceptAction(() => {
			let data = {
				period: closingPeriod.value,
				year: closingYear.value
			};
			this.closingInvoiceConfirm1(data);
		});
		d.open();
	}

	closingInvoiceConfirm1(data) {
		let d = this.getApplication().getDialog();
		d.clear();
		if (!this.isMobile()) d.width = '400px';
		d.setTitle(MSG.CLOSE_RECEIVED_INVOICES);
		d.setContentHTML(`¿Está seguro que quiere cerrar el periodo entre la fecha ${this.getStartDate(data)} e ${this.getEndDate(data)}?`);
		d.addAcceptAction(() => {
			this.closingInvoiceConfirm2(data);
		});
		d.open();
	}

	closingInvoiceConfirm2(data) {
		let d = this.getApplication().getDialog();
		d.clear();
		if (!this.isMobile()) d.width = '400px';
		d.setTitle(MSG.CLOSE_RECEIVED_INVOICES);
		d.setContentHTML(`Va a cerrar el periodo comprendido entre la fecha  ${this.getStartDate(data)} e ${this.getEndDate(data)}, ¿Está seguro?`);
		d.addAcceptAction(() => {
			saveInvoiceClosing(data)
				.then(r => this.showToast({
					type: "success",
					message: "El cierre se ha realizado correctamente.",
				}))
				.catch(error => this.showToast(error));
		});
		d.open();
	}

	getStartDate(data) {
		let period = data.period;
		let year = data.year;

		if (period === '1T') return `01/01/${year}`;
		else if (period === '2T') return `01/04/${year}`;
		else if (period === '3T') return `01/07/${year}`;
		else if (period === '4T') return `01/10/${year}`;
		else return `01/${period}/${year}`;
	}

	getEndDate(data) {
		let period = data.period;
		let year = data.year;

		if (period === '1T') return `31/03/${year}`;
		else if (period === '2T') return `31/06/${year}`;
		else if (period === '3T') return `30/09/${year}`;
		else if (period === '4T') return `31/12/${year}`;
		else return `30/${period}/${year}`;
	}

	refreshInvoicePanel() {
		this.buildInvoiceHomeToolbarOptions();
		this.aonInvoiceHome();
		this.buildCounter();
	}

	addInvoiceFile() {
		if(!this.checkConfiguration()) return;
		this.getElement(this.INPUT_FILE).click();
	}

	upload(files) {
		getCompanyActivities({}).then(activities => {
			let data = { uploaded: 0 };
			if (activities.length > 1) {
				activities.push({
					id: "all",
					description: "TODAS"
				});
				let activity = createSelect(this.ACTIVITY, MSG.ACTIVITY);
				activity.setAlias("id", "description");
				if (activities.length > 0) {
					activity.setOptions(activities);
					activity.value = activities[0].id;
				}

				let d = new AonDialog();
				let rootPanel = document.getElementById("rootPanel");
				rootPanel.appendChild(d);
				d.clear();

				d.setTitle(MSG.UPLOAD_INVOICE);
				d.setContent(activity);
				d.addAcceptAction(() => {
					data.activity = activity.getValueObject().id;
					let uploadToast = this.getElement('aonUploadToast');
					if (!uploadToast) {
						uploadToast = new AonUploadToast();
						this.appendChild(uploadToast);
					}

					uploadToast.setJobId(generateJobId());
					for (let file of files) {
						uploadToast.addFile("invoice", file, data);
					}
				});
				d.open();
			} else {
				if (activities.length > 0) {
					data.activity = activities[0].id;
				}
				let uploadToast = this.getElement('aonUploadToast');
				if (!uploadToast) {
					uploadToast = new AonUploadToast();
					this.appendChild(uploadToast);
				}
				uploadToast.setJobId(generateJobId());
				for (let file of files) {
					uploadToast.addFile("invoice", file, data);
				}
			}
		});
	}

	uploadCamera(files) {
		getReader(files[0]).then(file => {
			this.buildInvoiceImageEditor(file);
		}).catch(() => null);
	}

	downloadRegistryExcel(type) {
		if(!this.checkConfiguration()) return;
		let data = {
			domainId: LS.getDomainId(),
			domainName: LS.getDomainName(),
			domainLogin: LS.getDomainLogin(),
			type
		};
		let json = btoa(JSON.stringify(data));
		downloadRegistryExcel(json);
	}

	buildInvoiceImageEditor(file) {
		let editor = new AonImageEditor();
		editor.setImage("data:image/jpeg;base64," + file.content);
		editor.addEventListener(EVENT.CROPPER, (e) => {
			let uploadToast = this.getElement('aonUploadToast');
			if (!uploadToast) {
				uploadToast = new AonUploadToast();
				this.appendChild(uploadToast);
			}
			let data = { uploaded: 0, prefix: 'CM' };
			uploadToast.setJobId(generateJobId());
			uploadToast.addFile("invoice", e.detail, data);

			this.rootPanel(new AonInvoicePanel());
		});
		this.rootPanel(editor);
	}

	aonInvoice(type, invoice) {
		if(!this.checkConfiguration()) return;
		let aonInvoice = this.getApplication();
		if (this.isMobile() && aonInvoice.TOOLBAR) {
			let toolbar = this.getElement(aonInvoice.TOOLBAR);
			toolbar.removeButtons();
		}
		let component = this.isMobile() ? new AonMobileInvoice() : new AonInvoice();
		component.setType(type);
		component.setInvoice(invoice);
		if (invoice && invoice.file) {
			component.fileOpened = true;
			this.getApplication().buildDragAndDrop(false);
		}

		aonInvoice.setContent(component);
	}

	aonInvoiceHome() {
		if(!this.checkConfiguration()) return;
		this.getApplication().setContent(new AonInvoiceHome());
		this.getApplication().buildDragAndDrop(false);
	}

	aonInvoiceById(id) {
		if(!this.checkConfiguration()) return;
		getInvoice(id)
			.then((invoice) => this.aonInvoice(invoice.type, invoice))
			.catch((error) => this.showToast(error));
	}

	async selectOption(option) {
		let aonInvoice = this.getApplication();
		if (option) {
			let toolbar = this.getElement(aonInvoice.TOOLBAR);
			toolbar.option = option.name;
			this.selectedOption = option;
			if (option.id === OPTION.FISCAL_DRAFT.id) {
				await this.getFiscalModelDraft();

			}
		}
	}

	async getFiscalModelDraft() {
		let aonInvoice = this.getApplication();

		let futureFiscalFilter = await FiscalUtils.getFutureFiscalFilter();

		let aonFutureTax = new AonFutureTax(INVOICE, futureFiscalFilter);
		aonInvoice.setContent(aonFutureTax);
	}


	// BIDOQ INTEGRATION

	async hasBidoq() {
		let data = {
			document: localStorage.getItem('aon_domain_document')
		};
		let response = await checkBidoq(data);
		return response;
	}

	async importBidoqDocumentsToAon() {
		let hasBidoq = await this.hasBidoq();
		if(!hasBidoq) return;

		// Crear overlay
		let loadingOverlay = document.createElement('div');
		loadingOverlay.id = 'aonDocumentalLoadingOverlay';
		loadingOverlay.style.position = 'absolute';
		loadingOverlay.style.top = '0';
		loadingOverlay.style.left = '0';
		loadingOverlay.style.width = '100%';
		loadingOverlay.style.height = '100%';
		loadingOverlay.style.backgroundColor = 'rgba(241, 236, 236, 0.8)';
		loadingOverlay.style.display = 'flex';
		loadingOverlay.style.alignItems = 'center';
		loadingOverlay.style.justifyContent = 'center';
		loadingOverlay.style.zIndex = '10';

		// Contenedor del spinner + texto
		let spinnerContainer = document.createElement('div');
		spinnerContainer.style.display = 'flex';
		spinnerContainer.style.flexDirection = 'column';
		spinnerContainer.style.alignItems = 'center';

		// Spinner
		let spinner = document.createElement('div');
		spinner.classList.add('preloader-wrapper', 'active');
		spinner.innerHTML = `
		<span class="material-symbols-outlined">
			refresh
		</span>
	`;

		let icon = spinner.querySelector('.material-symbols-outlined');
		icon.style.fontSize = '48px';
		icon.style.animation = 'rotate 2s linear infinite';

		// Texto
		let text = document.createElement('div');
		text.textContent = 'Importando documentos desde Bidoq...';
		text.style.marginTop = '12px';
		text.style.fontSize = '16px';
		text.style.color = '#333';
		text.style.fontFamily = 'Arial, sans-serif';

		// Estilo para la animación del spinner
		if (!document.getElementById('spinner-style')) {
			let style = document.createElement('style');
			style.id = 'spinner-style';
			style.innerHTML = `
			@keyframes rotate {
				0% {
					transform: rotate(0deg);
				}
				100% {
					transform: rotate(360deg);
				}
			}
		`;
			document.head.appendChild(style);
		}

		// Armar estructura
		spinnerContainer.appendChild(spinner);
		spinnerContainer.appendChild(text);
		loadingOverlay.appendChild(spinnerContainer);

		// Agregar overlay al contenedor
		let container = document.body;
		container.appendChild(loadingOverlay);
		try {
			let data = {
				document: localStorage.getItem('aon_domain_document'),
			};
			let count = await getBidoqToOCRCount(data);
			let size = 10;
			for (let i = 0; i < count; i = i + size) {
				let data_bidoq = {
					document: localStorage.getItem('aon_domain_document'),
					order: i,
					size: size
				}
				text.textContent = 'Importando documentos desde Bidoq... ' + i + ' de ' + count;
				await getBidoqToOCR(data_bidoq);
			}
		} catch (error) {
			console.error("Error al importar documentos:", error);
		} finally {
			// Quitar el spinner
			loadingOverlay.remove();
		}
	}
}
if (!window.customElements.get(TAG.AON_INVOICE_PANEL)) {
	window.customElements.define(TAG.AON_INVOICE_PANEL, AonInvoicePanel);
}
