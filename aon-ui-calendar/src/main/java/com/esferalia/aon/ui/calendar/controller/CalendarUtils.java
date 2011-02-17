package com.esferalia.aon.ui.calendar.controller;

import com.esferalia.aon.calendar.Calendar;
import com.esferalia.aon.calendar.enumeration.CalendarSource;
import com.esferalia.aon.payroll.Contract;

public class CalendarUtils {

	public static Calendar obtainCalendar(CalendarSource source, Integer id) {
		// TODO Auto-generated method stub
		
		
//		if(source==CalendarSource.CONTRACT){ 
//			if(getContract()!=null && getContract().getCalendar()!=null){
//				this.select(event, getContract().getCalendar());
//			}
//		} else if(source==CalendarSource.WORKPLACE){ 
//			if(getWorkPlace()!=null && getWorkPlace().getCalendar()!=null){
//				this.select(event, getWorkPlace().getCalendar());
//			}
//		} else if(source==CalendarSource.ENTERPRISE){ 
//			if(getEnterprise()!=null && getEnterprise().getCalendar()!=null){
//				this.select(event, getEnterprise().getCalendar());
//			}
//		} else if(source==CalendarSource.AGREEMENT){ 
//			if(getAgreement()!=null && getAgreement().getCalendar()!=null){
//				this.select(event, getAgreement().getCalendar());
//			}
//		}
//		
//		
		
		
		return null;
	}
	
	
	public static Calendar obtainCalendar(Contract contract) {
		if(contract.getCalendar()!=null){
			return contract.getCalendar();
		} else if (contract.getWorkPlace().getCalendar()!=null){
			return contract.getWorkPlace().getCalendar();
		}  else if (contract.getWorkPlace().getEnterprise().getCalendar()!=null){
			return contract.getWorkPlace().getEnterprise().getCalendar();
		}  else if (contract.getWorkPlace().getEnterprise().getAgreement().getCalendar()!=null){
			return contract.getWorkPlace().getEnterprise().getAgreement().getCalendar();
		}
		
		return null;
	}
	
	
	

}
