class AonDocumental extends HTMLElement {

	constructor () {
		super();
	}

	connectedCallback () {
		this.innerHTML = `
			<aon-application id="aonDocumental" title="DOCUMENTAL"></aon-application>
		`;
    this.build();
 	}

 	build() {
		let aonDocumental = document.getElementById('aonDocumental');

		aonDocumental.addToolbarOption('Add', 'add', () => {alert('Add Example')});

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
		aonDocumental.addSidenavOptions('DOCUMENTOS', documentOptions);

		let categoryOptions = [
      {
				name: 'A Contabilizar',
				icon: 'folder',
				fn: () => this.loadIndex()
			},
      {
				name: 'Banco',
				icon: 'folder',
				fn: () => this.loadIndex()
			},
      {
				name: 'Contable',
				icon: 'folder',
				fn: () => this.loadIndex()
			},
      {
				name: 'Fiscal',
				icon: 'folder',
				fn: () => this.loadIndex()
			},
      {
				name: 'Jurídico',
				icon: 'folder',
				fn: () => this.loadIndex()
			},
      {
				name: 'Seguros',
				icon: 'folder',
				fn: () => this.loadIndex()
			},
      {
				name: 'Enviados',
				icon: 'folder',
				fn: () => this.loadIndex()
			},
      {
				name: 'Nóminas Empleados',
				icon: 'folder',
				fn: () => this.loadIndex()
			}
		];
		aonDocumental.addSidenavOptions('CATEGORIAS', categoryOptions);
	}

	loadIndex() {
		let aonDocumental = document.getElementById('aonDocumental');
		aonDocumental.setContentHTML('<iframe src="./index.html" style="width:100%;height:100%;border:none;"></iframe>');
	}
}
window.customElements.define('aon-documental', AonDocumental);
