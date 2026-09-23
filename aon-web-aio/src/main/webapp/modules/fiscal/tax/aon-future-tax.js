import { AonElement } from "../../../components/AonElement.js";
import { formatNumber } from "../../../services/utils.js";
import { getEstimationModelsFiscal } from "../../../services/fiscalService.js";
import {
  TAG,
  MSG,
  CONSTANT,
  MATERIAL_ICONS,
} from "../../../environments/environments.js";
import { AonMobileList } from "../../../components/aon-mobile-list.js";
import { AonTable } from "../../../components/aon-table.js";
import { FiscalUtils } from "../FiscalUtils.js";
import { FISCAL_VIEWS } from "../FiscalEnums.js";
import { AonTaxDetail } from "./aon-tax-detail.js";

export class AonFutureTax extends AonElement {

  TABLE_ID;

  applicationEl;
  filter;
  app;

  get id() {
    return this.getAttribute(CONSTANT.ID);
  }

  set id(id) {
    this.setAttribute(CONSTANT.ID, id);
  }

  /**
  * @param {services/app.js} app
  * Example
  * FISCAL, INVOICE
  * 
  * @param {Object} filter
  * Example
  * {
  *   period: "T3"
  *   periodText: "3º Trim. 2024"
  *   title: "3º Trim. 2024"
​  *   year: 2024
  * }
  */
  constructor(app, filter) {
    super();
    this.app = app;
    this.filter = filter;
    console.log("AonFutureFilter");
    console.log(this.filter);
  }

  connectedCallback() {
    this.initialize();
    this.build();
  }

  initialize() {
    this.id = this.id || FISCAL_VIEWS.AON_FUTURE_TAX;
    this.TABLE_ID = this.id + "Table";
    this.addToolbarTitle();
  }

  addToolbarTitle(){
    this.applicationEl = this.getApplication();
    this.applicationEl.addToolbarTitle("Precálculo Impuestos");
  }

  async build() {
    this.paintView();
    await this.getTable();
  }

  paintView() {
    let aonTable = this.isMobile() ? new AonMobileList() : new AonTable();
    aonTable.id = this.TABLE_ID;
    this.appendChild(aonTable);
  }

  async getTable() {
    if (this.isMobile()) {
      await this.getTableMobile();
    } else {
      await this.getTableDesk();
    }
  }

  async getTableDesk() {
    const aonTable = this.getElement(this.TABLE_ID);
    if (aonTable) {
      aonTable.removeColumns();
      aonTable.addColumn("", "string", "lettersHtml", "10%");
      aonTable.addColumn("Modelo", "", "modelText", "auto");
      aonTable.addColumn("Hacienda", "", "hacienda", "10%");
      aonTable.addColumn("Ejercicio", "", "year", "10%");
      aonTable.addColumn("Periodo", "", "periodText", "10%");
      aonTable.addColumn("Estado", "string", "statusHtml", "12%");
      aonTable.addColumn("Importe", "number", "resultFormat", "15%");
      aonTable.addColumn("", "icons", "icons", "100px");

      try {
        const resp = await this.getData();
        aonTable.removeRows();

        if (resp.length) {
          resp.forEach((res) => {
            this.buildIcons(res);
            let tr = aonTable.addRow(res, () => {
              this.getApplication().setContent(new AonTaxDetail(res, "future"));
              //this.openDialog(res);
            });

            tr.id = "aonFiscalRow";
          });

          let elementHTML = document.createElement(TAG.DIV);
          elementHTML.style.alignItems = "center";
          elementHTML.style.fontWeight = "bold";
          elementHTML.style.maxWidth = "6rem";
          elementHTML.title = "Borrador";
          
          let description = document.createElement(TAG.SPAN);
          description.innerText = "Total";
          elementHTML.appendChild(description);

          const statusHtml = elementHTML.innerHTML;

          let row = aonTable.addRow({
            statusHtml: statusHtml,
            statusText: "Total",
            resultFormat: this.getTotal(resp),
          });
          row.style.fontWeight = "600";
          row.id = "aonFiscalRow";
        } else {
          aonTable.empty();
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
        aonTable.removeAllLi();
        resp.forEach((res, idx) => {
          let options = {
            iconHtmlCustom: /*html*/ `${res.lettersHtml}<span style="float: right;color: black;font-weight: 500; margin-top: 10px;">${res.resultFormat}</span>`,
            title: `${res.model || res.modelText}`,
            subtitle: `${res.periodText} - ${res.year}`,
          };
          aonTable.addLi(options, idx, () => this.openDialog(res));
        });

        aonTable.addLi({
          title: "Total",
          iconHtmlCustom: /*html*/ `<span style="float: right;color: black;font-weight: 600; margin-top: 10px;">${this.getTotal(
            resp
          )}</span>`,
        });
      } catch (e) {
        console.log(e);
      }
    }
  }

  async getData() {
    const estimationModels = await getEstimationModelsFiscal(this.filter);
    let estimationModelDatos = estimationModels.filter((estimationModel) => estimationModel.amount && estimationModel.amount != 0).map((estimationModel) => this.formatEstimationModel(estimationModel))
    return estimationModelDatos;
  }

  formatEstimationModel(model) {
    const newModel = ""; //Empty

    let elementHTML = document.createElement(TAG.DIV);
    elementHTML.style.alignItems = "center";
    elementHTML.style.fontWeight = "bold";
    elementHTML.style.maxWidth = "6rem";
    elementHTML.title = "Borrador";
    
    let description = document.createElement(TAG.SPAN);
    description.innerText = "Borrador";
    elementHTML.appendChild(description);

    const statusHtml = elementHTML.outerHTML;

    return {
      ...model,
      resultFormat: !isNaN(model.amount)
        ? formatNumber(model.amount, 2, 2, "EUR")
        : null,
      periodText: this.filter.periodText,
      modelText: model.description,
      year : this.filter.year,
      period : this.filter.period,
      hacienda : model.hacienda, // Alava, AEAT...
      result : model.amount,
      statusHtml,
      lettersHtml : `<div style="font-weight: bold;">Precálculo</div>`,
      statusText : "Borrardor",
      newModel
    };
  }

  getTotal(models) {
    let total = models.reduce((t, model) => t + model.result, 0);
    return formatNumber(total, 2, 2, "EUR");
  }

  // Dialog

  openDialog(resp) {
    const dialog = this.applicationEl.getDialog();
    dialog.clear();

    if (!this.isMobile()) dialog.width = "500px";
    
    if (dialog.getButtonAccept()) dialog.getButtonAccept().remove();
    if (dialog.getButtonCancel()) dialog.getButtonCancel().remove();

    let div = this.builDialog(resp);
    dialog.setContent(div);

    dialog.open();
  }

  builDialog(resp) {
    const div = this.createElement(TAG.DIV);
    const divImg = this.createElement(TAG.DIV);
    divImg.style.fontSize = 18;
    divImg.appendChild(FiscalUtils.createImgAdmin(resp.administration));

    const spanTextImg = this.createElement(TAG.SPAN);
    spanTextImg.style.marginLeft = 3;
    spanTextImg.textContent = `${MSG.MODEL} ${resp.newModel} (${resp.modelText})`;
    divImg.appendChild(spanTextImg);
    div.appendChild(divImg);

    const divOne = this.createElement(TAG.DIV);
    divOne.className = "aonFlexBetween colorGrey aonFontWeight-700";
    divOne.style.margin = "10px 0";

    const divTextOne = this.createElement(TAG.DIV);
    divTextOne.textContent = `${resp.periodText} - ${resp.year}`;
    divOne.appendChild(divTextOne);

    const divTextTwo = this.createElement(TAG.DIV);
    divTextTwo.style.textAlign = "end";
    divTextTwo.style.color = "black";
    divTextTwo.textContent = resp.resultFormat;
    divOne.appendChild(divTextTwo);
    div.appendChild(divOne);

    return div;
  }

  buildIcons(res) {
    let icons = [];

    if (["FINISHED", "SENT"].includes(res.status)) {
      let icon = {
        icon: MATERIAL_ICONS.PDF,
        color: "var(--aonTaxBuildPrintRes)",
        fn : () => this.getPdf(res)
      };
      icons.push(icon);
    };

    let icon = {
      icon: MATERIAL_ICONS.LIST_ALT,
      title: "Ver facturas y nóminas incluidas",
      color: "var(--aonTaxBuildPrintRes)",
      fn : () => this.getApplication().setContent(new AonTaxDetail(res, "future"))
    };
    icons.push(icon);
    res.icons = icons;
  }

}

window.customElements.define("aon-future-tax", AonFutureTax);
