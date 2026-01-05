import { AonElement } from '../../components/AonElement.js';
import { Apps, ClassicApps, getAppsByDur } from '../../services/app.js';
import { getDomainNotice, getDomainUserRoles, getTaskCount, getTaskHolder, getTimeControl, getAttach, getPeriodLaboral, getTrailData, getCompanyOne, getCompanyActivities, saveInvoiceConfiguration, getInvoiceConfiguration, closeSession } from '../../services/service.js';
import { getAccessBidoq } from '../../services/bidoqService.js';
import { DomainUserRoles } from '../../models/DomainUserRoles.js';
import { CONSTANT, CSS, EVENT, MATERIAL_ICONS, MSG, TAG } from '../../environments/environments.js';
import { AonSign } from '../timecontrol/aon-sign.js';
import { AonTimecontrol } from '../timecontrol/aon-timecontrol.js';
import { AonMessenger } from '../messenger/aon-messenger.js';
import { AonIconButton } from '../../components/aon-icon-button.js';
import { AonInvoicePanel } from '../invoice/aon-invoice-panel.js';
import * as OPTION from '../invoice/InvoiceOptions.js';
import * as GWT from "../../gwt/gwt.js";
import * as LS from "../../services/localStorageService.js";
import { TASK_SOURCE } from '../messenger/MessengerEnums.js';
import { AonFiscal } from '../fiscal/aon-fiscal.js';
import { AonLaboral } from '../laboral/aon-laboral.js';
import { AonSaltra } from '../laboral/aon-saltra.js';
import { AonAccounting } from '../accounting/aon-accounting.js';
import { AonWarehouse } from '../warehouse/aon-warehouse.js';
import { AonCard } from '../../components/aon-card.js';
import { AonDialog } from '../../components/aon-dialog';
import { AonDashboardGraphicsTrial } from '../accounting/aon-graphics-dashboard-trial.js';
import { AonFiscalCard } from '../fiscal/aon-fiscal-card.js';
import { AonModelMatrixCard } from '../fiscal/aon-model-matrix-card.js';
import { AonStatistics } from '../timecontrol/time-control/statistics/aon-statistics.js';
import { AonPayrollCard } from '../laboral/payroll/aon-payroll-card.js';
import { AonMessengerCard } from '../messenger/aon-messenger-card.js';
import { getModelsFiscal } from '../../services/service.js';
import { sortBy, waitEl } from '../../services/utils.js';
import { FiscalUtils } from '../fiscal/FiscalUtils.js';
import { AonBankCard } from '../accounting/aon-bank-card.js';
import { AonCompanyCostsCard, paintCompanyCostPieChart } from '../laboral/company/aon-company-costs-card.js';
import { AonDashboardChargePayments } from '../accounting/aon-dashboard-charge-payments.js';
import { AonMarketing } from '../marketing/aon-marketing.js';
import { MessegerUtils } from '../messenger/utils/MessengerUtils.js';
import { AonTrial } from '../invoice/aon-trial.js';
import { AonDashboardSalesPurchases } from '../accounting/aon-dashboard-sales-purchases.js';
import { AonJsfAccountingGraph, AonJsfPayrollGraph, AonJsfContractGraph } from '../aon-jsf-app.js';
import { createSelect } from '../../components/CreateComponent.js';
import { InvoiceCommunicationConfiguration } from '../../models/InvoiceCommunicationConfiguration.js';
import { AonInvoiceCommunication } from '../invoice/aon-invoice-communication.js';
import { isPersonaFisica, isValid } from '../../services/documentUtils.js';
import { Invoice } from '../invoice/Invoice.js';
import { AonCheckbox } from '../../components/aon-checkbox.js';
import { AonBasicTable } from '../../components/aon-basic-table.js';

export class AonDesktop extends AonElement {
	dur;
	AON_DESKTOP;
	appOption;
	SIDENAV_ACTIVITY_SUMMARY;


	conditionsAccepted; // Invoice Configuration  conditions.

	static get observedAttributes() {
		return [];
	}

	get id() {
		return this.getAttribute('id');
	}

	set id(id) {
		this.setAttribute('id', id);
	}

	constructor() {
		super();
	}

	initialize() {
		this.id = this.id || 'aonDesktop';
		this.AON_DESKTOP = 'aonDesktopMain';
		this.SIDENAV_ACTIVITY_SUMMARY = [];
		this.TIMECONTROL_TITLE = this.id + 'TimecontrolTitle';
		this.TIMECONTROL_SIGN = this.id + 'TimecontrolSign';
		this.INVOICE_CONFIGURATION_DIALOG = this.id + 'InvoiceConfigurationDialog';
	}

	getDur() {
		return this.dur;
	}

	async connectedCallback() {
		if (LS.isNewTheme()) {
			/*let span = this.getElement('aonHeaderHome');
			if (span) span.style.display = 'none';*/

			let expandButtonDiv = this.getElement('aonExpandButtonDiv');
			if (expandButtonDiv) expandButtonDiv.style.display = 'block';
		}

		this.initialize();
		
		let r = await getDomainUserRoles({});
		this.dur = new DomainUserRoles(r);
		this.build();

		await this.getInvoiceConfiguration();
		if(!this.checkConfigurationComplete(this.ic, false))
			this.buildInvoiceConfigurationDialog();
	
	}

	disconnectedCallback() {
		if (LS.isNewTheme()) {
			let span = this.getElement('aonHeaderHome');
			if (span) span.style.display = 'block';

			let expandButtonDiv = this.getElement('aonExpandButtonDiv');
			if (expandButtonDiv) expandButtonDiv.style.display = 'none';
		}
	}

	async getInvoiceConfiguration() {
		this.ic = await getInvoiceConfiguration();
	}

	buildInvoiceConfigurationDialog() {
		let dialog = this.getElement(this.INVOICE_CONFIGURATION_DIALOG);
		if(!dialog) {
			dialog = new AonDialog();
			dialog.id = this.INVOICE_CONFIGURATION_DIALOG;
			this.appendChild(dialog);
			dialog.autoclose = false;
			dialog.setDialogWidth('90%');
			dialog.removeCliclOutsideDialogClose();
			this.getElement(dialog.BUTTON_CLOSE).style.display = 'none';
			
			let communication =  new AonInvoiceCommunication();
			communication.setConfiguration(this.ic);
			communication.onChange(() => {
				this.ic = communication.getConfiguration();
				let com = communication.getCommunicationConfiguration();
				if(com instanceof InvoiceCommunicationConfiguration) {
					this.ic.communication = com.toJSON();
				} else {
					this.ic.communication = new InvoiceCommunicationConfiguration(com).toJSON();
				}
			});

			let div = this.createDiv();
		
			let aviso = this.createDiv();
			aviso.style.backgroundColor = '#fde400ff';
			aviso.style.padding = '10px';
			aviso.style.margin = '10px';
			aviso.innerHTML = "<span style='color:red'>Aviso Importante</span>: Como usuario de AON SIF (Sistema de Facturación) adaptado a la normativa de la \"ley antifraude\" y regulado por el Reglamento RRSIF (RD 1007/2023), debe cumplimentar los datos que se solicitan a continuación. El Cliente es el único responsable de la correcta activación de la modalidad de comunicación, configuración del software y validación de su certificado digital en el software para la comunicación de facturas a la Administración Tributaria (AEAT o Haciendas Forales) a través de los sistemas VeriFactu, No VeriFactu, LROE o Ticket BAI. <br><b>AON SOLUTIONS, S.L.U. no será responsable</b> de información no veraz o incorrecta incluida por el usuario en el SIF.</br>";

			div.appendChild(aviso);
			div.appendChild(communication);

			dialog.clear();
			dialog.setTitle(MSG.INVOICE_CONFIGURATION);
			dialog.setContent(div);

			this.buildConditions(div);

			dialog.addAction2({
					id: "Salir",
					title: "Salir",
					icon: MATERIAL_ICONS.ARROW_BACK,
					position: "left",
				}, () => {
					if(this.getDur().isParentUser()) {
						this.getAonHeader().goToParent();
					} else {
						closeSession();
					}
				});

			dialog.addAcceptAction(() => {		
				if(this.checkConfigurationComplete(this.ic, true)) { 
					saveInvoiceConfiguration(this.ic);
				} else {
					this.buildInvoiceConfigurationDialog();
				}
			});

			dialog.open();
		} else {
			// Existe simplemente lo abrimos
			dialog.open();
		}
	}

	getAonHeader() {
		return document.querySelector(TAG.AON_HEADER);
	}

	buildConditions(parent) {
		let conditions = this.createDiv();
		parent.appendChild(conditions);
		let table = new AonBasicTable();
		// table.style.top = '20px';
		// table.style.position = 'relative'; 
		conditions.appendChild(table);
		table.addRow();
		
		let checkBox = new AonCheckbox();
		checkBox.setCh
		let td = table.addCell(checkBox)
		// td.style.width = '15px';
		let span3 = this.createElement(TAG.SPAN);
		span3.innerHTML = 'He leido y acepto las <a target="_blank" class="aonLink" href="http://aonsolutions.es/docs/aon_condiciones_generales_del_contrato.pdf">CONDICIONES GENERALES</a> del contrato de licencia de software y los términos <a target="_blank" class="aonLink" href="https://aonsolutions.es/docs/Aon-Declaracion%20Responsable%20VeriFactu.pdf">DECLARACIÓN RESPONSABLE del SIF</a> (Sistema Informático de facturación)';
		table.addCell(span3);
		
		checkBox.addEventListener(EVENT.CHANGE, () => {
			this.conditionsAccepted = checkBox.isChecked();
		});
	}

	checkConfigurationComplete(config, showError) {
		if(showError && !this.conditionsAccepted) {
			this.showMessageError("Debe aceptar las condiciones para continuar.");
			return false;
		}
		if(!config || !config.company || !config.company.document || !config.communication) return false;
		let icc = new InvoiceCommunicationConfiguration(config.communication);
		if(!isValid(config.company.document)) {
			if(showError) this.showMessageError("El NIF/CIF de la empresa no es válido.");
			return false;
		}
		if(isPersonaFisica(config.company.document) && !(config.company.person || config.person)) {
			if(showError) this.showMessageError("Es obligatorio rellenar todos los datos de la persona física.");
			return false;
		} 
		if(showError && !icc.isNoSif() && (icc.isCommonTerritory() || icc.isCanarias()) && !icc.isSii() && !icc.willBeSii() && !icc.isVerifactu() && !icc.willBeVerifactu() && !icc.isNoVerifactu() && !icc.willBeNoVerifactu()) {
			this.showMessageError("Es obligatorio selecionar Verifactu, No Verifactu o SII para empresas del territorio común.");
			return false;
		} 
		if(!icc.getAdministration().isUnknown() && (icc.hasCommunication() || icc.willBeCommunication() || icc.isNoSif())) {
			return true;
		} else {
			if(showError) this.showMessageError("Es obligatorio selecionar una administración para la comunicación electrónica de facturas.");
			return false;
		}
	}

	build() {
		let company = JSON.parse(localStorage.getItem("company"));
		this.innerHTML = `<aon-application id="${this.AON_DESKTOP}" main="true"></aon-application>`;
		let aonDesktop = this.getElement(this.AON_DESKTOP);

		let filter = {
			attachType: 'registry',
			attachModule: company.registry,
			type: 0
		};

		let parentFilter = {
			attachType: 'registry',
			domainId: company.parentId,
			type: 0
		};
		let f = this.getDur().hasCustomView() || this.getDur().isEmployee()
			? filter : parentFilter;
		// Content
		let content = this.createElement(TAG.DIV);
		content.id = "content";
		aonDesktop.setContent(content);

		this.createDashboard(content, company);
		// Quitamos el toolbar
		const toolbar = this.querySelector('aon-toolbar');
		if (toolbar)
			toolbar.remove();
	}

	async createDashboard(parent, company) {
		// Clear parent
		while (parent.lastElementChild) {
			parent.removeChild(parent.lastElementChild);
		}

		let dashboard = this.createElement(TAG.DIV);
		dashboard.id = "dashboard";
		dashboard.className = CSS.AON_DASHBOARD;
		parent.appendChild(dashboard);

		// Cards Panel
		let cardsPanel = this.createElement(TAG.DIV);
		cardsPanel.className = CSS.AON_CARDS_PANEL;
		cardsPanel.id = "cardsPanel";
		dashboard.appendChild(cardsPanel);

		// ------------------------------------------------
		// Timecontrol 
		// ------------------------------------------------
		if (this.getDur().isTimecontrol() && this.getDur().isEmployee()) {
			let timecontrolCard = new AonCard();
			timecontrolCard.classList.add(CSS.AON_DASHBOARD_CARD);
			timecontrolCard.id = CONSTANT.TIMECONTROL;
			timecontrolCard.title = MSG.TIMECONTROL;
			timecontrolCard.setApp(Apps.TIMECONTROL);
			timecontrolCard.addEventListener(EVENT.CLICK_TITLE, () => this.appSelection(Apps.TIMECONTROL.app));
			cardsPanel.appendChild(timecontrolCard);

			getTimeControl().then(r => {
				let aonSign = new AonSign();
				timecontrolCard.clearContent();
				timecontrolCard.setContent(new AonStatistics());
				timecontrolCard.setContent(aonSign);

				aonSign.buildSignin(r);
				let aonHeader = this.getElement('aonHeader');
				aonHeader.timeControlStatus(r);
			});
		}

		// ------------------------------------------------
		// Fiscal
		// ------------------------------------------------
		if (this.getDur().isFiscalManager()) {
			// Modelos
			let fiscalCard = new AonCard();
			fiscalCard.classList.add(CSS.AON_DASHBOARD_CARD);
			fiscalCard.id = "fiscalCard";
			fiscalCard.title = MSG.TAXES;
			fiscalCard.setApp(Apps.FISCAL);
			cardsPanel.appendChild(fiscalCard);

			let aonModelMatrixCard = new AonModelMatrixCard();
			fiscalCard.setContent(aonModelMatrixCard);

		} else if (this.getDur().isFiscal()) {
			// Impuestos
			let fiscalCard = new AonCard();
			fiscalCard.classList.add(CSS.AON_DASHBOARD_CARD);
			fiscalCard.id = "fiscalCard";
			fiscalCard.title = MSG.TAXES;
			fiscalCard.setApp(Apps.FISCAL);
			fiscalCard.addEventListener(EVENT.CLICK_TITLE, async () => {
				let fiscalFilter = await aonFiscalCard.getFilter();
				this.appSelectionFilter(Apps.FISCAL.app, fiscalFilter);
			});
			cardsPanel.appendChild(fiscalCard);
			fiscalCard.insertAdjacentHTML('beforeend', "<aon-dialog-menu id='aonCardFiscalOption'> </aon-dialog-menu>");

			let fiscalDefaultFilter = this.getFiscalFilter();
			let aonFiscalCard = new AonFiscalCard(fiscalDefaultFilter);
			fiscalCard.setContent(aonFiscalCard);

			let spanPeriod = this.createElement(TAG.SPAN);
			if (fiscalDefaultFilter) {
				fiscalDefaultFilter.then(filter => spanPeriod.innerHTML = filter ? filter.title : '');
			}
			fiscalCard.addSection2(spanPeriod);

			fiscalCard.addTitleButton(MSG.OPTIONS, MATERIAL_ICONS.MORE_VERT, false, () => this.filterFiscal());
			await this.filterFutureFiscal();
		}

		// ------------------------------------------------
		// Accounting
		// ------------------------------------------------
		let aonJsfAccountingGraphCard ;
		if (this.getDur().isAccountingManager() ) {
			let accountingGraphCard = new AonCard();
			accountingGraphCard.classList.add(CSS.AON_DASHBOARD_CARD);
			accountingGraphCard.id = "accountingGraphCard";
			accountingGraphCard.title = "Contabilidad";
			accountingGraphCard.message = "Apuntes Contables";
			accountingGraphCard.setApp(Apps.ACCOUNTING);
			cardsPanel.appendChild(accountingGraphCard);

			aonJsfAccountingGraphCard = new AonJsfAccountingGraph();
			accountingGraphCard.setContent(aonJsfAccountingGraphCard);
		} else if (this.getDur().isAccounting()) {
			// PyG Card
			let defaultYear = new Date().getFullYear();

			if (new Date().getTime() < new Date(new Date().getFullYear(), 0, 31))
				defaultYear = defaultYear - 1;

			let aonDashboardGraphicsTrial = new AonDashboardGraphicsTrial("yearly", defaultYear);
			aonDashboardGraphicsTrial.id = "aonDashboardGraphicsTrial";

			let pygCard = new AonCard();
			pygCard.classList.add(CSS.AON_DASHBOARD_CARD);
			pygCard.id = "pygCard";
			pygCard.title = "Pérdidas y Ganancias";
			pygCard.setApp(Apps.ACCOUNTING);
			cardsPanel.appendChild(pygCard);
			pygCard.insertAdjacentHTML('beforeend', "<aon-dialog-menu id='aonCardPyGOption'> </aon-dialog-menu>");

			pygCard.setContent(aonDashboardGraphicsTrial);
			pygCard.addTitleButton(MSG.OPTIONS, MATERIAL_ICONS.MORE_VERT, false, () => this.filterPyG(pygCard));

			pygCard.addEventListener(EVENT.CLICK_TITLE, () => {
				let dashboardGraphicsTrial = this.getElement('aonDashboardGraphicsTrial');
				this.appSelectionFilter(Apps.ACCOUNTING.app, dashboardGraphicsTrial.getFilter());
			});
		}

		// ------------------------------------------------
		// Payroll
		// ------------------------------------------------
		if (this.getDur().isPayrollManager()  ) {
			let contractGraphCard = new AonCard();
			let payrollGraphCard = new AonCard();
			payrollGraphCard.classList.add(CSS.AON_DASHBOARD_CARD);
			payrollGraphCard.id = "payrollGraphCard";
			payrollGraphCard.title = "Nominas";
			payrollGraphCard.message = "Nóminas"; //MSG.SALARIES;
			payrollGraphCard.setApp(Apps.PAYROLL);
			cardsPanel.appendChild(payrollGraphCard);

			let aonJsfPayrollGraphCard = new AonJsfPayrollGraph();
			let aonJsfContractGraphCard = new AonJsfContractGraph();
			
			if ( aonJsfAccountingGraphCard ) {
				aonJsfAccountingGraphCard.isLoaded().then( () => {
					payrollGraphCard.setContent(aonJsfPayrollGraphCard);
					aonJsfPayrollGraphCard.isLoaded().then( () => {
						contractGraphCard.setContent(aonJsfContractGraphCard);
					});
				});
			} else {
				payrollGraphCard.setContent(aonJsfPayrollGraphCard);
				aonJsfPayrollGraphCard.isLoaded().then( () => {
					contractGraphCard.setContent(aonJsfContractGraphCard);
				});
			}

			contractGraphCard.classList.add(CSS.AON_DASHBOARD_CARD);
			contractGraphCard.id = "contractGraphCard";
			contractGraphCard.title = "Contratos";
			contractGraphCard.message = `Contratos`; //MSG.CONTRACTS;
			contractGraphCard.setApp(Apps.PAYROLL);
			cardsPanel.appendChild(contractGraphCard);
		} if ( this.getDur().isPayrollManager() ){
			let payrollCard = new AonCard();
			payrollCard.classList.add(CSS.AON_DASHBOARD_CARD);
			payrollCard.id = CONSTANT.PAYROLL;
			payrollCard.title = MSG.LABORAL_COSTS;
			payrollCard.setApp(Apps.PAYROLL);
			payrollCard.addEventListener(EVENT.CLICK_TITLE, () => {
				this.appSelectionFilter(Apps.PAYROLL.app, this.getElement('aon-company-costs-card').getFilter());
			});
			cardsPanel.appendChild(payrollCard);
			payrollCard.insertAdjacentHTML('beforeend', "<aon-dialog-menu id='aonCardPayrollOption'> </aon-dialog-menu>");

			let lastMonthFilter = getPeriodLaboral("last_month");
			lastMonthFilter.period = lastMonthFilter.value;

			let aonCompanyCostsCard = new AonCompanyCostsCard(lastMonthFilter);
			payrollCard.setContent(aonCompanyCostsCard);
			await paintCompanyCostPieChart();
			payrollCard.addTitleButton(MSG.OPTIONS, MATERIAL_ICONS.MORE_VERT, false, () => this.filterPayrollStatics(payrollCard));
		} else if (this.getDur().isPayroll()) {
			// Nominas
			let payrollCard = new AonCard(() => this.appSelection(Apps.PAYROLL.app));
			payrollCard.classList.add(CSS.AON_DASHBOARD_CARD);
			payrollCard.id = "payroll";
			payrollCard.title = MSG.PAYSHEETS;
			payrollCard.setApp(Apps.PAYROLL);
			payrollCard.addEventListener(EVENT.CLICK_TITLE, () => this.appSelection(Apps.PAYROLL.app));
			cardsPanel.appendChild(payrollCard);

			let aonPayrollCard = new AonPayrollCard();
			payrollCard.setContent(aonPayrollCard);
		}

		// ------------------------------------------------
		// Usage Summary
		// ------------------------------------------------
		if ((this.getDur().isInvoice() || this.getDur().isAccounting()) && this.getDur().isTrial() ) {
			// Trial Card
			let trialCard = new AonCard();
			trialCard.classList.add(CSS.AON_DASHBOARD_CARD);
			trialCard.id = "trial";
			trialCard.title = "Versión Evaluación (Resumen de uso)";
			trialCard.setApp(this.getDur().isInvoice() ? Apps.INVOICE : Apps.ACCOUNTING);
			cardsPanel.appendChild(trialCard);

			trialCard.setContent(new AonTrial());

			trialCard.addEventListener(EVENT.CLICK_TITLE, () => {
				this.appSelection(this.getDur().isInvoice() ? Apps.INVOICE.app : Apps.ACCOUNTING.app);
			});
		}

		// ------------------------------------------------
		// Sales & Purchases ( only for users ¿ portal ?)
		// ------------------------------------------------
		if ((this.getDur().isInvoiceUser()) || (this.getDur().isAccountingUser())) {
			// Ventas y Gastos Card
			let defaultYear = new Date().getFullYear();

			if (new Date().getTime() < new Date(new Date().getFullYear(), 0, 31))
				defaultYear = defaultYear - 1;

			let aonDashboardSalesPurchases = new AonDashboardSalesPurchases("yearly", defaultYear);
			aonDashboardSalesPurchases.id = "aonDashboardSalesPurchases";

			let vygCard = new AonCard();
			vygCard.classList.add(CSS.AON_DASHBOARD_CARD);
			vygCard.id = "vygCard";
			vygCard.title = "Ventas y Gastos";
			vygCard.setApp(this.getDur().isInvoice() ? Apps.INVOICE : Apps.ACCOUNTING);
			cardsPanel.appendChild(vygCard);
			vygCard.insertAdjacentHTML('beforeend', "<aon-dialog-menu id='aonCardVyGOption'> </aon-dialog-menu>");

			vygCard.setContent(aonDashboardSalesPurchases);
			vygCard.addTitleButton(MSG.OPTIONS, MATERIAL_ICONS.MORE_VERT, false, () => this.filterVyG(vygCard));

			vygCard.addEventListener(EVENT.CLICK_TITLE, () => {
				this.appSelection(this.getDur().isInvoice() ? Apps.INVOICE.app : Apps.ACCOUNTING.app);
			});

			// Cobros y Pagos 
			let cypCard = new AonCard();
			cypCard.classList.add(CSS.AON_DASHBOARD_CARD);
			cypCard.id = "cyp";
			cypCard.title = "Cobros y Pagos";
			cypCard.setApp(this.getDur().isInvoice() ? Apps.INVOICE : Apps.ACCOUNTING);
			cardsPanel.appendChild(cypCard);
			cypCard.insertAdjacentHTML('beforeend', "<aon-dialog-menu id='aonCardCyPOption'> </aon-dialog-menu>");

			cypCard.setContent(new AonDashboardChargePayments("current_month"));
			cypCard.addTitleButton(MSG.OPTIONS, MATERIAL_ICONS.MORE_VERT, false, () => this.filterCyP(cypCard));

			cypCard.addEventListener(EVENT.CLICK_TITLE, () => {
				this.appSelection(this.getDur().isInvoice() ? Apps.INVOICE.app : Apps.ACCOUNTING.app);
			});
		}

		// ------------------------------------------------
		// Banks
		// ------------------------------------------------
		if (this.getDur().isBank()) {
			// Bancos
			let bankCard = new AonCard();
			bankCard.classList.add(CSS.AON_DASHBOARD_CARD);
			bankCard.id = "bankCard";
			bankCard.title = MSG.BANKS;
			bankCard.setApp(Apps.ACCOUNTING);
			bankCard.addEventListener(EVENT.CLICK_TITLE, () => {
				this.appSelection(Apps.ACCOUNTING.app);
				this.isElementLoaded("#aonAccountingSidenavbanksnordigen")
					.then(selector => {
						selector.click();
					});
			});
			cardsPanel.appendChild(bankCard);

			let aonBankCard = new AonBankCard(company.registry);
			bankCard.setContent(aonBankCard);
		}

		// ------------------------------------------------
		// Documental | No se quiere por ahora
		// ------------------------------------------------
		// if (this.getDur().isDocumentalUser()) {
		// 	// Documental
		// 	let documentalCard = new AonCard();
		// 	documentalCard.classList.add(CSS.AON_DASHBOARD_CARD);
		// 	documentalCard.id = "documentalCard";
		// 	documentalCard.title = MSG.DOCUMENTARY;
		// 	documentalCard.setApp(Apps.DOCUMENTAL);
		// 	documentalCard.addEventListener(EVENT.CLICK_TITLE, () => {
		// 		this.appSelection(Apps.DOCUMENTAL.app);
		// 	});
		// 	cardsPanel.appendChild(documentalCard);

		// 	let aonBankCard = new AonDocumentalCard();
		// 	documentalCard.setContent(aonBankCard);
		// }

		// ------------------------------------------------
		// Requests ( Issues )
		// ------------------------------------------------
		// if (this.getDur().isMessengerManager() || this.getDur().isMessenger()) {
		// 	// Solicitudes
		// 	let messengerCard = new AonCard();
		// 	messengerCard.classList.add(CSS.AON_DASHBOARD_CARD);
		// 	messengerCard.id = "messengerCard";
		// 	// messengerCard.title = "Solicitudes";
		// 	messengerCard.message = MSG.REQUESTS;
		// 	messengerCard.setApp(Apps.MESSENGER);
		// 	messengerCard.addEventListener(EVENT.CLICK_TITLE, () => this.appSelection(Apps.MESSENGER.app));
		// 	cardsPanel.appendChild(messengerCard);
		// 	messengerCard.getCardTitle1().style.cursor = 'pointer';
		// 	let aonMessengerCard = new AonMessengerCard(() => this.appSelection(Apps.MESSENGER.app));

		// 	messengerCard.setContent(aonMessengerCard);

		// 	let messages = await MessegerUtils.getMeseggers();
		// 	let messageBadge = MessegerUtils.getMessageBadge(messages.length);
		// 	messageBadge.addEventListener(EVENT.CLICK, () => this.appSelection(Apps.MESSENGER.app));
		// 	messengerCard.addSection2(messageBadge);

		// 	messengerCard.firstChild.style.minHeight = "28rem";
		// 	messengerCard.firstChild.children.item(1).style.height = "22.5rem";
		// 	messengerCard.firstChild.style.margin = '0';
		// }

		// Check if company has address, required for TRIAL
		let data = {
			additional_info: ['ADDRESSES']
		};

		getCompanyOne(data).then(cp => {
			if (!cp.addresses || cp.addresses.length === 0) {
				this.getApplication().confirmDialog(
					"Dirección",
					"No existe una direccion para esta empresa. Cumplimentelá antes de continuar.",
					async () => {
						let aonHeader = this.getElement('aonHeader');
						aonHeader.aonConfiguration();

						waitEl(`ul[id*="aonConfigurationSidenavEMPRESAList"] li[id*="aonConfigurationSidenav"]`).then(liGeneralInfo => {
							liGeneralInfo.click();
						});
					}
				);
			}
		});
	}

	async isElementLoaded(selector) {
		while (document.querySelector(selector) === null) {
			await new Promise(resolve => requestAnimationFrame(resolve))
		}
		return document.querySelector(selector);
	};

	async isElementLoaded(selector) {
		while (document.querySelector(selector) === null) {
			await new Promise(resolve => requestAnimationFrame(resolve))
		}
		return document.querySelector(selector);
	};

	filterPyG(pygCard) {
		let button = this.getElement('pygCardTitleSection2OpcionesButtonIconButton');
		let top = button.getBoundingClientRect().top;
		const left = button.getBoundingClientRect().left;

		let pyGYearSelect = this.getElement('pyGyearelect');
		let period = JSON.parse(pyGYearSelect.value);
		let pygyYear = period.name;

		let aonDashboardGraphicsTrial = this.getElement('aonDashboardGraphicsTrial');

		let d = document.getElementById('aonCardPyGOption');

		const anual = {
			name: 'Vista Anual',
			title: "Vista Anual",
			icon: 'calendar_today',
			backgroundColor: "#4472C4",
			fn: () => {
				pygCard.clear();
				aonDashboardGraphicsTrial = new AonDashboardGraphicsTrial("yearly", pygyYear);
				aonDashboardGraphicsTrial.id = "aonDashboardGraphicsTrial";
				pygCard.setContent(aonDashboardGraphicsTrial);
			}
		};

		const trimestral = {
			name: 'Vista Trimestral',
			title: "Vista Trimestral",
			icon: 'calendar_today',
			backgroundColor: "#4472C4",
			fn: () => {
				pygCard.clear();
				aonDashboardGraphicsTrial = new AonDashboardGraphicsTrial("quarterly", pygyYear);
				aonDashboardGraphicsTrial.id = "aonDashboardGraphicsTrial";
				pygCard.setContent(aonDashboardGraphicsTrial);
			}
		};

		const mensual = {
			name: "Vista Mensual",
			title: "Vista Mensual",
			icon: 'calendar_today',
			backgroundColor: "#4472C4",
			fn: () => {
				pygCard.clear();
				aonDashboardGraphicsTrial = new AonDashboardGraphicsTrial("monthly", pygyYear);
				aonDashboardGraphicsTrial.id = "aonDashboardGraphicsTrial";
				pygCard.setContent(aonDashboardGraphicsTrial);
			}
		};

		let options = [anual, trimestral, mensual];

		d.setMenuOptions(options, top, left);
		d.open();
	}

	filterVyG(vygCard) {
		let button = this.getElement('vygCardTitleSection2OpcionesButtonIconButton');
		let top = button.getBoundingClientRect().top;
		const left = button.getBoundingClientRect().left;

		let vyGYearSelect = this.getElement('vyGyearSelect');
		let period = JSON.parse(vyGYearSelect.value);
		let vygYear = period.name;

		let aonDashboardSalesPurchases = this.getElement('aonDashboardSalesPurchases');

		let d = document.getElementById('aonCardVyGOption');

		const anual = {
			name: 'Vista Anual',
			title: "Vista Anual",
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
			title: "Vista Trimestral",
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

	filterCyP(cypCard) {
		let button = this.getElement('cypTitleSection2OpcionesButtonIconButton');
		let top = button.getBoundingClientRect().top;
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

	filterPayrollStatics(payrollCard) {
		let button = this.getElement('payrollTitleSection2OpcionesButtonIconButton');
		let top = button.getBoundingClientRect().top;
		const left = button.getBoundingClientRect().left;

		let d = document.getElementById('aonCardPayrollOption');

		let periods = getPeriodLaboral();
		periods.pop();

		let options = periods.map(option => ({
			...option,
			period: option.value,
			fn: async () => {
				payrollCard.setContent(new AonCompanyCostsCard({ ...option, period: option.value }));
				await paintCompanyCostPieChart();
			}
		}));

		d.setMenuOptions(options, top, left);
		d.open();
	}

	async getFiscalFilter() {
		let result = await this.getFilterModels();
		if (!result || result.length == 0) return undefined;
		let period = result[0];
		return { year: period.year, period: period.period, title: period.periodText + " " + period.year };
	}

	async getFilterModels() {
		const datos = await getModelsFiscal();
		let orderDatos = [];
		if (datos) {
			orderDatos = sortBy(datos, 'year', 'desc')
				.map((model) => FiscalUtils.getModelNew(model));
		}

		const result = orderDatos.filter(function(a) {
			var key = a.year + '|' + a.period;
			if (!this[key]) {
				this[key] = true;
				return true;
			}
		}, Object.create(null));

		result.sort(function(a, b) {
			var aSize = a.year;
			var bSize = b.year;
			var aLow = a.period;
			var bLow = b.period;

			if (aSize == bSize) {
				return (aLow < bLow) ? -1 : (aLow > bLow) ? 1 : 0;
			}
			else {
				return (aSize < bSize) ? -1 : 1;
			}
		});

		return result.reverse();
	}

	filterFiscal() {
		let button = this.getElement('fiscalCardTitleSection2OpcionesButtonIconButton');
		let top = button.getBoundingClientRect().top;
		const left = button.getBoundingClientRect().left;

		let d = document.getElementById('aonCardFiscalOption');

		let aonFiscalCard = document.getElementById('aonFiscalCard');
		let result = aonFiscalCard.getFilterModels;

		let options = [];

		if (!result || result.length === 0) {
			let period = this.getCurrentFiscalPeriod();
			let year = new Date().getFullYear();

			// Borrador actual (para empresas nuevas o sin modelos existentes)
			let periodCurrent;
			let periodTextCurrent;
			let yearCurrent;

			if (period == "T1") {
				periodCurrent = "T1";
				periodTextCurrent = "1º Trim.";
				yearCurrent = year;
			} else if (period == "T2") {
				periodCurrent = "T2";
				periodTextCurrent = "2º Trim.";
				yearCurrent = year;
			} else if (period == "T3") {
				periodCurrent = "T3";
				periodTextCurrent = "3º Trim.";
				yearCurrent = year;
			} else {
				periodCurrent = "T4";
				periodTextCurrent = "4º Trim.";
				yearCurrent = year;
			}

			let periodOptCurrent = {
				name: periodTextCurrent + " " + yearCurrent + " (B)",
				title: periodTextCurrent + " " + yearCurrent + " (Borrador)",
				icon: MATERIAL_ICONS.EVENT,
				backgroundColor: "#4472C4",
				fn: () => aonFiscalCard.filterEstimationTable({ year: yearCurrent, period: periodCurrent, title: "Borrador " + periodTextCurrent + " " + yearCurrent })
			};
			options.push(periodOptCurrent);
		} else {
			// Borrador future
			let periodFuture;
			let periodTextFuture;
			let yearFuture;
			if (result[0].period == "T1") {
				periodFuture = "T2";
				periodTextFuture = "2º Trim.";
				yearFuture = result[0].year;
			} else if (result[0].period == "T2") {
				periodFuture = "T3";
				periodTextFuture = "3º Trim.";
				yearFuture = result[0].year;
			} else if (result[0].period == "T3") {
				periodFuture = "T4";
				periodTextFuture = "4º Trim.";
				yearFuture = result[0].year;
			} else {
				periodFuture = "T1";
				periodTextFuture = "1º Trim.";
				yearFuture = result[0].year + 1;
			}
			let periodOptFuture = {
				name: periodTextFuture + " " + yearFuture + " (B)",
				title: periodTextFuture + " " + yearFuture + " (Borrador)",
				icon: MATERIAL_ICONS.EVENT,
				backgroundColor: "#4472C4",
				fn: () => aonFiscalCard.filterEstimationTable({ year: yearFuture, period: periodFuture, title: "Borrador " + periodTextFuture + " " + yearFuture })
			};
			options.push(periodOptFuture);

			// Borrador
			let periodCurrent;
			let periodTextCurrent;
			let yearCurrent;

			if (result[0].period == "T1") {
				periodCurrent = "T1";
				periodTextCurrent = "1º Trim.";
				yearCurrent = result[0].year;
			} else if (result[0].period == "T2") {
				periodCurrent = "T2";
				periodTextCurrent = "2º Trim.";
				yearCurrent = result[0].year;
			} else if (result[0].period == "T3") {
				periodCurrent = "T3";
				periodTextCurrent = "3º Trim.";
				yearCurrent = result[0].year;
			} else {
				periodCurrent = "T4";
				periodTextCurrent = "4º Trim.";
				yearCurrent = result[0].year;
			}

			let periodOptCurrent = {
				name: periodTextCurrent + " " + yearCurrent + " (B)",
				title: periodTextCurrent + " " + yearCurrent + " (Borrador)",
				icon: MATERIAL_ICONS.EVENT,
				backgroundColor: "#4472C4",
				fn: () => aonFiscalCard.filterEstimationTable({ year: yearCurrent, period: periodCurrent, title: "Borrador " + periodTextCurrent + " " + yearCurrent })
			};
			options.push(periodOptCurrent);

			// Filtros
			for (let index = 0; index < 4; index++) {
				const period = result[index];
				const periodOpt = {
					name: period.periodText + " " + period.year,
					title: period.periodText + " " + period.year,
					icon: MATERIAL_ICONS.EVENT,
					backgroundColor: "#4472C4",
					fn: () => aonFiscalCard.filterTable({ year: period.year, period: period.period, title: period.periodText + " " + period.year })
				};
				options.push(periodOpt);
			}
		}

		d.setMenuOptions(options, top, left);
		d.open();
	}

	getCurrentFiscalPeriod() {
		const fecha = new Date();
		const mes = fecha.getMonth(); // getMonth() devuelve un número entre 0 (enero) y 11 (diciembre)

		if (mes >= 0 && mes <= 2) return "T1";  // Enero - Marzo
		if (mes >= 3 && mes <= 5) return "T2";  // Abril - Junio
		if (mes >= 6 && mes <= 8) return "T3";  // Julio - Septiembre
		return "T4"; // Octubre - Diciembre
	}

	async filterFutureFiscal() {
		let aonFiscalCard = document.getElementById('aonFiscalCard');
		let result = await this.getFilterModels();

		// Borrador actual (para empresas nuevas o sin modelos existentes)
		if (!result || result.length === 0) {

			let period = this.getCurrentFiscalPeriod();
			let year = new Date().getFullYear();

			let periodCurrent;
			let periodTextCurrent;
			let yearCurrent;

			if (period == "T1") {
				periodCurrent = "T1";
				periodTextCurrent = "1º Trim.";
				yearCurrent = year;
			} else if (period == "T2") {
				periodCurrent = "T2";
				periodTextCurrent = "2º Trim.";
				yearCurrent = year;
			} else if (period == "T3") {
				periodCurrent = "T3";
				periodTextCurrent = "3º Trim.";
				yearCurrent = year;
			} else {
				periodCurrent = "T4";
				periodTextCurrent = "4º Trim.";
				yearCurrent = year;
			}

			setTimeout(() => {
				aonFiscalCard.filterEstimationTable({ year: yearCurrent, period: periodCurrent, title: "Borrador " + periodTextCurrent + " " + yearCurrent })
			}, 500);

		} else {
			let lastPeriod;
			let period;
			let periodText;
			let year;

			if (result[0].period == "T1") {
				period = "T2";
				periodText = "2º Trim.";
				year = result[0].year;
				lastPeriod = new Date(result[0].year + "-" + "03-31");
			} else if (result[0].period == "T2") {
				period = "T3";
				periodText = "3º Trim.";
				year = result[0].year;
				lastPeriod = new Date(result[0].year + "-" + "06-30");
			} else if (result[0].period == "T3") {
				period = "T4";
				periodText = "4º Trim.";
				year = result[0].year;
				lastPeriod = new Date(result[0].year + "-" + "09-30");
			} else {
				period = "T1";
				periodText = "1º Trim.";
				year = result[0].year + 1;
				lastPeriod = new Date(result[0].year + "-" + "12-31");
			}

			const dayDiff = Math.floor((new Date() - lastPeriod) / (1000 * 60 * 60 * 24));

			if (dayDiff > 30) {
				setTimeout(() => {
					aonFiscalCard.filterEstimationTable({ year: year, period: period, title: "Borrador " + periodText })
				}, 500);
			}
		}
	}

	createAppList(parent, company) {
		// Clear parent
		while (parent.lastElementChild) {
			parent.removeChild(parent.lastElementChild);
		}

		let appsList = this.createElement(TAG.DIV);
		appsList.id = "apps"
		parent.appendChild(appsList);

		let ul = this.createElement(TAG.UL);
		ul.classList.add(CSS.AON_UL);
		ul.classList.add(CSS.AON_LIST_GROUP);

		if (company.parentId || company.type !== 'CONSULTANCY') {

			for (let key in Apps) {
				const app = Apps[key];
				if (this.isApp(app)) {
					if (Apps.MESSENGER.app === app.app) {
						getTaskHolder({ reload: true }).then(({ id }) => {
							if (id)
								this.addApp(app, ul);
						});
					} else {
						this.addApp(app, ul);
					}
				}
			}

			this.openFirstApp(this.getDur());
		} else {
			let li = this.createElement(TAG.LI);
			li.classList.add(CSS.AON_LIST_GROUP_ITEM);
			if (!LS.isNewTheme()) {
				li.classList.add(CSS.AON_APP_LI);
			}
			li.addEventListener(EVENT.CLICK, () => {
				this.rootPanelHtml('<aon-configuration></aon-configuration>');
			});

			let span = this.createElement(TAG.SPAN);
			span.innerHTML = `<aon-icon icon="aon_settings" color="black" size="30px"></aon-icon>`;

			let span2 = this.createElement(TAG.SPAN);
			span2.className = 'aonAppTitle';
			span2.innerHTML = MSG.CONFIGURATION;
			span.appendChild(span2);

			let buttons = this.createElement(TAG.SPAN);

			let i = this.createElement('i');
			i.className = 'material-icons';
			i.innerHTML = 'keyboard_arrow_right';
			buttons.appendChild(i);

			span.appendChild(buttons);
			li.appendChild(span);
			ul.appendChild(li);
		}
		appsList.appendChild(ul);
	}

	openFirstApp(dur) {
		const appsOpen = getAppsByDur(dur).filter(app => ![Apps.NOTES.app, Apps.TIMECONTROL.app, Apps.MESSENGER.app].includes(app.app));

		if (appsOpen && appsOpen.length === 1) {
			let app = appsOpen[0];
			if (app.app === Apps.AON_SALTRA.app) {
				this.rootPanel(new AonSaltra())
				this.appOption = false;
			}
		}
	}

	buildTitle(title) {
		let div = this.createElement(TAG.DIV);
		div.innerHTML = title;
		return div;
	}

	addApp(app, ul) {
		if (this.isApp(app)) {
			let li = this.createElement(TAG.LI);
			ul.appendChild(li)
			li.id = this.AON_DESKTOP + app.app.initCap();
			li.classList.add(CSS.AON_LIST_GROUP_ITEM);
			if (!LS.isNewTheme()) {
				li.classList.add(CSS.AON_APP_LI);
			}
			li.title = app.title;
			li.addEventListener(EVENT.CLICK, () => {
				this.appSelection(app.app);
				this.appOption = false
			});

			let span = this.createElement(TAG.SPAN);


			if (app.icon) {
				span.innerHTML = `<aon-icon icon="${app.icon}" color="${app.color}" size="30px"></aon-icon>`;
			} else {
				let img = this.createElement(TAG.IMG);
				img.src = app.logo;
				span.appendChild(img);
			}
			let span2 = this.createElement(TAG.SPAN);
			span2.className = 'aonAppTitle';
			span2.innerHTML = app.title;
			span.appendChild(span2);

			let buttons = this.createElement(TAG.SPAN);
			buttons.id = li.id + 'Buttons';

			if (app.options && app.options.stat) {
				let stat = new AonIconButton();
				stat.id = li.id + 'Stat';
				stat.icon = "bar_chart";
				stat.title = MSG.STATISTICS;
				stat.addEventListener(EVENT.CLICK, (event) => {
					this.appOption = true;
					event.preventDefault();
					this.statOption(app.app);
				});
				buttons.appendChild(stat);
			}

			if (app.options && app.options.upload && this.hasUploadRole(app)) {
				let upload = new AonIconButton();
				upload.id = li.id + 'Upload';
				upload.icon = MATERIAL_ICONS.FILE_UPLOAD;
				upload.title = MSG.UPLOAD_FILE;
				upload.addEventListener(EVENT.CLICK, (event) => {
					this.appOption = true;
					event.preventDefault();
					this.uploadOption(app.app);
				});
				buttons.appendChild(upload);
			}

			if (app.options && app.options.add) {
				let add = new AonIconButton();
				add.id = li.id + 'Add';
				add.icon = "add";
				add.title = MSG.NEW;
				add.addEventListener(EVENT.CLICK, (event) => {
					this.appOption = true;
					event.preventDefault();
					this.addOption(app.app, add);
				});
				buttons.appendChild(add);
			}

			let menu = new AonIconButton();

			menu.id = li.id + 'Menu';
			menu.icon = app.options && app.options.menu
				&& this.isOpenMenu(app)
				? "menu_open" : "keyboard_arrow_right";


			menu.title = app.options && app.options.menu
				&& this.isOpenMenu(app)
				? MSG.OPEN_MENU : MSG.OPEN;

			menu.addEventListener(EVENT.CLICK, (event) => {
				if (app.options && app.options.menu
					&& this.isOpenMenu(app)) {
					this.appOption = true;
					event.preventDefault();
					this.menuOption(app);
				}
			});
			buttons.appendChild(menu);

			span.appendChild(buttons);
			li.appendChild(span);
		}
	}

	appSelection(app) {
		if (!this.appOption)
			switch (app) {
				case Apps.DOCUMENTAL.app:
					// this.rootPanel(this.getDur().isBidoq() ? new AonDocumentalAyudat() : new AonDocumental());
					break;
				case Apps.ACCOUNTING.app:
					this.rootPanel(new AonAccounting());
					break;
				case Apps.FISCAL.app:
					this.rootPanel(new AonFiscal());
					break;
				case Apps.COMUNICA.app:
					const comunica = new AonLaboral();
					comunica.title = MSG.COMUNICA;
					this.rootPanel(comunica);
					break;
				case Apps.PAYROLL.app:
					const payroll = new AonLaboral();
					payroll.title = MSG.PAYROLL;
					this.rootPanel(payroll);
					break;
				case Apps.INVOICE.app:
					this.rootPanel(new AonInvoicePanel());
					break;
				case Apps.TIMECONTROL.app:
					this.rootPanel(new AonTimecontrol());
					break;
				case Apps.MESSENGER.app:
					this.rootPanel(new AonMessenger());
					break;
				case Apps.AON_SALTRA.app:
					this.rootPanel(new AonSaltra());
					break;
				case Apps.WAREHOUSE.app:
					this.rootPanel(new AonWarehouse());
					break;
				case Apps.MARKETING.app:
					this.rootPanel(new AonMarketing());
					break;
				case ClassicApps.AON_SOLUTIONS.app:
					open('https://' + localStorage.getItem('aon_domain_name') + '/login?token=' + localStorage.getItem('aon_session_id'));
					break;
				case ClassicApps.BIDOQ.app:
					getAccessBidoq().then(r => {
						const { datos } = r;
						if (datos && datos.ruta) {
							open(datos.respuesta);
						} else {
							open('https://mispapeles.es/');
						}
					});
					break;
				case ClassicApps.SELFCONTA.app:
					open('https://mispapeles.es/selfconta/')
					break;
			}
	}

	appSelectionFilter(app, filter) {
		if (!this.appOption)
			switch (app) {
				case Apps.ACCOUNTING.app:
					this.rootPanel(new AonAccounting(filter));
					break;
				case Apps.PAYROLL.app:
					const payroll = new AonLaboral(filter);
					payroll.title = MSG.PAYROLL;
					this.rootPanel(payroll);
					break;
				case Apps.FISCAL.app:
					this.rootPanel(new AonFiscal(filter));
					break;
			}
	}

	development(title) {
		let aonApplication = this.getApplication();
		aonApplication.development(title);
	}

	isApp(app) {
		if (Apps.ACCOUNTING.app === app.app)
			return this.getDur().isAccounting();
		else if (Apps.FISCAL.app === app.app)
			return this.getDur().isFiscal();
		else if (Apps.COMUNICA.app === app.app)
			return (this.getDur().isComunicaManager() || this.getDur().isComunicaPortal()) && !this.getDur().isPayroll();
		else if (Apps.PAYROLL.app === app.app)
			return this.getDur().isPayroll();
		else if (Apps.DOCUMENTAL.app === app.app)
			return this.getDur().isDocumental();
		else if (Apps.TIMECONTROL.app === app.app)
			return this.getDur().isTimecontrol();
		else if (Apps.INVOICE.app === app.app)
			return this.getDur().isInvoice();
		else if (Apps.MESSENGER.app === app.app)
			return this.getDur().isMessenger();
		else if (Apps.AON_SALTRA.app === app.app)
			return !this.getDur().isComunica() && !this.getDur().isPayroll() && this.getDur().isSaltra();
		else if (Apps.WAREHOUSE.app === app.app) {
			const domain = this.getDur().getDomain();
			return domain.getName() && (domain.getName().includes("udapa") || domain.getName().includes("paturpat") || this.isLocal());
		} else if (Apps.MARKETING.app === app.app) {
			return this.getDur().isMarketing() && this.isBeta();
		} else if (ClassicApps.AON_SOLUTIONS.app === app.app) {
			return this.getDur().isAon();
		} else if (ClassicApps.BIDOQ.app === app.app) {
			return this.getDur().isBidoq();
		} else if (ClassicApps.SELFCONTA.app === app.app) {
			return this.getDur().isSelfconta();
		} else return false;
	}

	isOpenMenu(app) {
		if (Apps.ACCOUNTING.app === app.app)
			return this.getDur().isAccountingManager();
		else if (Apps.FISCAL.app === app.app)
			return this.getDur().isFiscalManager();
		else if (Apps.PAYROLL.app === app.app)
			return this.getDur().isPayrollManager();
		else return false;
	}

	statOption(app) {
		switch (app) {
			case Apps.DOCUMENTAL.app:
				break;
			case Apps.ACCOUNTING.app:
				this.rootPanel(new AonAccounting());
				break;
			case Apps.FISCAL.app:
				break;
			case Apps.COMUNICA.app:
				break;
			case Apps.PAYROLL.app:
				const payroll = new AonLaboral();
				payroll.title = MSG.PAYROLL;
				this.rootPanel(payroll);
				break;
			case Apps.INVOICE.app:
				GWT.iLoad(GWT.INVOICE_STAT);
				break;
			case Apps.TIMECONTROL.app:
				break;
			case Apps.MESSENGER.app:
				if (this.isBeta())
					GWT.iLoad(GWT.TASK_STAT);
				break;
		}
	}

	addOption(app, button) {
		switch (app) {
			case Apps.DOCUMENTAL.app:
				this.getElement(this.INPUT_DOCUMENT_FILE).click();
				break;
			case Apps.ACCOUNTING.app:
				break;
			case Apps.FISCAL.app:
				break;
			case Apps.COMUNICA.app:
				break;
			case Apps.PAYROLL.app:
				break;
			case Apps.INVOICE.app:
				this.addInvoice(button);
				break;
			case Apps.TIMECONTROL.app:
				break;
			case Apps.MESSENGER.app:
				let aonMessengerChat = new AonMessenger();
				aonMessengerChat.data = { source: TASK_SOURCE.QUERY };
				this.rootPanel(aonMessengerChat);
				break;
		}
	}

	menuOption(app) {
		let aonMenu = this.getElement('aonMenu');
		aonMenu.buildAppMenu(app);
	}

	addNewOptions(e) {
		let rect = e.target.getBoundingClientRect();
		let x = e.clientX - rect.left;
		let y = e.clientY - rect.top;

		const top = rect.top + y;
		const left = rect.left + x + 180;

		let d = this.getElement(this.getApplication().OPTION_DIALOG);

		const NEW_MESSENGER = {
			id: 'messenger',
			name: MSG.NEW_REQUEST,
			icon: 'message',
			fn: () => { this.development(MSG.NEW_REQUEST); }
		};

		let actions = [NEW_INVOICE, NEW_DOCUMENT, NEW_MESSENGER];

		d.setMenuOptions(actions, top, left);
		d.open();
	}

	addInvoice(button, dashboard) {
		let invoicePanel = new AonInvoicePanel();
		let top;
		let left;
		if (dashboard) {
			top = button.getBoundingClientRect().top + 55;
			left = button.getBoundingClientRect().right + 7;
		} else {
			top = button.getBoundingClientRect().top;
			left = button.getBoundingClientRect().left;
		}

		let d = this.getApplication().getOptionDialog();
		let options = [{
			name: 'Emitidas',
			icon: MATERIAL_ICONS.UNARCHIVE,
			fn: () => {
				invoicePanel.option = OPTION.CREATE_INVOICE_ISSUED;
				this.rootPanel(invoicePanel);
			}
		}, {
			name: 'Recibidas',
			icon: MATERIAL_ICONS.ARCHIVE,
			fn: () => {
				invoicePanel.option = OPTION.CREATE_INVOICE_RECEIVED;
				this.rootPanel(invoicePanel);
			}
		}, {
			name: 'Tickets/Justificantes',
			icon: MATERIAL_ICONS.RECEIPT,
			fn: () => {
				invoicePanel.option = OPTION.CREATE_INVOICE_TICKET;
				this.rootPanel(invoicePanel);
			}
		}];
		d.setMenuOptions(options, top, left);
		d.open();
	}

	hasUploadRole(app) {
		if (app.app === Apps.DOCUMENTAL.app) {
			return this.getDur().isDocumentalPortal() || this.getDur().isDocumentalManager();
		} else return true;
	}
}

if (!window.customElements.get('aon-desktop')) {
	window.customElements.define('aon-desktop', AonDesktop);
}