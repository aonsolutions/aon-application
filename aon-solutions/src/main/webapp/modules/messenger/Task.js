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

    constructor() {
        this.id          = undefined,
        this.number      = undefined,
        this.title       = undefined,
        this.workgroup   = { id:null };
        this.task_holder = { id:null };
        this.workflow    = [];
    }

    createTask(task) {
        if(task) {
            this.id          = task.id || undefined;
            this.number      = task.number || undefined;
            this.domain      = task.domain || localStorage.getItem('aon_domain_id');
            this.sender      = task.sender || undefined;
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
 
  addWorkflow(workflow) {
    this.workflow.push(workflow);
  }

  deleteWorkflow(id) {
    this.workflow = this.workflow.filter(workflow=> workflow.id!=id);
  }
}

