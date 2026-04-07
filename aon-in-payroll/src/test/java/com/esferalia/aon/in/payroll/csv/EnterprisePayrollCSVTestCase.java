package com.esferalia.aon.in.payroll.csv;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.model.type.SalaryType;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.javafaker.Faker;
import com.github.javafaker.Number;
import com.github.javafaker.Pokemon;

//@Ignore
public class EnterprisePayrollCSVTestCase {
	
	
	
	public static class EnterprisePayroll implements IEnterprisePayroll {
		
		@JsonProperty("Empleado")
		private String employee;
		@JsonProperty("Categoria Profesional")
		private String employeeCategory;
		@JsonProperty("Centro de trabajo")
		private String workplace;
		@JsonProperty("Tipo")
		private SalaryType salaryType;
		
		
		@JsonProperty("Bruto")
		private Double raw;
		@JsonProperty("Seg. Social empleado")
		private Double employeeSS;
		@JsonProperty("IRPF")
		private Double irpf;
		@JsonProperty("L\u00edquido")
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
		@JsonProperty("Base IRPF Mon.")
		private Double moneyIrpfBase;
		@JsonProperty("Base IRPF Esp.")
		private Double inKindIrpfBase;
		
		@JsonProperty("Contingencias comunes empleado")
		private Double cgc;
		@JsonProperty("Contingencias profesionales empleado")
		private Double cgp;
		@JsonProperty("Desempleo")
		private Double unemployment;
		@JsonProperty("Formaci\u00f3n profesional empleado")
		private Double jobTraining;
		@JsonProperty("Anticipos")
		private Double advancedPayment;
		@JsonProperty("Otras deducciones empleado")
		private Double otherDeductions;
		@JsonProperty("Fuerza mayor empleado")
		private Double estruc;
		@JsonProperty("No estructurales empleado")
		private Double noEstruct;
		@JsonProperty("Embargos")
		private Double embargos;
		
		@JsonProperty("Cont. com. empresa")
		private Double cgcEnterprise;
		@JsonProperty("Cont. prof. empresa")
		private Double cgpEnterprise;
		@JsonProperty("Desempleo empresa")
		private Double unemploymentEnterprise;
		@JsonProperty("Form. prof. empresa")
		private Double jobTrainingEnterprise;
		@JsonProperty("Fogasa")
		private Double fogasaEnterprise;
		@JsonProperty("Fuerza mayor empresa")
		private Double estrucEnterprise;
		@JsonProperty("No estructurales empresa")
		private Double noEstructEnterprise;

		
		
		@Override
		public String getEmployee() {
			return employee;
		}
		
		@Override
		public String getEmployeeCategory() {
			return employeeCategory;
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

		@Override
		public Double getCgp() {
			return cgp;
		}

		@Override
		public Double getAdvancedPayment() {
			return advancedPayment;
		}

		@Override
		public Double getOtherDeductions() {
			return otherDeductions;
		}

		@Override
		public Double getCgcEnterprise() {
			return cgcEnterprise;
		}

		@Override
		public Double getCgpEnterprise() {
			return cgpEnterprise;
		}

		@Override
		public Double getUnemploymentEnterprise() {
			return unemploymentEnterprise;
		}

		@Override
		public Double getJobTrainingEnterprise() {
			return jobTrainingEnterprise;
		}

		@Override
		public Double getFogasaEnterprise() {
			return fogasaEnterprise;
		}

		@Override
		public Double getEstrucEnterprise() {
			return estrucEnterprise;
		}

		@Override
		public Double getNoEstructEnterprise() {
			return noEstructEnterprise;
		}

		@Override
		public SalaryType getSalaryType() {
			return salaryType;
		}

		@Override
		public Double getEstruc() {
			return estruc;
		}

		@Override
		public Double getNoEstruct() {
			return noEstruct;
		}

		@Override
		public Double getEmbargos() {
			return embargos;
		}

		@Override
		public Double getInkindIrpfBase() {
			return inKindIrpfBase;
		}

		@Override
		public Double getMoneyIrpfBase() {
			return moneyIrpfBase;
		}

		@Override
		public Date getStartDate() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Date getEndDate() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public IEnterprisePayroll getOriginalPayroll() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public IEnterprisePayroll setOriginalPayroll(IEnterprisePayroll originalPayroll) {
			// TODO Auto-generated method stub
			return this;
		}

		@Override
		public String getEmployeeNaf() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getCcc() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Double getFundae() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void setFundae(Double fundae) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Double getItCompensation() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Integer getEmployeeId() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Double getInKind() {
			// TODO Auto-generated method stub
			return null;
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
				enterprisePayroll.salaryType = typeOf(((Double)number.randomDouble(0, 0, 3)).byteValue(), SalaryType.class);
				enterprisePayroll.raw = number.randomDouble(2, 0, 2000);
				enterprisePayroll.employeeSS = number.randomDouble(2, 0, 2000);
				enterprisePayroll.irpf = number.randomDouble(2, 0, 2000);
				enterprisePayroll.liquid = number.randomDouble(2, 0, 2000);
				enterprisePayroll.enterpriseSS = number.randomDouble(2, 0, 2000);
				enterprisePayroll.totalCost = number.randomDouble(2, 0, 2000);
				enterprisePayroll.totalSS = number.randomDouble(2, 0, 2000);
				enterprisePayroll.bonuses = number.randomDouble(2, 0, 2000);
				enterprisePayroll.cgcBase = number.randomDouble(2, 0, 2000);
				enterprisePayroll.irpfBase = number.randomDouble(2, 0, 2000);
				enterprisePayroll.inKindIrpfBase = number.randomDouble(2, 0, 2000);
				enterprisePayroll.moneyIrpfBase = number.randomDouble(2, 0, 2000);
				enterprisePayroll.cgc = number.randomDouble(2, 0, 2000);
				enterprisePayroll.cgp = number.randomDouble(2, 0, 2000);
				enterprisePayroll.unemployment = number.randomDouble(2, 0, 2000);
				enterprisePayroll.jobTraining = number.randomDouble(2, 0, 2000);
				enterprisePayroll.advancedPayment = number.randomDouble(2, 0, 2000);
				enterprisePayroll.otherDeductions = number.randomDouble(2, 0, 2000);
				enterprisePayroll.estruc = number.randomDouble(2, 0, 2000);
				enterprisePayroll.noEstruct = number.randomDouble(2, 0, 2000);
				enterprisePayroll.embargos = number.randomDouble(2, 0, 2000);
				enterprisePayroll.cgcEnterprise = number.randomDouble(2, 0, 2000);
				enterprisePayroll.cgpEnterprise = number.randomDouble(2, 0, 2000);
				enterprisePayroll.unemploymentEnterprise = number.randomDouble(2, 0, 2000);
				enterprisePayroll.jobTrainingEnterprise = number.randomDouble(2, 0, 2000);
				enterprisePayroll.fogasaEnterprise = number.randomDouble(2, 0, 2000);
				enterprisePayroll.estrucEnterprise = number.randomDouble(2, 0, 2000);
				enterprisePayroll.noEstructEnterprise = number.randomDouble(2, 0, 2000);
				
				
				payrollList.add(enterprisePayroll);
			}
		}
		
		try {
			EnterprisePayrollCSV.write(System.out/*new FileOutputStream("src/test/resources/com/esferalia/aon/in/payroll/csv/prueba.csv")*/, payrollList);
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}
	
	
	private static <T extends Enum<?>> T typeOf(Byte ordinal, Class<T> type) {
		if ( ordinal == null )
			return null;
		try {
			return type.getEnumConstants()[ordinal];
		} catch ( Exception e ) {
			return null;
		}
	}

}
