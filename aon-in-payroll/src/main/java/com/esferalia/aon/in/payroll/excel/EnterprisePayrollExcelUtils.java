package com.esferalia.aon.in.payroll.excel;

import static com.esferalia.aon.watson.util.AonNumberUtils.zeroIfNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import com.esferalia.aon.in.payroll.excel.EnterprisePayrollExcel.EnterprisePayroll;

public class EnterprisePayrollExcelUtils {

	protected static void clearCalendar(Calendar cal) {
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.HOUR, 0);
	}

	protected static String getMonthYearName(Date date) {
		if (date == null)
			return "FECHA INDEFINIDA";
		Locale esLocale = new Locale("es", "ES");
		DateFormat df = new SimpleDateFormat("MMMMMMMMMM 'de' YYYY", esLocale);
		return df.format(date).toUpperCase(esLocale);
	}

	protected static Double sumThings(Double ...numbers) {
		if (numbers == null)
			return null;
		
		if (Arrays.stream(numbers).allMatch(Objects::isNull))
			return null;
		else {
			Double acum = 0.0;
			for (Double number : numbers) {
				acum += zeroIfNull(number);
			}
			return acum;
		}
	}

	protected static String getDateString (Integer month, Integer year) {
		if (month == null || year == null)
			return "";
		else {
			switch (month) {
			case 1:
				return "enero de "+year;
			case 2:
				return "febrero de "+year;
			case 3:
				return "marzo de "+year;
			case 4:
				return "abril de "+year;
			case 5:
				return "mayo de "+year;
			case 6:
				return "junio de "+year;
			case 7:
				return "julio de "+year;
			case 8:
				return "agosto de "+year;
			case 9:
				return "septiembre de "+year;
			case 10:
				return "octubre de "+year;
			case 11:
				return "noviembre de "+year;
			case 12:
				return "diciembre de "+year;
			default:
				return "";
			}
		}
	}

	protected static List<String> getInnerPeriodStrings(Date startDate, Date endDate) {
		if (startDate == null || endDate == null)
			return Collections.emptyList();
		
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(startDate);
		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(endDate);
		
		List<String> periods = new LinkedList<>();
		
		while(cal1.get(Calendar.YEAR) <= cal2.get(Calendar.YEAR) && cal1.get(Calendar.MONTH) <= cal2.get(Calendar.MONTH)) {
			
			periods.add(getDateString(cal1.get(Calendar.MONTH) + 1, cal1.get(Calendar.YEAR)).toUpperCase(new Locale("es", "ES")));
			
			cal1.add(Calendar.MONTH, 1);
		}
		
		return periods;
		
	}

	protected static String getAppropiatePeriodString(Date startDate, Date endDate) {
		if (startDate == null || endDate == null)
			return "";
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(startDate);
		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(endDate);
		
		boolean sameYear = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
		boolean sameMonth = cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);
		boolean fromStartToEnd = cal1.get(Calendar.DAY_OF_MONTH) == 1 && cal2.get(Calendar.DAY_OF_MONTH) == cal2.getActualMaximum(Calendar.DAY_OF_MONTH);
		
		
		if (sameYear && sameMonth && fromStartToEnd) {
			return getDateString(cal1.get(Calendar.MONTH) + 1, cal1.get(Calendar.YEAR));
		} else {
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy", new Locale("es"));
			String strStartDate = df.format(startDate);
			String strEndDate = df.format(endDate);
			
			return "del " + strStartDate + " al " + strEndDate;
		}
		
	}
	
	protected static void sumPayrolls(EnterprisePayroll originalPayroll, EnterprisePayroll newPayroll) {
		originalPayroll.irpf				 	=	sumThings(originalPayroll.getIrpf(), newPayroll.getIrpf());
		originalPayroll.cgcBase					=	sumThings(originalPayroll.getCgcBase(), newPayroll.getCgcBase());
		originalPayroll.irpfBase 				=	sumThings(originalPayroll.getIrpfBase(), newPayroll.getIrpfBase());
		originalPayroll.inKindIrpfBase			=	sumThings(originalPayroll.getInkindIrpfBase(), newPayroll.getInkindIrpfBase());
		originalPayroll.moneyIrpfBase	 		=	sumThings(originalPayroll.getMoneyIrpfBase(), newPayroll.getMoneyIrpfBase());
		originalPayroll.raw 					=	sumThings(originalPayroll.getRaw(), newPayroll.getRaw());
		originalPayroll.liquid	 				=	sumThings(originalPayroll.getLiquid(), newPayroll.getLiquid());
		originalPayroll.employeeSS	 			=	sumThings(originalPayroll.getEmployeeSS(), newPayroll.getEmployeeSS());
		originalPayroll.enterpriseSS			=	sumThings(originalPayroll.getEnterpriseSS(), newPayroll.getEnterpriseSS());
		originalPayroll.totalSS 				=	sumThings(originalPayroll.getTotalSS(), newPayroll.getTotalSS());
		originalPayroll.totalCost	 			=	sumThings(originalPayroll.getTotalCost(), newPayroll.getTotalCost());
		originalPayroll.itCompensation 			=	sumThings(originalPayroll.getItCompensation(), newPayroll.getItCompensation());
		originalPayroll.bonuses 				=	sumThings(originalPayroll.getBonuses(), newPayroll.getBonuses());
		originalPayroll.fundae	 				=	sumThings(originalPayroll.getFundae(), newPayroll.getFundae());
		originalPayroll.cgc 					=	sumThings(originalPayroll.getCgc(), newPayroll.getCgc());
		originalPayroll.cgp 					=	sumThings(originalPayroll.getCgp(), newPayroll.getCgp());
		originalPayroll.unemployment 			=	sumThings(originalPayroll.getUnemployment(), newPayroll.getUnemployment());
		originalPayroll.jobTraining 			=	sumThings(originalPayroll.getJobTraining(), newPayroll.getJobTraining());
		originalPayroll.advancedPayment 		=	sumThings(originalPayroll.getAdvancedPayment(), newPayroll.getAdvancedPayment());
		originalPayroll.otherDeductions 		=	sumThings(originalPayroll.getOtherDeductions(), newPayroll.getOtherDeductions());
		originalPayroll.estruc	 				=	sumThings(originalPayroll.getEstruc(), newPayroll.getEstruc());
		originalPayroll.noEstruct	 			=	sumThings(originalPayroll.getNoEstruct(), newPayroll.getNoEstruct());
		originalPayroll.cgcEnterprise	 		=	sumThings(originalPayroll.getCgcEnterprise(), newPayroll.getCgcEnterprise());
		originalPayroll.cgpEnterprise	 		=	sumThings(originalPayroll.getCgpEnterprise(), newPayroll.getCgpEnterprise());
		originalPayroll.unemploymentEnterprise	= 	sumThings(originalPayroll.getUnemploymentEnterprise(), newPayroll.getUnemploymentEnterprise());
		originalPayroll.jobTrainingEnterprise 	=	sumThings(originalPayroll.getJobTrainingEnterprise(), newPayroll.getJobTrainingEnterprise());
		originalPayroll.fogasaEnterprise 		=	sumThings(originalPayroll.getFogasaEnterprise(), newPayroll.getFogasaEnterprise());
		originalPayroll.estrucEnterprise 		=	sumThings(originalPayroll.getEstrucEnterprise(), newPayroll.getEstrucEnterprise());
		originalPayroll.noEstructEnterprise 	=	sumThings(originalPayroll.getNoEstructEnterprise(), newPayroll.getNoEstructEnterprise());
		originalPayroll.embargos 				=	sumThings(originalPayroll.getEmbargos(), newPayroll.getEmbargos());
	}
	

}
