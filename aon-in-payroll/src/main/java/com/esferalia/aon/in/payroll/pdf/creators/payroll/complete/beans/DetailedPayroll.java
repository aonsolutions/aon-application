package com.esferalia.aon.in.payroll.pdf.creators.payroll.complete.beans;

import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

import com.esferalia.aon.in.payroll.pdf.creators.payroll.CraTypes;
import com.sun.xml.xsom.impl.scd.Iterators.Map;

public class DetailedPayroll {
	
	private Optional <String> enterprise;
	private Optional <String> address;
	private Optional <String> address_2;
	private Optional <String> cif;
	private Optional <String> ccc;
	
	private Optional <String> employee;
	private Optional <String> nif;
	private Optional <String> nss;
	private Optional <String> professional_group;
	private Optional <String> quotation_group;
	private Optional <Date>   antiquity;
 	private Optional <Date>   liquid_period_start;
 	
 	private Optional <Integer> total_days;
 	
 	private Optional <Map<Integer,ArrayList<DetailedPayrollAccrual>>> accruals;
 	private Optional <Map<Integer,ArrayList<DetailedPayrollDeduction>>> deductions;
 	
 	private Optional <Double> accrual_total;
 	private Optional <Double> deduction_total;
 	private Optional <Double> payroll_total;
 	
 	private Optional <Contingency_bases> contingencies;
 	

}
