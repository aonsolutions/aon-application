package com.esferalia.aon.occam.api.model.activity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class ActivitySummaryObject implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8329371157680338499L;

	private Integer id;
	private String name;
	private String nameUrl;
	private String firstSurname;
	private String secondSurname;

	private String startDate;
	private String endDate;
	private Integer startCount;
	private Integer endCount;

	private Integer salaryCount;
	private Integer salaryExtraCount;
	private Integer salarySettleCount;
	private Integer salaryOtherCount;

	private Integer itCommonDiseaseCount;
	private Integer itOccupationalDiseaseCount;
	private Integer itMaternityCount;
	private Integer itOtherCount;

	private List<ActivitySummaryObject> childs;

	public ActivitySummaryObject() {
		super();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNameUrl() {
		return nameUrl;
	}

	public void setNameUrl(String nameUrl) {
		this.nameUrl = nameUrl;
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
		return parseDate(startDate);
	}

	public void setStartDate(Date startDate) {
		this.startDate = formatDate(startDate);
	}

	public Date getEndDate() {
		return parseDate(endDate);
	}

	public void setEndDate(Date endDate) {
		this.endDate = formatDate(endDate);
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

	public Integer getSalarySettleCount() {
		return salarySettleCount;
	}

	public void setSalarySettleCount(Integer salarySettleCount) {
		this.salarySettleCount = salarySettleCount;
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

	public List<ActivitySummaryObject> getChilds() {
		return childs;
	}

	public void setChilds(List<ActivitySummaryObject> childs) {
		this.childs = childs;
	}

	private String formatDate(Date date) {
		if (date == null)
			return null;

		int day = date.getDate();
		int month = date.getMonth() + 1;
		int year = date.getYear() + 1900;

		return pad2(day) + "/" + pad2(month) + "/" + year;
	}

	private String pad2(int n) {
		return (n < 10 ? "0" : "") + n;
	}

	private Date parseDate(String value) {
		if (value == null || value.isEmpty())
			return null;

		try {
			String[] parts = value.split("/");
			int day = Integer.parseInt(parts[0]);
			int month = Integer.parseInt(parts[1]) - 1; // Date espera 0-based
			int year = Integer.parseInt(parts[2]) - 1900;

			return new Date(year, month, day);
		} catch (Exception e) {
			return null;
		}
	}
}
