package com.esferalia.aon.payroll;


import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import org.apache.commons.lang.time.DateUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.annotations.Heritable;
import com.code.aon.common.enumeration.Month;
import com.esferalia.aon.entity.master.AgreementExtraDB;
import com.esferalia.aon.payroll.calculator.sql.SQLContractExtraCalculatorContext.DateFormatException;

@Entity
@Table(name="agreement_extra")
@Heritable
public class AgreementExtra extends AgreementExtraDB  {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private static final Pattern AGREEMENT_DATE_PATTERN = 
		Pattern.compile("(\\d+)/(\\d+)\\s*\\+?([-]?\\d+)?");

	public static Date parseAgreementDate ( String string, int year ) {
		
		Matcher matcher = AGREEMENT_DATE_PATTERN.matcher(string);
		
		if ( !matcher.find() ) {
			throw new  DateFormatException(string);
		}

		String days = matcher.group(1);
		String month = matcher.group(2);
		String yearOffset = matcher.group(3);
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(days));
		calendar.set(Calendar.MONTH, Integer.parseInt(month)-1);
		if ( yearOffset != null ) {
			year += Integer.parseInt(yearOffset);
		}
		calendar.set(Calendar.YEAR,  year );

		return DateUtils.truncate(calendar, Calendar.DAY_OF_MONTH).getTime();
	}

	public Date getStartDate(int year ) {
		return parseAgreementDate(getStartDate(), year);
	}
	public Date getEndDate(int year ) {
		return parseAgreementDate(getEndDate(), year);
	}
	public Date getIssueDate(int year ) {
		return parseAgreementDate(getIssueDate(), year);
	}
	
	@Transient
	public Month getStartDateMonth(){
		return Month.getMonthByValue(getMonth(getStartDate())!=null?getMonth(getStartDate()):0);
	}
	public void setStartDateMonth(Month startDateMonth){
		setStartDate( setMonth(getStartDate(), startDateMonth, "01"));
	}
	@Transient
	public boolean getStartDatePrevious(){
		return getPrevious(getStartDate());
	}
	public void setStartDatePrevious(boolean startDatePrevious){
		setStartDate( setPrevious(getStartDate(), startDatePrevious));
	}
	@Transient
	public Month getEndDateMonth(){
		return Month.getMonthByValue(getMonth(getEndDate())!=null?getMonth(getEndDate()):0);
	}
	public void setEndDateMonth(Month endDateMonth){
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, endDateMonth.ordinal());
		setEndDate( setMonth(getEndDate(), endDateMonth, String.valueOf(cal.getActualMaximum(Calendar.DAY_OF_MONTH))));
	}
	@Transient
	public boolean getEndDatePrevious(){
		return getPrevious(getEndDate());
	}
	public void setEndDatePrevious(boolean endDatePrevious){
		setEndDate( setPrevious(getEndDate(), endDatePrevious));
	}
	@Transient
	public Month getIssueDateMonth() {
		return Month.getMonthByValue(getMonth(getIssueDate())!=null?getMonth(getIssueDate()):0);
	}
	public void setIssueDateMonth(Month issueDateMonth) {
		if( issueDateMonth != null ){
			String m = (new Integer(issueDateMonth.ordinal()+1)).toString();
			setIssueDate( (getIssueDate()==null?"01":getIssueDate().substring(0, 2))+"/"+(m.length()==1?"0"+m:m));
		}
	}
	@Transient
	public Integer getIssueDateDay() {
		return getDay(getIssueDate());
	}
	public void setIssueDateDay(Integer issueDateDay) {
		setIssueDate( setDay(getIssueDate(), issueDateDay.toString()));
	}
	
	
	private Integer getMonth(String date){
		if(date!=null && date.length()>4){
			return Integer.parseInt(date.substring(3, 5))-1;
		}
		return null;
	}
	private String setMonth(String date, Month month, String dateDay){
		String m = (new Integer(month.ordinal()+1)).toString();
		return dateDay+"/"+(m.length()==1?"0"+m:m);
	}
	private Integer getDay(String date){
		if(date!=null && date.length()>=2){
			return Integer.parseInt(date.substring(0, 2));
		}
		return null;
	}
	private String setDay(String date, String dateDay){
		if(date==null || dateDay==null){
			return null;
		}
		return (dateDay.length()==1?"0"+dateDay:dateDay)+date.substring(2, date.length());
	}
	private boolean getPrevious(String date){
		if(date == null){
			return false;
		}
		return date.endsWith("-1");
	}
	private String setPrevious(String date, boolean previous){
		if(previous){
			if(!date.endsWith("-1")){
				return date.substring(0, date.length())+" -1";
			}
		} else {
			if(date.endsWith("-1")){
				return date.substring(0, 5);
			}
		}
		return date;
	}
	
}
