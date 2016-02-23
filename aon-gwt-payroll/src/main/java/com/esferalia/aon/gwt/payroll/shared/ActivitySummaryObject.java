package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.Date;


public class ActivitySummaryObject implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8329371157680338499L;

	private String name;
	private String firstSurname;
	private String secondSurname;

	private Date startDate;
	private Date endDate;
	private Integer startCount;
	private Integer endCount;
	
	private Integer salaryCount;
	private Integer salaryExtraCount;
	private Integer salaryOtherCount;

	private Integer itCommonDiseaseCount;
	private Integer itOccupationalDiseaseCount;
	private Integer itMaternityCount;
	private Integer itOtherCount;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getFirstSurname() {
		return firstSurname;
	}
	public void setFirstSurname(String firstSurname) {
		this.firstSurname = firstSurname;
	}
	public String getSecondSurname() {
		return secondSurname;
	}
	public void setSecondSurname(String secondSurname) {
		this.secondSurname = secondSurname;
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
	public Integer getStartCount() {
		return startCount;
	}
	public void setStartCount(Integer startCount) {
		this.startCount = startCount;
	}
	public Integer getEndCount() {
		return endCount;
	}
	public Integer getSalaryCount() {
		return salaryCount;
	}
	public Integer getSalaryExtraCount() {
		return salaryExtraCount;
	}
	public Integer getSalaryOtherCount() {
		return salaryOtherCount;
	}
	public void setEndCount(Integer endCount) {
		this.endCount = endCount;
	}
	public void setSalaryCount(Integer salaryCount) {
		this.salaryCount = salaryCount;
	}
	public void setSalaryExtraCount(Integer salaryExtraCount) {
		this.salaryExtraCount = salaryExtraCount;
	}
	public void setSalaryOtherCount(Integer salaryOtherCount) {
		this.salaryOtherCount = salaryOtherCount;
	}
	public String getFullname() {
		StringBuffer sb = new StringBuffer();

		if (firstSurname != null) {
			sb.append(firstSurname.trim());
		}
		if (secondSurname != null) {
			if (sb.length() > 0) {
				sb.append(" ");
			}
			sb.append(secondSurname.trim());
		}
		if (sb.length() > 0) {
			sb.append(", ");
		}
		sb.append(getName());

		return sb.toString();
	}
	public Integer getItCommonDiseaseCount() {
		return itCommonDiseaseCount;
	}
	public void setItCommonDiseaseCount(Integer itCommonDiseaseCount) {
		this.itCommonDiseaseCount = itCommonDiseaseCount;
	}
	public Integer getItOccupationalDiseaseCount() {
		return itOccupationalDiseaseCount;
	}
	public void setItOccupationalDiseaseCount(Integer itOccupationalDiseaseCount) {
		this.itOccupationalDiseaseCount = itOccupationalDiseaseCount;
	}
	public Integer getItMaternityCount() {
		return itMaternityCount;
	}
	public void setItMaternityCount(Integer itMaternityCount) {
		this.itMaternityCount = itMaternityCount;
	}
	public Integer getItOtherCount() {
		return itOtherCount;
	}
	public void setItOtherCount(Integer itOtherCount) {
		this.itOtherCount = itOtherCount;
	}
	
	
	
}
