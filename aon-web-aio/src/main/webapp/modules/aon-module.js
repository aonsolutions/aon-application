import { AonElement } from '../components/AonElement.js';
import { login } from '../services/service.js';
import { AonHome } from './aon-home.js';
import { CONSTANT, TAG } from '../environments/environments.js';
import { AonLoader } from '../components/aon-loader.js';
import { AonLogin } from './login/aon-login.js';
import { AonNewInput } from "../components/aon-new-input.js";
import { AonMobileParent } from './company/aon-mobile-parent.js';
import { AonParent } from './aon-parent.js';

import * as LS from '../services/localStorageService.js';

import { initSingletonAccess } from '../js/singletonAccess.js';
 

export class AonModule extends AonElement {

	AON_LOGIN;
	AON_HOME;
	AON_MODULE_LOADER;

	constructor() {
		super();
	}

	connectedCallback() {
		this.initialize();
		this.load();
	}

	initialize() {
		initSingletonAccess();
		this.AON_LOGIN = 'aonLogin';
		this.AON_HOME = 'aonHome';
		this.AON_MODULE_LOADER = 'aonModuleLoader';
	}

	buildLogin() {
		this.clear();
		let login = new AonLogin(new AonNewInput(), this.newCompanyLogoDiv());
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
		
	}

	startLoading() {
		this.getElement(this.AON_MODULE_LOADER).startLoading();
	}

	stopLoading() {
		this.getElement(this.AON_MODULE_LOADER).stopLoading();
	}

	load() {
		this.loading();
		this.checkLogin();
	}

	checkLogin() {
		const urlParams = new URLSearchParams(location.search);
		let username = urlParams.get(CONSTANT.USER);
		let password = urlParams.get(CONSTANT.PASSWORD);
		let token = urlParams.get(CONSTANT.TOKEN);

		if ((username && password) || token) {
			if(username) sessionStorage.setItem(CONSTANT.USERNAME, username);
			if(password) sessionStorage.setItem(CONSTANT.PASSWORD, password);
			if(token) sessionStorage.setItem(CONSTANT.TOKEN, token);

			window.location = `${window.location.origin}${window.location.pathname}`;
		} else this.login();
	}

	login() {
		let username = sessionStorage.getItem(CONSTANT.USERNAME);
		let password = sessionStorage.getItem(CONSTANT.PASSWORD);
		let token = sessionStorage.getItem(CONSTANT.TOKEN);

		let data;
		if ((username && password) || token) {
			data = {username, password, token};
			sessionStorage.removeItem(CONSTANT.USERNAME);
			sessionStorage.removeItem(CONSTANT.PASSWORD);
			sessionStorage.removeItem(CONSTANT.TOKEN);
		} else if(LS.getToken() && LS.getToken().length > 0) {
			data = {token: LS.getToken()};
		} else this.buildLogin();

		if (data) {
			login(data).then(() => {
				this.buildHome();
				if (this.isMobile()) {
					let mobileParent = new AonMobileParent();
					mobileParent.selection = true;
					this.rootPanel(mobileParent);
				} else {
					this.rootPanel(new AonParent());
				}
			}).catch(e => {
				this.buildLogin();
				this.showLoginErrorToast(e);
			});
		}
	}

	loading() {
		let loader = new AonLoader();
		loader.id = this.AON_MODULE_LOADER;
		this.appendChild(loader);
		loader.startLoading();
	}

	showLoginErrorToast(e) {
		let error = JSON.parse(e);
		const loginToast = this.getElement("aonLoginToast");
		loginToast.start({ type: 'error', message: error.message, delay: 3000 });
	}

	newCompanyLogoDiv() {
		let divCompanyLogo = this.createElement(TAG.DIV);
		let imgCompanyLogo = this.createElement(TAG.IMG);
		imgCompanyLogo.src = 'aonDocuments/company.logo';
		imgCompanyLogo.style.maxWidth = '201px';
		divCompanyLogo.appendChild(imgCompanyLogo);
		return divCompanyLogo;
	}

}

if (!window.customElements.get(TAG.AON_MODULE)) {
	window.customElements.define(TAG.AON_MODULE, AonModule);
}
