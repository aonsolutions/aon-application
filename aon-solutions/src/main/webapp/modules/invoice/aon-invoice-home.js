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
import { getInvofoxConfiguration } from "../../services/invoiceService.js";

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
		if(this.getDur().isInvofox()) {
			getInvofoxConfiguration().then(r => {
				let uploadToast = this.getElement('aonUploadToast');
				if(!uploadToast){ 
					uploadToast = new AonUploadToast();
					uploadToast.invofoxConfiguration = r;
					uploadToast.setDur(this.getDur());
					this.appendChild(uploadToast);
				}
				let data = {
					uploaded : 0
				}
				for (let file of files) {
					uploadToast.addFile("invoice", file, data);
				}
			});
		}else {
			let uploadToast = this.getElement('aonUploadToast');
			if(!uploadToast){ 
				uploadToast = new AonUploadToast();
				uploadToast.invofoxConfiguration = r;
				uploadToast.setDur(this.getDur());
				this.appendChild(uploadToast);
			}
			let data = {
				uploaded : 0
			}
			for (let file of files) {
				uploadToast.addFile("invoice", file, data);
			}
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
		div.style.display = 'grid';
		div.style.gridTemplateColumns = '150px 150px';
    	div.style.gap = '5px';
		div.style.flex = '1';
		div.style.padding = '0px';

		let pendingRecords = this.createDiv();
		pendingRecords.id = 'pendingRecords';
		pendingRecords.style.border = '1px solid #ebebeb';
		pendingRecords.style.display = 'flex';
		pendingRecords.style.flexDirection = 'column';
		pendingRecords.style.justifyContent = 'center';
		pendingRecords.style.gap = '3px';
		pendingRecords.style.textAlign = 'center';
		pendingRecords.style.padding = '8px';
		pendingRecords.style.flex = '1';
		pendingRecords.style.borderRadius = '5px';
		pendingRecords.overflow = 'hidden';
		pendingRecords.style.cursor = 'pointer';
		pendingRecords.style.marginBottom ='3px';
		pendingRecords.style.gridColumn = '1 / -1';

		pendingRecords.addEventListener(EVENT.MOUSEOVER, () => pendingRecords.style.backgroundColor = '#f1f1f1');
		pendingRecords.addEventListener(EVENT.MOUSELEAVE, () => pendingRecords.style.backgroundColor = 'transparent');
		pendingRecords.addEventListener(EVENT.CLICK, () => this.aonInvoiceList(
			{status:'accounting', recorded: 'PENDING', page:1, per_page: 50}
		));

		div.appendChild(pendingRecords);

		let pendingRecordNumber = this.createDiv();
		pendingRecordNumber.id = 'pendingRecordNumber';
		pendingRecordNumber.innerHTML = '0';
		pendingRecordNumber.style.fontWeight = 'bold';
		pendingRecordNumber.style.fontSize = '36px';
		pendingRecordNumber.style.lineHeight = '47px';
		pendingRecordNumber.style.color = 'var(--aonGreen)';

		pendingRecords.appendChild(pendingRecordNumber);

		let pendingRecordName = this.createDiv();
		pendingRecordName.id = 'pendingRecordName';
		pendingRecordName.innerHTML = "Pendientes de Contabilizar";
		pendingRecordName.style.fontWeight = 'normal';
		pendingRecordName.style.color = 'gray';
		pendingRecordName.style.lineHeight = '21px';
		pendingRecords.appendChild(pendingRecordName);

		// PENDING
		let pending = this.createDiv();
		pending.id = 'pending';
		pending.style.border = '1px solid #ebebeb';
		pending.style.flexDirection = 'column';
		pending.style.justifyContent = 'center';
		pending.style.gap = '3px';
		pending.style.textAlign = 'center';
		pending.style.padding = '8px';
		pending.style.display = 'flex';
		pending.style.borderRadius = '5px';
		pending.overflow = 'hidden';
		pending.style.marginBottom ='3px';
		pending.style.gridColumn = '1 / -1';
		div.appendChild(pending);

		let pendingNameRow = this.createDiv();
		pendingNameRow.style.display = "flex";
		pendingNameRow.style.flexDirection = "row";
		pendingNameRow.style.justifyContent = "center";
		pendingNameRow.style.alignItems = "left";
		pending.appendChild(pendingNameRow);
	

		let pendingName = this.createDiv();
		pendingName.id = 'pendingName';
		pendingName.innerHTML = MSG.PENDING_DOCUMENTS;
		pendingName.style.fontWeight = 'normal';
		pendingName.style.color = 'gray';
		pendingName.style.lineHeight = '21px';
		pendingName.style.flexWrap = 'wrap';
		pendingName.style.flexBasis = '33.33%';
		pendingName.style.flexGrow = '3';
		pendingNameRow.appendChild(pendingName);

		let pendingCounterRow = this.createDiv();
		pendingCounterRow.style.display = "flex";
		pendingCounterRow.style.flexDirection = "row";
		pendingCounterRow.style.justifyContent = "center";
		pendingCounterRow.style.alignItems = "left";
		pending.appendChild(pendingCounterRow);

		// PENDING OUTPUT
		let pendingIssued = this.createDiv();
		pendingIssued.id = 'pendingIssued';
		pendingIssued.style.display = 'flex';
		pendingIssued.style.flexDirection = 'column';
		pendingIssued.style.justifyContent = 'center';
		pendingIssued.style.gap = '8px';
		pendingIssued.style.textAlign = 'center';
		pendingIssued.style.padding = '8px';
		pendingIssued.style.flexWrap = 'wrap';
		pendingIssued.style.flexBasis = '33%';
		pendingIssued.style.flexGrow = '1';
		pendingIssued.style.borderRadius = '5px';
		pendingIssued.overflow = 'hidden';
		pendingIssued.style.cursor = 'pointer';
		pendingIssued.style.marginBottom ='3px';
		pendingCounterRow.appendChild(pendingIssued);

		pendingIssued.addEventListener(EVENT.MOUSEOVER, () => pendingIssued.style.backgroundColor = '#f1f1f1');
		pendingIssued.addEventListener(EVENT.MOUSELEAVE, () => pendingIssued.style.backgroundColor = 'transparent');
		pendingIssued.addEventListener(EVENT.CLICK, () => this.aonInvoiceList(
			{status: CONSTANT.INBOX, type: CONSTANT.INBOX},
			{status: CONSTANT.OCR_INBOX, page: 0, perPage: 50, publicStatus:['approved', 'pendingCorrection'], type:['invoice', 'ticket']}
		));

		let pendingIssuedNumber = this.createDiv();
		pendingIssuedNumber.id = 'pendingIssuedNumber';
		pendingIssuedNumber.innerHTML = '0';
		pendingIssuedNumber.style.fontWeight = 'bold';
		pendingIssuedNumber.style.fontSize = '36px';
		pendingIssuedNumber.style.lineHeight = '47px';
		pendingIssuedNumber.style.color = 'var(--aonOrange)';
		pendingIssued.appendChild(pendingIssuedNumber);


		let pendingIssuedName = this.createDiv();
		pendingIssuedName.id = 'pendingIssuedName';
		pendingIssuedName.innerHTML = MSG.ISSUEDS;
		pendingIssuedName.style.fontWeight = 'normal';
		pendingIssuedName.style.color = 'gray';
		pendingIssuedName.style.lineHeight = '21px';
		pendingIssued.appendChild(pendingIssuedName);

		// PENDING RECEIVED
		let pendingReceived = this.createDiv();
		pendingReceived.id = 'pendingReceived';
		pendingReceived.style.display = 'flex';
		pendingReceived.style.flexDirection = 'column';
		pendingReceived.style.justifyContent = 'center';
		pendingReceived.style.gap = '8px';
		pendingReceived.style.textAlign = 'center';
		pendingReceived.style.padding = '8px';
		pendingReceived.style.flexWrap = 'wrap';
		pendingReceived.style.flexBasis = '33%';
		pendingReceived.style.flexGrow = '1';
		pendingReceived.style.borderRadius = '5px';
		pendingReceived.overflow = 'hidden';
		pendingReceived.style.cursor = 'pointer';
		pendingReceived.style.marginBottom ='3px';
		pendingReceived.style.marginLeft ='3px';
		pendingCounterRow.appendChild(pendingReceived);

		pendingReceived.addEventListener(EVENT.MOUSEOVER, () => pendingReceived.style.backgroundColor = '#f1f1f1');
		pendingReceived.addEventListener(EVENT.MOUSELEAVE, () => pendingReceived.style.backgroundColor = 'transparent');
		pendingReceived.addEventListener(EVENT.CLICK, () => this.aonInvoiceList(
			{status: CONSTANT.INBOX, type: CONSTANT.INBOX},
			{status: CONSTANT.OCR_INBOX, page: 0, perPage: 50, publicStatus:['approved', 'pendingCorrection'], type:['invoice', 'ticket']}
		));

		let pendingReceivedNumber = this.createDiv();
		pendingReceivedNumber.id = 'pendingReceivedNumber';
		pendingReceivedNumber.innerHTML = '0';
		pendingReceivedNumber.style.fontWeight = 'bold';
		pendingReceivedNumber.style.fontSize = '36px';
		pendingReceivedNumber.style.lineHeight = '47px';
		pendingReceivedNumber.style.color = 'var(--aonOrange)';
		pendingReceived.appendChild(pendingReceivedNumber);

		let pendingReceivedName = this.createDiv();
		pendingReceivedName.id = 'pendingReceivedName';
		pendingReceivedName.innerHTML = MSG.RECEIVEDS;
		pendingReceivedName.style.fontWeight = 'normal';
		pendingReceivedName.style.color = 'gray';
		pendingReceivedName.style.lineHeight = '21px';
		pendingReceived.appendChild(pendingReceivedName);

		// PENDING TICKETS
		let pendingTicket = this.createDiv();
		pendingTicket.id = 'pendingTicket';
		pendingTicket.style.display = 'flex';
		pendingTicket.style.flexDirection = 'column';
		pendingTicket.style.justifyContent = 'center';
		pendingTicket.style.gap = '8px';
		pendingTicket.style.textAlign = 'center';
		pendingTicket.style.padding = '8px';
		pendingTicket.style.flexWrap = 'wrap';
		pendingTicket.style.flexBasis = '33%';
		pendingTicket.style.flexGrow = '1';
		pendingTicket.style.borderRadius = '5px';
		pendingTicket.overflow = 'hidden';
		pendingTicket.style.cursor = 'pointer';
		pendingTicket.style.marginBottom ='3px';
		pendingTicket.style.marginLeft ='3px';
		pendingCounterRow.appendChild(pendingTicket);

		pendingTicket.addEventListener(EVENT.MOUSEOVER, () => pendingTicket.style.backgroundColor = '#f1f1f1');
		pendingTicket.addEventListener(EVENT.MOUSELEAVE, () => pendingTicket.style.backgroundColor = 'transparent');
		pendingTicket.addEventListener(EVENT.CLICK, () => this.aonInvoiceList(
			{status: CONSTANT.INBOX, type: CONSTANT.INBOX},
			{status: CONSTANT.OCR_INBOX, page: 0, perPage: 50, publicStatus:['approved', 'pendingCorrection'], type:['invoice', 'ticket']}
		));

		let pendingTicketNumber = this.createDiv();
		pendingTicketNumber.id = 'pendingTicketNumber';
		pendingTicketNumber.innerHTML = '0';
		pendingTicketNumber.style.fontWeight = 'bold';
		pendingTicketNumber.style.fontSize = '36px';
		pendingTicketNumber.style.lineHeight = '47px';
		pendingTicketNumber.style.color = 'var(--aonOrange)';
		pendingTicket.appendChild(pendingTicketNumber);

		let pendingTicketName = this.createDiv();
		pendingTicketName.id = 'pendingTicketName';
		pendingTicketName.innerHTML = MSG.TICKETS;
		pendingTicketName.style.fontWeight = 'normal';
		pendingTicketName.style.color = 'gray';
		pendingTicketName.style.lineHeight = '21px';
		pendingTicket.appendChild(pendingTicketName);

		// REJECTED / TRASH

		let pendingRevision = this.createDiv();
		pendingRevision.id = 'pendingRevision';
		pendingRevision.style.border = '1px solid #ebebeb';
		pendingRevision.style.display = 'flex';
		pendingRevision.style.flexDirection = 'column';
		pendingRevision.style.justifyContent = 'center';
		pendingRevision.style.gap = '3px';
		pendingRevision.style.textAlign = 'center';
		pendingRevision.style.padding = '8px';
		pendingRevision.style.flex = '1';
		pendingRevision.style.borderRadius = '5px';
		pendingRevision.overflow = 'hidden';
		pendingRevision.style.cursor = 'pointer';
		pendingRevision.style.marginBottom ='3px';

		pendingRevision.addEventListener(EVENT.MOUSEOVER, () => pendingRevision.style.backgroundColor = '#f1f1f1');
		pendingRevision.addEventListener(EVENT.MOUSELEAVE, () => pendingRevision.style.backgroundColor = 'transparent');
		pendingRevision.addEventListener(EVENT.CLICK, () => this.aonInvoiceList(
			{status: CONSTANT.REJECTED},
			{status: CONSTANT.OCR_INBOX, page: 0, perPage: 50, publicStatus:['pendingDecission', 'discarded'], type: ['invoice', 'ticket']}
		));


		div.appendChild(pendingRevision);

		let pendingRevisionNumber = this.createDiv();
		pendingRevisionNumber.id = 'pendingRevisionNumber';
		pendingRevisionNumber.innerHTML = '0';
		pendingRevisionNumber.style.fontWeight = 'bold';
		pendingRevisionNumber.style.fontSize = '36px';
		pendingRevisionNumber.style.lineHeight = '47px';
		pendingRevisionNumber.style.color = 'var(--aonRed)';
		pendingRevision.appendChild(pendingRevisionNumber);

		let pendingRevisionName = this.createDiv();
		pendingRevisionName.id = 'pendingRevisionName';
		pendingRevisionName.innerHTML = MSG.REJECTEDS;
		pendingRevisionName.style.fontWeight = 'normal';
		pendingRevisionName.style.color = 'gray';
		pendingRevisionName.style.lineHeight = '21px';
		pendingRevision.appendChild(pendingRevisionName);

		// TRASH
		let trash = this.createDiv();
		trash.id = 'trash';
		trash.style.border = '1px solid #ebebeb';
		trash.style.display = 'flex';
		trash.style.flexDirection = 'column';
		trash.style.justifyContent = 'center';
		trash.style.gap = '3px';
		trash.style.textAlign = 'center';
		trash.style.padding = '8px';
		trash.style.flex = '1';
		trash.style.borderRadius = '5px';
		trash.overflow = 'hidden';
		trash.style.cursor = 'pointer';
		trash.style.marginBottom ='3px';

		trash.addEventListener(EVENT.MOUSEOVER, () => trash.style.backgroundColor = '#f1f1f1');
		trash.addEventListener(EVENT.MOUSELEAVE, () => trash.style.backgroundColor = 'transparent');
		trash.addEventListener(EVENT.CLICK, () => this.aonInvoiceList(
			{status: CONSTANT.DRAFT},
			{status: CONSTANT.OCR_INBOX, page: 0, perPage: 50, publicStatus:['discarded', 'rejected', 'error']}
		));

		div.appendChild(trash);

		let trashNumber = this.createDiv();
		trashNumber.id = 'trashNumber';
		trashNumber.innerHTML = '0';
		trashNumber.style.fontWeight = 'bold';
		trashNumber.style.fontSize = '36px';
		trashNumber.style.lineHeight = '47px';
		trashNumber.style.color = 'var(--aonGray)';
		trash.appendChild(trashNumber);

		let trashName = this.createDiv();
		trashName.id = 'trashName';
		trashName.innerHTML = MSG.IN_TRASH;
		trashName.style.fontWeight = 'normal';
		trashName.style.color = 'gray';
		trashName.style.lineHeight = '21px';
		trash.appendChild(trashName);
		
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
		
		let pendingRevision = getCounter()[OPTION.RAWDOC_REJECT.id] || 0;
		let pendingRevisionNumber = this.getElement('pendingRevisionNumber');
		if(pendingRevisionNumber) pendingRevisionNumber.innerHTML = pendingRevision;

		let trash = getCounter()[OPTION.RAWDOC_DRAFT.id] || 0;
		let trashNumber = this.getElement('trashNumber');
		if(trashNumber) trashNumber.innerHTML = trash;
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