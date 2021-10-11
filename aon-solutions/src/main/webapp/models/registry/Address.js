export class Address {
    
    id;
    domain;
    registry;
    main;
    address;
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
           this.address = address.address;
           this.city = address.city;
           this.province = address.province;
           this.country = address.country;
           this.zip = address.zip;
           this.dirty = address.dirty || false;
           this.removed = address.removed || false;
        } else {
            this.address = '';
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
   
    getAddress() {
        return this.address;
    }

    setAddress(address) {
        this.setDirty(true);
        this.address = address; 
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
        return this.city;
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

    getFullAddress() {
        return `${this.getAddress()}, ${this.getZip()} ${this.getCity()}, ${this.getProvince()}, ${this.getCountry()}`;
    }
}