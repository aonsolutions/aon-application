import {closeSession} from  '../services/service.js';
import {isMobile} from  '../services/utils.js';
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

			<div id="aonHeaderMobile" class="aonHeader">
				<span>
					<img id="aonLogoMobile" class="aonLogo" src="../assets/logoMobile.png" width="130px" />
				</span>

				<span id="aonHeaderMobileCompanyList" class="aonRight60 aonHeaderButtonMobile" style="display:none;">
					<aon-icon-button id="aonHeaderMobileCompanyListButton" icon="business"></aon-icon-button>
				</span>

				<span id="aonHeaderMobileUser" class="aonRight20 aonHeaderButtonMobile">
					<aon-icon-button id="aonHeaderMobileUserButton" icon="account_circle"></aon-icon-button>
				</span>

				<span class="aonRight160">
					<ul class="mdl-menu mdl-js-menu mdl-js-ripple-effect" for="aonHeaderMobileUserButton">
						<li id="aonHeaderMobileConfiguration" class="mdl-menu__item">
							<i class="material-icons mdl-list__item-icon aonMenuIcon">settings</i>
							Configuración
						</li>
						<li id="aonHeaderMobileCloseSession" class="mdl-menu__item">
							<i class="material-icons mdl-list__item-icon aonMenuIcon">input</i>
							Cerrar Sesión
						</li>
					</ul>
				</span>

			</div>

			<div id="aonHeaderWeb" class="aonHeader" >
				<span>
					<img id="aonLogo" class="aonLogo" style="display:none;"  src="../assets/logo.png" width="240px" />
					<img id="aonLogo2" class="aonLogo" style="display:none;" src="../assets/logoCompany.png" width="135px" />
					<img id="aonLogoParent" class="aonParentLogo" style="display:none;" />
				</span>

				<span id="aonHeaderCompanyList" class="aonRight100" style="display:none;">
					<aon-icon-button id="aonHeaderCompanyListButton" icon="business"></aon-icon-button>
				</span>

				<span id="aonHeaderCompanyLogo" class="aonRight140" style="display:none;">
					<img id="aonHeaderCompanyLogoImg" height="40px"/>
				</span>

				<span id="aonHeaderSearch" class="aonRight100" >
					<aon-search-box id="aonHeaderSearchBox"></aon-search-box>
				</span>

				<span id="aonHeaderHelp" class="aonRight60" style="display:none;">
					<aon-icon-button id="aonHeaderHelpButton" icon="help_outline"></aon-icon-button>
				</span>

				<span id="aonHeaderApps" class="aonRight60">
					<aon-icon-button id="aonHeaderAppsButton" icon="apps"></aon-icon-button>
				</span>

				<span id="aonHeaderUser" class="aonRight20">
					<aon-icon-button id="aonHeaderUserButton" icon="account_circle"></aon-icon-button>
				</span>

				<span id="aonHeaderCompany" class="aonRight140" style="display:none;top:55px;">
					<span id="aonHeaderCompanyName"> </span>
				</span>

				<span id="aonHeaderHome" class="aonRight100" style="display:none;top:40px;">
					<aon-icon-button id="aonHeaderHomeButton" icon="home" outlined="true"></aon-icon-button>
				</span>

				<span id="aonHeaderAlma" class="aonRight60" style="display:none;top:40px;">
					<aon-icon-button id="aonHeaderAlmaButton" image="../assets/apps/alma.png"></aon-icon-button>
				</span>

				<span id="aonHeaderShowMenu" class="aonRight20" style="display:none; top:40px;background-color:#f1f1f1; border-radius: 100px 0 0 100px;right: 0;padding-right: 20px;">
					<aon-icon-button id="aonHeaderShowMenuButton" icon="keyboard_arrow_right" noHover="true"></aon-icon-button>
				</span>

				<span class="aonRight160">
					<ul class="mdl-menu mdl-js-menu mdl-js-ripple-effect" for="aonHeaderUserButton">
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
		const BASE_ID = isMobile() ? 'aonHeaderMobile' : 'aonHeader';
		let aonHeaderMobile = document.getElementById('aonHeaderMobile');
		let aonHeaderWeb = document.getElementById('aonHeaderWeb');
		aonHeaderMobile.style.display = isMobile() ? 'block' : 'none';
		aonHeaderWeb.style.display = !isMobile() ? 'block' : 'none';

		document.getElementById(BASE_ID + 'CloseSession')
			.addEventListener('click', () => closeSession());

		this.buildLogo();
		if(!isMobile()) {
			let aonHeaderHomeButton = document.getElementById(BASE_ID + 'HomeButton');
			aonHeaderHomeButton.addEventListener('click', () => {
				rootPanel('<aon-desktop id="aonDesktop"></aon-desktop>');
				let aonDesktop = document.getElementById('aonDesktop');
				aonDesktop.setAttribute('company', this.getAttribute('company'));
			});

			let aonHeaderAlmaButton = document.getElementById(BASE_ID + 'AlmaButton');
			aonHeaderAlmaButton.addEventListener('click', () => {
					open('http://chatbotalma.com/');
			});

			let aonHeaderHelpButton = document.getElementById(BASE_ID + 'HelpButton');
			aonHeaderHelpButton.addEventListener('click', () => {
				open('https://faqs.aonsolutions.es/');
			});

			let aonHeaderShowMenu = document.getElementById(BASE_ID + 'ShowMenu');
			aonHeaderShowMenu.addEventListener('click', () => {
				let aonHeaderShowMenuButton = document.getElementById(BASE_ID + 'ShowMenuButton');
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
		}

		let aonHeaderCompanyListButton = document.getElementById(BASE_ID + 'CompanyListButton');
		aonHeaderCompanyListButton.addEventListener('click', () => {
			if(!isMobile()) {
				let aonLogo = document.getElementById('aonLogo');
				aonLogo.style.display = 'block';

				let aonLogo2 = document.getElementById('aonLogo2');
				aonLogo2.style.display = 'none';

				let aonLogoParent = document.getElementById('aonLogoParent');
				aonLogoParent.style.display = 'none';

				let aonHeaderSearch = document.getElementById(BASE_ID + 'Search');
				aonHeaderSearch.style.display = 'block';

				let aonHeaderHelp = document.getElementById(BASE_ID + 'Help');
				aonHeaderHelp.style.display = 'none';

				let aonHeaderApps = document.getElementById(BASE_ID + 'Apps');
				aonHeaderApps.style.display = 'block';

				let aonHeaderCompanyLogo = document.getElementById(BASE_ID + 'CompanyLogo');
				aonHeaderCompanyLogo.style.display = 'none';

				let aonHeaderHome = document.getElementById(BASE_ID + 'Home');
				aonHeaderHome.style.display = 'none';

				let aonHeaderAlma = document.getElementById(BASE_ID + 'Alma');
				aonHeaderAlma.style.display = 'none';

				let aonHeaderShowMenu = document.getElementById(BASE_ID + 'ShowMenu');
				aonHeaderShowMenu.style.display = 'none';

				let aonHeaderCompany = document.getElementById(BASE_ID + 'Company');
				aonHeaderCompany.style.display = 'none';

				let aonMenu = document.getElementById('aonMenu');
				aonMenu.buildMenu();
				aonMenu.setAttribute('opened', true);
				aonMenu.removeAttribute('company');
				aonMenu.removeAttribute('user');
				aonMenu.toogle();
			}

			let aonHeaderCompanyList = document.getElementById(BASE_ID + 'CompanyList');
			aonHeaderCompanyList.style.display = 'none';

			this.removeAttribute('company');
			this.removeAttribute('user');

			localStorage.removeItem('aon_domain_id');
			localStorage.removeItem('aon_domain_name');
			rootPanel('<aon-parent id="aonParent"></aon-parent>');
		});

		let aonHeaderConfiguration = document.getElementById(BASE_ID + 'Configuration');
		aonHeaderConfiguration.addEventListener('click', () => {
			this.aonConfiguration();
		});
	}

	buildLogo() {
		let aonLogo = isMobile() ? document.getElementById('aonLogoMobile') : document.getElementById('aonLogo');
		aonLogo.style.display = 'block';
		if(!isMobile()){
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
