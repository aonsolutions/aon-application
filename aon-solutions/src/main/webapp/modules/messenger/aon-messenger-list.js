import { AonMobileList } from "../../components/aon-mobile-list.js";
import { AonTable } from "../../components/aon-table.js";
import { AonElement } from "../../components/AonElement.js";
import { CONSTANT, CSS, EVENT, MSG, TAG, MATERIAL_ICONS, AON_ICONS } from "../../environments/environments.js";
import { getTaskOne, getTasks } from "../../services/taskService.js";
import { sortBy } from "../../services/utils.js";
import { MESSENGER_VIEWS, TAG_TYPE, TASK_SOURCE, TASK_STATUS } from "./MessengerEnums.js";
import { AonMessenger } from "./aon-messenger.js";
import { addTasks, setIndexTask, setTasks } from "./TaskCache.js";
import { getIconJson } from "./shared/utils.js";
import { getTaskHolder } from "../../services/taskHolderService.js";
import * as LS from '../../services/localStorageService.js';
import { AonDateUtils } from "../utils/AonDateUtils.js";
import { SigninSidenav } from "../timecontrol/signinEnums.js";
import { firstLetters, StringTwoLetters } from "../timecontrol/time-control/utils.js";
import { createTagHtml } from "./shared/creationUtils.js";
import { AonIcon } from "../../components/aon-icon.js";

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
    if (this.getApplication())
      this.getApplication().removeFloatOption();
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
    if(this.isMobile())
      this.applicationEl.addFloatOption(SigninSidenav.ADD, () =>  this.applicationParentEl.showView(MESSENGER_VIEWS.AON_MESSENGER_CHAT, {source:TASK_SOURCE.QUERY}));
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
      this.AON_TABLE.addColumn(MSG.ISSUE, "string", "newTitle", "50%");
      this.AON_TABLE.addColumn("Asignado", "string", "assigned", "5%");
      this.AON_TABLE.addColumn(MSG.DATE, "string", "dateParse", "14%");
    } 

    this.fnMore = () => { if(this.MORE) this.loadMore()}; 

    this.AON_TABLE.addEventListener(EVENT.MORE, this.fnMore);

    await this.loadMore(true);  
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

  async loadMore(reload) {

    const application = this.getApplication();

    // if(reload) application.startLoader();
    
    const datos = await this.getData();

    // if(reload) application.stopLoader();

    if(reload){
      setTasks(datos);
    } else {
      addTasks(datos);
    }

    if(this.isMobile()){
      if(reload) this.AON_TABLE.removeAllLi();
      this.getDataMobile(datos);
    } else {
      if(reload) this.AON_TABLE.removeRows();
      this.getDataDesktop(datos);
    } 

    if(reload && datos.length<=0) {
      this.AON_TABLE.empty();
    }
	}

  getDataDesktop(datos){
    try {
      const company = LS.getCompany();
      const domainId = parseInt(LS.getDomainId());
      const document = company ? company.document: undefined;
      const documentTh = this.TASK_HOLDER ? this.TASK_HOLDER.document  : undefined;
      datos.map((res, idx) => {
       const row = this.AON_TABLE.addRow({ 
          ...res, 
          dateParse: this.getNewDateParse(res),
          newTitle: this.getTitleDesktop(res, document, documentTh),
          assigned: this.getAssigned(res, domainId),
          lettersHtml: this.getIcon(res),
        }, () =>  this.goMessengerChat(res, idx));

        row.style.padding = "16px 0px 20px";
      });
    } catch (e) {
      console.log(e);
    }
  }
  
  getDataMobile(datos){
    try{
      const company = LS.getCompany();
      const document = company ? company.document: undefined;
      const documentTh = this.TASK_HOLDER ? this.TASK_HOLDER.document  : undefined;
      datos.map((res, idx) => {
       this.AON_TABLE.addLi({
          title: this.getTitleMobile(res, document, documentTh), //`${res.newNumber} ${res.title}`,
          subtitle: this.getSubtitleMobileOne(res),//AonDateUtils.getDayMonth(res.date),
          subtitleTwo: this.getSubtitleMobile(res),//AonDateUtils.getDayMonth(res.date),
          ...this.getIconList(res)
        }, idx, () => this.goMessengerChat(res, idx));
      });
    } catch (e) {
      console.log(e);
    }
  }

  async getData(){
    let data = []
    try {
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
            newNumber: "#"+(task.number ? task.number : 0).toString().padStart(5,0)
        }));
      }
      data = sortBy(data, 'id','desc');
    } catch (error) {
      console.log("error>>",error);
      this.showError(error);
    }
    return data;
  }

  getTitleDesktop(res, document, documentTh){
  
    let div = this.createElement(TAG.DIV);
    div.style.position = "relative";

    div.appendChild(this.getTitleHtmlDesktop(res));
    
    div.appendChild(this.getSubTitleHtml(res, document, documentTh));

    return div.outerHTML;
  }

  getTitleHtmlDesktop(res){
    const title = res.title || "Sin asunto";
    let div = this.createElement(TAG.DIV);
    div.style.top = "-15px";
    div.style.position = "absolute";
    div.style.left = "0";
    div.style.right = "0";

    let divFlex = this.createElement(TAG.DIV);
    divFlex.style.display = "flex";
    divFlex.style.whiteSpace ="nowrap";
    div.appendChild(divFlex);

    const divOne = this.createElement(TAG.DIV);
    divOne.innerText = title;
    divOne.title = title;
    divOne.style.fontWeight = 550;
    divOne.style.fontSize = "14px";
    divOne.style.overflow = CONSTANT.HIDDEN;
    divFlex.appendChild(divOne);
    
    const divTwo = this.createElement(TAG.DIV);
    divTwo.style.display = 'flex';
    divTwo.style.overflow = CONSTANT.HIDDEN;
    divTwo.style.marginLeft = "5px";
    divTwo.style.marginTop = "-4px";
    divTwo.style.gap = "3px";
    divFlex.appendChild(divTwo);

    this.getTagsLabel(res.tags).forEach(tag => {
      const divTag = createTagHtml(tag, divTwo);
      divTag.style.margin = "0";
      divTag.style.textAlign = "center";
    });

    return div;
  }

  getSubTitleHtml(res, document, documentTh){
    const type = this.getTagType(res.tags) || "";
    // sender
    const sender  = this.getSender(res, document, documentTh);
  
    //----------- DESCRIPTION
    let description = res.description;
    try { description = JSON.parse(res.description).observation;  } catch (e) {}

    let div = this.createElement(TAG.DIV);
    div.style = "position:absolute; top:6px; left:0; right:0;white-space:nowrap; text-overflow:ellipsis; overflow: hidden;";

    let spanOne = this.createElement(TAG.SPAN);
    const subTitle = type+" "+ res.newNumber+" "+ sender;
    spanOne.title = subTitle;
    spanOne.textContent = subTitle;
    div.appendChild(spanOne);

    if(description){
      const dText = description.replace(/<[^>]+>|&nbsp;|\n/g, ' ');
      let span = this.createElement(TAG.SPAN);
      span.textContent = dText;
      span.title = dText;
      span.style.color = "grey";
      span.style.marginLeft = "3px";
      div.appendChild(span);
    }
    return div;
  }
  
  getTitleMobile(res, document, documentTh){
    const div = this.createElement(TAG.DIV);
    div.style.display = "flex";

    const senderDiv = this.createElement(TAG.DIV);
    senderDiv.style.overflow = "hidden";
    senderDiv.style.whiteSpace = "hidden";
    senderDiv.style.textOverflow = "ellipsis";
    senderDiv.style.fontWeight = "500";
    senderDiv.innerText = this.getSender(res, document, documentTh);
    div.appendChild(senderDiv);

    const dateDiv = this.createElement(TAG.DIV);
    dateDiv.style.color = "grey";
    dateDiv.style.marginLeft = "auto";
    dateDiv.style.fontSize = "14px";
    dateDiv.style.fontWeight = "500";
    div.appendChild(dateDiv);

    let date = AonDateUtils.getDayMonthOrFull(res.date);
    dateDiv.innerText = date;

    return div.outerHTML;
  }

  getSubtitleMobileOne(res){
    const div = this.createElement(TAG.DIV);
    div.style.color = "black";
    div.innerText = res.title || "Sin asunto";
    return div.outerHTML;
  }

  getSubtitleMobile(res){
    const type = this.getTagType(res.tags) || "";
    const div = this.createElement(TAG.DIV);
    div.style.display = "flex";

    const divOne = this.createElement(TAG.DIV);
    divOne.innerText = type+" "+ res.newNumber;
    div.appendChild(divOne);

    const divTwo = this.createElement(TAG.DIV);
    divTwo.style.display = "flex";
    divTwo.style.gap = "2px";
    divTwo.style.marginLeft = "2px";
    divTwo.style.overflow = "hidden";
    // divTwo.style.textOverflow = "ellipsis";
    // divTwo.style.flexWrap = "nowrap";

    divTwo.style.height =  "19px";
    divTwo.style.flexWrap =  "wrap";
    div.appendChild(divTwo);

    this.getTagsLabel(res.tags).forEach(tag => {
      const divTag = createTagHtml(tag, divTwo);
      divTag.style.margin = "0";
      divTag.style.textAlign = "center";
      divTag.style.padding = "1px 4px";
      divTwo.style.height =  "19px";
      divTag.style.fontSize = "12px";
    });


   return div.outerHTML;
  }


  getNewDateParse(res){
    const dateText =  firstLetters(AonDateUtils.setFullDate(res.date)) + " " + AonDateUtils.setTime(res.date);
    const email = res.gtask_id;

    const div = this.createElement(TAG.DIV);
    div.style.position = "relative";
    
    const style = `position:absolute; left:0; right:0; white-space:nowrap; text-overflow:ellipsis; overflow: hidden;`;

    let divTwo = this.createElement(TAG.DIV);
    divTwo.style = `bottom:${email ? -1 : -9}px; ${style}`;
    divTwo.innerText = dateText;
    divTwo.title = dateText;
    div.appendChild(divTwo);
    
    if(email){
      let divThree = this.createElement(TAG.DIV);
      divThree.textContent = email;
      divThree.title = email;
      divThree.style = `color:grey; position:absolute; top:4px; ${style}`;
      div.appendChild(divThree);
    } 
    return div.outerHTML;

  }   

  getSender(res, document, documentTh){
      let sender  ="";
      if(res.registry && res.registry.name && document !== res.registry.document) 
        sender = `${res.registry.name} ${sender}`;
      else if(res.sender && res.sender.name && documentTh !== res.sender.document) 
        sender = `${res.sender.name} ${sender}`;
      else if(res.workgroup && res.workgroup.description) // GRUPO ASIGNADO
        sender = res.workgroup.description;
      else 
        sender = "SIN GRUPO ASIGNADO";

      return sender;
  }

  getAssigned(res, domainId){
    const workgroup = res.workgroup;
    let person = undefined;
    let workgroupDescription = undefined;
    if( res.domain && res.domain.id && domainId !== parseInt(res.domain.id) )  
      person = res.domain.description;
    else if(res.task_holder&&res.task_holder.id)                                                
      person = res.task_holder.alias || res.task_holder.name; 

    if(workgroup&&workgroup.description) 
      workgroupDescription = workgroup.description;

    let div = this.createElement(TAG.DIV);
    div.style.display = "flex";
    div.style.gap = "4px";

    if(workgroupDescription){
      let divOne = this.createElement(TAG.DIV);
      const color = person ? "949393" :"FF6F1D";
      divOne.style = `border: 2px solid #${color}; color: #${color};border-radius: 29px;height: 23px; line-height: 21px; width: 23px; display: block  font-size: 15px;text-align: center`;
      divOne.title  = workgroupDescription;
      div.appendChild(divOne);

      let icon = this.createElement(TAG.I);
      icon.style.fontSize = "17px";
      icon.style.lineHeight = "19px";
      icon.className = CSS.MATERIAL_ICONS;
      icon.innerText = MATERIAL_ICONS.PEOPLE_ALT;
      divOne.appendChild(icon);
    }

    if(person){
      let divTwo = this.createElement(TAG.DIV);
      divTwo.style = "border: 2px solid var(--aonBlack); color: var(--aonBlack);border-radius: 29px;height: 23px; line-height: 21px; width: 23px; display: block  font-size: 15px;text-align: center;";
      divTwo.innerText  = StringTwoLetters(person.toUpperCase());
      divTwo.title  = person;
      div.appendChild(divTwo);
    }

    if(!workgroupDescription && !person){
      let divOne = this.createElement(TAG.DIV);
      divOne.style = "border: 2px solid #f44336; color: #f44336;border-radius: 29px;height: 23px; line-height: 21px; width: 23px; display: block  font-size: 15px;text-align: center;";
      divOne.title  = "Sin asignar";
      div.appendChild(divOne);

      let icon = this.createElement(TAG.I);
      icon.style.fontSize = "17px";
      icon.style.lineHeight = "19px";
      icon.className = CSS.MATERIAL_ICONS;
      icon.innerText = MATERIAL_ICONS.GROUP_OFF;
      divOne.appendChild(icon);
    }

    return div.outerHTML;
  }


  getIconList = ({source,status}) => ({      
    ...getIconJson({source,status}),
    icon_class:CONSTANT.MATERIAL_ICONS_OUTLINED,
    icon_title:source,
  })

  getIcon({source,status, parent}){
    let iconJson = getIconJson({source,status});

    let span = this.createElement(TAG.SPAN);
    span.style.color = iconJson.icon_color;
    span.title = source;

    let icon = this.createElement(TAG.I);

    if(parent) {
      icon = new AonIcon();
      icon.icon = AON_ICONS.AON_BRANCH;
      icon.color = iconJson.icon_color;
    } else {
      icon.className = CONSTANT.MATERIAL_ICONS_OUTLINED;
      icon.textContent = iconJson.icon;
    }

    span.appendChild(icon);

    return span.outerHTML;
  }

  getTagType(tags){
    if(tags && tags.length){
      let type = tags.find(tag=> tag.tag_type === TAG_TYPE.TASK_TYPE);
      return type ? type.name : null;
    }
    return null;
  }

  getTagsLabel(tags){
    if(tags && tags.length)
      return tags.filter(tag=> tag.tag_type === TAG_TYPE.TASK_LABEL);
    return [];
  }

  isCau(){  //IS CAU
    return parseInt(localStorage.getItem("taskCau") || 0);
  }

  async goMessengerChat(res, idx){
    setIndexTask(idx);
    if(!this.applicationParentEl){
      let aonMessenger = new AonMessenger();
      aonMessenger.data = res;
      this.rootPanel(aonMessenger);
    } else if(res && res.id){
      this.applicationEl.startLoading();
      try{
        const data = await getTaskOne({id:res.id});
        this.applicationParentEl.showView(MESSENGER_VIEWS.AON_MESSENGER_CHAT, data);
      } catch(err){
        this.showError(err);
      }
      this.applicationEl.stopLoading();
    }

  }
}
window.customElements.define("aon-messenger-list", AonMessengerList);
