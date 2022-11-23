import * as LS from '../services/localStorageService.js';

export class ActivityType {
    id;
    domain;
    description;
    projectType;
    active;
    dirty;

    constructor(activityType) {
        if(activityType) {
            this.id = activityType.id;
            this.domain = activityType.domain || LS.getDomainId();
            this.description = activityType.description || '';
            this.projectType = activityType.projectType || '';
            this.active = activityType.active || true;
            this.dirty = activityType.dirty || false;
        } else {
            this.domain = LS.getDomainId();
            this.description = '';
            this.active = true;
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

    getDescription() {
        return this.description;
    }

    setDescription(description) {
        this.setDirty(true);
        this.description = description;
        return this;
    }

    getProjectType() {
        return this.projectType;
    }

    setProjectType(projectType) {
        this.setDirty(true);
        this.projectType = projectType;
        return this;
    }

    isActive() {
        return this.active;
    }

    setActive(active){
        this.setDirty(true);
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
} 