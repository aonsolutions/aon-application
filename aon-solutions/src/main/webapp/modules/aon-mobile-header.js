import {AonElement} from '../components/AonElement.js';
import {closeSession, getSigninStatus, updateSigninStatus} from  '../services/service.js';
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

		getSigninStatus().then(r => {
			let aonUserConnected = document.createElement('div');
			aonUserConnected.id = BASE_ID + 'UserConnected';
			aonUserConnected.className = 'aonConnected';
			if(r.status === 'in'){
				aonUserConnected.style.backgroundColor = '#86D364';
			} else if(r.status === 'pause') {
				aonUserConnected.style.backgroundColor = '#F39F1D';
			} else {
				aonUserConnected.style.backgroundColor = '#DC4D30';
			}
			let aonHeaderUserButtonIconButton = document.getElementById('aonHeaderUserButtonIconButton');
			aonHeaderUserButtonIconButton.appendChild(aonUserConnected);
		});

		let aonHeaderUserButton = document.getElementById('aonHeaderUserButton');
		aonHeaderUserButton.addEventListener('click', () => {
			getSigninStatus().then(r => {
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
		});
	}

	aonFichar(signin) {
		updateSigninStatus(signin);

		let aonSign = this.getElement('aonSign');
		if(aonSign) {
			aonSign.buildSignin();
		}

		let aonUserConnected = document.getElementById('aonHeaderUserConnected');
		aonUserConnected.style.backgroundColor = signin === 'in' ? '#86D364' : '#DC4D30';
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
			aonLogo.src = '../assets/ayudat-logo3.png';
//			aonLogo.style.top = '0px';
		} else aonLogo.src = '../assets/aon-logo2.png';
		aonLogo.addEventListener('click', () => {
			rootPanel('<aon-mobile-desktop id="aonDesktop"></aon-mobile-desktop>');
		});
	}
}

window.customElements.define('aon-mobile-header', AonMobileHeader);
