import { AonMobileList } from "../../components/aon-mobile-list.js";
import { AonTable } from "../../components/aon-table.js";
import { AonElement } from "../../components/AonElement.js";
import { COLORS, CONSTANT, CSS, EVENT, MATERIAL_ICONS } from "../../environments/environments.js";
import { DomainUserRoles } from "../../models/DomainUserRoles.js";
import { getDomainUserRoles } from "../../services/companyService.js";
import { getTasks } from "../../services/taskService.js";
import { setFullDate, setTime } from "../../services/utils.js";
import { SigninSidenav } from "../signin/signinEnums.js";
import { firstLetters } from "../signin/time-control/utils.js";
import { ICON_TYPES, MESSENGER_VIEWS, TASK_STATUS } from "./MessengerEnums.js";
import { AonMessenger } from "./aon-messenger.js";

export class AonMessengerList extends AonElement {
  TABLE_ID;
  _list;
  MORE;
  static get observedAttributes() {
    return [""];
  }

  setFilter(filter) {
		return this.setAttribute(CONSTANT.FILTER, JSON.stringify(filter));
	}

  getFilter() {
		return this.hasAttribute(CONSTANT.FILTER) ? JSON.parse(this.getAttribute(CONSTANT.FILTER))	: {page:0, perPage:30, status: TASK_STATUS.PENDING};
	}
  
  constructor() {
    super();
    this.id = this.id || MESSENGER_VIEWS.AON_MESSENGER_LIST;
    this.TOOLBAR = this.id + "Toolbar";
  }

  connectedCallback() {
    this.initialize();
    getDomainUserRoles({}).then(r => {
      this.dur = new DomainUserRoles(r);
      this.build();
    });
  }

  disconnectedCallback() {
    if (this.getApplication()){
      this.getApplication().removeFloatOption();
      this.getApplication().removeToolbarOptions();
    } 
  }

  initialize() {
    this.id = this.id || MESSENGER_VIEWS.AON_MESSENGER_LIST;
    this.TOOLBAR = this.id + "Toolbar";
    this.applicationEl = this.getApplication();
    this.applicationParentEl = this.getApplicationParent();
    this._list = [];
    this.MORE = true;
  }

  async build() {
    this.paintTable();
    this.buildToolbar();
  }

  paintTable(divNotification) {
    this.TABLE_ID = this.id + "Table";
    let aonTable = this.isMobile() ?  new AonMobileList() : new AonTable();
    aonTable.id = this.TABLE_ID;
    if (divNotification) {
      divNotification.appendChild(aonTable);
    } else {
      this.appendChild(aonTable);
    }

    if (this.isMobile()){
      aonTable.removeAllLi();
    } else {
      aonTable.removeColumns();
      aonTable.addColumn("", "icon", "icon", "2%");
      aonTable.addColumn("Titulo", "string", "titleDescription", "30%");
      aonTable.addColumn("Fecha", "string", "dateParse", "20%");
    } 
    aonTable.addEventListener(EVENT.MORE, ()=>{ if(this.MORE) this.loadMore(); })

    this.loadMore();
  
  }

  buildToolbar() {
    this.applicationEl.removeToolbarOptions();
    if(this.isBeta()){
      if(this.isMobile()){
        this.applicationEl.addFloatOption(SigninSidenav.ADD, () => this.applicationParentEl.showView(MESSENGER_VIEWS.AON_MESSENGER_CHAT));
      } else {
        this.applicationEl.addToolbarOption2(SigninSidenav.ADD, () => this.applicationParentEl.showView(MESSENGER_VIEWS.AON_MESSENGER_CHAT));
      }
    }
    this.buildToolbarSearch();
  }

  buildToolbarSearch(){
    let btnSearch = this.applicationEl.addSearchOption();
    
    btnSearch.addEventListener(EVENT.SEARCH, ({detail}) => {
      console.log(detail);
    });
    btnSearch.addEventListener(EVENT.SEARCH_VALUE, ({detail})=>{
      this._list = [];
      console.log(detail);
    });
    
    // btnSearch.buildOptionsFilter(INPUTS);//INPUTS
  }

  async getData(){
    let data = []
    try {
      let filter = this.getFilter();    
      filter.page = filter.page + 1;
      this.setFilter(filter);

      const tasks = await getTasks(filter);
      if(tasks.length == 0)
        this.MORE = false;
      else {
        data = tasks.map(task=>{
          const newNumber = (task.number ? task.number : 0).toString().padStart(5,0);
          const newTitle  = `#${newNumber} ${task.title}`;
          const titleDescription = `<span style="font-size:14px;font-weight: 500;">${newTitle}</span>`;
          return {
            ...task,
            date:task.start_date,
            newTitle,
            titleDescription
          };
        });
      }
    } catch (error) {
      this.showError(error);
    }
    return data;
  }

  async loadMore() {
		let aonTable = this.getElement(this.TABLE_ID);
    const datos = await this.getData();
    if(this.isMobile()){
      datos.map((res, idx) => {
        const dateParse = firstLetters(setFullDate(res.date)) + " " + setTime(res.date);
        const options = {
          icon: MATERIAL_ICONS.INFO,
          icon_color: CSS.variable(res.status == TASK_STATUS.PENDING || res.status == TASK_STATUS.IN_PROGRESS ? COLORS.ONLINE_GREEN : COLORS.GRAYSON),
          icon_class: ICON_TYPES.MATERIAL_ICONS_OUTLINED,
          title: res.newTitle,
          subtitle: dateParse, 
        };
        aonTable.addLi(options, idx, () => this.goMessengerChat(res));
      });
    } else {
      datos.map((res) => {
        const dateParse = firstLetters(setFullDate(res.date)) + " " + setTime(res.date);
        res.icon = MATERIAL_ICONS.INFO;
        res.icon_title = "status";
        res.icon_color = CSS.variable(res.status == TASK_STATUS.PENDING || res.status == TASK_STATUS.IN_PROGRESS ? COLORS.ONLINE_GREEN : COLORS.GRAYSON);
        res.icon_class = ICON_TYPES.MATERIAL_ICONS_OUTLINED;
        aonTable.addRow({...res, dateParse}, () =>  this.goMessengerChat(res));
      });
    }
	}

  goMessengerChat(res){
    if(!this.applicationParentEl){
      let aonMessenger = new AonMessenger();
      aonMessenger.data = res;
      this.rootPanel(aonMessenger);
    } else
      this.applicationParentEl.showView(MESSENGER_VIEWS.AON_MESSENGER_CHAT, res);
  }
}
window.customElements.define("aon-messenger-list", AonMessengerList);
