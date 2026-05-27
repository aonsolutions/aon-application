package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.Date;

public class CostParams implements Serializable {

	private static final long serialVersionUID = 6015720258657135544L;

	private Date start;
	private Date end;
	
	private Integer enterprise;
	private Integer workplace;
	
	private boolean salary;
	private boolean extra;
	private boolean settle;
	private boolean procedural;
	private boolean delay;
	private boolean l00;
	private boolean l02;
	private boolean l03;
	private boolean l13;
	
	
	private boolean groupByWorkplace = false;
	
	public CostParams() {
		super();
	}

	public Date getStart() {
		return start;
	}

	public CostParams setStart(Date start) {
		this.start = start;
		return this;
	}

	public Date getEnd() {
		return end;
	}

	public CostParams setEnd(Date end) {
		this.end = end;
		return this;
	}
	
	public Integer getEnterprise() {
		return enterprise;
	}

	public CostParams setEnterprise(Integer enterprise) {
		this.enterprise = enterprise;
		return this;
	}

	public Integer getWorkplace() {
		return workplace;
	}

	public CostParams setWorkplace(Integer workplace) {
		this.workplace = workplace;
		return this;
	}

	public boolean isSalary() {
		return salary;
	}

	public CostParams setSalary(boolean salary) {
		this.salary = salary;
		return this;
	}

	public boolean isExtra() {
		return extra;
	}

	public CostParams setExtra(boolean extra) {
		this.extra = extra;
		return this;
	}

	public boolean isSettle() {
		return settle;
	}

	public CostParams setSettle(boolean settle) {
		this.settle = settle;
		return this;
	}

	public boolean isDelay() {
		return delay;
	}

	public CostParams setDelay(boolean delay) {
		this.delay = delay;
		return this;
	}
	
	public boolean isProcedural() {
		return procedural;
	}
	
	public CostParams setProcedural(boolean procedural) {
		this.procedural = procedural;
		return this;
	}

	public boolean isL00() {
		return l00;
	}

	public CostParams setL00(boolean l00) {
		this.l00 = l00;
		return this;
	}
	
	public boolean isL02() {
		return l02;
	}

	public CostParams setL02(boolean l02) {
		this.l02 = l02;
		return this;
	}
	
	public boolean isL03() {
		return l03;
	}

	public CostParams setL03(boolean l03) {
		this.l03 = l03;
		return this;
	}

	public boolean isL13() {
		return l13;
	}

	public CostParams setL13(boolean l13) {
		this.l13 = l13;
		return this;
	}

	public boolean isGroupByWorkplace() {
		return groupByWorkplace;
	}

	public CostParams setGroupByWorkplace(boolean groupByWorkplace) {
		this.groupByWorkplace = groupByWorkplace;
		return this;
	}
	
}
