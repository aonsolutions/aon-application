import { TASK_SOURCE, TASK_STATUS, WORKFLOW_TYPES } from "../../modules/messenger/MessengerEnums.js";
import * as LS from '../../services/localStorageService.js';
import { Domain } from "../Domain.js";
import { Project } from "../project/Project.js";
import { TaskHolder } from "../project/TaskHolder.js";
import { Workgroup } from "../project/Workgroup.js";
import { Registry } from "../registry/Registry.js";

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
  //TMP
  domainTmp;
  workflowTmp;
  myTaskHolder;
  auth;

  constructor(task) {
    // this._onPropertyChanged = (propName, val) => {};
    if(task)
      this.setTask(task);
    else {
      this.id          = undefined;
      this.number      = undefined;
      this.title       = undefined;
      this.gtask_id    = undefined;
      this.description = undefined;
      this.source_id   = undefined;
      this.start_date  = undefined;
      this.parent      = undefined;
      this.auth        = undefined;
      this.domain      = new Domain();
      this.workgroup   = new Workgroup();
      this.project     = new Project();
      this.task_holder = new TaskHolder();
      this.sender      = new TaskHolder();
      this.registry    = new Registry();
      this.source      = TASK_SOURCE.QUERY;
      this.status      = TASK_STATUS.PENDING;
      this.workflow    = [];
      this.workflowTmp = {};
      this.files = [];
    }
  }

  // task._onPropertyChanged = ("source", val) => {
  //   console.log(s);
  // }

  setTask(task) {
    if(task) {
      this.id          = task.id || undefined;
      this.auth        = task.auth || {};
      this.status      = task.status || TASK_STATUS.PENDING;
      this.number      = task.number || undefined;
      this.workgroup   = new Workgroup(task.workgroup);
      this.registry    = new Registry(task.registry);
      this.myTaskHolder= new TaskHolder(task.myTaskHolder);
      this.task_holder = new TaskHolder(task.task_holder);
      this.title       = task.title || "";
      this.description = task.description || "";
      this.source      = task.source || TASK_SOURCE.QUERY;
      this.source_id   = task.source_id || undefined;
      this.start_date  = task.start_date || undefined;
      this.parent      = task.parent || undefined;
      this.gtask_id    = task.gtask_id || (!this.id && this.auth.email && !this.isGestor() ? this.auth.email : undefined);
      this.domain      = new Domain(task.domain); 
      this.workflow    = task.workflow || [];
      this.domainTmp   = this.domain;
      this.workflowTmp = {
        comment:"",
        domain:this.domain.id,
        task_holder: this.myTaskHolder,
        task: this.id,
        type: WORKFLOW_TYPES.COMMENT,
        email: this.auth.email ? this.auth.email : undefined
      }
      this.setProject(new Project(task.project));
      this.sender = new TaskHolder( task.id ? task.sender : this.senderCondition() );
    }   
  }

  editTask(task){
    if(task) {
      if(task.id)                                 this.setId(task.id);
      if(task.number)                             this.setNumber(task.number);
      if(task.gtask_id)                           this.setGTaskId(task.gtask_id);
      if(task.workgroup && task.workgroup.id)     this.setWorkgroup(new Workgroup(task.workgroup));
      if(task.task_holder && task.task_holder.id) this.setTaskHolder(task.task_holder);
      if(task.status)                             this.setStatus(task.status);
      if(task.source_id)                          this.setSourceId(task.source_id);
      if(task.registry && task.registry.id)       this.setRegistry(new Registry(task.registry));
      if(task.project && task.project.id)         this.setProject(new Project(task.project));
      if(task.description)                        this.setDescription(task.description);
      if(task.start_date)                         this.setStartDate(task.start_date);
      if(task.parent)                             this.setParent(task.parent);
      this.setFiles([]);
    }
  }

  /**
   * 
   * @param {Boolean} projectDefault default project, false clean, true not clean
   */
  cleanTask(projectDefault=false){
    if(!this.id){
      this.workgroup   = new Workgroup();
      if(!projectDefault)this.setProject(new Project());
      this.registry    = new Registry();
      this.task_holder = new TaskHolder();
      this.title       = "";
      this.description = "";
      this.source_id   = undefined;
      this.workflow    = [];
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

  isProject(){
    return this.project && this.project.id ? true : false;
  }

  isExternal(){
    return this.id && parseInt(LS.getDomainId()) !== parseInt(this.domain.id);
  }

  isGestor(){
    return "OFFICE" === LS.getCompany().type;
  }

  senderCondition(){
    return !this.isGestor() && this.isProject() ? undefined : this.myTaskHolder;
  }

  /**
   * CHANGE VALUES WHEN PROJECT CHANGE 
   */
  changeProject(){
    if(this.isProject() && !this.isExternal())
      this.setDomain(new Domain(this.project.domain));

    if(this.isProject()){
      const {projectHolder} = this.project;
      const workgroup = projectHolder.workgroup && projectHolder.workgroup.id ?  projectHolder.workgroup : this.workgroup;
      this.setWorkgroup(new Workgroup(workgroup));
      const taskHolder = projectHolder.taskHolder && projectHolder.taskHolder.id ? projectHolder.taskHolder : this.task_holder;
      this.setTaskHolder(taskHolder);
    }

    const registry = this.project.registry && this.project.registry.id ? this.project.registry : this.registry;
    this.setRegistry(new Registry(registry));

    if(!this.id)
     this.setSender( new TaskHolder(this.senderCondition()));

    this.workflowTmp = {
      comment:"",
      domain:this.domain.id,
      task_holder: this.myTaskHolder,
      task: this.id,
      type: WORKFLOW_TYPES.COMMENT,
      email: this.auth.email ? this.auth.email : undefined
    }
  }
}

