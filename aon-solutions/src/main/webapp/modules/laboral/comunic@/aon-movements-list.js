import { AonElement } from "../../../components/AonElement.js";
import { disabledForm, isEmptyObject, setDate } from "../../../services/utils.js";
import { getMovements, getEmployee } from "../../../services/service.js";
import { EXCEPTION_MESSAGE, PAYROLL_VIEWS } from "../PayrollEnums.js";
import { AON_SWITCH } from "../../../environments/aonTag.js";
import { CONSTANT, EVENT } from "../../../environments/environments.js";
import { SigninSidenav } from "../../signin/signinEnums.js";
import { AonMobileList } from "../../../components/aon-mobile-list.js";
import { AonTable } from "../../../components/aon-table.js";


export class AonMovementsList extends AonElement {
  TABLE_ID;
  searchFilter;
  static get observedAttributes() {
    return [CONSTANT.FILLED];
  }

  get filter() {
    return this.getAttribute(CONSTANT.FILLED);
  }

  set filter(filter) {
    this.setAttribute(CONSTANT.FILLED, filter);
  }

  attributeChangedCallback(name, oldValue, newValue) {
    if (CONSTANT.FILLED === name) this.build();
  }

  constructor() {
    super();
  }

  connectedCallback() {
    this.initialize();
    this.build();
  }

  initialize(){
    this.id = this.id || PAYROLL_VIEWS.AON_MOVEMENTS_LIST;
    this.TABLE_ID = this.id + "Table";
    this.applicationEl = this.getApplication();
    this.applicationParentEl = this.getApplicationParent();
  }

  disconnectedCallback() {
    if (this.applicationEl) this.applicationEl.removeFloatOption();
  }

  build() {
    this.paintView();
    this.buildToobar();
    this.getTable();
  }

  paintView() {
    let aonTable = this.isMobile() ? new AonMobileList() : new AonTable();
    aonTable.id = this.TABLE_ID;
    this.appendChild(aonTable);
  }

  buildToobar() {
    this.applicationEl.removeToolbarOptions();
    if (this.isMobile()) {
      this.applicationEl.addFloatOption(SigninSidenav.ADD, () => this.applicationParentEl.showView(PAYROLL_VIEWS.AON_ALTA_DIRECTA) );
    } else {
      this.applicationEl.addToolbarOption2(SigninSidenav.ADD, () =>  this.applicationParentEl.showView(PAYROLL_VIEWS.AON_ALTA_DIRECTA));
    }
    this.applicationEl.addSearchOption();
    this.applicationEl.addEventListener(EVENT.SEARCH, ({detail}) => this.search(detail));
  }

  async getTable(){
    this.applicationEl.startLoader();
    if (this.isMobile()) await this.getTableMobile();
    else await this.getTableDesk();
    this.applicationEl.stopLoader();
  }

  async getTableDesk() {
    const aonTable = this.getElement(this.TABLE_ID);
    if (aonTable) {
      aonTable.removeColumns();
      aonTable.addColumn("Nombre", "string", "name", "35%");
      aonTable.addColumn("DNI/NIE", "string", "dni", "15%");
      aonTable.addColumn("Movimiento", "string", "status", "10%");
      aonTable.addColumn("Cuenta", "string", "ctaCtiCompleta", "10%");
      aonTable.addColumn("Fecha", "date", "fecha", "10%");
      try {
        const resp = await this.getData();
        if(resp){
          aonTable.removeRows();
          resp.map((res) => {
            aonTable.addRow(res, (el) => this.aonMovement(el, res));
          });
        }
      } catch (e) {
        console.log(e);
      }
    }
  }

  async getTableMobile() {
    const aonTable = this.getElement(this.TABLE_ID);
    if (aonTable) {
      aonTable.createAonDialog();
      try {
        const resp = await this.getData();
        if(resp){
          aonTable.removeAllLi();
          resp.map((res, idx) => {
            aonTable.addLi(
              {
                aonIcon: "aon_seg_social",
                title: `${res.name}`,
                subtitle: `${res.status} ${res.fecha}`,
                option: this.applicationParentEl.getOptions(res),
              },
              idx,
              (el) => this.aonMovement(el, res)
            );
          });
        }
      } catch (e) {
        console.log(e);
      }
    }
  }

  async aonMovement({ }, { regime, ctaCti, nss, prev, situation, status }) {
    this.applicationEl.startLoading();
  
    try {
      let resp = await getEmployee({ regime, ctaCti, nss });
      if (resp) {
        const data = { ...resp, prev, situation, status };
        const aonAltaDirecta = await this.applicationParentEl.showView(PAYROLL_VIEWS.AON_ALTA_DIRECTA, data);
        if (aonAltaDirecta) {
          disabledForm(`${aonAltaDirecta.id}EmpresaCard`);
          disabledForm(`${aonAltaDirecta.id}TrabajadorCard`, AON_SWITCH);
        }
      }
    } catch (error) {
      this.showError(error);
    }
    this.applicationEl.stopLoading();
  }

  async getData() {
    let data = [];
    try {
      const movements = this.applicationParentEl._movements;
      if(this.searchFilter && !isEmptyObject(movements)){
        data = movements.filter(({name, ipf, ctaCtiCompleta})=> this.includeSearch(name) || this.includeSearch(ipf) || this.includeSearch(ctaCtiCompleta));
      } else {
        const resp = movements || await getMovements(this.getFilter());
        data = resp
          .sort((a, b) => new Date(b.fra) - new Date(a.fra))
          .map((res) => {
          const { ipf, fra, situation } = res;
          const fecha = setDate(fra);
          const date_now = new Date();
          const prev = new Date(fra).getTime() > date_now.getTime();
          const dni = ipf.toString().substring(1);
          let color = "#000";
          let tipo_mov = situation === "AL" ? "Alta" : "Baja";
          if (prev) {
            color = "#488601";
            tipo_mov = `${tipo_mov} Previa`;
          } else if (this.applicationParentEl.anularCondition(situation, fra)) {
            color = "#CB8D00";
            tipo_mov = `${tipo_mov} Consolidada`;
          } else {
            tipo_mov = `${tipo_mov} Consolidada`;
          }
          const status = `<span style="font-weight: 700;color: ${color};">${tipo_mov}</span>`;
          const ctaCtiCompleta = res.regime+"-"+res.ctaCti;
          return {
            ...res,
            dni,
            fecha,
            status,
            prev,
            ctaCtiCompleta,
          };
        });
        this.applicationParentEl._movements = data;
      }
    } catch (error) {
      if(typeof error === "string") error = JSON.parse(error);
      if(error &&  EXCEPTION_MESSAGE[error.message]){
        error.message =  EXCEPTION_MESSAGE[error.message];
      }
      if(!this.isMobile()){
        this.applicationParentEl.showView(PAYROLL_VIEWS.AON_CERT);
      }
      this.showToast(error);
    }
    return data;
  }

  getFilter = () => JSON.parse(this.getAttribute(CONSTANT.FILTER));

  setFilter = (filter) => this.setAttribute(CONSTANT.FILTER, JSON.stringify(filter));

  search(detail){
    this.searchFilter = detail
    this.getTable();
  }

  includeSearch(str){
    return str.toLowerCase().includes(this.searchFilter.toLowerCase());
  }
  
}
window.customElements.define("aon-movements-list", AonMovementsList);
