import { AonMobileList } from "../../components/aon-mobile-list.js";
import { AonTable } from "../../components/aon-table.js";
import { AonElement } from "../../components/AonElement.js";
import { CONSTANT, EVENT, MATERIAL_ICONS, MSG, TAG } from "../../environments/environments.js";
import { DomainUserRoles } from "../../models/DomainUserRoles.js";
import { getDomainUserRoles } from "../../services/companyService.js";
import { getTasks } from "../../services/taskService.js";
import { setFullDate, setTime } from "../../services/utils.js";
import { SigninSidenav } from "../signin/signinEnums.js";
import { firstLetters } from "../signin/time-control/utils.js";
import { ICON_TYPES, MessengerOptions, MESSENGER_VIEWS, TASK_SOURCE, TASK_STATUS } from "./MessengerEnums.js";
import { AonMessenger } from "./aon-messenger.js";
import { addTasks, setIndexTask } from "./TaskCache.js";

export class AonMessengerList extends AonElement {
  TABLE_ID;
  MORE;
  KEY_VIEW;
  TASK_HOLDER;
  static get observedAttributes() {
    return [];
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
    this.TASK_HOLDER = this.applicationParentEl.TASK_HOLDER;
    this.INDEX = 0;
    this.MORE = true;
    this.KEY_VIEW = Math.random();
    this.applicationEl.setKeyView(this.KEY_VIEW);
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
      // aonTable.addColumn("  ", "icon", "icon", "2%");
      aonTable.addColumn("", "string", "lettersHtml", "2%");
      aonTable.addColumn(MSG.NUMBER, "string", "newNumber", "5%");
      aonTable.addColumn(MSG.TITLE, "string", "title", "30%");
      aonTable.addColumn(MSG.DATE, "string", "dateParse", "20%");
    } 

    aonTable.addEventListener(EVENT.MORE, ()=>{ 
      if(this.KEY_VIEW === this.applicationEl.getKeyView()){
        if(this.MORE) this.loadMore(); 
      }
    });

    this.loadMore();
  
  }

  buildToolbar() {
    this.applicationEl.removeToolbarOptions();
    if(this.isBeta()){
      if(this.isMobile()){
        this.applicationEl.addFloatOption(SigninSidenav.ADD, ({target}) =>  this.addTask(target));
      } else {
        this.applicationEl.addToolbarOption2(SigninSidenav.ADD, ({target}) =>{
          if(TASK_SOURCE.CAU === this.applicationParentEl._filter.source){
            this.applicationParentEl.showView(MESSENGER_VIEWS.AON_MESSENGER_CHAT, {source:TASK_SOURCE.CAU})
          } else {
            this.addTask(target);
          }
        });
      }
    }
    this.buildToolbarSearch();
  }

  buildToolbarSearch(){
    let btnSearch = this.applicationEl.addSearchOption();
    btnSearch.addEventListener(EVENT.SEARCH, ({detail}) => {
      let filter = this.getFilter();    
      filter.search = detail;
      this.setFilter(filter);
      this.paintTable();
    });
    // btnSearch.addEventListener(EVENT.SEARCH_VALUE, ({detail})=>{ });
    
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
          return {
            ...task,
            date:task.start_date,
            newTitle,
            newNumber
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
    addTasks(datos);
    if(this.isMobile()){
      datos.map((res, idx) => {
        const dateParse = firstLetters(setFullDate(res.date)) + " " + setTime(res.date);
        const options = {
          title: res.newTitle,
          subtitle: dateParse,
          ...this.getIconList(res)
        };
        aonTable.addLi(options, idx, () => this.goMessengerChat(res, idx));
      });
    } else {
      datos.map((res, idx) => {
        const dateParse = firstLetters(setFullDate(res.date)) + " " + setTime(res.date);
        const newData = { 
          ...res, 
          // ...this.getIconList(res),
          lettersHtml: this.getIcon(res),
          dateParse
        };
        aonTable.addRow(newData, () =>  this.goMessengerChat(res, idx));
      });
    }
	}

  addTask(button){
		const left = button.getBoundingClientRect().left;
    let top  = button.getBoundingClientRect().top;
    if(this.isMobile()) top = top - 50;

    let options = [{
      name: MSG.QUERY,
      icon: 'assignment',
      fn: () => this.applicationParentEl.showView(MESSENGER_VIEWS.AON_MESSENGER_CHAT, {source:TASK_SOURCE.CAU})
    }, {
      name: MSG.REQUEST,
      icon: 'archive',
      fn: () => this.applicationParentEl.showView(MESSENGER_VIEWS.AON_MESSENGER_CHAT, {source:TASK_SOURCE.GITHUB})
    }];

    const d = this.applicationEl.getOptionDialog();
    d.setMenuOptions(options, top, left);
    d.open();
  }


  getIconList(res){
    const {AON_MESSENGER_LIST_OPEN,AON_MESSENGER_LIST_CLOSE,AON_MESSENGER_LIST_ARCHIVE} = MessengerOptions;
    let icon = MATERIAL_ICONS.INFO;
    if(res.source===TASK_SOURCE.CAU) 
      icon = MATERIAL_ICONS.SUPPORT_AGENT;
    else if(res.source===TASK_SOURCE.GITHUB) 
      icon = MATERIAL_ICONS.ASSIGNMENT;

    let color = AON_MESSENGER_LIST_OPEN.icon_color;
    if(res.status === TASK_STATUS.FINISHED) 
      color = AON_MESSENGER_LIST_CLOSE.icon_color;
    else if(res.status === TASK_STATUS.DELETED) 
      color = AON_MESSENGER_LIST_ARCHIVE.icon_color;
      
    return {
      icon,
      icon_color:color,//CSS.variable(res.status == TASK_STATUS.PENDING || res.status == TASK_STATUS.IN_PROGRESS ? COLORS.ONLINE_GREEN : COLORS.GRAYSON),
      icon_class:ICON_TYPES.MATERIAL_ICONS_OUTLINED,
      icon_title:res.source,
    }
  }

  getIcon(res){
    const {AON_MESSENGER_LIST_OPEN,AON_MESSENGER_LIST_CLOSE,AON_MESSENGER_LIST_ARCHIVE} = MessengerOptions;
    let icon = MATERIAL_ICONS.INFO;
    if(res.source===TASK_SOURCE.CAU) 
      icon = MATERIAL_ICONS.SUPPORT_AGENT;
    else if(res.source===TASK_SOURCE.GITHUB) 
      icon = MATERIAL_ICONS.ASSIGNMENT;

    let color = AON_MESSENGER_LIST_OPEN.icon_color;
    if(res.status === TASK_STATUS.FINISHED) 
      color = AON_MESSENGER_LIST_CLOSE.icon_color;
    else if(res.status === TASK_STATUS.DELETED) 
      color = AON_MESSENGER_LIST_ARCHIVE.icon_color;

    let span = this.createElement(TAG.SPAN);
    span.style.color = color;
    span.title = res.source;

    let iOne = this.createElement("i");
    iOne.className = ICON_TYPES.MATERIAL_ICONS_OUTLINED;
    iOne.textContent = icon;
    span.appendChild(iOne);

    //ICON WAY (IN OR OUT)
    // if(res.task_holder && res.task_holder.id === this.TASK_HOLDER.id) {
    //   span.appendChild(this.createIcon(MATERIAL_ICONS.ARROW_DROP_DOWN));
    // } else if(res.sender && res.sender.id === this.TASK_HOLDER.id){
    //   span.appendChild(this.createIcon(MATERIAL_ICONS.ARROW_DROP_UP)); // ,"-12px"
    // }

    return span.outerHTML;
  }

  createIcon(icon, marginTop="11px"){
    let i = this.createElement("i");
    i.className = ICON_TYPES.MATERIAL_ICONS_OUTLINED;
    i.textContent = icon;
    i.style.marginTop = marginTop;
    i.style.position = "fixed";
    i.style.marginLeft = "-11px";
    return i;
  }

  goMessengerChat(res, idx){
    setIndexTask(idx);
    if(!this.applicationParentEl){
      let aonMessenger = new AonMessenger();
      aonMessenger.data = res;
      this.rootPanel(aonMessenger);
    } else
      this.applicationParentEl.showView(MESSENGER_VIEWS.AON_MESSENGER_CHAT, res);
  }
}
window.customElements.define("aon-messenger-list", AonMessengerList);
