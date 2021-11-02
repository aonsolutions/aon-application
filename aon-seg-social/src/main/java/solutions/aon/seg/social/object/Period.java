package solutions.aon.seg.social.object;

import static java.util.Calendar.HOUR_OF_DAY;
import static java.util.Calendar.MILLISECOND;
import static java.util.Calendar.MINUTE;
import static java.util.Calendar.SECOND;

import java.util.Calendar;
import java.util.Date;

public class Period {
	private Date startDate;
	private Date endDate;
	
	private Double hours;
	private Double baseCC;
	private Double baseAT;
	private Double quoteDays;
	
	
	public Period(Date startDate, Date endDate) {
		super();
		this.startDate = startDate;
		this.endDate = endDate;
	}
	public Date getStartDate() {
		return startDate;
	}
	public Period setStartDate(Date startDate) {
		if (startDate != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(startDate);
			cal.set(HOUR_OF_DAY, 0);
			cal.set(MINUTE, 0);
			cal.set(SECOND, 0);
			cal.set(MILLISECOND, 0);
			startDate = cal.getTime();
		}
		this.startDate = startDate;
		return this;
	}
	public Date getEndDate() {
		return endDate;
	}
	public Period setEndDate(Date endDate) {
		if (endDate != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(endDate);
			cal.set(HOUR_OF_DAY, 0);
			cal.set(MINUTE, 0);
			cal.set(SECOND, 0);
			cal.set(MILLISECOND, 0);
			endDate = cal.getTime();
		}
		this.endDate = endDate;
		return this;
	}
	
	public Double getHours() {
		return hours;
	}
	
	public Period setHours(Double hours) {
		this.hours = hours;
		return this;
	}
	
	
	public Double getBaseAT() {
		return baseAT;
	}
	
	public Period setBaseAT(Double baseAT) {
		this.baseAT = baseAT;
		return this;
	}

	public Double getBaseCC() {
		return baseCC;
	}
	
	public Period setBaseCC(Double baseCC) {
		this.baseCC = baseCC;
		return this;
	}
	
	public Double getQuoteDays() {
		return quoteDays;
	}
	
	public Period setQuoteDays(Double quoteDays) {
		this.quoteDays = quoteDays;
		return this;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
		result = prime * result + ((startDate == null) ? 0 : startDate.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Period other = (Period) obj;
		if (endDate == null) {
			if (other.endDate != null)
				return false;
		} else if (!endDate.equals(other.endDate))
			return false;
		if (startDate == null) {
			if (other.startDate != null)
				return false;
		} else if (!startDate.equals(other.startDate))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "Period [startDate=" + startDate + ", endDate=" + endDate + ", hours=" + hours + ", baseCC=" + baseCC
				+ ", baseAT=" + baseAT + ", quoteDays=" + quoteDays + "]";
	}
	
	
}
