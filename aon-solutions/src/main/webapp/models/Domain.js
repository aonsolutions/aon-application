import * as LS from '../services/localStorageService.js';

export class Domain {

    id;
    name;
    type;

    constructor(domain) {
        if(domain) {
           this.id = domain.id || LS.getDomainId();
           this.name = domain.name || LS.getDomainName();
           this.type = domain.type;
        } else {
            this.id = LS.getDomainId();
            this.name = LS.getDomainName();
        }
    }

    getId() {
        return this.id;
    }

    setId(id) {
        this.id = id;
        return this;
    }

    getName() {
        return this.name;  
    }

    setName(name) {
        this.name = name;
        return this;
    }

    isOffice(){
        return this.type && 'OFFICE' === this.type.toUpperCase();
    }
} 