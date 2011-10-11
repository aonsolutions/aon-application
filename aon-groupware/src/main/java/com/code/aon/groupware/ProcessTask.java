package com.code.aon.groupware;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.common.ITransferObject;

@Entity
@Table(name="process_task")
public class ProcessTask implements ITransferObject {

	private static final long serialVersionUID = 5454224486922816197L;

	private Integer id;
	private Campaign campaign;
	private ProcessDetail processDetail;
	private Task task;

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
	@JoinColumn(name="campaign")
    @ForeignKey(name = "FK_PROCESS_TASK_CAMPAIGN")
    @Index(name = "IDX_PROCESS_TASK_CAMPAIGN")
	public Campaign getCampaign() {
		return campaign;
	}
	public void setCampaign(Campaign campaign) {
		this.campaign = campaign;
	}

	@ManyToOne
	@JoinColumn(name="process_detail", nullable=false)
    @ForeignKey(name = "FK_PROCESS_TASK_PD")
    @Index(name = "IDX_PROCESS_TASK_PD")
	public ProcessDetail getProcessDetail() {
		return processDetail;
	}
	public void setProcessDetail(ProcessDetail processDetail) {
		this.processDetail = processDetail;
	}

	@ManyToOne
	@JoinColumn(name="task", nullable=false)
    @ForeignKey(name = "FK_PROCESS_TASK_TASK")
    @Index(name = "IDX_PROCESS_TASK_TASK")
	public Task getTask() {
		return task;
	}
	public void setTask(Task task) {
		this.task = task;
	}

	@Override	
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final ProcessTask o = (ProcessTask) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.id, o.id)
				.append(this.campaign, o.campaign)
				.append(this.processDetail, o.processDetail)
				.append(this.task, o.task)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(this.id)
			.append(this.campaign)
			.append(this.processDetail)
			.append(this.task)
			.toHashCode();
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}