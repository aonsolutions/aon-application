import {AonElement} from '../../components/AonElement.js';
import {getMovements, getIDC, getTA} from '../../services/service.js';
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
		`;
		this.build();
	}
	 


 	build() {
		let aonMovementTable = this.getElement('aonMovementTable');
		aonMovementTable.addColumn('Apellidos y nombre', 'string', 'nombres');
		aonMovementTable.addColumn('DNI/NIE', 'string', 'dni');
		aonMovementTable.addColumn('Fecha', 'date', 'fecha');
		aonMovementTable.addColumn('Opción', 'fn', 'option');
		this.getTable();
	}

	async getTable() {
		let aonMovementTable = this.getElement('aonMovementTable');
		if(aonMovementTable) {
			try{
				let resp = await getMovements(this.getFilter());
				aonMovementTable.removeRows();
				resp.map(resp=>{
					let {name:nombres, ipf, fra:fecha} = resp;
					// let identificacion = ipf.toString().substring(0,1);
					let dni = ipf.toString().substring(1);
					let data = {
						nombres,
						dni,
						fecha,
						option:[
							{
								name:'Obtener TA',
								icon:'print',
								fn: (el) => this.getTa(resp, el)
							},
							{
								name:'Obtener IDC',
								icon:'print',
								fn: (el) => this.getIdc(resp, el)
							},
							{
								name:'Anular',
								icon:'delete',
								fn: (el) => this.deleteMov(resp, el)
							},
						]
					};
					aonMovementTable.addRow(data, (el) => this.aonMovement(el, data));
				})
			} catch(e){
				console.log(e);
			}
		}
	}

	aonMovement({target:el}, data) {
		console.log(data);
	}

	deleteMov(data, el){
		if (confirm(`Estas seguro de anular el movimiento de ${data.nombres} ?`)) {
			el.remove(); //delete td
			let toast = this.getElement(`divToast`);
			toast.start({message:'Alta eliminada!', type: 'error'});
			console.log("eliminar movimiento>>", data);
		}	
	}

	getTa(data, el){
		getTA(data); // open pdf
	}

	getIdc(data, el) {
		getIDC(data); // open pdf
	}

	getFilter = () => JSON.parse(this.getAttribute('filter'));
	
	setFilter = (filter) => this.setAttribute('filter', JSON.stringify(filter));
}
window.customElements.define('aon-movements-list', AonMovementsList);
