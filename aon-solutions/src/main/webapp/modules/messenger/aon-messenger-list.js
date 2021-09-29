import { AonMobileList } from "../../components/aon-mobile-list.js";
import { AonTable } from "../../components/aon-table.js";
import { AonElement } from "../../components/AonElement.js";
import { CONSTANT, EVENT, MSG, TAG } from "../../environments/environments.js";
import { getTasks } from "../../services/taskService.js";
import { setFullDate, setTime, sortBy } from "../../services/utils.js";
import { SigninSidenav } from "../signin/signinEnums.js";
import { firstLetters } from "../signin/time-control/utils.js";
import { ICON_TYPES, MESSENGER_VIEWS, TASK_FILTER, TASK_SOURCE, TASK_STATUS, TASK_STATUS_VALUE } from "./MessengerEnums.js";
import { AonMessenger } from "./aon-messenger.js";
import { addTasks, setIndexTask, setTasks } from "./TaskCache.js";
import { getIconJson } from "./shared/utils.js";
import { getCustomers } from "../../services/registryService.js";
import { getTaskHolder } from "../../services/taskHolderService.js";
import * as LS from '../../services/localStorageService.js';

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
    this.build();
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
    this.INDEX = 0;
    this.MORE = true;
    this.applicationEl = this.getApplication();
    this.applicationParentEl = this.getApplicationParent();
    this.TASK_HOLDER = this.applicationParentEl.TASK_HOLDER;
    this.KEY_VIEW = Math.random();
    this.applicationEl.setKeyView(this.KEY_VIEW);
  }

  async build() {
    this.paintTable();
    this.buildToolbar();
  }

  async paintTable(divNotification) {
    this.TABLE_ID = this.id + "Table";
    let aonTable = this.isMobile() ?  new AonMobileList() : new AonTable();
    aonTable.id = this.TABLE_ID;
    if (divNotification) {
      await this.isFromNotification(aonTable, divNotification);
    } else {
      this.appendChild(aonTable);
    }

    if (this.isMobile()){
      aonTable.removeAllLi();
    } else {
      aonTable.removeColumns();
      aonTable.addColumn("", "string", "lettersHtml", "2%");
      aonTable.addColumn(MSG.NUMBER, "string", "newNumber", "5%");
      aonTable.addColumn(MSG.ISSUE, "string", "newTitle", "30%");
      aonTable.addColumn("Asignado", "string", "assigned", "20%");
      aonTable.addColumn(MSG.DATE, "string", "dateParse", "20%");
    } 

    aonTable.addEventListener(EVENT.MORE, ()=>{ 
      if(this.KEY_VIEW === this.applicationEl.getKeyView()){
        if(this.MORE) this.loadMore(); 
      }
    });

    setTasks([]);

    this.loadMore();
  }

  async isFromNotification(aonTable, divNotification){
    divNotification.appendChild(aonTable);
		const th = await getTaskHolder({reload:false}).catch(()=>null);
    if(th){
      this.TASK_HOLDER = th;
      let filter = this.getFilter();    
      filter.task_holder = th.id;
      this.setFilter(filter);
    }
  }

  buildToolbar() {
    this.applicationEl.removeToolbarOptions();
    if(this.isBeta()){
      if(this.isMobile()){
        this.applicationEl.addFloatOption(SigninSidenav.ADD, () =>  this.applicationParentEl.showView(MESSENGER_VIEWS.AON_MESSENGER_CHAT, {source:TASK_SOURCE.QUERY}));
      } else {
        this.applicationEl.addToolbarOption2(SigninSidenav.ADD, () =>{
          this.applicationParentEl.showView(MESSENGER_VIEWS.AON_MESSENGER_CHAT, {source:TASK_SOURCE.QUERY});
        });
      }
    }
    this.buildToolbarSearch();
  }

  buildToolbarSearch(){
    let btnSearch = this.applicationEl.addSearchOption();
    btnSearch.addEventListener(EVENT.SEARCH, ({detail}) => {
      this.setFilter({...this.getFilter(), page:0, perPage:30, search:detail});
      this.loadMoreSearch();
    });

    btnSearch.addEventListener(EVENT.SEARCH_VALUE, ({detail})=>{
      if(detail) {
        this.setFilter({...this.getFilter(), page:0, perPage:30, task_holder:detail.task_holder, registry: detail.registry, startDate: detail.startDate});
        this.loadMoreSearch();
      } 
    });

    btnSearch.buildOptionsFilter(TASK_FILTER);//INPUTS
    this.searchValueDefault();
  }

  searchValueDefault(){
    let registryEl = this.getElement("registry");
    let taskHolderEl = this.getElement("task_holder");
    let statusEl = this.getElement("status");
    getCustomers({reload:true, page:1, perPage:50}).then(customers=>{
      registryEl.setOptions(customers.map(c=> ({...c, value: c.id})) );
    })

    registryEl.addEventListener(EVENT.INPUT,async({target})=>{
        const value = target.value;
        if(value.length > 2){
          const cs = await getCustomers({reload:true, page:1, perPage:30, search: value});
          registryEl.setOptions( cs.map( c=> ({...c, value: c.id}) ) );
        }
    })

    if(this.applicationParentEl)
      this.applicationParentEl.getTaskHoldersEnterprise().then(ths=>
        taskHolderEl.setOptions(ths)
      );

    statusEl.setOptions(TASK_STATUS_VALUE);
  }

  async loadMoreSearch(){
    let aonTable = this.getElement(this.TABLE_ID);
    if(this.isMobile()){
      aonTable.removeAllLi();
    } else {
      aonTable.removeRows();
    }

    await this.loadMore();
  }

  async loadMore() {
    let application = this.getApplication();
    if(application) application.startLoader();
    const datos = await this.getData();
    addTasks(datos);
    if(this.isMobile())
      this.getDataMobile(datos);
    else 
      this.getDataDesktop(datos);

    if(application) application.stopLoader();    
	}

  getDataDesktop(datos){
    try {
      let aonTable = this.getElement(this.TABLE_ID);
      const company = LS.getCompany();
      const document = company ? company.document: undefined;
      datos.map((res, idx) => {
        const dateParse = firstLetters(setFullDate(res.date)) + " " + setTime(res.date);
        let newTitle  =  res.title;
        if(res.registry && res.registry.name && document !== res.registry.document) 
          newTitle = `<b>[${res.registry.name}]</b> ${newTitle}`;
  
        let assigned = "";
        if(res.task_holder&&res.task_holder.id)           assigned = res.task_holder.alias || res.task_holder.name; 
        else if(res.workgroup&&res.workgroup.description) assigned = res.workgroup.description;
  
        const newData = { 
          ...res, 
          newTitle,
          assigned,
          dateParse,
          lettersHtml: this.getIcon(res),
        };
        aonTable.addRow(newData, () =>  this.goMessengerChat(res, idx));
      });
    } catch (e) {}
  }
  
  getDataMobile(datos){
    try{
      let aonTable = this.getElement(this.TABLE_ID);
      datos.map((res, idx) => {
        const dateParse = firstLetters(setFullDate(res.date)) + " " + setTime(res.date);
        const newTitle  = `#${res.newNumber} ${res.title}`;
        const options = {
          title: newTitle,
          subtitle: dateParse,
          ...this.getIconList(res)
        };
        aonTable.addLi(options, idx, () => this.goMessengerChat(res, idx));
      });
    } catch (e) {}
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
          return {
            ...task,
            date:task.start_date,
            newNumber
          };
        });
      }
      data = sortBy(data, 'id','desc');
    } catch (error) {
      console.log("error>>",error);
      this.showError(error);
    }
    return data;
  }

  getIconList(res){      
    return {
      ...getIconJson(res),
      icon_class:ICON_TYPES.MATERIAL_ICONS_OUTLINED,
      icon_title:res.source,
    }
  }

  getIcon(res){
    let icon = getIconJson(res);

    let span = this.createElement(TAG.SPAN);
    span.style.color = icon.icon_color;
    span.title = res.source;

    let iOne = this.createElement("i");
    iOne.className = ICON_TYPES.MATERIAL_ICONS_OUTLINED;
    iOne.textContent = icon.icon;
    span.appendChild(iOne);

    //ICON WAY (IN OR OUT)
    // if(res.task_holder && res.task_holder.id === this.TASK_HOLDER.id) {
    //   span.appendChild(this.createIcon(MATERIAL_ICONS.ARROW_DROP_DOWN));
    // } else if(res.sender && res.sender.id === this.TASK_HOLDER.id){
    //   span.appendChild(this.createIcon(MATERIAL_ICONS.ARROW_DROP_UP)); // ,"-12px"
    // }

    return span.outerHTML;
  }

  // createIcon(icon, marginTop="11px"){
  //   let i = this.createElement("i");
  //   i.className = ICON_TYPES.MATERIAL_ICONS_OUTLINED;
  //   i.textContent = icon;
  //   i.style.marginTop = marginTop;
  //   i.style.position = "fixed";
  //   i.style.marginLeft = "-11px";
  //   return i;
  // }

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
