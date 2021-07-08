import { AonToolbar } from "../../components/aon-toolbar.js";
import { AonElement } from "../../components/AonElement.js";
import { COLORS, CONSTANT, CSS, EVENT } from "../../environments/environments.js";
import { ToolbarType } from "../../models/enums.js";
import { newComponent, setClasses, setStyles, setValueName, waitEl } from "../../services/utils.js";
import { createMainView, createMobileMainView, inputId } from "./createComponents.js";
import { buildDesktopChat, buildMobileChat, fillChat } from "./shared/messenger-chat.js";
import { buildDesktopWritter, sendMessage } from "./shared/messenger-writter.js";
import { MESSENGER_COMPONENTS, MESSENGER_IDS, MESSENGER_VIEWS, TASK_WORKFLOW_TYPE } from "./MessengerEnums.js";
import { createOutlinedMaterialIcon } from "./shared/creationUtils.js";
import * as ACTIONS from "../actions.js";
import { saveTask, getTaskWorkflow } from "../../services/taskService.js";

export class AonMessengerChat extends AonElement {
  _data;
  UPDATE;
  static get observedAttributes() {
    return [CONSTANT.DATA];
  }

  get data() {
    return JSON.parse(this.getAttribute(CONSTANT.DATA) || "{}");
  }

  set data(data){
    this.setAttribute(CONSTANT.DATA, JSON.stringify(data));
  }
  
  constructor() {
    super();
  }

  connectedCallback() {
    this.initialize();
    this.build();
  }

  disconnectedCallback() {
    this.deleteToolbar();
  }

  initialize() {
    this.id = this.id || MESSENGER_VIEWS.AON_MESSENGER_CHAT;
    this.applicationEl = this.getApplication();
    this.applicationParentEl = this.getApplicationParent();
    this.deleteToolbar();
    this._data =  {
      id: undefined,
      number:undefined,
      title: undefined,
      for: "",
      workflows: []
    }
  }

  deleteToolbar(){
    try {
      this.getApplication().removeFloatOption();
      this.getApplication().removeToolbarOptions();
    } catch (error) {}
  }
  
  build() {
    if(this.data && this.data.id){
      this._data={...this._data,...this.data};
      this.UPDATE = true;
    } 

    this.paintView();
  }

  paintView() {
    /*Base font-size*/
    this.style.fontSize = "12px";
    /**
     * Switching between mobile and desktop
     */
    if (this.isMobile()){ 
      this.paintMobile();
    } else { 
      this.paintDesktop();
    }
  
    this.appendChild(inputId());
    if(this.UPDATE)  this.setValues();
  }

  paintDesktop() {
    this.buildToolbarDesktop();

    const mainView = createMainView();
    mainView.appendTo(this);

    const writter = newComponent({
      classes: [CSS.FLEX_COLUMN, CSS.FLEX_ALIGN_CENTER],
      styles: {
        width: "50%",
        height: '100%',
        minWidth: "400px",
        maxWidth: "600px",
        paddingTop: '5vh',
        paddingRight: '20px',
        paddingLeft: '60px',
        top: 0
      }
    });
    writter.appendTo(mainView.element);

    buildDesktopWritter(writter, this._data, this);
    this.buildCommentDesktop();
   
    setTimeout(() => {
      mainView.element.style.opacity = 1;
      mainView.element.style.marginTop = 0;
    }, 100);
  }

  buildCommentDesktop(){

    const mainView = this.getElement(MESSENGER_IDS.MAIN_DIV);
    const chat = newComponent({
      classes: [CSS.FLEX_ROW],
      styles: {
        width: "50%",
        height: '90%',
        minWidth: "400px",
        maxWidth: "600px",
        paddingTop: '5vh',
        paddingRight: '40px',
        paddingLeft: '40px',
      }
    }).element;
    if(!this.UPDATE) chat.style.display="none";
    
    mainView.appendChild(chat);
    buildDesktopChat(chat);
    /**
   * Setting the chat line once all is rendered
   * DO NOT change this, is compulsory.
   */
    const lined = this.selector(".continueLined");
    if (lined)
      lined.style.setProperty("--height", lined.scrollHeight + "px");
  }

  buildToolbarDesktop(){
    const data = this._data;
    console.log(data);
    const toolbar = new AonToolbar();
    toolbar.id = "id";
		toolbar.type = ToolbarType.SECONDARY;
    toolbar.title = "#" + (data.number  || "0").toString().padStart(5,0);
    this.appendChild(toolbar);

    toolbar.addButton2(ACTIONS.BACK,() => {
      const mainView = this.getElement(MESSENGER_IDS.MAIN_DIV);
      mainView.style.opacity    = "0";
      mainView.style.transition = ".25s";
      setTimeout(() => {
        this.applicationParentEl.showView(MESSENGER_VIEWS.AON_MESSENGER_LIST);
      }, 250);
    });

    const titleSpan = toolbar.querySelector(`.${CSS.AON_SECONDARY_TOOLBAR_TITLE}`);

    setClasses(titleSpan,[CSS.FLEX_ROW,CSS.FLEX_ALIGN_CENTER]);

    const status = createOutlinedMaterialIcon({
      color: CSS.variable(COLORS.ONLINE_GREEN),
      name: "info",
      size: "20px"
    });
    status.element.style.marginLeft = "10px"
    status.appendTo(titleSpan);
  }

  paintMobile() {
    if(this.UPDATE){
      let span = this.applicationEl.addFloatOption({
        name: "Addcomment",
        icon: "add_comment",
        id: "Addcomment",
      }, () => {
        /**
         * Hidding float button
         */
       setStyles(span.querySelector("button") , {
          transition : "0.25s",
          opacity : 0
        });
        /**
         * show writter
         */
        let componentWrite = setStyles(document.getElementById(MESSENGER_COMPONENTS.WRITTER),{display : "flex"});
        setTimeout(() => {
          setStyles(componentWrite,{
            zIndex:  9,
            opacity: 1,
            left: 0,
          });
        }, 100);
      });
    }

    const mainView = createMobileMainView();
    mainView.appendTo(this);
    
    const chat = newComponent({
      classes: [CSS.FLEX_ROW],
      styles: {
        width: "100%",
        height: "100%",
        maxWidth: "600px",
        paddingRight: "20px",
        paddingLeft: "20px",
      }
    });
    chat.appendTo(mainView.element);

    buildMobileChat(chat, this._data, this);

    setTimeout(() => {
      mainView.element.style.opacity = 1;
      mainView.element.style.marginTop = 0;
    }, 100);
  }

  setValues(){
    const {id, workgroup, task_holder} = this._data;
    if(id) setValueName(MESSENGER_IDS.TASK_ID, id);
    if(workgroup && workgroup.id) setValueName(MESSENGER_IDS.WORKGROUP, workgroup.id);
    if(task_holder && task_holder.id) setValueName(MESSENGER_IDS.TASKHOLDER, task_holder.id);

    //FILL CHATS WORKFLOW
    this.getTaskWorkflow(id);
  }

  formSerialize(){
    let aonTextArea = this.getElement(MESSENGER_IDS.COMMENT_TASK);
    const comment = aonTextArea.value;
    if(comment) sendMessage(aonTextArea); //remove comment and processed
    const sender = this.applicationParentEl.SENDER;
    const domain = sender.domain.id;
    const taskId = this.selector("#"+MESSENGER_IDS.TASK_ID).value;
    let json = {
      domain,
      sender,
      id: taskId,
      title:this.selector(`#${MESSENGER_IDS.TITLE_TASK}`).innerText,
      workgroup: {
        id:this.selector("#"+MESSENGER_IDS.WORKGROUP).value
      },
      task_holder:{
        id:this.selector("#"+MESSENGER_IDS.TASKHOLDER).value
      },
      workflow:[]
    };
    
    if(!this.UPDATE) { // CREATE
      json.description = comment;
      json.workflow.push({
        domain,
        comment,
        task_holder:sender,
        type:TASK_WORKFLOW_TYPE.OPEN
      });
    } 

    json.workflow.push({
      domain,
      comment,
      task_holder:sender,
      type:TASK_WORKFLOW_TYPE.COMMENT
    })

    return json;
  }

  getTaskWorkflow(taskId){
    getTaskWorkflow({taskId}).then(workflows=>{
      fillChat(workflows);
    });
  }

  async save(){
    this.applicationEl.startLoading();
    try {
      const form = this.formSerialize();
      const data = await saveTask(form);
      if(!this.UPDATE){
        this.applicationParentEl.showView(MESSENGER_VIEWS.AON_MESSENGER_CHAT, data);
      }
    } catch (error) {
      this.showError(error);
    }
    this.applicationEl.stopLoading();
  }

  selector(selector){
    return document.querySelector(selector) || {};
  }
}

window.customElements.define("aon-messenger-chat", AonMessengerChat);
