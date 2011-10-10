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
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.config.WorkGroup;

@Entity
@Table(name="task_holder_workgroup")
public class TaskHolderWorkgroup implements ITransferObject {

	private static final long serialVersionUID = 7804966037720748099L;

	private Integer id;
	private TaskHolder taskHolder;
	private WorkGroup workGroup;

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
	@JoinColumn(name="task_holder", nullable=false)
    @ForeignKey(name = "FK_TASK_HOLDER_WORKGROUP_TASK_HOLDER")
    @Index(name = "IDX_TASK_HOLDER_WORKGROUP_TASK_HOLDER")
	public TaskHolder getTaskHolder() {
		return taskHolder;
	}
	public void setTaskHolder(TaskHolder taskHolder) {
		this.taskHolder = taskHolder;
	}

	@ManyToOne
	@JoinColumn(name="workgroup", nullable=false)
    @ForeignKey(name = "FK_TASK_HOLDER_WORKGROUP_WORKGROUP")
    @Index(name = "IDX_TASK_HOLDER_WORKGROUP_WORKGROUP")    				
	public WorkGroup getWorkGroup() {
		return workGroup;
	}
	public void setWorkGroup(WorkGroup workGroup) {
		this.workGroup = workGroup;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final TaskHolderWorkgroup o = (TaskHolderWorkgroup) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()			
				.append(this.taskHolder, o.taskHolder)
				.append(this.workGroup, o.workGroup)				
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)				
			.append(taskHolder)
			.append(workGroup)			
			.toHashCode();
	}	

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}
	
}