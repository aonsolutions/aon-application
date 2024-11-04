import { AonElement } from "../../../components/AonElement.js";

import {
  getCertCorriente,
  getQuoteType,
  getReportAffiliateInAlta,
  getReportAffiliateInMovPrev,
  getCccForActivity,
  getIdcCcc,
} from "../../../services/service.js";
import { PAYROLL_VIEWS } from "../PayrollEnums.js";
import { CONSTANT } from "../../../environments/environments.js";
import { AonMobileList } from "../../../components/aon-mobile-list.js";
import { AonTable } from "../../../components/aon-table.js";
import { AonDateUtils } from "../../utils/AonDateUtils.js";


export class AonCtaList extends AonElement {
  TABLE_ID;
  static get observedAttributes() {
    return [CONSTANT.FILTER];
  }

  get filter() {
    return this.getAttribute(CONSTANT.FILTER);
  }

  set filter(filter) {
    this.setAttribute(CONSTANT.FILTER, filter);
  }

  attributeChangedCallback(name, oldValue, newValue) {
    if (CONSTANT.FILTER === name) this.build();
  }

  constructor() {
    super();
    this.id = this.id || PAYROLL_VIEWS.AON_CTA_LIST;
    this.TABLE_ID = this.id + "Table";
  }

  connectedCallback() {
    this.build();
  }

  async build() {
    this.paintView();
    this.getApplication().startLoader();
    if (this.isMobile()) await this.getTableMobile();
    else await this.getTableDesk();
    this.getApplication().stopLoader();
  }
  
  paintView() {
    this.getApplication().removeToolbarOptions();        
    let aonTable = this.isMobile() ? new AonMobileList() : new AonTable();
    aonTable.id = this.TABLE_ID;
    this.appendChild(aonTable);
  }

  async getTableDesk() {
    const aonTable = this.getElement(this.TABLE_ID);
    if (aonTable) {
      aonTable.addColumn("Tipo", "string", "tipo", "20%");
      aonTable.addColumn("Cuenta de cotización", "string", "ccc", "40%");
      aonTable.addColumn("Provincia", "string", "geozone", "35%");
      aonTable.addColumn("Opción", "fn", "option", "5%");
      try {
        const resp = await this.getData();
        aonTable.removeRows();
        resp.map((res) => {
          aonTable.addRow({
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
    const aonTable = this.getElement(this.TABLE_ID);
    if (aonTable) {
      aonTable.createAonDialog();
      try {
        const resp = await this.getData();
        aonTable.removeAllLi();
        resp.map((res, idx) => {
          aonTable.addLi(
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
        name: "Cert. Estar al corriente S.S",
        title: "Cert. Estar al corriente S.S",
        permission: true,
        aonIcon: "aon_seg_social",
        fn: () => this.getCertCorriente(res),
      },
      {
        name: "Trabajadores en alta",
        title: "Trabajadores en alta",
        permission: true,
        aonIcon: "aon_seg_social",
        fn: () => this.getReportAffiliateInAlta(res),
      },
      {
        name: "Movimientos previos",
        title: "Movimientos previos",
        permission: true,
        aonIcon: "aon_seg_social",
        fn: () => this.getReportAffiliateInMovPrev(res),
      },
      {
        name: "IDC",
        title: "IDC",
        permission: true,
        aonIcon: "aon_seg_social",
        fn: () => this.getIdcCcc(res),
      },
    ];
  }

  async getData() {
    let data = [];
    try {
      const resp = await getCccForActivity();
      if(resp && resp.cccs){
          for (const key in resp.cccs) {
            let ctaCti = resp.cccs[key];
            ctaCti["tipo"] = await this.getTipo(ctaCti.type);
            data.push(ctaCti);
          }
      }
    } catch (e) {
      console.log(e);
    }
    return data;
  }

  async getTipo(data) {
    const { name } = await getQuoteType(data);
    return name;
  }

  async getCertCorriente(data) {
    this.getApplication().startLoading();
    try {
      const { ccc, cccRegimeCode: regime } = data;
      await getCertCorriente({ ccc, regime }); // open pdf
    } catch (error) {
      this.showToast(error);
		}
    this.getApplication().stopLoading();
  }

  async getReportAffiliateInAlta(data) {
    this.getApplication().startLoading();
    try {
      const { ccc, cccRegimeCode: regime } = data;
      await getReportAffiliateInAlta({ ccc, regime }); // open pdf
    } catch (error) {
      this.showToast(error);
		}
    this.getApplication().stopLoading();
  }

  async getReportAffiliateInMovPrev(data) {
    this.getApplication().startLoading();
    try {
      const { ccc, cccRegimeCode: regime } = data;
      await getReportAffiliateInMovPrev({ ccc, regime }); // open pdf
    } catch (error) {
      this.showToast(error);
		}
    this.getApplication().stopLoading();
  }
  
  async getIdcCcc(data) {
    this.getApplication().startLoading();
    try {
      const { ccc, cccRegimeCode: regime } = data;
      const fecha = AonDateUtils.formatDateOrigin( new Date());
      await getIdcCcc({ ccc, regime, fecha }); // open pdf
    } catch (error) {
      this.showToast(error);
		}
    this.getApplication().stopLoading();
  }
}
window.customElements.define("aon-cta-list", AonCtaList);
