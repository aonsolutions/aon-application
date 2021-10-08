import { Domain } from "./Domain";

export class Attach {
    id;
    domain;
    attachType;
    attachModule;
    name;
    size;
    driveId;
    scope;
    date;
    type;
    confidential;

    // FILE
    content;
    contentType;
    contentName;
    contentEncoding;
    contentSize;
    
    constructor(attach) {
        if(attach) {
            this.id = attach.id;
            this.domain = new Domain(attach.domain);
            this.attachType = attach.attachType;
            this.attachModule = attach.attachModule;
            this.name = attach.name;
            this.size = attach.size;
            this.driveId = attach.driveId;
            this.scope = attach.scope;
            this.date = attach.date;
            this.type = attach.type;
            this.confidential = attach.confidential;

            this.content = attach.content;
            this.contentType = attach.contentType;
            this.contentName = attach.contentName;
            this.contentEncoding = attach.contentEncoding;
            this.contentSize = attach.contentSize;
        } else {
            this.domain = new Domain();
            this.date = new Date();
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

    getAttachType() {
        return this.attachType;
    }

    setAttachType(attachType) {
        this.attachType = attachType;
        return this;
    }

    getAttachModule() {
        return this.attachModule;
    }

    setAttachModule(attachModule) {
        this.attachModule = attachModule;
        return this;
    }

    getName() {
        return this.name;
    }

    setName(name) {
        this.name = name;
        return this;
    }

    getSize() {
        return this.size;
    }

    setSize(size) {
        this.size = size;
        return this;
    }

    getDriveId() {
        return this.driveId;
    }
    setDriveId(driveId) {
        this.driveId = driveId;
        return this;
    }  

    getScope() {
        return this.scope;
    }

    setScope(scope) {
        this.scope = scope;
        return this;
    }
    
    getDate() {
        return this.date;
    }

    setDate(date) {
        this.date = date;
        return this;
    }
    
    getType() {
        return this.type;
    }

    setType(type) {
        this.type = type;
        return this;
    }

    isConfidential(){
        return this.confidential;
    }

    setConfidential(confidential){
        this.confidential = confidential;       
    }

    getContent() {
        return this.content;
    }

    setContent(content) {
        this.content = content;
        return this;
    }

    getContentType() {
        return this.contentType;
    }

    setContentType(contentType) {
        this.contentType = contentType;
        return this;
    }

    getContentSize() {
        return this.contentSize;
    }

    setContentSize(contentSize) {
        this.contentSize = contentSize;
        return this;
    }

    getContentName() {
        return this.contentName;
    }

    setContentName(contentName) {
        this.contentName = contentName;
        return this;
    }

    getContentEncoding() {
        return this.contentEncoding;
    }

    setContentEncoding(contentEncoding) {
        this.contentEncoding = contentEncoding;
        return this;
    }
}