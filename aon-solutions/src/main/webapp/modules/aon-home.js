import {AonElement} from '../components/AonElement.js';

import '../components/aon-icon-button.js';
import './aon-header.js';
import './aon-menu.js';
import './aon-mobile-header.js';
import './aon-mobile-menu.js';

export class AonHome extends AonElement {
	constructor () {
		super();
	}

	connectedCallback () {
		if(this.isMobile() && !localStorage.getItem('company')) {
			getCompanies().then( companies => {
				if(companies.length > 0) {
					let company = companies[0];
					localStorage.setItem('company', JSON.stringify(company));
					this.build();
				}
			});
		} else this.build();
	}

	build(){
		this.innerHTML = this.isMobile()
		?  `
			<aon-mobile-header id="aonHeader"></aon-mobile-header>
			<div id="rootPanel" class="aonMobileRootPanel"></div>
			<aon-mobile-menu id="aonMobileMenu"></aon-mobile-menu>
		`
		: `
			<span id="aonShowMenu" style="position:absolute; z-index:2;display:none; top:60px;opacity:0.7;background-color:#f1f1f1; border-radius: 100px 0 0 100px;right: 0;">
				<aon-icon-button id="aonShowMenuButton" icon="keyboard_arrow_left" noHover="true"></aon-icon-button>
			</span>
			<aon-header id="aonHeader"></aon-header>
			<aon-menu id="aonMenu" class="aonMenu"></aon-menu>
			<div id="rootPanel" class="rootPanel"></div>
		`;

		if(!this.isMobile()) {
			let aonShowMenu = document.getElementById('aonShowMenu');
			aonShowMenu.addEventListener('mouseover', () => {
				if(localStorage.getItem('aon_domain_id')){
					let aonMenuSidenav = document.getElementById('aonMenuSidenav');
					aonMenuSidenav.style.transitionDuration = '0ms';
					aonMenuSidenav.style.width = '150px';
					document.querySelectorAll("[id^='aonMenuListApp-']").forEach((item, i) => {
						item.style.display = 'inline-block';
						item.style.fontSize = '12px';
						item.style.fontFamily = 'Roboto,sans-serif';
						item.style.color = 'black';
					});
				}
			});
		}
	}
}
window.customElements.define('aon-home', AonHome);
