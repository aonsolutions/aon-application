import './aon-invoice.js';

class AonInvoicePanel extends HTMLElement {
	constructor () {
		super();
	}

	connectedCallback () {
		this.innerHTML = `
		<style>
			.aon-header {
				background-color: #fff;
				color: #000;
			}

			.aon-padding-left20 {
				padding-left: 20px !important;
			}
		</style>

		<div class="mdl-layout mdl-js-layout mdl-layout--fixed-header">
			<header class="mdl-layout__header aon-header">
				<div class="mdl-layout__header-row aon-padding-left20">
					<button id="aon-invoice-menu-button" class="mdl-button mdl-js-button mdl-button--icon">
						<i class="material-icons">menu</i>
					</button>
					<span class="mdl-layout-title aon-padding-left20">FACTURAS</span>
				<div class="mdl-layout-spacer"></div>

				<button class="mdl-button mdl-js-button mdl-button--icon">
					<i class="material-icons">add</i>
				</button>
			</header>

			<main class="mdl-layout__content">
				<div class="page-content">
					<!-- AON CONFIGURATION MENU (SIDENAV) -->
					<div id="aon-invoice-sidenav" class="sidenav">
						<ul class="mdl-menu mdl-menu--bottom-left mdl-js-menu mdl-js-ripple-effect aon-clip">
							<li class="mdl-menu__item aon-opacity">
								<i class="material-icons mdl-list__item-icon"  style="vertical-align: middle;">person</i>
								<span class="aon-menu-item-span"> Usuarios </span>
							</li>
							<li class="mdl-menu__item aon-opacity">
								<i class="material-icons mdl-list__item-icon" style="vertical-align: middle;">business</i>
								<span class="aon-menu-item-span"> Empresas	</span>
							</li>
						</ul>
					</div>

					<!-- AON CONFIGURATION CONTENT -->
					<div id="aon-invoice-content" class"aon-content">
						<aon-invoice></aon-invoice>
					</div>

				</div>
			</main>
		</div>
		`;
		this.build();
  	}

  	build(){
			let menu = document.getElementById('aon-invoice-menu-button');
 			menu.addEventListener('click', () => this.toogle());
  	}

		toogle() {
			if(document.getElementById("aon-invoice-sidenav").style.width === "250px"){
				document.getElementById("aon-invoice-sidenav").style.width = "0px";
				document.getElementById("aon-invoice-content").style.marginLeft = "0px";
			} else {
				document.getElementById("aon-invoice-sidenav").style.width = "250px";
				document.getElementById("aon-invoice-content").style['margin-left'] = "250px";
			}
		}
}

window.customElements.define('aon-invoice-panel', AonInvoicePanel);
