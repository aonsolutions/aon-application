import * as LS from '../services/localStorageService.js';

export class Domain {

    id;
    name;
    description;
    owner;
    parentId;
    domainType;
    enableHeredity;
    domainManagement;
    active;
    scope;
    maxDefinedUsers;
    definedUsers;

    constructor(domain) {
        if(domain) {
           this.id = domain.id || LS.getDomainId();
           this.name = domain.name || LS.getDomainName();
           this.description = domain.description;
           this.domainType = domain.domainType;
           this.owner = domain.owner;
           this.parentId = domain.parentId;
           this.enableHeredity = domain.enableHeredity;
           this.domainManagement = domain.domainManagement;
           this.active = domain.active;
           this.scope = domain.scope;
           this.maxDefinedUsers = domain.maxDefinedUsers;
           this.definedUsers = domain.definedUsers;
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

    getDescription() {
        return this.description;   
    }

    setDescription(description) {
        this.description = description;
        return this;
    }

    getOwner() {
        return this.owner;
    }

    setOwner(owner) {
        this.owner = owner;
        return this;
    }

    getParentId() {
        return this.parentId; 
    }

    setParentId(parentId) {
        this.parentId = parentId;
        return this;
    }

    getDomainType() {
        return this.domainType; 
    }

    setDomainType(domainType) {
        this.domainType = domainType;
        return this;
    }
    
    getEnableHeredity() {
        return this.enableHeredity; 
    }

    setEnableHeredity(enableHeredity) {
        this.enableHeredity = enableHeredity;
        return this;
    }

    isDomainManagement() {
        return this.domainManagement; 
    }

    setDomainManagement(domainManagement) {
        this.domainManagement = domainManagement;
        return this;
    }

    isActive() {
        return this.active; 
    }

    setActive(active) {
        this.active = active;
        return this;
    }

    getScope() {
        return this.scope; 
    }

    setScope(scope) {
        this.scope = scope;
        return this;
    }

    getMaxDefinedUsers() {
        return this.maxDefinedUsers; 
    }

    setMaxDefinedUsers(maxDefinedUsers) {
        this.maxDefinedUsers = maxDefinedUsers;
        return this;
    }

    getDefinedUsers() {
        return this.definedUsers; 
    }

    setDefinedUsers(definedUsers) {
        this.definedUsers = definedUsers;
        return this;
    }

    isOffice(){
        return this.getDomainType() && 'OFFICE' === this.getDomainType().toUpperCase();
    }

    isConsultancy() {
        return this.getDomainType() && 'CONSULTANCY' === this.getDomainType().toUpperCase();
    }

    isEnterprise() {
        return this.getDomainType() && 'ENTERPRISE' === this.getDomainType().toUpperCase();
    }

    isGarage() {
        return this.getDomainType() && 'GARAGE' === this.getDomainType().toUpperCase();
    }

    isAcademy() {
        return this.getDomainType() && 'ACADEMY' === this.getDomainType().toUpperCase();
    }

    isHotel() {
        return this.getDomainType() && 'HOTEL' === this.getDomainType().toUpperCase();
    }

    isCommerce() {
        return this.getDomainType() && 'COMMERCE' === this.getDomainType().toUpperCase();
    }

    isAdmin() {
        return this.getDomainType() && 'ADMIN' === this.getDomainType().toUpperCase();
    }

    isGeneric() {
        return this.getDomainType() && 'GENERIC' === this.getDomainType().toUpperCase();
    }

    isKitDigital() {
        return this.getDomainType() && 'KIT_DIGITAL' === this.getDomainType().toUpperCase();
    }

    isParent() {
        return this.parentId === undefined && this.isConsultancy();
    }
} 