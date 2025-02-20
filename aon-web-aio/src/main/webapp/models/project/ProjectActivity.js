import * as LS from '../../services/localStorageService.js';
import { ActivityType } from '../ActivityType.js';

export class ProjectActivity {
    id;
    domain;
    project;
    activityType;
    active;
    dirty;

    removed;

    constructor(projectActivity) {
        if(projectActivity) {
            this.id = projectActivity.id;
            this.domain = projectActivity.domain || LS.getDomainId();
            this.project = projectActivity.project;
            this.activityType = new ActivityType(projectActivity.activityType);
            this.active = projectActivity.active;
            this.dirty = projectActivity.dirty || false;
            this.removed = projectActivity.removed || false;
        } else {
            this.domain = LS.getDomainId();
            this.active = true;
            this.dirty  = true;
            this.removed = false;
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

    getActivityType() {
        return this.activityType;
    }

    setActivityType(activityType) {
        this.activityType = activityType;
        return this;
    }

    isActive() {
        return this.active;
    }

    setActive(active) {
        this.active = active;
        return this;
    }

    isDirty() {
        return this.dirty;
    }

    setDirty(dirty) {
        this.dirty = dirty;
        return this;
    }

    isRemoved() {
        return this.removed;
    }

    setRemoved(removed) {
        this.removed = removed;
        return this;
    }
} 