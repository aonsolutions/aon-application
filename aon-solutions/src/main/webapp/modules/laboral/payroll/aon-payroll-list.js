import { AonElement } from "../../../components/AonElement.js";
import { formatNumber, isEmptyObject, setDateTimestamp, setDateTimestampDay, setValueName, sortBy, waitEl } from "../../../services/utils.js";
import { getEmployeeSalaries, getEnterpriseSalaries } from "../../../services/service.js";
import {  PresenceFilterInput, SigninSidenav } from "../../signin/signinEnums.js";
import "../../../components/aon-table.js";
import "../../../components/aon-mobile-list.js";
import "../../../components/aon-filter.js";
export class AonPayrollList extends AonElement {
  TABLE_ID;
  static get observedAttributes() {
    return ["filter"];
  }

  get filter() {
    return JSON.parse(this.getAttribute("filter"));
  }

  set filter(filter) {
    this.setAttribute("filter", JSON.stringify(filter));
  }

  get id() {
    return this.getAttribute("id");
  }

  set id(id) {
    this.setAttribute("id", id);
  }

  attributeChangedCallback(name, oldValue, newValue) {
    if ("filter" === name) this.getTable();
  }

  constructor() {
    super();
  }

  connectedCallback() {
    this.initialize();
    this.build();
  }

  initialize(){
    this.id = this.id || "aonPayrollList";
    this.TABLE_ID = this.id + "Table";
    this.aonLaboralEl = this.getElement("aonLaboral");
    this.aonLaboralParentEl = this.aonLaboralEl.getParent();
    this.aonLaboralEl.addToolbarTitle("Nóminas");
  }
  
  async build(){
    this.paintView();
    // this.buildToolbar();
    // await this.buildFilter();
    await this.getTable();
  }

  paintView() {
    let innerHTML =  `<aon-filter id="${this.id}Filter" title="Filtros"></aon-filter>`;
    if (this.isMobile()) {
      innerHTML = innerHTML + ` <aon-mobile-list id='${this.TABLE_ID}' />`;
    } else {
      innerHTML = innerHTML + `<aon-table id='${this.TABLE_ID}' />`;
    }

    this.innerHTML = innerHTML;
  }

  buildToolbar() {
    this.aonLaboralEl.removeToolbarOptions();
    const filterEl = this.getElement(`${this.id}Filter`);
    this.aonLaboralEl.addToolbarOption2(SigninSidenav.FILTER, (e) =>
      filterEl.openFilter()
    );
  }


  async buildFilter() {
    let aonFilter = this.getElement(`${this.id}Filter`);
    aonFilter.setInputs(PresenceFilterInput);
    aonFilter.addEventListener("applyFilter", ({detail}) => {
      if(detail) this.aonLaboralParentEl.setDataFilter(detail);
    });

    // let periodEl = this.getElement("period");
    // periodEl.options = JSON.stringify(await getPeriod());

    // periodEl.addEventListener('change', ({detail}) => {
    //   if(detail){
    //     const {startDate, endDate} = detail;
    //     setValueName('startDate', startDate);
    //     setValueName('endDate', endDate);
    //   }
    // });

    this.getElement("startDate").addEventListener("change",(ev)=>{
      periodEl.value = "personalized";
    });
    this.getElement("endDate").addEventListener("change",(ev)=>{
      periodEl.value = "personalized";
    });
  }

  async getTable() {
    this.aonLaboralEl = await waitEl("#aonLaboral");
    this.aonLaboralEl.startLoader();
    if (this.isMobile()) await this.getTableMobile();
    else await this.getTableDesk();
    this.aonLaboralEl.stopLoader();
    this.aonLaboralParentEl.changeFilter();
  }



  async getTableDesk() {
    const aonTable = this.getElement(this.TABLE_ID);
    if (aonTable) {
      aonTable.removeColumns();
      aonTable.addColumn("Nombre", "string", "name", "30%");
      aonTable.addColumn("C. Trabajo", "string", "workplaceName", "20%");
      aonTable.addColumn("F. Inicio", "date", "startDateParse", "10%");
      aonTable.addColumn("F. Fin", "date", "endDateParse", "10%");
      aonTable.addColumn("Bruto", "number", "totalPayment", "10%");
      aonTable.addColumn("Deducciones", "number", "totalDeduction", "10%");
      aonTable.addColumn("Líquido", "number", "totalLiquid", "10%");
      try {
        const resp = await this.getData();
        aonTable.removeRows();
        resp.map((res) => {
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

        let isEmployee = this.aonLaboralParentEl.isEmployee();
        
        resp.map((res, idx) => {
          let options = {
            paddingTopTitle: "5px",
            iconHtmlCustom: `${res.lettersHtml} <span style="padding-top: 5px;float: right;color: rgba(0,0,0,.54);">${res.totalLiquid}</span>`,
            title: `${res.endDateParse}`,
          };
          if(!isEmployee){
            options.title = res.name;
            options.subtitle = res.endDateParse;
          }
          aonTable.addLi(options, idx, (el) => this.aonEvent(el, res));
        });
      } catch (e) {
        console.log(e);
      }
    }
  }

  async getData() {
    this.aonLaboralEl.startLoader();
    let data = [];
    try {
      let filter = null;
      try {filter = {...this.aonLaboralParentEl._filter};} catch (error) {}

      let datos = null;
      if(this.aonLaboralParentEl.isEmployee()){
        datos = await getEmployeeSalaries(filter);
      } else {
        datos = await getEnterpriseSalaries(filter);
      }
      if (!isEmptyObject(datos)) {
        sortBy(datos, 'endDate', 'desc')
        .filter(({endDate})=> new Date(endDate) <= new Date())
        .map(
          async ({
            contract,
            employeeName,
            endDate,
            id,
            startDate,
            totalDeduction,
            totalLiquid,
            totalPayment,
            type,
            workplaceName,
          }) => {
              const typeText = this.getTypeSalaryText(type);
              const lettersType = (typeText).substr(0,1);
              let color = "";
              if("E"===lettersType)      color = "in"
              else if("F"===lettersType) color = "fin"; 
              else if("A"===lettersType) color = "pause";

              const lettersHtml = `<div class="profile-letters ${color}">${lettersType}</div>`;
              const obj = {
                lettersHtml,
                contract,
                name: employeeName,
                startDateParse: startDate,
                endDateParse:  endDate,
                id,
                totalDeduction: formatNumber(totalDeduction.toString(), 2),
                totalLiquid: formatNumber(totalLiquid.toString(), 2),
                totalPayment: formatNumber(totalPayment.toString(), 2),
                type,
                workplaceName,
              };
              data.push(obj);
          }
        );
      }
    } catch (e) {
      console.log(e);
    }
    this.aonLaboralEl.stopLoader();
    return data;
  }

  aonEvent({ target }, data) {
    const parent = this.aonLaboralParentEl;
    if(parent) parent.getSalary({salaryId:data.id});
  }

  getTypeSalaryText(type){
    let newType = "";
    switch(type){
      case "SALARY":
        newType="NOMINA";
      break;
      case "EXTRA":
        newType="EXTRA";
      break;
      case "SETTLE":
        newType="FINIQUITO";
      break;
      case "DELAY":
        newType="ATRASOS";
      break;
    }
    return newType;
  }
}

window.customElements.define("aon-payroll-list", AonPayrollList);
