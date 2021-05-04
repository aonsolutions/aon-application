import {AonElement} from '../../components/AonElement.js';
import { getDomainUserRoles } from '../../services/companyService.js';
import { DomainUserRoles } from '../../models/DomainUserRoles.js';
import { MSG } from '../../environments/environments.js';
import { FiscalOptions, FISCAL_VIEWS} from "./FiscalEnums.js";
import { AonTax } from './tax/aon-tax.js';
import { AonApplication } from '../../components/aon-application.js';
class AonFiscal extends AonElement {
	AON_FISCAL;
  dur;
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
        this.AON_FISCAL = FISCAL_VIEWS.AON_FISCAL;
      }
    
      getDur() {
        return this.dur;  
      }

      build() {
        this.paintView();
        this.buildToolbar();
        this.showView(FISCAL_VIEWS.AON_TAX);
      }

      paintView(){
        this.createApplication(this.AON_FISCAL, MSG.FISCAL, new AonApplication())
        this.applicationEl = this.getApplication();
      }

      buildToolbar(){
        let fiscalOpts = [];

        let tax = FiscalOptions.AON_TAX;
        tax.fn = () => this.showView(FISCAL_VIEWS.AON_TAX);
        fiscalOpts.push(tax);
        
        this.applicationEl.addSidenavOptions(MSG.FISCAL, fiscalOpts);
      }

      showView(view, data, filter = undefined){
        return new Promise(async(resolve)=>{
          let aonView = undefined;
          // if(!this.getElement(view)){
            switch(view){
              case FISCAL_VIEWS.AON_TAX:
                aonView = new AonTax();
                break;
            }
            if(aonView){
              aonView.id = view;
              if(filter) aonView.filter = filter;
              this.applicationEl.setContent(aonView);
            }
          // }
          resolve(aonView);
        });
      }

}
window.customElements.define('aon-fiscal', AonFiscal);
