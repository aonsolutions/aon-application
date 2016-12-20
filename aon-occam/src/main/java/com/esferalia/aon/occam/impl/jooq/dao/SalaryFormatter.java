package com.esferalia.aon.occam.impl.jooq.dao;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.SalaryEntry;
import com.esferalia.aon.watson.util.AonStringUtils;

public class SalaryFormatter {

	public static final SimpleDateFormat FMT = new SimpleDateFormat("dd/MM/yyyy");
	public static final DecimalFormat DEC = new DecimalFormat("#,##0.00");
	private static final String NO_DATA = "<div>NO SE ENCONTRARON DATOS</div>";
	private static final String MAIN_MSG = "<pre class=\"aon-fixed-font aon-font-medium aon-margin-bottom\">{0}<pre>";
	private static final String DIV_MSG = "<div>{0}</div>";
	private static final String DIV_MSG_BOLD= "<div><b>{0}</b></div>";
	
	
	public static String formatSalariesForAccount(String title, LinkedList<SalaryEntry> list) {
		StringBuilder buf = new StringBuilder();
		String header = AonStringUtils.repeat(" ", 2)
				+ AonStringUtils.rightPad("NOMBRE EMPLEADO",40)
				+ AonStringUtils.leftPad("Rem. Mon.",12) 
				+ AonStringUtils.leftPad("Rem. Esp.",12)
				+ AonStringUtils.leftPad("Dietas",12)
				+ AonStringUtils.leftPad("Indemniz.",12)
				+ AonStringUtils.leftPad("Anticipos",12)
				+ AonStringUtils.leftPad("Embargos",12)
				+ AonStringUtils.leftPad("Otr. Ded.",12)
				+ AonStringUtils.leftPad("IRPF",12)
				+ AonStringUtils.leftPad("IRPF Esp.",12)
				+ AonStringUtils.leftPad("SS Emple.",12)
				+ AonStringUtils.leftPad("SS Empre.",12);
		
		buf.append(MessageFormat.format(DIV_MSG,AonStringUtils.repeat(" ", header.length())));
		buf.append(MessageFormat.format(DIV_MSG_BOLD,AonStringUtils.center(title, header.length())));
		buf.append(MessageFormat.format(DIV_MSG,AonStringUtils.repeat(" ", header.length())));
		buf.append(MessageFormat.format(DIV_MSG_BOLD,AonStringUtils.repeat("-", header.length()))); 
		buf.append(MessageFormat.format(DIV_MSG_BOLD,header));
		buf.append(MessageFormat.format(DIV_MSG_BOLD,AonStringUtils.repeat("-", header.length())));
		
		if (list == null || list.size() == 0) {
			buf.append(MessageFormat.format(DIV_MSG,AonStringUtils.repeat(" ", header.length())));
			buf.append(NO_DATA);			
			buf.append(MessageFormat.format(DIV_MSG,AonStringUtils.repeat(" ", header.length())));
		}
		
		SalaryEntry sum = new SalaryEntry();
		for (SalaryEntry br : list) {
			sum.setMoneySalary(sum.getMoneySalary() +  br.getMoneySalary());
			sum.setInKindSalary(sum.getInKindSalary() +br.getInKindSalary());
			sum.setAllowance(sum.getAllowance() +br.getAllowance());
			sum.setSalaryCompensation(sum.getSalaryCompensation() +br.getSalaryCompensation());
			sum.setSalaryDedAdvPayment(sum.getSalaryDedAdvPayment() +br.getSalaryDedAdvPayment());
			sum.setSalaryDedSeize(sum.getSalaryDedSeize() +br.getSalaryDedSeize());
			sum.setSalaryOtherDeductions(sum.getSalaryOtherDeductions() + br.getSalaryOtherDeductions());
			sum.setIrpf(sum.getIrpf() + br.getIrpf());
			sum.setInKindIrpf(sum.getInKindIrpf() + br.getInKindIrpf());
			sum.setEmployeeSocialInsurance(sum.getEmployeeSocialInsurance() + br.getEmployeeSocialInsurance());
			sum.setCompanySocialInsurance(sum.getCompanySocialInsurance() + br.getCompanySocialInsurance());
			buf.append(MessageFormat.format(DIV_MSG
					 ,AonStringUtils.repeat(" ", 2)
					+ AonStringUtils.rightPad(br.getSalaryDescription(),40)
					+ AonStringUtils.leftPad(DEC.format(br.getMoneySalary()),12) 
					+ AonStringUtils.leftPad(DEC.format(br.getInKindSalary()),12)
					+ AonStringUtils.leftPad(DEC.format(br.getAllowance()),12)
					+ AonStringUtils.leftPad(DEC.format(br.getSalaryCompensation()),12)
					+ AonStringUtils.leftPad(DEC.format(br.getSalaryDedAdvPayment()),12)
					+ AonStringUtils.leftPad(DEC.format(br.getSalaryDedSeize()),12)
					+ AonStringUtils.leftPad(DEC.format(br.getSalaryOtherDeductions()),12)
					+ AonStringUtils.leftPad(DEC.format(br.getIrpf()),12)
					+ AonStringUtils.leftPad(DEC.format(br.getInKindIrpf()),12)
					+ AonStringUtils.leftPad(DEC.format(br.getEmployeeSocialInsurance()),12)
					+ AonStringUtils.leftPad(DEC.format(br.getCompanySocialInsurance()),12)
					));
		}
		buf.append(MessageFormat.format(DIV_MSG_BOLD,AonStringUtils.repeat("-", header.length())));
		buf.append(MessageFormat.format(DIV_MSG
				 ,AonStringUtils.repeat(" ", 2)
				+ AonStringUtils.leftPad("TOTAL",40)
				+ AonStringUtils.leftPad(DEC.format(sum.getMoneySalary()),12) 
				+ AonStringUtils.leftPad(DEC.format(sum.getInKindSalary()),12)
				+ AonStringUtils.leftPad(DEC.format(sum.getAllowance()),12)
				+ AonStringUtils.leftPad(DEC.format(sum.getSalaryCompensation()),12)
				+ AonStringUtils.leftPad(DEC.format(sum.getSalaryDedAdvPayment()),12)
				+ AonStringUtils.leftPad(DEC.format(sum.getSalaryDedSeize()),12)
				+ AonStringUtils.leftPad(DEC.format(sum.getSalaryOtherDeductions()),12)
				+ AonStringUtils.leftPad(DEC.format(sum.getIrpf()),12)
				+ AonStringUtils.leftPad(DEC.format(sum.getInKindIrpf()),12)
				+ AonStringUtils.leftPad(DEC.format(sum.getEmployeeSocialInsurance()),12)
				+ AonStringUtils.leftPad(DEC.format(sum.getCompanySocialInsurance()),12)
				));
		return MessageFormat.format(MAIN_MSG, buf.toString());			
	}
	
}
