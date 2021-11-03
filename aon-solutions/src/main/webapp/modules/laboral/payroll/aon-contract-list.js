import { AonElement } from "../../../components/AonElement.js";
import { getContracts } from "../../../services/service.js";
import { isEmptyObject, sortBy } from "../../../services/utils.js";
import { CONTRACT_OPTIONS, PAYROLL_VIEWS } from "../PayrollEnums.js";
import { CONSTANT, EVENT, MSG } from "../../../environments/environments.js";
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
    this._filter= {
      allEmployees: false 
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
    btnSearch.addEventListener(EVENT.SEARCH_VALUE, ({detail:{allEmployees, search}})=>{
      this._list = [];
      this._filter.allEmployees = allEmployees;
      this.search(search);
    });

    btnSearch.buildOptionsFilter([{
      type: CONSTANT.HTML_ELEMENT,
      element: new AonSwitch(),
      id: "aonSwitchFilter",
      name:"allEmployees",
      title:"Contratos inactivos",
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
      aonTable.addColumn("DNI/NIE", "string", "document", "10%");
      aonTable.addColumn("Nª SS", "string", "ssNumber", "10%");
      aonTable.addColumn("Tipo contrato", "string", "contractType", "10%");
      aonTable.addColumn("Centro trabajo", "string", "workplaceName", "10%");
      aonTable.addColumn(MSG.CATEGORY, "string", "agreementCategory", "10%");
      aonTable.addColumn(MSG.START_DATE, "date", "startDateParse", "10%");
      aonTable.addColumn(MSG.END_DATE, "date", "endDateParse", "10%");
      aonTable.addColumn("Opción", "fn", "option", "5%");
      try {
        const resp = await this.getData();
        aonTable.removeRows();
        resp.map((res) => {
          let options = { ...res, name:`${res.surName} ${res.name}`}
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
            icon: "assignment",
            title: ` ${res.surName} ${res.name}`,
            subtitle: `(${res.document}) ${AonDateUtils.setDate(res.startDateParse)}`,
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
        fn: (el) => this.applicationParentEl.getContratoPdf(res, el),
      });
    }

    options.push({
      ...CONTRACT_OPTIONS.TA,
      fn: (el) => this.applicationParentEl.getTa({ regime:res.regime, ctaCti: res.ctaCti, nss:res.ssNumber, fra:res.startDate }, el)
    });
    
    if(!prev){
      options.push({
        ...CONTRACT_OPTIONS.IDC,
        fn: (el) => this.applicationParentEl.getIdc({ regime:res.regime, ctaCti: res.ctaCti, nss:res.ssNumber, fra:res.startDate }, el)
      });
    }

    return options;
  }

  async getData() {
    let data = [];
    try {
      if(this.searchFilter && !isEmptyObject(this._list)){
        data = this._list.filter(({name, document, ssNumber})=> this.includeSearch(name) || this.includeSearch(document) || this.includeSearch(ssNumber));
      } else {
        const contracts = await getContracts(this._filter);
        data = sortBy(contracts, 'startDate', 'desc').map(
          (res) => {
            res.contractType = Number.parseInt(res.contractType);
            if (res.completeCCC) {
              res.regime = res.completeCCC.toString().substr(0,4);
              res.ctaCti = res.completeCCC.toString().substr(4);
            } 
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

}
window.customElements.define("aon-contract-list", AonContractList);