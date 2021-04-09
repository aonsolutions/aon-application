import { AonElement } from "../../../components/AonElement.js";
import { PRESENCE_FILTER, SigninSidenav } from "../../signin/signinEnums.js";
import { getPeriodLaboral } from "../../../services/laboralService.js";
import { formatNumber, isEmptyObject, serializeForm, setValueName, waitEl } from "../../../services/utils.js";
import { DATA_TEST, TAX_ENUMS } from "../FiscalEnums.js";
import "../../../components/aon-table.js";
import "../../../components/aon-mobile-list.js";
import "../../../components/aon-filter.js";
import '../../../components/aon-switch.js';
import '../../../components/aon-input.js';
import "../../../components/aon-select.js";
import { getCompanyBanks } from "../../../services/companyService.js";

export class AonTax extends AonElement {
  TABLE_ID;
  BANKS;
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
    this.applicationParentEl = this.getApplicationParent();
    this.applicationEl.addToolbarTitle("Impuestos");
  }

  async build() {
    this.paintView();
    this.buildToolbar();
    await this.buildFilter();
    await this.getTable();
    await this.getBanks();
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
      aonTable.addColumn("Modelo", "", "modelText", "20%");
      aonTable.addColumn("Ejercicio", "", "year", "10%");
      aonTable.addColumn("Periodo", "", "periodText", "10%");
      aonTable.addColumn("Estado", "", "statusText", "10%");
      aonTable.addColumn("Importe", "number", "result", "10%");
      try {
        const resp = await this.getData();
        aonTable.removeRows();
        resp.map((res) => {
          aonTable.addRow(res, (el) => this.openDialog(res));
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
            iconHtmlCustom: /*html*/ `${res.lettersHtml}<span style="float: right;color: black;font-weight: 500; margin-top: 10px;">${res.resultFormat}</span>`,
            title: `${res.model}`,
            subtitle: `${res.periodText} - ${res.year}`,
          };
          aonTable.addLi(options, idx, (el) => this.openDialog(res));
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
          let newModel = TAX_ENUMS.TAX_MODEL_NUMBER[resp.model];
          let color = "";
          if("PENDING"===resp.status) {
            color = "fin"
          } else if("FINISHED"===resp.status) {
            color = "in"
          }
          const lettersHtml = /*html*/`<div class="profile-letters size ${color}">${
            TAX_ENUMS.TAX_MODEL_NUMBER[resp.model]
          }</div>`;
          const obj = {
            ...resp,
            lettersHtml,
            resultFormat:formatNumber(resp.result, 2, "EUR"),
            periodText: TAX_ENUMS.TAX_PERIOD[resp.period],
            statusText: TAX_ENUMS.TAX_STATUS[resp.status],
            modelText: TAX_ENUMS.TAX_MODEL_TEXT[newModel],
            newModel
          };
          data.push(obj);
        });
      }
    } catch (e) {
      console.log(e);
    }
    return data;
  }

  openDialog(resp) {
    const dialog = this.applicationEl.getDialog();
    dialog.clear();
    if (!this.isMobile()) dialog.width = "500px";
    dialog.open();
    dialog.setContentHTML(
      /*html*/`
      <div style="font-size: 18;"><img id ="imgAeat"></img> Modelo ${resp.newModel} (${resp.modelText})</div>
      
      <div class="aonFlexBetween colorGrey aonFontWeight-700" style="margin: 10px 0;">
        <div>${resp.periodText} - ${resp.year}</div>
        <div style="text-align: end;color:black;">${resp.resultFormat}</div>
      </div>
      <div class="aonFlexBetween colorGrey aonFontWeight-700" style="margin: 20px 0;">
        <div>Tipo: <span style="color:black;"> ${TAX_ENUMS.TAX_TYPE[resp.type]}</span></div>
      </div>
      <form id="${this.id}Form">
        <aon-select name="rbank" id="iban" title="IBAN" hidden></aon-select>
        <div class="aon-margin-0" id="divNrc" hidden>
          <aon-switch class="aonWidth25" style="width: 26%;" id="switchDni" title="NRC"></aon-switch>
          <aon-input class="aonWidth75"  style="width: 72%;"  name="nrc" id="nrc" description="Nº Referencia Completo" type="text" disabled="true" value="${resp.nrc}"></aon-input>
        <div>
        <aon-input name="id" id="id" description="id" value="${resp.id}" visible="false"></aon-input>
      </form>`
    );
    dialog.addSendAction(() => this.save(resp,dialog));
    this.setImg(resp);
    this.visibleFields(resp);
    this.eventData(resp);
  }

  eventData(resp){
    this.getElement('switchDni').addEventListener('change', ({ target }) => {
        let nrc = this.getElement("nrc");
        if(nrc) nrc.disabled = !target.checked;
        if(!target.checked) nrc.value ="";
    });

    const iban = this.getElement('iban');
    this.getBanks().then(result=>{
      if(result){
        let options = result.map(r=> ({
          name: `${r.bankAccount} - ${r.alias}`,
          value: `${r.id}`
        }));
        iban.options = JSON.stringify(options);
      }
    })
  }


  getFormValues() {
      const form = this.getElement(`${this.id}Form`);
      return serializeForm(form);
  }

  async getBanks(){
    if(isEmptyObject(this.BANKS)){
      let result = await getCompanyBanks().catch(e=>null);
      if(result) this.BANKS = result;
    }
    return this.BANKS;
  }

  setImg({administration}){
    let img = this.getElement('imgAeat');
    if(img && administration){
      const path = "assets/img/";
      let src = "aeat.png";
      switch(administration){
        case "ALAVA":
          src = "aeat_alava.png";
          break;
        case "BIZKAIA":
          src = "aeat_biskaia.png";
          break;
        case "GIPUZKOA":
          src = "aeat_gipuzcoa.png";
          break;
        case "NAVARRA":
          src = "aeat_navarra.png";
          break;
      }
      img.src=path+src;
    }
  }

  visibleFields({type}){
    if(type){
      let iban = this.getElement("iban");
      let divNrc = this.getElement("divNrc");
      let ibanHidden = true;
      let nrcHidden  = true;
      switch(type){
        case "DEPOSIT":
          ibanHidden = nrcHidden = false;
        break;
        case "BANK":
        case "PAYBACK":
          ibanHidden = false;
        break;
      }
      iban.hidden  = ibanHidden;
      divNrc.hidden  = nrcHidden;
    }
  }


  save(resp, dialog){
    this.applicationEl.startLoading();
    let form = {...resp,...this.getFormValues()};
    console.log(form);
    setTimeout(()=>{
      this.applicationEl.stopLoading();
      this.applicationEl.getToast().start({message: "Datos guardados!", type:"success"});
      dialog.close();
    },2000)
  }

}

window.customElements.define("aon-tax", AonTax);
