import {isMobile} from  '../services/utils.js';
import {Apps, AccountingMenu, PayrollMenu, AeatFiscalMenu, ArabaFiscalMenu,
	 GipuzkoaFiscalMenu, BizkaiaFiscalMenu, NavarraFiscalMenu, ToolsMenu} from  '../services/app.js';
import {getDomainApps, setDomainApp} from  '../services/service.js';
import {startModule, rootPanel} from '../services/gwtLoader.js';
import './aon-icon.js';
import './aon-marketplace.js';

class AonDesktop extends HTMLElement {

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
					if(isMobile()) {
						this.buildMobile(r);
					} else this.build(r);
					componentHandler.upgradeAllRegistered();
				});
			}
		}
	}

	constructor () {
		super();
	}

	connectedCallback () {
		let company = this.getAttribute('company') ? JSON.parse(this.getAttribute('company')) : undefined;
		if(company) {
			getDomainApps(company.domain).then(r => {
				if(isMobile()){
					this.buildMobile(r);
				} else this.build(r);
				componentHandler.upgradeAllRegistered();
			});
		}
  }

	buildMobile(r) {
		let ul = document.createElement('ul');
		ul.style.margin = '0px';
		ul.style.padding = '0px';
		ul.style.listStyle = 'none';
		for (let key in Apps){
			if(r[Apps[key].app] ? r[Apps[key].app] : false) {
				ul.appendChild(this.buildApp(Apps[key]));
			}
		}
		this.appendChild(ul);
	}

	buildApp(app) {
		let li = document.createElement('li');
		li.style.display = 'inline-block';
		li.id = 'aonMenuList' + app.app;
		li.style.height = '80px';
		li.style.width = (window.innerWidth / 3) + 'px';
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

		this.innerHTML = '';
		this.appendChild(div);
		this.appendChild(div2);

		let launchButton = document.getElementById('aon-menu-sidenav-app-launch-button');
		let closeButton = document.getElementById('aon-menu-sidenav-app-close-button');
		closeButton.addEventListener('click', () => {
			let company = this.getAttribute('company') ? JSON.parse(this.getAttribute('company')) : undefined;
			if(company) {
				getDomainApps(company.domain).then(r => {
					this.innerHTML = '';
					this.buildMobile(r);
					componentHandler.upgradeAllRegistered();
				});
			}
		});
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
			case Apps.CONTRATA.app:
				alert('CONTRAT@');
				break;
			case Apps.PORTAL.app:
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


	build(r) {
		let div = document.createElement('div');
		div.style.marginLeft = '100px';
		div.style.marginRight = '100px';

		let banner = document.createElement('div');
		banner.style.border = '1px solid #ddd';
		banner.style.marginTop = '20px';
		banner.style.height = '100px';
		div.appendChild(banner);

		div.appendChild(this.buildTitle('CONTRATADOS'));

		let ul = document.createElement('ul');
		ul.className = 'list-group';

		for (let key in Apps){
			if(r[Apps[key].app] ? r[Apps[key].app] : false) {
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
		div.appendChild(this.buildCards(r));

		div.appendChild(this.buildTitle('OTROS SERVICIOS'));
		// TODO

		this.appendChild(div);
	}

	buildTitle(title) {
		let div = document.createElement('div');
		div.style.color = 'gray';
		div.style.paddingTop = '20px';
		div.style.paddingBottom = '20px';
		div.innerHTML = title;
		return div;
	}

	buildCards(r) {
		let ul = document.createElement('ul');
		ul.style.margin = '0px';
		ul.style.padding = '0px';
		for (let key in Apps){
			if(!r[Apps[key].app]) {
				let li = document.createElement('li');
				li.style.display = 'inline-block';
				li.style.marginRight = '20px';
				li.style.marginBottom = '20px';
				let div = document.createElement('div');
				div.style.margin = '0px';
				div.className = 'demo-card-wide mdl-card mdl-shadow--2dp';

				let span = document.createElement('span');
				span.style.margin = '20px';

				span.innerHTML = `<aon-icon icon="aon_app" color="lightgray" size="60px"></aon-icon>`;


				let span2 = document.createElement('span');
				span2.className = 'aonMarketplaceTitle';
				span2.innerHTML = Apps[key].title;

				span.appendChild(span2);

				let div2 = document.createElement('div');
				let span3 = document.createElement('span');
				span3.style.padding = '25px';
				span3.style.color = '#7E7E7E';
				span3.innerHTML = Apps[key].description;
				div2.appendChild(span3);

				let buttons = document.createElement('span');
				buttons.style.position = 'absolute';
				buttons.style.bottom = '10px';
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

				let contratar = document.createElement('button');
				contratar.type = 'button';
				contratar.className = 'btn btn-outline-dark';
				contratar.style.width = '100px';
				contratar.style.borderRadius = '25px';
				contratar.innerHTML = 'Contratar';
				contratar.addEventListener('click', () => {
				 	rootPanel('<aon-marketplace id="aonMarketplace" > </aon-marketplace>');
					if(this.getAttribute('company')){
						let aonMarketplace = document.getElementById('aonMarketplace');
						aonMarketplace.setAttribute('company', this.getAttribute('company'));
					}
				});
				buttons.appendChild(contratar);

				div.appendChild(span);
				div.appendChild(div2);
				div.appendChild(buttons);


				li.appendChild(div);
				ul.appendChild(li);
			}
		}
		return ul;
	}
}

window.customElements.define('aon-desktop', AonDesktop);
