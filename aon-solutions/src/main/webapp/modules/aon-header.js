import {AonElement} from '../components/AonElement.js';
import {closeSession, getTimeControl, saveTimeControl, clearDurum} from  '../services/service.js';
import {getPosition} from '../services/maps.js';
import {rootPanel} from '../services/gwtLoader.js';

import '../components/aon-dialog-menu.js';
import '../components/aon-icon-button.js';
import '../components/aon-search-box.js';
import './configuration/aon-configuration.js';
import './invoice/aon-invoice-panel.js';
import './invoice/aon-invoice.js';
import './company/aon-desktop.js';
import './company/aon-mobile-desktop.js';
import './company/aon-parent.js';
import './user/aon-user.js';
import './messenger/aon-messenger.js';
import './notification/aon-notification-icon.js';
import { MATERIAL_ICONS, MSG } from '../environments/environments.js';
import { AonApiDoc } from './dev/aon-api-doc.js';

export class AonHeader extends AonElement {

	BASE_ID;
	activeTimecontrol;

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

	constructor () {
		super();
	}

	connectedCallback () {
		this.initialize();
		this.activeTimecontrol = false;
		this.build();
  }

	initialize() {
		this.BASE_ID = 'aonHeader';
		this.AON_HEADER_WEB = this.BASE_ID + 'Web';
		this.AON_LOGO = 'aonLogo';
		this.AON_HEADER_SEARCH = this.BASE_ID + 'Search';
		this.AON_HEADER_SEARCH_BOX = this.AON_HEADER_SEARCH + 'Box';
		this.AON_HEADER_USER = this.BASE_ID + 'User';
		this.AON_HEADER_USER_BUTTON = this.AON_HEADER_USER + 'Button';
		this.AON_HEADER_NOTIFICATION = this.BASE_ID + 'Notification';
		this.AON_HEADER_HELP = this.BASE_ID + 'Help';
		this.AON_HEADER_HELP_BUTTON = this.AON_HEADER_HELP + 'Button';
		this.AON_HEADER_COMPANY_LIST = this.BASE_ID + 'CompanyList';
		this.AON_HEADER_COMPANY_LIST_BUTTON = this.AON_HEADER_COMPANY_LIST + 'Button';
		this.AON_HEADER_HOME = this.BASE_ID + 'Home';
		this.AON_HEADER_HOME_BUTTON = this.AON_HEADER_HOME + 'Button';
		this.AON_HEADER_COMPANY = this.BASE_ID + 'Company';
		this.AON_HEADER_COMPANY_NAME = this.AON_HEADER_COMPANY + 'Name';
		this.AON_HEADER_DIALOG_HELP_OPTION = this.BASE_ID + 'DialogHelpOption';
		this.AON_HEADER_DIALOG_USER_OPTION = this.BASE_ID + 'DialogUserOption';
	}

	build() {
		this.innerHTML = /*html*/`
			<div id="aonHeaderWeb" class="aonHeader" >
				<span>
					<img id="aonLogo" class="aonLogo" width="230px" />
				</span>

				<span id="aonHeaderSearch" class="aonLeft250 aonHeaderButton" >
					<aon-search-box id="aonHeaderSearchBox"></aon-search-box>
				</span>

				<span id="aonHeaderUser" class="aonRight20 aonHeaderButton">
					<aon-icon-button id="aonHeaderUserButton" icon="account_circle"></aon-icon-button>
				</span>

				<span id="aonHeaderNotiication" class="aonRight60 aonHeaderButton">
					<aon-notification-icon></aon-notification-icon>
				</span>

				<span id="aonHeaderHelp" class="aonRight100 aonHeaderButton">
					<aon-icon-button id="aonHeaderHelpButton" icon="help_outline"></aon-icon-button>
				</span>

				<span id="aonHeaderCompanyList" class="aonRight140 aonHeaderButton" style="display:none;">
					<aon-icon-button id="aonHeaderCompanyListButton" icon="business"></aon-icon-button>
				</span>

				<span id="aonHeaderHome" class="aonRight180 aonHeaderButton" style="display:none;">
					<aon-icon-button id="aonHeaderHomeButton" icon="home" outlined="true"></aon-icon-button>
				</span>

				<span id="aonHeaderCompany" class="aonHeaderButton" style="display:none;top:25px;right: 220px;">
					<span id="aonHeaderCompanyName"> </span>
				</span>
			</div>
			<aon-dialog-menu id="aonHeaderDialogHelpOption" > </aon-dialog-menu>
			<aon-dialog-menu id="aonHeaderDialogUserOption" > </aon-dialog-menu>
			`;

		let aonHeaderWeb = document.getElementById('aonHeaderWeb');

		this.buildLogo();

		if(!this.isMobile()) {
			let aonHeaderHomeButton = document.getElementById(this.BASE_ID + 'HomeButton');
			aonHeaderHomeButton.addEventListener('click', () => {
				rootPanel(this.isMobile()
					? '<aon-mobile-desktop id="aonDesktop"></aon-mobile-desktop>'
					: '<aon-desktop id="aonDesktop"></aon-desktop>');
				let aonDesktop = document.getElementById('aonDesktop');
				aonDesktop.setAttribute('company', this.getAttribute('company'));
			});

			let aonHeaderHelpButton = document.getElementById(this.BASE_ID + 'HelpButton');
			aonHeaderHelpButton.addEventListener('click', () => {
				getDomainUserRoles({}).then(r => {
					this.dur = new DomainUserRoles(r);
					this.build();
				
					const top  = aonHeaderUserButton.getBoundingClientRect().top;
					const left = aonHeaderUserButton.getBoundingClientRect().left;
					let d = document.getElementById('aonHeaderDialogHelpOption');

					let options = [{
						name: 'Solicitudes',
						icon: 'assignment',
						fn: () =>rootPanel('<aon-messenger></aon-messenger>')
					}, {
						name: 'Ayuda',
						icon: 'help_outline',
						fn: () => rootPanel('<iframe height="100%" width="100%" src="https://faqs.aonsolutions.es/"></iframe>')
					}];
					if(this.dur.isDev()) {
						options.push({
							name: MSG.API_DOCUMENTATION,
							icon: MATERIAL_ICONS.API,
							fn: () => this.rootPanel(new AonApiDoc())
						});
					}
					d.setMenuOptions(options, top, left);
					d.open();
				});
			});
		}

		let aonHeaderCompanyListButton = document.getElementById(this.BASE_ID + 'CompanyListButton');
		aonHeaderCompanyListButton.addEventListener('click', () => {
			if(!this.isMobile()) {
				let aonHeaderSearch = document.getElementById(this.BASE_ID + 'Search');
				aonHeaderSearch.style.display = 'block';

				let aonHeaderHome = document.getElementById(this.BASE_ID + 'Home');
				aonHeaderHome.style.display = 'none';

				let aonHeaderCompany = document.getElementById(this.BASE_ID + 'Company');
				aonHeaderCompany.style.display = 'none';

				let aonShowMenu = document.getElementById('aonShowMenu');
				aonShowMenu.style.display = 'none';

				let aonMenu = document.getElementById('aonMenu');
				aonMenu.removeAttribute('company');
				aonMenu.removeAttribute('user');
				aonMenu.close();
			}

			let aonHeaderCompanyList = document.getElementById(this.BASE_ID + 'CompanyList');
			aonHeaderCompanyList.style.display = 'none';

			this.removeAttribute('company');
			this.removeAttribute('user');

			localStorage.removeItem('aon_domain_id');
			localStorage.removeItem('aon_domain_name');
			localStorage.removeItem('aon_domain_login');
			clearDurum();
			rootPanel('<aon-parent id="aonParent"></aon-parent>');
		});
		if(this.activeTimecontrol) {
			getTimeControl().then(r => {
				this.timeControlStatus(r);
			});;
		}
		let aonHeaderUserButton = document.getElementById('aonHeaderUserButton');
		aonHeaderUserButton.addEventListener('click', () => {
			if(this.activeTimecontrol) {
				getTimeControl().then(r => {
					this.timeControlStatus(r);
					const top  = aonHeaderUserButton.getBoundingClientRect().top;
					const left = aonHeaderUserButton.getBoundingClientRect().left;
					let d = document.getElementById('aonHeaderDialogUserOption');

					let fichajeText = r.status === 'in' ? 'Marcar Salida': 'Marcar Entrada';
					let signin = r.status === 'in' ? {status: 'out'} : {status: 'in'};
					let options = [{
							name: fichajeText,
							icon: 'alarm',
							fn: () => this.aonFichar(signin)
						}, {
							name: 'Configuración',
							icon: 'settings',
							fn: () => this.aonConfiguration()
						}, {
							name: 'Cerrar Sesión',
							icon: 'input',
							fn: () => {
								this.activeTimecontrol= false;
								closeSession();
							}
						}];
						d.setMenuOptions(options, top, left);
						d.open();
					});
				} else {
					const top  = aonHeaderUserButton.getBoundingClientRect().top;
					const left = aonHeaderUserButton.getBoundingClientRect().left;
					let d = document.getElementById('aonHeaderDialogUserOption');

					let options = [{
							name: 'Configuración',
							icon: 'settings',
							fn: () => this.aonConfiguration()
						}, {
							name: 'Cerrar Sesión',
							icon: 'input',
							fn: () => closeSession()
						}];
						d.setMenuOptions(options, top, left);
						d.open();
				}
		});
	}

	timeControlStatus(signin) {
		this.activeTimecontrol = true;
		let aonUserConnected = document.getElementById('aonHeaderUserConnected');
		if(!aonUserConnected) {
			aonUserConnected = document.createElement('div');
			aonUserConnected.id = this.BASE_ID + 'UserConnected';
			aonUserConnected.className = 'aonConnected';

			let aonHeaderUserButtonIconButton = document.getElementById('aonHeaderUserButtonIconButton');
			aonHeaderUserButtonIconButton.appendChild(aonUserConnected);
		}

		if(signin.status === 'in'){
			aonUserConnected.style.backgroundColor = '#86D364';
		} else if(signin.status === 'pause') {
			aonUserConnected.style.backgroundColor = '#F39F1D';
		} else {
			aonUserConnected.style.backgroundColor = '#DC4D30';
		}
	}

	aonFichar(signin) {
		getPosition().then(position => {
			if(position) {
				signin.coordinates = position.latitude + ',' + position.longitude;
			}
			saveTimeControl(signin).then(r => {
				let aonSign = this.getElement('aonSign');
				if(aonSign) {
					aonSign.buildSignin(r);
				}

				this.timeControlStatus(r);
			});
		});
	}

	buildLogo() {
		let aonLogo = document.getElementById('aonLogo');

		if(window.location.href.includes('ayudat')){
			aonLogo.src = '../assets/ayudat-logo2.png';
		} else if(window.location.href.includes('translogia') || window.location.href.includes('tedi')){
			aonLogo.src = '../assets/ayudat-logo3.png';
			// aonLogo.src = '../assets/tedi-logo.png';
			// aonLogo.style.top = '0px';
		} else aonLogo.src = '../assets/aon-logo2.png';
		aonLogo.addEventListener('click', () => {
			if(localStorage.getItem('aon_domain_id')){
				rootPanel(this.isMobile()
					? '<aon-mobile-desktop id="aonDesktop"></aon-mobile-desktop>'
					: '<aon-desktop id="aonDesktop"></aon-desktop>');
				let aonDesktop = document.getElementById('aonDesktop');
				aonDesktop.setAttribute('company', this.getAttribute('company'));
			} else {
				rootPanel('<aon-parent id="aonParent"></aon-parent>');
			}
		})

	}

	aonConfiguration() {
		rootPanel('<aon-configuration id="aon-configuration"></aon-configuration>');
		let aonConfiguration = document.getElementById('aon-configuration');
		if(this.getAttribute('company')){
			aonConfiguration.setAttribute('company', this.getAttribute('company'));
		}
		if(this.getAttribute('user')){
			aonConfiguration.setAttribute('user', this.getAttribute('user'));
		}
	}

	showCompanyOption(company) {
		let aonHeaderCompanyList = this.getElement(this.AON_HEADER_COMPANY_LIST);
		aonHeaderCompanyList.style.display = company ? 'block' : 'none';

		let aonHeaderHelp = this.getElement(this.AON_HEADER_HELP);
		aonHeaderHelp.style.display = company ? 'block' : 'none';

		let aonHeaderSearch = this.getElement(this.AON_HEADER_SEARCH);
		aonHeaderSearch.style.display = company ? 'none' : 'block';

		let aonHeaderHome = this.getElement(this.AON_HEADER_HOME);
		aonHeaderHome.style.display = company ? 'block' : 'none';

		let aonHeaderCompany = this.getElement(this.AON_HEADER_COMPANY);
		aonHeaderCompany.style.display = company ? 'block' : 'none';

		let aonHeaderCompanyName = this.getElement(this.AON_HEADER_COMPANY_NAME);
		aonHeaderCompanyName.innerHTML = company ? company.name : '';
	}
}

window.customElements.define('aon-header', AonHeader);
