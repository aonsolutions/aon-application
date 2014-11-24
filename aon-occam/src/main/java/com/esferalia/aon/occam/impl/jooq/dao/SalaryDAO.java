package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.SalaryDeduction.SALARY_DEDUCTION;
import static com.esferalia.aon.jooq.tables.SalaryEmbargo.SALARY_EMBARGO;
import static com.esferalia.aon.jooq.tables.SalaryPayment.SALARY_PAYMENT;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.function.Function;

import org.jooq.AggregateFunction;
import org.jooq.Field;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;
import org.jooq.lambda.SQL;
import org.jooq.lambda.Unchecked;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.SalaryAccountEntry;
import com.esferalia.aon.occam.api.model.SalaryAccountEntry.SalaryAccountEntryLine;
import com.esferalia.aon.occam.api.model.SalaryAccountEntry.SalaryAccountEntryLineType;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.watson.AonCoreException;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.util.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class SalaryDAO {
	
	private static final String DEFAULT_SALARY_CONCEPT = "NÓMINAS";
	private static final String SUM_IRPF_BASE = "SUM_IRPF_BASE";
	private static final String SUM_TOTAL_IRPF = "SUM_TOTAL_IRPF";
	private static final String SUM_SOCIAL_SECURITY_CONTRIBUTIONS = "SUM_SOCIAL_SECURITY_CONTRIBUTIONS";
	private static final String SUM_TOTAL_ENTERPRISE = "SUM_TOTAL_ENTERPRISE";
	private static final String SUM_TOTAL_LIQUID = "SUM_TOTAL_LIQUID";
	private static final String ALLOWANCE_SUM = "ALLOWANCE_SUM";
	private static final String COMPENSATION_SUM = "COMPENSATION_SUM";
	private static final String SEIZE_SUM = "DEDUCTION_SEIZE_SUM";
	private static final String DED_ADVANCE_SUM = "DEDUCTION_ADVANCE_SUM";
	private static final String DED_IN_KIND_SUM = "DED_IN_KIND_SUM";
	private static final String DED_OTHER_SUM = "DEDUCTION_TYPE_OTHER_SUM";
	
	
	// TODO ARRRGGGGGHHH!!!
	private static final Byte DEDUCTION_ADVANCE = 7;
	private static final Byte DEDUCTION_IN_KIND = 8;
	private static final Byte DEDUCTION_TYPE_OTHER = 9;
	// --------------------
	

	public static SalaryAccountEntry getSalaryEntry(AONContext ctx, Integer enterprise
			,Date from, Date to,String concept, Integer registryBank) {
		ctx.checkRead();
		if (enterprise == null) throw new AonCoreException(AonError.EMPTY_ENTERPRISE);
		if (from == null) throw new AonCoreException(AonError.EMPTY_DATE_FROM);
		if (to == null) throw new AonCoreException(AonError.EMPTY_DATE_TO);
		Field<BigDecimal> sueldosYSalarios  = DSL.sum(DSL.round( SALARY.IRPF_BASE ,2)).as(SUM_IRPF_BASE);
		Field<BigDecimal> totalIRPF  = DSL.sum(DSL.round( SALARY.TOTAL_IRPF ,2)).as(SUM_TOTAL_IRPF);
		Field<BigDecimal> segSocEmployee  = DSL.sum(DSL.round( SALARY.SOCIAL_SECURITY_CONTRIBUTIONS,2)).as(SUM_SOCIAL_SECURITY_CONTRIBUTIONS);
		Field<BigDecimal> segSocCompany = DSL.sum(DSL.round( SALARY.TOTAL_ENTERPRISE,2)).as(SUM_TOTAL_ENTERPRISE);
		Field<BigDecimal> totalLiquid  = DSL.sum(DSL.round( SALARY.TOTAL_LIQUID,2)).as(SUM_TOTAL_LIQUID);

		AggregateFunction<BigDecimal> salaryPaymentAmountSum = DSL.sum(DSL.round( SALARY_PAYMENT.AMOUNT,2));
		// DIETAS
		Field<BigDecimal> dietas = DSL.sum(
				DSL.select(salaryPaymentAmountSum)
					.from(SALARY_PAYMENT)
					.where(SALARY_PAYMENT.SALARY.equal(SALARY.ID))
					.and(SALARY_PAYMENT.TYPE.between( (byte) 42 , (byte) 50) )
					.asField()).as(ALLOWANCE_SUM);
		
		// INDEMNIZACIONES
		Field<BigDecimal> indemnizaciones = DSL.sum(
				DSL.select(salaryPaymentAmountSum)
					.from(SALARY_PAYMENT)
					.where(SALARY_PAYMENT.SALARY.equal(SALARY.ID))
					.and(SALARY_PAYMENT.TYPE.between( (byte) 51 , (byte) 54) )
					.asField()).as(COMPENSATION_SUM);
		
		AggregateFunction<BigDecimal> salaryDeductionAmountSum = DSL.sum(DSL.round( SALARY_DEDUCTION.AMOUNT,2));
		// OTRAS DEDUCCIONES
		Field<BigDecimal> otherDeductions = DSL.sum(
				DSL.select(salaryDeductionAmountSum)
					.from(SALARY_DEDUCTION)
					.where(SALARY_DEDUCTION.SALARY.equal(SALARY.ID))
					.and(SALARY_DEDUCTION.TYPE.equal( DEDUCTION_TYPE_OTHER  ))
					.asField()).as(DED_OTHER_SUM);
		// DEDUCCIONES EN ESPECIE
		Field<BigDecimal> inKindDeductions = DSL.sum(
				DSL.select(salaryDeductionAmountSum)
					.from(SALARY_DEDUCTION)
					.where(SALARY_DEDUCTION.SALARY.equal(SALARY.ID))
					.and(SALARY_DEDUCTION.TYPE.equal( DEDUCTION_IN_KIND ))
					.asField()).as(DED_IN_KIND_SUM);
		// DEDUCCIONES de ANTICIPOS
		Field<BigDecimal> advanceDeductions = DSL.sum(
				DSL.select(salaryDeductionAmountSum)
					.from(SALARY_DEDUCTION)
					.where(SALARY_DEDUCTION.SALARY.equal(SALARY.ID))
					.and(SALARY_DEDUCTION.TYPE.equal( DEDUCTION_ADVANCE ))
					.asField()).as(DED_ADVANCE_SUM);
		
		AggregateFunction<BigDecimal> salaryEmbargoAmountSum = DSL.sum(DSL.round( SALARY_EMBARGO.AMOUNT,2));
		// DIETAS
		Field<BigDecimal> seize = DSL.sum(
				DSL.select(salaryEmbargoAmountSum)
					.from(SALARY_EMBARGO)
					.where(SALARY_EMBARGO.SALARY.equal(SALARY.ID))
					.asField()).as(SEIZE_SUM);

		String sql = ctx.getDslContext()
			.select( 
					sueldosYSalarios 
					,totalIRPF
					,segSocEmployee
					,segSocCompany
					,totalLiquid
					,dietas
					,indemnizaciones
					,otherDeductions
					,inKindDeductions
					,advanceDeductions
					,seize
					)
			.from(SALARY)
			.join(CONTRACT).on(SALARY.CONTRACT.equal(CONTRACT.ID))
			.join(WORKPLACE).on(CONTRACT.WORKPLACE.equal(WORKPLACE.ID))
			.where(WORKPLACE.ENTERPRISE.equal(enterprise))
			.and(SALARY.ISSUE_DATE.between(AonDateUtils.toSql(from), AonDateUtils.toSql(to)))
			.getSQL();
		SalaryAccountEntry sae = new SalaryAccountEntry();
		PreparedStatement stmt;
		try {
			stmt = ctx.getDslContext()
					.configuration()
					.connectionProvider()
					.acquire()
					.prepareStatement(sql);
			sae.setDate(to);
			sae.setSecurityLevel( SecurityLevel.OFFICIAL );
			// TODO Un pelín escaso ... 
			sae.setConcept((AonStringUtils.isNotEmpty(concept))?concept:DEFAULT_SALARY_CONCEPT);
			// ------------------------
			sae.setRegistryBank(registryBank);
			Function<ResultSet, SalaryAccountEntry> function = 
					Unchecked.function(rs -> {
						sae.addLine(new SalaryAccountEntryLine(
								SalaryAccountEntryLineType.SALARY, null, rs.getDouble( SUM_IRPF_BASE )));
						sae.addLine(new SalaryAccountEntryLine(
								SalaryAccountEntryLineType.RETENTION, null, rs.getDouble(SUM_TOTAL_IRPF)));
						// IN KIND
						// sae.addLine( new
						// SalaryAccountEntryLine(SalaryAccountEntryLineType.SALARY_IN_KIND,null,rs.getDouble(XX)));
						// sae.addLine( new
						// SalaryAccountEntryLine(SalaryAccountEntryLineType.RETENTION_IN_KIND,null,rs.getDouble(XX)));

						sae.addLine(new SalaryAccountEntryLine(
								SalaryAccountEntryLineType.EMPLOYEE_SOC_INS,
								null, rs.getDouble(SUM_SOCIAL_SECURITY_CONTRIBUTIONS)));
						sae.addLine(new SalaryAccountEntryLine(
								SalaryAccountEntryLineType.DEFAULT_PENDING_SALARY,
								null, rs.getDouble(SUM_TOTAL_LIQUID)));
						sae.addLine(new SalaryAccountEntryLine(
								SalaryAccountEntryLineType.COMPANY_SOC_INS,
								null, rs.getDouble(SUM_TOTAL_ENTERPRISE)));
						sae.addLine(new SalaryAccountEntryLine(
								SalaryAccountEntryLineType.ALLOWANCE,
								null, rs.getDouble(ALLOWANCE_SUM)));
						sae.addLine(new SalaryAccountEntryLine(
								SalaryAccountEntryLineType.COMPENSATION,
								null, rs.getDouble(COMPENSATION_SUM)));
						sae.addLine(new SalaryAccountEntryLine(
								SalaryAccountEntryLineType.DED_ADVANCE_PAYMENT,
								null, rs.getDouble(DED_ADVANCE_SUM)));
						sae.addLine(new SalaryAccountEntryLine(
								SalaryAccountEntryLineType.DED_IN_KIND,
								null, rs.getDouble(DED_IN_KIND_SUM)));
						sae.addLine(new SalaryAccountEntryLine(
								SalaryAccountEntryLineType.DED_OTHER,
								null, rs.getDouble(DED_OTHER_SUM)));
						sae.addLine(new SalaryAccountEntryLine(
								SalaryAccountEntryLineType.DED_SEIZE,
								null, rs.getDouble(SEIZE_SUM)));
						return sae;
					});
			
			SQL.seq(stmt,function).forEach(System.out::println);
			
		} catch (DataAccessException | SQLException e) {
			throw new AonCoreException(e.getMessage(),e);
		}
		return sae;
	}


}
