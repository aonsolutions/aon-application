import { AonElement } from '../components/AonElement.js';
import { rootPanel } from '../services/gwtLoader.js';
import { getToken , getCompanies, getUser, getUserAppRole } from '../services/service.js';
import { AonLogin } from './login/aon-login.js';
import { AonHome } from './aon-home.js';
import { TAG } from '../environments/environments.js'; 
import * as LS  from '../services/localStorageService.js';
import { waitEl } from '../services/utils.js';

export class AonModule extends AonElement {

	AON_LOGIN;
	AON_HOME;

	constructor () {
		super();
	}

	connectedCallback () {
		this.initialize();
		this.load();
	}

	initialize(){
		this.AON_LOGIN = 'aonLogin';
		this.AON_HOME = 'aonHome';
	}

	buildLogin(){
		this.clear();
		let login = new AonLogin();
		login.id = this.AON_LOGIN;
		this.appendChild(login)
	}

	buildHome() {
		this.clear();
		let home = new AonHome();
		home.id = this.AON_HOME;
		this.appendChild(home);
	}

	load() {
		if(getToken()){
			LS.removeDomain();
			this.buildHome();

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
		} else {
			this.buildLogin();
		}
	}

	

	companySelection(company) {
		LS.setCompany(JSON.stringify(company));
		LS.setDomainId(company.id);
		LS.setDomainName(company.domain);

		let home = this.getElement(this.AON_HOME);
		home.showMenu(true);

		let aonHeader = this.getElement(home.AON_HEADER);
		aonHeader.showCompanyOption(company);

		if(!this.isMobile()){
			
			let aonMenu = this.getElement(home.AON_MENU);
			aonMenu.clear();
			aonMenu.init();
		}

		getUser().then(user => {
			LS.setDomainLogin(user.login);
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
if(!window.customElements.get(TAG.AON_MODULE)){
	window.customElements.define(TAG.AON_MODULE, AonModule);
}
