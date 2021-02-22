package com.esferalia.aon.in.payroll.csv;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.SalaryBonus.SALARY_BONUS;
import static com.esferalia.aon.jooq.tables.SalaryDeduction.SALARY_DEDUCTION;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.DSLContext;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
public class EnterprisePayrollCSV {
	public static void write(OutputStream outputStream, Collection<IEnterprisePayroll> payrolls) throws JsonGenerationException, JsonMappingException, IOException {
		
		
		
		CsvSchema schema = CsvSchema.builder()
		        .addColumn("Empleado", CsvSchema.ColumnType.STRING)
		        .addColumn("Centro de trabajo", CsvSchema.ColumnType.STRING)
		        .addColumn("Bruto", CsvSchema.ColumnType.NUMBER)
		        .addColumn("Seg. Social empleado", CsvSchema.ColumnType.NUMBER)
		        .addColumn("IRPF", CsvSchema.ColumnType.NUMBER)
		        .addColumn("Líquido", CsvSchema.ColumnType.NUMBER)
		        .addColumn("Seg. Social empresa", CsvSchema.ColumnType.NUMBER)
		        .addColumn("Coste total", CsvSchema.ColumnType.NUMBER)
		        .addColumn("Seg. Social total", CsvSchema.ColumnType.NUMBER)
		        .addColumn("Bonificaciones", CsvSchema.ColumnType.NUMBER)
		        .addColumn("Base Cont. Comunes", CsvSchema.ColumnType.NUMBER)
		        .addColumn("Base IRPF", CsvSchema.ColumnType.NUMBER)
		        .addColumn("Contingencias comunes", CsvSchema.ColumnType.NUMBER)
		        .addColumn("Desempleo", CsvSchema.ColumnType.NUMBER)
		        .addColumn("Formación profesional", CsvSchema.ColumnType.NUMBER)
		        .setUseHeader(true)
		        .build();
		
		
		CsvMapper mapper = new CsvMapper();
		
		mapper.writer(schema).writeValue(outputStream, payrolls);
	}
	
	
	
	
//	public static Stream<EnterprisePayroll> getEnterprisePayrolls(DSLContext ctx, Condition condition)
//			throws IOException {
//
//		Map<Integer, Double> bonusesMap = ctx.select().from(SALARY).innerJoin(SALARY_BONUS).onKey().where(condition)
//				.fetchStreamInto(SALARY_BONUS)
//				.collect(Collectors.toMap(s -> s.getSalary(), s -> s.getAmount(), (a1, a2) -> a1 + a2));
//		
////		Map<Integer, Map<String, Double>> deductions = ctx.select().from(SALARY).innerJoin(SALARY_DEDUCTION).onKey()
////				.where(condition).fetchStream().collect(Collectors.toMap(s -> s.get(SALARY_DEDUCTION.SALARY),
////						s -> Collectors.toMap(s.get(SALARY_DEDUCTION.DEDUCTION_CONCEPT), s.get(SALARY_DEDUCTION.AMOUNT))));
//
////		Map<Integer, Map<String, Double>> deductions = ctx.select().from(SALARY).innerJoin(SALARY_DEDUCTION).onKey()
////				.where(condition).fetchStream().collect(Collectors.groupingBy(s -> s.get(SALARY.ID), 
////						Collectors.toMap(s -> s.get(SALARY.ID), s -> Collectors.toMap(s -> s.get(SALARY_DEDUCTION.DESCRIPTION), s -> s.get(SALARY_DEDUCTION.AMOUNT)))));
//
//		Map<Integer, Map<String, Double>> deductions = new HashMap<Integer, Map<String, Double>>();
//		ctx.select().from(SALARY).innerJoin(SALARY_DEDUCTION).onKey().where(condition).fetchStream().forEach(s -> {
//			if (deductions.get(s.get(SALARY.ID)) != null) {
//				deductions.get(s.get(SALARY.ID)).put(s.get(SALARY_DEDUCTION.DEDUCTION_CONCEPT),
//						s.get(SALARY_DEDUCTION.AMOUNT));
//			} else {
//				Map<String, Double> map = new HashMap<String, Double>();
//				map.put(s.get(SALARY_DEDUCTION.DEDUCTION_CONCEPT), s.get(SALARY_DEDUCTION.AMOUNT));
//				deductions.put(s.get(SALARY.ID), map);
//
//			}
//		});
//
//		return ctx.select().from(SALARY).innerJoin(CONTRACT).onKey().innerJoin(WORKPLACE).onKey().where(condition)
//				.fetchStream().map(record -> {
//					EnterprisePayroll enterprisePayroll = new EnterprisePayroll();
//					enterprisePayroll.employee = record.get(SALARY.EMPLOYEE_NAME);
//					enterprisePayroll.workplace = record.get(WORKPLACE.DESCRIPTION);
//
//					enterprisePayroll.irpf = record.get(SALARY.TOTAL_IRPF);
//
//					enterprisePayroll.cgcBase = record.get(SALARY.CGC_BASE);
//					enterprisePayroll.irpfBase = record.get(SALARY.IRPF_BASE);
//
//					enterprisePayroll.raw = record.get(SALARY.TOTAL_PAYMENT);
//					enterprisePayroll.liquid = record.get(SALARY.TOTAL_LIQUID);
//					enterprisePayroll.employeeSS = record.get(SALARY.SOCIAL_SECURITY_CONTRIBUTIONS);
//					enterprisePayroll.enterpriseSS = record.get(SALARY.TOTAL_ENTERPRISE);
//					enterprisePayroll.totalSS = enterprisePayroll.employeeSS + enterprisePayroll.enterpriseSS;
//					enterprisePayroll.totalCost = enterprisePayroll.totalSS + enterprisePayroll.irpf;
//
//					enterprisePayroll.bonuses = bonusesMap.get(record.get(SALARY.ID)) != null
//							? bonusesMap.get(record.get(SALARY.ID))
//							: 0d;
//
//					Map<String, Double> map = deductions.get(record.get(SALARY.ID));
//					if (map != null) {
//						enterprisePayroll.cgc = map.get("CGC") != null ? map.get("CGC") : 0d;
//						enterprisePayroll.unemployment = map.get("DESMPL") != null ? map.get("DESMPL") : 0d;
//						enterprisePayroll.jobTraining = map.get("FP") != null ? map.get("FP") : 0d;
//					}
//
//					return enterprisePayroll;
//				});
//	}
	
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

		Map<Integer, Map<String, Double>> deductions = new HashMap<Integer, Map<String, Double>>();
		ctx.select().from(SALARY).innerJoin(CONTRACT).on(CONTRACT.ID.eq(SALARY.CONTRACT)).innerJoin(WORKPLACE)
				.on(CONTRACT.WORKPLACE.eq(WORKPLACE.ID)).innerJoin(SALARY_DEDUCTION)
				.on(SALARY.ID.eq(SALARY_DEDUCTION.SALARY)).where(condition).fetchStream().forEach(s -> {
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
					enterprisePayroll.employeeSS = record.get(SALARY.SOCIAL_SECURITY_CONTRIBUTIONS);
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
	
	
	
}
