package com.esferalia.aon.ui.payroll.controller.salary;

import java.util.Date;

import com.code.aon.common.util.CommonUtil;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.enumeration.DismissCause;

public class SettleParams {
	
	private static int YEAR_MAX_DAYS = 365;
	private static int MIN_NOTICE_DAYS = 15;
	
	private Contract contract;
	private Date suspensionDate;
	private Date seniorityDate;
	private Date noticeDate;
	private Integer noticeDays;
	private Double noticeDayAmount;
	private DismissCause dismissCause;
	private Integer compensationDays;
	private Double compensation;
	private Double dayAmount;
	private Integer daysPerYear;
	private Integer pendingVacation;
	private Double vacationDayAmount;
	private Double vacationAmount;
	
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
		if(getSeniorityDate()!=null && suspensionDate!=null && getDismissCause()!=null ){
			setCompensationDays( new Double((getYears()*getDaysPerYear())).intValue() );
			calculateCompensationDays();
		}
	}
	public Date getSeniorityDate() {
		return seniorityDate;
	}
	public Date getNoticeDate() {
		return noticeDate;
	}
	public void setNoticeDate(Date noticeDate) {
		this.noticeDate = noticeDate;
//		if(getSuspensionDate()!=null && noticeDate!=null){
//			setNoticeDays(differenceBetweenDates(getSuspensionDate(), noticeDate)-1);
//		}
		if(getSeniorityDate()!=null && suspensionDate!=null && getDismissCause()!=null ){
			calculateCompensationDays();
		}
	}
	public boolean isNoticeLimitExceeded(){
		if(getSuspensionDate()!=null && noticeDate!=null){
			if(Math.abs(differenceBetweenDates(getSuspensionDate(), noticeDate)-1) > MIN_NOTICE_DAYS){
				return true;
			}
		}
		return false;
	}
	
	public Integer getNoticeDays() {
		return noticeDays;
	}
	public void setNoticeDays(Integer noticeDays) {
		this.noticeDays = noticeDays;
	}
	public Double getNoticeDayAmount() {
		return noticeDayAmount;
	}
	public void setNoticeDayAmount(Double noticeDayAmount) {
		this.noticeDayAmount = noticeDayAmount;
	}
	public void setSeniorityDate(Date seniorityDate) {
		this.seniorityDate = seniorityDate;
	}
	public DismissCause getDismissCause() {
		return dismissCause;
	}
	public void setDismissCause(DismissCause dismissCause) {
		this.dismissCause = dismissCause;
		if(dismissCause!=null){
			setDaysPerYear(dismissCause.getCompensationDaysPerYear());
		}
	}
	public Integer getCompensationDays() {
		return compensationDays;
	}
	public boolean isVoluntaryLeave() {
		if(getDismissCause()==DismissCause.DC4){
			return true;
		}
		return false;
	}
	public void setCompensationDays(Integer compensationDays) {
		this.compensationDays = compensationDays;
		if(compensationDays!=null && getDayAmount()!=null){
			setCompensation(CommonUtil.round(compensationDays*getDayAmount()));
		}
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
	}
	public Integer getDaysPerYear() {
		return daysPerYear;
	}
	public void setDaysPerYear(Integer daysPerYear) {
		this.daysPerYear = daysPerYear;
		if(getSeniorityDate()!=null && getSuspensionDate()!=null && getDismissCause()!=null ){
			setCompensationDays( new Double((getYears()*getDaysPerYear())).intValue() );
		}
	}
	public Integer getPendingVacation() {
		return pendingVacation;
	}
	public void setPendingVacation(Integer pendingVacation) {
		this.pendingVacation = pendingVacation;
	}
	public Double getVacationDayAmount() {
		return vacationDayAmount;
	}
	public void setVacationDayAmount(Double vacationDayAmount) {
		this.vacationDayAmount = vacationDayAmount;
	}
	public Double getVacationAmount() {
		return vacationAmount==null?0.0:vacationAmount;
	}
	public void setVacationAmount(Double vacationAmount) {
		this.vacationAmount = vacationAmount;
	}
	public Double getToalAmount(){
		return getCompensation()+getVacationAmount();
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
	
	private Integer differenceBetweenDates(Date from, Date to) {
		Integer diffDays = new Integer(0);
		final Double MS_PER_DAY = new Double(1000 * 60 * 60 * 24);
//		if(from.before(to)) {
			diffDays = (int)((Math.floor((to.getTime() - from.getTime()) / MS_PER_DAY + 0.5d) + 1));
//		}
		return diffDays;
	}
	
	private void calculateCompensationDays(){
//		if(getSeniorityDate()!=null && getSuspensionDate()!=null && getDismissCause()!=null ){
			if(isNoticeLimitExceeded() && getCompensationDays()!=null){
				if(isVoluntaryLeave()){
					setCompensationDays(getCompensationDays() + 15);
				} else {
					setCompensationDays(getCompensationDays() - 15);
					setCompensationDays( getCompensationDays()<0?(0):(getCompensationDays()) );
				}
			}
//		}
	}
	
}
