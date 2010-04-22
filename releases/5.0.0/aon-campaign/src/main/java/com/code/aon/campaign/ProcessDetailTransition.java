package com.code.aon.campaign;

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

import com.code.aon.common.ITransferObject;

@Entity
@Table(name="process_detail_transition")
public class ProcessDetailTransition implements ITransferObject {

	private static final long serialVersionUID = 1030141795904320316L;

	private Integer id;
	
	private ProcessDetail processDetail;
	private ProcessDetail nextProcessDetail;
	private ProcessTransitionType processTransitionType;
	

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
	@JoinColumn( name="process_detail",nullable=false )
	public ProcessDetail getProcessDetail() {
		return processDetail;
	}

	public void setProcessDetail(ProcessDetail processDetail) {
		this.processDetail = processDetail;
	}

	@ManyToOne
	@JoinColumn( name="next_process_detail",nullable=false )
	public ProcessDetail getNextProcessDetail() {
		return nextProcessDetail;
	}

	public void setNextProcessDetail(ProcessDetail nextProcessDetail) {
		this.nextProcessDetail = nextProcessDetail;
	}

	@ManyToOne
	@JoinColumn( name="process_transition_type",nullable=false )
	public ProcessTransitionType getProcessTransitionType() {
		return processTransitionType;
	}

	public void setProcessTransitionType(ProcessTransitionType processTransitionType) {
		this.processTransitionType = processTransitionType;
	}

	@Override	
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final ProcessDetailTransition o = (ProcessDetailTransition) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.processDetail, o.processDetail)
				.append(this.nextProcessDetail, o.nextProcessDetail)
				.append(this.processTransitionType, o.processTransitionType)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().
			append(processDetail).append(id)
			.append(nextProcessDetail)
			.append(processTransitionType)
			.toHashCode();
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}