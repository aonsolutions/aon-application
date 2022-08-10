import {AonElement} from '../../components/AonElement.js';
import { Apps, getAppsByDur} from  '../../services/app.js';
import {getDomainNotice, getDomainUserRoles, getTaskCount, getTaskHolder, getTimeControl, getAttach} from  '../../services/service.js';
import {getAccessBidoq} from  '../../services/bidoqService.js';
import {DomainUserRoles} from '../../models/DomainUserRoles.js';
import { CONSTANT, CSS, EVENT, MATERIAL_ICONS, MSG, TAG } from '../../environments/environments.js';
import { AonDocumentalAyudat } from '../documental/ayudat/aon-documental-ayudat.js';
import { AonDocumental } from '../documental/aon-documental.js';
import { AonSign } from '../timecontrol/aon-sign.js';
import { AonTimecontrol } from '../timecontrol/aon-timecontrol.js';
import { uploadInvoices } from "../invoice/InvoiceUtils.js";
import { uploadDocuments } from "../documental/DocumentalUtils.js";
import { AonMessenger } from '../messenger/aon-messenger.js';
import { AonIconButton } from '../../components/aon-icon-button.js';
import { AonInvoicePanel } from '../invoice/aon-invoice-panel.js';
import * as OPTION from '../invoice/InvoiceOptions.js';
import * as GWT from "../../gwt/gwt.js";
import { TASK_SOURCE } from '../messenger/MessengerEnums.js';
import { AonFiscal } from '../fiscal/aon-fiscal.js';
import { AonLaboral } from '../laboral/aon-laboral.js';
import { AonSaltra } from '../laboral/aon-saltra.js';
import '../../components/aon-icon.js';
import '../../components/aon-application.js';
import '../marketplace/aon-marketplace.js';
import { getOfficeProjects } from '../../services/projectService.js';
import { Project } from '../../models/project/Project.js';
import { getNoteCount } from '../../services/noteService.js';
import { AonAccounting } from '../accounting/aon-accounting.js';
import { Attach } from '../../models/Attach.js';

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
	}	

	getDur() {
		return this.dur;
	}

	connectedCallback () {
		this.initialize();
		getDomainUserRoles({}).then(r => {
			this.dur = new DomainUserRoles(r);
			this.build();
		});
  	}

	build() {
		let company = JSON.parse(localStorage.getItem("company"));
		this.innerHTML = /*html*/`
			<input id='${this.INPUT_INVOICE_FILE}' style='display:none;' type='file' name='file' multiple>
			<input id='${this.INPUT_DOCUMENT_FILE}' style='display:none;' type='file' name='file' multiple>
			<aon-application id="${this.AON_DESKTOP}" title="Desktop" main="true"></aon-application>`;
		let aonDesktop = this.getElement(this.AON_DESKTOP);

		let inputInvoiceFile = this.getElement(this.INPUT_INVOICE_FILE);
		inputInvoiceFile.addEventListener(EVENT.CHANGE, ({target}) => uploadInvoices(inputInvoiceFile, target.files));
		
		let inputDocumentFile = this.getElement(this.INPUT_DOCUMENT_FILE);
		inputDocumentFile.addEventListener(EVENT.CHANGE, ({target}) => uploadDocuments(inputDocumentFile, target.files, this.getDur()));

		let divLogo = this.createElement(TAG.DIV);
		divLogo.id = this.id + 'Logo';
		aonDesktop.getSidenav().appendChild(divLogo);

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

		if(this.isBeta()) {
			let myGestor = {
				id: 'Gestor',
				name: 'MI GESTOR'
			};
			aonDesktop.addSidenavOptions2(myGestor, []);
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

		if(this.getDur().isTimecontrol()) {
			getTimeControl().then(r => {
				let aonSign = new AonSign();
				aonDesktop.addSidenavWidget(MSG.TIMECONTROL.toUpperCase(), aonSign);
				aonSign.buildSignin(r);
				let aonHeader = this.getElement('aonHeader');
				aonHeader.timeControlStatus(r);
			});
		}
		let div = this.createElement(TAG.DIV);
		div.style.marginLeft = '100px';
		div.style.marginRight = '100px';
		aonDesktop.setContent(div);

		// if(!this.isBeta()){
		// 	let divSlide = this.createElement(TAG.DIV);
		// 	divSlide.appendChild(new AonStat());
		// 	div.appendChild(divSlide);
		// }

		div.appendChild(this.buildTitle(MSG.AVAILABLE.toUpperCase()));

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
			li.classList.add(CSS.AON_APP_LI);
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
		div.appendChild(ul);
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
			li.classList.add(CSS.AON_APP_LI);
			li.style.borderRight = '0px';
			li.style.borderLeft = '0px';
			li.style.cursor = 'pointer';
			li.title = app.title;
			li.addEventListener(EVENT.CLICK, () => {
				this.appSelection(app.app);
				this.appOption = false
			});
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
							uploadDocuments(inputDocumentFile, files, this.getDur());
							break;
						case Apps.INVOICE.app:
							let inputInvoiceFile = this.getElement(this.INPUT_INVOICE_FILE);
							uploadInvoices(inputInvoiceFile, files);
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
		else return false;
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

		application.addSidenavOptions2({
			id: MSG.ACTIVITY_SUMMARY.toUpperCase(),
			name:MSG.ACTIVITY_SUMMARY.toUpperCase()
		},[]);

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
				name: MSG.REJECTED_INVOICES,
				icon: MATERIAL_ICONS.REPORT,
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
			name: 'Nueva Factura',
			icon: MATERIAL_ICONS.RECEIPT,
			fn: () => this.getElement(this.INPUT_INVOICE_FILE).click()
		};

		const NEW_DOCUMENT = {
			id: 'document',
			name: 'Nuevo Documento',
			icon: 'description',
			fn: () => this.getElement(this.INPUT_DOCUMENT_FILE).click()
		};

		const NEW_MESSENGER = {
			id: 'messenger',
			name: 'Nueva Solicitud',
			icon: 'message',
			fn: () => {this.development(MSG.NEW_REQUEST);}
		};

	  	let actions = [NEW_INVOICE, NEW_DOCUMENT, NEW_MESSENGER];
	  
	  	d.setMenuOptions(actions, top, left);
	  	d.open();
	}

	addInvoice(button) {		
		let invoicePanel = new AonInvoicePanel();	
		let top  = button.getBoundingClientRect().top;
		const left = button.getBoundingClientRect().left;
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
