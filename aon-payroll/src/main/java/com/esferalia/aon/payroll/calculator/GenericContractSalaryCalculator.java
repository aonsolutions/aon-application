package com.esferalia.aon.payroll.calculator;

import static com.esferalia.aon.payroll.enumeration.ContextVariable.ADDITIONAL_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.ADDITIONAL_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.ALL;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGC_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGC_BASE_ENTERPRISE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGP_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGP_BASE_ENTERPRISE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.DIRECT_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.DROP_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.EMBARGO_PAID;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.EMPLOYEE_QUOTA;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.ENTERPRISE_QUOTA;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.ERE_BASES;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.ERE_FACTORS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.FRIDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.IN_KIND;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.IRPF_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.LEAVE_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MATERNITY_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MONDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MONTHLY_PAYMENTS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MONTH_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.NON_STRUCTURAL_OVERTIME_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.OFF_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.PREST_IT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.QUOTE_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.SALARY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.SATURDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.STRIKE_FACTOR;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.STRUCTURAL_OVERTIME_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.SUNDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.TC2;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.THURSDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.TOTAL_LIQUID;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.TOTAL_PAYMENT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.TUESDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.WEDNESDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.WORKED_DAYS;
import static com.esferalia.aon.salary.expression.ExpressionScope.APPLICATION;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.mvel2.CompileException;
import org.mvel2.ConversionException;
import org.mvel2.ast.IsDef;

import com.code.aon.AonVersion;
import com.code.aon.common.AonException;
import com.esferalia.aon.payroll.DelegateContractPayment;
import com.esferalia.aon.payroll.calculator.TaxCalculator.NotNowException;
import com.esferalia.aon.payroll.calculator.TaxCalculator.YesExtraException;
import com.esferalia.aon.payroll.calculator.sql.SQLContractSalaryCalculatorContext.PaymentVariable;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.ISalaryBuilder;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.calculator.ISalaryCalculator;
import com.esferalia.aon.salary.calculator.ISalaryCalculatorContext;
import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.PaymentTypeVisitor;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.CheckException;
import com.esferalia.aon.salary.expression.ExpressionContext;
import com.esferalia.aon.salary.expression.ExpressionContext.ExpressionExceptionWrapper;
import com.esferalia.aon.salary.expression.ExpressionContext.RemoveVariableError;
import com.esferalia.aon.salary.expression.ExpressionContext.RemovedExpressionVariable;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ExpressionScope;
import com.esferalia.aon.salary.expression.HideException;
import com.esferalia.aon.salary.expression.IExpression;
import com.esferalia.aon.salary.expression.IExpressionVariable;
import com.esferalia.aon.salary.expression.ITimedResult;
import com.esferalia.aon.salary.expression.ITimedVariable;
import com.esferalia.aon.salary.expression.InterruptedException;
import com.esferalia.aon.salary.expression.InvalidVariables;
import com.esferalia.aon.salary.expression.Period;
import com.esferalia.aon.salary.expression.RemoveException;
import com.esferalia.aon.salary.expression.UndefinedVariablesException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class GenericContractSalaryCalculator<T extends ISalary, C extends ISalaryCalculatorContext> implements ISalaryCalculator<T, C> {

	private static final String EXPRESSION_SYNTAX_ERROR = "Error sint\u00E1ctico en la expresi\u00F3n";
	private static final String BASE_CGC_MIN_MSG = "%s %.2f ha sido ampliada al m\u00ednimo obligatorio %.2f";
	private static final String BASE_CGC_MAX_MSG = "%s %.2f ha sido limitada al m\u00e1ximo permitido %.2f";
	private static final String BASE_CGP_MIN_MSG = "%s %.2f ha sido ampliada al m\u00ednimo obligatorio %.2f";
	private static final String BASE_CGP_MAX_MSG = "%s %.2f ha sido limitada al m\u00e1ximo permitido %.2f";
	private static final String DESCRIPTION_UNDEF_ERROR = "Error en la descripic\u00F3n, variable '%s' desconocida.";
	private static final String DESCRIPTION_UNKNOWN_ERROR = "Error desconocido en la descripic\u00F3n.";
	private static final String DESCRIPTION_SYNTAX_ERROR = "Error sint\u00E1ctico en la descripic\u00F3n.";
	private static final String BUILDER_VARIABLE = "BUILDER";

	static final String IT_PAY_MSG = "El <span <span style='color:orange;'>%s</span> devenga durante la incapacidad temporal."
			+ "<ul style='margin-left:1em;'>"
			+ "<li>¿Es una Mejora de la Prestaci\u00F3n de la SS por Incapacidad Temporal?. Elija el tipo <span style='color:orange;'>CRA 0055</span></li>"
			+ "<li>Revise la expressi\u00F3n <span style='color:orange;'>%s</span>. ¿ Falta mutiplicar por <span style='color:orange;'>DIAS_TRABAJADOS / DIAS_MES</span> ?.</li>"
			+ "</ul>";
	
	static final String STRIKE_PAY_MSG = "El <span <span style='color:orange;'>%s</span> devenga durante la huelga."
			+ "<ul style='margin-left:1em;'>"
			+ "<li>Revise la expressi\u00F3n <span style='color:orange;'>%s</span>. ¿ Falta mutiplicar por <span style='color:orange;'>DIAS_TRABAJADOS / DIAS_MES</span> ?.</li>"
			+ "</ul>";
	
	static final String CONSTANT_PAY_MSG = "Revise el concepto <span style='color:orange;'>%s</span>. ¿ Falta mutiplicar por <span style='color:orange;'>DIAS_TRABAJADOS / DIAS_MES</span> ?."
			;
	
	
	private static interface INamedContractPayment extends IContractPayment{
		public String getSurName();
	}
	
	private static final class NamedContractPayment extends DelegateContractPayment implements INamedContractPayment{
		private NamedContractPayment(IContractPayment contractPayment) {
			super(contractPayment);
		}

//		@Override
//		public String getName() {
//			String name = super.getName();
//			if ( AonStringUtils.isNotBlank(name) 
//					&& !name.startsWith("__"))
//				return name;
//			
//			return getSurName();
//		}
		
		@Override
		public String getSurName() {
			String description = super.getDescription();
			String expression = super.getExpression();
			
			if (getScope() != ExpressionScope.SYSTEM
				&& getScope() != ExpressionScope.APPLICATION
				&& AonStringUtils.isNotBlank(description) && 
				!AonStringUtils.equalsIgnoreCase(description, expression) 
				) {
				String surname = 
						description.toUpperCase()
						.replaceAll("\\s", "_")
						.replaceAll("\u00c1", "A")
						.replaceAll("\u00c9", "E")
						.replaceAll("\u00cd", "I")
						.replaceAll("\u00d3", "O")
						.replaceAll("\u00da", "U")
						.replaceAll("\u00dc", "U")
						.replaceAll("\u00d1", "N")
						.replaceAll("\\W", "")
						;
				// TODO: check surname not in ContextVariable.
				
				if ( isContextVariableName(surname) ) 
					return super.getName();
				
				if ( !AonStringUtils.equalsIgnoreCase(surname, expression) )
					return surname;
			}
			
			return super.getName();
		}

		protected boolean isContextVariableName(String surname) {
			return Arrays.stream( new ContextVariable[] {
			ADDITIONAL_HOURS})
			.map( v -> v.getName())
			.anyMatch( n -> AonStringUtils.equals(n,surname));
		}
		
	}

	public static interface IListener {

		public void onCheckError(String message);

		public void onInvalidData(String variableName, String message);

		public void onCompileError(String variableName, String message);

		public void onRemove(IContractBonus bonus);

		public void onRemove(IContractDeduction payment);

		public void onRemove(IContractPayment payment);

		public void onCheckError(IContractPayment payment, String message);

		public void onCompileError(IContractPayment payment, String message);

		public void onUndefinedData(IContractPayment payment, RemovedExpressionVariable<?> var);

		public void onInvalidData(IContractPayment payment, String variableName, String message);

		public void onUndefinedData(IContractPayment payment, String variableName, String message);

		public void onCheckError(IContractDeduction deduction, String message);

		public void onCompileError(IContractDeduction deduction, String message);

		public void onUndefinedData(IContractDeduction deduction, RemovedExpressionVariable<?> var);

		public void onInvalidData(IContractDeduction deduction, String variableName, String message);

		public void onUndefinedData(IContractDeduction deduction, String variableName, String message);

		public void onCheckError(IContractBonus bonus, String message);

		public void onCompileError(IContractBonus bonus, String message);

		public void onInvalidData(IContractBonus bonus, String variableName, String message);

	}

	public static class Listener implements IListener {

		@Override
		public void onCheckError(String message) {
		}

		@Override
		public void onInvalidData(String variableName, String message) {
		}

		@Override
		public void onCompileError(String variableName, String message) {
		}

		@Override
		public void onRemove(IContractBonus bonus) {
		}

		@Override
		public void onRemove(IContractDeduction payment) {
		}

		@Override
		public void onRemove(IContractPayment payment) {
		}

		@Override
		public void onCheckError(IContractPayment payment, String message) {
		}

		@Override
		public void onCompileError(IContractPayment payment, String message) {
		}

		@Override
		public void onUndefinedData(IContractPayment payment, RemovedExpressionVariable<?> var) {
		}

		@Override
		public void onInvalidData(IContractPayment payment, String variableName, String message) {
		}

		@Override
		public void onUndefinedData(IContractPayment payment, String variableName, String message) {
		}

		@Override
		public void onCheckError(IContractDeduction deduction, String message) {
		}

		@Override
		public void onCompileError(IContractDeduction deduction, String message) {
		}

		@Override
		public void onUndefinedData(IContractDeduction deduction, RemovedExpressionVariable<?> var) {
		}

		@Override
		public void onInvalidData(IContractDeduction deduction, String variableName, String message) {
		}

		@Override
		public void onUndefinedData(IContractDeduction deduction, String variableName, String message) {
		}

		@Override
		public void onCheckError(IContractBonus bonus, String message) {
		}

		@Override
		public void onCompileError(IContractBonus bonus, String message) {
		}

		@Override
		public void onInvalidData(IContractBonus bonus, String variableName, String message) {
		}

	}

	private static interface Resolver<T, E extends Throwable> {
		void resolve(T t) throws E;
	}

	private static class AonExceptionWrapper extends RuntimeException {

		public AonExceptionWrapper(AonException cause) {
			super(cause);
		}

		public AonException getAonException() {
			return (AonException) getCause();
		}
	}

	private static class UndefPayment extends SimpleContractPayment implements IHasPayment<IContractPayment>, INamedContractPayment {

		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

		private String surName ;
		private INamedContractPayment namedContractPayment;
		
		private UndefinedVariablesException exception;
		

		public UndefPayment(INamedContractPayment namedContractPayment, UndefinedVariablesException exception) {
			super(namedContractPayment);
			this.exception = exception;
			this.namedContractPayment = namedContractPayment;
			this.surName = namedContractPayment.getSurName();
		}

		boolean isSelfUndefined() {
			String name = getName();
			if (name == null)
				return false;

			for (String var : exception.getVariableNames())
				if (StringUtils.equals(var, name))
					return true;

			return false;
		}

		boolean dependsOn(String var) {
			if (var == null)
				return false;
			for (String v : exception.getVariableNames())
				if (var.equals(v))
					return true;
			return false;
		}

		boolean willBeDefined(Collection<String> willbeDefined) {

			for (String var : exception.getVariableNames())
				if (!willbeDefined.contains(var))
					return false;

			return true;
		}

		void onUndefinedData(GenericContractSalaryCalculator<?,?> calculator) {
			calculator.onUndefinedData(this, exception.getMessage(), exception.getVariableNames());
		}
		
		@Override
		public IContractPayment getPayment() {
			return namedContractPayment;
		}
		
		@Override
		public String getSurName() {
			return surName;
		}
		
	}
	
	private static class UndefDeduction  extends SimpleContractDeduction  {
		
		
		private UndefinedVariablesException exception;

		public UndefDeduction(IContractDeduction contractDeduction, UndefinedVariablesException exception) {
			super(contractDeduction);
			this.exception = exception;
		}

		boolean willBeDefined(Collection<String> willbeDefined) {

			for (String var : exception.getVariableNames())
				if (!willbeDefined.contains(var))
					return false;

			return true;
		}
		
	}

	private IListener listener;
	private ISalaryBuilder<T> salaryBuilder;

	public GenericContractSalaryCalculator() {
	}

	public GenericContractSalaryCalculator(ISalaryBuilder<T> salaryBuilder) {
		this.salaryBuilder = salaryBuilder;
	}

	public void setListener(IListener listener) {
		this.listener = listener;
	}

	@Override
	public void setSalaryBuilder(ISalaryBuilder<T> salaryBuilder) {
		this.salaryBuilder = salaryBuilder;
	}

	@Override
	public boolean accept(ISalaryCalculatorContext ctx) {
		return (ctx instanceof ContractSalaryCalculatorContext);
	}

	@Override
	public void initialize(ISalaryCalculatorContext ctx) throws SalaryException {
		// TODO : Borrar.
	}

	@Override
	public T calculate(C ctx) throws SalaryException {
		IContractSalaryCalculatorContext contractSalaryCalculatorContext = (IContractSalaryCalculatorContext) ctx;
		
		Date start = ctx.getStartDate();
		Date end = ctx.getEndDate();

		ExpressionContext expressionContext = ctx.getExpressionContext();

		salaryBuilder.createNewSalary();
		salaryBuilder.setContract(contractSalaryCalculatorContext.getSalaryProxy());

		expressionContext.setVariable(BUILDER_VARIABLE, salaryBuilder, start, end);

		fillEnterpriseData(contractSalaryCalculatorContext);
		fillEmployeeData(contractSalaryCalculatorContext);
		fillSalaryData(contractSalaryCalculatorContext);
		Double totalPayment = fillPayments(contractSalaryCalculatorContext);
		Double totalDeduction = fillDeductions(contractSalaryCalculatorContext);

		expressionContext.setVariable(TOTAL_LIQUID, totalPayment - totalDeduction, start, end);
		Double totalEmbargos = fillEmbargos(contractSalaryCalculatorContext);

		salaryBuilder.setTotalDeduction(totalDeduction + totalEmbargos);

		Double totalCost = fillCosts(contractSalaryCalculatorContext);
//		expressionContext.setVariable(ENTERPRISE_QUOTA, totalCost, start, end);
		Double totalBonus = fillBonus(contractSalaryCalculatorContext);

		Double totalEnterprise = totalCost - totalBonus;
		salaryBuilder.setTotalEnterprise(totalEnterprise);

		salaryBuilder.setTotalLiquid(totalPayment - totalDeduction - totalEmbargos);

		fillTimeUnits(contractSalaryCalculatorContext);

		fillData(contractSalaryCalculatorContext);

		return salaryBuilder.getSalary();
	}

	// -------------------------------------------------------------- Protected

	protected void fillEnterpriseData(IContractSalaryCalculatorContext ctx) {
		salaryBuilder.setCcc(ctx.getCcc());
		salaryBuilder.setEnterpriseName(ctx.getEnterpriseName());
		salaryBuilder.setEnterpriseAddress(ctx.getEnterpriseAddress());
		salaryBuilder.setEnterpriseDocument(ctx.getEnterpriseDocument());
		salaryBuilder.setEnterpriseCity(ctx.getEnterpriseCity());
	}

	protected void fillEmployeeData(IContractSalaryCalculatorContext ctx) {
		
		salaryBuilder.setEmployeeName(ctx.getEmployeeName());
		salaryBuilder.setEmployeeDocument(ctx.getEmployeeDocument());
		salaryBuilder.setRegistration(ctx.getRegistration());
		salaryBuilder.setSocialSecurityNumber(ctx.getSocialSecurityNumber());
		salaryBuilder.setCategory(ctx.getCategory());
		salaryBuilder.setQuoteGroup(ctx.getQuoteGroup());
		salaryBuilder.setSeniorityDate(ctx.getSeniorityDate());
		salaryBuilder.setRegime(Optional.ofNullable(ctx.getSSRegime()).orElse(SSRegimeType.GENERAL).getCode());
	}

	protected void fillSalaryData(IContractSalaryCalculatorContext ctx) {
		salaryBuilder.setIssueDate(ctx.getIssueDate());
		salaryBuilder.setStartDate(ctx.getStartDate());
		salaryBuilder.setEndDate(ctx.getEndDate());
		salaryBuilder.setChargeDate(ctx.getChargeDate());
		salaryBuilder.setType(ctx.getSalaryType());

	}

	protected Double fillPayments(IContractSalaryCalculatorContext ctx) throws SalaryException {
		try {

			ExpressionContext expressionContext = ctx.getExpressionContext();

			Date start = ctx.getStartDate();
			Date end = ctx.getEndDate();

			QuoteCalculator quoteCalculator = getQuoteCalculator(ctx);

			TaxCalculator taxCalculator = getTaxCalculator(ctx);

			Date issueDate = ctx.getIssueDate();
			
			
			Set<String> paymentsVars = new HashSet<String>();
			LinkedList<UndefPayment> undefPayments = new LinkedList<UndefPayment>();
			Collection<IContractPayment> contractPayments = ctx.getContractPayments();
			
			LinkedList<UndefPayment> undefTotalPayments = new LinkedList<UndefPayment>();
			LinkedList<UndefPayment> undefMonthlyPayments = new LinkedList<UndefPayment>();

			HashSet<String> alreadyDefined = new HashSet<String>();

			List<Period> leavePeriods = new ArrayList<Period>(); 
			for ( Period p : expressionContext.getPeriods(LEAVE_DAYS))
				if ( expressionContext.getVariable(WORKED_DAYS, p.getStart(), p.getEnd()) == null)
					leavePeriods.add(p);

			List<Period> offPeriods = new ArrayList<Period>(); 
			for ( Period p : expressionContext.getPeriods(STRIKE_FACTOR))
				if ( expressionContext.getVariable(WORKED_DAYS, p.getStart(), p.getEnd()) == null)
					offPeriods.add(p);

			for ( Period p : expressionContext.getPeriods(DROP_DAYS))
				if ( expressionContext.getVariable(WORKED_DAYS, p.getStart(), p.getEnd()) == null)
					offPeriods.add(p);

			for ( Period p : expressionContext.getPeriods(OFF_DAYS))
				if ( expressionContext.getVariable(WORKED_DAYS, p.getStart(), p.getEnd()) == null)
					offPeriods.add(p);
			
			for ( ContextVariable ereFactor : ERE_FACTORS )
				for ( Period p : expressionContext.getPeriods(ereFactor))
						offPeriods.add(p);			

			for (IContractPayment icontractPayment : contractPayments) {
				INamedContractPayment contractPayment = new NamedContractPayment(icontractPayment);
				try {
					resolvePayment(contractPayment, start, end, issueDate, expressionContext, taxCalculator,
							quoteCalculator, leavePeriods, offPeriods);
					alreadyDefined.add(contractPayment.getName());
					alreadyDefined.add(contractPayment.getSurName());
					copyResults(expressionContext, contractPayment);
				} catch (UndefinedTotalPaymentException e) {
					undefTotalPayments.add(new UndefPayment(contractPayment, e));
				} catch (UndefinedContextVariablesException e) {
					onUndefinedData(contractPayment, e.getMessage(), e.getVariableNames());
					addResult(expressionContext, contractPayment.getName(), start, end, 0.00);
					addResult(expressionContext, contractPayment.getSurName(), start, end, 0.00);
				} catch (UndefinedVariablesException e) {
					UndefPayment undefPayment = new UndefPayment(contractPayment, e);
					if (undefPayment.dependsOn(MONTHLY_PAYMENTS)) {
						undefMonthlyPayments.add(undefPayment);
					} else if (!undefPayment.isSelfUndefined()) {
						undefPayments.add(undefPayment);
					} else {
						undefPayment.onUndefinedData(this);
					}
				}
				paymentsVars.add(contractPayment.getName());
				paymentsVars.add(contractPayment.getSurName());
			}

			Collections.sort(undefPayments, (p1, p2) -> {
				if (p1.dependsOn(p2.getName()))
					return 1;
				if (p2.dependsOn(p1.getName()))
					return -1;

				if (p1.getName() == p2.getName())
					return 0;
				if (p1.getName() == null)
					return 1;
				if (p2.getName() == null)
					return -1;
				return p1.getName().compareTo(p2.getName());
			});

			HashSet<String> willBeDefined = new HashSet<String>();

			for (ListIterator<UndefPayment> listIterator = undefPayments.listIterator(); listIterator.hasNext();) {
				UndefPayment undefPayment = listIterator.next();
				if (undefPayment.willBeDefined(paymentsVars)) {
					willBeDefined.addAll(paymentsVars); // ??? Why?
					willBeDefined.add(undefPayment.getName());
					willBeDefined.add(undefPayment.getSurName());
					continue;
				}
				// clean undefined ...
				listIterator.remove();
				undefPayment.onUndefinedData(this);
				// will not be calculated, but like it's a payment save it like
				// zero.
				alreadyDefined.add(undefPayment.getName());
				addResult(expressionContext, undefPayment.getName(), start, end, 0.00);
				addResult(expressionContext, undefPayment.getSurName(), start, end, 0.00);
			}
			// Many payments can share same variable...
			paymentsVars.addAll(willBeDefined);
			paymentsVars.addAll(alreadyDefined);

			int undefined = 0;
			while (undefPayments.size() > 0) {
				UndefPayment undefPayment = undefPayments.pop();
				try {
					resolvePayment(undefPayment, start, end, issueDate, expressionContext, taxCalculator,
							quoteCalculator, leavePeriods, offPeriods);
					paymentsVars.add(undefPayment.getName());
					paymentsVars.add(undefPayment.getSurName());
					copyResults(expressionContext, undefPayment);
				} catch (UndefinedTotalPaymentException e) {
					undefTotalPayments.add(undefPayment);
				} catch (UndefinedContextVariablesException e) {
					paymentsVars.remove(undefPayment.getName());
					addResult(expressionContext, undefPayment.getName(), start, end, 0.00);
					addResult(expressionContext, undefPayment.getSurName(), start, end, 0.00);
				} catch (UndefinedVariablesException e) {

					if (undefPayment.willBeDefined(paymentsVars)) {
						undefPayments.add(undefPayment);

						if (++undefined >= undefPayments.size())
							break; // we've already eval all undef payments
					} else {
						undefPayment.onUndefinedData(this);
						paymentsVars.remove(undefPayment.getName());
						paymentsVars.remove(undefPayment.getSurName());
					}
				}
			}

			for (UndefPayment undefPayment : undefPayments)
				undefPayment.onUndefinedData(this);
			
			
			Map<Period, Double> monthlyPayments = taxCalculator.getAmounts(PaymentType.CRA_0001);
			if ( monthlyPayments.isEmpty() )  {
				monthlyPayments = Collections.singletonMap(new Period(start,end), 0.00);
			}
			
			for (Period p : Period.sub(List.copyOf(monthlyPayments.keySet()), leavePeriods ) ) {
				expressionContext.setVariable(MONTHLY_PAYMENTS, monthlyPayments.get(p), p.getStart(), p.getEnd());
			}
			
			
			for (UndefPayment undefMonthlyPayment : undefMonthlyPayments) {
				try {
					resolvePayment(undefMonthlyPayment, start, end, issueDate, expressionContext, taxCalculator,
							quoteCalculator, leavePeriods, offPeriods);
					copyResults(expressionContext, undefMonthlyPayment);
				} catch (UndefinedVariablesException e) {
					onUndefinedData(undefMonthlyPayment, e.getMessage(), e.getVariableNames());
				}
			}

			double totalPayment = taxCalculator.getTotalPayment();
			expressionContext.setVariable(TOTAL_PAYMENT, totalPayment, start, end);

			Date irpfDate = ctx.getIrpfDate();
			expressionContext.setVariable(IRPF_BASE, taxCalculator.getIrpfBase(), irpfDate, irpfDate);
			
			undefTotalPayments.sort((p1,p2)-> AonNumberUtils.compare(p1.getId(),p2.getId() ) );
			for (UndefPayment undefTotalPayment : undefTotalPayments) {
				try {
					resolvePayment(undefTotalPayment, start, end, issueDate, expressionContext, taxCalculator,
							quoteCalculator, leavePeriods, offPeriods);
					copyResults(expressionContext, undefTotalPayment);
				} catch (UndefinedVariablesException e) {
					onUndefinedData(undefTotalPayment, e.getMessage(), e.getVariableNames());
				}
			}

			totalPayment = taxCalculator.getTotalPayment();
			expressionContext.setVariable(TOTAL_PAYMENT, totalPayment, start, end);

			salaryBuilder.setTotalPayment(totalPayment);

			salaryBuilder.setRemuneration(taxCalculator.getRenumeration());

			salaryBuilder.setItBase(quoteCalculator.getItBase());

			salaryBuilder.setIrpfBase(taxCalculator.getIrpfBase());
			salaryBuilder.setMoneyIrpfBase(taxCalculator.getMoneyIrpfBase());
			salaryBuilder.setInkindIrpfBase(taxCalculator.getInKindIrpfBase());
			expressionContext.setVariable(IRPF_BASE, taxCalculator.getIrpfBase(), irpfDate, irpfDate);

			Double rawCgcbase = quoteCalculator.getRawCgcBase();
			salaryBuilder.setRawCgcBase(rawCgcbase);

			Double cgcBase = rawCgcbase;
			try {
				cgcBase = quoteCalculator.getCgcBase();
			} catch (UndefinedVariablesException e) {
				onInvalidData(e.getVariableNames());
			}

			if (AonNumberUtils.compare(rawCgcbase, cgcBase, 3) > 0) 
				onCheckError(String.format(BASE_CGC_MAX_MSG, CGC_BASE.getDescription(), rawCgcbase, cgcBase));
			else if (AonNumberUtils.compare(rawCgcbase, cgcBase, 3) < 0) {
				fixBaseCgcMin(expressionContext, start, end, quoteCalculator, taxCalculator, issueDate, leavePeriods,
							offPeriods, rawCgcbase, cgcBase);
				rawCgcbase = quoteCalculator.getRawCgcBase();
				salaryBuilder.setRawCgcBase(rawCgcbase);
				cgcBase = quoteCalculator.getCgcBase();
			}


			Double ereBase = quoteCalculator.getEreBase();
			if (ereBase != null)
				cgcBase += ereBase;
			Double maternityBase = quoteCalculator.getMaternityBase();
			if (maternityBase != null)
				cgcBase += maternityBase;
//			Double directPayBase = quoteCalculator.getDirectPayBase();
//			if (directPayBase != null)
//				cgcBase += directPayBase;
			salaryBuilder.setCgcBase(cgcBase);
			
			addVars(expressionContext, CGC_BASE_ENTERPRISE,  ERE_BASES);
			addVars(expressionContext, CGC_BASE_ENTERPRISE,  MATERNITY_BASE, DIRECT_BASE, CGC_BASE);
//			if (cgcBase != null)
//				expressionContext.setVariable(CGC_BASE_ENTERPRISE, cgcBase, start, end);
			

			Double rawCgpbase = quoteCalculator.getRawCgpBase();

			Double cgpBase = rawCgpbase;
			try {
				cgpBase = quoteCalculator.getCgpBase();
			} catch (UndefinedVariablesException e) {
				onInvalidData(e.getVariableNames());
			}
			if (AonNumberUtils.compare(rawCgpbase, cgpBase, 3) > 0) // rawCgcbase
																	// > cgpBase
				onCheckError(String.format(BASE_CGP_MAX_MSG, CGP_BASE.getDescription(), rawCgcbase, cgpBase));
			else if (AonNumberUtils.compare(rawCgpbase, cgpBase, 3) < 0) // rawCgcbase
																			// <
																			// cgpBase
				onCheckError(String.format(BASE_CGP_MIN_MSG, CGP_BASE.getDescription(), rawCgcbase, cgpBase));

			if (ereBase != null)
				cgpBase += ereBase;
			if (maternityBase != null)
				cgpBase += maternityBase;
//			if (directPayBase != null)
//				cgpBase += directPayBase;
			salaryBuilder.setCgpBase(cgpBase);

			addVars(expressionContext, CGP_BASE_ENTERPRISE, ERE_BASES);
			addVars(expressionContext, CGP_BASE_ENTERPRISE, MATERNITY_BASE, DIRECT_BASE, CGP_BASE);
			//copyResults(expressionContext, CGP_BASE, CGP_BASE_ENTERPRISE);
//			if (cgpBase != null)
//				expressionContext.setVariable(CGP_BASE_ENTERPRISE, cgpBase, start, end);

			Double nonStructuralBase = quoteCalculator.getNonStructuralBase();
			salaryBuilder.setNonHExtraBase(nonStructuralBase);
			// if (nonStructuralBase != null)
			// expressionContext.setVariable(NON_STRUCTURAL_OVERTIME_BASE,
			// nonStructuralBase, start, end);

			Double structuralBase = quoteCalculator.getStructuralBase();
			salaryBuilder.setHExtraBase(structuralBase);
			// if (structuralBase != null)
			// expressionContext.setVariable(STRUCTURAL_OVERTIME_BASE,
			// structuralBase, start, end);

			salaryBuilder.setProExtBase(quoteCalculator.getProExtBase());

			return taxCalculator.getTotalPayment();
		} catch (SalaryExpressionException e) {
			throw e.getSalaryException();
		} catch (ExpressionException e) {
			throw new SalaryException(e.getMessage(), e);
		} catch (AonException e) {
			throw new SalaryException(e.getMessage(), e);
		} catch (RuntimeException e) {
			throw e;
			// TODO: handle exception
		}
	}


	protected QuoteCalculator getQuoteCalculator(IContractSalaryCalculatorContext ctx) {
		return QuoteCalculator.getQuoteCalculator(ctx);
	}

	protected TaxCalculator getTaxCalculator(IContractSalaryCalculatorContext ctx) {
		return TaxCalculator.getTaxCalculator(ctx);
	}

	protected Double fillDeductions(IContractSalaryCalculatorContext ctx) throws SalaryException {
		try {
			double totalIrpf = 0;
			double totalDeduction = 0;
			double ssContributions = 0;

			Date start = ctx.getStartDate();
			Date end = ctx.getEndDate();
			
			LinkedList<IContractDeduction> undefContractDeductions = new LinkedList<IContractDeduction>(); 
			
			Collection<IContractDeduction> contractDeductions = ctx.getContractDeductions();
			ExpressionContext expressionContext = ctx.getExpressionContext();
			for (IContractDeduction contractDeduction : contractDeductions) {
				DeductionType type = contractDeduction.getType();

				Date deductionStart = null;
				Date deductionEnd = null;

				if (type.isTaxDeduction()) {
					deductionStart = ctx.getIrpfDate();
					deductionEnd = ctx.getIrpfDate();

				} else {
					deductionStart = Period.max(contractDeduction.getStartDate(), start);
					deductionEnd = Period.min(contractDeduction.getEndDate(), end);
					if (deductionEnd.before(deductionStart)) {
						continue; // TODO : must be done in context ?
					}
				}
				try {

					Double deduction = resolveDeduction(expressionContext, contractDeduction, deductionStart,
							deductionEnd);
					totalDeduction += deduction;

					if (type.isSsDeduction()) {
						ssContributions += deduction;
						expressionContext.setVariable(EMPLOYEE_QUOTA, ssContributions, start, end);
					} else if (type.isTaxDeduction()) {
						totalIrpf += deduction;
					}
				} catch (HideException e) {
					if (AonStringUtils.isNotBlank(e.getMessage()))
						onCheckError(e.getMessage());
				} catch (RemoveException e) {
					// TODO: Something ??? It's really necessary...
				} catch (InvalidVariables e) {
					onInvalidData(contractDeduction, e.getMessage(), e.getVariables());
				} catch (InterruptedException e) {
					throw e; // Not catch
				} catch (CheckException e) {
					onCheckError(contractDeduction, e.getMessage());
				} catch (RemoveVariableError e) {
					onUndefinedData(contractDeduction, e.getVariable());
				} catch (UndefinedVariablesException e) {
					undefContractDeductions.add(new UndefDeduction(contractDeduction, e));
					//onUndefinedData(contractDeduction, e.getMessage(), e.getVariableNames());
				} catch (CompileException e) {
					onCompileError(contractDeduction, getSyntaxExpressionErrorMessage(contractDeduction));

				}
			}
			
			for (IContractDeduction contractDeduction : undefContractDeductions) {
				DeductionType type = contractDeduction.getType();

				Date deductionStart = Period.max(contractDeduction.getStartDate(), start);
				Date deductionEnd = Period.min(contractDeduction.getEndDate(), end);
				
				try {

					Double deduction = resolveDeduction(expressionContext, contractDeduction, deductionStart,
							deductionEnd);
					totalDeduction += deduction;

					if (type.isSsDeduction()) {
						ssContributions += deduction;
						expressionContext.setVariable(EMPLOYEE_QUOTA, ssContributions, start, end);
					} 
				} catch (HideException e) {
					if (AonStringUtils.isNotBlank(e.getMessage()))
						onCheckError(e.getMessage());
				} catch (RemoveException e) {
					// TODO: Something ??? It's really necessary...
				} catch (InvalidVariables e) {
					onInvalidData(contractDeduction, e.getMessage(), e.getVariables());
				} catch (InterruptedException e) {
					throw e; // Not catch
				} catch (CheckException e) {
					onCheckError(contractDeduction, e.getMessage());
				} catch (RemoveVariableError e) {
					onUndefinedData(contractDeduction, e.getVariable());
				} catch (UndefinedVariablesException e) {
					onUndefinedData(contractDeduction, e.getMessage(), e.getVariableNames());
				} // compilation error 
			}
			

			salaryBuilder.setTotalIrpf(totalIrpf);
			salaryBuilder.setTotalSS(ssContributions);

			return totalDeduction;
		} catch (ExpressionException e) {
			throw new SalaryException(e.getMessage(), e);
		} catch (AonException e) {
			throw new SalaryException(e.getMessage(), e);
		} catch (Exception e) {
			throw new SalaryException(e.getMessage(), e);
		}
	}

	protected Double fillEmbargos(IContractSalaryCalculatorContext ctx) throws SalaryException {

		double total = 0.00;

		try {

			Collection<IContractEmbargo> contractEmbargos = ctx.getContractEmbargos();

			Date start = ctx.getStartDate();
			Date end = ctx.getEndDate();

			ExpressionContext expressionContext = ctx.getExpressionContext();

			for (IContractEmbargo contractEmbargo : contractEmbargos) {

				Date embargoStart = Period.max(contractEmbargo.getStartDate(), start);
				Date embargoEnd = Period.min(contractEmbargo.getEndDate(), end);

				double left = contractEmbargo.getAmount();
				expressionContext.setVariable(EMBARGO_PAID, left, embargoStart, embargoEnd);
				try {
					double embargo = resolveEmbargo(expressionContext, contractEmbargo, embargoStart, embargoEnd);
					total += embargo;
				} catch (RemoveException e) {
					// TODO: Something ??? It's really necessary...
				} catch (InvalidVariables e) {
					onInvalidData(contractEmbargo, e.getMessage(), e.getVariables());
				} catch (CheckException e) {
					onCheckError(contractEmbargo, e.getMessage());
				} catch (RemoveVariableError e) {
					onUndefinedData(contractEmbargo, e.getVariable());
				} catch (UndefinedVariablesException e) {
					onUndefinedData(contractEmbargo, e.getMessage(), e.getVariableNames());
				} catch (CompileException e) {
					onCompileError(contractEmbargo, getSyntaxExpressionErrorMessage(contractEmbargo));
				}

			}

		} catch (ExpressionException e) {
			throw new SalaryException(e.getMessage(), e);
		} catch (AonException e) {
			throw new SalaryException(e.getMessage(), e);
		}

		return total;

	}

	protected Double fillCosts(IContractSalaryCalculatorContext ctx) throws SalaryException {
		double total = 0.00; // TODO; mejor null ???
		try {

			Collection<IContractCost> contractCosts = ctx.getContractCosts();

			Date start = ctx.getStartDate();
			Date end = ctx.getEndDate();

			ExpressionContext expressionContext = ctx.getExpressionContext();

			for (IContractCost contractCost : contractCosts) {

				Date costStart = Period.max(contractCost.getStartDate(), start);
				Date costEnd = Period.min(contractCost.getEndDate(), end);

				try {
					List<ITimedResult<Double>> amounts = expressionContext.addExpression(contractCost, costStart,
							costEnd, Double.class);
					double cost = 0.00;
					for (ITimedResult<Double> amount : amounts) {
						Double value = amount.getValue();
						if (value != null) {
							String description = null;
							Period period = amount.getPeriod();
							try {
								
								description = expressionContext.evalTemplate(contractCost.getDescription(),
										period.getStart(), period.getEnd());
							} catch (CompileException e) {
								onCompileError(contractCost, DESCRIPTION_SYNTAX_ERROR);
							} catch (UndefinedVariablesException e) {
								onCheckError(contractCost,
										String.format(DESCRIPTION_UNDEF_ERROR, e.getVariableNames()[0]));
							} catch (Exception e) {
								onCheckError(contractCost, DESCRIPTION_UNKNOWN_ERROR);
							}
							salaryBuilder.addCost(value, description, period.getStart(), period.getEnd(), contractCost, amount.getContext());
							cost += value;
							addResult(expressionContext, ENTERPRISE_QUOTA.getName(), period.getStart(), period.getEnd(), value);
						}
					}
					total += cost;
					
				} catch (RemoveException | RemoveVariableError e) {
					// TODO: Something ??? It's really necessary...
				} catch (IllegalArgumentException e) {
					// costStart > costEnd, ignore .
				} catch (UndefinedVariablesException e) {

				} catch (ExpressionException e) {
					throw new SalaryException(e.getMessage(), e);
				}

			}

		} catch (AonException e) {
			throw new SalaryException(e.getMessage(), e);
		}

		return total;
	}

	protected Double fillBonus(IContractSalaryCalculatorContext ctx) throws SalaryException {
		double total = 0.00; // TODO; mejor null ???
		try {

			Collection<IContractBonus> contractBonuses = ctx.getContractBonus();

			Date start = ctx.getStartDate();
			Date end = ctx.getEndDate();

			ExpressionContext expressionContext = ctx.getExpressionContext();

			for (IContractBonus contractBonus : contractBonuses) {

				Date bonusStart = Period.max(contractBonus.getStartDate(), start);
				Date bonusEnd = Period.min(contractBonus.getEndDate(), end);

				if (bonusEnd.before(bonusStart)) {
					continue; // TODO : must be done in context ?
				}
				
				if ( bonusStart.after(start))
					ContextFunctions.section(ctx.getExpressionContext(), AonDateUtils.add(bonusStart, Calendar.DAY_OF_MONTH,-1));
				if ( bonusEnd.before(end))
					ContextFunctions.section(ctx.getExpressionContext(), bonusEnd);
					
					
				total += resolveBonus(bonusStart, bonusEnd, contractBonus, expressionContext);
			}

		} catch (UndefinedVariablesException e) {
			// TODO: Something Here
		} catch (ExpressionException e) {
			throw new SalaryException(e.getMessage(), e);
		} catch (AonException e) {
			throw new SalaryException(e.getMessage(), e);
		}

		return total;
	}

	protected double resolveBonus(Date bonusStart, Date bonusEnd, IContractBonus contractBonus,
			ExpressionContext expressionContext) throws ExpressionException, UndefinedVariablesException {
		double bonus = 0.00;
		try {
			List<ITimedResult<Double>> amounts = expressionContext.eval(contractBonus.getExpression(),
					bonusStart, bonusEnd, Double.class);

			
			//amounts = fixBonusResults(contractBonus, amounts);
			
			for (ITimedResult<Double> amount : amounts) {
				Double value = amount.getValue();
				Period period = amount.getPeriod();
				if (value != null) {
					String description = null;
					try {
						description = expressionContext.evalTemplate(contractBonus.getDescription(),
								period.getStart(), period.getEnd());
					} catch (CompileException e) {
						onCompileError(contractBonus, DESCRIPTION_SYNTAX_ERROR);
					} catch (UndefinedVariablesException e) {
						onCheckError(contractBonus,
								String.format(DESCRIPTION_UNDEF_ERROR, e.getVariableNames()[0]));
					} catch (Exception e) {
						onCheckError(contractBonus, DESCRIPTION_UNKNOWN_ERROR);
					}
					salaryBuilder.addBonus(value, description, period.getStart(), period.getEnd(), contractBonus, amount.getContext());
				}
				if (amount.getValue() != null)
					bonus += amount.getValue();
				
				
			}
			return bonus;

		} catch (InvalidVariables e) {
			onInvalidData(contractBonus, e.getMessage(), e.getVariables());
		} catch (CheckException e) {
			onCheckError(contractBonus, e.getMessage());
		} catch (HideException e) {
			// Hide, do nothing
		} catch (RemoveException | RemoveVariableError e) {
			onRemove(contractBonus);
		} catch (CompileException e) {
			onCompileError(contractBonus, getSyntaxExpressionErrorMessage(contractBonus));
		}
		return bonus;
	}

	protected void forEachPaymentPeriod(IContractPayment contractPayment, Date start, Date end,
			ExpressionContext expressionContext, Resolver<Period, AonException> resolver) throws AonException {

		Date paymentStart = Period.max(contractPayment.getStartDate(), start);
		Date paymentEnd = Period.min(contractPayment.getEndDate(), end);
		if (paymentEnd.before(paymentStart)) {
			return; // TODO : must be done in context ?
		}

		Period paymentPeriod = new Period(paymentStart, paymentEnd);

		if (contractPayment.getType() == null) {
			resolver.resolve(paymentPeriod);
			return;
		}

		try {
			contractPayment.getType().accept(new PaymentTypeVisitor() {

				@Override
				public void visitOther(PaymentType paymentType) {
					try {
						resolver.resolve(paymentPeriod);
					} catch (AonException e) {
						throw new AonExceptionWrapper(e);
					}
				}

				@Override
				public void visitSalaryInKind(PaymentType paymentType) {
					try {
						resolver.resolve(paymentPeriod);
					} catch (AonException e) {
						throw new AonExceptionWrapper(e);
					}
				}

				@Override
				public void visitStructuralHours(PaymentType paymentType) {
					visitNonStructuralHours(paymentType);
				}

				@Override
				public void visitNonStructuralHours(PaymentType paymentType) {
					List<Period> periods = Period.intersect(Collections.singletonList(paymentPeriod),
							expressionContext.getPeriods(WORKED_DAYS));
					try {
						for (Period p : periods)
							resolver.resolve(p);
					} catch (AonException e) {
						throw new AonExceptionWrapper(e);
					}
				}
			});
		} catch (AonExceptionWrapper e) {
			throw e.getAonException();
		}

	}

	protected List<ITimedResult<Double>> fixConstantResult(IContractPayment contractPayment, ITimedResult<Double> result, Date start, Date end, ExpressionContext expressionContext) 
	throws UnsupportedOperationException, UndefinedVariablesException
	{
		if ( Period.compare(contractPayment.getStartDate(), start) < 0 ||
				Period.compare(contractPayment.getEndDate(), end) > 0 ) 
			throw new UnsupportedOperationException(String.format(CONSTANT_PAY_MSG, contractPayment.getDescription())); 
		
		return Collections.singletonList(result);
	}

	protected List<ITimedResult<Double>> fixConstantAgreementResult(IContractPayment contractPayment, ITimedResult<Double> result, Date start, Date end, ExpressionContext expressionContext) 
	throws UnsupportedOperationException, UndefinedVariablesException
	{
		return Collections.singletonList(result);
	}

	protected List<ITimedResult<Double>> fixConstantExtraResult(IContractPayment contractPayment, ITimedResult<Double> result, Date start, Date end, ExpressionContext expressionContext) 
	throws UnsupportedOperationException, UndefinedVariablesException
	{
		return Collections.singletonList(result);
	}

	protected List<ITimedResult<Double>> fixGuaranteedResults(IContractPayment contractPayment, List<ITimedResult<Double>> results, List<Period> its , Date start, Date end, ExpressionContext expressionContext) 
	throws UnsupportedOperationException, UndefinedVariablesException
	{
		return results;
	}

	protected List<ITimedResult<Double>> fixImprovementResults(IContractPayment contractPayment, List<ITimedResult<Double>> results, List<Period> offs , Date start, Date end, ExpressionContext expressionContext) 
	throws UnsupportedOperationException, UndefinedVariablesException
	{

		return results;
	}

	protected List<ITimedResult<Double>> fixConstantAgreementGuaranteed(IContractPayment contractPayment, ITimedResult<Double> results, List<Period> its , Date start, Date end, ExpressionContext expressionContext) 
	throws UnsupportedOperationException, UndefinedVariablesException
	{
		return Collections.singletonList(results);
	}
	

	protected List<ITimedResult<Double>> fixItResults(IContractPayment contractPayment, List<ITimedResult<Double>> results, List<Period> its , Date start, Date end, ExpressionContext expressionContext) 
	throws UnsupportedOperationException, UndefinedVariablesException
	{
		if (results.size() == 1 
			&& ( contractPayment.getType() == PaymentType.CRA_0002 
			|| contractPayment.getType() == PaymentType.CRA_0003 ))
			return  shareResults(results.get(0), its);
		
		if (results.size() == 1 && results.get(0).getContext().isEmpty() ) {
			//;
			System.out.println("Fix Constant...!!!!!");
		}

		throw new UnsupportedOperationException(String.format(IT_PAY_MSG, contractPayment.getDescription(),
				contractPayment.getExpression())); 
	}

	protected List<ITimedResult<Double>> fixStrikeResults(IContractPayment contractPayment, List<ITimedResult<Double>> results, List<Period> its , Date start, Date end, ExpressionContext expressionContext) 
	throws UnsupportedOperationException
	{
		throw new UnsupportedOperationException(String.format(STRIKE_PAY_MSG, contractPayment.getDescription(),
				contractPayment.getExpression())); 
	}

	protected List<ITimedResult<Double>> fixExtraResults(IContractPayment contractPayment, List<ITimedResult<Double>> results, Date start, Date end, ExpressionContext expressionContext) 
	{
		return results;
	}
	
	protected void fixBaseCgcMin(ExpressionContext expressionContext, Date start, Date end,
			QuoteCalculator quoteCalculator, TaxCalculator taxCalculator, Date issueDate, List<Period> leavePeriods,
			List<Period> offPeriods, Double rawCgcbase, Double cgcBase) throws AonException {
		onCheckError(String.format(BASE_CGC_MIN_MSG, CGC_BASE.getDescription(), rawCgcbase, cgcBase));
	}

	protected PaymentType getPaymentType(IContractPayment contractPayment) {
		return contractPayment.getType();
	}

	protected void resolvePayment(IContractPayment contractPayment,
			Date start,
			Date end,
			Date issueDate,
			ExpressionContext expressionContext, 
			TaxCalculator taxCalculator, 
			QuoteCalculator quoteCalculator, 
			List<Period> leavePeriods, 
			List<Period> strikePeriods)
			throws AonException {
		Date paymentStart = Period.max(contractPayment.getStartDate(), start);
		Date paymentEnd = Period.min(contractPayment.getEndDate(), end);
		if (paymentEnd.before(paymentStart)) {
			return; // TODO : must be done in context ?
		}

		expressionContext.setVariable(
				ContextVariable.PAYMENT_VARIABLE, 
				new PaymentVariable(contractPayment),
				paymentStart, paymentEnd);

		String name = contractPayment.getName();
		
		try {
			List<ITimedResult<Double>> results = expressionContext.eval(contractPayment.getExpression(), paymentStart,
					paymentEnd, Double.class);
			
			// Fix variable with same name than payment. Remove variable.?
			results.stream()
			.filter(r->r.getContext().containsKey(name))
			.findAny().ifPresent( r-> expressionContext.removeVariable(name));
			
			PaymentType contractPaymentType = getPaymentType(contractPayment);
			
			if ( contractPayment.getScope() == APPLICATION ) {
				; // Skip APPLICATION Concepts
			} else if (contractPaymentType != PaymentType.CRA_0008
					&& contractPaymentType != PaymentType.CRA_0055 
					&& !AonStringUtils.equals(ContextVariable.GUARENTEED, name)
					&& !AonStringUtils.equals(ContextVariable.PREST_IT, name)
					&& Period.intersects(results.stream().filter(r -> r.getValue() != null && r.getValue() != 0.00)
							.map(r -> r.getPeriod()).iterator(), leavePeriods.iterator())) {
				try {
					results = fixItResults(contractPayment, results, leavePeriods, start, end, expressionContext);
				} catch (UnsupportedOperationException e) {
					onCheckError(contractPayment, e.getMessage());
				}
			} else if ( contractPaymentType != PaymentType.CRA_0006
					&& !AonStringUtils.equals(ContextVariable.IMPROVEMENT, name)
					&& Period.intersects(results.stream().filter(r -> r.getValue() != null && r.getValue() != 0.00)
					.map(r -> r.getPeriod()).iterator(), strikePeriods.iterator())) {
				try {
					results = fixStrikeResults(contractPayment, results, strikePeriods, start, end, expressionContext);
				} catch (UnsupportedOperationException e) {
					onCheckError(contractPayment, e.getMessage());
				}
			} else  if ((contractPaymentType == PaymentType.CRA_0055
					|| AonStringUtils.equals(ContextVariable.GUARENTEED, name))
					&& !Period.intersects(results.stream().filter(r -> r.getValue() != null && r.getValue() != 0.00)
							.map(r -> r.getPeriod()).iterator(), leavePeriods.iterator())) {
				// GARANTIZADO... at NO I.T
				results = fixGuaranteedResults(contractPayment, results, leavePeriods, start, end, expressionContext);
			} else  if ((contractPaymentType == PaymentType.CRA_0056
					|| AonStringUtils.equals(ContextVariable.IMPROVEMENT, name))) {
				// MEJORAS... at NO 
				try {
					results = fixImprovementResults(contractPayment, results, strikePeriods, start, end, expressionContext);
				} catch (NullPointerException e) {
				}
			} else if ( results.size() == 1 && 
					results.get(0).getValue() != null && 
					results.get(0).getValue() > 0.00 && 
					results.get(0).getContext().size() == 0 ) {
				try {
					results = fixConstantResult(contractPayment, results.get(0), start, end, expressionContext);
				} catch (UnsupportedOperationException e) {
					onCheckError(contractPayment, e.getMessage());
				}
			} else if ( results.size() == 1 && 
					results.get(0).getValue() != null && 
					results.get(0).getValue() > 0.00 &&
					isExtra(expressionContext) &&
					(contractPaymentType == PaymentType.CRA_0004) &&
					allAgreementConstants(results.get(0).getContext()) ) {
				results = fixConstantExtraResult(contractPayment, results.get(0), paymentStart, paymentEnd, expressionContext);
			} else if ( results.size() == 1 && 
					results.get(0).getValue() != null && 
					results.get(0).getValue() > 0.00 && 
					( 
					isPartialMonth(results.get(0))  
					|| isPartial(expressionContext, results.get(0).getPeriod())
					) &&
					allAgreementConstants(results.get(0).getContext()) ) {
				try {
					results = fixConstantAgreementResult(contractPayment, results.get(0), start, end, expressionContext);
				} catch (UnsupportedOperationException e) {
					onCheckError(contractPayment, e.getMessage());
				}
			} else if ( results.size() == 1 && 
					results.get(0).getValue() != null && 
					results.get(0).getValue() > 0.00 && 
					(contractPaymentType == PaymentType.CRA_0055) &&
					contractPayment.getScope() == ExpressionScope.AGREEMENT &&
					allAgreementConstants(results.get(0).getContext()) ) {
				try {
					results = fixConstantAgreementGuaranteed(contractPayment, results.get(0), leavePeriods, start, end, expressionContext);
				} catch (UnsupportedOperationException e) {
					onCheckError(contractPayment, e.getMessage());
				}
			} 
			
			if ( !results.isEmpty() && 
					contractPaymentType == PaymentType.CRA_0004 && 
					contractPayment.getSalaryType() == SalaryType.SALARY ) {
				if ( !results.get(0).getContext().containsKey(ContextVariable.PRORATION))
					results = fixExtraResults(contractPayment, results, start, end, expressionContext);
			}			
			
			double resultsDouble = results.stream().filter( r -> r.getValue() != null ).collect(Collectors.summingDouble( r -> r.getValue() ));		

			for (ITimedResult<Double> result : results) {

				Date resultStart = result.getPeriod().getStart();
				Date resultEnd = result.getPeriod().getEnd();

				Double resultDouble = result.getValue();
				double resultValue = AonNumberUtils.isValid(resultDouble) ? resultDouble : 0.00;

				addResult(expressionContext, name, resultStart, resultEnd, resultValue);

				expressionContext.setVariable(ALL, resultValue, resultStart, resultEnd);

				// Here we add 'all' variables involved in quote.
				double quote = 0.00;
				List<ITimedResult<Double>> quoteResults = quoteCalculator.quote(contractPayment, resultStart, resultEnd,
						resultValue);

				for (ITimedResult<Double> quoteResult : quoteResults) {
					try {
						for (Entry<String, ITimedVariable<?>> entry : quoteResult.getContext().entrySet())
							salaryBuilder.addData(entry.getKey(), entry.getValue());
					} catch (ExpressionExceptionWrapper e) {
						if (e.getCause() instanceof UndefinedVariablesException)
							onInvalidData(((UndefinedVariablesException) e.getCause()).getVariableNames());
					}
					quote += quoteResult.getValue();
				}

				try {
					Double tax ;
					try {
						tax = taxCalculator.tax(contractPayment, resultStart, resultEnd, issueDate, resultValue, resultsDouble);
					} catch ( YesExtraException e ) {
						tax = e.getTax();
						resultValue = e.getTax();
					}
					String description = null;
					try {
						description = expressionContext.evalTemplate(contractPayment.getDescription(), resultStart,
								resultEnd);
						// TODO ¿ Append period to description ?
						// description = getDescriptionPeriod(description,
						// paymentStart, paymentEnd, amountStart,
						// amountEnd);
					} catch (CompileException e) {
						onCompileError(contractPayment, DESCRIPTION_SYNTAX_ERROR);
					} catch (UndefinedVariablesException e) {
						onCheckError(contractPayment, String.format(DESCRIPTION_UNDEF_ERROR, e.getVariableNames()[0]));
					} catch (Exception e) {
						onCheckError(contractPayment, DESCRIPTION_UNKNOWN_ERROR);
					} 
					salaryBuilder.addPayment(resultValue, quote, tax, description, resultStart, resultEnd,
							contractPayment, result.getContext());

				} catch (NotNowException e) {
					salaryBuilder.addZeroPayment(quote, 0.00, resultStart, resultEnd, contractPayment,
							result.getContext());

				} 
				
				saveResult(contractPayment, expressionContext, resultStart, resultEnd, resultValue);
				
			}

			if (AonStringUtils.isNotBlank(name)) 
				for (Period unSetPeriod : Period.sub(new Period(start, end), expressionContext.getPeriods(name)))
					//for ( Period p: Period.sub(unSetPeriod, leavePeriods))
					for ( Period p : Period.intersect(Collections.singleton(unSetPeriod), expressionContext.getPeriods(WORKED_DAYS.getName())) )						
						addResult(expressionContext, name, p.getStart(), p.getEnd(), 0.00);
			
			

			// quoteCalculator.quote(contractPayment, paymentStart,
			// paymentEnd, total);

		} catch (HideException e) {
			if (AonStringUtils.isNotBlank(e.getMessage()))
				onCheckError(e.getMessage());
			addResult(expressionContext, name, start, end, 0.00);
		} catch (RemoveException e) {
			onRemove(contractPayment);
			addResult(expressionContext, name, start, end, 0.00);
		} catch (InvalidVariables e) {
			onInvalidData(contractPayment, e.getMessage(), e.getVariables());
		} catch (InterruptedException e) {
			throw e; // Not catch
		} catch (CheckException e) {
			onCheckError(contractPayment, e.getMessage());
		} catch (RemoveVariableError e) {
			onUndefinedData(contractPayment, e.getVariable());
		} catch (UndefinedTotalPaymentException e) {
			throw e;
		} catch (UndefinedVariablesException e) {
			UndefinedContextVariablesException.throvv(e);
			// onUndefinedData(contractPayment, e.getMessage(),
			// e.getVariableNames());
		} catch (CompileException e) {
			onCompileError(contractPayment, getSyntaxExpressionErrorMessage(contractPayment));
			addResult(expressionContext, name, start, end, 0.00);
		} catch ( ConversionException e ) {
			throw new UndefinedVariablesException();
		} catch ( NumberFormatException e ) {
			throw new UndefinedVariablesException();
		} 

	}

	private void saveResult(IContractPayment contractPayment, ExpressionContext expressionContext, Date resultStart,
			Date resultEnd, double resultValue) {
		if ( contractPayment == null )
			return;
		if ( contractPayment.getType()== null )
			return;
		
		contractPayment.getType().accept( new PaymentTypeVisitor() {
			
			@Override
			public void visitStructuralHours(PaymentType paymentType) {
			}
			
			@Override
			public void visitSalaryInKind(PaymentType paymentType) {
				addResult(expressionContext, IN_KIND.getName(), resultStart, resultEnd, resultValue);
			}
			
			@Override
			public void visitOther(PaymentType paymentType) {
			}
			
			@Override
			public void visitNonStructuralHours(PaymentType paymentType) {
			}
		});
	}

	// -------------------------------------------------------------- Protected

	protected static boolean allAgreementConstants(Map<String, ITimedVariable<?>> context) {
		return context.values().stream()
		.allMatch(var -> {
			if (!( var instanceof IExpressionVariable<?> ) ) 
				return false;
			
			IExpression expression = ((IExpressionVariable<?>)var).getExpression();
			return expression.getScope() == ExpressionScope.AGREEMENT;
		}
		);
	}

	// ---------------------------------------------------------------- Private
	
	private static boolean isPartialMonth(ITimedResult<?> result) {
		int startDay = AonDateUtils.getDay(result.getPeriod().getStart());
		if ( startDay > 1 ) 
			return true;
		
		int endDay = AonDateUtils.getDay(result.getPeriod().getEnd());
		int lastDay = AonDateUtils.getDay(AonDateUtils.getMonthLastDay(result.getPeriod().getEnd()));
		return endDay < lastDay;
	}

	private static boolean isPartial(ExpressionContext expressionContext, Period p) {
		try {
			for ( ITimedResult<Double> factor : expressionContext.eval(ContextVariable.PARTIAL_FACTOR.toString(), p.getStart(), p.getEnd(), Double.class) ) {
				if ( factor.getValue() < 1.00 ) 
					return true;
			}
			return false;
			
		} catch ( Exception e ) {
			return false;
		}
	}

	private static boolean isExtra(ExpressionContext expressionContext) {
		try {
			return 
			expressionContext.getVariables(ContextVariable.EXTRA_PAY)
			.stream().map(v -> (Boolean) v.getValue(v.getPeriod()))
			.findAny().orElse(Boolean.FALSE)
			;
		} catch ( Exception e ) {
			return expressionContext.getVariables(ContextVariable.SELF)
					.stream()
					.map(v -> (IContractSalaryCalculatorContext) v.getValue(v.getPeriod()))
					.map(c -> c.getSalaryType() == SalaryType.EXTRA )
					.findAny().orElse(Boolean.FALSE)
					;
		}
	}

	private static void addResult(ExpressionContext expressionContext, String name, Date resultStart, Date resultEnd,
			Double resultValue) {
		if (StringUtils.isEmpty(name))
			return;
		long resultDays = getDays(resultStart, resultEnd);
		
		Date valueStart = resultStart;
		List<ITimedVariable<Number>> prevs = getVariables(expressionContext, name, resultStart, resultEnd);
		for (ITimedVariable<Number> prev : prevs) {
			Date prevStart = prev.getPeriod().getStart();
			Date prevEnd = prev.getPeriod().getEnd();
			
			long prevDays = getDays(prevStart, prevEnd);
			
			try {
				Number prevValue = prev.getValue(prev.getPeriod());
				if (valueStart.compareTo(prevStart) < 0) {
					Date valueEnd = prev(prevStart);
					long valueDays = getDays(valueStart, valueEnd);
					expressionContext.setVariable(name, resultValue / resultDays * valueDays, valueStart,valueEnd);
				} 
				valueStart = Period.max(valueStart, prevStart);
				Date valueEnd = Period.min(prevEnd, resultEnd);
				long valueDays = getDays(valueStart, valueEnd);
				if (prev instanceof IExpressionVariable<?>) {
					expressionContext.setVariable(name, resultValue / resultDays * valueDays, valueStart, valueEnd);
				}
				else {
					expressionContext.setVariable(name, ( resultValue  / resultDays + prevValue.doubleValue() / prevDays )  * valueDays, valueStart, valueEnd);
				}
				
				valueStart = next(valueEnd);
				if ( Period.compare(prevEnd, valueEnd) > 0 ) {					
					valueDays = getDays(valueStart, prevEnd);
					expressionContext.setVariable(name, prevValue.doubleValue() / prevDays * valueDays, valueStart,prevEnd);
				}
			} catch (Exception e) {
				System.err.println(String.format("ERROR [%s]: %s", name, e.getLocalizedMessage()));
			}
		}
		if (valueStart.compareTo(resultEnd) <= 0) {
			expressionContext.setVariable(name, resultValue / resultDays * getDays(valueStart, resultEnd), valueStart, resultEnd);
		}
	}

	private static <T> List<ITimedVariable<T>> getVariables(ExpressionContext expressionContext, String name,
			Date startDate, Date endDate) {
		List<ITimedVariable<Object>> values = expressionContext.getVariables(name);
		if (values == null)
			return Collections.emptyList();

		Period period = new Period(startDate, endDate);
		List<ITimedVariable<T>> ret = new ArrayList<ITimedVariable<T>>();

		for (ITimedVariable<?> var : values) {
			Period intersect = var.getPeriod().intersect( period );
			if (intersect == null)
				continue;
			
			ret.add((ITimedVariable<T>) var);
		}

		return ret;

	}

	private static long getDays(Date valueStart, Date valueEnd) {
		return new Period(valueStart, valueEnd ).daysStream().count();
	}

	private static void copyResults(ExpressionContext expressionContext, ContextVariable dest, ContextVariable ...srcs ) {
		for (ContextVariable src : srcs)
			copyResults(expressionContext, src.getName(), dest.getName());
		
	}

	private static void copyResults(ExpressionContext expressionContext, ContextVariable src, ContextVariable dest) {
		copyResults(expressionContext, src.getName(), dest.getName());
	}

	private static void copyResults(ExpressionContext expressionContext, INamedContractPayment namedContractPayment) {
		copyResults(expressionContext, namedContractPayment.getName(), namedContractPayment.getSurName());
	}
	private static void copyResults(ExpressionContext expressionContext, String name, String surName) {
		if (StringUtils.isBlank(surName))
			return;
		if (StringUtils.equals(name, surName))
			return;
		if (StringUtils.isBlank(name))
			name = ALL;
		for (ITimedVariable<Object> var : expressionContext.getVariables(name)) {
			expressionContext.putVariable(surName, var);
		}
	}

	private static void addVars(ExpressionContext expressionContext, ContextVariable dest, ContextVariable ...adds) {
		for (ContextVariable add : adds) {
			addVar(expressionContext, dest.getName(), add.getName());
		}
	}
	private static void addVar(ExpressionContext expressionContext, String dest, String add) {
		List<ITimedVariable<Number>> adds = expressionContext.getVariables(add);
		adds.stream().filter(v -> v.getValue(v.getPeriod()) != null )
		.forEach( v -> addResult(expressionContext, dest, v.getPeriod().getStart(), v.getPeriod().getEnd(), v.getValue(v.getPeriod()).doubleValue()));
	}

	public static final String DAY_FOMAT = "%s ( %te )";
	public static final String DAY_PERIOD_FOMAT = "%s ( %te - %te )";
	public static final String COMPLETE_PERIOD_FOMAT = "%s ( %te/%<tm - %te/%<tm )";

	private Double resolveDeduction(ExpressionContext ctx, IContractDeduction d, Date start, Date end)
			throws ExpressionException {

		List<ITimedResult<Double>> results = ctx.addExpression(d, start, end, Double.class);

		Double total = 0.00;

		for (ITimedResult<Double> result : results) {
			Double value = result.getValue();
			Period period = result.getPeriod();

			if (value == null || value == 0) {
				salaryBuilder.addZeroDeduction(period.getStart(), period.getEnd(), d, result.getContext());
				continue;
			}

			String description = null;
			try {
				description = ctx.evalTemplate(d.getDescription(), period.getStart(), period.getEnd());
			} catch (CompileException e) {
				onCompileError(d, DESCRIPTION_SYNTAX_ERROR);
			} catch (UndefinedVariablesException e) {
				onCheckError(d, String.format(DESCRIPTION_UNDEF_ERROR, e.getVariableNames()[0]));
			} catch (Exception e) {
				onCheckError(d, DESCRIPTION_UNKNOWN_ERROR);
			}
			salaryBuilder.addDeduction(value, description, period.getStart(), period.getEnd(), d, result.getContext());
			total += value;
		}

		results = null;

		return total;
	}

	private Double resolveEmbargo(ExpressionContext ctx, IContractEmbargo embargo, Date start, Date end)
			throws ExpressionException {

		List<ITimedResult<Double>> results = ctx.eval(embargo.getExpression(), start, end, Double.class);

		Double total = 0.00;

		for (ITimedResult<Double> result : results) {
			Double value = result.getValue();

			if (value == null || value == 0) {
				salaryBuilder.addZeroEmbargo(embargo.getId(), embargo, result.getContext());
				continue;
			}

			String description = null;
			try {
				Period period = result.getPeriod();
				description = ctx.evalTemplate(embargo.getDescription(), period.getStart(), period.getEnd());
			} catch (CompileException e) {
				onCompileError(embargo, DESCRIPTION_SYNTAX_ERROR);
			} catch (UndefinedVariablesException e) {
				onCheckError(embargo, String.format(DESCRIPTION_UNDEF_ERROR, e.getVariableNames()[0]));
			} catch (Exception e) {
				onCheckError(embargo, DESCRIPTION_UNKNOWN_ERROR);
			}
			salaryBuilder.addEmbargo(embargo.getId(), value, description, embargo, result.getContext());
			total += value;
		}

		results = null;

		return total;
	}

	protected void fillData(IContractSalaryCalculatorContext ctx) throws SalaryException {
		fillData(ctx, 
				new String[] { 
				TC2.getName(), 
				MONTH_DAYS.getName(), 
				QUOTE_DAYS.getName(), 
				CGC_BASE.getName(), 
				CGP_BASE.getName(),
				WORKED_DAYS.getName(), 
				CGC_BASE_ENTERPRISE.getName(), 
				CGP_BASE_ENTERPRISE.getName(),
				DIRECT_BASE.getName(), 
				MATERNITY_BASE.getName(), 
				ADDITIONAL_BASE.getName(), 
				STRUCTURAL_OVERTIME_BASE.getName(),
				NON_STRUCTURAL_OVERTIME_BASE.getName(), 
				SALARY_HOURS.getName(),
				//WORKED_HOURS.getName(),
				ADDITIONAL_HOURS.getName(),

				MONDAY_HOURS.getName(),
				TUESDAY_HOURS.getName(),
				WEDNESDAY_HOURS.getName(),
				THURSDAY_HOURS.getName(),
				FRIDAY_HOURS.getName(),
				SATURDAY_HOURS.getName(),
				SUNDAY_HOURS.getName(),
				
				ContextVariable.SLD_C737.getName(),
				ContextVariable.SLD_H06.getName(),
				ContextVariable.SLD_H03.getName(),
				ContextVariable.SLD_H04.getName(),

				PREST_IT
				});
		fillData(ctx, ERE_BASES);
		
	}
	
	protected void fillData(IContractSalaryCalculatorContext ctx, ContextVariable  vars []) throws SalaryException {
		String names [] = Arrays.stream(vars).map(v->v.getName()).toArray(String[]::new);
		fillData(ctx, names);
	}
	
	protected void fillData(IContractSalaryCalculatorContext ctx, String  names []) throws SalaryException {
		ExpressionContext expressionContext = ctx.getExpressionContext();
		for (String name : names) {
			
			try {
				for (ITimedVariable<?> data :expressionContext.eval(name, ctx.getStartDate(), ctx.getEndDate())){
					try {
						data = ((ITimedResult<?>) data).getContext().getOrDefault(name, data);
					} catch (Throwable t) {
					}
					
					try {
						salaryBuilder.addData(name, data);
					} catch (Throwable t) {
					}
				}
			} catch (Throwable t) {
			}
		}
	}

	protected void fillTimeUnits(IContractSalaryCalculatorContext ctx) {
		try {
			if ( ctx.getExpressionContext().isDef(ContextVariable.TOTAL_DAYS)) {
				salaryBuilder.setTimeUnits(ctx.getExpressionContext().getVariables(ContextVariable.TOTAL_DAYS).stream()
					.map(var -> (Number) var.getValue(var.getPeriod()))
					.collect(Collectors.summingDouble(number -> number.doubleValue())).intValue());
				return;
			}
		
		} catch ( Exception e ) {
		}
		try {
			salaryBuilder.setTimeUnits(ctx.getExpressionContext().getVariables(ContextVariable.QUOTE_DAYS).stream()
					.map(var -> (Number) var.getValue(var.getPeriod()))
					.collect(Collectors.summingDouble(number -> number.doubleValue())).intValue());
			return;
		} catch (Exception e) {
		}
		salaryBuilder
		.setTimeUnits((int) (AonDateUtils.getDaysBetweenDates(ctx.getStartDate(), ctx.getEndDate()) + 1));
	}

	protected void onInvalidData(String... variableNames) {
		if (listener != null) {
			for (int i = 0; i < variableNames.length; i++) {
				listener.onInvalidData(variableNames[i], null);
			}
		}
	}

	protected void onCheckError(String message) {
		if (listener != null) {
			listener.onCheckError(message);
		}
	}

	protected void onCheckError(IContractBonus bonus, String message) {
		if (listener != null) {
			listener.onCheckError(bonus, message);
		}
	}

	protected void onInvalidData(IContractBonus bonus, String message, String... variableNames) {
		if (listener != null) {
			for (int i = 0; i < variableNames.length; i++) {
				listener.onInvalidData(bonus, variableNames[i], message);
			}
		}
	}

	protected void onCheckError(IContractDeduction dedcution, String message) {
		if (listener != null) {
			listener.onCheckError(dedcution, message);
		}
	}

	protected void onCompileError(IContractDeduction deduction, String message) {
		if (listener != null) {
			listener.onCompileError(deduction, message);
		}
	}

	protected void onInvalidData(IContractDeduction deduction, String message, String... variableNames) {
		if (listener != null) {
			for (int i = 0; i < variableNames.length; i++) {
				listener.onInvalidData(deduction, variableNames[i], message);
			}
		}
	}

	protected void onCompileError(IContractBonus bonus, String message) {
		if (listener != null) {
			listener.onCompileError(bonus, message);
		}
	}

	protected void onUndefinedData(IContractDeduction deduction, RemovedExpressionVariable<?> var) {
		if (listener != null) {
			listener.onUndefinedData(deduction, var);
		}
	}

	protected void onUndefinedData(IContractDeduction deduction, String message, String... variableNames) {
		if (listener != null) {
			for (int i = 0; i < variableNames.length; i++) {
				listener.onUndefinedData(deduction, variableNames[i], message);
			}
		}
	}

	protected void onRemove(IContractBonus bonus) {
		if (listener != null) {
			listener.onRemove(bonus);
		}
	}

	protected void onRemove(IContractPayment payment) {
		if (listener != null) {
			listener.onRemove(payment);
		}
	}

	protected void onCheckError(IContractPayment payment, String message) {
		if (listener != null) {
			listener.onCheckError(payment, message);
		}
	}

	protected void onCompileError(IContractPayment payment, String message) {
		if (listener != null) {
			listener.onCompileError(payment, message);
		}
	}

	protected void onInvalidData(IContractPayment payment, String message, String... variableNames) {
		if (listener != null) {
			for (int i = 0; i < variableNames.length; i++) {
				listener.onInvalidData(payment, variableNames[i], message);
			}
		}
	}

	protected void onUndefinedData(IContractPayment payment, RemovedExpressionVariable<?> var) {
		if (listener != null) {
			listener.onUndefinedData(payment, var);
		}
	}

	protected void onUndefinedData(IContractPayment payment, String message, String... variableNames) {
		if (listener != null) {
			for (int i = 0; i < variableNames.length; i++) {
				listener.onUndefinedData(payment, variableNames[i], message);
			}
		}
	}
	

	// ------------------------------------------------------------------------

	private static Date prev(Date date) {
		return addDays2Date(date, -1);
	}

	private static Date next(Date date) {
		return addDays2Date(date, 1);
	}

	private static Date addDays2Date(Date date, int days) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, days);
		return calendar.getTime();
	}

	static List<ITimedResult<Double>> shareResults(ITimedResult<Double> result, List<Period> its) {
		List<ITimedResult<Double>> fixed = new ArrayList<ITimedResult<Double>>();

		List<Period> noIts = Period.sub(result.getPeriod(), its);

		long noItDays = noIts.stream()
				.collect(Collectors.summingLong(p -> AonDateUtils.getDaysBetweenDates(p.getStart(), p.getEnd())));

		for (Period p : noIts) {
			fixed.add(new ITimedResult<Double>() {
				@Override
				public Period getPeriod() {
					return p;
				}

				@Override
				public Double getValue() {
					return getValue(p);
				}

				@Override
				public Double getValue(Period period) {
					return result.getValue() * AonDateUtils.getDaysBetweenDates(period.getStart(), period.getEnd())
							/ noItDays;
				}

				@Override
				public Map<String, ITimedVariable<?>> getContext() {
					return result.getContext();
				}
			});
		}
		;

		return fixed;
	}
	
	
	
	private static String getSyntaxExpressionErrorMessage(IExpression expression) {
		String str = expression.getExpression();
		Pattern pattern = Pattern.compile("(/\\*(user|read-only)\\*/)((?:[^/]|(?:/[^\\*]))*)(/\\*\\*/)");
		Matcher matcher = pattern.matcher(str);
		
		return EXPRESSION_SYNTAX_ERROR + " '" + (matcher.find() ? matcher.group(3) : str) +"'";
	}
	

}

