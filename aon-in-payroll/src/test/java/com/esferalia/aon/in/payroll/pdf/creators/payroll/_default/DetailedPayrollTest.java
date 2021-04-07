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

import com.esferalia.aon.in.payroll.pdf.maker.PdfMaker;
import com.esferalia.aon.in.payroll.pdf.maker.exceptions.CanNotCreatePdfException;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.beans.Accrual;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.beans.Deduction;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.beans.PayrollTypes;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.beans.ContingencyBases.ContingencyBasesBuilder;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.beans.DefaultPayroll.DefaultPayrollBuilder;

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
		ContingencyBasesBuilder con_builder = new ContingencyBasesBuilder();
		
		con_builder.setMonthlyAmount(Optional.of(9999.99))
		.setExtraProrationAmount(Optional.of(9999.99))
		.setCommonContBase(Optional.of(9999.99))
		.setCommonContType(Optional.of(99.99))
		.setCommonContApEnterprise(Optional.of(9999.99))
		.setProfessionalContBase(Optional.of(9999.99))
		.setAtEpType(Optional.of(99.99))
		.setAtEpApEnterprise(Optional.of(9999.99))
		.setUnemploymentType(Optional.of(99.99))
		.setUnemploymentApEnterprise(Optional.of(9999.99))
		.setProfesFormType(Optional.of(99.99))
		.setProfesFormApEnterprise(Optional.of(9999.99))
		.setFogasaType(Optional.of(99.99))
		.setFogasaApEnterprise(Optional.of(9999.99))
		.setForceMajeureBase(Optional.of(9999.99))
		.setForceMajeureType(Optional.of(99.99))
		.setForceMajeureApEnterprise(Optional.of(9999.99))
		.setNoStructBase(Optional.of(9999.99))
		.setNoStructType(Optional.of(99.99))
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
			PdfMaker.printDefaultPayroll(new ByteArrayOutputStream(), builder.build(), DetailedPayrollTest.class.getResourceAsStream("logo.png"),new Locale("Es"));
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
