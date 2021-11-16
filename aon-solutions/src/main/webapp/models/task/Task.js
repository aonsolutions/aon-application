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
  appParams;

  constructor(task) {
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
      this.setWorkflowTmp({domain: this.domain.id});
      this.files = [];
    }
  }

//   task._onPropertyChanged = (name, val) => {
//     console.log(change, val);
// }

  setTask(task) {
    if(task) {
      this.myTaskHolder= new TaskHolder(task.myTaskHolder);
      this.setId(task.id || undefined);
      this.setAuth(task.auth || {});
      this.setStatus(task.status || TASK_STATUS.PENDING);
      this.setNumber(task.number || undefined);
      this.setWorkgroup(new Workgroup(task.workgroup));
      this.setRegistry(new Registry(task.registry));
      this.setTaskHolder(new TaskHolder(task.task_holder));
      this.setTitle(task.title || "");
      this.setDescription(task.description || "")
      this.setSource(task.source || TASK_SOURCE.QUERY);
      this.setSourceId(task.source_id || undefined);
      this.setStartDate(task.start_date || undefined);
      this.setParent(task.parent || undefined);
      this.setGTaskId(task.gtask_id || (!this.id && this.auth.email && !this.isAdvisoryCompany() ? this.auth.email : undefined));
      this.setDomain(new Domain(task.domain)) 
      this.setWorkflow(task.workflow || []);
      this.setDomainTmp(this.domain);

      this.setWorkflowTmp({
        comment:"",
        domain:this.domain.id,
        task_holder: this.myTaskHolder,
        task: this.id,
        type: WORKFLOW_TYPES.COMMENT,
        email: this.auth.email ? this.auth.email : undefined
      });
  
      this.setProject(new Project(task.project));
      this.setSender(new TaskHolder( task.id ? task.sender : this.senderCondition() ))
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
  // cleanTask(projectDefault=false){
  //   this.workgroup   = new Workgroup();
  //   if(!projectDefault)
  //     this.setProject(new Project());
  //   this.registry    = new Registry();
  //   this.task_holder = new TaskHolder();
  //   this.title       = "";
  //   this.description = "";
  //   this.source_id   = undefined;
  //   this.workflow    = [];
  //   this.setFiles([]);
  // }

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
    if(TASK_SOURCE.REQUEST !== v)
      this.source_id = null;
  }

  getSourceId() {
    return this.source_id;
  }

  setSourceId(source_id) {
    this.source_id = source_id;
    this.onPropertyChanged('source_id', this.source_id);
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

  setAppParams(appParams){
    this.appParams = appParams;
  }

  getAppParams(){
    return this.appParams;
  }

  isProject(){
    return this.project && this.project.id ? true : false;
  }

  isExternal(){
    let bool = false;
    if( !this.isAdvisoryCompany() && this.id && this.isOtherDomain())
      bool = true;
    return bool;
  }

  isOtherDomain(){
    return parseInt(LS.getDomainId()) !== parseInt(this.domain.id) ? true : false;
  }

  isAdvisoryCompany(){
    return "OFFICE" === LS.getCompany().type ? true : false;
  }

  senderCondition(){
    return !this.isAdvisoryCompany() && this.isProject() && !this.isExternal() ? undefined : this.myTaskHolder;
  }

  getWorkgroupDefault() {
    let wg;
    if(this.getAppParams()){
      let exist = this.getAppParams().find(({name})=> name ==="APP_DEFAULT_REQUESTS_WORKGROUP");
      if(exist) wg = parseInt(exist.value);
    }
    return wg;
  }

  getTaskHolderDefault() {
    let th;
    if(this.getAppParams()){
      let exist = this.getAppParams().find(({name})=> name ==="APP_DEFAULT_REQUESTS_TASK_HOLDER");
      if(exist) th = parseInt(exist.value);
    }
    return th;
  }

  changeWhAndTh(){
    if(!this.id && this.isOtherDomain()){
      if(!this.getWorkgroup().id && this.getWorkgroupDefault())
        this.setWorkgroup({id: this.getWorkgroupDefault()});

      if(!this.getTaskHolder().id && this.getTaskHolderDefault())
        this.setTaskHolder({id: this.getTaskHolderDefault()});
    }
  }

  /**
   * CHANGE VALUES WHEN PROJECT CHANGE 
   */
  changeProject(){
    if(this.isProject()){
      const {projectHolder} = this.project;
      const workgroup = projectHolder.workgroup && projectHolder.workgroup.id ?  projectHolder.workgroup : this.workgroup;
      this.setWorkgroup(new Workgroup(workgroup));
      const taskHolder = projectHolder.taskHolder && projectHolder.taskHolder.id ? projectHolder.taskHolder : this.task_holder;
      this.setTaskHolder(taskHolder);
    } else if(!this.id) {
      this.setWorkgroup(new Workgroup());
      this.setTaskHolder(new TaskHolder());
    }

    if(!this.id)
     this.setSender( new TaskHolder(this.senderCondition()));

    this.setWorkflowTmp({
      comment:"",
      domain:this.domain.id,
      task_holder: this.myTaskHolder,
      task: this.id,
      type: WORKFLOW_TYPES.COMMENT,
      email: this.auth.email ? this.auth.email : undefined
    });
  }
  
  onPropertyChanged(propName, val){
    return (propName, val);
  }
}

