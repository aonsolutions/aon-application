export class Media {
    id;
    domain;
    registry;
    media;
    value;
    comment;
    administrative;
    commercial;
    technical;
    dirty;
    removed;
    
    constructor(media) {
        if(media) {
           this.id = media.id;
           this.domain = media.domain;
           this.registry = media.registry;
           this.media = media.media;
           this.value = media.value;
           this.comment = media.comment || '';
           this.administrative = media.administrative;
           this.commercial = media.commercial;
           this.technical = media.technical;
           this.dirty = media.dirty || false;
           this.removed = media.removed || false;
        } else {
            this.value = '';
            this.comment = '';
            this.administrative = true;
            this.commercial = true;
            this.technical = true;  
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

    getMedia() {
        return this.media;
    }

    setMedia(media) {
        this.media = media; 
        return this;
    }

    isEmail() {
        return this.media && 'EMAIL' === this.media.toUpperCase();
    }

    isPhone() {
        return this.media && ('CELLULAR' === this.media.toUpperCase()
            || 'FIXED_PHONE' === this.media.toUpperCase());
    }

    isWeb() {
        return this.media && 'WEB' === this.media.toUpperCase();
    }

    getValue() {
        return this.value;
    }

    setValue(value) {
        this.setDirty(true);
        this.value = value; 
        return this;
    }

    getComment() {
        return this.comment;
    }

    setComment(comment) {
        this.setDirty(true);
        this.comment = comment; 
        return this;
    }

    isAdministrative() {
        return this.administrative;
    }

    setAdministrative(administrative) {
        this.administrative = administrative; 
        return this;
    }

    isCommercial() {
        return this.commercial;
    }

    setCommercial(commercial) {
        this.commercial = commercial; 
        return this;
    }

    isTechnical() {
        return this.technical;
    }

    setTechnical(technical) {
        this.technical = technical; 
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