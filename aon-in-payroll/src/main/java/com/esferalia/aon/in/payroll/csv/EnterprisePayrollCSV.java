package com.esferalia.aon.in.payroll.csv;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.SalaryBonus.SALARY_BONUS;
import static com.esferalia.aon.jooq.tables.SalaryDeduction.SALARY_DEDUCTION;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.SalaryRecord;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Salary;
import com.esferalia.aon.occam.api.model.Salary.Bonus;
import com.esferalia.aon.occam.api.model.Salary.Cost;
import com.esferalia.aon.occam.api.model.Salary.Embargo;
import com.esferalia.aon.occam.api.model.type.DeductionType;
import com.esferalia.aon.occam.api.model.type.SalaryType;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator.Feature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
public class EnterprisePayrollCSV {
	public static void write(OutputStream outputStream, Collection<IEnterprisePayroll> payrolls) throws JsonGenerationException, JsonMappingException, IOException {
		
		
		
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
		        .setUseHeader(true)
		        .build();
		
		
		CsvMapper mapper = new CsvMapper();
		
		mapper.configure(Feature.IGNORE_UNKNOWN, true);
		mapper.writer(schema).writeValue(outputStream, payrolls);
	}
	
	
	public static Stream<EnterprisePayroll> getEnterprisePayrolls(AONContext aonContext, final int month,
			final int year, Integer enterpriseId, Integer workplaceId) throws IOException {

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
					.mapToDouble(d -> d.getAmount()).sum();
			Double cgp = s.getDeductions().stream()
					.filter(d -> d.getDeductionType().ordinal() == DeductionType.IT.ordinal()
							|| d.getDeductionType().ordinal() == DeductionType.IMS.ordinal())
					.mapToDouble(d -> d.getAmount()).sum();
			Double unemployment = s.getDeductions().stream()
					.filter(d -> d.getDeductionType().ordinal() == DeductionType.UNEMPLOYMENT.ordinal())
					.mapToDouble(d -> d.getAmount()).sum();
			Double jobTraining = s.getDeductions().stream()
					.filter(d -> d.getDeductionType().ordinal() == DeductionType.JOB_TRAINING.ordinal())
					.mapToDouble(d -> d.getAmount()).sum();
			Double advancedPayment = s.getDeductions().stream()
					.filter(d -> d.getDeductionType().ordinal() == DeductionType.ADVANCE_PAYMENT.ordinal())
					.mapToDouble(d -> d.getAmount()).sum();
			Double otherDeductions = s.getDeductions().stream()
					.filter(d -> d.getDeductionType().ordinal() == DeductionType.OTHER.ordinal())
					.mapToDouble(d -> d.getAmount()).sum();
			Double estruc = s.getDeductions().stream()
					.filter(d -> d.getDeductionType().ordinal() == DeductionType.STRUCTURAL_OVERTIME.ordinal())
					.mapToDouble(d -> d.getAmount()).sum();
			Double noEstruct = s.getDeductions().stream()
					.filter(d -> d.getDeductionType().ordinal() == DeductionType.NON_STRUCTURAL_OVERTIME.ordinal())
					.mapToDouble(d -> d.getAmount()).sum();
			Double embargos = s.getEmbargos().stream()
					.mapToDouble(Embargo::getAmount).sum();
			// PICKING UP COSTS
			Double cgcEnterprise = s.getCosts().stream()
					.filter(c -> c.getCostType().ordinal() == DeductionType.COMMON_CONTINGENCY.ordinal())
					.mapToDouble(c -> c.getAmount()).sum();
			Double cgpEnterprise = s.getCosts().stream()
					.filter(c -> c.getCostType().ordinal() == DeductionType.IT.ordinal()
					|| c.getCostType().ordinal() == DeductionType.IMS.ordinal())
					.mapToDouble(c -> c.getAmount()).sum();
			
			Double unemploymentEnterprise = s.getCosts().stream()
					.filter(c -> c.getCostType().ordinal() == DeductionType.UNEMPLOYMENT.ordinal())
					.mapToDouble(c -> c.getAmount()).sum();
			Double jobTrainingEnterprise = s.getCosts().stream()
					.filter(c -> c.getCostType().ordinal() == DeductionType.JOB_TRAINING.ordinal())
					.mapToDouble(c -> c.getAmount()).sum();
			Double fogasaEnterprise = s.getCosts().stream()
					.filter(c -> c.getCostType().ordinal() == DeductionType.FOGASA.ordinal())
					.mapToDouble(c -> c.getAmount()).sum();
			Double estrucEnterprise = s.getCosts().stream()
					.filter(c -> c.getCostType().ordinal() == DeductionType.STRUCTURAL_OVERTIME.ordinal())
					.mapToDouble(c -> c.getAmount()).sum();
			Double noEstrucEnterprise = s.getCosts().stream()
					.filter(c -> c.getCostType().ordinal() == DeductionType.NON_STRUCTURAL_OVERTIME.ordinal())
					.mapToDouble(c -> c.getAmount()).sum();

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
	@Deprecated
	public static Stream<EnterprisePayroll> getEnterprisePayrolls(DSLContext ctx, Condition condition)
			throws IOException {

		Map<Integer, Double> bonusesMap = ctx.select().from(SALARY).innerJoin(CONTRACT)
				.on(CONTRACT.ID.eq(SALARY.CONTRACT)).innerJoin(WORKPLACE).on(CONTRACT.WORKPLACE.eq(WORKPLACE.ID))
				.innerJoin(SALARY_BONUS).on(SALARY.ID.eq(SALARY_BONUS.SALARY)).where(condition)
				.fetchStreamInto(SALARY_BONUS)
				.collect(Collectors.toMap(s -> s.getSalary(), s -> s.getAmount(), (a1, a2) -> a1 + a2));

		System.out.println(ctx.select().from(SALARY).innerJoin(CONTRACT).on(CONTRACT.ID.eq(SALARY.CONTRACT))
				.innerJoin(WORKPLACE).on(CONTRACT.WORKPLACE.eq(WORKPLACE.ID)).innerJoin(SALARY_BONUS)
				.on(SALARY.ID.eq(SALARY_BONUS.SALARY)).where(condition).getSQL());

//		Map<Integer, Map<String, Double>> deductions = ctx.select().from(SALARY).innerJoin(SALARY_DEDUCTION).onKey()
//				.where(condition).fetchStream().collect(Collectors.groupingBy(s -> s.get(SALARY.ID), 
//						Collectors.toMap(s -> s.get(SALARY.ID), s -> Collectors.toMap(s -> s.get(SALARY_DEDUCTION.DESCRIPTION), s -> s.get(SALARY_DEDUCTION.AMOUNT)))));
		
		ArrayList<Double> dedBonuses = new ArrayList<Double>();
		
		Map<Integer, Map<String, Double>> deductions = new HashMap<Integer, Map<String, Double>>();
		ctx.select().from(SALARY).innerJoin(CONTRACT).on(CONTRACT.ID.eq(SALARY.CONTRACT)).innerJoin(WORKPLACE)
				.on(CONTRACT.WORKPLACE.eq(WORKPLACE.ID)).innerJoin(SALARY_DEDUCTION)
				.on(SALARY.ID.eq(SALARY_DEDUCTION.SALARY)).where(condition).fetchStream().forEach(s -> {
					
					
					if (s.get(SALARY_DEDUCTION.TYPE) == null &&
							s.get(SALARY_DEDUCTION.AMOUNT) != null &&
							s.get(SALARY_DEDUCTION.AMOUNT) < 0
						)
						dedBonuses.add(s.get(SALARY_DEDUCTION.AMOUNT));
					
					if (deductions.get(s.get(SALARY.ID)) != null) {
						deductions.get(s.get(SALARY.ID)).put(s.get(SALARY_DEDUCTION.DEDUCTION_CONCEPT),
								s.get(SALARY_DEDUCTION.AMOUNT));
					} else {
						Map<String, Double> map = new HashMap<String, Double>();
						map.put(s.get(SALARY_DEDUCTION.DEDUCTION_CONCEPT), s.get(SALARY_DEDUCTION.AMOUNT));
						deductions.put(s.get(SALARY.ID), map);

					}
				});

		return ctx.select().from(SALARY).innerJoin(CONTRACT).onKey().innerJoin(WORKPLACE).onKey().where(condition)
				.fetchStream().map(record -> {
					EnterprisePayroll enterprisePayroll = new EnterprisePayroll();
					enterprisePayroll.employee = record.get(SALARY.EMPLOYEE_NAME);
					enterprisePayroll.workplace = record.get(WORKPLACE.DESCRIPTION);

					enterprisePayroll.irpf = record.get(SALARY.TOTAL_IRPF);

					enterprisePayroll.cgcBase = record.get(SALARY.CGC_BASE);
					enterprisePayroll.irpfBase = record.get(SALARY.IRPF_BASE);

					enterprisePayroll.raw = record.get(SALARY.TOTAL_PAYMENT);
					enterprisePayroll.liquid = record.get(SALARY.TOTAL_LIQUID);
					
					Double employeeSS = record.get(SALARY.SOCIAL_SECURITY_CONTRIBUTIONS);
					if (!dedBonuses.isEmpty()) {
						Double amount = dedBonuses.stream().mapToDouble(a -> a).sum();
						
						if (employeeSS != null && amount != null)
							employeeSS += amount;
						else if (amount != null)
							employeeSS = amount;
					}
					
					
					enterprisePayroll.employeeSS = employeeSS;
					
					
					
					
					enterprisePayroll.enterpriseSS = record.get(SALARY.TOTAL_ENTERPRISE);
					enterprisePayroll.totalSS = enterprisePayroll.employeeSS + enterprisePayroll.enterpriseSS;
					enterprisePayroll.totalCost = enterprisePayroll.totalSS + enterprisePayroll.irpf;

					enterprisePayroll.bonuses = bonusesMap.get(record.get(SALARY.ID));

					Map<String, Double> map = deductions.get(record.get(SALARY.ID));
					if (map != null) {

						enterprisePayroll.cgc = map.get("CGC");
						enterprisePayroll.unemployment = map.get("DESMPL");
						enterprisePayroll.jobTraining = map.get("FP");
					}

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
		
		
		
	}
	
	
	
}
