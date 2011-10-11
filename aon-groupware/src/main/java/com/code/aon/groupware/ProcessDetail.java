package com.code.aon.groupware;

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

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.config.WorkGroup;
import com.code.aon.groupware.enumeration.DateReference;
import com.code.aon.groupware.enumeration.Priority;

@Entity
@Table(name="process_detail")
public class ProcessDetail implements ITransferObject {

	private static final long serialVersionUID = 1030141795904320316L;

	private Integer id;
	private Process process;
	private String description;
	private int position;
	private DateReference dateReference;
	private int days;
	private int alertDays;
	private WorkGroup workgroup;
    private boolean active;
	private Priority priority;
	private String comments;

	public ProcessDetail() {
		this.active = true;
	}
	
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
	@JoinColumn( name="process",nullable=false )
	public Process getProcess() {
		return process;
	}
	public void setProcess(Process process) {
		this.process = process;
	}

	@Column(length=64, nullable=false)
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	@Column( nullable=false )
	public int getPosition() {
		return position;
	}
	public void setPosition(int position) {
		this.position = position;
	}
	
	@Column(name="date_reference")
	public DateReference getDateReference() {
		return dateReference;
	}
	public void setDateReference(DateReference dateReference) {
		this.dateReference = dateReference;
	}

	public int getDays() {
		return days;
	}
	public void setDays(int days) {
		this.days = days;
	}

	@Column(name="alert_days")
	public int getAlertDays() {
		return alertDays;
	}

	public void setAlertDays(int alertDays) {
		this.alertDays = alertDays;
	}

	@ManyToOne
	@JoinColumn( name="workgroup" )
	public WorkGroup getWorkgroup() {
		return workgroup;
	}
	public void setWorkgroup(WorkGroup workgroup) {
		this.workgroup = workgroup;
	}

	public Priority getPriority() {
		return priority;
	}
	public void setPriority(Priority priority) {
		this.priority = priority;
	}

	public boolean isActive() {
        return active;
    }
    public void setActive(boolean active) {
        this.active = active;
    }

	@Lob
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}

	@Override	
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final ProcessDetail o = (ProcessDetail) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.id, o.id)
				.append(this.process, o.process)
				.append(this.description, o.description)
				.append(this.position, o.position)
				.append(this.dateReference, o.dateReference)
				.append(this.days, o.days)
				.append(this.alertDays, o.alertDays)
				.append(this.workgroup, o.workgroup)
				.append(this.active, o.active)
				.append(this.priority, o.priority)
				.append(this.comments, o.comments)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(this.id)
			.append(this.process)
			.append(this.description)
			.append(this.position)
			.append(this.dateReference)
			.append(this.days)
			.append(this.alertDays)
			.append(this.workgroup)
			.append(this.active)
			.append(this.priority)
			.append(this.comments)
			.toHashCode();
	}

	@Override
	public String toString() {
		return PojoToStringBuilder.reflectionToString(this);
	}
}