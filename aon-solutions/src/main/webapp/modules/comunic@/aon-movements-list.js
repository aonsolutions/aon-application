import {AonElement} from '../../components/AonElement.js';
import {getMovements} from '../../services/service.js';
import '../../components/aon-table.js';
import '../../components/aon-toast.js';


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
			<aon-toast id="divToast"></aon-toast>
			<aon-table id='aonMovementTable'></aon-table>
			<aon-dialog id="aonDialogAddOption" type="menu"></aon-dialog>
		`;
		this.build();
 	}

 	build() {
		let aonMovementTable = this.getElement('aonMovementTable');
		aonMovementTable.addColumn('Nombre', 'string', 'nombre');
		aonMovementTable.addColumn('Apellidos', 'string', 'last_name');
		aonMovementTable.addColumn('DNI/NIE', 'string', 'dni');
		aonMovementTable.addColumn('Movimiento', 'string', 'mov');
		aonMovementTable.addColumn('T. contrato', 'string', 'tipo_contrato');
		aonMovementTable.addColumn('Fecha', 'date', 'fecha');
		this.init();
	}

	init() {
		let aonMovementTable = this.getElement('aonMovementTable');
		if(aonMovementTable) {
			getMovements(this.getFilter()).then(Movements => {
				aonMovementTable.removeRows();
				Movements.forEach((data, i) => {
					aonMovementTable.addRow(data, (el) => this.aonMovement(el, data));
				});
			});
		}
	}

	aonMovement({target:el}, data) {
		const top  = el.getBoundingClientRect().top;
		const left = el.getBoundingClientRect().left;
		let d = this.getElement('aonDialogAddOption');

		let options = [{
				name: 'Anular',
				icon: 'delete',
				fn: () => this.deleteMov(data, el)
			}];
		d.setMenuOptions(options, top, left);
		d.open();
	}

	deleteMov(data, el){
		if (confirm(`Estas seguro de anular ${data.mov} de ${data.nombre} ?`)) {
			el.parentNode.remove(); //delete parent Node
			let toast = this.getElement(`divToast`);
			toast.start({message:'Alta eliminada!', type: 'error'});
			console.log("eliminar movimiento>>", data);
		}	
	}

	getFilter() {
		return JSON.parse(this.getAttribute('filter'))
	}

	setFilter(filter) {
		return this.setAttribute('filter', JSON.stringify(filter));
	}
}
window.customElements.define('aon-movements-list', AonMovementsList);
