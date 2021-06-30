import { AonElement } from "../../../components/AonElement.js";
import { formatNumber, isEmptyObject, formatDate, sortBy, waitEl, geMonthYear, setValueName } from "../../../services/utils.js";
import { firstLetters } from "../../signin/time-control/utils.js";
import { getEmployeeSalaries, getEnterpriseSalaries, getPeriodLaboral, getWorkplaceCCCs, getAllEmployeesWorkplace } from "../../../services/service.js";
import {  PRESENCE_FILTER } from "../../signin/signinEnums.js";
import { PAYROLL_FILTER, PAYROLL_VIEWS } from "../PayrollEnums.js";
import { AonToolbar } from "../../../components/aon-toolbar.js";
import { ToolbarType } from "../../../models/enums.js";
import { CONSTANT, EVENT, MSG } from '../../../environments/environments.js';
import * as ACTION from '../../actions.js';
import { AonMobileList } from "../../../components/aon-mobile-list.js";
import { AonTable } from "../../../components/aon-table.js";


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
    this.applicationEl = this.getApplication();
    this.applicationParentEl = this.getApplicationParent();
    this.applicationEl.addToolbarTitle(MSG.PAYSHEETS);
    this._list=[];
  }

  async build(){
    this.paintView();
    if(!this.applicationParentEl.isEmployee()){
      this.buildToolbar();
      this.applicationParentEl.changeFilter();
    }
    await this.getTable();
  }

  paintView() {    
    let aonTable = this.isMobile() ? new AonMobileList() : new AonTable();
    aonTable.id = this.TABLE_ID;
    this.appendChild(aonTable);
  }

  buildToolbar() {
    this.applicationEl.removeToolbarOptions();
    this.buildToolbarSearch();
    this.searchValueDefault();
  }


  buildToolbarSearch(){
    let btnSearch = this.applicationEl.addSearchOption();
    
    const searchFn = ({detail}) => {
      this.searchFilter = detail;
      this.search();
    }
    const searchValueFn = ({detail})=>{
      this._list = [];
      if(detail) this.applicationParentEl.setDataFilter(detail);
    }

    btnSearch.addEventListener(EVENT.SEARCH, searchFn);
    btnSearch.addEventListener(EVENT.SEARCH_VALUE, searchValueFn);

    
    btnSearch.buildOptionsFilter([
      ...PAYROLL_FILTER,
      ...PRESENCE_FILTER
    ]);
  }


  async searchValueDefault(){
    // ----------WORKPLACES ------------
    let workplaces = await getWorkplaceCCCs();
    let workplaceEl = this.getElement("workplace");
    workplaceEl.options = JSON.stringify( workplaces.map(({workplace})=> ({ name: workplace.description, value: workplace.id})) );
    workplaceEl.addEventListener(EVENT.CHANGE, ({detail}) => {
      if(detail) this.getEmployees(detail);
    });
    // ----------WORKPLACES END ------------


    //------------------PERIOD---------
    let periodEl = this.getElement("period");
    periodEl.options = JSON.stringify(getPeriodLaboral());
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
    this.applicationEl = await waitEl("#aonLaboral");
    this.applicationEl.startLoader();
    this.isMobile() ? await this.getTableMobile() :  this.getTableDesk();
    this.applicationEl.stopLoader();
  }


  buildToolbarMobile(){
    let parentEl = this.applicationParentEl;
    if(!parentEl.isEmployee()){
      const toolbarEl = new AonToolbar();
      const filterEl = this.getElement(`${this.id}Filter`);
      toolbarEl.type = ToolbarType.SECONDARY;
      this.insertBefore(toolbarEl, filterEl);
      toolbarEl.removeButtons();
      toolbarEl.addButton2(ACTION.BACK, () =>parentEl.showView(PAYROLL_VIEWS.AON_COMPANY_COSTS_LIST))
      toolbarEl.title = MSG.PAYSHEETS;
    }
  }


  async getTableDesk() {
    const aonTable = this.getElement(this.TABLE_ID);
    if (aonTable) {SigninSidenav
      aonTable.addColumn("Nombre", "string", "name", "30%");
      aonTable.addColumn("C. Trabajo", "string", "workplaceName", "20%");
      aonTable.addColumn("F. Inicio", "date", "startDateP", "10%");
      aonTable.addColumn("F. Fin", "date", "endDateP", "10%");
      aonTable.addColumn("Bruto", "number", "totalPayment", "10%");
      aonTable.addColumn("Deducciones", "number", "totalDeduction", "10%");
      aonTable.addColumn("Líquido", "number", "totalLiquid", "10%");
      try {
        const resp = await this.getData();
        aonTable.removeRows();
        resp.map((res) => {
          res.startDateP =  formatDate(res.startDate);
          res.endDateP   = formatDate(res.endDate);
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

        const isEmployee = this.applicationParentEl.isEmployee();

        resp.map((res, idx) => {
          let options = {};
          let dateParse = firstLetters(geMonthYear(res.endDate));
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
      if(this._list.length){
        data = this._list;
      } else {
        const isEmployee = this.applicationParentEl.isEmployee();
        let filter = this.applicationParentEl._filter;
        let datos = isEmployee ? await getEmployeeSalaries(filter) : await getEnterpriseSalaries(filter);
        if (!isEmptyObject(datos)) {
          datos = isEmployee ? sortBy(datos, 'endDate', 'desc').filter(({endDate})=> new Date(endDate) <= new Date()) : sortBy(datos, 'employeeName', 'asc');
          data = datos.map(({
              contract,
              employeeName:name,
              endDate,
              id,
              startDate,
              totalDeduction,
              totalLiquid,
              totalPayment,
              type,
              workplaceName,
            }) => {
                const lettersType = this.applicationParentEl.getTypeSalaryText(type);
                const lettersHtml = `<div class="profile-letters ${lettersType.color}">${lettersType.typeReduce}</div>`;
                return {
                  id,
                  lettersHtml,
                  contract,
                  type,
                  workplaceName,
                  name,
                  startDate,
                  endDate,
                  totalDeduction: formatNumber(totalDeduction, 2, "EUR"),
                  totalLiquid: formatNumber(totalLiquid, 2, "EUR"),
                  totalPayment: formatNumber(totalPayment, 2, "EUR"),
                }
            }
          );
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
    const parent = this.applicationParentEl;
    if(parent) parent.getSalary({salaryId:data.id});
  }

  async getEmployees(detail){
    try {
      let employeeEl = this.getElement("employee");
      let employees = await getAllEmployeesWorkplace({workplace: detail.value, allEmployees:true});
      if(employees.length>0) {
        employeeEl.options = JSON.stringify(
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
