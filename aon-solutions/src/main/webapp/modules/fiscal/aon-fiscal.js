import { AonElement } from "../../components/AonElement.js";
import { getCompanyBanks, getDomainUserRoles } from "../../services/companyService.js";
import { DomainUserRoles } from "../../models/DomainUserRoles.js";
import {  MSG, MATERIAL_ICONS } from "../../environments/environments.js";
import { FiscalOptions, FISCAL_VIEWS, TAX_ENUMS } from "./FiscalEnums.js";
import { AonTax } from "./tax/aon-tax.js";
import { AonApplication } from "../../components/aon-application.js";
import Apps from "../../services/app.js";
import { getModelsFiscal } from "../../services/fiscalService.js";
import { sortBy } from "../../services/utils.js";
import { FiscalUtils } from "./FiscalUtils.js";
import { SigninSidenav } from "../timecontrol/signinEnums.js";
import * as GWT from '../../gwt/gwt.js';
import { RETENTION_PANEL, VAT_PANEL } from "../invoice/InvoiceOptions.js";

export class AonFiscal extends AonElement {
  AON_FISCAL;
  MODELS = [];
  BANKS=[];
  dur;
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
    this.createApplication(this.AON_FISCAL, MSG.FISCAL, new AonApplication());
    this.applicationEl = this.getApplication();
  }

  buildToolbar() {

    let application = this.getApplication();

    application.addToolbarOption2({...SigninSidenav.SYNCHRONIZE, name:MSG.UPDATE}, () =>
			this.rootPanel(new AonFiscal())
		);

    if(this.isMobile()){
			application.addMobileSidenavHeader(Apps.FISCAL);
		} else {
    
      application.addToolbarOption2(VAT_PANEL, () =>{
        application.closeSidenav();
        this.showView(FISCAL_VIEWS.VAT_PANEL);
      });
  
      application.addToolbarOption2(RETENTION_PANEL, () =>{
        application.closeSidenav();
        this.showView(FISCAL_VIEWS.IRPF_REPORT);
      });
  
      application.addTitleToolSection("Estimaciones");
    }

    this.getModelsFiscal().then(mdls=>{
      let firstYear = this.getFirstYear(mdls);
      if(firstYear){
        this._filter.year = firstYear;
      }

      let ejercicios = this.getDataForKey(mdls, 'year')
      .map(year=> ({
        ...FiscalOptions.AON_TAX, 
        id:year,
        icon: MATERIAL_ICONS.EVENT,
        name:year,
        clickable:true,
        fn: ()=>{
          this._filter.year = year;
          this.addBackgroundSidenav();
          this.showView(FISCAL_VIEWS.AON_TAX);
        },
      }));

      
    let data = {
			id: "Ejercicio",
			title:"Ejercicio",
  		name:"Ejercicio",
      color: Apps.FISCAL.color
		};

      application.addSidenavOptions2(data, ejercicios);


      let periods = this.getDataForKey(mdls, 'period')
      .sort()
      .map(period=> ({
        ...FiscalOptions.AON_TAX, 
        id:period,
        icon: MATERIAL_ICONS.EVENT,
        name:TAX_ENUMS.TAX_PERIOD[period],
        clickable:true,
        fn: ()=>{
          this._filter.period = this._filter.period  === period ? undefined : period;
          this.addBackgroundSidenav();
          this.showView(FISCAL_VIEWS.AON_TAX);
        },
      }));

     
      let data2 = {
        id: "Periodo",
        title:"Periodo",
        name:"Periodo",
        color: Apps.FISCAL.color
      };
      application.addSidenavOptions2(data2, periods);

      let models = this.getModelsNoRepeat(mdls).map(model=> ({
          ...FiscalOptions.AON_TAX, 
          id:model.model,
          icon:undefined,
          img: FiscalUtils.getPathImg(model.administration),
          name: model.modelText,
          fn: () =>{
            this._filter.model = this._filter.model  === model.model ? undefined : model.model;
            this.addBackgroundSidenav();
            this.showView(FISCAL_VIEWS.AON_TAX);
          }
      }));

      let data3 = {
        id: "Modelo",
        title:"Modelo",
        name:"Modelo",
        color: Apps.FISCAL.color
      };
      application.addSidenavOptions(data3, models);

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

  addBackgroundSidenav(){
    const application = this.getApplication();
    const filter = this._filter;
		if(filter){
			application.removeBackgroundSidenavAll();
			if(filter.year){
				application.addBackgroundSidenav(filter.year);
			} 
      if(filter.period){
				application.addBackgroundSidenav(filter.period);
			} 
      if(filter.model){
				application.addBackgroundSidenav(filter.model);
			} 
		}
	}

  showView(view, data, filter = undefined) {
    return new Promise(async (resolve) => {
      let aonView = undefined;
      switch (view) {
        case FISCAL_VIEWS.AON_TAX:
          aonView = new AonTax();
        break;
        case FISCAL_VIEWS.VAT_PANEL:
				  GWT.load(GWT.VAT_REPORT, this.applicationEl.CONTENT);
				break;
        case FISCAL_VIEWS.IRPF_REPORT:
          GWT.load(GWT.IRPF_REPORT, this.applicationEl.CONTENT);
        break;
      }
      if (aonView) {
        aonView.id = view;
        if (filter) aonView.filter = filter;
        if(data) aonView.data = data;
        this.applicationEl.setContent(aonView);
      }
      resolve(aonView);
    });
  }
}
window.customElements.define("aon-fiscal", AonFiscal);
