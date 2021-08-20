import {AonElement} from '../components/AonElement.js';
import {Apps, AuxApps, MenuApps, AccountingMenu, PayrollMenu, AeatFiscalMenu, ToolsMenu} from  '../services/app.js';
import {getDomainUserRoles} from  '../services/service.js';
import {DomainUserRoles} from '../models/DomainUserRoles.js';
import { CONSTANT, EVENT, TAG } from '../environments/environments.js';
import {AonDocumental} from './documental/aon-documental.js';
import {AonDocumentalAyudat} from './documental/ayudat/aon-documental-ayudat.js';
import '../components/aon-icon.js';
import '../components/aon-icon-button.js';
import './signin/aon-signin.js';
import './faqs/aon-faqs.js';
import './laboral/aon-laboral.js';
import './example/aon-example.js';
import './imports/aon-imports.js';

import './invoice/aon-invoice-panel.js';

import * as GWT from "../gwt/gwt.js";
import { AonMessenger } from './messenger/aon-messenger.js';

const ID = 'id';
const OPENED = 'opened';
const APP = 'app';

export class AonMenu extends AonElement {

	dur;

	get id() {
		return this.getAttribute(CONSTANT.ID);
	}

	set id(id) {
		this.setAttribute(CONSTANT.ID, id);
	}

	get opened() {
		return this.getAttribute('opened');
	}

	set opened(opened) {
		this.setAttribute('opened', opened);
	}

	get app() {
		return this.getAttribute('app');
	}

	set app(app) {
		this.setAttribute('app', app);
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

	constructor() {
		super();
	}

	connectedCallback () {
		this.initialize();
		this.init();
  }

	initialize() {
		this.dur = [];
	}

	init() {
		this.setAttribute('opened', true);
		if(localStorage.getItem('aon_domain_id') && localStorage.getItem('company')){
			getDomainUserRoles({}).then(r => {
				this.dur = new DomainUserRoles(r);
				this.build();
			});
		}
	}

	getDur() {
		return this.dur;
	}

	toogle() {
		let aonMenuSidenav = this.getElement('aonMenuSidenav');
		if(this.getAttribute('opened')) {
			this.close();
		} else if(this.getAttribute('app')){
			aonMenuSidenav.style.width = '250px';
			this.setAttribute('opened', true);
		} else {
			aonMenuSidenav.style.width = '60px';
			this.getRootPanel().style.marginRight = '60px';
			this.setAttribute('opened', true);
		}
	}

	appSelection(app) {
		switch(app){
			case Apps.DOCUMENTAL.app:
				this.rootPanel(this.getDur().isBidoq() ? new AonDocumentalAyudat() : new AonDocumental());
				break;
			case Apps.ACCOUNTING.app:
					if(this.getDur().isAccountingManager()) {
					this.buildAppMenu(Apps.ACCOUNTING);
				} else this.rootPanelHtml('<aon-accounting></aon-accounting>');
				break;
			case Apps.FISCAL.app:
				if(this.getDur().isFiscalManager()) {
					this.buildAppMenu(Apps.FISCAL);
				} else this.rootPanelHtml('<aon-fiscal></aon-fiscal>');
				break;
			case Apps.PAYROLL.app:
				if(this.getDur().isPayrollManager()) {
					this.buildAppMenu(Apps.PAYROLL);
				} else this.rootPanelHtml('<aon-laboral></aon-laboral>');
				break;
			case Apps.INVOICE.app:
				this.rootPanelHtml('<aon-invoice-panel></aon-invoice-panel>');
				break;
			case Apps.TIMECONTROL.app:
				this.rootPanelHtml('<aon-signin></aon-signin>');
				break;
			case Apps.MESSENGER.app:
				this.rootPanel(new AonMessenger());
				break;
			case AuxApps.TOOLS.app:
				this.buildAppMenu(AuxApps.TOOLS);
				break;
		}
	}

	build() {
		this.innerHTML = `
			<div id="aonMenuSidenav" class="aonMenuSidenav"></div>
		`;
		let aonMenuSidenav = this.getElement('aonMenuSidenav');

		aonMenuSidenav.addEventListener('mouseover', () => {
			if(aonMenuSidenav.style.width !== '250px' && aonMenuSidenav.style.width !== '0px' && localStorage.getItem('aon_domain_id')){
				aonMenuSidenav.style.transitionDuration = '0ms';
				aonMenuSidenav.style.width = '175px';
				document.querySelectorAll("[id^='aonMenuListApp-']").forEach((item, i) => {
					item.style.display = 'inline-block';
					item.style.fontSize = '12px';
					item.style.fontFamily = 'Roboto,sans-serif';
					item.style.color = 'black';
					item.style.position = 'absolute';
					item.style.right = '50px';
					item.style.margin = '10px';
				});
			}
		});

		aonMenuSidenav.addEventListener(EVENT.MOUSELEAVE, () => {
			aonMenuSidenav.style.transitionDuration = '500ms';
			if(this.getAttribute('opened')) {
				aonMenuSidenav.style.width = '60px';
				this.getRootPanel().style.marginRight = '60px';
			} else {
				aonMenuSidenav.style.width = '0px';
				this.getRootPanel().style.marginRight = '0px';
			}
			document.querySelectorAll("[id^='aonMenuListApp-']").forEach((item, i) => {
				item.style.display = 'none';
			});
			this.buildMenu();
		});

		if(this.getAttribute('opened')) {
			aonMenuSidenav.style.width = '60px';
			this.getRootPanel().style.marginRight = '60px';
		} else {
			aonMenuSidenav.style.width = '0px';
			this.getRootPanel().style.marginRight = '0px';
		}
		this.buildMenu();
	}

	buildMenu() {
		let ul = this.createElement(TAG.UL);
		ul.id = 'aonMenuList';
		ul.style.margin = '0px';
		ul.style.padding = '0px';
		ul.style.listStyle = 'none';

		let li = this.createElement(TAG.LI);
		li.style.textAlign =  'right';
		li.style.paddingRight = '12px';
		li.style.backgroundColor = 'transparent';
		const icon = this.getAttribute('opened') ? 'keyboard_arrow_right' : 'keyboard_arrow_left';
		li.innerHTML =`<aon-icon-button id="aonMenuShowButton" icon="${icon}"></aon-icon-button>`;
		ul.appendChild(li);

		if(localStorage.getItem('aon_domain_id') && localStorage.getItem('company')){
			// let company = JSON.parse(localStorage.getItem('company'));
			for (let item in MenuApps){
				if(this.isApp(MenuApps[item])){
					ul.appendChild(this.buildApp(MenuApps[item]));
				}
			}
			if(this.getDur().isAdmin()) {
				let liAdd = this.createElement(TAG.LI);
				liAdd.style.textAlign =  'right';
				liAdd.style.paddingRight = '10px';
				liAdd.style.backgroundColor = 'transparent';
				liAdd.innerHTML =`<aon-icon-button id="aonMenuAddButton" icon="add"></aon-icon-button>`;
				ul.appendChild(liAdd);
			}
			aonMenuSidenav.innerHTML = '';
			aonMenuSidenav.appendChild(ul);

			let aonMenuShowButton = this.getElement('aonMenuShowButton');
			aonMenuShowButton.addEventListener(EVENT.CLICK, () => {
				if(this.getAttribute('opened'))
					this.removeAttribute('opened');
				else this.setAttribute('opened', true);
				let toolSection = this.getElement(this.getApplication().getToolbar().TOOL_SECTION);
				// let toolSection = document.querySelector("[id*='aonToolbarToolSection']");
				if(toolSection) {
					toolSection.style.paddingRight = this.getAttribute('opened') ? '0px' : '40px';
				}
				const icon = this.getAttribute('opened') ? 'keyboard_arrow_right' : 'keyboard_arrow_left';
				aonMenuShowButton.setAttribute('icon', icon);
			});

			if(this.getDur().isAdmin()) {
				let aonMenuAddButton = this.getElement('aonMenuAddButton');
				aonMenuAddButton.addEventListener(EVENT.CLICK, () => {
					this.rootPanelHtml('<aon-marketplace id="aonMarketplace" > </aon-marketplace>');
				});
			}
		}
	}

	buildApp(app) {
		let li = this.createElement(TAG.LI);
		if (app) {
			li.id = 'aonMenuList' + app.app;
	    li.style.backgroundColor = 'transparent';
	    li.addEventListener('mouseover', () => {
	      let img = this.getElement('aonMenuListAppImg-' + app.app);
	      img.size = '40px';
	    });

	    li.addEventListener(EVENT.MOUSELEAVE, () => {
	      let img = this.getElement('aonMenuListAppImg-' + app.app);
	      img.size = '30px';
	    });

	    let a = this.createElement('a');
	    a.style.cursor = 'pointer';
	    a.style.textAlign = 'right';
	    a.addEventListener(EVENT.CLICK, () => {
	      this.appSelection(app.app);
	    });

	    let div = this.createElement(TAG.DIV);
	    div.style.padding = '8px 0px';

	    if (app.icon) {
	      div.innerHTML = `
				<span id="aonMenuListApp-${app.app}" style="display:none;"> ${app.title} </span>
				<aon-icon id="aonMenuListAppImg-${app.app}" icon="${app.icon}" color="${app.color}" size="30px" style="margin-right: 15px;"></aon-icon>
			`;

	    } else {
	      let span = this.createElement(TAG.SPAN);
	      span.id = 'aonMenuListApp-' + app.app;
	      span.style.display = 'none';
	      span.style.marginRight = '5px';
	      span.innerHTML = app.title;
	      div.appendChild(span);

	      let img = this.createElement(TAG.IMG);
	      img.id = 'aonMenuListAppImg-' + app.app;
	      img.style.width = '30px';
	      img.style.marginRight = '15px';
	      img.src = app.logo;
	      img.title = app.title;
	      div.appendChild(img);
	    }
	    let div2 = this.createElement(TAG.DIV);

	    a.appendChild(div);
	    li.appendChild(a);
	  }
		return li;
	}

	buildAppMenu(app) {
		let company;
		if(this.getAttribute('company')){
			company = JSON.parse(this.getAttribute('company'));
		}

		let aonMenuSidenav = this.getElement('aonMenuSidenav');
		aonMenuSidenav.style.width = '250';

		let div = this.createElement(TAG.DIV);
	 	div.className = 'aonMenuSidenavAppToolbar';
		div.style.backgroundColor = this.getAppToolbarBackgroundColor(app);

		let span = this.createElement(TAG.SPAN);
		span.className= 'aonTitle';
		span.innerHTML = app.title;

		let span2 = this.createElement(TAG.SPAN);
		span2.className = 'aonRight40';
		span2.innerHTML = '<aon-icon-button id="aon-menu-sidenav-app-launch-button" icon="launch" color="white" noHover="true"></aon-icon-button>';

		let span3 = this.createElement(TAG.SPAN);
		span3.className = 'aonRight0';
		span3.innerHTML = '<aon-icon-button id="aon-menu-sidenav-app-close-button" icon="close" color="white" noHover="true"></aon-icon-button>';
		div.appendChild(span);
		div.appendChild(span2);
		div.appendChild(span3);

		let div2 = this.createElement(TAG.DIV);
		let ul = this.createElement(TAG.UL);
		ul.className = 'aonMenuSidenavSubAppList';
		this.getSubApps(app.app).forEach(subapp => {
			if(!subapp.parent || (subapp.parent && company && company.parent)){
				ul.appendChild(this.buildSubAppMenu(subapp));
			}
		});

		div2.appendChild(ul);

		aonMenuSidenav.innerHTML = '';
		aonMenuSidenav.appendChild(div);
		aonMenuSidenav.appendChild(div2);

		let launchButton = this.getElement('aon-menu-sidenav-app-launch-button');
		launchButton.addEventListener(EVENT.CLICK, () => {
			if(Apps.ACCOUNTING.app === app.app) {
				this.rootPanelHtml('<aon-accounting></aon-accounting>');
			} else if(Apps.FISCAL.app === app.app) {
				this.rootPanelHtml('<aon-fiscal></aon-fiscal>');
			} else if(Apps.PAYROLL.app === app.app) {
				this.rootPanelHtml('<aon-laboral></aon-laboral>');
			}
		});
		let closeButton = this.getElement('aon-menu-sidenav-app-close-button');
		closeButton.addEventListener(EVENT.CLICK, () => {
			aonMenuSidenav.style.width = '175px';
			this.buildMenu();
		});
	}

	development(title) {
		let aonApplication = document.querySelector('aon-application');
		aonApplication.development(title);
	}

	getSubApps(app){
		switch(app){
			case Apps.ACCOUNTING.app:
			 	return AccountingMenu;
			case Apps.FISCAL.app:
				return AeatFiscalMenu;
			case Apps.PAYROLL.app:
				return PayrollMenu;
			case AuxApps.TOOLS.app:
				return ToolsMenu;
		}
	}

	buildSubAppMenu(subapp) {
		let li = this.createElement(TAG.LI);
		li.className = 'aonMenuSidenavSubAppListItem';
		li.title = subapp.title;
		li.addEventListener('mouseover', () => {
 			li.style.backgroundColor = '#f1f1f1';
		});
		li.addEventListener(EVENT.MOUSELEAVE, () => {
			li.style.backgroundColor = 'transparent';
		});

		let a = this.createElement('a');
		a.className = 'aonMenuLink';

		let span = this.createElement(TAG.SPAN);
		span.style.fontSize = "12px";
		span.style.fontWeight = "400";
		span.innerHTML = subapp.title;
		a.appendChild(span);
		li.appendChild(a);
		li.addEventListener(EVENT.CLICK, () => {
			if(subapp.content) {
				this.rootPanelHtml(subapp.content);
			} else if(subapp.initAction) {
				open('https://' + localStorage.getItem('aon_domain_name') + '/login?initAction='+subapp.initAction+'&token=' + localStorage.getItem('aon_session_id'));
			} else GWT.startModule(subapp.module, subapp.entryPoint);

			let aonMenuSidenav = this.getElement('aonMenuSidenav');
			if(this.getAttribute('opened')) {
				aonMenuSidenav.style.width = '60px';
				this.getRootPanel().style.marginRight = '60px';
			} else {
				aonMenuSidenav.style.width = '0px';
				this.getRootPanel().style.marginRight = '0px';
			}
			this.buildMenu();
		});
		return li
	}

	getAppToolbarBackgroundColor(app) {
		let company;
		if(this.getAttribute('company')){
			company = JSON.parse(this.getAttribute('company'));
		}
		if(Apps.FISCAL.app === app.app) {
			if(company && company.administration && 'ALAVA' === company.administration){
				return '#a30c51';
			} else if(company && company.administration && 'BIZKAIA' === company.administration){
				return '#d70004';
			} else if(company && company.administration && 'GIPUZKOA' === company.administration){
				return '#a1c031';
			} else if(company && company.administration && 'NAVARRA' === company.administration){
				return '#da002a';
			} else return '#3a85c3';
		}
		return app.color ? app.color : '#f1f1f1';
	}

	addApp(app) {
		let ul = this.getElement('aonMenuList');
		ul.appendChild(this.buildApp(app));
	}

	removeApp(app) {
		let ul = this.getElement('aonMenuList');
		let li = this.getElement('aonMenuList' + app.app);
		ul.removeChild(li);
	}

	close() {
		let aonMenuSidenav = this.getElement('aonMenuSidenav');
		aonMenuSidenav.style.width = '0px';
		this.getRootPanel().style.marginRight = '0px';
		this.removeAttribute('opened');
	}

	isApp(app) {
		if(MenuApps.ACCOUNTING.app === app.app)
			return this.getDur().isAccounting();
		else if(MenuApps.FISCAL.app === app.app)
			return this.getDur().isFiscal();
		else if(MenuApps.PAYROLL.app === app.app)
			return this.getDur().isPayroll() || this.getDur().isComunica();
		else if(MenuApps.DOCUMENTAL.app === app.app)
			return this.getDur().isDocumental();
		else if(MenuApps.TIMECONTROL.app === app.app)
			return this.getDur().isTimecontrol();
		else if(MenuApps.INVOICE.app === app.app)
			return this.getDur().isInvoice();
		else if(MenuApps.MESSENGER.app === app.app)
			return this.getDur().isMessenger();
		else if(MenuApps.TOOLS.app === app.app)
			return true;
		else return false;
	}

}

window.customElements.define('aon-menu', AonMenu);
