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
				fn: () => {
					aonDocumental.setAttribute('title', 'DOCUMENTAL / Recientes');
					aonDocumental.setContentHTML('<iframe src="./indexNew.html" style="width:100%;height:100%;border:none;"></iframe>');
				}
			},
      {
				name: 'A Contabilizar',
				icon: 'folder',
				fn: () => {
					aonDocumental.setAttribute('title', 'DOCUMENTAL / A Contabilizar');
					aonDocumental.setContentHTML('<iframe src="./indexNew.html" style="width:100%;height:100%;border:none;"></iframe>');
				}
			},
      {
				name: 'Banco',
				icon: 'folder',
				fn: () => {
					aonDocumental.setAttribute('title', 'DOCUMENTAL / Banco');
					aonDocumental.setContentHTML('<iframe src="./indexNew.html" style="width:100%;height:100%;border:none;"></iframe>');
				}
			},
      {
				name: 'Contable',
				icon: 'folder',
				fn: () => {
					aonDocumental.setAttribute('title', 'DOCUMENTAL / Contable');
					aonDocumental.setContentHTML('<iframe src="./indexNew.html" style="width:100%;height:100%;border:none;"></iframe>');
				}
			},
      {
				name: 'Fiscal',
				icon: 'folder',
				fn: () => {
					aonDocumental.setAttribute('title', 'DOCUMENTAL / Fiscal');
					aonDocumental.setContentHTML('<iframe src="./indexNew.html" style="width:100%;height:100%;border:none;"></iframe>');
				}
			},
      {
				name: 'Jurídico',
				icon: 'folder',
				fn: () => {
					aonDocumental.setAttribute('title', 'DOCUMENTAL / Jurídico');
					aonDocumental.setContentHTML('<iframe src="./indexNew.html" style="width:100%;height:100%;border:none;"></iframe>');
				}
			},
      {
				name: 'Seguros',
				icon: 'folder',
				fn: () => {
					aonDocumental.setAttribute('title', 'DOCUMENTAL / Seguros');
					aonDocumental.setContentHTML('<iframe src="./indexNew.html" style="width:100%;height:100%;border:none;"></iframe>');
				}
			},
      {
				name: 'Enviados',
				icon: 'folder',
				fn: () => {
					aonDocumental.setAttribute('title', 'DOCUMENTAL / Enviados');
					aonDocumental.setContentHTML('<iframe src="./indexNew.html" style="width:100%;height:100%;border:none;"></iframe>');
				}
			},
      {
				name: 'Nóminas Empleados',
				icon: 'folder',
				fn: () => {
					aonDocumental.setAttribute('title', 'DOCUMENTAL / Nóminas Empleados');
					aonDocumental.setContentHTML('<iframe src="./indexNew.html" style="width:100%;height:100%;border:none;"></iframe>');
				}
			}
		];
		aonDocumental.addSidenavOptions('OPCIONES', options);
	}
}
window.customElements.define('aon-documental', AonDocumental);
