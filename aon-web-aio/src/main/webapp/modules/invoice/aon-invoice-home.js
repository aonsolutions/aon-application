import { AonElement } from "../../components/AonElement.js";
import { AonCard } from "../../components/aon-card.js";
import { AonDashboardButton } from "../../components/aon-dashboard-button.js";
import { AonNewUpload } from "../../components/aon-new-upload.js";
import { CONSTANT, CSS, EVENT, MATERIAL_ICONS, MSG, TAG } from "../../environments/environments.js";
import Apps from "../../services/app.js";
import { AonDashboardChargePayments } from "../accounting/aon-dashboard-charge-payments.js";
import { AonInvoice } from "./aon-invoice.js";
import { AonMobileInvoice } from "./aon-mobile-invoice.js";

import { getCounter } from './InvoiceCounter.js';
import * as OPTION from './InvoiceOptions.js';
import * as LS from '../../services/localStorageService.js';
import { AonUploadToast } from "../../components/aon-upload-toast.js";
import { generateJobId } from "./InvoiceUtils.js";
import { AonTrial } from "./aon-trial.js";
import { AonDashboardSalesPurchases } from "../accounting/aon-dashboard-sales-purchases.js";
import { AonIncome } from "./aon-income.js";
import { Income } from "./Income.js";
import { AonExpense } from "./aon-expense.js";
import { Expense } from "./Expense.js";
import { AonInvoiceProcessing } from "./aon-invoice-processing.js";
import { createSelect } from "../../components/CreateComponent.js";
import { getCompanyActivities } from "../../services/companyService.js";
import { AonDialog } from "../../components/aon-dialog.js";

export class AonInvoiceHome extends AonElement {

    DASHBOARD;
    UPLOAD_PANEL;
    UPLOAD_INVOICE;
    FAST_PANEL;
	CARD_PANEL;

	CHARGE_AND_PAYMENTS;
	INVOICE_RESUME;

    NEW_ISSUED_INVOICE;
    NEW_RECEIVED_INVOICE;
    NEW_TICKET_INVOICE;
	NEW_INCOME;
	NEW_EXPENSE;


    get id() {
		return this.getAttribute(CONSTANT.ID);
	}

	set id(id) {
		this.setAttribute(CONSTANT.ID, id);
	}

    connectedCallback () {
		this.initialize();
		this.buildDur().then(r => {
			this.build();
		});
	}

    initialize() {
        this.id =  this.id || 'aonInvoiceHome'
        this.DASHBOARD = this.id + 'Dashboard';
        this.UPLOAD_PANEL = this.id + 'UploadPanel';
        this.UPLOAD_INVOICE = this.id + 'UploadInvoice'
        this.FAST_PANEL = this.id + 'FastPanel';
		this.CARD_PANEL = this.id + 'CardPanel';
        this.NEW_ISSUED_INVOICE = this.id + 'NewIssuedInvoice';
        this.NEW_RECEIVED_INVOICE = this.id + 'NewReceivedInvoice';
        this.NEW_TICKET_INVOICE = this.id + 'NewTicketInvoice';
		this.NEW_INCOME = this.id + "NewIncome";
		this.NEW_EXPENSE = this.id + "NewExpense";
		this.CHARGE_AND_PAYMENTS = this.id + 'ChargeAndPayments';
		this.INVOICE_RESUME = this.id + 'InvoiceResume';
    }

    build() {
        let dashboard = this.createDiv(this.DASHBOARD, CSS.AON_DASHBOARD);
		dashboard.style.margin = '10px 10px 0px 10px';
        this.appendChild(dashboard);

		if (!this.getDur().isTrial() || this.getDur().hasBeenTrial())
        	this.buildUploadPanel(dashboard);

		this.buildFastPanel(dashboard);
		this.buildCardPanel(dashboard);
    }

    buildUploadPanel(dashboard) {
        let upload = this.createDiv(this.UPLOAD_PANEL, CSS.AON_UPLOAD_PANEL);
        dashboard.appendChild(upload);

        let uploadInv = new AonNewUpload();
        uploadInv.id = this.UPLOAD_INVOICE;
        uploadInv.setMessage("Arrastra o selecciona para subir facturas");
        uploadInv.setType("Invoice");
        upload.appendChild(uploadInv);
    }

	uploadInvoiceHome(input, files) {
		getCompanyActivities({}).then(activities => {
			let data = { uploaded: 0 };
			if(activities.length > 1) {
				activities.push({
					id: "all",
					description: "TODAS"
        		});
				let activity =  createSelect(this.ACTIVITY, MSG.ACTIVITY);
				activity.setAlias("id", "description");
				if(activities.length > 0) {
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
				if(activities.length > 0) {
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

    buildFastPanel(dashboard) {
		let fastPanel = this.createDiv(this.FAST_PANEL, CSS.AON_FAST_ACCESS);
		dashboard.appendChild(fastPanel);

        let newIssuedInvoice = new AonDashboardButton();
		newIssuedInvoice.setId(this.NEW_ISSUED_INVOICE);
		newIssuedInvoice.setIcon(MATERIAL_ICONS.UNARCHIVE);
		newIssuedInvoice.setMessage(MSG.NEW_ISSUED_INVOICE);
		newIssuedInvoice.addEventListener(EVENT.CLICK, () => {
			this.aonInvoice('emitida');
		});
		fastPanel.appendChild(newIssuedInvoice);

        let newReceivedInvoice = new AonDashboardButton();
		newReceivedInvoice.setId(this.NEW_RECEIVED_INVOICE);
		newReceivedInvoice.setIcon(MATERIAL_ICONS.ARCHIVE);
		newReceivedInvoice.setMessage(MSG.NEW_RECEIVED_INVOICE);
		newReceivedInvoice.addEventListener(EVENT.CLICK, () => {
			this.aonInvoice('recibida');
		});
		fastPanel.appendChild(newReceivedInvoice);

        let newTicket = new AonDashboardButton();
		newTicket.setId(this.NEW_TICKET_INVOICE);
		newTicket.setIcon(MATERIAL_ICONS.RECEIPT);
		newTicket.setMessage(MSG.NEW_TICKET);
		newTicket.addEventListener(EVENT.CLICK, () => {
			this.aonInvoice('ticket');
		});
		fastPanel.appendChild(newTicket);

		let newIncome = new AonDashboardButton();
		newIncome.setId(this.NEW_INCOME);
		newIncome.setIcon('add_card');
		newIncome.setMessage("Nuevo Ingreso");
		newIncome.addEventListener(EVENT.CLICK, () => {
			this.getApplication().setContent(new AonIncome(new Income()));
		});
		fastPanel.appendChild(newIncome);

		let newExpense = new AonDashboardButton();
		newExpense.setId(this.NEW_EXPENSE);
		newExpense.setIcon(MATERIAL_ICONS.ACCOUNT_BALANCE_WALLET);
		newExpense.setMessage("Nuevo Gasto");
		newExpense.addEventListener(EVENT.CLICK, () => {
			this.getApplication().setContent(new AonExpense(new Expense()));
		});
		fastPanel.appendChild(newExpense);
    }

	buildCardPanel(dashboard) {
		let cardPanel = this.createDiv(this.CARD_PANEL, CSS.FLEX_ROW);
		cardPanel.style.gap = '1rem';
		cardPanel.style.flexWrap = 'wrap';
		dashboard.appendChild(cardPanel);

		if(!this.getDur().isTrial() || this.getDur().hasBeenTrial()) {
			let invoiceResumeCard = new AonCard();
			invoiceResumeCard.classList.add(CSS.AON_DASHBOARD_CARD);
			invoiceResumeCard.id = this.INVOICE_RESUME;
			invoiceResumeCard.message = "Resumen Facturación";
			invoiceResumeCard.setApp(Apps.INVOICE);
			cardPanel.appendChild(invoiceResumeCard);
			invoiceResumeCard.setContent(this.buildInvoiceResumeCard());

			invoiceResumeCard.firstChild.style.marginLeft = '0';
			invoiceResumeCard.firstChild.style.minHeight = "460px";
			invoiceResumeCard.firstChild.children.item(1).style.height = "315px";
			invoiceResumeCard.firstChild.style.margin = '0';
		}

		if(!LS.isSuite() && this.getDur().isTrial()) {
			// Trial Card
			let trialCard = new AonCard();
			trialCard.classList.add(CSS.AON_DASHBOARD_CARD);
			trialCard.id = "trial";
			trialCard.message = "Versión Evaluación (Resumen de uso)";
			trialCard.setApp(this.getDur().isInvoice() ? Apps.INVOICE : Apps.ACCOUNTING);
			cardPanel.appendChild(trialCard);
			trialCard.getCardTitle1().style.cursor = 'pointer';

			trialCard.setContent(new AonTrial());
			trialCard.firstChild.style.marginLeft = '0';
			trialCard.firstChild.style.minHeight = "28rem";
			trialCard.firstChild.children.item(1).style.height = "22.5rem";
			trialCard.firstChild.style.margin = '0';
		}

		if(!LS.isSuite()) {
			// Ventas y Gastos Card
			let defaultYear = new Date().getFullYear();

			if(new Date().getTime() < new Date(new Date().getFullYear(), 0, 31))
				defaultYear = defaultYear - 1;

			let aonDashboardSalesPurchases = new AonDashboardSalesPurchases("yearly", defaultYear);
			aonDashboardSalesPurchases.id = "aonDashboardSalesPurchases";

			let vygCard = new AonCard();
			vygCard.classList.add(CSS.AON_DASHBOARD_CARD);
			vygCard.id = "vygCard";
			// pygCard.title = "Pérdidas y Ganancias";
			vygCard.message = "Ventas y Gastos";
			vygCard.setApp(this.getDur().isInvoice() ? Apps.INVOICE : Apps.ACCOUNTING);
			cardPanel.appendChild(vygCard);
			vygCard.getCardTitle1().style.cursor = 'pointer';
			vygCard.insertAdjacentHTML( 'beforeend', "<aon-dialog-menu id='aonCardVyGOption'> </aon-dialog-menu>" );

			vygCard.setContent(aonDashboardSalesPurchases);
			vygCard.addTitleButton(MSG.OPTIONS, MATERIAL_ICONS.MORE_VERT, false, () => this.filterVyG(vygCard));
			vygCard.firstChild.style.marginLeft = '0';
			vygCard.firstChild.style.minHeight = "28rem";
			vygCard.firstChild.children.item(1).style.height = "22.5rem";
			vygCard.firstChild.style.margin = '0';
		}

		if(!LS.isSuite()) {
			let cypCard = new AonCard();
			cypCard.classList.add(CSS.AON_DASHBOARD_CARD);
			cypCard.id = this.CHARGE_AND_PAYMENTS;
			cypCard.message = "Cobros y Pagos";
			cypCard.setApp(Apps.INVOICE);
			cardPanel.appendChild(cypCard);
			cypCard.insertAdjacentHTML( 'beforeend', "<aon-dialog-menu id='aonCardCyPOption'> </aon-dialog-menu>" );
	
			cypCard.setContent(new AonDashboardChargePayments("current_month"));
			cypCard.addTitleButton(MSG.OPTIONS, MATERIAL_ICONS.MORE_VERT, false, () => this.filterCyP(cypCard));
			cypCard.firstChild.style.marginLeft = '0';
			cypCard.firstChild.style.minHeight = "28rem";
			cypCard.firstChild.children.item(1).style.height = "22.5rem";
			cypCard.firstChild.style.margin = '0';
		}
	}

	buildInvoiceResumeCard() {
		let div = this.createDiv();
		div.classList.add("aonInvoiceHomeDiv");

		// INVOICE

		let invoiceDiv = this.createDiv();
		invoiceDiv.id = this.ID + 'Invoice';
		invoiceDiv.classList.add("aonInvoiceHomePendingDiv");
		invoiceDiv.overflow = 'hidden';
		div.appendChild(invoiceDiv);

		let invoiceNameRow = this.createDiv();
		invoiceNameRow.classList.add("aonInvoiceHomePendingNameRow");
		invoiceDiv.appendChild(invoiceNameRow);

		let invoiceName = this.createDiv();
		invoiceName.id = this.ID + 'invoiceName';
		invoiceName.innerHTML = "Facturas (" + new Date().getFullYear() + ")";
		invoiceName.classList.add("aonInvoiceHomePendingName");
		invoiceNameRow.appendChild(invoiceName);

		let invoiceCounterRow = this.createDiv();
		invoiceCounterRow.classList.add("aonInvoiceHomePendingCounterRow");
		invoiceDiv.appendChild(invoiceCounterRow);

		// ISSUED INVOICE

		let invoiceIssuedDiv = this.createDiv('invoiceIssued', 'aonInvoiceHomeOptionDiv');
		invoiceIssuedDiv.overflow = 'hidden';
		invoiceCounterRow.appendChild(invoiceIssuedDiv);

		// pendingIssuedDiv.addEventListener(EVENT.CLICK, () => this.aonInvoiceList(
		// 	{status: CONSTANT.INBOX, type: 'emitida'},
		// 	{status: CONSTANT.OCR_INBOX, page: 0, perPage: 50, publicStatus:['approved', 'pendingCorrection'], type:['invoice', 'ticket'], companyActsLike:'issuer'}
		// ));

		let invoiceIssuedNumber = this.createDiv();
		invoiceIssuedNumber.id = 'invoiceIssuedNumber';
		invoiceIssuedNumber.innerHTML = getCounter()[OPTION.INVOICE_ISSUED_BETA.id] || 0;
		invoiceIssuedNumber.classList.add("aonInvoiceHomePendingIssuedNumber");
		invoiceIssuedNumber.style.color = 'var(--aonGreen)';
		invoiceIssuedDiv.appendChild(invoiceIssuedNumber);

		let invoiceIssuedName = this.createDiv();
		invoiceIssuedName.id = 'invoiceIssuedName';
		invoiceIssuedName.innerHTML = MSG.ISSUEDS;
		invoiceIssuedName.classList.add("aonInvoiceHomePendingIssuedName");
		invoiceIssuedDiv.appendChild(invoiceIssuedName);

		// PENDING RECEIVED
		let invoiceReceivedDiv = this.createDiv('invoiceReceived', 'aonInvoiceHomeOptionDiv');
		invoiceReceivedDiv.overflow = 'hidden';
		invoiceCounterRow.appendChild(invoiceReceivedDiv);


		// pendingReceivedDiv.addEventListener(EVENT.CLICK, () => this.aonInvoiceList(
		// 	{status: CONSTANT.INBOX, type: 'recibida'},
		// 	{status: CONSTANT.OCR_INBOX, page: 0, perPage: 50, publicStatus:['approved', 'pendingCorrection'], type:['invoice'], companyActsLike:'ne+issuer'}
		// ));

		let invoiceReceivedNumber = this.createDiv();
		invoiceReceivedNumber.id = 'invoiceReceivedNumber';
		invoiceReceivedNumber.innerHTML = getCounter()[OPTION.INVOICE_RECEIVED_BETA.id] || 0;
		invoiceReceivedNumber.classList.add("aonInvoiceHomePendingReceivedNumber");
		invoiceReceivedNumber.style.color = 'var(--aonGreen)';
		invoiceReceivedDiv.appendChild(invoiceReceivedNumber);

		let invoiceReceivedName = this.createDiv();
		invoiceReceivedName.id = 'invoiceReceivedName';
		invoiceReceivedName.innerHTML = MSG.RECEIVEDS;
		invoiceReceivedName.classList.add("aonInvoiceHomePendingReceivedName");
		invoiceReceivedDiv.appendChild(invoiceReceivedName);

		// PENDING TICKETS
		let invoiceTicketDiv = this.createDiv('invoiceTicket', 'aonInvoiceHomeOptionDiv');
		invoiceTicketDiv.overflow = 'hidden';
		invoiceCounterRow.appendChild(invoiceTicketDiv);

		// invoiceTicketDiv.addEventListener(EVENT.CLICK, () => this.aonInvoiceList(
		// 	{status: CONSTANT.INBOX, type: 'ticket'},
		// 	{status: CONSTANT.OCR_INBOX, page: 0, perPage: 50, publicStatus:['approved', 'pendingCorrection'], type:['ticket'], companyActsLike:'ne+issuer'}
		// ));

		let invoiceTicketNumber = this.createDiv();
		invoiceTicketNumber.id = 'invoiceTicketNumber';
		invoiceTicketNumber.innerHTML = getCounter()[OPTION.INVOICE_TICKET.id] || 0;
		invoiceTicketNumber.classList.add("aonInvoiceHomePendingTicketNumber");
		invoiceTicketNumber.style.color = 'var(--aonGreen)';
		invoiceTicketDiv.appendChild(invoiceTicketNumber);

		let invoiceTicketName = this.createDiv();
		invoiceTicketName.id = 'invoiceTicketName';
		invoiceTicketName.innerHTML = MSG.TICKETS;
		invoiceTicketName.classList.add("aonInvoiceHomePendingTicketName");
		invoiceTicketDiv.appendChild(invoiceTicketName);

		// PENDING
		let pendingDiv = this.createDiv();
		pendingDiv.id = 'pending';
		pendingDiv.classList.add("aonInvoiceHomePendingDiv");
		pendingDiv.overflow = 'hidden';
		div.appendChild(pendingDiv);

		let pendingNameRow = this.createDiv();
		pendingNameRow.classList.add("aonInvoiceHomePendingNameRow");
		pendingDiv.appendChild(pendingNameRow);
	

		let pendingName = this.createDiv();
		pendingName.id = 'pendingName';
		pendingName.innerHTML = "Borradores / Proforma";
		pendingName.classList.add("aonInvoiceHomePendingName");
		pendingNameRow.appendChild(pendingName);

		let pendingCounterRow = this.createDiv();
		pendingCounterRow.classList.add("aonInvoiceHomePendingCounterRow");
		pendingDiv.appendChild(pendingCounterRow);

		// PENDING OUTPUT
		let pendingIssuedDiv = this.createDiv('pendingIssued', 'aonInvoiceHomeOptionDiv');
		pendingIssuedDiv.overflow = 'hidden';
		pendingCounterRow.appendChild(pendingIssuedDiv);

		pendingIssuedDiv.addEventListener(EVENT.CLICK, () => this.aonInvoiceList(
			{status: CONSTANT.INBOX, type: 'emitida'},
			{status: CONSTANT.OCR_INBOX, page: 0, perPage: 50, publicStatus:['approved', 'pendingCorrection'], type:['invoice', 'ticket'], companyActsLike:'issuer'}
		));

		let pendingIssuedNumber = this.createDiv();
		pendingIssuedNumber.id = 'pendingIssuedNumber';
		pendingIssuedNumber.innerHTML = getCounter()[OPTION.PROFORMA_INVOICES.id] || 0;
		pendingIssuedNumber.classList.add("aonInvoiceHomePendingIssuedNumber");
		pendingIssuedDiv.appendChild(pendingIssuedNumber);


		let pendingIssuedName = this.createDiv();
		pendingIssuedName.id = 'pendingIssuedName';
		pendingIssuedName.innerHTML = MSG.ISSUEDS;
		pendingIssuedName.classList.add("aonInvoiceHomePendingIssuedName");
		pendingIssuedDiv.appendChild(pendingIssuedName);

		// PENDING RECEIVED
		
		let pendingReceivedDiv = this.createDiv('pendingReceived', 'aonInvoiceHomeOptionDiv');
		pendingReceivedDiv.overflow = 'hidden';
		pendingCounterRow.appendChild(pendingReceivedDiv);

		pendingReceivedDiv.addEventListener(EVENT.CLICK, () => this.aonInvoiceList(
			{status: CONSTANT.INBOX, type: 'recibida'},
			{status: CONSTANT.OCR_INBOX, page: 0, perPage: 50, publicStatus:['approved', 'pendingCorrection'], type:['invoice'], companyActsLike:'ne+issuer'}
		));

		let pendingReceivedNumber = this.createDiv();
		pendingReceivedNumber.id = 'pendingReceivedNumber';
		pendingReceivedNumber.innerHTML = getCounter()[OPTION.RAWDOC_INBOX_RECEIVED_NEW.id] || 0;
		pendingReceivedNumber.classList.add("aonInvoiceHomePendingReceivedNumber");
		pendingReceivedDiv.appendChild(pendingReceivedNumber);

		let pendingReceivedName = this.createDiv();
		pendingReceivedName.id = 'pendingReceivedName';
		pendingReceivedName.innerHTML = MSG.RECEIVEDS;
		pendingReceivedName.classList.add("aonInvoiceHomePendingReceivedName");
		pendingReceivedDiv.appendChild(pendingReceivedName);

		// PENDING TICKETS
		let pendingTicketDiv = this.createDiv('pendingTicket', 'aonInvoiceHomeOptionDiv');
		pendingTicketDiv.overflow = 'hidden';
		pendingCounterRow.appendChild(pendingTicketDiv);

		pendingTicketDiv.addEventListener(EVENT.CLICK, () => this.aonInvoiceList(
			{status: CONSTANT.INBOX, type: 'ticket'},
			{status: CONSTANT.OCR_INBOX, page: 0, perPage: 50, publicStatus:['approved', 'pendingCorrection'], type:['ticket'], companyActsLike:'ne+issuer'}
		));

		let pendingTicketNumber = this.createDiv();
		pendingTicketNumber.id = 'pendingTicketNumber';
		pendingTicketNumber.innerHTML = getCounter()[OPTION.RAWDOC_INBOX_TICKET_NEW.id] || 0;
		pendingTicketNumber.classList.add("aonInvoiceHomePendingTicketNumber");
		pendingTicketDiv.appendChild(pendingTicketNumber);

		let pendingTicketName = this.createDiv();
		pendingTicketName.id = 'pendingTicketName';
		pendingTicketName.innerHTML = MSG.TICKETS;
		pendingTicketName.classList.add("aonInvoiceHomePendingTicketName");
		pendingTicketDiv.appendChild(pendingTicketName);

		// PROCESSING-PENDING / REJECTED / TRASH

		let otherDiv = this.createDiv();
		otherDiv.id = 'other';
		otherDiv.classList.add("aonInvoiceHomePendingDiv");
		otherDiv.overflow = 'hidden';
		div.appendChild(otherDiv);

		let otherNameRow = this.createDiv();
		otherNameRow.classList.add("aonInvoiceHomePendingNameRow");
		otherDiv.appendChild(otherNameRow);
	

		let otherName = this.createDiv();
		otherName.id = 'pendingName';
		otherName.innerHTML = "Documentos Pendientes";
		otherName.classList.add("aonInvoiceHomePendingName");
		otherNameRow.appendChild(otherName);

		let otherCounterRow = this.createDiv();
		otherCounterRow.classList.add("aonInvoiceHomePendingCounterRow");
		otherDiv.appendChild(otherCounterRow);

		// EN TRAMITE (PROCESSING-PENDING)

		let processingDiv = this.createDiv('processing', 'aonInvoiceHomeOptionDiv');
		processingDiv.overflow = 'hidden';
		otherCounterRow.appendChild(processingDiv);

		processingDiv.addEventListener(EVENT.CLICK, () => this.aonInvoiceProcessing());

		let processingNumber = this.createDiv();
		processingNumber.id = 'processingNumber';
		processingNumber.innerHTML = getCounter()[OPTION.RAWDOC_PROCESSING.id] || 0;
		processingNumber.classList.add("aonInvoiceHomeProcessingNumber");
		processingDiv.appendChild(processingNumber);

		let processingName = this.createDiv();
		processingName.id = 'processingName';
		processingName.innerHTML = MSG.PROCCESSING;
		processingName.classList.add("aonInvoiceHomeProcessingName");
		processingDiv.appendChild(processingName);

		// REJECT

		let rejectedDiv = this.createDiv('pendingRevision', 'aonInvoiceHomeOptionDiv');
		rejectedDiv.overflow = 'hidden';
		otherCounterRow.appendChild(rejectedDiv);
		
		rejectedDiv.addEventListener(EVENT.CLICK, () => this.aonInvoiceList({status: CONSTANT.REJECTED}));
;

		let rejectedNumber = this.createDiv();
		rejectedNumber.id = 'rejectedNumber';
		rejectedNumber.innerHTML = getCounter()[OPTION.RAWDOC_REJECT.id] || 0;
		rejectedNumber.classList.add("aonInvoiceHomeRejectedNumber");
		rejectedDiv.appendChild(rejectedNumber);

		let rejectedName = this.createDiv();
		rejectedName.id = 'rejectedName';
		rejectedName.innerHTML = MSG.REVIEW;
		rejectedName.classList.add("aonInvoiceHomeRejectedName");
		rejectedDiv.appendChild(rejectedName);

		// TRASH
		let trashDiv = this.createDiv('trash', 'aonInvoiceHomeOptionDiv');
		trashDiv.overflow = 'hidden';		
		otherCounterRow.appendChild(trashDiv);

		trashDiv.addEventListener(EVENT.CLICK, () => this.aonInvoiceList({status: CONSTANT.DRAFT}));

		let trashNumber = this.createDiv();
		trashNumber.id = 'trashNumber';
		trashNumber.innerHTML = getCounter()[OPTION.RAWDOC_TRASH.id] || 0;
		trashNumber.classList.add("aonInvoiceHomeTrashNumber");
		trashDiv.appendChild(trashNumber);

		let trashName = this.createDiv();
		trashName.id = 'trashName';
		trashName.innerHTML = MSG.TRASH;
		trashName.classList.add("aonInvoiceHomeTrashName");
		trashDiv.appendChild(trashName);
		
		return div;
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
		if(invoice && invoice.file) {
			component.fileOpened = true;
			this.getApplication().buildDragAndDrop(false);
		}

		aonInvoice.setContent(component);
	}

	aonInvoiceList(filter, invofoxFilter) {
		this.getApplication().getParent().aonInvoiceList(filter, invofoxFilter);
	}

	aonInvoiceProcessing() {
		this.getApplication().setContent(new AonInvoiceProcessing());
	}

	// PROVISIONAL - AÑADIRLO EN UNICO SITIO.
	filterCyP(cypCard){
		let button = this.getElement(this.CHARGE_AND_PAYMENTS + 'TitleSection2OpcionesButtonIconButton');
		let top  = button.getBoundingClientRect().top;
		const left = button.getBoundingClientRect().left;

		let d = document.getElementById('aonCardCyPOption');

		const currentMonth = {
			name: 'Mes Actual',
			title: "Mes Actual",
			icon: 'calendar_today',
			backgroundColor: "#4472C4",
			fn: () => {
				cypCard.clear();
				cypCard.setContent(new AonDashboardChargePayments("current_month"));
			}
		};

		const nextMonth = {
			name: 'Hasta próximo mes',
			title: "Hasta próximo mes",
			icon: 'calendar_today',
			backgroundColor: "#4472C4",
			fn: () => {
				cypCard.clear();
				cypCard.setContent(new AonDashboardChargePayments("next_month"));
			}
		};

		const next3Month = {
			name: 'Próximos 3 meses',
			title: "Próximos 3 meses",
			icon: 'calendar_today',
			backgroundColor: "#4472C4",
			fn: () => {
				cypCard.clear();
				cypCard.setContent(new AonDashboardChargePayments("next_3month"));
			}
		};

		const next6Month = {
			name: 'Próximos 6 meses',
			title: "Próximos 6 meses",
			icon: 'calendar_today',
			backgroundColor: "#4472C4",
			fn: () => {
				cypCard.clear();
				cypCard.setContent(new AonDashboardChargePayments("next_6month"));
			}
		};

		const yearly = {
			name: 'Año Actual',
			title: "Año Actual",
			icon: 'calendar_today',
			backgroundColor: "#4472C4",
			fn: () => {
				cypCard.clear();
				cypCard.setContent(new AonDashboardChargePayments("yearly"));
			}
		};

		let options = [currentMonth, nextMonth, next3Month, next6Month, yearly];

		d.setMenuOptions(options, top, left);
		d.open();
	}

	filterVyG(vygCard){
		let button = this.getElement('vygCardTitleSection2OpcionesButtonIconButton');
		let top  = button.getBoundingClientRect().top;
		const left = button.getBoundingClientRect().left;

		let vyGYearSelect = this.getElement('vyGyearSelect');
		let period = JSON.parse(vyGYearSelect.value);
        let vygYear = period.name;

		let aonDashboardSalesPurchases = this.getElement('aonDashboardSalesPurchases');

		let d = document.getElementById('aonCardVyGOption');

		const anual = {
			name: 'Vista Anual',
			title:"Vista Anual",
			icon: 'calendar_today',
			backgroundColor: "#4472C4",
			fn: () => {
				vygCard.clear();
				aonDashboardSalesPurchases = new AonDashboardSalesPurchases("yearly", vygYear);
				aonDashboardSalesPurchases.id = "aonDashboardSalesPurchases";
				vygCard.setContent(aonDashboardSalesPurchases);
			}
		};

		const trimestral = {
			name: 'Vista Trimestral',
			title:"Vista Trimestral",
			icon: 'calendar_today',
			backgroundColor: "#4472C4",
			fn: () => {
				vygCard.clear();
				aonDashboardSalesPurchases = new AonDashboardSalesPurchases("quarterly", vygYear);
				aonDashboardSalesPurchases.id = "aonDashboardSalesPurchases";
				vygCard.setContent(aonDashboardSalesPurchases);
			}
		};

		const mensual = {
			name: "Vista Mensual",
			title: "Vista Mensual",
			icon: 'calendar_today',
			backgroundColor: "#4472C4",
			fn: () => {
				vygCard.clear();
				aonDashboardSalesPurchases = new AonDashboardSalesPurchases("monthly", vygYear);
				aonDashboardSalesPurchases.id = "aonDashboardSalesPurchases";
				vygCard.setContent(aonDashboardSalesPurchases);
			}
		};
	
		let options = [anual, trimestral, mensual];
		
		d.setMenuOptions(options, top, left);
		d.open();
	}

}
if(!window.customElements.get(TAG.AON_INVOICE_HOME)){
	window.customElements.define(TAG.AON_INVOICE_HOME, AonInvoiceHome);
}