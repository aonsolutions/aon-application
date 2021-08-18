import { AonElement } from "../../components/AonElement.js";
import { CONSTANT, EVENT, MSG } from "../../environments/environments.js";
import { setStyles } from "../../services/utils.js";
import { MESSENGER_IDS, MESSENGER_VIEWS, TASK_SOURCE, TASK_STATUS, WORKFLOW_TYPES} from "./MessengerEnums.js";
import {
  saveTask,
  getTaskWorkflow,
  saveTaskWorkflow,
  saveTaskAttach,
  deleteTask
} from "../../services/taskService.js";
import { Task } from "./Task.js";
import { buildDesktop } from "./shared/MessengerChat.js";
import { fillChat, sendMessage } from "./shared/utils.js";
import { buildMobile } from "./shared/MessengerChatMobile.js";
import * as ACTIONS from "../actions.js";
import { getFormVacationJson } from "./forms/vacation.js";

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

    this.eventListenerAll();

    //FILL CHATS WORKFLOW
    if (this.task.id) this.getTaskWorkflow();
  }

  paintView() {
    this.style.fontSize = "12px";
    if (this.isMobile()) 
      this.paintMobile();
    else 
      this.paintDesktop();
  }

  paintDesktop() {
    buildDesktop(this);
  }

  paintMobile() {
    buildMobile(this);
  }

  buildTaskWorkflow(){
    this.task.setWorkflow([]);
    if (this.task.id) { //UPDATE
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

    let descriptionTask = this.getElement(MESSENGER_IDS.DESCRIPTION_TASK);
    if(descriptionTask) descriptionTask.addEventListener(EVENT.INPUT, ()=>{
      if(descriptionTask.value) this.task.setDescription(descriptionTask.value)
    });

    let processTypeEl = this.getElement(MESSENGER_IDS.PROCESS_TYPE);
    if(processTypeEl) processTypeEl.addEventListener(EVENT.CHANGE, ({detail})=>{
      if(detail && detail.value) this.task.setSourceId(detail.value)
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
      console.log(error);
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
      this.save();
      this.applicationParentEl.showView(MESSENGER_VIEWS.AON_MESSENGER_CHAT, this.task);
    // });
  }

  async getTaskWorkflow() {
    this.applicationEl.startLoading();
    try {
      let workflow = await getTaskWorkflow({ taskId:this.task.id });
      this.task.setWorkflow(workflow);
      fillChat(workflow);
      if(workflow.length>0) this.addButtonDelete();
    } catch (error) {}
    this.applicationEl.stopLoading();
  }

  addButtonDelete(){
    if(this.task.status == TASK_STATUS.DELETED) 
      this.getElement(this.TOOLBAR).addButtonAfter(ACTIONS.DELETE, () => this.deleteTask())
  }

  async save() {
    this.applicationEl.startLoading();
    
    this.buildTaskWorkflow();
    if(this.task.source === TASK_SOURCE.GITHUB)
      await this.saveSourceProcess();
    else 
      await this.saveSourceManual();

    this.applicationEl.stopLoading();    
  }

  async saveSourceManual(){
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
  }


  async saveSourceProcess(){
    try {
        this.task.title = document.getElementById(MESSENGER_IDS.PROCESS_TYPE).getText();
        this.task.description = getFormVacationJson();
        const data = await saveTask(this.task);
        this.task.editTask(data);
        if(this.getData().id){
          this.setData(data);
          if(this.task.getWorkflow().length) fillChat(this.task.getWorkflow());
        } else {
          this.applicationParentEl.showView(MESSENGER_VIEWS.AON_MESSENGER_CHAT, this.task);
        }
    } catch (error) {
      this.showError(error);
    }
  }

  async uploadFile({file, taskId}) {
    let result = await saveTaskAttach({file, taskId}).catch(e=>null);
    return result;
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
    setStyles(this.getElement(MESSENGER_IDS.MAIN_DIV),{ opacity:"0", transition:".25s" });

    setTimeout(() => this.applicationParentEl.showView(MESSENGER_VIEWS.AON_MESSENGER_LIST, undefined, this.applicationParentEl._filter), 250);
  }
}

window.customElements.define("aon-messenger-chat", AonMessengerChat);
