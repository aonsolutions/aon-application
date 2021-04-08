import { AonElement } from "../../../components/AonElement.js";
// import { getPeriod, getStatus, getTimeControlList, getTimeControlExcel } from "../../../services/service.js";
// import { isEmptyObject, setDateTimestamp, setDateTimestampDay, setValueName, sortBy, waitEl } from "../../../services/utils.js";
// import { dateCustomDayHour, StringTwoLetters, timeHour } from "./utils.js";
import "../../../components/aon-table.js";
import "../../../components/aon-mobile-list.js";
import "../../../components/aon-filter.js";
import { PRESENCE_FILTER, SigninSidenav } from "../../signin/signinEnums.js";
import { getPeriodLaboral } from "../../../services/laboralService.js";
import { formatNumber, setValueName, waitEl } from "../../../services/utils.js";
import { DATA_TEST, TAX_ENUMS } from "../FiscalEnums.js";
// import { FISCAL_VIEWS } from "../FiscalEnums.js";

export class AonTax extends AonElement {
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
    this.id = this.id || FISCAL_VIEWS.AON_TAX;
    this.TABLE_ID = this.id + "Table";
    this.applicationEl = this.getApplication();
    this.applicationParenEl = this.getApplicationParent();
    this.applicationEl.addToolbarTitle("Impuestos");
  }

  async build() {
    this.paintView();
    this.buildToolbar();
    await this.buildFilter();
    await this.getTable();
  }

  paintView() {
    let innerHTML = `<aon-filter id="${this.id}Filter" title="Filtros"></aon-filter>`;
    if (this.isMobile()) {
      innerHTML = innerHTML + ` <aon-mobile-list id='${this.TABLE_ID}' />`;
    } else {
      innerHTML = innerHTML + `<aon-table id='${this.TABLE_ID}' />`;
    }

    this.innerHTML = innerHTML;
    this.applicationEl.development(undefined, "Esta opción está en desarrollo y los datos son de prueba.");
  }

  buildToolbar() {
    this.applicationEl.removeToolbarOptions();
    const filterEl = this.getElement(`${this.id}Filter`);
    this.applicationEl.addToolbarOption2(SigninSidenav.FILTER, (e) =>
      filterEl.openFilter()
    );
  }

  async buildFilter() {
    let aonFilter = this.getElement(`${this.id}Filter`);
    aonFilter.setInputs(PRESENCE_FILTER);
    aonFilter.addEventListener("applyFilter", ({ detail }) => {
      if (detail) {
        console.log(detail);
      }
    });

    let periodEl = this.getElement("period");
    periodEl.options = JSON.stringify(getPeriodLaboral());

    periodEl.addEventListener("change", ({ detail }) => {
      if (detail) {
        const { startDate, endDate } = detail;
        setValueName("startDate", startDate);
        setValueName("endDate", endDate);
      }
    });

    this.getElement("startDate").addEventListener("change", (ev) => {
      periodEl.value = "personalized";
    });
    this.getElement("endDate").addEventListener("change", (ev) => {
      periodEl.value = "personalized";
    });
  }

  async getTable() {
    this.applicationEl = await waitEl("#aonFiscal");
    this.applicationEl.startLoader();
    if (this.isMobile()) await this.getTableMobile();
    else await this.getTableDesk();
    this.applicationEl.stopLoader();
  }

  async getTableDesk() {
    const aonTable = this.getElement(this.TABLE_ID);
    if (aonTable) {
      aonTable.removeColumns();
      aonTable.addColumn("", "string", "lettersHtml", "6%");
      aonTable.addColumn("Ejercicio", "", "year", "10%");
      aonTable.addColumn("Periodo", "", "textPeriod", "10%");
      aonTable.addColumn("Estado", "", "textStatus", "10%");
      aonTable.addColumn("Importe", "number", "result", "10%");
      try {
        const resp = await this.getData();
        aonTable.removeRows();
        resp.map((res) => {
          aonTable.addRow(res, (el) => this.openDialog());
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
        resp.map((res, idx) => {
          let options = {
            iconHtmlCustom: /*html*/ `${res.lettersHtml}<span style="float: right;color: black;font-weight: 500; margin-top: 10px;">${res.result}</span>`,
            title: `${res.model}`,
            subtitle: `${res.textPeriod} - ${res.year}`,
          };
          aonTable.addLi(options, idx, (el) => this.openDialog());
        });
      } catch (e) {
        console.log(e);
      }
    }
  }

  async getData() {
    let data = [];
    try {
      const datos = DATA_TEST;
      if (datos) {
        datos.map((resp) => {
          let color = "";
          if("PENDING"===resp.status) {
            color = "fin"
          } else if("FINISHED"===resp.status) {
            color = "in"
          }
          const lettersHtml = /*html*/`<div class="profile-letters size ${color}">${
            TAX_ENUMS.TAX_MODEL[resp.model]
          }</div>`;
          const obj = {
            ...resp,
            lettersHtml,
            result: formatNumber(resp.result, 2, "EUR"),
            textPeriod: TAX_ENUMS.TAX_PERIOD[resp.period],
            textStatus: TAX_ENUMS.TAX_STATUS[resp.status],
          };
          data.push(obj);
        });
      }
    } catch (e) {
      console.log(e);
    }
    return data;
  }

  openDialog() {
    // this.applicationEl.development();
    let dialog = this.applicationEl.getDialog();
    if (!this.isMobile()) dialog.width = "400px";
    dialog.open();
    dialog.setContentHTML(
      /*html*/`
      <form>
      <h1>TEST</h1>
      </form>`
    );
    dialog.addAcceptAction(() => {});
  }
}

window.customElements.define("aon-tax", AonTax);
