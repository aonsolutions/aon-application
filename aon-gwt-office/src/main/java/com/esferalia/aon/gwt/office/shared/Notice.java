package com.esferalia.aon.gwt.office.shared;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.esferalia.aon.gwt.common.shared.HasId;

public class Notice implements Serializable, HasId<Integer> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer id;
	private Integer domain;
	private Date date;
	private Integer sender; //Remitente del aviso
	private String subject; // Asunto o cuerpo del aviso
	private Integer recipient; // Destinatario del aviso
	private String source; // Origen del aviso
	private String company; // Empresa donde trabaja el origen del aviso
	private Integer status; // Estado del aviso
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
	
	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	public void setRecipient(Integer recipient) {
		this.recipient = recipient;
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
	
	public void setType(Integer type) {
		this.type = type;
	}
	
	public void setPriority(Integer priority) {
		this.priority = priority;
	}
	
	public void setNotice(Integer notice) {
		this.notice = notice;
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
	
	public String getSubject() {
		return subject;
	}
	
	public Integer getRecipient() {
		return recipient;
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
	
	public Integer getType() {
		return type;
	}
	
	public Integer getPriority() {
		return priority;
	}
	
	public Integer getNotice() {
		return notice;
	}
	

}
