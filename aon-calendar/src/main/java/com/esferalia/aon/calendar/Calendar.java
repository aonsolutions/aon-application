package com.esferalia.aon.calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.esferalia.aon.calendar.enumeration.DayType;

@Entity
@Table(name = "calendar")
public class Calendar implements ITransferObject{
	
	private static final long serialVersionUID = -3190986972412754073L;

	private Integer id;
	
	private Calendar calendar;
	
	private boolean generic;

	private Holiday holiday;
	
    private String description;
    
    private String comments;
    
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
    
    private double anualHours;    
    
    public Calendar() {
    	setAnualHours(0);
		setMonday(DayType.WORKING_DAY);
		setMondayHours(8);
		setTuesday(DayType.WORKING_DAY);
		setTuesdayHours(8);
		setWednesday(DayType.WORKING_DAY);
		setWednesdayHours(8);
		setThursday(DayType.WORKING_DAY);
		setThursdayHours(8);
		setFriday(DayType.WORKING_DAY);
		setFridayHours(8);
		setSaturday(DayType.NOT_WORKING_DAY);
		setSaturdayHours(0);
		setSunday(DayType.NOT_WORKING_DAY);
		setSundayHours(0);
	}
    
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@ManyToOne
    @JoinColumn(name="calendar", nullable = true)
    @ForeignKey(name="FK_CALENDAR_CALENDAR")
    @Index(name="IDX_CALENDAR_CALENDAR")
	public Calendar getCalendar() {
		return calendar;
	}

	public void setCalendar(Calendar calendar) {
		this.calendar = calendar;
	}
	
	public boolean isGeneric() {
		return generic;
	}

	public void setGeneric(boolean generic) {
		this.generic = generic;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn( name="holiday")	
	@ForeignKey(name = "FK_CALENDAR_HOLIDAY")
	@Index(name = "IDX_CALENDAR_HOLIDAY")
	public Holiday getHoliday() {
		return holiday;
	}
	
	public void setHoliday(Holiday holiday) {
		this.holiday = holiday;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public DayType getMonday() {
		return monday;
	}
	
	@Column(name="comments")
	@Lob
	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
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
	@Column(name="anual_hours")
	public double getAnualHours() {
		return anualHours;
	}

	public void setAnualHours(double anualHours) {
		this.anualHours = anualHours;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final Calendar o = (Calendar) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.description, o.description)	
				.append(this.calendar, o.calendar)
				.append(this.generic, o.generic)
				.append(this.holiday, o.holiday)
				.append(this.description, o.description)
				.append(this.comments, o.comments)
				.append(this.monday, o.monday)
				.append(this.mondayHours, o.mondayHours)
				.append(this.tuesday, o.tuesday)
				.append(this.tuesdayHours, o.tuesdayHours)
				.append(this.wednesday, o.wednesday)
				.append(this.wednesdayHours, o.wednesdayHours)
				.append(this.thursday, o.thursday)
				.append(this.thursdayHours, o.thursdayHours)
				.append(this.friday, o.friday)
				.append(this.fridayHours, o.fridayHours)
				.append(this.saturday, o.saturday)
				.append(this.saturdayHours, o.saturdayHours)
				.append(this.sunday, o.sunday)
				.append(this.sundayHours, o.sundayHours)
				.append(this.anualHours, o.anualHours)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)
			.append(holiday)
			.append(calendar)
			.append(generic)
			.append(description)
			.append(comments)
			.append(monday)
			.append(mondayHours)
			.append(tuesday)
			.append(tuesdayHours)
			.append(wednesday)
			.append(wednesdayHours)
			.append(thursday)
			.append(thursdayHours)
			.append(friday)
			.append(fridayHours)
			.append(saturday)
			.append(saturdayHours)
			.append(sunday)
			.append(sundayHours)
			.append(anualHours)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}
	
	
}