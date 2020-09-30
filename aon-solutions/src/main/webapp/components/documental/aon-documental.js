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

				}
			},
      {
				name: 'A Contabilizar',
				icon: 'folder',
				fn: () => {

				}
			},
      {
				name: 'Banco',
				icon: 'folder',
				fn: () => {

				}
			},
      {
				name: 'Contable',
				icon: 'folder',
				fn: () => {

				}
			},
      {
				name: 'Fiscal',
				icon: 'folder',
				fn: () => {

				}
			},
      {
				name: 'Jurídico',
				icon: 'folder',
				fn: () => {

				}
			},
      {
				name: 'Seguros',
				icon: 'folder',
				fn: () => {

				}
			},
      {
				name: 'Enviados',
				icon: 'folder',
				fn: () => {

				}
			},
      {
				name: 'Nóminas Empleados',
				icon: 'folder',
				fn: () => {

				}
			}
		];
		aonDocumental.addSidenavOptions('OPCIONES', options);
	}
}
window.customElements.define('aon-documental', AonDocumental);
