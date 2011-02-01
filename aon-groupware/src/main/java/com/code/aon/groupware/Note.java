package com.code.aon.groupware;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Type;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.config.User;

@Entity
@Table(name="note")
public class Note implements ITransferObject {

	private static final long serialVersionUID = -4521279825663201587L;

	private Integer id;
	
    private String subject;
    
    private Date date;
    
	private User owner;
	
	private String note;
	
	
	@Id
	@GeneratedValue
	@Column(nullable=false)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

    @Column(length=64, nullable=false)
    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    @Column(name="date", nullable=false)
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@ManyToOne
	@JoinColumn(name="owner")
	@Fetch(FetchMode.JOIN)
	@ForeignKey(name = "FK_NOTE_OWNER")
	@Index(name = "IDX_NOTE_OWNER")				
	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	@Lob
	@Type(type="stringClob")
	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final Note o = (Note) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.date, o.date)
				.append(this.note, o.note)
				.append(this.owner, o.owner)
				.append(this.subject, o.subject)				
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().
			append(date).append(id).
			append(note).append(owner).
			append(subject).
			toHashCode();
	}
	
	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}
	
}