import {AonElement} from '../../components/AonElement.js';
import { getDayMonth } from '../../services/utils.js';
import {getMovements, getEmployee} from '../../services/service.js';
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
		this.aonComunica = this.getElement('aonComunica');
		this.aonComunicaEl = this.aonComunica.getParent();
	}

	connectedCallback () {
		this.paintView();
		this.init();
	}

	paintView(){
		this.innerHTML = `
			<aon-toast id="divToast"></aon-toast>
			<aon-table id='aonMovementTable'></aon-table>
		`;
	}

	disconnectedCallback(){
		if(this.aonComunica) this.aonComunica.removeFloatOption();
	}

 	init() {
		this.getTable();
	}

	async getTable() {
		let aonMovementTable = this.getElement('aonMovementTable');
		aonMovementTable.addColumn('Apellidos y nombre', 'string', 'nombres', '40%');
		aonMovementTable.addColumn('DNI/NIE', 'string', 'dni', '15%');
		aonMovementTable.addColumn('Movimiento', 'string', 'status', '20%');
		aonMovementTable.addColumn('Fecha', 'date', 'fecha', '15%');
		aonMovementTable.addColumn('Opción', 'fn', 'option', '15%');
		if(aonMovementTable) {
			this.aonComunica.startLoader();
			try {
				let resp = await getMovements(this.getFilter());
				aonMovementTable.removeRows();
				resp = resp.sort((a,b)=> new Date(b.fra) - new Date(a.fra));
				resp.map( res => {
					let {name:nombres, ipf, fra, situation} = res;
					let fecha = getDayMonth(fra);
					let date_now = new Date();
					let prev = new Date(fra).getTime() > date_now.getTime();
					let status = prev ?
					`<span style="font-weight: 700;">${situation==="AL" ? "Alta" : "Baja"} Previa</span> ` :
					`<span style="font-weight: 700;color: #B32000;">${situation==="AL" ? "Alta" : "Baja"} Consolidada</span>`;
					let dni = ipf.toString().substring(1);
					let option = [
						{
							name:'Obtener TA',
							icon:'print',
							fn: (el) => this.aonComunicaEl.getTa(res, el)
						},
						{
							name:'Obtener IDC',
							icon:'print',
							fn: (el) => this.aonComunicaEl.getIdc(res, el)
						}
					];
					if( this.aonComunicaEl.anularCondition(situation, fra)  ) {
						option.push({
							name:'Anular',
							icon:'delete_forever',
							fn: (el) => this.aonComunicaEl.deleteMov(res, el)
						});
					}
					res = {
						...res,
						nombres,
						dni,
						fecha,
						status,
						prev,
						option
					};

					aonMovementTable.addRow(res, (el) => this.aonMovement(el, res));
				})
			} catch(e){
				console.log(e);
			}
			this.aonComunica.stopLoader();
		}
	}

	async aonMovement({target:el}, data) {
		let id = 'aonAltaDirecta';
		this.aonComunica.startLoader();
		this.aonComunica.setContentHTML(`<aon-alta-directa id="${id}"></aon-alta-directa>`);
		try {
			const resp = await getEmployee(data);
			if(resp){
				let aonAltaDirecta = this.getElement(id);
				if(aonAltaDirecta){
					//DISABLED FORMS
					aonAltaDirecta.disabledForm(`${id}EmpresaCard`);
					aonAltaDirecta.disabledForm(`${id}TrabajadorCard`, 'aon-switch');
					//parseData
					aonAltaDirecta.data = resp;
				}
			}
		} catch(error){}
		this.aonComunica.stopLoader();
	}

	getFilter = () => JSON.parse(this.getAttribute('filter'));

	setFilter = (filter) => this.setAttribute('filter', JSON.stringify(filter));
}
window.customElements.define('aon-movements-list', AonMovementsList);
