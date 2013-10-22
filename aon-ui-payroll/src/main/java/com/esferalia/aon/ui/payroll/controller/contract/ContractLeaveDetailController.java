package com.esferalia.aon.ui.payroll.controller.contract;


import java.util.Calendar;
import java.util.Date;

import javax.faces.event.ValueChangeEvent;

import org.apache.commons.lang.StringUtils;

import com.code.aon.ui.form.LinesController;
import com.esferalia.aon.payroll.ContractLeave;
import com.esferalia.aon.payroll.ContractLeaveDetail;
import com.esferalia.aon.ui.payroll.utils.NumberValidation;

public class ContractLeaveDetailController extends LinesController {
	
	public Boolean getValidCollegeNumber() {
		return checkCollegeNumber((ContractLeaveDetail) this.getTo());
	}
	
	public Boolean getValidCias() {
		return checkCiasNumber((ContractLeaveDetail) this.getTo());
	}
	
	public Boolean checkCollegeNumber(ContractLeaveDetail detail){
		if (detail!=null && !StringUtils.isBlank(detail.getCollegeNumber())) {
			if (NumberValidation.validCollegeNumberPattern(detail.getCollegeNumber())
					&& NumberValidation.validCollegeNumberControlDigit(detail.getCollegeNumber())) {
				return true;
			} else {
				return false;
			}
		} else {
			return null;
		}
	}
	
	public Boolean checkCiasNumber(ContractLeaveDetail detail){
		if (detail!=null && !StringUtils.isBlank(detail.getCias())) {
			if (NumberValidation.validCiasPattern(detail.getCias())
					&& NumberValidation.validCiasControlDigit(detail .getCias())) {
				return true;
			} else {
				return false;
			}
		} else {
			return null;
		}
	}
	
	public void onChangeConfirmNumber(ValueChangeEvent event){
		Integer n = (Integer) event.getNewValue();
		if(n!=null && n>0){
			ContractLeave leave = (ContractLeave) this.getMasterController().getTo();
			ContractLeaveDetail detail = (ContractLeaveDetail) this.getTo();
			detail.setDate(getConfirmSuggestedDate(leave.getStartDate(), n));
		}
	}
	
	public Date getConfirmSuggestedDate(Date startDate, Integer confirmReportNumber) {
		--confirmReportNumber; // starts from zero
		if(startDate==null){
			return new Date();
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);
		cal.add(Calendar.DAY_OF_YEAR, 3 + (((confirmReportNumber==null?0:confirmReportNumber)  * 7)) );
		return cal.getTime();
	}
	
}
