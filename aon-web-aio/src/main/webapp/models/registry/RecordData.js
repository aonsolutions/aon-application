import * as LS from '../../services/localStorageService.js';
import { RecordDataType, CommercialRegistryCode } from './RecordDataEnums.js';

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
    type;
    irus;
    commercialRegistryCode;

    constructor(recordData) {
        const d = recordData || {};
        this.id = d.id;
        this.domain = d.domain || LS.getDomainId();
        this.registry = d.registry;
        this.creationDate = d.creationDate;
        this.description = d.description || 'Escritura de constitución';
        this.notary = d.notary || '';
        this.number = d.number || '';
        this.recordDate = d.recordDate;
        this.volume = d.volume || '';
        this.section = d.section || '';
        this.page = d.page || '';
        this.sheet = d.sheet || '';
        this.registration = d.registration || 'Inscrita en el Registro mercantil';
        this.attach = d.attach;
        this.type = d.type ? RecordDataType.safeValueOfName(d.type) : RecordDataType.OTHER_REGISTRATIONS;
        this.irus = d.irus || '';
        this.commercialRegistryCode = CommercialRegistryCode.safeValueOfCode(d.commercialRegistryCode);
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

    getRegistry() {
        return this.registry;
    }

    setRegistry(registry) {
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
        return this;
    }

    getNotary() {
        return this.notary;
    }

    setNotary(notary) {
        this.notary = notary;
        return this;
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

    getType() {
        return this.type;
    }

    setType(type) {
        this.type = type;
        return this;
    }

    getIrus() {
        return this.irus;
    }

    setIrus(irus) {
        this.irus = irus;
        return this;
    }

    getCommercialRegistryCode() {
        return this.commercialRegistryCode;
    }

    setCommercialRegistryCode(commercialRegistryCode) {
        this.commercialRegistryCode = commercialRegistryCode;
        return this;
    }

    toJSON() {
        return {
            id: this.id,
            domain: this.domain,
            registry: this.registry,
            creationDate: this.creationDate,
            description: this.description,
            notary: this.notary,
            number: this.number,
            recordDate: this.recordDate,
            volume: this.volume,
            section: this.section,
            page: this.page,
            sheet: this.sheet,
            registration: this.registration,
            attach: this.attach,
            type: this.type ? this.type.name : null,
            irus: this.irus,
            commercialRegistryCode: this.commercialRegistryCode ? this.commercialRegistryCode.code : null
        }
    }

}
