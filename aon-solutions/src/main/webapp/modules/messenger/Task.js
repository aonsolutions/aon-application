import { TASK_SOURCE, TASK_STATUS, WORKFLOW_TYPES } from "./MessengerEnums";
import * as LS from "../../services/localStorageService.js";
export class Task {
  id;
  number;
  domain;
  sender;
  workgroup;
  task_holder;
  title;
  registry;
  workflow;
  description;
  status;
  source;
  source_id;
  gtask_id;
  files;
  start_date;
  parent;
  project;

  domainTmp;
  workflowTmp;
  senderTmp;
  auth;

  constructor() {
      this.id          = undefined;
      this.status      = undefined;
      this.number      = undefined;
      this.title       = undefined;
      this.gtask_id    = undefined;
      this.description = undefined;
      this.source      = TASK_SOURCE.QUERY;
      this.source_id   = undefined;
      this.start_date  = undefined;
      this.parent      = undefined;
      this.auth        = undefined;
      this.sender      = {};
      this.workgroup   = {};
      this.registry    = {};
      this.project     = {};
      this.task_holder = {};
      this.workflow    = [];
      this.workflowTmp = {};
      this.files = [];
      // this._onPropertyChanged = (propName, val) => {};
  }

  // task._onPropertyChanged = ("source", val) => {
  //   console.log(s);
  // }

  createTask(task) {
    if(task) {
      this.id          = task.id || undefined;
      this.status      = task.status || TASK_STATUS.PENDING;
      this.number      = task.number || undefined;
      this.workgroup   = task.workgroup || {};
      this.registry    = task.registry || {};
      this.sender      = task.sender || {};
      this.senderTmp   = this.sender || {};
      this.task_holder = task.task_holder || {};
      this.title       = task.title || "";
      this.description = task.description || "";
      this.auth        = task.auth || {};
      this.source      = task.source || TASK_SOURCE.QUERY;
      this.source_id   = task.source_id || undefined;
      this.start_date  = task.start_date || undefined;
      this.parent      = task.parent || undefined;
      this.gtask_id    = task.gtask_id || (!this.id && this.auth.email ? this.auth.email : undefined);
      this.domain      = task.domain || {id:parseInt(LS.getDomainId()), name: LS.getDomainName()}; 
      this.domainTmp   = this.domain;
      this.workflow    = task.workflow || [];
      this.workflowTmp = {
        comment:"",
        domain:this.domain.id,
        task_holder: this.sender,
        task: this.id,
        type: WORKFLOW_TYPES.COMMENT,
        email: this.auth.email ? this.auth.email : undefined
      }
      this.project     = task.project ? this.setProject(task.project) : {};
    }   
  }

  editTask(task){
    if(task) {
      if(task.id)                                 this.setId(task.id);
      if(task.number)                             this.setNumber(task.number);
      if(task.gtask_id)                           this.setGTaskId(gtask_id);
      if(task.workgroup && task.workgroup.id)     this.setWorkgroup(task.workgroup);
      if(task.task_holder && task.task_holder.id) this.setTaskHolder(task.task_holder);
      if(task.status)                             this.setStatus(task.status);
      if(task.source_id)                          this.setSourceId(task.source_id);
      if(task.registry && task.registry.id)       this.setRegistry(task.registry);
      if(task.project && task.project.id)         this.setProject(task.project);
      if(task.description)                        this.setDescription(task.description);
      if(task.start_date)                         this.setStartDate(task.start_date);
      if(task.parent)                             this.setParent(task.parent);
      this.setFiles([]);
    }
  }

  cleanTask(){
    if(!this.id){
      this.workgroup   = {};
      this.registry    = {};
      this.task_holder = {};
      this.title       = "";
      this.description = "";
      this.source_id   = undefined;
      this.workflow    = [];
      this.setProject({});
      this.setFiles([]);
    }
  }

  getId() {
    return this.id;
  }

  setId(id) {
    this.id = id;
  }

  getStatus() {
    return this.status;
  }

  setStatus(status) {
    this.status = status;
  }

  getNumber() {
    return this.number;
  }

  setNumber(number) {
    this.number = number;
  }

  getDomain() {
    return this.domain;
  }

  setDomain(domain) {
    this.domain = domain;
  }

  getDomainTmp() {
    return this.domainTmp;
  }

  setDomainTmp(domainTmp) {
    this.domainTmp = domainTmp;
  }

  getSender() {
    return this.sender;
  }

  setSender(sender) {
    this.sender = sender;
  }

  getProject() {
    return this.project;
  }

  setProject(project) {
    this.project = project;
    this.changeProject();
  }

  getSource() {
    return this.source;
  }

  setSource(v) {
    this.source = v;
    // this._onPropertyChanged('source', v);
  }

  getSourceId() {
    return this.source_id;
  }

  setSourceId(source_id) {
    this.source_id = source_id;
  }

  getWorkgroup() {
    return this.workgroup;
  }

  setWorkgroup(workgroup) {
    this.workgroup = workgroup;
  }

  getTaskHolder() {
    return this.task_holder;
  }

  setTaskHolder(task_holder) {
    this.task_holder = task_holder;
  }

  getTitle() {
    return this.title;
  }

  setTitle(title) {
    this.title = title;
  }

  getGTaskId() {
    return this.gtask_id;
  }

  setGTaskId(gtask_id) {
    this.gtask_id = gtask_id;
  }

  getRegistry() {
    return this.registry;
  }

  setRegistry(registry) {
    this.registry = registry;
  }

  getWorkflow(){
    return this.workflow;
  }

  setWorkflow(workflow){
    this.workflow = workflow;
  }

  getWorkflowTmp(){
    return this.workflowTmp;
  }

  setWorkflowTmp(workflowTmp){
    this.workflowTmp = workflowTmp;
  }
 
  addWorkflow(workflow) {
    this.workflow.push(workflow);
  }

  setDescription(description) {
    this.description = description;
  }

  getDescription(){
    return this.description;
  }
  
  setDescriptionJson(json) {
    this.description = JSON.stringify({...this.getDescriptionJson(), ...json}); 
  }

  getDescriptionJson() {
    let json = {};
    try { json = JSON.parse(this.description);  } catch (e) {json.observation = this.description;}
    return json;
  }
 
  deleteWorkflow(id) {
    this.workflow = this.workflow.filter(workflow=> workflow.id!=id);
  }

  getFiles() {
    return this.files;
  }

  setFiles(files) {
    this.files = files;
  }

  getAuth() {
    return this.auth;
  }

  setAuth(auth) {
    this.auth = auth;
  }
  
  getStartDate() {
    return this.start_date;
  }

  setStartDate(start_date) {
    this.start_date = start_date;
  }

  getParent() {
    return this.parent;
  }

  setParent(parent) {
    this.parent = parent;
  }

  isExternal(){
    return this.project && this.project.id ? true : false;
  }

  /**
   * CHANGE VALUES WHEN PROJECT CHANGE 
   */
  changeProject(){
    const domain = this.project.domain && this.project.domain.id ? this.project.domain : this.domainTmp;
    this.setDomain(domain);

    const {project_holder} = this.project;

    if(project_holder && project_holder.id){
      const workgroup = project_holder.workgroup && project_holder.workgroup.id ?  project_holder.workgroup : {};
      this.setWorkgroup(workgroup);
      const task_holder = project_holder.task_holder && project_holder.task_holder.id ? project_holder.task_holder : {};
      this.setTaskHolder(task_holder);
    }

    const registry = this.project.registry && this.project.registry.id ? this.project.registry : {};
    this.setRegistry(registry);

    let sender = {};
    const isGestor = "OFFICE" === LS.getCompany().type;
    if(isGestor){
      if(TASK_SOURCE.QUERY === this.source)
        sender = this.senderTmp;
      // if(TASK_SOURCE.REQUEST === this.source)
    } else if(!this.project.id){
      sender = this.senderTmp;
    }
    this.setSender(sender);

    this.workflowTmp = {
      comment:"",
      domain:this.domain.id,
      task_holder: this.sender,
      task: this.id,
      type: WORKFLOW_TYPES.COMMENT,
      email: this.auth.email ? this.auth.email : undefined
    }
  }
}

