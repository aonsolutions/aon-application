package com.esferalia.aon.in.payroll.csv;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.SalaryRecord;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Salary;
import com.esferalia.aon.occam.api.model.Salary.Bonus;
import com.esferalia.aon.occam.api.model.Salary.Cost;
import com.esferalia.aon.occam.api.model.Salary.Deduction;
import com.esferalia.aon.occam.api.model.Salary.Embargo;
import com.esferalia.aon.occam.api.model.type.DeductionType;
import com.esferalia.aon.occam.api.model.type.SalaryType;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator.Feature;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;


public class EnterprisePayrollCSV {
	
	private EnterprisePayrollCSV() {
	    throw new IllegalStateException("Utility class");
	  }
	
	public static void write(OutputStream outputStream, Collection<IEnterprisePayroll> payrolls) throws IOException {
		
		
		
		CsvSchema schema = CsvSchema.builder()
		        .addColumn("Empleado", CsvSchema.ColumnType.STRING)
		        .addColumn("Centro", CsvSchema.ColumnType.STRING)
		        .addColumn("Tipo", CsvSchema.ColumnType.STRING)
		        .addColumn("Bruto", CsvSchema.ColumnType.NUMBER)
		        .addColumn("S.S.Empr.", CsvSchema.ColumnType.NUMBER)
		        .addColumn("Coste total", CsvSchema.ColumnType.NUMBER)
		        .addColumn("S.S.Empl.", CsvSchema.ColumnType.NUMBER)
		        .addColumn("IRPF", CsvSchema.ColumnType.NUMBER)
		        .addColumn("Líquido", CsvSchema.ColumnType.NUMBER)
		        .addColumn("S.S.Total", CsvSchema.ColumnType.NUMBER)
		        .addColumn("C.C.Empr", CsvSchema.ColumnType.NUMBER)
		        .addColumn("C.Prof.Empr", CsvSchema.ColumnType.NUMBER)
		        .addColumn("Desemp.Emp.", CsvSchema.ColumnType.NUMBER)
		        .addColumn("F.P.Empr", CsvSchema.ColumnType.NUMBER)
		        .addColumn("Fogasa", CsvSchema.ColumnType.NUMBER)
		        .addColumn("Fuerza mayor Empr.", CsvSchema.ColumnType.NUMBER)
		        .addColumn("No Estruc.Empr", CsvSchema.ColumnType.NUMBER)
		        .addColumn("Bonif.", CsvSchema.ColumnType.NUMBER)
		        .addColumn("C.C.Empl.", CsvSchema.ColumnType.NUMBER)
		        .addColumn("C.Prof.Empl.", CsvSchema.ColumnType.NUMBER)
		        .addColumn("Desempleo", CsvSchema.ColumnType.NUMBER)
		        .addColumn("F.P.Empl.", CsvSchema.ColumnType.NUMBER)
		        .addColumn("Fuerza Mayor Empl.", CsvSchema.ColumnType.NUMBER)
		        .addColumn("No Estruc.Empl", CsvSchema.ColumnType.NUMBER)
		        .addColumn("Anticipos", CsvSchema.ColumnType.NUMBER)
		        .addColumn("Embargos", CsvSchema.ColumnType.NUMBER)
		        .addColumn("Otr.Ded.Empl", CsvSchema.ColumnType.NUMBER)
		        .addColumn("Base C.C.", CsvSchema.ColumnType.NUMBER)
		        .addColumn("Base IRPF", CsvSchema.ColumnType.NUMBER)
		        .addColumn("Base IRPF Mon.", CsvSchema.ColumnType.NUMBER)
		        .addColumn("Base IRPF Esp.", CsvSchema.ColumnType.NUMBER)
		        .setUseHeader(true)
		        .build();
		
		
		CsvMapper mapper = new CsvMapper();
		
		mapper.configure(Feature.IGNORE_UNKNOWN, true);
		mapper.writer(schema).writeValue(outputStream, payrolls);
	}
	
	
	public static Stream<EnterprisePayroll> getEnterprisePayrolls(AONContext aonContext, final int month,
			final int year, Integer enterpriseId, Integer workplaceId) {

		Condition condition = DSL.year(SALARY.ISSUE_DATE).eq(year).and(DSL.month(SALARY.ISSUE_DATE).eq(month))
				.and(ENTERPRISE.REGISTRY.eq(enterpriseId));
		if (workplaceId != null)
			condition.and(WORKPLACE.ID.eq(workplaceId));

		Map<Integer, String> workplaces = aonContext.getDslContext().select(SALARY.ID, WORKPLACE.DESCRIPTION)
				.from(SALARY).innerJoin(CONTRACT).onKey().innerJoin(WORKPLACE).onKey().innerJoin(ENTERPRISE).onKey()
				.where(condition).fetchStream().collect(HashMap::new,
						(m, v) -> m.put(v.get(SALARY.ID), v.get(WORKPLACE.DESCRIPTION)), HashMap::putAll);

		Collection<Integer> ids = aonContext.getDslContext().select(SALARY.ID, WORKPLACE.DESCRIPTION).from(SALARY)
				.innerJoin(CONTRACT).onKey().innerJoin(WORKPLACE).onKey().innerJoin(ENTERPRISE).onKey().where(condition)
				.fetchStreamInto(SALARY).map(SalaryRecord::getId).collect(Collectors.toList());

		Stream<Salary> salaries = AON.getSalaries(aonContext,
				s -> s.getIdProperty().in(ids.toArray(new Integer[ids.size()])));
		return salaries.map(s -> {

			EnterprisePayroll enterprisePayroll = new EnterprisePayroll();
			enterprisePayroll.employee = s.getEmployeeName();
			enterprisePayroll.workplace = workplaces.get(s.getId());
			enterprisePayroll.salaryType = s.getSalaryType();
			
			switch (s.getSalaryType().ordinal()) {
				case 0:
					enterprisePayroll.salaryTypeStr = "NÓMINA";
					break;
				case 1:
					enterprisePayroll.salaryTypeStr = "EXTRA";
					break;
				case 2:
					enterprisePayroll.salaryTypeStr = "FINIQUITO";
					break;
				case 3:
					enterprisePayroll.salaryTypeStr = "ATRASOS";
					break;
				default:
					enterprisePayroll.salaryTypeStr = "NÓMINA";
					
			}

			enterprisePayroll.irpf = s.getTotalIrpf();

			enterprisePayroll.cgcBase = s.getCommonContingenciesBase();
			enterprisePayroll.irpfBase = s.getIrpfBase();
			enterprisePayroll.inKindIrpfBase = s.getInkindIrpfBase();
			enterprisePayroll.moneyIrpfBase = s.getMoneyIrpfBase();

			enterprisePayroll.raw = s.getTotalPayment();
			enterprisePayroll.liquid = s.getTotalLiquid();
			enterprisePayroll.employeeSS = s.getTotalSSContributions();

			enterprisePayroll.enterpriseSS = s.getCosts().stream().mapToDouble(Cost::getAmount).sum();

			enterprisePayroll.totalSS = enterprisePayroll.employeeSS + enterprisePayroll.enterpriseSS;
			enterprisePayroll.totalCost = enterprisePayroll.enterpriseSS + enterprisePayroll.irpf
					+ enterprisePayroll.raw;
			
			

			enterprisePayroll.bonuses = s.getBonuses().stream().mapToDouble(Bonus::getAmount).sum();
			// PICKING UP DEDUCTIONS
			Double cgc = s.getDeductions().stream()
					.filter(d -> d.getDeductionType().ordinal() == DeductionType.COMMON_CONTINGENCY.ordinal())
					.mapToDouble(Deduction::getAmount).sum();
			Double cgp = s.getDeductions().stream()
					.filter(d -> d.getDeductionType().ordinal() == DeductionType.IT.ordinal()
							|| d.getDeductionType().ordinal() == DeductionType.IMS.ordinal())
					.mapToDouble(Deduction::getAmount).sum();
			Double unemployment = s.getDeductions().stream()
					.filter(d -> d.getDeductionType().ordinal() == DeductionType.UNEMPLOYMENT.ordinal())
					.mapToDouble(Deduction::getAmount).sum();
			Double jobTraining = s.getDeductions().stream()
					.filter(d -> d.getDeductionType().ordinal() == DeductionType.JOB_TRAINING.ordinal())
					.mapToDouble(Deduction::getAmount).sum();
			Double advancedPayment = s.getDeductions().stream()
					.filter(d -> d.getDeductionType().ordinal() == DeductionType.ADVANCE_PAYMENT.ordinal())
					.mapToDouble(Deduction::getAmount).sum();
			Double otherDeductions = s.getDeductions().stream()
					.filter(d -> d.getDeductionType().ordinal() == DeductionType.OTHER.ordinal())
					.mapToDouble(Deduction::getAmount).sum();
			Double estruc = s.getDeductions().stream()
					.filter(d -> d.getDeductionType().ordinal() == DeductionType.STRUCTURAL_OVERTIME.ordinal())
					.mapToDouble(Deduction::getAmount).sum();
			Double noEstruct = s.getDeductions().stream()
					.filter(d -> d.getDeductionType().ordinal() == DeductionType.NON_STRUCTURAL_OVERTIME.ordinal())
					.mapToDouble(Deduction::getAmount).sum();
			Double embargos = s.getEmbargos().stream()
					.mapToDouble(Embargo::getAmount).sum();
			// PICKING UP COSTS
			Double cgcEnterprise = s.getCosts().stream()
					.filter(c -> c.getCostType().ordinal() == DeductionType.COMMON_CONTINGENCY.ordinal())
					.mapToDouble(Cost::getAmount).sum();
			Double cgpEnterprise = s.getCosts().stream()
					.filter(c -> c.getCostType().ordinal() == DeductionType.IT.ordinal()
					|| c.getCostType().ordinal() == DeductionType.IMS.ordinal())
					.mapToDouble(Cost::getAmount).sum();
			
			Double unemploymentEnterprise = s.getCosts().stream()
					.filter(c -> c.getCostType().ordinal() == DeductionType.UNEMPLOYMENT.ordinal())
					.mapToDouble(Cost::getAmount).sum();
			Double jobTrainingEnterprise = s.getCosts().stream()
					.filter(c -> c.getCostType().ordinal() == DeductionType.JOB_TRAINING.ordinal())
					.mapToDouble(Cost::getAmount).sum();
			Double fogasaEnterprise = s.getCosts().stream()
					.filter(c -> c.getCostType().ordinal() == DeductionType.FOGASA.ordinal())
					.mapToDouble(Cost::getAmount).sum();
			Double estrucEnterprise = s.getCosts().stream()
					.filter(c -> c.getCostType().ordinal() == DeductionType.STRUCTURAL_OVERTIME.ordinal())
					.mapToDouble(Cost::getAmount).sum();
			Double noEstrucEnterprise = s.getCosts().stream()
					.filter(c -> c.getCostType().ordinal() == DeductionType.NON_STRUCTURAL_OVERTIME.ordinal())
					.mapToDouble(Cost::getAmount).sum();

			// DEDUCTIONS
			enterprisePayroll.cgc = cgc;
			enterprisePayroll.cgp = cgp;
			enterprisePayroll.unemployment = unemployment;
			enterprisePayroll.jobTraining = jobTraining;
			enterprisePayroll.advancedPayment = advancedPayment;
			enterprisePayroll.otherDeductions = otherDeductions;
			enterprisePayroll.estruc = estruc;
			enterprisePayroll.noEstruct = noEstruct;
			// COSTS
			enterprisePayroll.cgcEnterprise = cgcEnterprise;
			enterprisePayroll.cgpEnterprise = cgpEnterprise;
			enterprisePayroll.unemploymentEnterprise = unemploymentEnterprise;
			enterprisePayroll.jobTrainingEnterprise = jobTrainingEnterprise;
			enterprisePayroll.fogasaEnterprise = fogasaEnterprise;
			enterprisePayroll.estrucEnterprise = estrucEnterprise;
			enterprisePayroll.noEstructEnterprise = noEstrucEnterprise;
			enterprisePayroll.embargos = embargos;

			return enterprisePayroll;
		});
	}
	
	
	public static class EnterprisePayroll implements IEnterprisePayroll {
		
		@JsonProperty("Empleado")
		private String employee;
		@JsonProperty("Centro")
		private String workplace;
		private SalaryType salaryType;
		@JsonProperty("Tipo")
		private String salaryTypeStr;
		
		@JsonProperty("Bruto")
		private Double raw;
		@JsonProperty("S.S.Empl.")
		private Double employeeSS;
		@JsonProperty("IRPF")
		private Double irpf;
		@JsonProperty("Líquido")
		private Double liquid;
		@JsonProperty("S.S.Empr.")
		private Double enterpriseSS;
		@JsonProperty("Coste total")
		private Double totalCost;
		@JsonProperty("S.S.Total")
		private Double totalSS;
		@JsonProperty("Bonif.")
		private Double bonuses;
		
		@JsonProperty("Base C.C.")
		private Double cgcBase;
		@JsonProperty("Base IRPF")
		private Double irpfBase;
		@JsonProperty("Base IRPF Mon.")
		private Double moneyIrpfBase;
		@JsonProperty("Base IRPF Esp.")
		private Double inKindIrpfBase;
		
		@JsonProperty("C.C.Empl.")
		private Double cgc;
		@JsonProperty("C.Prof.Empl.")
		private Double cgp;
		@JsonProperty("Desempleo")
		private Double unemployment;
		@JsonProperty("F.P.Empl.")
		private Double jobTraining;
		@JsonProperty("Anticipos")
		private Double advancedPayment;
		@JsonProperty("Otr.Ded.Empl")
		private Double otherDeductions;
		@JsonProperty("Fuerza Mayor Empl.")
		private Double estruc;
		@JsonProperty("No Estruc.Empl")
		private Double noEstruct;
		@JsonProperty("Embargos")
		private Double embargos;
		
		@JsonProperty("C.C.Empr")
		private Double cgcEnterprise;
		@JsonProperty("C.Prof.Empr")
		private Double cgpEnterprise;
		@JsonProperty("Desemp.Emp.")
		private Double unemploymentEnterprise;
		@JsonProperty("F.P.Empr")
		private Double jobTrainingEnterprise;
		@JsonProperty("Fogasa")
		private Double fogasaEnterprise;
		@JsonProperty("Fuerza mayor Empr.")
		private Double estrucEnterprise;
		@JsonProperty("No Estruc.Empr")
		private Double noEstructEnterprise;

		
		@Override
		public Integer getEmployeeId() {
			return null;
		}
		
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

		public String getSalaryTypeStr() {
			return salaryTypeStr;
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
		public Double getInKind() {
			// TODO Auto-generated method stub
			return null;
		}

		
		
		
	}
	
	
	
}
