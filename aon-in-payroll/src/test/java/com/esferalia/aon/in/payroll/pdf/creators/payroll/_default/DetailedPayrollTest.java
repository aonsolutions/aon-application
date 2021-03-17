package com.esferalia.aon.in.payroll.pdf.creators.payroll._default;

import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.junit.Test;

import com.esferalia.aon.in.payroll.pdf.creators.PdfMaker;
import com.esferalia.aon.in.payroll.pdf.creators.exceptions.CanNotCreatePdfException;
import com.esferalia.aon.in.payroll.pdf.creators.payroll._default.beans.Contingency_bases.Contingency_bases_builder;
import com.esferalia.aon.in.payroll.pdf.creators.payroll._default.beans.DefaultPayroll.DefaultPayrollBuilder;
import com.esferalia.aon.in.payroll.pdf.creators.payroll.commons.Accrual;
import com.esferalia.aon.in.payroll.pdf.creators.payroll.commons.Deduction;
import com.esferalia.aon.in.payroll.pdf.creators.payroll.commons.PayrollTypes;

public class DetailedPayrollTest {
	
	@Test
	public void random_print_test() {		
		
		System.out.println("\n\n-----------------------------------");
		System.out.println(" PAYROLL CREATOR");
		System.out.println("-----------------------------------");
		System.out.println("\n Starting.....");
		
		DefaultPayrollBuilder builder = new DefaultPayrollBuilder();
		
		//CREATE ACCRUALS
		Map<Integer,ArrayList<Accrual>> ac = new HashMap<Integer,ArrayList<Accrual>>();
		
		for(int i = 0; i < random(5)-1; i++) ac.put((int) i+1,new ArrayList<Accrual>());
		Set<Integer> keys = ac.keySet();
		
		for(Integer key : keys) {
			for (int i = 0; i < random(10)-1; i++) {
				Accrual accrual = new Accrual(random(999),"Descripcion por defecto.");
				ac.get(key).add(accrual);
			}
		}
		System.out.println(" Preparing accruals.....");
		
		///CREATE DEDUCTIONS
		Map<Integer,ArrayList<Deduction>> de = new HashMap<Integer,ArrayList<Deduction>>();
		
		de.put(1,new ArrayList<Deduction>());
		de.put(2,new ArrayList<Deduction>());
		de.put(3,new ArrayList<Deduction>());
		de.put(4,new ArrayList<Deduction>());
		de.put(5,new ArrayList<Deduction>());
		
		keys = de.keySet();
		for(Integer key : keys) {
			for (int i = 0; i < random(3); i++) {
				Deduction deduction =
						new Deduction(
								random(100000),
								"Descripcion por defecto",
								random(100)
						);
				de.get(key).add(deduction);
			}
		}
		System.out.println(" Preparing deductions.....");
		
		//CREATE CONTIGENCIES
		Contingency_bases_builder con_builder = new Contingency_bases_builder();
		
		con_builder.setMonthly_amount(Optional.of(9999.99))
		.setExtra_proration_amount(Optional.of(9999.99))
		.setCommon_cont_base(Optional.of(9999.99))
		.setCommon_cont_type(Optional.of(99.99))
		.setCommon_cont_ap_enterprise(Optional.of(9999.99))
		.setProfessional_cont_base(Optional.of(9999.99))
		.setAt_ep_type(Optional.of(99.99))
		.setAt_ep_ap_enterprise(Optional.of(9999.99))
		.setUnemployment_type(Optional.of(99.99))
		.setUnemployment_ap_enterprise(Optional.of(9999.99))
		.setProfes_form_type(Optional.of(99.99))
		.setProfes_form_ap_enterprise(Optional.of(9999.99))
		.setFogasa_type(Optional.of(99.99))
		.setFogasa_ap_enterprise(Optional.of(9999.99))
		.setForce_majeure_base(Optional.of(9999.99))
		.setForce_majeure_type(Optional.of(99.99))
		.setForce_majeure_ap_enterprise(Optional.of(9999.99))
		.setNo_struct_base(Optional.of(9999.99))
		.setNo_struct_type(Optional.of(99.99))
		.setNo_struct_ap_enterprise(Optional.of(9999.99))
		.setIrpf_esp(Optional.of(99999.99))
		.setIrpf_retrib_diner(Optional.of(99999.99))
		.setTotal(Optional.of(99999.99));
		System.out.println(" Setting up contingencies.....");
		
		//BUILD PAYROLL
		builder
		.setEnterprise(Optional.of("DEMO EMPRESA HERMANOS DE LA PAZ Y ASOCIADOS S.L"))
		.setAddress(Optional.of("Calle Duque de Wellington, 522 (01010)"))
		.setAddress_2(Optional.of("Vitoria-Gazteiz"))
		.setCif(Optional.of("58595859M"))
		.setCcc(Optional.of("8935713546370"))
		.setEmployee(Optional.of("Iker Gónzalez Con Apellido Inventado de la Fuente Pérez Abech"))
		.setNif(Optional.of("47227931-F"))
		.setNss(Optional.of("11004767999"))
		.setProfessional_group(Optional.of("Director"))
		.setQuotation_group(Optional.of("01"))
		.setAntiquity(Optional.of(new Date()))
		.setLiquid_period_start(Optional.of(new Date()))
		.setLiquid_period_end(Optional.of(new Date()))
		.setTotal_days(Optional.of(30))
		.setAccruals(Optional.of(ac))
		.setDeductions(Optional.of(de))
		.setAccrual_total(Optional.of(99999.99))
		.setDeduction_total(Optional.of(9999.99))
		.setPayroll_total(Optional.of(9999.99))
		.setPayrollType(Optional.of(PayrollTypes.Type.EXTRAS))
		.setContingencies(Optional.of(con_builder.build()));
		
		try { 
			System.out.println(" Printing PDF file..... \n");
			PdfMaker.print_default_payroll(new ByteArrayOutputStream(), builder.build(), DetailedPayrollTest.class.getResourceAsStream("logo.png"),new Locale("Es"));
			System.out.println(" >> DONE.");
		} 
		catch (CanNotCreatePdfException e) {
			e.printStackTrace(); 
			fail("Can not create the payroll");
		} 
	}
	

	//RANDOM BETWEEN 0 AND Y
	public double random(double y){
		//return 3d;
		Double r = 1 + Math.random()*(y-1);
		return r;
	}
}
