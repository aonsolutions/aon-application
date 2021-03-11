import { AonElement } from "../../components/AonElement.js";
import { DomainUserRoles } from "../../models/DomainUserRoles.js";
import { getDomainUserRoles, getSalaryPdf } from "../../services/service.js";
import { formatDateOrigin, setValueName } from "../../services/utils.js";
import { AonPayrollList } from "./payroll/aon-payroll-list.js";

class AonLaboral extends AonElement {
    AON_LABORAL;
    _roles;
    _filter;
    constructor () {
        super();
    }
    
    connectedCallback () {
        this.initialize();
        getDomainUserRoles({reload:true}).then(r=>{
            this._roles = new DomainUserRoles(r);
            this.build();
          })
    }

    initialize(){
        this.AON_LABORAL = "aonLaboral";
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
        let laboralOptions = [
            {
                name: 'Nóminas',
                icon: 'text_snippet',
                fn: () => this.showView("aonPayrollList")
            }
        ];
        if(!this.isEmployee()){
          laboralOptions.push({
            name: 'Costes de empresa',
            icon: 'assignment',
            fn: () =>  this.aonLaboralEl.development("Costes de empresa")
          });
        } 
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

  showView(view, data, filter = undefined){
    return new Promise(async(resolve)=>{
      let aonView = undefined;
      if(!this.getElement(view)){
        switch(view){
          case "aonPayrollList":
            aonView = new AonPayrollList();
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
    return !this._roles.isPayrollManager() && !this._roles.isPayrollPortal();
  }

}
window.customElements.define('aon-laboral', AonLaboral);
