package com.esferalia.aon.in.payroll.excel;

import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.SalaryBonus.SALARY_BONUS;
import static org.jooq.impl.DSL.sum;
import static org.junit.Assert.fail;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Optional;

import org.jooq.DSLContext;
import org.jooq.Record1;
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

		private double raw;
		private double employeeSS;
		private double irpf;
		private double liquid;
		private double enterpriseSS;
		private double totalCost;
		private double totalSS;
		private double bonuses;

		private double cgcBase;
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
			EnterprisePayrollExcel.write(new FileOutputStream("src/test/resources/com/esferalia/aon/in/payroll/excel/prueba1.xlsx"),
					payrollList, Optional.empty());
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}

	@Test
	@Ignore
	public void testWriteFromDB() {
		try (Connection cn = DriverManager.getConnection("jdbc:mysql://172.17.0.2:3306/ayudat-aonsolutions-net", "root",
				"root"); DSLContext context = DSL.using(cn); DSLContext contextBonus = DSL.using(cn)) {

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
						Record1<BigDecimal> bonus = contextBonus.select(sum(SALARY_BONUS.AMOUNT)).from(SALARY_BONUS)
								.where(SALARY_BONUS.SALARY.eq(record.getValue(SALARY.ID))).fetchAny();
						System.out.println(bonus.get(0));
						payroll.bonuses = 0d;
						payrolls.add(payroll);
					});

			try {
				EnterprisePayrollExcel.write(new FileOutputStream("src/test/resources/com/esferalia/aon/in/payroll/excel/prueba2.xlsx"),
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
	public void testWriteFromDBnotJooq() {
		String sql = "select * from salary";
		String preparedSql = "select sum(amount) as bonus from salary_bonus where salary=?";
		try (Connection cn = DriverManager.getConnection("jdbc:mysql://172.17.0.2:3306/ayudat-aonsolutions-net", "root",
				"root"); Statement st = cn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
			PreparedStatement ps = cn.prepareStatement(preparedSql);
			ArrayList<IEnterprisePayroll> payrolls = new ArrayList<IEnterprisePayroll>();
			while (rs.next()) {
				EnterprisePayroll payroll = new EnterprisePayroll();
				payroll.raw = rs.getDouble("total_payment");
				payroll.cgcBase = rs.getDouble("cgc_base");
				payroll.employee = rs.getString("employee_name");
				payroll.employeeSS = rs.getDouble("social_security_contributions");
				payroll.enterpriseSS = rs.getDouble("total_enterprise") - rs.getDouble("total_payment");
				payroll.irpf = rs.getDouble("total_irpf");
				payroll.irpfBase = rs.getDouble("irpf_base");
				payroll.liquid = rs.getDouble("total_liquid");
				payroll.totalCost = rs.getDouble("total_enterprise");
				payroll.totalSS = payroll.employeeSS + payroll.enterpriseSS;
				payroll.workplace = rs.getString("enterprise_name");

				ps.setInt(1, rs.getInt("id"));
				try (ResultSet rs1 = ps.executeQuery()) {
					payroll.bonuses = rs1.next() ? rs1.getDouble("bonus") : 0d;
				}
				payrolls.add(payroll);
			}

			try {
				EnterprisePayrollExcel.write(new FileOutputStream("src/test/resources/com/esferalia/aon/in/payroll/excel/prueba3.xlsx"),
						payrolls, Optional.empty());
			} catch (IOException e) {
				fail(e.getMessage());
			}
		} catch (SQLException e) {
			fail("SQL FAIL: "+e.getMessage());
		}
	}

}
