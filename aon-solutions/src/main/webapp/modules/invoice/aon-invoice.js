import { AonElement } from '../../components/AonElement.js';
import { getInvoice, getInvoiceAccounts, insertInvoice, acceptInvoice, deleteInvoice, deleteRawdocInvoices,
	 getCompanyActivities, getPaymethods, getRegistry, getRegistryBanks, sendInvoice2Mail, getRegistryPaymethod, getSalesSeries, 
	 signInvoice, getInvoiceConfiguration, saveInvofoxDocument, getAeatCertificates, getWorkplaces, getTbaiHistory, downloadFacturae, getCustomerEmails,
	getPaymethod, getInvofoxTextContent, getSupplierTransaction, getCreditorTransaction, recordSelfconta, getRegistrySuggestedAccount, 
	getInvofoxDocument} from '../../services/service.js';
import { getCompany } from '../../services/companyService.js';
	 import { Invoice, getDocumentNumber } from './Invoice.js';
import { getNextInvoice, getPreviousInvoice } from './InvoiceCache.js';
import { ToolbarType } from '../../models/enums.js';

import { AonToolbar } from '../../components/aon-toolbar.js';
import { AonCard } from '../../components/aon-card.js';
import { AonViewer } from '../../components/aon-viewer.js';
import { CONSTANT, CSS, EVENT, MATERIAL_ICONS, MSG, TAG } from '../../environments/environments.js';

import * as GWT from '../../gwt/gwt.js';
import * as ACTION from '../actions.js';
import { Transactions } from '../../services/transaction.js';
import { ErrCode, ErrKey, getTaxPercentageOption, getTaxType, getTaxTypeName, TaxIVAPercentage, TaxType, WithholdingType } from './invoiceEnums.js';
import { getInvestAssets, getItems} from '../../services/productService.js';
import * as LS from '../../services/localStorageService.js';
import { AonBasicTable } from '../../components/aon-basic-table.js';
import { AonNewDate } from '../../components/aon-new-date.js';
import { AonDialog } from '../../components/aon-dialog.js';
import { AonIconButton } from '../../components/aon-icon-button.js';
import { AonInput } from '../../components/aon-input.js';
import { AonNumber } from '../../components/aon-number.js';
import { AonCustomerSuggestion } from '../registry/customer/aon-customer-suggestion.js';
import { AonSelect } from '../../components/aon-select.js';
import { AonSuggestion } from '../../components/aon-suggestion.js';
import { AonSwitch } from '../../components/aon-switch.js';
import { AonTab } from '../../components/aon-tab.js';
import { AonEmail } from '../../components/aon-email.js';
import { AonAutosizeTextarea } from '../../components/aon-autosize-textarea.js';

import {INVOICE} from  '../../services/app.js';
import { AonDate } from '../../components/aon-date.js';
import { AonNewInput } from '../../components/aon-new-input.js';
import { AonNewSuggestion } from '../../components/aon-new-suggestion.js';
import { AonNewNumber } from '../../components/aon-new-number.js';
import { AonNewSelect } from '../../components/aon-new-select.js';
import { AonNewTextarea } from '../../components/aon-new-textarea.js';
import { AonRegistrySuggestion } from '../registry/aon-registry-suggestion.js';

export class AonInvoice extends AonElement {

	invoice;
	focusId;
	TOOLBAR;
	GENERAL
	GENERAL_CARD;
	COMMENT_CARD;
	INVOICE_CARD;
	DETAIL_CARD;
	FINANCE_CARD;
	DATA;
	FILE;
	RECORD_INVOICE_DIALOG;

	fileOpened;

	company;
	rbanks;
	series;
	configuration;

	get id() {
		return this.getAttribute(CONSTANT.ID);
	   }

	set id(id) {
	   this.setAttribute(CONSTANT.ID, id);
	}

	get type() {
	  return this.getAttribute(CONSTANT.TYPE);
 	}

	set type(type) {
	    this.setAttribute(CONSTANT.TYPE, type);
  	}

	get autosave() {
		return this.getAttribute(CONSTANT.AUTOSAVE);
  	}

  	set autosave(autosave) {
		this.setAttribute(CONSTANT.AUTOSAVE, autosave);
	}

	constructor () {
   		super();
	}

	async connectedCallback () {
		this.initialize();
		this.initializeFunctions();
		this.buildDur().then(r => {
			this.build();
		});
		
		this.configuration = await getInvoiceConfiguration();
		getCompany().then(company => {
			const registry = this.getInvoice().isEmitida()
				? this.getInvoice().getRegistry().id 
				: company.id;
			this.company = company;
			this.getInvoice().surcharge = this.getInvoice().surcharge || company.surcharge;
			this.getInvoice().vatAccrualPayment = this.getInvoice().vatAccrualPayment || company.vatAccrualPayment;
			if(registry) {
				let data = {
					id: registry,
					registry: registry,
					additional_info: ['banks', 'paymethods']
				};

				getRegistry(data).then(r => {
					this.rbanks = r.banks;
					this.rpaymethods = r.paymethods;
				});
			}
		});


	}

	initialize(){
		this.accept = true;
		this.rbanks = [];
		this.fileOpened = this.fileOpened || false;
		this.id = this.id || 'aonInvoiceSheet';
		this.TOOLBAR = this.id + 'Toolbar';
		this.DIV = this.id + CONSTANT.DIV.initCap();
		this.TABS = this.id + 'Tabs';
		this.CONTENT = this.id + 'Content';
		this.DATA = this.id + 'Data';
		this.TBAI_CARD = this.id + 'Tbai' + CONSTANT.CARD.initCap();
		this.COMMUNICATION = this.id + 'Communication';
		this.GENERAL = this.DATA + 'General';
		this.GENERAL_CARD = this.GENERAL + CONSTANT.CARD.initCap();
		this.GENERAL_CARD_TABLE = this.GENERAL_CARD + CONSTANT.TABLE.initCap();
		this.COMMENTS = this.DATA + 'Comments';
		this.COMMENT_CARD = this.DATA + 'CommentsCard';
		this.REMARKS_CARD = this.DATA + 'RemarksCard';
		this.FILE = this.id + 'File';
		this.INPUT_FILE = this.id + 'InputFile'
		this.invoice = this.invoice || new Invoice().setType(this.type);
		this.options = this.options || [
			{ title: MSG.GENERAL_DATA, fn: () => this.buildInvoiceContent()},
			{ title: MSG.COMMUNICATION, fn: () => this.buildCommunication()}
		];

		this.SERIE = CONSTANT.AON_INVOICE + CONSTANT.SERIE.initCap();
		this.SERVICE = CONSTANT.AON_INVOICE + CONSTANT.SERVICE.initCap();
		this.INVESTMENT = CONSTANT.AON_INVOICE + CONSTANT.INVESTMENT.initCap();
		this.RECTIFIED = CONSTANT.AON_INVOICE + CONSTANT.RECTIFIED.initCap();
		this.NUMBER = CONSTANT.AON_INVOICE + CONSTANT.NUMBER.initCap();
		this.REFERENCE = CONSTANT.AON_INVOICE + CONSTANT.REFERENCE.initCap();
		this.DATE = CONSTANT.AON_INVOICE + CONSTANT.DATE.initCap();
		this.TOTAL = CONSTANT.AON_INVOICE + CONSTANT.TOTAL.initCap();
		this.REGISTRY = CONSTANT.AON_INVOICE + CONSTANT.REGISTRY.initCap();
		this.CATEGORY = CONSTANT.AON_INVOICE + CONSTANT.CATEGORY.initCap();
		this.PAYMETHOD = CONSTANT.AON_INVOICE + CONSTANT.PAYMETHOD.initCap();
		this.WORKPLACE = CONSTANT.AON_INVOICE + CONSTANT.WORKPLACE.initCap();
		this.DIALOG_BLANK = CONSTANT.AON_INVOICE + 'DialogBlank';

		// ----- TAX

		this.TAX = CONSTANT.AON_INVOICE_TAX;
		this.TAX_TABLE = this.TAX + CONSTANT.TABLE.initCap();
		this.TAX_TABLE2 = this.TAX_TABLE + '2';
		this.TAX_DIV = this.TAX + 'Div';
	
		this.ACTIVITY = CONSTANT.AON_INVOICE + CONSTANT.ACTIVITY.initCap();

		this.TRANSACTION_TYPE = CONSTANT.AON_INVOICE + CONSTANT.TRANSACTION_TYPE.initCap();
		this.SURCHARGE = CONSTANT.AON_INVOICE + CONSTANT.SURCHARGE.initCap();
		this.WITHHOLDING = CONSTANT.AON_INVOICE + CONSTANT.WITHHOLDING.initCap();
		this.WITHHOLDING_FARMER = CONSTANT.AON_INVOICE + CONSTANT.WITHHOLDING_FARMER.initCap();
		this.VAT_ACCRUAL_PAYMENT = CONSTANT.AON_INVOICE + CONSTANT.VAT_ACCRUAL_PAYMENT.initCap();
		 
		this.TAX_TYPE = this.TAX + CONSTANT.TYPE.initCap();
		this.TAX_PERCENTAGE = this.TAX + CONSTANT.PERCENTAGE.initCap();
		this.TAX_BASE = this.TAX + CONSTANT.BASE.initCap();
		this.TAX_QUOTA = this.TAX  + CONSTANT.QUOTA.initCap();
		this.TAX_DELETE = this.TAX  + CONSTANT.DELETE.initCap();
		this.TAX_ADD = this.TAX  + CONSTANT.ADD.initCap();

		// ----- DETAIL

		this.DETAIL = CONSTANT.AON_INVOICE_DETAIL;
		this.DETAIL_TABLE = this.DETAIL + CONSTANT.TABLE.initCap();
		this.DETAIL_ADD = this.DETAIL + CONSTANT.ADD.initCap();
		this.DETAIL_DELETE = this.DETAIL + CONSTANT.DELETE.initCap();
		this.DETAIL_OPTIONS = this.DETAIL + CONSTANT.OPTIONS.initCap();
		this.DETAIL_DESCRIPTION = this.DETAIL + CONSTANT.DESCRIPTION.initCap();
		this.DETAIL_QUANTITY = this.DETAIL + CONSTANT.QUANTITY.initCap();
		this.DETAIL_PRICE = this.DETAIL + CONSTANT.PRICE.initCap();
		this.DETAIL_DISCOUNT = this.DETAIL + CONSTANT.DISCOUNT.initCap();
		this.DETAIL_AMOUNT = this.DETAIL + CONSTANT.AMOUNT.initCap();
		this.DETAIL_VAT = this.DETAIL + CONSTANT.VAT.initCap();
		this.DETAIL_WITHHOLDING = this.DETAIL + CONSTANT.WITHHOLDING.initCap();
		this.DETAIL_PREPAYMENT = this.DETAIL + CONSTANT.PREPAYMENT.initCap();
		this.DETAIL_CATEGORY = this.DETAIL + CONSTANT.CATEGORY.initCap();
		// TODO BIEN AFECTO

		// ----- FINANCE

		this.FINANCE = CONSTANT.AON_INVOICE_FINANCE;
		this.FINANCE_TABLE = this.FINANCE + CONSTANT.TABLE.initCap();
		this.FINANCE_ADD = this.FINANCE + CONSTANT.ADD.initCap();
		this.FINANCE_DUE_DATE = this.FINANCE + CONSTANT.DUE_DATE.initCap();
		this.FINANCE_PAYMETHOD = this.FINANCE + CONSTANT.PAYMETHOD.initCap();
		this.FINANCE_BANK_ACCOUNT = this.FINANCE + CONSTANT.BANK_ACCOUNT.initCap();
		this.FINANCE_AMOUNT = this.FINANCE + CONSTANT.AMOUNT.initCap();
		this.FINANCE_DELETE = this.FINANCE + CONSTANT.DELETE.initCap();
		
		// ------ MESSAGES/ERRRORS
		this.MESSAGES = this.DATA + 'Messages';
		this.ERRORS_CARD = this.DATA + 'ErrorsCard';

		this.RECORD_INVOICE_DIALOG = this.id + 'RecordInvoiceDialog';
	}

	initializeFunctions() {
		window.getInvoice = () => {
			return JSON.stringify(this.getInvoice());
		}

		window.reloadInvoice = (invoiceId) => {
			if(invoiceId) {
				this.isInvofoxInvoice() && this.setInvofoxState(CONSTANT.EXPORTED);
				getInvoice(invoiceId).then((inv) => {
					this.invoice = new Invoice(inv);
					this.reload();
				});
				this.getApplication().getParent().buildCounter();
				
			} 

		}
	}

	getInvoice() {
		return this.invoice;
	}

	setInvoice(invoice) {
		this.invoice = new Invoice(invoice);
		if(!invoice)
			this.invoice.setType(this.type);
	}

	setType(type) {
		this.setAttribute('type', type);
	}

	build() {
		this.clear();
		this.buildInputFile();
		this.buildToolbar();
		this.buildContent();
		this.focus();
		this.resize();
		window.addEventListener(EVENT.RESIZE, () => {
			this.resize();
		});
	}

	resize() {
		if(!this.isMobile()){
			let general = this.getElement(this.GENERAL);
			let generalCard = this.getElement(this.GENERAL_CARD);
			let tax = this.getElement(this.TAX);
			let remarksCard = this.getElement(this.REMARKS_CARD);
			let commentCard = this.getElement(this.COMMENT_CARD);
			
			if(window.innerWidth && window.innerWidth > 1100 && !this.fileOpened){
				if(general) general.style.display='flex';
				if(generalCard) generalCard.style.width = '50%';
				if(tax) tax.style.width = '50%';
				let hasComment = this.invoice.comments && this.invoice.comments != undefined && this.invoice.comments != '';
				let hasRemarks = this.invoice.remarks && this.invoice.remarks.length > 0;
				if(hasComment && hasRemarks) {
					if(remarksCard) remarksCard.style.width = '50%';
					if(commentCard) commentCard.style.width = '50%';
				}

			} else if(window.innerWidth && window.innerWidth < 1050){
				if(general) general.style.display='block';
				if(generalCard) generalCard.style.width = '100%';
				if(tax) tax.style.width = '100%';
				if(remarksCard) remarksCard.style.width = '100%';
				if(commentCard) commentCard.style.width = '100%';
			} 
				
			if(window.innerWidth && window.innerWidth < 900){
				this.getApplication().closeSidenav();
			}
		}
	}

	reload(){
		this.clear();
		this.build();
	}

	setFocus(focusId) {
		this.focusId = focusId;
	}

	focus() {
		if(this.focusId)
			this.getElement(this.focusId).focus();
	}

	buildInputFile() {
		let inputFile = this.createElement(TAG.INPUT);
		inputFile.id = this.INPUT_FILE;
		inputFile.style.display = 'none';
		inputFile.type = 'file';
		inputFile.name = 'file';
		this.appendChild(inputFile);
		inputFile.addEventListener('change', ({target}) => this.preview());
	} 

	preview() {
		let fileDiv = this.getElement(this.FILE);
		let fileInput = this.getElement(this.INPUT_FILE);
		const file = fileInput.files[0];

		const READER = new FileReader();
		READER.readAsDataURL(file);
		READER.onload = (_event) => {
			this.attach(READER.result, file.type);
		};
	}

	attach(fileDataUri,  mimetype){
		if (fileDataUri.length > 0) {
			const base64File = fileDataUri.split(',')[1];
			const data = {
				file: {
					content: base64File,
					contentType: mimetype,
					contentEncoding: 'base64'
				},
				invoice: this.getInvoice()
			};
			let aonInvoice = document.getElementById('aonInvoice');
			aonInvoice.startLoader();
			insertInvoice(data).then((r) => {
				aonInvoice.stopLoader();
				this.invoice = new Invoice(r);
				this.reload();
			});
		}
	}

	buildToolbar() {
		let invoiceToolbar = new AonToolbar();
		invoiceToolbar.id = this.TOOLBAR;
		invoiceToolbar.type = ToolbarType.SECONDARY;
		invoiceToolbar.title = this.getInvoiceTitle();
		this.appendChild(invoiceToolbar);
		invoiceToolbar.removeButtons();
		invoiceToolbar.addButton2(ACTION.NEXT, () => this.nextInvoice());
		invoiceToolbar.addButton2(ACTION.PREVIOUS, () => this.previousInvoice());
		invoiceToolbar.addSeparator();

		if(!this.getInvoice().isOcrStatus(CONSTANT.REJECTED, CONSTANT.DISCARDED, CONSTANT.PENDING_DECISSION, CONSTANT.ERROR )
			&& !this.getInvoice().isDraft()){
			invoiceToolbar.addButton('Options', 'more_vert', (e) => {
				e.preventDefault();
				let rect = e.target.getBoundingClientRect();
				let x = e.clientX - rect.left;
				let y = e.clientY - rect.top;
	
				const top  = rect.top + y;
				const left = rect.left + x;
	
				let d = document.getElementById(aonInvoice.OPTION_DIALOG);
				let moreActions = [];
				if(this.getInvoice().isInbox() ){
					let remarks = ACTION.REMARKS;
					remarks.permission = true;
					remarks.backgroundColor = INVOICE.color;
					remarks.fn = () => this.addInvoiceRemarks();
					moreActions.push(remarks);
				}
	
				let comment = ACTION.COMMENT;
				comment.permission = true;
				comment.backgroundColor = INVOICE.color;
				comment.fn = () => this.addInvoiceComment();
				moreActions.push(comment);
	
				let send = ACTION.SEND_INVOICE;
				send.permission = true;
				send.backgroundColor = INVOICE.color;
				send.fn = () => this.sendInvoice();
				moreActions.push(send);
	
				if(!this.getInvoice().isRawdoc()){
					let rectify = ACTION.RECTIFY_INVOICE;
					rectify.permission = true;
					rectify.backgroundColor = INVOICE.color;
					rectify.fn = () => this.rectifyInvoice();
					moreActions.push(rectify);
				}
	
				let duplicate = ACTION.DUPLICATE_INVOICE;
				duplicate.permission = true;
				duplicate.backgroundColor = INVOICE.color;
				duplicate.fn = () => this.duplicateInvoice();
				moreActions.push(duplicate);
				if(this.getInvoice().isInbox()){
					let changeType = ACTION.CHANGE_TYPE;
					changeType.permission = true;
					changeType.backgroundColor = INVOICE.color;
					changeType.fn = () => this.changeType();
					moreActions.push(changeType);
				}
				if(!this.getInvoice().isRawdoc() && this.getInvoice().isEmitida()){
					let sign = ACTION.SIGN_INVOICE;
					sign.permission = true;
					sign.backgrounColor = INVOICE.color;
					sign.fn = () => this.signInvoice();
					moreActions.push(sign);
					let face = ACTION.FACTURAE;
					face.permission = true;
					face.backgrounColor = INVOICE.color;
					face.fn = () => this.facturae();
					moreActions.push(face);
				}
				d.setMenuOptions(moreActions, top, left);
				d.open();
			});
		}

		if(this.getInvoice().isInbox() && this.getDur().isInvoiceManager()) {
			invoiceToolbar.addButton2(ACTION.RECORD, () => this.recordInvoice());
			invoiceToolbar.addButton2(ACTION.REJECT, () => this.rejectInvoice());
			invoiceToolbar.addSeparator();
		} 
		if((this.getInvoice().isOcrStatus(CONSTANT.APPROVED, CONSTANT.PENDING_CORRECTION) 
		   || this.getInvoice().isPending()) && this.getDur().isInvoiceManager()){
			invoiceToolbar.addButton2(ACTION.RECORD, () => this.recordInvoice());
		}
		
		if(this.getInvoice().isRejected()) {
			invoiceToolbar.addButton2(ACTION.DELETE, () => this.trashInvoice());
			invoiceToolbar.addButton2(ACTION.RESTORE, () => this.restoreInvoice());
		} else if(this.getInvoice().isDraft()) {
			invoiceToolbar.addButton2(ACTION.DELETE_FOREVER, () => this.removeInvoice());
			invoiceToolbar.addButton2(ACTION.RESTORE, () => this.restoreInvoice());
		} else if(this.getInvoice().isInbox()){
			invoiceToolbar.addButton2(ACTION.DELETE, () => this.trashInvoice());
			invoiceToolbar.addButton2(ACTION.ACCEPT, () => this.acceptInvoice());
			if(!this.autosave && this.getInvoice().isInbox()){
				invoiceToolbar.addButton2(ACTION.SAVE, () => this.save());
			}
		} else if (this.getInvoice().isPending()){
			invoiceToolbar.addButton2(ACTION.DELETE, () => this.trashPendingInvoice());
		} else if (this.getInvoice().isOcrStatus(CONSTANT.APPROVED, CONSTANT.PENDING_CORRECTION) ) {
			invoiceToolbar.addButton2(ACTION.ACCEPT, () => this.acceptInvoice());
			invoiceToolbar.addButton2(ACTION.REJECT, () => this.rejectInvoice());
			invoiceToolbar.addButton2(ACTION.DELETE, () => this.trashInvoice());
		} else if(this.getInvoice().isOcrStatus(CONSTANT.PENDING_DECISSION, CONSTANT.DISCARDED)) {
			invoiceToolbar.addButton2(ACTION.RESTORE, () => this.restoreInvoice());
			invoiceToolbar.addButton2(ACTION.DELETE, () => this.trashInvoice());
		} else if(this.getInvoice().isOcrStatus(CONSTANT.ERROR )) {
			invoiceToolbar.addButton2(ACTION.RESTORE, () => this.restoreInvoice());
			invoiceToolbar.addButton2(ACTION.DELETE_FOREVER, () => this.removeOcrInvoice());
		}

		invoiceToolbar.addButton2(ACTION.BACK, () => this.back());
		
		if(!this.getInvoice().file && !this.invoice.isEmitida()){
			invoiceToolbar.addButtonTitle(ACTION.ADD_FILE, () => this.addInvoiceFile());
		} else {
			invoiceToolbar.addButtonTitle(ACTION.SHOW_FILE, () => this.showFile());
		}

		if(this.getInvoice().isTbai()) {
			invoiceToolbar.addButtonTitle(ACTION.TICKETBAI, () => open(this.getInvoice().getTbaiUrl()));
		}
	}

	buildContent() {
		let div = this.createElement(TAG.DIV);
		div.id = this.DIV;
		div.className = CSS.AON_FLEX;
		this.appendChild(div);

		let data = this.getElement(this.DATA) || this.createElement(TAG.DIV);
		data.id = this.DATA;
		this.clearElement(data);
		data.style.width = this.fileOpened ? '50%' : '100%';
		data.className = CSS.AON_SUB_CONTENT;
		div.appendChild(data);

		this.buildTabs(data);

		this.buildInvoiceContent();
		this.buildFileContent();

		if(this.fileOpened) {
			this.showFile();
		}
	}

	buildTabs(data) {
		if(!this.invoice.isRawdoc() && this.invoice.isTbai()) {
			let tab = new AonTab();
			tab.id = this.TABS;
			tab.setOptions(this.options);
			data.appendChild(tab);
		}
	}

	buildInvoiceContent() {
		let data = this.getElement(this.DATA);
		let content = this.getElement(this.CONTENT);
		if(!content) {
			content = this.createElement(TAG.DIV);
			content.id = this.CONTENT;
			data.appendChild(content);
		}
		this.clearElement(content);
		this.buildCommentCard(content);

		this.buildMessagesCard(content);

		let general = this.createElement(TAG.DIV);
		general.id = this.GENERAL;
		general.className = this.fileOpened ? CSS.AON_BLOCK : CSS.AON_FLEX;
		content.appendChild(general);

		this.buildGeneralCard(general);
		this.buildTaxCard(general);

		this.buildDetailCard(content);
		this.buildFinanceCard(content);
		if ( LS.isNewTheme() ) { 
			this.showFieldsMessages(content);
		}
	}

	buildCommunication() {
		let content = this.getElement(this.CONTENT);
		this.clearElement(content);

		let communication = this.createElement(TAG.DIV);
		communication.id = this.COMMUNICATION;
		communication.className = this.fileOpened ? CSS.AON_BLOCK : CSS.AON_FLEX;
		content.appendChild(communication);
		this.buildTbaiCard(communication);
	}

	buildFileContent() {
		let div = this.getElement(this.DIV);
		let file = this.createElement(TAG.DIV);
		file.id  = this.FILE;
		file.className = CSS.AON_SUB_CONTENT;
		div.appendChild(file);
	}

	buildTbaiCard(parent) {
		let card = this.createAonElement(new AonCard(), this.TBAI_CARD, MSG.TICKETBAI);
		card.style.width = this.fileOpened ? '100%' : '50%';
		parent.appendChild(card);

		let table = this.getElement(this.DETAIL_TABLE);
		if(!table) {
			table = new AonBasicTable();
			table.id = this.DETAIL_TABLE;
			card.setContent(table);
		}
		table.removeRows();

		getTbaiHistory(this.invoice.id).then(requests => {
			for(let i = 0; i < requests.length; i++) {
				let history = requests[i];
				this.printTbaiHistory(table, history, i);
			}
		})

	}

	printTbaiHistory(table, history, i) {
		table.addRow(); // ----- ROW i
		
		let span = this.createElement(TAG.SPAN);
		span.innerHTML = history.date;
		table.addCell(span);

		let span2 = this.createElement(TAG.SPAN);
		span2.innerHTML = history.operation;
		table.addCell(span2);

		let icon = this.createElement(TAG.I);
		icon.className = "material-icons";
		icon.style.color =  history.ok ? "green" : "red";
        icon.innerHTML = history.ok ? MATERIAL_ICONS.CHECK_CIRCLE : MATERIAL_ICONS.ERROR;
		table.addCell(icon);

		let requestDownload = new AonIconButton();
		requestDownload.icon = MATERIAL_ICONS.FILE_DOWNLOAD;
		requestDownload.addEventListener(EVENT.CLICK, () => {
			open(history.requestUrl, '_blank');
		});
		table.addCell(requestDownload);

		let responseDownload = new AonIconButton();
		responseDownload.icon = MATERIAL_ICONS.FILE_DOWNLOAD;
		responseDownload.addEventListener(EVENT.CLICK, () => {
			open(history.responseUrl, '_blank');
		});
		table.addCell(responseDownload);
	}
	
	buildCommentCard(parent) {
		let commentsDiv = this.createElement(TAG.DIV);
		commentsDiv.id = this.COMMENTS;
		commentsDiv.className = CSS.AON_FLEX;
		parent.appendChild(commentsDiv);

		let hasComment = this.invoice.comments && this.invoice.comments != undefined && this.invoice.comments != '';
		let hasRemarks = this.invoice.remarks && this.invoice.remarks.length > 0;

		let remarksCard = new AonCard();
		remarksCard.id = this.REMARKS_CARD;
		remarksCard.title = MSG.REMARKS;
		if(hasComment && hasRemarks) 
			remarksCard.style.width = '50%';
		else remarksCard.style.width = '100%';

		if(!hasRemarks) remarksCard.className = CSS.AON_NONE;
		commentsDiv.appendChild(remarksCard);

		remarksCard.setContentHTML('');
		remarksCard.setBackground('#ffc');

		if(hasRemarks) {
			let ul = this.createElement(TAG.UL);
			ul.classList.add(CSS.AON_UL);
			ul.style.width = '100%';
			remarksCard.setContent(ul);
			this.invoice.remarks.forEach((item, i) => {
				if(item.reason) {
					let li = this.createElement(TAG.LI);
					li.style.backgrounColor = 'transparent !important';
					let strs1 = item.reason + '';
					strs1.split('\n').forEach(str => {
						let span = this.createElement(TAG.SPAN);
						span.innerHTML = str;
						li.appendChild(span);
						li.appendChild(this.createElement('br'));
					});
					ul.appendChild(li);
				}
			});
		}

		let commentsCard = new AonCard();
		commentsCard.id = this.COMMENT_CARD;
		commentsCard.title = MSG.COMMENT;
		if(hasComment && hasRemarks) 
			commentsCard.style.width = '50%';
		else commentsCard.style.width = '100%';
		if(!hasComment) commentsCard.className = CSS.AON_NONE;
		commentsDiv.appendChild(commentsCard);

		commentsCard.setContentHTML('');
		// commentsCard.setBackground('#ECC0EF');
		commentsCard.setBackground('#D3D8FF');
		
		if(hasComment) {
			let div = this.createElement(TAG.DIV);
			div.id = 'commentsLinesDiv';
			commentsCard.setContent(div);
			let strs = '';
			if(this.isString(this.invoice.comments)){
				strs = this.invoice.comments + '';
			} else {
				strs = JSON.stringify(this.invoice.comments);
			}
			strs.split('\n').forEach(str => {
				let span = this.createElement(TAG.SPAN);
				span.innerHTML = str;
				div.appendChild(span);
				div.appendChild(this.createElement('br'));
			});
		}
	}
	
	showFieldsMessages(parent){
		
		let getText = ( err ) => {
			switch ( err.code ) {
				case ErrCode.ERR_EMPTY_VALUE: 
					return "Sin valor"
				case ErrCode.ERR_LOW_CONFIDENCE: 
					return "Poca confianza"
				case ErrCode.ERR_INVALID_FORMAT: 
					return "Formato no válido"
				case ErrCode.ERR_INCORRECT_VALUE: 
					return "Valor incorrecto"
				default:
					return err.message;
			}
		};
		
		let getMessageHTML = ( err ) => {
			let span = this.createElement(TAG.SPAN)
			span.style.fontSize = "12px";
			span.innerHTML = getText(err);
			return span.outerHTML;
			
		};
		
		if(this.invoice.messages) {
			this.invoice.messages
			.filter( err => err.context )
			//.filter( err => !err.context.line )
			.forEach( (err, i ) => {
				try {
					switch ( err.context.key ){
						case ErrKey.DOMAIN: 
							break;
						case ErrKey.WORKPLACE: 
							break;
						case ErrKey.TYPE: 
							break;
						case ErrKey.BASES_QUOTAS: 
							break;
						case ErrKey.SERIES: 
							this.getElement(this.SERIE).addError(getMessageHTML(err));
							break;
						case ErrKey.DUPLICATED_SERIES_NUMBER: 
							this.getElement(this.SERIE).addError(getMessageHTML(err));
							break;
						case ErrKey.NUMBER: 
						case ErrKey.REFERENCE_CODE: 
						case ErrKey.DUPLICATED_REFERENCE_CODE: 
							this.getElement(this.REFERENCE).addError(getMessageHTML(err));
							break;
						case ErrKey.TRANSACTION: 
							break;
						case ErrKey.ISSUE_DATE: 
							this.getElement(this.DATE).addError(getMessageHTML(err));
							break;
						case ErrKey.TAX_DATE: 
							break;
						case ErrKey.TAX_RATE: 
							this.getElement(`${this.TAX_PERCENTAGE}${err.context.line}` ).addError(getMessageHTML(err));
							break;
						case ErrKey.TAX_BASE: 
							this.getElement(`${this.TAX_BASE}${err.context.line}`).addError(getMessageHTML(err));
							break;
						case ErrKey.TAX_QUOTA: 
							this.getElement(`${this.TAX_QUOTA}${err.context.line}`).addError(getMessageHTML(err));
							break;
						case ErrKey.IRPF_RATE: {
							let line = this.getElement(this.TAX_TABLE2).rows - 1;
							this.getElement(`${this.TAX_PERCENTAGE}${line}` ).addError(getMessageHTML(err));
							break;
						}
						case ErrKey.IRPF_BASE: { 
							let line = this.getElement(this.TAX_TABLE2).rows - 1;
							this.getElement(`${this.TAX_BASE}${line}` ).addError(getMessageHTML(err));
							break;
						}
						case ErrKey.IRPF_QUOTA: { 
							let line = this.getElement(this.TAX_TABLE2).rows - 1;
							this.getElement(`${this.TAX_QUOTA}${line}`).addError(getMessageHTML(err));
							break;
						}
						case ErrKey.SCOPE: 
							break;
						case ErrKey.REGISTRY: 
							break;
						case ErrKey.AMBIGUOUS_REGISTRY: 
							break;
						case ErrKey.RDOCUMENT:{
								let registry = this.getElement(this.REGISTRY); 
								registry.getElement(registry.DOCUMENT).addError(getMessageHTML(err));
							}
							break;
						case ErrKey.RDOCUMENT_COUNTRY: 
							break;
						case ErrKey.RNAME: { 
								let registry = this.getElement(this.REGISTRY); 
								registry.getElement(registry.NAME).addError(getMessageHTML(err));
							}
							break;
						case ErrKey.ADDRESS: { 
								let registry = this.getElement(this.REGISTRY); 
								registry.getElement(registry.ADDRESS).addError(getMessageHTML(err));
							}
							break;
						case ErrKey.DETAIL_DESCRIPTION: 
							break;
						case ErrKey.DETAILS: 
							break;
						case ErrKey.ACCOUNT_ENTRY: 
							break;
						case ErrKey.FINANCE_AMOUNT_ZERO: 
							break;
						case ErrKey.FINANCE_WRONG_DUE_DATE: 
							break;
						case ErrKey.FINANCE_WRONG_ACCOUNT_BANK: 
							break;
						case ErrKey.TOTAL: 
							this.getElement(this.TOTAL).addError(getMessageHTML(err));
							break;
						case ErrKey.PAY_METHOD: 
							let line = err.context.line || 0; 
							this.getElement(`${this.FINANCE_PAYMETHOD}${line}`).addError(getMessageHTML(err));
							break;
						default:
							break;
					}
				} catch ( e ) {
					//console.error(e);
				}
			});
		}
	}

	buildMessagesCard(parent) {
		let messagesDiv = this.createElement(TAG.DIV);
		messagesDiv.id = this.MESSAGES;
		messagesDiv.className = CSS.AON_FLEX;
		parent.appendChild(messagesDiv);

		let hasErrors = this.invoice.messages && this.invoice.messages.length > 0;
		if ( !hasErrors  ){
			return;
		}

		let errorsCard = new AonCard();
		errorsCard.id = this.ERRORS_CARD;
		//errorsCard.title = MSG.ERRORS;
		errorsCard.style.width = '100%';
		
		messagesDiv.appendChild(errorsCard);
		

		errorsCard.setContentHTML('');
		errorsCard.setBackground('#ffc');

		let ul = this.createElement(TAG.UL);
		ul.classList.add(CSS.AON_UL);
		ul.style.width = '100%';
		errorsCard.setContent(ul);
		
		let createErrorDiv = (description, className) => {
			
			let errorDiv = this.createElement(TAG.DIV);
			errorDiv.className = className ;
			errorDiv.className += " " + CSS.AON_FLEX ;
			errorDiv.className += " " + CSS.FLEX_ALIGN_CENTER;
			
			let iconSpan = this.createElement(TAG.SPAN);
			iconSpan.className = CSS.MATERIAL_ICONS;
			iconSpan.className += " " + CSS.AON_INPUT_MSG_ERROR;
			iconSpan.innerHTML = MATERIAL_ICONS.WARNING;
			iconSpan.style.color = className === CSS.AON_INVOICE_ERROR ? "#e83151": "#e3a733" ; 
			errorDiv.appendChild(iconSpan);

			let spaceSpan = this.createElement(TAG.SPAN);
			spaceSpan.style.width = "16px";
			errorDiv.appendChild(spaceSpan);
			
			let descriptionSpan = this.createElement(TAG.SPAN);
			descriptionSpan.innerHTML = description;
			errorDiv.appendChild(descriptionSpan);
			
			return errorDiv;			
		};
		
		let createViewDiv = (viewMessage, hideMessage, errors) => {
			let viewDiv = this.createElement(TAG.DIV);
			
			let viewButtonSpan = this.createElement(TAG.SPAN);
			viewButtonSpan.innerHTML = viewMessage;
			let hideButtonSpan = this.createElement(TAG.SPAN);
			hideButtonSpan.innerHTML = hideMessage;
			
			let errorsDiv = this.createElement(TAG.DIV);
			let errorsUl = this.createElement(TAG.UL);
			errors.forEach( (error, i ) => {
				let errorLi = this.createElement(TAG.LI);
				let errorDiv = this.createElement(TAG.DIV);
				let errorSpan = this.createElement(TAG.SPAN);
				errorSpan.innerHTML = error.message;
				errorDiv.appendChild(errorSpan);	
				errorLi.appendChild(errorDiv);	
				errorsUl.appendChild(errorLi);
				
				errorDiv.style.paddingTop = "12px";	
				errorDiv.style.paddingLeft = "3px";	
			});

			errorsDiv.appendChild(errorsUl);
			
			viewDiv.appendChild(viewButtonSpan);
			viewDiv.appendChild(hideButtonSpan);
			viewDiv.appendChild(errorsDiv);
			
			
			errorsDiv.style.display = "none";
			hideButtonSpan.style.display = "none";
			
			viewButtonSpan.style.cursor = "pointer";
			viewButtonSpan.onclick = function(){
				viewButtonSpan.style.display = "none";
				errorsDiv.style.removeProperty("display");
				hideButtonSpan.style.removeProperty("display");
			};
			hideButtonSpan.style.cursor = "pointer";
			hideButtonSpan.onclick = function(){
				errorsDiv.style.display = "none";
				hideButtonSpan.style.display = "none";
				viewButtonSpan.style.removeProperty("display");
			};
			
			// customize			
			viewDiv.style.color = "#3d4045";
			viewButtonSpan.style.fontWeight = "600";
			hideButtonSpan.style.fontWeight = "600";
			
			return viewDiv; 
		}
		
		let createErrorsDiv = ( titleMessage, viewMessage, hideMessage,  className, errors ) => {
			
			if ( errors.length > 1 ){
				let li = this.createElement(TAG.LI);
				li.style.backgrounColor = 'transparent !important';
				let errorDiv = createErrorDiv(errors.length + " " +  titleMessage, className );
				let viewDiv = createViewDiv(viewMessage, hideMessage, errors);
				li.appendChild(errorDiv);
				li.appendChild(viewDiv);
				ul.appendChild(li);

				errorDiv.style.paddingLeft = "3px";	
				viewDiv.style.paddingLeft = '48px';
				viewDiv.style.paddingBottom = "12px";	

			} else if ( errors.length == 1 ) {
				errors.forEach((error, i) => {
					let li = this.createElement(TAG.LI);
					li.style.backgrounColor = 'transparent !important';
					let errorDiv  = createErrorDiv(error.message, className );
					li.appendChild(errorDiv);
					ul.appendChild(li);

					errorDiv.style.paddingLeft = "3px";	
					errorDiv.style.paddingBottom = "12px";	

				});
			}

		}
		
		let customizeErrorsCards = (errorsCard, errors) =>  {
			// customize card 
			let errorColor = "#e83151";
			let mainCard = errorsCard.getCard();
			mainCard.style.padding = "0px"; 
			mainCard.style.boxShadow = "none";
			mainCard.style.backgroundColor = "white";
			mainCard.style.borderColor = errorColor;
			let titleCard = errorsCard.getCardTitle();
			titleCard.style.paddingTop = "15px"; 
			titleCard.style.paddingLeft = "22px"; 
			titleCard.style.paddingRight = "22px"; 
			titleCard.style.marginBottom = "0px"; 
			titleCard.style.paddingBottom = "15px"; 
			titleCard.style.color = "white";
			titleCard.style.backgroundColor = errorColor;
			
			let expandSpan = this.createElement(TAG.SPAN);
			expandSpan.style.cursor = "pointer";
			expandSpan.className = CSS.MATERIAL_ICONS;
			expandSpan.innerHTML = MATERIAL_ICONS.ARROW_DROP_DOWN;
			expandSpan.onclick = function() {};
			let collapseSpan = this.createElement(TAG.SPAN);
			collapseSpan.style.cursor = "pointer";
			collapseSpan.className = CSS.MATERIAL_ICONS;
			collapseSpan.innerHTML = MATERIAL_ICONS.ARROW_DROP_UP;
			
			let iconSpan = this.createElement(TAG.SPAN);
			iconSpan.className = CSS.MATERIAL_ICONS;
			iconSpan.innerHTML = MATERIAL_ICONS.WARNING;
			let messageSpan = this.createElement(TAG.SPAN);
			messageSpan.innerHTML = errors + " " + ( errors > 1 ? MSG.ERRORS.toLowerCase() : MSG.ERRORS.substring(0,MSG.ERRORS.length-2).toLowerCase() );
			messageSpan.style.paddingLeft = "8px";
			
			let titleCard1 = errorsCard.getCardTitle1();
			titleCard1.appendChild(expandSpan);
			
			titleCard1.appendChild(collapseSpan);
			titleCard1.appendChild(iconSpan);
			titleCard1.appendChild(messageSpan);
			
			
			let contentCard = errorsCard.getContent();
			contentCard.style.paddingTop = "15px";
			contentCard.style.paddingLeft = "22px"; 
			contentCard.style.paddingRight = "22px"; 
			contentCard.style.paddingBottom = "15px";

			contentCard.style.display = "none"; 
			collapseSpan.onclick = function() { 
				contentCard.style.display = "none";
				collapseSpan.style.display = "none"; 
				expandSpan.style.removeProperty("display"); 
			};
			expandSpan.onclick = function() { 
				contentCard.style.display = "block";
				expandSpan.style.display = "none"; 
				collapseSpan.style.removeProperty("display"); 
			};
			collapseSpan.onclick();
			
		}
		
		let emptyValueErrors =
		this.invoice.messages.filter( err => err.code === ErrCode.ERR_EMPTY_VALUE );
		createErrorsDiv( MSG.ERR_EMPTY_VALUE, MSG.VIEW_FIELDS, MSG.HIDE_FIELDS, CSS.AON_INVOICE_ERROR , emptyValueErrors )
		
		let lowConfidenceErrors =
		this.invoice.messages.filter( err => err.code === ErrCode.ERR_LOW_CONFIDENCE );
		createErrorsDiv( MSG.ERR_LOW_CONFIDENCE, MSG.VIEW_FIELDS, MSG.HIDE_FIELDS, CSS.AON_INVOICE_WARNING , lowConfidenceErrors )
		
		let otherErrors =
		this.invoice.messages.filter( err => !emptyValueErrors.includes(err)  && !lowConfidenceErrors.includes(err) );
		otherErrors.forEach((item, i) => {
			let li = this.createElement(TAG.LI);
			li.style.backgrounColor = 'transparent !important';

			let errorDiv  = createErrorDiv(item.message, CSS.AON_INVOICE_ERROR );
			li.appendChild(errorDiv);
			ul.appendChild(li);
		});
		
		let errorsCount = this.invoice.messages.length;
		
		customizeErrorsCards(errorsCard, errorsCount );
	}

	onChangeInvoiceTotal(value) {
		this.invoice.setTotal(value);
		// this.setFocus(this.TOTAL);
		this.reload();
		if(this.autosave) this.save();
	}

	buildGeneralCard(parent) {
		let dn = "";
		if (!this.isInvofoxInvoice() && !this.invoice.isRawdoc() ) {
			dn = MSG.INVOICE_DATA + " (" + getDocumentNumber(this.invoice) + ")";
		} else {
			dn = MSG.DOCUMENT_DATA;
		}
		let card = this.createAonElement(new AonCard(), this.GENERAL_CARD, dn);
		card.style.width = '50%';
		parent.appendChild(card);

		let dialog = new AonDialog();
		dialog.id = this.DIALOG_BLANK;
		dialog.type = CONSTANT.BLANK;
		this.appendChild(dialog);
		card.addTitleButton(MSG.OPTIONS, MATERIAL_ICONS.MORE_VERT, false, () => {
			let div = this.createElement(TAG.DIV);
			div.style.margin = '15px';
			let button  = this.getElement(card.TITLE_SECTION2 + MSG.OPTIONS + 'Button');

			// ----- SERVICE

			let service = new AonSwitch();
			service.id = this.SERVICE;
			service.title = MSG.SERVICE;
			service.readonly = this.invoice.isReadonly();
			service.checked = this.invoice.isService();
			div.appendChild(service);
			service.addEventListener(EVENT.CHANGE, () => {
				this.invoice.setService(service.checked);
			});


			// ----- BIENES INVERSION

			let investment = new AonSwitch();
			investment.id = this.INVESTMENT;
			investment.title = MSG.INVESTMENT;
			investment.readonly = this.invoice.isReadonly();
			investment.checked = this.invoice.isInvestment();
			div.appendChild(investment);
			investment.addEventListener(EVENT.CHANGE, () => {
				this.invoice.setInvestment(investment.checked);
			});


			// ----- RECTIFICATIVA

			let rectified = new AonSwitch();
			rectified.id = this.RECTIFIED;
			rectified.title = MSG.RECTIFIED;
			rectified.readonly = this.invoice.isReadonly();
			div.appendChild(rectified);
			rectified.addEventListener(EVENT.CHANGE, () => {
				this.invoice.setRectified(rectified.checked);
			});
			rectified.checked = this.invoice.isRectified();

			const top  = button.getBoundingClientRect().top;
			const left = button.getBoundingClientRect().left;
			dialog.setContent(div, top, left);
			dialog.open();

			service.setWidth('150px');
			service.setMarginBottom('10px');

			investment.setWidth('150px');
			investment.setMarginBottom('10px');

			rectified.setWidth('150px');
			rectified.setMarginBottom('10px');
		});

		let table = new AonBasicTable();
		table.id = this.GENERAL_CARD_TABLE;
		card.setContent(table);

		table.addRow(); // ----- ROW 1
		
		let div = this.createDiv();
		div.className = CSS.AON_FLEX;
		table.addCell(div, '4');
		
		if(this.invoice.isEmitida()) {
			// ----- SERIE
			let serieSpan = this.createTableSpan("20%", "2px");
			div.appendChild(serieSpan);

			let serie = this.createAonElement(LS.isNewTheme() ? new AonNewSuggestion() : new AonSuggestion(), this.SERIE, MSG.SERIE);
			serieSpan.appendChild(serie);
			serie.setMaxlength(5);
			serie.addEventListener(EVENT.AON_KEYUP, (e) => {
				serie.buildOptions(this.series.filter(f => f.description && f.description.includes(serie.value)).map(r => {return {
					name: r.description,
					value: r.description,
					item: r};}));
			});
			serie.readonly = this.invoice.isReadonly();
			serie.value = this.invoice.serie;
			serie.addEventListener(EVENT.CHANGE, () => this.onChangeSerie(serie.value));
			serie.addEventListener(EVENT.SELECT, () => this.onChangeSerie(serie.value));

			// ----- NUMBER

			let numberSpan = this.createTableSpan("30%", "2px");
			div.appendChild(numberSpan);

			let number = LS.isNewTheme() ? new AonNewInput() : new AonInput();
			number.id = this.NUMBER;
			number.description = MSG.NUMBER;
			number.title = MSG.NUMBER;
			if(this.invoice.number > -1)
				number.value = this.invoice.number;
			number.readonly = CONSTANT.READONLY;
			number.disabled = CONSTANT.TRUE;
			numberSpan.appendChild(number);
			if(this.invoice.isInbox()) {
				getSalesSeries({}).then(r => {
					this.series = r;
					let enabled = this.invoice.isInbox() && r.filter(f => f.description == this.invoice.serie).length === 0;
					number.readonly = !enabled;
					number.disabled = !enabled;					
				});

				number.addEventListener(EVENT.CHANGE, () => {
					this.invoice.number = number.value;
					if(this.autosave) this.save();
				});
			}
		} else {
			// ----- REFERENCE
			let referenceSpan = this.createTableSpan("45%", "2px");
			div.appendChild(referenceSpan);

			let reference = LS.isNewTheme() ? new AonNewInput() : new AonInput();
			reference.id = this.REFERENCE;
			reference.description = MSG.REFERENCE;
			reference.title = MSG.REFERENCE;
			reference.value = this.invoice.reference;
			reference.readonly = this.invoice.isReadonly();
			reference.addEventListener(EVENT.CHANGE, () => {
				this.invoice.setReference(reference.value);
				if(this.autosave) this.save();
			});
			referenceSpan.appendChild(reference);
		}

		// ----- DATE

		let dateSpan = this.createTableSpan("30%", "2px");
		div.appendChild(dateSpan);

		let date = LS.isNewTheme() ? new AonNewDate() : new AonDate();
		date.id = this.DATE;
		date.title = MSG.DATE;
		if(this.invoice.isReadonly())
			date.readonly = this.invoice.isReadonly();
		date.addEventListener(EVENT.CHANGE, () => {
			this.invoice.setDate(date.value);
			if(this.autosave) this.save();
		});

		date.value = this.invoice.date;
		dateSpan.appendChild(date);
		date.value = this.invoice.date;
	
		// ----- TOTAL

		let totalSpan = this.createTableSpan("25%", "0px");
		div.appendChild(totalSpan);

		let total = this.createAonNumber(this.TOTAL, MSG.TOTAL, this.invoice.total);
		total.value;
		total.onChange(() => this.onChangeInvoiceTotal(total.value));
		total.readonly = this.invoice.isReadonly()
			|| this.invoice.taxes.filter(f => TaxType.IVA === f.tax).length > 1
			|| this.invoice.details.length > 0;
		if(LS.isNewTheme() && (this.invoice.taxes.filter(f => TaxType.IVA === f.tax).length > 1 || this.invoice.details.length > 0))
			total.disabled = CONSTANT.TRUE;
		totalSpan.appendChild(total);
		if(!LS.isNewTheme() && (this.invoice.taxes.filter(f => TaxType.IVA === f.tax).length > 1 || this.invoice.details.length > 0))
			total.disabled = CONSTANT.TRUE;
		// ***** OLD THEME
		total.readonly = this.invoice.isReadonly()
		|| this.invoice.taxes.filter(f => TaxType.IVA === f.tax).length > 1
		|| this.invoice.details.length > 0;
		// *****

		table.addRow(); // ----- ROW 2

		// ----- REGISTRY
		if(this.invoice.isEmitida()) {
			let customer = new AonCustomerSuggestion();	
			customer.id = this.REGISTRY;
			customer.showAddress = true;
			customer.readonly = this.invoice.isReadonly();
			customer.setCustomer(this.invoice.getRegistry());
			customer.addEventListener(EVENT.SELECT_REGISTRY, () => this.onChangeRegistry(customer.getCustomer()));
			customer.addEventListener(EVENT.CUSTOMER_CHANGE, () => {
				this.invoice.receiver.address = customer.getCustomer().address;
			});
			table.addCell(customer, '4');	
		} else {
			let registry = new AonRegistrySuggestion();
			registry.id = this.REGISTRY;
			registry.showAddress = true;
			registry.types = this.invoice.getRegistryType();
			// registry.value = this.invoice.getRegistry();
			registry.setRegistry(this.invoice.getRegistry());
			registry.addEventListener(EVENT.SELECT_REGISTRY, () => this.onChangeRegistry(registry.getRegistry()));
			registry.addEventListener(EVENT.CUSTOMER_CHANGE, () => {
				this.invoice.receiver.address = registry.getRegistry().address;
			});
			table.addCell(registry, '6');
			
			// registry.readonly = this.invoice.isReadonly();
			// registry.addEventListener(EVENT.CHANGE, () => {
			// 	this.invoice.setRegistry(registry.getRegistry());
			// 	if(this.autosave) this.save();
			// });
			// registry.addEventListener(EVENT.SELECT_REGISTRY, () => {
			// 	this.invoice.setRegistry(registry.getRegistry());
			// 	getRegistryPaymethod({registry: registry.getRegistry().id}).then(rpm => {
			// 		this.invoice.setPaymethod(rpm.paymethod.id);
			// 		getPaymethod(rpm.paymethod.id).then(pm => {
			// 			if(pm.type === 'NEGOTIABLE_DOCUMENT') {
			// 				getRegistryPaymethod({registry: this.company.id}).then(crpm => {
			// 					this.invoice.setBankAccount(crpm.bank.bank_account);
			// 					this.reload();
			// 				});
			// 				// getRegistryBanks(this.company.id).then(r => {
			// 				// 	this.invoice.setBankAccount(r[0] ? r[0].bank_account : "");
			// 				// 	this.reload();
			// 				// });	
			// 			} else if(pm.type === 'BANK_TRANSFER'){
			// 				this.invoice.setBankAccount(rpm.bank.bank_account);
			// 				this.reload();
			// 			} else {
			// 				this.invoice.setBankAccount("");
			// 				this.reload();
			// 			}
			// 		});
			// 	});
			// 	if(this.autosave) this.save();
			// });
			// table.addCell(registry, this.invoice.isEmitida() ? '4' : '6');	
		}

		table.addRow(); // ----- ROW 3

		// ----- CATEGORY
		if(this.getDur().hasAccounting() || this.getDur().hasParentAccounting()) {
			let category = LS.isNewTheme() ? new AonNewSelect() : new AonSelect();
			category.id = this.CATEGORY;
			category.title = MSG.CATEGORY;
			category.autocomplete = true;
			category.readonly = this.invoice.isReadonly();
			category.addEventListener(EVENT.SELECT, () => {
				this.invoice.setCategory(category.value);
				if(this.autosave) this.save();
			});
			table.addCell(category, this.invoice.isEmitida() ? '4' : '6');
			getInvoiceAccounts({type: this.invoice.getInvoiceType()}).then(accounts => {
				let accs = accounts.map(acc => {return {name: acc.name, value: acc.code};});
				category.options = JSON.stringify(accs);
				category.value = this.invoice.getCategory();
			});
		}

		getWorkplaces().then(r => {
			if(!this.invoice.workplace && r.length > 0) {
				this.invoice.setWorkplace(r[0].id);
			}
			if(r.length > 1) {
				table.addRow();
				let workplaces = r.map(w => {return {name: w.description, value: w.id};});
				let workplace =  LS.isNewTheme() ? new AonNewSelect() : new AonSelect();
				workplace.id = this.WORKPLACE;
				workplace.title = MSG.WORKPLACE;
				workplace.autocomplete = true;
				workplace.readonly = this.invoice.isReadonly();
				workplace.options = JSON.stringify(workplaces);
				workplace.value = this.invoice.getWorkplace();
				workplace.addEventListener(EVENT.SELECT, () => {
					this.invoice.setWorkplace(workplace.value);
					if(this.autosave) this.save();
				});
				table.addCell(workplace, this.invoice.isEmitida() ? '4' : '6');
			} else if(r.length === 1) this.invoice.setWorkplace(r[0].id);
		}).catch(e => {
			console.error(e);
		});
	}

	onChangeSerie(value) {	
		this.invoice.setSerie(value);
		if(this.invoice.isInbox()) {
			let enabled = this.invoice.isInbox() && this.series && this.series.filter(f => f.description == this.invoice.serie).length === 0;
			this.getElement(this.NUMBER).readonly = !enabled;
			this.getElement(this.NUMBER).disabled = !enabled;
			if(!enabled) {
				this.invoice.number = '';
				this.getElement(this.NUMBER).value = '';
			} else {
				this.invoice.number = '1';
				this.getElement(this.NUMBER).value = '1';
			}
		}
		if(this.autosave) this.save();
	}

	buildTaxCard(parent) {
		let card = new AonCard();
		card.id = this.TAX;
		card.title = MSG.TAXES_DETAIL;
		card.style.width = '50%';
		parent.appendChild(card);

		let dialog = this.getElement(this.DIALOG_BLANK);
		card.addTitleButton(MSG.OPTIONS, MATERIAL_ICONS.MORE_VERT, false, () => {
			let div = this.createElement(TAG.DIV);
			div.style.margin = '15px';
			let button  = this.getElement(card.TITLE_SECTION2 + MSG.OPTIONS + 'Button');

			// ----- VAT ACCRUAL PAYMENT - CRITERIO DE CAJA

			let accrual = new AonSwitch();
			accrual.id = this.VAT_ACCRUAL_PAYMENT;
			accrual.title = MSG.VAT_ACCRUAL_PAYMENT;
			accrual.readonly = this.invoice.isReadonly();
			accrual.checked = this.invoice.isVatAccrualPayment();
			div.appendChild(accrual);
			accrual.addEventListener(EVENT.CHANGE, () => {
				this.invoice.setVatAccrualPayment(accrual.checked);
			});

			// ----- SURCHARGE - RECARGO DE EQUIVALENCIA

			let surcharge = new AonSwitch();
			surcharge.id = this.SURCHARGE;
			surcharge.title = MSG.SURCHARGE_RE;
			surcharge.readonly = this.invoice.isReadonly();
			surcharge.addEventListener(EVENT.CHANGE, () => {
				this.invoice.setSurcharge(surcharge.checked);
				this.reload();
				if(this.autosave) this.save();
			});
			div.appendChild(surcharge);
			surcharge.checked = this.invoice.isSurcharge();

			// ----- REGIMEN ESPECIAL AGRARIO

			let farmer = new AonSwitch();
			farmer.id = this.WITHHOLDING_FARMER;
			farmer.title = MSG.WITHHOLDING_FARMER;
			farmer.readonly = this.invoice.isReadonly();
			farmer.addEventListener(EVENT.CHANGE, () => {
				this.invoice.setWithholdingFarmer(farmer.checked);
				this.reload();
				if(this.autosave) this.save();
			});
			div.appendChild(farmer);
			if(this.invoice.isEmitida() && !this.invoice.isNacional() ) {
				if(this.invoice.isWithholdingFarmer()){
					this.invoice.setWithholdingFarmer(false);
				}
				farmer.setDisabled(true);
			}
			farmer.checked = this.invoice.isWithholdingFarmer();
	
			const top  = button.getBoundingClientRect().top;
			const left = button.getBoundingClientRect().left;
			dialog.setContent(div, top, left, '225px');
			dialog.open();

			farmer.setWidth('200px');
			farmer.setMarginBottom('10px');

			surcharge.setWidth('200px');
			surcharge.setMarginBottom('10px');

			accrual.setWidth('200px');
			accrual.setMarginBottom('10px');
		});

		this.buildTaxCardContent(card);	
	}

	buildTaxCardContent(card) {
		if(!card) card = this.getElement(this.TAX);
		let table = this.getElement(this.TAX_TABLE);
		if(!table){
			table = new AonBasicTable();
			table.id = this.TAX_TABLE;
			card.setContent(table);
		}
		table.removeRows();

		table.addRow(); // ----- ROW 1

		let div = this.createDiv();
		div.className = CSS.AON_FLEX;
		table.addCell(div, '4');

		// ----- TRANSACTION TYPE
		let transactionSpan = this.createTableSpan("50%", "2px");
		div.appendChild(transactionSpan);

		let transaction =  LS.isNewTheme() ? new AonNewSelect() : new AonSelect();
		transaction.id = this.TRANSACTION_TYPE;
		transaction.title = MSG.TRANSACTION_TYPE;
		transaction.options = JSON.stringify(Transactions);
		transaction.value = this.invoice.transaction;
		transaction.readonly = this.invoice.isReadonly();
		transaction.addEventListener(EVENT.SELECT, () => {
			this.invoice.setTransaction(transaction.value);
			this.setFocus(transaction.id);
			this.reload();
			if(this.autosave) this.save();
		});
		transactionSpan.appendChild(transaction);

		// ----- ACTIVITY TYPE
		let activitySpan = this.createTableSpan("50%", "0px");
		div.appendChild(activitySpan);

		let activity =  LS.isNewTheme() ? new AonNewSelect() : new AonSelect();
		activity.id = this.ACTIVITY;
		activity.title = MSG.ACTIVITY;
		activity.readonly = this.invoice.isReadonly();
		activity.setAlias("id", "description");
		activity.addEventListener(EVENT.SELECT, () => {
			this.invoice.setActivity(activity.getValueObject());
			this.setFocus(activity.id);
			this.reload();
			if(this.autosave) this.save();
		});
		activitySpan.appendChild(activity);
		getCompanyActivities({}).then(activities => {
			if(activities.length > 0) {
				this.invoice.setActivity(this.invoice.getActivity() || activities[0]);
				activity.setOptions(activities);
				activity.value = this.invoice.getActivity().id;
			}
		});

		// ----- TAXES

		let taxesTable = this.getElement(this.TAX_TABLE2);
		if(!taxesTable) {
			taxesTable = new AonBasicTable();
			taxesTable.id = this.TAX_TABLE2;
			card.addContent(taxesTable);
		}
		taxesTable.removeRows();

		// if(this.invoice.isCcm()) {
			// this.invoice.taxes = this.invoice.taxes.filter(f => TaxType.IRPF === f.tax);
		// }

		if(this.invoice.isVatEnabled()){
			for(let i = 0; i < this.invoice.taxes.length; i++) {
				let tax = this.invoice.taxes[i];
				if(TaxType.IVA === tax.tax)
					this.printTax(taxesTable, tax, i);
			}
		}
		if(this.invoice.isNacional() || this.invoice.isCcm()) {
			for(let i = 0; i < this.invoice.taxes.length; i++) {
				let tax = this.invoice.taxes[i];
				if(TaxType.IRPF === tax.tax) {
					this.invoice.withholding = true;
					this.printTax(taxesTable, tax, i);
				}
			}
		}


		// let div = this.getElement(this.TAX_DIV);
		// if(!div) {
		// 	div = this.createElement(TAG.DIV);
		// 	div.id = this.TAX_DIV;
		// 	card.addContent(div);
		// }
		// this.clearElement(div);

		// ----- WITHHOLDING
		let irpfTableId = 'irpfTable';
		let irpfTable = this.getElement(irpfTableId);
		if(!irpfTable){
			irpfTable = new AonBasicTable();
			irpfTable.id = 'irpfTable';
			card.addContent(irpfTable);
		}
		irpfTable.removeRows();
		irpfTable.addRow();

		if(!this.invoice.isReadonly() && this.invoice.details.length === 0) {
			// ----- ADD TAX

			let addButton = new AonIconButton();
			addButton.id = this.TAX_ADD;
			addButton.title = MSG.ADD_TAX;
			addButton.icon = MATERIAL_ICONS.ADD;

			addButton.addEventListener('click', () => {
				if(this.invoice.isVatEnabled()) {
					this.setFocus(this.TAX_TYPE + this.invoice.taxes.length);
					this.invoice.addTax();
					this.reload();
					if(this.autosave) this.save();
				}
			});
			irpfTable.addCell(addButton);
			addButton.setDisabled(!this.invoice.isVatEnabled());
		}

		let irpf = new AonSwitch();
		irpf.id = this.WITHHOLDING;
		irpf.title = MSG.IRPF; // MSG.WITHHOLDING;

		irpf.readonly = this.invoice.isReadonly() || this.invoice.details.length > 0;
		irpf.addEventListener(EVENT.CHANGE, () => {
			this.invoice.setWithholding(irpf.checked, this.configuration ? this.configuration.withholdingPercent : undefined);
			this.reload();
			if(this.autosave) this.save();
		});
		irpfTable.addCell(irpf, '1');
		if(!this.invoice.isNacional() && !this.invoice.isCcm()) {
			irpf.setDisabled(true);
		}
		if(this.invoice.isReadonly()) irpf.setDisabled(true);
		irpf.checked = this.invoice.taxes.filter(r => TaxType.IRPF === r.type || TaxType.IRPF === r.tax).length > 0;

		let irpfType =  LS.isNewTheme() ? new AonNewSelect() : new AonSelect();
		irpfType.id = 'irpfwithholdingTYpe';
		irpfType.title = 'Tipo IRPF';
		irpfType.setAlias('id', 'name');
		if(this.invoice.taxes.filter(r => TaxType.IRPF === r.type || TaxType.IRPF === r.tax).length === 0) {
			irpfType.disabled = 'true';
		}
		irpfType.setOptions(WithholdingType);

		irpfType.addEventListener(EVENT.SELECT, () => {
			let detail = WithholdingType.find(v => v.id == irpfType.value);
			this.invoice.setWithholdingType(detail);
			this.reload();
			if(this.autosave) this.save();
		});
		irpfTable.addCell(irpfType, '3');
		irpfType.readonly = this.invoice.isReadonly();
		if(this.invoice.taxes.filter(r => TaxType.IRPF === r.type || TaxType.IRPF === r.tax).length > 0) {
			let val = this.invoice.taxes.filter(r => TaxType.IRPF === r.type || TaxType.IRPF === r.tax)[0].withholding_type;
			irpfType.value = val || (this.configuration ? this.configuration.withholdingPercent : undefined);
		} 
	}

	async onChangeRegistry(registry) { 
		this.invoice.setRegistry(registry);
		if(registry.paymethod) {
			this.invoice.setPaymethod(registry.paymethod.paymethod.id);
			this.invoice.finances.forEach((finance, i) => {
				let pm = registry.paymethod.paymethod;
				if((!this.invoice.isEmitida() && pm.type === 'NEGOTIABLE_DOCUMENT')
						|| (this.invoice.isEmitida() && pm.type === 'BANK_TRANSFER')) {
					getRegistryPaymethod({registry: this.company.id}).then(crpm => {
						let ba = this.getElement(this.FINANCE_BANK_ACCOUNT + i);
						if(ba) {
							ba.value = crpm.bank.bank_account;
							finance.bank_account = ba.value;
							this.invoice.setFinance(finance, i);
						}	
					});
					// getRegistryBanks(this.company.id).then(r => {
				   	// 	let ba = this.getElement(this.FINANCE_BANK_ACCOUNT + i);
				   	// 	ba.value = r[0] ? r[0].bank_account : "";
				   	// 	finance.bank_account = ba.value;
				   	// 	this.invoice.setFinance(finance, i);
					// });	
			   	} else if((this.invoice.isEmitida() && pm.type === 'NEGOTIABLE_DOCUMENT')
				 		|| (!this.invoice.isEmitida() && pm.type === 'BANK_TRANSFER')){
				   	getRegistryBanks(this.invoice.getRegistry().id).then(r => {
						let ba = this.getElement(this.FINANCE_BANK_ACCOUNT + i);
						if(ba) {
							ba.value = r[0] ? r[0].bank_account : "";
							finance.bank_account = ba.value;
							this.invoice.setFinance(finance, i); 
						}
				   	});
				} else { 
					finance.bank_account = "";
					this.invoice.setFinance(finance, i);
				}
			});				
		}
		if(registry.transaction) this.invoice.setTransaction(registry.transaction);
		else {
			let transactionJSON = await getSupplierTransaction({id: registry.id})
			if(!transactionJSON.transaction)
				transactionJSON = await getCreditorTransaction({id: registry.id})
			if(transactionJSON.transaction)
				this.invoice.setTransaction(transactionJSON.transaction);
		}
		if(registry.withholding) this.invoice.setWithholding(registry.withholding);
		if(registry.surcharge) this.invoice.setSurcharge(registry.surcharge);

		let data = {
			registry: this.invoice.getRegistry().id,
			type: this.invoice.type
		};

		if(this.getDur().hasAccounting() || this.getDur().hasParentAccounting()){
			getRegistrySuggestedAccount(data).then(r => {
				this.invoice.setCategory(r.code);
				this.getElement(this.CATEGORY).value = r.code;
			});
		}

		this.getElement(this.TOTAL).value = this.invoice.getTotal();
		this.buildTaxCardContent();
		this.buildDetailCard();
		this.buildFinanceCard();
		if(this.autosave) this.save();

	}

	onChangeTaxBase(tax, value, i) {
		tax.base = value;
		this.invoice.setTax(tax, i);
		this.reload();
		if(this.autosave) this.save();
	}

	printTax(taxesTable, tax, i) {
		taxesTable.addRow(); // ----- ROW i

		// ----- TAX PERCENT

		tax.type = tax.type || tax.tax;
		let percentage = LS.isNewTheme() ? new AonNewSelect() : new AonSelect();
		percentage.id = this.TAX_PERCENTAGE + i;
		percentage.title = '% ' + getTaxTypeName(tax.type, this.isMobile());
		percentage.options = JSON.stringify(getTaxPercentageOption(tax.type));
		percentage.addEventListener(EVENT.SELECT, () => {
			tax.percentage = percentage.value;
			tax.type = getTaxType(tax.percentage);
			this.invoice.setTax(tax, i);
			this.reload();
			if(this.autosave) this.save();
		});
		taxesTable.addCell(percentage);
		percentage.readonly = this.invoice.isReadonly() 
			|| this.invoice.details.length > 0
			|| tax.type.includes('IRPF');

		if(this.invoice.details.length > 0 || tax.type.includes('IRPF'))
			percentage.disabled = CONSTANT.TRUE;
		
		percentage.value = tax.percentage;

		// ----- TAX BASE

		let base = this.createAonNumber(this.TAX_BASE + i, MSG.BASE, tax.base);
		base.onChange(() => this.onChangeTaxBase(tax, base.value, i))
		taxesTable.addCell(base);
		base.readonly = this.invoice.isReadonly() 
			|| this.invoice.details.length > 0
			|| tax.type.includes('IRPF');

		if(this.invoice.details.length > 0 || tax.type.includes('IRPF'))
			base.disabled = CONSTANT.TRUE;

		// ----- TAX QUOTA

		let quotaVal = tax.surcharge_quota ? tax.quota + tax.surcharge_quota : tax.quota;

		let quota = this.createAonNumber(this.TAX_QUOTA + i, MSG.QUOTA, quotaVal)
		taxesTable.addCell(quota);
		quota.readonly = this.invoice.isReadonly() || this.invoice.details.length > 0 || tax.type.includes('IRPF');
		if(this.invoice.details.length > 0 || tax.type.includes('IRPF'))
			quota.disabled = CONSTANT.TRUE;

		// ----- TAX DELETE

		if(!this.invoice.isReadonly() && this.invoice.details.length === 0) {
			let taxDelete = new AonIconButton();
			taxDelete.id = this.TAX_DELETE + i;
			taxDelete.title = MSG.DELETE_TAX;
			taxDelete.icon = MATERIAL_ICONS.REMOVE_CIRCLE;
			taxDelete.addEventListener(EVENT.CLICK, () => {
				this.invoice.deleteTax(tax, i);
				this.setFocus(undefined);
				this.reload();
				if(this.autosave) this.save();
			});
			taxesTable.addCell(taxDelete);
		}
	}

	buildDetailCard(parent) {
		let card = this.getElement(this.DETAIL);
		if(!card) {
			card = new AonCard();
			card.id = this.DETAIL;
			card.title = MSG.INVOICE_CONCEPTS;
			parent.appendChild(card);
		}

		let table = this.getElement(this.DETAIL_TABLE);
		if(!table) {
			table = new AonBasicTable();
			table.id = this.DETAIL_TABLE;
			card.setContent(table);
		}
		table.removeRows();
		for(let i = 0; i < this.invoice.details.length; i++) {
			let detail = this.invoice.details[i];
			if(this.fileOpened) this.printMinimizeDetail(table, detail, i)
			else this.printDetail(table, detail, i);
		}

		let div = this.createElement(TAG.DIV);
		card.addContent(div);

		let addButton = this.getElement(this.DETAIL_ADD);
		if(!this.invoice.isReadonly() && !addButton) {
			addButton = new AonIconButton();
			addButton.id = this.DETAIL_ADD;
			addButton.title = MSG.ADD_DETAIL;
			addButton.icon = MATERIAL_ICONS.ADD;
			addButton.addEventListener(EVENT.CLICK, () => {
				this.setFocus(this.DETAIL_DESCRIPTION + this.invoice.details.length);
				this.invoice.addDetail();
				this.reload();
				if(this.fileOpened){
					const i = this.invoice.details.length -1;
					const detail = this.invoice.details[i];
					this.printDetailDialog(detail, i);
				}
				if(this.autosave) this.save();
			});
			div.appendChild(addButton);
		}
	}

	printMinimizeDetail(table, detail, i) {
		table.addRow();

		// ----- DETAIL CONCEPT | DESCRIPTION | PRODUCT

		let description = LS.isNewTheme() ? new AonNewSuggestion() : new AonSuggestion();
		description.id = this.DETAIL_DESCRIPTION + i;
		description.title = MSG.CONCEPT;
		description.addEventListener(EVENT.AON_KEYUP, (e) => {
			if(description.value.length > 2) {
				let data = { value: description.value};
				getItems(data).then(r => {
					description.buildOptions(r.map(r => {return {
						name: r.name,
						value: r,
						item: r};}));
				}).catch(e => this.showError(e));
			  }
		});

		description.addEventListener(EVENT.CHANGE, () => {
			this.invoice.details[i].description = description.value;
			this.setFocus(this.DETAIL_QUANTITY + i);
		});

		description.addEventListener(EVENT.SELECT,(e) => {
			detail.description = e.detail.name;
			detail.item = e.detail.item;
			detail.price = e.detail.item.price;
			this.invoice.setDetail(detail, i);
			this.setFocus(this.DETAIL_DESCRIPTION + i);
			this.reload();
		});

		let td = table.addCell(description);
		td.style.width = '60%';
		description.readonly = this.invoice.isReadonly();
		description.value = detail.description;

		// ----- DETAIL AMOUNT

		let amount = this.createAonNumber(this.DETAIL_AMOUNT + i, MSG.AMOUNT, detail.amount);
		table.addCell(amount);		
		amount.readonly = CONSTANT.TRUE;
		amount.disabled = CONSTANT.TRUE;

		// ----- DETAIL OPTIONS

		let detailOptions = new AonIconButton();
		detailOptions.id = this.DETAIL_OPTIONS + i;
		detailOptions.title = MSG.OPTIONS;
		detailOptions.icon = MATERIAL_ICONS.EDIT;
		detailOptions.addEventListener(EVENT.CLICK, () => {
			this.printDetailDialog(detail, i);
		});
		table.addCell(detailOptions);

		// ----- DETAIL DELETE

		if(!this.invoice.isReadonly()) {
			let detailDelete = new AonIconButton();
			detailDelete.id = this.DETAIL_DELETE + i;
			detailDelete.title = MSG.DELETE_DETAIL;
			detailDelete.icon = MATERIAL_ICONS.REMOVE_CIRCLE;
			detailDelete.addEventListener(EVENT.CLICK, () => {
				this.setFocus(undefined);
				this.invoice.deleteDetail(detail, i);
				this.reload();
				if(this.autosave) this.save();
			});
			table.addCell(detailDelete);
		}
	}

	createAonNumber(id, title, value) {
		let aonNumber = this.createAonElement(LS.isNewTheme() ? new AonNewNumber() : new AonNumber(), id, title);
		aonNumber.format = CONSTANT.TRUE;
		aonNumber.decimals = "2";
		aonNumber.readonly = this.invoice.isReadonly();
		aonNumber.value = value || 0.0;
		return aonNumber;
	}

	createTableSpan(width, marginRight){
		let span = this.createSpan();
		span.style.width= width;
		span.style.marginRight = marginRight;
		return span;
	}	

	onChangeDetail(detail, i, dialog) {
		this.invoice.setDetail(detail, i);
		this.reload();
		if(dialog) this.printDetailDialog(this.invoice.details[i], i);
		if(this.autosave) this.save();
	}

	onChangeDetailQuantity(detail, value, i, dialog) {
		detail.quantity = value;
		if(!dialog) this.setFocus(this.DETAIL_PRICE + i);
		this.onChangeDetail(detail, i, dialog);
	}

	onChangeDetailPrice(detail, value, i, dialog) {
		detail.price = value;
		if(!dialog)	this.setFocus(this.DETAIL_DISCOUNT + i);
		this.onChangeDetail(detail, i, dialog);
	}

	onChangeDetailDiscount(detail, value, i, dialog) {
		detail.discount = value;
		if(!dialog) this.setFocus(this.DETAIL_VAT + i);
		this.onChangeDetail(detail, i, dialog);
	}

	onChangeDetailVat(detail, value, i, dialog) {
		detail.percentage = value;
		detail.vat = value;
		this.invoice.setDetail(detail, i);
		this.onChangeDetail(detail, i, dialog);
	}

	printDetail(table, detail, i) {
		table.addRow(); // ----- ROW i

		// ----- DETAIL CONCEPT | DESCRIPTION | PRODUCT
		
		let description = LS.isNewTheme() ? new AonNewTextarea() : new AonAutosizeTextarea();
		description.id = this.DETAIL_DESCRIPTION + i;
		description.title = MSG.CONCEPT;
		description.readonly = this.invoice.isReadonly();
		description.value = detail.description;
		description.addEventListener(EVENT.AON_KEYUP, (e) => {
			if(description.value.length > 2) {
				let data = { value: description.getValue()};
				getItems(data).then(r => {
					description.buildOptions(r.map(r => {
						return {
							name: r.description && r.description !== '' ? r.description : r.name,
							value: r,
							item: r};}));
				}).catch(e => this.showError(e));
			  }
		});

		description.addEventListener(EVENT.CHANGE, () => {
			this.invoice.details[i].description = description.value;
			this.setFocus(this.DETAIL_QUANTITY + i);
		});

		description.addEventListener(EVENT.SELECT,(e) => {
			detail.description = e.detail.name;
			detail.item = e.detail.item;
			detail.price = e.detail.item.price;
			detail.prepayment = ("PREPAYMENT" === e.detail.item.product.type);
			this.invoice.setDetail(detail, i);
			this.setFocus(this.DETAIL_DESCRIPTION + i);
			this.reload();
		});

		let td = table.addCell(description);
		td.style.width = '50%';


		// ----- DETAIL QUANTITY

		let quantity = this.createAonNumber(this.DETAIL_QUANTITY + i, MSG.QUANTITY, detail.quantity);
		quantity.onChange(() => this.onChangeDetailQuantity(detail, quantity.value, i));
		let td2 = table.addCell(quantity);
		td2.style.verticalAlign = "bottom";
		quantity.readonly = this.invoice.isReadonly()

		// ----- DETAIL PRICE

		let price = this.createAonNumber(this.DETAIL_PRICE + i, MSG.PRICE, detail.price);
		price.onChange(() => this.onChangeDetailPrice(detail, price.value, i));
		let td3 = table.addCell(price);
		td3.style.verticalAlign = "bottom";
		price.readonly = this.invoice.isReadonly()

		// ----- DETAIL DISCOUNT

		let discount = this.createAonNumber(this.DETAIL_DISCOUNT + i, '%Dto', detail.discount);
		discount.onChange(() => this.onChangeDetailDiscount(detail, discount.value, i));
		let td4 = table.addCell(discount);
		td4.style.verticalAlign = "bottom";
		discount.readonly = this.invoice.isReadonly()

		// ----- DETAIL AMOUNT

		let amount = this.createAonNumber(this.DETAIL_AMOUNT + i, MSG.AMOUNT, detail.amount);
		let td5 = table.addCell(amount);
		td5.style.verticalAlign = "bottom";
		amount.readonly = CONSTANT.TRUE;
		amount.disabled = CONSTANT.TRUE;

		// ----- DETAIL VAT
		if(this.invoice.isVatEnabled()) {
			let vat = LS.isNewTheme() ? new AonNewSelect() : new AonSelect();
			vat.id = this.DETAIL_VAT + i;
			vat.title = '%IVA';
			vat.options = JSON.stringify(TaxIVAPercentage);
			if(detail.prepayment === undefined) detail.prepayment = false;
			vat.readonly = this.invoice.isReadonly() || (detail.prepayment && detail.prepayment == 'true');
			
			if(detail.prepayment && detail.prepayment == 'true') {
				vat.disabled = detail.prepayment && detail.prepayment == 'true';
			} 

			vat.addEventListener(EVENT.SELECT, () => this.onChangeDetailVat(detail, vat.value, i));
			let td6 = table.addCell(vat);
			td6.style.verticalAlign = "bottom";
			if(this.invoice.isEmitida() && !this.invoice.isNacional()) {
				detail.percentage = undefined;
				detail.vat = undefined;
				vat.setDisabled(true);
			}
			detail.percentage = detail.percentage || detail.vat;
			if(!detail.percentage && (!detail.prepayment || detail.prepayment == 'false')) 
				detail.percentage = 21.0;
			if(detail.percentage) vat.value = detail.percentage;
		} else if(detail.percentage !== 0.0){
			detail.percentage = 0.0;
			this.invoice.setDetail(detail, i);
		}

		// ----- DETAIL OPTIONS

		let detailOptions = new AonIconButton();
		detailOptions.id = this.DETAIL_OPTIONS + i;
		detailOptions.title = MSG.OPTIONS;
		detailOptions.icon = MATERIAL_ICONS.EDIT;
		detailOptions.addEventListener(EVENT.CLICK, () => {
			this.printDetailDialog(detail, i);
		});
		table.addCell(detailOptions);
		

		// ----- DETAIL DELETE

		if(!this.invoice.isReadonly()) {
			let detailDelete = new AonIconButton();
			detailDelete.id = this.DETAIL_DELETE + i;
			detailDelete.title = MSG.DELETE_DETAIL;
			detailDelete.icon = MATERIAL_ICONS.REMOVE_CIRCLE;
			detailDelete.addEventListener(EVENT.CLICK, () => {
				this.setFocus(undefined);
				this.invoice.deleteDetail(detail, i);
				this.reload();
				if(this.autosave) this.save();
			});
			table.addCell(detailDelete);
		}
	}

	printDetailDialog(detail, i) {
		let dialog = this.getApplication().getDialog();
		dialog.setTitle("DETALLE");
		dialog.addAcceptAction(() => {
			
		});

		let div = this.createElement(TAG.DIV);
		div.style.margin = '15px';
		dialog.setContent(div);

		let table = new AonBasicTable();
		table.id = this.DIALOG + 'Detail';
		div.appendChild(table);

		table.addRow(); // ----- ROW 1

		// ----- DETAIL CONCEPT | DESCRIPTION | PRODUCT
		
		let description = LS.isNewTheme() ? new AonNewSuggestion() : new AonSuggestion();
		description.id = this.DETAIL_DESCRIPTION + 'Dialog' + i;
		description.title = MSG.CONCEPT;
		description.addEventListener(EVENT.AON_KEYUP, () => {
			if(description.value.length > 2) {
				let data = { value: description.value};
				getItems(data).then(r => {
					description.buildOptions(r.map(r => {return {
						name: r.name,
						value: r,
						item: r};}));
				}).catch(e => this.showError(e));
			  } 
		});

		description.addEventListener(EVENT.CHANGE, () => {
			this.invoice.details[i].description = description.value;
		});
		
		description.addEventListener(EVENT.SELECT,(e) => {
			detail.description = e.detail.name; 
			detail.item = e.detail.item;
			detail.price = e.detail.item.price;
			this.invoice.setDetail(detail, i);
			this.reload();
			this.printDetailDialog(this.invoice.details[i], i);
		});

		table.addCell(description, this.invoice.isVatEnabled() && (!detail.prepayment || detail.prepayment == 'false') ? '3' : '4');
		description.readonly = this.invoice.isReadonly();
		description.value = detail.description;

		// ----- DETAIL VAT
		if(this.invoice.isVatEnabled() &&  (!detail.prepayment || detail.prepayment == 'false')) {
			let vat = LS.isNewTheme() ? new AonNewSelect() : new AonSelect();
			vat.id = this.DETAIL_VAT + 'Dialog' + i;
			vat.title = '%IVA';
			vat.options = JSON.stringify(TaxIVAPercentage);
			if(detail.prepayment === undefined) detail.prepayment = false;
			vat.readonly = this.invoice.isReadonly() || detail.prepayment;
			vat.addEventListener(EVENT.SELECT, () => {
				detail.percentage = vat.value;
				this.invoice.setDetail(detail, i);
				this.reload();
				this.printDetailDialog(this.invoice.details[i], i);
				if(this.autosave) this.save();
			});
			table.addCell(vat);
			if(this.invoice.isEmitida() && !this.invoice.isNacional()) {
				detail.percentage = undefined;
				detail.vat = undefined;
				vat.setDisabled(true);
			}
			detail.percentage = detail.percentage || detail.vat;
			if(!detail.percentage) {
				detail.percentage = 21.0;
			}
			if(detail.percentage) vat.value = detail.percentage;
		} else if(detail.percentage !== 0.0) {
			detail.percentage = 0.0;
			this.invoice.setDetail(detail, i);
		}

		table.addRow(); // ----- ROW 2

		// ----- DETAIL QUANTITY

		let quantity = this.createAonNumber(this.DETAIL_QUANTITY + 'Dialog' + i, MSG.QUANTITY, detail.quantity);
		quantity.onChange(() => this.onChangeDetailQuantity(detail, quantity.value, i, true));
		table.addCell(quantity);
		quantity.readonly = this.invoice.isReadonly()
	
		// ----- DETAIL PRICE

		let price = this.createAonNumber(this.DETAIL_PRICE + 'Dialog' + i, MSG.PRICE, detail.price);
		price.onChange(() => this.onChangeDetailPrice(detail, price.value, i, true));
		table.addCell(price);
		price.readonly = this.invoice.isReadonly()
	
		// table.addRow(); // ----- ROW 3

		// ----- DETAIL DISCOUNT

		let discount = this.createAonNumber(this.DETAIL_DISCOUNT + 'Dialog' + i, '%Dto', detail.discount);
		discount.onChange(() => this.onChangeDetailDiscount(detail, discount.value, i, true));
		table.addCell(discount);
		discount.readonly = this.invoice.isReadonly()

		// ----- DETAIL AMOUNT

		let amount = this.createAonNumber(this.DETAIL_AMOUNT + 'Dialog' + i, MSG.AMOUNT, detail.amount);
		table.addCell(amount);
		amount.readonly = CONSTANT.TRUE;
		amount.disabled = CONSTANT.TRUE;

		table.addRow(); // ----- ROW 4

		// ----- CATEGORY
		if(this.getDur().hasAccounting() || this.getDur().hasParentAccounting()) {
			let category = LS.isNewTheme() ? new AonNewSelect() : new AonSelect();
			category.id = this.DETAIL_CATEGORY + i;
			category.title = MSG.CATEGORY;
			category.autocomplete = true;
			category.readonly = this.invoice.isReadonly();
			category.addEventListener(EVENT.SELECT, () => {
				detail.category = category.value;
				this.invoice.setDetail(detail, i);
				if(this.autosave) this.save();
			});
			table.addCell(category, '2');
			getInvoiceAccounts({type: this.invoice.getInvoiceType()}).then(accounts => {
				let accs = accounts.map(acc => {return {name: acc.name, value: acc.code};});
				category.options = JSON.stringify(accs);
				category.value = detail.category || this.invoice.getCategory();
			});	
		}

		// ----- BIEN AFECTO
		// TODO
		let bienAfecto = LS.isNewTheme() ? new AonNewSelect() : new AonSelect();
		bienAfecto.id = 'aonInvoiceDetailBienAfecto';
		bienAfecto.title = 'Bien Afecto'; 
		bienAfecto.autocomplete = true;
		bienAfecto.setAlias('id', 'description');
		bienAfecto.readonly = this.invoice.isReadonly();

		bienAfecto.addEventListener(EVENT.SELECT, () => {
			detail.investAsset = bienAfecto.value;
			this.invoice.setDetail(detail, i);
			if(this.autosave) this.save();
		});

		table.addCell(bienAfecto, '2');
		getInvestAssets({}).then(investAssets => {
			bienAfecto.options = JSON.stringify(investAssets);
			if(detail.investAsset)
				bienAfecto.value = detail.investAsset;		
		});
		
		table.addRow();

		// ----- PREPAYMENT | SUPLIDO
		
		let prepayment = new AonSwitch();
		prepayment.id = this.DETAIL_PREPAYMENT + 'Dialog' + i;
		prepayment.title = 'Suplido';//MSG.DETAIL_PREPAYMENT;
		prepayment.addEventListener(EVENT.CHANGE, () => {
			detail.prepayment = prepayment.checked;
			if((!detail.prepayment || detail.prepayment == 'false'))
				detail.percentage = 21.0;
			else detail.percentage = 0.0;
			this.invoice.setDetail(detail, i);
			this.reload();
			this.printDetailDialog(this.invoice.details[i], i);
			if(this.autosave) this.save();
		});
		table.addCell(prepayment);
		prepayment.readonly = this.invoice.isReadonly();
		prepayment.checked = detail.prepayment;

		dialog.open();
	}

	buildFinanceCard(parent) {
		let card = this.getElement(this.FINANCE);
		if(!card) {
			card = new AonCard();
			card.id = this.FINANCE;
			card.title = MSG.EXPIRATIONS;
			parent.appendChild(card);
			card.addTitleButton("Resetear", MATERIAL_ICONS.AUTORENEW, false, () => {
				this.invoice.resetFinances();
				this.reload();
			});
		}

		let table = this.getElement(this.FINANCE_TABLE);
		if(!table) {
			table = new AonBasicTable();
			table.id = this.FINANCE_TABLE;
			card.setContent(table);
		}
		table.removeRows();
		for(let i = 0; i < this.invoice.finances.length; i++) {
			let finance = this.invoice.finances[i];
			if(this.fileOpened) this.printMinimizeFinance(table, finance, i)
			else this.printFinance(table, finance, i);
		}

		let div = this.createElement(TAG.DIV);
		card.addContent(div);

		let addButton = this.getElement(this.FINANCE_ADD);
		if(!this.invoice.isReadonly() && !addButton) {
			let addButton = new AonIconButton();
			addButton.id = this.FINANCE_ADD;
			addButton.title = MSG.ADD_FINANCE;
			addButton.icon = MATERIAL_ICONS.ADD;
			addButton.addEventListener('click', () => {
				this.setFocus(this.FINANCE_DUE_DATE + this.invoice.finances.length);
				this.invoice.addFinance();
				this.reload();
				if(this.fileOpened) {
					const i = this.invoice.finances.length -1;
					const finance = this.invoice.finances[i];
					this.printFinanceDialog(finance, i);
				}
				if(this.autosave) this.save();
			});
			div.appendChild(addButton);
		}
	}


	onChangeFinanceAmount(finance, value, i, dialog) {
		this.setFocus(this.FINANCE_AMOUNT + i);
		finance.amount = value;
		this.invoice.setFinance(finance, i);
		if(this.autosave) this.save();		
	}

	printFinanceDialog(finance, i) {
		let dialog = this.getApplication().getDialog();
		dialog.setTitle("VENCIMIENTO");
		dialog.addAcceptAction(() => {
			
		});
		

		let div = this.createElement(TAG.DIV);
		div.style.margin = '15px';
		dialog.setContent(div);

		let table = new AonBasicTable();
		table.id = this.DIALOG + 'Detail';
		div.appendChild(table);

		table.addRow(); // ----- ROW 1

		// ----- FINANCE DUE DATE

		let date = LS.isNewTheme() ? new AonNewDate() : new AonDate();
		date.id = this.FINANCE_DUE_DATE + 'Dialog' + i;
		date.title = MSG.DATE; //MSG.DUE_DATE;
		date.readonly = this.invoice.isReadonly();
		date.addEventListener(EVENT.CHANGE, () => {
			finance.due_date = date.value;
			this.setFocus(date.id);
			this.invoice.setFinance(finance, i);
			if(this.autosave) this.save();
		});
		date.value = finance.due_date;
		table.addCell(date);
		date.value = finance.due_date;

		table.addRow(); // ----- ROW 2

		// ----- FINANCE PAYMETHOD

		let paymethod = LS.isNewTheme() ? new AonNewSelect() : new AonSelect();
		paymethod.id =this.FINANCE_PAYMETHOD + 'Dialog' + i;
		paymethod.title = MSG.PAYMETHOD;
		paymethod.autocomplete = true;
		// paymethod.options = JSON.stringify(Paymethods);
		paymethod.readonly = this.invoice.isReadonly();
		paymethod.addEventListener(EVENT.SELECT, () => {
			this.setFocus(this.FINANCE_BANK_ACCOUNT + i);
			finance.paymethod = paymethod.value;
			this.invoice.setFinance(finance, i);
			if(this.autosave) this.save();
		});
		table.addCell(paymethod);

		getPaymethods({}).then(paymethods => {
			let pms = paymethods.map(pm => {return {name: pm.name, value: pm.id};});
			paymethod.options = JSON.stringify(pms);
			paymethod.value = finance.paymethod;
		});


		table.addRow(); // ----- ROW 3

		// ----- FINANCE BANK ACCOUNT | RBANK
		
		let bankAccount = LS.isNewTheme() ? new AonNewSuggestion() : new AonSuggestion();
		bankAccount.id = this.FINANCE_BANK_ACCOUNT + 'Dialog' + i;
		bankAccount.title = 'Cuenta Bancaria'; //MSG.BANK_ACCOUNT;
		bankAccount.readonly = this.invoice.isReadonly();

		bankAccount.addEventListener(EVENT.KEYUP, () => {

		});

		bankAccount.addEventListener(EVENT.CHANGE, () => {
			this.setFocus(this.FINANCE_AMOUNT + i);	
			finance.bank_account = bankAccount.value;
			this.invoice.setFinance(finance, i);
		});
	
		// bankAccount.addEventListener(EVENT.SELECT,() => {
		// 	this.setFocus(this.FINANCE_AMOUNT + i);
		// })
		
		table.addCell(bankAccount);
		finance.bank_account = finance.bank_account || finance.iban;
		bankAccount.value = finance.bank_account;

		table.addRow(); // ----- ROW 3

		// ----- FINANCE AMOUNT
	
		let amount = this.createAonNumber(this.FINANCE_AMOUNT + 'Dialog' + i, MSG.AMOUNT, finance.amount);
		amount.onChange(() => this.onChangeFinanceAmount(finance, amount.value, i, true));
		table.addCell(amount);
		amount.readonly = this.invoice.isReadonly()
		dialog.open();
	}

	printMinimizeFinance(table, finance, i) {
		table.addRow(); // ----- ROW i
		
		// ----- FINANCE DUE DATE

		let date = LS.isNewTheme() ? new AonNewDate() : new AonDate();
		date.id = this.FINANCE_DUE_DATE + i;
		date.title = MSG.DATE; //MSG.DUE_DATE;
		date.readonly = this.invoice.isReadonly();
		date.addEventListener(EVENT.CHANGE, () => {
			finance.due_date = date.value;
			this.setFocus(date.id);
			this.invoice.setFinance(finance, i);
			if(this.autosave) this.save();
		});
		date.value = finance.due_date;
		table.addCell(date);
		date.value = finance.due_date;

		// ----- FINANCE AMOUNT

		let amount = this.createAonNumber(this.FINANCE_AMOUNT + i, MSG.AMOUNT, finance.amount);
		amount.onChange(() => this.onChangeFinanceAmount(finance, amount.value, i));
		table.addCell(amount);
		amount.readonly = this.invoice.isReadonly()

		// ----- FINANCE OPTIONS

		let financeOptions = new AonIconButton();
		financeOptions.id = this.FINANCE_OPTIONS + i;
		financeOptions.title = MSG.OPTIONS;
		financeOptions.icon = MATERIAL_ICONS.EDIT;
		financeOptions.addEventListener(EVENT.CLICK, () => {
			this.printFinanceDialog(finance, i);
		});
		table.addCell(financeOptions);

		// ----- FINANCE DELETE
		
		if(!this.invoice.isReadonly()) {
			let financeDelete = new AonIconButton();
			financeDelete.id = this.FINANCE_DELETE + i;
			financeDelete.title = MSG.DELETE_FINANCE;
			financeDelete.icon = MATERIAL_ICONS.REMOVE_CIRCLE;
			financeDelete.addEventListener(EVENT.CLICK, () => {
				this.invoice.deleteFinance(finance, i);
				this.reload();
				if(this.autosave) this.save();
			});
			table.addCell(financeDelete);
		}
	}

	printFinance(table, finance, i) {
		table.addRow(); // ----- ROW i

		// ----- FINANCE DUE DATE

		let date = LS.isNewTheme() ? new AonNewDate() : new AonDate();
		date.id = this.FINANCE_DUE_DATE + i;
		date.title = MSG.DATE; //MSG.DUE_DATE;
		date.readonly = this.invoice.isReadonly();
		date.addEventListener(EVENT.CHANGE, () => {
			finance.due_date = date.value;
			this.setFocus(date.id);
			this.invoice.setFinance(finance, i);
			if(this.autosave) this.save();
		});
		date.value = finance.due_date;
		let dateCell = table.addCell(date);
		date.value = finance.due_date;
		dateCell.style.width = '15%';

		// ----- FINANCE PAYMETHOD

		let paymethod = LS.isNewTheme() ? new AonNewSelect() : new AonSelect();
		paymethod.id = this.FINANCE_PAYMETHOD + i;
		paymethod.title = MSG.PAYMETHOD;
		paymethod.autocomplete = true;
		paymethod.setAlias("id", "name");
		// paymethod.options = JSON.stringify(Paymethods);
		paymethod.readonly = this.invoice.isReadonly();
		paymethod.addEventListener(EVENT.SELECT, () => {
			this.setFocus(this.FINANCE_BANK_ACCOUNT + i);
			finance.paymethod = paymethod.value;
			this.invoice.setFinance(finance, i);
			if(this.autosave) this.save();
			const pm = paymethod.getOptions().filter(f => f.id == paymethod.value)[0];
			if((!this.invoice.isEmitida() && pm.type === 'NEGOTIABLE_DOCUMENT')
				 	|| (this.invoice.isEmitida() && pm.type === 'BANK_TRANSFER')) {

				getRegistryPaymethod({registry: this.company.id}).then(crpm => {
					let ba = this.getElement(this.FINANCE_BANK_ACCOUNT + i);
					if(ba) {
						ba.value = crpm.bank.bank_account;
						finance.bank_account = ba.value;
						this.invoice.setFinance(finance, i);
					}
				});
				// getRegistryBanks(this.company.id).then(r => {
				// 	let ba = this.getElement(this.FINANCE_BANK_ACCOUNT + i);
				// 	ba.value = r[0] ? r[0].bank_account : "";
				// 	finance.bank_account = ba.value;
				// 	this.invoice.setFinance(finance, i);
				// });	
			} else if((this.invoice.isEmitida() && pm.type === 'NEGOTIABLE_DOCUMENT')
				|| (!this.invoice.isEmitida() && pm.type === 'BANK_TRANSFER')) { 
				getRegistryBanks(this.invoice.getRegistry().id).then(r => {
					let ba = this.getElement(this.FINANCE_BANK_ACCOUNT + i);
					if(ba) {
						ba.value = r[0] ? r[0].bank_account : "";
						finance.bank_account = ba.value;
						this.invoice.setFinance(finance, i);
					}
				});
			} else {
				let ba = this.getElement(this.FINANCE_BANK_ACCOUNT + i);
				if(ba) {
					ba.value = "";
					finance.bank_account = "";
					this.invoice.setFinance(finance, i);
				}
			}
		});
		let paymethodCell = table.addCell(paymethod);
		paymethodCell.style.width = '25%';

		getPaymethods({}).then(paymethods => {
			paymethod.setOptions(paymethods);
			paymethod.value = finance.paymethod;
		});

		// ----- FINANCE BANK ACCOUNT | RBANK

		let bankAccount = LS.isNewTheme() ? new AonNewSuggestion() : new AonSuggestion();
		bankAccount.id = this.FINANCE_BANK_ACCOUNT + i;
		bankAccount.title = 'Cuenta Bancaria'; //MSG.BANK_ACCOUNT;
		bankAccount.readonly = this.invoice.isReadonly();

		bankAccount.addEventListener(EVENT.AON_KEYUP, () => {
			bankAccount.buildOptions(this.rbanks.filter(f => f.bank_account.includes(bankAccount.value))
				.map(r => {return {
						name: r.bank_account,
						value: r.bank_account,
						rbank: r};}));
		});

		bankAccount.addEventListener(EVENT.CHANGE, () => {
			this.setFocus(this.FINANCE_AMOUNT + i);
			finance.bank_account = bankAccount.value;
			this.invoice.setFinance(finance, i);
		});

		// bankAccount.addEventListener(EVENT.SELECT,() => {
		// 	this.setFocus(this.FINANCE_AMOUNT + i);
		// })

		let ibanCell = table.addCell(bankAccount);
		ibanCell.style.width = '50%';
		finance.bank_account = finance.bank_account || finance.iban;
		bankAccount.value = finance.bank_account || '';

		// ----- FINANCE AMOUNT

		let amount = this.createAonNumber(this.FINANCE_AMOUNT + i, MSG.AMOUNT, finance.amount);
		amount.onChange(() => this.onChangeFinanceAmount(finance, amount.value, i));
		table.addCell(amount);
		amount.readonly = this.invoice.isReadonly()

		// ----- FINANCE DELETE

		if(!this.invoice.isReadonly()) {
			let financeDelete = new AonIconButton();
			financeDelete.id = this.FINANCE_DELETE + i;
			financeDelete.title = MSG.DELETE_FINANCE;
			financeDelete.icon = MATERIAL_ICONS.REMOVE_CIRCLE;
			financeDelete.addEventListener(EVENT.CLICK, () => {
				this.invoice.deleteFinance(finance, i);
				this.reload();
				if(this.autosave) this.save();
			});
			table.addCell(financeDelete);
		}
	}

	getInvoiceTitle() {
		if(this.getInvoice().isEmitida()) {
			return MSG.INVOICE_ISSUED;
		} else if(this.getInvoice().isTicket()){
			return MSG.TICKET;
		} else return MSG.INVOICE_RECEIVED;
	}

	save(msg) {
		msg = msg || MSG.SAVED_DATA;
		insertInvoice(this.getInvoice()).then(r => {
			this.getInvoice().id = r.id;
			this.showMessage(msg);
		}).catch(e => this.showError(e));
	}

	back() {
		let parent = this.getApplication().getParent();
		if(this.isMobile())
			parent.buildToolbarOptions();
		parent.aonInvoiceList(parent.filter, parent.invofoxFilter);
	}

	more(e) {
		e.preventDefault();
		let rect = e.target.getBoundingClientRect();
		let x = e.clientX - rect.left;
		let y = e.clientY - rect.top;

		const top  = rect.top + y;
		const left = rect.left + x;

		let d = this.getApplication().getOptionDialog();
		let rectify = ACTION.RECTIFY;
		rectify.fn = () => this.rectifyInvoice();
		let duplicate = ACTION.DUPLICATE;
		duplicate.fn = () => this.duplicateInvoice();
		d.setMenuOptions([rectify], top, left);
		d.open();
	}

	showFile(){
		let invoiceToolbar = this.getElement(this.TOOLBAR);
		let button = this.getElement(invoiceToolbar.TITLE_SECTION + 'ShowFileButton');
		let visible = 'visibility_off' === button.icon;
		let fileDiv = this.getElement(this.FILE);
		let dataDiv = this.getElement(this.DATA);
		this.fileOpened = !visible;
		if(visible) {
			button.icon = 'visibility';
			fileDiv.style.display = 'none';
			dataDiv.style.width = '100%';

			let general = this.getElement(this.GENERAL);
			if(general) general.style.display = 'flex';

			let communication = this.getElement(this.COMMUNICATION);
			if(communication) communication.style.display = 'flex';
		} else {
			button.icon = 'visibility_off';
			fileDiv.style.display = 'block';
			fileDiv.style.width = '50%';
			dataDiv.style.width = '50%';
			
			let general = this.getElement(this.GENERAL);
			if(general) general.style.display = 'block';

			let communication = this.getElement(this.COMMUNICATION);
			if(communication) communication.style.display = 'block';

			this.clearElement(fileDiv);

			let json = {
				id: this.getInvoice().id,
				source: this.getInvoice().isRawdoc() ? 'rawdoc' : 'invoice',
				domain_id: LS.getDomainId(),
				domain_name: LS.getDomainName(),
				login: LS.getDomainLogin()
			};

			let viewer = new AonViewer();
			viewer.type = !this.getInvoice().file || this.getInvoice().isEmitida()
				? 'application/pdf' : this.getInvoice().file.content_type;
			viewer.file = !this.getInvoice().file || this.getInvoice().isEmitida()
			 	? '/ms/api/download_invoice_pdf?json=' + btoa(JSON.stringify(json))
			 	: this.getInvoice().file.path;
			viewer.width = fileDiv.offsetWidth;
			viewer.addEventListener(EVENT.SEND_MAIL, () => this.sendInvoice());
			viewer.addEventListener(EVENT.PRINT_IMAGE, () => { getInvofoxTextContent(this.getInvoice().insight.invofoxId).then(t => viewer.printImageTextLayer(t)); } );
			viewer.addEventListener(EVENT.PRINT_PDF_PAGE, (e) => { if ( !e.detail.text ) getInvofoxTextContent(this.getInvoice().insight.invofoxId).then(t => viewer.printPdfTextLayer(e.detail.page, t)); } );
			fileDiv.appendChild(viewer);
		}
		this.buildDetailCard();
		this.buildFinanceCard();
	}

	previousInvoice() {
		let invoice = getPreviousInvoice();
		this.changeInvoice(invoice);
	}

	nextInvoice() {
		let invoice = getNextInvoice();
		this.changeInvoice(invoice);
	}

	changeInvoice(invoice) {
		let inv = new Invoice(invoice);
		if(invoice.invofox) {
			getInvofoxDocument(invoice.id).then( doc => {
				let aip = document.querySelector('aon-invoice-panel');
				aip.aonInvoice(invoice.type, doc);
			}); 
		} else if(invoice && inv && !inv.isRawdoc()){
			getInvoice(invoice.id).then((inv) => {
				let aip = document.querySelector('aon-invoice-panel');
				aip.aonInvoice(invoice.type, inv);
			});
		} else if(invoice)  {
			this.getApplication().getParent().aonInvoice(invoice.type, invoice);
		}
	}

	acceptInvoice() {
		// if((this.getDur().hasAccounting() || this.getDur().hasParentAccounting())
		// 	&& !this.invoice.category) {
		// 	this.showError({
		// 		type: CONSTANT.ERROR,
		// 		message: "Para Aceptar es necesaria la categoría."
		// 	});
		// } else {
			if(this.invoice.isEmitida() && this.configuration.tbai.active) {
				let d = this.getApplication().getDialog();
				d.clear();
				if(!this.isMobile()) d.width = '400px';
				d.setTitle(MSG.ACCEPT);
				let certSelect = this.createAonElement(LS.isNewTheme() ? new AonNewSelect() : new AonSelect(), "cert", "Certificado");
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
					this.accept = false;
					let data = this.getInvoice();
					data.cert = certSelect.value;
					acceptInvoice(data).then(r => {
						this.getApplication().getParent().buildCounter();
						this.invoice = new Invoice(r);
						this.getApplication().stopLoader(); 
						this.reload();
					}).catch(e => {
						this.accept = true;
						this.getApplication().stopLoader(); 
						this.showError(e)
					});
				});			
				d.open();
			} else if(this.accept) {
				this.getApplication().startLoader();
				this.accept = false;
				acceptInvoice(this.getInvoice())
				.then(r => {
					this.isInvofoxInvoice() && this.setInvofoxState(CONSTANT.EXPORTED);
					this.getApplication().getParent().buildCounter();
					this.invoice = new Invoice(r);
					this.getApplication().stopLoader(); 
					this.reload();
				}).catch(e => {
					this.accept = true;
					this.getApplication().stopLoader(); 
					this.showError(e)
				});
			}
		// }
	}
	
	setInvofoxState(publicState) {
		try {
			let invofoxDocumentId = this.getInvofoxDocumentId();
			saveInvofoxDocument({_id: invofoxDocumentId , publicState: publicState});
		}  catch ( e ){
			
		}
	}

	recordInvoice() {
		if(this.invoice.category) {
			let div = this.getElement("PRUEBA_RAWDOC_RECORD");
			if(!div) {
				div = this.createDiv("PRUEBA_RAWDOC_RECORD");
				div.style.display = 'none';
				this.appendChild(div);
			}
			this.clearElement(div);
	
			GWT.load(GWT.RAWDOC_RECORD, "PRUEBA_RAWDOC_RECORD");
		} else {
			this.showError({
				type: CONSTANT.ERROR,
				message: "Para Contabilizar es necesario la categoría."
			});
		}
	}

	rejectInvoice() {
		if ( this.isInvofoxInvoice() ) {
			this.setInvofoxState(CONSTANT.PENDING_DECISSION);
			this.back();
			return;
		}
			
		let d = this.getApplication().getDialog();
		d.clear();
		if(!this.isMobile()) d.width = '400px';
		d.setTitle(MSG.REJECT);
		d.setContentHTML('<textarea id="commentTextArea" maxlength="256" class="aonTextarea"> </textarea>');
		d.addAcceptAction(() => {
			let ta = this.getElement('commentTextArea');
			if(!ta.value.isEmpty()){
				let dt = new Date()
				let m = dt.getMonth() + 1;
				let month = m < 10 ? '0' + m : m;
				let dateStr = dt.getDay() + '/' + month  + '/' + dt.getYear() + ' ' + dt.getHours() + ':' + dt.getMinutes() + ':' + dt.getSeconds();
				let comment = {
					date: dateStr,
					user: '',
					status:  'Rechazado',
					reason: ta.value
				};
				this.invoice.remarks.push(comment);
			}
			this.invoice.status = CONSTANT.REJECTED;
			this.build();
			this.save();
		});

		let ta = this.getElement('commentTextArea');
		ta.style.outline = 'none';
		ta.style.width = '100%';
		ta.style.height = '100px';
		d.open();
	}

	addInvoiceRemarks() {
		let aonInvoice = this.getElement('aonInvoice');
		let d = document.getElementById(aonInvoice.DIALOG);
		d.clear();
		if(!this.isMobile()) d.width = '400px';
		d.setTitle(MSG.ADD_REMARKS);
		d.setContentHTML('<textarea id="commentTextArea" maxlength="256" class="aonTextarea"> </textarea>');
		d.addAcceptAction(() => {
			let ta = this.getElement('commentTextArea');
			let dt = new Date()
			let m = dt.getMonth() + 1;
			let month = m < 10 ? '0' + m : m;
			let dateStr = dt.getDay() + '/' + month  + '/' + dt.getYear() + ' ' + dt.getHours() + ':' + dt.getMinutes() + ':' + dt.getSeconds();
			let comment = {
				date: dateStr,
				user: '',
				status: this.getCommentStatus(),
				reason: ta.value
			};
			this.invoice.remarks.push(comment);
			this.reload();
			if(this.autosave) this.save();
		});

		let ta = this.getElement('commentTextArea');
		ta.style.outline = 'none';
		ta.style.width = '100%';
		ta.style.height = '100px';
		d.open();
	}

	addInvoiceComment() {
		let aonInvoice = this.getElement('aonInvoice');
		let d = document.getElementById(aonInvoice.DIALOG);

		let textarea = this.createElement('textarea');
		textarea.maxLength = "256";
		textarea.id = 'commentTextArea';
		textarea.className = 'aonTextarea';
		textarea.value = this.invoice.comments;
		textarea.style.outline = 'none';
		textarea.style.width = '100%';
		textarea.style.height = '100px';

		d.clear();
		if(!this.isMobile()) d.width = '400px';
		d.setTitle(MSG.ADD_COMMENT);
		d.setContent(textarea);
		d.addAcceptAction(() => {
			this.invoice.comments = textarea.value;
			this.reload();
			if(this.autosave) this.save();
		});
		d.open();
	}

	addInvoiceFile() {
		this.getElement(this.INPUT_FILE).click();
	}
	
	changeType() {
		let types = [{
			name: 'Emitida',
			value: 'emitida',
		}, {
			name: 'Recibida',
			value: 'recibida',
		}, {
			name: 'Ticket',
			value: 'ticket',
		}];

		let type = LS.isNewTheme() ? new AonNewSelect() : new AonSelect();
		type.id = this.TYPE;
		type.title = MSG.TYPE;
		type.setOptions(types);
		type.value = this.getInvoice().type;

		let aonInvoice = this.getElement('aonInvoice');
		let d = document.getElementById(aonInvoice.DIALOG);
		d.clear();
		if(!this.isMobile()) d.width = '400px';
		d.setTitle(MSG.CHANGE_TYPE);
		d.setContent(type);
		d.addAcceptAction(() => {
			this.getInvoice().setType(type.value);
			this.reload();
			if(this.autosave) this.save();
		});
		d.open();
	}
	
	rectifyInvoice() {
		let aonInvoice = this.getElement('aonInvoice');
		let d = document.getElementById(aonInvoice.DIALOG);
		d.clear();
		if(!this.isMobile()) d.width = '400px';
		d.setTitle(MSG.RECTIFY_INVOICE);
		d.setContentHTML('<textarea id="commentTextArea" maxlength="256" class="aonTextarea" placeholder="Causa..."></textarea>');
		d.addAcceptAction(() => {
			let recInv = this.invoice;
			recInv.setRectificationInvoice(this.getInvoice());

			let ta = this.getElement('commentTextArea');
			let dt = new Date()
			let m = dt.getMonth() + 1;
			let month = m < 10 ? '0' + m : m;
			let dateStr = dt.getDay() + '/' + month  + '/' + dt.getYear() + ' ' + dt.getHours() + ':' + dt.getMinutes() + ':' + dt.getSeconds();
			let comment = {
				date: dateStr,
				user: '',
				status: this.getCommentStatus(),
				reason: ta.value
			};
			recInv.remarks.push(comment);
			recInv.id = undefined;
			recInv.date = new Date();
			recInv.series = 'R' + new Date().getFullYear();
			recInv.serie = 'R' + new Date().getFullYear();
			recInv.number = undefined;
			recInv.reference = undefined;
			recInv.status = 'inbox';
			recInv.tbai = undefined;
			recInv.tbaiUrl = undefined;

			if(recInv.finances) {
				recInv.finances.forEach((item, i) => {
					recInv.finances[i].id = undefined;
					recInv.finances[i].due_date = new Date();
					recInv.finances[i].amount = recInv.finances[i].amount * (-1);
				});
			}

			if(recInv.details) {
				recInv.details.forEach((item, i) => {
					item.id = undefined;
					item.quantity= item.quantity * (-1);
					recInv.setDetail(item, i);
				});
			}

			let aip = document.querySelector('aon-invoice-panel');
			aip.aonInvoice(recInv.type, recInv);
		});

		let ta = this.getElement('commentTextArea');
		ta.style.outline = 'none';
		ta.style.width = '100%';
		ta.style.height = '100px';
		d.open();
	}

	sendInvoice() {
		let aonInvoice = this.getElement('aonInvoice');

		let d = document.getElementById(aonInvoice.DIALOG);
		d.clear();
		if(!this.isMobile()) d.width = '400px';
		d.setTitle(MSG.SEND_INVOICES);
		if(LS.isNewTheme()){
			let aonEmail = this.createAonElement(new AonEmail(), 'sendInvoicesMail',MSG.EMAIL);
			d.setContent(aonEmail);
		} else d.setContentHTML('<aon-input id="sendInvoicesMail" description="Email"></aon-input>');
		d.addAcceptAction(() => {
			let mail = this.getElement('sendInvoicesMail');
			let message = {
				to: mail.value,
				invoice: this.invoice
			};
			sendInvoice2Mail(message).then(() => {});
		});
		if(this.invoice.isEmitida()) {
			getCustomerEmails(this.invoice.getRegistry()).then(emails => {
				let mail = this.getElement('sendInvoicesMail');
				mail.value = emails[0] || ''; 
			});
		}
		d.open();
	}

	signInvoice() {
		signInvoice(this.invoice.id).then(r => {});
	}

	facturae() {
		let d = this.getApplication().getDialog();
		d.clear();
		if(!this.isMobile()) d.width = '400px';
		d.setTitle("FACTURAE");
		let div  =this.createDiv();
		let certSelect = this.createAonElement(LS.isNewTheme() ? new AonNewSelect() : new AonSelect(), "cert", "Certificado");
		div.appendChild(certSelect);
		getAeatCertificates().then(certs => {
			certSelect.setOptions(certs.map(s => {
				return {
				  value: s.id,
				  name: s.name
				}
			  }));
		}); 
		let span = this.createSpan();
		span.innerHTML = '<textarea id="legalLiterals" maxlength="250" style="width:100%;" class="aonTextarea" placeholder="Literales Legales..."></textarea>';
		div.appendChild(span);
		d.setContent(div);
		d.addAcceptAction(() => {
			let data = {
				id: this.invoice.id,
				domainName: LS.getDomainName(),
				domainId: LS.getDomainId()
			}
			data.cert = certSelect.value;
			data.legalLiterals = this.getElement('legalLiterals').value;
			downloadFacturae(data).then(r => {});
		});			
		d.open();
	}

	duplicateInvoice() {
		let dupInv = this.invoice;
		dupInv.id = undefined;
		dupInv.date = new Date();
		dupInv.number = undefined;
		dupInv.reference = '';
		dupInv.status = 'inbox';
		dupInv.tbai = undefined;
		dupInv.tbaiUrl = undefined;
		if(dupInv.finances) {
			dupInv.finances.forEach((item, i) => {
				dupInv.finances[i].id = undefined;
				dupInv.finances[i].due_date = new Date();
			});
		}
		
		if(dupInv.details) {
			dupInv.details.forEach((item, i) => {
				dupInv.details[i].id = undefined;
			});
		}

		let aip = document.querySelector('aon-invoice-panel');
		aip.aonInvoice(dupInv.type, dupInv);
	}

	getCommentStatus(){
		if(this.invoice.isInbox())
			return 'Inbox';
		else if(this.invoice.isRejected()) {
			return 'Rechazado'
		} else return 'Papelera';
	}
	
	isInvofoxInvoice() {
		return this.getInvofoxDocumentId();
	}
		
	getInvofoxDocumentId(){
		return this.invoice.insight?.invofoxId;	
	}

	trashInvoice() {
		if(this.isInvofoxInvoice()) {
			this.setInvofoxState(CONSTANT.ERROR);
		} else {
			this.getInvoice().status = CONSTANT.DRAFT;
			this.save(MSG.MOVED_TO_TRASH);
		}
		this.reload();
	}

	trashPendingInvoice() {
		let data = {id: this.getInvoice().id};
		if(this.getInvoice().isTbai()) {
			let d = this.getApplication().getDialog();
			d.clear();
			if(!this.isMobile()) d.width = '400px';
			d.setTitle("Anular");
			let certSelect = this.createAonElement(LS.isNewTheme() ? new AonNewSelect() : new AonSelect(), "cert", "Certificado");
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
				let data = this.getInvoice();
				data.cert = certSelect.value;
				deleteInvoice(data).then(() => {
					this.getApplication().stopLoader(); 
					this.showMessage(MSG.DELETED_DATA);
					this.back();
				}).catch(e => this.showError(e));
			});			
			d.open();
		} else {
			deleteInvoice(data).then(() => {
				this.showMessage(MSG.DELETED_DATA);
				this.back();
			}).catch(e => this.showError(e));
		}
	}

	restoreInvoice() {
		if (this.isInvofoxInvoice()) {
			this.setInvofoxState(CONSTANT.PENDING_CORRECTION);
		} else {
			this.getInvoice().status = CONSTANT.INBOX;
			this.save(MSG.RESTORED_DATA);
		}		
		this.reload();
	}

	removeInvoice() {
		deleteRawdocInvoices([this.getInvoice().id]).then(() => {
			let d = this.getApplication().getDialog();
			d.clear();
			if(!this.isMobile()) d.width = '400px';
			d.setTitle(MSG.DELETE_FOREVER);
			d.setContentHTML(MSG.DELETE_FOREVER_INVOICE_CONFIRMATION);
			d.addAcceptAction(() => this.back());
			d.open();
		});
	}

	removeOcrInvoice() {
		let d = this.getApplication().getDialog();
		d.clear();
		if(!this.isMobile()) d.width = '400px';
		d.setTitle(MSG.DELETE_FOREVER);
		d.setContentHTML(MSG.DELETE_CONFIRM);
		d.addAcceptAction(() => {
			this.isInvofoxInvoice() && this.setInvofoxState(CONSTANT.REJECTED);	
			this.back();
		});
		d.open();
	}

}
if(!window.customElements.get(TAG.AON_INVOICE)){
	window.customElements.define(TAG.AON_INVOICE, AonInvoice);
}
