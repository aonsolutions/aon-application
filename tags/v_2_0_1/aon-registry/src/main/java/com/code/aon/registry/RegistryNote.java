package com.code.aon.registry;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.common.ITransferObject;
import com.code.aon.registry.enumeration.NoteType;

@Entity
@Table(name="rnote")
public class RegistryNote implements ITransferObject {
	
	private Integer id;
	
	private Registry registry;
	
	private String description;
	
	private Date noteDate;
	
	private String comments;
	
	private NoteType notetype;

	@Id
	@GeneratedValue
	@Column(nullable=false)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@ManyToOne
	@JoinColumn(name="registry", nullable=false)
	public Registry getRegistry() {
		return registry;
	}

	public void setRegistry(Registry registry) {
		this.registry = registry;
	}

	@Column(length=64, nullable=false)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name="note_date")
	public Date getNoteDate() {
		return noteDate;
	}

	public void setNoteDate(Date noteDate) {
		this.noteDate = noteDate;
	}

	@Column(length=65535, nullable=false)
	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	/**
	 * @return the notetype
	 */
    @Column(name="note_type")
	public NoteType getNotetype() {
		return notetype;
	}

	/**
	 * @param notetype the notetype to set
	 */
	public void setNotetype(NoteType notetype) {
		this.notetype = notetype;
	}
	
	@Transient
	public String getShortComments() {
		if (comments != null &&
				comments.length()>SHORT_DESC_LENGTH)
			return comments.substring(0,SHORT_DESC_LENGTH)+"...";
		return comments;
	}

	private int SHORT_DESC_LENGTH = 45; 

	
}