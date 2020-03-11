import './aon-configuration.js'

function aonConfiguration() {
	rootPanel('<aon-configuration></aon-configuration>');
	getUsers();
}

function aonDesktop() {
	rootPanel('<aon-desktop></aon-desktop>');
}

class AonMenu extends HTMLElement {
	constructor () {
		super();
	}
	
	connectedCallback () {
		this.innerHTML = `
			<style>
				.aon-right20 {
					position: absolute;
					right:20px;
				}
				
				.aon-right160 {
					position: absolute;
					right:160px;
				}
				
				.aon-menu-icon {
					position: relative;
					top: 7px;
				}
				.aon-toolbar-padding{
					padding-top: 10px;
					padding-left: 10px;
				}
				
			</style>
			
			<div class="aon-toolbar-padding" >
				<span style="margin-left:5px;">
					<img style="cursor: pointer;"  src="../tedi-center/assets/logo.png" onclick="aonDesktop()"  width="240px" />
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
					<button class="mdl-button mdl-js-button" onclick="startModule('aon_gwt_aio', 'config');">
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
window.aonDesktop = aonDesktop;
