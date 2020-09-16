class AonContrata extends HTMLElement {

	constructor () {
		super();
	}

	connectedCallback () {
		this.innerHTML = `
			<!-- AON CONTRAT@ TOOLBAR -->
			<aon-toolbar id="aonContrata" title="CONTRAT@"></aon-toolbar>
			<!-- AON CONFIGURATION MENU (SIDENAV) -->
			<div id="aonContrataSidenav" class="sidenav">
				<ul class="aonClip">
          <li id="aonContrataEmployee" class="aonAppMenuSidenavList aonOpacity" >
            <span class="aonMenuItemSpan"> Empleados </span>
          </li>

          <li id="aonContrataContract" class="aonAppMenuSidenavList aonOpacity" >
            <span class="aonMenuItemSpan"> Contratos	</span>
          </li>

          <li id="aonContrataMovements" class="aonAppMenuSidenavList aonOpacity">
            <span class="aonMenuItemSpan"> Movimientos </span>
          </li>

          <li id="aonContrataIT" class="aonAppMenuSidenavList aonOpacity">
            <span class="aonMenuItemSpan"> Partes IT </span>
          </li>

          <li id="aonContrataCerts" class="aonAppMenuSidenavList aonOpacity" >
            <span class="aonMenuItemSpan"> Informes y Cerificados </span>
          </li>
				</ul>
			</div>

			<!-- AON CONTRAT@ CONTENT -->
			<div id="aonContrataContent" class="aonContent">

			</div>

		`;
    this.build();
 	}

 	build() {

	}
}
window.customElements.define('aon-contrata', AonContrata);
