import { Registry } from './Registry.js';

export class TaskHolder extends Registry {

    registry;
    type;
    user;
    active;
    costProfile;

    status;
    workgroups;

    constructor(taskHolder) {
        super(taskHolder);
        if(taskHolder) {
            this.registry = taskHolder.registry || taskHolder.id;
            this.type = taskHolder.type;
            this.user = taskHolder.user;
            this.active = taskHolder.active;
            this.costProfile = taskHolder.costProfile;
            this.status = taskHolder.status;
            this.workgroups = taskHolder.workgroups || [];
        } else {
            this.active = true;
            this.status = 'ACTIVE';
            this.workgroups = [];
        }
    }

    getRegistry() {
        return this.registry;
    }

    getType(){
        return this.type;
    }

    setType(type) {
        this.type = type;
        return this;
    }

    getUser() {
        return this.user;
    }

    setUser(user) {
        this.user = user;
        return this;
    }

    getCostProfile() {
        return this.costProfile;
    }

    setCostProfile(costProfile){ 
        this.costProfile = costProfile;
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

    getStatus() {
        return this.status;
    }

    setStatus(status) {
        this.status = status;
        return this;
    }

    getWorkgroups() {
        return this.workgroups;
    }

    setWorkgroups(workgroups) {
        this.workgroups = workgroups;
    }

    addWorkgroup(workgroup) {
        this.workgroups.push(workgroup);
    }
} 