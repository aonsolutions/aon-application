import { WORKFLOW_TYPES } from "./MessengerEnums";

export class Task {
    id;
    number;
    domain;
    sender;
    workgroup;
    task_holder;
    title;
    workflow;
    workflowTmp;
    description;
    constructor() {
        this.id          = undefined;
        this.number      = undefined;
        this.title       = undefined;
        this.description = undefined;
        this.workgroup   = { };
        this.task_holder = { };
        this.workflow    = [];
        this.workflowTmp = {};
    }

  createTask(task) {
    if(task) {
        this.id          = task.id || undefined;
        this.number      = task.number || undefined;
        this.workgroup   = task.workgroup || {};
        this.task_holder = task.task_holder || {};
        this.title       = task.title || "";
        this.workflow    = task.workflow || [];
        this.workflowTmp = {
            domain:this.domain,
            comment:"",
            task_holder: this.sender,
            type: WORKFLOW_TYPES.COMMENT,
        }
    }   
  }

  editTask(task){
    if(task) {
      if(task.id)                                 this.id          = task.id;
      if(task.number)                             this.number      = task.number;
      if(task.workgroup && task.workgroup.id)     this.workgroup   = task.workgroup;
      if(task.task_holder && task.task_holder.id) this.task_holder = task.task_holder;
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
 
  deleteWorkflow(id) {
    this.workflow = this.workflow.filter(workflow=> workflow.id!=id);
  }
  
}

