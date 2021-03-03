import {AonElement} from '../../components/AonElement.js';
import {Apps, ClassicApps, Services, OtherServices, Packs} from  '../../services/app.js';
import {getDomainUserRoles, setDomainApp} from  '../../services/service.js';
import {DomainUserRoles} from '../../models/DomainUserRoles.js';
import {App} from '../../models/enums.js';


import '../../components/aon-card.js';
import '../../components/aon-icon-button.js';
import '../../components/aon-icon.js';

// CONSTANTS
import * as CONSTANT from "../../environments/constants.js";
import * as MSG from "../../environments/msg.js";
import * as MATERIAL_ICONS from "../../environments/materialIcons.js";

export class AonMarketplace extends AonElement {

	APP;
	apps;

	get id() {
		return this.getAttribute('id');
	}

	set id(id) {
		this.setAttribute('id', id);
	}

	constructor () {
		super();
	}

	connectedCallback () {
		this.initialize();
		getDomainUserRoles({}).then(r => {
			this.apps = r.domainApps;
			this.build(new DomainUserRoles(r));
		});
	}

	initialize() {
		this.id = this.id || 'aonMarketplace';
		this.APP = this.id + 'App';
		this.apps = [];
	}

	build(dur) {
		let contratar = document.createElement('button');
		contratar.className = 'aonButton';
		contratar.style.width = '110px';
		contratar.style.padding = '0.3rem 0.8rem';
		contratar.style.borderRadius = '25px';
		contratar.innerHTML = MSG.AON_MSG_CONTRACT;
		contratar.style.backgroundColor = '#12ccd1';
		contratar.style.position = 'absolute';
		contratar.style.right = '70px';
		contratar.style.top = '15px';
		contratar.addEventListener('click', () => {
			this.save();
		});
		this.appendChild(contratar);

		this.buildTitle('Packs');
		this.buildApps(Packs, dur);

		this.buildTitle('Aplicaciones');
		this.buildApps(Apps, dur);

		this.buildTitle('Servicios');
		this.buildApps(Services, dur);

		this.buildTitle('Aplicaciones Clásicas');
		this.buildApps(ClassicApps, dur);

		this.buildTitle('Otros Servicios');
		this.buildApps(OtherServices, dur);
	}

	buildApps(apps, dur) {
		let ul = document.createElement('ul');
		ul.className = 'list-group';
		ul.style.marginLeft = '60px';
		ul.style.marginRight = '60px';
		this.appendChild(ul);
		for (let key in apps){
			const app = apps[key];
			let contratado = this.hasApp(dur, app.app.toUpperCase());
			let li = document.createElement('li');
			li.className = 'list-group-item aonAppLi';
			let span = document.createElement('span');
			span.style.margin = '20px';

			if(app.icon) {
				let color = contratado || app.app.includes('pack') ? app.color : 'lightgray';
				span.innerHTML = `<aon-icon id="${this.APP + app.app + 'Icon'}" icon="${app.icon}" color="${color}" size="30px"></aon-icon>`;
			} else {
				let img = document.createElement('img');
				img.style.width = '30px';
				img.src = app.logo;
				span.appendChild(img);
			}

			let span2 = document.createElement('span');
			span2.className = 'aonAppTitle';
			span2.innerHTML = app.title;
			span.appendChild(span2);

			if(app.subtitle){
				let span3 = document.createElement('span');
				span3.innerHTML = app.subtitle;
				span3.style.color = 'gray';
				span.appendChild(span3);
			}

			let buttons = document.createElement('span');
			buttons.style.position = 'absolute';
			buttons.style.right = '10px';

			let price = document.createElement('span');
			price.id = this.APP + app.app + 'Price';
			price.style.margin = '10px';
			price.style.color = 'gray';
			price.innerHTML = app.price;
			buttons.appendChild(price);

			let moreInfo = document.createElement('a');
			moreInfo.style.margin = '10px';
			moreInfo.style.color = 'gray';
			moreInfo.style.cursor = 'pointer';
			moreInfo.innerHTML = 'Más Info';
			moreInfo.addEventListener('click', () => {
				window.open(app.moreInfo || 'https://www.aonsolutions.es/');
			});
			buttons.appendChild(moreInfo);

			let contratar = document.createElement('button');
			contratar.id = this.APP + app.app + 'ContractButton';
			contratar.className = 'aonButton';
			contratar.style.width = '110px';
			contratar.style.padding = '0.3rem 0.8rem';
			contratar.style.borderRadius = '25px';
			contratar.innerHTML = contratado ? MSG.AON_MSG_DEACTIVATE : MSG.AON_MSG_ACTIVATE;
			contratar.style.backgroundColor = '#002469';
			contratar.style.opacity = contratado ? '0.3' : '1';
			if(this.isDisabled(dur, app.app.toUpperCase()) || this.hasParentApp(dur, app.app.toUpperCase()) || app.disabled){
				contratar.disabled = true;
				contratar.style.opacity = '0.3';
				contratar.style.backgroundColor = 'gray';
			}

			contratar.addEventListener('click', () => {
				contratado = !contratado;
				this.activate(app, contratado, false);
			});

			buttons.appendChild(contratar);
			span.appendChild(buttons);
			li.appendChild(span);
			ul.appendChild(li);
		}
	}

	activate(app, contract, disabled){
		let contractIcon = this.getElement(this.APP + app.app + 'Icon');
		if(contractIcon)
			contractIcon.color = contract || app.app.includes('pack') ? app.color : 'lightgray';
		let contractButton = this.getElement(this.APP + app.app + 'ContractButton');
		contractButton.innerHTML = contract ? MSG.AON_MSG_DEACTIVATE : MSG.AON_MSG_ACTIVATE;
		contractButton.style.opacity = contract ? '0.3' : '1';
		if(contract && !disabled) {
			this.apps.push(app.app.toUpperCase());
		} else if(this.apps.includes(app.app.toUpperCase())) {
			const index = this.apps.indexOf(app.app.toUpperCase());
			this.apps.splice(index, 1);
		}

		if((contract && disabled) || app.disabled) {
			contractButton.disabled = true;
			contractButton.style.opacity = '0.3';
			contractButton.style.backgroundColor = 'gray';
		} else {
			contractButton.disabled = false;
			contractButton.style.backgroundColor = '#002469';
		}

		if(app.app === 'pack_suite') {
			this.activate(Packs.PORTAL, contract, true);
			this.activate(Packs.PAYROLL, contract, true);
			this.activate(Packs.FISCAL_ACCOUNTING, contract, true);
		}

		if(app.apps && !disabled) {
			for (let i = 0; i < app.apps.length ; i++) {
				this.activate(app.apps[i], contract, true);
			}
		}
	}

	save() {
		setDomainApp({
			apps: this.apps
		}).then(() => {
			let aonApplication = document.querySelector('aon-application');
			let toast = this.getElement(aonApplication.TOAST);
			toast.start({
				type: 'success',
				message: 'Datos Guardados Correctamente'
			});
			getDomainUserRoles({reload:true}).then(r => {
				this.build(new DomainUserRoles(r));
			});
		});
	}

	buildTitle(title) {
		let div = document.createElement('div');
		div.style.color = 'gray';
		div.style.paddingTop = '20px';
		div.style.paddingBottom = '20px';
		div.style.marginLeft = '60px';
		div.innerHTML = title.toUpperCase();
		this.appendChild(div);
	}

	hasApp(dur, app) {
		if(App.ACCOUNTING === app)
			return dur.hasAccounting();
		else if(App.FISCAL === app)
			return dur.hasFiscal();
		else if(App.PAYROLL === app)
			return dur.hasPayroll();
		else if(App.COMUNICA === app)
			return dur.hasComunica();
		else if(App.DOCUMENTAL === app)
			return dur.hasDocumental();
		else if(App.TIMECONTROL === app)
			return dur.hasTimeControl();
		else if(App.INVOICE === app)
			return dur.hasInvoice();
		else if(App.MESSENGER === app)
			return dur.hasMessenger();
		else if(App.PACK_SUITE === app)
			return dur.hasPackSuite();
		else if(App.PACK_PORTAL === app)
			return dur.hasPackPortal();
		else if(App.PACK_PAYROLL === app)
			return dur.hasPackPayroll();
		else if(App.PACK_FISCAL_ACCOUNTING === app)
			return dur.hasPackFiscalAccounting();
		else return dur.hasApp(app);
	}

	hasParentApp(dur, app) {
		if(App.ACCOUNTING === app)
			return dur.hasParentAccounting();
		else if(App.FISCAL === app)
			return dur.hasParentFiscal();
		else if(App.PAYROLL === app)
			return dur.hasParentPayroll();
		else if(App.COMUNICA === app)
			return dur.hasParentComunica();
		else if(App.DOCUMENTAL === app)
			return dur.hasParentDocumental();
		else if(App.TIMECONTROL === app)
			return dur.hasParentTimeControl();
		else if(App.INVOICE === app)
			return dur.hasParentInvoice();
		else if(App.MESSENGER === app)
			return dur.hasParentMessenger();
		else if(App.PACK_SUITE)
			return dur.hasParentPackSuite();
		else if(App.PACK_PORTAL)
			return dur.hasParentPackPortal();
		else if(App.PACK_PAYROLL)
			return dur.hasParentPackPayroll();
		else if(App.PACK_FISCAL_ACCOUNTING)
			return dur.hasParentPackFiscalAccounting();
		else return dur.hasParentApp(app);
	}

	isDisabled(dur, app){
		if(App.ACCOUNTING === app)
			return dur.hasPackSuite() || dur.hasPackFiscalAccounting();
		else if(App.FISCAL === app)
			return dur.hasPackSuite() || dur.hasPackFiscalAccounting();
		else if(App.PAYROLL === app)
			return dur.hasPackSuite() || dur.hasPackPayroll();
		else if(App.COMUNICA === app)
			return dur.hasPackSuite() || dur.hasPackPayroll();
		else if(App.DOCUMENTAL === app)
			return dur.hasPackSuite() || dur.hasPackPortal();
		else if(App.TIMECONTROL === app)
			return dur.hasPackSuite() || dur.hasPackPortal() || dur.hasComunica();
		else if(App.INVOICE === app)
			return dur.hasPackSuite() || dur.hasPackPortal();
		else if(App.MESSENGER === app)
			return dur.hasPackSuite() || dur.hasPackPortal();
		else if(App.PACK_PORTAL === app)
			return dur.hasPackSuite();
		else if(App.PACK_PAYROLL === app)
			return dur.hasPackSuite();
		else if(App.PACK_FISCAL_ACCOUNTING === app)
			return dur.hasPackSuite();
		else return false;
	}
}
if(!window.customElements.get('aon-marketplace')){
	window.customElements.define('aon-marketplace', AonMarketplace);
}
