import {AonElement} from '../../components/AonElement.js';
import {getMovements} from '../../services/service.js';
import '../../components/aon-table.js';

export class AonMovementsList extends AonElement {

	static get observedAttributes() {
		return ['filter'];
	}

	get filter() {
		return this.getAttribute('filter');
	}

	set filter(filter) {
		this.setAttribute('filter', filter);
	}

	attributeChangedCallback(name, oldValue, newValue) {
		if('filter' === name) this.init();
	}

	constructor () {
		super();
	}

	connectedCallback () {
		this.innerHTML = `
			<aon-table id='aonMovementTable'></aon-table>
		`;
		this.build();
 	}

 	build() {
		let aonMovementTable = this.getElement('aonMovementTable');
		aonMovementTable.addColumn('Nombre', 'string', 'name');
		aonMovementTable.addColumn('Apellidos', 'string', 'last_name');
		aonMovementTable.addColumn('DNI/NIE', 'string', 'dni');
		aonMovementTable.addColumn('Movimiento', 'string', 'mov');
		aonMovementTable.addColumn('Número de afiliación', 'string', 'naf');
		aonMovementTable.addColumn('Tipo de contrato', 'string', 'tipo_contrato');
		aonMovementTable.addColumn('Fecha', 'date', 'fecha');
		this.init();
	}

	init() {
		let aonMovementTable = this.getElement('aonMovementTable');
		if(aonMovementTable) {
			getMovements(this.getFilter()).then(Movements => {
				aonMovementTable.removeRows();
				Movements.forEach((dato, i) => {
					aonMovementTable.addRow(dato, () => this.aonMovement(dato));
				});
			});
		}
	}

	aonMovement(dato) {
		console.log(dato);
	}

	getFilter() {
		return JSON.parse(this.getAttribute('filter'))
	}

	setFilter(filter) {
		return this.setAttribute('filter', JSON.stringify(filter));
	}
}
window.customElements.define('aon-movements-list', AonMovementsList);
