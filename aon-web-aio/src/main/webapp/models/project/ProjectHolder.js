import * as LS from '../../services/localStorageService.js';
import { TaskHolder } from '../registry/TaskHolder.js';
import { Workgroup } from './Workgroup.js';

export class ProjectHolder {
    id;
    domain;
    project;
    start_date;
    end_date;
    workgroup;
    taskHolder;
    dirty;

    constructor(projectHolder) {
        if(projectHolder) {
            this.id = projectHolder.id;
            this.domain = projectHolder.domain || LS.getDomainId();
            this.project = projectHolder.project;
            this.start_date = projectHolder.start_date || new Date();
            this.end_date = projectHolder.end_date;
            this.workgroup = new Workgroup(projectHolder.workgroup);
            this.taskHolder = new TaskHolder(projectHolder.taskHolder);
            this.dirty = projectHolder.dirty || false;
        } else {
            this.domain = LS.getDomainId();
            this.start_date = new Date();
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
        return this.start_date;
    }
 
    setStartDate(startDate) {
        this.setDirty(true);
        this.start_date = startDate;
        return this;
    }

    getEndDate() {
        return this.end_date;
    }

    setEndDate(endDate) {
        this.setDirty(true);
        this.end_date = endDate;
        return this;
    }

    getWorkgroup() {
        return this.workgroup;
    }

    setWorkgroup(workgroup){
        this.setDirty(true);
        this.workgroup = new Workgroup(workgroup);
        return this;
    }

    getTaskHolder() {
        return this.taskHolder;
    }

    setTaskHolder(taskHolder) {
        this.setDirty(true);
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