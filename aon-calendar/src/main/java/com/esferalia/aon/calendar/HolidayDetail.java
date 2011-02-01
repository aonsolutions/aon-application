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
@Table(name = "holiday_detail")
public class HolidayDetail implements ITransferObject{
		
	private static final long serialVersionUID = -7740963277074268015L;

	private Integer id;
	
	private Holiday holiday;
	
	private Date date;
		
	private String description;
       
    @Id
    @GeneratedValue
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="holiday", nullable = false)
    @ForeignKey(name="FK_HOLIDAY_DETAIL_HOLIDAY")
    @Index(name="IDX_HOLIDAY_DETAIL_HOLIDAY")   
	public Holiday getHoliday() {
		return holiday;
	}

	public void setHoliday(Holiday holiday) {
		this.holiday = holiday;
	}

	@Temporal(TemporalType.DATE)
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
    
}