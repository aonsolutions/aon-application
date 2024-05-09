import { AonElement } from 'aonsolutions/components/AonElement.js';
import { getToken , getCompanies, getUser, login} from 'aonsolutions/services/service.js';

import { AonParent } from './aon-parent.js';
import { AonHome } from './aon-home.js';
import { TAG } from 'aonsolutions/environments/environments.js'; 
import * as LS  from 'aonsolutions/services/localStorageService.js';
import 'aonsolutions/modules/company/aon-mobile-parent.js';
import { AonLoader } from 'aonsolutions/components/aon-loader.js';
import { AonNewLogin } from 'aonsolutions/modules/login/aon-new-login.js';

import { AonNewInput } from "aonsolutions/components/aon-new-input.js";


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
		let login = new AonNewLogin(new AonNewInput(), this.newCompanyLogoDiv());
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

	async load() {
		await this.checkLogin();
		if(getToken()){
			LS.removeDomain();
			this.buildHome();
		} else {
//			this.buildHome();
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
	
	newCompanyLogoDiv() {
		let divCompanyLogo = this.createElement(TAG.DIV);
		let imgCompanyLogo = this.createElement(TAG.IMG);
		imgCompanyLogo.src='aonDocuments/company.logo';
		imgCompanyLogo.style.maxWidth='201px';
		divCompanyLogo.appendChild(imgCompanyLogo);
		return divCompanyLogo;
	} 

}
if(!window.customElements.get(TAG.AON_MODULE)){
	window.customElements.define(TAG.AON_MODULE, AonModule);
}
