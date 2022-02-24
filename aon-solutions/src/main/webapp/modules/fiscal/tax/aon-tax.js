import { AonElement } from "../../../components/AonElement.js";
import { formatNumber, isEmptyObject, serializeForm, waitEl, sortBy, disabledForm } from "../../../services/utils.js";
import { getCompanyBanks, getModelsFiscal, setModelStatus } from "../../../services/service.js";
import { CONST_FISCAL, TAX_ENUMS } from "../FiscalEnums.js";
import { AonCheckbox } from "../../../components/aon-checkbox.js";
import { AonSelect } from "../../../components/aon-select.js";
import { AonInput } from "../../../components/aon-input.js";
import { AonSwitch } from "../../../components/aon-switch.js";
import { EVENT, TAG,  MSG, CONSTANT } from "../../../environments/environments.js";
import { AonMobileList } from "../../../components/aon-mobile-list.js";
import { AonTable } from "../../../components/aon-table.js";

export class AonTax extends AonElement {
  TABLE_ID;
  BANKS;
  DIALOG_CHECKBOX;
  searchFilter;
  _list;
  static get observedAttributes() {
    return [];
  }

  get id() {
    return this.getAttribute(CONSTANT.ID);
  }

  set id(id) {
    this.setAttribute(CONSTANT.ID, id);
  }

  attributeChangedCallback(name, oldValue, newValue) {}

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
    this._list = [];
  }

  async build() {
    this.paintView();
    this.buildToolbar();
    await this.getTable();
    this.getBanks();
  }

  paintView() {
    let aonTable = this.isMobile() ? new AonMobileList() : new AonTable();
    aonTable.id = this.TABLE_ID;
    this.appendChild(aonTable);
  }

  buildToolbar() {
    this.applicationEl.removeToolbarOptions();
    this.buildToolbarSearch();
    // this.searchValueDefault();
  }

  buildToolbarSearch(){
    let btnSearch = this.applicationEl.addSearchOption();
    
    const searchFn = ({detail}) => {
      this.searchFilter = detail;
      this.search();
    }
    
    const searchValueFn = ({detail})=>{
      this._list = [];
      if(detail) console.log(detail);
    }

    btnSearch.addEventListener(EVENT.SEARCH, searchFn);
    btnSearch.addEventListener(EVENT.SEARCH_VALUE, searchValueFn);

    // btnSearch.buildOptionsFilter(PRESENCE_FILTER);//INPUTS

    let buttonSearchAccept = btnSearch.querySelector("div>button");
    if(buttonSearchAccept) buttonSearchAccept.disabled = true;
  }

  // searchValueDefault(){
  //   let periodEl = this.getElement("period");
  //   periodEl.options = JSON.stringify(getPeriodLaboral());
  //   periodEl.addEventListener(EVENT.CHANGE, ({detail}) => {
  //     if(detail){
  //       const {startDate, endDate} = detail;
  //       setValueName('startDate', startDate);
  //       setValueName('endDate', endDate);
  //     }
  //   });

  //   this.getElement("startDate").addEventListener(EVENT.CHANGE,()=>periodEl.value = "personalized");
  //   this.getElement("endDate").addEventListener(EVENT.CHANGE,()=>periodEl.value = "personalized");
  // }


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
          aonTable.addRow(res, () => this.openDialog(res));
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
          aonTable.addLi(options, idx, () => this.openDialog(res));
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
    } else {
      disabledForm(`${this.id}Form`, 'aon-switch');
    }
    dialog.open();
    this.visibleFields(resp);
    this.eventData(resp);
  }

  getDialogHtml(resp){
    const div = this.createElement(TAG.DIV);
    const divImg = this.createElement(TAG.DIV);
    divImg.style.fontSize= 18;
    const imgAeat = this.createElement(TAG.IMG);
    imgAeat.id = "imgAeat";
    imgAeat.src = this.getPathImg(resp.administration);
    divImg.appendChild(imgAeat);

    const spanTextImg =  this.createElement(TAG.SPAN);
    spanTextImg.style.marginLeft = 3;
    spanTextImg.textContent = `Modelo ${resp.newModel} (${resp.modelText})`;
    divImg.appendChild(spanTextImg);
    div.appendChild(divImg);

    const divOne = this.createElement(TAG.DIV);
    divOne.className = "aonFlexBetween colorGrey aonFontWeight-700";
    divOne.style.margin= "10px 0";

    const divTextOne =  this.createElement(TAG.DIV);
    divTextOne.textContent = `${resp.periodText} - ${resp.year}`;
    divOne.appendChild(divTextOne);

    const divTextTwo =  this.createElement(TAG.DIV);
    divTextTwo.style.textAlign="end";
    divTextTwo.style.color="black";
    divTextTwo.textContent = resp.resultFormat;
    divOne.appendChild(divTextTwo);
    div.appendChild(divOne);

    if(resp.typeText){
      const divK =  this.createElement(TAG.DIV);
      divK.className = "aonFlexBetween colorGrey aonFontWeight-700";
      divK.style.margin = "20px 0";
      const divT =  this.createElement(TAG.DIV);
      divT.innerHTML = `Tipo: <span style="color:black;"> ${resp.typeText}</span>`;
      divK.appendChild(divT);
      div.appendChild(divK);
    }

    //---FORM------
    const form =  this.createElement(TAG.FORM);
    form.id = `${this.id}Form`;
    div.appendChild(form);

    const aonSelect = new AonSelect();
    aonSelect.name = "iban";
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

    const divNrc =  this.createElement(TAG.DIV);
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
    aonInputNrc.description = "Nº Ref Completo";
    aonInputNrc.name = "nrc";
    aonInputNrc.type = "text";
    aonInputNrc.disabled = true;
    if(resp.nrc) aonInputNrc.value = resp.nrc;
    divNrc.appendChild(aonInputNrc);
    //---END FORM---

    return div;
  }

  createFooterDialog(resp, dialog){
    const buttonAccept = dialog.addSendAction(() =>{
      this.save(resp).then(()=>{
        dialog.close();
      });
    });
    const divAction = this.getElement(dialog.ACTION);
    const div = this.createElement(TAG.DIV);
    const checkBox = new AonCheckbox();
    const span = this.createElement(TAG.SPAN);
    span.style.color = "grey";
    span.style.fontSize = "12px";
    span.style.fontWeight= 500;
    span.textContent = "Acepto los datos reflejados";
    checkBox.id = this.DIALOG_CHECKBOX;
    checkBox.description = span.outerHTML;
    checkBox.addEventListener(EVENT.CHANGE, ({target})=>{
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
      if(this._list.length){
        data = this._list;
      } else {
        const datos = await getModelsFiscal();
        if (datos) {
          data = sortBy(datos,'year','desc').filter(el=>"PENDING"!==el.status).map((resp) => {
            let newModel = TAX_ENUMS.TAX_MODEL_NUMBER[resp.model];
            let color = "";
            if("PENDING"===resp.status)       color = "fin";
            else if("FINISHED"===resp.status) color = "in";

            const lettersHtml = /*html*/`<div class="profile-letters size ${color}">${
              TAX_ENUMS.TAX_MODEL_NUMBER[resp.model]
            }</div>`;
            return {
              ...resp,
              lettersHtml,
              resultFormat:formatNumber(resp.result, 2, "EUR"),
              periodText: TAX_ENUMS.TAX_PERIOD[resp.period],
              statusText: TAX_ENUMS.TAX_STATUS[resp.status],
              modelText: TAX_ENUMS.TAX_MODEL_TEXT[newModel],
              typeText:  TAX_ENUMS.TAX_TYPE[resp.type],
              newModel
            }
          });
        }
        this._list = data;
        if(this.searchFilter) data = this.filterSearch(["periodText", "statusText", "modelText", "newModel", "model"], data);
      }
    } catch (e) {
      console.log(e);
    }
    return data;
  }

  eventData(resp){
    this.getElement('switchDni').addEventListener(EVENT.CHANGE, ({ target }) => {
        let nrc = this.getElement("nrc");
        if(nrc) nrc.disabled = !target.checked;
        if(!target.checked) nrc.value ="";
    });

    const iban = this.getElement('iban');
    this.getBanks().then(result=>{
      if(result){
        let options = result.map(r=> ({
          name: `${r.bankAccount} - ${r.alias}`,
          value: `${this.replaceAllPoint(r.bankAccount)}`
        }));
        iban.options = JSON.stringify(options);
        if(resp.iban) iban.value = resp.iban;
      }
    })
  }

  replaceAllPoint(str){
    return String(str).replaceAll(".","");
  }

  getFormValues() {
      let formObj = serializeForm(this.getElement(`${this.id}Form`));
      if(!isEmptyObject(this.BANKS) && formObj.iban){
        const bankObj = this.BANKS.find(bank =>  this.replaceAllPoint(bank.bankAccount) === formObj.iban);
        if(bankObj){
          formObj["bankAlias"] = bankObj.alias;
          formObj["bankBic"] = bankObj.bic;
        }
      }
      return formObj;
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
      const {TAX_ADMIN} = TAX_ENUMS;
      let src = "aeat.png";
      switch(administration){
        case TAX_ADMIN.ALAVA:
          src = "aeat_alava.png";
          break;
        case TAX_ADMIN.BIZKAIA:
          src = "aeat_biskaia.png";
          break;
        case TAX_ADMIN.GIPUZKOA:
          src = "aeat_gipuzcoa.png";
          break;
        case TAX_ADMIN.NAVARRA:
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
        case CONST_FISCAL.DEPOSIT:
          ibanHidden = nrcHidden = false;
        break;
        case CONST_FISCAL.BANK:
        case CONST_FISCAL.PAYBACK:
          ibanHidden = false;
        break;
      }
      iban.hidden  = ibanHidden;
      divNrc.hidden  = nrcHidden;
    }
  }

  async save(resp){
    this.applicationEl.startLoading();
    try {
      const form = {...resp,...this.getFormValues()};
      await setModelStatus(form);
      await this.getTable();
      this.showToast({message: MSG.SAVED_DATA, type: CONSTANT.SUCCESS});
    } catch (error) {
      this.showToast(error);
    }
    this.applicationEl.stopLoading();
  }


  search(){
    this._list = this.filterSearch(["periodText", "statusText", "modelText", "newModel", "model"], this._list);
    this.getTable();
  }

  filterSearch(keys, lists){
    let list = [];
    if(this.searchFilter && lists.length){
      list = lists.filter((lt)=> keys.some(key=>lt[key] && lt[key].toString().toLowerCase().includes(this.searchFilter.toLowerCase())));
    }
    return list;
  }

}

window.customElements.define("aon-tax", AonTax);
