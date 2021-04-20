import {AonElement} from '../components/AonElement.js';
import {rootPanel} from '../services/gwtLoader.js';
import {setPosition} from '../services/maps.js';
import { getToken, saveAuthDevice , getCompanies, getUser, getUserAppRole } from '../services/service.js';
import './login/aon-login.js';
import './register/aon-register.js';
import './aon-home.js';
import './company/aon-parent.js';
import './company/aon-mobile-parent.js';
import './company/aon-mobile-desktop.js';
import './company/aon-desktop.js';

import * as EVENT from "../../environments/aonEvent.js";
import * as AON_TAG from "../../environments/aonTag.js";

export class AonModule extends AonElement {

	AON_LOGIN;
	AON_REGISTER;
	AON_HOME_DIV;
	AON_HOME;
	AON_DESKTOP;
	AON_PARENT;

	constructor () {
		super();
	}

	connectedCallback () {
		this.initialize();
		this.setWindowApp();

		let loginDiv= this.createElement('div');
		loginDiv.id = this.AON_LOGIN;
		loginDiv.style.display = 'none';
		this.appendChild(loginDiv);
		loginDiv.innerHTML = '<aon-login></aon-login>';

		let homeDiv = this.createElement('div');
		homeDiv.id = this.AON_HOME_DIV;
		homeDiv.style.display = 'none';
		homeDiv.innerHTML = `<aon-home id="${this.AON_HOME}"></aon-home>`;
		this.appendChild(homeDiv);

		let registerDiv= this.createElement('div');
		registerDiv.id = this.AON_REGISTER;
		registerDiv.style.display = 'none';
		this.appendChild(registerDiv);
		registerDiv.innerHTML = '<aon-register></aon-register>';
		this.load();
	}

	initialize(){
		this.AON_LOGIN = 'aonLogin';
		this.AON_REGISTER = 'aonRegister';
		this.AON_HOME_DIV = 'aonHomeDiv';
		this.AON_HOME = 'aonHome';
		this.AON_DESKTOP = 'aonDesktop';
		this.AON_PARENT = 'aonParent';
	}

	load() {
		if(getToken()){
			localStorage.removeItem('aon_domain_id');
			localStorage.removeItem('aon_domain_name');
			localStorage.removeItem('aon_domain_login');

			this.clearElementById('rootPanel');
			this.getElement(this.AON_LOGIN).style.display = 'none';
			let homeDiv = this.getElement(this.AON_HOME_DIV);
			homeDiv.style.display = 'block';
			getCompanies().then(companies => {
				if(companies.length === 1){
					this.companySelection(companies[0]);
				} else {
					this.getElement(this.AON_HOME).showMenu(false);
					rootPanel(this.isMobile()
					 	? '<aon-mobile-parent id="aonParent"></aon-mobile-parent>'
					 	: '<aon-parent id="aonParent"></aon-parent>');
				}
			});
			window.dispatchEvent( new Event('userAuth') );
		} else {
			this.getElement(this.AON_LOGIN).style.display = 'block';
			this.getElement(this.AON_HOME).style.display = 'none';
		}
	}

	setWindowApp(){
		window.setPosition = (pos) => setPosition(pos);

		window.setTokenFCM =  (token) => {
			window.tokenFCM = token;
			this.saveTokenFcm(token);
		}

		window.setNotificationAction = (data) =>  {
			window.dispatchEvent( new CustomEvent(EVENT.RECEIVED_NOTIFICATION, {detail:data}));
		}
	}

	saveTokenFcm(tokenFCM){
		console.log("TOKEN FCM", tokenFCM);
		saveAuthDevice({tokenFCM});
	}

	companySelection(company) {
		localStorage.setItem('company', JSON.stringify(company));
		localStorage.setItem("aon_domain_id", company.id);
		localStorage.setItem("aon_domain_name", company.domain);

		let home = this.getElement(this.AON_HOME);
		home.showMenu(true);

		let aonHeader = this.getElement(home.AON_HEADER);
		aonHeader.showCompanyOption(company);

		if(!this.isMobile()){
			let aonMenu = this.getElement('aonMenu');
			aonMenu.clear();
			aonMenu.init();
		}

		getUser().then(user => {
			localStorage.setItem('aon_domain_login', user.login);
			getUserAppRole().then(user => {
				if(!this.isMobile()){
					aonHeader.setAttribute('company', JSON.stringify(company));
					aonHeader.setAttribute('user', JSON.stringify(user));
				}
				rootPanel(this.isMobile()
					? '<aon-mobile-desktop id="aonDesktop"></aon-mobile-desktop>'
					: '<aon-desktop id="aonDesktop"></aon-desktop>');
				let aonDesktop = document.getElementById('aonDesktop');
				aonDesktop.setAttribute('company', JSON.stringify(company));
				aonDesktop.setAttribute('user', JSON.stringify(user));
			});
		});
	}

}
if(!window.customElements.get(AON_TAG.AON_MODULE)){
	window.customElements.define(AON_TAG.AON_MODULE, AonModule);
}
