package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.Date;

public class ActivitySummaryParams implements Serializable {

	private static final long serialVersionUID = 6015720258657135544L;

	private Integer childomain;
	
	private String description;
	
	private Date start;
	private Date end;
	
	private boolean startContract;
	private boolean endContract;
	
	private boolean salary;
	private boolean extra;
	private boolean settle;
	private boolean delay;
	
	private boolean itCD;
	private boolean itOD;
	private boolean itMP;
	private boolean itOT;
	
	private int limit;
	private int offset;
	
	private String orderBy;
	private boolean asc = true;
	
	private boolean isOffice = false;
	
	public ActivitySummaryParams() {
		super();
	}

	public String getDescription() {
		return description;
	}

	public ActivitySummaryParams setDescription(String description) {
		this.description = description;
		return this;
	}

	public Date getStart() {
		return start;
	}

	public ActivitySummaryParams setStart(Date start) {
		this.start = start;
		return this;
	}

	public Date getEnd() {
		return end;
	}

	public ActivitySummaryParams setEnd(Date end) {
		this.end = end;
		return this;
	}

	public Integer getChildomain() {
		return childomain;
	}

	public ActivitySummaryParams setChildomain(Integer childomain) {
		this.childomain = childomain;
		return this;
	}

	public boolean isStartContract() {
		return startContract;
	}

	public ActivitySummaryParams setStartContract(boolean startContract) {
		this.startContract = startContract;
		return this;
	}

	public boolean isEndContract() {
		return endContract;
	}

	public ActivitySummaryParams setEndContract(boolean endContract) {
		this.endContract = endContract;
		return this;
	}

	public boolean isSalary() {
		return salary;
	}

	public ActivitySummaryParams setSalary(boolean salary) {
		this.salary = salary;
		return this;
	}

	public boolean isExtra() {
		return extra;
	}

	public ActivitySummaryParams setExtra(boolean extra) {
		this.extra = extra;
		return this;
	}

	public boolean isSettle() {
		return settle;
	}

	public ActivitySummaryParams setSettle(boolean settle) {
		this.settle = settle;
		return this;
	}

	public boolean isDelay() {
		return delay;
	}

	public ActivitySummaryParams setDelay(boolean delay) {
		this.delay = delay;
		return this;
	}

	public boolean isItCD() {
		return itCD;
	}

	public ActivitySummaryParams setItCD(boolean itCD) {
		this.itCD = itCD;
		return this;
	}

	public boolean isItOD() {
		return itOD;
	}

	public ActivitySummaryParams setItOD(boolean itOD) {
		this.itOD = itOD;
		return this;
	}

	public boolean isItMP() {
		return itMP;
	}

	public ActivitySummaryParams setItMP(boolean itMP) {
		this.itMP = itMP;
		return this;
	}

	public boolean isItOT() {
		return itOT;
	}

	public ActivitySummaryParams setItOT(boolean itOT) {
		this.itOT = itOT;
		return this;
	}

	public int getLimit() {
		return limit;
	}
	
	public ActivitySummaryParams setLimit(int limit) {
		this.limit = limit;
		return this;
	}
	
	public int getOffset() {
		return offset;
	}
	
	public ActivitySummaryParams setOffset(int offset) {
		this.offset = offset;
		return this;
	}
	
	public String getOrderBy() {
		return orderBy;
	}
	
	public ActivitySummaryParams setOrderBy(String orderBy) {
		this.orderBy = orderBy;
		return this;
	}
	
	public boolean isAsc() {
		return asc;
	}
	
	public ActivitySummaryParams setAsc(boolean asc) {
		this.asc = asc;
		return this;
	}

	public boolean isOffice() {
		return isOffice;
	}

	public ActivitySummaryParams setOffice(boolean isOffice) {
		this.isOffice = isOffice;
		return this;
	}
	
	
}
