package com.esferalia.aon.gwt.payroll.shared;

import static com.esferalia.aon.gwt.payroll.shared.Shared.format;
import static com.esferalia.aon.gwt.payroll.shared.Shared.parse;

import java.io.Serializable;
import java.util.Date;

public class ContractSalaryInfo implements Serializable {
	private String type;
	private String start;
	private String end;
	private Double totalLiquid;
	
	public ContractSalaryInfo() {
		super();
	}

	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Date getStart() {
		return Shared.parse(start);
	}
	public void setStart(Date start) {
		this.start = format(start);
	}
	public Date getEnd() {
		return parse(end);
	}
	public void setEnd(Date end) {
		this.end = format(end);
	}
	public Double getTotalLiquid() {
		return totalLiquid;
	}
	public void setTotalLiquid(Double totalLiquid) {
		this.totalLiquid = totalLiquid;
	}
	
}