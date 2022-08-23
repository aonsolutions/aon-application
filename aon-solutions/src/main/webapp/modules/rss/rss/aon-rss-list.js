import { AonElement } from "../../../components/AonElement.js";
import { setAttributes } from "../../../services/utilsComponents.js";
import { CONSTANT, EVENT, MSG, TAG } from "../../../environments/environments.js";
import { AonMobileList } from "../../../components/aon-mobile-list.js";
import { AonTable } from "../../../components/aon-table.js";
import { RssEnums } from "./../RssEnums.js";
import { SigninSidenav } from "../../timecontrol/signinEnums.js";
import { AonRssAdd } from "./aon-rss-add.js";
import {getRss} from "../../../services/newsService.js";

export class AonRssList extends AonElement {
  TABLE_ID;
  searchFilter;
  _list;
  static get observedAttributes() {
    return [];
  }

  get id() {
    return this.getAttribute(CONSTANT.ID);
  }

  set id(id) {
    this.setAttribute(CONSTANT.ID, id);
  }

  attributeChangedCallback(name, oldValue, newValue) {}

  constructor() {
    super();
  }

  connectedCallback() {
    this.initialize();
    this.build();
  }

  initialize(){
    this.id = this.id || RssEnums.RSS_VIEWS.AON_RSS_LIST;
    this.TABLE_ID = this.id + "Table";
    this.applicationEl = this.getApplication();
    this.applicationParentEl = this.getApplicationParent();
    this._list = [];
  }
  
  build(){
    this.paintView();
    this.buildToolbar();
    this.getTable();

    this.goAonRssAdd(); //TODO
    // this.applicationParentEl.addEventListener("filterParent",()=> {
    //   this._list = [];
    //   this.getTable();
    // });
  }

  paintView() {
    let aonTable = this.isMobile() ? new AonMobileList() : new AonTable();
    aonTable.id = this.TABLE_ID;
    this.appendChild(aonTable);
  }

  buildToolbar() {
    this.applicationEl.removeToolbarOptions();
    this.applicationEl.addToolbarOption2(SigninSidenav.ADD, () => this.goAonRssAdd());
    this.buildToolbarSearch();
  }

  buildToolbarSearch(){
    let btnSearch = this.applicationEl.addSearchOption();
    
    btnSearch.addEventListener(EVENT.SEARCH, ({detail}) => {
      this.searchFilter = detail;
      this.search();
    });
    
    btnSearch.addEventListener(EVENT.SEARCH_VALUE, ({detail})=>{
      this._list = [];
      // if(detail) this.applicationParentEl.setDataFilter(detail);
    });

    // let arrayNewFilter = PRESENCE_FILTER;
    // arrayNewFilter.push({
    //   type: CONSTANT.HTML_ELEMENT,
    //   element: new AonSwitch(),
    //   id: "aonSwitchFilter",
    //   name:"active",
    //   title:"Usuarios activos",
    //   checked:true
    // })
    
    // btnSearch.buildOptionsFilter(arrayNewFilter);//INPUTS
  }


  async getTable() {
    this.applicationEl.startLoader();
    if (this.isMobile()) {
      await this.getTableMobile();
    }
    else {
      await this.getTableDesk();
    } 
    this.applicationEl.stopLoader();
  }

  async getTableDesk() {
    const aonTable = this.getElement(this.TABLE_ID);
    if (aonTable) {
      try {
      aonTable.removeColumns();
      aonTable.addColumn(MSG.TITLE, "string", "name", "34%");
      aonTable.addColumn(MSG.DESCRIPTION, "string", "description","35%");
    
      //   const resp = await this.getData();
      //   aonTable.removeRows();
      //   resp.map((res) => {
      //     let lastStatus = res.last_date ? `${res.textStatus} ${AonDateUtils.setDateTimestampDay(res.last_date)}` : null;
      //     res.lastStatus = lastStatus;
      //     aonTable.addRow(res, (el) =>this.goAonRss(res, idx));
      //   });
      } catch (e) {
        console.log(e);
      }
    }
  }

  async getTableMobile() {
    const aonTable = this.getElement(this.TABLE_ID);
    if (aonTable) {
      try {
        // const resp = await this.getData();
        // aonTable.removeAllLi();
        // resp.map((res, idx) => {
        //   let subtitle = null;
        //   if(res.last_date){
        //     const dateParse = dateCustomDayHour(res.last_date) || AonDateUtils.setDateTimestamp(res.last_date);
        //     subtitle = `${dateParse} <span style="float: right;">${res.nameLocation}</span> `;
        //   }
        //   let options = {
        //     iconHtmlCustom: `${res.lettersHtml} <span style="float: right;color: rgba(0,0,0,.54);">${res.duration}</span>`,
        //     title: `${res.name}`,
        //     subtitle
        //   };
        //   aonTable.addLi(options, idx, (el) => this.goAonRss(res, idx));
        // });
      } catch (e) {
        console.log(e);
      }
    }
  }



  async getData() {
    let data = [];
    try {
      if(this._list.length){
        data = this._list;
      } else {
        let filter = null;
        // try {filter = {...this.applicationParentEl._filter};} catch (error) {}
        // const datos = await getRssList(filter);
        // if (datos) {
        //   sortBy(datos, 'last_date', 'desc').map(({
        //       time,
        //       last_date,
        //       status,
        //       coordinates,
        //       last_location,
        //     }) => {
           
        //       data.push({
        //         time,
        //         last_date,
        //         status,
        //         coordinates,
        //         last_location,
        //       });
        //     }
        //   );

        //   this._list = data;

        //   if(this.searchFilter){
        //     // data = this.filterSearch(["name", "nameLocation"], data);
        //   }
        // }
      }
    } catch (e) { console.log(e); }
    return data;
  }



  search(){
    this._list = this.filterSearch(["name", "nameLocation"], this._list);
    this.getTable();
  }

  filterSearch(keys, lists){
    let list = [];
    if(this.searchFilter && lists.length){
      list = lists.filter((lt)=> keys.some(key=>lt[key] && lt[key].toString().toLowerCase().includes(this.searchFilter.toLowerCase())));
    }
    return list;
  }

  async goAonRssAdd(res, idx) {
    let data = null;
    
    if(res && res.id){
      this.applicationEl.startLoading();
      data = await getRss({ id: res.id }).catch(err => this.showError(err));
      this.applicationEl.stopLoading();
    }

    let view = new AonRssAdd();
    view.data = data;
    this.applicationEl.setContent(view);
  }
}

window.customElements.define("aon-rss-list", AonRssList);
