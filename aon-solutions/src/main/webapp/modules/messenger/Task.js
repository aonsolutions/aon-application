import { TASK_SOURCE, TASK_STATUS, WORKFLOW_TYPES } from "./MessengerEnums";

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
  workflowTmp;
  description;
  status;
  source;
  source_id;
  gtask_id;
  constructor() {
      this.id          = undefined;
      this.status      = undefined;
      this.number      = undefined;
      this.title       = undefined;
      this.gtask_id    = undefined;
      this.description = undefined;
      this.source      = TASK_SOURCE.CAU;
      this.source_id   = undefined;
      this.workgroup   = {};
      this.registry    = {};
      this.task_holder = {};
      this.workflow    = [];
      this.workflowTmp = {};
  }

  createTask(task) {
    if(task) {
      this.id          = task.id || undefined;
      this.status      = task.status || TASK_STATUS.PENDING;
      this.number      = task.number || undefined;
      this.workgroup   = task.workgroup || {};
      this.registry    = task.registry || {};
      this.task_holder = task.task_holder || {};
      this.title       = task.title || "";
      this.description = task.description || "";
      this.gtask_id    = task.gtask_id || "";
      this.source      = task.source || TASK_SOURCE.CAU;
      this.source_id   = task.source_id || undefined;
      this.workflow    = task.workflow || [];
      this.workflowTmp = {
        domain:this.domain,
        comment:"",
        task_holder: this.sender,
        type: WORKFLOW_TYPES.COMMENT,
        task: this.id
      }
    }   
  }

  editTask(task){
    if(task) {
      if(task.id)                                 this.id          = task.id;
      if(task.number)                             this.number      = task.number;
      if(task.gtask_id)                           this.gtask_id    = task.gtask_id;
      if(task.workgroup && task.workgroup.id)     this.workgroup   = task.workgroup;
      if(task.task_holder && task.task_holder.id) this.task_holder = task.task_holder;
      if(task.status)                             this.status = task.status;
      if(task.source_id)                          this.source_id = task.source_id;
      if(task.registry && task.registry.id)       this.registry      = task.registry;
      // if(task.title)                              this.title       = task.title;
      // if(task.domain)                             this.domain      = task.domain;
      // if(task.workflow)                           this.workflow    = task.workflow;
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

  getSender() {
    return this.sender;
  }

  setSender(sender) {
    this.sender = sender;
  }

  getSource() {
    return this.source;
  }

  setSource(source) {
    this.source = source;
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
}

