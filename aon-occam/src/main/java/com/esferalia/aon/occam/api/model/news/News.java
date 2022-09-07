package com.esferalia.aon.occam.api.model.news;

import java.io.Serializable;
import java.util.Date;
import java.util.Optional;

import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.registry.Category;
import com.esferalia.aon.occam.api.model.security.Scope;

public class News  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private Domain domain;
	private String title; // titulo de la noticia
	private String description; // descripcion de la noticia
	private String content; // contenido de la noticia
	private String url; // url de la noticia
	private boolean active; 
	private boolean rss; // indica si la noticia se va a publicar en rss
	private Date initDate;
	private Date endDate;
	private Category category; // channel
	private NewsType type;
	private Scope scope;
	
	private Integer rattach;
//	private Integer template;

	public Integer getId() {
		return id;
	}
	
	public News setId(Integer id) {
		this.id = id;
		return this;
	}
	
	public Domain getDomain() {
		return domain;
	}
	
	public News setDomain(Domain domain) {
		this.domain = domain;
		return this;
	}
	
	public String getTitle() {
		return title;
	}
	
	public News setTitle(String title) {
		this.title = title;
		return this;
	}
	
	public Optional<String> getDescription() {
		return Optional.ofNullable(description);
	}
	
	public News setDescription(String description) {
		this.description = description;
		return this;
	}
	
	public String getContent() {
		return content;
	}
	
	public News setContent(String content) {
		this.content = content;
		return this;
	}
	
	public Optional<String> getUrl() {
		return Optional.ofNullable(url);
	}
	
	public News setUrl(String url) {
		this.url = url;
		return this;
	}
	
	public boolean isActive() {
		return active;
	}
	
	public News setActive(boolean active) {
		this.active = active;
		return this;
	}
	
	public boolean isRss() {
		return rss;
	}
	
	public News setRss(boolean rss) {
		this.rss = rss;
		return this;
	}
	
	public Optional<Date> getInitDate() {
		return Optional.ofNullable(initDate);
	}
	
	public News setInitDate(Date initDate) {
		this.initDate = initDate;
		return this;
	}
	
	public Optional<Date> getEndDate() {
		return Optional.ofNullable(endDate);
	}
	
	public News setEndDate(Date endDate) {
		this.endDate = endDate;
		return this;
	}
	
	public Category getCategory() {
		return category;
	}
	
	public News setCategory(Category category) {
		this.category = category;
		return this;
	}
	
	public NewsType getType() {
		return type;
	}
	
	public News setType(NewsType type) {
		this.type = type;
		return this;
	}
	
	public Scope getScope() {
		return scope;
	}
	
	public News setScope(Scope scope) {
		this.scope = scope;
		return this;
	}
	
	public Optional<Integer> getRattach() {
		return Optional.ofNullable(rattach);
	}
	
	public News setRattach(Integer rattach) {
		this.rattach = rattach;
		return this;
	}
}

