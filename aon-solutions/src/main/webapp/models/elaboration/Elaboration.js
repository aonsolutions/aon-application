import { Domain } from "../Domain.js";

export class Elaboration {

    id;
	domain;
    serie;
    number;
    reference;
    date;
    item;
    description;
    warehouse;
    status;
    comments;
    remarks;
    source;
    sourceId;

	creationUser;
	creationDate;
	modificationUser;
	modificationDate;

    constructor(elaboration){
        if(elaboration) {
            this.id = elaboration.id;
            this.domain = new Domain(elaboration.domain);
            this.serie = elaboration.serie || '';
            this.number = elaboration.number;
            this.reference = elaboration.reference;
            this.date = elaboration.date;
            this.item = elaboration.item;
            this.description = elaboration.description;
            this.warehouse = elaboration.warehouse;
            this.status = elaboration.status;
            this.comments = elaboration.comments;
            this.remarks = elaboration.remarks;
            this.source = elaboration.source;
            this.sourceId = elaboration.sourceId;
        
            this.creationUser = elaboration.creationUser;
            this.creationDate = elaboration.creationDate;
            this.modificationUser = elaboration.modificationUser;
            this.modificationDate = elaboration.modificationDate;
        } else {
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

    getSerie() {
        return this.serie;
    }

    setSerie(serie) {
        this.serie = serie;
        return this;
    }

    getNumber() {
        return this.number;
    }

    setNumber(id) {
        this.number = number;
        return this;
    }   

    getReference() {
        return this.reference;
    }

    setReference(reference) {
        this.reference = reference;
        return this;
    }

    getDate() {
        return this.date;
    }

    setDate(date) {
        this.date = date;
        return this;
    }

    getItem() {
        return this.item;
    }

    setItem(item) {
        this.item = item;
        return this;
    }

    getDescription() {
        return this.description;
    }

    setDescription(description) {
        this.description = description;
        return this;
    }

    getWarehouse() {
        return this.warehouse;
    }

    setWarehouse(warehouse) {
        this.warehouse = warehouse;
        return this;
    }

    getStatus() {
        return this.status;
    }

    setStatus(status) {
        this.status = status;
        return this;
    }

    getComments() {
        return this.comments;
    }

    setComments(comments) {
        this.comments = comments;
        return this;
    }

    getRemarks() {
        return this.remarks;
    }

    setRemarks(remarks) {
        this.remarks = remarks;
        return this;
    }

    getSource() {
        return this.source;
    }

    setSource(source) {
        this.source = source;
        return this;
    }

    getSourceId() {
        return this.sourceId;
    }

    setSourceId(sourceId) {
        this.sourceId = sourceId;
        return this;
    }
}
