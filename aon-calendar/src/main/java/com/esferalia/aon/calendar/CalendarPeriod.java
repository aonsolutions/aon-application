package com.esferalia.aon.calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.common.enumeration.Month;
import com.esferalia.aon.calendar.enumeration.DayType;

@Entity
@Table(name = "calendar_period")
public class CalendarPeriod implements ITransferObject{
	
	private static final long serialVersionUID = 5550862489620324713L;

	private Integer id;
    private Calendar calendar;
    private String description;
	private Month month;
    private Integer startDay;
    private Integer endDay;
    private DayType monday;
    private DayType tuesday;
    private DayType wednesday;
    private DayType thursday;
    private DayType friday;
    private DayType saturday;
    private DayType sunday;
    private double mondayHours;
    private double tuesdayHours;
    private double wednesdayHours;
    private double thursdayHours;
    private double fridayHours;
    private double saturdayHours;
    private double sundayHours;
    
    @Id
    @GeneratedValue
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="calendar", nullable = false)
    @ForeignKey(name="FK_CALENDAR_PERIOD_CALENDAR")
    @Index(name="IDX_CALENDAR_PERIOD_CALENDAR")   
	public Calendar getCalendar() {
		return calendar;
	}

	public void setCalendar(Calendar calendar) {
		this.calendar = calendar;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
    public Month getMonth() {
		return month;
	}

	public void setMonth(Month month) {
		this.month = month;
	}

	@Column(name="start_day")
	public Integer getStartDay() {
		return startDay;
	}

	public void setStartDay(Integer startDay) {
		this.startDay = startDay;
	}

	@Column(name="end_day")
	public Integer getEndDay() {
		return endDay;
	}

	public void setEndDay(Integer endDay) {
		this.endDay = endDay;
	}

	public DayType getMonday() {
		return monday;
	}

	public void setMonday(DayType monday) {
		this.monday = monday;
	}
	@Column(name="monday_hours")
	public double getMondayHours() {
		return mondayHours;
	}

	public void setMondayHours(double mondayHours) {
		this.mondayHours = mondayHours;
	}

	public DayType getTuesday() {
		return tuesday;
	}

	public void setTuesday(DayType tuesday) {
		this.tuesday = tuesday;
	}
	@Column(name="tuesday_hours")
	public double getTuesdayHours() {
		return tuesdayHours;
	}

	public void setTuesdayHours(double tuesdayHours) {
		this.tuesdayHours = tuesdayHours;
	}

	public DayType getWednesday() {
		return wednesday;
	}

	public void setWednesday(DayType wednesday) {
		this.wednesday = wednesday;
	}
	@Column(name="wednesday_hours")
	public double getWednesdayHours() {
		return wednesdayHours;
	}

	public void setWednesdayHours(double wednesdayHours) {
		this.wednesdayHours = wednesdayHours;
	}

	public DayType getThursday() {
		return thursday;
	}

	public void setThursday(DayType thursday) {
		this.thursday = thursday;
	}
	
	@Column(name="thursday_hours")
	public double getThursdayHours() {
		return thursdayHours;
	}

	public void setThursdayHours(double thursdayHours) {
		this.thursdayHours = thursdayHours;
	}

	public DayType getFriday() {
		return friday;
	}

	public void setFriday(DayType friday) {
		this.friday = friday;
	}
	@Column(name="friday_hours")
	public double getFridayHours() {
		return fridayHours;
	}

	public void setFridayHours(double fridayHours) {
		this.fridayHours = fridayHours;
	}

	public DayType getSaturday() {
		return saturday;
	}

	public void setSaturday(DayType saturday) {
		this.saturday = saturday;
	}
	@Column(name="saturday_hours")
	public double getSaturdayHours() {
		return saturdayHours;
	}

	public void setSaturdayHours(double saturdayHours) {
		this.saturdayHours = saturdayHours;
	}

	public DayType getSunday() {
		return sunday;
	}

	public void setSunday(DayType sunday) {
		this.sunday = sunday;
	}
	@Column(name="sunday_hours")
	public double getSundayHours() {
		return sundayHours;
	}

	public void setSundayHours(double sundayHours) {
		this.sundayHours = sundayHours;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final CalendarPeriod o = (CalendarPeriod) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.calendar, o.calendar)
				.append(this.description, o.description)
				.append(this.month, o.month)
				.append(this.startDay, o.startDay)
				.append(this.endDay, o.endDay)
				.append(this.monday, o.monday)
				.append(this.tuesday, o.tuesday)
				.append(this.wednesday, o.wednesday)
				.append(this.thursday, o.thursday)
				.append(this.friday, o.friday)
				.append(this.saturday, o.saturday)
				.append(this.sunday, o.sunday)
				.append(this.mondayHours, o.mondayHours)
				.append(this.tuesdayHours, o.tuesdayHours)
				.append(this.wednesdayHours, o.wednesdayHours)
				.append(this.thursdayHours, o.thursdayHours)
				.append(this.fridayHours, o.fridayHours)
				.append(this.saturdayHours, o.saturdayHours)
				.append(this.sundayHours, o.sundayHours)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)
			.append(calendar)
			.append(description)
			.append(month)
			.append(startDay)
			.append(endDay)
			.append(monday)
			.append(tuesday)
			.append(wednesday)
			.append(thursday)
			.append(friday)
			.append(saturday)
			.append(sunday)
			.append(mondayHours)
			.append(tuesdayHours)
			.append(wednesdayHours)
			.append(thursdayHours)
			.append(fridayHours)
			.append(saturdayHours)
			.append(sundayHours)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

}