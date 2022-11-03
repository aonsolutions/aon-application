import * as LS from '../../services/localStorageService.js';

export class Workgroup {
    id;
    domain;
    description;
    active;
    dirty;

    removed;

    constructor(workgroup) {
        if(workgroup) {
           this.id = workgroup.id; 
           this.domain = workgroup.domain || LS.getDomainId();
           this.description = workgroup.description;
           this.active = workgroup.active;
           this.dirty = workgroup.dirty;

           this.removed = workgroup.removed || false;
        } else {
            this.domain = LS.getDomainId();
            this.description = '';
            this.active = true;
            this.dirty = false;
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

    getDescription() {
        return this.description;
    }

    setDescription(description) {
        this.setDirty(true);
        this.description = description;
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


    isRemoved() {
        return this.removed;
    }

    setRemoved(removed) {
        this.removed = removed;
        return this;
    }

    remove(){
        this.setRemoved(true);
    }
} 