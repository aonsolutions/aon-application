package net.aonsolutions.payroll.report;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.EnterpriseData.ENTERPRISE_DATA;
import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.SalaryCost.SALARY_COST;
import static com.esferalia.aon.jooq.tables.SalaryDeduction.SALARY_DEDUCTION;
import static com.esferalia.aon.jooq.tables.SalaryPayment.SALARY_PAYMENT;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;

import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record3;
import org.jooq.Result;
import org.jooq.exception.DataAccessException;

import com.esferalia.aon.jooq.tables.Raddress;
import com.esferalia.aon.jooq.tables.SalaryCost;
import com.esferalia.aon.jooq.tables.SalaryDeduction;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.salary.enumeration.SalaryType;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class SalaryReport {
	
	
	public  static ThreadLocal<DSLContext> DSLCONTEXT = new ThreadLocal<DSLContext>();
	
	private static final String REPORT_LIKE_PARAM = "PAY_REPORT_%_PAY";
	private static final String REPORT_FORMAT = "/com/esferalia/aon/ui/payroll/report/%s.jasper";
	
	// ------------------------------------------------------------------------

	public static void print(DSLContext dslContext, Condition condition, OutputStream os) throws JRException {
		
		DSLCONTEXT.set(dslContext);
		
		InputStream is = getSalaryReportAsStream(dslContext, condition);
		
		JRDataSource jrDataSource = getSalaries(dslContext, condition);
		
		Map<String, Object> parameters = new HashMap<String, Object>();
//		parameters.put(JRParameter.)
		
		JasperPrint jasperPrint = JasperFillManager.fillReport(is, parameters, jrDataSource);
		JasperExportManager.exportReportToPdfStream(jasperPrint, os);
	}
	
	
	// ------------------------------------------------------------------------
	

	private static JRDataSource getSalaries(DSLContext dslContext, Condition condition) throws DataAccessException {
		
		Map<Integer, Salary> salaries = new HashMap<Integer,Salary>();

		dslContext
		.select()
		.from(SALARY)
		.innerJoin(CONTRACT).on(SALARY.CONTRACT.eq(CONTRACT.ID))
		.innerJoin(WORKPLACE).on(CONTRACT.WORKPLACE.eq(WORKPLACE.ID))
		.innerJoin(RADDRESS).on(WORKPLACE.ADDRESS.eq(RADDRESS.ID))
		.where(condition)
		.fetch()
		.map( r -> new Salary()
				.setId(r.get(SALARY.ID))
				.setType(r.get(SALARY.TYPE))
				
				.setEndDate(r.get(SALARY.END_DATE))
				.setStartDate(r.get(SALARY.START_DATE))
				.setIssueDate(r.get(SALARY.ISSUE_DATE))
				.setChargeDate(r.get(SALARY.CHARGE_DATE))
				.setTimeUnits(r.get(SALARY.TIME_UNITS))

				.setItBase(r.get(SALARY.IT_BASE))
				.setIrpfBase(r.get(SALARY.IRPF_BASE))
				.setCommonBase(r.get(SALARY.CGC_BASE))
				.setProfessionalBase(r.get(SALARY.CGP_BASE))
				.setExtraPayProration(r.get(SALARY.PRO_EXT_BASE))
				.setMoneyIrpfBase(r.get(SALARY.MONEY_IRPF_BASE))
				.setInKindIrpfBase(r.get(SALARY.INKIND_IRPF_BASE))
				.setOvertimeBase(r.get(SALARY.HEXTRA_BASE))
				.setNonEstructuralOvertimeBase(r.get(SALARY.NON_HEXTRA_BASE))

				.setRemuneration(r.get(SALARY.REMUNERATION))
				.setTotalLiquid(r.get(SALARY.TOTAL_LIQUID))
				.setTotalPayment(r.get(SALARY.TOTAL_PAYMENT))
				.setTotalDeduction(r.get(SALARY.TOTAL_DEDUCTION))
				.setTotalEnterprise(r.get(SALARY.TOTAL_ENTERPRISE))
				.setSocialSecurityContributions(r.get(SALARY.SOCIAL_SECURITY_CONTRIBUTIONS))

				.setCcc(r.get(SALARY.CCC))
				.setEnterpriseName(r.get(SALARY.ENTERPRISE_NAME))
				.setEnterpriseAddress(r.get(SALARY.ENTERPRISE_ADDRESS))
				.setEnterpriseDocument(r.get(SALARY.ENTERPRISE_DOCUMENT))

				.setEmployeeName(r.get(SALARY.EMPLOYEE_NAME))
				.setEmployeeDocument(r.get(SALARY.EMPLOYEE_DOCUMENT))
				.setSocialSecurityNumber(r.get(SALARY.SOCIAL_SECURITY_NUMBER))

				.setCategory(r.get(SALARY.CATEGORY))
				.setQuoteGroup(r.get(SALARY.QUOTE_GROUP))
				.setSeniorityDate(r.get(SALARY.SENIORITY_DATE))

				.setEnterpriseId(r.get(WORKPLACE.ENTERPRISE))

				//.setProvince(r.get(RADDRESS.CITY))
				
		)
		.forEach( s -> salaries.put(s.getId(), s));
		;
		
		dslContext
		.select()
		.from(SALARY)
		.innerJoin(SALARY_PAYMENT).on(SALARY.ID.eq(SALARY_PAYMENT.SALARY))
		.where(condition)
		.fetch()
		.forEach( r -> salaries.get(r.get(SALARY.ID))
				.addPayment(new Item.Payment()
						.setType(r.get(SALARY_PAYMENT.TYPE))
						.setAmount(r.get(SALARY_PAYMENT.AMOUNT))
						.setName(r.get(SALARY_PAYMENT.PAYMENT_CONCEPT))
						.setDescription(r.get(SALARY_PAYMENT.DESCRIPTION))
						.setExpression(r.get(SALARY_PAYMENT.EXPRESSION))
						.get(Payment.class)
						)
				);
		;
		
		dslContext
		.select()
		.from(SALARY)
		.innerJoin(SALARY_DEDUCTION).on(SALARY.ID.eq(SALARY_DEDUCTION.SALARY))
		.where(condition)
		.fetch()
		.forEach( r -> salaries.get(r.get(SALARY.ID))
				.addDeduction(new Item.Deduction()
						.setType(r.get(SALARY_DEDUCTION.TYPE))
						.setAmount(r.get(SALARY_DEDUCTION.AMOUNT))
						.setName(r.get(SALARY_DEDUCTION.DEDUCTION_CONCEPT))
						.setDescription(r.get(SALARY_DEDUCTION.DESCRIPTION))
						.setExpression(r.get(SALARY_DEDUCTION.EXPRESSION))
						.get(Deduction.class)
						)
				);
		;
		
		dslContext
		.select()
		.from(SALARY)
		.innerJoin(SALARY_COST).on(SALARY.ID.eq(SALARY_COST.SALARY))
		.where(condition)
		.fetch()
		.forEach( r -> salaries.get(r.get(SALARY.ID))
				.addCost(new Item.Deduction()
						.setType(r.get(SALARY_COST.TYPE))
						.setAmount(r.get(SALARY_COST.AMOUNT))
						.setName(r.get(SALARY_COST.COST_CONCEPT))
						.setDescription(r.get(SALARY_COST.DESCRIPTION))
						.get(Deduction.class)
						)
				);
		;
		
		return new JRBeanCollectionDataSource(salaries.values());
	}

	private static InputStream getSalaryReportAsStream(DSLContext dslContext, Condition condition) throws DataAccessException {
		
		Result<Record3<Byte, String, String>> result = 
		dslContext
		.select(SALARY.TYPE, ENTERPRISE_DATA.NAME, ENTERPRISE_DATA.EXPRESSION)
		.from(SALARY)
		.innerJoin(CONTRACT).on(SALARY.CONTRACT.eq(CONTRACT.ID))
		.innerJoin(WORKPLACE).on(CONTRACT.WORKPLACE.eq(WORKPLACE.ID))
		.leftJoin(ENTERPRISE_DATA).on(WORKPLACE.ENTERPRISE.eq(ENTERPRISE_DATA.ENTERPRISE))
		.where(condition)
//		.and(ENTERPRISE_DATA.NAME.like(REPORT_LIKE_PARAM))
//		.and(DSL.trim(ENTERPRISE_DATA.EXPRESSION).ne(""))
		.fetch()
		;
		

		InputStream is = null;
		SalaryType type = null ;
		
		for ( int i = 0; i < result.size(); i++ ) {
			Record3<Byte, String, String> record = result.get(i);
			type = getSalaryType( record.get(SALARY.TYPE));
			String name = record.get(ENTERPRISE_DATA.NAME);
			if ( !type.getReportName().equals(name) )
				continue;
			
			is =  SalaryReport.class.getResourceAsStream(String.format(REPORT_FORMAT, record.get(ENTERPRISE_DATA.EXPRESSION)));
			if ( is != null )
				return is;
		}
		
		return SalaryReport.class.getResourceAsStream( String.format(REPORT_FORMAT, type.getDefaultReport() ) );
		
	}
	
	private static SalaryType getSalaryType( Byte b ) {
		try {
			return SalaryType.values()[b];
		} catch ( NullPointerException | IndexOutOfBoundsException e ) {
			return SalaryType.SALARY;
		}
		
	}
	
	
	public static void main(String[] args) {
		
	}

}
