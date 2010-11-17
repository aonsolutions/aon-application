package com.esferalia.aon.calendar;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.common.ITransferObject;


@Entity
@Table(name = "calendar_holiday")
public class CalendarHoliday implements ITransferObject {
	
	private static final long serialVersionUID = 1008072886778528235L;

	/** The id. */
    private Integer id;
    
    /** The description */
    private String description;

    /** The calendar. */
    private Calendar calendar;
    
    /** The date. */
    private Date date;
    
    @Id
    @GeneratedValue
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="calendar", nullable = false)
    @ForeignKey(name="FK_CALENDAR_HOLIDAY_CALENDAR")
    @Index(name="IDX_CALENDAR_HOLIDAY_CALENDAR")   
	public Calendar getCalendar() {
		return calendar;
	}

	public void setCalendar(Calendar calendar) {
		this.calendar = calendar;
	}
	
	@Temporal(TemporalType.DATE)
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

}