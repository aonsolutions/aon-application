import * as LS from '../../services/localStorageService.js';
import { Domain } from '../Domain.js';
import { RegistryType } from '../enums.js';
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
            this.tas = project.tas;
            this.commercial = project.commercial;
            this.reservation = project.reservation;
            this.active = project.active;
            this.projectHolder = new ProjectHolder(project.projectHolder);
            this.dirty = project.dirty;
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
        this.type = new ProjectType(type);
        return this;
    }

    getRegistry() {
        return this.registry;
    }

    setRegistry(registry) {
        this.registry = registry;
        return this;
    }

    getProjectHolder() {
        return this.projectHolder;
    }

    setProjectHolder(projectHolder) {
        this.ProjectHolder = new ProjectHolder(projectHolder);
        return this;
    }

    getName() {
        return this.name;
    }

    setName(name) {
        this.name = name;
        return this;
    }

    getAlias() {
        return this.alias;
    }

    setAlias(alias) {
        this.alias = alias;
        return this;
    }

    getDate() {
        return this.date;
    }

    setDate(date) {
        this.date = date;
        return this;
    }

    isTas() {
        return this.tas;
    }

    setTas(tas){
        this.tas = tas;
        return this;
    }
    
    isCommercial() {
        return this.commercial;
    }

    setCommercial(commercial){
        this.commercial = commercial;
        return this;
    }
    
    isReservation() {
        return this.reservation;
    }

    setReservation(reservation){
        this.reservation = reservation;
        return this;
    }

    isActive() {
        return this.active;
    }

    setActive(active){
        this.active = active;
        return this;
    }
}
