import * as LS from '../../services/localStorageService.js';

export class RecordData {
    id;
    domain;
    registry;
	creationDate;
	description;
	notary;
	number;
	recordDate;
	volume;
	section;
	page;
	sheet;
	registration;	
	attach;
    
    constructor(recordData) {
        if(recordData) {
            this.id = recordData.id;
            this.domain = recordData.domain || LS.getDomainId();
            this.registry = recordData.registry;
            this.creationDate = recordData.creationDate;
            this.description = recordData.description || 'Escritura de constitución';
            this.notary = recordData.notary || '';
            this.number = recordData.number || '';
            this.recordDate = recordData.recordDate;
            this.volume = recordData.volume || '';
            this.section = recordData.section || '';
            this.page = recordData.page || '';
            this.sheet = recordData.sheet || '';
            this.registration = recordData.registration || 'Inscrita en el Registro mercantil';	
            this.attach = recordData.attach;
        } else {
            this.description = 'Escritura de constitución';
            this.registration = 'Inscrita en el Registro mercantil';
            this.volume = '';
            this.section = '';
            this.page = '';
            this.sheet = '';
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
    
    setRegistry(registry){ 
        this.registry = registry;
        return this;
    }

    getCreationDate() {
        return this.creationDate;
    }

    setCreationDate(creationDate) {
        this.creationDate = creationDate;
        return this;
    }

    getRecordDate() {
        return this.recordDate;
    }

    setRecordDate(recordDate) {
        this.recordDate = recordDate;
        return this;
    }

    getDescription() {
        return this.description;
    }

    setDescription(description) {
        this.description = description;
        return this.description;
    }

    getNotary() {
        return this.notary;
    }

    setNotary(notary) {
        this.notary = notary;
        return notary;
    }

    getNumber() {
        return this.number;
    }

    setNumber(number) {
        this.number = number;
        return this;
    }

    getVolume() {
        return this.volume;
    }

    setVolume(volume) {
        this.volume = volume;
        return this;
    }

    getSection() {
        return this.section;
    }

    setSection(section) {
        this.section = section;
        return this;
    }
    
    getPage() {
        return this.page;
    }

    setPage(page) {
        this.page = page;
        return this;
    }
    
    getSheet() {
        return this.sheet;
    }

    setSheet(sheet) {
        this.sheet = sheet;
        return this;
    }

    getRegistration() {
        return this.registration;
    }	

    setRegistration(registration) {
        this.registration = registration;
        return this;
    }

    getAttach() {
        return this.attach;
    }

    setAttach(attach) {
        this.attach = attach;
        return this; 
    }

}