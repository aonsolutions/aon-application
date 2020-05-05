import './aon-configuration.js';
import './aon-invoice-panel.js';
import './aon-invoice.js';
import './aon-user.js';


function aonConfiguration() {
	rootPanel('<aon-configuration></aon-configuration>');
	getUsers();
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

			<div class="aon-toolbar-padding" >
				<span style="margin-left:5px;">
					<img style="cursor: pointer;"  src="../tedi-center/assets/logo.png" onclick="load()"  width="240px" />
				</span>

				<span style="margin-left:20px;">
					<button class="mdl-button mdl-js-button" onclick="startModule('aon_gwt_aio', 'documents');">
						Documental
					</button>
				</span>
				<span style="margin-left:20px;">
					<button class="mdl-button mdl-js-button" onclick="startModule('aon_gwt_aio', 'issues');">
						Call Center
					</button>
				</span>

				<span style="margin-left:20px;">
					<button class="mdl-button mdl-js-button" onclick="aonInvoicePanel()">
						FACTURAS
					</button>
				</span>

				<span class="aon-right20" style="height: 60px;">
					<button id="aon-user-menu" class="mdl-button mdl-js-button mdl-button--icon">
						<i class="material-icons">account_circle</i>
					</button>
				</span>
				<span class="aon-right160">
					<ul class="mdl-menu mdl-js-menu mdl-js-ripple-effect" for="aon-user-menu">
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
  }
}

window.customElements.define('aon-menu', AonMenu);
window.aonConfiguration = aonConfiguration;
window.aonInvoicePanel = aonInvoicePanel;
