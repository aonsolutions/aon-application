import * as LS from '../../services/localStorageService.js';
import { Domain } from '../Domain.js';

export class Registry {
    id;
    domain;
    document;
    documentType;
    documentCountry;
    name;
    alias;
    legalPerson;
    nationality;
    confidential;
    global;
    dirty;

    constructor(registry) {
        if(registry) {
            this.id = registry.id;
            this.domain = new Domain(registry.domain);
            this.document = registry.document;
            this.documentType = registry.documentType;
            this.documentCountry = registry.documentCountry;
            this.name = registry.name;
            this.alias = registry.alias;
            this.legalPerson = registry.legalPerson;
            this.confidential = registry.confidential;
            this.global = registry.global;
            this.dirty = registry.dirty;
        } else {
            this.domain = new Domain();
            this.document = '';
            this.name = '';
            this.alias = '';
            this.legalPerson = false;
            this.confidential = false;
            this.global = false;
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


} 