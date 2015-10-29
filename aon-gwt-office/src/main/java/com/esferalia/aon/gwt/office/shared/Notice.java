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

	public static enum Type {
		TICKET("Ticket"), AVISO("Aviso"), NOTA("Nota"), COMMENT("Comentario");

		private String description;

		static Map<Integer, String> DESCRIPTIONS = new HashMap<Integer, String>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			{
				put(0, TICKET.name());
				put(1, AVISO.name());
				put(2, NOTA.name());
				put(3, COMMENT.name());
			}
		};

		private Type(String description) {
			this.description = description;
		}

		public String getDescription(int index) {
			return DESCRIPTIONS.get(index);
		}
		
		
		public static Type valueOf(int i) {
			return Type.values()[i];
		}
	}

	private Integer id;
	private Integer domain;
	private Date date;
	private String subject;
	private Integer recipient;
	private Type type;

	private List<Notice> comments;

	public Notice() {
		comments = new LinkedList<Notice>();
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Override
	public Integer getId() {
		return this.id;
	}

	public void setDomain(Integer domain) {
		this.domain = domain;
	}

	public Integer getDomain() {
		return this.domain;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Date getDate() {
		return this.date;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getSubject() {
		return this.subject;
	}

	public void setRecipient(Integer recipient) {
		this.recipient = recipient;
	}

	public Integer getRecipient() {
		return recipient;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public Type getType() {
		return this.type;
	}

	public void addComment(Notice notice) {
		comments.add(notice);
	}

	public List<Notice> getComments() {
		return comments;
	}
}
