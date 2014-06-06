package com.esferalia.aon.payroll.calculator;

import static com.esferalia.aon.payroll.enumeration.ContextVariable.ALL;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGC_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGP_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.EMBARGO_LEFT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.EMBARGO_LIMIT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.EMBARGO_MAX;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.EMBARGO_PAID;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.EMPLOYEE_QUOTA;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.ENTERPRISE_QUOTA;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.IRPF_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.NON_STRUCTURAL_OVERTIME_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.STRUCTURAL_OVERTIME_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.TOTAL_LIQUID;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.TOTAL_PAYMENT;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.mvel2.CompileException;

import com.code.aon.AonVersion;
import com.code.aon.common.AonException;
import com.code.aon.common.util.CommonUtil;
import com.esferalia.aon.payroll.calculator.TaxCalculator.NotNowException;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.ISalaryBuilder;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.calculator.ISalaryCalculator;
import com.esferalia.aon.salary.calculator.ISalaryCalculatorContext;
import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.expression.CheckException;
import com.esferalia.aon.salary.expression.ExpressionContext;
import com.esferalia.aon.salary.expression.ExpressionContext.RemoveVariableError;
import com.esferalia.aon.salary.expression.ExpressionContext.RemovedExpressionVariable;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ITimedObject;
import com.esferalia.aon.salary.expression.ITimedResult;
import com.esferalia.aon.salary.expression.InvalidVariables;
import com.esferalia.aon.salary.expression.Period;
import com.esferalia.aon.salary.expression.RemoveException;
import com.esferalia.aon.salary.expression.UndefinedVariablesException;

public class ContractSalaryCalculator implements ISalaryCalculator {

	public interface IListener {

		public void onCheckError(String message);

		public void onInvalidData(String variableName, String message);

		public void onCompileError(String variableName, String message);

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

		boolean willBeDefined(Collection<String> willbeDefined) {

			for (String var : exception.getVariableNames())
				if (!willbeDefined.contains(var))
					return false;

			return true;
		}

		void onUndefinedData(ContractSalaryCalculator calculator) {
			calculator.onUndefinedData(this, exception.getMessage(),
					exception.getVariableNames());
		}

	}

	private IListener listener;
	private ISalaryBuilder salaryBuilder;

	public void setListener(IListener listener) {
		this.listener = listener;
	}

	public void setSalaryBuilder(ISalaryBuilder salaryBuilder) {
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
	public ISalary calculate(ISalaryCalculatorContext ctx)
			throws SalaryException {
		IContractSalaryCalculatorContext contractSalaryCalculatorContext = (IContractSalaryCalculatorContext) ctx;

		Date start = ctx.getStartDate();
		Date end = ctx.getEndDate();

		ExpressionContext expressionContext = ctx.getExpressionContext();

		salaryBuilder.createNewSalary();
		salaryBuilder.setContract(contractSalaryCalculatorContext
				.getSalaryProxy());

		fillEnterpriseData(contractSalaryCalculatorContext);
		fillEmployeeData(contractSalaryCalculatorContext);
		fillSalaryData(contractSalaryCalculatorContext);
		Double totalPayment = fillPayments(contractSalaryCalculatorContext);
		Double totalDeduction = fillDeductions(contractSalaryCalculatorContext);

		expressionContext.addVariable(TOTAL_LIQUID, totalPayment
				- totalDeduction, start, end);
		Double totalEmbargos = fillEmbargos(contractSalaryCalculatorContext);

		salaryBuilder.setTotalDeduction(totalDeduction + totalEmbargos);

		Double totalCost = fillCosts(contractSalaryCalculatorContext);
		expressionContext.addVariable(ENTERPRISE_QUOTA, totalCost, start, end);
		Double totalBonus = fillBonus(contractSalaryCalculatorContext);

		Double totalEnterprise = totalCost - totalBonus;
		salaryBuilder.setTotalEnterprise(totalEnterprise);

		salaryBuilder.setTotalLiquid(totalPayment - totalDeduction
				- totalEmbargos);

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
		long days = CommonUtil.getDaysBetweenDates(ctx.getStartDate(),
				ctx.getEndDate()) + 1;
		salaryBuilder.setTimeUnits((int) days);
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

			Date chargeDate = ctx.getChargeDate();

			Set<String> paymentsVars = new HashSet<String>();
			LinkedList<UndefPayment> undefPayments = new LinkedList<UndefPayment>();
			Collection<IContractPayment> contractPayments = ctx
					.getContractPayments();

			LinkedList<UndefPayment> undefTotalPayments = new LinkedList<UndefPayment>();

			for (IContractPayment contractPayment : contractPayments) {
				try {
					resolvePayment(contractPayment, start, end, chargeDate,
							expressionContext, taxCalculator, quoteCalculator);
				} catch (UndefinedTotalPaymentException e) {
					undefTotalPayments
							.add(new UndefPayment(contractPayment, e));
				} catch (UndefinedContextVariablesException e) {
					onUndefinedData(contractPayment, e.getMessage(),
							e.getVariableNames());
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

			for (ListIterator<UndefPayment> listIterator = undefPayments
					.listIterator(); listIterator.hasNext();) {
				UndefPayment undefPayment = listIterator.next();
				if (undefPayment.willBeDefined(paymentsVars))
					continue;
				// clean undefined ...
				listIterator.remove();
				undefPayment.onUndefinedData(this);
				paymentsVars.remove(undefPayment.getName());
			}

			int undefined = 0;
			while (undefPayments.size() > 0) {
				UndefPayment undefPayment = undefPayments.pop();
				try {
					resolvePayment(undefPayment, start, end, chargeDate,
							expressionContext, taxCalculator, quoteCalculator);

				} catch (UndefinedTotalPaymentException e) {
					undefTotalPayments.add(undefPayment);
				} catch (UndefinedContextVariablesException e) {
					paymentsVars.remove(undefPayment.getName());
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
			expressionContext.addVariable(TOTAL_PAYMENT, totalPayment, start,
					end);

			for (UndefPayment undefTotalPayment : undefTotalPayments) {
				try {
					resolvePayment(undefTotalPayment, start, end, chargeDate,
							expressionContext, taxCalculator, quoteCalculator);
				} catch (UndefinedVariablesException e) {
					onUndefinedData(undefTotalPayment, e.getMessage(),
							e.getVariableNames());
				}
			}
			totalPayment = taxCalculator.getTotalPayment();
			expressionContext.addVariable(TOTAL_PAYMENT, totalPayment, start,
					end);

			salaryBuilder.setTotalPayment(totalPayment);

			salaryBuilder.setRemuneration(taxCalculator.getRenumeration());

			salaryBuilder.setItBase(quoteCalculator.getItBase());

			Date irpfDate = ctx.getIrpfDate();
			salaryBuilder.setIrpfBase(taxCalculator.getIrpfBase());
			salaryBuilder.setInkindIrpfBase(taxCalculator.getInKindIrpfBase());
			expressionContext.addVariable(IRPF_BASE,
					taxCalculator.getIrpfBase(), irpfDate, irpfDate);

			double rawCgcbase = quoteCalculator.getRawCgcBase();
			salaryBuilder.setRawCgcBase(quoteCalculator.getRawCgcBase());

			double cgcBase = rawCgcbase;
			try {
				cgcBase = quoteCalculator.getCgcBase();
			} catch (UndefinedVariablesException e) {
				onInvalidData(e.getVariableNames());
			}
			salaryBuilder.setCgcBase(cgcBase);
			double cgcBaseVar = cgcBase - quoteCalculator.getMaternityBase();
			expressionContext.addVariable(CGC_BASE, cgcBaseVar, start, end);
			// System.out.printf("CGC_BASE=%.3f \r\n", cgcBaseVar);

			double cgpBase = quoteCalculator.getRawCgpBase();
			try {
				cgpBase = quoteCalculator.getCgpBase();
			} catch (UndefinedVariablesException e) {
				onInvalidData(e.getVariableNames());
			}
			salaryBuilder.setCgpBase(cgpBase);
			double cgpBaseVar = cgpBase - quoteCalculator.getMaternityBase();
			expressionContext.addVariable(CGP_BASE, cgpBaseVar, start, end);

			salaryBuilder.setNonHExtraBase(quoteCalculator
					.getNonStructuralBase());
			expressionContext.addVariable(NON_STRUCTURAL_OVERTIME_BASE,
					quoteCalculator.getNonStructuralBase(), start, end);

			salaryBuilder.setHExtraBase(quoteCalculator.getStructuralBase());
			expressionContext.addVariable(STRUCTURAL_OVERTIME_BASE,
					quoteCalculator.getStructuralBase(), start, end);

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
					} else if (type.isTaxDeduction()) {
						totalIrpf += deduction;
					}
				} catch (RemoveException e) {
					// TODO: Something ??? It's really necessary...
				} catch (InvalidVariables e) {
					onInvalidData(contractDeduction, e.getMessage(),
							e.getVariables());
				} catch (CheckException e) {
					onCheckError(contractDeduction, e.getMessage());
				} catch (RemoveVariableError e) {
					onUndefinedData(contractDeduction, e.getVariable());
				} catch (UndefinedVariablesException e) {
					onUndefinedData(contractDeduction, e.getMessage(),
							e.getVariableNames());
				} catch (CompileException e) {
					onCompileError(contractDeduction, e.getMessage());
				}

			}

			salaryBuilder.setTotalIrpf(totalIrpf);
			salaryBuilder.setSocialSecurityContributions(ssContributions);
			expressionContext.addVariable(EMPLOYEE_QUOTA, ssContributions,
					start, end);

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
				expressionContext.addVariable(EMBARGO_PAID, left, embargoStart,
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
							} catch (Exception e) {
								// TODO : Log ???
							}
							salaryBuilder.addCost(value, description,
									contractCost, amount.getContext());
							cost += value;
						}
					}
					total += cost;
				} catch (IllegalArgumentException e) {
					// costStart > costEnd, ignore .
				}

			}

		} catch (UndefinedVariablesException e) {

		} catch (ExpressionException e) {
			throw new SalaryException(e.getMessage(), e);
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
								salaryBuilder.addBonus(value, description,
										contractBonus, amount.getContext());
							} catch (Exception e) {
								// TODO : Log ???
							}
						}
						bonus += amount.getValue();
					}
					total += bonus;

				} catch (InvalidVariables e) {
					onInvalidData(contractBonus, e.getMessage(),
							e.getVariables());
				} catch (CheckException e) {
					onCheckError(contractBonus, e.getMessage());
				}
			}

		} catch (ExpressionException e) {
			throw new SalaryException(e.getMessage(), e);
		} catch (AonException e) {
			throw new SalaryException(e.getMessage(), e);
		}

		return total;
	}

	// ---------------------------------------------------------------- Private

	private void resolvePayment(IContractPayment contractPayment, Date start,
			Date end, Date chargeDate, ExpressionContext expressionContext,
			TaxCalculator taxCalculator, QuoteCalculator quoteCalculator)
			throws AonException {
		Date paymentStart = Period.max(contractPayment.getStartDate(), start);
		Date paymentEnd = Period.min(contractPayment.getEndDate(), end);
		if (paymentEnd.before(paymentStart)) {
			return; // TODO : must be done in context ?
		}

		try {

			List<ITimedResult<Double>> results = expressionContext
					.addExpression(contractPayment, paymentStart, paymentEnd,
							Double.class);

			for (ITimedResult<Double> result : results) {

				Date amountStart = result.getPeriod().getStart();
				Date amountEnd = result.getPeriod().getEnd();

				Double valueDouble = result.getValue();
				double value = valueDouble != null ? valueDouble : 0.00;

				expressionContext.addVariable(ALL, value, amountStart,
						amountEnd);

				Double quote = quoteCalculator.quote(contractPayment,
						amountStart, amountEnd, value);

				try {
					Double tax = taxCalculator.tax(contractPayment,
							amountStart, amountEnd, chargeDate, value);
					String description = null;
					try {
						description = expressionContext.evalTemplate(
								contractPayment.getDescription(), amountStart,
								amountEnd);
						// TODO ¿ Append period to description ?
						// description = getDescriptionPeriod(description,
						// paymentStart, paymentEnd, amountStart,
						// amountEnd);
					} catch (Exception e) {
						// TODO : Log ???
					}
					salaryBuilder.addPayment(value, quote, tax, description,
							amountStart, amountEnd, contractPayment,
							result.getContext());

				} catch (NotNowException e) {
					salaryBuilder.addZeroPayment(quote, 0.00, contractPayment,
							result.getContext());

				}

			}
			// quoteCalculator.quote(contractPayment, paymentStart,
			// paymentEnd, total);

		} catch (RemoveException e) {
			// TODO: Something ??? It's really necessary...
		} catch (InvalidVariables e) {
			onInvalidData(contractPayment, e.getMessage(), e.getVariables());
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

			if (value == null || value == 0) {
				salaryBuilder.addZeroDeduction(d, result.getContext());
				continue;
			}

			String description = null;
			try {
				Period period = result.getPeriod();
				description = ctx.evalTemplate(d.getDescription(),
						period.getStart(), period.getEnd());
			} catch (Exception e) {
				// TODO : Log ???
			}
			salaryBuilder.addDeduction(value, description, d,
					result.getContext());
			total += value;
		}

		results = null;

		return total;
	}

	private Double resolveEmbargo(ExpressionContext ctx,
			IContractEmbargo embargo, Date start, Date end)
			throws ExpressionException {

		List<ITimedResult<Double>> results = ctx.addExpression(embargo, start,
				end, Double.class);

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
			} catch (Exception ex) {
				// TODO : Log ???
			}
			salaryBuilder.addEmbargo(embargo.getId(), value, description,
					embargo, result.getContext());
			total += value;
		}

		results = null;

		return total;
	}

	private void onInvalidData(String... variableNames) {
		if (listener != null) {
			for (int i = 0; i < variableNames.length; i++) {
				listener.onInvalidData(variableNames[i], null);
			}
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

}
