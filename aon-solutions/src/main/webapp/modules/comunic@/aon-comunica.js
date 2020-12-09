import { AonElement } from '../../components/AonElement.js';
import { startModule } from '../../services/gwtLoader.js';
import { getIDC, getTA, postDeleteMov } from '../../services/service.js';
import { addDays } from '../../services/utils.js';
import './aon-movements.js';
import '../../components/aon-toast.js';
import '../../components/aon-application.js';
export class AonComunica extends AonElement {

	AON_COMUNICA;
	MOVEMENTS;

	constructor() {
		super();
		this.AON_COMUNICA = 'aonComunica';
		this.MOVEMENTS = this.AON_COMUNICA + 'Movements';
	}

	connectedCallback() {
		this.innerHTML = `
			<aon-toast id="${this.AON_COMUNICA}Toast"></aon-toast>
			<aon-application id="${this.AON_COMUNICA}" title="COMUNIC@"></aon-application>
		`;
		this.aonComunica = this.getElement(this.AON_COMUNICA);

		this.build()
	}

	build() {
		let options = [
			{
				name: 'Contratos',
				aonIcon: {
					icon: 'contract',
					color: 'black'
				},
				fn: () => {
					this.aonComunica.removeToolbarOptions();
					startModule('aon_gwt_payroll', 'MainContrata', this.aonComunica.CONTENT);
				}
			},
			{
				name: 'Partes IT',
				icon: 'local_hospital',
				fn: () => {
					this.aonComunica.removeToolbarOptions();
					startModule('aon_gwt_payroll', 'MainIT', this.aonComunica.CONTENT);
				}
			},
			{
				name: 'CCC',
				icon: 'account_balance',
				fn: () => {
					this.aonComunica.removeToolbarOptions();
					startModule('aon_gwt_payroll', 'MainCCC', this.aonComunica.CONTENT);
				}
			},
			{
				name: 'Certificados',
				aonIcon: {
					icon: 'cert',
					color: 'black'
				},
				fn: () => {
					this.aonComunica.removeToolbarOptions();
					startModule('aon_gwt_payroll', 'MainDigitalCertificates', this.aonComunica.CONTENT);
				}
			},
			{
				name: 'Movimientos',
				icon: 'repeat',
				fn: () => this.aonComunica.setContentHTML(`<aon-movements id="${this.MOVEMENTS}" ></aon-movements>`)
			}
		];
		this.aonComunica.addSidenavOptions('OPCIONES', options);

		//if(this.isMobile()){
		this.getElement(this.aonComunica.TOOLBAR).setAttribute('option', 'Movimientos');
		this.aonComunica.setContentHTML(`<aon-movements id="${this.MOVEMENTS}" ></aon-movements>`);
		//}
		//this.aonComunica.setContentHTML(`<aon-alta-directa></aon-alta-directa>`);
	}

	async deleteMov(data, el) {
		if (confirm(`Estas seguro de anular el movimiento de ${data.name} ?`)) {
			const toast = this.getElement(`${this.AON_COMUNICA}Toast`);
			this.aonComunica.startLoader();
			try {
				await postDeleteMov(data);
				toast.start({ message: `${data.situation == "AL" ? "Alta" : "Baja"} eliminada!` });
				if (el) el.remove(); //delete td
			} catch (error) {
				toast.start({ message: error, type: 'error' });
			}
			this.aonComunica.stopLoader();
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
}
window.customElements.define('aon-comunica', AonComunica);
// aonComunicaMovementsList