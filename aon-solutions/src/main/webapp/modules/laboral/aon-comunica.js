import { AonElement } from "../../components/AonElement.js";
import { DomainUserRoles } from "../../models/DomainUserRoles.js";
import { getDomainUserRoles, getIDC, getTA, movDelete } from "../../services/service.js";
import { setValueName, waitEl } from "../../services/utils.js";
import { PayrollOptions, PAYROLL_VIEWS, CONTRACT_OPTIONS } from "./PayrollEnums.js";
import { AonMovementsList } from "./comunic@/aon-movements-list.js";
import { AonAltaDirecta } from "./comunic@/aon-alta-directa.js";
import { MSG, CONSTANT } from "../../environments/environments.js";
import { AonApplication } from "../../components/aon-application.js";
import { AonCtaList } from "./cta/aon-cta-list.js";
import * as GWT from '../../gwt/gwt.js';
import Apps from "../../services/app.js";
import '../../css/aon.css';
import { AonDateUtils } from "../utils/AonDateUtils.js";

export class AonComunica extends AonElement {

  AON_COMUNICA;
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
    this.AON_COMUNICA = 'aonComunica';
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
    this.showView(PAYROLL_VIEWS.AON_MOVEMENTS_LIST);
  }

  paintView(){
    this.createApplication(this.AON_COMUNICA, this.title, new AonApplication());
    this.applicationEl = this.getApplication();
  }

  buildToolbar(){
    if(this.isMobile()){
			this.applicationEl.addMobileSidenavHeader(Apps.COMUNICA);
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
        this.applicationEl.removeToolbarOptions();
        this.showView(PAYROLL_VIEWS.AON_CTA_LIST);
      }
      options.push(aon_cta_list);
      if(!this.isMobile()){
        let aon_cert = PayrollOptions.AON_CERT;
        aon_cert.fn = () => {
          this.applicationEl.removeToolbarOptions();
          if(window.innerWidth && window.innerWidth < 900){ this.applicationEl.closeSidenav(); }
          this.showView(PAYROLL_VIEWS.AON_CERT);
        }
        options.push(aon_cert);
      }

    this.applicationEl.addSidenavOptions(MSG.COMUNICA, options);

    let movButton = this.getElement('aonComunicaSidenavMovimientosAonIcon');
    if(movButton){
      movButton.style.position = 'relative';
      movButton.style.top = '3px';
    }
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
      fn: (el) => this.getTa(res, el)
    });
    // if(!res.prev){
      option.push({
				...CONTRACT_OPTIONS.IDC,
				fn: (el) => this.getIdc(res, el)
		  });
    // }
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

  async getIdc({ regime, ctaCti, nss, fea, feb, fra, frb, situation }) {
		this.applicationEl.startLoading();
		try {
      let fecha = feb || fea;
      let isBaja = situation && (situation.toLowerCase().includes("baja") || situation.toLowerCase().includes("bj"));
      if(isBaja){
        fecha = fea;
      }
      if(!fecha) {
        fecha = frb || fra;
        fecha = (new Date().getTime() > new Date(fecha).getTime()) ? fecha : AonDateUtils.formatDateOrigin(new Date());
      }
      
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

  showView(view, data = undefined, filter = undefined){
    return new Promise(async(resolve)=>{
      let aonView = undefined;
      switch(view){
      case PAYROLL_VIEWS.AON_CERT:
          this.loadGwt(GWT.MAIN_DIGITAL_CERTIFICATES);
          break;
      case PAYROLL_VIEWS.AON_CTA_LIST:
          if(this.isMobile()){
            aonView = new AonCtaList();
          } else {
            this.loadGwt(GWT.MAIN_CCC);
          }
          break;
      case PAYROLL_VIEWS.AON_CONTRACT_LIST:
        this.goContractDesk();
        break;
      case PAYROLL_VIEWS.AON_MOVEMENTS_LIST:
          aonView = new AonMovementsList();
          break;
      case PAYROLL_VIEWS.AON_ALTA_DIRECTA:
          aonView = new AonAltaDirecta();
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
}
window.customElements.define('aon-comunica', AonComunica);
