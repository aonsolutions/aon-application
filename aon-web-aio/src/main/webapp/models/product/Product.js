import { Domain } from "../Domain.js";

export class Product {

    id;
	domain;
	name;
	code;
	brand;
	category;
	status;
	type;
	kind;
	vat;
	retention;
	inventoriable;
	serializable;
	lotable;
	manufactured;
	composition;
	compositionPrice;
	packaged;
	salesAccount;
	purchaseAccount;

	creationUser;
	creationDate;
	modificationUser;
	modificationDate;

    constructor(product){
        if(product) {
            this.id = product.id;
            this.domain = new Domain(product.domain);
            this.name = product.name;
            this.code = product.code;
            this.brand = product.brand;
            this.category = product.category;
            this.status = product.status;
            this.type = product.type;
            this.kind = product.kind;
            this.vat = product.vat;
            this.retention = product.retention;
            this.inventoriable = product.inventoriable;
            this.serializable = product.serializable;
            this.lotable = product.lotable;
            this.manufactured = product.manufactured;
            this.composition = product.composition;
            this.compositionPrice = product.compositionPrice;
            this.packaged = product.packaged;
            this.salesAccount = product.salesAccount;
            this.purchaseAccount = product.purchaseAccount;
        
            this.creationUser = product.creationUser;
            this.creationDate = product.creationDate;
            this.modificationUser = product.modificationUser;
            this.modificationDate = product.modificationDate;
        } else {
            this.domain = new Domain();
            this.name = '';
            this.code = '';
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

    getName() {
        return this.name;
    }

    setName(name) {
        this.name = name;
        return this;
    }

    getBrand() {
        return this.brand;
    }

    setBrand(brand) {
        this.brand = brand;
        return this;
    }

    getCategory() {
        return this.category;
    }

    setCategory(category) {
        this.category = category;
        return this;
    }

    getStatus() {
        return this.status;
    }

    setStatus(status) {
        this.status = status;
        return this;
    }

    getType(){
        return this.type;
    }

    setType(type) {
        this.type = type;
        return this;
    }

    getKind() {
        return this.kind;
    }

    setKind(kind) {
        this.kind = kind;
        return this;
    }

    getVat() {
        return this.vat;
    }

    setVat(vat) {
        this.vat = vat;
        return this;
    }

    getRetention() {
        return this.retention;
    }

    setRetention(retention) {
        this.retention = retention;
        return this;
    }

    isInventoriable() {
        return this.inventoriable;
    }

    setInventoriable(inventoriable) {
        this.inventoriable = inventoriable;
        return this;
    }

    isSerializable() {
        return this.serializable;
    }

    setSerializable(serializable) {
        this.serializable = serializable;
        return this;
    }

    isLotable() {
        return this.lotable;
    }

    setLotable(lotable) {
        this.lotable = lotable;
        return this;
    }

    isManufactured() {
        return this.manufactured;
    }

    setManufactured(manufactured) {
        this.manufactured = manufactured;
        return this;
    }
    
    isComposition() {
        return this.composition;
    }

    setComposition(composition) {
        this.composition = composition;    
    }

    isCompositionPrice() {
        return this.compositionPrice;
    }

    setCompositionPrice(compositionPrice) {
        this.compositionPrice = compositionPrice;    
    }

    isPackaged() {
        return this.packaged;
    }

    setPackaged(packaged) {
        this.packaged = packaged;    
    }

    getSalesAccount() {
        return this.salesAccount;
    }

    setSalesAccount(salesAccount) {
        this.salesAccount = salesAccount;    
    }

    getPurchaseAccount() {
        return this.purchaseAccount;
    }

    setPurchaseAccount(purchaseAccount) {
        this.purchaseAccount = purchaseAccount;    
    }
}
