import { TAG_TYPE, TASK_SOURCE, TASK_STATUS, WORKFLOW_TYPES } from "../../modules/messenger/MessengerEnums.js";
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
  evaluation;
  source;
  source_id;
  gtask_id;
  files;
  start_date;
  creation_user;
  creation_date;
  parent;
  project;
  tags;
  childs;

  //TMP
  parentObj;
  domainTmp;
  workflowTmp;
  myTaskHolder;
  auth;
  domainCompany;
  
  constructor(task) {
    if(task){
      this.setTask(task);
    } else {
      this.id          = undefined;
      this.number      = undefined;
      this.title       = undefined;
      this.gtask_id    = undefined;
      this.description = undefined;
      this.source_id   = undefined;
      this.start_date  = undefined;
      this.parent      = undefined;
      this.auth        = undefined;
      this.parentObj   = undefined;
      this.evaluation  = undefined;
      this.domain      = new Domain();
      this.workgroup   = new Workgroup();
      this.project     = new Project();
      this.task_holder = new TaskHolder();
      this.sender      = new TaskHolder();
      this.registry    = new Registry();
      this.source      = TASK_SOURCE.QUERY;
      this.status      = TASK_STATUS.PENDING;
      this.workflow    = [];
      this.tags    = [];
      this.childs  = [];
      this.setWorkflowTmp({domain: this.domain.id});
      this.files = [];

      this.creation_user  = undefined;
      this.creation_date  = undefined;
    }
  }

//   task.onPropertyChanged = (name, val) => {
//     console.log(change, val);
// }

  setTask(task) {
    if(task) {
      this.myTaskHolder = new TaskHolder(task.myTaskHolder);
      this.setId(task.id || undefined);
      this.setAuth(task.auth || {});
      this.setStatus(task.status || TASK_STATUS.PENDING);
      this.setEvaluation(task.evaluation);
      this.setNumber(task.number || undefined);
      this.setWorkgroup(new Workgroup(task.workgroup));
      this.setRegistry(new Registry(task.registry));
      this.setTaskHolder(new TaskHolder(task.task_holder));
      this.setTitle(task.title || "");
      this.setDescription(task.description || "")
      this.setSource(task.source || TASK_SOURCE.QUERY);
      this.setSourceId(task.source_id || undefined);
      this.setStartDate(task.start_date || undefined);
      this.setCreationUser(task.creation_user || undefined);
      this.setCreationDate(task.creation_date || undefined);
      this.setParent(task.parent || undefined);
      this.setGTaskId(task.gtask_id  || undefined);
      this.setDomain(new Domain(task.domain));
      this.setWorkflow(task.workflow || []);
      this.setTags(task.tags || []);
      this.setChilds(task.childs || []);
      this.setParentObj(task.parentObj || undefined);
      this.setDomainTmp(this.domain);

      if(task.domainCompany) {
        this.setDomainCompany(task.domainCompany);
      }

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
      if(task.evaluation)                         this.setEvaluation(task.evaluation);
      if(task.source_id)                          this.setSourceId(task.source_id);
      if(task.registry && task.registry.id)       this.setRegistry(new Registry(task.registry));
      if(task.project && task.project.id)         this.setProject(new Project(task.project));
      if(task.description)                        this.setDescription(task.description);
      if(task.start_date)                         this.setStartDate(task.start_date);
      if(task.creation_user)                      this.setCreationUser(task.creation_user);
      if(task.creation_date)                      this.setCreationDate(task.creation_date);
      if(task.parent)                             this.setParent(task.parent);
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

  getEvaluation() {
    return this.evaluation;
  }

  setEvaluation(evaluation) {
    this.evaluation = evaluation;
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

  getDomainCompany() {
    return this.domainCompany;
  }

  setDomainCompany(domainCompany) {
    this.domainCompany = domainCompany;
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
    this.onPropertyChanged('project', this.project);
  }

  getSource() {
    return this.getSourceTask(); /**this.source;*/ 
  }

  setSource(v) {
    this.source = v;
    if(TASK_SOURCE.REQUEST !== v){
      this.source_id = null;
    }
  }

  getSourceId() {
    return this.source_id;
  }

  setSourceId(source_id) {
    this.source_id = source_id;
    this.onPropertyChanged('source_id', source_id);
  }

  getWorkgroup() {
    return this.workgroup;
  }

  setWorkgroup(workgroup) {
    this.workgroup = workgroup;
  }

  setTags(tags) {
    this.tags = tags;
    this.onPropertyChanged('tags', this.tags);
  }

  getTags() {
    return this.tags || [];
  }

  setChilds(childs) {
    this.childs = childs;
  }

  getChilds() {
    return this.childs || [];
  }

  addChild(child) {
    const existName = this.childs.find(t=> t.id === child.id);
    if(!existName) {
      this.childs.push(child);
    }
  
    this.onPropertyChanged('childs', this.child);
  }

  removeChild(id) {
    this.childs = this.childs.filter(t=> t.id != id);
    this.onPropertyChanged('childs', this.childs);
  }

  setParentObj(parentObj) {
    this.parentObj = parentObj;
  }

  getParentObj() {
    return this.parentObj;
  }

  addTag(tag) {
    const existName = this.tags.find(t=> t.name === tag.name);
    if(!existName) {
      this.tags.push(tag);
    }
  
    this.onPropertyChanged('tags', this.tags);
  }

  removeTag(id) {
    this.tags = this.tags.filter(tag=> tag.id != id);
    this.onPropertyChanged('tags', this.tags);
  }

  removeTagName(name) {
    this.tags = this.tags.filter(tag=> tag.name != name);
    this.onPropertyChanged('tags', this.tags);
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

  addWorkflow(workflow) {
    this.workflow.push(workflow);
  }

  setDescription(description) {
    this.description = description;
  }

  getDescription(){
    return this.description;
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

  getCreationUser() {
    return this.creation_user;
  }

  setCreationUser(creation_user) {
    this.creation_user = creation_user;
  }

  getCreationDate() {
    return this.creation_date;
  }

  setCreationDate(creation_date) {
    this.creation_date = creation_date;
  }

  getParent() {
    return this.parent;
  }

  setParent(parent) {
    this.parent = parent;
  }

  //-----------------------------------------------------AUX------------------------------------------------
  
  getSourceTask() {
    let source = this.source;
    if(this.isChild() && source === TASK_SOURCE.TASK){
      const parent = this.getParentObj();
      if(parent && parent.source !== TASK_SOURCE.GROUPED){
        source = parent.source;
      }
    }
    return source;
  }

  setTaskType(tagType){
    this.tags = this.tags.filter(tag=> tag.tag_type != TAG_TYPE.TASK_TYPE);
    this.addTag(tagType);
  }

  getTaskType(){
    let type = this.tags.find(tag=> tag.tag_type === TAG_TYPE.TASK_TYPE);
    return type ? type.id : null;
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

  getWorkflowTmp(){
    return this.workflowTmp;
  }

  setWorkflowTmp(workflowTmp){
    this.workflowTmp = workflowTmp;
  }

	isParent() {
		return this.parent == null;
	}

  isChild() {
		return !this.isParent();
	}

  isTask(){
    return this.getSource() === TASK_SOURCE.TASK;
  }
 
  isGrouped(){
    return this.getSource() === TASK_SOURCE.GROUPED;
  }
 
  isProject(){
    const project = this.getProject();
    return project && project.id ? true : false;
  }

  isExternal(){
    return !this.isAdvisoryCompany() && this.getId() && this.isOtherDomain() ? true : false;
  }

  isOtherDomain(){
    return parseInt(LS.getDomainId()) !== parseInt(this.domain.id) ? true : false;
  }

  isAdvisoryCompany(){
    return "OFFICE" === this.getDomainCompany().domainType ? true : false;
  }

  senderCondition(){
    return !this.isAdvisoryCompany() && this.isProject() && !this.isExternal() ? undefined : this.myTaskHolder;
  }

  onPropertyChanged(propName, val){
    return (propName, val);
  }
}

