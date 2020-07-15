import './aon-configuration.js';
import './aon-invoice-panel.js';
import './aon-invoice.js';
import './aon-user.js';
import './aon-icon-button.js';

function aonConfiguration() {
	rootPanel('<aon-configuration></aon-configuration>');
}

function aonInvoicePanel() {
	rootPanel('<aon-invoice-panel></aon-invoice-panel>');
}

class AonMenu extends HTMLElement {
	constructor () {
		super();
	}

	connectedCallback () {
		this.innerHTML = `

			<div class="aon-header" >
				<span>
					<img id="aon-logo" class="aon-logo" src="../aon-solutions/assets/logo.png" onclick="load()"  width="240px" />
				</span>

				<span id="aon-header-company-list" class="aon-right100" style="display:none;">
					<aon-icon-button id="aon-header-company-list-button" icon="business"></aon-icon-button>
				</span>

				<span id="aon-header-help" class="aon-right60" style="display:none;">
					<aon-icon-button id="aon-header-help-button" icon="help_outline"></aon-icon-button>
				</span>

				<span id="aon-header-apps" class="aon-right60">
					<aon-icon-button id="aon-header-apps-button" icon="apps"></aon-icon-button>
				</span>

				<span id="aon-header-user" class="aon-right20">
					<aon-icon-button id="aon-header-user-button" icon="account_circle"></aon-icon-button>
				</span>

				<span id="aon-header-home" class="aon-right100" style="display:none;top:40px;">
					<aon-icon-button id="aon-header-home-button" icon="home"></aon-icon-button>
				</span>

				<span id="aon-header-show-menu" class="aon-right20" style="display:none; top:40px;background-color:#f1f1f1; border-radius: 100px 0 0 100px;right: 0;padding-right: 20px;">
					<aon-icon-button id="aon-header-show-menu-button" icon="keyboard_arrow_right"></aon-icon-button>
				</span>

				<span class="aon-right160">
					<ul class="mdl-menu mdl-js-menu mdl-js-ripple-effect" for="aon-header-user-button">
						<li class="mdl-menu__item" onclick="aonConfiguration()">
							<i class="material-icons mdl-list__item-icon aon-menu-icon">settings</i>
							Configuración
						</li>
						<li class="mdl-menu__item" onclick="closeSession()">
							<i class="material-icons mdl-list__item-icon aon-menu-icon">input</i>
							Cerrar Sesión
						</li>
					</ul>
				</span>
			</div>
			`;

			this.build();
  }

	build() {
		this.buildLogo();

		let aonHeaderCompanyListButton = document.getElementById('aon-header-company-list-button');
		aonHeaderCompanyListButton.addEventListener('click', () => {
			let aonHeaderCompanyList = document.getElementById('aon-header-company-list');
			aonHeaderCompanyList.style.display = 'none';

			let aonHeaderHelp = document.getElementById('aon-header-help');
			aonHeaderHelp.style.display = 'none';

			let aonHeaderApps = document.getElementById('aon-header-apps');
			aonHeaderApps.style.display = 'block';

			let aonHeaderHome = document.getElementById('aon-header-home');
			aonHeaderHome.style.display = 'none';

			let aonHeaderShowMenu = document.getElementById('aon-header-show-menu');
			aonHeaderShowMenu.style.display = 'none';

			let aonMenu = document.getElementById('aonMenu');
			aonMenu.setAttribute('opened', true);
			aonMenu.toogle();

			localStorage.removeItem('aon_domain_id');
			localStorage.removeItem('aon_domain_name');
			rootPanel('<aon-parent></aon-parent>');
		});
	}

	buildLogo() {
		let aonLogo = document.getElementById('aon-logo');
		if(window.location.href.includes('ayudat')){
			aonLogo.src = '../aon-solutions/assets/ayudat-logo.png';
			aonLogo.style.top = '0px';
			aonLogo.style.width = '150px';
		} else if(window.location.href.includes('translogia') || window.location.href.includes('tedi')){
			aonLogo.src = '../aon-solutions/assets/tedi-logo.png';
			aonLogo.style.top = '0px';
		} else {
			aonLogo.src = '../aon-solutions/assets/logo.png';
		}
	}
}

window.customElements.define('aon-header', AonMenu);
window.aonConfiguration = aonConfiguration;
window.aonInvoicePanel = aonInvoicePanel;
