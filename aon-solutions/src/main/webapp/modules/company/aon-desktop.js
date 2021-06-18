import {AonElement} from '../../components/AonElement.js';
import { Apps} from  '../../services/app.js';
import {getDomainNotice, getDomainUserRoles, getTimeControl} from  '../../services/service.js';
import {getAccessBidoq} from  '../../services/bidoqService.js';
import {DomainUserRoles} from '../../models/DomainUserRoles.js';
import { EVENT, MSG, TAG } from '../../environments/environments.js';
import { AonDocumentalAyudat } from '../documental/ayudat/aon-documental-ayudat.js';
import { AonDocumental } from '../documental/aon-documental.js';
import '../../components/aon-icon.js';
import '../../components/aon-application.js';
import '../marketplace/aon-marketplace.js';
import '../invoice/aon-invoice-panel.js';
import '../laboral/aon-laboral.js';
import '../messenger/aon-messenger.js';
import '../fiscal/aon-fiscal.js';
import '../accounting/aon-accounting.js';
import '../signin/aon-signin.js';
import './aon-stat.js';
import { uploadInvoices } from "../invoice/InvoiceUtils.js";
import { uploadDocuments } from "../documental/DocumentalUtils.js";

export class AonDesktop extends AonElement {

	dur;
	AON_DESKTOP;
	INPUT_INVOICE_FILE;
	INPUT_DOCUMENT_FILE;

	static get observedAttributes() {
		return [];
	}

	get id() {
		return this.getAttribute('id');
	}

	set id(id) {
		this.setAttribute('id', id);
	}

	get company() {
		return this.getAttribute('company');
	}

	set company(company) {
		this.setAttribute('company', company);
	}

	get user() {
		return this.getAttribute('user');
	}

	set user(user) {
		this.setAttribute('user', user);
	}

	constructor () {
		super();
	}

	initialize(){
		this.id = this.id || 'aonDesktop';
		this.AON_DESKTOP = 'aonDesktopMain';
		this.INPUT_INVOICE_FILE = this.id + 'InputInvoiceFile';
		this.INPUT_DOCUMENT_FILE = this.id + 'InputDocumentFile';
	}

	getDur() {
		return this.dur;
	}

	connectedCallback () {
		this.initialize();
		let company = JSON.parse(localStorage.getItem("company"));
		getDomainUserRoles({}).then(r => {
			this.dur = new DomainUserRoles(r);
			if(company.parentId || company.type !== 'CONSULTANCY'){
				getDomainNotice().then(notice => {
					this.build(notice);
				});
			} else this.build();
		});
  	}

	build(notice) {
		let company = JSON.parse(localStorage.getItem("company"));
		this.innerHTML = /*html*/`
			<input id='${this.INPUT_INVOICE_FILE}' style='display:none;' type='file' name='file' multiple>
			<input id='${this.INPUT_DOCUMENT_FILE}' style='display:none;' type='file' name='file' multiple>
			<aon-application id="${this.AON_DESKTOP}" title="Desktop" main="true"></aon-application>`;
		let aonDesktop = this.getElement(this.AON_DESKTOP);
		
		this.getElement(this.INPUT_INVOICE_FILE).addEventListener('change', ({target}) => uploadInvoices(target.files));
	    this.getElement(this.INPUT_DOCUMENT_FILE).addEventListener('change', ({target}) => uploadDocuments(target.files, this.getDur()));

		let sidenav = this.getElement(this.getApplication().SIDENAV);
		sidenav.innerHTML = `
		<div>
		 	<button id="aonNew" class="aonButton" style="padding: 1rem;width: 140px;background-color: white;margin: 15px;border-radius: 50px;color: #002469;display: flex;">
		 		<i id="aonNewIcon" class="material-icons-outlined" style="">add</i>
		 		<span style="
		 				margin-top: 5px;
		 				margin-left: 10px;
		 				position: relative;
		 				">
		 			NUEVO
		 		</span>
			</button>
		</div>
		`;

		let aonNew = this.getElement('aonNew');
		aonNew.addEventListener(EVENT.CLICK, (e) => this.addNewOptions(e));

		if(company.parentId || company.type !== 'CONSULTANCY'){
			let inboxCount = 0;
			if(notice.invoice && notice.invoice.inbox && notice.invoice.inbox.count && notice.invoice.inbox.count > 0) {
				inboxCount = notice.invoice.inbox.count;
			}

			let rejectedCount = 0;
			if(notice.invoice && notice.invoice.rejected && notice.invoice.rejected.count && notice.invoice.rejected.count > 0) {
				rejectedCount = notice.invoice.rejected.count;
			}

			let taskOptions = [{
					name: 'Notificaciones',
					icon: 'notifications',
					fn: () => {}
				},{
					name: 'Facturas Pendientes',
					count: inboxCount,
					icon: 'inbox',
					fn: () => {
						if(inboxCount > 0) {
							this.rootPanelHtml('<aon-invoice-panel></aon-invoice-panel>');
						}
					}
				}, {
					name: 'Facturas Rechazadas',
					count: rejectedCount,
					icon: 'report',
					fn: () => {
						if(rejectedCount > 0) {
							this.rootPanelHtml('<aon-invoice-panel status="refused"></aon-invoice-panel>');
						}
					}
				}, {
					name: 'Solicitudes',
					icon: 'assignment',
					fn: () => this.isBeta() ? this.rootPanelHtml('<aon-messenger></aon-messenger>') : this.development('Solicitud')
				}
			];
			aonDesktop.addSidenavOptions('TAREAS PENDIENTES', taskOptions);
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
				img: 'assets/apps/aon.png',
				fn: () => open('https://' + localStorage.getItem('aon_domain_name') + '/login?token=' + localStorage.getItem('aon_session_id'))
			});
		}

		if(this.getDur().isBidoq()){
			classicOptions.push({
				name: 'Bidoq',
				img: 'assets/apps/bidoq.png',
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
				fn: () =>open('https://mispapeles.es/selfconta/')
			});
		}

		if(classicOptions.length > 0)
			aonDesktop.addSidenavOptions('VISTA CLÁSICA', classicOptions);

		getTimeControl().then(r => {
			aonDesktop.addSidenavWidgetHTML('CONTROL HORARIO','<aon-sign></aon-sign>');
			let aonHeader = this.getElement('aonHeader');
			aonHeader.timeControlStatus(r);
		});

		let div = this.createElement(TAG.DIV);
		div.style.marginLeft = '100px';
		div.style.marginRight = '100px';
		aonDesktop.setContent(div);

		let banner = this.createElement(TAG.DIV);
		banner.style.marginTop = '20px';

		let bannerImg = this.createElement(TAG.IMG);
		bannerImg.src = 'assets/img/atp_img_publi.jpg';
		bannerImg.style.width = '100%';
		bannerImg.style.maxWidth = '1117px';
		banner.appendChild(bannerImg);
		let divSlide = this.createElement(TAG.DIV);
		divSlide.innerHTML = '<aon-stat></aon-stat>'
		div.appendChild(divSlide);

		div.appendChild(this.buildTitle('DISPONIBLES'));

		let ul = this.createElement(TAG.UL);
		ul.className = 'list-group';

		if(company.parentId || company.type !== 'CONSULTANCY'){
			for (let key in Apps){
				if(this.isApp(Apps[key])) {
					let li = this.createElement(TAG.LI);
					li.className = 'list-group-item aonAppLi';
					li.style.borderRight = '0px';
					li.style.borderLeft = '0px';
					li.style.cursor = 'pointer';
					li.addEventListener('click', () => {
						this.appSelection(Apps[key].app);
					});
					let span = this.createElement(TAG.SPAN);
					span.style.margin = '20px';

					if(Apps[key].icon) {
						span.innerHTML = `<aon-icon icon="${Apps[key].icon}" color="${Apps[key].color}" size="30px"></aon-icon>`;
					} else {
						let img = this.createElement(TAG.IMG);
						img.style.width = '30px';
						img.src = Apps[key].logo;
						span.appendChild(img);
					}
					let span2 = this.createElement(TAG.SPAN);
					span2.className = 'aonAppTitle';
					span2.innerHTML = Apps[key].title;
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
			}
  	} else {
			let li = this.createElement(TAG.LI);
			li.className = 'list-group-item aonAppLi';
			li.style.borderRight = '0px';
			li.style.borderLeft = '0px';
			li.style.cursor = 'pointer';
			li.addEventListener('click', () => {
				this.rootPanelHtml('<aon-configuration></aon-configuration>');
			});
			let span = this.createElement(TAG.SPAN);
			span.style.margin = '20px';

			span.innerHTML = `<aon-icon icon="aon_app" color="black" size="30px"></aon-icon>`;

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

	buildTitle(title) {
		let div = this.createElement(TAG.DIV);
		div.style.color = 'gray';
		div.style.paddingTop = '20px';
		div.style.paddingBottom = '20px';
		div.innerHTML = title;
		return div;
	}

	appSelection(app) {
		switch(app){
			case Apps.DOCUMENTAL.app:
				const aonDocumental = this.getDur().isBidoq() ? new AonDocumentalAyudat() : new AonDocumental();
				this.rootPanel(aonDocumental);
				break;
			case Apps.ACCOUNTING.app:
				this.rootPanelHtml('<aon-accounting></aon-accounting>');
				break;
			case Apps.FISCAL.app:
				this.rootPanelHtml('<aon-fiscal></aon-fiscal>');
				break;
			case Apps.COMUNICA.app:
				this.rootPanelHtml(`<aon-laboral title="${MSG.COMUNICA}"></aon-laboral>`);
				break;
			case Apps.PAYROLL.app:
				this.rootPanelHtml(`<aon-laboral title="${MSG.PAYROLL}"></aon-laboral>`);
				break;
			case Apps.INVOICE.app:
				this.rootPanelHtml('<aon-invoice-panel></aon-invoice-panel>');
				break;
			case Apps.TIMECONTROL.app:
				this.rootPanelHtml('<aon-signin></aon-signin>');
				break;
			case Apps.MESSENGER.app:
				this.isBeta() ? this.rootPanelHtml('<aon-messenger></aon-messenger>') : this.development('Solicitud');
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
		else return false;
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
			icon: 'receipt',
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
			fn: () => {this.development('Nueva Solicitud');}
		};

	  	let actions = [NEW_INVOICE, NEW_DOCUMENT, NEW_MESSENGER];
	  
	  	d.setMenuOptions(actions, top, left);
	  	d.open();
	}
}

if(!window.customElements.get('aon-desktop')){
	window.customElements.define('aon-desktop', AonDesktop);
}
