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

		let options = [
			{
				name: 'Recientes',
				icon: 'access_time',
				fn: () => this.loadIndex()
			},
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
		aonDocumental.addSidenavOptions('OPCIONES', options);

		this.loadIndex();
	}

	loadIndex() {
		let aonDocumental = document.getElementById('aonDocumental');
		aonDocumental.setContentHTML('<iframe src="../../aon-suite/public/documental/index.html" style="width:100%;height:100%;border:none;"></iframe>');
	}
}
window.customElements.define('aon-documental', AonDocumental);
