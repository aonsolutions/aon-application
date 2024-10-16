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
		this.CHARGE_AND_PAYMENTS = this.id + 'ChargeAndPayments';
		this.INVOICE_RESUME = this.id + 'InvoiceResume';
    }

    build() {
        let dashboard = this.createDiv(this.DASHBOARD, CSS.AON_DASHBOARD);
		dashboard.style.margin = '10px 10px 0px 10px';
        this.appendChild(dashboard);

        this.buildUploadPanel(dashboard);
		this.buildFastPanel(dashboard);
		this.buildCardPanel(dashboard);
    }

    buildUploadPanel(dashboard) {
        let upload = this.createDiv(this.UPLOAD_PANEL, CSS.AON_UPLOAD_PANEL);
        dashboard.appendChild(upload);

        let uploadInv = new AonNewUpload();
        uploadInv.id = this.UPLOAD_INVOICE;
        uploadInv.setMessage(MSG.UPLOAD_INVOICE);
        uploadInv.setType("Invoice");
        upload.appendChild(uploadInv);
    }

	uploadInvoiceHome(input, files) {
		let uploadToast = this.getElement('aonUploadToast');
		if(!uploadToast){ 
			uploadToast = new AonUploadToast();
			this.appendChild(uploadToast);
		}
		let data = { uploaded : 0 };
		uploadToast.setJobId(generateJobId());
		for (let file of files) {
			uploadToast.addFile("invoice", file, data);
		}
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
    }

	buildCardPanel(dashboard) {
		let cardPanel = this.createDiv(this.CARD_PANEL, CSS.FLEX_ROW);
		cardPanel.style.gap = '1rem';
		cardPanel.style.flexWrap = 'wrap';
		dashboard.appendChild(cardPanel);

		let invoiceResumeCard = new AonCard();
		invoiceResumeCard.classList.add(CSS.AON_DASHBOARD_CARD);
		invoiceResumeCard.id = this.INVOICE_RESUME;
		invoiceResumeCard.message = "Resumen Facturas";
		invoiceResumeCard.setApp(Apps.INVOICE);
		cardPanel.appendChild(invoiceResumeCard);
		invoiceResumeCard.setContent(this.buildInvoiceResumeCard());


		invoiceResumeCard.firstChild.style.marginLeft = '0';
		invoiceResumeCard.firstChild.style.minHeight = "420px";
		invoiceResumeCard.firstChild.children.item(1).style.height = "315px";
		invoiceResumeCard.firstChild.style.margin = '0';


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
			cypCard.firstChild.style.minHeight = "420px";
			cypCard.firstChild.children.item(1).style.height = "315px";
			cypCard.firstChild.style.margin = '0';
		}

		this.updateCounterHome();
	}

	buildInvoiceResumeCard() {
		let div = this.createDiv();
		div.classList.add("aonInvoiceHomeDiv");

		let pendingRecordsDiv = this.createDiv();
		pendingRecordsDiv.id = 'pendingRecords';
		pendingRecordsDiv.classList.add("aonInvoiceHomePendingRecordsDiv");
		pendingRecordsDiv.overflow = 'hidden';

		pendingRecordsDiv.addEventListener(EVENT.CLICK, () => this.aonInvoiceList(
			{status:'accounting', recorded: 'PENDING', page:1, per_page: 50}
		));

		div.appendChild(pendingRecordsDiv);

		let pendingRecordNumber = this.createDiv();
		pendingRecordNumber.id = 'pendingRecordNumber';
		pendingRecordNumber.innerHTML = '0';
		pendingRecordNumber.classList.add("aonInvoiceHomePendingRecordNumber");

		pendingRecordsDiv.appendChild(pendingRecordNumber);

		let pendingRecordName = this.createDiv();
		pendingRecordName.id = 'pendingRecordName';
		pendingRecordName.innerHTML = "Pendientes de Contabilizar";
		pendingRecordName.classList.add("aonInvoiceHomePendingRecordName");
		pendingRecordsDiv.appendChild(pendingRecordName);

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
		pendingName.innerHTML = MSG.PENDING_DOCUMENTS;
		pendingName.classList.add("aonInvoiceHomePendingName");
		pendingNameRow.appendChild(pendingName);

		let pendingCounterRow = this.createDiv();
		pendingCounterRow.classList.add("aonInvoiceHomePendingCounterRow");
		pendingDiv.appendChild(pendingCounterRow);

		// PENDING OUTPUT
		let pendingIssuedDiv = this.createDiv();
		pendingIssuedDiv.id = 'pendingIssued';
		pendingIssuedDiv.classList.add("aonInvoiceHomePendingIssuedDiv");
		pendingIssuedDiv.overflow = 'hidden';
		pendingCounterRow.appendChild(pendingIssuedDiv);

		pendingIssuedDiv.addEventListener(EVENT.CLICK, () => this.aonInvoiceList(
			{status: CONSTANT.INBOX, type: 'emitida'},
			{status: CONSTANT.OCR_INBOX, page: 0, perPage: 50, publicStatus:['approved', 'pendingCorrection'], type:['invoice', 'ticket'], companyActsLike:'issuer'}
		));

		let pendingIssuedNumber = this.createDiv();
		pendingIssuedNumber.id = 'pendingIssuedNumber';
		pendingIssuedNumber.innerHTML = '0';
		pendingIssuedNumber.classList.add("aonInvoiceHomePendingIssuedNumber");
		pendingIssuedDiv.appendChild(pendingIssuedNumber);


		let pendingIssuedName = this.createDiv();
		pendingIssuedName.id = 'pendingIssuedName';
		pendingIssuedName.innerHTML = MSG.ISSUEDS;
		pendingIssuedName.classList.add("aonInvoiceHomePendingIssuedName");
		pendingIssuedDiv.appendChild(pendingIssuedName);

		// PENDING RECEIVED
		let pendingReceivedDiv = this.createDiv();
		pendingReceivedDiv.id = 'pendingReceived';
		pendingReceivedDiv.classList.add("aonInvoiceHomePendingReceivedDiv");
		pendingReceivedDiv.overflow = 'hidden';
		pendingCounterRow.appendChild(pendingReceivedDiv);


		pendingReceivedDiv.addEventListener(EVENT.CLICK, () => this.aonInvoiceList(
			{status: CONSTANT.INBOX, type: 'recibida'},
			{status: CONSTANT.OCR_INBOX, page: 0, perPage: 50, publicStatus:['approved', 'pendingCorrection'], type:['invoice'], companyActsLike:'ne+issuer'}
		));

		let pendingReceivedNumber = this.createDiv();
		pendingReceivedNumber.id = 'pendingReceivedNumber';
		pendingReceivedNumber.innerHTML = '0';
		pendingReceivedNumber.classList.add("aonInvoiceHomePendingReceivedNumber");
		pendingReceivedDiv.appendChild(pendingReceivedNumber);

		let pendingReceivedName = this.createDiv();
		pendingReceivedName.id = 'pendingReceivedName';
		pendingReceivedName.innerHTML = MSG.RECEIVEDS;
		pendingReceivedName.classList.add("aonInvoiceHomePendingReceivedName");
		pendingReceivedDiv.appendChild(pendingReceivedName);

		// PENDING TICKETS
		let pendingTicketDiv = this.createDiv();
		pendingTicketDiv.id = 'pendingTicket';
		pendingTicketDiv.classList.add("aonInvoiceHomePendingTicketDiv");
		pendingTicketDiv.overflow = 'hidden';
		pendingCounterRow.appendChild(pendingTicketDiv);

		pendingTicketDiv.addEventListener(EVENT.CLICK, () => this.aonInvoiceList(
			{status: CONSTANT.INBOX, type: 'ticket'},
			{status: CONSTANT.OCR_INBOX, page: 0, perPage: 50, publicStatus:['approved', 'pendingCorrection'], type:['ticket'], companyActsLike:'ne+issuer'}
		));

		let pendingTicketNumber = this.createDiv();
		pendingTicketNumber.id = 'pendingTicketNumber';
		pendingTicketNumber.innerHTML = '0';
		pendingTicketNumber.classList.add("aonInvoiceHomePendingTicketNumber");
		pendingTicketDiv.appendChild(pendingTicketNumber);

		let pendingTicketName = this.createDiv();
		pendingTicketName.id = 'pendingTicketName';
		pendingTicketName.innerHTML = MSG.TICKETS;
		pendingTicketName.classList.add("aonInvoiceHomePendingTicketName");
		pendingTicketDiv.appendChild(pendingTicketName);

		// REJECTED / TRASH

		let rejectedDiv = this.createDiv();
		rejectedDiv.id = 'pendingRevision';
		rejectedDiv.classList.add("aonInvoiceHomeRejectedDiv");
		rejectedDiv.overflow = 'hidden';

		rejectedDiv.addEventListener(EVENT.CLICK, () => this.aonInvoiceList(
			{status: CONSTANT.REJECTED},
			{status: CONSTANT.OCR_INBOX, page: 0, perPage: 50
				, publicStatus:[CONSTANT.PENDING_DECISSION, CONSTANT.REJECTED], type: ['invoice', 'ticket']}
		));


		div.appendChild(rejectedDiv);

		let rejectedNumber = this.createDiv();
		rejectedNumber.id = 'rejectedNumber';
		rejectedNumber.innerHTML = '0';
		rejectedNumber.classList.add("aonInvoiceHomeRejectedNumber");
		rejectedDiv.appendChild(rejectedNumber);

		let rejectedName = this.createDiv();
		rejectedName.id = 'rejectedName';
		rejectedName.innerHTML = MSG.REJECTEDS;
		rejectedName.classList.add("aonInvoiceHomeRejectedName");
		rejectedDiv.appendChild(rejectedName);

		// TRASH
		let trashDiv = this.createDiv();
		trashDiv.id = 'trash';
		trashDiv.classList.add("aonInvoiceHomeTrashDiv");
		trashDiv.overflow = 'hidden';

		trashDiv.addEventListener(EVENT.CLICK, () => this.aonInvoiceList(
			{status: CONSTANT.DRAFT},
			{status: CONSTANT.OCR_INBOX, page: 0, perPage: 50, publicStatus:[CONSTANT.DISCARDED]}
		));

		div.appendChild(trashDiv);

		let trashNumber = this.createDiv();
		trashNumber.id = 'trashNumber';
		trashNumber.innerHTML = '0';
		trashNumber.classList.add("aonInvoiceHomeTrashNumber");
		trashDiv.appendChild(trashNumber);

		let trashName = this.createDiv();
		trashName.id = 'trashName';
		trashName.innerHTML = MSG.IN_TRASH;
		trashName.classList.add("aonInvoiceHomeTrashName");
		trashDiv.appendChild(trashName);
		
		return div;
	}

	updateCounterHome() {
		let issued = getCounter()[OPTION.INVOICE_ISSUED.id] || 0;
		let received = getCounter()[OPTION.INVOICE_RECEIVED.id] || 0;
		let ticket = getCounter()[OPTION.INVOICE_TICKET.id] || 0;
		let total = issued + received + ticket;
		let pendingRecordNumber = this.getElement('pendingRecordNumber');
		if(pendingRecordNumber) pendingRecordNumber.innerHTML = total;

		// let pending = getCounter()[OPTION.INVOICE_PENDINGS.id] || 0;
		//let pendingNumber = this.getElement('pendingNumber');
		//if(pendingNumber) pendingNumber.innerHTML = pending;

		let pendingIssuedCounter = getCounter()[OPTION.RAWDOC_INBOX_ISSUED.id] || 0;
		let pendingIssuedNumber = this.getElement('pendingIssuedNumber');
		if(pendingIssuedNumber) pendingIssuedNumber.innerHTML = pendingIssuedCounter;

		let pendingReceivedCounter = getCounter()[OPTION.RAWDOC_INBOX_RECEIVED.id] || 0;
		let pendingReceivedNumber = this.getElement('pendingReceivedNumber');
		if(pendingReceivedNumber) pendingReceivedNumber.innerHTML = pendingReceivedCounter;
		
		let pendingTicketCounter = getCounter()[OPTION.RAWDOC_INBOX_TICKET.id] || 0;
		let pendingTicketNumber = this.getElement('pendingTicketNumber');
		if(pendingTicketNumber) pendingTicketNumber.innerHTML = pendingTicketCounter;
		
		let rejectedCounter = getCounter()[OPTION.RAWDOC_REJECT.id] || 0;
		let rejectedNumber = this.getElement('rejectedNumber');
		if(rejectedNumber) rejectedNumber.innerHTML = rejectedCounter;

		let trashCounter = getCounter()[OPTION.RAWDOC_DRAFT.id] || 0;
		let trashNumber = this.getElement('trashNumber');
		if(trashNumber) trashNumber.innerHTML = trashCounter;
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

}
if(!window.customElements.get(TAG.AON_INVOICE_HOME)){
	window.customElements.define(TAG.AON_INVOICE_HOME, AonInvoiceHome);
}