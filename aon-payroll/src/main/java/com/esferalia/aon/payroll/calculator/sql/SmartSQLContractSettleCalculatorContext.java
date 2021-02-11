/**
 * 
 */
package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.jooq.tables.AgreementExtra.AGREEMENT_EXTRA;
import static com.esferalia.aon.jooq.tables.AgreementLevel.AGREEMENT_LEVEL;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractPayment.CONTRACT_PAYMENT;
import static com.esferalia.aon.jooq.tables.PaymentConcept.PAYMENT_CONCEPT;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.SalaryPayment.SALARY_PAYMENT;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.jooq.conf.ParamType;
import org.jooq.impl.DSL;

import com.code.aon.common.AonException;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.jooq.tables.records.AgreementExtraRecord;
import com.esferalia.aon.jooq.tables.records.ContractPaymentRecord;
import com.esferalia.aon.jooq.tables.records.SalaryRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.AgreementExtra;
import com.esferalia.aon.payroll.PaymentConcept;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.SystemPayment;
import com.esferalia.aon.payroll.calculator.CompositePayments;
import com.esferalia.aon.payroll.calculator.IContractPayment;
import com.esferalia.aon.payroll.calculator.IContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.SmartContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.TaxCalculator;
import com.esferalia.aon.payroll.calculator.sql.FilterCollection.Filter;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.sql.SQLConstants;
import com.esferalia.aon.payroll.sql.SQLConstants.ContractColumns;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ITimedVariable;
import com.esferalia.aon.salary.expression.Period;
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
			return new CompositePayments<IContractPayment>(super.getContractPayments(), getAgreementExtraPayments(), getContractExtraPayments());
		} catch (SQLException e) {
			throw new AonException(e);
		}
	}
	
	// ------------------------------------------------------------------------
	public Collection<IContractPayment> getAgreementExtraPayments() throws SQLException, ExpressionException, SalaryException {
		AONContext aonCtx = new AONContext(connection);
		DSLContext dslCtx = aonCtx.getDslContext();
		
		boolean redefined = 
		dslCtx.fetchCount(
		DSL
		.select(CONTRACT_PAYMENT.ID)
		.from(CONTRACT_PAYMENT)
		.innerJoin(PAYMENT_CONCEPT).onKey()
		.where(CONTRACT_PAYMENT.CONTRACT.eq(getId()))
		.and(CONTRACT_PAYMENT.TYPE.eq((byte)4)
		.or(CONTRACT_PAYMENT.TYPE.isNull().and(PAYMENT_CONCEPT.TYPE.eq((byte)4))))
		) > 0 ;
		if ( redefined )
			return Collections.emptyList();

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
				Date extraEndDate  = AgreementExtra.parseAgreementDate(extra.getEndDate(), year);
	
				if ( extraIssueDate.before(settleEndDate)
					 && extraIssueDate.after(contractStartDate) ) {
					extraStartDate  = AgreementExtra.parseAgreementDate(extra.getStartDate(), ++year);
					if ( extraStartDate.after(settleEndDate))  
						break;  // Nothing to calculate
				} else if (extraEndDate.before(settleEndDate)  ) {
					extraStartDate  = AgreementExtra.parseAgreementDate(extra.getStartDate(), ++year);
					if ( extraStartDate.after(settleEndDate))  
						break;  // Nothing to calculate
				}
				
				
				// TODO: Extract to method ?
				SQLExtraSalaryCalculatorContext extraCtx = 
						new SQLExtraSalaryCalculatorContext(getConnection(), extra.getId(), year, settleEndDate, getChargeDate(), criteria) ;
				if ( !extraCtx.next() )
					break;
				
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
						extraPayment.setExpression(String.format(Locale.US, "/*hideable*/%f", amount));
						extraPayment.setIrpfExpression(String.format(Locale.US, "%f", tax));
						extraPayment.setQuoteExpression(String.format(Locale.US, "%f", quote));
						
						extraPayments.add( extraPayment );
						super.addPayment(amount, quote, tax, description, startDate, endDate, payment, context);
					}
				}).calculate(extraCtx);
				
				if ( extraPayments.isEmpty()  )
					break;
				
				
				
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
					record = 
						dslCtx
						.select()
						.from(SALARY)
						.innerJoin(SALARY_PAYMENT).on(SALARY.ID.eq(SALARY_PAYMENT.SALARY))
						.where(SALARY.CONTRACT.eq(getId()))
						.and(SALARY.ID.notIn(calculatedExtras))
						.and(SALARY.TYPE.eq((byte)SalaryType.SALARY.ordinal()))
						.and(SALARY_PAYMENT.AMOUNT.gt(0.00))
						.and(SALARY_PAYMENT.DESCRIPTION.eq(extraPayments.get(0).getDescription()))
						.and(DSL.year(SALARY.ISSUE_DATE).eq(DSL.year(new java.sql.Date(extraIssueDate.getTime()))))
						.and(DSL.month(SALARY.ISSUE_DATE).eq(DSL.month(new java.sql.Date(extraIssueDate.getTime()))))
						.fetchAnyInto(SALARY)
							;
					if ( record == null ) {
						extrasPayments.addAll(extraPayments);
						extrasPayments.add(newExtraMsgPayment(extraPayments.get(0).getDescription()));
					}
				} else { 
					calculatedExtras.add(record.getId());
				}
				
				extraStartDate  = AgreementExtra.parseAgreementDate(extra.getStartDate(), ++year);
			}
		}
		
		return extrasPayments;
		
	}
	
	public Collection<IContractPayment> getContractExtraPayments() throws SQLException, ExpressionException, SalaryException {
		AONContext aonCtx = new AONContext(connection);
		DSLContext dslCtx = aonCtx.getDslContext();
		
		Result<ContractPaymentRecord> extras = dslCtx
		.select()
		.from(CONTRACT_PAYMENT)
		.innerJoin(PAYMENT_CONCEPT).onKey()
		.where(CONTRACT_PAYMENT.CONTRACT.eq(getId()))
		.and(CONTRACT_PAYMENT.MONTH.isNotNull())
		.and(CONTRACT_PAYMENT.TYPE.eq((byte)4)
		.or(CONTRACT_PAYMENT.TYPE.isNull().and(PAYMENT_CONCEPT.TYPE.eq((byte)4))))
		.fetchInto(CONTRACT_PAYMENT)
		;
		
		Date contractStartDate = getContractStartate();
		
		Collection<Integer> calculatedExtras = new ArrayList<Integer>();
		Collection<IContractPayment> extrasPayments = new ArrayList<IContractPayment>(extras.size());

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(SQLConstants.CONTRACT + "." + ContractColumns.ID, getId());
				
		PaymentConcept autoGenratedConcept =  new PaymentConcept();
		autoGenratedConcept.setId(Integer.MAX_VALUE);
		
		for ( int i = 0; i < extras.size(); i++ ) {
			
			int year = AonDateUtils.get(settleEndDate, Calendar.YEAR );
			
			ContractPaymentRecord extra = extras.get(i);
			
			Date extraStartDate  = getStartDate(extra, year);
			
			if (Period.compare(extra.getEndDate(), extraStartDate) < 0 )
				continue;
			
//			if ( extraStartDate.after(settleEndDate))  
//				continue;  // Nothing to calculate
			while ( !extraStartDate.after(settleEndDate )) {
			
				Date extraIssueDate  = getEndDate(extra, year);
				Date extraEndDate  = getEndDate(extra, year);
	
				if ( extraIssueDate.before(settleEndDate)
					 && extraIssueDate.after(contractStartDate) ) {
					extraStartDate  = getStartDate(extra, ++year);
					extraIssueDate  = getEndDate(extra, year);
					if ( extraStartDate.after(settleEndDate))  
						break;  // Nothing to calculate
				} else if (extraEndDate.before(settleEndDate)  ) {
					extraStartDate  = getStartDate(extra, ++year);
					extraIssueDate  = getEndDate(extra, year);
					if ( extraStartDate.after(settleEndDate))  
						break;  // Nothing to calculate
				}
				
	
				if (Period.compare(extra.getEndDate(), extraStartDate) < 0 )
					break;
				
				
				// TODO: Extract to method ?
				SQLContractExtraCalculatorContext extraCtx = 
						new SQLContractExtraCalculatorContext(connection, extraStartDate, extraIssueDate, extraIssueDate, criteria) {
					@Override
					protected Filter<IContractPayment> getExtraPaymentFilter() {
						return ( p ) -> {
							return p.getMonth() != null && ((byte) p.getMonth().ordinal()) == extra.getMonth();
						};
					}
				};
				if ( !extraCtx.next() )
					break;
				
				Period contractPaymentPeriod = new Period(extra.getStartDate(), extra.getEndDate());
				
				List<IContractPayment> extraPayments = new ArrayList<IContractPayment>(extras.size());
				
				Salary salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder() {
					@Override
					public void addPayment(Double amount, Double quote, Double tax, String description, Date startDate,
							Date endDate, IPayment payment, Map<String, ITimedVariable<?>> context) {
						
						Period salaryPaymentPeriod =  new Period(startDate, endDate);
						
						if ( !salaryPaymentPeriod.intersects(contractPaymentPeriod))
							return;
						
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
					
				}){
					@Override
					protected TaxCalculator getTaxCalculator(IContractSalaryCalculatorContext ctx) {
						return new TaxCalculator() {
							
							@Override
							public double tax(IContractPayment payment, Date start, Date end, Date issueDate, double amount, double total)
									throws AonException {
								return amount;
							}
							
							@Override
							public Map<Period, Double> getAmounts(PaymentType type) {
								return Collections.emptyMap();
							}
						};
					}
				}.calculate(extraCtx);
				
				if ( extraPayments.isEmpty()  )
					break;
				
				
				
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
					record = 
						dslCtx
						.select()
						.from(SALARY)
						.innerJoin(SALARY_PAYMENT).on(SALARY.ID.eq(SALARY_PAYMENT.SALARY))
						.where(SALARY.CONTRACT.eq(getId()))
						.and(SALARY.ID.notIn(calculatedExtras))
						.and(SALARY.TYPE.eq((byte)SalaryType.SALARY.ordinal()))
						.and(SALARY_PAYMENT.AMOUNT.gt(0.00))
						.and(SALARY_PAYMENT.DESCRIPTION.eq(extraPayments.get(0).getDescription()))
						.and(DSL.year(SALARY.ISSUE_DATE).eq(DSL.year(new java.sql.Date(extraIssueDate.getTime()))))
						.and(DSL.month(SALARY.ISSUE_DATE).eq(DSL.month(new java.sql.Date(extraIssueDate.getTime()))))
						.fetchAnyInto(SALARY)
							;
					if ( record == null ) {
						extrasPayments.addAll(extraPayments);
//						extrasPayments.add(newExtraMsgPayment(extraPayments.get(0).getDescription()));
					}
				} else { 
					calculatedExtras.add(record.getId());
				}
				
				extraStartDate  = getStartDate(extra, ++year);
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
	
	private static Date getStartDate(ContractPaymentRecord extra, int year) {
		Calendar calendar = Calendar.getInstance();
//		calendar.set(Calendar.HOUR, 0);
//		calendar.set(Calendar.MINUTE, 0);
//		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, extra.getMonth());
		calendar.set(Calendar.DAY_OF_MONTH,calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		calendar.add(Calendar.YEAR,-1);
		calendar.add(Calendar.DAY_OF_MONTH,1);
		return calendar.getTime();
	}
	
	private static Date getEndDate(ContractPaymentRecord extra, int year) {
		Calendar calendar = Calendar.getInstance();
//		calendar.set(Calendar.HOUR, 0);
//		calendar.set(Calendar.MINUTE, 0);
//		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, extra.getMonth());
		calendar.set(Calendar.DAY_OF_MONTH,calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		return calendar.getTime();
	}

}
