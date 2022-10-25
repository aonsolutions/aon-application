import { Bank } from '../../modules/registry/bank/Bank.js';
import { Domain } from '../Domain.js';
import { RegistryPaymethod } from '../RegistryPaymethod.js';
import { Address } from './Address.js';
import { Media } from './Media.js';
import { RecordData } from './RecordData.js';
import { RegistrySegment } from './RegistrySegment.js';

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

    addresses;
    media;
    banks;
    paymethod;
    record_data;
    rsegment;

    constructor(registry) {
        if(registry) {
            this.id = registry.id;
            this.domain = new Domain(registry.domain);
            this.document = registry.document || '';
            this.documentType = registry.documentType;
            this.documentCountry = registry.documentCountry || 'ES';
            this.name = registry.name || '';
            this.alias = registry.alias || '';
            this.legalPerson = registry.legalPerson;
            this.confidential = registry.confidential;
            this.global = registry.global;
            this.addresses = registry.addresses 
                ? registry.addresses.map(a => new Address(a))
                : [];
            this.media = registry.media 
                ? registry.media.map(m => new Media(m))
                : [];

            this.banks = registry.banks 
                ? registry.banks.map(m => new Bank(m))
                : [];
            this.paymethod = new RegistryPaymethod(registry.paymethod);
            this.dirty = registry.dirty;
            this.record_data = new RecordData(registry.record_data);

            this.rsegment = registry.rsegment 
                ? registry.rsegment.map(a => new RegistrySegment(a))
                : [];
        } else {
            this.domain = new Domain();
            this.document = '';
            this.documentCountry = 'ES';
            this.name = '';
            this.alias = '';
            this.legalPerson = false;
            this.confidential = false;
            this.global = false;
            this.dirty = false;
            this.addresses = [];
            this.media = [];
            this.banks = [];
            this.paymethod = new RegistryPaymethod();
            this.record_data = new RecordData();

            this.rsegment = [];
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

    getDocument() {
        return this.document;
    }

    setDocument(document) {
        this.setDirty(true);
        this.document = document;
        return this;
    }

    getDocumentType() {
        return this.documentType;    
    }

    setDocumentType(documentType) {
        this.setDirty(true);
        this.documentType = documentType;
        return this;
    }

    getDocumentCountry() {
        return this.documentCountry;
    }
    
    setDocumentCountry(documentCountry) {
        this.setDirty(true);
        this.documentCountry = documentCountry;
        return this;
    }

    getName() {
        return this.name;
    }

    setName(name) {
        this.setDirty(true);
        this.name = name;
        return this;
    }

    getAlias() {
        return this.alias;
    }

    setAlias(alias) {
        this.setDirty(true);
        this.alias = alias;
        return this;
    }

    isLegalPerson() {
        return this.legalPerson;
    } 

    setLegalPerson(legalPerson) {
        this.setDirty(true);
        this.legalPerson = legalPerson;
        return this;
    }

    getNationality() {
        return this.nationality;
    }

    setNationality(nationality) {
        this.setDirty(true);
        this.nationality = nationality;
        return this;
    }

    isConfidential() {
        return this.confidential;
    }

    setConfidential(confidential) {
        this.setDirty(true);
        this.confidential = confidential;
        return this;
    }

    isGlobal() {
        return this.global;
    }

    setGlobal(global) {
        this.global = global;
        return this;
    }

    getAddresses() {
        return this.addresses;
    }

    setAddresses(addresses) {
        this.addresses = addresses;
        return this;
    }

    addAddress(address) {
        this.getAddresses().push(address)
        return this.getAddresses();
    } 

    getBanks() {
        return this.banks;
    }

    setBanks(banks) {
        this.banks = banks;
        return this;
    }

    addBank(bank) {
        this.getBanks().push(bank)
        return this.getBanks();
    } 

    getMedia() {
        return this.media;
    }

    setMedia(media) {
        this.media = media;
        return this;
    }

    addMedia(media) {
        this.getMedia().push(media)
        return this.getMedia();
    } 

    getPaymethod() {
        return this.paymethod;
    }

    setPaymethod(paymethod) {
        this.paymethod = paymethod;
        return this;
    }

    isDirty() {
        return this.dirty;
    }

    setDirty(dirty) {
        this.dirty = dirty;
        return this;
    }

    getRecordData() {
        return this.record_data;
    }

    setRecordData(recordData) {
        this.record_data = recordData;
        return this;
    }

    getRegistrySegment() {
        return this.rsegment;
    }

    setRegistrySegment(rsegments) {
        this.rsegment = rsegments;
        return this;
    }

    addRegistrySegment(rsegment) {
        this.getRegistrySegment().push(rsegment)
        return this.getRegistrySegment();
    } 

} 