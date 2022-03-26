import { AonMobileList } from "../../components/aon-mobile-list.js";
import { AonTable } from "../../components/aon-table.js";
import { AonElement } from "../../components/AonElement.js";
import { CONSTANT, EVENT, MSG, TAG } from "../../environments/environments.js";
import { getTasks } from "../../services/taskService.js";
import { sortBy } from "../../services/utils.js";
import { MESSENGER_VIEWS, TASK_FILTER, TASK_SOURCE, TASK_STATUS, TASK_STATUS_VALUE } from "./MessengerEnums.js";
import { AonMessenger } from "./aon-messenger.js";
import { addTasks, setIndexTask, setTasks } from "./TaskCache.js";
import { getIconJson } from "./shared/utils.js";
import { getCustomers } from "../../services/registryService.js";
import { getTaskHolder } from "../../services/taskHolderService.js";
import * as LS from '../../services/localStorageService.js';
import { AonDateUtils } from "../utils/AonDateUtils.js";
import { SigninSidenav } from "../timecontrol/signinEnums.js";
import { firstLetters } from "../timecontrol/time-control/utils.js";

export class AonMessengerList extends AonElement {
  MORE;
  TASK_HOLDER;
  AON_TABLE;
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
    if(this.AON_TABLE) this.AON_TABLE.removeEventListener(EVENT.MORE, this.fnMore);
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
    this.AON_TABLE = null;
  }

  async build() {
    this.paintTable();
    this.buildToolbar();
  }

  async paintTable(divNotification) {
    this.AON_TABLE = this.isMobile() ?  new AonMobileList() : new AonTable();
    this.AON_TABLE.id = this.id + "Table";
    if (divNotification) {
      await this.isFromNotification(this.AON_TABLE, divNotification);
    } else {
      this.appendChild(this.AON_TABLE);
    }

    if (this.isMobile()){
      this.AON_TABLE.removeAllLi();
    } else {
      this.AON_TABLE.removeColumns();
      this.AON_TABLE.addColumn("", "string", "lettersHtml", "2%");
      this.AON_TABLE.addColumn(MSG.NUMBER, "string", "newNumber", "5%");
      this.AON_TABLE.addColumn(MSG.ISSUE, "string", "newTitle", "35%");
      this.AON_TABLE.addColumn("Asignado", "string", "assigned", "20%");
      this.AON_TABLE.addColumn(MSG.DATE, "string", "dateParse", "15%");
    } 

    this.fnMore = () => { if(this.MORE) this.loadMore()}; 

    this.AON_TABLE.addEventListener(EVENT.MORE, this.fnMore);

    setTasks([]);

    const application = this.getApplication();
    if(application) application.startLoader();
    await this.loadMore(true);
    if(application) application.stopLoader();    
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
    if(this.isMobile()){
      this.applicationEl.addFloatOption(SigninSidenav.ADD, () =>  this.applicationParentEl.showView(MESSENGER_VIEWS.AON_MESSENGER_CHAT, {source:TASK_SOURCE.QUERY}));
    } else {
      this.applicationEl.addToolbarOption2(SigninSidenav.ADD, () =>{
        this.applicationParentEl.showView(MESSENGER_VIEWS.AON_MESSENGER_CHAT, {source:TASK_SOURCE.QUERY});
      });
    }
    this.buildToolbarSearch();
  }

  buildToolbarSearch(){
    let btnSearch = this.applicationEl.addSearchOption();
    btnSearch.addEventListener(EVENT.SEARCH, ({detail}) => {
      this.setFilter({...this.getFilter(), page:0, perPage:30, search:detail});
      this.loadMore(true);
    });

    btnSearch.addEventListener(EVENT.SEARCH_VALUE, ({detail})=>{
      if(detail) {
        this.setFilter({...this.getFilter(), page:0, perPage:30, task_holder:detail.task_holder, registry: detail.registry, startDate: detail.startDate, workgroup: detail.workgroup});
        this.loadMore(true);
      } 
    });

    btnSearch.buildOptionsFilter(TASK_FILTER);//INPUTS
    this.searchValueDefault();
  }

  searchValueDefault(){
    let registryEl = this.getElement("registry");
    let taskHolderEl = this.getElement("task_holder");
    let statusEl = this.getElement("status");
    let workgroup = this.getElement("workgroup");
    getCustomers({reload:true, page:1, perPage:50}).then(customers=>{
      registryEl.setOptions(customers.map(c=> ({...c, value: c.id})) );
    });

    registryEl.addEventListener(EVENT.INPUT,async({target})=>{
        const value = target.value;
        if(value.length > 2){
          const cs = await getCustomers({reload:true, page:1, perPage:30, value});
          registryEl.setOptions( cs.map( c=> ({...c, value: c.id}) ) );
        }
    })
    
    this.getApplicationParent().getMyWorkgroups().then(wgs=>{
      workgroup.setOptions(wgs);
    });

    if(this.getApplicationParent())
      this.getApplicationParent().getTaskHoldersEnterprise().then(ths=>taskHolderEl.setOptions(ths));

    statusEl.setOptions(TASK_STATUS_VALUE);
  }

  async loadMore(search = false) {

    const application = this.getApplication();

    if(application && !search) application.startLoader();

    const datos = await this.getData();

    addTasks(datos);
    if(this.isMobile()){
      if(search)this.AON_TABLE.removeAllLi();
      this.getDataMobile(datos);
    } else {
      if(search)this.AON_TABLE.removeRows();
      this.getDataDesktop(datos);
    }

    if(application && !search) application.stopLoader();    
	}

  getDataDesktop(datos){
    try {
      const company = LS.getCompany();
      const domainId = parseInt(LS.getDomainId());
      const document = company ? company.document: undefined;
      const documentTh = this.TASK_HOLDER ? this.TASK_HOLDER.document  : undefined;
      datos.map((res, idx) => {
        this.AON_TABLE.addRow({ 
          ...res, 
          dateParse: firstLetters(AonDateUtils.setFullDate(res.date)) + " " + AonDateUtils.setTime(res.date),
          newTitle: this.getNewTitle(res, document, documentTh),
          assigned: this.getAssigned(res, domainId),
          lettersHtml: this.getIcon(res),
        }, () =>  this.goMessengerChat(res, idx));
      });
    } catch (e) {
      console.log(e);
    }
  }
  
  getDataMobile(datos){
    try{
      datos.map((res, idx) => {
        this.AON_TABLE.addLi({
          title: `#${res.newNumber} ${res.title}`,
          subtitle: firstLetters(AonDateUtils.setFullDate(res.date)) + " " + AonDateUtils.setTime(res.date),
          ...this.getIconList(res)
        }, idx, () => this.goMessengerChat(res, idx));
      });
    } catch (e) {
      console.log(e);
    }
  }

  async getData(){
    let data = []
    // try {
      let filter = this.getFilter();    
      filter.page = filter.page + 1;
      this.setFilter(filter);
      let tasks = await getTasks(filter);
    
      if(tasks.length == 0)
        this.MORE = false;
      else {
        data = tasks
        .filter((v,idx)=>tasks.findIndex((m) => m.id === v.id) === idx)
        .map(task=>({
            ...task,
            date:task.start_date,
            newNumber: (task.number ? task.number : 0).toString().padStart(5,0)
        }));
      }
      data = sortBy(data, 'id','desc');
    // } catch (error) {
    //   console.log("error>>",error);
    //   this.showError(error);
    // }
    return data;
  }

  getIconList = ({source,status}) => ({      
    ...getIconJson({source,status}),
    icon_class:CONSTANT.MATERIAL_ICONS_OUTLINED,
    icon_title:source,
  })


  getIcon({source,status}){
    let icon = getIconJson({source,status});

    let span = this.createElement(TAG.SPAN);
    span.style.color = icon.icon_color;
    span.title = source;

    let iOne = this.createElement("i");
    iOne.className = CONSTANT.MATERIAL_ICONS_OUTLINED;
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

  getAssigned(res, domainId){
    let assigned = "";
    if( res.domain && res.domain.id && domainId !== parseInt(res.domain.id) )  
      assigned = res.domain.description;
    else if(res.task_holder&&res.task_holder.id)                                                
      assigned = res.task_holder.alias || res.task_holder.name; 
    else if(res.workgroup&&res.workgroup.description) 
      assigned = res.workgroup.description;
    return assigned;
  }

  getNewTitle(res, document, documentTh){
    let div = this.createElement(TAG.DIV);
    div.style.position = "relative";

    let newTitle  = res.title;
    if(res.registry && res.registry.name && document !== res.registry.document) 
      newTitle = `[${res.registry.name}] ${newTitle}`;
    else if(res.sender && res.sender.name && documentTh !== res.sender.document) 
      newTitle = `[${res.sender.name}] ${newTitle}`;

    let description = res.description;
    try { description = JSON.parse(res.description).observation;  } catch (e) {}

    let divTwo = this.createElement(TAG.DIV);
    divTwo.style = `font-weight: 550; bottom:${description ? 1 : -9}px; position:absolute; left:0; right:0; white-space:nowrap; text-overflow:ellipsis; overflow: hidden;`;
    divTwo.innerText = newTitle;
    divTwo.title = newTitle;
    div.appendChild(divTwo);

    if(description){
      const dText = description.replace(/<[^>]+>|&nbsp;|\n/g, ' ');
      let divThree = this.createElement(TAG.DIV);
      divThree.textContent = dText;
      divThree.title = dText;
      divThree.style = "color:grey; position:absolute; top:3px; left:0; right:0; white-space:nowrap; text-overflow:ellipsis; overflow: hidden;";
      div.appendChild(divThree);
    }
  
    return div.outerHTML;
  }

  // createIcon(icon, marginTop="11px"){
  //   let i = this.createElement("i");
  //   i.className = CONSTANT.MATERIAL_ICONS_OUTLINED;
  //   i.textContent = icon;
  //   i.style.marginTop = marginTop;
  //   i.style.position = "fixed";
  //   i.style.marginLeft = "-11px";
  //   return i;
  // }


  // convertToPlain(html){
  //   let tmp = this.createElement("div");
  //   tmp.innerHTML = html;
  //   return tmp.textContent || tmp.innerText || "";
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
