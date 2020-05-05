import './aon-login.js';
import './aon-home.js';
import './aon-parent.js';
import './aon-desktop.js';

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
			if(localStorage.getItem('session_id')){
				getCompanies().then(companies => {
					document.getElementById("aonLogin").style.display = 'none';
					document.getElementById("aonHome").style.display = 'block';
					if(companies.length === 1){
						localStorage.setItem('domain_id', companies[0].id);
						localStorage.setItem('domain_name', companies[0].domain);
						rootPanel('<aon-desktop></aon-desktop>');
					} else {
						localStorage.removeItem('domain_id');
						localStorage.removeItem('domain_name');
						rootPanel('<aon-parent></aon-parent>');
					}
				}).catch(error => {
					alert(error);
				});
			} else {
				document.getElementById("aonLogin").style.display = 'block';
				document.getElementById("aonHome").style.display = 'none';
			};
		}
	}

	window.customElements.define('aon-module',  AonModule);

})();
