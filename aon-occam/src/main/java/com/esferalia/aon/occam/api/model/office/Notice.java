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
	private Date date;
	private Integer sender; //Remitente del aviso
	private String title; // Asunto del aviso
	private String body; //Cuerpo del aviso
	private Integer recipient; // Destinatario del aviso
	private String phone;
	private String source; // Origen del aviso
	private String company; // Empresa donde trabaja el origen del aviso
	private Integer status; // Estado del aviso
	private Integer workgroup;
	private Integer type; //Tipo de aviso
	private Integer priority; //Prioridad
	
	private Integer notice; //Notice al que referencia **null si es cabecera = titulo

	private List<Notice> comments;

	public Notice() {
		comments = new LinkedList<Notice>();
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
	
	public void setSender(Integer sender) {
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
	
	public void setPhone(String phone) {
		this.phone = phone;
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
	
	public void setType(Integer type) {
		this.type = type;
	}
	
	public void setPriority(Integer priority) {
		this.priority = priority;
	}
	
	public void setNotice(Integer notice) {
		this.notice = notice;
	}
	
	public void addNotice(Notice notice) {
		this.comments.add(notice);
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
	
	public Integer getSender() {
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
		return (phone != null) ? phone : "";
	}
	
	public String getSource() {
		return (source != null) ? source : "";
	}
	
	public String getCompany() {
		return (company != null) ? company : "";
	}
	
	public Integer getStatus() {
		return status;
	}
	
	public Integer getWorkgroup() {
		return workgroup;
	}
	
	public Integer getType() {
		return type;
	}
	
	public Integer getPriority() {
		return priority;
	}
	
	public Integer getNotice() {
		return notice;
	}
	
	public List<Notice> getComment() {
		return comments;
	}
}
