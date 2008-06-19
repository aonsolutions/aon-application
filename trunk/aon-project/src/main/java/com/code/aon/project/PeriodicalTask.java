package com.code.aon.project;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.annotations.AonPOJOInitializationInvalidateRestoreNull;
import com.code.aon.config.User;
import com.code.aon.project.enumeration.TaskPeriod;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="periodical_task")
public class PeriodicalTask implements ITransferObject {
	
	private static final long serialVersionUID = -1505827359603532393L;

	private Integer id;
	
	private Date startDate;
	
	private Date endDate;
	
	private Date nextDate;
	
	private TaskPeriod period;
	
	private int quantity;
	
	private Task task;
	
	private User owner;

	@Id
	@GeneratedValue
	@Column(nullable=false)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name="start_date", nullable=false)
	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	@Column(name="end_date")
	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	@Column(name="next_date")
	public Date getNextDate() {
		return nextDate;
	}

	public void setNextDate(Date nextDate) {
		this.nextDate = nextDate;
	}

	@Column(nullable=false)
	public TaskPeriod getPeriod() {
		return period;
	}

	public void setPeriod(TaskPeriod period) {
		this.period = period;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	@ManyToOne
	@JoinColumn(name="task", nullable=false)
	@AonPOJOInitializationInvalidateRestoreNull
	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	@ManyToOne
	@JoinColumn(name="owner", nullable=false)
	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}
}