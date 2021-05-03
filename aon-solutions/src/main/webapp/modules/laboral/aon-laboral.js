import { AonElement } from "../../components/AonElement.js";
import { DomainUserRoles } from "../../models/DomainUserRoles.js";
import { getContratoPdf, getDomainUserRoles, getIDC, getSalaryPdf, getTA, postDeleteMov } from "../../services/service.js";
import { setValueName } from "../../services/utils.js";
import { AonPayrollList } from "./payroll/aon-payroll-list.js";
import { AonDocumentalList } from "../documental/aon-documental-list.js";
import { AonMobileDocumentalList } from "../documental/aon-mobile-documental-list.js";
import { PayrollOptions, PAYROLL_VIEWS } from "./PayrollEnums.js";
import { AonContractList } from "./payroll/aon-contract-list.js";
import { AonMovements } from "./comunic@/aon-movements.js";
import { AonMovementsList } from "./comunic@/aon-movements-list.js";
import { AonAltaDirecta } from "./comunic@/aon-alta-directa.js";
import { AonCompanyCostsList } from "./company/aon-company-costs-list.js";
import { MSG } from "../../environments/environments.js";
import { AonApplication } from "../../components/aon-application.js";
import { CONTRACT_OPTIONS } from "./PayrollEnums.js";
import { AonCtaList } from "./comunic@/cta/aon-cta-list.js";
import * as GWT from '../../gwt/gwt.js';


class AonLaboral extends AonElement {

  AON_LABORAL;
  dur;
  _filter;
	_roles;
	MOVEMENTS;
	_movements;
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
  }

  getDur() {
    return this.dur;  
  }

  build() {
    this.paintView();
    this.buildToolbar();
    if(this.isEmployee() && !this.getDur().isComunica()){
      this.showView(PAYROLL_VIEWS.AON_PAYROLL_LIST);
    } else if(this.isComunicaNotPayroll() ){
      this.showView(PAYROLL_VIEWS.AON_MOVEMENTS);
    } else if(this.getDur().isPayroll()) {
      this.showView(PAYROLL_VIEWS.AON_COMPANY_COSTS_LIST);
    }
  }

  paintView(){
    this.createApplication(this.AON_LABORAL, MSG.PAYROLL, new AonApplication());
    this.applicationEl = this.getApplication();
  }

  buildToolbar(){
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
      aon_comunica.fn = () => this.showView(PAYROLL_VIEWS.AON_MOVEMENTS);
      laboralOptions.push(aon_comunica);
    }

    this.applicationEl.addSidenavOptions(MSG.PAYROLL, laboralOptions);

    if(this.isComunica() || !this.isEmployee()){
      let aon_cta_list = PayrollOptions.AON_CCC;
      aon_cta_list.fn = () =>  this.showView(PAYROLL_VIEWS.AON_CTA_LIST);
      conf.push(aon_cta_list);
      if(!this.isMobile()){
        let aon_cert = PayrollOptions.AON_CERT;
        aon_cert.fn = () => {
          this.applicationEl.removeToolbarOptions();
          this.showView(PAYROLL_VIEWS.AON_CERT);
        }
        conf.push(aon_cert);
      }
      this.applicationEl.addSidenavOptions(MSG.CONFIGURATION, conf);
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
		let option = [
			{
				...CONTRACT_OPTIONS.TA,
				fn: (el) => this.getTa(res, el)
			},
			{
				...CONTRACT_OPTIONS.IDC,
				fn: (el) => this.getIdc(res, el)
			}
		];
		if (this.anularCondition(res.situation, res.fra)) {
			option.push({
				...CONTRACT_OPTIONS.DELETE,
				fn: (el) => this.deleteMov(res, el)
			});
		}
		return option;
	}

  async getContratoPdf(data, el) {
    this.applicationEl.startLoading();
    try {
      const { document: ipf, startDate: fecha } = data;
      await getContratoPdf({ ipf, fecha });
    } catch (error) {
      this.showToast(error);
		}
    this.applicationEl.stopLoading();
  }

  async getTa(data, el) {
		this.applicationEl.startLoading();
		try {
			const { regime, ctaCti, nss, fra } = data;
			await getTA({ regime, ctaCti, nss, fra }); // open pdf
		} catch (error) {
      this.showToast(error);
		}
		this.applicationEl.stopLoading();
	}

  anularCondition(situation, fra) {
		const date_prev = new Date().addDay(-2);
		// const sit = ["AL", "BJ", "BAJA", "ALTA"];
		// (situation.indexOf(sit) > -1) &&
		return (date_prev.getTime() <= new Date(fra).getTime());
	}

	async getIdc(data, el) {
		this.applicationEl.startLoading();
		try {
			const { regime, ctaCti, nss, fra } = data;
			await getIDC({ regime, ctaCti, nss, fra }); // open pdf
		} catch (error) {
      this.showToast(error);
		}
    this.applicationEl.stopLoading();
  }

  async deleteMov(data, el) {
    this.applicationEl.confirmDialog("Anular movimiento", `Estas seguro de anular el movimiento de ${data.name} ?`, async() => {
        this.applicationEl.startLoading();
        try {
          await postDeleteMov(data);
          this.showToast({ message: `${data.situation == "AL" ? "Alta" : "Baja"} eliminada!` });
          if(this._movements)this._movements = this._movements.filter(({ctaCti,fra,ipf,nss,regime,situation}) => !(ctaCti.includes(data.ctaCti) && fra.includes(data.fra) && ipf.includes(data.ipf) && nss.includes(data.nss) && regime.includes(data.regime) && situation.includes(data.situation)))
          this.showView(PAYROLL_VIEWS.AON_MOVEMENTS);
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
        obj.type="ATRASOS";
        obj.color = "pause";
      break;
    }
    if(obj.type) obj.typeReduce = obj.type.toString().substr(0,1);
    return obj;
  }

  showView(view, data, filter = undefined){
    return new Promise(async(resolve)=>{
      let aonView = undefined;
      if(!this.getElement(view)){
        switch(view){
          case PAYROLL_VIEWS.AON_PAYROLL_LIST:
            aonView = new AonPayrollList();
            break;
          case PAYROLL_VIEWS.AON_SEPA_FILES_LIST:
            aonView = this.isMobile() ? new AonMobileDocumentalList() : new AonDocumentalList();
            aonView.setFilter({type: 'system'});
            break;
          case PAYROLL_VIEWS.AON_CONTRACT_LIST:
            if(this.isMobile()){
              aonView = new AonContractList();
            } else {
              GWT.load(GWT.MAIN_CONTRATA, this.applicationEl.CONTENT);
            }
            break;
          case PAYROLL_VIEWS.AON_MOVEMENTS:
            aonView = new AonMovements();
            break;
          case PAYROLL_VIEWS.AON_CERT:
              GWT.load(GWT.MAIN_DIGITAL_CERTIFICATES, this.applicationEl.CONTENT);
            break;
          case PAYROLL_VIEWS.AON_CTA_LIST:
            if(this.isMobile()){
                aonView = new AonCtaList();
            } else {
                GWT.load(GWT.MAIN_CCC, this.applicationEl.CONTENT);
            }
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
          this.applicationEl.setContent(aonView);
        }
      }
      resolve(aonView);
    });
  }

  isComunica(){
    return this.getDur().isComunicaManager() || this.getDur().isComunicaPortal();
  }

  isEmployee(){
    return !this.getDur().isPayrollManager() && !this.getDur().isPayrollPortal();
  }

  isComunicaNotPayroll(){
    return this.isComunica() && !this.getDur().isPayroll();
  }
}
window.customElements.define('aon-laboral', AonLaboral);
