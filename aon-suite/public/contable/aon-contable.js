class AonContable extends HTMLElement {

	constructor () {
		super();
	}

	connectedCallback () {
		this.innerHTML = `
			<aon-application id="aonContable" title="CONTABLE"></aon-application>
		`;
    this.build();
 	}

 	build() {
		let aonContable = document.getElementById('aonContable');

		aonContable.addToolbarOption('Add', 'add', () => {alert('Add Example')});

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
		aonContable.addSidenavOptions('DOCUMENTOS', documentOptions);

		let categoryOptions = [
		{
				name: 'Resumen',
				icon: 'folder',
				fn: () => this.loadIndex()
			},
      {
				name: 'PPyGG',
				icon: 'folder',
				fn: () => this.loadPPYGG()
			},

      {
				name: 'Balance',
				icon: 'folder',
				fn: () => this.loadBalance()
			}
		];
		aonContable.addSidenavOptions('CATEGORIAS', categoryOptions);
	}

	loadIndex() {
		let aonContable = document.getElementById('aonContable');
		aonContable.setContentHTML('<iframe src="./index.html" style="width:100%;height:100%;border:none;"></iframe>');
	}

	loadPPYGG() {
		let aonContable = document.getElementById('aonContable');
		aonContable.setContentHTML('<iframe src="./ppygg.html" style="width:100%;height:100%;border:none;"></iframe>');
	}

	loadBalance() {
		let aonContable = document.getElementById('aonContable');
		aonContable.setContentHTML('<iframe src="./balance.html" style="width:100%;height:100%;border:none;"></iframe>');
	}
}
window.customElements.define('aon-contable', AonContable);
