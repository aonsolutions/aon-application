package com.code.aon.registry;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Type;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.registry.enumeration.NoteType;

@Entity
@Table(name="rnote")
public class RegistryNote implements ITransferObject {
	
	private static final long serialVersionUID = 1710105082036901638L;

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
	@ForeignKey(name = "FK_RNOTE_REGISTRY")
	@Index(name = "IDX_RNOTE_REGISTRY")
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
	@Temporal(TemporalType.DATE)
	public Date getNoteDate() {
		return noteDate;
	}

	public void setNoteDate(Date noteDate) {
		this.noteDate = noteDate;
	}

	@Lob
	@Type(type="stringClob")
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
	
	private int SHORT_DESC_LENGTH = 45; 

	@Transient
	public String getShortComments() {
		if (comments != null && comments.length()>SHORT_DESC_LENGTH)
			return comments.substring(0,SHORT_DESC_LENGTH)+"...";
		return comments;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final RegistryNote o = (RegistryNote) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.comments, o.comments)
				.append(this.description, o.description)				
				.append(this.noteDate, o.noteDate)
				.append(this.notetype, o.notetype)
				.append(this.registry, o.registry)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(comments)
			.append(description)
			.append(id)
			.append(noteDate)
			.append(notetype)	
			.append(registry)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	} 

}