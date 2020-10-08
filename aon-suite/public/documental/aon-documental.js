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

		aonDocumental.addToolbarOption('Subir', 'file_upload', () => {
			const contentIframe = document.querySelector('iframe');

			contentIframe.contentWindow.document.getElementById('upload').click();
		});

		let documentOptions = [
			{
				name: 'Recientes',
				icon: 'access_time',
				fn: () => this.loadIndex('recientes')
			},
			{
				name: 'Pendientes',
				icon: 'inbox',
				fn: () => this.loadIndex('pendientes')
			}
		];
		aonDocumental.addSidenavOptions('DOCUMENTOS', documentOptions);

		let categoryOptions = [
      {
				name: 'A Contabilizar',
				icon: 'folder',
				fn: () => this.loadIndex(5)
			},
      {
				name: 'Banco',
				icon: 'folder',
				fn: () => this.loadIndex(13)
			},
      {
				name: 'Contable',
				icon: 'folder',
				fn: () => this.loadIndex(15)
			},
      {
				name: 'Fiscal',
				icon: 'folder',
				fn: () => this.loadIndex(8)
			},
      {
				name: 'Jurídico',
				icon: 'folder',
				fn: () => this.loadIndex(6)
			},
      {
				name: 'Seguros',
				icon: 'folder',
				fn: () => this.loadIndex(17)
			},
      {
				name: 'Enviados',
				icon: 'folder',
				fn: () => this.loadIndex(1)
			},
      {
				name: 'Nóminas Empleados',
				icon: 'folder',
				fn: () => this.loadIndex('nominas')
			}
		];
		aonDocumental.addSidenavOptions('CATEGORIAS', categoryOptions);
		this.loadIndex();
	}

	loadIndex(folder = 5) {
		let aonDocumental = document.getElementById('aonDocumental');
		aonDocumental.setContentHTML('<iframe src="./index.html?folder=' + folder + '" style="width:100%;height:100%;border:none;"></iframe>');
	}
}
window.customElements.define('aon-documental', AonDocumental);
