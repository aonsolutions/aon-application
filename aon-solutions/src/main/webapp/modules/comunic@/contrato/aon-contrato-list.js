import { AonElement } from '../../../components/AonElement.js';
import { getContracts, getContratoPdf, getCopyBasicPdf } from '../../../services/service.js';
import { setDate } from '../../../services/utils.js';
import '../../../components/aon-table.js';
import '../../../components/aon-mobile-list.js';

export class AonContratoList extends AonElement {
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
        this.aonComunicaToolbar = this.getElement('aonComunicaToolbar');
		this.aonComunicaEl = this.aonComunica.getParent();
		this.ID = "aonContratoTable";
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
        this.aonComunicaToolbar.setAttribute('option', 'Contratos');
		this.aonComunica.startLoader();
		if (this.isMobile())
			await this.getTableMobile();
		else
			await this.getTableDesk();
		this.aonComunica.stopLoader();
	}

	async getTableDesk() {
		const aonCtaTable = this.getElement(this.ID);
		// aonCtaTable.addColumn('Tipo', 'string', 'tipo', '20%');
		// aonCtaTable.addColumn('Cuenta de cotización', 'string', 'ccc', '40%');
		// aonCtaTable.addColumn('Provincia', 'string', 'geozone', '35%');
		// aonCtaTable.addColumn('Opción', 'fn', 'option', '5%');
		// if (aonCtaTable) {
		// 	try {
		// 		const resp = await this.getData();
		// 		aonCtaTable.removeRows();
		// 		resp.map(res => {
		// 			aonCtaTable.addRow({
		// 				...res,
		// 				ccc: `${res.cccRegimeCode} - ${res.ccc}`,
		// 				option: this.getOptions(res)
		// 			});
		// 		})
		// 	} catch (e) {
		// 		const toast = this.getElement(`aonComunicaToast`);
		// 		if ("invalidCertificate" === e) {
		// 			if (toast) toast.start({ message: e, type: 'error' });
		// 			this.getElement('aonComunicaSidenavCertificados').click()
		// 		}
		// 	}
		// }
	}

	async getTableMobile() {
		const aonCtaTable = this.getElement(this.ID);
		if (aonCtaTable) {
			aonCtaTable.createAonDialog();
			try {
				const resp = await this.getData();
				aonCtaTable.removeAllLi();
				resp.map((res, idx) => {
					let options = {
						icon: 'assignment',
						title: ` ${res.surName} ${res.name}`,
						subtitle: `(${res.document}) ${res.startDate}`
					};
					if(res.contractType) options.option = this.getOptions(res);
					aonCtaTable.addLi(options, idx);
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
				name:'Contrato',
				aonIcon: 'aon_cto',
				fn: (el) =>this.getContratoPdf(res, el)
            },
			{
				name:'Copia básica',
				aonIcon: 'aon_cbc',
				fn: (el) =>this.getCopyBasicPdf(res, el)
			}
		];
	}

	async getData() {
        let data = [];
        try {
            const contracts = await getContracts({allEmployees:false});
            contracts.map(({employeeInfo, contractInfo:{startDate, contractType}})=>{

				contractType = Number.parseInt(contractType);
				if(contractType) employeeInfo.contractType = contractType;

                if(startDate){
					employeeInfo.fecha = startDate;
					employeeInfo.startDate = setDate(startDate);
				} 

                data.push(employeeInfo);
            });
        } catch(e) {
            console.log(e);
        }

		return data;
	}

	getContratoPdf(data, el) {
		const { document:ipf, fecha } = data;
		getContratoPdf({ ipf, fecha });
    }

    getCopyBasicPdf(data, el) {
		const { document:ipf, fecha } = data;
		getCopyBasicPdf({ ipf, fecha });
	}
}
window.customElements.define('aon-contrato-list', AonContratoList);
