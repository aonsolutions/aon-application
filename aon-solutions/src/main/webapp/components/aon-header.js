import {closeSession} from  '../services/service.js';
import {rootPanel} from '../services/gwtLoader.js';

import './aon-configuration.js';
import './aon-invoice-panel.js';
import './aon-invoice.js';
import './aon-user.js';
import './aon-icon-button.js';
import './aon-search-box.js';



class AonHeader extends HTMLElement {

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

			<div class="aonHeader" >
				<span>
					<img id="aonLogo" class="aonLogo" src="../assets/logo.png" width="240px" />
					<img id="aonLogo2" class="aonLogo" style="display:none;" src="../assets/logoCompany.png" width="135px" />
					<img id="aonLogoParent" class="aonParentLogo" style="display:none;" />
				</span>

				<span id="aon-header-company-list" class="aonRight100" style="display:none;">
					<aon-icon-button id="aon-header-company-list-button" icon="business"></aon-icon-button>
				</span>

				<span id="aonHeaderCompanyLogo" class="aonRight140" style="display:none;">
					<img id="aonHeaderCompanyLogoImg" height="40px"/>
				</span>

				<span id="aon-header-search" class="aonRight100" >
					<aon-search-box id="aon-header-search-box"></aon-search-box>
				</span>

				<span id="aon-header-help" class="aonRight60" style="display:none;">
					<aon-icon-button id="aon-header-help-button" icon="help_outline"></aon-icon-button>
				</span>

				<span id="aon-header-apps" class="aonRight60">
					<aon-icon-button id="aon-header-apps-button" icon="apps"></aon-icon-button>
				</span>

				<span id="aon-header-user" class="aonRight20">
					<aon-icon-button id="aon-header-user-button" icon="account_circle"></aon-icon-button>
				</span>

				<span id="aon-header-company" class="aonRight140" style="display:none;top:55px;">
					<span id="aon-header-company-name"> </span>
				</span>

				<span id="aon-header-home" class="aonRight100" style="display:none;top:40px;">
					<aon-icon-button id="aon-header-home-button" icon="home" outlined="true"></aon-icon-button>
				</span>

				<span id="aon-header-alma" class="aonRight60" style="display:none;top:40px;">
					<aon-icon-button id="aon-header-alma-button" image="../assets/apps/alma.png"></aon-icon-button>
				</span>

				<span id="aon-header-show-menu" class="aonRight20" style="display:none; top:40px;background-color:#f1f1f1; border-radius: 100px 0 0 100px;right: 0;padding-right: 20px;">
					<aon-icon-button id="aon-header-show-menu-button" icon="keyboard_arrow_right" noHover="true"></aon-icon-button>
				</span>

				<span class="aonRight160">
					<ul class="mdl-menu mdl-js-menu mdl-js-ripple-effect" for="aon-header-user-button">
						<li id="aonHeaderConfiguration" class="mdl-menu__item">
							<i class="material-icons mdl-list__item-icon aonMenuIcon">settings</i>
							Configuración
						</li>
						<li id="aonHeaderCloseSession" class="mdl-menu__item">
							<i class="material-icons mdl-list__item-icon aonMenuIcon">input</i>
							Cerrar Sesión
						</li>
					</ul>
				</span>
			</div>
			`;

			this.build();
  }

	build() {

		document.getElementById('aonHeaderCloseSession')
			.addEventListener('click', () => closeSession());

		this.buildLogo();

		let aonHeaderCompanyListButton = document.getElementById('aon-header-company-list-button');
		aonHeaderCompanyListButton.addEventListener('click', () => {

			let aonLogo = document.getElementById('aonLogo');
			aonLogo.style.display = 'block';

			let aonLogo2 = document.getElementById('aonLogo2');
			aonLogo2.style.display = 'none';

			let aonLogoParent = document.getElementById('aonLogoParent');
			aonLogoParent.style.display = 'none';

			let aonHeaderCompanyList = document.getElementById('aon-header-company-list');
			aonHeaderCompanyList.style.display = 'none';

			let aonHeaderSearch = document.getElementById('aon-header-search');
			aonHeaderSearch.style.display = 'block';

			let aonHeaderHelp = document.getElementById('aon-header-help');
			aonHeaderHelp.style.display = 'none';

			let aonHeaderApps = document.getElementById('aon-header-apps');
			aonHeaderApps.style.display = 'block';

			let aonHeaderCompanyLogo = document.getElementById('aonHeaderCompanyLogo');
			aonHeaderCompanyLogo.style.display = 'none';

			let aonHeaderHome = document.getElementById('aon-header-home');
			aonHeaderHome.style.display = 'none';

			let aonHeaderAlma = document.getElementById('aon-header-alma');
			aonHeaderAlma.style.display = 'none';

			let aonHeaderShowMenu = document.getElementById('aon-header-show-menu');
			aonHeaderShowMenu.style.display = 'none';

			let aonHeaderCompany = document.getElementById('aon-header-company');
			aonHeaderCompany.style.display = 'none';

			let aonMenu = document.getElementById('aonMenu');
			aonMenu.buildMenu();
			aonMenu.setAttribute('opened', true);
			aonMenu.removeAttribute('company');
			aonMenu.removeAttribute('user');
			aonMenu.toogle();

			this.removeAttribute('company');
			this.removeAttribute('user');

			localStorage.removeItem('aon_domain_id');
			localStorage.removeItem('aon_domain_name');
			rootPanel('<aon-parent id="aonParent"></aon-parent>');
		});

		let aonHeaderHomeButton = document.getElementById('aon-header-home-button');
		aonHeaderHomeButton.addEventListener('click', () => {
			rootPanel('<aon-desktop id="aonDesktop"></aon-desktop>');
			let aonDesktop = document.getElementById('aonDesktop');
			aonDesktop.setAttribute('company', this.getAttribute('company'));
		});

		let aonHeaderAlmaButton = document.getElementById('aon-header-alma-button');
		aonHeaderAlmaButton.addEventListener('click', () => {
				open('http://chatbotalma.com/');
		});

		let aonHeaderHelpButton = document.getElementById('aon-header-help-button');
		aonHeaderHelpButton.addEventListener('click', () => {
			open('https://faqs.aonsolutions.es/');
		});

		let aonHeaderShowMenu = document.getElementById('aon-header-show-menu');
		aonHeaderShowMenu.addEventListener('click', () => {
			let aonHeaderShowMenuButton = document.getElementById('aon-header-show-menu-button');
			let aonMenu = document.getElementById('aonMenu');
			aonMenu.toogle();
			if(aonMenu.getAttribute('opened')) {
				aonHeaderShowMenu.style.paddingRight = '20px';
				aonHeaderShowMenuButton.setAttribute('icon', 'keyboard_arrow_right');
			} else {
				aonHeaderShowMenu.style.paddingRight = '0px';
				aonHeaderShowMenuButton.setAttribute('icon', 'keyboard_arrow_left');
			}
		});

		let aonHeaderConfiguration = document.getElementById('aonHeaderConfiguration');
		aonHeaderConfiguration.addEventListener('click', () => {
			this.aonConfiguration();
		});
	}

	buildLogo() {
		let aonLogo = document.getElementById('aonLogo');
		if(window.location.href.includes('ayudat')){
			aonLogo.src = '../assets/ayudat-logo.png';
			aonLogo.style.top = '0px';
			aonLogo.style.width = '150px';
		} else if(window.location.href.includes('translogia') || window.location.href.includes('tedi')){
			aonLogo.src = '../assets/tedi-logo.png';
			aonLogo.style.top = '0px';
		} else {
			aonLogo.src = '../assets/logo.png';
		}
		aonLogo.addEventListener('click', () => {
			if(localStorage.getItem('aon_domain_id')){
				rootPanel('<aon-desktop id="aonDesktop"></aon-desktop>');
				let aonDesktop = document.getElementById('aonDesktop');
				aonDesktop.setAttribute('company', this.getAttribute('company'));
			} else {
				rootPanel('<aon-parent id="aonParent"></aon-parent>');
			}
		})

	}

	aonConfiguration() {
		rootPanel('<aon-configuration id="aon-configuration"></aon-configuration>');
		let aonConfiguration = document.getElementById('aon-configuration');
		if(this.getAttribute('company')){
			aonConfiguration.setAttribute('company', this.getAttribute('company'));
		}
		if(this.getAttribute('user')){
			aonConfiguration.setAttribute('user', this.getAttribute('user'));
		}
	}
}

window.customElements.define('aon-header', AonHeader);
