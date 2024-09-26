import { AonElement } from "../../../components/AonElement.js";
import {
  getDomainUserRoles,
  openFileBase64,
  getAttach,
  getAeatCertificates,
  getFiscalModelsInvoinces,
  getEstimationModelsFiscalInvoinces,
  downloadInvoices,
  downloadInvoiceExcel,
  getSalaryPdf
} from "../../../services/service.js";
import { DomainUserRoles } from "../../../models/DomainUserRoles.js";

import "../../../components/aon-table.js";

import { EVENT, MSG, TAG } from "../../../environments/environments.js";
import * as ACTION from "../../actions.js";
import { FISCAL } from "../../../services/app.js";
import { formatNumber, serializeForm } from "../../../services/utils.js";
import { AonToolbar } from "../../../components/aon-toolbar.js";
import { ToolbarType } from "../../../models/enums.js";
import { DataAttachSource } from "../../../models/DataAttachSource.js";
import { AonTax } from "./aon-tax.js";
import { FiscalUtils } from "../FiscalUtils.js";
import { AonSelect } from "../../../components/aon-select.js";
import { AonInput } from "../../../components/aon-input.js";
import { AonNumber } from "../../../components/aon-number.js";
import { AonDate } from "../../../components/aon-date.js";
import { AonCheckbox } from "../../../components/aon-checkbox.js";
import { CONST_FISCAL } from "../FiscalEnums.js";
import { getDocumentNumber } from "../../invoice/Invoice.js";
import { AonTab } from "../../../components/aon-tab.js";
import { AonFutureTax } from "./aon-future-tax.js";
import { createList } from "../../../components/CreateComponent.js";

export class AonTaxDetail extends AonElement {
  TOOLBAR;
  TABS;
  CONTENT;
  INVOICE_TABLE;
  SALARY_TABLE;

  _roles;
  applicationEl;
  tax;
  type;

  // List of data
  salaries;
  invoices;

  /**
   * 
   * @param {Object} tax 
   * @param {Strung} type ("tax", "future")
   */
  constructor(tax, type) {
    super();
    console.log("AonTaxDetail");

    this.tax = tax;
    this.type = type
  }

  connectedCallback() {
    this.initialize();
    getDomainUserRoles({}).then((r) => {
      this._roles = new DomainUserRoles(r);
      this.build();
    });
  }

  initialize() {
    this.TOOLBAR = "taxDetailToolbar";
    this.TABS = "aonTaxDetailTabs";
    this.CONTENT = "aonTaxDetailContent";
    this.INVOICE_TABLE = "aonTaxDetailTable";
    this.SALARY_TABLE = "aonSalaryDetailTable";
  }

  build() {
    let toolbar = new AonToolbar();
    toolbar.id = this.TOOLBAR;
    toolbar.type = ToolbarType.SECONDARY;
    if(this.type == "tax")
      toolbar.title = this.tax.modelText + " (" +  this.tax.periodText + " " + this.tax.year + ")";
    else if(this.type == "future") 
      toolbar.title = this.tax.modelText + " (" +  this.tax.periodText + ")";

    this.appendChild(toolbar);

    if(["FINISHED", "SENT"].includes(this.tax.status)){
      toolbar.addButton2(ACTION.DOWNLOAD_PDF_2, () => this.getPdf(this.tax));
    }

    if(["CUSTOMER_CHECK"].includes(this.tax.status)){
      toolbar.addButton2(ACTION.ACCOUNT_BALANCE, () => this.openDialog(this.tax));
    }

    toolbar.addButton2(ACTION.BACK, () => this.back());

    this.initTabs();
    this.initContent();

    this.init();
  }

  initTabs(){
    let tab = new AonTab();
		tab.id = this.TABS;
		this.appendChild(tab);
  }

  initContent(){
    let content = this.createElement(TAG.DIV);
		content.id = this.CONTENT;
		content.style.width = "100%";
		this.appendChild(content);
  }

  createInvoiceTable(){
    this.removeInvoiceActions();

    let content = this.getElement(this.CONTENT);
		this.clearElement(content);

    let aonTaxDetailTable = createList(this.INVOICE_TABLE);
    aonTaxDetailTable.selectable = 'true';
    content.appendChild(aonTaxDetailTable);

    let aonTaxDetailTableBody = aonTaxDetailTable.getElementsByTagName("tbody")[0];
    aonTaxDetailTableBody.style.height = "calc(100vh - 240px)";

    aonTaxDetailTable.addColumn(MSG.DATE, 'date', 'dateTable', '120px');
    aonTaxDetailTable.addColumn(MSG.INVOICE_NUMBER, 'string', 'reference', '200px');
    aonTaxDetailTable.addColumn(MSG.HOLDER, 'string', 'name', 'auto');
    aonTaxDetailTable.addColumn(MSG.AMOUNT, 'number', 'totalParse', '100px');

    aonTaxDetailTable.addEventListener('select', () => {
			if(aonTaxDetailTable.selected.length === 1) {
				this.addInvoiceActions();
			} else if(aonTaxDetailTable.selected.length === 0){
				this.removeInvoiceActions();
			}
		});

    if(this.invoices){
      this.invoices.forEach(invoice => {
        if(!invoice.name){
          invoice.name = invoice.type === 'emitida'
            ? (invoice.receiver ? invoice.receiver.name : '')
            : (invoice.sender ? invoice.sender.name : '');
        }
        let date = new Date(invoice.date);
        let day = date.getDate();
        let month = date.getMonth() + 1;
        let year = date.getFullYear();
        invoice.dateTable = day.toString().zeros(2) + '/' + month.toString().zeros(2) + '/' + year.toString();
        invoice.documentNumber =  getDocumentNumber(invoice);
        invoice.totalParse = formatNumber(invoice.total, 2, "EUR");

        aonTaxDetailTable.addRow(invoice, () => {});
      });
    }
  }

  addInvoiceActions(){
    let toolbar = this.getElement(this.TOOLBAR);
    toolbar.addButton2End(ACTION.DOWNLOAD_INVOICE, () => this.downloadInvoices());
    toolbar.addButton2End(ACTION.DOWNLOAD_EXCEL_INVOICE, () => this.downloadInvoiceExcel());
  }

  removeInvoiceActions(){
    let toolbar = this.getElement(this.TOOLBAR);
    toolbar.removeButton(ACTION.DOWNLOAD_INVOICE.id);
    toolbar.removeButton(ACTION.DOWNLOAD_EXCEL_INVOICE.id);
  }

  downloadInvoices() {
		let aonInvoiceTable = this.getElement(this.INVOICE_TABLE);
		let data = {
			domainId: localStorage.getItem('aon_domain_id'),
			domainName: localStorage.getItem('aon_domain_name'),
			domainLogin: localStorage.getItem('aon_domain_login'),
			ids: aonInvoiceTable.selected.map(r => r.id),
      status : "accounting"
		};
		let json = btoa(JSON.stringify(data));
		downloadInvoices(json);
	}

  downloadInvoiceExcel() {
    let aonInvoiceTable = this.getElement(this.INVOICE_TABLE);
    let data = {
      domainId: localStorage.getItem("aon_domain_id"),
      domainName: localStorage.getItem("aon_domain_name"),
      domainLogin: localStorage.getItem("aon_domain_login"),
      ids: aonInvoiceTable.selected.map((r) => r.id)
    };
    let json = btoa(JSON.stringify(data));
    downloadInvoiceExcel(json);
  }

  createSalaryTable(){
    this.removeInvoiceActions();
    
    let content = this.getElement(this.CONTENT);
		this.clearElement(content);

    let aonSalaryDetailTable = createList(this.SALARY_TABLE);
    content.appendChild(aonSalaryDetailTable);

    let aonSalaryDetailTableBody = aonSalaryDetailTable.getElementsByTagName("tbody")[0];
    aonSalaryDetailTableBody.style.height = "calc(100vh - 240px)";

    aonSalaryDetailTable.addColumn(MSG.DATE, 'date', 'endDate', '120px');
    aonSalaryDetailTable.addColumn(MSG.EMPLOYEE, 'string', 'employeeName', 'auto');
    aonSalaryDetailTable.addColumn(MSG.AMOUNT, 'number', 'totalParse', '100px');

    if(this.salaries){
      this.salaries.forEach(salary => {
        salary.totalParse = formatNumber(salary.totalLiquid, 2, "EUR");
        aonSalaryDetailTable.addRow(salary, () => this.getSalary({salaryId : salary.id}));
      });
    }
  }

  async getSalary(data){
    this.getApplication().startLoading();
    try {
      await getSalaryPdf(data);
    } catch (error) {
      this.showToast(error);
    }
		this.getApplication().stopLoading();
  }

  async init() {
    this.getApplication().startLoader();

    let aonTab = this.getElement(this.TABS);
    console.log("Init");
    console.log(aonTab);
           
    if(this.type == "tax"){
      let filter = { fsModel : this.tax.id };
  
      getFiscalModelsInvoinces(filter).then(modelData => {

        this.salaries = modelData.salary;
        this.invoices = modelData.invoice;

        console.log("getFiscalModelsInvoinces");
        console.log(this.salaries);
        console.log(this.invoices);
      
        if(this.invoices && this.invoices.length > 0)
          aonTab.addOption({ title: `${MSG.INVOICES} (${this.invoices.length})`, fn: () => this.createInvoiceTable()});

        if(this.salaries && this.salaries.length > 0)
          aonTab.addOption({ title: `Nóminas (${this.salaries.length})`, fn: () => this.createSalaryTable()})

        if(this.invoices && this.invoices.length > 0)
          this.createInvoiceTable();
        else  if(this.salaries && this.salaries.length > 0)
          this.createSalaryTable();
        
        this.getApplication().stopLoader();
      });

    } else if(this.type == "future"){
      let filter = {
        year : this.tax.year,
        period : this.tax.period,
        type : this.getTypeByDescription(this.tax.description)
      }

      getEstimationModelsFiscalInvoinces(filter).then(modelData => {

        this.salaries = modelData.salary;
        this.invoices = modelData.invoice;

        console.log("getEstimationModelsFiscalInvoinces");
        console.log(this.salaries);
        console.log(this.invoices);

        if(this.invoices && this.invoices.length > 0)
          aonTab.addOption({ title: `${MSG.INVOICES} (${this.invoices.length})`, fn: () => this.createInvoiceTable()});

        if(this.salaries && this.salaries.length > 0)
          aonTab.addOption({ title: `Nóminas (${this.salaries.length})`, fn: () => this.createSalaryTable()})
       
        if(this.invoices && this.invoices.length > 0)
          this.createInvoiceTable();
        else  if(this.salaries && this.salaries.length > 0)
          this.createSalaryTable();

        // if(filter.type == "IRPF_ALAVA" || filter.type == "IRPF_BIZKAIA"  || filter.type == "IRPF_GIPUZKOA"  || filter.type == "IRPF_NAVARRA"  || filter.type == "IRPF_AEAT"){
        //   this.createSalaryTable();
        // } else {
        //   this.createInvoiceTable();
        // }

        this.getApplication().stopLoader();
      });
    }

  }

  getTypeByDescription(description){
    if(description){
      if(description.includes("IVA")) return "VAT";
      else if(description.includes("Alava")) return "IRPF_ALAVA";
      else if(description.includes("Bizkaia")) return "IRPF_BIZKAIA";
      else if(description.includes("Gipuzkoa")) return "IRPF_GIPUZKOA";
      else if(description.includes("Navarra")) return "IRPF_NAVARRA";
      else if(description.includes("AEAT")) return "IRPF_AEAT";
      else if(description.includes("Arrendamiento")) return "IRPF_ARRENDAMIENTO";
      else if(description.includes("Profesional")) return "IRFP_PROFESIONAL";
    }
    return null;
  }

  getAmount(sepaDetail) {
    return formatNumber(
      !sepaDetail.finance ? 0 : sepaDetail.finance.amount,
      2,
      "EUR"
    );
  }

  getTotal(sepaDoc) {
    if (!sepaDoc.fbatch_details || sepaDoc.fbatch_details.length === 0) {
      return formatNumber(0, 2, "EUR");
    }

    let total = sepaDoc.fbatch_details.reduce(
      (t, detail) => t + detail.amount,
      0
    );
    return formatNumber(total, 2, "EUR");
  }

  back() {
    //this.getApplication().setContent(new AonFiscal());
    if(this.type == "tax")
      this.getApplication().setContent(new AonTax());
    else if(this.type == "future") {
      let filter = {
        periodText: this.tax.periodText,
        year : this.tax.year,
        period : this.tax.period
      }
      this.getApplication().setContent(new AonFutureTax(FISCAL, filter));
    }
  }

  getPdf({ id: source_id, newModel }) {
    const source = DataAttachSource.getValueByName(newModel);
    if (!source) {
      this.showMessageError("DataAttachSource not found." + newModel);
      return;
    }

    getAttach({
      attachType: "data",
      file: true,
      source_id,
      source,
    })
      .then((r) => {
        if (r && r.id && r.contentType && r.content) {
          openFileBase64(r.content, r.contentType);
        } else {
          this.showMessageError("Declaración no encontrada!");
        }
      })
      .catch((error) => {
        this.showError(error);
      });
  }

  openDialog(resp) {
    this.applicationEl = this.getApplication();
    const dialog = this.applicationEl.getDialog();
    dialog.clear();
    if (!this.isMobile()) {
      dialog.width = "500px";
    }

    if (dialog.getButtonAccept()) dialog.getButtonAccept().remove();
    if (dialog.getButtonCancel()) dialog.getButtonCancel().remove();

    let div = this.builDialog(resp);
    dialog.setContent(div);

    if ("CUSTOMER_CHECK" === resp.status) {
      this.createFooterDialog(resp, dialog, div);
    } else {
      disabledForm(`${this.id}Form`, "aon-switch");
    }

    dialog.open();

    this.visibleFields(resp);

    this.eventData(resp);
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
    
    // Esto no aparece en el modelo 303 a ingresar, pues en el 303 se deja elegir el tipo de ingreso que se quiere hacer (para poder indicar aplazamiento) 
    if (resp.model != "IVA" || resp.result <= 0)
	    if(resp.typeText){
	      const divK =  this.createElement(TAG.DIV);
	      divK.classList.add("aonFlexBetween", "colorGrey", "aonFontWeight-700");
	      divK.style.margin = "20px 0";
	      const divT =  this.createElement(TAG.DIV);
	      divT.innerHTML = `${MSG.TYPE}: <span style="color:black;"> ${resp.typeText}</span>`;
	      divK.appendChild(divT);
	      div.appendChild(divK);
	    }

    //---FORM------
    const form = this.createElement(TAG.FORM);
    form.id = `${this.id}Form`;
    div.appendChild(form);
    
    // Para el Modelo 303 a ingresar, se deja elegir el tipo de ingreso
    if (resp.model == "IVA" && resp.result > 0) {
	    let types = [
			{ value: 'DEPOSIT', name: 'Ingreso'}, 
			{ value: 'BANK', name: 'Domiciliación'}, 
			{ value: 'DEPOSIT_CCT', name: 'Ingreso a anotar en CCT'},
			{ value: 'DEFERRAL', name: 'Solicitud de aplazamiento'}
		];
			
	    const aonSelectTipo = new AonSelect();
	    aonSelectTipo.name = "tipodec";
	    aonSelectTipo.id = "tipodec";
	    aonSelectTipo.title = "Tipo";
	    aonSelectTipo.setOptions(types)    
	    if (resp.type) aonSelectTipo.value = resp.type;
	    aonSelectTipo.addEventListener(EVENT.SELECT, () => {
					resp.type = aonSelectTipo.value;
					this.visibleFields(resp);
				});
	    form.appendChild(aonSelectTipo);
    }

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
    
    // NRC

    const divNrc = this.createElement(TAG.DIV);
    divNrc.hidden = true;
    divNrc.className = "aon-margin-0";
    divNrc.id = "divNrc";
    form.appendChild(divNrc);

    const aonInputNrc = new AonInput();
    aonInputNrc.className = "aonWidth75";
    aonInputNrc.style.width = "72%";
    aonInputNrc.id = "nrc";
    aonInputNrc.description = "NRC";
    aonInputNrc.name = "nrc";
    aonInputNrc.type = "text";
    if (resp.nrc) aonInputNrc.value = resp.nrc;
    divNrc.appendChild(aonInputNrc);
    
    // DATOS APLAZAMIENTO
    
    const divAplazamiento = this.createElement(TAG.DIV);
    divAplazamiento.hidden = true;
    divAplazamiento.className= "aon-margin-0";
    divAplazamiento.id= "divAplazamiento";
    form.appendChild(divAplazamiento);
    
    const aonInputPlazos = new AonNumber();
    aonInputPlazos.className = "aonWidth75";
    aonInputPlazos.id = "plazos";
    aonInputPlazos.description = "Número de Plazos";
    if (resp.plazos) aonInputPlazos.value = resp.plazos;  
    divAplazamiento.appendChild(aonInputPlazos);
    
    const aonInputFechaPlazo = new AonDate();
    aonInputFechaPlazo.className = "aonWidth75";
    aonInputFechaPlazo.id = "fechaPlazo";
	aonInputFechaPlazo.title = "Fecha Primer Plazo";
	aonInputFechaPlazo.readonly = ("CUSTOMER_CHECK"!==resp.status);	
	divAplazamiento.appendChild(aonInputFechaPlazo);
		
    // CERTIFICADO ELECTRONICO
    
    const aonSelect2 = new AonSelect();
    aonSelect2.name = "certi";
    aonSelect2.id = "certi";
    aonSelect2.title = "Certificado para la Presentación";
    aonSelect2.hidden =
      resp.presModelAuto == 0 || "CUSTOMER_CHECK" != resp.status;
    form.appendChild(aonSelect2);

    //---END FORM---

    return div;
  }

  createFooterDialog(resp, dialog, divMain) {
    let textArea = undefined;
    let buttonAccept = undefined;

    const div = this.createElement(TAG.DIV);
    div.style.textAlign = "right";
    const checkBox = new AonCheckbox();
    const span = this.createElement(TAG.SPAN);
    span.style.color = "grey";
    span.style.fontSize = "12px";
    span.style.fontWeight = "500";
    span.textContent = "Acepto los datos reflejados";
    checkBox.id = this.DIALOG_CHECKBOX;
    checkBox.description = span.outerHTML;
    checkBox.addEventListener(EVENT.CHANGE, ({ target }) => {
      buttonAccept.disabled = target.checked ? false : true;
    });

    div.appendChild(checkBox);

    // Si esta configurado presentacion automatica del modelo, mostrar texto informandolo (tambien se muestra si está en entorno de pruebas de la AEAT)
    if (resp.presModelAuto == 1) {
      const divTextPres = this.createElement(TAG.DIV, "divTextPres");
      divTextPres.style.marginTop = 6;
      divTextPres.style.textAlign = "center";
      divTextPres.style.fontWeight = "bold";
      if (resp.testEnvironment) {
        divTextPres.innerHTML =
          '<span style="color:red;">ENTORNO DE PRUEBAS DE LA AEAT</span><br>Si acepta los datos, el modelo se presentará automaticamente.';
      } else {
        divTextPres.innerHTML =
          '<span style="color:red;">PRESENTACION DEL MODELO</span><br>Si acepta los datos, el modelo se presentará automaticamente.';
      }
      div.appendChild(divTextPres);
    }

    divMain.appendChild(div);

    // Boton Rechazar
    const buttonCancel = dialog.addCancelAction(() =>{
      this.visibleFields(null);
      const tipodec = this.getElement('tipodec');
      if (tipodec) tipodec.disabled = true;
      div.innerHTML = "";
      textArea = new AonAutosizeTextarea();
      textArea.name = "reasonReject";
      textArea.title = "Motivo del rechazo";
      this.getElement(`${this.id}Form`).appendChild(textArea);

      buttonCancel.remove();
      buttonAccept.disabled = false;
    }, false);

    buttonCancel.innerHTML = "Rechazar";

    buttonAccept = dialog.addSendAction(() => {
      if (textArea) {
        const value = textArea.value;
        if (!value) return false;
      }
      this.save(resp).then(() => {
        dialog.close();
      });
    });

    buttonAccept.disabled = true;
    buttonCancel.style.padding = buttonAccept.style.padding = "0.5rem 1rem";
  }

  async eventData(resp) {
    const iban = this.getElement("iban");
    
    this.getApplicationParent()
      .getBanks()
      .then((result) => {
        if (result) {
          let options = result.map((r) => ({
            name: `${r.bank_account} - ${r.alias}`,
            value: `${this.replaceAllPoint(r.bank_account)}`,
          }));
          iban.setOptions(options);
          if (resp.iban) {
            iban.value = resp.iban;
          }
        }
      });

    const certi = this.getElement("certi");
    getAeatCertificates().then((certs) => {
      certi.setOptions(
        certs.map((s) => {
          return {
            value: s.id,
            name: s.name,
          };
        })
      );
      // Si solo hay un certificado, se muestra ese seleccionado por defecto
      if (certi.getOptions().length == 1) {
        certi.value = certi.getOptions()[0].value;
      }
		
	if (resp.fechaPlazo) {
		const fechaPlazo = this.getElement('fechaPlazo');		
		fechaPlazo.value = resp.fechaPlazo;			
	}	
		
    });
  }

  replaceAllPoint(str) {
    return String(str).replaceAll(".", "");
  }

  getFormValues() {

      let banks = this.getApplicationParent().BANKS;
      let formObj = serializeForm(this.getElement(`${this.id}Form`));
      if(!isEmptyObject(banks) && formObj.iban){
        const bankObj = banks.find(bank => this.replaceAllPoint(bank.bank_account) == formObj.iban);
        if(bankObj){
          formObj["bankAlias"] = bankObj.alias;
          formObj["bic"] = bankObj.bic;
        } 
      } 
      
      const nrc = this.getElement('nrc');
      formObj["nrc"] = nrc.value;      
      
      const certi = this.getElement('certi');
      formObj["certi"] = certi.value;
      
      const plazos = this.getElement('plazos');
      formObj["plazos"] = plazos.value;      
      const fechaPlazo = this.getElement('fechaPlazo');
      formObj["fechaPlazo"] = fechaPlazo.value;      
      
      return formObj;
      
  }

  visibleFields(resp){
	
    let iban = this.getElement("iban");
    let divNrc = this.getElement("divNrc");
    let divAplazamiento = this.getElement("divAplazamiento");
    let certi = this.getElement('certi');
    let divTextPres = this.getElement('divTextPres');
    let ibanHidden = true;      
    let nrcHidden = true;
    let aplazamientoHidden = true;
    let certiHidden = true;      
	
    if (resp) {
      certiHidden = (resp.presModelAuto==0 || "CUSTOMER_CHECK"!=resp.status);
      switch(resp.type){		
        case CONST_FISCAL.DEPOSIT:
          nrcHidden = false;
        break;
        case CONST_FISCAL.BANK:
        case CONST_FISCAL.PAYBACK:
          ibanHidden = false;
        break;
		case CONST_FISCAL.DEFERRAL:
          ibanHidden = false;
          aplazamientoHidden = false;
          certiHidden = true;
        break;        
      }      
    }
    
    iban.hidden = ibanHidden;
    divNrc.hidden = nrcHidden;
    divAplazamiento.hidden = aplazamientoHidden;      
    if (certi) certi.hidden = certiHidden;
    if (divTextPres) divTextPres.hidden = certiHidden;
    
  }

}
window.customElements.define("aon-tax-detail", AonTaxDetail);
