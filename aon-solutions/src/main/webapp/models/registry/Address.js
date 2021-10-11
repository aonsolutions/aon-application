import { Provinces } from "../../services/province.js";

export class Address {
    
    id;
    domain;
    registry;
    main;
    streetType;
    address;
    number;
    address2;
    city;
    province;
    country;
    zip;

    dirty;
    removed;
    
    constructor(address) {
        if(address) {
            this.id = address.id;
            this.domain = address.domain;
            this.registry = address.registry;
            this.main = address.main;
            this.streetType = address.streetType;
            this.address = address.address;
            this.number = address.number;
            this.address2 = address.address2;
            this.city = address.city;
            this.province = address.province;
            this.country = address.country;
            this.zip = address.zip;
            this.dirty = address.dirty || false;
            this.removed = address.removed || false;
        } else {
            this.address = '';
            this.number = '';
            this.address2 = '';
            this.city = '';
            this.province = '';
            this.country = 'ES';
            this.zip = '';
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

    isMain() {
        return this.main;
    }

    setMain(main) {
        this.setDirty(true);
        this.main = main; 
        return this;
    }
   
    getStreetType() {
        return this.streetType;
    }

    setStreetType(streetType) {
        this.setDirty(true);
        this.streetType = streetType;
        return this;
    }

    getAddress() {
        return this.address;
    }

    setAddress(address) {
        this.setDirty(true);
        this.address = address; 
        return this;
    }

    getNumber() {
        if(this.number === undefined) this.number = '';
        return this.number;
    }

    setNumber(number) {
        this.setDirty(true);
        this.number = number; 
        return this;
    }

    getAddress2() {
        if(this.address2 === undefined) this.address2 = '';
        return this.address2;
    }

    setAddress2(address2) {
        this.setDirty(true);
        this.address2 = address2; 
        return this;
    }

    getCity() {
        return this.city;
    }

    setCity(city) {
        this.setDirty(true);
        this.city = city; 
        return this;
    }

    getProvince() {
        return this.province;
    }

    setProvince(province) {
        this.setDirty(true);
        this.province = province; 
        return this;
    }

    getCountry() {
        return this.country;
    }

    setCountry(country) {
        this.setDirty(true);
        this.country = country; 
        return this;
    }

    getZip() {
        return this.zip;
    }

    setZip(zip) {
        this.setDirty(true);
        this.zip = zip; 
        this.calculateProvince();
        return this;
    }

    calculateProvince() {
        if(this.country === 'ES' && this.zip){
            const provinces = Provinces.filter(f => f.code === this.zip.substring(0,2));
            if(provinces.length > 0) 
                this.province = provinces[0].name;
        }
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

    getFullAddress() {
        return `${this.getAddress()}, ${this.getNumber()} ${this.getAddress2()}, ${this.getZip()} ${this.getCity()}, ${this.getProvince()}, ${this.getCountry()}`;
    }
}