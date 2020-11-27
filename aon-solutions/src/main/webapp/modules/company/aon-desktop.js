import {AonElement} from '../../components/AonElement.js';
import { Apps, Services, OtherServices, AccountingMenu, PayrollMenu, AeatFiscalMenu, ArabaFiscalMenu,
	 GipuzkoaFiscalMenu, BizkaiaFiscalMenu, NavarraFiscalMenu, ToolsMenu} from  '../../services/app.js';
import {getDomainApps, setDomainApp, getDomainNotice} from  '../../services/service.js';
import {bidoq} from  '../../services/bidoq.js';
import {startModule, rootPanel} from '../../services/gwtLoader.js';

import '../../components/aon-icon.js';
import '../../components/aon-application.js';

import '../marketplace/aon-marketplace.js';

export class AonDesktop extends AonElement {

	static get observedAttributes() {
		return ['company'];
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

	attributeChangedCallback(name, oldValue, newValue) {
		if('company' === name){
			let company = this.getAttribute('company') ? JSON.parse(this.getAttribute('company')) : undefined;
			if(company) {
				getDomainApps(company.domain).then(r => {
					getDomainNotice().then(notice => {
						this.build(r, notice);
					});
				});
			}
		}
	}

	constructor () {
		super();
		this.id = 'aonDesktop';
	}

	connectedCallback () {
		let company = this.getAttribute('company') ? JSON.parse(this.getAttribute('company')) : undefined;
		if(company) {
			getDomainApps(company.domain).then(r => {
				getDomainNotice().then(notice => {
					this.build(r, notice);
				});
			});
		}
  }

	build(r, notice) {
		this.innerHTML = `
			<aon-application id="aonDesktopMain" title="Desktop" main="true"></aon-application>
		`;

		let inboxCount = 0;
		if(notice.invoice && notice.invoice.inbox && notice.invoice.inbox.count && notice.invoice.inbox.count > 0) {
			inboxCount = notice.invoice.inbox.count;
		}

		let rejectedCount = 0;
		if(notice.invoice && notice.invoice.rejected && notice.invoice.rejected.count && notice.invoice.rejected.count > 0) {
			rejectedCount = notice.invoice.rejected.count;
		}

		let aonDesktop = document.getElementById('aonDesktopMain');

		let taskOptions = [{
				name: 'Documentos sin leer',
				icon: 'snippet_folder',
				fn: () => {}
			},{
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
				name: 'Solicitudes Abiertas',
				icon: 'assignment',
				fn: () => {}
			}, {
				name: 'Solicitudes para ti',
				icon: 'assignment_ind',
				fn: () => {}
			}
		];
		aonDesktop.addSidenavOptions('TAREAS PENDIENTES', taskOptions);

		let appOptions = [];
		for (let key in Apps){

			if(r.includes(Apps[key].app)) {
				let option = {
					name: Apps[key].title,
					aonIcon: {
					 	icon: Apps[key].icon,
					 	color: Apps[key].color
					},
					fn: () => this.appSelection(Apps[key].app)
				}
				appOptions.push(option);
			}
		}
		aonDesktop.addSidenavOptions('APLICACIONES CONTRATADAS', appOptions);

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

		let classicOptions = [
			{
				name: 'aonSolutions',
				img: '../assets/apps/aon.png',
				fn: () => open('https://' + localStorage.getItem('aon_domain_name') + '/login?token=' + localStorage.getItem('aon_session_id'))
			},
			{
				name: 'Bidoq',
				img: '../assets/apps/bidoq.png',
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
			}
		];
		aonDesktop.addSidenavOptions('VISTA CLÁSICA', classicOptions);

		let div = document.createElement('div');
		div.style.marginLeft = '100px';
		div.style.marginRight = '100px';
		aonDesktop.setContent(div);

		let banner = document.createElement('div');
		banner.style.marginTop = '20px';

		let bannerImg = document.createElement('img');
		bannerImg.src = '../assets/img/atp_img_publi.jpg';
		bannerImg.style.width = '100%';
		bannerImg.style.maxWidth = '1117px';
		banner.appendChild(bannerImg);
		div.appendChild(banner);

		div.appendChild(this.buildTitle('CONTRATADOS'));

		let ul = document.createElement('ul');
		ul.className = 'list-group';

		let contratados = document.getElementById('aonDesktopContratados');
		let services = document.getElementById('aonDesktopServiceContratados');


		for (let key in Apps){
			if(r.includes(Apps[key].app)) {
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

		div.appendChild(this.buildTitle('DISPONIBLES'));

		this.buildDisponibles(div, r);

		div.appendChild(this.buildTitle('OTROS SERVICIOS'));

		this.buildOtherServices(div, r);
	}

	buildTitle(title) {
		let div = document.createElement('div');
		div.style.color = 'gray';
		div.style.paddingTop = '20px';
		div.style.paddingBottom = '20px';
		div.innerHTML = title;
		return div;
	}

	buildDisponibles(el, r) {
		let ul = document.createElement('ul');
		ul.className = 'list-group';
		el.appendChild(ul);
		for (let key in Apps){
			if(!r.includes(Apps[key].app))
				this.buildListElement(ul, Apps, key, r);
		}
		for (let key in Services){
			if(!r.includes(Services[key].app))
			this.buildListElement(ul, Services, key, r);
		}
	}

	buildOtherServices(el, r) {
		let ul = document.createElement('ul');
		ul.className = 'list-group';
		el.appendChild(ul);
		for (let key in OtherServices){
			this.buildListElement(ul, OtherServices, key, r);
		}
	}

	buildListElement(ul, apps, key, r){
		let li = document.createElement('li');
		li.className = 'list-group-item aonAppLi';
		ul.appendChild(li);
		let span = document.createElement('span');
		span.style.margin = '20px';
		li.appendChild(span);

		if(apps[key].icon) {
			let dispId = "aonDesktopDisponibles" + apps[key].app;
			span.innerHTML = `<aon-icon id="${dispId}" icon="${apps[key].icon}" color="lightgrey" size="30px"></aon-icon>`;

			li.addEventListener('mouseover', () => {
				this.getElement(dispId).setAttribute('color', apps[key].color);
			});

			li.addEventListener('mouseleave', () => {
				this.getElement(dispId).setAttribute('color', 'lightgrey');
			});
		} else {
			let img = document.createElement('img');
			img.style.width = '30px';
			img.src = apps[key].logo;
			span.appendChild(img);
		}

		let span2 = document.createElement('span');
		span2.className = 'aonAppTitle';
		span2.innerHTML = apps[key].title;
		span.appendChild(span2);

		let buttons = document.createElement('span');
		buttons.style.position = 'absolute';
		buttons.style.right = '10px';

		let moreInfo = document.createElement('a');
		moreInfo.style.margin = '10px';
		moreInfo.style.color = 'gray';
		moreInfo.style.cursor = 'pointer';
		moreInfo.innerHTML = 'Más Info';
		moreInfo.addEventListener('click', () => {
			window.open(apps[key].moreInfo ? apps[key].moreInfo : 'https://www.aonsolutions.es/');
		});
		buttons.appendChild(moreInfo);
		let contratado = r.includes(apps[key].app)
		let contratar = document.createElement('button');
		contratar.className = 'aonButton';
		contratar.style.width = '110px';
		contratar.style.padding = '0.3rem 0.8rem';
		contratar.style.borderRadius = '25px';
		contratar.innerHTML = contratado ? 'Desactivar' : 'Contratar';
		contratar.style.backgroundColor = '#002469';
		contratar.style.opacity = contratado ? '0.3' : '1';
		if(OtherServices[key])  {
			buttons.style.right = '120px';
			contratar.disabled = true;
			contratar.style.backgroundColor= 'lightgrey';
		}
		contratar.addEventListener('click', () => {
			rootPanel('<aon-marketplace></aon-marketplace>');
		});
		if(!OtherServices[key])  {
			buttons.appendChild(contratar);
		}
		span.appendChild(buttons);

		return li;
	}

	appSelection(app) {
		switch(app){
			case Apps.DOCUMENTAL.app:
				//startModule('aon_gwt_aio', 'documents');
				rootPanel('<aon-documental></aon-documental>')
				break;
			case Apps.ACCOUNTING.app:
				rootPanel('<aon-contable></aon-contable>');
				break;
			case Apps.FISCAL.app:
				rootPanel('<aon-fiscal></aon-fiscal>');
				break;
			case Apps.PAYROLL.app:
				rootPanel('<aon-laboral></aon-laboral>');
				break;
			case Apps.COMUNICA.app:
				rootPanel('<aon-comunica></aon-comunica>');
				break;
			case Apps.INVOICE.app:
				rootPanel('<aon-invoice-panel></aon-invoice-panel>');
				break;
			case Apps.TIMECONTROL.app:
				rootPanel('<aon-signin></aon-signin>');
				break;
		}
	}

}

window.customElements.define('aon-desktop', AonDesktop);
