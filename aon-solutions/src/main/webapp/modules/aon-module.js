import {AonElement} from '../components/AonElement.js';
import {rootPanel} from '../services/gwtLoader.js';
import {setPosition} from '../services/maps.js';

import './login/aon-login.js';
import './aon-home.js';
import './company/aon-parent.js';
import './company/aon-mobile-desktop.js';

export class AonModule extends AonElement {
	AON_LOGIN;
	AON_HOME;
	AON_DESKTOP;
	AON_PARENT;

	constructor () {
		super();
		this.AON_LOGIN = 'aonLogin';
		this.AON_HOME = 'aonHome';
		this.AON_DESKTOP = 'aonDesktop';
		this.AON_PARENT = 'aonParent';
	}

	connectedCallback () {
		let loginDiv= this.createElement('div');
		loginDiv.id = this.AON_LOGIN;
		loginDiv.style.display = 'display:none'
		this.appendChild(loginDiv);
		loginDiv.innerHTML = '<aon-login></aon-login>';

		let homeDiv = this.createElement('div');
		homeDiv.id = this.AON_HOME;
		homeDiv.style.display = 'display:none'
		homeDiv.innerHTML = '<aon-home></aon-home>';
		this.appendChild(homeDiv);

		this.load();
	}

	load() {
		if(localStorage.getItem('aon_session_id')){
			document.getElementById("aonLogin").style.display = 'none';
			document.getElementById("aonHome").style.display = 'block';
			localStorage.removeItem('aon_domain_id');
			localStorage.removeItem('aon_domain_name');
			localStorage.removeItem('aon_domain_login');

			rootPanel(this.isMobile()
			 	? '<aon-mobile-desktop id="aonDesktop"></aon-mobile-desktop>'
			 	: '<aon-parent id="aonParent"></aon-parent>');
		} else {
			this.getElement(this.AON_LOGIN).style.display = 'block';
			this.getElement(this.AON_HOME).style.display = 'none';
		}
		
		window.setPosition = (pos) => setPosition(pos);
	}
}
window.customElements.define('aon-module',  AonModule);
