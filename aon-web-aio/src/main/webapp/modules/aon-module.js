import { AonElement } from '../components/AonElement.js';
import { login } from '../services/service.js';

import { AonHome } from './aon-home.js';
import { TAG } from '../environments/environments.js';
import * as LS from '../services/localStorageService.js';
import './company/aon-mobile-parent.js';
import { AonLoader } from '../components/aon-loader.js';
import { AonNewLogin } from './login/aon-new-login.js';

import { AonNewInput } from "../components/aon-new-input.js";
import { AonParent } from 'aonparent';
import { AonMobileParent } from './company/aon-mobile-parent.js';


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
		this.AON_LOGIN = 'aonLogin';
		this.AON_HOME = 'aonHome';
		this.AON_MODULE_LOADER = 'aonModuleLoader';
	}

	async buildLogin() {
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
		LS.setNewTheme(true, false);
		await this.checkLogin();
		this.showLoginErrorToast();

	}

	async checkLogin() {
		const urlParams = new URLSearchParams(location.search);
		let user = urlParams.get('user');
		let password = urlParams.get('password');
		let token = urlParams.get('token');
		let beta = location.pathname === '/beta';

		// Prevenir bucle infinito: evitamos intentar login con un token que ya falló
		const failedToken = sessionStorage.getItem('failedToken');
		if (token && failedToken === token) {
			console.warn('El token ya falló previamente. Mostrando pantalla de login.');
			await this.buildLogin();
			return;
		}

		let data = null;

		if (user && password) {
			data = { username: user, password: password };
		} else if (token) {
			data = { token };
		} else if (beta && LS.getToken() && LS.getToken().length > 0) {
			data = { token: LS.getToken() };
		} else {
			// Sin credenciales, mostramos el login
			await this.buildLogin();
			return;
		}

		login(data).then(() => {
			if (LS.getToken()) {
				this.buildHome();
				if (this.isMobile()) {
					let mobileParent = new AonMobileParent();
					mobileParent.selection = true;
					this.rootPanel(mobileParent);
				} else {
					this.rootPanel(new AonParent());
				}
			}
		}).catch(e => {
			LS.closeSession();

			if (token) sessionStorage.setItem('failedToken', token);

			// Guardamos el mensaje de error para mostrarlo después de redirigir
			sessionStorage.setItem('loginError', e);

			// Redirigimos para limpiar la URL y mostrar el login limpio
			window.location = `${window.location.origin}${window.location.pathname}`;
		});
	}

	showLoginErrorToast() {
		const errorMessage = sessionStorage.getItem('loginError');
		const error = JSON.parse(errorMessage);

		if (errorMessage) {
			const loginToast = this.getElement("aonLoginToast");
			loginToast.start({ type: 'error', message: error.message, delay: 3000 });

			// Limpiamos el error después de mostrarlo
			sessionStorage.removeItem('loginError');
			sessionStorage.removeItem('failedToken');
		}
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
