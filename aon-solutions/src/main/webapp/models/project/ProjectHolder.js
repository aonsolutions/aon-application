import * as LS from '../../services/localStorageService.js';
import { TaskHolder } from './TaskHolder.js';
import { Workgroup } from './Workgroup.js';

export class ProjectHolder {
    id;
    domain;
    project;
    startDate;
    endDate;
    workgroup;
    taskHolder;
    dirty;

    constructor(projectHolder) {
        if(projectHolder) {
            this.id = projectHolder.id;
            this.domain = projectHolder.domain || LS.getDomainId();
            this.project = projectHolder.project;
            this.startDate = projectHolder.startDate || new Date();
            this.endDate = projectHolder.endDate;
            this.workgroup = new Workgroup(projectHolder.workgroup);
            this.taskHolder = new TaskHolder(projectHolder.taskHolder);
            this.dirty = projectHolder.dirty;
        } else {
            this.domain = LS.getDomainId();
            this.startDate = new Date();
            this.workgroup = new Workgroup();
            this.taskHolder = new TaskHolder();
            this.dirty = false;
        }
    }

    getId() {
        return this.id;
    }

    setId(id) {
        this.id = id;
        return this;
    }

    getDomain() {
        return this.domain;  
    }

    setDomain(domain) {
        this.domain = domain;
        return this;
    }

    getProject() {
        return this.project;
    }

    setProject(project) {
        this.project = project;
        return this;
    }

    getStartDate(){
        return this.startDate;
    }
 
    setStartDate(startDate) {
        this.startDate = startDate;
        return this;
    }

    getEndDate() {
        return this.this.endDate;
    }

    setEndDate(endDate) {
        this.endDate = endDate()
        return this;
    }

    getWorkgroup() {
        return this.workgroup;
    }

    setWorkgroup(workgroup){
        this.workgroup = new Workgroup(workgroup);
        return this;
    }

    getTaskHolder() {
        return this.taskHolder;
    }

    setTaskHolder(taskHolder) {
        this.taskHolder = new TaskHolder(taskHolder);
        return this;
    }

    isDirty() {
        return this.dirty;
    }

    setDirty(dirty) {
        this.dirty = dirty;
        return this;
    }
} 