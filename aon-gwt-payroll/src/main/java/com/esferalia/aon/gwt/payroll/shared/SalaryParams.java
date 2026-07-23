package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.Date;

public class SalaryParams implements Serializable {

	private static final long serialVersionUID = 6015720258657135544L;

	private String description;
	private Date start;
	private Date end;
	
	private Integer workplace;
	private Integer contract;
	
	private boolean salary;
	private boolean extra;
	private boolean settle;
	private boolean delay;
	private boolean procedural;
	private boolean liquidations;
	
	private int limit;
	private int offset;
	
	private String orderBy;
	private boolean asc = true;
	
	public SalaryParams() {
		super();
	}

	public String getDescription() {
		return description;
	}

	public SalaryParams setDescription(String description) {
		this.description = description;
		return this;
	}

	public Date getStart() {
		return start;
	}

	public SalaryParams setStart(Date start) {
		this.start = start;
		return this;
	}

	public Date getEnd() {
		return end;
	}

	public SalaryParams setEnd(Date end) {
		this.end = end;
		return this;
	}
	
	public Integer getWorkplace() {
		return workplace;
	}

	public SalaryParams setWorkplace(Integer workplace) {
		this.workplace = workplace;
		return this;
	}

	public Integer getContract() {
		return contract;
	}

	public SalaryParams setContract(Integer contract) {
		this.contract = contract;
		return this;
	}

	public boolean isSalary() {
		return salary;
	}

	public SalaryParams setSalary(boolean salary) {
		this.salary = salary;
		return this;
	}

	public boolean isExtra() {
		return extra;
	}

	public SalaryParams setExtra(boolean extra) {
		this.extra = extra;
		return this;
	}

	public boolean isSettle() {
		return settle;
	}

	public SalaryParams setSettle(boolean settle) {
		this.settle = settle;
		return this;
	}

	public boolean isDelay() {
		return delay;
	}

	public SalaryParams setDelay(boolean delay) {
		this.delay = delay;
		return this;
	}

	public boolean isProcedural() {
		return procedural;
	}

	public SalaryParams setProcedural(boolean procedural) {
		this.procedural = procedural;
		return this;
	}
	
	public boolean isLiquidations() {
		return liquidations;
	}
	
	public SalaryParams setLiquidations(boolean liquidations) {
		this.liquidations = liquidations;
		return this;
	}

	public int getLimit() {
		return limit;
	}
	
	public SalaryParams setLimit(int limit) {
		this.limit = limit;
		return this;
	}
	
	public int getOffset() {
		return offset;
	}
	
	public SalaryParams setOffset(int offset) {
		this.offset = offset;
		return this;
	}
	
	public String getOrderBy() {
		return orderBy;
	}
	
	public SalaryParams setOrderBy(String orderBy) {
		this.orderBy = orderBy;
		return this;
	}
	
	public boolean isAsc() {
		return asc;
	}
	
	public SalaryParams setAsc(boolean asc) {
		this.asc = asc;
		return this;
	}
}
