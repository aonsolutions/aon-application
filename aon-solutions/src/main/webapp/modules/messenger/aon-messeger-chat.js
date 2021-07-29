import { AonToolbar } from "../../components/aon-toolbar.js";
import { AonElement } from "../../components/AonElement.js";
import { COLORS, CONSTANT, CSS, EVENT, MSG } from "../../environments/environments.js";
import { ToolbarType } from "../../models/enums.js";
import { setAttributes, setClasses, setStyles } from "../../services/utils.js";
import {
  buildDesktopChat,
  buildMobileChat,
  fillChat,
} from "./shared/messenger-chat.js";
import {
  buildDesktopWritter,
  sendMessage,
} from "./shared/messenger-writter.js";
import {
  MessengerOptions,
  MessengerSidenav,
  MESSENGER_COMPONENTS,
  MESSENGER_IDS,
  MESSENGER_VIEWS,
  TASK_STATUS,
  WORKFLOW_TYPES,
} from "./MessengerEnums.js";
import { createOutlinedMaterialIcon } from "./shared/creationUtils.js";
import * as ACTIONS from "../actions.js";
import {
  saveTask,
  getTaskWorkflow,
  saveTaskWorkflow,
  saveTaskAttach,
  deleteTask
} from "../../services/taskService.js";
import { Task } from "./Task.js";
import { createMainView, createMobileMainView } from "./createComponents.js";

export class AonMessengerChat extends AonElement {
  task;
  _data;
  TOOLBAR;
  static get observedAttributes() {
    return [CONSTANT.DATA];
  }

  get data() {
    return JSON.parse(this.getAttribute(CONSTANT.DATA) || "{}");
  }

  set data(data) {
    this.setAttribute(CONSTANT.DATA, JSON.stringify(data));
  }

  setData(data){
    this._data = data;
  }
  
  getData(){
    return this._data || {};
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

  deleteToolbar() {
    try {
      this.getApplication().removeFloatOption();
      this.getApplication().removeToolbarOptions();
    } catch (error) {}
  }

  initialize() {
    this.id = MESSENGER_VIEWS.AON_MESSENGER_CHAT;
    this.TOOLBAR = this.id+"Toolbar";
    this.applicationEl = this.getApplication();
    this.applicationParentEl = this.getApplicationParent();
    this.deleteToolbar();
    this.task = new Task();

    if(this.data.id) this.setData(this.data);

    const sender = this.applicationParentEl.SENDER;
    this.task.setSender(sender);
    this.task.setDomain(sender.domain.id);
    this.task.createTask(this.data);
  }
 
  build() {
    this.paintView();
  }

  paintView() {
    /*Base font-size*/
    this.style.fontSize = "12px";
    
    if (this.isMobile()) 
      this.paintMobile();
    else 
      this.paintDesktop();

    if (this.task.id)   //FILL CHATS WORKFLOW
      this.getTaskWorkflow();

    this.eventListenerAll();
  }

  paintDesktop() {
    this.buildToolbarDesktop();

    const mainView = createMainView(this);

    buildDesktopWritter(mainView, this);

    buildDesktopChat(this);
  }

  paintMobile() {
    if (this.task.id) {
      let span = this.applicationEl.addFloatOption(MessengerSidenav.ADD_COMMENT, () => {
          //Hidding float button
          setStyles(span.querySelector("button"), { transition: "0.25s", opacity: 0});
          // show writter
          let componentWrite = setStyles(document.getElementById(MESSENGER_COMPONENTS.WRITTER), { display: "flex" });
          setTimeout(() => setStyles(componentWrite, {zIndex: 9,opacity: 1,left: 0}), 100);
        }
      );
    }
    
    const mainView = createMobileMainView().element;
    this.appendChild(mainView);

    buildMobileChat(mainView, this);
  }

  buildToolbarDesktop() {
    const toolbar = setAttributes(new AonToolbar(), {
      id:this.TOOLBAR,
      type:ToolbarType.SECONDARY,
      title:"#" + (this.task.number || "0").toString().padStart(5, 0)
    });

    this.appendChild(toolbar);

    if(this.task.id){
      if(this.task.status == TASK_STATUS.PENDING || this.task.status == TASK_STATUS.IN_PROGRESS){
        toolbar.addButton2({
          ...MessengerOptions.AON_MESSENGER_LIST_CLOSE,
          name: 'Cerrar',
        }, () => this.updateTaskStatus(TASK_STATUS.FINISHED));
      }
      if(this.task.status == TASK_STATUS.DELETED || this.task.status == TASK_STATUS.FINISHED)
        toolbar.addButton2({...ACTIONS.RESTORE, name:"Reabrir"}, () => this.updateTaskStatus(TASK_STATUS.PENDING));

      if(this.task.status != TASK_STATUS.DELETED) 
        toolbar.addButton2({...MessengerOptions.AON_MESSENGER_LIST_ARCHIVE, name:MSG.STORE}, () => this.updateTaskStatus(TASK_STATUS.DELETED));
    }

    if(this.task.status == TASK_STATUS.PENDING || this.task.status == TASK_STATUS.IN_PROGRESS)
      toolbar.addButton2(ACTIONS.SAVE, () => this.save());

    toolbar.addButton2(ACTIONS.BACK, () => this.back());

    const titleSpan = toolbar.querySelector( `.${CSS.AON_SECONDARY_TOOLBAR_TITLE}` );

    setClasses(titleSpan, [CSS.FLEX_ROW, CSS.FLEX_ALIGN_CENTER]);

    const status = createOutlinedMaterialIcon({
      color: CSS.variable(this.task.status == TASK_STATUS.PENDING || this.task.status == TASK_STATUS.IN_PROGRESS ? COLORS.ONLINE_GREEN : COLORS.GRAYSON),
      name: "info",
      size: "20px",
    });
    status.element.style.marginLeft = "10px";
    status.appendTo(titleSpan);
  }

  buildData(){
    if (this.task.id) { //UPDATE
      this.task.setWorkflow([]);
      if( this.getData().workgroup && this.getData().workgroup.id!= this.task.getWorkgroup().id){
        this.task.addWorkflow({
            comment: this.task.getWorkgroup().description,
            domain:this.task.getDomain(),
            modification_date:new Date().getTime(),
            task_holder: this.task.getSender(),
            type: WORKFLOW_TYPES.ASSIGN,
        });
      }
      if( this.getData().task_holder && this.getData().task_holder.id!= this.task.getTaskHolder().id){
        this.task.addWorkflow({
          comment: this.task.getTaskHolder().description,
          domain:this.task.getDomain(),
          modification_date:new Date().getTime(),
          task_holder: this.task.getSender(),
          type: WORKFLOW_TYPES.ASSIGN,
        });
      }
    } else {
      this.task.addWorkflow({...this.task.getWorkflowTmp(), type: WORKFLOW_TYPES.OPEN}); // ADD WORKFLOW OPEN TASK
      this.task.addWorkflow({...this.task.getWorkflowTmp(), comment:this.task.getDescription()}); // ADD COMMENT
    }
  }
  
  eventListenerAll(){
    let titleEl = this.getElement(MESSENGER_IDS.TITLE_TASK)
    if(titleEl) titleEl.addEventListener(EVENT.KEYUP, ()=> this.task.setTitle(titleEl.innerText) )
    
    let workgroupEl = this.getElement(MESSENGER_IDS.WORKGROUP);
    if(workgroupEl) workgroupEl.addEventListener(EVENT.CHANGE, ({detail})=> {
      if(detail) this.task.setWorkgroup(detail);
    });

    let taskHolderEl = this.getElement(MESSENGER_IDS.TASKHOLDER);
    if(taskHolderEl) taskHolderEl.addEventListener(EVENT.CHANGE, ({detail})=>{
      if(detail) this.task.setTaskHolder(detail)
    });

    let commentTaskEl = this.getElement(MESSENGER_IDS.COMMENT_TASK);
    if(commentTaskEl) commentTaskEl.addEventListener(EVENT.KEYUP, ()=>{
      if(commentTaskEl.value) this.task.setDescription(commentTaskEl.value)
    });
  }

  async saveTaskWorkflow() {
    let aonTextArea = this.getElement(MESSENGER_IDS.COMMENT_TASK);
    const comment = await sendMessage(aonTextArea); 
    try {
      if(comment){
        await saveTaskWorkflow({
          ...this.task.getWorkflowTmp(),
          comment
        });
      }
    } catch (error) {
      this.showError(error);
    }
  }

  async updateTaskStatus(status){
    // this.applicationEl.confirmDialog(MSG.CONFIRM, "Estas seguro?", async()=>{
      this.task.setStatus(status);
      let type = undefined;
      switch(status){
        case TASK_STATUS.PENDING:
          type = WORKFLOW_TYPES.REOPEN;
        break;
        case TASK_STATUS.DELETED:
          type = WORKFLOW_TYPES.DELETE;
        break;
        case TASK_STATUS.FINISHED:
          type = WORKFLOW_TYPES.CLOSE;
        break;
      }
      await saveTaskWorkflow({...this.task.getWorkflowTmp(), type});
      await this.save();
      this.applicationParentEl.showView(MESSENGER_VIEWS.AON_MESSENGER_CHAT, this.task);
    // });
  }

  getTaskWorkflow() {
    getTaskWorkflow({ taskId:this.task.id }).then((workflow) => {
      this.task.setWorkflow(workflow);
      fillChat(workflow);
      if(workflow.length>0) this.addButtonDelete();
    });
  }

  addButtonDelete(){
    if(this.task.status == TASK_STATUS.DELETED) 
      this.getElement(this.TOOLBAR).addButtonAfter(ACTIONS.DELETE, () => this.deleteTask())
  }

  async save() {
    this.buildData();
    this.applicationEl.startLoading();
    try {
      if (this.task.getTitle()) {
        const data = await saveTask(this.task);
        this.task.editTask(data);
        if(this.getData().id){
          this.setData(data);
          if(this.task.getWorkflow().length) fillChat(this.task.getWorkflow());
        } else {
          this.applicationParentEl.showView(MESSENGER_VIEWS.AON_MESSENGER_CHAT, this.task);
        }
      }
    } catch (error) {
      this.showError(error);
    }
    this.applicationEl.stopLoading();
  }

  async uploadFile({file, taskId}) {
    return await saveTaskAttach({file, taskId}).catch(e=>null);
  }

  deleteTask(){
    this.applicationEl.confirmDialog(MSG.DELETE, MSG.DELETE_CONFIRM, async()=>{
      try {
          await deleteTask({taskId:this.task.id});
          this.showToast({message:MSG.DELETED_DATA, type:CONSTANT.ERROR});
          this.back();
      } catch (error) {
        this.showError(error);
      }
    });
  }

  back(){
    setStyles(this.getElement(MESSENGER_IDS.MAIN_DIV),{
      opacity:"0",
      transition:".25s"
    });

    setTimeout(() => this.applicationParentEl.showView(MESSENGER_VIEWS.AON_MESSENGER_LIST, undefined, this.applicationParentEl._filter), 250);
  }
}

window.customElements.define("aon-messenger-chat", AonMessengerChat);
