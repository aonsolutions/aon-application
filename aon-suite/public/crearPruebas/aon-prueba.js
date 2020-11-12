class aonPruebas extends HTMLElement {

	constructor () {
		super();
	}

	connectedCallback () {
		this.innerHTML = `
			<aon-application id="aonPruebas" title="PRUEBAS"></aon-application>
		`;
		this.build();
 	}

 	build() {
		this.loadIndex();
	}

	loadIndex() {
		let aonPruebas = document.getElementById('aonPruebas');
		aonPruebas.setContentHTML('<iframe src="./crear_datos.html" style="width:100%;height:100%;border:none;"></iframe>');
	}
}
window.customElements.define('aon-faqs', aonPruebas);
