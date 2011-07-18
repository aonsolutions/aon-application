package com.esferalia.aon.payroll;


import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.common.enumeration.Month;

@Entity
@Table(name="agreement_extra")
public class AgreementExtra implements ITransferObject {

	private static final long serialVersionUID = -3253265204034502980L;

	private Integer id;
	private Agreement agreement;
	private AgreementPayment agreementPayment;
	private String startDate;	
	private String endDate;
	private String issueDate;
	
	@Id
	@GeneratedValue
	@Column(nullable = false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@ManyToOne
    @JoinColumn( name="agreement", nullable = false, updatable = false )	
	@ForeignKey(name = "FK_AGREEMENT_EXTRA_AGREEMENT")
	@Index(name = "IDX_AGREEMENT_EXTRA_AGREEMENT")
	public Agreement getAgreement() {
		return agreement;
	}
	public void setAgreement(Agreement agreement) {
		this.agreement = agreement;
	}
	
	@ManyToOne
	@JoinColumn( name="agreement_payment", updatable = false )	
	@ForeignKey(name = "FK_AGREEMENT_EXTRA_AGREEMENT_PAYMENT")
	@Index(name = "IDX_AGREEMENT_EXTRA_AGREEMENT_PAYMENT")
	public AgreementPayment getAgreementPayment() {
		return agreementPayment;
	}
	public void setAgreementPayment(AgreementPayment agreementPayment) {
		this.agreementPayment = agreementPayment;
	}
	
	@Column( name = "start_date", length = 32, nullable = false )
    public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	@Column( name = "end_date", length = 32, nullable = false )
    public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}	
	
	@Column( name = "issue_date", length = 32, nullable = false )
	public String getIssueDate() {
		return issueDate;
	}
	public void setIssueDate(String issueDate) {
		this.issueDate = issueDate;
	}	
	
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final AgreementExtra o = (AgreementExtra) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.agreement, o.agreement)			
				.append(this.agreementPayment, o.agreementPayment)
				.append(this.startDate, o.startDate)
				.append(this.endDate, o.endDate)
				.append(this.issueDate, o.issueDate)			
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)
			.append(agreement)
			.append(agreementPayment)
			.append(startDate)
			.append(endDate)
			.append(issueDate)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}
	
	@Transient
	public Month getStartDateMonth(){
		return Month.getMonthByValue(getMonth(getStartDate())!=null?getMonth(getStartDate()):0);
	}
	public void setStartDateMonth(Month startDateMonth){
		startDate = setMonth(getStartDate(), startDateMonth, "01");
	}
	@Transient
	public boolean getStartDatePrevious(){
		return getPrevious(getStartDate());
	}
	public void setStartDatePrevious(boolean startDatePrevious){
		startDate = setPrevious(getStartDate(), startDatePrevious);
	}
	@Transient
	public Month getEndDateMonth(){
		return Month.getMonthByValue(getMonth(getEndDate())!=null?getMonth(getEndDate()):0);
	}
	public void setEndDateMonth(Month endDateMonth){
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, endDateMonth.ordinal());
		endDate = setMonth(getEndDate(), endDateMonth, String.valueOf(cal.getActualMaximum(Calendar.DAY_OF_MONTH)));
	}
	@Transient
	public boolean getEndDatePrevious(){
		return getPrevious(getEndDate());
	}
	public void setEndDatePrevious(boolean endDatePrevious){
		endDate = setPrevious(getEndDate(), endDatePrevious);
	}
	@Transient
	public Month getIssueDateMonth() {
		return Month.getMonthByValue(getMonth(getIssueDate())!=null?getMonth(getIssueDate()):0);
	}
	public void setIssueDateMonth(Month issueDateMonth) {
		if( issueDateMonth != null){
			String m = (new Integer(issueDateMonth.ordinal()+1)).toString();
			issueDate = "01/"+(m.length()==1?"0"+m:m);;
		}
	}
	@Transient
	public Integer getIssueDateDay() {
		return getDay(getIssueDate());
	}
	public void setIssueDateDay(Integer issueDateDay) {
		issueDate = setDay(issueDate, issueDateDay.toString());
	}
	
	private Integer getMonth(String date){
		if(date!=null && date.length()>4){
			return Integer.parseInt(date.substring(3, 5))-1;
		}
		return null;
	}
	private String setMonth(String date, Month month){
		if(date==null || month==null){
			return null;
		}
		String m = (new Integer(month.ordinal()+1)).toString();
		return "01/"+(m.length()==1?"0"+m:m);//+date.substring(5, date.length());
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
