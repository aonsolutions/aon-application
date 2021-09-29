import * as LS from '../../services/localStorageService.js';

export class ProjectType {
    id;
    domain;
    description;
    active;
    dirty;

    constructor(projectType) {
        if(projectType) {
           this.id = projectType.id;
           this.domain = projectType.domain;
           this.description = projectType.description;
           this.active = projectType.active;
           this.dirty = projectType.dirty;
        } else {
            this.domain = LS.getDomainId();
            this.description = '';
            this.active = true;
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
        this.setDirty(this.description !== description);
        this.description = description;
        return this;
    }

    isActive() {
        return this.active;
    }

    setActive(active){
        this.setDirty(this.active !== active);
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