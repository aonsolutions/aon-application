import './aon-header.js';
import './aon-menu.js';

class AonHome extends HTMLElement {
	constructor () {
		super();
	}

	connectedCallback () {
		this.innerHTML = `
			<span id="aonShowMenu" style="position:absolute; z-index:2;display:none; top:60px;opacity:0.7;background-color:#f1f1f1; border-radius: 100px 0 0 100px;right: 0;">
				<aon-icon-button id="aonShowMenuButton" icon="keyboard_arrow_left" noHover="true"></aon-icon-button>
			</span>
			<aon-header id="aonHeader"></aon-header>
			<aon-menu id="aonMenu" class="aonMenu"></aon-menu>
			<div id="rootPanel" class="rootPanel"></div>
		`;

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

window.customElements.define('aon-home', AonHome);
