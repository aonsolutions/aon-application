package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.payroll.enumeration.ContextVariable.WORKED_DAYS;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import javax.xml.ws.handler.MessageContext.Scope;

import com.code.aon.common.AonException;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Salary.ContextData;
import com.esferalia.aon.payroll.DelegateContractPayment;
import com.esferalia.aon.payroll.calculator.IContractBonus;
import com.esferalia.aon.payroll.calculator.IContractCost;
import com.esferalia.aon.payroll.calculator.IContractPayment;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionContext;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ExpressionImpl;
import com.esferalia.aon.salary.expression.ExpressionScope;
import com.esferalia.aon.salary.expression.ExpressionVariable;
import com.esferalia.aon.salary.expression.ITimedVariable;
import com.esferalia.aon.salary.expression.Period;
import com.esferalia.aon.watson.util.AonStringUtils;

public class SQLContractPPECalculatorContext extends SQLContractSalaryCalculatorContext {
	
	private static final String RED_PPE_E = "RED_PPE_E";
	private static final String PAID_PPE = "PPE_DEVENGADO";
	
	public SQLContractPPECalculatorContext(Connection connection, java.util.Date startDate, java.util.Date endDate,
			java.util.Date issueDate, Criteria criteria) throws SQLException, ExpressionException {
		super(connection, startDate, endDate, issueDate, criteria);
	}

	public SQLContractPPECalculatorContext(Connection connection, java.util.Date startDate, java.util.Date endDate,
			java.util.Date issueDate, java.util.Date chargeDate, Criteria criteria)
			throws SQLException, ExpressionException {
		super(connection, startDate, endDate, issueDate, chargeDate, criteria);
	}
	
	
	@Override
	protected void initContractExpressionCtx(NextHook hook) throws SQLException, ExpressionException {
		super.initContractExpressionCtx(hook);
		overrideSalaryHours(getExpressionContext());
		initDBPPEPayments(getExpressionContext());
	}
	
	@Override
	public SalaryType getSalaryType() {
		return SalaryType.DELAY;
	}

	@Override
	public Collection<IContractBonus> getContractBonus() throws AonException {
		// 04/2024 21/03/2024
		// Se recuerda que en las liquidaciones complementarias no procede la
		// aplicación de la reducción establecida en la DA disposición adicional
		// cuadragésima séptima de la LGSS (DA undécima del Real Decreto-ley 1/2023).
		return Collections.emptyList();
	}

	@Override
	public Collection<IContractPayment> getContractPayments() throws AonException {
		initMonthDays();
		readDelayCause();
		
		return new FilterCollection<IContractPayment>(p -> ContextVariable.PPE.equals(p.getName()),
				super.getContractPayments()) {
			@Override
			public IContractPayment next() {
				IContractPayment next = super.next();
				return new DelegateContractPayment(next) {
					@Override
					public PaymentType getType() {
						return PaymentType.CRA_0033;
					}

					@Override
					public SalaryType getSalaryType() {
						return SalaryType.DELAY;
					}

					@Override
					public String getExpression() {
						return String.format("%s; %s", WORKED_DAYS.getName(), super.getExpression());
					}
					
					@Override
					public String getQuoteExpression() {
						return String.format("(%s) - (isdef DIAS_TRABAJADOS ? IFNDEF(\"%s\",0.00) : 0.00) ", super.getQuoteExpression(), PAID_PPE);
					}
				};
			}
		};
	}
	
	@Override
	public Collection<IContractCost> getContractCosts() throws AonException {
		return new FilterCollection<>(p -> AonStringUtils.notEquals(p.getName(), RED_PPE_E),super.getContractCosts());
	}
	
	private void readDelayCause() {

		try {
			getExpressionContext().eval(ContextVariable.DELAY_CAUSE.getName(), getStartDate(), getEndDate(),
					PaymentType.class);
		} catch (Exception e) {
			getExpressionContext().putVariable(ContextVariable.DELAY_CAUSE.getName(),
					new ExpressionVariable<>(PaymentType.CRA_0033, new Period(getStartDate(), getEndDate()),
							new ExpressionImpl().setScope(ExpressionScope.CONTRACT)
									.setName(ContextVariable.DELAY_CAUSE.getName())
									.setExpression(PaymentType.CRA_0033.name())));
		}
		getExpressionContext().readVariable(ContextVariable.DELAY_CAUSE.getName(), getStartDate(), getEndDate(),
				PaymentType.class);
	}
	
	private void initMonthDays() {
		try {
			getExpressionContext().eval(ContextVariable.MONTH_DAYS.getName(), getStartDate(), getEndDate(),Number.class);
		} catch (Exception e) {
		}
	}

	private void overrideSalaryHours(ExpressionContext ctx) {
		
		for ( Period period : ctx.getPeriods(ContextVariable.SALARY_HOURS.getName())) {
			ctx.putVariable(ContextVariable.SALARY_HOURS.getName(), new ITimedVariable<Double>() {
				
				@Override
				public Period getPeriod() {
					return period;
				}
				
				@Override
				public Double getValue(Period period) {
					try {
						return getDBSalaryHours(period);
					} catch ( Exception e ) {
						try {
							return ctx.eval(ContextVariable.WORKED_HOURS.getName(), period.getStart(), period.getEnd(), Number.class)
									.stream().collect(Collectors.summingDouble( r -> r.getValue().doubleValue() ));
						} catch (Exception e1) {
							return 0.00;
						}
					}
				}
			});
		}
	}
	
	private Double getDBSalaryHours(Period period) {
		AONContext aonContext = new AONContext(getConnection());
		Double salaryHours = AON.getSalaryData(aonContext, 
		p -> p.getContractProperty().eq(this.getId())
		.and(p.getIsSalaryProperty().eq(Boolean.TRUE))
		.and(p.getEndDateProperty().ge(period.getStart())
		.and(p.getStartDateProperty().le(period.getEnd()))))
		.flatMap( s -> s.getContextData().get(ContextVariable.SALARY_HOURS.getName()).stream() )
		.collect(Collectors.summingDouble( d -> Double.parseDouble(d.getExpression())));
		if ( salaryHours == null || salaryHours == 0.00 )
			throw new NullPointerException();
		return salaryHours;
	}
	
	private void initDBPPEPayments(ExpressionContext ctx) {
		AON.getSalaryData(new AONContext(getConnection()), 
				p -> p.getContractProperty().eq(this.getId())
				//.and(p.getIsSalaryProperty().eq(Boolean.FALSE))
				.and(p.getEndDateProperty().ge(getStartDate())
				.and(p.getStartDateProperty().le(getEndDate()))))
		.flatMap( salary -> salary.getContextData().getOrDefault(ContextVariable.BASE_PPE.getName(), Collections.emptyList()).stream() )
		.forEach( data -> {
			try {
				ctx.addExpression(new ExpressionImpl().setName(PAID_PPE).setExpression(data.getExpression()).setScope(ExpressionScope.APPLICATION), data.getStartDate(), data.getEndDate());
			} catch (ExpressionException e) {
			}
		});
		;
		
		
	}

}