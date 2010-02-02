package com.code.aon.campaign;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.code.aon.common.ITransferObject;
import com.code.aon.project.Activity;
import com.code.aon.project.Task;

@Entity
@Table(name="activity_process")
public class ActivityProcess implements ITransferObject {

	private static final long serialVersionUID = 5454224486922816197L;

	private Integer id;
	
	private Campaign campaign;
	
	private Activity activity;
	
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
	@JoinColumn(name="campaign", nullable=false)
	public Campaign getCampaign() {
		return campaign;
	}

	public void setCampaign(Campaign campaign) {
		this.campaign = campaign;
	}

	@ManyToOne
	@JoinColumn(name="activity")
	public Activity getActivity() {
		return activity;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}

	@ManyToOne
	@JoinColumn(name="process_detail", nullable=false)
	public ProcessDetail getProcessDetail() {
		return processDetail;
	}

	public void setProcessDetail(ProcessDetail processDetail) {
		this.processDetail = processDetail;
	}

	@ManyToOne
	@JoinColumn(name="task", nullable=false)
	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}
}