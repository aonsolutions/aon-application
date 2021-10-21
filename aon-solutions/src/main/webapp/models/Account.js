import * as LS from '../services/localStorageService.js';

export class Account {

    id;
    domain;
    code;  
    description;
    alias;
    entryEnabled;
    level;
    active;
    costCenter;

    constructor(account) {
        if(account) {
            this.id = account.id;
            this.domain = account.domain || LS.getDomainId();
            this.code = account.code;
            this.description = account.description;
            this.alias = account.alias;
            this.entryEnabled = account.entryEnabled;
            this.level = account.level;
            this.active = account.active;
            this.costCenter = account.costCenter;
        } else {
            this.domain = LS.getDomainId();
            this.code = '';
            this.description = '';
            this.alias = '';
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

    getCode() {
        return this.code;
    }

    setCode(code) {
        this.code = code;
        return this;
    }

    getDescription() {
        return this.description;
    }

    setDescription(description) {
        this.description = description;
        return this;
    }

    getAlias() {
        return this.alias;
    }

    setAlias(alias) {
        this.alias = alias;
        return this;
    }

    isEntryEnabled() {
        return this.entryEnabled;
    }

    setEntryEnabled(entryEnabled) {
        this.entryEnabled = entryEnabled;
        return this;
    }

    getLevel() {
        return this.level;
    }

    setLevel(level) {
        this.level = level;
        return this;
    }
    
    isActive() {
        return this.active;
    }

    setActive(active) {
        this.active = active;
        return this;
    }
    
    getCostCenter() {
        return this.costCenter;
    }

    setCostCenter(costCenter) {
        this.costCenter = costCenter;
        return this.costCenter;
    }
}