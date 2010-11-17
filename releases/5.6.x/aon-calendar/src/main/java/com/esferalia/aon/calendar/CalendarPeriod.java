package com.esferalia.aon.calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.common.ITransferObject;
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
    
    private double mondayHours;
    
    private DayType tuesday;
    
    private double tuesdayHours;
    
    private DayType wednesday;
    
    private double wednesdayHours;
    
    private DayType thursday;
    
    private double thursdayHours;
    
    private DayType friday;
    
    private double fridayHours;
    
    private DayType saturday;
    
    private double saturdayHours;
    
    private DayType sunday;
    
    private double sundayHours;
    
//    private Date fromDate;
//    
//    private Date toDate;   
    
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
	
//	@Column(name="from_date")
//	@Temporal(TemporalType.DATE)
//	public Date getFromDate() {
//		return fromDate;
//	}
//
//	public void setFromDate(Date fromDate) {
//		this.fromDate = fromDate;
//	}
//	
//	@Column(name="to_date")
//	@Temporal(TemporalType.DATE)
//	public Date getToDate() {
//		return toDate;
//	}
//
//	public void setToDate(Date toDate) {
//		this.toDate = toDate;
//	}
	
	

}