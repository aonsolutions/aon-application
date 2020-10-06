class AonContrata extends HTMLElement {

	constructor () {
		super();
	}

	connectedCallback () {
		this.innerHTML = `
			<aon-application id="aonContrata" title="CONTRAT@"></aon-application>
		`;
    this.build();
 	}

 	build() {
		let aonContrata = document.getElementById('aonContrata');

		let options = [
			{
				name: 'Empleados',
				icon: 'people',
				fn: () => alert('Empleados!!')
			},
			{
				name: 'Contratos',
				aonIcon: {
					icon: 'contract',
					color: 'black'
				},
				fn: () => aonContrata.setContentHTML('<div> Contratos!! </div>')
			},
			{
				name: 'Movimientos',
				icon: 'repeat',
				fn: () => alert('Movimientos!!')
			},
			{
				name: 'Partes IT',
				icon: 'table_chart',
				fn: () => alert('Partes IT!!')
			},
			{
				name: 'Informes y Certificados',
				aonIcon: {
					icon: 'cert',
					color: 'black'
				},
				fn: () => alert('Informes y Certificados!!')
			}
		];
		aonContrata.addSidenavOptions('OPCIONES', options);
	}
}
window.customElements.define('aon-contrata', AonContrata);
