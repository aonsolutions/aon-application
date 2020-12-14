import { AonElement } from '../../../components/AonElement.js';
import { getCertCorriente, getWorkplaceCCCs, getTipoCtz } from '../../../services/service.js'
import '../../../components/aon-table.js';
import '../../../components/aon-mobile-list.js';

export class AonCtaList extends AonElement {
	ID;
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
		if ('filter' === name) this.build();
	}

	constructor() {
		super();
		this.aonComunica = this.getElement('aonComunica');
		this.aonComunicaEl = this.aonComunica.getParent();
		this.ID = "aonCtaTable";
	}

	connectedCallback() {
		this.paintView();
		this.build();
	}

	paintView() {
		if (this.isMobile())
			this.innerHTML = ` <aon-mobile-list id='${this.ID}' />`;
		else
			this.innerHTML = ` <aon-table id='${this.ID}' />`;
	}

	disconnectedCallback() {
		if (this.aonComunica) this.aonComunica.removeFloatOption();
	}

	async build() {
		this.aonComunica.startLoader();
		if (this.isMobile())
			await this.getTableMobile();
		else
			await this.getTableDesk();
		this.aonComunica.stopLoader();
	}

	async getTableDesk() {
		const aonCtaTable = this.getElement(this.ID);
		aonCtaTable.addColumn('Tipo', 'string', 'tipo', '20%');
		aonCtaTable.addColumn('Cuenta de cotización', 'string', 'ccc', '40%');
		aonCtaTable.addColumn('Provincia', 'string', 'geozone', '35%');
		aonCtaTable.addColumn('Opción', 'fn', 'option', '5%');
		if (aonCtaTable) {
			try {
				const resp = await this.getData();
				aonCtaTable.removeRows();
				resp.map(async(res) => {
					const tipo = await this.getTipo(res.type);
					aonCtaTable.addRow({
						...res,
						ccc: `${res.cccRegimeCode} - ${res.ccc}`,
						tipo,
						option: this.getOptions(res)
					});
				})
			} catch (e) {
				const toast = this.getElement(`aonComunicaToast`);
				if ("invalidCertificate" === e) {
					if (toast) toast.start({ message: e, type: 'error' });
					this.getElement('aonComunicaSidenavCertificados').click()
				}
			}
		}
	}

	async getTableMobile() {
		const aonCtaTable = this.getElement(this.ID);
		if (aonCtaTable) {
			aonCtaTable.createAonDialog();
			try {
				const resp = await this.getData();
				aonCtaTable.removeAllLi();
				resp.map((res, idx) => {
					aonCtaTable.addLi({
						icon:'assignment',
						title: `${res.cccRegimeCode} - ${res.ccc}`,
						subtitle: `${res.geozone}`,
						option: this.getOptions(res)
					}, idx);
				})
			} catch (e) {
				const toast = this.getElement(`aonComunicaToast`);
				if ("invalidCertificate" === e) {
					if (toast) toast.start({ message: e, type: 'error' });
					this.getElement('aonComunicaSidenavCertificados').click()
				}
			}
		}
	}

	getOptions(res){
		return [
			{
				name:'Certificado Corr.',
				icon:'print',
				fn: (el) =>this.getCertCorriente(res, el)
			}
		];
	}

	async getData() {
		const resp = await getWorkplaceCCCs();
		let cuentas = [];
		resp.map(({ccc}) => {
			ccc.map(c=>{
				cuentas.push(c);
			})
		})
		return cuentas;
	}

	async getTipo(data){
		const {name} = await getTipoCtz(data);
		return name;
	}

	getCertCorriente(data, el) {
		const { ccc, cccRegimeCode: regimen } = data;
		getCertCorriente({ ccc, regimen }); // open pdf
	}
}
window.customElements.define('aon-cta-list', AonCtaList);
