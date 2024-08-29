import { AonApplication } from "../../components/aon-application.js";
import { MSG } from "../../environments/environments.js";
import Apps, { COMUNICA } from "../../services/app.js";
import { AonComunicaUtils } from "./comunic@/aon-comunica-utils.js";
import { PayrollOptions, PAYROLL_VIEWS } from "./PayrollEnums.js";
import 'aoncss';

export class AonComunica extends AonComunicaUtils {

  initialize(){
    this.AON_COMUNICA = 'aonComunica';
    this.title = this.title || MSG.PAYROLL;
    this._movements = [];
    this._filter = [];
  }

  build() {
    this.paintView();
    this.buildToolbar();
    this.showView(PAYROLL_VIEWS.AON_MOVEMENTS_LIST);
  }

  paintView(){
    this.createApplication(this.AON_COMUNICA, this.title, new AonApplication());
  }

  buildToolbar(){
    if(this.isMobile()){
			this.getApplication().addMobileSidenavHeader(Apps.COMUNICA);
		}

    let options = [];

    let contract = PayrollOptions.AON_CONTRACT;
    contract.fn = () => this.showView(PAYROLL_VIEWS.AON_CONTRACT_LIST);
    options.push(contract);

    let movements = PayrollOptions.AON_COMUNICA;
    movements.fn = () => this.showView(PAYROLL_VIEWS.AON_MOVEMENTS_LIST);
    options.push(movements);

    let aon_cta_list = PayrollOptions.AON_CCC;
      aon_cta_list.fn = () =>{
        this.getApplication().removeToolbarOptions();
        this.showView(PAYROLL_VIEWS.AON_CTA_LIST);
      }
      options.push(aon_cta_list);
      if(!this.isMobile()){
        let aon_cert = PayrollOptions.AON_CERT;
        aon_cert.fn = () => {
          this.getApplication().removeToolbarOptions();
          if(window.innerWidth && window.innerWidth < 900){ this.getApplication().closeSidenav(); }
          this.showView(PAYROLL_VIEWS.AON_CERT);
        }
        options.push(aon_cert);
      }
    
    let data = {
      id: MSG.COMUNICA,
      title: MSG.COMUNICA,
      name: MSG.COMUNICA,
      app: COMUNICA,
      options
    };

    this.getApplication().addSidenavOptions3(data);

    let movButton = this.getElement('aonComunicaSidenavMovimientosAonIcon');
    if(movButton){
      movButton.style.position = 'relative';
      movButton.style.top = '3px';
    }
  }
}
window.customElements.define('aon-comunica', AonComunica);
