import { AonElement } from '../../components/AonElement.js';
import { AonSelect } from '../../components/aon-select.js';

import { Paymethods } from '../../services/paymethod.js';
import { getInvoices, getInvoice, insertInvoice, deleteRawdocInvoices,
	 sendInvoiceMail, downloadInvoices, getAeatCertificates, recordInvoices } from '../../services/service.js';
import { Invoice, getDocumentNumber } from './Invoice.js';

import {addInvoices, setInvoices, setIndex} from './InvoiceCache.js';

import { CONSTANT, MATERIAL_ICONS, MSG, TAG } from '../../environments/environments.js';

import * as ACTION from '../actions.js';
import { formatNumber, isBase64 } from '../../services/utils.js';
import * as LS from '../../services/localStorageService.js';
import { AonDateUtils } from '../utils/AonDateUtils.js';
import { createList } from '../../components/CreateComponent.js';

export class AonInvoiceList extends AonElement {

	more;
	filter;	
	TABLE;

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
			aonInvoiceTable.addColumn(MSG.DATE, 'date', 'dateTable', '120px');
			if (this.getFilter().status === 'accounting' && this.getFilter().type != 'sales') {
				aonInvoiceTable.addColumn(MSG.INVOICE_DOCUMENT_NUMBER, 'string', 'documentNumber', '150px');
			}
			aonInvoiceTable.addColumn(MSG.INVOICE_NUMBER, 'string', 'reference', '150px');
			aonInvoiceTable.addColumn(MSG.HOLDER, 'string', 'name', 'auto');
			aonInvoiceTable.addColumn(MSG.AMOUNT, 'number', 'totalParse', '100px');
			aonInvoiceTable.addColumn('', 'icons', 'icons', '100px');
	
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
		aonInvoiceTable.addColumn(MSG.DATE, 'date', 'dateTable', '120px');;
		aonInvoiceTable.addColumn(MSG.NAME, 'string', 'name', 'auto');
		aonInvoiceTable.addColumn('', 'icons', 'icons', '100px');

		this.init();
		aonInvoiceTable.addEventListener('more', () => {
			if(this.more)
				this.loadMore();
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
		invoice.totalParse = formatNumber(invoice.total, 2, "EUR");
		invoice.icons = this.buildRowIcons(invoice); 
		this.getTable().addRow(invoice, () => this.aonInvoice(invoice, idx), (e) => this.aonInvoiceContextMenu(e, invoice, idx));
	}

	paintProcessingRow(idx, invoice) {
		if(invoice.file) {
			let key = invoice.file.s3Key;
			let keyValues = key.split("/");
			let value = keyValues[keyValues.length - 1];
			let base64 = value.split("_")[1];
			let re = /(?:\.([^.]+))?$/;
			let ext = re.exec(base64)[0];
			base64 = base64.replace(ext, '');
			if(isBase64(base64)) {
				invoice.name = atob(base64) + ext;;
			} else invoice.name = value;
		}
		if(!invoice.name) invoice.name = '';

		this.getTable().addRow(invoice, () => {}, () => {});
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
		
		return icons;
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
		toolbar.addSeparator();

		if(this.getFilter().status === 'inbox') {
			//aonInvoice.addToolbarOption2(ACTION.DELETE_TO_TRASH, () => this.deleteInvoices());
			//aonInvoice.addToolbarOption2(ACTION.REJECT_INVOICE, () => this.rejectInvoices());
			toolbar.addSeparator();
			aonInvoice.addToolbarOption2(ACTION.DOWNLOAD_INVOICE, () => this.downloadInvoices());
			aonInvoice.addToolbarOption2(ACTION.SEND_INVOICE, () => this.sendInvoices());
		} else if(this.getFilter().status === CONSTANT.REFUSED || this.getFilter().status === CONSTANT.REJECTED){
			//aonInvoice.addToolbarOption2(ACTION.DELETE_TO_TRASH, () => this.deleteInvoices());
			//aonInvoice.addToolbarOption2(ACTION.RESTORE_INVOICE, () => this.restoreInvoices());
		} else if(this.getFilter().status ===  CONSTANT.TRASH || this.getFilter().status === CONSTANT.DRAFT){
			//aonInvoice.addToolbarOption2(ACTION.DELETE_FOREVER, () => this.deleteForeverInvoices());
			//aonInvoice.addToolbarOption2(ACTION.RESTORE_INVOICE, () => this.restoreInvoices());
		} else if(this.getFilter().status === 'accounting'){
			aonInvoice.addToolbarOption2(ACTION.DOWNLOAD_INVOICE, () => this.downloadInvoices());
			aonInvoice.addToolbarOption2(ACTION.SEND_INVOICE, () => this.sendInvoices());
			//if(this.isBeta()) {
			//	aonInvoice.addToolbarOption2(ACTION.DELETE_INVOICES, () => this.nullInvoices());
			//}
		}
	}

	deleteInvoices() {
		let cont = 0;
		let aonInvoiceTable = this.getTable();
		aonInvoiceTable.selected.forEach((invoice, i) => {
			invoice.status = CONSTANT.DRAFT;
			insertInvoice(invoice).then(() => {
				cont = cont + 1;
				if(cont === aonInvoiceTable.selected.length) {
					this.init();
				}
			});
		});
	}

	deleteForeverInvoices() {
		let aonInvoice = this.getElement('aonInvoice');
		let d = document.getElementById(aonInvoice.DIALOG);
		d.clear();
		if(!this.isMobile()) d.width = '400px';
		d.setTitle(MSG.DELETE_FOREVER);
		d.setContentHTML('Estás seguro de eliminar las facturas seleccionadas');
		d.addAcceptAction(() => deleteRawdocInvoices(this.getTable().selected.map(r => r.id)).then(() => this.init()));
		d.open();
	}

	rejectInvoices() {
		let cont = 0;
		let aonInvoiceTable = this.getTable();
		aonInvoiceTable.selected.forEach((invoice, i) => {
			invoice.status = CONSTANT.REJECTED;
			insertInvoice(invoice).then(() => {
				cont = cont + 1;
				if(cont === aonInvoiceTable.selected.length) {
					this.init();
				}
			});
		});
	}

	downloadInvoices() {
		let data = {
			domainId: localStorage.getItem('aon_domain_id'),
			domainName: localStorage.getItem('aon_domain_name'),
			domainLogin: localStorage.getItem('aon_domain_login'),
			ids: this.getTable().selected.map(r => r.id),
			status: this.getFilter().status
		};
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


	nullInvoices() {
		let d = this.getApplication().getDialog();
		d.clear();
		if(!this.isMobile()) d.width = '400px';
		d.setTitle(MSG.ACCEPT);
		let certSelect = this.createAonElement(new AonSelect(), "cert", "Certificado");
		getAeatCertificates().then(certs => {
			certSelect.setOptions(certs.map(s => {
				return {
				  value: s.id,
				  name: s.name
				}
			  }));
		}); 
		d.setContent(certSelect);
		d.addAcceptAction(() => {
			this.getApplication().startLoader();
			let data = {invoices: this.getTable().selected}
			data.cert = certSelect.value;
			nullInvoices(data).then(r => {
				this.getApplication().stopLoader(); 
				this.reload();
			}).catch(e => {
				this.getApplication().stopLoader(); 
				this.showError(e)
			});
		});			
		d.open();
	}

	restoreInvoices() {
		let cont = 0;
		let aonInvoiceTable = this.getTable();
		aonInvoiceTable.selected.forEach((invoice, i) => {
			invoice.status = CONSTANT.INBOX;
			insertInvoice(invoice).then(() => {
				cont = cont + 1;
				if(cont === aonInvoiceTable.selected.length) {
					this.init();
				}
			});
		});
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
	}

	getTable(){
		return this.getElement(this.TABLE);
	}

	isProcessing() {
		return this.getFilter().status &&  this.getFilter().status === 'processing';
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

	aonInvoice(invoice, i) {
		if(this.getFilter().status !== 'accounting') {
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

		let deleteTBAI = ACTION.DELETE_INVOICES;
		deleteTBAI.fn = () => this.nullInvoices();
		
		let send = ACTION.SEND_INVOICE;
		send.fn = () => this.sendInvoices();

		let download = ACTION.DOWNLOAD_INVOICE;
	    download.fn = () => this.downloadInvoices();

    	let record = ACTION.RECORD_INVOICE;
    	record.fn = () => this.getApplication().development(MSG.RECORD_INVOICE);

    	let reject = ACTION.REJECT_INVOICE;
    	reject.fn = () => this.rejectInvoices();

    	let restore = ACTION.RESTORE_INVOICE;
    	restore.fn = () => this.restoreInvoices();

    	let addComment = ACTION.COMMENT;
   		addComment.fn = () =>  this.getApplication().development(MSG.ADD_COMMENT);

  		let deleteInvoice = ACTION.DELETE_TO_TRASH;
	  	deleteInvoice.fn = () => this.deleteInvoices();

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
	  		actions = [restore, deleteInvoice];
	  	} else if(inv.isDraft()) {
	  		actions = [restore, deleteForever];
	  	}  else if(inv.isInbox() && number === 1){
			if(this.getDur().isInvoiceManager()){
	    		actions = [download, addComment, deleteInvoice, reject, record, rectify, duplicate];
	  		} else {
	    	  actions = [download, addComment, deleteInvoice, rectify, duplicate];
	    	}
		} else if(inv.isInbox() && number > 1){
			if(this.getDur().isInvoiceManager()){
	    		actions = [download, deleteInvoice, reject, record];
	  		} else {
	    	  actions = [download, deleteInvoice];
	    	}
		} else {
			actions = [send, download];
		}
	  	if(!inv.file && !inv.isEmitida() && number === 1){
	  		actions.push(addFile);
	  	}

	  	d.setMenuOptions(actions, top, left);
	  	d.open();
	}

	getFilter() {
		return this.filter || {status: 'inbox'};
	}

	setFilter(filter) {
		return this.filter = filter;
	}
}
if(!window.customElements.get(TAG.AON_INVOICE_LIST)){
	window.customElements.define(TAG.AON_INVOICE_LIST, AonInvoiceList);
}