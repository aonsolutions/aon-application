import { AonElement } from '../../components/AonElement.js';
import { Paymethods } from '../../services/paymethod.js';
import { getInvoices, getInvoice, insertInvoice, deleteRawdocInvoices, sendInvoiceMail, downloadInvoices, getAeatCertificates, recordInvoices, refreshProcessing, communicateInvoice} from '../../services/service.js';
import { Invoice, getDocumentNumber } from './Invoice.js';
import {addInvoices, setInvoices, setIndex} from './InvoiceCache.js';
import { COLORS, CONSTANT, MATERIAL_ICONS, MSG, TAG } from '../../environments/environments.js';
import { formatNumber, isBase64 } from '../../services/utils.js';
import { AonDateUtils } from '../utils/AonDateUtils.js';
import { createList, createSelect } from '../../components/CreateComponent.js';

import * as ACTION from '../actions.js';
import * as OPTION from './InvoiceOptions.js';
import * as LS from '../../services/localStorageService.js';
import { addCounter, transferCounter } from './InvoiceCounter.js';
import { getFutureRestoreFromOption, getFutureRestoreToOption, getFutureTrashPendingFromOption, getRejectFromOption, getRestoreFromOption, getRestoreToOption, getTrashPendingFromOption } from './InvoiceUtils.js';

export class AonInvoiceList extends AonElement {

	more;
	filter;	
	TABLE;
	icc;

	fn;

	get id() {
		return this.getAttribute(CONSTANT.ID);
	}

	set id(id) {
		this.setAttribute(CONSTANT.ID, id);
	}

	constructor () {
		super();
		this.more = true;
	}

	connectedCallback () {
		this.initialize();
		this.buildDur().then(() => this.build());
 	}

	disconnectedCallback() {
		this.removeInvoiceActions();
	}

	initialize() {
		this.id = this.id || 'aonInvoiceList';
		this.TABLE = 'aonInvoiceTable';
	}

 	build() {
		if(this.isProcessing()) this.buildProcessing();
		else {
			let aonInvoiceTable = createList(this.TABLE); 
			aonInvoiceTable.selectable = 'true';
			this.appendChild(aonInvoiceTable);
			if (this.getFilter().status === 'accounting' && this.getFilter().type === 'sales') {
				aonInvoiceTable.addColumn(MSG.OPERATION_DATE, 'date', 'dateTable', '120px');
				aonInvoiceTable.addColumn(MSG.EXPEDITION_DATE, 'date', 'expDate', '120px');
			} else {
				aonInvoiceTable.addColumn(MSG.DATE, 'date', 'dateTable', '120px');
			}
			if (this.getFilter().status === 'accounting' && this.getFilter().type != 'sales') {
				aonInvoiceTable.addColumn(MSG.INVOICE_DOCUMENT_NUMBER, 'string', 'documentNumber', '150px');
			}
			aonInvoiceTable.addColumn(MSG.INVOICE_NUMBER, 'string', 'reference', '150px');
			aonInvoiceTable.addColumn(MSG.HOLDER, 'string', 'name', 'auto');
			aonInvoiceTable.addColumn(MSG.AMOUNT, 'number', 'totalParse', '100px');
			aonInvoiceTable.addColumn('', 'icons', 'icons', this.isInvoice() ? '140px' : '180px');
	
			this.init();
			aonInvoiceTable.addEventListener('more', () => {
				if(this.more)
					this.loadMore();
			});
	
			aonInvoiceTable.addEventListener('select', () => {
				if(aonInvoiceTable.selected.length === 1) {
					this.addInvoiceActions();
				} else if(aonInvoiceTable.selected.length === 0){
					this.removeInvoiceActions();
				}
			});
		}
	}

	buildProcessing() {
		let aonInvoiceTable = createList(this.TABLE); 
		aonInvoiceTable.selectable = 'true';
		this.appendChild(aonInvoiceTable);
		aonInvoiceTable.addColumn(MSG.DATE, 'date', 'creation_date', '120px');
		aonInvoiceTable.addColumn(MSG.NAME, 'string', 'name', 'auto');
		aonInvoiceTable.addColumn(MSG.USER, 'string', 'creation_user', '120px');
		aonInvoiceTable.addColumn('', 'icons', 'icons', '100px');

		this.init();
		aonInvoiceTable.addEventListener('more', () => {
			if(this.more)
				this.loadMore();
		});

		aonInvoiceTable.addEventListener('select', () => {
			let aonInvoice = this.getElement('aonInvoice');
			if(aonInvoiceTable.selected.length === 0){
				this.removeInvoiceActions();
				aonInvoice.removeToolbarOption(ACTION.REPROCESS);
			} else {
				this.addInvoiceActions();
				if(this.getDur().isInvofox()) {
					this.rp = true;
					aonInvoice.addToolbarOption2(ACTION.REPROCESS, () => this.refreshProcessing());
				}
			} 
		});	
	}

	paintAccountingRow(idx, invoice) {
		if(!invoice.name){
			invoice.name = invoice.type === 'emitida'
				? (invoice.receiver ? invoice.receiver.name : '')
				: (invoice.sender ? invoice.sender.name : '');
		}
		invoice.paymethod = invoice.finances && invoice.finances.length > 0
			? this.getPaymethod(invoice.finances[0].paymethod) : '';
		invoice.date = AonDateUtils.parse(invoice.date);
		invoice.dateTable = AonDateUtils.formatDate(invoice.date);
		invoice.documentNumber =  getDocumentNumber(invoice);
		invoice.totalParse = formatNumber(invoice.total, 2, 2, "EUR");
		invoice.icons = this.buildRowIcons(invoice); 
		let tr = this.getTable().addRow(invoice, () => this.aonInvoice(invoice, idx), (e) => this.aonInvoiceContextMenu(e, invoice, idx));
		if(invoice.altered) {
			// tr.style.backgroundColor = '#ffe3e3';
		}
		tr.id = "aonInvoiceRow";
	}

	paintProcessingRow(idx, invoice) {
		if(invoice.file) {
			let key = invoice.file.s3Key;
			if(key) {
				let keyValues = key.split("/");
				let value = keyValues[keyValues.length - 1];
				let array = value.split("_");
				let base64 = array[array.length - 1];
				let re = /(?:\.([^.]+))?$/;
				let ext = re.exec(base64)[0];
				base64 = base64.replace(ext, '');
				if(isBase64(base64)) {
					invoice.name = atob(base64) + ext;
				} else invoice.name = value;
			}
		}
		if(!invoice.name) invoice.name = '';
		invoice.icons = this.buildProcessingIcons(invoice);

		let tr = this.getTable().addRow(invoice, () => this.aonInvoice(invoice, idx, this.fn), () => {});
		tr.id = "aonInvoiceRow";
	}
		
	loadMore() {
		let aonInvoiceTable = this.getTable();
		let filter = this.getFilter();
		
		if(aonInvoiceTable && filter.page && filter.status === 'accounting') {
			filter.page = filter.page + 1;
			this.setFilter(filter);
			getInvoices(filter).then(invoices => {
				if(invoices.length == 0)
					this.more = false;
				addInvoices(invoices);
				invoices.forEach((invoice, i) => {
					this.paintAccountingRow(i, invoice);
				});
			});
		}
	}

	init() {
		this.more = true;
		let aonInvoiceTable = this.getTable();
		if(aonInvoiceTable) {
			getInvoices(this.getFilter()).then(invoices => {
				if(!this.getDur().isInvoiceManager() && !this.getDur().isInvoicePortal()) {
					invoices = invoices.filter(f => f.creation_user === LS.getDomainLogin());
				} 
				if(this.getFilter().status === CONSTANT.INBOX && this.getFilter().type) {
					invoices = invoices.filter(f => f.type.toLowerCase() === this.getFilter().type.toLowerCase());
				}
				invoices = invoices.sort((a, b) => new Date(b.date) - new Date(a.date));

				setInvoices(invoices);
			
				aonInvoiceTable.removeRows();
				aonInvoiceTable.selected = [];
				this.removeInvoiceActions();
				invoices.forEach((invoice, i) => {
					if(this.isProcessing()) this.paintProcessingRow(i, invoice);
					else this.paintAccountingRow(i, invoice);
				});
			});
		}
	}

	buildProcessingIcons(invoice) {
		let icons = [];
		if(invoice.status === 'processing'){
			let icon = {
				icon: MATERIAL_ICONS.SCHEDULE,
				title: "Procesando",
				color: "gray"
			};
			icons.push(icon);
		}

		if(this.getDur().isInvofox() && (invoice.invofox || invoice.ocrStatus)) {
			let icon = {
				icon: this.getOcrInvoiceStatusIcon(),
				title: this.getOcrInvoiceStatusIconTitle(invoice),
				color: this.getOcrInvoiceStatusIconColor(invoice)
			};
			icons.push(icon);
		}
		return icons;
	}

	buildRowIcons(invoice) {
		let inv = new Invoice(invoice);
		let icons = [];

		let icon = {
			icon: this.getInvoiceTypeIcon(invoice),
			title: this.getInvoiceTypeIconText(invoice),
			color: this.getInvoiceTypeIconColor()
		};
		icons.push(icon);

		if(!inv.isRawdoc() && !invoice.invofox) {
			let icon = {
				icon: this.getInvoiceStatusIcon(invoice),
				title: this.getInvoiceStatusIconText(invoice),
				color: this.getInvoiceStatusIconColor(invoice)
			};
			icons.push(icon);
		}

		if(invoice.invofox || invoice.ocrStatus) {
			let icon = {
				icon: this.getOcrInvoiceStatusIcon(),
				title: this.getOcrInvoiceStatusIconTitle(invoice),
				color: this.getOcrInvoiceStatusIconColor(invoice)
			};
			icons.push(icon);
		}

		if(inv.isSigned()) {
			let icon = {
				icon: MATERIAL_ICONS.LICENSE,
				title: '',
				color: COLORS.AON_DARK_GRAY
			};
			icons.push(icon);			
		} 

		if(inv.isRawdoc()) {
			let icon = {
				icon: MATERIAL_ICONS.INFO,
				title: invoice.creation_date + ' - ' + invoice.creation_user,
				color: COLORS.AON_LIGHT_GRAY
			};
			icons.push(icon);
		}
		
		if (this.hasCommunicationInfo(invoice)) {
			if(!inv.isRawdoc()) {
				let ci = invoice.communicationInfo;
				let keys = Object.keys(ci ?? {});
				for (let i = 0; i < keys.length; i++) {
					let key = keys[i];
					let communicationStatus = ci[key].communicationStatus;
					let communicationType = ci[key].communicationType;
					let icon = {
						icon: MATERIAL_ICONS.QR_CODE_2,
						title: key + " " + this.getCommunicationStatusLabel(communicationType, communicationStatus),
						color: this.getCommunicationStatusColor(communicationType, communicationStatus)
					};
					icons.push(icon);
				}
			}
		}

		return icons;
	}


	hasCommunicationInfo(invoice) {
		return (invoice
			&& invoice.communicationInfo 
			&& Object.keys(invoice.communicationInfo).length > 0);
	}

	getCommunicationStatusLabel(type, status) {
		if("PENDING" === status && "NO_VERIFACTU" !== type) return "Pendiente";
		else if("ACCEPTED" === status || ("PENDING" === status && "NO_VERIFACTU" === type)) return "Aceptada";
		else if("ACCEPTED_WITH_ERRORS" === status) return "Aceptada con errores";
		else if("EXTERNALLY_COMMUNICATED" === status) return "Com. Externamente";
		else if("WRONG" === status) return "Incorrecta";
		else return "Sin Estado";
	}

	getCommunicationStatusColor(type, status) {
		if("PENDING" === status && "NO_VERIFACTU" !== type) return "orange";
		else if("ACCEPTED" === status || ("PENDING" === status && "NO_VERIFACTU" === type)) return "green";
		else if("ACCEPTED_WITH_ERRORS" === status) return "yellow";
		else if("EXTERNALLY_COMMUNICATED" === status) return "blue";
		else if("WRONG" === status) return "red"
		else return "gray";
	}

	getInvoiceTypeIcon(invoice) {
		let inv = new Invoice(invoice);
		if(inv.isEmitida()) return MATERIAL_ICONS.UNARCHIVE;
		if(inv.isRecibida()) return MATERIAL_ICONS.ARCHIVE;
		if(inv.isTicket()) return MATERIAL_ICONS.RECEIPT;
	}

	getInvoiceTypeIconText(invoice) {
		let inv = new Invoice(invoice);
		if(inv.isEmitida()) return 'Emitida';
		if(inv.isRecibida()) return 'Recibida';
		if(inv.isTicket()) return 'Ticket';
	}

	getInvoiceTypeIconColor() {
		return "#5f6368";
	}

	getInvoiceStatusIcon(invoice) {
		let inv = new Invoice(invoice);
		if(inv.isAccounting()) return MATERIAL_ICONS.CHECK_CIRCLE;
		if(inv.isPending()) return MATERIAL_ICONS.ERROR;
	}

	getInvoiceStatusIconText(invoice) {
		let inv = new Invoice(invoice);
		if(inv.isAccounting()) return 'Contabilizada';
		if(inv.isPending()) return 'Pendiente de Contabilizar';
	}

	getInvoiceStatusIconColor(invoice) {
		let inv = new Invoice(invoice);
		if(inv.isAccounting()) return '#5cb85c';
		if(inv.isPending()) return '#8A8A8A';
	}

	getOcrInvoiceStatusIcon() {
		return MATERIAL_ICONS.CIRCLE;
	}

	getOcrInvoiceStatusIconTitle(invoice) {
		let inv = new Invoice(invoice);
		if(inv.isOcrStatus(CONSTANT.PROCESSING) || inv.isRawdocOcrStatus(CONSTANT.PROCESSING)) {
			return MSG.PROCCESSING;
		} else if(inv.isOcrStatus(CONSTANT.APPROVED) || inv.isRawdocOcrStatus(CONSTANT.APPROVED)) {
			return MSG.APPROVED;
		} else if(inv.isOcrStatus(CONSTANT.EXPORTED) || inv.isRawdocOcrStatus(CONSTANT.EXPORTED)) {
			return MSG.EXPORTED;
		} else if(inv.isOcrStatus(CONSTANT.ERROR) || inv.isRawdocOcrStatus(CONSTANT.ERROR)){
			return MSG.ERROR;
		} else if(inv.isOcrStatus(CONSTANT.REJECTED) || inv.isRawdocOcrStatus(CONSTANT.REJECTED)){
			return MSG.REJECTED;
		} else if(inv.isOcrStatus(CONSTANT.DISCARDED) || inv.isRawdocOcrStatus(CONSTANT.DISCARDED)) {
			return MSG.DISCARDED;
		} else if(inv.isOcrStatus(CONSTANT.PENDING_CORRECTION) || inv.isRawdocOcrStatus(CONSTANT.PENDING_CORRECTION)) {
			return MSG.PENDING_CORRECTION;
		}else if(inv.isOcrStatus(CONSTANT.PENDING_DECISSION) || inv.isRawdocOcrStatus(CONSTANT.PENDING_DECISSION)) {
			return MSG.PENDING_DECISSION;
		} else { 
			return "";
		}
	}

	getOcrInvoiceStatusIconColor(invoice) {
		let inv = new Invoice(invoice);
		if(inv.isOcrStatus(CONSTANT.PROCESSING) || inv.isRawdocOcrStatus(CONSTANT.PROCESSING)) {
			return '#bbb';
		} else if(inv.isOcrStatus(CONSTANT.APPROVED, CONSTANT.EXPORTED) || inv.isRawdocOcrStatus(CONSTANT.APPROVED, CONSTANT.EXPORTED)) {
			return 'green';
		} else if(inv.isOcrStatus(CONSTANT.ERROR, CONSTANT.REJECTED, CONSTANT.DISCARDED) || inv.isRawdocOcrStatus(CONSTANT.ERROR, CONSTANT.REJECTED, CONSTANT.DISCARDED)) {
			return 'red';
		} else if(inv.isOcrStatus(CONSTANT.PENDING_CORRECTION, CONSTANT.PENDING_DECISSION) || inv.isRawdocOcrStatus(CONSTANT.PENDING_CORRECTION, CONSTANT.PENDING_DECISSION)) {
			return 'orange';
		} else { 
			return "#5f6368";
		}
	}
	
	addInvoiceActions() {
		this.removeInvoiceActions();
		let aonInvoice = this.getElement('aonInvoice');
		let toolbar = this.getElement(aonInvoice.TOOLBAR);

		if(this.getFilter().status === 'inbox') {
			toolbar.addSeparator();
			aonInvoice.addToolbarOption2(ACTION.DELETE_TO_TRASH, () => this.toTrash());
		} else if(this.getFilter().status === CONSTANT.REJECTED){
			toolbar.addSeparator();
			aonInvoice.addToolbarOption2(ACTION.DELETE_TO_TRASH, () => this.toTrash());

		} else if(this.getFilter().status === CONSTANT.TRASH){
			toolbar.addSeparator();
			aonInvoice.addToolbarOption2(ACTION.DELETE_FOREVER, () => this.deleteForeverInvoices());
			aonInvoice.addToolbarOption2(ACTION.RESTORE_INVOICE, () => this.restoreInvoices());
		} else if(this.getFilter().status === 'accounting'){
			toolbar.addSeparator();
			aonInvoice.addToolbarOption2(ACTION.DOWNLOAD_INVOICE, () => this.downloadInvoices());
			aonInvoice.addToolbarOption2(ACTION.SEND_INVOICE, () => this.sendInvoices());
			if(this.isSig() || this.getDur().isAlpha()) 
				aonInvoice.addToolbarOption2(ACTION.COMMUNICATE_INVOICE, () => this.communicateInvoices());
		} else if(this.isProcessing()) {
			toolbar.addSeparator();
			aonInvoice.addToolbarOption2(ACTION.DELETE_TO_TRASH, () => this.toTrash());
		}
	}

	communicateInvoices() {
		if(this.icc.isTbai() || this.icc.isLroe() || this.icc.isVerifactu() || this.icc.isSii()) {	
			this.certificateDialog((certificate) => this.communicatingInvoices(certificate));
		} else  this.communicatingInvoices();
	}

	certificateDialog(action) {
		let dialog = this.getApplication().getDialog();
		dialog.clear();
		if(!this.isMobile()) dialog.width = '400px';
		dialog.setTitle(MSG.COMMUNICATE_SELECTED_INVOICES);

		let certSelect = createSelect("cert", MSG.CERTIFICATE);
		certSelect.setAlias("id", "name");	

		getAeatCertificates().then(certs => certSelect.setOptions(certs));
		dialog.setContent(certSelect);
		dialog.addAcceptAction(() => action(certSelect.value));
		dialog.open();
	}

	communicatingInvoices(certificate) {
		console.log("CERTIFICADO: " + certificate);
		let div = this.createDiv();
		let dialog = this.getApplication().getDialog();
		dialog.clear();
		if(!this.isMobile()) dialog.width = '400px';
		dialog.setTitle(MSG.COMMUNICATING_SELECTED_INVOICES);
		dialog.setContent(div);
		dialog.open();

		let aonInvoiceTable = this.getTable();
		aonInvoiceTable.selected.forEach((invoice, i) => {
			let icDiv = this.createDiv("invoiceCommunicationDiv" + i);
			icDiv.innerHTML = invoice.reference + " - comunicando...";
			div.appendChild(icDiv);
			let data = {
				invoice: invoice.id,
				certificate
			}
			communicateInvoice(data).then(() => {
				icDiv.innerHTML = invoice.reference + " - comunicada con éxito";
				icDiv.style.color = "green";
				icDiv.style.fontWeight = "bold";
			}).catch(e => {
				icDiv.innerHTML = invoice.reference + " - ERROR: " + e.message;
				icDiv.style.color = "red";
				icDiv.style.fontWeight = "bold";
			});
		});
	}

	toTrash() {
		let aonInvoice = this.getElement('aonInvoice');
		let d = document.getElementById(aonInvoice.DIALOG);
		d.clear();
		if(!this.isMobile()) d.width = '400px';
		d.setTitle(MSG.DELETE_FOREVER);
		d.setContentHTML('Estás seguro de enviar las facturas seleccionadas a la papelera?');
		d.addAcceptAction(() => {
			let cont = 0;
			let aonInvoiceTable = this.getTable();
			aonInvoiceTable.selected.forEach((invoice, i) => {
				this.updateCounter(getTrashPendingFromOption(invoice), OPTION.RAWDOC_TRASH, 1);
				this.updateCounter(getFutureTrashPendingFromOption(invoice), OPTION.FUTURE_RAWDOC_TRASH, 1);
				invoice.status = CONSTANT.TRASH;
				insertInvoice(invoice).then(() => {
					cont = cont + 1;
					if(cont === aonInvoiceTable.selected.length) {
						this.init();
					}
				});
			});
		});
		d.open();
	}

	setIcc(icc) {
		this.icc = icc; 
	}

	deleteForeverInvoices() {
		let aonInvoice = this.getElement('aonInvoice');
		let d = document.getElementById(aonInvoice.DIALOG);
		d.clear();
		if(!this.isMobile()) d.width = '400px';
		d.setTitle(MSG.DELETE_FOREVER);
		d.setContentHTML('Estás seguro de eliminar las facturas seleccionadas');
		d.addAcceptAction(() => {
			this.updateCounter(OPTION.RAWDOC_TRASH, undefined, -1 * this.getTable().selected.length);
			this.updateCounter(OPTION.FUTURE_RAWDOC_TRASH, undefined, -1 * this.getTable().selected.length);
			deleteRawdocInvoices(this.getTable().selected.map(r => r.id)).then(() => this.init())
		});
		d.open();
	}
	
	refreshProcessing() {
		if(this.rp) {
			this.rp = false;
			this.getApplication().startLoading();
			let rawdoc = this.getTable().selected.filter(f =>  f.status === 'processing').map(r => r.id);
			refreshProcessing({rawdoc}).then(r => {
				this.getApplication().stopLoading();
			    let parent = this.getApplication().getParent();
				parent.buildCounter();
	    		parent.aonInvoiceList({status: CONSTANT.PROCESSING});
				let aonInvoice = this.getElement('aonInvoice');
				aonInvoice.removeToolbarOption(ACTION.REPROCESS);
			}).catch(e => {
				let aonInvoice = this.getElement('aonInvoice');
				aonInvoice.removeToolbarOption(ACTION.REPROCESS);
				this.getApplication().stopLoading();
				this.showError(e);	
			});
		}
  	}

	downloadInvoices() {
		let data = this.getFilter();
		data.domainId = LS.getDomainId();
		data.domainName = LS.getDomainName();
		data.domainLogin = LS.getDomainLogin();
		if(!this.getTable().selectedAll) {
			data.ids = this.getTable().selected.map(r => r.id);
		}

		let json = btoa(JSON.stringify(data));
		downloadInvoices(json);
	}

	sendInvoices() {
		let aonInvoice = this.getElement('aonInvoice');
		let d = document.getElementById(aonInvoice.DIALOG);
		d.clear();
		if(!this.isMobile()) d.width = '400px';
		d.setTitle(MSG.SEND_INVOICES);
		d.setContentHTML('<aon-input id="sendInvoicesMail" description="Email"></aon-input>');
		d.addAcceptAction(() => {
			let mail = this.getElement('sendInvoicesMail');
			let message = {
				to: mail.value,
				invoices: this.getTable().selected
			};
			sendInvoiceMail(message).then(() => {});
		});
		d.open();
	}

	recordInvoices() {
		let aonInvoice = this.getElement('aonInvoice');
		let d = document.getElementById(aonInvoice.DIALOG);
		d.clear();
		if(!this.isMobile()) d.width = '400px';
		d.setTitle(MSG.RECORD_INVOICE);
		d.setContentHTML('Estás seguro de aceptar y contabilizar las facturas seleccionadas');
		d.addAcceptAction(() => {
			let data = {
				invoices:this.getTable().selected.map(r => r.id)			
			}
			recordInvoices(data).then(() => this.init())
		});
		d.open();
	}

	restoreInvoices() {
		let cont = 0;
		let aonInvoiceTable = this.getTable();
		aonInvoiceTable.selected.forEach((invoice, i) => {
			this.updateCounter(getRestoreFromOption(invoice), getRestoreToOption(invoice), 1);
			this.updateCounter(getFutureRestoreFromOption(invoice), getFutureRestoreToOption(invoice), 1);
			invoice.status = CONSTANT.INBOX;
			insertInvoice(invoice).then(() => {
				cont = cont + 1;
				if(cont === aonInvoiceTable.selected.length) {
					this.init();
				}
			});
		});
	}

	updateCounter(from, to, count) {
		if(!to) {
			addCounter(from, count);
			this.getApplication().getParent().updateCounterSpan(from);
		}  else {
			transferCounter(from, to, count);
			this.getApplication().getParent().updateCounterSpan(from);
			this.getApplication().getParent().updateCounterSpan(to);
		}
	}
	
	removeInvoiceActions() {
		let aonInvoice = this.getElement('aonInvoice');
		let toolbar = this.getElement(aonInvoice.TOOLBAR);
		toolbar.removeSeparators();
		aonInvoice.removeToolbarOption(ACTION.DELETE_TO_TRASH);
		aonInvoice.removeToolbarOption(ACTION.REJECT_INVOICE);
		aonInvoice.removeToolbarOption(ACTION.RESTORE_INVOICE);
		aonInvoice.removeToolbarOption(ACTION.DELETE_FOREVER);
		aonInvoice.removeToolbarOption(ACTION.DOWNLOAD_INVOICE);
		aonInvoice.removeToolbarOption(ACTION.SEND_INVOICE);
		aonInvoice.removeToolbarOption(ACTION.COMMUNICATE_INVOICE);
	}

	getTable(){
		return this.getElement(this.TABLE);
	}

	isProcessing() {
		return this.getFilter().status &&  this.getFilter().status === 'processing';
	}

	isInvoice() {
		return this.getFilter().status &&  this.getFilter().status === 'accounting';
	}

	getPaymethod(paymethod) {
		let value = '';
		Paymethods.forEach((item, i) => {
			if(item.value === paymethod) {
				value = item.name;
			}
		});
		return value;
	}

	aonInvoice(invoice, i, fn) {
		if(this.fn) {
			setIndex(i);
			this.fn(invoice, i);
		} else if(this.getFilter().status !== 'accounting') {
			setIndex(i);
			let aip = document.querySelector('aon-invoice-panel');
			aip.aonInvoice(invoice.type, invoice);
		} else {
			getInvoice(invoice.id).then((inv) => {
				setIndex(i);
				let aip = document.querySelector('aon-invoice-panel');
				aip.aonInvoice(invoice.type, inv);
			});
		}
	}

	aonInvoiceContextMenu(e, invoice, i) {
		const number = this.getTable().selected.length;
		
		let inv = new Invoice(invoice);
	
		e.preventDefault();
		let rect = e.target.getBoundingClientRect();
    	let x = e.clientX - rect.left;
		let y = e.clientY - rect.top;

		const top  = rect.top + y;
	  	const left = rect.left + x;

    	let aonInvoice = this.getElement('aonInvoice');
   		let d = document.getElementById(aonInvoice.OPTION_DIALOG);

		let send = ACTION.SEND_INVOICE;
		send.fn = () => this.sendInvoices();

		let download = ACTION.DOWNLOAD_INVOICE;
	    download.fn = () => this.downloadInvoices();

    	let restore = ACTION.RESTORE_INVOICE;
    	restore.fn = () => this.restoreInvoices();

    	let addComment = ACTION.COMMENT;
   		addComment.fn = () =>  this.getApplication().development(MSG.ADD_COMMENT);

  		let toTrash = ACTION.DELETE_TO_TRASH;
	  	toTrash.fn = () => this.toTrash();

	  	let deleteForever = ACTION.DELETE_FOREVER;
	  	deleteForever.fn = () => this.deleteForeverInvoices();
		  
	  	let rectify = ACTION.RECTIFY_INVOICE;
	  	rectify.fn = () => this.getApplication().development(MSG.RECTIFY_INVOICE);

	  	let duplicate = ACTION.DUPLICATE_INVOICE;
	  	duplicate.fn = () => this.getApplication().development(MSG.DUPLICATE_INVOICE);

    	let addFile = ACTION.ADD_FILE;
  		addFile.fn = () => this.getApplication().development(MSG.ADD_FILE);

	  	let actions = [];
	  	if(inv.isRejected()) {
	  		actions = [toTrash];
	  	} else if(inv.isTrash()) {
	  		actions = [restore, deleteForever];
	  	}  else if(inv.isInbox() && number === 1){
			if(this.getDur().isInvoiceManager()){
				actions = [addComment, toTrash, reject, rectify, duplicate];
	  		} else {
	    	  actions = [addComment, toTrash, rectify, duplicate];
	    	}
		} else if(inv.isInbox() && number > 1){
			if(this.getDur().isInvoiceManager()){
	    		actions = [toTrash, reject];
	  		} else {
	 			actions = [toTrash];
	    	}
		} else if(!inv.isRawdoc()){
			actions = [send, download];
		}
	  	if(!inv.file && !inv.isEmitida() && number === 1){
	  		actions.push(addFile);
	  	}
		if(actions.length > 0) {
		  	d.setMenuOptions(actions, top, left);
		  	d.open();
		}
	}

	getFilter() {
		return this.filter || {status: 'inbox'};
	}

	setFilter(filter) {
		return this.filter = filter;
	}

	setFn(fn) {
		this.fn = fn;
	}
}
if(!window.customElements.get(TAG.AON_INVOICE_LIST)){
	window.customElements.define(TAG.AON_INVOICE_LIST, AonInvoiceList);
}