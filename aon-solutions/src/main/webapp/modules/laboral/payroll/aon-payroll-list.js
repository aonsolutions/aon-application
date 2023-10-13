import { AonElement } from "../../../components/AonElement.js";
import { formatNumber, isEmptyObject, sortBy, waitEl, setValueName } from "../../../services/utils.js";
import { firstLetters } from "../../timecontrol/time-control/utils.js";
import { getEmployeeSalaries, getEnterpriseSalaries, getPeriodLaboral, getWorkplaceCCCs, getAllEmployeesWorkplace } from "../../../services/service.js";
import {  PRESENCE_FILTER } from "../../timecontrol/signinEnums.js";
import { PAYROLL_FILTER, PAYROLL_VIEWS } from "../PayrollEnums.js";
import { CONSTANT, EVENT, MSG } from '../../../environments/environments.js';
import { AonMobileList } from "../../../components/aon-mobile-list.js";
import { AonTable } from "../../../components/aon-table.js";
import { AonDateUtils } from "../../utils/AonDateUtils.js";
import { PAYROLL } from "../../../services/app.js";


export class AonPayrollList extends AonElement {
  TABLE_ID;
  searchFilter;
  _list;
  static get observedAttributes() {
    return [CONSTANT.FILTER];
  }

  get filter() {
    return JSON.parse(this.getAttribute(CONSTANT.FILTER));
  }

  set filter(filter) {
    this.setAttribute(CONSTANT.FILTER, JSON.stringify(filter));
  }

  get id() {
    return this.getAttribute("id");
  }

  set id(id) {
    this.setAttribute("id", id);
  }

  attributeChangedCallback(name, oldValue, newValue) {
    if (CONSTANT.FILTER === name) this.getTable();
  }

  constructor() {
    super();
  }

  connectedCallback() {
    this.initialize();
    this.build();
  }

  initialize(){
    this.id = this.id || PAYROLL_VIEWS.AON_PAYROLL_LIST;
    this.TABLE_ID = this.id + "Table";
    this.getApplication().addToolbarTitle(MSG.PAYSHEETS);
    this._list=[];
  }

  async build(){
    this.paintView();
    if(!this.isEmployee()){
      this.buildToolbar();
      this.getApplicationParent().changeFilter();
    }
    await this.getTable();
  }

  paintView() {    
    let aonTable = this.isMobile() ? new AonMobileList() : new AonTable();
    aonTable.id = this.TABLE_ID;
    aonTable.setApp(PAYROLL);
    this.appendChild(aonTable);
  }

  buildToolbar() {
    this.getApplication().removeToolbarOptions();
    this.buildToolbarSearch();
  }


  buildToolbarSearch(){
    const btnSearch = this.getApplication().addSearchOption();
    
    let timeOut = null;
    btnSearch.addEventListener(EVENT.SEARCH_NEW, ({detail})=>{
			clearTimeout(timeOut);
			
			timeOut = setTimeout(() => {
        this._list = [];
        if(detail) this.getApplicationParent().setDataFilter(detail);
			}, 300);
    });
    
    btnSearch.buildOptionsFilter([
      ...PAYROLL_FILTER,
      ...PRESENCE_FILTER
    ]);
    this.searchValueDefault();
  }


  async searchValueDefault(){
    // ----------WORKPLACES ------------
    let workplaces = await getWorkplaceCCCs();
    let workplaceEl = this.getElement("workplace");
    workplaceEl.setOptions(workplaces.map(({workplace})=> ({ name: workplace.description, value: workplace.id})));
    workplaceEl.addEventListener(EVENT.CHANGE, ({detail}) => {
      if(detail) this.getEmployees(detail);
    });
    // ----------WORKPLACES END ------------


    //------------------PERIOD---------
    let periodEl = this.getElement("period");
    periodEl.setOptions(getPeriodLaboral());
    periodEl.addEventListener(EVENT.CHANGE, ({detail}) => {
      if(detail){
        const {startDate, endDate} = detail;
        setValueName('startDate', startDate);
        setValueName('endDate', endDate);
      }
    });
    // ----------PERIOD END ------------

    this.getElement("startDate").addEventListener(EVENT.CHANGE, ()=>periodEl.value = "personalized");
    this.getElement("endDate").addEventListener(EVENT.CHANGE, ()=>periodEl.value = "personalized");
  }

  async getTable() {
    await waitEl("#aonLaboral");
    this.getApplication().startLoader();
    this.isMobile() ? await this.getTableMobile() :  this.getTableDesk();
    this.getApplication().stopLoader();
  }

  async getTableDesk() {
    const aonTable = this.getElement(this.TABLE_ID);
    if (aonTable) {
      aonTable.removeColumns();
      if(!this.isEmployee()){
        aonTable.addColumn(MSG.NAME, "string", "name", "30%");
        aonTable.addColumn("C. Trabajo", "string", "workplaceName", "20%");
      }
      aonTable.addColumn("F. Inicio", "date", "startDateP", "10%");
      aonTable.addColumn("F. Fin", "date", "endDateP", "10%");
      aonTable.addColumn("Bruto", "number", "totalPayment", "10%");
      aonTable.addColumn("Deducciones", "number", "totalDeduction", "10%");
      aonTable.addColumn("Líquido", "number", "totalLiquid", "10%");
      try {
        const resp = await this.getData();
        aonTable.removeRows();
        resp.map((res) => {
          res.startDateP =  AonDateUtils.formatDate(res.startDate);
          res.endDateP   = AonDateUtils.formatDate(res.endDate);
          aonTable.addRow(res, (el) => this.aonEvent(el, res));
        });
      } catch (e) {
        console.log(e);
      }
    }
  }

  async getTableMobile() {
    const aonTable = this.getElement(this.TABLE_ID);
    if (aonTable) {
      try {
        const resp = await this.getData();
        aonTable.removeAllLi();

        const isEmployee = this.isEmployee();

        resp.map((res, idx) => {
          let options = {};
          let dateParse = firstLetters(AonDateUtils.getMonthYear(res.endDate));
          if(isEmployee){
            options.paddingTopTitle = "5px";
            options.iconHtmlCustom = `${res.lettersHtml} <span style="padding-top: 5px;float: right;color: rgba(0,0,0,.54);">${res.totalLiquid}</span>`;
            options.title = `${dateParse}`;
          } else {
            options.iconHtmlCustom = `${res.lettersHtml}`;
            options.title = res.name;
            options.subtitle = `${dateParse} <span style="float: right;">${res.totalLiquid}</span> `;
          }
          aonTable.addLi(options, idx, (el) => this.aonEvent(el, res));
        });
      } catch (e) {
        console.log(e);
      }
    }
  }

  async getData() {
    let data = [];
    try {
      if(this._list.length)
        data = this._list;
      else {
        let filter = this.getApplicationParent()._filter;
        let datos = []; 
        if(this.isEmployee())  {
          datos = await getEmployeeSalaries(filter);
          datos = sortBy(datos, 'endDate', 'desc').filter(({endDate})=> new Date(endDate) <= new Date());
        } else {
          datos = await getEnterpriseSalaries(filter);
          datos = sortBy(datos, 'employeeName', 'asc');
        }

        if (!isEmptyObject(datos)) {
          data = datos.map(res => ({
            ...res,
            name: res.employeeName,
            lettersHtml: this.getDivIconStyle(res.type),
            totalDeduction: formatNumber(res.totalDeduction, 2, "EUR"),
            totalLiquid: formatNumber(res.totalLiquid, 2, "EUR"),
            totalPayment: formatNumber(res.totalPayment, 2, "EUR"),
          }));
          this._list = data;
          if(this.searchFilter) data = this.filterSearch(["name", "workplaceName"], data);
        }
      }
    } catch (e) {
      console.log(e);
    }
    return data;
  }

  search(){
    this._list = this.filterSearch(["name", "workplaceName"], this._list);
    this.getTable();
  }

  filterSearch(keys, lists){
    let list = [];
    if(this.searchFilter && lists.length){
      list = lists.filter((lt)=> keys.some(key=>lt[key] && lt[key].toString().toLowerCase().includes(this.searchFilter.toLowerCase())));
    }
    return list;
  }

  aonEvent({ }, data) {
    const parent = this.getApplicationParent();
    if(parent) {
      parent.getSalary({
        salaryId: data.id,
        enterpriseId: data.enterpriseId,
        type: data.type
      });
    }
  }

  isEmployee(){
    return this.getApplicationParent().isEmployee();
  }

  getDivIconStyle(type){
    const {color, typeReduce} = this.getApplicationParent().getTypeSalaryText(type);
    let div = this.createElement("div");
    div.innerText = typeReduce;
    div.classList.add("profile-letters");
    if(color) div.classList.add(color);
    return div.outerHTML;
  }

  async getEmployees(detail){
    try {
      let employeeEl = this.getElement("employee");
      let employees = await getAllEmployeesWorkplace({workplace: detail.value, allEmployees:true});
      if(employees.length>0) {
        employeeEl.setOptions(
          sortBy(employees, 'surName', 'asc').map(({name, surName, contractId})=> ({name:surName+" "+name, value:contractId}))
        );
        employeeEl.hidden =  false;
      }
      else employeeEl.hidden =  true;
    } catch (error) {
      console.log(error);
    }
  }
}

window.customElements.define("aon-payroll-list", AonPayrollList);
