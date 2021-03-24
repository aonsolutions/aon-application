import { AonElement } from "../../../../components/AonElement.js";
import {
  getCertCorriente,
  getWorkplaceCCCs,
  getTipoCtz,
} from "../../../../services/service.js";
import "../../../../components/aon-table.js";
import "../../../../components/aon-mobile-list.js";


export class AonCtaList extends AonElement {
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
    this.id = this.id || "aonCtaList";
    this.applicationEl = this.getApplication();
    this.TABLE_ID = this.id + "Table";
  }

  connectedCallback() {
    this.build();
  }

  async build() {
    this.paintView();
    this.applicationEl.startLoader();
    if (this.isMobile()) await this.getTableMobile();
    else await this.getTableDesk();
    this.applicationEl.stopLoader();
  }
  
  paintView() {
    this.applicationEl.removeToolbarOptions();
    if (this.isMobile())
      this.innerHTML = ` <aon-mobile-list id='${this.TABLE_ID}' />`;
    else this.innerHTML = ` <aon-table id='${this.TABLE_ID}' />`;
  }

  async getTableDesk() {
    const aonCtaTable = this.getElement(this.TABLE_ID);
    if (aonCtaTable) {
      aonCtaTable.addColumn("Tipo", "string", "tipo", "20%");
      aonCtaTable.addColumn("Cuenta de cotización", "string", "ccc", "40%");
      aonCtaTable.addColumn("Provincia", "string", "geozone", "35%");
      aonCtaTable.addColumn("Opción", "fn", "option", "5%");
      try {
        const resp = await this.getData();
        aonCtaTable.removeRows();
        resp.map((res) => {
          aonCtaTable.addRow({
            ...res,
            ccc: `${res.cccRegimeCode} - ${res.ccc}`,
            option: this.getOptions(res),
          });
        });
      } catch (e) {
        console.log(e);
      }
    }
  }

  async getTableMobile() {
    const aonCtaTable = this.getElement(this.TABLE_ID);
    if (aonCtaTable) {
      aonCtaTable.createAonDialog();
      try {
        const resp = await this.getData();
        aonCtaTable.removeAllLi();
        resp.map((res, idx) => {
          aonCtaTable.addLi(
            {
              icon: "assignment",
              title: `${res.cccRegimeCode} - ${res.ccc}`,
              subtitle: `(${res.tipo}) ${res.geozone}`,
              option: this.getOptions(res),
            },
            idx
          );
        });
      } catch (e) {
        console.log(e);
      }
    }
  }

  getOptions(res) {
    return [
      {
        name: "Cert. Corriente",
        icon: "print",
        fn: (el) => this.getCertCorriente(res, el),
      },
    ];
  }

  async getData() {
    let cuentas = [];
    try {
      const workplaces = await getWorkplaceCCCs();
      for (const workplace in workplaces) {
        const cccs = workplaces[workplace].ccc;
        if (cccs)
          for (const ccc in cccs) {
            let cuenta = cccs[ccc];
            let exists = cuentas.some(
              (el) =>
                el.ccc === cuenta.ccc &&
                el.cccRegimeCode === cuenta.cccRegimeCode
			);
            if (!exists) {
              cuenta["tipo"] = await this.getTipo(cuenta.type);
              cuentas.push(cuenta);
            }
          }
      }
    } catch (e) {
      console.log(e);
    }

    return cuentas;
  }

  async getTipo(data) {
    const { name } = await getTipoCtz(data);
    return name;
  }

  async getCertCorriente(data, el) {
    this.applicationEl.startLoading();
    try {
      const { ccc, cccRegimeCode: regimen } = data;
      await getCertCorriente({ ccc, regimen }); // open pdf
    } catch ({message, type}) {
			if(message && type) this.applicationEl.getToast().start({ message, type});
		}
    this.applicationEl.stopLoading();
  }
}
window.customElements.define("aon-cta-list", AonCtaList);
