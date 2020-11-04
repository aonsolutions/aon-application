import {closeSession} from  '../services/service.js';
import {isMobile} from  '../services/utils.js';
import {rootPanel} from '../services/gwtLoader.js';

import '../components/aon-icon-button.js';
import '../components/aon-search-box.js';

import './configuration/aon-configuration.js';
import './invoice/aon-invoice-panel.js';
import './invoice/aon-invoice.js';
import './company/aon-mobile-desktop.js';
import './user/aon-user.js';

class AonMobileHeader extends HTMLElement {

	get id() {
		return this.getAttribute('id');
	}

	set id(id) {
		this.setAttribute('id', id);
	}

	get company() {
		return this.getAttribute('company');
	}

	set company(company) {
		this.setAttribute('company', company);
	}

	constructor () {
		super();
	}

	connectedCallback () {
		this.innerHTML = `
			<div id="aonHeaderWeb" class="aonHeader" >
				<span>
					<img id="aonMobileLogo" class="aonMobileLogo" width="230px" />
				</span>
			</div>
			`;

			this.build();
  }

	build() {
		const BASE_ID = 'aonHeader';
		let aonHeaderWeb = document.getElementById('aonHeaderWeb');
		this.buildLogo();
	}

	buildLogo() {
		let aonLogo = document.getElementById('aonMobileLogo');

		if(window.location.href.includes('ayudat')){
			aonLogo.src = '../assets/ayudat-logo2.png';
		} else if(window.location.href.includes('translogia') || window.location.href.includes('tedi')){
			aonLogo.src = '../assets/tedi-logo.png';
			aonLogo.style.top = '0px';
		} else aonLogo.src = '../assets/aon-logo2.png';
		aonLogo.addEventListener('click', () => {
			if(localStorage.getItem('aon_domain_id')){
				rootPanel('<aon-mobile-desktop id="aonDesktop"></aon-mobile-desktop>');
				let aonDesktop = document.getElementById('aonDesktop');
				aonDesktop.setAttribute('company', this.getAttribute('company'));
			} else {
				rootPanel('<aon-parent id="aonParent"></aon-parent>');
			}
		});
	}
}

window.customElements.define('aon-mobile-header', AonMobileHeader);
