import { AonElement } from '../../components/AonElement.js';
import { startModule } from '../../services/gwtLoader.js';
import { getDomainUserRoles, getIDC, getTA, postDeleteMov } from '../../services/service.js';
import { addDays } from '../../services/utils.js';
import { DomainUserRoles } from '../../models/DomainUserRoles.js';
import './aon-movements.js';
import '../../components/aon-toast.js';
import '../../components/aon-application.js';
import './cta/aon-cta-list.js';
import './contrato/aon-contrato-list.js';


export class AonComunica extends AonElement {
	_roles;
	AON_COMUNICA;
	MOVEMENTS;
	constructor() {
		super();
	}
	
	connectedCallback() {
		this.initialize();
		getDomainUserRoles({reload:true}).then(r=>{
			this._roles = new DomainUserRoles(r);
			this.build();
		})
	}
	disconnectedCallback() {}

	initialize(){
		this.AON_COMUNICA = 'aonComunica';
		this.MOVEMENTS = this.AON_COMUNICA + 'Movements';
	}
	

	build() {
		this.paintView();
		if(!this.isEmployee()){
			this.aonComunicaEl = this.getElement(this.AON_COMUNICA);
			this.buildToolbar();
			if(this.isMobile()) this.painViewContract();
			else this.paintViewCtz();
		}
	}

	paintView() {
		this.innerHTML = `
			<aon-toast id="${this.AON_COMUNICA}Toast"></aon-toast>
			<aon-application id="${this.AON_COMUNICA}" title="COMUNIC@"></aon-application>
		`;
	}

	buildToolbar(){
		const options = [
			{
				name: 'Contratos',
				aonIcon: {
					icon: 'contract',
					color: 'black'
				},
				fn: () => this.painViewContract()
			},
			{
				name: 'Partes IT',
				icon: 'local_hospital',
				fn: () => {
					this.aonComunicaEl.removeToolbarOptions();
					startModule('aon_gwt_payroll', 'MainIT', this.aonComunicaEl.CONTENT);
				}
			},
			{
				name: 'CCC',
				icon: 'account_balance',
				fn: () => this.paintViewCtz()
			},
			{
				name: 'Certificados',
				aonIcon: {
					icon: 'cert',
					color: 'black'
				},
				fn: () => {
					this.aonComunicaEl.removeToolbarOptions();
					startModule('aon_gwt_payroll', 'MainDigitalCertificates', this.aonComunicaEl.CONTENT);
				}
			},
			{
				name: 'Movimientos',
				aonIcon: {
					icon: 'aon_seg_social',
					color: 'black'
				},
				fn: () => this.aonComunicaEl.setContentHTML(`<aon-movements id="${this.MOVEMENTS}" ></aon-movements>`)
			}
		];
		if(this.isMobile() || (this._roles.isComunicaPortal() && !this._roles.isComunicaManager())){
			delete options[1]; 
			delete options[3]; 
		}

		this.aonComunicaEl.addSidenavOptions('TGSS/SEPE', options);
	}


	painViewContract(){
		this.aonComunicaEl.removeToolbarOptions();
		if(this.isMobile())
			this.aonComunicaEl.setContentHTML(`<aon-contrato-list id="aonContratoList" ></aon-contrato-list>`)
		else {
			startModule('aon_gwt_payroll', 'MainContrata', this.aonComunicaEl.CONTENT);
		}
	}

	paintViewCtz(){
		this.getElement(this.aonComunicaEl.TOOLBAR).setAttribute('option', 'Cuentas de cotización');
		this.aonComunicaEl.setContentHTML(`<aon-cta-list id="aonCtaList" ></aon-cta-list>`)
	}

	async deleteMov(data, el) {
		if (confirm(`Estas seguro de anular el movimiento de ${data.name} ?`)) {
			const toast = this.getElement(`${this.AON_COMUNICA}Toast`);
			this.aonComunicaEl.startLoader();
			try {
				await postDeleteMov(data);
				toast.start({ message: `${data.situation == "AL" ? "Alta" : "Baja"} eliminada!` });
				if (el) el.remove(); //delete td
			} catch (error) {
				toast.start({ message: error, type: 'error' });
			}
			this.aonComunicaEl.stopLoader();
		}
	}

	async getTa(data, el) {
		this.aonComunicaEl.startLoading();
		const { regime, ctaCti, nss, fra } = data;
		await getTA({ regime, ctaCti, nss, fra }); // open pdf
		this.aonComunicaEl.stopLoading();
	}

	async getIdc(data, el) {
		this.aonComunicaEl.startLoading();
		const { regime, ctaCti, nss, fra } = data;
		await getIDC({ regime, ctaCti, nss, fra }); // open pdf
		this.aonComunicaEl.stopLoading();
	}

	anularCondition(situation, fra) {
		const date_prev = addDays(new Date(), -2);
		// const sit = ["AL", "BJ", "BAJA", "ALTA"];
		// (situation.indexOf(sit) > -1) &&
		return (date_prev.getTime() <= new Date(fra).getTime());
	}

	getOptions(res) {
		let option = [
			{
				id: 'Ta',
				name: 'Obtener TA',
				aonIcon: 'aon_ta',
				fn: (el) => this.getTa(res, el)
			},
			{
				id: 'Idc',
				name: 'Obtener IDC',
				aonIcon: 'aon_idc',
				fn: (el) => this.getIdc(res, el)
			}
		];
		if (this.anularCondition(res.situation, res.fra)) {
			option.push({
				id: 'Delete',
				name: 'Anular',
				icon: 'delete_forever',
				fn: (el) => this.deleteMov(res, el)
			});
		}
		return option;
	}

	isEmployee(){
		return !this._roles.isComunicaManager() && !this._roles.isComunicaPortal();
	}
}
window.customElements.define('aon-comunica', AonComunica);