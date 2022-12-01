package com.esferalia.aon.in.payroll.excel;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;
import java.util.stream.IntStream;

import org.junit.Test;

import com.esferalia.aon.in.payroll.csv.IEnterprisePayroll;
import com.esferalia.aon.occam.api.model.type.SalaryType;
import com.github.javafaker.Faker;
import com.github.javafaker.Number;
import com.github.javafaker.Pokemon;

//@Ignore
public class EnterprisePayrollExcelTestCase {

	public static class EnterprisePayroll implements IEnterprisePayroll {
		private Integer employeeId;
		private String employee;
		private String workplace;
		private SalaryType salaryType;

		private Double raw;
		private Double employeeSS;
		private Double irpf;
		private Double liquid;
		private Double enterpriseSS;
		private Double totalCost;
		private Double totalSS;
		private Double bonuses;

		private Double cgcBase;
		private Double irpfBase;
		private Double moneyIrpfBase;
		private Double inKindIrpfBase;

		private Double cgc;
		private Double cgp;
		private Double unemployment;
		private Double jobTraining;
		private Double advancedPayment;
		private Double otherDeductions;
		private Double estruc;
		private Double noEstruct;
		private Double embargos;

		private Double cgcEnterprise;
		private Double cgpEnterprise;
		private Double unemploymentEnterprise;
		private Double jobTrainingEnterprise;
		private Double fogasaEnterprise;
		private Double estrucEnterprise;
		private Double noEstructEnterprise;

		@Override
		public String getEmployee() {
			return employee;
		}

		@Override
		public String getWorkplace() {
			return workplace;
		}
		
		@Override
		public SalaryType getSalaryType() {
			return salaryType;
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
		public Double getCgp() {
			return cgp;
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
			return employeeId;
		}

		@Override
		public Double getInKind() {
			// TODO Auto-generated method stub
			return null;
		}

	}
	
	@Test
//	@Ignore
	public void testWriteComplete() {
		IntStream.range(0, 40).forEach(num -> testWriteSingle(ExcelType.COMPLETE));
	}
	@Test
//	@Ignore
	public void testWriteSummary() {
		IntStream.range(0, 40).forEach(num -> testWriteSingle(ExcelType.SUMMARY));
	}
	
	public void testWriteSingle(ExcelType type) {
		Pokemon pokemon = Faker.instance().pokemon();
		Number number = Faker.instance().number();
		ArrayList<IEnterprisePayroll> payrollList = new ArrayList<IEnterprisePayroll>();

		for (int i = 0; i < number.numberBetween(2, 10); i++) {
			String workplace = pokemon.location();
			for (int j = 0; j < number.numberBetween(5, 15); j++) {
				String employee = pokemon.name();
				EnterprisePayroll enterprisePayroll = new EnterprisePayroll();
				enterprisePayroll.workplace = workplace;
				enterprisePayroll.salaryType = typeOf(((Double)number.randomDouble(0, 0, SalaryType.L13.ordinal())).byteValue() , SalaryType.class);
				if (number.numberBetween(1, 100) <= 20) {
					enterprisePayroll.salaryType = null;
				}
				enterprisePayroll.employee = employee;
				enterprisePayroll.employeeId = (int) number.randomNumber();
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
				payrollList.add(enterprisePayroll);
			}
		}

		try {
			
			EnterprisePayrollExcel.write(
					OutputStream.nullOutputStream(),
//					new FileOutputStream(MessageFormat.format("./ExcelPayrollTest{0}.xls", type == ExcelType.COMPLETE ? "Complete" : "Summary")),
					payrollList, Optional.empty(), "", "",type);
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
