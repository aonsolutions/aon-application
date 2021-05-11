import {AonElement} from '../components/AonElement.js';
import {closeSession, getTimeControl, saveTimeControl} from  '../services/service.js';
import {getPosition} from '../services/maps.js';
import {rootPanel} from '../services/gwtLoader.js';
import '../components/aon-icon-button.js';
import '../components/aon-dialog-menu.js';
import './configuration/aon-configuration.js';
import './company/aon-mobile-desktop.js';
import './notification/aon-notification-icon.js';
import { EVENT } from '../environments/environments.js';

export class AonMobileHeader extends AonElement {

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
		
		this.build();
  }

	initialize() {
		this.BASE_ID = 'aonHeader';
		this.activeTimecontrol = false;
	}

	build() {
		this.innerHTML = /*html*/`
			<div id="aonHeaderWeb" class="aonMobileHeader" >
				<span>
					<img id="aonMobileLogo" class="aonLogo" />
				</span>

				<span id="aonHeaderUser" class="aonRight20 aonMobileHeaderButton">
					<aon-icon-button id="aonHeaderUserButton" icon="account_circle"></aon-icon-button>
				</span>

				<span id="aonHeaderNotification" class="aonRight60 aonMobileHeaderButton">
					<aon-notification-icon></aon-notification-icon>
				</span>

				<span id="aonHeaderCompanyList" class="aonRight100 aonMobileHeaderButton" style="display=none">
					<aon-icon-button id="aonHeaderCompanyListButton" icon="business"></aon-icon-button>
				</span>
			</div>

			<aon-dialog-menu id="aonHeaderDialogUserOption" > </aon-dialog-menu>
			`;

		// let aonHeaderWeb = this.getElement('aonHeaderWeb');
		this.buildLogo();
		if(this.activeTimecontrol) {
			getTimeControl().then(r => {
				this.timeControlStatus(r);
			});
		}

		let aonHeaderCompanyListButton = this.getElement('aonHeaderCompanyListButton');
		aonHeaderCompanyListButton.addEventListener(EVENT.CLICK, () => {
			rootPanel('<aon-mobile-parent id="aonParent"></aon-mobile-parent>');
		});

		let aonHeaderUserButton = this.getElement('aonHeaderUserButton');
		aonHeaderUserButton.addEventListener('click', () => {
			if(this.activeTimecontrol) {
				getTimeControl().then(r => {
					this.timeControlStatus(r);
					const top  = aonHeaderUserButton.getBoundingClientRect().top;
					const left = aonHeaderUserButton.getBoundingClientRect().left;
					let d = this.getElement('aonHeaderDialogUserOption');
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
							this.activeTimecontrol = false;
							closeSession()
						}
					}];

					d.setMenuOptions(options, top, left);
					d.open();

					if(r && r.task_holder){
						d.setContentTitle(r.task_holder.name)
					}


				});
			} else {
				const top  = aonHeaderUserButton.getBoundingClientRect().top;
				const left = aonHeaderUserButton.getBoundingClientRect().left;
				let d = this.getElement('aonHeaderDialogUserOption');
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
		let aonUserConnected = this.getElement('aonHeaderUserConnected');
		if(!aonUserConnected) {
			aonUserConnected = document.createElement('div');
			aonUserConnected.id = this.BASE_ID + 'UserConnected';
			aonUserConnected.className = 'aonConnected';

			let aonHeaderUserButtonIconButton = this.getElement('aonHeaderUserButtonIconButton');
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
			if(position && position.latitude) {
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

	aonConfiguration() {
		rootPanel('<aon-configuration id="aon-configuration"></aon-configuration>');
		let aonConfiguration = this.getElement('aon-configuration');
		if(this.getAttribute('company')){
			aonConfiguration.setAttribute('company', this.getAttribute('company'));
		}
		if(this.getAttribute('user')){
			aonConfiguration.setAttribute('user', this.getAttribute('user'));
		}
	}

	buildLogo() {
		let aonLogo = this.getElement('aonMobileLogo');
		const href = window.location.href;       
		if(href.includes('ayudat')){
			aonLogo.src = 'assets/ayudat-logo2.png';
		} else if(href.includes('translogia') || href.includes('tedi')){
			aonLogo.src = 'assets/ayudat-logo3.png';
		} else {
			aonLogo.src = 'assets/aon-logo3.svg';
			aonLogo.style.top = "21px";
			aonLogo.style.width = "123px";
			aonLogo.style.marginLeft = "21px";
		}
		aonLogo.addEventListener('click', () => {
			rootPanel('<aon-mobile-desktop id="aonDesktop"></aon-mobile-desktop>');
		});
	}

	showCompanyOption(company) {

	}
}

window.customElements.define('aon-mobile-header', AonMobileHeader);
