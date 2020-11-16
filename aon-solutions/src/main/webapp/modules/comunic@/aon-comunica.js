import {AonElement} from '../../components/AonElement.js';
import {startModule} from '../../services/gwtLoader.js';
import './aon-movements.js';
export class AonComunica extends AonElement {

	AON_COMUNICA;
	MOVEMENTS;

	constructor () {
		super();
		this.AON_COMUNICA = 'aonComunica';
		this.MOVEMENTS = this.AON_COMUNICA + 'Movements';
	}

	connectedCallback () {
		this.innerHTML = `
			<aon-application id="${this.AON_COMUNICA}" title="COMUNIC@"></aon-application>
		`;
    this.build();
 	}

 	build() {
		let aonComunica = this.getElement(this.AON_COMUNICA);

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
				fn: () => startModule('aon_gwt_payroll', 'MainContrata', aonComunica.CONTENT)
			},
			{
				name: 'Partes IT',
				icon: 'local_hospital',
				fn: () => startModule('aon_gwt_payroll', 'MainIT', aonComunica.CONTENT)
			},
			{
				name: 'CCC',
				icon: 'account_balance',
				fn: () => startModule('aon_gwt_payroll', 'MainCCC', aonComunica.CONTENT)
			},
			{
				name: 'Certificados',
				aonIcon: {
					icon: 'cert',
					color: 'black'
				},
				fn: () => startModule('aon_gwt_payroll', 'MainDigitalCertificates', aonComunica.CONTENT)
			},
			{
				name: 'Movimientos',
				icon: 'repeat',
				fn: () => aonComunica.setContentHTML(`<aon-movements id="${this.MOVEMENTS}" ></aon-movements>`)
			}
		];
		aonComunica.addSidenavOptions('OPCIONES', options);

		//if(this.isMobile()){
		aonComunica.setContentHTML(`<aon-movements id="${this.MOVEMENTS}" ></aon-movements>`);
		//}

		// aonComunica.setContentHTML(`<aon-alta-directa></aon-alta-directa>`);
		
	}
}
window.customElements.define('aon-comunica', AonComunica);
