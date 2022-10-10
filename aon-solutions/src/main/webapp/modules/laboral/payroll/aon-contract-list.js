import { AonElement } from "../../../components/AonElement.js";
import { getContracts, getContractSepe } from "../../../services/service.js";
import { isEmptyObject, sortBy } from "../../../services/utils.js";
import { CONTRACT_OPTIONS, PAYROLL_VIEWS } from "../PayrollEnums.js";
import { CONSTANT, EVENT, MATERIAL_ICONS, MSG } from "../../../environments/environments.js";
import { AonMobileList } from "../../../components/aon-mobile-list.js";
import { AonTable } from "../../../components/aon-table.js";
import { AonDateUtils } from "../../utils/AonDateUtils.js";
import { AonSwitch } from "../../../components/aon-switch.js";

export class AonContractList extends AonElement {
  TABLE_ID;
  searchFilter;
  _list;
  _filter;
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
    this.id = this.id || PAYROLL_VIEWS.AON_CONTRACT_LIST;
    this.TABLE_ID = this.id + "Table";
    this.applicationEl = this.getApplication();
    this.applicationParentEl = this.getApplicationParent();
    this._filter = {
      contractAll: false 
    };
  }

  connectedCallback() {
    this.paintView();
    this.build();
  }

  paintView() {
    let aonTable = this.isMobile() ? new AonMobileList() : new AonTable();
    aonTable.id = this.TABLE_ID;
    this.appendChild(aonTable);
  }

  build() {
    this.buildToobar();
    this.getTable();
  }

  buildToobar() {
    this.applicationEl.removeToolbarOptions();
    this.applicationEl.addToolbarTitle("Contratos");
    this.buildToolbarSearch();
    
  }

  buildToolbarSearch(){
    const btnSearch = this.applicationEl.addSearchOption();
    btnSearch.addEventListener(EVENT.SEARCH, ({detail}) => this.search(detail));
    btnSearch.addEventListener(EVENT.SEARCH_NEW, ({detail:{contractAll, search}})=>{
      this._list = [];
      this._filter.contractAll = contractAll;
      this.search(search);
    });

    btnSearch.buildOptionsFilter([{
      type: CONSTANT.HTML_ELEMENT,
      element: new AonSwitch(),
      id: "aonSwitchFilter",
      name:"contractAll",
      title:"Todos los contratos",
      checked:false
    }]);
  }


  async getTable(){
    this.applicationEl.startLoader();
    if (this.isMobile()) await this.getTableMobile();
    else await this.getTableDesk();
    this.applicationEl.stopLoader();
  }


  async getTableDesk() {
    const aonTable = this.getElement(this.TABLE_ID);
    if (aonTable) {
      aonTable.removeColumns();
      aonTable.addColumn(MSG.NAME, "string", "name", "20%");
      aonTable.addColumn("DNI/NIE", "string", "ipf", "10%");
      aonTable.addColumn("Nª SS", "string", "naf", "10%");
      aonTable.addColumn("Tipo contrato", "string", "contractType", "10%");
      aonTable.addColumn(MSG.WORKPLACE, "string", "workplaceName", "10%");
      // aonTable.addColumn(MSG.CATEGORY, "string", "agreementCategory", "10%");
      aonTable.addColumn(MSG.START_DATE, "date", "startDateParse", "10%");
      aonTable.addColumn(MSG.END_DATE, "date", "endDateParse", "10%");
      aonTable.addColumn("Opción", "fn", "option", "5%");
      try {
        const resp = await this.getData();
        aonTable.removeRows();
        resp.map((res) => {
          let options = { ...res}
          if (res.contractType) options.option = this.getOptions(res);
          aonTable.addRow(options);
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
          let options = {
            icon: MATERIAL_ICONS.ASSIGNMENT,
            title: res.name,
            subtitle: `(${res.ipf}) ${res.startDateParse}`,
          };
          if (res.contractType) options.option = this.getOptions(res);
          aonTable.addLi(options, idx);
        });
      } catch (e) {
        console.log(e);
      }
    }
  }

  getOptions(res) {
    let options = [];
    const prev = new Date(res.startDate).getTime() > new Date().getTime();
    
    if(!prev){
      options.push({
        ...CONTRACT_OPTIONS.CONTRACT,
        fn: (el) => this.getContratoPdf(res, el),
      });
    }

    options.push({
      ...CONTRACT_OPTIONS.TA,
      fn: (el) => this.applicationParentEl.getTa({ regime:res.regime, ctaCti: res.ctaCti, nss:res.naf, fra:res.startDate, frb:res.endDate }, el)
    });
    
    if(!prev){
      options.push({
        ...CONTRACT_OPTIONS.IDC,
        fn: (el) => this.applicationParentEl.getIdc({ regime:res.regime, ctaCti: res.ctaCti, nss:res.naf, fra:res.startDate }, el)
      });
    }

    return options;
  }

  async getData() {
    let data = [];
    try {
      if(this.searchFilter && !isEmptyObject(this._list)){
        data = this._list.filter(({name, ipf, naf})=> this.includeSearch(name) || this.includeSearch(ipf) || this.includeSearch(naf));
      } else {
        const contracts = await getContracts(this._filter);
        data = sortBy(contracts, 'startDate', 'desc').map(
          (res) => {
            res.contractType = Number.parseInt(res.contractType);
            let startDate = AonDateUtils.formatDateOrigin(res.startDate);
            let startDateParse = AonDateUtils.formatDate(res.startDate);
            let endDateParse = "";
            if(res.endDate) endDateParse = AonDateUtils.formatDate(res.endDate);
            return {...res, startDate, startDateParse, endDateParse};
          }
        );
        this._list = data;
      }
    } catch (e) {
      console.log(e);
    }
    return data;
  }

  search(detail){
    this.searchFilter = detail;
    this.getTable();
  }

  includeSearch(str){
    return this.searchFilter && str && str.toLowerCase().includes(this.searchFilter.toLowerCase());
  }

  async getContratoPdf(data) {
    this.applicationEl.startLoading();
    try {
      const { ipf, startDate } = data;
      await getContractSepe({ ipf, startDate });
    } catch (error) {
      this.showToast(error);
		}
    this.applicationEl.stopLoading();
  }
}
window.customElements.define("aon-contract-list", AonContractList);