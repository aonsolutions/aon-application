import {AonElement} from '../components/AonElement.js';
import {startModule, rootPanel} from '../services/gwtLoader.js';
import {Apps, MenuApps, AccountingMenu, PayrollMenu, AeatFiscalMenu, ToolsMenu, PortalMenu} from  '../services/app.js';
import {getDomainApps, getUserAppRole} from  '../services/service.js';
import '../components/aon-icon.js';
import './comunic@/aon-comunica.js';
import './messenger/aon-messenger.js';
import './signin/aon-signin.js';
import './faqs/aon-faqs.js';
import './contable/aon-contable.js';
import './fiscal/aon-fiscal.js';
import './laboral/aon-laboral.js';
import './example/aon-example.js';
import './imports/aon-imports.js';
import './documental/aon-documental.js';
import './invoice/aon-invoice-panel.js';

const ID = 'id';
const OPENED = 'opened';
const APP = 'app';

export class AonMenu extends AonElement {
	_roles;

	get id() {
		return this.getAttribute('id');
	}

	set id(id) {
		this.setAttribute('id', id);
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
		this._roles = [];
	}

	connectedCallback () {
		this.build();
  }

	toogle() {
		let aonMenuSidenav = document.getElementById('aonMenuSidenav');
		let rootPanel = document.getElementById('rootPanel');
		if(this.getAttribute('opened')) {
			this.close();
		} else if(this.getAttribute('app')){
			aonMenuSidenav.style.width = '250px';
			this.setAttribute('opened', true);
		} else {
			aonMenuSidenav.style.width = '60px';
			rootPanel.style.marginRight = '60px';
			this.setAttribute('opened', true);
		}
	}

	appSelection(app) {
		switch(app){
    	case Apps.DOCUMENTAL.app:
				rootPanel('<aon-documental></aon-documental>')
				//startModule('aon_gwt_aio', 'documents');
				break;
    	case Apps.ACCOUNTING.app:
				if(this._roles.includes('ADMIN') || this._roles.includes('ACCOUNTING_MANAGER')) {
					this.buildAppMenu(Apps.ACCOUNTING);
				} else {
					rootPanel('<aon-contable></aon-contable>')
				}
				break;
			case Apps.FISCAL.app:
				if(this._roles.includes('ADMIN') || this._roles.includes('FISCAL_MANAGER')) {
					this.buildAppMenu(Apps.FISCAL);
				} else {
					rootPanel('<aon-fiscal></aon-fiscal>')
				}
				break;
			case Apps.PAYROLL.app:
				if(this._roles.includes('ADMIN') || this._roles.includes('PAYROLL_MANAGER')) {
					this.buildAppMenu(Apps.PAYROLL);
				} else {
					rootPanel('<aon-laboral></aon-laboral>')
				}
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

	getApp(app) {
		switch(app.toLowerCase()){
    	case Apps.DOCUMENTAL.app:
				return Apps.DOCUMENTAL;
    	case Apps.ACCOUNTING.app:
				return Apps.ACCOUNTING;
			case Apps.FISCAL.app:
				return Apps.FISCAL;
			case Apps.PAYROLL.app:
				return Apps.PAYROLL;
			case Apps.COMUNICA.app:
				return Apps.COMUNICA;
			case Apps.INVOICE.app:
				return Apps.INVOICE;
			case Apps.TIMECONTROL.app:
				return Apps.TIMECONTROL;
		}
	}

	build() {
		this.innerHTML = `
			<div id="aonMenuSidenav" class="aonMenuSidenav"></div>
		`;
		let rootPanel = document.getElementById('rootPanel');
		let aonMenuSidenav = document.getElementById('aonMenuSidenav');

		aonMenuSidenav.addEventListener('mouseover', () => {
			if(aonMenuSidenav.style.width !== '250px' && aonMenuSidenav.style.width !== '0px' && localStorage.getItem('aon_domain_id')){
				aonMenuSidenav.style.transitionDuration = '0ms';
				aonMenuSidenav.style.width = '150px';
				document.querySelectorAll("[id^='aonMenuListApp-']").forEach((item, i) => {
					item.style.display = 'inline-block';
					item.style.fontSize = '12px';
					item.style.fontFamily = 'Roboto,sans-serif';
					item.style.color = 'black';
				});
			}
		});

		aonMenuSidenav.addEventListener('mouseleave', () => {
			aonMenuSidenav.style.transitionDuration = '500ms';
			if(this.getAttribute('opened')) {
				aonMenuSidenav.style.width = '60px';
				rootPanel.style.marginRight = '60px';
			} else {
				aonMenuSidenav.style.width = '0px';
				rootPanel.style.marginRight = '0px';
			}
			document.querySelectorAll("[id^='aonMenuListApp-']").forEach((item, i) => {
				item.style.display = 'none';
			});
			this.buildMenu();
		});

		if(this.getAttribute('opened')) {
			aonMenuSidenav.style.width = '60px';
			rootPanel.style.marginRight = '60px';
		} else {
			aonMenuSidenav.style.width = '0px';
			rootPanel.style.marginRight = '0px';
		}
		this.buildMenu();
	}

	buildMenu() {
		let ul = document.createElement('ul');
		ul.id = 'aonMenuList';
		ul.style.margin = '0px';
		ul.style.padding = '0px';
		ul.style.listStyle = 'none';

		let li = document.createElement('li');
		li.style.textAlign =  'right';
		li.style.paddingRight = '12px';
		li.style.backgroundColor = 'transparent';
		const icon = this.getAttribute('opened') ? 'keyboard_arrow_right' : 'keyboard_arrow_left';
		li.innerHTML =`<aon-icon-button id="aonMenuShowButton" icon="${icon}"></aon-icon-button>`;
		ul.appendChild(li);

		if(localStorage.getItem('aon_domain_id') && localStorage.getItem('company')){
			let company = JSON.parse(localStorage.getItem('company'));
			getDomainApps(company.domain).then(apps => {
				getUserAppRole().then(roles => {
					this._roles = roles;
					for (let item in MenuApps){
						if(apps.includes(MenuApps[item]) && (roles.includes('ADMIN') || roles.includes(MenuApps[item].toUpperCase()))){
							ul.appendChild(this.buildApp(this.getApp(MenuApps[item])));
						}
					}

					let liAdd = document.createElement('li');
					liAdd.style.textAlign =  'right';
					liAdd.style.paddingRight = '10px';
					liAdd.style.backgroundColor = 'transparent';
					liAdd.innerHTML =`<aon-icon-button id="aonMenuAddButton" icon="add"></aon-icon-button>`;
					ul.appendChild(liAdd);

					aonMenuSidenav.innerHTML = '';
					aonMenuSidenav.appendChild(ul);

					let aonMenuShowButton = document.getElementById('aonMenuShowButton');
					aonMenuShowButton.addEventListener('click', () => {

						if(this.getAttribute('opened'))
							this.removeAttribute('opened');
						else this.setAttribute('opened', true);

						let toolSection = document.querySelector("[id*='aonToolbarToolSection']");
						if(toolSection) {
							toolSection.style.paddingRight = this.getAttribute('opened') ? '0px' : '40px';
						}
						const icon = this.getAttribute('opened') ? 'keyboard_arrow_right' : 'keyboard_arrow_left';
						aonMenuShowButton.setAttribute('icon', icon);
					});

					let aonMenuAddButton = document.getElementById('aonMenuAddButton');
					aonMenuAddButton.addEventListener('click', () => {
						rootPanel('<aon-marketplace id="aonMarketplace" > </aon-marketplace>');
						if(this.getAttribute('company')){
							let aonMarketplace = document.getElementById('aonMarketplace');
							aonMarketplace.setAttribute('company', this.getAttribute('company'));
					 }
					});
				});
			});
		}

	}

	buildApp(app) {
		let li = document.createElement('li');
		if (app) {
			li.id = 'aonMenuList' + app.app;
	    li.style.backgroundColor = 'transparent';
	    li.addEventListener('mouseover', () => {
	      let img = document.getElementById('aonMenuListAppImg-' + app.app);
	      img.size = '40px';
	    });

	    li.addEventListener('mouseleave', () => {
	      let img = document.getElementById('aonMenuListAppImg-' + app.app);
	      img.size = '30px';
	    });

	    let a = document.createElement('a');
	    a.style.cursor = 'pointer';
	    a.style.textAlign = 'right';
	    a.addEventListener('click', () => {
	      this.appSelection(app.app);
	    });

	    let div = document.createElement('div');
	    div.style.padding = '8px 0px';

	    if (app.icon) {
	      div.innerHTML = `
				<span id="aonMenuListApp-${app.app}" style="display:none;"> ${app.title} </span>
				<aon-icon id="aonMenuListAppImg-${app.app}" icon="${app.icon}" color="${app.color}" size="30px" style="margin-right: 15px;"></aon-icon>
			`;

	    } else {
	      let span = document.createElement('span');
	      span.id = 'aonMenuListApp-' + app.app;
	      span.style.display = 'none';
	      span.style.marginRight = '5px';
	      span.innerHTML = app.title;
	      div.appendChild(span);

	      let img = document.createElement('img');
	      img.id = 'aonMenuListAppImg-' + app.app;
	      img.style.width = '30px';
	      img.style.marginRight = '15px';
	      img.src = app.logo;
	      img.title = app.title;
	      div.appendChild(img);
	    }
	    let div2 = document.createElement('div');

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

		let aonMenuSidenav = document.getElementById('aonMenuSidenav');
		aonMenuSidenav.style.width = '250';

		let div = document.createElement('div');
	 	div.className = 'aonMenuSidenavAppToolbar';
		div.style.backgroundColor = this.getAppToolbarBackgroundColor(app);

		let span = document.createElement('span');
		span.className= 'aonTitle';
		span.innerHTML = app.title;

		let span2 = document.createElement('span');
		span2.className = 'aonRight40';
		span2.innerHTML = '<aon-icon-button id="aon-menu-sidenav-app-launch-button" icon="launch" color="white" noHover="true"></aon-icon-button>';

		let span3 = document.createElement('span');
		span3.className = 'aonRight0';
		span3.innerHTML = '<aon-icon-button id="aon-menu-sidenav-app-close-button" icon="close" color="white" noHover="true"></aon-icon-button>';
		div.appendChild(span);
		div.appendChild(span2);
		div.appendChild(span3);

		let div2 = document.createElement('div');
		let ul = document.createElement('ul');
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

		let launchButton = document.getElementById('aon-menu-sidenav-app-launch-button');
		launchButton.addEventListener('click', () => {
			if(Apps.ACCOUNTING.app === app.app) {
				rootPanel('<aon-contable></aon-contable>');
			} else if(Apps.FISCAL.app === app.app) {
				rootPanel('<aon-fiscal></aon-fiscal>');
			} else if(Apps.PAYROLL.app === app.app) {
				rootPanel('<aon-laboral></aon-laboral>');
			}
		});
		let closeButton = document.getElementById('aon-menu-sidenav-app-close-button');
		closeButton.addEventListener('click', () => {
			aonMenuSidenav.style.width = '150px';
			this.buildMenu();
		});
	}

	getSubApps(app){
		switch(app){
			case Apps.ACCOUNTING.app:
			 	return AccountingMenu;
			case Apps.FISCAL.app:
				return AeatFiscalMenu;
			case Apps.PAYROLL.app:
				return PayrollMenu;
			case Apps.TOOLS.app:
				return ToolsMenu;
			case Apps.PORTAL.app:
				return PortalMenu;
		}
	}

	buildSubAppMenu(subapp) {
		let li = document.createElement('li');
		li.className = 'aonMenuSidenavSubAppListItem';
		li.title = subapp.title;
		li.addEventListener('mouseover', () => {
 			li.style.backgroundColor = '#f1f1f1';
		});
		li.addEventListener('mouseleave', () => {
			li.style.backgroundColor = 'transparent';
		});

		let a = document.createElement('a');
		a.className = 'aonMenuLink';

		let span = document.createElement('span');
		span.style.fontSize = "12px";
		span.style.fontWeight = "400";
		span.innerHTML = subapp.title;
		a.appendChild(span);
		li.appendChild(a);
		li.addEventListener('click', () => {
			if(subapp.content) {
				rootPanel(subapp.content);
			} else if(subapp.initAction) {
				open('https://' + localStorage.getItem('aon_domain_name') + '/login?initAction='+subapp.initAction+'&token=' + localStorage.getItem('aon_session_id'));
			} else startModule(subapp.module, subapp.entryPoint);

			let aonMenuSidenav = document.getElementById('aonMenuSidenav');
			let rp = document.getElementById('rootPanel');
			if(this.getAttribute('opened')) {
				aonMenuSidenav.style.width = '60px';
				rp.style.marginRight = '60px';
			} else {
				aonMenuSidenav.style.width = '0px';
				rp.style.marginRight = '0px';
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
		switch(app.app){
			case Apps.ACCOUNTING.app:
				return Apps.ACCOUNTING.color;
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
				return Apps.PAYROLL.color;
			case Apps.TOOLS.app:
				return Apps.TOOLS.color;
			default: return app.color ? app.color : '#f1f1f1';
		}
	}

	addApp(app) {
		let ul = document.getElementById('aonMenuList');
		ul.appendChild(this.buildApp(app));
	}

	removeApp(app) {
		let ul = document.getElementById('aonMenuList');
		let li = document.getElementById('aonMenuList' + app.app);
		ul.removeChild(li);
	}

	close() {
		let aonMenuSidenav = document.getElementById('aonMenuSidenav');
		let rootPanel = document.getElementById('rootPanel');
		aonMenuSidenav.style.width = '0px';
		rootPanel.style.marginRight = '0px';
		this.removeAttribute('opened');
	}

}

window.customElements.define('aon-menu', AonMenu);
