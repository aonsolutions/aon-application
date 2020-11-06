import {AonElement} from '../components/AonElement.js';
import {closeSession} from  '../services/service.js';
import {rootPanel} from '../services/gwtLoader.js';

import '../components/aon-icon-button.js';


import './configuration/aon-configuration.js';
import './invoice/aon-invoice-panel.js';
import './invoice/aon-invoice.js';
import './company/aon-mobile-desktop.js';
import './user/aon-user.js';

export class AonMobileHeader extends AonElement {

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
					<img id="aonMobileLogo" class="aonLogo" width="230px" />
				</span>
				<span id="aonHeaderUser" class="aonRight20 aonHeaderButton">
					<aon-icon-button id="aonHeaderUserButton" icon="account_circle"></aon-icon-button>
				</span>
			</div>

			<aon-dialog id="aonHeaderDialogUserOption" type="menu" > </aon-dialog>
			`;

			this.build();
  }

	build() {
		const BASE_ID = 'aonHeader';
		let aonHeaderWeb = document.getElementById('aonHeaderWeb');
		this.buildLogo();

		let aonUserConnected = document.createElement('div');
		aonUserConnected.id = BASE_ID + 'UserConnected';
		aonUserConnected.className = 'aonConnected';
		aonUserConnected.style.backgroundColor = 'red';

		let aonHeaderUserButtonIconButton = document.getElementById('aonHeaderUserButtonIconButton');
		aonHeaderUserButtonIconButton.appendChild(aonUserConnected);

		let aonHeaderUserButton = document.getElementById('aonHeaderUserButton');
		aonHeaderUserButton.addEventListener('click', () => {
			const top  = aonHeaderUserButton.getBoundingClientRect().top;
			const left = aonHeaderUserButton.getBoundingClientRect().left;
			let d = document.getElementById('aonHeaderDialogUserOption');

			let fichajeText = aonUserConnected.style.backgroundColor === 'red'
				? 'Marcar Entrada': 'Marcar Salida';
			let options = [{
					name: fichajeText,
					icon: 'alarm',
					fn: () => this.aonFichar()
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
	}

	aonFichar() {
		let aonUserConnected = document.getElementById('aonHeaderUserConnected');
		aonUserConnected.style.backgroundColor =
			aonUserConnected.style.backgroundColor === 'red' ? '#35ac19' : 'red';
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

	buildLogo() {
		let aonLogo = document.getElementById('aonMobileLogo');

		if(window.location.href.includes('ayudat')){
			aonLogo.src = '../assets/ayudat-logo2.png';
		} else if(window.location.href.includes('translogia') || window.location.href.includes('tedi')){
			aonLogo.src = '../assets/tedi-logo.png';
			aonLogo.style.top = '0px';
		} else aonLogo.src = '../assets/aon-logo2.png';
		aonLogo.addEventListener('click', () => {
			rootPanel('<aon-mobile-desktop id="aonDesktop"></aon-mobile-desktop>');
		});
	}
}

window.customElements.define('aon-mobile-header', AonMobileHeader);
