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

@Ignore
public class EnterprisePayrollCSVTestCase {
	
	
	
	public static class EnterprisePayroll implements IEnterprisePayroll {
		
		@JsonProperty("Empleado")
		private String employee;
		@JsonProperty("Centro de trabajo")
		private String workplace;
		@JsonProperty("Bruto")
		private double raw;
		@JsonProperty("Seg. Social empleado")
		private double employeeSS;
		@JsonProperty("IRPF")
		private double irpf;
		@JsonProperty("Líquido")
		private double liquid;
		@JsonProperty("Seg. Social empresa")
		private double enterpriseSS;
		@JsonProperty("Coste total")
		private double totalCost;
		@JsonProperty("Seg. Social total")
		private double totalSS;
		@JsonProperty("Bonificaciones")
		private double bonuses;
		
		@JsonProperty("Base Cont. Comunes")
		private double cgcBase;
		@JsonProperty("Base IRPF")
		private double irpfBase;
		
		@Override
		public String getEmployee() {
			return employee;
		}

		@Override
		public String getWorkplace() {			
			return workplace;
		}

		@Override
		public double getRaw() {			
			return raw;
		}

		@Override
		public double getEmployeeSS() {			
			return employeeSS;
		}

		@Override
		public double getIrpf() {			
			return irpf;
		}

		@Override
		public double getLiquid() {			
			return liquid;
		}

		@Override
		public double getEnterpriseSS() {			
			return enterpriseSS;
		}

		@Override
		public double getTotalCost() {			
			return totalCost;
		}

		@Override
		public double getTotalSS() {			
			return totalSS;
		}

		@Override
		public double getBonuses() {			
			return bonuses;
		}

		@Override
		public double getCgcBase() {
			return cgcBase;
		}

		@Override
		public double getIrpfBase() {
			return irpfBase;
		}
		
	}
	
	
	@Test
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
