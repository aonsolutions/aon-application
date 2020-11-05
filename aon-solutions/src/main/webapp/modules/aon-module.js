import {isMobile} from '../services/utils.js';
import {rootPanel} from '../services/gwtLoader.js';

import './login/aon-login.js';
import './aon-home.js';
import './company/aon-parent.js';
import './company/aon-desktop.js';

(function() {

	class AonModule extends HTMLElement {

		constructor () {
			super();
		}

		connectedCallback () {
			this.innerHTML = `
				<div id="aonLogin" style="display:none">
					<aon-login></aon-login>
				</div>
				<div id="aonHome" style="display:none">
					<aon-home> </aon-home>
				</div>
			`;
			this.load();
		}

		load() {
			if(localStorage.getItem('aon_session_id')){
				document.getElementById("aonLogin").style.display = 'none';
				document.getElementById("aonHome").style.display = 'block';
				localStorage.removeItem('aon_domain_id');
				localStorage.removeItem('aon_domain_name');
				rootPanel(isMobile()
					? '<aon-mobile-desktop id="aonDesktop"></aon-mobile-desktop>'
					: '<aon-parent id="aonParent"></aon-parent>');
			} else {
				document.getElementById("aonLogin").style.display = 'block';
				document.getElementById("aonHome").style.display = 'none';
			};
		}
	}

	window.customElements.define('aon-module',  AonModule);

})();
