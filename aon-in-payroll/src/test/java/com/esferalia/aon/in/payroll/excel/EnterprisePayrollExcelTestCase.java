package com.esferalia.aon.in.payroll.excel;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.SalaryBonus.SALARY_BONUS;
import static com.esferalia.aon.jooq.tables.SalaryDeduction.SALARY_DEDUCTION;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;
import static org.jooq.impl.DSL.sum;
import static org.junit.Assert.fail;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.SQLDialect;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.junit.Ignore;
import org.junit.Test;

import com.esferalia.aon.in.payroll.csv.IEnterprisePayroll;
import com.github.javafaker.Faker;
import com.github.javafaker.Number;
import com.github.javafaker.Pokemon;

@Ignore
public class EnterprisePayrollExcelTestCase {

	public static class EnterprisePayroll implements IEnterprisePayroll {
		private String employee;
		private String workplace;

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

		private Double cgc;
		private Double unemployment;
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
	@Ignore
	public void testWrite() {
		Pokemon pokemon = Faker.instance().pokemon();
		Number number = Faker.instance().number();
		ArrayList<IEnterprisePayroll> payrollList = new ArrayList<IEnterprisePayroll>();

		for (int i = 0; i < number.numberBetween(2, 10); i++) {
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
				enterprisePayroll.cgcBase = number.randomDouble(2, 0, 2000);
				enterprisePayroll.irpfBase = number.randomDouble(2, 0, 2000);
				payrollList.add(enterprisePayroll);
			}
		}

//		DSLContext context = DSL.using(connection);
//		
//		context.select().from(SALARY).ex1

		try {
			EnterprisePayrollExcel.write(
					new FileOutputStream("src/test/resources/com/esferalia/aon/in/payroll/excel/prueba1.xlsx"),
					payrollList, Optional.empty());
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}

	@Test
	@Ignore
	public void testWriteFromDB() {

		try (Connection cn = getConnection(); DSLContext context = DSL.using(cn);) {

			ArrayList<IEnterprisePayroll> payrolls = new ArrayList<IEnterprisePayroll>();
			context.select().from(SALARY.getName())/* .where(SALARY.ENTERPRISE_NAME.notLike("%JUANA%")) */.fetch()
					.stream().forEach(record -> {
						EnterprisePayroll payroll = new EnterprisePayroll();
						payroll.raw = record.getValue(SALARY.TOTAL_PAYMENT);
						payroll.cgcBase = record.getValue(SALARY.CGC_BASE);
						payroll.employee = record.getValue(SALARY.EMPLOYEE_NAME);
						payroll.enterpriseSS = record.getValue(SALARY.TOTAL_ENTERPRISE)
								- record.getValue(SALARY.TOTAL_PAYMENT);
						payroll.employeeSS = record.getValue(SALARY.SOCIAL_SECURITY_CONTRIBUTIONS);
						payroll.irpf = record.getValue(SALARY.TOTAL_IRPF);
						payroll.irpfBase = record.getValue(SALARY.IRPF_BASE);
						payroll.liquid = record.getValue(SALARY.TOTAL_LIQUID);
						payroll.totalCost = record.getValue(SALARY.TOTAL_ENTERPRISE);
						payroll.totalSS = payroll.employeeSS + payroll.enterpriseSS;
						payroll.workplace = record.getValue(SALARY.ENTERPRISE_NAME);
						Record1<BigDecimal> bonus = context.select(sum(SALARY_BONUS.AMOUNT)).from(SALARY_BONUS)
								.where(SALARY_BONUS.SALARY.eq(record.getValue(SALARY.ID))).fetchAny();
						System.out.println(bonus.get(0));
						payroll.bonuses = 0d;
						payrolls.add(payroll);
					});

			try {
				EnterprisePayrollExcel.write(
						new FileOutputStream("src/test/resources/com/esferalia/aon/in/payroll/excel/prueba2.xlsx"),
						payrolls, Optional.empty());
			} catch (IOException e) {
				fail(e.getMessage());
			}
		} catch (SQLException e) {
			e.printStackTrace();
			fail("SQL FAIL");
		}
	}

	@Test
	@Ignore
	public void testWriteFromDBJooq() {

		Settings settings = new Settings();
		settings.setRenderSchema(false);

		try (Connection connection = getConnection();
				DSLContext ctx = DSL.using(connection, SQLDialect.MYSQL, settings)) {
			Condition condition = DSL.year(SALARY.ISSUE_DATE).eq(2020);
			Collection<IEnterprisePayroll> payrolls = getEnterprisePayrolls(ctx, condition)
					.collect(Collectors.toList());

			EnterprisePayrollExcel.write(
					new FileOutputStream("src/test/resources/com/esferalia/aon/in/payroll/excel/prueba3.xlsx"),
					payrolls, Optional.empty());
		} catch (IOException e) {
			fail(e.getMessage());
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	private Connection getConnection() throws SQLException {
		return DriverManager.getConnection("jdbc:mysql://172.17.0.2:3306/ayudat-aonsolutions-net", "root", "root");
	}

	public static Stream<EnterprisePayroll> getEnterprisePayrolls(DSLContext ctx, Condition condition)
			throws IOException {

		Map<Integer, Double> bonusesMap = ctx.select().from(SALARY).innerJoin(SALARY_BONUS).onKey().where(condition)
				.fetchStreamInto(SALARY_BONUS)
				.collect(Collectors.toMap(s -> s.getSalary(), s -> s.getAmount(), (a1, a2) -> a1 + a2));

//		Map<Integer, Map<String, Double>> deductions = ctx.select().from(SALARY).innerJoin(SALARY_DEDUCTION).onKey()
//				.where(condition).fetchStream().collect(Collectors.groupingBy(s -> s.get(SALARY.ID), 
//						Collectors.toMap(s -> s.get(SALARY.ID), s -> Collectors.toMap(s -> s.get(SALARY_DEDUCTION.DESCRIPTION), s -> s.get(SALARY_DEDUCTION.AMOUNT)))));

		Map<Integer, Map<String, Double>> deductions = new HashMap<Integer, Map<String, Double>>();
		ctx.select().from(SALARY).innerJoin(SALARY_DEDUCTION).onKey().where(condition).fetchStream().forEach(s -> {
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

					enterprisePayroll.bonuses = bonusesMap.get(record.get(SALARY.ID)) != null
							? bonusesMap.get(record.get(SALARY.ID))
							: 0d;

					Map<String, Double> map = deductions.get(record.get(SALARY.ID));
					if (map != null) {
						if (map.get("CGC") != null) {
							enterprisePayroll.cgc = map.get("CGC");
						} else {
							enterprisePayroll.cgc = 0d;
						}

						if (map.get("DESMPL") != null) {
							enterprisePayroll.unemployment = map.get("DESMPL");
						} else {
							enterprisePayroll.unemployment = 0d;
						}

						if (map.get("FP") != null) {
							enterprisePayroll.jobTraining = map.get("FP");
						} else {
							enterprisePayroll.jobTraining = 0d;
						}
					}

					return enterprisePayroll;
				});
	}

}
