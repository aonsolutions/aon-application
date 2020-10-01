import {closeSession} from  '../services/service.js';
import {isMobile} from  '../services/utils.js';
import {rootPanel} from '../services/gwtLoader.js';

import './aon-configuration.js';
import './invoice/aon-invoice-panel.js';
import './invoice/aon-invoice.js';
import './aon-user.js';
import './aon-icon-button.js';
import './aon-search-box.js';



class AonHeader extends HTMLElement {

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

				<span id="aonHeaderHelp" class="aonRight60 aonHeaderButton" style="display:none;">
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

				<span class="aonRight160">
					<ul class="mdl-menu mdl-js-menu mdl-js-ripple-effect" for="aonHeaderUserButton">
						<li id="aonHeaderFichar" class="mdl-menu__item">
							<i class="material-icons mdl-list__item-icon aonMenuIcon">alarm</i>
							<span id="aonHeaderFicharText"> Marcar Entrada </span>
						</li>
						<li id="aonHeaderConfiguration" class="mdl-menu__item">
							<i class="material-icons mdl-list__item-icon aonMenuIcon">settings</i>
							Configuración
						</li>
						<li id="aonHeaderCloseSession" class="mdl-menu__item">
							<i class="material-icons mdl-list__item-icon aonMenuIcon">input</i>
							Cerrar Sesión
						</li>
					</ul>
				</span>
			</div>
			`;

			this.build();
  }

	build() {
		const BASE_ID = 'aonHeader';
		let aonHeaderWeb = document.getElementById('aonHeaderWeb');

		document.getElementById(BASE_ID + 'CloseSession')
			.addEventListener('click', () => closeSession());

		this.buildLogo();
		if(!isMobile()) {
			let aonHeaderHomeButton = document.getElementById(BASE_ID + 'HomeButton');
			aonHeaderHomeButton.addEventListener('click', () => {
				rootPanel('<aon-desktop id="aonDesktop"></aon-desktop>');
				let aonDesktop = document.getElementById('aonDesktop');
				aonDesktop.setAttribute('company', this.getAttribute('company'));
			});

			let aonHeaderHelpButton = document.getElementById(BASE_ID + 'HelpButton');
			aonHeaderHelpButton.addEventListener('click', () => {
				rootPanel('<iframe height="100%" src="https://faqs.aonsolutions.es/"></iframe>');
			});
		}

		let aonHeaderCompanyListButton = document.getElementById(BASE_ID + 'CompanyListButton');
		aonHeaderCompanyListButton.addEventListener('click', () => {
			if(!isMobile()) {
				let aonHeaderSearch = document.getElementById(BASE_ID + 'Search');
				aonHeaderSearch.style.display = 'block';

				let aonHeaderHelp = document.getElementById(BASE_ID + 'Help');
				aonHeaderHelp.style.display = 'none';

				let aonHeaderHome = document.getElementById(BASE_ID + 'Home');
				aonHeaderHome.style.display = 'none';

				let aonHeaderCompany = document.getElementById(BASE_ID + 'Company');
				aonHeaderCompany.style.display = 'none';

				let aonShowMenu = document.getElementById('aonShowMenu');
				aonShowMenu.style.display = 'none';

				let aonMenu = document.getElementById('aonMenu');
				aonMenu.removeAttribute('company');
				aonMenu.removeAttribute('user');
				aonMenu.close();
			}

			let aonHeaderCompanyList = document.getElementById(BASE_ID + 'CompanyList');
			aonHeaderCompanyList.style.display = 'none';

			this.removeAttribute('company');
			this.removeAttribute('user');

			localStorage.removeItem('aon_domain_id');
			localStorage.removeItem('aon_domain_name');
			rootPanel('<aon-parent id="aonParent"></aon-parent>');
		});

		let aonHeaderConfiguration = document.getElementById(BASE_ID + 'Configuration');
		aonHeaderConfiguration.addEventListener('click', () => {
			this.aonConfiguration();
		});

		let aonUserConnected = document.createElement('div');
		aonUserConnected.id = BASE_ID + 'UserConnected';
		aonUserConnected.className = 'aonConnected';
		aonUserConnected.style.backgroundColor = 'red';

		let aonHeaderUserButtonIconButton = document.getElementById('aonHeaderUserButtonIconButton');
		aonHeaderUserButtonIconButton.appendChild(aonUserConnected);

		let aonHeaderFichar = document.getElementById(BASE_ID + 'Fichar');
		aonHeaderFichar.addEventListener('click', () => {
			let text = document.getElementById(BASE_ID + 'FicharText');
			if(aonUserConnected.style.backgroundColor === 'red'){
				aonUserConnected.style.backgroundColor = '#35ac19';
				text.innerHTML = 'Marcar Salida';
			} else {
				aonUserConnected.style.backgroundColor = 'red';
				text.innerHTML = 'Marcar Entrada';
			}

		});
	}

	buildLogo() {
		let aonLogo = document.getElementById('aonLogo');

		if(window.location.href.includes('ayudat')){
			aonLogo.src = '../assets/ayudat-logo2.png';
		} else if(window.location.href.includes('translogia') || window.location.href.includes('tedi')){
			aonLogo.src = '../assets/tedi-logo.png';
			aonLogo.style.top = '0px';
		} else aonLogo.src = '../assets/aon-logo2.png';
		aonLogo.addEventListener('click', () => {
			if(localStorage.getItem('aon_domain_id')){
				rootPanel('<aon-desktop id="aonDesktop"></aon-desktop>');
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
