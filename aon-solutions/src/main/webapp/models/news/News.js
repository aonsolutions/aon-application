import { Domain } from "../Domain.js";

const NewsType = {
    NEWS:"news",
    MESSAGE:"message",
}

export class News {

    id;
	domain;
	title; // titulo de la noticia
	description; // descripcion de la noticia
	content; // contenido de la noticia
	url; // url de la noticia
	active; 
	rss; // indica si la noticia se va a publicar en rss
	initDate;
	endDate;
	category; // channel
	type;
	scope;
	
    constructor(news) {
        if(news) {
            this.id = news.id;
            this.domain = new Domain(news.domain);
            this.title = news.title;
            this.description = news.description;
            this.content = news.content;
            this.url = news.url;
            this.active = news.active;
            this.rss = news.rss;
            this.initDate = news.initDate;
            this.endDate = news.endDate;
            this.category = news.category;
            this.type = news.type;
            this.scope = news.scope;
        } else {
            this.domain = new Domain();
            this.type = NewsType.NEWS;
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

    getTitle() {
        return this.title;
    }
        
    setTitle(title) {
        this.title = title; 
        return this;
    }

    getDescription(){
        return this.description;
    }

    setDescription(description){
        this.description = description;
        return this;
    }

    getContent(){
        return this.content;
    }

    setContent(content){
        this.content = content;
        return this;
    }
    
    getUrl(){
        return this.url;
    }

    setUrl(url){
        this.url = url;
        return this;
    }

    getActive(){
        return this.active;
    }

    setActive(active){
        this.active = active;
        return this;
    }

    getRss(){
        return this.rss;
    }

    setRss(rss){
        this.rss = rss;
        return this;
    }

    getInitDate(){
        return this.initDate;
    }

    setInitDate(initDate){
        this.initDate = initDate;
        return this;
    }

    getEndDate(){
        return this.endDate;
    }

    setEndDate(endDate){
        this.endDate = endDate;
        return this;
    }

    getCategory(){
        return this.category;
    }

    setCategory(category){
        this.category = category;
        return this;
    }

    getType(){
        return this.type;
    }

    setType(type){
        this.type = type;
        return this;
    }

    getScope(){
        return this.scope;
    }

    setScope(scope){
        this.scope = scope;
        return this;
    }
        
}