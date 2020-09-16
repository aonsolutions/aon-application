class AonExample extends HTMLElement {

	constructor () {
		super();
	}

	connectedCallback () {
		this.innerHTML = `
			<!-- AON EXAMPLE TOOLBAR -->
			<aon-toolbar id="aonExample" title="EXAMPLE"></aon-toolbar>
			<!-- AON EXAMPLE MENU (SIDENAV) -->
			<div id="aonExampleSidenav" class="sidenav">
				<ul class="aonClip">

				</ul>
			</div>

			<!-- AON CONTRAT@ CONTENT -->
			<div id="aonExampleContent" class="aonContent">

			</div>

		`;
    this.build();
 	}

 	build() {

	}
}
window.customElements.define('aon-example', AonExample);
