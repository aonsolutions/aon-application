import { Domain } from '../Domain.js';

export class RegistryItem {

	id;
	domain;
	registry;
	item;
	type;
	code;
	price;
	discountExpr;
	priority;
	workplace;
	status;

    removed;

    constructor(ritem) {
        if(ritem) {
            this.id = ritem.id;
            this.domain = ritem.domain;
            this.registry = ritem.registry;
            this.item = ritem.item;
            this.type = ritem.type;
            this.code = ritem.code;
            this.price = ritem.price;
            this.discountExpr = ritem.discountExpr;
            this.priority = ritem.priority;
            this.workplace = ritem.workplace;
            this.status = ritem.status;
            
            this.removed = ritem.removed || false;
        }else {
            this.domain = new Domain();

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

    getRegistry(){
        return this.registry;
    }

    setRegistry(registry) {
        this.registry = registry;
        return this;
    }

    getItem(){
        return this.item;
    }

    setItem(item) {
        this.item = item;
        return this;
    }

    getType(){
        return this.type;
    }

    setType(type) {
        this.type = type;
        return this;
    }

    getCode(){
        return this.code;
    }

    setCode(code) {
        this.code = code;
        return this;
    }

    getPrice(){
        return this.price;
    }

    setPrice(price) {
        this.price = price;
        return this;
    }

    getDiscountExpr(){
        return this.discountExpr;
    }

    setDiscountExpr(discountExpr) {
        this.discountExpr = discountExpr;
        return this;
    }

    getPriority(){
        return this.priority;
    }

    setPriority(priority) {
        this.priority = priority;
        return this;
    }

    getWorkplace(){
        return this.workplace;
    }

    setWorkplace(workplace) {
        this.workplace = workplace;
        return this;
    }

    getStatus(){
        return this.status;
    }

    setStatus(status) {
        this.status = status;
        return this;
    }

    isRemoved() {
        return this.removed;
    }

    setRemoved(removed) {
        this.removed = removed;
        return this;
    }

    removed(){
        this.setRemoved(true);
    }
    
}