package com.esferalia.aon.calendar;

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

@Entity
@Table(name = "holiday")
public class Holiday implements ITransferObject{
	
	private static final long serialVersionUID = -3934004755428661093L;

	private Integer id;
	
	private Holiday holiday;

    private String description;
    
    private boolean editable;
    
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
    @ForeignKey(name="FK_HOLIDAY_HOLIDAY")
    @Index(name="IDX_HOLIDAY_HOLIDAY")  
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

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}
	
}