import { AonElement } from "../../components/AonElement.js";
import { DomainUserRoles } from "../../models/DomainUserRoles.js";
import { getDomainUserRoles, getIDC, getSalaryPdf, getTA, movDelete, updateContracts } from "../../services/service.js";
import { setValueName, waitEl } from "../../services/utils.js";
import { AonPayrollList } from "./payroll/aon-payroll-list.js";
import { AonDocumentalList } from "../documental/aon-documental-list.js";
import { AonMobileDocumentalList } from "../documental/aon-mobile-documental-list.js";
import { PayrollOptions, PAYROLL_VIEWS, CONTRACT_OPTIONS } from "./PayrollEnums.js";
import { AonContractList } from "./payroll/aon-contract-list.js";
import { AonMovementsList } from "./comunic@/aon-movements-list.js";
import { AonAltaDirecta } from "./comunic@/aon-alta-directa.js";
import { AonCompanyCostsList } from "./company/aon-company-costs-list.js";
import { MSG, CONSTANT } from "../../environments/environments.js";
import { AonApplication } from "../../components/aon-application.js";
import { AonCtaList } from "./cta/aon-cta-list.js";
import * as GWT from '../../gwt/gwt.js';
import { AonDateUtils } from '../utils/AonDateUtils.js';
import Apps from "../../services/app.js";

export class AonLaboral extends AonElement {

  AON_LABORAL;
  dur;
  _filter;
	MOVEMENTS;
	_movements;

  static get observedAttributes() {
    return [CONSTANT.TITLE];
  }

  get title() {
    return this.getAttribute(CONSTANT.TITLE);
  }

  set title(title) {
    this.setAttribute(CONSTANT.TITLE, title);
  }

  constructor () {
    super();
  }

  connectedCallback () {
    this.initialize();
    getDomainUserRoles({reload:true}).then(r=>{
      this.dur = new DomainUserRoles(r);
      this.build();
    });
  }


  initialize(){
    this.AON_LABORAL = PAYROLL_VIEWS.AON_LABORAL;
    this.title = this.title || MSG.PAYROLL;
    this._movements = [];
    this._filter = [];
  }

  getDur() {
    return this.dur;  
  }

  build() {
    this.paintView();
    this.buildToolbar();
    if(this.isEmployee() && !this.isComunica()){
      this.showView(PAYROLL_VIEWS.AON_PAYROLL_LIST);
    } else if(this.isComunicaNotPayroll() ){
      this.showView(PAYROLL_VIEWS.AON_MOVEMENTS_LIST);
    } else if(this.isPayroll() && !this.isEmployee()) {
      this.showView(PAYROLL_VIEWS.AON_COMPANY_COSTS_LIST);
    }
  }

  paintView(){
    this.createApplication(this.AON_LABORAL, this.title, new AonApplication());
    this.applicationEl = this.getApplication();
  }

  buildToolbar(){
    if(this.isMobile()){
			this.applicationEl.addMobileSidenavHeader(Apps.PAYROLL);
    }

    let laboralOptions = [];
    let conf = [];
    if(!this.isComunicaNotPayroll()){
      let paysheet = PayrollOptions.PAYSHEET;
      paysheet.fn = () => this.showView(PAYROLL_VIEWS.AON_PAYROLL_LIST);
      laboralOptions.push(paysheet);
    }

    if(!this.isEmployee()){
      let contract = PayrollOptions.AON_CONTRACT;
      contract.fn = () => this.showView(PAYROLL_VIEWS.AON_CONTRACT_LIST);
      laboralOptions.push(contract);

      let companyCosts = PayrollOptions.COMPANY_COSTS;
      companyCosts.fn = () =>  this.showView(PAYROLL_VIEWS.AON_COMPANY_COSTS_LIST);
      laboralOptions.push(companyCosts);

      let sepa = PayrollOptions.SEPA_FILES;
      sepa.fn = () => {
        this.removeToolbarOptions();
        this.showView(PAYROLL_VIEWS.AON_SEPA_FILES_LIST);
      }
      laboralOptions.push(sepa);
    }

    if(this.isComunica()){
      let aon_comunica = PayrollOptions.AON_COMUNICA;
      aon_comunica.fn = () => this.showView(PAYROLL_VIEWS.AON_MOVEMENTS_LIST);
      laboralOptions.push(aon_comunica);
    }

    this.applicationEl.addSidenavOptions(MSG.PAYROLL.toUpperCase(), laboralOptions);

    if(this.isComunica() || !this.isEmployee()){
      let aon_cta_list = PayrollOptions.AON_CCC;
      aon_cta_list.fn = () =>{
        this.applicationEl.removeToolbarOptions();
        this.showView(PAYROLL_VIEWS.AON_CTA_LIST);
      }
      conf.push(aon_cta_list);
      if(!this.isMobile()){
        let aon_cert = PayrollOptions.AON_CERT;
        aon_cert.fn = () => {
          this.applicationEl.removeToolbarOptions();
          if(window.innerWidth && window.innerWidth < 900){ this.applicationEl.closeSidenav(); }
          this.showView(PAYROLL_VIEWS.AON_CERT);
        }
        conf.push(aon_cert);
      }
      this.applicationEl.addSidenavOptions(MSG.CONFIGURATION.toUpperCase(), conf);
    }

    let iconContract = this.getElement(this.applicationEl.SIDENAV + PayrollOptions.AON_CONTRACT.name + "AonIcon");
    if(iconContract){iconContract.style.marginLeft = "3px";}
  }

  async getSalary(data){
    this.applicationEl.startLoading();
    try {
      await getSalaryPdf(data);
    } catch (error) {
      this.showToast(error);
    }
		this.applicationEl.stopLoading();
  }

  async setDataFilter(data){
    try {
      this._filter = {...this._filter, ...data};
      this.applicationEl.getChild().filter = true;
    } catch (error) {}
  }

  changeFilter(){
    try {
      const filterParent = this._filter;
      for(const obj in filterParent){
        setValueName(obj, filterParent[obj]);
      }
    } catch (error) {}
  }

  removeToolbarOptions() {
    let application = this.getApplication();
    let toolbar = this.getElement(application.TOOLBAR);
    if(toolbar)toolbar.removeButtons();
  }

  getOptions(res) {
    let option = [];
    option.push({
      ...CONTRACT_OPTIONS.TA,
      fn: () => this.getTa(res)
    });
    if(!res.prev){
      option.push({
				...CONTRACT_OPTIONS.IDC,
				fn: () => this.getIdc(res)
		  });
    }
		if (this.anularCondition(res.situation, res.fra)) {
			option.push({
				...CONTRACT_OPTIONS.DELETE,
				fn: (el) => this.deleteMov(res, el)
			});
		}
		return option;
	}

  async getTa({ regime, ctaCti, nss, fra, frb }) {
		this.applicationEl.startLoading();
		try {
      let newData = { regime, ctaCti, nss, fra };
      if(frb) newData["frb"] = frb;
			await getTA(newData); // open pdf
		} catch (error) {
      this.showToast(error);
		}
		this.applicationEl.stopLoading();
	}

  async getIdc({ regime, ctaCti, nss, fea, feb, fra, frb }) {
		this.applicationEl.startLoading();
		try {
      let fecha = feb || fea;
      if(!fecha) 
        fecha = frb || fra;
			await getIDC({ regime, ctaCti, nss, fra:fecha }); // open pdf
		} catch (error) {
      this.showToast(error);
		}
    this.applicationEl.stopLoading();
  }

  anularCondition(situation, fecha) {
		const date_prev = new Date().addDay(-2);
		// const sit = ["AL", "BJ", "BAJA", "ALTA"];
		// (situation.indexOf(sit) > -1) &&
		return (date_prev.getTime() <= new Date(fecha).getTime());
	}

  async deleteMov(data, el) {
    this.applicationEl.confirmDialog(MSG.DELETE, `${MSG.DELETE_CONFIRM} el movimiento de ${data.name} ?`, async() => {
        this.applicationEl.startLoading();
        try {
          await movDelete({
            ...data,
            nombre: data.nombre || data.name
          });
          this.showToast({ message: `${data.situation == "AL" ? "Alta" : "Baja"} eliminada!` });
          if(this._movements.length){
            this._movements = this._movements.filter(({ctaCti,fra,frb,ipf,nss,regime,situation}) => {
              const dtFecha = data.frb || data.fra;
              const fecha = frb || fra;
              return !(ctaCti.includes(data.ctaCti) && fecha.includes(dtFecha) && ipf.includes(data.ipf) && nss.includes(data.nss) && regime.includes(data.regime) && situation.includes(data.situation))
            });
          }
          this.showView(PAYROLL_VIEWS.AON_MOVEMENTS_LIST);
        } catch (error) {
          this.showToast(error);
        }
      this.applicationEl.stopLoading();
    });
  }

  getTypeSalaryText(type){
    let obj = {color:"",  type:"" ,typeReduce:""};
    switch(type){
      case "SALARY":
        obj.type="NOMINA";
      break;
      case "EXTRA":
        obj.type="EXTRA";
        obj.color = "in";
      break;
      case "SETTLE":
        obj.type="FINIQUITO";
        obj.color = "fin";
      break;
      case "DELAY":
        obj.type = "ATRASOS";
        obj.color = "pause";
      break;
    }
    if(obj.type) obj.typeReduce = obj.type.toString().substr(0,1);
    return obj;
  }

  async updateContracts(){
    this.applicationEl.startLoader();
    //SINCRONIZED INIT YEAR
    await updateContracts({employeesOld:true, employeesPrev:true, startDate: AonDateUtils.formatDateOrigin( new Date().addMonth(-3)) }).catch(e=>console.log("erros",e));
    console.log("----------UPDATE CONTRACTS------");
    this.applicationEl.stopLoader();
  }

  showView(view, data = undefined, filter = undefined){
    return new Promise(async(resolve)=>{
      let aonView = undefined;
        switch(view){
          case PAYROLL_VIEWS.AON_PAYROLL_LIST:
            aonView = new AonPayrollList();
            break;
          case PAYROLL_VIEWS.AON_SEPA_FILES_LIST:
            aonView = this.isMobile() ? new AonMobileDocumentalList() : new AonDocumentalList();
            aonView.setFilter({type: 'system'});
            break;
          case PAYROLL_VIEWS.AON_CONTRACT_LIST:
            if(this.isMobile()) 
              aonView = new AonContractList();
            else
              this.goContractDesk();
            break;
          case PAYROLL_VIEWS.AON_CERT:
            this.loadGwt(GWT.MAIN_DIGITAL_CERTIFICATES);
            break;
          case PAYROLL_VIEWS.AON_CTA_LIST:
            if(this.isMobile())
              aonView = new AonCtaList();
            else 
              this.loadGwt(GWT.MAIN_CCC);
            break;
          case PAYROLL_VIEWS.AON_MOVEMENTS_LIST:
            aonView = new AonMovementsList();
            break;
          case PAYROLL_VIEWS.AON_ALTA_DIRECTA:
            aonView = new AonAltaDirecta();
            break;
          case PAYROLL_VIEWS.AON_COMPANY_COSTS_LIST:
          aonView = new AonCompanyCostsList();
            break;
        }
        if(aonView){
          aonView.id = view;
          if(filter) aonView.filter = filter;
          if(data) aonView.data = data;
          this.applicationEl.setContent(aonView);
        }
      resolve(aonView);
    });
  }

  goContractDesk(){
    this.getApplication().removeToolbarOptions();

    this.loadGwt(GWT.MAIN_CONTRATA);

    this.getApplication().closeSidenav();
  }

  loadGwt(module){

    let application = this.getApplication();
    
    this.clearElementById(application.CONTENT);

    application.startLoader();

    GWT.load(module, application.CONTENT);

    waitEl(`#${application.CONTENT} .aon_toolbar`).finally(()=> {
      application.stopLoader();
      this.fixSpacing();
    });
  }

  fixSpacing() {
    if (!document.querySelector("aon-module")) 
      waitEl(`#${this.getApplication().getContent().id} div:first-child`).then(el => el.style.position = "").catch(err => console.log(err));        
  }
    
  isComunica(){
    return this.getDur().isComunica();
  }

  isEmployee(){
    return !this.getDur().isPayrollManager() && !this.getDur().isPayrollPortal();
  }

  isPayroll(){
    return this.getDur().isPayroll();
  }

  isComunicaNotPayroll(){
    return this.isComunica() && !this.isPayroll();
  }
}
window.customElements.define('aon-laboral', AonLaboral);
