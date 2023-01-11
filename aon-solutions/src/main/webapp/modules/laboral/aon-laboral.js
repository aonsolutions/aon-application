import { AonApplication } from "../../components/aon-application.js";
import { MSG } from "../../environments/environments.js";
import Apps from "../../services/app.js";
import { getSalaryPdf } from "../../services/service.js";
import { AonComunicaUtils } from "./comunic@/aon-comunica-utils.js";
import { PayrollOptions, PAYROLL_VIEWS } from "./PayrollEnums.js";

export class AonLaboral extends AonComunicaUtils {

  initialize(){
    this.AON_LABORAL = PAYROLL_VIEWS.AON_LABORAL;
    this.title = this.title || MSG.PAYROLL;
    this._movements = [];
    this._filter = [];
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
  }

  buildToolbar(){
    if(this.isMobile()){
			this.getApplication().addMobileSidenavHeader(Apps.PAYROLL);
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

    this.getApplication().addSidenavOptions(MSG.PAYROLL.toUpperCase(), laboralOptions);

    if(this.isComunica() || !this.isEmployee()){
      let aon_cta_list = PayrollOptions.AON_CCC;
      aon_cta_list.fn = () =>{
        this.getApplication().removeToolbarOptions();
        this.showView(PAYROLL_VIEWS.AON_CTA_LIST);
      }
      conf.push(aon_cta_list);
      if(!this.isMobile()){
        let aon_cert = PayrollOptions.AON_CERT;
        aon_cert.fn = () => {
          this.getApplication().removeToolbarOptions();
          if(window.innerWidth && window.innerWidth < 900){ this.getApplication().closeSidenav(); }
          this.showView(PAYROLL_VIEWS.AON_CERT);
        }
        conf.push(aon_cert);
      }
      this.getApplication().addSidenavOptions(MSG.CONFIGURATION.toUpperCase(), conf);
    }

    let iconContract = this.getElement(this.getApplication().SIDENAV + PayrollOptions.AON_CONTRACT.name + "AonIcon");
    if(iconContract){iconContract.style.marginLeft = "3px";}
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
}
window.customElements.define('aon-laboral', AonLaboral);
