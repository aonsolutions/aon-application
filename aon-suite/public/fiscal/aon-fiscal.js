class AonFiscal extends HTMLElement {

	constructor () {
		super();
	}

	connectedCallback () {
		this.innerHTML = `
			<aon-application id="aonFiscal" title="FISCAL"></aon-application>
		`;
    this.build();
 	}

 	build() {
		let aonFiscal = document.getElementById('aonFiscal');

		aonFiscal.addToolbarOption('Add', 'add', () => {alert('Add Example')});

		let documentOptions = [
			{
				name: 'Recientes',
				icon: 'access_time',
				fn: () => this.loadIndex()
			},
			{
				name: 'Pendientes',
				icon: 'inbox',
				fn: () => this.loadIndex()
			}
		];
		aonFiscal.addSidenavOptions('DOCUMENTOS', documentOptions);

		let categoryOptions = [
		{
				name: 'Resumen',
				icon: 'folder',
				fn: () => this.loadIndex()
			}
		];
		aonFiscal.addSidenavOptions('CATEGORIAS', categoryOptions);
	}

	loadIndex() {
		let aonFiscal = document.getElementById('aonFiscal');
		aonFiscal.setContentHTML('<iframe src="./index.html" style="width:100%;height:100%;border:none;"></iframe>');
	}

}
window.customElements.define('aon-fiscal', AonFiscal);
