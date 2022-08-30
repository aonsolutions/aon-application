import { Domain } from "../Domain.js";

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
            this.setNews(news);
        } else {
            this.domain = new Domain();
            this.active = true;
            this.rss    = false;
        }
    }

    setNews(news){
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
    }

    getId() {
        return this.id;
    }

    setId(id) {
        this.id = id; 
    }

    getDomain() {
        return this.domain;
    }

    setDomain(domain) {
        this.domain = domain; 
    }

    getTitle() {
        return this.title;
    }
        
    setTitle(title) {
        this.title = title; 
    }

    getDescription(){
        return this.description;
    }

    setDescription(description){
        this.description = description;
    }

    getContent(){
        return this.content;
    }

    setContent(content){
        this.content = content;
    }
    
    getUrl(){
        return this.url;
    }

    setUrl(url){
        this.url = url;
    }

    getActive(){
        return this.active;
    }

    setActive(active){
        this.active = active;
    }

    getRss(){
        return this.rss;
    }

    setRss(rss){
        this.rss = rss;
    }

    getInitDate(){
        return this.initDate;
    }

    setInitDate(initDate){
        this.initDate = initDate;
    }

    getEndDate(){
        return this.endDate;
    }

    setEndDate(endDate){
        this.endDate = endDate;
    }

    getCategory(){
        return this.category;
    }

    setCategory(category){
        this.category = category;
    }

    getType(){
        return this.type;
    }

    setType(type){
        this.type = type;
    }

    getScope(){
        return this.scope;
    }

    setScope(scope){
        this.scope = scope;
    }
        
}