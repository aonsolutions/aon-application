import * as LS from '../../services/localStorageService.js';

export class Workgroup {
    id;
    domain;
    description;
    active;
    dirty;

    constructor(workgroup) {
        if(workgroup) {
           this.id = workgroup.id;
           this.domain = workgroup.domain;
           this.description = workgroup.description;
           this.active = workgroup.active;
           this.dirty = workgroup.dirty;
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