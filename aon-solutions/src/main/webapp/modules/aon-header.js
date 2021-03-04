import {AonElement} from '../components/AonElement.js';
import {closeSession, getTimeControl, saveTimeControl, getToken} from  '../services/service.js';
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
		if(getToken()){
			getTimeControl().then(r => {
				this.activeTimecontrol = true;
				this.timeControlStatus(r);
			}).catch(e => {
				this.activeTimecontrol = false;
			});
			this.build();
		}
  }

	initialize() {
		this.BASE_ID = 'aonHeader';
	}

	build() {
		this.innerHTML = `
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

				<span id="aonHeaderHelp" class="aonRight60 aonHeaderButton">
					<aon-icon-button id="aonHeaderHelpButton" icon="help_outline"></aon-icon-button>
				</span>

				<span id="aonHeaderCompanyList" class="aonRight100 aonHeaderButton" style="display:none;">
					<aon-icon-button id="aonHeaderCompanyListButton" icon="business"></aon-icon-button>
				</span>

				<span id="aonHeaderHome" class="aonRight140 aonHeaderButton" style="display:none;">
					<aon-icon-button id="aonHeaderHomeButton" icon="home" outlined="true"></aon-icon-button>
				</span>

				<span id="aonHeaderCompany" class="aonRight180 aonHeaderButton" style="display:none;top:25px;">
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
				d.setMenuOptions(options, top, left);
				d.open();
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
							fn: () => closeSession()
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
}

window.customElements.define('aon-header', AonHeader);
