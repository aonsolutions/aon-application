import { Domain } from '../Domain.js';
import { Registry } from '../registry/Registry.js';
import { ProjectHolder } from './ProjectHolder.js';
import { ProjectType } from './ProjectType.js';

export class Project {
    id;
    domain;
    type;
    registry;
    name;
    alias;
    date;
    tas;
    commercial;
    reservation;
    active;
    projectHolder;
    projectHolders;
    dirty;

    constructor(project) {
        if(project) {
            this.id = project.id;
            this.domain = new Domain(project.domain);
            this.type = new ProjectType(project.type);
            this.registry = new Registry(project.registry);
            this.name = project.name;
            this.alias = project.alias;
            this.date = project.date;
            this.tas = project.tas || false;
            this.commercial = project.commercial || false;
            this.reservation = project.reservation || false;
            this.active = project.active || true;
            this.projectHolder = new ProjectHolder(project.projectHolder);
            this.dirty = project.dirty || false;
            this.projectHolders = project.projectHolders || undefined;
        } else {
            this.domain = new Domain();
            this.type = new ProjectType();
            this.registry = new Registry();
            this.name = '';
            this.alias = '';
            this.date = new Date();
            this.tas = false;
            this.commercial = false;
            this.reservation = false;
            this.active = true;
            this.projectHolder = new ProjectHolder();
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
        this.domain = new Domain(domain);
        return this;
    }

    getType() {
        return this.type;
    }

    setType(type) { 
        this.setDirty(true);
        this.type = new ProjectType(type);
        return this;
    }

    getRegistry() {
        return this.registry;
    }

    setRegistry(registry) {
        this.setDirty(true);
        this.registry = registry;
        return this;
    }

    getProjectHolder() {
        return this.projectHolder;
    }

    setProjectHolder(projectHolder) {
        this.setDirty(true);
        this.projectHolder = new ProjectHolder(projectHolder);
        return this;
    }

    getName() {
        return this.name;
    }

    setName(name) {
        this.setDirty(true);
        this.name = name;
        return this;
    }

    getAlias() {
        return this.alias;
    }

    setAlias(alias) {
        this.setDirty(true);
        this.alias = alias;
        return this;
    }

    getDate() {
        return this.date;
    }

    setDate(date) {
        this.setDirty(true);
        this.date = date;
        return this;
    }

    getProjectHolders() {
        return this.projectHolders || [];
    }

    setProjectHolders(projectHolders) {
        this.projectHolders = projectHolders.map(holder => new ProjectHolder(holder).setDirty(true));
        return this;
    }

    isTas() {
        return this.tas;
    }

    setTas(tas){
        this.setDirty(true);
        this.tas = tas;
        return this;
    }
    
    isCommercial() {
        return this.commercial;
    }

    setCommercial(commercial){
        this.setDirty(true);
        this.commercial = commercial;
        return this;
    }
    
    isReservation() {
        return this.reservation;
    }

    setReservation(reservation){
        this.setDirty(true);
        this.reservation = reservation;
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
        return this.dirty || this.getProjectHolder().isDirty();
    }

    setDirty(dirty) {
        this.dirty = dirty;
        return this;
    }

    /**
     * 
     * @returns Project
     */
    clone() {
        return new Project(this);
    }
}
