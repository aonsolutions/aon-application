import { AonElement } from '../components/AonElement.js';
import { getToken , getCompanies, login} from '../services/service.js';
import { AonLogin } from './login/aon-login.js';

import { AonHome } from './aon-home.js';
import { TAG } from '../environments/environments.js'; 
import * as LS  from '../services/localStorageService.js';
import './company/aon-mobile-parent.js';
import { AonLoader } from '../components/aon-loader.js';
import { AonNewLogin } from './login/aon-new-login.js';

export class AonModule extends AonElement {

	AON_LOGIN;
	AON_HOME;
	AON_MODULE_LOADER;

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
		this.AON_MODULE_LOADER = 'aonModuleLoader';
	}

	buildLogin(){
		this.clear();
		let login = LS.isNewTheme() ? new AonNewLogin() : new AonLogin();
		login.id = this.AON_LOGIN;
		this.appendChild(login)
	}

	buildHome() {
		this.clear();
		let home = new AonHome();
		home.id = this.AON_HOME;
		this.appendChild(home);
		let loader = new AonLoader();
		loader.id = this.AON_MODULE_LOADER;
		this.appendChild(loader);
		this.orientationLocked();
	}

	startLoading() {
		this.getElement(this.AON_MODULE_LOADER).startLoading();
	}

	stopLoading() {
		this.getElement(this.AON_MODULE_LOADER).stopLoading();
	}

	async load() {
		await this.checkLogin();
		if(getToken()){
			LS.removeDomain();
			this.buildHome();

			if(this.isMobile() && LS.getCompany()) {
				this.companySelection(LS.getCompany(), false);
				let home = this.getElement(this.AON_HOME);
				let aonHeader = this.getElement(home.AON_HEADER);
				let companyListButton = this.getElement(aonHeader.COMPANY_LIST_BUTTON);
				companyListButton.setDisabled(true);
				companyListButton.color = 'lightgray';
				getCompanies().then(() => {
					 companyListButton.setDisabled(false);
					 companyListButton.color = 'white';
				});
			} else {
				getCompanies().then(companies => {
					alert(companies.length)
					if(companies.length === 1){
						this.companySelection(companies[0], true);
					} else {
						this.getElement(this.AON_HOME).showMenu(false);
						this.rootPanelHtml(this.isMobile()
						 	? '<aon-mobile-parent id="aonParent"></aon-mobile-parent>'
						 	: '<aon-parent id="aonParent"></aon-parent>');
					}
				});
			}
		} else {
			this.buildLogin();
		}
	}

	async checkLogin() {
		const urlParams = new URLSearchParams(location.search);
		let user = urlParams.get('user');
		let password = urlParams.get('password');
		let token = urlParams.get('token');
		if(user && password) {
			const data = {
				username: user,
				password: password,
			};

			try {
				await login(data);
			} catch (e) {
				alert(e);
			}
			window.location = window.location.origin;
		}

		if(token) {
			LS.setToken(token);
			window.location = window.location.origin;
		}
	} 

	companySelection(company, onlyOne) {
		LS.setCompany(JSON.stringify(company));
		LS.setDomainId(company.id);
		LS.setDomainName(company.domain);
		LS.setDomainDocumnet(company.document);
		LS.setOnlyOne(onlyOne);
		LS.setDomainLogin(company.login);
		
		let home = this.getElement(this.AON_HOME);
		home.showMenu(true);

		let aonHeader = this.getElement(home.AON_HEADER);
		aonHeader.showCompanyOption(company, onlyOne);

		if(!this.isMobile()){			
			let aonMenu = this.getElement(home.AON_MENU);
			aonMenu.clear();
			aonMenu.init();
		} else aonHeader.companyIn(onlyOne);

		this.rootPanelHtml(this.isMobile()
			? '<aon-mobile-desktop id="aonDesktop"></aon-mobile-desktop>'
			: '<aon-desktop id="aonDesktop"></aon-desktop>');
	}

	async orientationLocked(){
		// try {
		// 	let orientKey = 'orientation';
		// 	if ('mozOrientation' in screen) {
		// 		orientKey = 'mozOrientation';
		// 	} else if ('msOrientation' in screen) {
		// 		orientKey = 'msOrientation';
		// 	}

		// 	let resp = await window.screen[orientKey].lock("portrait-primary");
		// 	console.log(resp)
		// } catch (error) {
		// 	alert(error);
		// }
	}
}
if(!window.customElements.get(TAG.AON_MODULE)){
	window.customElements.define(TAG.AON_MODULE, AonModule);
}
