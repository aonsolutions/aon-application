import { AonElement } from '../../components/AonElement.js';
import { startModule } from '../../services/gwtLoader.js';
import { getIDC, getTA, postDeleteMov } from '../../services/service.js';
import { addDays } from '../../services/utils.js';
import './aon-movements.js';
import '../../components/aon-toast.js';
import '../../components/aon-application.js';
import './cta/aon-cta-list.js';

export class AonComunica extends AonElement {

	AON_COMUNICA;
	MOVEMENTS;
	constructor() {
		super();
		this.AON_COMUNICA = 'aonComunica';
		this.MOVEMENTS = this.AON_COMUNICA + 'Movements';
	}

	connectedCallback() {
		this.paintView();
		this.build()
	}
	paintView() {
		this.innerHTML = `
			<aon-toast id="${this.AON_COMUNICA}Toast"></aon-toast>
			<aon-application id="${this.AON_COMUNICA}" title="COMUNIC@"></aon-application>
		`;
	}
	build() {
		this.aonComunicaEl = this.getElement(this.AON_COMUNICA);

		this.buildToolbar();
		this.getElement(this.aonComunicaEl.TOOLBAR).setAttribute('option', 'Movimientos');
		this.aonComunicaEl.setContentHTML(`<aon-movements id="${this.MOVEMENTS}" ></aon-movements>`);
	}

	buildToolbar(){
		let options = [
			{
				name: 'Contratos',
				aonIcon: {
					icon: 'contract',
					color: 'black'
				},
				fn: () => {
					this.aonComunicaEl.removeToolbarOptions();
					startModule('aon_gwt_payroll', 'MainContrata', this.aonComunicaEl.CONTENT);
				}
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
				fn: () => {
					this.getElement(this.aonComunicaEl.TOOLBAR).setAttribute('option', 'Cuentas de cotización');
					this.aonComunicaEl.setContentHTML(`<aon-cta-list id="aonCtaList" ></aon-cta-list>`)
				}
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
				icon: 'repeat',
				fn: () => this.aonComunicaEl.setContentHTML(`<aon-movements id="${this.MOVEMENTS}" ></aon-movements>`)
			}
		];
		this.aonComunicaEl.addSidenavOptions('OPCIONES', options);
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

	getTa(data, el) {
		const { regime, ctaCti, nss, fra } = data;
		getTA({ regime, ctaCti, nss, fra }); // open pdf
	}

	getIdc(data, el) {
		const { regime, ctaCti, nss, fra } = data;
		getIDC({ regime, ctaCti, nss, fra }); // open pdf
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
}
window.customElements.define('aon-comunica', AonComunica);
// aonComunicaMovementsList