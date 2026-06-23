import { AonElement } from "../../../components/AonElement.js";
import { getDomainUserRoles } from "../../../services/companyService.js";
import { DomainUserRoles } from "../../../models/DomainUserRoles.js";
import { CSS } from "../../../environments/environments.js";
import { TAG } from "../../../environments/environments.js";
import { formatNumber, isEmptyObject, sortBy, waitEl, setValueName } from "../../../services/utils.js";
import { getEmployeeSalaries, getEnterpriseSalaries, getPeriodLaboral, getWorkplaceCCCs, getAllEmployeesWorkplace } from "../../../services/service.js";
import { AonDateUtils } from "../../utils/AonDateUtils.js";

export class AonPayrollCard extends AonElement {
  AON_FISCAL;
  MODELS = [];
  BANKS=[];
  dur;
  TABLE_ID;
  content;
  _filter;
  
  constructor() {
    super();
  }

  connectedCallback() {
    this.initialize();
    getDomainUserRoles({ reload: true }).then((r) => {
      this.dur = new DomainUserRoles(r);
      this.build();
    });
  }

  initialize() {

  }

  getDur() {
    return this.dur;
  }

  build() {
    this.paintView();
    this.buildToolbar();
  }

  paintView() {
    let cardContent = this.createElement(TAG.DIV);
    cardContent.className = CSS.AON_FLEX_COLUMN;
    cardContent.id = "payrollCardTable";
    this.appendChild(cardContent);
  }

  buildToolbar() {
    this.getPayrolls().then(payrolls => this.getTable(payrolls));
  }

  async getPayrolls() {
    let data = [];
    try {
      let filter = this.getApplicationParent() ? this.getApplicationParent()._filter : this._filter;
      let datos = []; 
      if(this.dur.isEmployee())  {
        if(!filter) filter = {
          endDate: AonDateUtils.getLastMonthLastDayFormat(new Date()),
        }
        datos = await getEmployeeSalaries(filter);
        datos = sortBy(datos, 'endDate', 'desc').filter(({endDate})=> new Date(endDate) <= new Date());
      } else {
        if(!filter) filter = {
          period: 'lastMonth',
          startDate: AonDateUtils.getLastMonthFirstDayFormat(new Date()),
          endDate: AonDateUtils.getLastMonthLastDayFormat(new Date()),
        }
        datos = await getEnterpriseSalaries(filter);
        datos = sortBy(datos, 'employeeName', 'asc');
      }

      if (!isEmptyObject(datos)) {
        data = datos.map(res => ({
          ...res,
          name: res.employeeName,
          lettersHtml: this.getDivIconStyle(res.type),
          totalDeduction: formatNumber(res.totalDeduction, 2, 2, "EUR"),
          totalLiquid: formatNumber(res.totalLiquid, 2, 2, "EUR"),
          totalPayment: formatNumber(res.totalPayment, 2, 2, "EUR"),
        }));
        this._list = data;
        if(this.searchFilter) data = this.filterSearch(["name", "workplaceName"], data);
      }
      
    } catch (e) {
      console.log(e);
    }
    return data;
  }

  getDivIconStyle(type){
    const {color, typeReduce} = this.getTypeSalaryText(type);
    let div = this.createElement("div");
    div.innerText = typeReduce;
    div.classList.add("profile-letters");
    if(color) div.classList.add(color);
    return div;
  }

  getTypeSalaryText(type) {
    let obj = { color: "", type: "", typeReduce: "" };
    switch (type) {
      case "SALARY":
        obj.type = "NOMINA";
        break;
      case "EXTRA":
        obj.type = "EXTRA";
        obj.color = "in";
        break;
      case "SETTLE":
        obj.type = "FINIQUITO";
        obj.color = "fin";
        break;
      case "DELAY":
        obj.type = "ATRASOS";
        obj.color = "pause";
        break;
    }
    if (obj.type) obj.typeReduce = obj.type.toString().substr(0, 1);
    return obj;
  }

  getTable(payrolls) {
    const meses = ["Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Deciembre"];

    let content = this.getElement("payrollCardTable");

    let maxPayrolls = payrolls && payrolls.length < 5 ? payrolls.length : 5;

    for (let index = 0; index < maxPayrolls; index++) {
      const payroll = payrolls[index];
      
      let row = this.createElement(TAG.DIV);
      row.className = CSS.AON_FLEX;
      row.style.justifyContent = "space-between";
      row.style.width = "100%";
      row.style.borderBottom = "1px solid #ddd";
      row.style.padding = "1rem 0";

      let leftContent = this.createElement(TAG.DIV);
      leftContent.className = CSS.AON_FLEX;
      leftContent.style.gap = ".5rem";
      leftContent.style.alignContent = "center";
      leftContent.style.alignItems = "center";

      let icon = payroll.lettersHtml;
      leftContent.appendChild(icon);

      let month = this.createElement(TAG.SPAN);
      month.style.fontSize = "1.1rem";
      month.style.color = "rgb(51, 169, 169)";
      month.style.fontWeight = "500";
      month.style.minWidth = "6rem";
      month.style.textAlign = "left";
      
      month.innerHTML = meses[new Date(Date.parse(payroll.endDate)).getMonth()];
      leftContent.appendChild(month);

      let year = this.createElement(TAG.SPAN);
      year.style.fontSize = "1rem";
      year.style.color = "rgb(51, 169, 169)";
      year.style.fontWeight = "500";
      year.innerHTML = new Date(Date.parse(payroll.endDate)).getFullYear();
      leftContent.appendChild(year);

      if(!this.dur.isEmployee() && payroll.employeeName) {
        let name = this.createElement(TAG.SPAN);
        name.style.fontSize = "1rem";
        name.style.color = "rgb(51, 169, 169)";
        name.style.fontWeight = "500";
        name.textContent = payroll.employeeName;
        leftContent.appendChild(name);
      }
      let rightContent = this.createElement(TAG.DIV);
      rightContent.className = CSS.AON_FLEX;
      rightContent.style.alignItems = "center";
      rightContent.style.gap = "1rem";

      let amount = this.createElement(TAG.SPAN);
      amount.style.fontWeight = "bold";
      amount.style.minWidth = "5rem";
      amount.style.textAlign = "right";
      amount.innerHTML = payroll.totalLiquid;
      rightContent.appendChild(amount);

      row.appendChild(leftContent);
      row.appendChild(rightContent);

      content.appendChild(row);
    }
  }

}
window.customElements.define("aon-payroll-card", AonPayrollCard);
