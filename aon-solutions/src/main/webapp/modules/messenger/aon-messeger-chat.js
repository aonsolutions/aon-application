import { AonElement } from "../../components/AonElement.js";
import { CONSTANT, CSS, MSG } from "../../environments/environments.js";
import { APP_PARAMS_REQUEST, MESSENGER_COMPONENTS, MESSENGER_IDS, MESSENGER_VIEWS, TASK_SOURCE, TASK_STATUS, WORKFLOW_TYPES} from "./MessengerEnums.js";
import { saveTask, getTaskWorkflow, saveTaskWorkflow, saveTaskAttach, deleteTask, sendTaskHistoric, deleteTaskWorkflow, updateTaskWorkflow} from "../../services/taskService.js";
import {getWorkgroups} from '../../services/workgroupService.js';
import { Task } from "../../models/task/Task.js";
import { buildDesktop } from "./shared/MessengerChat.js";
import { buildMobile } from "./shared/MessengerChatMobile.js";
import { checkButtonsToolbar, checkFilesAddEventDescription, sendMessage, setStyleMessageHistoric, setTaskTags } from "./shared/utils.js";
import { getFormVacationJson } from "./forms/vacation.js";
import { TaskFill } from "./shared/TaskFill.js";
import { getFormMovJson } from "./forms/mov-ss.js";
import { getFormTimeJson } from "./forms/time-control.js";
import { getOfficeProjects } from "../../services/projectService.js";
import { Workgroup } from "../../models/project/Workgroup.js";
import { TaskHolder } from "../../models/project/TaskHolder.js";
import { getTastHoldersWorkGroup } from "../../services/taskHolderService.js";
import {getDomainLogin} from '../../services/localStorageService.js';
import { TaskCreationUtils } from "./shared/TaskCreationUtils.js";

export class AonMessengerChat extends AonElement {
  task;
  _data;
  TOOLBAR;
  PROJECTS;
  WORKGROUPS;
  MY_TASKHOLDER;
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
    
    if (this.getApplication()) {
      this.getApplication().removeFloatOption();
    }
  }

  disconnectedCallback() {}

  initialize() {
    this.id = MESSENGER_VIEWS.AON_MESSENGER_CHAT;
    this.TOOLBAR = this.id+"Toolbar";
    this.applicationEl = this.getApplication();
    this.applicationParentEl = this.getApplicationParent();
    this.PROJECTS = [];
    this.WORKGROUPS = [];
    this.setTask();
  }

  setTask(){
    let data = {
      ...this.data, 
      domainCompany:this.getDur().domain,
      auth:this.getAuth()
    };
      
    const th = this.applicationParentEl.TASK_HOLDER;
    if(th && th.id) {
      data.myTaskHolder = th;
      this.MY_TASKHOLDER = th;
    }

    this.setData(data); 
    this.task = new Task(this.getData());
    this.task.onPropertyChanged = (propName, val) => {
        if(propName == "project"){
          this.onChangeProject();
        } else if(propName == "tags"){
          setTaskTags();
        }
    }
    
    this.setWhAndTh();
  }

  build() {
    this.paintView();
    if(this.task && this.task.id){
      //FILL CHATS WORKFLOW
      this.getTaskWorkflow(this.task);
    }  
  }

  paintView() {
    this.style.fontSize = "12px";
    if (this.isMobile()) {
      this.paintMobile();
    } else {
      this.paintDesktop();
    }
  }

  paintDesktop() {
    buildDesktop(this);
  }

  paintMobile() {
    buildMobile(this);
  }

  /**
   * 
   * @param {String} text Optional
   * @param {Task} task Optional
   */
  async saveComment(text = undefined, task=undefined) {
    const taskW = task ? task : this.task;
    try {
          const {comment, messageEl, workflowId, taskId}  = await sendMessage(text, taskW); 
          if(comment){
            if(workflowId && taskId){
              this.updateComment(taskId, workflowId, comment);
            } else {
              this.saveCommentNew(taskW, comment, messageEl);
            }
          }
      } catch (error) {
        console.error("saveComment", error);
        this.showError(error);
      }
  }

   /**
   * 
   * @param {Task} task
   * @param {String} text
   * @param {HtmlElement} messageEl element html message
   */
    async saveCommentNew(task, text, messageEl) {
      try {
        const workflow = await saveTaskWorkflow({
          domain:task.getDomain().id,
          task: task.getId(),
          task_holder:this.MY_TASKHOLDER,
          type: WORKFLOW_TYPES.COMMENT,
          email: this.getAuth().email ? this.getAuth().email : undefined,
          comment:text
        });

        if(workflow){
          messageEl.dataset["id"] = workflow.id;
          if(this.isCau()) {
            this.sendMessageHistoric(task.id, workflow.id);
          }
        }
      } catch (error) {
        console.error("updateComment", error);
        this.showError(error);
      }
    }
  

  /**
   * 
   * @param {Number} taskId
   * @param {Number} workflow workflowId
   * @param {String} text
   */
   async updateComment(taskId, workflow, text) {
    try {
        let messageContent = document.querySelector(`${MESSENGER_COMPONENTS.MESSAGE}[data-id="${workflow}"] > .${CSS.MESSAGE_CONTENT}`);
        if(messageContent){
          await updateTaskWorkflow({
            task: taskId,
            comment:text,
            workflow
          });
          messageContent.innerHTML = text;
        }
    } catch (error) {
      console.error("updateComment", error);
      this.showError(error);
    }
  }

  /**
   * 
   * @param {String} status 
   * @param {String} comment Optional 
   */
  async updateTaskStatus(status, comment, dailyTracking = undefined) {
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
        await this.saveComment();
      break;
    }
    
    try {
      await saveTaskWorkflow({...this.task.getWorkflowTmp(), type, comment, auth:this.getAuth(), dailyTracking});
      await this.save();
      this.applicationParentEl.showView(MESSENGER_VIEWS.AON_MESSENGER_CHAT, this.task);
    } catch (error) {
      this.showError(error);
    }
  }

  async getTaskWorkflow(task) {
    try {
      const taskId = task.id;

      checkButtonsToolbar(this, task.id);

      let params = { task:taskId, domainId:task.domain.id, domainName:task.domain.name };
      
      if(task.parent){
        params.parent = task.parent;
      }

      if(this.isCau() && this.getAuth().email){
        params.email =  this.getAuth().email;
      }

      let workflows = await getTaskWorkflow(params);

      const taskHolderId = this.MY_TASKHOLDER ? this.MY_TASKHOLDER.id : null;

      TaskFill.fillChat(task, taskHolderId, workflows);
      
      this.applicationParentEl.markReadNotification(taskId);
    } catch (error) {
      console.error("getTaskWorkflow", error);
    }
  }

  async save() {
    let success = false;
    this.applicationEl.startLoading();
    this.autoCompleteTask();

    try {
      if(this.task.source === TASK_SOURCE.REQUEST){
        await this.saveSourceRequest();
      } else {
        await this.saveSourceQuery();
      }

      success = true;

      this.getTaskWorkflow(this.task);
    } catch (error) {
      console.log(error);
      this.showError(error);
    }
    
    this.applicationParentEl.updateCount();
    this.applicationEl.stopLoading();  
    
    return success;
  }

  async saveSourceRequest(){
      const json = this.getFormJson();
      if(json){
        this.task.setDescriptionJson(json);
        const data = await saveTask(this.task);
        this.task.editTask(data);
        if(this.getData().id){
          this.setData(data);
        } else {
          this.applicationParentEl.showView(MESSENGER_VIEWS.AON_MESSENGER_CHAT, this.task);
        }
      }
  }

  async saveSourceQuery(){
    if(!this.task.id){
      this.setCauData();
    }
      
    const data = await saveTask(this.task);
    this.task.editTask(data);

    checkFilesAddEventDescription(this.task);//check files description

    if(this.getData().id){
      this.setData(data);
    } else {
      this.applicationParentEl.showView(MESSENGER_VIEWS.AON_MESSENGER_CHAT, this.task);
    }
  }

  setCauData(){
    if(this.getCauInfo()){
      this.task.setDescriptionJson({
        cauInfo:this.getCauInfo(),
        ...this.task.getDescriptionJson()
      });
    }
  }

  closeTask(){
    if(this.isCau()){
    this.getApplication().confirmDialog(MSG.CLOSE, MSG.REQUEST_CLOSE_CONFIRM, ()=>{
      this.updateTaskStatus(TASK_STATUS.FINISHED); 
    });
    } else {
      TaskCreationUtils.openDialogDailyTracking();
    }
  }

  getCauInfo(){
    let cauInfo = this.applicationParentEl.cauInfo;
    let login = getDomainLogin();
    if(login) {
      cauInfo.login = login;
    }
    return cauInfo;
  }

  getAuth(){
    try{
      if(this.getCauInfo().auth && this.getCauInfo().auth.email){
        return this.getCauInfo().auth;
      }
    }catch(e){}

    return {};
  }

  async uploadFile({file, task}) {
    return await saveTaskAttach({file, task}).catch(()=>null);
  }

  deleteTask(){
    this.applicationEl.confirmDialog(MSG.DELETE, MSG.DELETE_CONFIRM, async()=>{
      try {
        await deleteTask(this.task);
        this.showToast({message:MSG.DELETED_DATA});
        this.applicationParentEl.updateCount();
        this.back();
      } catch (error) {
        this.showError(error);
      }
    });
  }

  deleteTaskWorkflow(id){
    this.applicationEl.confirmDialog(MSG.DELETE, MSG.DELETE_CONFIRM, async()=>{
      try {
        await deleteTaskWorkflow({id, domain:this.task.domain});
        this.showToast({message:MSG.DELETED_DATA});
        const element = document.querySelector(`[data-id='${id}']`);
        if(element) element.remove();
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

  /**
   * 
   * @param {Number} taskId 
   * @param {Number} workflowId 
   * @param {Boolean} showSuccess 
   */
  async sendMessageHistoric(taskId, workflowId, showSuccess=false){
    try {
      const workflows  = await sendTaskHistoric({task:taskId, workflowId: workflowId});
      if(showSuccess){
        this.showMessage("Comentario enviado por correo!");
      }

      setStyleMessageHistoric(workflows);
    } catch (error) {
      console.log(error);
    }
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
      const type = processType.value;
      if("1" === type){
        json = getFormVacationJson();
      } else if("2" === type){
        json = getFormMovJson();
      } else if("3" === type){
        json = getFormTimeJson();
      }
  
      if(json){//TAGS
        let descriptionJson = this.task.getDescriptionJson();
        if(descriptionJson.tags) json.tags = descriptionJson.tags;

        this.task.description = "";
      }
    }
    return json;
  }

  autoCompleteTask(){
    this.autoCompleteTaskWorkflow();

    if(!this.task.id){

      if(this.task.isOtherDomain()){ // OTHER DOMAIN
        if(!this.task.getGTaskId()){
          this.task.setGTaskId(this.task.auth.email);
        }
      }  else { 
        //TODO
      }

      if(this.task.source === TASK_SOURCE.CAU && this.isCau()){
        this.task.setSender({});
      }
    }
  }

  autoCompleteTaskWorkflow(){
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

  getTagsPanel() {
    let tags =  this.getApplicationParent()._tags || [];
    const tagsTask = this.task.getDescriptionJson().tags || [];
    return tags.filter(tag => !tagsTask.some(t=> t.id ===tag.id));
  }
  
  async getOfficeProjects(){
    if(!this.PROJECTS.length)
      this.PROJECTS = await getOfficeProjects().catch(()=>[]);
    return this.PROJECTS;
  }

  //---------- TASK FUNCTIONS
   /**
   * CHANGE VALUES WHEN PROJECT CHANGE 
   */
  onChangeProject(){
    if(this.task.isProject()){
      const {projectHolder} = this.task.getProject();
      const workgroup = projectHolder.workgroup && projectHolder.workgroup.id ?  projectHolder.workgroup : this.task.getWorkgroup();
      this.task.setWorkgroup(new Workgroup(workgroup));
      const taskHolder = projectHolder.taskHolder && projectHolder.taskHolder.id ? projectHolder.taskHolder : this.task.getTaskHolder();
      this.task.setTaskHolder(taskHolder);
    } else if(!this.task.getId()) {
      this.task.setWorkgroup(new Workgroup());
      this.task.setTaskHolder(new TaskHolder());
    }

    if(!this.task.getId()){
      this.task.setSender( new TaskHolder(this.task.senderCondition()));
    }

    this.task.setWorkflowTmp({
      comment:"",
      domain:this.task.getDomain().id,
      task_holder: this.task.myTaskHolder,
      task: this.task.getId(),
      type: WORKFLOW_TYPES.COMMENT,
      email: this.task.auth.email ? this.task.auth.email : undefined
    });
  }
  
  onChangeWhAndTh(appParams){
    if(!this.task.getId() && this.task.isOtherDomain() && appParams){
      if(!this.task.getWorkgroup().id){
        const exist = appParams.find(({name})=> name ===APP_PARAMS_REQUEST.APP_REQUESTS_EXT_WORKGROUP);
        if(exist)
          this.task.setWorkgroup({id: parseInt(exist.value)});
      }
      if(!this.task.getTaskHolder().id){
        const exist = appParams.find(({name})=> name ===APP_PARAMS_REQUEST.APP_REQUESTS_EXT_TASK_HOLDER);
        if(exist) 
          this.task.setTaskHolder({id:parseInt(exist.value)});
      }
    }
  }

    
  setWhAndTh(){
    if(!this.task.getId()){
      this.applicationParentEl.getAppParams().then(params=>{

        const externa = this.task.isOtherDomain() || this.isCau() || this.task.getSource() === TASK_SOURCE.CAU;

        const thParam = params[externa ? APP_PARAMS_REQUEST.APP_REQUESTS_EXT_TASK_HOLDER : APP_PARAMS_REQUEST.APP_REQUESTS_INT_TASK_HOLDER];
        const wgParam = params[externa ? APP_PARAMS_REQUEST.APP_REQUESTS_EXT_WORKGROUP : APP_PARAMS_REQUEST.APP_REQUESTS_INT_WORKGROUP];

        if(!this.task.getWorkgroup().id && wgParam){
          this.task.setWorkgroup({id: parseInt(wgParam)});
        }

        if(!this.task.getTaskHolder().id && thParam){
          this.task.setTaskHolder({id:parseInt(thParam)});
        }

      });
    }
  }

  async getWorkgroup(workgroup){
    const workgroups = await this.getWorkGroups();
    let options = [];
    if(workgroups && workgroups.length>0){
      options = workgroups.map( wg=> ({...wg, id: wg.value}) );
    }
    
    if( workgroup && workgroup.id && workgroup.description){
      const exist = options.some(({id})=> id  === workgroup.id );
      if(!exist){
        options.push({...workgroup, value:workgroup.id, name:workgroup.description});  
      }
    }

    return options;
  }
  /**
   * 
   * @param {TaskHolder} taskHolder myTaskHolder
   * @param {Object} workgroud workgroupID 
   * @returns 
   */
  async getTaskHolderByWorkgroup(taskHolder, {workgroup}){
    const taskHolders = await getTastHoldersWorkGroup({workgroup, active:1});

    let options = [];
    if(taskHolders && taskHolders.length>0){
      options = taskHolders.map( th=> ({...th, value: th.id}) )
    } else if(taskHolder.id && taskHolder.name) {
      options = [{...taskHolder, value:taskHolder.id}];
    }

    return options;
  }

  back(){
    let parent = this.applicationParentEl;
    parent.showView(MESSENGER_VIEWS.AON_MESSENGER_LIST, undefined, this.applicationParentEl._listFilter);
    parent.getNotifications();
  }
}

window.customElements.define("aon-messenger-chat", AonMessengerChat);
