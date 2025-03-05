import * as LS from '../services/localStorageService.js';

export class RegistryPaymethod {

    id;
    domain;
    registry;
    paymethod;
    bank;
    numberOfPymnts;
    daysToFirstPymnt;
    daysBetweenPymnts;
    pymntDays;

    dirty;
    removed;

    constructor(rpaymethod) {
        if(rpaymethod) {
            this.id = rpaymethod.id;
            this.domain = rpaymethod.domain || LS.getDomainId();
            this.registry = rpaymethod.registry;
            this.paymethod = rpaymethod.paymethod;
            this.bank = rpaymethod.bank;
            this.numberOfPymnts = rpaymethod.numberOfPymnts || 1;
            this.daysToFirstPymnt = rpaymethod.daysToFirstPymnt || 0;
            this.daysBetweenPymnts = rpaymethod.daysBetweenPymnts || 0;
            this.pymntDays = rpaymethod.pymntDays || '';
            this.dirty = rpaymethod.dirty || false;
            this.removed = rpaymethod.removed || false;
        } else {
            this.domain = LS.getDomainId();
            this.numberOfPymnts = 1;
            this.daysToFirstPymnt = 0;
            this.daysBetweenPymnts = 0;
            this.pymntDays = '';
            this.dirty = false;
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
        this.setDirty(true);
        this.domain = domain;
        return this;
    }

    getRegistry(){
        return this.registry;
    }

    setRegistry(registry){
        this.setDirty(true);
        this.registry = registry;
        return this;
    }

    getPaymethod() {
        return this.paymethod;
    }

    setPaymethod(paymethod) {
        this.setDirty(true);
        this.paymethod = paymethod;
        return this;
    }

    getBank() {
        return this.bank;
    }

    setBank(bank) {
        this.setDirty(true);
        this.bank = bank;
        return this;
    }

    getNumberOfPymnts() {
        return this.numberOfPymnts;
    }

    setNumberOfPymnts(numberOfPymnts) {
        this.setDirty(true);
        this.numberOfPymnts = numberOfPymnts;
        return this;
    }

    getDaysToFirstPymnt(){
        return this.daysToFirstPymnt;
    }

    setDaysToFirstPymnt(daysToFirstPymnt) {
        this.setDirty(true);
        this.daysToFirstPymnt = daysToFirstPymnt;
        return this;
    }

    getDaysBetweenPymnts(){
        return this.daysBetweenPymnts;
    }

    setDaysBetweenPymnts(daysBetweenPymnts) {
        this.setDirty(true);
        this.daysBetweenPymnts = daysBetweenPymnts;
        return this;
    }

    getPymntDays() {
        return this.pymntDays;
    }

    setPymntDay(pymntDays) {
        this.setDirty(true);
        this.pymntDays = pymntDays;
        return this;
    }

    isDirty() {
        return this.dirty;
    }

    setDirty(dirty) {
        this.dirty = dirty;
        return this;
    }

    isRemoved() {
        return this.removed;
    }

    setRemoved(removed) {
        this.removed = removed;
        return this;
    }

    remove() {
        this.setRemoved(true);
    }
} 
