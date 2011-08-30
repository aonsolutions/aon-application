package com.esferalia.aon.ui.payroll.controller.salary;

import java.util.Date;

import com.code.aon.common.enumeration.Month;
import com.code.aon.common.util.CommonUtil;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.enumeration.DismissCause;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.calculator.ISalaryCalculatorContext;
import com.esferalia.aon.salary.enumeration.SalaryType;

public class SettleParams {
	
	private static int YEAR_MAX_DAYS = 365;
	private static int MIN_NOTICE_DAYS = 15;
	
	private Contract contract;
	private DismissCause dismissCause;
	private Integer daysPerYear;

	private Date suspensionDate;
	private Date seniorityDate;
	private Date noticeDate;
	
	private Integer noticeDays;
	private Integer pendingVacationDays;
	private Integer compensationDays;
	
	private Double vacationDayAmount;
	private Double dayAmount;

	private Double vacationAmount;
	private Double noticeAmount;
	private Double compensation;
	
	public SettleParams(Contract contract) {
		this.contract = contract;
		this.seniorityDate = contract.getSeniorityDate();
		try {
			ISalary salary;
			int year = CommonUtil.getYear(new Date());
			int month = CommonUtil.getMonth(new Date());
			ISalaryCalculatorContext ctx = contract.getSalaryCalculatorContext(year, Month.values()[month-1], SalaryType.SALARY);
			salary = (ISalary) ctx.getSalaryProxy().getSalary();
			// TODO ******* revisar el calculo del salario diario *******  
			dayAmount = salary.getCommonBase()/30;
			vacationDayAmount = salary.getCommonBase()/30;
		} catch (SalaryException e) {
			// como tratar esto?
		}
	}
	
	public Contract getContract() {
		return contract;
	}
	public void setContract(Contract contract) {
		this.contract = contract;
	}
	public Date getSuspensionDate() {
		return suspensionDate;
	}
	public void setSuspensionDate(Date suspensionDate) {
		this.suspensionDate = suspensionDate;
		calculateCompensationDays();
		calculateNoticeDays();
		calculateCompensationAmount();
	}
	public Date getSeniorityDate() {
		return seniorityDate;
	}
	public void setSeniorityDate(Date seniorityDate) {
		this.seniorityDate = seniorityDate;
		calculateCompensationDays();
		calculateCompensationAmount();
	}
	public Date getNoticeDate() {
		return noticeDate;
	}
	public void setNoticeDate(Date noticeDate) {
		this.noticeDate = noticeDate;
		calculateCompensationDays();
		calculateNoticeDays();
		calculateNoticeDayAmount();
	}
	public Integer getNoticeDays() {
		return noticeDays;
	}
	public void setNoticeDays(Integer noticeDays) {
		this.noticeDays = noticeDays;
	}
	public Double getNoticeAmount() {
		return noticeAmount==null?0.0:noticeAmount;
	}
	public void setNoticeAmount(Double noticeAmount) {
		this.noticeAmount = noticeAmount;
	}
	public DismissCause getDismissCause() {
		return dismissCause;
	}
	public void setDismissCause(DismissCause dismissCause) {
		this.dismissCause = dismissCause;
		calculateDaysPerYear();
		calculateCompensationDays();
		calculateCompensationAmount();
	}
	public Integer getCompensationDays() {
		return compensationDays;
	}
	public void setCompensationDays(Integer compensationDays) {
		this.compensationDays = compensationDays;
		calculateCompensationAmount();
	}
	public Double getCompensation() {
		return compensation==null?0:compensation;
	}
	public void setCompensation(Double compensation) {
		this.compensation = compensation;
	}
	public Double getDayAmount() {
		return dayAmount;
	}
	public void setDayAmount(Double dayAmount) {
		this.dayAmount = dayAmount;
		calculateNoticeDayAmount();
		calculateCompensationAmount();
	}
	public Integer getDaysPerYear() {
		return daysPerYear;
	}
	public void setDaysPerYear(Integer daysPerYear) {
		this.daysPerYear = daysPerYear;
	}
	public Integer getPendingVacationDays() {
		return pendingVacationDays;
	}
	public void setPendingVacationDays(Integer pendingVacationDays) {
		this.pendingVacationDays = pendingVacationDays;
		calculateVacationAmount();
	}
	public Double getVacationDayAmount() {
		return vacationDayAmount;
	}
	public void setVacationDayAmount(Double vacationDayAmount) {
		this.vacationDayAmount = vacationDayAmount;
		calculateVacationAmount();
	}
	public Double getVacationAmount() {
		return vacationAmount==null?0.0:vacationAmount;
	}
	public void setVacationAmount(Double vacationAmount) {
		this.vacationAmount = vacationAmount;
	}
	public Double getTotalAmount(){
		return getCompensation()+getVacationAmount()+getNoticeAmount();
	}
	
	public Integer getDays() {
		if(getSuspensionDate()!=null && getSeniorityDate()!=null){
			return differenceBetweenDates(getSeniorityDate(), getSuspensionDate());
		}
		return null;
	}
	public Double getYears() {
		if(getDays()!=null){
			return CommonUtil.round(new Double(getDays())/YEAR_MAX_DAYS);
		}
		return null;
	}
	public Integer getLimit(){
		return getDismissCause().getMaximunMonths();
	}
	
	public boolean isNoticeLimitExceeded(){
		if(suspensionDate!=null && noticeDate!=null){
			if(Math.abs(differenceBetweenDates(noticeDate, suspensionDate)) <= MIN_NOTICE_DAYS){
				return true;
			}
		}
		return false;
	}
	public boolean isVoluntaryLeave() {
		if(getDismissCause()==DismissCause.DC4){
			return true;
		}
		return false;
	}
	
	
	private Integer differenceBetweenDates(Date from, Date to) {
		Integer diffDays = new Integer(0);
		final Double MS_PER_DAY = new Double(1000 * 60 * 60 * 24);
//		if(from.before(to)) {
			diffDays = (int)((Math.floor((to.getTime() - from.getTime()) / MS_PER_DAY + 0.5d) + 1));
//		}
			
		CommonUtil.getDaysBetweenDates(from, to, false);
			
		return diffDays;
	}
	
	private void calculateDaysPerYear() {
		if(dismissCause!=null){
			setDaysPerYear(dismissCause.getCompensationDaysPerYear());
		}
	}
	
	private void calculateCompensationDays(){
		compensationDays=0;
		if(daysPerYear!=null && getYears()!=null){
			compensationDays += new Double((getYears()*daysPerYear)).intValue();
		}
//		if(pendingVacationDays!=null){
//			compensationDays += pendingVacationDays;
//		}
//		calculateNoticeDays();
//		if(noticeDays!=null){
//			if(isVoluntaryLeave()){
//				compensationDays -= noticeDays;
//			} else {
//				compensationDays += noticeDays;
//			}
//		}
	}
	
	private void calculateNoticeDays(){
		if( !isNoticeLimitExceeded() ){
			setNoticeDays(null);
		} else if( isNoticeLimitExceeded() && suspensionDate!=null && noticeDate!=null ){
			Integer i = differenceBetweenDates(noticeDate, suspensionDate);
			setNoticeDays(i<0?0:i);
		} 
	}
	
	private void calculateVacationAmount(){
		if(pendingVacationDays!=null && vacationDayAmount!=null){
			setVacationAmount(pendingVacationDays*vacationDayAmount);
		}
	}
	
	private void calculateNoticeDayAmount(){
		if(noticeDays!=null && dayAmount!=null){
			setNoticeAmount(CommonUtil.round(noticeDays*dayAmount));
		}
	}
	
	private void calculateCompensationAmount(){
		if(compensationDays!=null && dayAmount!=null && daysPerYear!=null && getYears()!=null){
			Integer days = new Double((getYears()*daysPerYear)).intValue();
			setCompensation(CommonUtil.round(days*dayAmount));
		}
	}
	
}
