package com.esferalia.aon.in.payroll.csv;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.Ignore;
import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.javafaker.Faker;
import com.github.javafaker.Number;
import com.github.javafaker.Pokemon;

//@Ignore
public class EnterprisePayrollCSVTestCase {
	
	
	
	public static class EnterprisePayroll implements IEnterprisePayroll {
		
		@JsonProperty("Empleado")
		private String employee;
		@JsonProperty("Centro de trabajo")
		private String workplace;
		@JsonProperty("Bruto")
		private Double raw;
		@JsonProperty("Seg. Social empleado")
		private Double employeeSS;
		@JsonProperty("IRPF")
		private Double irpf;
		@JsonProperty("Líquido")
		private Double liquid;
		@JsonProperty("Seg. Social empresa")
		private Double enterpriseSS;
		@JsonProperty("Coste total")
		private Double totalCost;
		@JsonProperty("Seg. Social total")
		private Double totalSS;
		@JsonProperty("Bonificaciones")
		private Double bonuses;
		
		@JsonProperty("Base Cont. Comunes")
		private Double cgcBase;
		@JsonProperty("Base IRPF")
		private Double irpfBase;
		
		@JsonProperty("Contingencias comunes")
		private Double cgc;
		@JsonProperty("Desempleo")
		private Double unemployment;
		@JsonProperty("Formación profesional")
		private Double jobTraining;
		
		@Override
		public String getEmployee() {
			return employee;
		}

		@Override
		public String getWorkplace() {			
			return workplace;
		}

		@Override
		public Double getRaw() {			
			return raw;
		}

		@Override
		public Double getEmployeeSS() {			
			return employeeSS;
		}

		@Override
		public Double getIrpf() {			
			return irpf;
		}

		@Override
		public Double getLiquid() {			
			return liquid;
		}

		@Override
		public Double getEnterpriseSS() {			
			return enterpriseSS;
		}

		@Override
		public Double getTotalCost() {			
			return totalCost;
		}

		@Override
		public Double getTotalSS() {			
			return totalSS;
		}

		@Override
		public Double getBonuses() {			
			return bonuses;
		}

		@Override
		public Double getCgcBase() {
			return cgcBase;
		}

		@Override
		public Double getIrpfBase() {
			return irpfBase;
		}

		@Override
		public Double getCgc() {
			return cgc;
		}

		@Override
		public Double getUnemployment() {
			return unemployment;
		}

		@Override
		public Double getJobTraining() {
			return jobTraining;
		}
		
	}
	
	
	@Test
	//@Ignore
	public void testWrite() {
		Pokemon pokemon = Faker.instance().pokemon();
		Number number = Faker.instance().number();
		ArrayList<IEnterprisePayroll> payrollList = new ArrayList<IEnterprisePayroll>();
		
		for(int i = 0; i < number.numberBetween(2, 10); i++) {
			String workplace = pokemon.location();
			for (int j = 0; j < number.numberBetween(5, 15); j++) {
				String employee = pokemon.name();
				EnterprisePayroll enterprisePayroll = new EnterprisePayroll();
				enterprisePayroll.workplace = workplace;
				enterprisePayroll.employee = employee;
				enterprisePayroll.raw = number.randomDouble(2, 0, 2000);
				enterprisePayroll.employeeSS = number.randomDouble(2, 0, 2000);
				enterprisePayroll.irpf = number.randomDouble(2, 0, 2000);
				enterprisePayroll.liquid = number.randomDouble(2, 0, 2000);
				enterprisePayroll.enterpriseSS = number.randomDouble(2, 0, 2000);
				enterprisePayroll.totalCost = number.randomDouble(2, 0, 2000);
				enterprisePayroll.totalSS = number.randomDouble(2, 0, 2000);
				enterprisePayroll.bonuses = number.randomDouble(2, 0, 2000);
				payrollList.add(enterprisePayroll);
			}
		}
		
		try {
			EnterprisePayrollCSV.write(System.out/*new FileOutputStream("src/test/resources/com/esferalia/aon/in/payroll/csv/prueba.csv")*/, payrollList);
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}
	
	

}
