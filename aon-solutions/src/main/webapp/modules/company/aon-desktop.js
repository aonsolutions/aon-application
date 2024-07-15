import {AonElement} from '../../components/AonElement.js';
import { Apps, ClassicApps, getAppsByDur} from  '../../services/app.js';
import {getDomainNotice, getDomainUserRoles, getTaskCount, getTaskHolder, getTimeControl, getAttach, getPeriodLaboral} from  '../../services/service.js';
import {getAccessBidoq} from  '../../services/bidoqService.js';
import {DomainUserRoles} from '../../models/DomainUserRoles.js';
import { CONSTANT, CSS, EVENT, MATERIAL_ICONS, MSG, TAG } from '../../environments/environments.js';
import { AonDocumentalAyudat } from '../documental/ayudat/aon-documental-ayudat.js';
import { AonDocumental } from '../documental/aon-documental.js';
import { AonSign } from '../timecontrol/aon-sign.js';
import { AonTimecontrol } from '../timecontrol/aon-timecontrol.js';
import { uploadDocuments, uploadOption } from "../documental/DocumentalUtils.js";
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
import '../../components/aon-icon.js';
import '../../components/aon-application.js';
import { getOfficeProjects } from '../../services/projectService.js';
import { Project } from '../../models/project/Project.js';
import { getNoteCount } from '../../services/noteService.js';
import { AonAccounting } from '../accounting/aon-accounting.js';
import { Attach } from '../../models/Attach.js';
import { AonWarehouse } from '../warehouse/aon-warehouse.js';
import { AonNewUpload } from '../../components/aon-new-upload.js'
import { AonDashboardButton } from '../../components/aon-dashboard-button.js';
import { AonCard } from '../../components/aon-card.js';
import { AonDashboardGraphicsTrial } from '../accounting/aon-graphics-dashboard-trial.js';
import { AonFiscalCard } from '../fiscal/aon-fiscal-card.js';
import { AonStatistics } from '../timecontrol/time-control/statistics/aon-statistics.js';
import { AonPayrollCard } from '../laboral/payroll/aon-payroll-card.js';
import { AonMessengerCard } from '../messenger/aon-messenger-card.js';
import { getModelsFiscal } from '../../services/service.js';
import { sortBy } from '../../services/utils.js';
import { FiscalUtils } from '../fiscal/FiscalUtils.js';
import { AonBankCard } from '../accounting/aon-bank-card.js';
import { AonDashboardUploadButton } from '../../components/aon-dashboard-upload-button.js';
import { AonDocumentalCard } from '../documental/aon-documental-card.js';
import { AonCompanyCostsCard, paintCompanyCostPieChart } from '../laboral/company/aon-company-costs-card.js';
import { AonUploadToast } from '../../components/aon-upload-toast.js';
import { AonDashboardChargePayments } from '../accounting/aon-dashboard-charge-payments.js';
import { AonDialog } from '../../components/aon-dialog.js';
import { AonMarketing } from '../marketing/aon-marketing.js';

export class AonDesktop extends AonElement {

	dur;
	AON_DESKTOP;
	INPUT_INVOICE_FILE;
	INPUT_DOCUMENT_FILE;
	appOption;
	SIDENAV_ACTIVITY_SUMMARY;

	static get observedAttributes() {
		return [];
	}

	get id() {
		return this.getAttribute('id');
	}

	set id(id) {
		this.setAttribute('id', id);
	}

	constructor () {
		super();
	}

	initialize(){
		this.id = this.id || 'aonDesktop';
		this.AON_DESKTOP = 'aonDesktopMain';
		this.INPUT_INVOICE_FILE = this.id + 'InputInvoiceFile';
		this.INPUT_DOCUMENT_FILE = this.id + 'InputDocumentFile';
		this.SIDENAV_ACTIVITY_SUMMARY = [];
		this.TIMECONTROL_TITLE = this.id + 'TimecontrolTitle';
		this.TIMECONTROL_SIGN = this.id + 'TimecontrolSign';
	}	

	getDur() {
		return this.dur;
	}

	connectedCallback () {
		if(LS.isNewTheme()) {
			let span = this.getElement('aonHeaderHome');
			if(span) span.style.display = 'none';

			let expandButtonDiv = this.getElement('aonExpandButtonDiv');
			if(expandButtonDiv) expandButtonDiv.style.display = 'block';
		}

		this.initialize();
		getDomainUserRoles({}).then(r => {
			this.dur = new DomainUserRoles(r);
			this.build();
		});
  	}

	disconnectedCallback () {
		if(LS.isNewTheme()) {
			let span = this.getElement('aonHeaderHome');
			if(span) span.style.display = 'block';

			let expandButtonDiv = this.getElement('aonExpandButtonDiv');
			if(expandButtonDiv) expandButtonDiv.style.display = 'none';
		}
	}	


	build() {
		let company = JSON.parse(localStorage.getItem("company"));
		this.innerHTML = /*html*/`
			<input id='${this.INPUT_INVOICE_FILE}' style='display:none;' type='file' name='file' multiple>
			<input id='${this.INPUT_DOCUMENT_FILE}' style='display:none;' type='file' name='file' multiple>
			<aon-application id="${this.AON_DESKTOP}" main="true"></aon-application>`;
		let aonDesktop = this.getElement(this.AON_DESKTOP);

		let inputInvoiceFile = this.getElement(this.INPUT_INVOICE_FILE);
		inputInvoiceFile.addEventListener(EVENT.CHANGE, ({target}) => {
			this.uploadInvoiceDesktop(undefined, target.files);
		});
		
		let inputDocumentFile = this.getElement(this.INPUT_DOCUMENT_FILE);
		inputDocumentFile.addEventListener(EVENT.CHANGE, ({target}) => {
			this.uploadDocumentsDesktop(undefined, target.files);
		});

		let divLogo = this.createElement(TAG.DIV);
		divLogo.id = this.id + 'Logo';
		aonDesktop.getSidenav().appendChild(divLogo);
		if(LS.isNewTheme()) {
			aonDesktop.getSidenav().style.display = 'none';
		}
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
			?  filter : parentFilter; 

		getAttach(f).then(r => {
			let attach = new Attach(r);
			if(attach && attach.id && attach.getContentType().includes("image")){
				divLogo.style.maxHeight = '60px';
				divLogo.style.margin = '10px';
				divLogo.style.justifyContent = 'center';

				let data = {
					domain_id: attach.getDomain().getId(),
					attach_type: attach.getAttachType(),
					domain_name: attach.getDomain().getName(),
					id: attach.getId()
				};
				let url = location.href + 'ms/api/file/' + btoa(JSON.stringify(data));

				let img = this.createElement(TAG.IMG);
				img.id = this.id + 'LogoImg';
				img.style.maxHeight = '60px';
				img.style.maxWidth = '100%';
				img.style.borderRadius = '10px';
				img.src = url;
				divLogo.appendChild(img);
			}
		});

		if(this.getDur().hasCustomView()){
			getAttach(parentFilter).then(r => {
				let attach = new Attach(r);
				if(attach && attach.id && attach.getContentType().includes("image")){
					let data = {
						domain_id: attach.getDomain().getId(),
						attach_type: attach.getAttachType(),
						domain_name: attach.getDomain().getName(),
						id: attach.getId()
					};
					let url = location.href + 'ms/api/file/' + btoa(JSON.stringify(data));
	
					let headerLogo = this.getElement('aonLogo');
					headerLogo.src = url;
				}
			});
		}

		if(this.isBeta() && !this.getDur().getDomain().isOffice()) {
			let myGestor = {
				id: 'Gestor',
				name: MSG.MY_MANAGER,
				options: []
			};
			aonDesktop.addSidenavOptions3(myGestor);
			getOfficeProjects({}).then(offices => {
				this.clearElementById(aonDesktop.SIDENAV + myGestor.id + 'List');
				offices.forEach(office => {
					if(office.projects.length > 0) {
						office.projects.forEach((item,idx) => {
							let p = new Project(item);
							let h =  p.getProjectHolder().getTaskHolder().name || p.getProjectHolder().getWorkgroup().getDescription();
							let option = {
								name: p.getType().getDescription() + (h ? ' - ' + h : '') ,
								icon: MATERIAL_ICONS.SUPPORT_AGENT,
								fn: () => {}, 
								actions: [{
								  id: 'Contact'+idx,
								  icon: 'chat',
								  action: () => {
									let aonMessengerChat = new AonMessenger();	
									aonMessengerChat.data = {source:TASK_SOURCE.QUERY, project: item, domain: item.domain};
									this.rootPanel(aonMessengerChat);
								  }
								}]
							};
							aonDesktop.addSidenavOptionsListValue(myGestor, option);
						});
					}
           		});
			}); 
		}

		if(company.parentId || company.type !== 'CONSULTANCY'){
			this.getSidenavActivity();
		}

		let classicOptions = [];

		// if(!localStorage.getItem('aon_jsf') && this.getDur().isAon()){
		// 	classicOptions.push({
		// 		name: 'aonSolutions',
		// 		img: 'assets/apps/aon.png',
		// 		fn: () => open('https://' + localStorage.getItem('aon_domain_name') + '/login?token=' + localStorage.getItem('aon_session_id'))
		// 	});
		// }
		// if(localStorage.getItem('aon_jsf')){
		// 	classicOptions.push({
		// 		name: 'aonGestion',
		// 		img: 'assets/apps/aon.png',
		// 		fn: () => loadManagementPanel(this.getElement('aonDesktopMainContent'))
		// 	});
		// }

		if(this.getDur().isAon()){
			classicOptions.push({
				name: 'aonSolutions',
				img: 'assets/aon.png',
				style: CSS.AON_DESKTOP_MENU_CLASSIC_OPTION_AON,
				fn: () => open('https://' + localStorage.getItem('aon_domain_name') + '/login?token=' + localStorage.getItem('aon_session_id'))
			});
		}

		if(this.getDur().isBidoq()){
			classicOptions.push({
				name: 'Bidoq',
				img: 'assets/apps/bidoq.png',
				style: CSS.AON_DESKTOP_MENU_CLASSIC_OPTION,
				fn: () =>{
					getAccessBidoq().then(r => {
						const {datos} = r;
						if(datos && datos.ruta) {
							open(datos.respuesta);
						} else {
							open('https://mispapeles.es/');
						}
					});
				}
			});
		}

		if(this.getDur().isSelfconta()){
			classicOptions.push({
				name: 'Selfconta',
				img: 'assets/apps/selfconta.png',
				style: CSS.AON_DESKTOP_MENU_CLASSIC_OPTION,
				fn: () =>open('https://mispapeles.es/selfconta/')
			});
		}

		if(this.getDur().isSaltra()){
			classicOptions.push({
				name: 'Saltra',
				img: 'assets/apps/saltra.png',
				style: CSS.AON_DESKTOP_MENU_CLASSIC_OPTION,
				fn: () =>open('https://app.saltra.es/')
			});
		}

		if(classicOptions.length > 0)
			aonDesktop.addSidenavOptions(MSG.CLASSIC_VIEW.toUpperCase(), classicOptions);

		if(this.getDur().isTimecontrol() && !LS.isNewTheme()) {
			getTimeControl().then(r => {
				let aonSign = new AonSign();
				aonDesktop.addSidenavWidget(MSG.TIMECONTROL.toUpperCase(), aonSign);
				aonSign.buildSignin(r);
				let aonHeader = this.getElement('aonHeader');
				aonHeader.timeControlStatus(r);
			});
		}

		// Content
		let content = this.createElement(TAG.DIV);
		content.id = "content";
		content.style.margin = '0 1rem';
		aonDesktop.setContent(content);

		// Content data
		let contentData = this.createElement(TAG.DIV);
		contentData.id = "contentData";
		contentData.style.margin = '1rem';

		if(LS.isNewTheme()){
			// Intiliaze App List
			content.appendChild(contentData);
			this.createDashboard(contentData, company);
		} else {
			// Intiliaze App List
			this.createAppList(contentData, company);
			content.appendChild(contentData);
		}
	}

	uploadDocumentsDesktop(input, files){
		let d = new AonDialog();
		let rootPanel = document.getElementById("rootPanel");
		rootPanel.appendChild(d);
		d.clear();
		// if(isMobile()) d.width = '400px';
		d.setTitle(MSG.UPLOAD_FILE);
		d.setContent(uploadOption(this.getDur()));
		d.addAcceptAction(async() => {
		  let data = {
			  category: document.getElementById("aonDocumentalUploadCategory").value,
			  scope: document.getElementById("aonDocumentalUploadScope").value,
			  tag: document.getElementById("aonDocumentalUploadTag").value,
			  type: document.getElementById("aonDocumentalUploadType").value
		  }

		  let uploadToast = this.getElement('aonUploadToast');
		  if(!uploadToast){ 
			  uploadToast = new AonUploadToast();
			  uploadToast.setDur(this.getDur());
			  this.appendChild(uploadToast);
		  }
		  for (let file of files) {
			  uploadToast.addFile("documental", file, data);
		  }
		});
		d.open();
	}

	uploadInvoiceDesktop(input, files){
		let uploadToast = this.getElement('aonUploadToast');
		if(!uploadToast){ 
			uploadToast = new AonUploadToast();
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

	async createDashboard(parent, company){
		// Clear parent
		while (parent.lastElementChild) {
			parent.removeChild(parent.lastElementChild);
		}

		let dashboard = this.createElement(TAG.DIV);
		dashboard.id = "dashboard";
		dashboard.className = CSS.AON_DASHBOARD;
		parent.appendChild(dashboard);

		// Upload Panel
		let upload = this.createElement(TAG.DIV);
		upload.className = CSS.AON_UPLOAD_PANEL;
		upload.id = "uploads";
		dashboard.appendChild(upload);

		let uploadDoc = new AonNewUpload();
		uploadDoc.id = "docUpload";
		uploadDoc.setMessage("Subir documentación");
		uploadDoc.setType("Documental");
		upload.appendChild(uploadDoc);

		let uploadInv = new AonNewUpload();
		uploadInv.id = "factUpload";
		uploadInv.setMessage("Subir factura");
		uploadInv.setType("Invoice");
		upload.appendChild(uploadInv);

		// Fast Access Buttons Panel
		let fastAccessButtons = this.createElement(TAG.DIV);
		fastAccessButtons.className = CSS.AON_FAST_ACCESS;
		fastAccessButtons.id = "fastAccessButtons";
		dashboard.appendChild(fastAccessButtons);

		if(this.getDur().isInvoice()){
			let newInvoice = new AonDashboardButton();
			newInvoice.setId('newInvoice');
			newInvoice.setIcon('note_add');
			newInvoice.setMessage(MSG.NEW_INVOICE);
			newInvoice.addEventListener(EVENT.CLICK, () => {
				this.addInvoice(newInvoice, dashboard);
			});
			fastAccessButtons.appendChild(newInvoice);
		}

		if(this.getDur().isDocumental()){
			let newDocument = new AonDashboardUploadButton();
			newDocument.setId('newDocument');
			newDocument.setIcon('post_add');
			newDocument.setMessage(MSG.NEW_DOCUMENT);
			fastAccessButtons.appendChild(newDocument);
		}

		let newRequest = new AonDashboardButton();
		newRequest.setId('newRequest');
		newRequest.setIcon('add_comment');
		newRequest.setMessage('CREAR CONSULTA');
		newRequest.addEventListener(EVENT.CLICK, () => {
			let aonMessengerChat = new AonMessenger();	
			aonMessengerChat.data = {source:TASK_SOURCE.QUERY};
			this.rootPanel(aonMessengerChat);
		});
		fastAccessButtons.appendChild(newRequest);

		if(this.getDur().isAon()){
			let newEmployee = new AonDashboardButton();
			newEmployee.setId('newEmployee');
			newEmployee.setIcon('person_add');
			newEmployee.setMessage(MSG.NEW_EMPLOYEE);
			newEmployee.addEventListener(EVENT.CLICK, () => {
				let aonMessengerChat = new AonMessenger();	
				aonMessengerChat.data = {source:TASK_SOURCE.REQUEST};
				this.rootPanel(aonMessengerChat);

				this.isElementLoaded("#sourceTask").then(sourceTaskSelect => {
					console.log(sourceTaskSelect);
					sourceTaskSelect.value = "request";
					this.isElementLoaded("#processType").then(processTypeSelect => {
						console.log(processTypeSelect);
						processTypeSelect.value = "2";
						this.isElementLoaded("#aonMessengerToolbarHeaderTitleSectionMenuIconButton").then(sidenavBtn => {
							console.log(sidenavBtn);
							sidenavBtn.click();
						});
					});
				});
			});
			fastAccessButtons.appendChild(newEmployee);
		}

		// Cards Panel
		let cardsPanel = this.createElement(TAG.DIV);
		cardsPanel.className = CSS.AON_CARDS_PANEL;
		cardsPanel.style.gap = '1rem';
		cardsPanel.style.flexWrap = 'wrap';
		cardsPanel.id = "cardsPanel";
		dashboard.appendChild(cardsPanel);

		// Timecontrol
		if(this.getDur().isTimecontrol()) {
			let timecontrolCard = new AonCard();
			timecontrolCard.classList.add(CSS.AON_DASHBOARD_CARD);
			timecontrolCard.id = CONSTANT.TIMECONTROL;
			// timecontrolCard.title = MSG.TIMECONTROL;
			timecontrolCard.message = MSG.TIMECONTROL;
			timecontrolCard.setApp(Apps.TIMECONTROL);
			timecontrolCard.addEventListener(EVENT.CLICK_TITLE, () => this.appSelection(Apps.TIMECONTROL.app));
			cardsPanel.appendChild(timecontrolCard);	
			timecontrolCard.getCardTitle1().style.cursor = 'pointer';

			getTimeControl().then(r => {
				let div = this.createElement(TAG.DIV);
				timecontrolCard.setContent(div)
				let aonSign = new AonSign();
				div.appendChild(new AonStatistics());
				div.appendChild(aonSign);

				aonSign.buildSignin(r);
				let aonHeader = this.getElement('aonHeader');
				aonHeader.timeControlStatus(r);
				timecontrolCard.firstChild.style.minHeight = "420px";
				timecontrolCard.firstChild.style.margin = '0';
			});

		}
		
		if(this.getDur().isAccounting()) {
			// PyG Card
			let defaultYear = new Date().getFullYear();

			if(new Date().getTime() < new Date(new Date().getFullYear(), 0, 31))
				defaultYear = defaultYear - 1;

			let aonDashboardGraphicsTrial = new AonDashboardGraphicsTrial("yearly", defaultYear);
			aonDashboardGraphicsTrial.id = "aonDashboardGraphicsTrial";

			let pygCard = new AonCard();
			pygCard.classList.add(CSS.AON_DASHBOARD_CARD);
			pygCard.id = "pygCard";
			// pygCard.title = "Pérdidas y Ganancias";
			pygCard.message = "Pérdidas y Ganancias";
			pygCard.setApp(Apps.ACCOUNTING);
			cardsPanel.appendChild(pygCard);
			pygCard.getCardTitle1().style.cursor = 'pointer';
			pygCard.insertAdjacentHTML( 'beforeend', "<aon-dialog-menu id='aonCardPyGOption'> </aon-dialog-menu>" );

			pygCard.setContent(aonDashboardGraphicsTrial);
			pygCard.addTitleButton(MSG.OPTIONS, MATERIAL_ICONS.MORE_VERT, false, () => this.filterPyG(pygCard));
			pygCard.firstChild.style.marginLeft = '0';
			pygCard.firstChild.style.minHeight = "420px";
			pygCard.firstChild.children.item(1).style.height = "315px";
			pygCard.firstChild.style.margin = '0';

			pygCard.addEventListener(EVENT.CLICK_TITLE, () => {
				let dashboardGraphicsTrial = this.getElement('aonDashboardGraphicsTrial');
				this.appSelectionFilter(Apps.ACCOUNTING.app, dashboardGraphicsTrial.getFilter());
			});
		}

		if(this.getDur().isInvoice() || this.getDur().isAccounting()) {
			// Cobros y Pagos Card
			let cypCard = new AonCard();
			cypCard.classList.add(CSS.AON_DASHBOARD_CARD);
			cypCard.id = "cyp";
			cypCard.message = "Cobros y Pagos";
			cypCard.setApp(this.getDur().isInvoice() ? Apps.INVOICE : Apps.ACCOUNTING);
			cardsPanel.appendChild(cypCard);
			cypCard.getCardTitle1().style.cursor = 'pointer';
			cypCard.insertAdjacentHTML( 'beforeend', "<aon-dialog-menu id='aonCardCyPOption'> </aon-dialog-menu>" );

			cypCard.setContent(new AonDashboardChargePayments("current_month"));
			cypCard.addTitleButton(MSG.OPTIONS, MATERIAL_ICONS.MORE_VERT, false, () => this.filterCyP(cypCard));
			cypCard.firstChild.style.marginLeft = '0';
			cypCard.firstChild.style.minHeight = "420px";
			cypCard.firstChild.children.item(1).style.height = "315px";
			cypCard.firstChild.style.margin = '0';

			cypCard.addEventListener(EVENT.CLICK_TITLE, () => {
				this.appSelection(this.getDur().isInvoice() ? Apps.INVOICE.app : Apps.ACCOUNTING.app);
			});
		}

		if(this.getDur().isPayrollManager()) {
			// LABORAL
			let payrollCard = new AonCard();
			payrollCard.classList.add(CSS.AON_DASHBOARD_CARD);
			payrollCard.id = CONSTANT.PAYROLL;
			// payrollCard.title = MSG.LABORAL_COSTS;
			payrollCard.message = MSG.LABORAL_COSTS;
			payrollCard.setApp(Apps.PAYROLL);
			payrollCard.addEventListener(EVENT.CLICK_TITLE ,() => {
				this.appSelectionFilter(Apps.PAYROLL.app, this.getElement('aon-company-costs-card').getFilter());
			});
			cardsPanel.appendChild(payrollCard);
			payrollCard.getCardTitle1().style.cursor = 'pointer';
			payrollCard.insertAdjacentHTML( 'beforeend', "<aon-dialog-menu id='aonCardPayrollOption'> </aon-dialog-menu>" );
			
			let lastMonthFilter = getPeriodLaboral("last_month");
			lastMonthFilter.period = lastMonthFilter.value;

			let aonCompanyCostsCard = new AonCompanyCostsCard(lastMonthFilter);
			payrollCard.setContent(aonCompanyCostsCard);
			await paintCompanyCostPieChart();
			payrollCard.addTitleButton(MSG.OPTIONS, MATERIAL_ICONS.MORE_VERT, false, () => this.filterPayrollStatics(payrollCard));
			payrollCard.firstChild.style.minHeight = "420px";
			payrollCard.firstChild.children.item(1).style.height = "315px";
			payrollCard.firstChild.style.margin = '0';
		} else if(this.getDur().isPayroll()) {
			// Nominas
			let payrollCard = new AonCard(() => this.appSelection(Apps.PAYROLL.app));
			payrollCard.classList.add(CSS.AON_DASHBOARD_CARD);
			payrollCard.id = "payroll";
			// payrollCard.title = "Nóminas";
			payrollCard.message = "Nóminas";
			payrollCard.setApp(Apps.PAYROLL);
			payrollCard.addEventListener(EVENT.CLICK_TITLE ,() => this.appSelection(Apps.PAYROLL.app));
			cardsPanel.appendChild(payrollCard);
			payrollCard.getCardTitle1().style.cursor = 'pointer';

			let aonPayrollCard = new AonPayrollCard();
			payrollCard.setContent(aonPayrollCard);
			
			payrollCard.firstChild.style.minHeight = "420px";
			payrollCard.firstChild.children.item(1).style.height = "315px";
			payrollCard.firstChild.style.margin = '0';
		}

		if(this.getDur().isBank()) {
			// Bancos
			let bankCard = new AonCard();
			bankCard.classList.add(CSS.AON_DASHBOARD_CARD);
			bankCard.id = "bankCard";
			// bankCard.title = "Bancos";
			bankCard.message = "Bancos";
			bankCard.setApp(Apps.ACCOUNTING);
			bankCard.addEventListener(EVENT.CLICK_TITLE, () => {
				this.appSelection(Apps.ACCOUNTING.app);
				this.isElementLoaded("#aonAccountingSidenavbanksnordigen")
					.then(selector => {
						selector.click();
					});
			});
			cardsPanel.appendChild(bankCard);
			bankCard.getCardTitle1().style.cursor = 'pointer';
			
			let aonBankCard = new AonBankCard(company.registry);
			bankCard.setContent(aonBankCard);
			
			bankCard.firstChild.style.minHeight = "420px";
			bankCard.firstChild.children.item(1).style.height = "315px";
			bankCard.firstChild.style.margin = '0';
		}

		if(this.getDur().isFiscal()) {
			// Impuestos
			let fiscalCard = new AonCard();
			fiscalCard.classList.add(CSS.AON_DASHBOARD_CARD);
			fiscalCard.id = "fiscalCard";
			// fiscalCard.title = "Impuestos";
			fiscalCard.message = "Impuestos";
			fiscalCard.setApp(Apps.FISCAL);
			fiscalCard.addEventListener(EVENT.CLICK_TITLE, () => this.appSelection(Apps.FISCAL.app));
			cardsPanel.appendChild(fiscalCard);
			fiscalCard.getCardTitle1().style.cursor = 'pointer';
			fiscalCard.insertAdjacentHTML( 'beforeend', "<aon-dialog-menu id='aonCardFiscalOption'> </aon-dialog-menu>" );
    
			let fiscalDefaultFilter = this.getFiscalFilter();
			let aonFiscalCard = new AonFiscalCard(fiscalDefaultFilter);
			fiscalCard.setContent(aonFiscalCard);
			
			let spanPeriod = this.createElement(TAG.SPAN);
			spanPeriod.style.fontSize =  "1rem";
			spanPeriod.style.color = "#d2d2d6";
			spanPeriod.style.fontWeight = "500";
			if(fiscalDefaultFilter){
				fiscalDefaultFilter.then(filter => spanPeriod.innerHTML = filter ? filter.title : '');
			}
			fiscalCard.addSection2(spanPeriod);
			
			fiscalCard.addTitleButton(MSG.OPTIONS, MATERIAL_ICONS.MORE_VERT, false, () => this.filterFiscal(fiscalCard));
			fiscalCard.firstChild.style.minHeight = "420px";
			fiscalCard.firstChild.children.item(1).style.height = "315px";
			fiscalCard.firstChild.style.margin = '0';

			await this.filterFutureFiscal();
		}

		if(this.getDur().isDocumental()) {
			// Documental
			let documentalCard = new AonCard();
			documentalCard.classList.add(CSS.AON_DASHBOARD_CARD);
			documentalCard.id = "documentalCard";
			documentalCard.message = MSG.DOCUMENTARY;
			documentalCard.setApp(Apps.DOCUMENTAL);
			documentalCard.addEventListener(EVENT.CLICK_TITLE, () => {
				this.appSelection(Apps.DOCUMENTAL.app);
			});
			cardsPanel.appendChild(documentalCard);
			documentalCard.getCardTitle1().style.cursor = 'pointer';
			
			let aonBankCard = new AonDocumentalCard();
			documentalCard.setContent(aonBankCard);
			
			documentalCard.firstChild.style.minHeight = "420px";
			documentalCard.firstChild.children.item(1).style.height = "315px";
			documentalCard.firstChild.style.margin = '0';
		}

		if(this.getDur().isMessengerManager() || this.getDur().isMessenger()) {
			// Solicitudes
			let messengerCard = new AonCard();
			messengerCard.classList.add(CSS.AON_DASHBOARD_CARD);
			messengerCard.id = "messengerCard";
			// messengerCard.title = "Solicitudes";
			messengerCard.message = "Solicitudes";
			messengerCard.setApp(Apps.MESSENGER);
			messengerCard.addEventListener(EVENT.CLICK_TITLE, () => this.appSelection(Apps.MESSENGER.app));
			cardsPanel.appendChild(messengerCard);
			messengerCard.getCardTitle1().style.cursor = 'pointer';
			let aonMessengerCard = new AonMessengerCard(() => this.appSelection(Apps.MESSENGER.app));

			messengerCard.setContent(aonMessengerCard);
			
			messengerCard.firstChild.style.minHeight = "420px";
			messengerCard.firstChild.children.item(1).style.height = "315px";
			messengerCard.firstChild.style.margin = '0';
		}
	}

	async isElementLoaded(selector){
		while ( document.querySelector(selector) === null) {
		  await new Promise( resolve =>  requestAnimationFrame(resolve) )
		}
		return document.querySelector(selector);
	};

	async isElementLoaded(selector){
		while ( document.querySelector(selector) === null) {
		  await new Promise( resolve =>  requestAnimationFrame(resolve) )
		}
		return document.querySelector(selector);
	  };

	filterPyG(pygCard){
		let button = this.getElement('pygCardTitleSection2OpcionesButtonIconButton');
		let top  = button.getBoundingClientRect().top;
		const left = button.getBoundingClientRect().left;

		let pyGYearSelect = this.getElement('pyGyearelect');
		let period = JSON.parse(pyGYearSelect.value);
        let pygyYear = period.name;

		let aonDashboardGraphicsTrial = this.getElement('aonDashboardGraphicsTrial');

		let d = document.getElementById('aonCardPyGOption');

		const anual = {
			name: 'Vista Anual',
			title:"Vista Anual",
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
			title:"Vista Trimestral",
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

	filterCyP(cypCard){
		let button = this.getElement('cypTitleSection2OpcionesButtonIconButton');
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

	filterPayrollStatics(payrollCard){
		let button = this.getElement('payrollTitleSection2OpcionesButtonIconButton');
		let top  = button.getBoundingClientRect().top;
		const left = button.getBoundingClientRect().left;

		let d = document.getElementById('aonCardPayrollOption');
 
		let periods = getPeriodLaboral();
		periods.pop();

		let options = periods.map(option => ({
			...option,
			period: option.value,
			fn: async () => {
				payrollCard.setContent(new AonCompanyCostsCard({...option, period: option.value}));
				await paintCompanyCostPieChart();
			}
		  }));

		d.setMenuOptions(options, top, left);
		d.open();
	}

	async getFiscalFilter(){
		let result = await this.getFilterModels();
		if(!result || result.length == 0) return undefined;
		let period = result[0];
		return {year: period.year, period: period.period, title: period.periodText + " " + period.year};
	}

	async getFilterModels(){
		const datos = await getModelsFiscal();
		let orderDatos = [];
        if (datos) {
			orderDatos = sortBy(datos,'year','desc')
				.map((model) => FiscalUtils.getModelNew(model));
        }

		const result = orderDatos.filter(function (a) {
		  var key = a.year + '|' + a.period;
		  if (!this[key]) {
			  this[key] = true;
			  return true;
		  }
		}, Object.create(null));

		result.sort(function (a, b) {
		  var aSize = a.year;
		  var bSize = b.year;
		  var aLow = a.period;
		  var bLow = b.period;
	  
		  if(aSize == bSize)
		  {
			  return (aLow < bLow) ? -1 : (aLow > bLow) ? 1 : 0;
		  }
		  else
		  {
			  return (aSize < bSize) ? -1 : 1;
		  }
		});
	
		return result.reverse();
	  }

	filterFiscal(){
		let button = this.getElement('fiscalCardTitleSection2OpcionesButtonIconButton');
		let top  = button.getBoundingClientRect().top;
		const left = button.getBoundingClientRect().left;

		let d = document.getElementById('aonCardFiscalOption');

		let aonFiscalCard = document.getElementById('aonFiscalCard');
		let result = aonFiscalCard.getFilterModels;
		
		let options = [];

		// Borrador
		let period;
		let periodText;
		let year;
		if(result[0].period == "T1") {
			period = "T2";
			periodText = "2º Trim.";
			year = result[0].year;
		} else if(result[0].period == "T2") {
			period = "T3";
			periodText = "3º Trim.";
			year = result[0].year;
		} else if(result[0].period == "T3") {
			period = "T4";
			periodText = "4º Trim.";
			year = result[0].year;
		} else {
			period = "T1";
			periodText = "1º Trim.";
			year = result[0].year + 1;
		}
		const periodOpt = {
			name: periodText + " " + year + " (B)",
			title: periodText + " " + year + " (Borrador)",
			icon: MATERIAL_ICONS.EVENT,
			backgroundColor: "#4472C4",
			fn: () => aonFiscalCard.filterEstimationTable({year: year, period: period, title: "Borrador " + periodText})
		};
		options.push(periodOpt);

		// Filtros
		for (let index = 0; index < 4; index++) {
			const period = result[index];
			const periodOpt = {
				name: period.periodText + " " + period.year,
				title: period.periodText + " " + period.year,
				icon: MATERIAL_ICONS.EVENT,
				backgroundColor: "#4472C4",
				fn: () => aonFiscalCard.filterTable({year: period.year, period: period.period, title: period.periodText + " " + period.year})
			};
			options.push(periodOpt);
		}
		
		d.setMenuOptions(options, top, left);
		d.open();
	}

	async filterFutureFiscal(){
		let aonFiscalCard = document.getElementById('aonFiscalCard');
		let result = await this.getFilterModels();

		if(!result || result.length === 0) { return; }

		let lastPeriod;
		let period;
		let periodText;
		let year;

		if(result[0].period == "T1") {
			period = "T2";
			periodText = "2º Trim.";
			year = result[0].year;
			lastPeriod = new Date(result[0].year + "-" + "03-31");
		} else if(result[0].period == "T2") {
			period = "T3";
			periodText = "3º Trim.";
			year = result[0].year;
			lastPeriod = new Date(result[0].year + "-" + "06-30");
		} else if(result[0].period == "T3") {
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

		if(dayDiff > 30){
			setTimeout(() => {
				aonFiscalCard.filterEstimationTable({year: year, period: period, title: "Borrador " + periodText})
			}, 500);
		}
	}

	createAppList(parent, company){
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

		if(company.parentId || company.type !== 'CONSULTANCY'){
			
			for (let key in Apps){
				const app = Apps[key];
				if(this.isApp(app)) {
					if(Apps.MESSENGER.app === app.app){
						getTaskHolder({reload:true}).then(({id})=>{
							if(id) 
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
			if(!LS.isNewTheme()) {
				li.classList.add(CSS.AON_APP_LI);
			}
			li.style.borderRight = '0px';
			li.style.borderLeft = '0px';
			li.style.cursor = 'pointer';
			li.addEventListener(EVENT.CLICK, () => {
				this.rootPanelHtml('<aon-configuration></aon-configuration>');
			});
			
			let span = this.createElement(TAG.SPAN);
			span.style.margin = '20px';

			span.innerHTML = `<aon-icon icon="aon_settings" color="black" size="30px"></aon-icon>`;

			let span2 = this.createElement(TAG.SPAN);
			span2.className = 'aonAppTitle';
			span2.innerHTML = MSG.CONFIGURATION;
			span.appendChild(span2);

			let buttons = this.createElement(TAG.SPAN);
			buttons.style.position = 'absolute';
			buttons.style.right = '10px';

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

	openFirstApp(dur){
		const appsOpen = getAppsByDur(dur).filter(app=>  ![Apps.NOTES.app, Apps.TIMECONTROL.app,  Apps.MESSENGER.app].includes(app.app));

		if(appsOpen && appsOpen.length===1){
			let app = appsOpen[0];
			if( app.app === Apps.AON_SALTRA.app ){
				this.rootPanel(new AonSaltra())
				this.appOption = false;
			}
		}
	}

	buildTitle(title) {
		let div = this.createElement(TAG.DIV);
		div.style.color = 'gray';
		div.style.paddingTop = '20px';
		div.style.paddingBottom = '20px';
		div.innerHTML = title;
		return div;
	}
	
	addApp(app, ul){
		if(this.isApp(app)) {
		 	let li = this.createElement(TAG.LI);
			ul.appendChild(li)
			li.id = this.AON_DESKTOP + app.app.initCap();
			li.classList.add(CSS.AON_LIST_GROUP_ITEM);
			if(!LS.isNewTheme()) {
				li.classList.add(CSS.AON_APP_LI);
			}
			li.style.borderRight = '0px';
			li.style.borderLeft = '0px';
			li.style.cursor = 'pointer';
			li.title = app.title;
			li.addEventListener(EVENT.CLICK, () => {
				this.appSelection(app.app);
				this.appOption = false
			});
			if(LS.isNewTheme()) {

				li.addEventListener(EVENT.MOUSEOVER, () => {
					li.style.backgroundColor = app.backgroundColor || '#eaf1fb';
				});

				li.addEventListener(EVENT.MOUSELEAVE, () => {
					li.style.backgroundColor = 'transparent';
				});
			}

			let span = this.createElement(TAG.SPAN);
			span.style.margin = '20px';

			if(app.icon) {
				span.innerHTML = `<aon-icon icon="${app.icon}" color="${app.color}" size="30px"></aon-icon>`;
			} else {
				let img = this.createElement(TAG.IMG);
				img.style.width = '30px';
				img.src = app.logo;
				span.appendChild(img);
			}
			let span2 = this.createElement(TAG.SPAN);
			span2.className = 'aonAppTitle';
			span2.innerHTML = app.title;
			span.appendChild(span2);

			let buttons = this.createElement(TAG.SPAN);
			buttons.id = li.id + 'Buttons'
			buttons.style.position = 'absolute';
			buttons.style.right = '10px';
			buttons.style.top = '8px';

			if(app.options && app.options.stat) {
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

			if(app.options && app.options.upload && this.hasUploadRole(app)) {
				let upload = new AonIconButton();
				upload.id = li.id + 'Upload';
				upload.icon = "file_upload";
				upload.title = MSG.UPLOAD_FILE;
				upload.addEventListener(EVENT.CLICK, (event) => {
					this.appOption = true;
					event.preventDefault();
					this.uploadOption(app.app);
				});
				buttons.appendChild(upload);
			}

			if(app.options && app.options.add) {
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
				if(app.options && app.options.menu
					&& this.isOpenMenu(app)){
					this.appOption = true;
					event.preventDefault();
					this.menuOption(app);
				}
			});
			buttons.appendChild(menu);

			span.appendChild(buttons);
			li.appendChild(span);

			if (app.options && app.options.upload) {
				li.addEventListener(EVENT.DRAGOVER, (event) => {
					event.preventDefault();
					console.log(EVENT.DRAGOVER);
					li.classList.add('dragAndDrop');
				});
			
				li.addEventListener(EVENT.DRAGENTER, (event) => {
				  event.preventDefault();
				  li.classList.add('dragAndDrop');
				});
			
				li.addEventListener(EVENT.MOUSELEAVE, () => {
					li.classList.remove('dragAndDrop');
				});
			
				li.addEventListener(EVENT.MOUSEOVER, () => {
					li.classList.remove('dragAndDrop');	
				});
			
				document.addEventListener(EVENT.DRAGLEAVE, (event) => {
				  event.preventDefault();
				  let isClickInside = li.contains(event.target) || li === event.target;
				  if (!isClickInside) {
					li.classList.remove('dragAndDrop');
				  }
				});
			
				li.addEventListener(EVENT.DROP, (event) => {
					  event.preventDefault();
					  console.log(EVENT.DROP);
					  li.style.borderRight = "0px";
					  li.style.borderLeft = "0px";
					  li.style.borderTop = "0px";
					  li.style.borderBottom = "1px solid rgba(0,0,0,.125)";
					  li.style.opacity = "1";
					  if(event && event.dataTransfer && event.dataTransfer.files){
						let files = event.dataTransfer.files;

						switch(app.app){
						case Apps.DOCUMENTAL.app:
							let inputDocumentFile = this.getElement(this.INPUT_DOCUMENT_FILE);
							// uploadDocuments(inputDocumentFile, files, this.getDur());
							this.uploadDocumentsDesktop(undefined, files);
							break;
						case Apps.INVOICE.app:
							let inputInvoiceFile = this.getElement(this.INPUT_INVOICE_FILE);
							this.uploadInvoiceDesktop(undefined, files);
							break;								
						}
					}
				});
			}
		}
	}

	appSelection(app) {
		if(!this.appOption)
			switch(app){
				case Apps.DOCUMENTAL.app:
                    this.rootPanel(this.getDur().isBidoq() ? new AonDocumentalAyudat() : new AonDocumental());
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
						const {datos} = r;
						if(datos && datos.ruta) {
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
		if(!this.appOption)
			switch(app){
				case Apps.ACCOUNTING.app:
					this.rootPanel(new AonAccounting(filter));
					break;
				case Apps.PAYROLL.app:
					const payroll = new AonLaboral(filter);
					payroll.title = MSG.PAYROLL;
					this.rootPanel(payroll);
					break;
			}
	}

	development(title) {
		let aonApplication = this.getApplication();
		aonApplication.development(title);
	}

	isApp(app) {
		if(Apps.ACCOUNTING.app === app.app)
			return this.getDur().isAccounting();
		else if(Apps.FISCAL.app === app.app)
			return this.getDur().isFiscal();
		else if(Apps.COMUNICA.app === app.app)
			return (this.getDur().isComunicaManager() || this.getDur().isComunicaPortal() ) && !this.getDur().isPayroll();
		else if(Apps.PAYROLL.app === app.app)
			return this.getDur().isPayroll();
		else if(Apps.DOCUMENTAL.app === app.app)
			return this.getDur().isDocumental();
		else if(Apps.TIMECONTROL.app === app.app)
			return this.getDur().isTimecontrol();
		else if(Apps.INVOICE.app === app.app)
			return this.getDur().isInvoice();
		else if(Apps.MESSENGER.app === app.app)
			return this.getDur().isMessenger();
		else if(Apps.AON_SALTRA.app === app.app)
			return !this.getDur().isComunica() && !this.getDur().isPayroll() && this.getDur().isSaltra();
		else if(Apps.WAREHOUSE.app === app.app){
			const domain = this.getDur().getDomain();
			return domain.getName() && (domain.getName().includes("udapa") || domain.getName().includes("paturpat") || this.isLocal());
		}else if(Apps.MARKETING.app === app.app){
			return this.getDur().isMarketing() && this.isBeta();
		} else if(ClassicApps.AON_SOLUTIONS.app === app.app){
			return this.getDur().isAon();	
		} else if(ClassicApps.BIDOQ.app === app.app){
			return this.getDur().isBidoq();	
		} else if(ClassicApps.SELFCONTA.app === app.app){
			return this.getDur().isSelfconta();	
		} else return false;
	}

	isOpenMenu(app) {
		if(Apps.ACCOUNTING.app === app.app)
			return this.getDur().isAccountingManager();
		else if(Apps.FISCAL.app === app.app)
			return this.getDur().isFiscalManager();
		else if(Apps.PAYROLL.app === app.app)
			return this.getDur().isPayrollManager();
		else return false;
	}

	async getSidenavActivity(){
		let application = this.getApplication();

		application.addSidenavOptions3({
			id: MSG.ACTIVITY_SUMMARY.toUpperCase(),
			name:MSG.ACTIVITY_SUMMARY.toUpperCase(),
			options: []
		});

		if(this.getDur().isInvoice()) {
			await this.invoiceSidenav();
		}
		await this.requestSidenav();
		await this.noteSidenav();
		
		if(this.SIDENAV_ACTIVITY_SUMMARY.length){
			application.addSidenavOptionsList({
				id: MSG.ACTIVITY_SUMMARY.toUpperCase(),
				name:MSG.ACTIVITY_SUMMARY.toUpperCase()
			}, this.SIDENAV_ACTIVITY_SUMMARY);
		} else {
			application.removeSidenavById(MSG.ACTIVITY_SUMMARY.toUpperCase());
		}
	}

	async invoiceSidenav(){
		try {
			const notice = await getDomainNotice();
	
			let inboxCount = 0;
			let rejectedCount = 0;
			if(notice.invoice && notice.invoice.inbox && notice.invoice.inbox.count && notice.invoice.inbox.count > 0) {
				inboxCount = notice.invoice.inbox.count;
			}
			if(notice.invoice && notice.invoice.rejected && notice.invoice.rejected.count && notice.invoice.rejected.count > 0) {
				rejectedCount = notice.invoice.rejected.count;
			}

			this.SIDENAV_ACTIVITY_SUMMARY.push({
				name: MSG.PENDING_INVOICES,
				icon: 'inbox',
				count: inboxCount,
				fn: () => {
					this.rootPanel(new AonInvoicePanel());
				}
			});

			this.SIDENAV_ACTIVITY_SUMMARY.push({
				name: MSG.PENDING_REVIEW,
				icon: MATERIAL_ICONS.ERROR,
				count: rejectedCount,
				fn: () => {
					let aonInvoice = new AonInvoicePanel();
					aonInvoice.status = CONSTANT.REJECTED;
					this.rootPanel(aonInvoice);
				}
			});
		} catch (error) {
			console.log(error);
		}
	}

	async requestSidenav(){
		try {
			const {id:task_holder} = await getTaskHolder();
			if(task_holder){
				const count = await getTaskCount({task_holder});
				if(count.task_holder) this.SIDENAV_ACTIVITY_SUMMARY.push({
					name: MSG.REQUESTS_RECEIVED,
					icon: MATERIAL_ICONS.MOVE_TO_INBOX,
					count:count.task_holder,
					fn: () =>{
						let aonMessenger = new AonMessenger();
						aonMessenger._filter.task_holder = task_holder;
						this.rootPanel(aonMessenger);
					}
				});
				if(count.sender) this.SIDENAV_ACTIVITY_SUMMARY.push({
					name: MSG.REQUESTS_SENT,
					icon: MATERIAL_ICONS.OUTBOX,
					count:count.sender,
					fn: () =>{
						let aonMessenger = new AonMessenger();
						aonMessenger._filter.sender = task_holder;
						this.rootPanel(aonMessenger);
					} 
				});
			}
		} catch (error) {
			console.log(error);
		}
	}

	async noteSidenav(){
		try {
			const {total, total_expired} = await getNoteCount();
			if(total>0){
				this.SIDENAV_ACTIVITY_SUMMARY.push({
					name: MSG.NOTES,
					icon: MATERIAL_ICONS.STICKY_NOTE ,
					count:`${total_expired}/${total}`,
					fn: () =>{}
				});
			}
		} catch (error) {
			console.log(error);
		}
	}

	statOption(app) {
		switch(app){
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
				GWT.load(GWT.INVOICE_STAT);
				break;
			case Apps.TIMECONTROL.app:
				break;
			case Apps.MESSENGER.app:
				if(this.isBeta())
					GWT.load(GWT.TASK_STAT);
				break;
			}
	}

	addOption(app, button) {
		switch(app){
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
				aonMessengerChat.data = {source:TASK_SOURCE.QUERY};
				this.rootPanel(aonMessengerChat);
				break;
			}
	}

	menuOption(app) {
		let aonMenu = this.getElement('aonMenu');
		aonMenu.buildAppMenu(app);
	}
	
	uploadOption(app) {
		switch(app){
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
				this.getElement(this.INPUT_INVOICE_FILE).click();
				break;
			case Apps.TIMECONTROL.app:
				break;
			case Apps.MESSENGER.app:
				break;
			}
	}

	addNewOptions(e) {
		let rect = e.target.getBoundingClientRect();
    	let x = e.clientX - rect.left;
		let y = e.clientY - rect.top;

		const top  = rect.top + y;
	  	const left = rect.left + x + 180;

    	let d = this.getElement(this.getApplication().OPTION_DIALOG);

		const NEW_INVOICE = {
			id: 'invoice',
			name: MSG.NEW_INVOICE,
			icon: MATERIAL_ICONS.RECEIPT,
			fn: () => this.getElement(this.INPUT_INVOICE_FILE).click()
		};

		const NEW_DOCUMENT = {
			id: 'document',
			name: MSG.NEW_DOCUMENT,
			icon: 'description',
			fn: () => this.getElement(this.INPUT_DOCUMENT_FILE).click()
		};

		const NEW_MESSENGER = {
			id: 'messenger',
			name: MSG.NEW_REQUEST,
			icon: 'message',
			fn: () => {this.development(MSG.NEW_REQUEST);}
		};

	  	let actions = [NEW_INVOICE, NEW_DOCUMENT, NEW_MESSENGER];
	  
	  	d.setMenuOptions(actions, top, left);
	  	d.open();
	}

	addInvoice(button, dashboard) {		
		let invoicePanel = new AonInvoicePanel();	
		let top;
		let left;
		if(dashboard){
			top  = button.getBoundingClientRect().top + 55;
			left = button.getBoundingClientRect().right + 7;
		} else {
			top  = button.getBoundingClientRect().top;
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
		if(app.app === Apps.DOCUMENTAL.app){
			return this.getDur().isDocumentalPortal() || this.getDur().isDocumentalManager();
		} else return true;
	}
}

if(!window.customElements.get('aon-desktop')){
	window.customElements.define('aon-desktop', AonDesktop);
}
