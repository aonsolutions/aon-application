import {AonElement} from '../../components/AonElement.js';
import { getDayMonth } from '../../services/utils.js';
import {getMovements, getEmployee} from '../../services/service.js';
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
		this.aonComunica = this.getElement('aonComunica');
		this.aonComunicaEl = this.aonComunica.getParent();
	}

	connectedCallback () {
		this.paintView();
		this.init();
	}

	paintView(){
		this.innerHTML = `
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
		aonMovementTable.addColumn('Apellidos y nombre', 'string', 'nombres', '45%');
		aonMovementTable.addColumn('DNI/NIE', 'string', 'dni', '15%');
		aonMovementTable.addColumn('Movimiento', 'string', 'status', '25%');
		aonMovementTable.addColumn('Fecha', 'date', 'fecha', '15%');
		if(aonMovementTable) {
			this.aonComunica.startLoader();
			try {
				let resp = await getMovements(this.getFilter());
				aonMovementTable.removeRows();
				resp = resp.sort((a,b)=> new Date(b.fra) - new Date(a.fra));
				resp.map( res => {
					let {name:nombres, ipf, fra, situation} = res;
					const fecha = getDayMonth(fra);
					const date_now = new Date();
					const prev = new Date(fra).getTime() > date_now.getTime();
					let color = "#000";
					let tipo_mov = situation === "AL" ? "Alta" : "Baja";
					if(prev) {
						color = "#488601";
						tipo_mov =`${tipo_mov} previa`;
					} else if(this.aonComunicaEl.anularCondition(situation, fra)){
						color = "#CB8D00";
						tipo_mov =`${tipo_mov} Consolidada`;
					} else 
						tipo_mov = `${tipo_mov} Consolidada`;
					
					const status = `<span style="font-weight: 700;color: ${color};">${tipo_mov}</span>`;
					const dni = ipf.toString().substring(1);

					res = {
						...res,
						nombres,
						dni,
						fecha,
						status,
						prev
					};

					aonMovementTable.addRow(res, (el) => this.aonMovement(el, res));
				})
			} catch(e){
				const toast = this.getElement(`aonComunicaToast`);
				if("invalidCertificate" === e){
					if(toast) toast.start({ message: e, type: 'error' });
					this.getElement('aonComunicaSidenavCertificados').click()
				}
			}
			this.aonComunica.stopLoader();
		}
	}

	async aonMovement({target:el}, {regime, ctaCti, nss, prev, situation}) {
		let id = 'aonAltaDirecta';
		this.aonComunica.startLoader();
		this.aonComunica.setContentHTML(`<aon-alta-directa id="${id}"></aon-alta-directa>`);
		try {
			let resp = await getEmployee({regime, ctaCti, nss});
			if(resp){
				resp = {...resp, prev, situation};
				const aonAltaDirecta = this.getElement(id);
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
