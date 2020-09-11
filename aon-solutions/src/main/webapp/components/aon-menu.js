import {startModule, rootPanel} from '../services/gwtLoader.js';
import {Apps, AccountingMenu, PayrollMenu, AeatFiscalMenu, ArabaFiscalMenu,
	 GipuzkoaFiscalMenu, BizkaiaFiscalMenu, NavarraFiscalMenu, ToolsMenu} from  '../services/app.js';
import './aon-icon.js';

const ID = 'id';
const OPENED = 'opened';
const APP = 'app';

class AonMenu extends HTMLElement {

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

	connectedCallback () {
		this.innerHTML = `

			<div id="aonMenuSidenav" class="aonMenuSidenav">

			</div>
			`;
		this.buildMenu();
  }

	toogle() {
		let aonMenuSidenav = document.getElementById('aonMenuSidenav');
		let rootPanel = document.getElementById('rootPanel');
		if(this.getAttribute('opened')) {
			aonMenuSidenav.style.width = '0px';
			rootPanel.style.marginRight = '0px';
			this.removeAttribute('opened')
		} else if(this.getAttribute('app')){
			aonMenuSidenav.style.width = '250px';
			rootPanel.style.marginRight = '250px';
			this.setAttribute('opened', true);
		} else {
			aonMenuSidenav.style.width = '100px';
			rootPanel.style.marginRight = '100px';
			this.setAttribute('opened', true);
		}
	}

	appSelection(app) {
		switch(app){
    	case Apps.INVOICE.app:
				rootPanel('<aon-invoice-panel></aon-invoice-panel>');
				break;
    	case Apps.DOCUMENTAL.app:
				startModule('aon_gwt_aio', 'documents');
				break;
			case Apps.HELPDESK.app:
				startModule('aon_gwt_aio', 'issues');
				break;
    	case Apps.ACCOUNTING.app:
				this.buildAppMenu(Apps.ACCOUNTING);
				break;
			case Apps.FISCAL.app:
				this.buildAppMenu(Apps.FISCAL);
				break;
			case Apps.PAYROLL.app:
				this.buildAppMenu(Apps.PAYROLL);
				break;
			case Apps.OCR.app:
				alert('OCR');
				break;
			case Apps.AIO.app:
	      open('https://' + localStorage.getItem('aon_domain_name') + '/login?token=' + localStorage.getItem('aon_session_id'));
				break;
			case Apps.TOOLS.app:
				this.buildAppMenu(Apps.TOOLS);
				break;
			case Apps.SELFCONTA.app:
				alert('SELFCONTA');
				break;
			case Apps.SALTRA.app:
				alert('SALTRA');
				break;
			case Apps.BIDOQ.app:
				open('https://mispapeles.es/');
				break;
			case Apps.ALMA.app:
				alert('ALMA');
				break;
			case Apps.LEARNING.app:
				alert('LEARNING');
				break;
		}
	}

	getApp(app) {
		switch(app.toLowerCase()){
    	case Apps.INVOICE.app:
				return Apps.INVOICE;
    	case Apps.DOCUMENTAL.app:
				return Apps.DOCUMENTAL;
			case Apps.HELPDESK.app:
				return Apps.HELPDESK;
    	case Apps.ACCOUNTING.app:
				return Apps.ACCOUNTING;
			case Apps.FISCAL.app:
				return Apps.FISCAL;
			case Apps.PAYROLL.app:
				return Apps.PAYROLL;
			case Apps.OCR.app:
				return Apps.OCR;
			case Apps.AIO.app:
				return Apps.AIO;
			case Apps.TOOLS.app:
				return Apps.TOOLS;
			case Apps.SELFCONTA.app:
				return Apps.SELFCONTA;
			case Apps.SALTRA.app:
				return Apps.SALTRA;
			case Apps.BIDOQ.app:
				return Apps.BIDOQ;
			case Apps.ALMA.app:
				return Apps.ALMA;
			case Apps.LEARNING.app:
				return Apps.LEARNING;
		}
	}

	buildMenu() {
		let company;
		if(this.getAttribute('company')){
			company = JSON.parse(this.getAttribute('company'));
		}
		let rootPanel = document.getElementById('rootPanel');
		let aonMenuSidenav = document.getElementById('aonMenuSidenav');

		if(this.getAttribute('opened')) {
			aonMenuSidenav.style.width = '100px';
			rootPanel.style.marginRight = '100px';
		} else {
			aonMenuSidenav.style.width = '0px';
			rootPanel.style.marginRight = '0px';
		}

		let ul = document.createElement('ul');
		ul.id = 'aonMenuList';
		ul.style.margin = '0px';
		ul.style.padding = '0px';
		ul.style.listStyle = 'none';
		if(localStorage.getItem('aon_domain_id') && this.getAttribute('user')){
			let user = JSON.parse(this.getAttribute('user'));
			for (let key in user.apps){
				ul.appendChild(this.buildApp(this.getApp(key)));
			}
		}
		aonMenuSidenav.innerHTML = '';
		aonMenuSidenav.appendChild(ul);
	}

	buildApp(app) {
		let li = document.createElement('li');
		li.id = 'aonMenuList' + app.app;
		li.style.height = '80px';
		li.style.backgroundColor = 'transparent';
		li.addEventListener('mouseover', () => {
 			li.style.backgroundColor = '#f1f1f1';
		});
		li.addEventListener('mouseleave', () => {
			li.style.backgroundColor = 'transparent';
		});

		let a = document.createElement('a');
		a.style.width = '80px';
		a.style.cursor = 'pointer';
		a.style.textAlign = 'center';

		a.addEventListener('click', () => {
			this.appSelection(app.app);
		});

		let div = document.createElement('div');
		div.style.padding = '8px 0px';
		if(app.icon) {
			div.innerHTML = `<aon-icon icon="${app.icon}" color="${app.color}" size="40px"></aon-icon>`;
		} else {
			let img = document.createElement('img');
			img.style.width = '40px';
			img.src = app.logo;
			img.title = app.title;
			div.appendChild(img);
		}
		let div2 = document.createElement('div');

		let span = document.createElement('span');
		span.style.fontSize = '12px';
		span.style.fontFamily = 'Roboto,sans-serif';
		span.style.color = 'black';
		span.innerHTML = app.title;
		div2.appendChild(span);
		div.appendChild(div2);
		a.appendChild(div);
		li.appendChild(a);

		return li;
	}

	buildAppMenu(app) {
		let company;
		if(this.getAttribute('company')){
			company = JSON.parse(this.getAttribute('company'));
		}

		let rootPanel = document.getElementById('rootPanel');
		let aonMenuSidenav = document.getElementById('aonMenuSidenav');

		aonMenuSidenav.style.width = '250px';
		rootPanel.style.marginRight = '250px';

		let div = document.createElement('div');
	 	div.className = 'aonMenuSidenavAppToolbar';
		div.style.backgroundColor = this.getAppToolbarBackgroundColor(app.app);

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
		let closeButton = document.getElementById('aon-menu-sidenav-app-close-button');
		closeButton.addEventListener('click', () => {
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
			if(subapp.initAction) {
				open('https://' + localStorage.getItem('aon_domain_name') + '/login?initAction='+subapp.initAction+'&token=' + localStorage.getItem('aon_session_id'));
			} else startModule(subapp.module, subapp.entryPoint);
		});
		return li
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

	addApp(app) {
		let ul = document.getElementById('aonMenuList');
		ul.appendChild(this.buildApp(app));
	}

	removeApp(app) {
		let ul = document.getElementById('aonMenuList');
		let li = document.getElementById('aonMenuList' + app.app);
		ul.removeChild(li);
	}

}

window.customElements.define('aon-menu', AonMenu);
