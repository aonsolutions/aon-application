import { AonElement } from "../../../components/AonElement.js";
import {getContracts} from "../../../services/service.js";
import { setDate } from "../../../services/utils.js";
import "../../../components/aon-table.js";
import "../../../components/aon-mobile-list.js";
import { CONTRACT_OPTIONS, PAYROLL_VIEWS } from "../PayrollEnums.js";

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
    this.id = this.id || PAYROLL_VIEWS.AON_CONTRACT_LIST;
    this.TABLE_ID = this.id + "Table";
    this.applicationEl = this.getApplication();
    this.applicationParentEl = this.getApplicationParent();
    this.applicationToolbarEl = this.getElement(this.applicationEl.TOOLBAR);
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
    this.applicationEl.removeToolbarOptions();
    this.applicationToolbarEl.setAttribute("option", "Contratos");
    this.applicationEl.startLoader();
    if (this.isMobile()) await this.getTableMobile();
    else await this.getTableDesk();
    this.applicationEl.stopLoader();
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
        ...CONTRACT_OPTIONS.CONTRACT,
        fn: (el) => this.applicationParentEl.getContratoPdf(res, el),
      },
      {
        ...CONTRACT_OPTIONS.TA,
        fn: (el) => this.applicationParentEl.getTa({ regime:res.regime, ctaCti: res.ctaCti, nss:res.ssNumber, fra:res.startDate }, el)
      },
			{
        ...CONTRACT_OPTIONS.IDC,
				fn: (el) => this.applicationParentEl.getIdc({ regime:res.regime, ctaCti: res.ctaCti, nss:res.ssNumber, fra:res.startDate }, el)
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
    return data;
  }
}
window.customElements.define("aon-contract-list", AonContractList);