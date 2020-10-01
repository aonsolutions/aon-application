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
				fn: () => alert('Empleados!!')
			},
			{
				name: 'Contratos',
				fn: () => aonContrata.setContentHTML('<div> Contratos!! </div>')
			},
			{
				name: 'Movimientos',
				fn: () => alert('Movimientos!!')
			},
			{
				name: 'Partes IT',
				fn: () => alert('Partes IT!!')
			},
			{
				name: 'Informes y Certificados',
				fn: () => alert('Informes y Certificados!!')
			}
		];
		aonContrata.addSidenavOptions('OPCIONES', options);
	}
}
window.customElements.define('aon-contrata', AonContrata);
