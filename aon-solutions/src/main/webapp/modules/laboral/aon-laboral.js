import { AonElement } from "../../components/AonElement.js";
import { DomainUserRoles } from "../../models/DomainUserRoles.js";
import { getDomainUserRoles, getSalaryPdf } from "../../services/service.js";
import { formatDateOrigin, setValueName } from "../../services/utils.js";
import { AonPayrollList } from "./payroll/aon-payroll-list.js";
import { AonDocumentalList } from "../documental/aon-documental-list.js";
import { AonMobileDocumentalList } from "../documental/aon-mobile-documental-list.js";
import { PayrollOptions } from "./PayrollEnums.js";

class AonLaboral extends AonElement {

  AON_LABORAL;
  dur;
  _filter;

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
    this.showView("aonPayrollList");
  }

  paintView(){
    this.innerHTML = `
      <aon-application id="${this.AON_LABORAL}" title="LABORAL"></aon-application>`;
    this.aonLaboralEl = this.getApplication();
  }

  buildToolbar(){
    let paysheet = PayrollOptions.PAYSHEET;
    paysheet.fn = () => this.showView("aonPayrollList");
    let laboralOptions = [paysheet];
    if(!this.isEmployee()){
      let companyCosts = PayrollOptions.COMPANY_COSTS;
      companyCosts.fn = () =>  this.aonLaboralEl.development("Costes de empresa");
      laboralOptions.push(companyCosts);
    }
    let sepa = PayrollOptions.SEPA_FILES;
    sepa.fn = () => this.showView("aonSepaFilesList");
    laboralOptions.push(sepa);
    this.aonLaboralEl.addSidenavOptions('LABORAL', laboralOptions);
  }

  async getSalary(data){
    this.aonLaboralEl.startLoading();
		await getSalaryPdf(data);
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
    this.showView('aonSepaFilesList')
  }

  showView(view, data, filter = undefined){
    return new Promise(async(resolve)=>{
      let aonView = undefined;
      if(!this.getElement(view)){
        switch(view){
          case 'aonPayrollList':
            aonView = new AonPayrollList();
            break;
          case 'aonSepaFilesList':
            aonView = this.isMobile() ? new AonMobileDocumentalList() : new AonDocumentalList();
            aonView.setFilter({type: 'system'});
            break;
        }
        aonView.id = view;
        if(filter) aonView.filter = filter;
        this.aonLaboralEl.setContent(aonView);
      }
      resolve(true);
    });
  }

  isEmployee(){
    return !this.getDur().isPayrollManager() && !this.getDur().isPayrollPortal();
  }
}
window.customElements.define('aon-laboral', AonLaboral);
