import { AonElement } from "../../../components/AonElement.js";
import { PRESENCE_FILTER, SigninSidenav } from "../../signin/signinEnums.js";
import { getPeriodLaboral } from "../../../services/laboralService.js";
import { formatNumber, isEmptyObject, serializeForm, setValueName, waitEl } from "../../../services/utils.js";
import { getCompanyBanks, getModelsFiscal, setModelStatus } from "../../../services/service.js";
import { TAX_ENUMS } from "../FiscalEnums.js";
import * as AON_TAG from "../../../environments/aonTag.js";
import { AonCheckbox } from "../../../components/aon-checkbox.js";
import { AonSelect } from "../../../components/aon-select.js";
import { AonInput } from "../../../components/aon-input.js";
import { AonSwitch } from "../../../components/aon-switch.js";
import "../../../components/aon-table.js";
import "../../../components/aon-mobile-list.js";
import "../../../components/aon-filter.js";

export class AonTax extends AonElement {
  TABLE_ID;
  BANKS;
  DIALOG_CHECKBOX;
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
    this.DIALOG_CHECKBOX = this.id+"CheckBox";
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
      aonTable.addColumn("Importe", "number", "resultFormat", "10%");
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

  openDialog(resp) {
    const dialog = this.applicationEl.getDialog();
    dialog.clear();
    if (!this.isMobile()) dialog.width = "500px";

    dialog.setContent(this.getDialogHtml(resp));

    if("CUSTOMER_CHECK"===resp.status){
      this.createFooterDialog(resp, dialog);
    } 
    dialog.open();
    this.visibleFields(resp);
    this.eventData(resp);
  }

  getDialogHtml(resp){
    const div = this.createElement(AON_TAG.DIV);
    const divImg = this.createElement(AON_TAG.DIV);
    divImg.style.fontSize= 18;
    const imgAeat = this.createElement(AON_TAG.IMG);
    imgAeat.id = "imgAeat";
    imgAeat.src = this.getPathImg(resp.administration);
    divImg.appendChild(imgAeat);

    const spanTextImg =  this.createElement(AON_TAG.SPAN);
    spanTextImg.style.marginLeft = 3;
    spanTextImg.textContent = `Modelo ${resp.newModel} (${resp.modelText})`;
    divImg.appendChild(spanTextImg);
    div.appendChild(divImg);

    const divOne = this.createElement(AON_TAG.DIV);
    divOne.className = "aonFlexBetween colorGrey aonFontWeight-700";
    divOne.style.margin= "10px 0";

    const divTextOne =  this.createElement(AON_TAG.DIV);
    divTextOne.textContent = `${resp.periodText} - ${resp.year}`;
    divOne.appendChild(divTextOne);

    const divTextTwo =  this.createElement(AON_TAG.DIV);
    divTextTwo.style.textAlign="end";
    divTextTwo.style.color="black";
    divTextTwo.textContent = resp.resultFormat;
    divOne.appendChild(divTextTwo);
    div.appendChild(divOne);

    if(resp.typeText){
      const divK =  this.createElement(AON_TAG.DIV);
      divK.className = "aonFlexBetween colorGrey aonFontWeight-700";
      divK.style.margin = "20px 0";
      const divT =  this.createElement(AON_TAG.DIV);
      divT.innerHTML = `Tipo: <span style="color:black;"> ${resp.typeText}</span>`;
      divK.appendChild(divT);
      div.appendChild(divK);
    }

    //---FORM------
    const form =  this.createElement(AON_TAG.FORM);
    form.id = `${this.id}Form`;
    div.appendChild(form);

    const aonSelect = new AonSelect();
    aonSelect.name = "rbank";
    aonSelect.id = "iban";
    aonSelect.title = "IBAN";
    aonSelect.hidden = true;
    form.appendChild(aonSelect);

    const aonInputId = new AonInput();
    aonInputId.name = "id";
    aonInputId.id = "id";
    aonInputId.description = "id";
    aonInputId.value = resp.id;
    aonInputId.visible = false;
    form.appendChild(aonInputId);

    const divNrc =  this.createElement(AON_TAG.DIV);
    divNrc.hidden = true;
    divNrc.className= "aon-margin-0";
    divNrc.id= "divNrc";
    form.appendChild(divNrc);

    const aonSwitch = new AonSwitch();
    aonSwitch.className = "aonWidth25";
    aonSwitch.style.width= "26%";
    aonSwitch.id = "switchDni";
    aonSwitch.title = "NRC";
    divNrc.appendChild(aonSwitch);

    const aonInputNrc = new AonInput();
    aonInputNrc.className = "aonWidth75";
    aonInputNrc.style.width= "72%";
    aonInputNrc.id = "nrc";
    aonInputNrc.description = "Nº Referencia Completo";
    aonInputNrc.name = "nrc";
    aonInputNrc.type = "text";
    aonInputNrc.disabled = true;
    aonInputNrc.value = resp.nrc;
    divNrc.appendChild(aonInputNrc);

    //---END FORM---

    return div;
  }

  createFooterDialog(resp, dialog){
    let buttonAccept = dialog.addSendAction(() => this.save(resp,dialog));
    let divAction = this.getElement(dialog.ACTION);
    let div = this.createElement("div");
    let checkBox = new AonCheckbox();
    let span = this.createElement('span');
    span.style.color = "grey";
    span.style.fontSize= "12px";
    span.style.fontWeight= 500;
    span.textContent = "Acepto los datos reflejados";
    checkBox.id = this.DIALOG_CHECKBOX;
    checkBox.description = span.outerHTML;
    checkBox.addEventListener('change', ({target})=>{
      if(target.checked) buttonAccept.disabled = false;
      else buttonAccept.disabled = true;
    })
    div.appendChild(checkBox);
    divAction.insertBefore(div, buttonAccept);
    buttonAccept.disabled = true;
  }

  async getData() {
    let data = [];
    try {
      const datos = await getModelsFiscal();
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
            typeText:  TAX_ENUMS.TAX_TYPE[resp.type],
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

  getPathImg(administration){
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
      return path+src;
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


  async save(resp, dialog){
    this.applicationEl.startLoading();
    try {
      let form = {...resp,...this.getFormValues()};
      await setModelStatus(form);
      await this.getTable(); //reload
      this.applicationEl.getToast().start({message: "Datos guardados!", type:"success"});
      dialog.close();
    } catch (error) {
      if(typeof error ==="string") error = JSON.parse(error);
      const {message, type} = error;
      this.applicationEl.getToast().start({ message, type});
    }
    this.applicationEl.stopLoading();
  }

}

window.customElements.define("aon-tax", AonTax);
