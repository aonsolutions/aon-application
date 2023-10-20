import { AonElement } from "../../../components/AonElement.js";
import { isEmptyObject, serializeForm, waitEl, disabledForm, formatNumber } from "../../../services/utils.js";
import { getAttach, openFileBase64, setModelStatus, getAeatCertificates } from "../../../services/service.js";
import { CONST_FISCAL } from "../FiscalEnums.js";
import { AonCheckbox } from "../../../components/aon-checkbox.js";
import { AonSelect } from "../../../components/aon-select.js";
import { AonInput } from "../../../components/aon-input.js";
import { AonSwitch } from "../../../components/aon-switch.js";
import { EVENT, TAG,  MSG, CONSTANT, MATERIAL_ICONS } from "../../../environments/environments.js";
import { AonMobileList } from "../../../components/aon-mobile-list.js";
import { AonTable } from "../../../components/aon-table.js";
import { FiscalUtils } from "../FiscalUtils.js";
import { AonAutosizeTextarea } from "../../../components/aon-autosize-textarea.js";
import { DataAttachSource } from "../../../models/DataAttachSource.js";
import { FISCAL } from "../../../services/app.js";
// import { Attach } from "../../../models/Attach.js";

export class AonTax extends AonElement {
	
  ERROR_TEMPLATE_START = "<html>"
		+ "<head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"/></head>"
		+ "<body>";
  ERROR_TEMPLATE_AEAT = "<div style=\""
		+ "font-family: arial, 'lucida Grande', 'Trebuchet MS', sans-serif;"
		+ "font-weight: bold;"
		+ "margin-top: 20px;"
		+ "\">"
		+ "La Agencia Tributaria devolvió los siguientes mensajes de error, en la presentación del modelo:"
		+ "</div>";
  ERROR_TEMPLATE_BEFORE = "<html>"
		+ "<ul style=\""
		+ "background-attachment: scroll;"
		+ "background-clip: border-box;"
		+ "background-position: 3px 2px;"
		+ "background-repeat: no-repeat;"
		+ "background-size: auto auto;"
		+ "background-color: #ffd0d0;"
		+ "border: solid black 1px;"
		+ "font-size: small;"
		+ "font-family: arial, 'lucida Grande', 'Trebuchet MS', sans-serif;"
		+ "font-weight: bold;"
		+ "border: solid black 1px;"
		+ "padding-top: 20px;"
		+ "padding-bottom: 20px;"
		+ "\">";
  ERROR_TEMPLATE_AFTER = "</ul>";
  ERROR_TEMPLATE_END = "</body></html>";	
	
  TABLE_ID;
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
    this.applicationEl.addToolbarTitle("Impuestos");
    this._list = [];
  }

  async build() {
    this.paintView();
    this.buildToolbar();
    await this.getTable();
    this.getApplicationParent().getBanks();
  }

  paintView() {
    let aonTable = this.isMobile() ? new AonMobileList() : new AonTable();
    aonTable.id = this.TABLE_ID;
    aonTable.setApp(FISCAL);
    this.appendChild(aonTable);
  }

  buildToolbar() {
  }


  async getTable() {
    this.applicationEl = await waitEl("#aonFiscal");
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
      aonTable.addColumn("", "string", "lettersHtml", "6%");
      aonTable.addColumn("Modelo", "", "modelText", "20%");
      aonTable.addColumn("Ejercicio", "", "year", "10%");
      aonTable.addColumn("Periodo", "", "periodText", "10%");
      aonTable.addColumn("Estado", "", "statusText", "10%");
      aonTable.addColumn("Importe", "number", "resultFormat", "8%");
      aonTable.addColumn("", 'icon', 'icon', '5%');

      try {
        const resp = await this.getData();
        aonTable.removeRows();

        if(resp.length){
          resp.forEach((res) => {
            this.buildPrint(res);
            aonTable.addRow(res, () => this.openDialog(res));
          });

          let row = aonTable.addRow({
            statusText:"Total",
            resultFormat:this.getTotal(resp)
          });
          row.style.fontWeight = "600";
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
            title: `${res.model}`,
            subtitle: `${res.periodText} - ${res.year}`,
          };
          aonTable.addLi(options, idx, () => this.openDialog(res));
        });

        aonTable.addLi({
          title:"Total",
          iconHtmlCustom: /*html*/ `<span style="float: right;color: black;font-weight: 600; margin-top: 10px;">${this.getTotal(resp)}</span>`,
        });
      } catch (e) {
        console.log(e);
      }
    }
  }

  async getData() {
    const applicationParent = this.getApplicationParent();
    let filter = applicationParent._filter;
    let datos = await applicationParent.getModelsFiscal();

    if(filter.year){
      datos = datos.filter(({year})=> year==filter.year );
    } 

    if(filter.period){
      datos = datos.filter(({period})=> period==filter.period );
    } 

    if(filter.model){
      datos = datos.filter(({model})=> model==filter.model );
    }
  
    return datos;
  }


  getTotal(models){
    let total = models.reduce((t, model) => t + model.result, 0);
    return formatNumber(total, 2, "EUR");
  }

  openDialog(resp) {	
    const dialog = this.applicationEl.getDialog();
    dialog.clear();    
    if (!this.isMobile()){ 
      dialog.width = "500px";
    }
    
    if (dialog.getButtonAccept())
       dialog.getButtonAccept().remove();
    if (dialog.getButtonCancel())
       dialog.getButtonCancel().remove();

    let div = this.builDialog(resp);
    dialog.setContent(div);

    if("CUSTOMER_CHECK"===resp.status){
      this.createFooterDialog(resp, dialog, div);
    } else {
      disabledForm(`${this.id}Form`, 'aon-switch');
    }

    dialog.open();

    this.visibleFields(resp);
    
    this.eventData(resp);
  }

  builDialog(resp){
    const div = this.createElement(TAG.DIV);
    const divImg = this.createElement(TAG.DIV);
    divImg.style.fontSize = 18;
    divImg.appendChild(FiscalUtils.createImgAdmin(resp.administration));

    const spanTextImg =  this.createElement(TAG.SPAN);
    spanTextImg.style.marginLeft = 3;
    spanTextImg.textContent = `${MSG.MODEL} ${resp.newModel} (${resp.modelText})`;
    divImg.appendChild(spanTextImg);
    div.appendChild(divImg);

    const divOne = this.createElement(TAG.DIV);
    divOne.className = "aonFlexBetween colorGrey aonFontWeight-700";
    divOne.style.margin= "10px 0";

    const divTextOne =  this.createElement(TAG.DIV);
    divTextOne.textContent = `${resp.periodText} - ${resp.year}`;
    divOne.appendChild(divTextOne);

    const divTextTwo =  this.createElement(TAG.DIV);
    divTextTwo.style.textAlign = "end";
    divTextTwo.style.color = "black";
    divTextTwo.textContent = resp.resultFormat;
    divOne.appendChild(divTextTwo);
    div.appendChild(divOne);

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

    //const aonSwitch = new AonSwitch();
    //aonSwitch.className = "aonWidth25";
    //aonSwitch.style.width= "26%";
    //aonSwitch.id = "switchDni";
    //aonSwitch.title = "NRC";
    //divNrc.appendChild(aonSwitch);

    const aonInputNrc = new AonInput();
    aonInputNrc.className = "aonWidth75";
    aonInputNrc.style.width= "72%";
    aonInputNrc.id = "nrc";
    //aonInputNrc.description = "Nº Ref Completo";
    aonInputNrc.description = "NRC";
    aonInputNrc.name = "nrc";
    aonInputNrc.type = "text";
    //aonInputNrc.disabled = true;
    if(resp.nrc) aonInputNrc.value = resp.nrc;
    divNrc.appendChild(aonInputNrc);
    
    const aonSelect2 = new AonSelect();
    aonSelect2.name = "certi";
    aonSelect2.id = "certi";
    aonSelect2.title = "Certificado para la Presentación";
    aonSelect2.hidden = (resp.presModelAuto==0 || "CUSTOMER_CHECK"!=resp.status);
    form.appendChild(aonSelect2);
    
    //---END FORM---

    return div;
  }

  createFooterDialog(resp, dialog, divMain){

    let textArea = undefined;
    let buttonAccept = undefined;

    const div = this.createElement(TAG.DIV);
    div.style.textAlign = "right";
    const checkBox = new AonCheckbox();
    const span = this.createElement(TAG.SPAN);
    span.style.color = "grey";
    span.style.fontSize  = "12px";
    span.style.fontWeight= "500";
    span.textContent = "Acepto los datos reflejados";
    checkBox.id = this.DIALOG_CHECKBOX;
    checkBox.description = span.outerHTML;
    checkBox.addEventListener(EVENT.CHANGE, ({target})=>{
      buttonAccept.disabled = target.checked ? false : true;
    })

    div.appendChild(checkBox);
    
    // Si esta configurado presentacion automatica del modelo, mostrar texto informandolo (tambien se muestra si está en entorno de pruebas de la AEAT)
	if (resp.presModelAuto==1) {
	    const divTextPres =  this.createElement(TAG.DIV);
	    divTextPres.style.marginTop = 6; 
	    divTextPres.style.textAlign = "center";
	    divTextPres.style.fontWeight= "bold";
	    if (resp.testEnvironment) {	       
	       divTextPres.innerHTML = '<span style="color:red;">ENTORNO DE PRUEBAS DE LA AEAT</span><br>Si acepta los datos, el modelo se presentará automaticamente.';
	    }
	    else { 
	       divTextPres.innerHTML = '<span style="color:red;">PRESENTACION DEL MODELO</span><br>Si acepta los datos, el modelo se presentará automaticamente.';
	    }
	    div.appendChild(divTextPres);
	     
    }    
    
    divMain.appendChild(div);

    const buttonCancel = dialog.addCancelAction(() =>{
      this.visibleFields({type:"d"})
      div.innerHTML = "";
      textArea = new AonAutosizeTextarea();
      textArea.name = "reasonReject";
      textArea.title = "Motivo del rechazo";
      this.getElement(`${this.id}Form`).appendChild(textArea);

      buttonCancel.remove();
      buttonAccept.disabled = false;

    }, false);

    buttonCancel.innerHTML = "Rechazar";

    buttonAccept = dialog.addSendAction(() =>{

      if(textArea){
        const value = textArea.value;
        if(!value) return false;
      } 
      this.save(resp).then(()=>{
        dialog.close();        
      });
    });

    buttonAccept.disabled = true;
    buttonCancel.style.padding = buttonAccept.style.padding = "0.5rem 1rem";
  }

  eventData(resp){
    //this.getElement('switchDni').addEventListener(EVENT.CHANGE, ({ target }) => {
    //    let nrc = this.getElement("nrc");
    //    if(nrc) {
    //      nrc.disabled = !target.checked;
    //    }
    //    if(!target.checked) {
    //      nrc.value ="";
    //    }
    //});

    const iban = this.getElement('iban');
    this.getApplicationParent()
    .getBanks().then(result=>{
      if(result){
        let options = result.map(r=> ({
          name: `${r.bank_account} - ${r.alias}`,
          value: `${this.replaceAllPoint(r.bank_account)}`
        }));
        iban.setOptions(options);
        if(resp.iban) {
          iban.value = resp.iban;
        }
      }
    })
   
    const certi = this.getElement('certi');    
    getAeatCertificates().then(certs => {		
			certi.setOptions(certs.map(s => {
				return {
				  value: s.id,
				  name: s.name
				}
			  }));
			// Si solo hay un certificado, se muestra ese seleccionado por defecto			  
			if (certi.getOptions().length == 1) {
				certi.value = certi.getOptions()[0].value;
			}
		});
    
  }

  replaceAllPoint(str){
    return String(str).replaceAll(".","");
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
      
      return formObj;
  }

  visibleFields({type,status}){
    if(type){
      let iban = this.getElement("iban");
      let divNrc = this.getElement("divNrc");
      let ibanHidden = true;
      let nrcHidden  = true;
      switch(type){
        case CONST_FISCAL.DEPOSIT:
          // ibanHidden = false; // El IBAN no se necesita en el ingreso, solo se necesita el NRC 
          //nrcHidden = "CUSTOMER_CHECK"!=status;
          nrcHidden = false;
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

  buildPrint(res){
    
    if(!["FINISHED", "SENT"].includes(res.status))
      return ;

    res.icon = MATERIAL_ICONS.PRINT;
    res.icon_color = "grey";
    res.fn = () => this.getPdf(res);
  }

  async save(resp){
	  
    this.applicationEl.startLoading();
    try {
		
      const form = {...resp,...this.getFormValues()};
      this.clearModels();
      await setModelStatus(form);
      await this.getTable();      
      this.showMessage();
      
    } catch (error) {
      
      console.error(error);
          
      // Controlar los posibles errores    
      let errorJson = JSON.parse(error);      
      if (errorJson) {
		  if (errorJson.class_name == "AonApiAeatError") {
			  
			  // La Agencia Tributaria ha devuelto errores en la presentación del modelo, mostramos los errores
			  
			  let errorMessages = JSON.parse(errorJson.message);
			  
			  if (this.isMobile()) {
				  
				  // MOBILE: Se muestran los mensajes de error escondiendo la tabla de modelos
				  
				  const aonTable = this.getElement(this.TABLE_ID);
				  if (aonTable) {
					  aonTable.hidden = true;  
				  }
				  
				  const aeatErrorsDiv = this.createElement(TAG.DIV);

				  let text = '<span style="margin: 5px;color:red;font-weight: bold;font-size: medium;text-align: center; display: block">PRESENTACION DEL MODELO<br>Mensajes de Error devueltos por la Agencia Tributaria<br><hr></span>' +
         					  '<ul style="margin:5px;color:black;font-size: small;text-align: left;">';

				  errorMessages.respuesta.errores.forEach((res) => {
					  text = text + `<li>${res}</li>`;
				  });

				  text = text + '</ul>';

				  aeatErrorsDiv.innerHTML = text;
				  this.appendChild(aeatErrorsDiv);
				  
			  } else {
				  
				  // DESKTOP: Se muestran los mensajes de error en una pestaña nueva del navegador
			  
			  	  let text = this.ERROR_TEMPLATE_START + 
			                 this.ERROR_TEMPLATE_AEAT + 
			                 this.ERROR_TEMPLATE_BEFORE;
			                 
			      errorMessages.respuesta.errores.forEach((res) => {
					  text = text + "<li>" + res + "</li>";
				  }); 	  
			                 
			      text = text + this.ERROR_TEMPLATE_AFTER +
			                    this.ERROR_TEMPLATE_END;
			      let file = new Blob([text], { type: "text/html" });
      			  let url = URL.createObjectURL(file);
      	          window.open(url, '_blank');
      	          await this.getTable();
				  
			  }
			  		  
		  } else if (errorJson.class_name == "AonApiAeatException") {
			  
			  // La presentación del modelo no se ha producido correctamente
			  
			  if (this.isMobile()) {
				  
				  // MOBILE: Simplemente se muestra error en la presentación del modelo				  
				  await this.getTable();
				  this.showMessageError("ERROR EN LA PRESENTACIÓN DEL MODELO");
				  
			  } else {
				  
				  // DESKTOP: Se intenta mostrar en una ventana nueva del navegador el documento HTML devuelto en la llamada a la presentación				  
				  let text = errorJson.message;
			      let file = new Blob([text], { type: "text/html" });
      			  let url = URL.createObjectURL(file);
      	          window.open(url, '_blank');
      	          await this.getTable();
				  
			  }
			  
		  } else {
			  
			  // Otros errores no relacionados directamente con la llamada a la presentación del modelo
			  
			  await this.getTable();
			  this.showMessageError(errorJson.message);
			  			  
		  }		  
	  } else {
		this.showMessageError(error);  
	  }
    }     
    this.applicationEl.stopLoading();
  }

  getPdf({id:source_id, newModel}){
    const source = DataAttachSource.getValueByName(newModel);
    if(!source) {
      this.showMessageError("DataAttachSource not found."+ newModel);
      return;
    }

    getAttach({
      attachType: 'data',
      file:true,
      source_id,
      source,
    })
    .then(r=>{      
      if(r && r.id && r.contentType && r.content){
        openFileBase64(r.content, r.contentType);
      } else {
        this.showMessageError("Declaración no encontrada!");
      }
      // const attach = new Attach(r);
      // const data = {
      //   domain_id: attach.getDomain().getId(),
      //   attach_type: attach.getAttachType(),
      //   domain_name: attach.getDomain().getName(),
      //   id: attach.getId()
      // };
      // openFileUrl(location.href + 'ms/api/file/' + btoa(JSON.stringify(data)), attach.getContentType());
    })
    .catch(error=>{ 
      this.showError(error);
    });
  }

  clearModels(){
    this.getApplicationParent().MODELS = [];
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