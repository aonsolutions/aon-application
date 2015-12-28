package com.esferalia.aon.occam.api.model.office;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.occam.api.model.HasId;

public class Notice implements Serializable, HasId {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer id;
	private Integer domain;
	private Integer recipient; // Destinatario del aviso
	private Integer status; // Estado del aviso
	private Integer workgroup; // Grupo de trabajo al que va dirigido el aviso
	private Integer notice; //Notice al que referencia **null si es cabecera = titulo
	
	private String title; // Asunto del aviso
	private String body; //Cuerpo del aviso
	private String contact; //Modo de contacto
	private String source; // Origen del aviso
	private String company; // Empresa donde trabaja el origen del aviso
	private String type; //Tipo de aviso
	private String priority; //Prioridad
	
	private Date date;
	private User sender; //Remitente del aviso
	
	private List<Tag> tags;
	private List<NoticeComment> comments;

	public Notice() {
		tags = new LinkedList<Tag>();
		comments = new LinkedList<NoticeComment>();
	}
	
	// ===============SETTERS=================== //
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public void setDomain(Integer domain) {
		this.domain = domain;
	}
	
	public void setDate(Date date) {
		this.date = date;
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
	
	public void setContact(String contact) {
		this.contact = contact;
	}
	
	public void setSource(String source) {
		this.source = source;
	}
	
	public void setCompany(String company) {
		this.company = company;
	}
	
	public void setStatus(Integer status) {
		this.status = status;
	}
	
	public void setWorkgroup(Integer workgroup) {
		this.workgroup = workgroup;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public void setPriority(String priority) {
		this.priority = priority;
	}
	
	public void setNotice(Integer notice) {
		this.notice = notice;
	}
	
	public void addTag (Tag tag) {
		this.tags.add(tag);
	}
	
	public void addNotice(NoticeComment comment) {
		this.comments.add(comment);
	}
	
	// ===============GETTERS=================== //
	
	@Override
	public Integer getId() {		
		return id;
	}
	
	public Integer getDomain() {
		return domain;
	}
	
	public Date getDate() {
		return date;
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
	
	public Integer getRecipient() {
		return recipient;
	}
	
	public String getPhone() {
		return contact;
	}
	
	public String getSource() {
		return source;
	}
	
	public String getCompany() {
		return company;
	}
	
	public Integer getStatus() {
		return status;
	}
	
	public Integer getWorkgroup() {
		return workgroup;
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
	
	public List<NoticeComment> getComments() {
		return comments;
	}
}
