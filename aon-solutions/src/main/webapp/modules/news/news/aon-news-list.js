import { AonElement } from "../../../components/AonElement.js";
import { AonMobileList } from "../../../components/aon-mobile-list.js";
import { AonTable } from "../../../components/aon-table.js";
import { CONSTANT, EVENT, MSG, TAG } from "../../../environments/environments.js";
import { sortBy } from "../../../services/utils.js";
import { getNewOne, getNews} from "../../../services/newsService.js";
import { NewsCache } from "../NewsCache.js";
import { NewsEnums } from "../NewsEnums.js";
import {AonDateUtils} from "../../utils/AonDateUtils.js"
import {AonNewsAdd}  from "./aon-news-add.js";

export class AonNewsList extends AonElement {
  MORE;
  AON_TABLE;
  _filter;
  static get observedAttributes() {
    return [];
  }
  
  constructor() {
    super();
  }

  connectedCallback() {
    this.initialize();
    this.build();
  }

  disconnectedCallback() {}

  initialize() {
    this.id = this.id || NewsEnums.VIEWS_NEWS.AON_NEWS_LIST;
    this.TOOLBAR = this.id + "Toolbar";
    this.applicationEl = this.getApplication();
    this.MORE  = true;
    this.AON_TABLE = null;
    this._filter = {
			search: undefined,
			page:0, 
			perPage:30
		};
  }

  async build() {
    this.buildToolbar();
    this.paintTable();

    await this.loadMore(true);
  }

  buildToolbar() {
    this.applicationEl.removeToolbarOptions();
    this.applicationEl.addToolbarOption2(NewsEnums.NewsSidenav.ADD, () =>   this.goNewAdd());

    this.buildToolbarSearch();

    if (this.isMobile()) {
      this.applicationEl.addFloatOption(NewsEnums.NewsSidenav.ADD, () =>
        this.goNewAdd()
      );
    }
  }

  buildToolbarSearch(){
    let btnSearch = this.applicationEl.addSearchOption();
    
    btnSearch.addEventListener(EVENT.SEARCH, ({detail}) => {
      // this.search();
    });
    
    btnSearch.addEventListener(EVENT.SEARCH_VALUE, ({detail})=>{
      // this._list = [];
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

  paintTable() {
    this.AON_TABLE = this.isMobile() ? new AonMobileList() : new AonTable();
    this.AON_TABLE.id = this.id + "Table";

    this.appendChild(this.AON_TABLE);
 
    if (this.isMobile()) {
      this.AON_TABLE.removeAllLi();
    } else {
      this.AON_TABLE.removeColumns();
      this.AON_TABLE.addColumn(MSG.TITLE, "string", "title", "50%");
      this.AON_TABLE.addColumn(MSG.DATE, "string", "dateParse","20%");
      this.AON_TABLE.addColumn("Àmbito", "string", "scopeName","20%");
      this.AON_TABLE.addColumn(MSG.ACTIVE, "string", "activeStr","10%");
    }
    
    this.AON_TABLE.addEventListener(EVENT.MORE,() =>{
      if(this.MORE){
        this.loadMore(false)
      }
    });
  }

  async loadMore(reload) {
  
    this.AON_TABLE.loading(true);
    
    const datos = await this.getData();

    this.AON_TABLE.loading(false);

    if (reload) {
      NewsCache.setNews(datos);
    } else {
      NewsCache.addNews(datos);
    }

    if (this.isMobile()) {
      if (reload) this.AON_TABLE.removeAllLi();
      this.getDataMobile(datos);
    } else {
      if (reload) this.AON_TABLE.removeRows();
      this.getDataDesktop(datos);
    }

    if (reload && datos.length <= 0) {
      this.AON_TABLE.empty();
    }
  }

  getDataDesktop(datos) {
    try {
      datos.forEach((res, idx) => {
        this.AON_TABLE.addRow(
          {
            ...res
          },
          () => this.goNewAdd(res, idx)
        );
      });
    } catch (e) {
      console.log(e);
    }
  }

  getDataMobile(datos) {
    try {
      datos.map((res, idx) => {
        this.AON_TABLE.addLi(
          {
            title: res.title,
            subtitle:res.scopeName,
            subtitleTwo:res.dateParse,
          },
          idx,
          () => this.goNewAdd(res, idx)
        );
      });
    } catch (e) {
      console.log(e);
    }
  }

  async getData() {
    let data = [];
    try {
    
      this._filter.page = this._filter.page + 1;
      let news = await getNews(this._filter);
      if (news.length == 0) {
        this.MORE = false;
      } else {
        data = news
          .map((n) => {
            const dateParse = n.initDate ? (AonDateUtils.setFullDate(n.initDate) + " " +AonDateUtils.setTime(n.initDate)) : ""
            return {
              ...n,
              activeStr: n.active ? "Si" : "No",
              scopeName: n.scope && n.scope.description ? n.scope.description : "",
              dateParse
            }
          });
      }
      data = sortBy(data, "id", "desc");
    } catch (error) {
      console.log("error>>", error);
      this.showError(error);
    }
    return data;
  }

  async goNewAdd(res, idx) {
    const aonNewsAdd = new AonNewsAdd();

    if(idx) NewsCache.setIndexNews(idx);
    if (res && res.id) {
      this.applicationEl.startLoading();
      try {
        const data = await getNewOne({ id: res.id });
        aonNewsAdd.data = data;
      } catch (err) {
        this.showError(err);
      }
      this.applicationEl.stopLoading();
    }
   
    this.applicationEl.setContent(aonNewsAdd);
  }
}
window.customElements.define("aon-news-list", AonNewsList);
