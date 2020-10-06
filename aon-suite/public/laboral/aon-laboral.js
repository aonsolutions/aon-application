class AonLaboral extends HTMLElement {

	constructor () {
		super();
	}

	connectedCallback () {
		this.innerHTML = `
			<aon-application id="aonLaboral" title="LABORAL"></aon-application>
		`;
    this.build();
 	}

 	build() {
		let aonLaboral = document.getElementById('aonLaboral');

		aonLaboral.addToolbarOption('Add', 'add', () => {alert('Add Example')});

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
		aonLaboral.addSidenavOptions('DOCUMENTOS', documentOptions);

		let categoryOptions = [
		{
				name: 'Resumen',
				icon: 'folder',
				fn: () => this.loadIndex()
			}
		];
		aonLaboral.addSidenavOptions('CATEGORIAS', categoryOptions);
	}

	loadIndex() {
		let aonLaboral = document.getElementById('aonLaboral');
		aonLaboral.setContentHTML('<iframe src="./index.html" style="width:100%;height:100%;border:none;"></iframe>');
	}

}
window.customElements.define('aon-laboral', AonLaboral);
