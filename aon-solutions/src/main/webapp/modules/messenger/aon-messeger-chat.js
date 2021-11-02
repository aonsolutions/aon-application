import { AonElement } from "../../components/AonElement.js";
import { CONSTANT, MSG } from "../../environments/environments.js";
import { MESSENGER_IDS, MESSENGER_VIEWS, TASK_SOURCE, TASK_STATUS, WORKFLOW_TYPES} from "./MessengerEnums.js";
import { saveTask, getTaskWorkflow, saveTaskWorkflow, saveTaskAttach, deleteTask} from "../../services/taskService.js";
import {getWorkgroups} from '../../services/workgroupService.js';
import { Task } from "../../models/task/Task.js";
import { buildDesktop } from "./shared/MessengerChat.js";
import { buildMobile } from "./shared/MessengerChatMobile.js";
import { checkFilesAddEventDescription, sendMessage } from "./shared/utils.js";
import * as ACTIONS from "../actions.js";
import { getFormVacationJson } from "./forms/vacation.js";
import { fillChat } from "./shared/fill.js";
import { getFormMovJson } from "./forms/mov-ss.js";
import { getFormTimeJson } from "./forms/time-control.js";

export class AonMessengerChat extends AonElement {
  task;
  _data;
  TOOLBAR;
  WORKGROUPS;
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
    this.WORKGROUPS = [];
    this.deleteToolbar();
    this.setTask();
  }

  setTask(){
    let data = {...this.data};

    const myTaskHolder = this.applicationParentEl.TASK_HOLDER;
    if(myTaskHolder && myTaskHolder.id) 
      data.myTaskHolder = myTaskHolder;
      
    if(this.applicationParentEl.cauData.auth && this.applicationParentEl.cauData.auth.email)  
      data.auth = this.applicationParentEl.cauData.auth;

    this.setData(data); 
    this.task = new Task(this.getData());
  }

  build() {
    this.paintView();
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
      if( this.getData().workgroup && this.task.getWorkgroup().id && this.getData().workgroup.id!= this.task.getWorkgroup().id){
        this.task.addWorkflow({
          comment: this.task.getWorkgroup().description,
          domain:this.task.getWorkflowTmp().domain,
          creation_date:new Date().getTime(),
          task_holder: this.task.myTaskHolder,
          type: WORKFLOW_TYPES.ASSIGN,
          email:this.task.auth.email
        });
      }
      if( this.getData().task_holder && this.task.getTaskHolder().id && this.getData().task_holder.id!= this.task.getTaskHolder().id){
        this.task.addWorkflow({
          comment: this.task.getTaskHolder().name,
          domain:this.task.getWorkflowTmp().domain,
          creation_date:new Date().getTime(),
          task_holder: this.task.myTaskHolder,
          type: WORKFLOW_TYPES.ASSIGN,
          email:this.task.auth.email
        });
      }
    } else {
      this.task.addWorkflow({...this.task.getWorkflowTmp(), type: WORKFLOW_TYPES.OPEN}); // ADD WORKFLOW OPEN TASK
    }
  }

  /**
   * 
   * @param {String} text Optional
   */
  async saveComment(text) {
    const [comment, messengeEl] = await sendMessage(text, this); 
    try {
      if(comment){
        const workflow = await saveTaskWorkflow({...this.task.getWorkflowTmp(), comment});
        if(workflow)
          messengeEl.dataset["id"] = workflow.id;
      }
    } catch (error) {
      console.log(error);
      this.showError(error);
    }
  }

  /**
   * 
   * @param {String} status 
   * @param {String} comment Optional 
   */
  async updateTaskStatus(status, comment){
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
      await saveTaskWorkflow({...this.task.getWorkflowTmp(), type, comment});
      await this.save();
      this.applicationParentEl.showView(MESSENGER_VIEWS.AON_MESSENGER_CHAT, this.task);
    // });
  }

  async getTaskWorkflow() {
    this.applicationEl.startLoading();
    try {
      let workflows = await getTaskWorkflow({ task:this.task.id, domainId:this.task.domain.id, domainName:this.task.domain.name });
      this.task.setWorkflow(workflows);
      fillChat(this, workflows);
      if(workflows.length>0) this.addButtonDelete();
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

    // let btnInternal = this.getElement(MESSENGER_IDS.EXTERNAL_TASK);
    // if(btnInternal && btnInternal.isChecked() && !this.task.project.id){
    //   this.showError({message:"Proyecto requerido", type:CONSTANT.ERROR});
    // } else {
      if(this.task.source === TASK_SOURCE.REQUEST)
        await this.saveSourceRequest();
      else 
        await this.saveSourceQuery();
    // }
    this.applicationParentEl.updateCount();
    this.applicationEl.stopLoading();    
  }

  async saveSourceRequest(){
    try {
        const json = this.getFormJson();
        if(json){
          this.task.setDescriptionJson(json);
          const data = await saveTask(this.task);
          this.task.editTask(data);
          if(this.getData().id){
            this.setData(data);
            if(this.task.getWorkflow().length) 
              fillChat(this, this.task.getWorkflow());
          } else {
            this.applicationParentEl.showView(MESSENGER_VIEWS.AON_MESSENGER_CHAT, this.task);
          }
        }
    } catch (error) {
      console.log(error);
      this.showError(error);
    }
  }

  async saveSourceQuery(){
    try {
      const data = await saveTask(this.task);
      this.task.editTask(data);
      checkFilesAddEventDescription(this.task);//check files description

      if(this.getData().id){
        this.setData(data);
        if(this.task.getWorkflow().length) 
          fillChat(this, this.task.getWorkflow());
      } else {
        this.applicationParentEl.showView(MESSENGER_VIEWS.AON_MESSENGER_CHAT, this.task);
      }
    } catch (error) {
      console.log(error);
      this.showError(error);
    }
  }

  async uploadFile({file, task}) {
    let result = await saveTaskAttach({file, task}).catch(e=>null);
    return result;
  }

  deleteTask(){
    this.applicationEl.confirmDialog(MSG.DELETE, MSG.DELETE_CONFIRM, async()=>{
      try {
        await deleteTask({task:this.task.id});
        this.showToast({message:MSG.DELETED_DATA});
        this.applicationParentEl.updateCount();
        this.back();
      } catch (error) {
        this.showError(error);
      }
    });
  }

  async getWorkGroups(){
    if(!this.WORKGROUPS.length){
      await getWorkgroups({status:"ACTIVE"}).then(wgs=>{
        this.WORKGROUPS =  wgs.map(t => ({...t, value: t.id, description: t.description, name:t.description}));
      })
    }
    return this.WORKGROUPS;
  }
  
  getDur(){
		return this.applicationParentEl.getDur();
	}

  isCau(){     //IS CAU
    return parseInt(localStorage.getItem("taskCau") || 0);
  }

  /**
   * 
   * @returns json of description task
   */
  getFormJson(){
    let json = null;
    const processType = this.getElement(MESSENGER_IDS.PROCESS_TYPE);
    if(processType){
      this.task.title = processType.getText();
    
      if("1" === processType.value )
        json = getFormVacationJson();
      else if("2" === processType.value )
        json = getFormMovJson();
      else if("3" === processType.value )
        json = getFormTimeJson();
  
      if(json){
        //TAGS
        let descriptionJson = this.task.getDescriptionJson();
        if(descriptionJson.tags) json.tags = descriptionJson.tags;

        this.task.description = "";
      }
    }
    return json;
  }
  
  back(){
   this.applicationParentEl.showView(MESSENGER_VIEWS.AON_MESSENGER_LIST, undefined, this.applicationParentEl._filter);
  }
}

window.customElements.define("aon-messenger-chat", AonMessengerChat);
