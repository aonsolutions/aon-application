import {AonElement} from '../../components/AonElement.js';
import {setDate, getDayMonth } from '../../services/utils.js';
import {getMovements, getIDC, getTA, postDeleteMov} from '../../services/service.js';
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
		aonMovementTable.addColumn('Movimiento', 'string', 'status');
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
				resp = resp.sort((a,b)=> new Date(b.fra) - new Date(a.fra));
				resp.map(res=>{
					let {name:nombres, ipf, fra, situation} = res;
					let fecha = getDayMonth(fra);
					let prev =  setDate(fra) > setDate(new Date()); // true == prev
					let status = prev ? 
					`<span style="font-weight: 700;">${situation==="AL" ? "Alta" : "Baja"} Previa</span> ` :
					`<span style="font-weight: 700;color: #B32000;">${situation==="AL" ? "Alta" : "Baja"} Consolidada</span>`;
					res = {...res, prev};
					let dni = ipf.toString().substring(1);
					let data = {
						...res,
						nombres,
						dni,
						fecha,
						status,
						prev,
						option:[
							{
								name:'Obtener TA',
								icon:'print',
								fn: (el) => this.getTa(res, el)
							},
							{
								name:'Obtener IDC',
								icon:'print',
								fn: (el) => this.getIdc(res, el)
							},
							{
								name:'Anular',
								icon:'delete',
								fn: (el) => this.deleteMov(res, el)
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

	async deleteMov(data, el){
		console.log(data);
		if (confirm(`Estas seguro de anular el movimiento de ${data.name} ?`)) {
			let toast = this.getElement(`divToast`);
			try {
				await postDeleteMov(data);
				toast.start({message:'Alta eliminada!', type: 'success'});
				el.remove(); //delete td
			} catch (error) {
				toast.start({message:error, type: 'error'});
			}
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
