package com.esferalia.aon.payroll;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.common.enumeration.Month;
import com.code.aon.common.util.CommonUtil;
import com.esferalia.aon.entity.master.FanBatchDB;

@Entity
@Table(name="fan_batch")
public class FanBatch extends FanBatchDB {
	
	private static final long serialVersionUID = 1L;
	
	@Transient
	public Integer getYear() {
		if(getDate()==null){
			setDate( new Date() );
		}
		return CommonUtil.getYear(getDate());
	}
	public void setYear(Integer year) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(getDate());
		cal.set(Calendar.YEAR, year);
		setDate(cal.getTime());
	}

	@Transient
	public Month getMonth() {
		if(getDate()==null){
			setDate( new Date() );
		}
		return Month.getMonthByValue(CommonUtil.getMonth(getDate()));
	}
	public void setMonth(Month month) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(getDate());
		cal.set(Calendar.MONTH, month.getValue());
		setDate(cal.getTime());
	}
	
}
