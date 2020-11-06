import {AonElement} from '../../components/AonElement.js';
import {startModule} from '../../services/gwtLoader.js';

export class AonContrata extends AonElement {

	AON_CONTRATA;

	constructor () {
		super();
		this.AON_CONTRATA = 'aonContrata';
	}

	connectedCallback () {
		this.innerHTML = `
			<aon-application id="${this.AON_CONTRATA}" title="CONTRAT@"></aon-application>
		`;
    this.build();
 	}

 	build() {
		let aonContrata = this.getElement(this.AON_CONTRATA);

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
				fn: () => startModule('aon_gwt_payroll', 'MainContrata', 'aonContrataContent')
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
