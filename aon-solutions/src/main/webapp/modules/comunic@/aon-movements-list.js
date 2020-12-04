import {AonElement} from '../../components/AonElement.js';
import { getDayMonth, addDays } from '../../services/utils.js';
import {getMovements, getIDC, getTA, postDeleteMov, getEmployee} from '../../services/service.js';
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

	disconnectedCallback(){
		let aonComunica = this.getElement('aonComunica');
		if(aonComunica) aonComunica.removeFloatOption();
	}

 	build() {
		this.getTable();
	}

	async getTable() {
		let aonMovementTable = this.getElement('aonMovementTable');
		let aonComunica = this.getElement('aonComunica');
		aonMovementTable.addColumn('Apellidos y nombre', 'string', 'nombres', '40%');
		aonMovementTable.addColumn('DNI/NIE', 'string', 'dni', '15%');
		aonMovementTable.addColumn('Movimiento', 'string', 'status', '20%');
		aonMovementTable.addColumn('Fecha', 'date', 'fecha', '15%');
		aonMovementTable.addColumn('Opción', 'fn', 'option', '15%');
		if(aonMovementTable) {
			aonComunica.startLoader();
			try {
				let resp = await getMovements(this.getFilter());
				aonMovementTable.removeRows();
				resp = resp.sort((a,b)=> new Date(b.fra) - new Date(a.fra));
				resp.map( res => {
					let {name:nombres, ipf, fra, situation} = res;
					let fecha = getDayMonth(fra);
					let date_now = new Date();
					let date_prev = addDays(date_now, -2);
					let prev = new Date(fra).getTime() > date_now.getTime();
					let status = prev ?
					`<span style="font-weight: 700;">${situation==="AL" ? "Alta" : "Baja"} Previa</span> ` :
					`<span style="font-weight: 700;color: #B32000;">${situation==="AL" ? "Alta" : "Baja"} Consolidada</span>`;
					let dni = ipf.toString().substring(1);
					let option = [
						{
							name:'Obtener TA',
							icon:'print',
							fn: (el) => this.getTa(res, el)
						},
						{
							name:'Obtener IDC',
							icon:'print',
							fn: (el) => this.getIdc(res, el)
						}
					];
					if( ("AL" === situation || "BJ" === situation) && (date_prev.getTime() <= new Date(fra).getTime())  ) {
						option.push({
							name:'Anular',
							icon:'delete',
							fn: (el) => this.deleteMov(res, el)
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
			aonComunica.stopLoader();
		}
	}

	async aonMovement({target:el}, data) {
		let aonComunica = this.getElement('aonComunica');
		let id = 'aonAltaDirecta';
		aonComunica.startLoader();
		aonComunica.setContentHTML(`<aon-alta-directa id="${id}"></aon-alta-directa>`);
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
		aonComunica.stopLoader();
	}

	async deleteMov(data, el){
		if (confirm(`Estas seguro de anular el movimiento de ${data.name} ?`)) {
			let toast = this.getElement(`divToast`);
			let aonComunica = this.getElement('aonComunica');
			aonComunica.startLoader();
			try {
				await postDeleteMov(data);
				toast.start({message:`${data.situation == "AL" ? "Alta" : "Baja"} eliminada!`});
				el.remove(); //delete td
			} catch (error) {
				toast.start({message:error, type: 'error'});
			}
			aonComunica.stopLoader();
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
