/**
 * 
 */
package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.jooq.tables.AgreementExtra.AGREEMENT_EXTRA;
import static com.esferalia.aon.jooq.tables.AgreementLevel.AGREEMENT_LEVEL;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.SalaryPayment.SALARY_PAYMENT;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.jooq.DSLContext;
import org.jooq.Result;

import com.code.aon.common.AonException;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.jooq.tables.records.AgreementExtraRecord;
import com.esferalia.aon.jooq.tables.records.SalaryRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.AgreementExtra;
import com.esferalia.aon.payroll.PaymentConcept;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.SystemPayment;
import com.esferalia.aon.payroll.calculator.CompositePayments;
import com.esferalia.aon.payroll.calculator.IContractPayment;
import com.esferalia.aon.payroll.calculator.SmartContractSalaryCalculator;
import com.esferalia.aon.payroll.sql.SQLConstants;
import com.esferalia.aon.payroll.sql.SQLConstants.ContractColumns;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ITimedVariable;
import com.esferalia.aon.salary.payment.IPayment;
import com.esferalia.aon.watson.util.AonDateUtils;

/**
 * @author rtrepiana
 *
 */
public class SmartSQLContractSettleCalculatorContext extends SQLContractSettleCalculatorContext {
	
	private Date settleEndDate;

	/**
	 */
	public SmartSQLContractSettleCalculatorContext(Connection connection, Date startDate, Date endDate, Date issueDate)
			throws SQLException, ExpressionException {
		super(connection, startDate, endDate, issueDate);
		this.settleEndDate = endDate;
	}

	/**
	 */
	public SmartSQLContractSettleCalculatorContext(Connection connection, Date startDate, Date endDate, Date issueDate,
			Criteria criteria) throws SQLException, ExpressionException {
		super(connection, startDate, endDate, issueDate, criteria);
		this.settleEndDate = endDate;
	}

	/**
	 */
	public SmartSQLContractSettleCalculatorContext(Connection connection, Date startDate, Date endDate, Date issueDate,
			Criteria criteria, Criteria paymentsCriteria) throws SQLException, ExpressionException {
		super(connection, startDate, endDate, issueDate, criteria, paymentsCriteria);
		this.settleEndDate = endDate;
	}

	/**
	 */
	public SmartSQLContractSettleCalculatorContext(Connection connection, Date startDate, Date endDate, Date issueDate,
			Date chargeDate, Criteria criteria) throws SQLException, ExpressionException {
		super(connection, startDate, endDate, issueDate, chargeDate, criteria);
		this.settleEndDate = endDate;
	}

	/**
	 */
	public SmartSQLContractSettleCalculatorContext(Connection connection, Date startDate, Date endDate, Date issueDate,
			Date chargeDate, Criteria criteria, Criteria paymentsCriteria) throws SQLException, ExpressionException {
		super(connection, startDate, endDate, issueDate, chargeDate, criteria, paymentsCriteria);
		this.settleEndDate = endDate;
	}
	
	
	// ------------------------------------- SQLContractSettleCalculatorContext
	
	@Override
	public Collection<IContractPayment> getContractPayments() throws AonException {
		try {
			return new CompositePayments<IContractPayment>(super.getContractPayments(), getExtraPayments());
		} catch (SQLException e) {
			throw new AonException(e);
		}
	}
	
	// ------------------------------------------------------------------------
	public Collection<IContractPayment> getExtraPayments() throws SQLException, ExpressionException, SalaryException {
		AONContext aonCtx = new AONContext(connection);
		DSLContext dslCtx = aonCtx.getDslContext();
		
		Result<AgreementExtraRecord> extras = dslCtx
		.select()
		.from(CONTRACT)
		.innerJoin(AGREEMENT_LEVEL).on(CONTRACT.AGREEMENT_LEVEL.eq(AGREEMENT_LEVEL.ID))
		.innerJoin(AGREEMENT_EXTRA).on(AGREEMENT_LEVEL.AGREEMENT.eq(AGREEMENT_EXTRA.AGREEMENT))
		.where(CONTRACT.ID.eq(getId()))
		.fetchInto(AGREEMENT_EXTRA)
		;
		
		Date contractStartDate = getContractStartate();
		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(SQLConstants.CONTRACT + "." + ContractColumns.ID, getId());
		
		Collection<Integer> calculatedExtras = new ArrayList<Integer>();
		Collection<IContractPayment> extrasPayments = new ArrayList<IContractPayment>(extras.size());
		
		PaymentConcept autoGenratedConcept =  new PaymentConcept();
		autoGenratedConcept.setId(Integer.MAX_VALUE);
		
		for ( int i = 0; i < extras.size(); i++ ) {
			
			int year = AonDateUtils.get(settleEndDate, Calendar.YEAR );
			
			AgreementExtraRecord extra = extras.get(i);
			
			Date extraStartDate  = AgreementExtra.parseAgreementDate(extra.getStartDate(), year);
			
//			if ( extraStartDate.after(settleEndDate))  
//				continue;  // Nothing to calculate
			while ( !extraStartDate.after(settleEndDate )) {
			
				Date extraIssueDate  = AgreementExtra.parseAgreementDate(extra.getIssueDate(), year);
	
				if ( extraIssueDate.before(settleEndDate)
					 && extraIssueDate.after(contractStartDate) ) {
					extraStartDate  = AgreementExtra.parseAgreementDate(extra.getStartDate(), ++year);
					if ( extraStartDate.after(settleEndDate))  
						continue;  // Nothing to calculate
				}
				
				
				// TODO: Extract to method ?
				SQLExtraSalaryCalculatorContext extraCtx = 
						new SQLExtraSalaryCalculatorContext(getConnection(), extra.getId(), year, settleEndDate, getChargeDate(), criteria) ;
				if ( !extraCtx.next() )
					continue;
				
				List<IContractPayment> extraPayments = new ArrayList<IContractPayment>(extras.size());
				
				Salary salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder() {
					@Override
					public void addPayment(Double amount, Double quote, Double tax, String description, Date startDate,
							Date endDate, IPayment payment, Map<String, ITimedVariable<?>> context) {
						SystemPayment extraPayment = new SystemPayment();
						
						if ( payment instanceof IContractPayment )
							extraPayment.setId(((IContractPayment)payment).getId());
						
						extraPayment.setType(PaymentType.CRA_0000);
						extraPayment.setSalaryType(SalaryType.SETTLE);
						extraPayment.setStartDate(startDate);
						extraPayment.setEndDate(endDate);
						extraPayment.setPaymentConcept(autoGenratedConcept);
	
						extraPayment.setDescription(description);
						extraPayment.setExpression(String.format(Locale.US, "%f", amount));
						extraPayment.setIrpfExpression(String.format(Locale.US, "%f", tax));
						extraPayment.setQuoteExpression(String.format(Locale.US, "%f", quote));
						
						extraPayments.add( extraPayment );
						super.addPayment(amount, quote, tax, description, startDate, endDate, payment, context);
					}
				}).calculate(extraCtx);
				
				if ( extraPayments.isEmpty()  )
					continue;
				
				
				SalaryRecord record = 
				dslCtx
				.select()
				.from(SALARY)
				.innerJoin(SALARY_PAYMENT).on(SALARY.ID.eq(SALARY_PAYMENT.SALARY))
				.where(SALARY.CONTRACT.eq(getId()))
				.and(SALARY.ID.notIn(calculatedExtras))
				.and(SALARY.TYPE.eq((byte)salary.getType().ordinal()))
				.and(SALARY.END_DATE.eq(new java.sql.Date(salary.getEndDate().getTime())))
				.and(SALARY.START_DATE.eq(new java.sql.Date(salary.getStartDate().getTime())))
				.and(SALARY_PAYMENT.DESCRIPTION.eq(extraPayments.get(0).getDescription()))
				.fetchAnyInto(SALARY)
				;
				
				if ( record == null ) {
					extrasPayments.addAll(extraPayments);
					extrasPayments.add(newExtraMsgPayment(extraPayments.get(0).getDescription()));
				} else { 
					calculatedExtras.add(record.getId());
				}
				
				extraStartDate  = AgreementExtra.parseAgreementDate(extra.getStartDate(), ++year);
			}
		}
		
		return extrasPayments;
		
	}
	
	private IContractPayment newExtraMsgPayment(String message) {
		SystemPayment extraPayment = new SystemPayment();
		
		extraPayment.setType(PaymentType.CRA_0000);
		extraPayment.setSalaryType(SalaryType.SETTLE);
		extraPayment.setStartDate(getStart());
		extraPayment.setEndDate(getEnd());

		extraPayment.setExpression(String.format(Locale.US, "HIDE(\""
				+ "<div>%s incluida en el finiquito. Si no desea incluirla, genere esta paga extra.</div>"
				+"<div>&nbsp;</div><div class='aon-text-right'><span class='aon-icon aon-icon-logo' />aon Solutions</div>\");"
				+ "\")", 
				message ));
		return extraPayment;
	}
	

}
