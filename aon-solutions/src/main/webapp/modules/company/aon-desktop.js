import {AonElement} from '../../components/AonElement.js';
import { Apps} from  '../../services/app.js';
import {getDomainNotice, getDomainUserRoles, getTimeControl} from  '../../services/service.js';
import {bidoq} from  '../../services/bidoq.js';
import {rootPanel} from '../../services/gwtLoader.js';
import {DomainUserRoles} from '../../models/DomainUserRoles.js';


import '../../components/aon-icon.js';
import '../../components/aon-application.js';

import '../marketplace/aon-marketplace.js';
import '../invoice/aon-invoice-panel.js';
import '../documental/aon-documental.js';
import '../laboral/aon-laboral.js';
import '../messenger/aon-messenger.js';
import '../fiscal/aon-fiscal.js';
import '../accounting/aon-accounting.js';
import '../signin/aon-signin.js';
import './aon-stat.js';

export class AonDesktop extends AonElement {

	dur;
	AON_DESKTOP;
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
		this.AON_DESKTOP = 'aonDesktopMain';
	}

	getDur() {
		return this.dur;
	}

	connectedCallback () {
		this.initialize();
		getDomainUserRoles({}).then(r => {
			this.dur = new DomainUserRoles(r);
			getDomainNotice().then(notice => {
				this.build(notice);
			});
		});
  }

	build(notice) {
		this.innerHTML = /*html*/`<aon-application id="${this.AON_DESKTOP}" title="Desktop" main="true"></aon-application>`;

		let inboxCount = 0;
		if(notice.invoice && notice.invoice.inbox && notice.invoice.inbox.count && notice.invoice.inbox.count > 0) {
			inboxCount = notice.invoice.inbox.count;
		}

		let rejectedCount = 0;
		if(notice.invoice && notice.invoice.rejected && notice.invoice.rejected.count && notice.invoice.rejected.count > 0) {
			rejectedCount = notice.invoice.rejected.count;
		}

		let aonDesktop = this.getElement(this.AON_DESKTOP);

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
						rootPanel('<aon-invoice-panel></aon-invoice-panel>');
					}
				}
			}, {
				name: 'Facturas Rechazadas',
				count: rejectedCount,
				icon: 'report',
				fn: () => {
					if(rejectedCount > 0) {
						rootPanel('<aon-invoice-panel status="refused"></aon-invoice-panel>');
					}
				}
			}, {
				name: 'Solicitudes',
				icon: 'assignment',
				fn: () => this.isBeta() ? rootPanel('<aon-messenger></aon-messenger>') : this.development('Solicitud')
			}
		];
		aonDesktop.addSidenavOptions('TAREAS PENDIENTES', taskOptions);

		// let appOptions = [];
		// for (let key in Apps){
		// 	if(this.isApp(Apps[key])) {
		// 		let option = {
		// 			name: Apps[key].title,
		// 			aonIcon: {
		// 			 	icon: Apps[key].icon,
		// 			 	color: Apps[key].color
		// 			},
		// 			fn: () => this.appSelection(Apps[key].app)
		// 		}
		// 		appOptions.push(option);
		// 	}
		// }
		// aonDesktop.addSidenavOptions('APLICACIONES DISPONIBLES', appOptions);

		// let serviceOptions = [];
		// for (let key in Services){
		// 	if(r.includes(Services[key].app)) {
		// 		let option = {
		// 			name: Services[key].title,
		// 			fn: () => alert('Panel ' + Services[key].title)
		// 		};
		// 		if(Services[key].logo){
		// 			option.img = Services[key].logo
		// 		} else {
		// 			option.aonIcon = {
		// 				icon: Services[key].icon,
		// 				color: Services[key].color
		// 			};
		// 		}
		// 		serviceOptions.push(option);
		// 	}
		// }
		// aonDesktop.addSidenavOptions('SERVICIOS CONTRATADOS', serviceOptions);

		let classicOptions = [];

		if(!localStorage.getItem('aon_jsf')){ // && this.getDur().isAon()){
			classicOptions.push({
				name: 'aonSolutions',
				img: 'assets/apps/aon.png',
				fn: () => open('https://' + localStorage.getItem('aon_domain_name') + '/login?token=' + localStorage.getItem('aon_session_id'))
			});
		}
		if(localStorage.getItem('aon_jsf')){
			classicOptions.push({
				name: 'aonGestion',
				img: 'assets/apps/aon.png',
				fn: () => loadManagementPanel(this.getElement('aonDesktopMainContent'))
			});
		}

		if(this.getDur().isBidoq()){
			classicOptions.push({
				name: 'Bidoq',
				img: 'assets/apps/bidoq.png',
				fn: () =>{
					bidoq().then(r => {
						let data = JSON.parse(r);
						if(data && data.datos && data.datos.ruta) {
							open(data.datos.respuesta);
						} else {
							open('https://mispapeles.es/');
						}
					});
				}
			});
		}

		if(classicOptions.length > 0)
			aonDesktop.addSidenavOptions('VISTA CLÁSICA', classicOptions);

		getTimeControl().then(r => {
			aonDesktop.addSidenavWidgetHTML('CONTROL HORARIO','<aon-sign></aon-sign>');
			let aonHeader = document.getElementById('aonHeader');
			aonHeader.timeControlStatus(r);
		});


		let div = document.createElement('div');
		div.style.marginLeft = '100px';
		div.style.marginRight = '100px';
		aonDesktop.setContent(div);

		let banner = document.createElement('div');
		banner.style.marginTop = '20px';

		let bannerImg = document.createElement('img');
		bannerImg.src = 'assets/img/atp_img_publi.jpg';
		bannerImg.style.width = '100%';
		bannerImg.style.maxWidth = '1117px';
		banner.appendChild(bannerImg);
		let divSlide = document.createElement('div');
		divSlide.innerHTML = '<aon-stat></aon-stat>'
		div.appendChild(divSlide);

		div.appendChild(this.buildTitle('DISPONIBLES'));

		let ul = document.createElement('ul');
		ul.className = 'list-group';

		let contratados = document.getElementById('aonDesktopContratados');
		let services = document.getElementById('aonDesktopServiceContratados');


		for (let key in Apps){
			if(this.isApp(Apps[key])) {
				let li = document.createElement('li');
				li.className = 'list-group-item aonAppLi';
				li.style.borderRight = '0px';
				li.style.borderLeft = '0px';
				li.style.cursor = 'pointer';
				li.addEventListener('click', () => {
					this.appSelection(Apps[key].app);
				});
				let span = document.createElement('span');
				span.style.margin = '20px';

				if(Apps[key].icon) {
					span.innerHTML = `<aon-icon icon="${Apps[key].icon}" color="${Apps[key].color}" size="30px"></aon-icon>`;
				} else {
					let img = document.createElement('img');
					img.style.width = '30px';
					img.src = Apps[key].logo;
					span.appendChild(img);
				}
				let span2 = document.createElement('span');
				span2.className = 'aonAppTitle';
				span2.innerHTML = Apps[key].title;
				span.appendChild(span2);

				let buttons = document.createElement('span');
				buttons.style.position = 'absolute';
				buttons.style.right = '10px';

				// let moreInfo = document.createElement('a');
				// moreInfo.style.margin = '10px';
				// moreInfo.style.color = 'gray';
				// moreInfo.style.cursor = 'pointer';
				// moreInfo.innerHTML = 'Más Info';
				// moreInfo.addEventListener('click', () => {
				// 	window.open('https://www.aonsolutions.es/');
				// });
				// buttons.appendChild(moreInfo);

				let i = this.createElement('i');
				i.className = 'material-icons';
				i.innerHTML = 'keyboard_arrow_right';
				buttons.appendChild(i);

				// let open = document.createElement('button');
				// open.type = 'button';
				// open.className = 'btn btn-outline-dark';
				// open.style.width = '100px';
				// open.style.borderRadius = '25px';
				// open.innerHTML = 'Abrir';
				// buttons.appendChild(open);
				span.appendChild(buttons);
				li.appendChild(span);
				ul.appendChild(li);
			}
  	}
		div.appendChild(ul);
	}

	buildTitle(title) {
		let div = document.createElement('div');
		div.style.color = 'gray';
		div.style.paddingTop = '20px';
		div.style.paddingBottom = '20px';
		div.innerHTML = title;
		return div;
	}

	appSelection(app) {
		switch(app){
			case Apps.DOCUMENTAL.app:
				rootPanel('<aon-documental></aon-documental>')
				break;
			case Apps.ACCOUNTING.app:
				rootPanel('<aon-accounting></aon-accounting>');
				break;
			case Apps.FISCAL.app:
				rootPanel('<aon-fiscal></aon-fiscal>');
				break;
			case Apps.PAYROLL.app:
				rootPanel('<aon-laboral></aon-laboral>');
				break;
			case Apps.INVOICE.app:
				rootPanel('<aon-invoice-panel></aon-invoice-panel>');
				break;
			case Apps.TIMECONTROL.app:
				rootPanel('<aon-signin></aon-signin>');
				break;
			case Apps.MESSENGER.app:
				this.isBeta() ? rootPanel('<aon-messenger></aon-messenger>') : this.development('Solicitud');
				break;
		}
	}

	development(title) {
		let aonApplication = document.querySelector('aon-application');
		let d = document.getElementById(aonApplication.DIALOG);
		d.clear();
		if(!this.isMobile()) d.width = '400px';
		d.setTitle(title);
		d.setContentHTML('Esta opción está en desarrollo...');
		d.addAcceptAction(() => {});
		d.open();
	}

	isApp(app) {
		if(Apps.ACCOUNTING.app === app.app)
			return this.getDur().isAccounting();
		else if(Apps.FISCAL.app === app.app)
			return this.getDur().isFiscal();
		else if(Apps.PAYROLL.app === app.app)
			return this.getDur().isPayroll();
		// else if(Apps.COMUNICA.app === app.app)
		// 	return this.getDur().isComunica();
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
}

if(!window.customElements.get('aon-desktop')){
	window.customElements.define('aon-desktop', AonDesktop);
}
