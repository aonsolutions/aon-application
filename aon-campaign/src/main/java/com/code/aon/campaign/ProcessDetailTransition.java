package com.code.aon.campaign;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.code.aon.campaign.enumeration.DateReference;
import com.code.aon.common.ITransferObject;
import com.code.aon.config.WorkGroup;

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

}