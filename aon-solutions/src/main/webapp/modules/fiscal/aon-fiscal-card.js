import { AonElement } from "../../components/AonElement.js";
import { getCompanyBanks, getDomainUserRoles } from "../../services/companyService.js";
import { isEmptyObject, serializeForm, waitEl, disabledForm, formatNumber } from "../../services/utils.js";
import { DomainUserRoles } from "../../models/DomainUserRoles.js";
import {  MSG, MATERIAL_ICONS } from "../../environments/environments.js";
import { FiscalOptions, FISCAL_VIEWS, TAX_ENUMS } from "./FiscalEnums.js";
import { getAttach, openFileBase64, setModelStatus } from "../../services/service.js";
import { AonTax } from "./tax/aon-tax.js";
import { AonApplication } from "../../components/aon-application.js";
import Apps from "../../services/app.js";
import { getModelsFiscal } from "../../services/fiscalService.js";
import { sortBy } from "../../services/utils.js";
import { FiscalUtils } from "./FiscalUtils.js";
import { SigninSidenav } from "../timecontrol/signinEnums.js";
import * as GWT from '../../gwt/gwt.js';
import { RETENTION_PANEL, VAT_PANEL } from "../invoice/InvoiceOptions.js";
import { AonCard } from "../../components/aon-card.js";
import { TAG } from "../../environments/environments.js";
import { AonTable } from "../../components/aon-table.js";

export class AonFiscalCard extends AonElement {
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
    this.AON_FISCAL = FISCAL_VIEWS.AON_FISCAL;
    this._filter = {
      year: undefined,
      period: undefined,
      model: undefined
    }
  }

  getDur() {
    return this.dur;
  }

  build() {
    this.paintView();
    this.buildToolbar();
  }

  paintView() {
    let aonTable = new AonTable();
    this.TABLE_ID = "fiscalCardTable";
    aonTable.id = this.TABLE_ID;
    this.appendChild(aonTable);
  }

  buildToolbar() {
    this.getModelsFiscal().then(mdls=>{
      this.showView(FISCAL_VIEWS.AON_TAX);
    })
  }

  async getModelsFiscal() {
    if(!this.MODELS.length){
      try {
        const datos = await getModelsFiscal();
        if (datos) {
          this.MODELS = sortBy(datos,'year','desc')
          .filter(({status})=>status!=="PENDING")
          .map((model) => FiscalUtils.getModelNew(model));
        }
      } catch (error) {
        console.error(error);
        this.showError(error);
      }
    }
    return this.MODELS;
  }

  async getBanks(){
    if(!this.BANKS.length){
      let result = await getCompanyBanks().catch(()=>null);
      if(result) {
        this.BANKS = result;
      }
    }
    return this.BANKS;
  }

  getFirstYear(models){
    let model = models.find(o => o.year);
    return model ? model.year : null;
  }

  orderBy(array, order='asc'){

  }

  getDataForKey(models, key){
    let datas = models.map(m => m[key]);
    return datas.filter((item, pos) => datas.indexOf(item) === pos);
  }

  getModelsNoRepeat(models){
    return models.filter((v,i)=>models.findIndex(v2=> v2.model===v.model)===i)
  }

  async showView(view, data, filter = undefined) {
    await this.getTable();
    document.querySelector("#fiscalCardTable table tbody").style.height = "auto";
  }

  async getTable() {
    await this.getTableDesk();
  }

  async getTableDesk() {
    const aonTable = this.getElement(this.TABLE_ID);
    if (aonTable) {
      aonTable.removeColumns();
      aonTable.addColumn("Modelo", "", "model", "25%");
      aonTable.addColumn("Ejercicio", "", "year", "25%");
      aonTable.addColumn("Periodo", "", "period", "25%");
      aonTable.addColumn("Importe", "number", "result", "25%");

      try {
        const resp = await this.getData();
        aonTable.removeRows();

        if(resp.length){
          resp.forEach((res) => {
            this.buildPrint(res);
            aonTable.addRow(res, () => {});
          });

          let row = aonTable.addRow({
            statusText:"Total",
            result:this.getTotal(resp)
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

  getTotal(models){
    let total = models.reduce((t, model) => t + model.result, 0);
    return formatNumber(total, 2, "EUR");
  }

  async getData() {
    let datos = await getModelsFiscal();
    let currentYear = new Date().getFullYear();
    let filterDatos = datos.filter(dato => { return dato.year == currentYear && dato.period === "T2" && dato.status === "SENT" });
    return filterDatos;
  }

  buildPrint(res){
    
    if(!["FINISHED", "SENT"].includes(res.status))
      return ;

    res.icon = MATERIAL_ICONS.PRINT;
    res.icon_color = "grey";
    res.fn = () => this.getPdf(res);
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
}
window.customElements.define("aon-fiscal-card", AonFiscalCard);
