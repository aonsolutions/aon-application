package com.esferalia.aon.in.payroll.pdf.creators.payroll._default;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.Ignore;
import org.junit.Test;

import com.esferalia.aon.in.payroll.pdf.creators.PdfMaker;
import com.esferalia.aon.in.payroll.pdf.creators.exceptions.CanNotCreatePdfException;
import com.esferalia.aon.in.payroll.pdf.creators.payroll._default.beans.DefaultPayrollAccrual;
import com.esferalia.aon.in.payroll.pdf.creators.payroll._default.beans.DefaultPayrollDeduction;
import com.esferalia.aon.in.payroll.pdf.creators.payroll._default.beans.Contingency_bases.Contingency_bases_builder;
import com.esferalia.aon.in.payroll.pdf.creators.payroll._default.beans.DefaultPayroll.DefaultPayrollBuilder;


public class DetailedPayrollTest {

	@Test
	@Ignore
	public void printTest() {
		DefaultPayrollBuilder builder = new DefaultPayrollBuilder();
		
		
		//CREATE ACCRUALS
		Map<Integer,ArrayList<DefaultPayrollAccrual>> ac = new HashMap<Integer,ArrayList<DefaultPayrollAccrual>>();
		
		ac.put(1,new ArrayList<DefaultPayrollAccrual>());
		ac.put(2,new ArrayList<DefaultPayrollAccrual>());
		ac.put(4,new ArrayList<DefaultPayrollAccrual>());
		
		for (int i = 0; i <5; i++) {
			
			DefaultPayrollAccrual accrual = new DefaultPayrollAccrual(Optional.of(999999999.99),Optional.of("Descripcion por defecto."));
			ac.get(1).add(accrual);
			ac.get(2).add(accrual);
			ac.get(4).add(accrual);
			
		}
		
		///CREATE DEDUCTIONS
		Map<Integer,ArrayList<DefaultPayrollDeduction>> de = new HashMap<Integer,ArrayList<DefaultPayrollDeduction>>();
		
		de.put(1,new ArrayList<DefaultPayrollDeduction>());
		de.put(2,new ArrayList<DefaultPayrollDeduction>());
		de.put(3,new ArrayList<DefaultPayrollDeduction>());
		de.put(4,new ArrayList<DefaultPayrollDeduction>());
		de.put(5,new ArrayList<DefaultPayrollDeduction>());
		
		for (int i = 0; i < 2; i++) {
			
			DefaultPayrollDeduction deduction =
					new DefaultPayrollDeduction(
							Optional.of(999999999.99),
							Optional.of("Descripcion por defecto"),
							Optional.of(99.99)
					);
			de.get(1).add(deduction);
			de.get(2).add(deduction);
			de.get(3).add(deduction);
			de.get(4).add(deduction);
			
		}
		
		//CREATE CONTIGENCIES
		Contingency_bases_builder con_builder = new Contingency_bases_builder();
		
		con_builder.setMonthly_amount(Optional.of(999999999.99))
		.setExtra_proration_amount(Optional.of(999999999.99))
		.setCommon_cont_base(Optional.of(999999999.99))
		.setCommon_cont_type(Optional.of(99.99))
		.setCommon_cont_ap_enterprise(Optional.of(999999999.99))
		.setProfessional_cont_base(Optional.of(999999999.99))
		.setAt_ep_type(Optional.of(99.99))
		.setAt_ep_ap_enterprise(Optional.of(999999999.99))
		.setUnemployment_type(Optional.of(99.99))
		.setUnemployment_ap_enterprise(Optional.of(999999999.99))
		.setProfes_form_type(Optional.of(99.99))
		.setProfes_form_ap_enterprise(Optional.of(999999999.99))
		.setFogasa_type(Optional.of(99.99))
		.setFogasa_ap_enterprise(Optional.of(999999999.99))
		.setForce_majeure_base(Optional.of(999999999.99))
		.setForce_majeure_type(Optional.of(99.99))
		.setForce_majeure_ap_enterprise(Optional.of(999999999.99))
		.setNo_struct_base(Optional.of(999999999.99))
		.setNo_struct_type(Optional.of(99.99))
		.setNo_struct_ap_enterprise(Optional.of(999999999.99))
		.setIrpf_esp(Optional.of(999999999.99))
		.setIrpf_retrib_diner(Optional.of(999999999.99));
	
		//BUILD PAYROLL
		builder
		.setEnterprise(Optional.of("DEMO EMPRESA S.L"))
		.setAddress(Optional.of("Calle Duque de Wellington, 522 (01010)"))
		.setAddress_2(Optional.of("Vitoria-Gazteiz"))
		.setCif(Optional.of("58595859M"))
		.setCcc(Optional.of("8935713546370"))
		.setEmployee(Optional.of("Iker Gónzalez"))
		.setNif(Optional.of("47227931-F"))
		.setNss(Optional.of("11004767999"))
		.setProfessional_group(Optional.of("Director"))
		.setQuotation_group(Optional.of("01"))
		.setAntiquity(Optional.of(new Date()))
		.setLiquid_period_start(Optional.of(new Date()))
		.setLiquid_period_end(Optional.of(new Date()))
		.setTotal_days(Optional.of(999999999))
		.setAccruals(Optional.of(ac))
		.setDeductions(Optional.of(de))
		.setAccrual_total(Optional.of(999999999.99))
		.setDeduction_total(Optional.of(999999999.99))
		.setPayroll_total(Optional.of(999999999.99))
		.setContingencies(Optional.of(con_builder.build()));
		
		try { PdfMaker.print_default_payroll("payroll.pdf", builder.build(), Optional.empty());} 
		catch (CanNotCreatePdfException e) {
			e.printStackTrace(); 
			fail("Can not create the payroll");
		}
		
	}

	//RANDOM BETWEEN 0 AND Y
	public double random(double y){
		return Math.random()*y;
	}
}
