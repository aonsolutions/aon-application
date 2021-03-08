import { AonElement } from "../../components/AonElement.js";
import { DomainUserRoles } from "../../models/DomainUserRoles.js";
import { getDomainUserRoles, getSalaryPdf } from "../../services/service.js";
import { setValueName } from "../../services/utils.js";
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
        this.paintView();
        this.buildToolbar();
        this.showView("aonPayrollList");
    }
    
    paintView(){
        this.innerHTML = `
        <aon-application id="${this.AON_LABORAL}" title="LABORAL"></aon-application>`;
        this.aonLaboralEl = this.getElement(this.AON_LABORAL);
    }

    buildToolbar(){

        let aonLaboral = document.getElementById(this.AON_LABORAL);

        let employerOptions = [
            {
                name: 'Nóminas',
                icon: 'text_snippet',
                fn: () => this.showView("aonPayrollList")
            }
        ];
        aonLaboral.addSidenavOptions('EMPLEADOS', employerOptions);

        // let actionsOptions = [
        //     {
        //         name: 'Crear alta',
        //         icon: 'event_available',
        //         fn: () => this.loadNew('alta')
        //     },
        //     {
        //         name: 'Crear baja',
        //         icon: 'event_busy',
        //         fn: () => this.loadNew('baja')
        //     }
        // ];
        // aonLaboral.addSidenavOptions('ACCIONES', actionsOptions);
        // getSalaryPdf({salaryId:39777});
    }

    changeFilter(){
      try {
        const filterParent = this._filter;
        for(const obj in filterParent){
          setValueName(obj, filterParent[obj]);
        }
      } catch (error) {}
    }

  async getSalary(data){
    this.aonLaboralEl.startLoading();
		await getSalaryPdf(data);
		this.aonLaboralEl.stopLoading();
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
