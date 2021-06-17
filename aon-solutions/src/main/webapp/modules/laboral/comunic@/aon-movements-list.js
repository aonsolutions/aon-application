import { AonElement } from "../../../components/AonElement.js";
import { disabledForm, formatDateOrigin, setDate, setValueName } from "../../../services/utils.js";
import { getMovements, getEmployee, getCccLife, getPeriodLaboral } from "../../../services/service.js";
import { EXCEPTION_MESSAGE, PAYROLL_VIEWS } from "../PayrollEnums.js";
import { AON_SWITCH } from "../../../environments/aonTag.js";
import { CONSTANT, EVENT, TAG } from "../../../environments/environments.js";
import { PRESENCE_FILTER, SigninSidenav } from "../../signin/signinEnums.js";
import { AonMobileList } from "../../../components/aon-mobile-list.js";
import { AonTable } from "../../../components/aon-table.js";


export class AonMovementsList extends AonElement {
  TABLE_ID;
  searchFilter;
  _list;
  static get observedAttributes() {
    return [CONSTANT.FILTER];
  }

  get filter() {
    return this.getAttribute(CONSTANT.FILTER);
  }

  set filter(filter) {
    this.setAttribute(CONSTANT.FILTER, filter);
  }

  attributeChangedCallback(name, oldValue, newValue) {
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
    this._list = [];
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
    const btnSearch = this.applicationEl.addSearchOption();
    btnSearch.addEventListener(EVENT.SEARCH, ({detail}) => {
      this.searchFilter = detail;
      this.search();
    });

    btnSearch.addEventListener(EVENT.SEARCH_VALUE, ({detail})=>{
      if(detail) this.getNewData(detail);
    });

    btnSearch.buildOptionsFilter(PRESENCE_FILTER);//INPUTS
    this.searchValueDefault();
  }

  searchValueDefault(){
    let periodEl = this.getElement("period");
    periodEl.options = JSON.stringify(getPeriodLaboral());
    periodEl.addEventListener(EVENT.CHANGE, ({detail}) => {
      if(detail){
        const {startDate, endDate} = detail;
        setValueName('startDate', startDate);
        setValueName('endDate', endDate);
      }
    });

    this.getElement("startDate").addEventListener(EVENT.CHANGE,()=>periodEl.value = "personalized");
    this.getElement("endDate").addEventListener(EVENT.CHANGE,()=>periodEl.value = "personalized");
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
      aonTable.addColumn("Fecha", "date", "fechaParse", "10%");
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
            aonTable.addLi({
                aonIcon: "aon_seg_social",
                title: `${res.name}`,
                subtitle: `${res.status} ${res.fechaParse}`,
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
      if(this._list.length){
        data = this._list;
      } else {
        const resp = movements || await getMovements(this.getFilter());
        data = resp
        .sort((a, b) => new Date(b.fra) - new Date(a.fra))
        .map((res) => this.formatData(res));
        this.applicationParentEl._movements = data;
        this._list = data;
        if(this.searchFilter) data = this.filterSearch(["name", "ipf","ctaCtiCompleta", "fechaParse"], data);
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
  
  formatData(res){
    const { ipf, fra, situation } = res;
    const fechaParse = setDate(fra);
    const date_now = new Date();
    const prev = new Date(fra).getTime() > date_now.getTime();
    const dni = ipf.toString().substring(1);
    let color = "#000";
    let tipo_mov = situation.indexOf("AL")>=0 ? "Alta" : "Baja";
    if (prev) {
      color = "#488601";
      tipo_mov = `${tipo_mov} Previa`;
    } else if (this.applicationParentEl.anularCondition(situation, fra)) {
      color = "#CB8D00";
      tipo_mov = `${tipo_mov} Consolidada`;
    } else {
      tipo_mov = `${tipo_mov} Consolidada`;
    }
    let span = this.createElement(TAG.SPAN);
    span.style.fontWeight = 600;
    span.style.color = color;
    span.innerText = tipo_mov;
    const status = span.outerHTML;
    const ctaCtiCompleta = res.regime+"-"+res.ctaCti;
    return {
      ...res,
      dni,
      fechaParse,
      status,
      prev,
      ctaCtiCompleta,
      tipo_mov: tipo_mov.toLowerCase()
    };
  }

  getFilter = () => JSON.parse(this.getAttribute(CONSTANT.FILTER));

  setFilter = (filter) => this.setAttribute(CONSTANT.FILTER, JSON.stringify(filter));

  search(){
    this._list = this.filterSearch(["name", "ipf","ctaCtiCompleta", "fechaParse"], this._list);
    this.getTable();
  }

  filterSearch(keys, lists){
    let list = [];
    if(this.searchFilter && lists.length){
      list = lists.filter((lt)=> keys.some(key=>lt[key] && lt[key].toString().toLowerCase().includes(this.searchFilter.toLowerCase())));
    }
    return list;
  }

  async getNewData(detail){
    this.applicationEl.startLoader();
    this.setFilter(detail);
    try {
      const resp = await getCccLife(this.getFilter());
      let data = this._list;
      resp.sort((a, b) =>  new Date(b.frb || b.fra) - new Date(a.frb || a.fra))
      .map((res) => {
        const newData = this.formatData(res);
        const fra =  newData.frb || newData.fra;
        const exists = data.some(r=> 
          r.nss.indexOf(newData.nss)>=0 && 
          r.ctaCti.indexOf(newData.ctaCti)>=0 &&
          r.tipo_mov.indexOf(newData.tipo_mov)>=0 &&
          r.ipf.indexOf(newData.ipf)>=0 &&
          (r.frb || r.fra).indexOf(fra)>=0
        );
        if(!exists) data.push(newData);
      });

      // data = data.filter(r=> {
      //   const fra = r.frb || r.fra;
      //   return  (detail.startDate && new Date(detail.startDate).getTime() >= new Date(fra).getTime() ) || (detail.endDate && new Date(detail.endDate).getTime() <= new Date(fra).getTime() ) 
      // })

      this._list = data;
      this.search();
    } catch (error) {console.log(error);}
    this.applicationEl.stopLoader();
  }
}
window.customElements.define("aon-movements-list", AonMovementsList);
