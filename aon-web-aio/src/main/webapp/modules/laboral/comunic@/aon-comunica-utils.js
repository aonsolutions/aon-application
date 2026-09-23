import { AonElement } from "../../../components/AonElement.js";
import { CONSTANT, MSG, EVENT } from "../../../environments/environments.js";
import * as GWT from "../../../gwt/gwt.js";
import { DomainUserRoles } from "../../../models/DomainUserRoles.js";
import {
  getDomainUserRoles,
  getIDC, getTA,
  movDelete
} from "../../../services/service.js";
import { setValueName, waitEl } from "../../../services/utils.js";
import { AonDocumentalList } from "../../documental/aon-documental-list.js";
import { AonMobileDocumentalList } from "../../documental/aon-mobile-documental-list.js";
import { AonCompanyCostsList } from "../company/aon-company-costs-list.js";
import { AonAltaDirecta } from "../comunic@/aon-alta-directa.js";
import { AonMovementsList } from "../comunic@/aon-movements-list.js";
import { AonCtaList } from "../cta/aon-cta-list.js";
import { AonContractList } from "../payroll/aon-contract-list.js";
import { AonPayrollList } from "../payroll/aon-payroll-list.js";
import { CONTRACT_OPTIONS, PAYROLL_VIEWS } from "../PayrollEnums.js";
import { AonCompanyCostsListNew, paintCompanyCostPieChart } from "../company/aon-company-costs-list-new.js";
import { AonDocumentalSepaList, AonSepaList } from "../../documental/aon-sepa-list.js";

export class AonComunicaUtils extends AonElement {
  static get observedAttributes() {
    return [CONSTANT.TITLE];
  }

  get title() {
    return this.getAttribute(CONSTANT.TITLE);
  }

  set title(title) {
    this.setAttribute(CONSTANT.TITLE, title);
  }

  getDur() {
    return this.dur;
  }

  constructor() {
    super();
  }

  connectedCallback() {
    this.initialize();
    getDomainUserRoles({ reload: true }).then((r) => {
      this.dur = new DomainUserRoles(r);
      this.build();
    });
  }

  build() { /* TODO document why this method 'build' is empty */ }

  async setDataFilter(data) {
    try {
      this._filter = { ...this._filter, ...data };
      this.getApplication().getChild().filter = true;
    } catch (error) {}
  }

  changeFilter() {
    try {
      const filterParent = this._filter;
      for (const obj in filterParent) {
        setValueName(obj, filterParent[obj]);
      }
    } catch (error) {}
  }

  removeToolbarOptions() {
    let application = this.getApplication();
    let toolbar = this.getElement(application.TOOLBAR);
    if (toolbar) toolbar.removeButtons();
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

  async getTa({ regime, ctaCti, nss, fra, frb, checkIDC }) {
		this.getApplication().startLoading();
		try {
      let newData = { regime, ctaCti, nss, checkIDC };

      if(frb){
        newData.frb = frb;
      }

      if(fra){
        newData.fra = fra;
      }

			await getTA(newData); // open pdf
		} catch (error) {
      this.showToast(error);
		}
		this.getApplication().stopLoading();
	}

  async getIdc({ regime, ctaCti, nss, fra, frb, situation, checkIDC }) {
		this.getApplication().startLoading();
		try {
      
      let newData = { regime, ctaCti, nss, checkIDC };

      let fecha = fra || frb;
      let isBaja = situation && (situation.toLowerCase().includes("baja") || situation.toLowerCase().includes("bj"));
      
      if(isBaja){
        fecha = fea;
      }

      if(fecha) {
        newData.fra = fecha
      }
      
			await getIDC(newData); // open pdf
		} catch (error) {
      this.showToast(error);
		}
    this.getApplication().stopLoading();
  }

  anularCondition(situation, fecha) {
    const date_prev = new Date().addDay(-2);
    // const sit = ["AL", "BJ", "BAJA", "ALTA"];
    // (situation.indexOf(sit) > -1) &&
    return date_prev.getTime() <= new Date(fecha).getTime();
  }

  async deleteMov(data, el) {
    this.getApplication().confirmDialog(
      MSG.DELETE,
      `${MSG.DELETE_CONFIRM} el movimiento de ${data.name} ?`,
      async () => {
        this.getApplication().startLoading();
        try {
          await movDelete({
            ...data,
            nombre: data.nombre || data.name,
          });
          this.showToast({ message: `${data.situation == "AL" ? "Alta" : "Baja"} eliminada!` });
          if (this._movements.length) {
            this._movements = this._movements.filter(
              ({ ctaCti, fra, frb, ipf, nss, regime, situation }) => {
                const dtFecha = data.frb || data.fra;
                const fecha = frb || fra;
                return !(
                  ctaCti.includes(data.ctaCti) &&
                  fecha.includes(dtFecha) &&
                  ipf.includes(data.ipf) &&
                  nss.includes(data.nss) &&
                  regime.includes(data.regime) &&
                  situation.includes(data.situation)
                );
              }
            );
          }
          this.showView(PAYROLL_VIEWS.AON_MOVEMENTS_LIST);
        } catch (error) {
          this.showToast(error);
        }
        this.getApplication().stopLoading();
      }
    );
  }

  getTypeSalaryText(type) {
    let obj = { color: "", type: "", typeReduce: "" };
    switch (type) {
      case "SALARY":
        obj.type = "NOMINA";
        break;
      case "EXTRA":
        obj.type = "EXTRA";
        obj.color = "in";
        break;
      case "SETTLE":
        obj.type = "FINIQUITO";
        obj.color = "fin";
        break;
      case "DELAY":
        obj.type = "ATRASOS";
        obj.color = "pause";
        break;
    }
    if (obj.type) obj.typeReduce = obj.type.toString().substr(0, 1);
    return obj;
  }

  showView(view, data = undefined, filter = undefined) {
    return new Promise(async (resolve) => {
      let aonView = undefined;
      switch (view) {
        case PAYROLL_VIEWS.AON_PAYROLL_LIST:
          aonView = new AonPayrollList();
          break;
        case PAYROLL_VIEWS.AON_SEPA_FILES_LIST:
          aonView = this.isMobile() ? new AonMobileDocumentalList() : new AonSepaList();
          if(this.isMobile()) aonView.setFilter({ type: "system" });
          break;
        case PAYROLL_VIEWS.AON_CONTRACT_LIST:
          if (this.isMobile()) 
            aonView = new AonContractList();
          else 
            this.goContractDesk();
            this.getApplication().closeSidenav();
          break;
        case PAYROLL_VIEWS.AON_CERT:
          this.loadGwt(GWT.MAIN_DIGITAL_CERTIFICATES);
          this.getApplication().closeSidenav();
          break;
        case PAYROLL_VIEWS.AON_CTA_LIST:
          if (this.isMobile()) 
            aonView = new AonCtaList();
          else 
            this.loadGwt(GWT.MAIN_CCC);
            this.getApplication().closeSidenav();
          break;
        case PAYROLL_VIEWS.AON_MOVEMENTS_LIST:
          aonView = new AonMovementsList();
          break;
        case PAYROLL_VIEWS.AON_ALTA_DIRECTA:
          aonView = new AonAltaDirecta();
          break;
        case PAYROLL_VIEWS.AON_COMPANY_COSTS_LIST:
          aonView = new AonCompanyCostsListNew();
          aonView.addEventListener(EVENT.BUILD, paintCompanyCostPieChart );
          break;
      }
      if (aonView) {
        aonView.id = view;
        if (filter) aonView.filter = filter;
        if (data) aonView.data = data;
        this.getApplication().setContent(aonView);
        
        if(view === PAYROLL_VIEWS.AON_COMPANY_COSTS_LIST){
          /*await paintCompanyCostPieChart();*/
        }
      }
      resolve(aonView);
    });
  }

  showViewFilter(view, cardFilter) {
    return new Promise(async (resolve) => {
      let aonView = undefined;
      switch (view) {
        case PAYROLL_VIEWS.AON_COMPANY_COSTS_LIST:
          aonView = new AonCompanyCostsListNew(cardFilter);
          break;
      }
      if (aonView) {
        aonView.id = view;
        this.getApplication().setContent(aonView);
        
        if(view === PAYROLL_VIEWS.AON_COMPANY_COSTS_LIST){
          await paintCompanyCostPieChart();
        }
      }
      resolve(aonView);
    });
  }

  goContractDesk() {
    // this.getApplication().removeToolbarOptions();
    this.loadGwt(GWT.MAIN_CONTRATA);
  }

  loadGwt(module) {
    let application = this.getApplication();

    this.clearElementById(application.CONTENT);

    application.startLoader();

    GWT.iLoad(module, application.CONTENT);

    waitEl(`#${application.CONTENT} iframe`).finally(() => {
      this.fixBackgroundColor();
    });

    waitEl(`#${application.CONTENT} .aon_toolbar`).finally(() => {
      application.stopLoader();
      this.fixSpacing();
      this.fixTableHeaderBackgroundColor();
    });
  }

  fixTableHeaderBackgroundColor(){
    let application = this.getApplication();
    let iframe = document.querySelector(`#${application.CONTENT} iframe`);
    let iframeContent = iframe.contentWindow.document;

    let tableHeaders = iframeContent.body.querySelectorAll(`div.aon_custom_table_header`);
    if(tableHeaders) tableHeaders.forEach(tableHeader => tableHeader.style.backgroundColor = "#fafafa");
    
    // waitEl(`div.aon_custom_table_header`).finally(() => {
    //   let tableHeader = iframeContent.body.querySelector(`div.aon_custom_table_header`);
    //   tableHeader.style.backgroundColor = "#fafafa";
    // });
  }

  fixBackgroundColor(){
    let application = this.getApplication();
    let iframe = document.querySelector(`#${application.CONTENT} iframe`);
    iframe.contentWindow.document.body.style.backgroundColor = "transparent";
  }

  fixSpacing() {
    if (!document.querySelector("aon-module"))
      waitEl(`#${this.getApplication().getContent().id} div:first-child`)
        .then((el) => (el.style.position = ""))
        .catch((err) => console.log(err));
  }

  isComunica() {
    return this.getDur().isComunica();
  }

  isEmployee() {
    return !this.getDur().isPayrollManager() && !this.getDur().isPayrollPortal();
  }

  isPayroll() {
    return this.getDur().isPayroll();
  }

  isComunicaNotPayroll() {
    return this.isComunica() && !this.isPayroll();
  }
}
window.customElements.define("aon-comunica-utils", AonComunicaUtils);
