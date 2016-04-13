package com.esferalia.aon.occam.api.model.office;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.occam.api.model.HasId;
import com.esferalia.aon.occam.api.model.security.User;

public class Notice implements Serializable, HasId {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer id;
	private Integer domain;
	private Integer recipient; // Destinatario del aviso
	private Integer notice; //Notice al que referencia **null si es cabecera = titulo
	
	private String source; //ID del registry	
	private String status;
	private String title; // Asunto del aviso
	private String body; //Cuerpo del aviso
	private String type; //Tipo de aviso
	private String priority; //Prioridad
	private String company;
	
	private Date startDate;
	private Date endDate;
	private User sender; //Remitente del aviso
	
	private List<Tag> tags;
	private List<Notice> comments;

	public Notice() {
		tags = new LinkedList<Tag>();
		comments = new LinkedList<Notice>();
	}
	
	// ===============SETTERS=================== //
	
	public Notice setId(Integer id) {
		this.id = id;
		return this;
	}
	
	public Notice setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	
	public Notice setStartDate(Date startDate) {
		this.startDate = startDate;
		return this;
	}
	
	public Notice setEndDate(Date endDate) {
		this.endDate = endDate;
		return this;
	}
	
	public Notice setSender(User sender) {
		this.sender = sender;
		return this;
	}
	
	public Notice setTitle(String title) {		
		this.title = title;
		return this;
	}
	
	public Notice setBody(String body) {
		this.body = body;
		return this;
	}
	
	public Notice setRecipient(Integer recipient) {
		this.recipient = recipient;
		return this;
	}
	
	public Notice setSource(String source) {
		this.source = source;
		return this;
	}
	
	public Notice setStatus(String status) {
		this.status = status;
		return this;
	}
	
	public Notice setType(String type) {
		this.type = type;
		return this;
	}
	
	public Notice setPriority(String priority) {
		this.priority = priority;
		return this;
	}
	
	public Notice setCompany(String company) {
		this.company = company;
		return this;
	}
	
	public Notice setNotice(Integer notice) {
		this.notice = notice;
		return this;
	}
	
	public Notice addTag (Tag tag) {
		this.tags.add(tag);
		return this;
	}
	
	public Notice setTags(List<Tag> tags) {
		this.tags = tags;
		return this;
	}
	
	public Notice clearTagList() {
		this.tags = new LinkedList<Tag>();
		return this;
	}
	
	public Notice addNotice(Notice comment) {
		this.comments.add(comment);
		return this;
	}
	
	public Notice addComments(List<Notice> comments) {
		this.comments = comments;
		return this;
	}
	
	// ===============GETTERS=================== //
	
	@Override
	public Integer getId() {		
		return id;
	}
	
	public Integer getDomain() {
		return domain;
	}
	
	public Date getStartDate() {
		return startDate;
	}
	
	public Date getEndDate() {
		return endDate;
	}
	
	public User getSender() {
		return sender;
	}
	
	public String getTitle() {
		return (title != null) ? title : "" ;
	}
	
	public String getBody() {
		return (body != null) ? body : "";
	}
	
	public String getSource() {
		return source;
	}
	
	public Integer getRecipient() {
		return recipient;
	}
	
	public String getStatus() {
		return status;
	}
	
	public String getCompany() {
		return company;
	}
	
	public String getType() {
		return type;
	}
	
	public String getPriority() {
		return priority;
	}
	
	public Integer getNotice() {
		return notice;
	}
	
	public List<Tag> getTags() {
		return tags;		
	}
	
	public List<Notice> getComments() {
		return comments;
	}
}
