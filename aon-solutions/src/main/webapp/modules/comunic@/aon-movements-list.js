import {AonElement} from '../../components/AonElement.js';
import {setDate} from '../../services/utils.js';
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
				console.log(resp);
				resp.map(resp=>{
					let {name:nombres, ipf, fra} = resp;
					let fecha = setDate(fra);
					let now =  setDate(new Date());
					let status = fecha > now ? 'Previo'  : 'Consolidado';
					let dni = ipf.toString().substring(1);
					let data = {
						nombres,
						dni,
						fecha,
						status: `<b>${status}</b>`,
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
					let tr = aonMovementTable.addRow(data, (el) => this.aonMovement(el, data));	
					if(tr){
						if(fecha > now){ //prev
							
						} else {
							tr.style.backgroundColor = "#faca8f";
						}
					}
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
		if (confirm(`Estas seguro de anular el movimiento de ${data.name} ?`)) {
			let toast = this.getElement(`divToast`);
			try {
				data = {
					...data,
					regime: data.ctaCti,
					ctaCti: data.regime
				}
				await postDeleteMov(data);
				toast.start({message:'Alta eliminada!', type: 'success'});
				el.remove(); //delete td
			} catch (error) {
				toast.start({message:error, type: 'error'});
			}
		}	
	}

	getTa(data, el){
		data = {
			...data,
			regime: data.ctaCti,
			ctaCti: data.regime
		}
		getTA(data); // open pdf
	}

	getIdc(data, el) {
		data = {
			...data,
			regime: data.ctaCti,
			ctaCti: data.regime
		}
		getIDC(data); // open pdf
	}

	getFilter = () => JSON.parse(this.getAttribute('filter'));
	
	setFilter = (filter) => this.setAttribute('filter', JSON.stringify(filter));
}
window.customElements.define('aon-movements-list', AonMovementsList);
