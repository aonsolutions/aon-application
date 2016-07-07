package com.esferalia.aon.payroll.calculator;

import static com.esferalia.aon.payroll.enumeration.ContextVariable.ALL;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGC_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGC_BASE_ENTERPRISE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGP_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGP_BASE_ENTERPRISE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.EMBARGO_PAID;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.EMPLOYEE_QUOTA;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.ENTERPRISE_QUOTA;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.ERE_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.IRPF_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MATERNITY_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.NON_STRUCTURAL_OVERTIME_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.PREST_IT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.QUOTE_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.STRUCTURAL_OVERTIME_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.TC2;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.TOTAL_LIQUID;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.TOTAL_PAYMENT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.WORKED_HOURS;

import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.mvel2.CompileException;

import com.code.aon.AonVersion;
import com.code.aon.common.AonException;
import com.esferalia.aon.payroll.calculator.TaxCalculator.NotNowException;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.ISalaryBuilder;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.calculator.ISalaryCalculator;
import com.esferalia.aon.salary.calculator.ISalaryCalculatorContext;
import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.expression.CheckException;
import com.esferalia.aon.salary.expression.ExpressionContext;
import com.esferalia.aon.salary.expression.ExpressionContext.ExpressionExceptionWrapper;
import com.esferalia.aon.salary.expression.ExpressionContext.RemoveVariableError;
import com.esferalia.aon.salary.expression.ExpressionContext.RemovedExpressionVariable;
import com.esferalia.aon.salary.expression.ExpressionException;
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

public class ContractSalaryCalculator<T extends ISalary> implements ISalaryCalculator<T> {

	private static final String BASE_CGC_MIN_MSG = "%s %.2f ha sido ampliada al m\u00ednimo obligatorio %.2f";
	private static final String BASE_CGC_MAX_MSG = "%s %.2f ha sido limitada al m\u00e1ximo permitido %.2f";
	private static final String BASE_CGP_MIN_MSG = "%s %.2f ha sido ampliada al m\u00ednimo obligatorio %.2f";
	private static final String BASE_CGP_MAX_MSG = "%s %.2f ha sido limitada al m\u00e1ximo permitido %.2f";
	private static final String DESCRIPTION_UNDEF_ERROR = "Error en la descripic\u00F3n, variable '%s' desconocida.";
	private static final String DESCRIPTION_UNKNOWN_ERROR = "Error desconocido en la descripic\u00F3n.";
	private static final String DESCRIPTION_SYNTAX_ERROR = "Error sint\u00E1ctico en la descripic\u00F3n.";
	private static final String BUILDER_VARIABLE = "BUILDER";


	public interface IListener {

		public void onCheckError(String message);

		public void onInvalidData(String variableName, String message);

		public void onCompileError(String variableName, String message);

		public void onRemove(IContractBonus bonus);

		public void onRemove(IContractDeduction payment);

		public void onRemove(IContractPayment payment);

		public void onCheckError(IContractPayment payment, String message);

		public void onCompileError(IContractPayment payment, String message);

		public void onUndefinedData(IContractPayment payment,
				RemovedExpressionVariable<?> var);

		public void onInvalidData(IContractPayment payment,
				String variableName, String message);

		public void onUndefinedData(IContractPayment payment,
				String variableName, String message);

		public void onCheckError(IContractDeduction deduction, String message);

		public void onCompileError(IContractDeduction deduction, String message);

		public void onUndefinedData(IContractDeduction deduction,
				RemovedExpressionVariable<?> var);

		public void onInvalidData(IContractDeduction deduction,
				String variableName, String message);

		public void onUndefinedData(IContractDeduction deduction,
				String variableName, String message);

		public void onCheckError(IContractBonus bonus, String message);

		public void onCompileError(IContractBonus bonus, String message);

		public void onInvalidData(IContractBonus bonus, String variableName,
				String message);

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
		public void onUndefinedData(IContractPayment payment,
				RemovedExpressionVariable<?> var) {
		}

		@Override
		public void onInvalidData(IContractPayment payment,
				String variableName, String message) {
		}

		@Override
		public void onUndefinedData(IContractPayment payment,
				String variableName, String message) {
		}

		@Override
		public void onCheckError(IContractDeduction deduction, String message) {
		}

		@Override
		public void onCompileError(IContractDeduction deduction, String message) {
		}

		@Override
		public void onUndefinedData(IContractDeduction deduction,
				RemovedExpressionVariable<?> var) {
		}

		@Override
		public void onInvalidData(IContractDeduction deduction,
				String variableName, String message) {
		}

		@Override
		public void onUndefinedData(IContractDeduction deduction,
				String variableName, String message) {
		}

		@Override
		public void onCheckError(IContractBonus bonus, String message) {
		}

		@Override
		public void onCompileError(IContractBonus bonus, String message) {
		}

		@Override
		public void onInvalidData(IContractBonus bonus, String variableName,
				String message) {
		}

	}
	
	

	private static class UndefPayment extends SimpleContractPayment {

		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

		private UndefinedVariablesException exception;

		public UndefPayment(IContractPayment contractPayment,
				UndefinedVariablesException exception) {
			super(contractPayment);
			this.exception = exception;
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

		boolean dependsOn(String var){
			if ( var == null )
				return false;
			for ( String v : exception.getVariableNames() )
				if ( var.equals(v)) 
					return true;
			return false;
		}

		boolean willBeDefined(Collection<String> willbeDefined) {

			for (String var : exception.getVariableNames())
				if (!willbeDefined.contains(var))
					return false;

			return true;
		}

		void onUndefinedData(ContractSalaryCalculator<?> calculator) {
			calculator.onUndefinedData(this, exception.getMessage(),
					exception.getVariableNames());
		}
		

	}
	

	private IListener listener;
	private ISalaryBuilder<T> salaryBuilder;
	
	public ContractSalaryCalculator() {
	}
	
	public ContractSalaryCalculator(ISalaryBuilder<T> salaryBuilder) {
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
	public T calculate(ISalaryCalculatorContext ctx)
			throws SalaryException {
		IContractSalaryCalculatorContext contractSalaryCalculatorContext = (IContractSalaryCalculatorContext) ctx;

		Date start = ctx.getStartDate();
		Date end = ctx.getEndDate();

		ExpressionContext expressionContext = ctx.getExpressionContext();

		salaryBuilder.createNewSalary();
		salaryBuilder.setContract(contractSalaryCalculatorContext
				.getSalaryProxy());

		expressionContext.setVariable(BUILDER_VARIABLE, salaryBuilder, start,
				end);

		fillEnterpriseData(contractSalaryCalculatorContext);
		fillEmployeeData(contractSalaryCalculatorContext);
		fillSalaryData(contractSalaryCalculatorContext);
		Double totalPayment = fillPayments(contractSalaryCalculatorContext);
		Double totalDeduction = fillDeductions(contractSalaryCalculatorContext);


		expressionContext.setVariable(TOTAL_LIQUID, totalPayment
				- totalDeduction, start, end);
		Double totalEmbargos = fillEmbargos(contractSalaryCalculatorContext);

		salaryBuilder.setTotalDeduction(totalDeduction + totalEmbargos);

		Double totalCost = fillCosts(contractSalaryCalculatorContext);
		expressionContext.setVariable(ENTERPRISE_QUOTA, totalCost, start, end);
		Double totalBonus = fillBonus(contractSalaryCalculatorContext);

		Double totalEnterprise = totalCost - totalBonus;
		salaryBuilder.setTotalEnterprise(totalEnterprise);

		salaryBuilder.setTotalLiquid(totalPayment - totalDeduction
				- totalEmbargos);

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
	}

	protected void fillEmployeeData(IContractSalaryCalculatorContext ctx) {
		salaryBuilder.setEmployeeName(ctx.getEmployeeName());
		salaryBuilder.setEmployeeDocument(ctx.getEmployeeDocument());
		salaryBuilder.setRegistration(ctx.getRegistration());
		salaryBuilder.setSocialSecurityNumber(ctx.getSocialSecurityNumber());
		salaryBuilder.setCategory(ctx.getCategory());
		salaryBuilder.setQuoteGroup(ctx.getQuoteGroup());
		salaryBuilder.setSeniorityDate(ctx.getSeniorityDate());
	}

	protected void fillSalaryData(IContractSalaryCalculatorContext ctx) {
		salaryBuilder.setIssueDate(ctx.getIssueDate());
		salaryBuilder.setStartDate(ctx.getStartDate());
		salaryBuilder.setEndDate(ctx.getEndDate());
		salaryBuilder.setChargeDate(ctx.getChargeDate());
		salaryBuilder.setType(ctx.getSalaryType());

	}

	protected Double fillPayments(IContractSalaryCalculatorContext ctx)
			throws SalaryException {
		try {

			ExpressionContext expressionContext = ctx.getExpressionContext();

			Date start = ctx.getStartDate();
			Date end = ctx.getEndDate();

			QuoteCalculator quoteCalculator = getQuoteCalculator(ctx);

			TaxCalculator taxCalculator = getTaxCalculator(ctx);

			Date issueDate = ctx.getIssueDate();

			Set<String> paymentsVars = new HashSet<String>();
			LinkedList<UndefPayment> undefPayments = new LinkedList<UndefPayment>();
			Collection<IContractPayment> contractPayments = ctx
					.getContractPayments();

			LinkedList<UndefPayment> undefTotalPayments = new LinkedList<UndefPayment>();

			HashSet<String> alreadyDefined = new HashSet<String>();
			for (IContractPayment contractPayment : contractPayments) {
				try {
					resolvePayment(contractPayment, start, end, issueDate,
							expressionContext, taxCalculator, quoteCalculator);
					alreadyDefined.add(contractPayment.getName());
				} catch (UndefinedTotalPaymentException e) {
					undefTotalPayments
							.add(new UndefPayment(contractPayment, e));
				} catch (UndefinedContextVariablesException e) {
					onUndefinedData(contractPayment, e.getMessage(),
							e.getVariableNames());					
					addResult(expressionContext, contractPayment.getName(), start, end, 0.00);
				} catch (UndefinedVariablesException e) {
					UndefPayment undefPayment = new UndefPayment(
							contractPayment, e);
					if (!undefPayment.isSelfUndefined()) {
						undefPayments.add(undefPayment);
					} else {
						undefPayment.onUndefinedData(this);
					}
				}
				paymentsVars.add(contractPayment.getName());
			}
			
			Collections.sort(undefPayments,(p1,p2)-> {
				if ( p1.dependsOn(p2.getName()))
					return 1;
				if ( p2.dependsOn(p1.getName()))
					return -1;

				if ( p1.getName() == p2.getName())
					return 0;
				if ( p1.getName() == null )
					return 1;
				if ( p2.getName() == null )
					return -1;
				return p1.getName().compareTo(p2.getName());
			}
			);
			
			HashSet<String> willBeDefined = new HashSet<String>();

			for (ListIterator<UndefPayment> listIterator = undefPayments
					.listIterator(); listIterator.hasNext();) {
				UndefPayment undefPayment = listIterator.next();
				if (undefPayment.willBeDefined(paymentsVars)) {
					willBeDefined.addAll(paymentsVars); // ??? Why?
					willBeDefined.add(undefPayment.getName());
					continue;
				}
				// clean undefined ...
				listIterator.remove();
				undefPayment.onUndefinedData(this);
				// will not be calculated, but like it's a payment save it like zero.
				alreadyDefined.add(undefPayment.getName());
				addResult(expressionContext, undefPayment.getName(), start, end, 0.00);
			}
			// Many payments can share same variable...
			paymentsVars.addAll(willBeDefined);
			paymentsVars.addAll(alreadyDefined);

			int undefined = 0;
			while (undefPayments.size() > 0) {
				UndefPayment undefPayment = undefPayments.pop();
				try {
					resolvePayment(undefPayment, start, end, issueDate,
							expressionContext, taxCalculator, quoteCalculator);
					paymentsVars.add(undefPayment.getName());
				} catch (UndefinedTotalPaymentException e) {
					undefTotalPayments.add(undefPayment);
				} catch (UndefinedContextVariablesException e) {
					paymentsVars.remove(undefPayment.getName());
					addResult(expressionContext, undefPayment.getName(), start, end, 0.00);
				} catch (UndefinedVariablesException e) {

					if (undefPayment.willBeDefined(paymentsVars)) {
						undefPayments.add(undefPayment);
						
						if (++undefined >= undefPayments.size())
							break; // we've already eval all undef payments
					} else {
						undefPayment.onUndefinedData(this);
						paymentsVars.remove(undefPayment.getName());
					}
				} 
			}

			for (UndefPayment undefPayment : undefPayments)
				undefPayment.onUndefinedData(this);

			double totalPayment = taxCalculator.getTotalPayment();
			expressionContext.setVariable(TOTAL_PAYMENT, totalPayment, start,
					end);

			Date irpfDate = ctx.getIrpfDate();
			expressionContext.setVariable(IRPF_BASE,
					taxCalculator.getIrpfBase(), irpfDate, irpfDate);


			for (UndefPayment undefTotalPayment : undefTotalPayments) {
				try {
					resolvePayment(undefTotalPayment, start, end, issueDate,
							expressionContext, taxCalculator, quoteCalculator);
				} catch (UndefinedVariablesException e) {
					onUndefinedData(undefTotalPayment, e.getMessage(),
							e.getVariableNames());
				}
			}

			totalPayment = taxCalculator.getTotalPayment();
			expressionContext.setVariable(TOTAL_PAYMENT, totalPayment, start,
					end);

			salaryBuilder.setTotalPayment(totalPayment);

			salaryBuilder.setRemuneration(taxCalculator.getRenumeration());

			salaryBuilder.setItBase(quoteCalculator.getItBase());

			salaryBuilder.setIrpfBase(taxCalculator.getIrpfBase());
			salaryBuilder.setMoneyIrpfBase(taxCalculator.getMoneyIrpfBase());
			salaryBuilder.setInkindIrpfBase(taxCalculator.getInKindIrpfBase());
			expressionContext.setVariable(IRPF_BASE,
					taxCalculator.getIrpfBase(), irpfDate, irpfDate);

			Double rawCgcbase = quoteCalculator.getRawCgcBase();
			salaryBuilder.setRawCgcBase(rawCgcbase);

			Double cgcBase = rawCgcbase;
			try {
				cgcBase = quoteCalculator.getCgcBase();
			} catch (UndefinedVariablesException e) {
				onInvalidData(e.getVariableNames());
			}
			
			if (AonNumberUtils.compare(rawCgcbase, cgcBase) > 0 ) //rawCgcbase > cgcBase
				onCheckError(String.format(BASE_CGC_MAX_MSG, CGC_BASE.getDescription(), rawCgcbase, cgcBase));
			else if (AonNumberUtils.compare(rawCgcbase, cgcBase) < 0 ) // rawCgcbase < cgcBase 
				onCheckError(String.format(BASE_CGC_MIN_MSG, CGC_BASE.getDescription(), rawCgcbase, cgcBase));
				
			
			Double ereBase = quoteCalculator.getEreBase();
			if ( ereBase != null ) 
				cgcBase += ereBase;
			Double maternityBase = quoteCalculator.getMaternityBase();
			if ( maternityBase != null ) 
				cgcBase += maternityBase;
			Double directPayBase = quoteCalculator.getDirectPayBase();
			if ( directPayBase != null ) 
				cgcBase += directPayBase;
			salaryBuilder.setCgcBase(cgcBase);

			if (cgcBase != null )
				expressionContext.setVariable(CGC_BASE_ENTERPRISE,
						cgcBase , start, end);

			Double cgpBase = quoteCalculator.getRawCgpBase();
			try {
				cgpBase = quoteCalculator.getCgpBase();
			} catch (UndefinedVariablesException e) {
				onInvalidData(e.getVariableNames());
			}
			if ( AonNumberUtils.compare(rawCgcbase, cgpBase) > 0 ) // rawCgcbase > cgpBase
				onCheckError(String.format(BASE_CGP_MAX_MSG, CGP_BASE.getDescription(), rawCgcbase, cgpBase));
			else if (AonNumberUtils.compare(rawCgcbase, cgpBase)< 0 ) // rawCgcbase < cgpBase 
				onCheckError(String.format(BASE_CGP_MIN_MSG, CGP_BASE.getDescription(), rawCgcbase, cgpBase));

			if ( ereBase != null ) 
				cgpBase += ereBase;
			if ( maternityBase != null ) 
				cgpBase += maternityBase;
			if ( directPayBase != null ) 
				cgpBase += directPayBase;
			salaryBuilder.setCgpBase(cgpBase);
			if (cgpBase != null )
				expressionContext.setVariable(CGP_BASE_ENTERPRISE,
						cgpBase , start, end);

			Double nonStructuralBase = quoteCalculator.getNonStructuralBase();
			salaryBuilder.setNonHExtraBase(nonStructuralBase);
			if (nonStructuralBase != null)
				expressionContext.setVariable(NON_STRUCTURAL_OVERTIME_BASE,
						nonStructuralBase, start, end);

			Double structuralBase = quoteCalculator.getStructuralBase();
			salaryBuilder.setHExtraBase(structuralBase);
			if (structuralBase != null)
				expressionContext.setVariable(STRUCTURAL_OVERTIME_BASE,
						structuralBase, start, end);

			salaryBuilder.setProExtBase(quoteCalculator.getProExtBase());

			return taxCalculator.getTotalPayment();
		} catch (SalaryExpressionException e) {
			throw e.getSalaryException();
		} catch (ExpressionException e) {
			throw new SalaryException(e.getMessage(), e);
		} catch (AonException e) {
			throw new SalaryException(e.getMessage(), e);
		} catch (RuntimeException e) {
			e.printStackTrace();
			throw e;
			// TODO: handle exception
		}
	}

	protected QuoteCalculator getQuoteCalculator(
			IContractSalaryCalculatorContext ctx) {
		return QuoteCalculator.getQuoteCalculator(ctx);
	}

	protected TaxCalculator getTaxCalculator(
			IContractSalaryCalculatorContext ctx) {
		return TaxCalculator.getTaxCalculator(ctx);
	}

	protected Double fillDeductions(IContractSalaryCalculatorContext ctx)
			throws SalaryException {
		try {
			double totalIrpf = 0;
			double totalDeduction = 0;
			double ssContributions = 0;

			Date start = ctx.getStartDate();
			Date end = ctx.getEndDate();

			Collection<IContractDeduction> contractDeductions = ctx
					.getContractDeductions();
			ExpressionContext expressionContext = ctx.getExpressionContext();
			for (IContractDeduction contractDeduction : contractDeductions) {
				DeductionType type = contractDeduction.getType();

				Date deductionStart = null;
				Date deductionEnd = null;

				if (type.isTaxDeduction()) {
					deductionStart = ctx.getIrpfDate();
					deductionEnd = ctx.getIrpfDate();

				} else {
					deductionStart = Period.max(
							contractDeduction.getStartDate(), start);
					deductionEnd = Period.min(contractDeduction.getEndDate(),
							end);
					if (deductionEnd.before(deductionStart)) {
						continue; // TODO : must be done in context ?
					}
				}
				try {

					Double deduction = resolveDeduction(expressionContext,
							contractDeduction, deductionStart, deductionEnd);
					totalDeduction += deduction;

					if (type.isSsDeduction()) {
						ssContributions += deduction;
						expressionContext.setVariable(EMPLOYEE_QUOTA, ssContributions,
								start, end);
					} else if (type.isTaxDeduction()) {
						totalIrpf += deduction;
					}
				} catch (RemoveException e) {
					// TODO: Something ??? It's really necessary...
				} catch (InvalidVariables e) {
					onInvalidData(contractDeduction, e.getMessage(),
							e.getVariables());
				} catch (InterruptedException e) {
					throw e; // Not catch
				} catch (CheckException e) {
					onCheckError(contractDeduction, e.getMessage());
				} catch (RemoveVariableError e) {
					onUndefinedData(contractDeduction, e.getVariable());
				} catch (UndefinedVariablesException e) {
					onUndefinedData(contractDeduction, e.getMessage(),
							e.getVariableNames());
				} catch (CompileException e) {
					e.printStackTrace();
					onCompileError(contractDeduction, e.getMessage());
				}

			}

			salaryBuilder.setTotalIrpf(totalIrpf);
			salaryBuilder.setTotalSS(ssContributions);

			salaryBuilder.setTotalDeduction(totalDeduction);
			return totalDeduction;
		} catch (ExpressionException e) {
			throw new SalaryException(e.getMessage(), e);
		} catch (AonException e) {
			throw new SalaryException(e.getMessage(), e);
		} catch (Exception e) {
			throw new SalaryException(e.getMessage(), e);
		}
	}

	protected Double fillEmbargos(IContractSalaryCalculatorContext ctx)
			throws SalaryException {

		double total = 0.00;

		try {

			Collection<IContractEmbargo> contractEmbargos = ctx
					.getContractEmbargos();

			Date start = ctx.getStartDate();
			Date end = ctx.getEndDate();

			ExpressionContext expressionContext = ctx.getExpressionContext();

			for (IContractEmbargo contractEmbargo : contractEmbargos) {

				Date embargoStart = Period.max(contractEmbargo.getStartDate(),
						start);
				Date embargoEnd = Period.min(contractEmbargo.getEndDate(), end);

				double left = contractEmbargo.getAmount();
				expressionContext.setVariable(EMBARGO_PAID, left, embargoStart,
						embargoEnd);
				try {
					double embargo = resolveEmbargo(expressionContext,
							contractEmbargo, embargoStart, embargoEnd);
					total += embargo;
				} catch (RemoveException e) {
					// TODO: Something ??? It's really necessary...
				} catch (InvalidVariables e) {
					onInvalidData(contractEmbargo, e.getMessage(),
							e.getVariables());
				} catch (CheckException e) {
					onCheckError(contractEmbargo, e.getMessage());
				} catch (RemoveVariableError e) {
					onUndefinedData(contractEmbargo, e.getVariable());
				} catch (UndefinedVariablesException e) {
					onUndefinedData(contractEmbargo, e.getMessage(),
							e.getVariableNames());
				} catch (CompileException e) {
					onCompileError(contractEmbargo, e.getMessage());
				}

			}

		} catch (ExpressionException e) {
			throw new SalaryException(e.getMessage(), e);
		} catch (AonException e) {
			throw new SalaryException(e.getMessage(), e);
		}

		return total;

	}

	protected Double fillCosts(IContractSalaryCalculatorContext ctx)
			throws SalaryException {
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
					List<ITimedResult<Double>> amounts = expressionContext
							.addExpression(contractCost, costStart, costEnd,
									Double.class);
					double cost = 0.00;
					for (ITimedResult<Double> amount : amounts) {
						Double value = amount.getValue();
						if (value != null) {
							String description = null;
							try {
								Period period = amount.getPeriod();
								description = expressionContext.evalTemplate(
										contractCost.getDescription(),
										period.getStart(), period.getEnd());
							} catch (CompileException e) {
								onCompileError(contractCost, DESCRIPTION_SYNTAX_ERROR);
							} catch (UndefinedVariablesException e) {
								onCheckError(contractCost, String.format(DESCRIPTION_UNDEF_ERROR, e.getVariableNames()[0] ));
							} catch (Exception e) {
								onCheckError(contractCost, DESCRIPTION_UNKNOWN_ERROR);
							}
							salaryBuilder.addCost(value, description,
									contractCost, amount.getContext());
							cost += value;
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

	protected Double fillBonus(IContractSalaryCalculatorContext ctx)
			throws SalaryException {
		double total = 0.00; // TODO; mejor null ???
		try {

			Collection<IContractBonus> contractBonuses = ctx.getContractBonus();

			Date start = ctx.getStartDate();
			Date end = ctx.getEndDate();

			ExpressionContext expressionContext = ctx.getExpressionContext();

			for (IContractBonus contractBonus : contractBonuses) {

				Date bonusStart = Period.max(contractBonus.getStartDate(),
						start);
				Date bonusEnd = Period.min(contractBonus.getEndDate(), end);

				if (bonusEnd.before(bonusStart)) {
					continue; // TODO : must be done in context ?
				}

				try {
					List<ITimedResult<Double>> amounts = expressionContext
							.eval(contractBonus.getExpression(), bonusStart,
									bonusEnd, Double.class);

					double bonus = 0.00;
					for (ITimedResult<Double> amount : amounts) {
						Double value = amount.getValue();
						if (value != null) {
							String description = null;
							try {
								Period period = amount.getPeriod();
								description = expressionContext.evalTemplate(
										contractBonus.getDescription(),
										period.getStart(), period.getEnd());
							} catch (CompileException e) {
								onCompileError(contractBonus, DESCRIPTION_SYNTAX_ERROR);
							} catch (UndefinedVariablesException e) {
								onCheckError(contractBonus, String.format(DESCRIPTION_UNDEF_ERROR, e.getVariableNames()[0] ));
							} catch (Exception e) {
								onCheckError(contractBonus, DESCRIPTION_UNKNOWN_ERROR);
							}
							salaryBuilder.addBonus(value, description,
									contractBonus, amount.getContext());
						}
						if ( amount.getValue()  != null )
							bonus += amount.getValue() ;
					}
					total += bonus;

				} catch (InvalidVariables e) {
					onInvalidData(contractBonus, e.getMessage(),
							e.getVariables());
				} catch (CheckException e) {
					onCheckError(contractBonus, e.getMessage());
				}catch (RemoveException | RemoveVariableError e) {
					onRemove(contractBonus);
				} catch (CompileException e) {
					onCompileError(contractBonus, e.getMessage());
				}
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

	protected void resolvePayment(IContractPayment contractPayment, Date start,
			Date end, Date issueDate, ExpressionContext expressionContext,
			TaxCalculator taxCalculator, QuoteCalculator quoteCalculator)
			throws AonException {
		Date paymentStart = Period.max(contractPayment.getStartDate(), start);
		Date paymentEnd = Period.min(contractPayment.getEndDate(), end);
		if (paymentEnd.before(paymentStart)) {
			return; // TODO : must be done in context ?
		}
		String name = contractPayment.getName();

		try {
			List<ITimedResult<Double>> results = expressionContext.eval(
					contractPayment.getExpression(), paymentStart, paymentEnd,
					Double.class);
			for (ITimedResult<Double> result : results) {

				Date resultStart = result.getPeriod().getStart();
				Date resultEnd = result.getPeriod().getEnd();

				Double resultDouble = result.getValue();
				double resultValue = resultDouble != null ? resultDouble : 0.00;
				
				addResult(expressionContext, name, resultStart, resultEnd, resultValue);

				expressionContext.setVariable(ALL, resultValue, resultStart,
						resultEnd);
				
				// Here we add 'all' variables involved in quote. 
				double quote = 0.00;
				List<ITimedResult<Double>> quoteResults = quoteCalculator.quote(contractPayment,
						resultStart, resultEnd, resultValue);
				for ( ITimedResult<Double> quoteResult : quoteResults ) {
					try {
						for ( Entry<String,ITimedVariable<?>> entry: quoteResult.getContext().entrySet() ) 
								salaryBuilder.addData(entry.getKey(), entry.getValue());
					} catch ( ExpressionExceptionWrapper  e) {
						if ( e.getCause() instanceof UndefinedVariablesException )
							onInvalidData(((UndefinedVariablesException)e.getCause()).getVariableNames() );
					}
					quote += quoteResult.getValue();
				}

				try {
					Double tax = taxCalculator.tax(contractPayment,
							resultStart, resultEnd, issueDate, resultValue);
					String description = null;
					try {
						description = expressionContext.evalTemplate(
								contractPayment.getDescription(), resultStart,
								resultEnd);
						// TODO ¿ Append period to description ?
						// description = getDescriptionPeriod(description,
						// paymentStart, paymentEnd, amountStart,
						// amountEnd);
					} catch (CompileException e) {
						onCompileError(contractPayment, DESCRIPTION_SYNTAX_ERROR);
					} catch (UndefinedVariablesException e) {
						onCheckError(contractPayment, String.format(DESCRIPTION_UNDEF_ERROR, e.getVariableNames()[0] ));
					} catch (Exception e) {
						onCheckError(contractPayment, DESCRIPTION_UNKNOWN_ERROR);
					}
					salaryBuilder.addPayment(resultValue, quote, tax,
							description, resultStart, resultEnd,
							contractPayment, result.getContext());

				} catch (NotNowException e) {
					salaryBuilder.addZeroPayment(quote, 0.00, resultStart, resultEnd, contractPayment,
							result.getContext());

				}

			}
			
			if ( AonStringUtils.isNotBlank(name))
				for ( Period p : Period.sub(new Period(start, end), expressionContext.getPeriods(name) ))
					addResult(expressionContext, name, p.getStart(), p.getEnd(), 0.00);
			
			// quoteCalculator.quote(contractPayment, paymentStart,
			// paymentEnd, total);

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
			onCompileError(contractPayment, e.getMessage());
			addResult(expressionContext, name, start, end, 0.00);
		} 

	}

	// ---------------------------------------------------------------- Private

	private static void addResult(ExpressionContext expressionContext, String name, Date resultStart, Date resultEnd, Double resultValue){
		if (StringUtils.isEmpty(name))
			return;
		Date valueStart = resultStart;
		List<ITimedVariable<Number>> prevs = expressionContext
				.getVariables(name, resultStart, resultEnd);
		for (ITimedVariable<Number> prev : prevs) {
			Date prevStart = prev.getPeriod().getStart();
			Date prevEnd = prev.getPeriod().getEnd();
			try {
				Number prevValue = prev.getValue(prev.getPeriod());
				if (valueStart.compareTo(prevStart) < 0)
					expressionContext.setVariable(name,
							resultValue, valueStart,
							prev(prevStart));
				expressionContext.setVariable(name, resultValue
						+ prevValue.doubleValue(), prevStart,
						prevEnd);
				valueStart = next(prevEnd);
			} catch (Exception e) {
				System.err.println(String.format("ERROR [%s]: %s",
						name, e.getLocalizedMessage()));
			}
		}
		if (valueStart.compareTo(resultEnd) <= 0) {
			expressionContext.setVariable(name, resultValue,
					valueStart, resultEnd);
		}
	}
	
	public static final String DAY_FOMAT = "%s ( %te )";
	public static final String DAY_PERIOD_FOMAT = "%s ( %te - %te )";
	public static final String COMPLETE_PERIOD_FOMAT = "%s ( %te/%<tm - %te/%<tm )";

	private Double resolveDeduction(ExpressionContext ctx,
			IContractDeduction d, Date start, Date end)
			throws ExpressionException {
		
		List<ITimedResult<Double>> results = ctx.addExpression(d, start, end,
				Double.class);

		Double total = 0.00;


		for (ITimedResult<Double> result : results) {
			Double value = result.getValue();
			Period period = result.getPeriod();

			if (value == null || value == 0) {
				salaryBuilder.addZeroDeduction(period.getStart(), period.getEnd(),d, result.getContext());
				continue;
			}

			String description = null;
			try {
				description = ctx.evalTemplate(d.getDescription(),
						period.getStart(), period.getEnd());
			} catch (CompileException e) {
				onCompileError(d, DESCRIPTION_SYNTAX_ERROR);
			} catch (UndefinedVariablesException e) {
				onCheckError(d, String.format(DESCRIPTION_UNDEF_ERROR, e.getVariableNames()[0] ));
			} catch (Exception e) {
				onCheckError(d, DESCRIPTION_UNKNOWN_ERROR);
			}
			salaryBuilder.addDeduction(value, description, 
					period.getStart(),
					period.getEnd(),
					d,
					result.getContext());
			total += value;
		}

		results = null;

		return total;
	}

	private Double resolveEmbargo(ExpressionContext ctx,
			IContractEmbargo embargo, Date start, Date end)
			throws ExpressionException {

		List<ITimedResult<Double>> results = ctx.eval(embargo.getExpression(),
				start, end, Double.class);

		Double total = 0.00;

		for (ITimedResult<Double> result : results) {
			Double value = result.getValue();

			if (value == null || value == 0) {
				salaryBuilder.addZeroEmbargo(embargo.getId(), embargo,
						result.getContext());
				continue;
			}

			String description = null;
			try {
				Period period = result.getPeriod();
				description = ctx.evalTemplate(embargo.getDescription(),
						period.getStart(), period.getEnd());
			} catch (CompileException e) {
				onCompileError(embargo, DESCRIPTION_SYNTAX_ERROR);
			} catch (UndefinedVariablesException e) {
				onCheckError(embargo, String.format(DESCRIPTION_UNDEF_ERROR, e.getVariableNames()[0] ));
			} catch (Exception e) {
				onCheckError(embargo, DESCRIPTION_UNKNOWN_ERROR);
			}
			salaryBuilder.addEmbargo(embargo.getId(), value, description,
					embargo, result.getContext());
			total += value;
		}

		results = null;

		return total;
	}

	protected void fillData(IContractSalaryCalculatorContext ctx)
			throws SalaryException {
		ExpressionContext expressionContext = ctx.getExpressionContext();
		for ( String name : new String []{
				TC2.getName(),
				QUOTE_DAYS.getName(),
				CGC_BASE.getName(), 
				CGP_BASE.getName(),
				ERE_BASE.getName(),
				MATERNITY_BASE.getName(),
				STRUCTURAL_OVERTIME_BASE.getName(),
				NON_STRUCTURAL_OVERTIME_BASE.getName(),
				WORKED_HOURS.getName(), 
				PREST_IT}){
			try {
				for ( ITimedVariable<Object> data: expressionContext.getVariables(name) ){
					try {
						salaryBuilder.addData(name, data);
					} catch ( Throwable t ){}
				}
			} catch ( Throwable t ) {}
		}
	}
	
	protected void fillTimeUnits(IContractSalaryCalculatorContext ctx) {
		try {
			salaryBuilder.setTimeUnits(
			ctx.getExpressionContext()
			.getVariables(ContextVariable.QUOTE_DAYS).stream()
			.map(var-> (Number)var.getValue(var.getPeriod()))
			.collect(Collectors.summingDouble(number->number.doubleValue()))
			.intValue()
			);
		}catch ( Exception e ) {
			salaryBuilder.setTimeUnits((int)(AonDateUtils.getDaysBetweenDates(ctx.getStartDate(), ctx.getEndDate())+1));
		}
	}

	private void onInvalidData(String... variableNames) {
		if (listener != null) {
			for (int i = 0; i < variableNames.length; i++) {
				listener.onInvalidData(variableNames[i], null);
			}
		}
	}

	private void onCheckError(String message) {
		if (listener != null) {
			listener.onCheckError(message);
		}
	}

	private void onCheckError(IContractBonus bonus, String message) {
		if (listener != null) {
			listener.onCheckError(bonus, message);
		}
	}

	private void onInvalidData(IContractBonus bonus, String message,
			String... variableNames) {
		if (listener != null) {
			for (int i = 0; i < variableNames.length; i++) {
				listener.onInvalidData(bonus, variableNames[i], message);
			}
		}
	}

	private void onCheckError(IContractDeduction dedcution, String message) {
		if (listener != null) {
			listener.onCheckError(dedcution, message);
		}
	}

	private void onCompileError(IContractDeduction deduction, String message) {
		if (listener != null) {
			listener.onCompileError(deduction, message);
		}
	}

	private void onInvalidData(IContractDeduction deduction, String message,
			String... variableNames) {
		if (listener != null) {
			for (int i = 0; i < variableNames.length; i++) {
				listener.onInvalidData(deduction, variableNames[i], message);
			}
		}
	}

	private void onCompileError(IContractBonus bonus, String message) {
		if (listener != null) {
			listener.onCompileError(bonus, message);
		}
	}

	private void onUndefinedData(IContractDeduction deduction,
			RemovedExpressionVariable<?> var) {
		if (listener != null) {
			listener.onUndefinedData(deduction, var);
		}
	}

	private void onUndefinedData(IContractDeduction deduction, String message,
			String... variableNames) {
		if (listener != null) {
			for (int i = 0; i < variableNames.length; i++) {
				listener.onUndefinedData(deduction, variableNames[i], message);
			}
		}
	}

	private void onRemove(IContractBonus bonus) {
		if (listener != null) {
			listener.onRemove(bonus);
		}
	}

	private void onRemove(IContractPayment payment) {
		if (listener != null) {
			listener.onRemove(payment);
		}
	}

	private void onCheckError(IContractPayment payment, String message) {
		if (listener != null) {
			listener.onCheckError(payment, message);
		}
	}

	private void onCompileError(IContractPayment payment, String message) {
		if (listener != null) {
			listener.onCompileError(payment, message);
		}
	}

	private void onInvalidData(IContractPayment payment, String message,
			String... variableNames) {
		if (listener != null) {
			for (int i = 0; i < variableNames.length; i++) {
				listener.onInvalidData(payment, variableNames[i], message);
			}
		}
	}

	private void onUndefinedData(IContractPayment payment,
			RemovedExpressionVariable<?> var) {
		if (listener != null) {
			listener.onUndefinedData(payment, var);
		}
	}

	private void onUndefinedData(IContractPayment payment, String message,
			String... variableNames) {
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

}
