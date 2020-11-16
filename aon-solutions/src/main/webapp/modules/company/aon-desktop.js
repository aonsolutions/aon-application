import {AonElement} from '../../components/AonElement.js';
import { Apps, Services, AccountingMenu, PayrollMenu, AeatFiscalMenu, ArabaFiscalMenu,
	 GipuzkoaFiscalMenu, BizkaiaFiscalMenu, NavarraFiscalMenu, ToolsMenu} from  '../../services/app.js';
import {getDomainApps, setDomainApp} from  '../../services/service.js';
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
					if(this.isMobile()) {
						this.buildMobile(r);
					} else this.build(r);
				//	componentHandler.upgradeAllRegistered();
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
				this.build(r);
			//	componentHandler.upgradeAllRegistered();
			});
		}
  }

	getAppToolbarBackgroundColor(app) {
		let company;
		if(this.getAttribute('company')){
			company = JSON.parse(this.getAttribute('company'));
		}
		switch(app){
			case Apps.ACCOUNTING.app:
				return '#D8B03D';
			case Apps.FISCAL.app:
				if(company && company.administration && 'ALAVA' === company.administration){
					return '#a30c51';
				} else if(company && company.administration && 'BIZKAIA' === company.administration){
					return '#d70004';
				} else if(company && company.administration && 'GIPUZKOA' === company.administration){
					return '#a1c031';
				} else if(company && company.administration && 'NAVARRA' === company.administration){
					return '#da002a';
				} else return '#3a85c3';
			case Apps.PAYROLL.app:
				return '#90BD75';
			case Apps.TOOLS.app:
				return 'gray';
			default: return '#f1f1f1';
		}
	}


	build(r) {
		this.innerHTML = `
			<aon-application id="aonDesktopMain" title="Desktop" main="true"></aon-application>
		`;
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
				icon: 'inbox',
				fn: () => {}
			}, {
				name: 'Facturas Rechazadas',
				icon: 'report',
				fn: () => {}
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
					fn: () => alert('Panel ' + Apps[key].title)
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
				li.className = 'list-group-item';
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

				let moreInfo = document.createElement('a');
				moreInfo.style.margin = '10px';
				moreInfo.style.color = 'gray';
				moreInfo.style.cursor = 'pointer';
				moreInfo.innerHTML = 'Más Info';
				moreInfo.addEventListener('click', () => {
					window.open('https://www.aonsolutions.es/');
				});
				buttons.appendChild(moreInfo);

				let open = document.createElement('button');
				open.type = 'button';
				open.className = 'btn btn-outline-dark';
				open.style.width = '100px';
				open.style.borderRadius = '25px';
				open.innerHTML = 'Abrir';
				buttons.appendChild(open);
				span.appendChild(buttons);
				li.appendChild(span);
				ul.appendChild(li);
			}
  	}
		div.appendChild(ul);

		div.appendChild(this.buildTitle('DESCUBRIR'));

		this.buildCards(div, r);

		div.appendChild(this.buildTitle('OTROS SERVICIOS'));
		// TODO
	}

	buildTitle(title) {
		let div = document.createElement('div');
		div.style.color = 'gray';
		div.style.paddingTop = '20px';
		div.style.paddingBottom = '20px';
		div.innerHTML = title;
		return div;
	}

	buildCards(el, r) {

	}

}

window.customElements.define('aon-desktop', AonDesktop);
