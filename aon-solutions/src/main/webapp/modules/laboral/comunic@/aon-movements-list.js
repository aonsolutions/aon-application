import { AonElement } from "../../../components/AonElement.js";
import { disabledForm, setValueName } from "../../../services/utils.js";
import { getMovements, getEmployee, getMovementsCccs } from "../../../services/service.js";
import { EXCEPTION_MESSAGE, PAYROLL_VIEWS } from "../PayrollEnums.js";
import { AON_SWITCH } from "../../../environments/aonTag.js";
import { CONSTANT,  EVENT, MSG, TAG } from "../../../environments/environments.js";
import { PRESENCE_FILTER, SigninSidenav } from "../../timecontrol/signinEnums.js";
import { AonMobileList } from "../../../components/aon-mobile-list.js";
import { AonTable } from "../../../components/aon-table.js";
import { AonDateUtils } from "../../utils/AonDateUtils.js";
import { PAYROLL } from "../../../services/app.js";


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

  getFilter = () => JSON.parse(this.getAttribute(CONSTANT.FILTER));

  setFilter = (filter) => this.setAttribute(CONSTANT.FILTER, JSON.stringify(filter));

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
    this._list = [];
    this.id = this.id || PAYROLL_VIEWS.AON_MOVEMENTS_LIST;
    this.TABLE_ID = this.id + "Table";
  }

  disconnectedCallback() {
    if (this.getApplication()){
      this.getApplication().stopLoader();
      this.getApplication().removeFloatOption();
    } 
  }

  build() {
    this.paintView();
    this.buildToobar();
    this.getTable();
  }

  paintView() {
    let aonTable = this.isMobile() ? new AonMobileList() : new AonTable();
    aonTable.id = this.TABLE_ID;
    aonTable.setApp(PAYROLL);
    this.appendChild(aonTable);
  }

  buildToobar() {
    this.getApplication().removeToolbarOptions();
    if (this.isMobile()) {
      this.getApplication().addFloatOption(SigninSidenav.ADD, () => this.getApplicationParent().showView(PAYROLL_VIEWS.AON_ALTA_DIRECTA) );
    } else {
      this.getApplication().addToolbarOption2(SigninSidenav.ADD, () =>  this.getApplicationParent().showView(PAYROLL_VIEWS.AON_ALTA_DIRECTA));
    }

    let timeOut = null;

    const btnSearch = this.getApplication().addSearchOption();
    btnSearch.addEventListener(EVENT.SEARCH_NEW, ({detail})=>{
      clearTimeout(timeOut);
      if(detail){
        if(detail.event === EVENT.KEYUP){
          this.searchFilter = detail.search;
          this.search();
        } else if(detail.startDate){
          timeOut = setTimeout(() => {
            this.getEmployeeForCcc(detail);
          }, 300);
        } 
      }
    });

    btnSearch.buildOptionsFilter(PRESENCE_FILTER);//INPUTS
    this.searchValueDefault();
  }

  searchValueDefault(){
    const today =  new Date();
    let periodEl = this.getElement("period");
    periodEl.setOptions(this.getPeriodComunica());
    periodEl.addEventListener(EVENT.CHANGE, ({detail}) => {
      if(detail){
        let {startDate, endDate} = detail;
        if(new Date(startDate) > today) startDate = AonDateUtils.formatDateOrigin(today);
        if(new Date(endDate) > today) endDate     = AonDateUtils.formatDateOrigin(today);
        setValueName('startDate', startDate);
        setValueName('endDate', endDate);
      }
    });
    
    let startDateEl = this.getElement("startDate");
    let endDateEl   = this.getElement("endDate");
    startDateEl.addEventListener(EVENT.CHANGE,({detail})=>{
      if(detail && new Date(detail) > today) startDateEl.value = AonDateUtils.formatDateOrigin(today);
      periodEl.value = "personalized";
    });
    endDateEl.addEventListener(EVENT.CHANGE,({detail})=>{
      if(detail && new Date(detail) > today) endDateEl.value = AonDateUtils.formatDateOrigin(today);
      periodEl.value = "personalized";
    });
  }


  async getTable(){
    this.getApplication().startLoader();
    if (this.isMobile()) await this.getTableMobile();
    else await this.getTableDesk();
    this.getApplication().stopLoader();
  }

  async getTableDesk() {
    const aonTable = this.getElement(this.TABLE_ID);
    if (aonTable) {
      aonTable.removeColumns();
      aonTable.addColumn("#", "number", "count", "2%");
      aonTable.addColumn(MSG.NAME, "string", "name", "31%");
      aonTable.addColumn("DNI/NIE", "string", "dni", "15%");
      aonTable.addColumn("Movimiento", "string", "status", "10%");
      aonTable.addColumn(MSG.ACCOUNT, "string", "ctaCtiCompleta", "10%");
      aonTable.addColumn(MSG.DATE, "date", "fechaParse", "10%");
      aonTable.addColumn("Opción", "fn", "option", "2%");
      try {
        const resp = await this.getData();
        if(resp){
          aonTable.removeRows();
          resp.map((res, idx) => {
            res.count = `<b>${idx+1}</b>`;
            res.option = this.getApplicationParent().getOptions({...res, checkIDC:true});
            aonTable.addRow(res, () => this.aonMovement(res));
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
                option: this.getApplicationParent().getOptions(res),
              },
              idx,() => this.aonMovement(res)
            );
          });
        }
      } catch (e) {
        console.log(e);
      }
    }
  }

  async aonMovement(data) {
    this.getApplication().startLoading();
    let newData = undefined;
    if(data.tc || data.contractType){
      newData = this.movParseData(data);
    } else {
      try {
        let resp = await getEmployee({ regime:data.regime, ctaCti:data.ctaCti, nss: data.nss, date:(data.fra || data.frb) });
        if (resp) {
          newData = { ...resp, ...data};
          if(resp.occupation){
            newData.ocup = resp.occupation;
          }
          if(resp.contractType){
            newData.contract = resp.contractType;
          }
          if(resp.factor){
            newData.coef = resp.factor;
          }
          if(resp.quoteGroup){
            newData.gc = resp.quoteGroup;
          }
        }
      } catch (error) {
        this.showError(error);
      }
    }

    if(newData){
      const aonAltaDirecta = await this.getApplicationParent().showView(PAYROLL_VIEWS.AON_ALTA_DIRECTA, newData);
      if (aonAltaDirecta) {
        disabledForm(`${aonAltaDirecta.id}EmpresaCard`);
        disabledForm(`${aonAltaDirecta.id}TrabajadorCard`, AON_SWITCH);
      }
    }

    this.getApplication().stopLoading();
  }

  movParseData(data){
    let dt = {...data, contract: data.tc || data.contractType};
    if(data.ep) dt.ocup = data.ep.toLowerCase();
    return dt;
  }

  search(){
    this._list = this.filterSearch(["name", "ipf","ctaCtiCompleta", "fechaParse", "status"], this.getApplicationParent()._movements);
    this.getTable();
  }

  filterSearch(keys, lists){
    let list = [];
    if(this.searchFilter && lists.length){
      list = lists.filter((lt)=> keys.some(key=>lt[key] && lt[key].toString().toLowerCase().includes(this.searchFilter.toLowerCase())));
    }

    const startDateEl = this.getElement("startDate");
    const endDateEl = this.getElement("endDate");
    if(startDateEl && endDateEl){
      const lts = list.length ? list : lists;
      if(lts && lts.length){
        let startDate = startDateEl.value;
        let endDate   = endDateEl.value;
        if(startDate || endDate){
          startDate = startDate || endDate;
          endDate = endDate || startDate;
          list = lts.filter(({fra})=>  new Date(fra) >= new Date(startDate) && new Date(fra) <= new Date(endDate))
        }
      }
    }
    return list;
  }

  formatData(res){
    let { ipf, fra, frb, situation } = res;
    fra = frb || fra;
    const fechaParse = AonDateUtils.setDate(fra);
    const date_now = new Date();
    const prev = new Date(fra).getTime() > date_now.getTime();
    let dni = ipf.toString();
    if(dni.length>10) {
      dni = dni.substring(1);
    }
    let color = "#000";
    let tipo_mov = situation.indexOf("AL")>=0 ? "Alta" : "Baja";
    if (prev) {
      color = "#488601";
      tipo_mov = `${tipo_mov} Previa`;
    } else if (this.getApplicationParent().anularCondition(situation, fra)) {
      color = "#CB8D00";
      tipo_mov = `${tipo_mov} Consolidada`;
    } else {
      tipo_mov = `${tipo_mov} Consolidada`;
    }
    let span = this.createElement(TAG.SPAN);
    span.style.fontWeight = 600;
    span.style.color = color;
    span.innerText = tipo_mov;
    return {
      ...res,
      fra,
      dni,
      ipf:dni,
      fechaParse,
      prev,
      status: span.outerHTML,
      ctaCtiCompleta: res.regime+"-"+res.ctaCti,
      tipo_mov: tipo_mov.toLowerCase()
    };
  }

  async getData() {
    let data = [];
    try {
      if(this._list.length){
        data = this._list;
      } else {
        const movements = this.getApplicationParent()._movements;
        const resp = movements.length ? movements : await getMovements(this.getFilter());
        data = resp
        .map((res) => this.formatData(res)).sort((a, b) => new Date(b.fra) - new Date(a.fra))
        this.getApplicationParent()._movements = data;
        if(this.searchFilter) data = this.filterSearch(["name", "ipf","ctaCtiCompleta", "fechaParse", "status"], data);
      }
    } catch (error) {
      if(typeof error === "string") {
        error = JSON.parse(error);
      }
      
      if(error && EXCEPTION_MESSAGE[error.message]){
        error.message =  EXCEPTION_MESSAGE[error.message];
      }

      if(!this.isMobile()){
        this.getApplicationParent().showView(PAYROLL_VIEWS.AON_CERT);
      }

      this.showToast(error);
    }
    return data;
  }
  
  async getEmployeeForCcc(detail){
    const parent =  this.getApplicationParent();
    this.getApplication().startLoader();
    this.setFilter(detail);
    try {
      let count = 0;
      const resp = await getMovementsCccs(this.getFilter());
      resp
      .map((res) => {
        const newData = this.formatData(res);
        const exists = parent._movements.some(r=> 
          r.nss.indexOf(newData.nss)>=0 && 
          r.ctaCti.indexOf(newData.ctaCti)>=0 &&
          r.tipo_mov.indexOf(newData.tipo_mov)>=0 &&
          r.ipf.indexOf(newData.ipf)>=0 &&
          (r.fra).indexOf(newData.fra)>=0
        );
        if(!exists) {
          count ++;
          parent._movements.push(newData);
        }
      });

      if(!count) {
        this.showToast({message:MSG.REQUEST_EMPTY_DATA});
      } else {
        parent._movements = parent._movements.sort((a, b) =>  new Date(b.fra) - new Date(a.fra));
      }

      this.search();
      
    } catch (error) {console.log(error);}
    this.getApplication().stopLoader();
  }

  getPeriodComunica(){
    const now = new Date();
    return [{
      name: "Últimos 3 meses",
      value: "last_three_month",
      startDate: AonDateUtils.formatDateOrigin(new Date().addMonth(-3)),
      endDate: AonDateUtils.formatDateOrigin(now),
    },
    {
      name: "Últimos 6 meses",
      value: "last_six_month",
      startDate: AonDateUtils.formatDateOrigin(new Date().addMonth(-6)),
      endDate: AonDateUtils.formatDateOrigin(now),
    },
    {
      name: "Año actual",
      value: "this_year",
      startDate: AonDateUtils.formatDateOrigin(new Date(now.getFullYear(), 0, 1)),
      endDate: AonDateUtils.formatDateOrigin(new Date(now.getFullYear(), 12, 0)),
    },
    {
      name: "Año anterior",
      value: "last_year",
      startDate: AonDateUtils.formatDateOrigin(new Date(now.getFullYear() - 1, 0, 1)),
      endDate: AonDateUtils.formatDateOrigin(new Date(now.getFullYear() - 1, 12, 0)),
    },
    {
      name: "Personalizado",
      value: "personalized",
    }];
  }
}
window.customElements.define("aon-movements-list", AonMovementsList);
