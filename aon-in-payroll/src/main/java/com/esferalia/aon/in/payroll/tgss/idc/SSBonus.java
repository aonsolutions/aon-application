package com.esferalia.aon.in.payroll.tgss.idc;

import java.util.Date;

public class SSBonus {

	private String ssNum;
	private String ccc;
	private Date startDate;
	private Date endDate;
	private String description;
	private Byte type; // Bonus.Type.values()
	private String formula;

	public SSBonus() {
		super();
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Byte getType() {
		return type;
	}

	public void setType(Byte type) {
		this.type = type;
	}

	public String getFormula() {
		return formula;
	}

	public void setFormula(String formula) {
		this.formula = formula;
	}

	public String getSsNum() {
		return ssNum;
	}

	public void setNss(String ssNum) {
		this.ssNum = ssNum;
	}

	public String getCcc() {
		return ccc;
	}

	public void setCcc(String ccc) {
		this.ccc = ccc;
	}

	@Override
	public String toString() {
		return "SSBonus -> SS Number : " + getSsNum() + ", CCC : " + getCcc() + ", Description : " + getDescription() + ", Formula : " + getFormula() + ", Start : "
				+ getStartDate() + ", End : " + getEndDate();
	}
}