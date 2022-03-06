import {AonElement} from '../../components/AonElement.js';
import {Apps, ClassicApps, Services, Packs} from  '../../services/app.js';
import {getDomainUserRoles, setDomainApp} from  '../../services/service.js';
import {DomainUserRoles} from '../../models/DomainUserRoles.js';
import {App, ToolbarType} from '../../models/enums.js';
import { AonToolbar } from '../../components/aon-toolbar.js';


import '../../components/aon-card.js';
import '../../components/aon-icon-button.js';
import '../../components/aon-icon.js';

import { CSS, MATERIAL_ICONS, MSG, TAG } from '../../environments/environments.js'; 
import * as ACTION from '../actions.js';
import { AonInput } from '../../components/aon-input.js';
import { AonToast } from '../../components/aon-toast.js';
import { IFRAME } from '../../environments/aonTag.js';
export class AonBooking extends AonElement {

	TOOLBAR;
	USER_NUMBER;
	APP;
	apps;
	users;
	definedUsers;

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
		getDomainUserRoles({reload: true}).then(r => {
			const dur = new DomainUserRoles(r);
			this.apps = dur.getDomainApps();
			this.users = dur.maxDefinedUsers;
			this.definedUsers = dur.definedUsers;
			this.build(dur);
		});
	}

	initialize() {
		this.id = this.id || 'aonBooking';
		this.APP = this.id + 'App';
		this.TOOLBAR = this.id + 'Toolbar';
		this.USER_NUMBER = this.id + 'UserNumber';
		this.apps = [];
	}

	build(dur) {
		let toolbar = new AonToolbar();
		toolbar.id = this.TOOLBAR;
		toolbar.type = ToolbarType.SECONDARY;
		toolbar.title = 'CONTRATACIÓN';
		this.appendChild(toolbar);
		toolbar.addButton2(ACTION.SAVE, () => this.save());

		let content = this.createElement(TAG.DIV);
		content.className = CSS.AON_SUB_CONTENT;
		this.appendChild(content);

		let users = new AonInput();
		users.description = 'Número de Usuarios';
		users.deion = 'Número de Usuarios';
		users.value = this.users;
		users.style.position = 'absolute';
		users.style.right = '70px';
		users.style.top = '5px';
		content.appendChild(users);
		users.onChange(r => this.users = users.value);
		users.addIconWithRemove(MATERIAL_ICONS.PERSON, undefined, () => users.value = '0');	

		this.buildTitle(content, 'Packs');
		this.buildApps(content, Packs, dur);

		this.buildTitle(content, MSG.APPLICATIONS);
		this.buildApps(content, Apps, dur);

		this.buildTitle(content, MSG.SERVICES);
		this.buildApps(content, Services, dur);

		this.buildTitle(content, MSG.CLASSIC_APPLICATIONS);
		this.buildApps(content, ClassicApps, dur);
	}

	buildApps(content, apps, dur) {
		let ul = document.createElement(TAG.UL);
		ul.classList.add(CSS.AON_UL);
		ul.classList.add(CSS.AON_LIST_GROUP_TOP);
		ul.style.marginLeft = '60px';
		ul.style.marginRight = '60px';
		content.appendChild(ul);
		for (let key in apps){
			const app = apps[key];
			let contratado = this.hasApp(dur, app.app.toUpperCase());
			let li = document.createElement('li');
		  	li.classList.add(CSS.AON_LIST_GROUP_ITEM);
		    li.classList.add(CSS.AON_APP_LI);
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
				span3.style.fontWeight = '400';
				span2.appendChild(span3);
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

			let moreInfo = document.createElement(TAG.A);
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
			contratar.innerHTML = contratado ? MSG.DEACTIVATE : MSG.ACTIVATE;
			contratar.style.backgroundColor = '#002469';
			contratar.style.opacity = contratado ? '0.3' : '1';
			if(this.isDisabled(dur, app.app.toUpperCase()) || this.hasParentApp(dur, app.app.toUpperCase()) || app.disabled){
				contratar.disabled = true;
				contratar.style.opacity = '0.3';
				contratar.style.backgroundColor = 'gray';
			}

			contratar.addEventListener('click', (e) => {
				e.preventDefault();
				e.stopPropagation();
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
		contractButton.innerHTML = contract ? MSG.DEACTIVATE : MSG.ACTIVATE;
		contractButton.style.opacity = contract ? '0.3' : '1';

		if(contract && !disabled) {
			if(!this.apps.includes(app.app.toUpperCase()))
				this.apps.push(app.app.toUpperCase());
		} else if(this.apps.includes(app.app.toUpperCase())) {
			this.apps.forEach((r, i) => {
				if(r === app.app.toUpperCase())
					this.apps.splice(i, 1);
			});
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
		let tID = this.id + 'Toast';
		let toast = this.getElement(tID);
		if(!toast){
			toast = new AonToast(); 
			toast.id = tID;
			this.appendChild(toast);
		}

		if(this.definedUsers > this.users) {
			toast.start({
				type: 'error',
				message: 'El número de usuarios no puede ser mayor que el número de usuarios contratados'
			});
		} else {
			setDomainApp({
				apps: this.apps,
				users: this.users
			}).then(() => {
				toast.start({
					type: 'success',
					message: 'Datos Guardados Correctamente'
				});
				getDomainUserRoles({reload:true}).then(r => {
					//this.build(new DomainUserRoles(r));
				});
			});
	
		}
	}

	buildTitle(content, title) {
		let div = document.createElement('div');
		div.style.color = 'gray';
		div.style.paddingTop = '20px';
		div.style.paddingBottom = '20px';
		div.style.marginLeft = '60px';
		div.innerHTML = title.toUpperCase();
		content.appendChild(div);
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
		else if(App.AIO === app)
			return dur.hasAon();
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
		else if(App.PACK_SUITE === app)
			return dur.hasParentPackSuite();
		else if(App.PACK_PORTAL === app)
			return dur.hasParentPackPortal();
		else if(App.PACK_PAYROLL === app)
			return dur.hasParentPackPayroll();
		else if(App.PACK_FISCAL_ACCOUNTING === app)
			return dur.hasParentPackFiscalAccounting();
		else if(App.AIO === app)
			return dur.hasParentAon();
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
		else if(App.AIO === app)
			return false;
		else if(App.BANK === app) {
			return dur.getDomain().isParent();
		}
		else return false;
	}
}
if(!window.customElements.get('aon-booking')){
	window.customElements.define('aon-booking', AonBooking);
}
