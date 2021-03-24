import { AonElement } from "../../../components/AonElement.js";
import {
  formatNumber,
  isEmptyObject,
  formatDate,
  sortBy,
  waitEl,
  geMonthYear,
  setValueName,
  formatDateOrigin,
  addMonth,
} from "../../../services/utils.js";
import { firstLetters } from "../../signin/time-control/utils.js";
import {
  getPeriodLaboral,
  getWorkplaceCCCs,
  getAllEmployeesWorkplace,
  getCompanyCosts,
  getCompanyCostsExcel,
} from "../../../services/service.js";
import { PRESENCE_FILTER, SigninSidenav } from "../../signin/signinEnums.js";
import {
  PAYROLL_FILTER,
  PAYROLL_FILTER_EXCEL_TYPE,
  PAYROLL_VIEWS,
} from "../PayrollEnums.js";
import "../../../components/aon-table.js";
import "../../../components/aon-mobile-list.js";
import "../../../components/aon-filter.js";
import { pieChar } from "./pieChar.js";

export class AonCompanyCostsList extends AonElement {
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

  initialize() {
    this.id = this.id || PAYROLL_VIEWS.AON_PAYROLL_LIST;
    this.TABLE_ID = this.id + "Table";
    this.applicationEl = this.getApplication();
    this.applicationParentEl = this.getApplicationParent();
    this.applicationEl.addToolbarTitle("Costes de empresa");
  }

  async build() {
    this.filterInit();
    this.paintView();
    this.buildToolbar();
    await this.buildFilter();
  }

  filterInit() {
    const now = new Date();
    this.filter = {
      period: "last_month",
      excelType: "COMPLETE",
      startDate: formatDateOrigin(
        new Date(now.getFullYear(), now.getMonth() - 1, 1)
      ),
      endDate: formatDateOrigin(
        new Date(now.getFullYear(), now.getMonth() - 1 + 1, 0)
      ),
    };
  }

  paintView() {
    let innerHTML = `<aon-filter id="${this.id}Filter" title="Filtros"></aon-filter>`;
    if (this.isMobile()) {
      innerHTML = innerHTML + ` <aon-mobile-list id='${this.TABLE_ID}' />`;
    } else {
      innerHTML = innerHTML + `<aon-table id='${this.TABLE_ID}' />`;
    }

    this.innerHTML = innerHTML;
    if(this.isMobile())this.paintChatTest();
  }

  paintChatTest() {
    let div = document.createElement('div')
    div.style.display = "flex";
    div.style.alignItems = "center";
    div.style.justifyContent = "center";
    div.style.flexDirection ="column";
    let myCanvas = document.createElement('canvas');
    let myLegend = document.createElement('legend');
    myLegend.setAttribute("for","myCanvas");
    myCanvas.id = "myCanvas";
    myCanvas.width = 300;
    myCanvas.height = 300;
    myCanvas.style.background= "white";
    myCanvas.style.position =  "relative";
    myCanvas.style.top =  "10px";
    div.appendChild(myCanvas);
    div.appendChild(myLegend);
    this.appendChild(div);
    
    pieChar({ 
        canvas:myCanvas,
        legend:myLegend,
        data:{
          "Test1": 10,
          "Test2": 14,
          "Test3": 2,
          "Test4": 12
        },
        colors:["#fde23e","#f16e23", "#57d9ff","#937e88"],
        doughnutHoleSize: 0
      });

  }

  buildToolbar() {
    this.applicationEl.removeToolbarOptions();
    const filterEl = this.getElement(`${this.id}Filter`);
    this.applicationEl.addToolbarOption2(SigninSidenav.FILTER, (e) =>
      filterEl.openFilter()
    );
    this.applicationEl.addToolbarOption2(SigninSidenav.EXCEL, (e) =>
      this.getCompanyCostsExcel()
    );
  }

  SUMMARY;
  async buildFilter() {
    let aonFilter = this.getElement(`${this.id}Filter`);
    aonFilter.setInputs([
      PAYROLL_FILTER[0],
      PAYROLL_FILTER_EXCEL_TYPE,
      ...PRESENCE_FILTER,
    ]);
    aonFilter.addEventListener("applyFilter", ({ detail }) => {
      if (detail) {
        this.filter = detail;
      }
    });

    // ----------WORKPLACES ------------
    let workplaces = await getWorkplaceCCCs();
    let workplaceEl = this.getElement("workplace");
    workplaceEl.options = JSON.stringify(
      workplaces.map(({ workplace }) => ({
        name: workplace.description,
        value: workplace.id,
      }))
    );
    // ----------WORKPLACES END ------------

    // ----------EXCELTYPE ------------
    let excelTypeEl = this.getElement("excelType");
    excelTypeEl.options = JSON.stringify(this.getTypeExcel());
    excelTypeEl.value = "COMPLETE";
    // ----------EXCELTYPE END ------------

    //------------------PERIOD---------
    let periodEl = this.getElement("period");
    periodEl.options = JSON.stringify(await getPeriodLaboral());
    periodEl.addEventListener("change", ({ detail }) => {
      if (detail) {
        const { startDate, endDate } = detail;
        setValueName("startDate", startDate);
        setValueName("endDate", endDate);
      }
    });
    // ----------PERIOD END ------------

    this.getElement("startDate").addEventListener(
      "change",
      (ev) => (periodEl.value = "personalized")
    );
    this.getElement("endDate").addEventListener(
      "change",
      (ev) => (periodEl.value = "personalized")
    );
  }

  async getTable() {
    this.applicationEl = await waitEl("#aonLaboral");
    this.applicationEl.startLoader();
    if (this.isMobile()) await this.getTableMobile();
    else await this.getTableDesk();
    this.applicationEl.stopLoader();
  }

  async getTableDesk() {
    const aonTable = this.getElement(this.TABLE_ID);
    if (aonTable) {
      aonTable.removeColumns();
      aonTable.addColumn("Nombre", "string", "name", "30%");
      aonTable.addColumn("C. Trabajo", "string", "workplaceName", "20%");
      aonTable.addColumn("Tipo", "string", "salaryType", "10%");
      aonTable.addColumn("Bruto", "number", "raw", "10%");
      aonTable.addColumn("Seg. Social", "number", "totalSS", "10%");
      aonTable.addColumn("Coste total", "number", "totalCost", "10%");
      try {
        const resp = await this.getData();
        aonTable.removeRows();
        let sumTotal = {
          raw: 0,
          totalCost: 0,
          totalSS: 0,
        };
        resp.map((res) => {
          sumTotal.raw += parseFloat(res.raw);
          sumTotal.totalCost += parseFloat(res.totalCost);
          sumTotal.totalSS += parseFloat(res.totalSS);
          aonTable.addRow(
            {
              ...res,
              raw: formatNumber(res.raw, 2, "EUR"),
              totalSS: formatNumber(res.totalSS, 2, "EUR"),
              totalCost: formatNumber(res.totalCost, 2, "EUR"),
            },
            (el) => console.log(el)
          );
        });

        if (resp.length > 0) {
          aonTable.addRow(
            {
              salaryType: `<span style="font-weight:800;">TOTALES</span`,
              raw: formatNumber(sumTotal.raw, 2, "EUR"),
              totalSS: formatNumber(sumTotal.totalSS, 2, "EUR"),
              totalCost: formatNumber(sumTotal.totalCost, 2, "EUR"),
            },
            (el) => console.log(el)
          );
        }
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
        // aonTable.removeAllLi();

        // let isEmployee = this.applicationParentEl.isEmployee();

        // resp.map((res, idx) => {
        //   let options = {};
        //   let dateParse = firstLetters(geMonthYear(res.endDate));
        //   if(isEmployee){
        //     options.paddingTopTitle = "5px";
        //     options.iconHtmlCustom = `${res.lettersHtml} <span style="padding-top: 5px;float: right;color: rgba(0,0,0,.54);">${res.totalLiquid}</span>`;
        //     options.title = `${dateParse}`;
        //   } else {
        //     options.iconHtmlCustom = `${res.lettersHtml}`;
        //     options.title = res.name;
        //     options.subtitle = `${dateParse} <span style="float: right;">${res.totalLiquid}</span> `;
        //   }
        //   aonTable.addLi(options, idx, (el) => this.aonEvent(el, res));
        // });
      } catch (e) {
        console.log(e);
      }
    }
  }

  async getData() {
    this.applicationEl.startLoader();
    let data = [];
    try {
      let datos = await getCompanyCosts(this.filter);
      if (!isEmptyObject(datos)) {
        sortBy(datos, "employee", "asc").map((resp) => {
          const lettersType = this.applicationParentEl.getTypeSalaryText(
            resp.salaryType
          );
          const lettersHtml = `<div class="profile-letters ${lettersType.color}">${lettersType.typeReduce}</div>`;
          const obj = {
            lettersHtml,
            name: resp.employee,
            salaryType: lettersType.type,
            raw: resp.raw,
            totalSS: resp.enterpriseSS,
            totalCost: resp.totalCost,
            workplaceName: resp.workplace,
          };
          data.push(obj);
        });
      }
    } catch (e) {
      console.log(e);
    }
    this.applicationEl.stopLoader();
    return data;
  }

  getTypeExcel() {
    return [
      {
        name: "COMPLETO",
        value: "COMPLETE",
      },
      {
        name: "RESUMEN",
        value: "SUMMARY",
      },
    ];
  }

  async getCompanyCostsExcel() {
    this.applicationEl.startLoading();
    try {
      await getCompanyCostsExcel({ ...this.filter });
    } catch ({ message, type }) {
      if (message && type)
        this.applicationEl.getToast().start({ message, type });
    }
    this.applicationEl.stopLoading();
  }
}

window.customElements.define("aon-company-costs-list", AonCompanyCostsList);
