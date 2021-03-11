import { AonElement } from "../../../components/AonElement.js";
import {
  getContracts,
  getContratoPdf
} from "../../../services/service.js";
import { setDate } from "../../../services/utils.js";
import "../../../components/aon-table.js";
import "../../../components/aon-mobile-list.js";

export class AonContratoList extends AonElement {
  TABLE_ID;
  static get observedAttributes() {
    return ["filter"];
  }

  get filter() {
    return this.getAttribute("filter");
  }

  set filter(filter) {
    this.setAttribute("filter", filter);
  }

  attributeChangedCallback(name, oldValue, newValue) {
    if ("filter" === name) this.build();
  }

  constructor() {
    super();
    this.aonComunica = this.getApplication();
    this.aonComunicaToolbar = this.getElement(this.aonComunica.TOOLBAR);
    this.TABLE_ID = "aonContratoTable";
  }

  connectedCallback() {
    this.paintView();
    this.build();
  }

  paintView() {
    if (this.isMobile())
      this.innerHTML = ` <aon-mobile-list id='${this.TABLE_ID}' />`;
    else this.innerHTML = ` <aon-table id='${this.TABLE_ID}' />`;
  }

  async build() {
    this.aonComunicaToolbar.setAttribute("option", "Contratos");
    this.aonComunica.startLoader();
    if (this.isMobile()) await this.getTableMobile();
    else await this.getTableDesk();
    this.aonComunica.stopLoader();
  }

  async getTableDesk() {
    // const aonCtaTable = this.getElement(this.TABLE_ID);
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
    const aonCtaTable = this.getElement(this.TABLE_ID);
    if (aonCtaTable) {
      aonCtaTable.createAonDialog();
      try {
        const resp = await this.getData();
        aonCtaTable.removeAllLi();
        resp.map((res, idx) => {
          let options = {
            icon: "assignment",
            title: ` ${res.surName} ${res.name}`,
            subtitle: `(${res.document}) ${setDate(res.startDate)}`,
          };
          if (res.contractType) options.option = this.getOptions(res);
          aonCtaTable.addLi(options, idx);
        });
      } catch (e) {
        console.log(e);
      }
    }
  }

  getOptions(res) {
    return [
      {
        name: "Contrato",
        aonIcon: "aon_cto",
        fn: (el) => this.getContratoPdf(res, el),
      },
      {
        name: "Obtener TA",
        aonIcon: "aon_ta",
        fn: (el) => this.aonComunica.getParent().getTa({ regime:res.regime, ctaCti: res.ctaCti, nss:res.ssNumber, fra:res.startDate }, el)
      },
    ];
  }

  async getData() {
    let data = [];
    try {
      const contracts = await getContracts({ allEmployees: false });
      contracts.map(
        ({ employeeInfo, contractInfo: { startDate, contractType, completeCCC } }) => {
          contractType = Number.parseInt(contractType);
          if (contractType) {
            employeeInfo.contractType = contractType;
          }
          if (startDate) {
            employeeInfo.startDate = startDate;
          }
          if(completeCCC){
            employeeInfo.regime = completeCCC.toString().substr(0,4);
            employeeInfo.ctaCti = completeCCC.toString().substr(4);
          } else {
            employeeInfo.contractType = undefined;
          }

          data.push(employeeInfo);
        }
      );
    } catch (e) {
      console.log(e);
    }
    
    //delete
    // data.push({
    //   contractType:"401",
    //   startDate: "2020-09-09",
    //   regime: "0111",
    //   ctaCti: "01105360062",
    //   surName: "VASQUEZ",
    //   name:"RAY",
    //   document: "Y7514970X",
    //   ssNumber:"291136796369"
    // });

    return data;
  }

  async getContratoPdf(data, el) {
    this.aonComunica.startLoading();
    const { document: ipf, startDate: fecha } = data;
    await getContratoPdf({ ipf, fecha });
    this.aonComunica.stopLoading();
  }
}
window.customElements.define("aon-contrato-list", AonContratoList);