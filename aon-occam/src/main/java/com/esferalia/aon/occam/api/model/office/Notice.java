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
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public void setDomain(Integer domain) {
		this.domain = domain;
	}
	
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	public void setSender(User sender) {
		this.sender = sender;
	}
	
	public void setTitle(String title) {		
		this.title = title;
	}
	
	public void setBody(String body) {
		this.body = body;
	}
	
	public void setRecipient(Integer recipient) {
		this.recipient = recipient;
	}
	
	public void setSource(String source) {
		this.source = source;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public void setPriority(String priority) {
		this.priority = priority;
	}
	
	public void setCompany(String company) {
		this.company = company;
	}
	
	public void setNotice(Integer notice) {
		this.notice = notice;
	}
	
	public void addTag (Tag tag) {
		this.tags.add(tag);
	}
	
	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}
	
	public void clearTagList() {
		this.tags = new LinkedList<Tag>();
	}
	
	public void addNotice(Notice comment) {
		this.comments.add(comment);
	}
	
	public void addComments(List<Notice> comments) {
		this.comments = comments;
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
