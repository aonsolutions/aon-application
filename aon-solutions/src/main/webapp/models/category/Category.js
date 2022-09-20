import { Domain } from "../Domain.js";

export class Category {

    id;
	domain;
	name; 
	type; 
    scope;
	description;
	url; 
    type;
    rattach;
    
    attach;
	

    constructor(category) {
        if(category) {
            this.setCategory(category);
        } else {
            this.domain = new Domain().getId();
            this.attach = {};
        }
    }

    setCategory(category){
        this.id = category.id;
        this.domain = category.domain;
        this.name = category.name;
        this.scope = category.scope;
        this.description = category.description;
        this.url = category.url;
        this.type = category.type;
        this.rattach = category.rattach;
        this.attach = {};
    }

    getId() {
        return this.id;
    }

    setId(id) {
        this.id = id; 
    }

    getDomain() {
        return this.domain;
    }

    setDomain(domain) {
        this.domain = domain; 
    }

    getName() {
        return this.name;
    }
        
    setName(name) {
        this.name = name; 
    }

    getType(){
        return this.type;
    }

    setType(type){
        this.type = type;
    }

    getScope(){
        return this.scope;
    }

    setScope(scope){
        this.scope = scope;
    }

    getDescription(){
        return this.description;
    }

    setDescription(description){
        this.description = description;
    }
    
    getUrl(){
        return this.url;
    }

    setUrl(url){
        this.url = url;
    }

    getRattach(){
        return this.rattach;
    }

    setRattach(rattach){
        this.rattach = rattach;
    }

    getAttach(){
        return this.attach;
    }

    setAttach(attach){
        this.attach = attach;
    }
}