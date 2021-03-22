import { AonElement } from "../../../components/AonElement.js";
import {getContracts} from "../../../services/service.js";
import { setDate } from "../../../services/utils.js";
import "../../../components/aon-table.js";
import "../../../components/aon-mobile-list.js";

export class AonContractList extends AonElement {
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
    this.id = this.id || "aonContractList";
    this.TABLE_ID = this.id + "Table";
    this.aonLaboral = this.getApplication();
    this.aonLaboralToolbar = this.getElement(this.aonLaboral.TOOLBAR);
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
    this.aonLaboralToolbar.setAttribute("option", "Contratos");
    this.aonLaboral.startLoader();
    if (this.isMobile()) await this.getTableMobile();
    else await this.getTableDesk();
    this.aonLaboral.stopLoader();
  }

  async getTableDesk() {}

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
        fn: (el) => this.aonLaboral.getParent().getContratoPdf(res, el),
      },
      {
        name: "Obtener TA",
        aonIcon: "aon_ta",
        fn: (el) => this.aonLaboral.getParent().getTa({ regime:res.regime, ctaCti: res.ctaCti, nss:res.ssNumber, fra:res.startDate }, el)
      },
			{
				name: 'Obtener IDC',
				aonIcon: 'aon_idc',
				fn: (el) => this.aonLaboral.getParent().getIdc({ regime:res.regime, ctaCti: res.ctaCti, nss:res.ssNumber, fra:res.startDate }, el)
			}
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
    data.push({
      contractType:"401",
      startDate: "2020-09-09",
      regime: "0111",
      ctaCti: "01105360062",
      surName: "VASQUEZ",
      name:"RAY",
      document: "Y7514970X",
      ssNumber:"291136796369"
    });

    return data;
  }
}
window.customElements.define("aon-contract-list", AonContractList);