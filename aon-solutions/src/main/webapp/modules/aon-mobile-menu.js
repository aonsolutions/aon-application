import {startModule, rootPanel} from '../services/gwtLoader.js';

import {bidoq} from '../services/bidoq.js';
import '../components/aon-icon.js'

class AonMobileMenu extends HTMLElement {

	get id() {
		return this.getAttribute('id');
	}

	set id(id) {
		this.setAttribute('id', id);
	}

	get opened() {
		return this.getAttribute('opened');
	}

	set opened(opened) {
		this.setAttribute('opened', opened);
	}

	get company() {
		return this.getAttribute('company');
	}

	set company(company) {
		this.setAttribute('company', company);
	}

	get user() {
		return this.getAttribute('user');
	}

	set user(user) {
		this.setAttribute('user', user);
	}

	constructor () {
		super();
	}

	connectedCallback () {
		this.build();
  }

	build() {
		this.innerHTML = `
			<div id="aonMobileMenuSidenav" class="aonMobileMenuSidenav">
				<span id="aonMobileMenuHome"  style="top: 10px; position: relative;">
					<aon-icon-button id="aonMobileMenuHomeButton" icon="home"></aon-icon-button>
				</span>

				<span id="aonMobileMenuDocumental"  style="top: 10px; position: relative;">
					<aon-icon-button id="aonMobileMenuDocumentalButton" icon="attach_file"></aon-icon-button>
				</span>

				<span id="aonMobileMenuAdd"  style="top: 10px; position: relative;">
					<aon-icon-button id="aonMobileMenuAddButton" icon="add"></aon-icon-button>
				</span>

				<span id="aonMobileMenuTime" style="top: 10px; position: relative;">
					<aon-icon-button id="aonMobileMenuTimeButton" icon="access_time"></aon-icon-button>
				</span>

				<span id="aonMobileMenuInvoice"  style="top: 10px; position: relative;">
					<aon-icon-button id="aonMobileMenuInvoiceButton" icon="receipt"></aon-icon-button>
				</span>
			</div>
		`;
		let rp = document.getElementById('rootPanel');
		let aonMenuSidenav = document.getElementById('aonMobileMenuSidenav');

		if(this.getAttribute('opened')) {
			rp.className = 'rootMobilePanel';
			aonMenuSidenav.style.height = '60px';
			rp.style.marginBottom = '60px';
		} else {
			rp.className = 'rootPanel';
			aonMenuSidenav.style.height = '0px';
			rp.style.marginBottom = '0px';
		}

		let aonMobileMenuInvoiceButton = document.getElementById('aonMobileMenuInvoiceButton');
		aonMobileMenuInvoiceButton.addEventListener('click', () => {
			rootPanel('<aon-invoice-panel></aon-invoice-panel>');
		});
	}


	open() {
		let aonMenuSidenav = document.getElementById('aonMobileMenuSidenav');
		let rootPanel = document.getElementById('rootPanel');
		rootPanel.className = 'rootMobilePanel';
		aonMenuSidenav.style.height = '60px';
		rootPanel.style.marginBottom = '60px';
		this.setAttribute('opened', true);
	}

	close() {
		let aonMenuSidenav = document.getElementById('aonMobileMenuSidenav');
		let rootPanel = document.getElementById('rootPanel');
		rootPanel.className = 'rootPanel';
		aonMenuSidenav.style.height = '0px';
		rootPanel.style.marginBottom = '0px';

		this.removeAttribute('opened');
	}

}

window.customElements.define('aon-mobile-menu', AonMobileMenu);
