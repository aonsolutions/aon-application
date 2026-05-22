import { AonElement } from '../../components/AonElement.js';
import {
	getInvoice, getInvoiceAccounts, insertInvoice, acceptInvoice, rectifyInvoice, deleteInvoice, deleteRawdocInvoices,
	getCompanyActivities, getPaymethods, getRegistryBanks, sendInvoice2Mail,
	signInvoice, getApiConfiguration, getAeatCertificates, downloadFacturae, getCustomerEmails,
	getInvofoxTextContent, getSupplierTransaction, getCreditorTransaction, getRegistrySuggestedAccount,
	getPaymethod, getRegistry,
	getAmortizationTypes,
	sendInvoiceRejectMail,
	getUserEmail,
	sendInvoiceRestoreMail
} from '../../services/service.js';
import { Invoice, getDocumentNumber } from './Invoice.js';
import { getNextInvoice, getPreviousInvoice } from './InvoiceCache.js';
import { ToolbarType } from '../../models/enums.js';
import { AonToolbar } from '../../components/aon-toolbar.js';
import { AonCard } from '../../components/aon-card.js';
import { AonViewer } from '../../components/aon-viewer.js';
import { CONSTANT, CSS, EVENT, AON_ICONS, MATERIAL_ICONS, MSG, TAG } from '../../environments/environments.js';
import { Transactions } from '../../services/transaction.js';
import { BillingPeriods, ErrCode, ErrKey, getTaxPercentageOption, getTaxType, getTaxTypeName, getVatLabel, getVats, TaxType, WithholdingType } from './invoiceEnums.js';
import { getInvestAssets, getItems } from '../../services/productService.js';
import { AonBasicTable } from '../../components/aon-basic-table.js';
import { AonDialog } from '../../components/aon-dialog.js';
import { AonIconButton } from '../../components/aon-icon-button.js';
import { AonCustomerSuggestion } from '../registry/customer/aon-customer-suggestion.js';
import { AonSwitch } from '../../components/aon-switch.js';
import { AonTab } from '../../components/aon-tab.js';
import { INVOICE } from '../../services/app.js';
import { AonRegistrySuggestion } from '../registry/aon-registry-suggestion.js';
import { addCounter, transferCounter } from './InvoiceCounter.js';
import { createDate, createEmail, createInput, createNumber, createSelect, createSuggestion, createTextarea } from '../../components/CreateComponent.js';
import { getFutureRejectFromOption, getFutureRestoreFromOption, getFutureRestoreToOption, getFutureTrashPendingFromOption, getRejectFromOption, getRestoreFromOption, getRestoreToOption, getTrashPendingFromOption } from './InvoiceUtils.js';
import { BankAccount } from '../registry/bank/BankAccount.js';
import { InvoiceCommunicationConfiguration } from '../../models/InvoiceCommunicationConfiguration.js';
import { AonInvoiceCommunication } from './aon-invoice-communication.js';

import * as GWT from '../../gwt/gwt.js';
import * as ACTION from '../actions.js';
import * as OPTION from './InvoiceOptions.js';
import * as LS from '../../services/localStorageService.js';
import * as JSF from '../aon-jsf-app.js';
import { isValid } from '../../services/documentUtils.js';
import { AonChat } from '../../components/aon-chat.js';
import { AonEmail } from '../../components/aon-email.js';
import { AmortizationPeriod } from '../../models/amortization/AmortizationEnums.js';
import { AonDateUtils } from '../utils/AonDateUtils.js';


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

	SIGN_CERTIFICATE;

	FACTURAE;
	FACTURAE_CERTIFICATE;
	FACTURAE_LEGAL_LITERALS;
	FACTURAE_PERIOD;

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

	constructor() {
		super();
	}

	async connectedCallback() {
		this.configuration = await getApiConfiguration();
		this.icc = new InvoiceCommunicationConfiguration(this.configuration.communication);

		this.initialize();

		this.getInvoice().surcharge = this.getInvoice().surcharge
			|| (!this.getInvoice().isEmitida() && this.getCompany().surcharge);

		this.getInvoice().vatAccrualPayment = this.getInvoice().vatAccrualPayment || this.getCompany().vatAccrualPayment;

		this.buildDur().then(r => {
			this.build();
		});
	}

	initialize() {
		this.accept = true;
		this.fileOpened = this.fileOpened || false;
		this.id = this.id || 'aonInvoiceSheet';
		this.TOOLBAR = this.id + 'Toolbar';
		this.DIV = this.id + CONSTANT.DIV.initCap();
		this.TABS = this.id + 'Tabs';
		this.CONTENT = this.id + 'Content';
		this.DATA = this.id + 'Data';
		this.COMMUNICATION = this.id + 'Communication';
		this.AMORTIZATION = this.id + 'Amortization';
		this.COMMUNICATION_CARD = this.COMMUNICATION + CONSTANT.CARD.initCap();
		this.GENERAL = this.DATA + 'General';
		this.GENERAL_CARD = this.GENERAL + CONSTANT.CARD.initCap();
		this.GENERAL_CARD_TABLE = this.GENERAL_CARD + CONSTANT.TABLE.initCap();
		this.COMMENTS = this.DATA + 'Comments';
		this.COMMENT_CARD = this.DATA + 'CommentsCard';
		this.RECTIFY_CONTENT = this.DATA + "RectifyContent"
		this.RECTIFY_CONTENT_TABLE = this.RECTIFY_CONTENT + CONSTANT.TABLE.initCap();
		this.RECTIFY_CONTENT_SERIES = this.RECTIFY_CONTENT + CONSTANT.RECTIFY_SERIES.initCap();
		this.RECTIFY_CONTENT_DATE = this.RECTIFY_CONTENT + CONSTANT.RECTIFY_DATE.initCap();
		this.RECTIFY_CONTENT_CAUSE = this.RECTIFY_CONTENT + CONSTANT.RECTIFY_CAUSE.initCap();
		this.FILE = this.id + 'File';
		this.INPUT_FILE = this.id + 'InputFile'
		this.invoice = this.invoice || new Invoice().setType(this.type);

		this.SERIE = CONSTANT.AON_INVOICE + CONSTANT.SERIE.initCap();
		this.SERVICE = CONSTANT.AON_INVOICE + CONSTANT.SERVICE.initCap();
		this.INVESTMENT = CONSTANT.AON_INVOICE + CONSTANT.INVESTMENT.initCap();
		this.RECTIFIER = CONSTANT.AON_INVOICE + CONSTANT.RECTIFIER.initCap();
		this.NUMBER = CONSTANT.AON_INVOICE + CONSTANT.NUMBER.initCap();
		this.REFERENCE = CONSTANT.AON_INVOICE + CONSTANT.REFERENCE.initCap();
		this.DATE = CONSTANT.AON_INVOICE + CONSTANT.DATE.initCap();
		this.EXP_DATE = CONSTANT.AON_INVOICE + "ExpDate";
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
		this.TAX_QUOTA = this.TAX + CONSTANT.QUOTA.initCap();
		this.TAX_DELETE = this.TAX + CONSTANT.DELETE.initCap();
		this.TAX_ADD = this.TAX + CONSTANT.ADD.initCap();

		// ----- DETAIL

		this.DETAIL = CONSTANT.AON_INVOICE_DETAIL;
		this.DETAIL_TABLE = this.DETAIL + CONSTANT.TABLE.initCap();
		this.DETAIL_ADD = this.DETAIL + CONSTANT.ADD.initCap();
		this.DETAIL_BUTTONS = this.DETAIL + CONSTANT.BUTTONS.initCap();
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

		// ----- COMMUNICATION
		this.COMMUNICATION_INFO = CONSTANT.AON_INVOICE_COMMUNICATION_INFO;
		this.COMMUNICATION_INFO_TABLE = this.COMMUNICATION_INFO + CONSTANT.TABLE.initCap();
		this.COMMUNICATION_HISTORY = CONSTANT.AON_INVOICE_COMMUNICATION_HISTORY;
		this.COMMUNICATION_HISTORY_TABLE = this.COMMUNICATION_HISTORY + CONSTANT.TABLE.initCap();

		// ----- FINANCE

		this.FINANCE = CONSTANT.AON_INVOICE_FINANCE;
		this.FINANCE_TABLE = this.FINANCE + CONSTANT.TABLE.initCap();
		this.FINANCE_ADD = this.FINANCE + CONSTANT.ADD.initCap();
		this.FINANCE_DUE_DATE = this.FINANCE + CONSTANT.DUE_DATE.initCap();
		this.FINANCE_PAYMETHOD = this.FINANCE + CONSTANT.PAYMETHOD.initCap();
		this.FINANCE_BANK_ACCOUNT = this.FINANCE + CONSTANT.BANK_ACCOUNT.initCap();
		this.FINANCE_AMOUNT = this.FINANCE + CONSTANT.AMOUNT.initCap();
		this.FINANCE_DELETE = this.FINANCE + CONSTANT.DELETE.initCap();
		this.FINANCE_OPTIONS = this.FINANCE + CONSTANT.OPTIONS.initCap();

		// ------ MESSAGES/ERRRORS
		this.MESSAGES = this.DATA + 'Messages';
		this.ERRORS_CARD = this.DATA + 'ErrorsCard';

		this.RECORD_INVOICE_DIALOG = this.id + 'RecordInvoiceDialog';

		this.FACTURAE = this.id + CONSTANT.FACTURAE.initCap();
		this.FACTURAE_CERTIFICATE = this.FACTURAE + CONSTANT.CERTIFICATE.initCap();
		this.FACTURAE_LEGAL_LITERALS = this.FACTURAE + CONSTANT.LEGAL_LITERALS.initCap();
		this.FACTURAE_PERIOD = this.FACTURAE + CONSTANT.PERIOD.initCap();

		this.SIGN_CERTIFICATE = this.id + "Sign" + CONSTANT.CERTIFICATE.initCap();
	}

	getConfiguration() {
		return this.configuration;
	}

	getCompany() {
		return this.configuration.company;
	}

	getCompanyBanks() {
		return this.configuration.company.banks
			? this.configuration.company.banks.filter(f => f.active)
			: [];
	}

	getTax(id) {
		if (id && this.configuration && this.configuration.taxes && this.configuration.taxes.length > 0) {
			return this.configuration.taxes.find(t => t.id === id);
		}
		return undefined;
	}

	getDefaultWithholdingType() {
		if (this.configuration && this.configuration.defaultRetentionTax) {
			if (this.configuration.taxes && this.configuration.taxes.length > 0) {
				let tax = this.getTax(this.configuration.defaultRetentionTax);
				if (tax) {
					return tax.withholding_type;
				}
			}
		}
		return undefined;
	}

	getInvoice() {
		return this.invoice;
	}

	setInvoice(invoice) {
		this.invoice = new Invoice(invoice);
		if (!invoice)
			this.invoice.setType(this.type);
	}

	setType(type) {
		this.setAttribute('type', type);
	}

	build() {
		let check = this.checkConfiguration() && this.checkSeries();
		if (this.icc.isNoSif() || !check) this.fileOpened = false;
		this.buildOptions();
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

	buildOptions() {
		this.options = [{ title: MSG.GENERAL_DATA, fn: () => this.buildInvoiceContent() }];
		if (this.getInvoice()?.communicationInfo) {
			this.options.push({ title: MSG.COMMUNICATION, fn: () => this.buildCommunication() });
		}
	}

	checkConfiguration() {
		if (!this.getDur().hasScopes()) {
			this.showMessageError("El usuario no tiene ámbitos asignados. Por favor, contacte con el administrador del dominio.");
			return false;
		}
		if (this.getInvoice().isEmitida()) {
			if (!this.icc.hasCommunication() && !this.icc.isNoSif()) {
				this.showMessageError("La configuración de facturación no está completa. Por favor, revise la configuración.");
				return false;
			}

			if (!this.icc.getAdministration().isUnknown() && (this.icc.hasCommunication() || this.icc.willBeCommunication() || this.icc.isNoSif())) {
				return true;
			} else {
				this.showMessageError("La configuración de facturación no está completa. Es obligatorio selecionar una administración para la comunicación electrónica de facturas.");
				return false;
			}
		}
		return true;
	}

	checkRegistry() {
		if (this.invoice.getRegistry().document && this.invoice.getRegistry().document != ""
			&& this.invoice.getRegistry().documentCountry == 'ES' && !isValid(this.invoice.getRegistry().document)) {
			this.showMessageError(this.invoice.isEmitida()
				? "El Documento del Cliente no es válido."
				: "El Documento del Proveedor/Acreedor no es válido.");
			return false;
		}
		return true;
	}

	checkSeries(accept) {
		if (!this.configuration.series || this.configuration.series.length == 0) {
			this.showMessageError("No hay series definidas en la configuración de la empresa. Por favor, contacte con el administrador del dominio.");
			return false;
		}

		let seriesOptions = this.configuration.series.filter(f => this.invoice.isRectifier() ? f.rectification : f.invoice);
		if (this.invoice.isRectifier() && seriesOptions.length == 0) {
			this.showMessageError("No hay series de rectificación definidas en la configuración de la empresa. Por favor, contacte con el administrador del dominio.");
			return false;
		} else if (seriesOptions.length == 0) {
			this.showMessageError("No hay series definidas para el tipo de factura en la configuración de la empresa. Por favor, contacte con el administrador del dominio.");
			return false;
		}

		if (accept && (!this.invoice.series || this.invoice.series == "")) {
			this.showMessageError("No se ha seleccionado la serie de la factura. Por favor, seleccione una serie para continuar.");
			return false;
		}

		return true;
	}

	resize() {
		if (!this.isMobile()) {
			let general = this.getElement(this.GENERAL);
			let generalCard = this.getElement(this.GENERAL_CARD);
			let tax = this.getElement(this.TAX);

			let rightPanelOpen = this.fileOpened || this.invoice.isRejected() || (this.invoice.remarks && this.invoice.remarks.filter(r => r.status == "Rechazado").length > 0);

			if (window.innerWidth && window.innerWidth > 1100 && !rightPanelOpen) {
				if (general) general.style.display = 'flex';
				if (generalCard) generalCard.style.width = '50%';
				if (tax) tax.style.width = '50%';
			} else if (window.innerWidth && window.innerWidth < 1050) {
				if (general) general.style.display = 'block';
				if (generalCard) generalCard.style.width = '100%';
				if (tax) tax.style.width = '100%';
			}

			if (window.innerWidth && window.innerWidth < 900) {
				this.getApplication().closeSidenav();
			}
		}
	}

	reload() {
		this.clear();
		this.build();
	}

	reloadTotal() {
		let totalSpan = this.getElement(this.TOTAL + 'Span');
		if(totalSpan) {
			this.clearElement(totalSpan);
			this.buildTotal(totalSpan);
		}
	}

	reloadTaxes() {
		let taxesTable = this.getElement(this.TAX_TABLE2);
		if (taxesTable) {
			taxesTable.removeRows();
			this.buildTaxes(taxesTable);
		}
		let taxAddButton = this.getElement(this.TAX_ADD);
		if(taxAddButton && (this.invoice.isReadonly() || this.invoice.details.length > 0)) {
			taxAddButton.remove();
		}
	}

	reloadFinances() {
		let table = this.getElement(this.FINANCE_TABLE);
		if (table) {
			table.removeRows();
			this.buildFinances(table);			
		}
	}

	buildTotal(parent) {
		let total = this.createAonNumber(this.TOTAL, MSG.TOTAL, this.invoice.total);
		total.readonly = this.invoice.isReadonly()
			|| this.invoice.taxes.filter(f => TaxType.IVA === f.tax).length > 1
			|| this.invoice.details.length > 0;
		if (LS.isNewTheme() && (this.invoice.taxes.filter(f => TaxType.IVA === f.tax).length > 1 || this.invoice.details.length > 0))
			total.disabled = CONSTANT.TRUE;
		parent.appendChild(total);
		if (!LS.isNewTheme() && (this.invoice.taxes.filter(f => TaxType.IVA === f.tax).length > 1 || this.invoice.details.length > 0))
			total.disabled = CONSTANT.TRUE;
		total.onChange(() => this.onChangeInvoiceTotal(total.value));	
	}

	setFocus(focusId) {
		this.focusId = focusId;
	}

	focus() {
		if (this.focusId) {
			let focusElement = this.getElement(this.focusId);
			if (focusElement) focusElement.focus();
		}
	}

	buildInputFile() {
		let inputFile = this.createElement(TAG.INPUT);
		inputFile.id = this.INPUT_FILE;
		inputFile.style.display = 'none';
		inputFile.type = 'file';
		inputFile.name = 'file';
		this.appendChild(inputFile);
		inputFile.addEventListener('change', ({ target }) => this.preview());
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

	attach(fileDataUri, mimetype) {
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
		invoiceToolbar.title = this.getInvoiceTitle()
		this.appendChild(invoiceToolbar);
		invoiceToolbar.removeButtons();
		invoiceToolbar.addButton2(ACTION.NEXT, () => this.nextInvoice());
		invoiceToolbar.addButton2(ACTION.PREVIOUS, () => this.previousInvoice());
		invoiceToolbar.addSeparator();

		if (!this.getInvoice().isTrash() && !this.getInvoice().isRejected()) {
			invoiceToolbar.addButton('Options', 'more_vert', (e) => {
				e.preventDefault();
				let rect = e.target.getBoundingClientRect();
				let x = e.clientX - rect.left;
				let y = e.clientY - rect.top;

				const top = rect.top + y;
				const left = rect.left + x;

				let d = document.getElementById(this.getApplication().OPTION_DIALOG);
				let moreActions = [];
				if (this.getInvoice().isInbox()) {
					let comment = ACTION.COMMENT;
					comment.permission = true;
					comment.backgroundColor = INVOICE.color;
					comment.fn = () => this.addInvoiceComment();
					moreActions.push(comment);
				}

				if(this.isBeta() && !this.getInvoice().amortization && this.getInvoice().isRawdoc() && !this.getInvoice().isEmitida()) {
					let inmobilized = ACTION.ADD_INMOBILIZED;
					inmobilized.permission = true;
					inmobilized.backgroundColor = INVOICE.color;
					inmobilized.fn = () => this.buildInmobilized();
					moreActions.push(inmobilized);
				}

				if (this.getInvoice().isEmitida()) {
					let send = ACTION.SEND_INVOICE;
					send.permission = true;
					send.backgroundColor = INVOICE.color;
					send.fn = () => this.sendInvoice();
					moreActions.push(send);
				}

				if (!this.getInvoice().isRawdoc()
					&& !this.getInvoice().isRectified()
					&& !this.getInvoice().isRectifier()
					&& !this.getInvoice().isAccountSource()) {

					let rectify = ACTION.RECTIFY_INVOICE;
					rectify.permission = true;
					rectify.backgroundColor = INVOICE.color;
					rectify.fn = () => this.rectifyInvoice();
					moreActions.push(rectify);
				}

				if (!this.getInvoice().isRectifier()) {
					let duplicate = ACTION.DUPLICATE_INVOICE;
					duplicate.permission = true;
					duplicate.backgroundColor = INVOICE.color;
					duplicate.fn = () => this.duplicateInvoice();
					moreActions.push(duplicate);
				}

				if (this.getInvoice().isProcessed()) {
					let changeType = ACTION.CHANGE_TYPE;
					changeType.permission = true;
					changeType.backgroundColor = INVOICE.color;
					changeType.fn = () => this.changeType();
					moreActions.push(changeType);
				}
				if (!this.getInvoice().isProforma() && this.getInvoice().isEmitida()) {
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

		if (this.showRejected()) invoiceToolbar.addButton2(ACTION.REJECT, () => this.rejectInvoice());
		if (this.showDelete()) invoiceToolbar.addButton2(ACTION.DELETE, () => this.getInvoice().isPending() ? this.trashPendingInvoice() : this.trashInvoice());
		if (this.showDeleteForever()) invoiceToolbar.addButton2(ACTION.DELETE_FOREVER, () => this.removeInvoice());
		if (this.showRestore()) invoiceToolbar.addButton2(ACTION.RESTORE, () => this.restoreInvoice());
		if (this.showAccept()) invoiceToolbar.addButton2(ACTION.ACCEPT, () => this.acceptInvoice());
		if (this.showSave()) invoiceToolbar.addButton2(ACTION.SAVE, () => this.save());

		invoiceToolbar.addButton2(ACTION.BACK, () => this.back());

		if ((this.getInvoice().file
			&& !((this.icc.isNoSif() || !this.checkConfiguration()) && this.getInvoice().file.path && this.getInvoice().file.path.includes('download_invoice_pdf')))
			|| (!this.icc.isNoSif() && this.invoice.isEmitida())) {
			invoiceToolbar.addButtonTitle(ACTION.SHOW_FILE, () => this.showFile(true));
		} else if (!this.invoice.isEmitida() && this.invoice.isInbox()) {
			invoiceToolbar.addButtonTitle(ACTION.ADD_FILE, () => this.addInvoiceFile());
		}
		if(this.invoice.isRawdoc() || (!this.invoice.isRawdoc() && this.invoice.hasRemarks())) 
			invoiceToolbar.addButtonTitle(ACTION.COMMENT, () => this.showLog());
		if(this.invoice.amortization) invoiceToolbar.addButtonTitle(ACTION.AMORTIZATION, () => this.buildInmobilized(this.invoice.amortization));
		this.buildCommunicationToolbar(invoiceToolbar);
	}

	showAccept() {
		return (this.icc.hasCommunication() || !this.getInvoice().isEmitida()) &&
			(this.getInvoice().isInbox() || (this.getInvoice().isProcessed() && !this.getInvoice().isEmitida()))
			&& ((this.getInvoice().isInbox() && this.getInvoice().isEmitida() && !this.getInvoice().file)
				|| !this.getInvoice().isEmitida())
			;
	}

	showDelete() {
		return !this.getInvoice().isFeeSource()
			&& (this.getInvoice().isRejected() || this.getInvoice().isInbox() || this.getInvoice().isPending());
	}

	showDeleteForever() {
		return this.getInvoice().isTrash();
	}

	showRejected() {
		return (this.getInvoice().isInbox() || this.getInvoice().isProcessed()) && this.getDur().isInvoiceManager();
	}

	showRestore() {
		return this.getInvoice().isRejected() || this.getInvoice().isTrash();
	}
	showSave() {
		return this.getInvoice().isInbox() || this.getInvoice().isProcessed();
	}

	buildCommunicationToolbar(invoiceToolbar) {
		if (this.icc.isVerifactuTest()) {
			let vaction = {
				id: 'Communication_verifactu_test',
				name: "VERIFACTUENTORNOTEST",
				title: "VERIFACTU ENTORNO TEST",
				icon: MATERIAL_ICONS.WARNING
			};
			let vtb = invoiceToolbar.addButtonTitle(vaction, () => window.alert("VERIFACTU ENTORNO TEST"));
			vtb.getButton().style.color = "red";
		}

		if (this.hasCommunicationInfo()) {
			let ci = this.getInvoice().communicationInfo;
			let keys = Object.keys(ci ?? {});
			for (let i = 0; i < keys.length; i++) {
				let key = keys[i];
				let communicationStatus = ci[key].communicationStatus;
				let checkURL = ci[key].checkUrl;
				let fn = (checkURL)
					? () => { if(checkURL) open(checkURL); }
					: undefined;
				let action = {
					id: 'Communication_' + key,
					name: key + " " + this.getCommunicationStatusLabel(communicationStatus),
					title: key + " " + this.getCommunicationStatusLabel(communicationStatus),
					icon: MATERIAL_ICONS.QR_CODE_2
				};
				let aib = invoiceToolbar.addButtonTitle(action, fn);
				if (aib && communicationStatus) {
					aib.getButton().style.color = this.getCommunicationStatusColor(communicationStatus);
				}
			}
		}
	}

	getCommunicationStatusLabel(status) {
		if ("PENDING" === status) return "Pendiente";
		else if ("ACCEPTED" === status) return "Aceptada";
		else if ("ACCEPTED_WITH_ERRORS" === status) return "Aceptada con errores";
		else if ("EXTERNALLY_COMMUNICATED" === status) return "Com. Externamente";
		else if ("WRONG" === status) return "Incorrecta";
		else return "Sin Estado";
	}

	getCommunicationStatusColor(status) {
		if ("PENDING" === status) return "orange";
		else if ("ACCEPTED" === status) return "green";
		else if ("ACCEPTED_WITH_ERRORS" === status) return "yellow";
		else if ("EXTERNALLY_COMMUNICATED" === status) return "blue";
		else if ("WRONG" === status) return "red"
		else return "gray";
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

		if(this.invoice.isRejected() || (this.invoice.remarks && this.invoice.remarks.filter(r => r.status == "Rechazado").length > 0)) {
			this.showLog();
		} else if (this.fileOpened) {
			this.showFile(false);
		}
	}

	hasCommunicationInfo() {
		return this.getInvoice()
			&& this.icc.hasCommunicationByType(this.getInvoice().type)
			&& this.getInvoice().communicationInfo
			&& Object.keys(this.getInvoice().communicationInfo).length > 0;
	}

	buildTabs(data) {
		if (!this.invoice.isRawdoc()) {
			let tab = new AonTab();
			tab.id = this.TABS;
			tab.setOptions(this.options);
			data.appendChild(tab);
		}
	}

	buildInvoiceContent() {
		let data = this.getElement(this.DATA);
		let content = this.getElement(this.CONTENT);
		if (!content) {
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
		if (LS.isNewTheme()) {
			this.showFieldsMessages(content);
		}
	}

	buildAmortization() {
		let content = this.getElement(this.CONTENT);
		this.clearElement(content);

		let amortization = this.createElement(TAG.DIV);
		amortization.id = this.AMORTIZATION;
		amortization.className = CSS.AON_BLOCK;
		let jsfInvoiceAmortization = new JSF.AonJsfInvoiceAmortization();
		jsfInvoiceAmortization.setElExpression(`expenseInvoice.select(null, (${this.invoice.id}).intValue() )`);
		amortization.appendChild(jsfInvoiceAmortization);
		content.appendChild(amortization);

	}

	buildCommunication() {
		let content = this.getElement(this.CONTENT);
		this.clearElement(content);

		let communication = new AonInvoiceCommunication();
		communication.invoice = this.getInvoice().id;
		content.appendChild(communication);
	}

	buildFileContent() {
		let div = this.getElement(this.DIV);
		let file = this.createElement(TAG.DIV);
		file.id = this.FILE;
		file.className = CSS.AON_SUB_CONTENT;
		div.appendChild(file);
	}

	buildCommentCard(parent) {
		let commentsDiv = this.createElement(TAG.DIV);
		commentsDiv.id = this.COMMENTS;
		commentsDiv.className = CSS.AON_FLEX;
		parent.appendChild(commentsDiv);

		let hasComment = this.invoice.comments && this.invoice.comments != undefined && this.invoice.comments != '';


		let commentsCard = new AonCard();
		commentsCard.id = this.COMMENT_CARD;
		commentsCard.title = MSG.COMMENT;
		commentsCard.style.width = '100%';
		if (!hasComment) commentsCard.className = CSS.AON_NONE;
		commentsDiv.appendChild(commentsCard);

		commentsCard.setContentHTML('');
		commentsCard.setBackground('#f5f5f5');

		if (hasComment) {
			let div = this.createElement(TAG.DIV);
			div.id = 'commentsLinesDiv';
			commentsCard.setContent(div);
			let strs = '';
			if (this.isString(this.invoice.comments)) {
				strs = this.invoice.comments + '';
			} else {
				strs = JSON.stringify(this.invoice.comments);
			}
			strs.split('\n').forEach(str => {
				let span = this.createElement(TAG.SPAN);
				span.textContent = str;
				div.appendChild(span);
				div.appendChild(this.createElement('br'));
			});
		}
	}

	showFieldsMessages(parent) {

		let getText = (err) => {
			switch (err.code) {
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

		let getMessageHTML = (err) => {
			let span = this.createElement(TAG.SPAN)
			span.style.fontSize = "12px";
			span.textContent = getText(err);
			return span.outerHTML;

		};

		if (this.invoice.messages) {
			this.invoice.messages
				.filter(err => err.context)
				//.filter( err => !err.context.line )
				.forEach((err, i) => {
					try {
						switch (err.context.key) {
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
								this.getElement(`${this.TAX_PERCENTAGE}${err.context.line}`).addError(getMessageHTML(err));
								break;
							case ErrKey.TAX_BASE:
								this.getElement(`${this.TAX_BASE}${err.context.line}`).addError(getMessageHTML(err));
								break;
							case ErrKey.TAX_QUOTA:
								this.getElement(`${this.TAX_QUOTA}${err.context.line}`).addError(getMessageHTML(err));
								break;
							case ErrKey.IRPF_RATE: {
								let line = this.getElement(this.TAX_TABLE2).rows - 1;
								this.getElement(`${this.TAX_PERCENTAGE}${line}`).addError(getMessageHTML(err));
								break;
							}
							case ErrKey.IRPF_BASE: {
								let line = this.getElement(this.TAX_TABLE2).rows - 1;
								this.getElement(`${this.TAX_BASE}${line}`).addError(getMessageHTML(err));
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
							case ErrKey.RDOCUMENT: {
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
					} catch (e) {
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
		if (!hasErrors) {
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
			errorDiv.className = className;
			errorDiv.className += " " + CSS.AON_FLEX;
			errorDiv.className += " " + CSS.FLEX_ALIGN_CENTER;

			let iconSpan = this.createElement(TAG.SPAN);
			iconSpan.className = CSS.MATERIAL_ICONS;
			iconSpan.className += " " + CSS.AON_INPUT_MSG_ERROR;
			iconSpan.textContent = MATERIAL_ICONS.WARNING;
			iconSpan.style.color = className === CSS.AON_INVOICE_ERROR ? "#e83151" : "#e3a733";
			errorDiv.appendChild(iconSpan);

			let spaceSpan = this.createElement(TAG.SPAN);
			spaceSpan.style.width = "16px";
			errorDiv.appendChild(spaceSpan);

			let descriptionSpan = this.createElement(TAG.SPAN);
			descriptionSpan.textContent = description;
			errorDiv.appendChild(descriptionSpan);

			return errorDiv;
		};

		let createViewDiv = (viewMessage, hideMessage, errors) => {
			let viewDiv = this.createElement(TAG.DIV);

			let viewButtonSpan = this.createElement(TAG.SPAN);
			viewButtonSpan.textContent = viewMessage;
			let hideButtonSpan = this.createElement(TAG.SPAN);
			hideButtonSpan.textContent = hideMessage;

			let errorsDiv = this.createElement(TAG.DIV);
			let errorsUl = this.createElement(TAG.UL);
			errors.forEach((error, i) => {
				let errorLi = this.createElement(TAG.LI);
				let errorDiv = this.createElement(TAG.DIV);
				let errorSpan = this.createElement(TAG.SPAN);
				errorSpan.textContent = error.message;
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
			viewButtonSpan.onclick = function () {
				viewButtonSpan.style.display = "none";
				errorsDiv.style.removeProperty("display");
				hideButtonSpan.style.removeProperty("display");
			};
			hideButtonSpan.style.cursor = "pointer";
			hideButtonSpan.onclick = function () {
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

		let createErrorsDiv = (titleMessage, viewMessage, hideMessage, className, errors) => {

			if (errors.length > 1) {
				let li = this.createElement(TAG.LI);
				li.style.backgrounColor = 'transparent !important';
				let errorDiv = createErrorDiv(errors.length + " " + titleMessage, className);
				let viewDiv = createViewDiv(viewMessage, hideMessage, errors);
				li.appendChild(errorDiv);
				li.appendChild(viewDiv);
				ul.appendChild(li);

				errorDiv.style.paddingLeft = "3px";
				viewDiv.style.paddingLeft = '48px';
				viewDiv.style.paddingBottom = "12px";

			} else if (errors.length == 1) {
				errors.forEach((error, i) => {
					let li = this.createElement(TAG.LI);
					li.style.backgrounColor = 'transparent !important';
					let errorDiv = createErrorDiv(error.message, className);
					li.appendChild(errorDiv);
					ul.appendChild(li);

					errorDiv.style.paddingLeft = "3px";
					errorDiv.style.paddingBottom = "12px";

				});
			}

		}

		let customizeErrorsCards = (errorsCard, errors) => {
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
			expandSpan.textContent = MATERIAL_ICONS.ARROW_DROP_DOWN;
			expandSpan.onclick = function () { };
			let collapseSpan = this.createElement(TAG.SPAN);
			collapseSpan.style.cursor = "pointer";
			collapseSpan.className = CSS.MATERIAL_ICONS;
			collapseSpan.textContent = MATERIAL_ICONS.ARROW_DROP_UP;

			let iconSpan = this.createElement(TAG.SPAN);
			iconSpan.className = CSS.MATERIAL_ICONS;
			iconSpan.textContent = MATERIAL_ICONS.WARNING;
			let messageSpan = this.createElement(TAG.SPAN);
			messageSpan.textContent = errors + " " + (errors > 1 ? MSG.ERRORS.toLowerCase() : MSG.ERRORS.substring(0, MSG.ERRORS.length - 2).toLowerCase());
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
			collapseSpan.onclick = function () {
				contentCard.style.display = "none";
				collapseSpan.style.display = "none";
				expandSpan.style.removeProperty("display");
			};
			expandSpan.onclick = function () {
				contentCard.style.display = "block";
				expandSpan.style.display = "none";
				collapseSpan.style.removeProperty("display");
			};
			collapseSpan.onclick();

		}

		let emptyValueErrors =
			this.invoice.messages.filter(err => err.code === ErrCode.ERR_EMPTY_VALUE);
		createErrorsDiv(MSG.ERR_EMPTY_VALUE, MSG.VIEW_FIELDS, MSG.HIDE_FIELDS, CSS.AON_INVOICE_ERROR, emptyValueErrors)

		let lowConfidenceErrors =
			this.invoice.messages.filter(err => err.code === ErrCode.ERR_LOW_CONFIDENCE);
		createErrorsDiv(MSG.ERR_LOW_CONFIDENCE, MSG.VIEW_FIELDS, MSG.HIDE_FIELDS, CSS.AON_INVOICE_WARNING, lowConfidenceErrors)

		let otherErrors =
			this.invoice.messages.filter(err => !emptyValueErrors.includes(err) && !lowConfidenceErrors.includes(err));
		otherErrors.forEach((item, i) => {
			let li = this.createElement(TAG.LI);
			li.style.backgrounColor = 'transparent !important';

			let errorDiv = createErrorDiv(item.message, CSS.AON_INVOICE_ERROR);
			li.appendChild(errorDiv);
			ul.appendChild(li);
		});

		let errorsCount = this.invoice.messages.length;

		customizeErrorsCards(errorsCard, errorsCount);
	}

	onChangeInvoiceTotal(value) {
		this.invoice.setTotal(value);
		this.reload();
	}

	buildGeneralCard(parent) {
		let dn = "";
		if (!this.isInvofoxInvoice() && !this.invoice.isRawdoc()) {
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
			let button = this.getElement(card.TITLE_SECTION2 + MSG.OPTIONS + 'Button');

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

			let rectifier = new AonSwitch();
			rectifier.id = this.RECTIFIER;
			rectifier.title = MSG.RECTIFIER;
			rectifier.readonly = this.invoice.isReadonly();
			div.appendChild(rectifier);
			rectifier.addEventListener(EVENT.CHANGE, () => {
				if(rectifier.isChecked()) {
					this.buildRectifedInvoiceDialog();
				} else {
					this.invoice.setRectifier(false);
					this.invoice.rectificationInvoice = undefined;
				}
			});
			rectifier.checked = this.invoice.isRectifier();

			const top = button.getBoundingClientRect().top;
			const left = button.getBoundingClientRect().left;
			dialog.setContent(div, top, left);
			dialog.open();

			service.setWidth('150px');
			service.setMarginBottom('10px');

			investment.setWidth('150px');
			investment.setMarginBottom('10px');

			rectifier.setWidth('150px');
			rectifier.setMarginBottom('10px');
		});

		let table = new AonBasicTable();
		table.id = this.GENERAL_CARD_TABLE;
		card.setContent(table);

		table.addRow(); // ----- ROW 1

		// ----- REGISTRY
		if (this.invoice.isEmitida()) {
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
		}

		if (this.invoice.isRawdoc() && this.invoice.getRegistry().documentCountry == 'ES'
			&& !isValid(this.invoice.getRegistry().document) && this.invoice.getRegistry().id) {
			let data = {
				id: this.invoice.getRegistry().id,
				additional_info: []
			};
			getRegistry(data).then(r => {
				if (isValid(r.document)) {
					this.invoice.getRegistry().document = r.document;
				}
			});
		}

		table.addRow(); // ----- ROW 2

		let div = this.createDiv();
		div.className = CSS.AON_FLEX;
		table.addCell(div, '4');

		// ----- SERIE

		let serieSpan = this.createTableSpan("20%", "2px");
		div.appendChild(serieSpan);
		let seriesOptions = this.configuration.series.filter(f => this.invoice.isRectifier() ? f.rectification : f.invoice);
		if(!this.invoice.isRectifier() && (!this.invoice.series || this.invoice.series == '') && this.configuration.defaultSeries) {
			this.invoice.series = this.configuration.defaultSeries;
		}
		if (!seriesOptions.map(o => o.code).includes(this.invoice.series)) {
			this.invoice.setSeries(seriesOptions.length > 0 ? seriesOptions[0].code : undefined);
		}
		let serie = createSelect(this.SERIE, MSG.SERIE);
		serie.setOptions(seriesOptions);
		serie.setAlias("code", "code");
		serie.setValue(this.invoice.series);
		serieSpan.appendChild(serie);
		serie.readonly = this.invoice.isReadonly();
		serie.addEventListener(EVENT.SELECT, () => this.onChangeSerie(serie.value));

		// ----- NUMBER

		let numberSpan = this.createTableSpan("30%", "2px");
		div.appendChild(numberSpan);

		let number = createInput(this.NUMBER, MSG.NUMBER);
		number.value = "PROFORMA";
		number.readonly = CONSTANT.READONLY;
		number.disabled = CONSTANT.TRUE;
		numberSpan.appendChild(number);

		// ----- REFERENCE
		let referenceWidth = !this.invoice.isRawdoc() && this.invoice.isEmitida() ? "25%" : "45%";
		let referenceSpan = this.createTableSpan(referenceWidth, "2px");
		div.appendChild(referenceSpan);

		let reference = createInput(this.REFERENCE, MSG.REFERENCE);
		reference.value = this.invoice.reference;
		reference.readonly = this.invoice.isReadonly();
		reference.addEventListener(EVENT.CHANGE, () => {
			this.invoice.setReference(reference.value);
		});
		referenceSpan.appendChild(reference);

		if (this.invoice.isRawdoc() && this.invoice.isEmitida() && !this.isInvofoxInvoice()) {
			serieSpan.style.display = 'block';
			numberSpan.style.display = 'block';
			referenceSpan.style.display = 'none';
		} else {
			if (this.isInvofoxInvoice()) this.getInvoice().number = undefined;
			serieSpan.style.display = 'none';
			numberSpan.style.display = 'none';
			referenceSpan.style.display = 'block';
		}

		// ----- DATE

		let dateWidth = !this.invoice.isRawdoc() && this.invoice.isEmitida() ? "25%" : "30%";
		let dateSpan = this.createTableSpan(dateWidth, "2px");
		div.appendChild(dateSpan);

		let date = createDate(this.DATE, MSG.OPERATION_DATE);
		date.setDate(this.invoice.date);
		if (this.invoice.isReadonly()) date.readonly = this.invoice.isReadonly();
		date.addEventListener(EVENT.CHANGE, () => {
			this.invoice.setDate(date.getDateValue());
		});
		dateSpan.appendChild(date);

		// ----- EXP DATE
		if(!this.invoice.isRawdoc() && this.invoice.isEmitida()) {
			let expDateSpan = this.createTableSpan("25%", "2px");
			div.appendChild(expDateSpan);

			let expDate = createDate(this.EXP_DATE, MSG.EXPEDITION_DATE);
			expDate.setDate(this.invoice.expDate);
			expDate.readonly = true;
			expDateSpan.appendChild(expDate);
		}

		// ----- TOTAL

		let totalSpan = this.createTableSpan("25%", "0px");
		totalSpan.id = this.TOTAL + 'Span'; 
		div.appendChild(totalSpan);
		this.buildTotal(totalSpan);

		table.addRow(); // ----- ROW 3

		// ----- CATEGORY
		if (this.getDur().hasAccounting() || this.getDur().hasParentAccounting()) {
			let category = createSelect(this.CATEGORY, MSG.CATEGORY);
			category.autocomplete = true;
			category.readonly = this.invoice.isReadonly();
			category.addEventListener(EVENT.SELECT, () => {
				this.invoice.setCategory(category.value);
			});
			table.addCell(category, this.invoice.isEmitida() ? '4' : '6');
			getInvoiceAccounts({ type: this.invoice.getInvoiceType() }).then(accounts => {
				let accs = accounts.map(acc => { return { name: acc.description, value: acc.code }; });
				category.options = JSON.stringify(accs);
				category.value = this.invoice.getCategory();
			});
		}

		// if(!this.invoice.workplace && this.configuration.workplaces.length > 0) {
		// 	this.invoice.setWorkplace(this.configuration.workplaces[0].id);
		// } 
		if (!this.invoice.workplace) {
			const activeWorkplace = this.configuration.workplaces.find(w => w.active);
			if (activeWorkplace) {
				this.invoice.setWorkplace(activeWorkplace.id);
			}
		}

		if (this.configuration.workplaces) {
			if (this.invoice.isReadonly()) {
				table.addRow();
				let workplace = createInput(this.WORKPLACE, MSG.WORKPLACE);
				workplace.readonly = this.invoice.isReadonly();
				const selectedWorkplace = this.configuration.workplaces.find(w => w.id == this.invoice.getWorkplace())
				workplace.value = selectedWorkplace?.description;
				table.addCell(workplace, this.invoice.isEmitida() ? '4' : '6');
			} else {
				let wps = this.configuration.workplaces.filter(w => w.active);
				if (wps) {
					if (wps.length === 1) {
						this.invoice.setWorkplace(wps[0].id);
					}
					if (wps.length > 1) {
						table.addRow();
						let workplace = createSelect(this.WORKPLACE, MSG.WORKPLACE);
						workplace.setAlias("id", "description");
						workplace.autocomplete = true;
						workplace.readonly = this.invoice.isReadonly();
						if (this.invoice.getWorkplace()) {
							let tempwp = wps.filter(w => w.id == this.invoice.getWorkplace());
							if (tempwp && tempwp.length === 1) {
								workplace.value = tempwp[0].id;
							}
						}
						if (!workplace.value) {
							workplace.value = wps[0].id;
						}
						workplace.setOptions(wps);
						workplace.addEventListener(EVENT.SELECT, () => {
							this.invoice.setWorkplace(workplace.value);
						});
						table.addCell(workplace, this.invoice.isEmitida() ? '4' : '6');
					}
				}
			}

		}
	}

	onChangeSerie(value) {
		this.invoice.setSeries(value);
		if (this.invoice.isInbox()) {
			this.invoice.number = undefined;
		}
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
			let button = this.getElement(card.TITLE_SECTION2 + MSG.OPTIONS + 'Button');

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
			});
			div.appendChild(farmer);
			if (this.invoice.isEmitida() && !this.invoice.isNacional()) {
				if (this.invoice.isWithholdingFarmer()) {
					this.invoice.setWithholdingFarmer(false);
				}
				farmer.setDisabled(true);
			}
			farmer.checked = this.invoice.isWithholdingFarmer();

			const top = button.getBoundingClientRect().top;
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
		if (!card) card = this.getElement(this.TAX);
		let table = this.getElement(this.TAX_TABLE);
		if (!table) {
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

		let transaction = createSelect(this.TRANSACTION_TYPE, MSG.TRANSACTION_TYPE);
		transaction.options = JSON.stringify(Transactions);
		transaction.value = this.invoice.transaction;
		transaction.readonly = this.invoice.isReadonly();
		transaction.addEventListener(EVENT.SELECT, () => {
			this.invoice.setTransaction(transaction.value);
			this.setFocus(transaction.id);
			this.reload();
		});
		transactionSpan.appendChild(transaction);

		// ----- ACTIVITY TYPE
		let activitySpan = this.createTableSpan("50%", "0px");
		div.appendChild(activitySpan);

		let activity = createSelect(this.ACTIVITY, MSG.ACTIVITY);
		activity.readonly = this.invoice.isReadonly();
		activity.setAlias("id", "description");
		activity.addEventListener(EVENT.SELECT, () => {
			this.invoice.setActivity(activity.getValueObject());
			this.setFocus(activity.id);
			this.reload();
		});
		activitySpan.appendChild(activity);
		getCompanyActivities({}).then(activities => {
			if (activities.length > 0) {
				if (!this.invoice.activity) this.invoice.setActivity(this.invoice.getActivity() || activities[0]);
				activity.setOptions(activities);
				activity.value = this.invoice.getActivity().id;
			}
		});

		// ----- TAXES

		let taxesTable = this.getElement(this.TAX_TABLE2);
		if (!taxesTable) {
			taxesTable = new AonBasicTable();
			taxesTable.id = this.TAX_TABLE2;
			card.addContent(taxesTable);
		}
		taxesTable.removeRows();

		this.buildTaxes(taxesTable);

		// ----- WITHHOLDING
		let irpfTableId = 'irpfTable';
		let irpfTable = this.getElement(irpfTableId);
		if (!irpfTable) {
			irpfTable = new AonBasicTable();
			irpfTable.id = 'irpfTable';
			card.addContent(irpfTable);
		}
		irpfTable.removeRows();
		irpfTable.addRow();

		if (!this.invoice.isReadonly() && this.invoice.details.length === 0) {
			// ----- ADD TAX

			let addButton = new AonIconButton();
			addButton.id = this.TAX_ADD;
			addButton.title = MSG.ADD_TAX;
			addButton.icon = MATERIAL_ICONS.ADD;
			addButton.className = CSS.AON_ICON_BUTTON_TOP;

			addButton.addEventListener('click', () => {
				if (this.invoice.isVatEnabled()) {
					this.setFocus(this.TAX_TYPE + this.invoice.taxes.length);
					this.invoice.addTax();
					this.reload();
				}
			});
			irpfTable.addCell(addButton);
			addButton.setDisabled(!this.invoice.isVatEnabled());
		}

		let irpf = new AonSwitch();
		irpf.id = this.WITHHOLDING;
		irpf.className = CSS.AON_SWITCH_TOP;
		irpf.title = MSG.IRPF; // MSG.WITHHOLDING;

		irpf.readonly = this.invoice.isReadonly() || this.invoice.details.length > 0
			|| this.invoice.isRectifier();
		irpf.addEventListener(EVENT.CHANGE, () => {
			this.invoice.setWithholding(irpf.checked, this.getDefaultWithholdingType());
			this.reload();
		});
		irpfTable.addCell(irpf, '1');
		if ((!this.invoice.isNacional() && !this.invoice.isCcm()) || this.invoice.isRectifier()) {
			irpf.setDisabled(true);
		}
		if (this.invoice.isReadonly()) irpf.setDisabled(true);
		irpf.checked = this.invoice.taxes.filter(r => TaxType.IRPF === r.type || TaxType.IRPF === r.tax).length > 0;

		let irpfType = createSelect('irpfwithholdingTYpe', 'Tipo IRPF');
		irpfType.setAlias('id', 'name');
		if (this.invoice.taxes.filter(r => TaxType.IRPF === r.type || TaxType.IRPF === r.tax).length === 0) {
			irpfType.disabled = 'true';
		}
		irpfType.setOptions(WithholdingType);

		irpfType.addEventListener(EVENT.SELECT, () => {
			let detail = WithholdingType.find(v => v.id == irpfType.value);
			this.invoice.withholding = true;
			this.invoice.setWithholdingType(detail);
			this.reload();
		});
		irpfTable.addCell(irpfType, '3');
		irpfType.readonly = this.invoice.isReadonly();
		if (this.invoice.taxes.filter(r => TaxType.IRPF === r.type || TaxType.IRPF === r.tax).length > 0) {
			let val = this.invoice.taxes.filter(r => TaxType.IRPF === r.type || TaxType.IRPF === r.tax)[0].withholding_type;
			irpfType.value = val || this.getDefaultWithholdingType();
		}
	}

	buildTaxes(table) {
		if (this.invoice.isVatEnabled()) {
			for (let i = 0; i < this.invoice.taxes.length; i++) {
				let tax = this.invoice.taxes[i];
				if (TaxType.IVA === tax.tax)
					this.printTax(table, tax, i);
			}
		}
		if (this.invoice.isNacional() || this.invoice.isCcm()) {
			for (let i = 0; i < this.invoice.taxes.length; i++) {
				let tax = this.invoice.taxes[i];
				if (TaxType.IRPF === tax.tax) {
					this.invoice.withholding = true;
					this.printTax(table, tax, i);
				}
			}
		}
	}

	getBank(invoice, registry) {
		let firstIban;
		let pm = registry.paymethod.paymethod;
		if ((invoice.isEmitida() && pm.type === 'NEGOTIABLE_DOCUMENT') || (!invoice.isEmitida() && pm.type === 'BANK_TRANSFER')) {
			firstIban = registry.banks && registry.banks.length > 0 ? registry.banks[0].bank_account : "";
		} else if ((!invoice.isEmitida() && pm.type === 'NEGOTIABLE_DOCUMENT') || (invoice.isEmitida() && pm.type === 'BANK_TRANSFER')) {
			let banks = this.getCompanyBanks();
			firstIban = banks && banks.length > 0
				? banks[0] : undefined;
		}
		return registry.paymethod && registry.paymethod.bank
			? registry.paymethod.bank : firstIban;
	}

	async onChangeRegistry(registry) {
		if (registry && registry.id) {
			this.invoice.setRegistry(registry);
			if (registry.paymethod) {
				let pm = registry.paymethod.paymethod;
				this.invoice.setPaymethod(pm.id);
				let bank = this.getBank(this.invoice, registry)
				let finance = {
					due_date: this.invoice.date,
					paymethod: this.invoice.paymethod,
					amount: this.invoice.total,
					bank_account: bank ? bank.bank_account : "",
					bankAlias: bank ? bank.alias : "",
					bic: bank ? bank.bic : ""
				};
				this.invoice.finances = [finance];
			}
			if (registry.transaction) this.invoice.setTransaction(registry.transaction);
			else {
				let transactionJSON = await getSupplierTransaction({ id: registry.id })
				if (!transactionJSON.transaction)
					transactionJSON = await getCreditorTransaction({ id: registry.id })
				if (transactionJSON.transaction)
					this.invoice.setTransaction(transactionJSON.transaction);
			}

			if (registry.withholding) this.invoice.setWithholding(registry.withholding);
			if (registry.surcharge) this.invoice.setSurcharge(registry.surcharge);

			let data = {
				registry: this.invoice.getRegistry().id,
				type: this.invoice.type
			};
			if (this.getDur().hasAccounting() || this.getDur().hasParentAccounting()) {
				getRegistrySuggestedAccount(data).then(r => {
					this.invoice.setCategory(r.code);
					this.getElement(this.CATEGORY).value = r.code;
				});
			}
			this.getElement(this.TOTAL).value = this.invoice.getTotal();
			this.buildTaxCardContent();
			this.buildDetailCard();
			this.buildFinanceCard();
		}
	}

	onChangeTaxBase(tax, value, i) {
		tax.base = value;
		this.invoice.setTax(tax, i);
		this.reload();
	}

	printTax(taxesTable, tax, i) {
		taxesTable.addRow(); // ----- ROW i

		// ----- TAX PERCENT

		tax.type = tax.type || tax.tax;
		let administration = this.configuration ? this.configuration.administration : '';
		let percentage = createSelect(this.TAX_PERCENTAGE + i, '% ' + getTaxTypeName(tax.type, this.isMobile(), administration));
		percentage.options = JSON.stringify(getTaxPercentageOption(tax.type, administration, this.invoice.isSurcharge(), this.getInvoice().isWithholdingFarmer()));
		percentage.addEventListener(EVENT.SELECT, () => {
			tax.percentage = percentage.value;
			tax.type = getTaxType(tax.percentage);
			this.invoice.setTax(tax, i);
			this.reload();
		});
		taxesTable.addCell(percentage);
		percentage.readonly = this.invoice.isReadonly()
			|| this.invoice.details.length > 0
			|| tax.type.includes('IRPF');

		if (this.invoice.details.length > 0 || tax.type.includes('IRPF'))
			percentage.disabled = CONSTANT.TRUE;

		percentage.value = tax.percentage;

		// ----- TAX BASE

		let base = this.createAonNumber(this.TAX_BASE + i, MSG.BASE, tax.base);
		taxesTable.addCell(base);
		base.readonly = this.invoice.isReadonly()
			|| this.invoice.details.length > 0
			|| tax.type.includes('IRPF');

		if (this.invoice.details.length > 0 || tax.type.includes('IRPF'))
			base.disabled = CONSTANT.TRUE;

		base.onChange(() => this.onChangeTaxBase(tax, base.value, i));

		// ----- TAX QUOTA

		let quotaVal = tax.surcharge_quota ? tax.quota + tax.surcharge_quota : tax.quota;

		let quota = this.createAonNumber(this.TAX_QUOTA + i, MSG.QUOTA, quotaVal)
		taxesTable.addCell(quota);
		quota.readonly = this.invoice.isReadonly() || this.invoice.details.length > 0 || tax.type.includes('IRPF');
		if (this.invoice.details.length > 0 || tax.type.includes('IRPF'))
			quota.disabled = CONSTANT.TRUE;

		// ----- TAX DELETE

		if (!this.invoice.isReadonly() && this.invoice.details.length === 0) {
			let taxDelete = new AonIconButton();
			taxDelete.id = this.TAX_DELETE + i;
			taxDelete.title = MSG.DELETE_TAX;
			taxDelete.icon = MATERIAL_ICONS.REMOVE_CIRCLE;
			taxDelete.className = CSS.AON_ICON_BUTTON_TOP;
			taxDelete.addEventListener(EVENT.CLICK, () => {
				this.invoice.deleteTax(tax, i);
				this.setFocus(undefined);
				this.reload();
			});
			taxesTable.addCell(taxDelete);
		}
	}

	// ╔════════════════════════════════════════════════════════════╗
	// ║                                                            ║	
	// ║                   		INVOICE DETAILS                     ║
	// ║                                                            ║
	// ╚════════════════════════════════════════════════════════════╝

	buildDetailCard(parent) {
		let card = this.getElement(this.DETAIL);
		if (!card && !parent) return;
		if (!card) {
			card = new AonCard();
			card.id = this.DETAIL;
			card.title = MSG.INVOICE_CONCEPTS;
			parent.appendChild(card);
		}

		let table = this.getElement(this.DETAIL_TABLE);
		if (!table) {
			table = new AonBasicTable();
			table.id = this.DETAIL_TABLE;
			card.setContent(table);
		}
		table.removeRows();
		for (let i = 0; i < this.invoice.details.length; i++) {
			let detail = this.invoice.details[i];
			if (this.fileOpened) this.printMinimizeDetail(table, detail, i)
			else this.printDetail(table, detail, i);
		}

		let div = this.createDiv(this.DETAIL_BUTTONS);
		card.addContent(div);

		let addButton = this.getElement(this.DETAIL_ADD);
		if (!this.invoice.isReadonly() && !addButton) {
			addButton = new AonIconButton();
			addButton.id = this.DETAIL_ADD;
			addButton.title = MSG.ADD_DETAIL;
			addButton.icon = MATERIAL_ICONS.ADD;
			addButton.addEventListener(EVENT.CLICK, () => {
				let detailIndex = this.invoice.details.length;
				let detail = this.invoice.addDetail();		
				this.printDetail(table, detail, detailIndex);
				this.getElement(this.DETAIL_DESCRIPTION + detailIndex).focus();
				this.onChangeDetail(detail, detailIndex);
				if (this.fileOpened) {
					const i = this.invoice.details.length - 1;
					const detail = this.invoice.details[i];
					this.printDetailDialog(detail, i);
				}
			});
			div.appendChild(addButton);
		}
	}

	onChangeDetail(detail, i) {
		this.invoice.setDetail(detail, i);
		this.reloadDetail(detail, i);
		this.reloadTotal();
		this.reloadTaxes();
		this.reloadFinances();
	}

	reloadDetail(detail, i) {		
		this.reloadValue(this.DETAIL_DESCRIPTION + i, detail.description);
		this.reloadValue(this.DETAIL_QUANTITY + i, detail.quantity);
		this.reloadValue(this.DETAIL_PRICE + i, detail.price);
		this.reloadValue(this.DETAIL_DISCOUNT + i, detail.discount);
		this.reloadValue(this.DETAIL_AMOUNT + i, detail.amount);

		this.reloadValue(this.DETAIL_DESCRIPTION + 'Dialog' + i, detail.description);
		this.reloadValue(this.DETAIL_QUANTITY + 'Dialog' + i, detail.quantity);
		this.reloadValue(this.DETAIL_PRICE + 'Dialog' + i, detail.price);
		this.reloadValue(this.DETAIL_DISCOUNT + 'Dialog' + i, detail.discount);
		this.reloadValue(this.DETAIL_AMOUNT + 'Dialog' + i, detail.amount);

		this.reloadDetailVat(detail, i);
	}

	reloadDetailVat(detail, i) {
		let percentage = detail.percentage ? detail.percentage : 0.0;
		let detailVat = this.getElement(this.DETAIL_VAT + i);
		if (detailVat) {
			detailVat.value = percentage;
		}

		let detailVatDialog = this.getElement(this.DETAIL_VAT + 'Dialog' + i);
		if (detailVatDialog) {
			detailVatDialog.value = percentage;
		}

		if (detail.prepayment && detail.prepayment == 'true') {
			if (detailVat) detailVat.setDisabled(true);
			if (detailVatDialog) detailVatDialog.setDisabled(true);			
		} else {
			if (detailVat) detailVat.setDisabled(false);
			if (detailVatDialog) detailVatDialog.setDisabled(false);
		}
	}

	reloadValue(elementId, value) {
		let element = this.getElement(elementId);
		if (element) {
			element.value = value;
		}
	}

	printMinimizeDetail(table, detail, i) {
		table.addRow();

		let td = this.printDetailDescription(table, detail, i);
		td.style.width = '60%';

		this.printDetailAmount(table, detail, i);
		this.printDetailEditButton(table, detail, i);
		this.printDetailDeleteButton(table, detail, i);
	}

	printDetail(table, detail, i) {
		table.addRow(); // ----- ROW i

		this.printDetailDescription(table, detail, i);
		this.printDetailQuantity(table, detail, i);
		this.printDetailPrice(table, detail, i);
		this.printDetailDiscount(table, detail, i);
		this.printDetailAmount(table, detail, i);
		this.printDetailVat(table, detail, i);
		this.printDetailEditButton(table, detail, i);
		this.printDetailDeleteButton(table, detail, i);
	}

	printDetailDialog(detail, i) {
		let dialog = this.getApplication().getDialog();
		dialog.setTitle("DETALLE");
		dialog.addAcceptAction(() => {});

		let div = this.createElement(TAG.DIV);
		div.style.margin = '15px';
		dialog.setContent(div);

		let table = new AonBasicTable();
		table.id = this.DIALOG + 'Detail';
		div.appendChild(table);

		table.addRow(); // ----- ROW 1

		this.printDetailDescription(table, detail, i, true);
		this.printDetailVat(table, detail, i, true);
		this.printDetailPrepayment(table, detail, i, true);

		table.addRow(); // ----- ROW 2

		this.printDetailQuantity(table, detail, i, true);
		this.printDetailPrice(table, detail, i, true);
		this.printDetailDiscount(table, detail, i, true);
		this.printDetailAmount(table, detail, i, true);

		if(this.getDur().hasAccounting() || this.getDur().hasParentAccounting() || !this.invoice.isEmitida()) {
			table.addRow(); // ----- ROW 3
		}

		this.printDetailCategory(table, detail, i);
		this.printDetailInvestAsset(table, detail, i);
		
		dialog.open();
	}

	printDetailDescription(table, detail, i, dialog) {
		const descriptionId = dialog ? this.DETAIL_DESCRIPTION + 'Dialog' + i : this.DETAIL_DESCRIPTION + i;
		let description = createTextarea(descriptionId, MSG.CONCEPT);
		description.readonly = this.invoice.isReadonly();
		description.value = detail.description;
		description.addEventListener(EVENT.AON_KEYUP, (e) => {
			if (description.value.length > 2) {
				let data = { value: description.getValue() };
				getItems(data).then(r => {
					description.buildOptions(r.map(r => {
						return {
							name: r.description && r.description !== '' ? r.description : r.name,
							value: r,
							item: r
						};
					}));
				}).catch(e => this.showError(e));
			}
		});


		description.addEventListener(EVENT.CHANGE, () => {
			this.invoice.details[i].description = description.value;
		});

		description.addEventListener(EVENT.SELECT, (e) => {
			detail.description = e.detail.name;
			detail.item = e.detail.item;
			detail.price = e.detail.item.price;
			if(e.detail.item.product.vat && e.detail.item.product.vat.percentage && this.invoice.isVatEnabled()) {
				detail.percentage = e.detail.item.product.vat.percentage;
			}
			detail.prepayment = ("PREPAYMENT" === e.detail.item.product.type);
			this.onChangeDetail(detail, i);
		});

		let td = dialog 
			? table.addCell(description, this.invoice.isVatEnabled() ? '2' : '3')
			: table.addCell(description);
		if(!dialog) td.style.width = '50%';
		return td;
	}

	printDetailQuantity(table, detail, i, dialog) {
		const quantityId = dialog ? this.DETAIL_QUANTITY + 'Dialog' + i : this.DETAIL_QUANTITY + i;
		let quantity = this.createAonNumber(quantityId, MSG.QUANTITY, detail.quantity);
		let td = table.addCell(quantity);
		if(!dialog) td.style.verticalAlign = "bottom";
		quantity.readonly = this.invoice.isReadonly();
		quantity.onChange(() => {
			detail.quantity = quantity.value;
			this.onChangeDetail(detail, i);
		});
		return td;		
	}

	printDetailPrice(table, detail, i, dialog) {
		const priceId = dialog ? this.DETAIL_PRICE + 'Dialog' + i : this.DETAIL_PRICE + i;
		let price = this.createAonNumber(priceId, MSG.PRICE, detail.price);
		let td = table.addCell(price);
		if(!dialog) td.style.verticalAlign = "bottom";
		price.readonly = this.invoice.isReadonly();
		price.onChange(() => {
			detail.price = price.value;
			this.onChangeDetail(detail, i);
		});
		return td;
	}

	printDetailDiscount(table, detail, i, dialog) {
		const discountId = dialog ? this.DETAIL_DISCOUNT + 'Dialog' + i : this.DETAIL_DISCOUNT + i;
		let discount = this.createAonNumber(discountId, '%Dto', detail.discount);
		discount.max = 100.0;
		let td = table.addCell(discount);
		if(!dialog) td.style.verticalAlign = "bottom";
		discount.readonly = this.invoice.isReadonly();
		discount.onChange(() => {
			detail.discount = discount.value;
			this.onChangeDetail(detail, i);
		});
		return td;
	}

	printDetailAmount(table, detail, i, dialog) {
		const amountId = dialog ? this.DETAIL_AMOUNT + 'Dialog' + i : this.DETAIL_AMOUNT + i;
		let amount = this.createAonNumber(amountId, MSG.AMOUNT, detail.amount);
		let td = table.addCell(amount);
		if(!dialog) td.style.verticalAlign = "bottom";
		amount.readonly = CONSTANT.TRUE;
		amount.disabled = CONSTANT.TRUE;
		return td;
	}

	printDetailVat(table, detail, i, dialog) {
		if (this.invoice.isVatEnabled()) {
			let administration = this.configuration ? this.configuration.administration : '';
			const vatId = dialog ? this.DETAIL_VAT + 'Dialog' + i : this.DETAIL_VAT + i;
			let vat = createSelect(vatId, getVatLabel(administration));
			vat.options = JSON.stringify(getVats(administration, this.getInvoice().isWithholdingFarmer()));
			if (detail.prepayment === undefined) detail.prepayment = false;
			vat.readonly = this.invoice.isReadonly() || (detail.prepayment && detail.prepayment == 'true');

			if (detail.prepayment && detail.prepayment == 'true') {
				vat.disabled = detail.prepayment && detail.prepayment == 'true';
			}

			vat.addEventListener(EVENT.SELECT, () => {
				detail.percentage = vat.value;
				this.onChangeDetail(detail, i);
			});
			let td = table.addCell(vat);
			if(!dialog) td.style.verticalAlign = "bottom";
			if (this.invoice.isEmitida() && !this.invoice.isNacional()) {
				detail.percentage = undefined;
				vat.setDisabled(true);
			}

			if (detail.percentage === undefined && (!detail.prepayment || detail.prepayment == 'false')) {
				detail.percentage = 21.0;
			}
			if (detail.percentage) vat.value = detail.percentage;
		} else if (detail.percentage !== 0.0) {
			detail.percentage = 0.0;
			this.invoice.setDetail(detail, i);
		}	
	}

	printDetailPrepayment(table, detail, i) { 
		// only use in detail dialog
		let prepayment = new AonSwitch();
		prepayment.id = this.DETAIL_PREPAYMENT + 'Dialog' + i;
		prepayment.className = CSS.AON_SWITCH_TOP;
		prepayment.title = MSG.PREPAYMENT;
		prepayment.addEventListener(EVENT.CHANGE, () => {
			detail.prepayment = prepayment.checked;
			if ((!detail.prepayment || detail.prepayment == 'false'))
				detail.percentage = 21.0;
			else detail.percentage = 0.0;
			
			this.onChangeDetail(detail, i);
		});
		table.addCell(prepayment);
		prepayment.readonly = this.invoice.isReadonly();
		prepayment.checked = detail.prepayment;
	}

	printDetailEditButton(table, detail, i) {
		let detailOptions = new AonIconButton();
		detailOptions.id = this.DETAIL_OPTIONS + i;
		detailOptions.title = MSG.OPTIONS;
		detailOptions.icon = MATERIAL_ICONS.EDIT;
		detailOptions.className = CSS.AON_ICON_BUTTON_TOP;
		detailOptions.addEventListener(EVENT.CLICK, () => {
			this.printDetailDialog(detail, i);
		});
		table.addCell(detailOptions);
	}

	printDetailDeleteButton(table, detail, i) {
		if (!this.invoice.isReadonly()) {
			let detailDelete = new AonIconButton();
			detailDelete.id = this.DETAIL_DELETE + i;
			detailDelete.title = MSG.DELETE_DETAIL;
			detailDelete.icon = MATERIAL_ICONS.REMOVE_CIRCLE;
			detailDelete.className = CSS.AON_ICON_BUTTON_TOP;
			detailDelete.addEventListener(EVENT.CLICK, () => {
				this.invoice.deleteDetail(detail, i);
				this.reload();
			});
			table.addCell(detailDelete);
		}		
	}

	printDetailCategory(table, detail, i) {
		// only use in detail dialog
		if (this.getDur().hasAccounting() || this.getDur().hasParentAccounting()) {
			let category = createSelect(this.DETAIL_CATEGORY + i, MSG.CATEGORY);
			category.autocomplete = true;
			category.readonly = this.invoice.isReadonly();
			category.addEventListener(EVENT.SELECT, () => {
				detail.category = category.value;
				this.invoice.setDetail(detail, i);
			});
			table.addCell(category, '2');
			getInvoiceAccounts({ type: this.invoice.getInvoiceType() }).then(accounts => {
				let accs = accounts.map(acc => { return { name: acc.description, value: acc.code }; });
				category.options = JSON.stringify(accs);
				category.value = detail.category || this.invoice.getCategory();
			});
		}
	}

	printDetailCategory(table, detail, i) {
		// only use in detail dialog
		if (this.getDur().hasAccounting() || this.getDur().hasParentAccounting()) {
			let category = createSelect(this.DETAIL_CATEGORY + i, MSG.CATEGORY);
			category.autocomplete = true;
			category.readonly = this.invoice.isReadonly();
			category.addEventListener(EVENT.SELECT, () => {
				detail.category = category.value;
				this.invoice.setDetail(detail, i);
			});
			table.addCell(category, '2');
			getInvoiceAccounts({ type: this.invoice.getInvoiceType() }).then(accounts => {
				let accs = accounts.map(acc => { return { name: acc.description, value: acc.code }; });
				category.options = JSON.stringify(accs);
				category.value = detail.category || this.invoice.getCategory();
			});
		}
	}

	printDetailInvestAsset(table, detail, i) {
		// only use in detail dialog
		if(!this.invoice.isEmitida()) {
			let bienAfecto = createSelect('aonInvoiceDetailBienAfecto', 'Bien Afecto');
			bienAfecto.autocomplete = true;
			bienAfecto.setAlias('id', 'description');
			bienAfecto.readonly = this.invoice.isReadonly();

			bienAfecto.addEventListener(EVENT.SELECT, () => {
				detail.investAsset = bienAfecto.value;
				let ia = bienAfecto.getValueObject();
				detail.vatDeductiblePercent = ia ? ia.vatPercent : 100.0;
				this.invoice.setDetail(detail, i);
			});

			table.addCell(bienAfecto, '2');
			getInvestAssets({}).then(investAssets => {
				bienAfecto.setOptions(investAssets);
				if (detail.investAsset)
					bienAfecto.value = detail.investAsset;
			});
		}
	}

	// ╔════════════════════════════════════════════════════════════╗
	// ║                                                            ║	
	// ║						INVOICE FINANCES                    ║
	// ║                                                            ║
	// ╚════════════════════════════════════════════════════════════╝

	buildFinanceCard(parent) {
		let card = this.getElement(this.FINANCE);
		if (!card) {
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
		if (!table) {
			table = new AonBasicTable();
			table.id = this.FINANCE_TABLE;
			card.setContent(table);
		}
		table.removeRows();
		this.buildFinances(table);

		let div = this.createElement(TAG.DIV);
		card.addContent(div);

		let addButton = this.getElement(this.FINANCE_ADD);
		if (!this.invoice.isReadonly() && !addButton) {
			addButton = new AonIconButton();
			addButton.id = this.FINANCE_ADD;
			addButton.title = MSG.ADD_FINANCE;
			addButton.icon = MATERIAL_ICONS.ADD;
			addButton.addEventListener('click', () => {
				this.setFocus(this.FINANCE_DUE_DATE + this.invoice.finances.length);
				this.invoice.addFinance();
				this.reload();
				if (this.isMinimize()) {
					const i = this.invoice.finances.length - 1;
					const finance = this.invoice.finances[i];
					this.printFinanceDialog(finance, i);
				}
			});
			div.appendChild(addButton);
		}
	}

	buildFinances(table) {
		for (let i = 0; i < this.invoice.finances.length; i++) {
			let finance = this.invoice.finances[i];
			this.printFinance(table, finance, i);
		}
	}

	printFinanceDialog(finance, i) {
		let dialog = this.getApplication().getDialog();
		dialog.setTitle("VENCIMIENTO");
		dialog.addAcceptAction(() => {
			let date = this.getElement(this.FINANCE_DUE_DATE + i);
			if (date) date.setDate(finance.due_date);

			let amount = this.getElement(this.FINANCE_AMOUNT + i);
			if (amount) amount.value = finance.amount;
		});

		let div = this.createElement(TAG.DIV);
		div.style.margin = '15px';
		dialog.setContent(div);

		let table = new AonBasicTable();
		table.id = this.DIALOG + 'Detail';
		div.appendChild(table);

		table.addRow(); // ----- ROW 1

		// ----- FINANCE DUE DATE
		this.buildFinanceDate(table, this.FINANCE_DUE_DATE + CONSTANT.DIALOG.initCap() + i, finance, i);

		table.addRow(); // ----- ROW 2

		// ----- FINANCE PAYMETHOD

		let paymethod = createSelect(this.FINANCE_PAYMETHOD + CONSTANT.DIALOG.initCap() + i, MSG.PAYMETHOD);
		paymethod.autocomplete = true;
		// paymethod.options = JSON.stringify(Paymethods);
		paymethod.readonly = this.invoice.isReadonly();
		paymethod.addEventListener(EVENT.SELECT, () => {
			this.setFocus(this.FINANCE_BANK_ACCOUNT + i);
			finance.paymethod = paymethod.value;
			this.invoice.setFinance(finance, i);
		});
		table.addCell(paymethod);

		getPaymethods({}).then(paymethods => {
			let pms = paymethods.map(pm => { return { name: pm.name, value: pm.id }; });
			paymethod.options = JSON.stringify(pms);
			paymethod.value = finance.paymethod;
		});


		table.addRow(); // ----- ROW 3

		// ----- FINANCE BANK ACCOUNT | RBANK

		let bankAccount = createSuggestion(this.FINANCE_BANK_ACCOUNT + CONSTANT.DIALOG.initCap() + i, 'Cuenta Bancaria'); //MSG.BANK_ACCOUNT;
		bankAccount.readonly = this.invoice.isReadonly();
		bankAccount.addEventListener(EVENT.KEYUP, () => {

		});

		bankAccount.addEventListener(EVENT.CHANGE, () => {
			this.setFocus(this.FINANCE_AMOUNT + i);
			this.updateBankAccount(finance, i, bankAccount.value);
		});

		bankAccount.addEventListener(EVENT.SELECT, (r) => {
			this.setFocus(this.FINANCE_AMOUNT + i);
			this.updateBankAccount(finance, i, r.detail.name);
		});

		table.addCell(bankAccount);
		finance.bank_account = finance.bank_account || finance.iban;
		bankAccount.value = finance.bank_account;

		table.addRow(); // ----- ROW 3

		// ----- FINANCE AMOUNT

		this.buildFinanceAmount(table, this.FINANCE_AMOUNT + CONSTANT.DIALOG.initCap() + i, finance, i);

		dialog.open();
	}

	updateBankAccount(finance, i, iban) {
		let ba = new BankAccount(iban);
		finance.bank_account = ba.iban;
		finance.bankAlias = ba?.bank ? ba.bank.substring(0, 24) : null;
		finance.bic = ba.bic;
		this.invoice.setFinance(finance, i);
	}

	isMinimize() {
		return this.fileOpened;
	}

	async printFinance(table, finance, i) {
		// Rellenamos finance.banks para añadir opciones a BankAccountSuggestion.
		let fpm = await getPaymethod(finance.paymethod);
		if ((!this.invoice.isEmitida() && fpm.type === 'NEGOTIABLE_DOCUMENT') || (this.invoice.isEmitida() && fpm.type === 'BANK_TRANSFER')) {
			finance.banks = this.getCompanyBanks();
		} else if ((this.invoice.isEmitida() && fpm.type === 'NEGOTIABLE_DOCUMENT') || (!this.invoice.isEmitida() && fpm.type === 'BANK_TRANSFER')) {
			getRegistryBanks(this.invoice.getRegistry().id).then(r => {
				finance.banks = r;
			});
		} else finance.banks = [];

		table.addRow(); // ----- ROW i

		// ----- FINANCE DUE DATE

		let dateCell = this.buildFinanceDate(table, this.FINANCE_DUE_DATE + i, finance, i);
		if (!this.isMinimize()) dateCell.style.width = '15%';

		// ----- FINANCE PAYMETHOD

		if (!this.isMinimize()) {
			let paymethod = createSelect(this.FINANCE_PAYMETHOD + i, MSG.PAYMETHOD);
			paymethod.autocomplete = true;
			paymethod.setAlias("id", "name");
			// paymethod.options = JSON.stringify(Paymethods);
			paymethod.readonly = this.invoice.isReadonly();

			let paymethodCell = table.addCell(paymethod);
			paymethodCell.style.width = '25%';

			getPaymethods({}).then(paymethods => {
				paymethod.setOptions(paymethods);
				paymethod.value = finance.paymethod;
			});

			// ----- FINANCE BANK ACCOUNT | RBANK
			let bankAccount = createSuggestion(this.FINANCE_BANK_ACCOUNT + i, 'Cuenta Bancaria'); //MSG.BANK_ACCOUNT;
			bankAccount.readonly = this.invoice.isReadonly();
			bankAccount.addEventListener(EVENT.AON_KEYUP, () => {
				bankAccount.buildOptions(finance.banks.filter(f => f.bank_account.includes(bankAccount.value))
					.map(r => {
						return {
							name: r.bank_account,
							value: r.bank_account,
							rbank: r
						};
					}));
			});

			bankAccount.addEventListener(EVENT.SELECT, (r) => {
				this.setFocus(this.FINANCE_AMOUNT + i);
				this.updateBankAccount(finance, i, r.detail.name);
			});

			bankAccount.addEventListener(EVENT.CHANGE, () => {
				this.setFocus(this.FINANCE_AMOUNT + i);
				this.updateBankAccount(finance, i, bankAccount.value);
			});

			let ibanCell = table.addCell(bankAccount);
			ibanCell.style.width = '50%';

			finance.bank_account = finance.bank_account || finance.iban;
			bankAccount.value = finance.bank_account || '';
			bankAccount.disabled = fpm.type !== 'NEGOTIABLE_DOCUMENT' && fpm.type !== 'BANK_TRANSFER';

			paymethod.addEventListener(EVENT.SELECT, () => {
				this.setFocus(this.FINANCE_BANK_ACCOUNT + i);
				finance.paymethod = paymethod.value;
				this.invoice.setFinance(finance, i);
				const pm = paymethod.getOptions().filter(f => f.id == paymethod.value)[0];
				bankAccount.disabled = pm.type !== 'NEGOTIABLE_DOCUMENT' && pm.type !== 'BANK_TRANSFER';
				if ((!this.invoice.isEmitida() && pm.type === 'NEGOTIABLE_DOCUMENT') || (this.invoice.isEmitida() && pm.type === 'BANK_TRANSFER')) {
					this.initFinanceBanks(this.getCompanyBanks(), finance, i, bankAccount);
				} else if ((this.invoice.isEmitida() && pm.type === 'NEGOTIABLE_DOCUMENT') || (!this.invoice.isEmitida() && pm.type === 'BANK_TRANSFER')) {
					getRegistryBanks(this.invoice.getRegistry().id).then(r => {
						this.initFinanceBanks(r, finance, i, bankAccount);
					});
				} else this.initFinanceBanks([], finance, i, bankAccount);
			});
		}

		// ----- FINANCE AMOUNT

		this.buildFinanceAmount(table, this.FINANCE_AMOUNT + i, finance, i);

		// ----- FINANCE OPTIONS
		if (this.isMinimize()) {
			let financeOptions = new AonIconButton();
			financeOptions.id = this.FINANCE_OPTIONS + i;
			financeOptions.title = MSG.OPTIONS;
			financeOptions.className = CSS.AON_ICON_BUTTON_TOP;
			financeOptions.icon = MATERIAL_ICONS.EDIT;
			financeOptions.addEventListener(EVENT.CLICK, () => {
				this.printFinanceDialog(finance, i);
			});
			table.addCell(financeOptions);
		}
		// ----- FINANCE DELETE

		if (!this.invoice.isReadonly()) {
			let financeDelete = new AonIconButton();
			financeDelete.id = this.FINANCE_DELETE + i;
			financeDelete.title = MSG.DELETE_FINANCE;
			financeDelete.className = CSS.AON_ICON_BUTTON_TOP;
			financeDelete.icon = MATERIAL_ICONS.REMOVE_CIRCLE;
			financeDelete.addEventListener(EVENT.CLICK, () => {
				this.invoice.deleteFinance(finance, i);
				this.reload();
			});
			table.addCell(financeDelete);
		}
	}

	initFinanceBanks(banks, finance, index, bankAccountWidget) {
		finance.banks = banks.filter(f => f.active);
		let firstBank = finance.banks && finance.banks.length > 0
			? finance.banks[0] : {};
		finance.bank_account = firstBank.bank_account || "";
		finance.bankAlias = firstBank.alias || "";
		finance.bic = firstBank.bic || "";
		this.invoice.setFinance(finance, index);
		bankAccountWidget.value = finance.bank_account;
	}

	buildFinanceDate(table, id, finance, i) {
		let date = createDate(id, MSG.DATE);
		date.readonly = this.invoice.isReadonly();
		date.setDate(finance.due_date);
		date.addEventListener(EVENT.CHANGE, () => {
			finance.due_date = date.getDateValue();
			this.setFocus(date.id);
			this.invoice.setFinance(finance, i);
		});
		return table.addCell(date);
	}

	buildFinanceAmount(table, id, finance, i) {
		let amount = this.createAonNumber(id, MSG.AMOUNT, finance.amount);
		table.addCell(amount);
		amount.readonly = this.invoice.isReadonly();
		amount.onChange(() => {
			this.setFocus(amount.id);
			finance.amount = amount.value;
			this.invoice.setFinance(finance, i);
		});
	}

	getInvoiceTitle() {
		if (this.getInvoice().isEmitida()) {
			return MSG.INVOICE_ISSUED;
		} else if (this.getInvoice().isTicket()) {
			return MSG.TICKET;
		} else return MSG.INVOICE_RECEIVED;
	}

	save(msg) {
		let ok = this.checkConfiguration();
		if (!ok) return;
		msg = msg || MSG.SAVED_DATA;
		insertInvoice(this.getInvoice()).then(r => {
			if (!this.getInvoice().id) {	
				this.updateCounter(this.getNewFrom(), undefined, 1);
				this.updateCounter(this.getFutureNewFrom(), undefined, 1);
			}
			this.getInvoice().id = r.id;
			this.showMessage(msg);
		}).catch(e => this.showError(e));
	}

	getNewFrom() {
		if (this.getInvoice().isEmitida()) return OPTION.PROFORMA_INVOICES;
		else if (this.getInvoice().isTicket()) return OPTION.RAWDOC_INBOX_TICKET_NEW;
		else return OPTION.RAWDOC_INBOX_RECEIVED_NEW;
	}

	getFutureNewFrom() {
		if (this.getInvoice().isEmitida()) return OPTION.FUTURE_PROFORMA_INVOICES;
		else if (this.getInvoice().isTicket()) return OPTION.FUTURE_RAWDOC_INBOX_TICKET_NEW;
		else return OPTION.FUTURE_RAWDOC_INBOX_RECEIVED_NEW;
	}

	back() {
		let parent = this.getApplication().getParent();
		if (this.invoice.status == 'processed') {
			parent.aonInvoiceProcessing();
		} else parent.aonInvoiceList(parent.filter, parent.invofoxFilter);
	}

	more(e) {
		e.preventDefault();
		let rect = e.target.getBoundingClientRect();
		let x = e.clientX - rect.left;
		let y = e.clientY - rect.top;

		const top = rect.top + y;
		const left = rect.left + x;

		let d = this.getApplication().getOptionDialog();
		let rectify = ACTION.RECTIFY;
		rectify.fn = () => this.rectifyInvoice();
		let duplicate = ACTION.DUPLICATE;
		duplicate.fn = () => this.duplicateInvoice();
		d.setMenuOptions([rectify], top, left);
		d.open();
	}

	showLog() {
		let invoiceToolbar = this.getElement(this.TOOLBAR);
		
		let commentButton = this.getElement(invoiceToolbar.TITLE_SECTION + 'CommentButton');
		let commentVisible = MATERIAL_ICONS.COMMENTS_DISABLED === commentButton.icon;

		let showFileButton = this.getElement(invoiceToolbar.TITLE_SECTION + 'ShowFileButton');
		if(showFileButton) showFileButton.icon = MATERIAL_ICONS.VISIBILITY;

		let rightDiv = this.getElement(this.FILE);
		let dataDiv = this.getElement(this.DATA);

		let general = this.getElement(this.GENERAL);
		let communication = this.getElement(this.COMMUNICATION);		
		
		if(commentVisible) {
			commentButton.icon = MATERIAL_ICONS.COMMENT;
			rightDiv.style.display = 'none';
			dataDiv.style.width = '100%';

			if (general) general.style.display = 'flex';
			if (communication) communication.style.display = 'flex';
		}  else {
			commentButton.icon = MATERIAL_ICONS.COMMENTS_DISABLED;
			rightDiv.style.display = 'block';
			rightDiv.style.width = '50%';
			dataDiv.style.width = '50%';

			if (general) general.style.display = 'block';
			if (communication) communication.style.display = 'block';

			this.clearElement(rightDiv);

			let chat =  new AonChat();
			chat.readonly = !this.invoice.isRawdoc();
			if(this.invoice.remarksDescription) chat.description = this.invoice.remarksDescription;
			
			chat.workflows = this.getInvoice().remarks.map(r => {
				let remark = {}; 
				remark.action = r.action;
				if(r.status == 'Rechazado') {
					remark.action = {
						title: 'Rechazada',
						icon: MATERIAL_ICONS.REPORT,
						color: 'red'
					};
				}

				if(r.status == 'Papelera') {
					remark.action = {
						title: 'Enviado a papelera',
						icon: MATERIAL_ICONS.DELETE,
						color: 'gray'
					};
				}

				if(r.status == 'Procesado') {
					remark.action = {
						title: 'Procesada',
						icon: MATERIAL_ICONS.DOCUMENT_SCANNER,
						color: 'gray'
					};
				}
				remark.user = r.user;
				remark.date = r.date;
				remark.comment = r.comment || r.reason;
				return remark;
			}) || [];
			chat.onComment((event) => {
				this.invoice.remarks.push(event.detail);
				this.save();
			});

			rightDiv.appendChild(chat);
		}

		if (general) {
			this.buildDetailCard();
			this.buildFinanceCard();
		}
	}

	showFile(reload) {
		let invoiceToolbar = this.getElement(this.TOOLBAR);
		let button = this.getElement(invoiceToolbar.TITLE_SECTION + 'ShowFileButton');
		let visible = MATERIAL_ICONS.VISIBILITY_OFF === button.icon;

		let commentButton = this.getElement(invoiceToolbar.TITLE_SECTION + 'CommentButton');
		if(commentButton) commentButton.icon = MATERIAL_ICONS.COMMENT;

		let fileDiv = this.getElement(this.FILE);
		let dataDiv = this.getElement(this.DATA);
		this.fileOpened = !visible;
		if (visible) {
			button.icon = MATERIAL_ICONS.VISIBILITY;
			fileDiv.style.display = 'none';
			dataDiv.style.width = '100%';

			let general = this.getElement(this.GENERAL);
			if (general) general.style.display = 'flex';

			let communication = this.getElement(this.COMMUNICATION);
			if (communication) communication.style.display = 'flex';
		} else {
			button.icon = MATERIAL_ICONS.VISIBILITY_OFF;
			fileDiv.style.display = 'block';
			fileDiv.style.width = '50%';
			dataDiv.style.width = '50%';

			let general = this.getElement(this.GENERAL);
			if (general) general.style.display = 'block';

			let communication = this.getElement(this.COMMUNICATION);
			if (communication) communication.style.display = 'block';

			this.clearElement(fileDiv);

			let viewer = new AonViewer();
			if (this.getInvoice().file) {
				viewer.type = this.getInvoice().file.content_type;
				viewer.file = this.getInvoice().file.path || this.getInvoice().file.url;
			} else {
				let json = {
					id: this.getInvoice().id,
					source: this.getInvoice().isRawdoc() ? 'rawdoc' : 'invoice',
					domain_id: LS.getDomainId(),
					domain_name: LS.getDomainName(),
					login: LS.getDomainLogin()
				};
				viewer.type = 'application/pdf';
				viewer.file = '/ms/api/download_invoice_pdf?json=' + btoa(JSON.stringify(json));
			}

			viewer.width = fileDiv.offsetWidth;
			viewer.addEventListener(EVENT.SEND_MAIL, () => this.sendInvoice());
			viewer.addEventListener(EVENT.PRINT_IMAGE, () => { getInvofoxTextContent(this.getInvoice().insight.invofoxId).then(t => viewer.printImageTextLayer(t)); });
			viewer.addEventListener(EVENT.PRINT_PDF_PAGE, (e) => { if (!e.detail.text) getInvofoxTextContent(this.getInvoice().insight.invofoxId).then(t => viewer.printPdfTextLayer(e.detail.page, t)); });
			fileDiv.appendChild(viewer);
		}

		let general = this.getElement(this.GENERAL);
		if (general) {
			this.buildDetailCard();
			if (reload) this.buildFinanceCard();
		}
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
		if (invoice && inv && !inv.isRawdoc()) {
			getInvoice(invoice.id).then((inv) => {
				let aip = document.querySelector('aon-invoice-panel');
				aip.aonInvoice(invoice.type, inv);
			});
		} else if (invoice) {
			this.getApplication().getParent().aonInvoice(invoice.type, invoice);
		}
	}

	acceptInvoice() {
		let ok = this.checkConfiguration();
		ok = ok && this.checkRegistry();
		ok = ok && this.checkSeries(true);
		if (!ok) return;

		if (!this.isInvofoxInvoice() && this.getInvoice().isEmitida()) this.getInvoice().setReference(undefined);
		if (this.invoice.isEmitida() && this.icc.hasCommunication() && !this.icc.isSif() && !this.icc.isNoVerifactu()) {
			let d = this.getApplication().getDialog();
			d.clear();
			if (!this.isMobile()) d.width = '400px';
			d.setTitle(MSG.ACCEPT);

			if (this.icc.isTbai() || this.icc.isLroe() || this.icc.isVerifactu() || this.icc.isSii()) {
				let certSelect = createSelect("cert", "Certificado");
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
					data.messages = undefined;
					acceptInvoice(data).then(r => {
						this.updateCounter(this.getAcceptFromOption(), this.getAcceptToOption(), 1);
						this.updateCounter(this.getFutureAcceptFromOption(), this.getFutureAcceptToOption(), 1);
						this.invoice = new Invoice(r);
						this.getApplication().stopLoader();
						this.reload();
					}).catch(e => {
						this.accept = true;
						this.getApplication().stopLoader();
						// this.showError(e)
						if (typeof e === "string") {
							try {
								e = JSON.parse(e);
							} catch (err) {
								console.error("No es un JSON válido:", err);
								return;
							}
						}
						if (e && e.messages) {
							if (this.invoice.messages) {
								this.invoice.messages.push(...e.messages);
							} else {
								this.invoice.messages = e.messages;
							}
							this.reload();
						} else {
							this.showError(e);
						}
					});
				});
			}
			d.open();
		} else if (this.accept) {
			this.getApplication().startLoader();
			this.accept = false;
			let data = this.getInvoice();
			data.messages = undefined;
			acceptInvoice(data)
				.then(r => {
					this.updateCounter(this.getAcceptFromOption(), this.getAcceptToOption(), 1);
					this.updateCounter(this.getFutureAcceptFromOption(), this.getFutureAcceptToOption(), 1);
					this.invoice = new Invoice(r);
					this.getApplication().stopLoader();
					this.reload();
				}).catch(e => {
					this.accept = true;
					this.getApplication().stopLoader();
					this.showError(e)
				});
		}
	}

	getAcceptFromOption() {
		if (this.getInvoice().isEmitida()) {
			return OPTION.PROFORMA_INVOICES;
		} else if (this.getInvoice().isTicket()) {
			return OPTION.RAWDOC_INBOX_TICKET;
		} else return OPTION.RAWDOC_INBOX_RECEIVED;
	}

	getFutureAcceptFromOption() {
		if (this.getInvoice().isEmitida()) {
			return OPTION.FUTURE_PROFORMA_INVOICES;
		} else if (this.getInvoice().isTicket()) {
			return OPTION.FUTURE_RAWDOC_INBOX_TICKET_NEW;
		} else return OPTION.FUTURE_RAWDOC_INBOX_RECEIVED_NEW;
	}

	getAcceptToOption() {
		if (this.getInvoice().isEmitida()) {
			return OPTION.INVOICE_ISSUED_BETA;
		} else if (this.getInvoice().isTicket()) {
			return OPTION.INVOICE_TICKET;
		} else return OPTION.INVOICE_RECEIVED_BETA;
	}

	getFutureAcceptToOption() {
		if (this.getInvoice().isEmitida()) {
			return OPTION.FUTURE_INVOICE_ISSUED_BETA;
		} else if (this.getInvoice().isTicket()) {
			return OPTION.FUTURE_INVOICE_TICKET;
		} else return OPTION.FUTURE_INVOICE_RECEIVED_BETA;
	}

	recordInvoice() {
		// if(this.invoice.category) {
		let div = this.getElement("PRUEBA_RAWDOC_RECORD");
		if (!div) {
			div = this.createDiv("PRUEBA_RAWDOC_RECORD");
			div.style.display = 'none';
			this.appendChild(div);
		}
		this.clearElement(div);
		document.body.classList.add('gwt-Selector');
		GWT.load(GWT.RAWDOC_RECORD, "PRUEBA_RAWDOC_RECORD");
		// } else {
		// 	this.showError({
		// 		type: CONSTANT.ERROR,
		// 		message: "Para Contabilizar es necesario la categoría."
		// 	});
		// }
	}

	rejectInvoice() {
		let div = this.createDiv();
		
		let d = this.getApplication().getDialog();
		d.clear();
		if (!this.isMobile()) d.width = '400px';
		d.setTitle(MSG.REJECT);
		d.setContent(div);
		
		let notify = new AonSwitch();
		notify.id = 'rejectNotifySwitch';
		notify.title = "Notificar por email";
		div.appendChild(notify);

		let email = new AonEmail();
		email.id = 'rejectInvoiceEmail';
		email.title = MSG.EMAIL;
		email.style.display = 'none';
		div.appendChild(email);
		getUserEmail({userLogin: this.invoice.creation_user}).then(emailData => {
			email.setValue(emailData.email);
		}).catch(e => {
			console.error("Error al obtener el email del usuario: " + e.message);
		});

		notify.addEventListener(EVENT.CHANGE, () => {
			if(notify.isChecked()) {
				email.style.display = 'block';
			} else {
				email.style.display = 'none';
			}
		});

		let textArea = this.createElement('textarea');
		textArea.id = 'commentTextArea';
		textArea.maxLength = 256;
		textArea.className = 'aonTextarea';
		textArea.style.marginTop = '10px';
		div.appendChild(textArea);

		let acceptButton = d.addAcceptAction(() => {
			let dt = new Date()
			let m = dt.getMonth() + 1;
			let month = m < 10 ? '0' + m : m;
			let dateStr = dt.getDate() + '/' + month + '/' + dt.getFullYear() + ' ' + dt.getHours() + ':' + dt.getMinutes() + ':' + dt.getSeconds();
			let comment = {
				date: dateStr,
				user: LS.getDomainLogin(),
				status: 'Rechazado',
				action: {
					title: 'Rechazada',
					icon: MATERIAL_ICONS.REPORT,
					color: 'red'
				},
				comment: textArea.value
			};
			this.invoice.remarks.push(comment);
			this.invoice.lastStatus = this.invoice.status;
			this.invoice.status = CONSTANT.REJECTED;
			this.build();
			this.save();
			// ENVIAR POR EMAIL SI SE HA INTRODUCIDO EMAIL
			if (notify.isChecked()) {
				sendInvoiceRejectMail({to: email.value}).then(() => {
					this.showMessage("Email enviado correctamente");
				}).catch(e => {
					this.showError("Error al enviar el email: " + e.message);
				});
			}
			this.updateCounter(getRejectFromOption(this.invoice), OPTION.RAWDOC_REJECT, 1);
			this.updateCounter(getFutureRejectFromOption(this.invoice), OPTION.FUTURE_RAWDOC_REJECT, 1);
		});
		acceptButton.disabled = true;

		let ta = this.getElement('commentTextArea');
		ta.style.outline = 'none';
		ta.style.width = '100%';
		ta.style.height = '100px';
		ta.addEventListener(EVENT.KEYUP, () => {
			acceptButton.disabled = ta.value.trim().length === 0;
		});
		d.open();
	}

	addInvoiceRemarks() {
		let aonInvoice = this.getElement('aonInvoice');
		let d = document.getElementById(aonInvoice.DIALOG);
		d.clear();
		if (!this.isMobile()) d.width = '400px';
		d.setTitle(MSG.ADD_REMARKS);
		d.setContentHTML('<textarea id="commentTextArea" maxlength="256" class="aonTextarea"> </textarea>');
		d.addAcceptAction(() => {
			let ta = this.getElement('commentTextArea');
			let dt = new Date()
			let m = dt.getMonth() + 1;
			let month = m < 10 ? '0' + m : m;
			let dateStr = dt.getDate() + '/' + month + '/' + dt.getFullYear() + ' ' + dt.getHours() + ':' + dt.getMinutes() + ':' + dt.getSeconds();
			let comment = {
				date: dateStr,
				user: '',
				status: this.getCommentStatus(),
				reason: ta.value
			};
			this.invoice.remarks.push(comment);
			this.reload();
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
		if (!this.isMobile()) d.width = '400px';
		d.setTitle(MSG.ADD_COMMENT);
		d.setContent(textarea);
		d.addAcceptAction(() => {
			this.invoice.comments = textarea.value;
			this.reload();
		});
		d.open();
	}

	buildInmobilized(amortization) {
		let aonInvoice = this.getElement('aonInvoice');
		let d = document.getElementById(aonInvoice.DIALOG);

		let div = this.createDiv();

		let date = createDate(this.id + 'InmbobilizedDate', MSG.DATE);
		date.setDate(amortization ? amortization.initialDate : this.invoice.date);
		date.readonly = this.invoice.isReadonly();
		div.appendChild(date);	
		
		let description = createInput( this.id + 'InmbobilizedDescription', MSG.DESCRIPTION);
		description.value = amortization ? amortization.description : '';
		description.readonly = this.invoice.isReadonly();
		div.appendChild(description);

		let type;
		if(this.invoice.isRawdoc()) {
			type = createSelect(this.id + 'InmbobilizedType', MSG.TYPE);
			type.setAlias("id", "description");
			type.readonly = this.invoice.isReadonly();
			div.appendChild(type);
			getAmortizationTypes().then(types => {
				type.setOptions(types);
				if (amortization && amortization.amortizationType) type.value = amortization.amortizationType.id;
			});
		}

		let period = createSelect(this.id + 'InmbobilizedPeriod', MSG.PERIOD);
		period.setAlias("value", "description");
		period.readonly = this.invoice.isReadonly();
		period.value = amortization ? amortization.feePeriod : AmortizationPeriod.YEARLY.value;
		period.setOptions(AmortizationPeriod.toArray());
		div.appendChild(period);

		let percentage = this.createAonNumber(this.id + 'InmbobilizedPercentage', MSG.PERCENTAGE, amortization ? amortization.percentage : 0.0);
		percentage.min = 0;
		percentage.max = 100;
		div.appendChild(percentage);

		let amount = this.createAonNumber(this.id + 'InmbobilizedAmount', MSG.AMOUNT, amortization ? amortization.amount : 0.0);
		div.appendChild(amount);

		d.clear();
		if (!this.isMobile()) d.width = '400px';
		d.setTitle("Añadir Inmovilizado");
		d.setContent(div);
		d.addAcceptAction(() => {
			if(this.invoice.isRawdoc()) {
				this.invoice.amortization = {
					domain: this.invoice.domain,
					description: description.value,
					amount: parseFloat(amount.value),
					percentage: parseFloat(percentage.value),
					initialDate: date.getDateValue(),
					amortizationType: type.getValueObject(),
					feePeriod: period.value
				};
				this.save();
				this.reload();
			}
		});
		d.open();
	}

	buildRectifedInvoiceDialog() {
		let aonInvoice = this.getElement('aonInvoice');
		let d = document.getElementById(aonInvoice.DIALOG);

		let div = this.createDiv();

		d.clear();
		if (!this.isMobile()) d.width = '400px';
		d.setTitle("Seleccione la factura rectificada");
		d.setContent(div);

		if(this.invoice.isEmitida()) {
			let seriesOptions = this.configuration.series.filter(f => f.invoice);
			let series = createSelect(this.id + 'RectifiedSeries', MSG.SERIE);
			series.setOptions(seriesOptions);
			series.setAlias("code", "code");
			series.setValue(this.invoice.series);

			div.appendChild(series);

			let number = createNumber(this.id + 'RectifiedNumber', MSG.NUMBER);
			div.appendChild(number);

			let date = createDate(this.id + 'RectifiedDate', MSG.DATE);
			date.setDate(new Date());
			div.appendChild(date);

			d.addAcceptAction(() => {
				this.invoice.setRectificationInvoice({
					series: series.value,
					number: number.value,
					date: date.getDateValue()
				});
				this.reload();
			});
		} else {
			let reference = createInput(this.id + 'RectifiedReference', MSG.REFERENCE);
			div.appendChild(reference);
			
			let date = createDate(this.id + 'RectifiedDate', MSG.DATE);
			date.setDate(new Date());
			div.appendChild(date);

			d.addAcceptAction(() => {
				this.invoice.setRectificationInvoice({
					referenceCode: reference.value,
					date: date.getDateValue()
				});
				this.reload();
			});
		}

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

		let type = createSelect(this.TYPE, MSG.TYPE);
		type.setOptions(types);
		type.value = this.getInvoice().type;

		let aonInvoice = this.getElement('aonInvoice');
		let d = document.getElementById(aonInvoice.DIALOG);
		d.clear();
		if (!this.isMobile()) d.width = '400px';
		d.setTitle(MSG.CHANGE_TYPE);
		d.setContent(type);
		d.addAcceptAction(() => {
			this.getInvoice().setType(type.value);
			this.reload();
		});
		d.open();
	}

	checkRectifySeries() {
		if (this.configuration
			&& this.configuration.series
			&& this.configuration.series.length > 0) {
			return this.configuration.series.some(ser => ser.rectification === true);
		}
		return false;
	}

	newRectifyInvoice() {
		if (this.invoice.isEmitida() && this.icc.hasCommunication()) {
			if (!this.checkRectifySeries()) {
				this.showMessageError("Debe existir al menos una serie para facturas rectificativas.");
				return;
			}

			let aonInvoice = this.getElement('aonInvoice');
			let d = document.getElementById(aonInvoice.DIALOG);
			d.clear();
			d.setTitle(MSG.RECTIFY_INVOICE);
			if (!this.isMobile()) d.width = '400px';

			let rectifyContent = this.createElement(TAG.DIV);
			rectifyContent.id = this.RECTIFY_CONTENT;
			d.setContent(rectifyContent);

			let table = new AonBasicTable();
			table.id = this.RECTIFY_CONTENT_TABLE;
			rectifyContent.appendChild(table);

			table.addRow();
			let rectSeries = createSelect(this.RECTIFY_CONTENT_SERIES, MSG.SERIE);
			let rectOptions = this.configuration.series
				.filter(ser => ser.rectification === true)
				.map(ser => { return { value: ser.code, name: ser.code }; }
				);
			rectSeries.options = JSON.stringify(rectOptions);
			if (rectOptions.length == 1) {
				rectSeries.value = rectOptions[0].value;
			}
			table.addCell(rectSeries);

			table.addRow();
			let rectDate = createDate(this.RECTIFY_CONTENT_DATE, MSG.DATE);
			rectDate.setDate(new Date());
			table.addCell(rectDate);

			table.addRow();
			let rectCause = createTextarea(this.RECTIFY_CONTENT_CAUSE, MSG.CAUSE);
			rectCause.maxlength = "256";
			rectCause.rows = "4";
			table.addCell(rectCause);

			d.addAcceptAction(() => {
				this.getApplication().startLoader();
				let data = {
					series: rectSeries.getValue(),
					date: rectDate.getValue(),
					cause: rectCause.getValue(),
					invoice: this.invoice
				};
				rectifyInvoice(data).then(r => {
					r.status = 'inbox';
					this.invoice = new Invoice(r);
					this.getApplication().stopLoader();
					this.fileOpened = false;
					this.reload();
				}).catch(e => {
					this.getApplication().stopLoader();
					this.showError(e)
				});
			});
			d.open();
		}
	}

	rectifyInvoice() {
		// [START] TEMP SOLUTION!!
		if (this.invoice.isEmitida() && this.icc.hasCommunication()) {
			return this.newRectifyInvoice();
		}
		// [END]

		let aonInvoice = this.getElement('aonInvoice');
		let d = document.getElementById(aonInvoice.DIALOG);
		d.clear();
		if (!this.isMobile()) d.width = '400px';
		d.setTitle(MSG.RECTIFY_INVOICE);
		d.setContentHTML('<textarea id="commentTextArea" maxlength="256" class="aonTextarea" placeholder="Causa..."></textarea>');
		d.addAcceptAction(() => {
			let recInv = this.invoice.clone();
			recInv.setRectificationInvoice(this.getInvoice());

			let ta = this.getElement('commentTextArea');
			let dt = new Date()
			let m = dt.getMonth() + 1;
			let month = m < 10 ? '0' + m : m;
			let dateStr = dt.getDate() + '/' + month + '/' + dt.getFullYear() + ' ' + dt.getHours() + ':' + dt.getMinutes() + ':' + dt.getSeconds();
			let comment = {
				date: dateStr,
				user: '',
				status: this.getCommentStatus(),
				reason: ta.value
			};
			recInv.remarks.push(comment);
			recInv.id = undefined;
			recInv.date = AonDateUtils.formatDate(new Date(), 'yyyy-MM-dd');
			recInv.series = 'R' + new Date().getFullYear();
			recInv.number = undefined;
			recInv.reference = undefined;
			recInv.status = 'inbox';
			recInv.tbai = undefined;
			recInv.tbaiUrl = undefined;

			if (recInv.finances) {
				recInv.finances.forEach((item, i) => {
					recInv.finances[i].id = undefined;
					recInv.finances[i].due_date = new Date();
					recInv.finances[i].amount = recInv.finances[i].amount * (-1);
				});
			}

			if (recInv.details) {
				recInv.details.forEach((item, i) => {
					item.id = undefined;
					item.quantity = item.quantity * (-1);
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
		if (!this.isMobile()) d.width = '400px';
		d.setTitle(MSG.SEND_INVOICES);
		let mail = createEmail('sendInvoicesMail', MSG.EMAIL);
		d.setContent(mail);
		d.addAcceptAction(() => {
			let message = {
				to: mail.value,
				invoice: this.invoice
			};
			sendInvoice2Mail(message).then(() => { });
		});
		if (this.invoice.isEmitida()) {
			getCustomerEmails(this.invoice.getRegistry()).then(emails => {
				mail.setValue(emails[0] || '');
			});
		}
		d.open();
	}

	signInvoice() {
		let d = this.getApplication().getDialog();
		d.clear();
		if (!this.isMobile()) d.width = '400px';
		d.setTitle("Firmar Factura");
		let div = this.createDiv();
		let certSelect = createSelect(this.SIGN_CERTIFICATE, MSG.CERTIFICATE, div);
		certSelect.setAlias('id', 'name');
		getAeatCertificates().then(certs => certSelect.setOptions(certs));

		d.setContent(div);
		d.addAcceptAction(() => {
			let data = {
				id: this.invoice.id,
				domainName: LS.getDomainName(),
				domainId: LS.getDomainId()
			};
			data.domainLogin = LS.getDomainLogin();
			data.cert = certSelect.value;
			signInvoice(data).then(r => { });
		});
		d.open();
	}

	facturae() {
		let d = this.getApplication().getDialog();
		d.clear();
		if (!this.isMobile()) d.width = '400px';
		d.setTitle(MSG.FACTURAE);
		let div = this.createDiv();
		let certSelect = createSelect(this.FACTURAE_CERTIFICATE, MSG.CERTIFICATE, div);
		certSelect.setAlias('id', 'name');
		getAeatCertificates().then(certs => certSelect.setOptions(certs));

		let period = createSelect(this.FACTURAE_PERIOD, MSG.PERIOD, div);
		period.setOptions(BillingPeriods);

		let legalLiterals = createTextarea(this.FACTURAE_LEGAL_LITERALS, MSG.LEGAL_LITERALS, div);

		d.setContent(div);
		d.addAcceptAction(() => {
			let data = {
				id: this.invoice.id,
				domainName: LS.getDomainName(),
				domainId: LS.getDomainId()
			};
			data.domainLogin = LS.getDomainLogin();
			data.cert = certSelect.value;
			data.period = period.value;
			data.legalLiterals = legalLiterals.getValue();
			downloadFacturae(data).then(r => { });
		});
		d.open();
	}

	duplicateInvoice() {
		let dupInv = this.invoice.clone();
		dupInv.id = undefined;
		dupInv.date = AonDateUtils.formatDate(new Date(), 'yyyy-MM-dd');
		dupInv.number = undefined;
		dupInv.reference = '';
		dupInv.status = 'inbox';
		dupInv.tbai = undefined;
		dupInv.tbaiUrl = undefined;
		dupInv.rectified = false;
		dupInv.file = undefined;
		dupInv.communicationInfo = undefined;
		dupInv.amortization = undefined;
		if (dupInv.finances) {
			dupInv.finances.forEach((item, i) => {
				dupInv.finances[i].id = undefined;
				dupInv.finances[i].due_date = AonDateUtils.formatDate(new Date(), 'yyyy-MM-dd');
			});
		}

		if (dupInv.details) {
			dupInv.details.forEach((item, i) => {
				dupInv.details[i].id = undefined;
			});
		}
		let aip = document.querySelector('aon-invoice-panel');
		aip.aonInvoice(dupInv.type, dupInv);
	}

	getCommentStatus() {
		if (this.invoice.isInbox())
			return 'Inbox';
		else if (this.invoice.isRejected()) {
			return 'Rechazado'
		} else return 'Papelera';
	}

	isInvofoxInvoice() {
		return this.getInvofoxDocumentId();
	}

	getInvofoxDocumentId() {
		return this.invoice.insight?.invofoxId;
	}

	trashInvoice() {
		this.updateCounter(this.getTrashFromOption(), OPTION.RAWDOC_TRASH, 1);
		this.updateCounter(this.getFutureTrashFromOption(), OPTION.FUTURE_RAWDOC_TRASH, 1);
		
		this.getInvoice().lastStatus = this.getInvoice().status;
		this.getInvoice().status = CONSTANT.TRASH;
		this.save(MSG.MOVED_TO_TRASH);
		this.reload();
	}

	getTrashFromOption() {
		if (this.getInvoice().isRejected() || (this.isInvofoxInvoice() && this.getInvoice().isOcrStatus(CONSTANT.DISCARDED, CONSTANT.PENDING_DECISSION)))
			return OPTION.RAWDOC_REJECT;
		else if (this.getInvoice().isEmitida()) return OPTION.PROFORMA_INVOICES;
		else if (this.getInvoice().isTicket()) return OPTION.RAWDOC_INBOX_TICKET_NEW;
		else return OPTION.RAWDOC_INBOX_RECEIVED_NEW;
	}

	getFutureTrashFromOption() {
		if (this.getInvoice().isRejected() || (this.isInvofoxInvoice() && this.getInvoice().isOcrStatus(CONSTANT.DISCARDED, CONSTANT.PENDING_DECISSION)))
			return OPTION.FUTURE_RAWDOC_REJECT;
		else if (this.getInvoice().isEmitida()) return OPTION.FUTURE_PROFORMA_INVOICES;
		else if (this.getInvoice().isTicket()) return OPTION.FUTURE_RAWDOC_INBOX_TICKET_NEW;
		else return OPTION.FUTURE_RAWDOC_INBOX_RECEIVED_NEW;
	}

	trashPendingInvoice() {

		let deleteText = `Tenga en cuenta que la anulación directa de una factura puede generar inconsistencias contables y fiscales. El proceso recomendado es emitir una factura rectificativa (nota de crédito), que permite corregir o dejar sin efecto la factura original de forma legal y trazable, manteniendo la integridad del registro contable.
			
			¿Desea continuar con la anulación directa de la factura?`;
		this.getApplication().confirmDialog(
			MSG.DELETE
			, this.invoice.isEmitida() ? deleteText : MSG.DELETE_CONFIRM + " la factura?"
			, () => {
				let data = { id: this.getInvoice().id };
				if (this.getInvoice().canBeAnnulled()) {
					let d = this.getApplication().getDialog();
					d.clear();
					if (!this.isMobile()) d.width = '400px';
					d.setTitle("Anular");
					let certSelect = createSelect("cert", "Certificado");
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
							this.updateCounter(getTrashPendingFromOption(this.invoice), OPTION.RAWDOC_TRASH, 1);
							this.updateCounter(getFutureTrashPendingFromOption(this.invoice), OPTION.FUTURE_RAWDOC_TRASH, 1);
							this.showMessage(MSG.DELETED_DATA);
							this.back();
						}).catch(e => {
							this.getApplication().stopLoader();
							this.showError(e);
						});
					});
					d.open();
				} else {
					this.getApplication().startLoader();
					deleteInvoice(data).then(() => {
						this.getApplication().stopLoader();
						this.updateCounter(getTrashPendingFromOption(this.invoice), OPTION.RAWDOC_TRASH, 1);
						this.updateCounter(getFutureTrashPendingFromOption(this.invoice), OPTION.FUTURE_RAWDOC_TRASH, 1);
						this.showMessage(MSG.DELETED_DATA);
						this.back();
					}).catch(e => {
						this.getApplication().stopLoader();
						this.showError(e);
					});
				}
			});
	}

	restoreInvoice() {
		let div = this.createDiv();
		
		let d = this.getApplication().getDialog();
		d.clear();
		if (!this.isMobile()) d.width = '400px';
		d.setTitle(MSG.RESTORE);
		d.setContent(div);
		
		let notify = new AonSwitch();
		notify.id = 'restoreNotifySwitch';
		notify.title = "Notificar por email";
		div.appendChild(notify);

		let email = new AonEmail();
		email.id = 'restoreInvoiceEmail';
		email.title = MSG.EMAIL;
		email.style.display = 'none';
		div.appendChild(email);

		let username;
		this.invoice.remarks.filter(f => f.status == 'Rechazado').forEach(r => username = r.user);
		if(username) {
			getUserEmail({userLogin: username}).then(emailData => {
				email.setValue(emailData.email);
			}).catch(e => {
				console.error("Error al obtener el email del usuario: " + e.message);
			});
		}
		notify.addEventListener(EVENT.CHANGE, () => {
			if(notify.isChecked()) {
				email.style.display = 'block';
			} else {
				email.style.display = 'none';
			}
		});

		let textArea = this.createElement('textarea');
		textArea.id = 'commentTextArea';
		textArea.maxLength = 256;
		textArea.className = 'aonTextarea';
		textArea.style.marginTop = '10px';
		div.appendChild(textArea);

		let acceptButton = d.addAcceptAction(() => {
			let dt = new Date()
			let m = dt.getMonth() + 1;
			let month = m < 10 ? '0' + m : m;
			let dateStr = dt.getDate() + '/' + month + '/' + dt.getFullYear() + ' ' + dt.getHours() + ':' + dt.getMinutes() + ':' + dt.getSeconds();
			let comment = {
				date: dateStr,
				user: LS.getDomainLogin(),
				status: 'Restaurado',
				action: {
					title: 'Restaurada',
					icon: MATERIAL_ICONS._360,
					color: 'green'
				},
				comment: textArea.value
			};
			this.invoice.remarks.push(comment);
			
			this.updateCounter(getRestoreFromOption(this.invoice), getRestoreToOption(this.invoice), 1);
			this.updateCounter(getFutureRestoreFromOption(this.invoice), getFutureRestoreToOption(this.invoice), 1);
			this.getInvoice().status = this.invoice.lastStatus || CONSTANT.INBOX;
			this.getInvoice().number = '';
			this.save(MSG.RESTORED_DATA);
			this.reload();

			// ENVIAR POR EMAIL SI SE HA INTRODUCIDO EMAIL
			if (notify.isChecked()) {
				sendInvoiceRestoreMail({to: email.value}).then(() => {
					this.showMessage("Email enviado correctamente");
				}).catch(e => {
					this.showError("Error al enviar el email: " + e.message);
				});
			}
		});
		acceptButton.disabled = this.invoice.isRejected();

		let ta = this.getElement('commentTextArea');
		ta.style.outline = 'none';
		ta.style.width = '100%';
		ta.style.height = '100px';
		ta.addEventListener(EVENT.KEYUP, () => {
			acceptButton.disabled = ta.value.trim().length === 0;
		});
		d.open();
	}

	removeInvoice() {
		let d = this.getApplication().getDialog();
		d.clear();
		if (!this.isMobile()) d.width = '400px';
		d.setTitle(MSG.DELETE_FOREVER);
		d.setContentHTML(MSG.DELETE_FOREVER_INVOICE_CONFIRMATION);
		d.addAcceptAction(() => {
			deleteRawdocInvoices([this.getInvoice().id]).then(() => {
				this.updateCounter(OPTION.RAWDOC_TRASH, undefined, -1);
				this.updateCounter(OPTION.FUTURE_RAWDOC_TRASH, undefined, -1);
				this.back()
			});
		});
		d.open();
	}

	updateCounter(from, to, count) {
		if (!to) {
			addCounter(from, count);
			this.getApplication().getParent().updateCounterSpan(from);
		} else {
			transferCounter(from, to, count);
			this.getApplication().getParent().updateCounterSpan(from);
			this.getApplication().getParent().updateCounterSpan(to);
		}
	}

	// COMPONENT UTILS

	createAonNumber(id, title, value, decimals) {
		let aonNumber = createNumber(id, title);
		aonNumber.format = CONSTANT.TRUE;
		aonNumber.decimals = "2";
		aonNumber.minDecimal = "2";
		aonNumber.maxDecimal = decimals || "2";
		aonNumber.readonly = this.invoice.isReadonly();
		aonNumber.value = value || 0.0;
		return aonNumber;
	}

	createTableSpan(width, marginRight) {
		let span = this.createSpan();
		span.style.width = width;
		span.style.marginRight = marginRight;
		return span;
	}

}
if (!window.customElements.get(TAG.AON_INVOICE)) {
	window.customElements.define(TAG.AON_INVOICE, AonInvoice);
}
