import { Domain } from '../Domain.js';

export class RegistrySegment {
    id;
    domain;
    segment;
    registry;

    constructor(rsegment) {
        if(rsegment) {
            this.id = rsegment.id;
            this.domain = new Domain(rsegment.domain);
            this.segment = rsegment.segment;
            this.registry = rsegment.registry;
        }else {
            this.domain = new Domain();
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

    getSegment(){
        return this.segment;
    }
    
    setSegment(segment){ 
        this.segment = segment;
        return this;
    }

    getRegistry(){
        return this.registry;
    }
    
    setRegistry(registry){ 
        this.registry = registry;
        return this;
    }


}