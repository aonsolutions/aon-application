import * as LS from '../services/localStorageService.js';

export class Paymethod {

    id;
    domain;
    name;
    type;

    constructor(paymethod) {
        if(paymethod) {
            this.id = paymethod.id;
            this.domain = paymethod.domain || LS.getDomainId();
            this.name = paymethod.name;
            this.type = paymethod.type;
        } else {
            this.domain = LS.getDomainId();
            this.name = '';
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

    getName() {
        return this.name;  
    }

    setName(name) {
        this.name = name;
        return this;
    }

    getType() {
        return this.type;
    }

    setType(type) {
        this.type = type;
        return this;
    }
} 