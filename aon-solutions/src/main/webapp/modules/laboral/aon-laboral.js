import { AonElement } from "../../components/AonElement.js";
import { DomainUserRoles } from "../../models/DomainUserRoles.js";
import { getContratoPdf, getDomainUserRoles, getIDC, getSalaryPdf, getTA, postDeleteMov } from "../../services/service.js";
import { addDays, formatDateOrigin, setValueName } from "../../services/utils.js";
import { AonPayrollList } from "./payroll/aon-payroll-list.js";
import { AonDocumentalList } from "../documental/aon-documental-list.js";
import { AonMobileDocumentalList } from "../documental/aon-mobile-documental-list.js";
import { PayrollOptions, PAYROLL_VIEWS } from "./PayrollEnums.js";
import { startModule } from "../../services/gwtLoader.js";
import { AonContractList } from "./payroll/aon-contract-list.js";
import { AonMovements } from "./comunic@/aon-movements.js";
import { AonCtaList } from "./comunic@/cta/aon-cta-list.js";

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
    this.AON_LABORAL = "aonLaboral";
  }

  getDur() {
    return this.dur;  
  }

  build() {
    if(!this.isEmployee()){
      let now = new Date();
      this._filter = {
        period: "personalized",
        startDate: formatDateOrigin(new Date(now.getFullYear(), (now.getMonth() -1), 1)),
        endDate: formatDateOrigin(new Date(now.getFullYear(), now.getMonth() + 1, 0))
      };
    }
    this.paintView();
    this.buildToolbar();
    this.showView(PAYROLL_VIEWS.AON_PAYROLL_LIST);
  }

  paintView(){
    this.innerHTML = `
      <aon-application id="${this.AON_LABORAL}" title="LABORAL"></aon-application>`;
    this.aonLaboralEl = this.getApplication();
  }

  buildToolbar(){
    let laboralOptions = [];
    let conf = [];
    let paysheet = PayrollOptions.PAYSHEET;
    paysheet.fn = () => this.showView(PAYROLL_VIEWS.AON_PAYROLL_LIST);
    laboralOptions.push(paysheet);

    if(!this.isEmployee()){

      let contract = PayrollOptions.AON_CONTRACT;
      contract.fn = () => this.showView(PAYROLL_VIEWS.AON_CONTRACT_LIST);
      laboralOptions.push(contract);

      let companyCosts = PayrollOptions.COMPANY_COSTS;
      companyCosts.fn = () =>  this.aonLaboralEl.development("Costes de empresa");
      laboralOptions.push(companyCosts);

      let sepa = PayrollOptions.SEPA_FILES;
      sepa.fn = () => this.showView(PAYROLL_VIEWS.AON_SEPA_FILES_LIST);
      laboralOptions.push(sepa);

      let aon_comunica = PayrollOptions.AON_COMUNICA;
      aon_comunica.fn = () => this.showView(PAYROLL_VIEWS.AON_MOVEMENTS);
      laboralOptions.push(aon_comunica);


      let aon_cta_list = PayrollOptions.AON_CCC;
      aon_cta_list.fn = () => this.showView(PAYROLL_VIEWS.AON_CTA_LIST);
      conf.push(aon_cta_list);
    }

    this.aonLaboralEl.addSidenavOptions('LABORAL', laboralOptions);

    if(conf.length > 0){this.aonLaboralEl.addSidenavOptions('CONFIGURACIÓN', conf);}
    
    let iconContract = this.getElement(this.aonLaboralEl.SIDENAV + PayrollOptions.AON_CONTRACT.name + "AonIcon");
    if(iconContract){iconContract.style.marginLeft = "3px";}


  }

  async getSalary(data){
    this.aonLaboralEl.startLoading();
    try {
      await getSalaryPdf(data);
    } catch (error) {
      this.aonLaboralEl.getToast().start({ message: error, type: 'error' });
    }

		this.aonLaboralEl.stopLoading();
  }

  async setDataFilter(data){
    try {
      this._filter = {...this._filter, ...data};
      this.aonLaboralEl.getChild().filter = true;
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


  aonDocumentalList(){
    this.showView(PAYROLL_VIEWS.AON_SEPA_FILES_LIST);
  }

  removeToolbarOptions() {
    let application = this.getApplication();
    let toolbar = this.getElement(application.TOOLBAR);
    toolbar.removeButtons();
  }

  getOptions(res) {
		let option = [
			{
				id: 'Ta',
				name: 'Obtener TA',
				aonIcon: 'aon_ta',
				fn: (el) => this.getTa(res, el)
			},
			{
				id: 'Idc',
				name: 'Obtener IDC',
				aonIcon: 'aon_idc',
				fn: (el) => this.getIdc(res, el)
			}
		];
		if (this.anularCondition(res.situation, res.fra)) {
			option.push({
				id: 'Delete',
				name: 'Anular',
				icon: 'delete_forever',
				fn: (el) => this.deleteMov(res, el)
			});
		}
		return option;
	}

  async getContratoPdf(data, el) {
    this.aonLaboralEl.startLoading();
    try {
      const { document: ipf, startDate: fecha } = data;
      await getContratoPdf({ ipf, fecha });
    } catch ({message, type}) {
			if(message && type) this.aonLaboralEl.getToast().start({ message, type});
		}
    this.aonLaboralEl.stopLoading();
  }

  async getTa(data, el) {
		this.aonLaboralEl.startLoading();
		try {
			const { regime, ctaCti, nss, fra } = data;
			await getTA({ regime, ctaCti, nss, fra }); // open pdf
		} catch ({message, type}) {
			if(message && type) this.aonLaboralEl.getToast().start({ message, type});
		}
		this.aonLaboralEl.stopLoading();
	}

  anularCondition(situation, fra) {
		const date_prev = addDays(new Date(), -2);
		// const sit = ["AL", "BJ", "BAJA", "ALTA"];
		// (situation.indexOf(sit) > -1) &&
		return (date_prev.getTime() <= new Date(fra).getTime());
	}

	async getIdc(data, el) {
		this.aonLaboralEl.startLoading();
		try {
			const { regime, ctaCti, nss, fra } = data;
			await getIDC({ regime, ctaCti, nss, fra }); // open pdf
		} catch ({message, type}) {
			if(message && type) this.aonLaboralEl.getToast().start({ message, type});
		}
    this.aonLaboralEl.stopLoading();
  }

  async deleteMov(data, el) {
    this.aonLaboralEl.confirmDialog("Anular movimiento", `Estas seguro de anular el movimiento de ${data.name} ?`, async() => {
        this.aonLaboralEl.startLoader();
        try {
          await postDeleteMov(data);
          this.aonLaboralEl.getToast().start({ message: `${data.situation == "AL" ? "Alta" : "Baja"} eliminada!` });
          if (el) el.remove(); //delete td
        } catch ({message, type}) {
          if(message && type) this.aonLaboralEl.getToast().start({ message, type});
        }
      this.aonLaboralEl.stopLoader();
    });
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
            this.removeToolbarOptions();
            aonView = this.isMobile() ? new AonMobileDocumentalList() : new AonDocumentalList();
            aonView.setFilter({type: 'system'});
            break;
          case PAYROLL_VIEWS.AON_CONTRACT_LIST:
            this.aonLaboralEl.removeToolbarOptions();
            if(this.isMobile()){
              aonView = new AonContractList();
            }else {
              startModule('aon_gwt_payroll', 'MainContrata', this.aonLaboralEl.CONTENT);
            }
            break;
          case PAYROLL_VIEWS.AON_MOVEMENTS:
            aonView = new AonMovements();
            break;
          case PAYROLL_VIEWS.AON_CERT:
            this.aonLaboralEl.removeToolbarOptions();
            startModule('aon_gwt_payroll', 'MainDigitalCertificates', this.aonLaboralEl.CONTENT);
            break;
          case PAYROLL_VIEWS.AON_CTA_LIST:
            this.aonLaboralEl.removeToolbarOptions();
            aonView = new AonCtaList();
            break;
        }
        if(aonView){
          aonView.id = view;
          if(filter) aonView.filter = filter;
          this.aonLaboralEl.setContent(aonView);
        }
      }
      resolve(true);
    });
  }

  isEmployee(){
    return !this.getDur().isPayrollManager() && !this.getDur().isPayrollPortal();
  }
}
window.customElements.define('aon-laboral', AonLaboral);
