package com.esferalia.aon.gwt.payroll.server;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.function.Supplier;

import jakarta.persistence.Transient;

import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.iterators.FilterIterator;

import com.code.aon.common.AonException;
import com.code.aon.common.enumeration.Month;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.Bonus;
import com.esferalia.aon.gwt.payroll.shared.Deduction;
import com.esferalia.aon.gwt.payroll.shared.ITDataPerson;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.esferalia.aon.gwt.payroll.shared.Salary;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft;
import com.esferalia.aon.gwt.payroll.shared.Variable;
import com.esferalia.aon.payroll.ContractBonus;
import com.esferalia.aon.payroll.ContractDeduction;
import com.esferalia.aon.payroll.ContractEmbargo;
import com.esferalia.aon.payroll.ContractPayment;
import com.esferalia.aon.payroll.IrpfOutcome;
import com.esferalia.aon.payroll.PaymentConcept;
import com.esferalia.aon.payroll.calculator.CompositePayments;
import com.esferalia.aon.payroll.calculator.ContractLeaveLoader.Leave;
import com.esferalia.aon.payroll.calculator.DelegateSQLContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.HierarchyDeductions;
import com.esferalia.aon.payroll.calculator.HierarchyIterator;
import com.esferalia.aon.payroll.calculator.IContractBonus;
import com.esferalia.aon.payroll.calculator.IContractDeduction;
import com.esferalia.aon.payroll.calculator.IContractEmbargo;
import com.esferalia.aon.payroll.calculator.IContractPayment;
import com.esferalia.aon.payroll.calculator.sql.FilterCollection;
import com.esferalia.aon.payroll.calculator.sql.ISQLContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.sql.SQLContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.sql.SQLContractSalaryCalculatorContext.NextHook;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.LeaveType;
import com.esferalia.aon.payroll.irpf.IIrpfCalculatorContext;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.DeferredExpressionVariable;
import com.esferalia.aon.salary.expression.ExpressionContext;
import com.esferalia.aon.salary.expression.ExpressionContext.DeferredExpressionException;
import com.esferalia.aon.salary.expression.ExpressionContext.ExpressionExceptionWrapper;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ExpressionImpl;
import com.esferalia.aon.salary.expression.ExpressionScope;
import com.esferalia.aon.salary.expression.IExpression;
import com.esferalia.aon.salary.expression.IExpressionVariable;
import com.esferalia.aon.salary.expression.ITimedVariable;
import com.esferalia.aon.salary.expression.Period;
import com.esferalia.aon.salary.expression.UndefinedVariablesException;
import com.esferalia.aon.watson.util.AonStringUtils;

public class SalaryDraftCalculatorContext<T extends SQLContractSalaryCalculatorContext>
		extends DelegateSQLContractSalaryCalculatorContext<T> {
	
	static interface IsDraft {
		
	}

	static class DraftPayment extends ContractPayment implements IsDraft{

		private String name;

		@Override
		@Transient
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		@Override
		@Transient
		public ExpressionScope getScope() {
			return ExpressionScope.SALARY;
		}

	}

	static class AgreementDraftPayment extends DraftPayment {

		@Override
		@Transient
		public ExpressionScope getScope() {
			return ExpressionScope.AGREEMENT;
		}

	}

	static class DraftDeduction extends ContractDeduction {
		private String name;

		@Override
		@Transient
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		@Override
		@Transient
		public ExpressionScope getScope() {
			return ExpressionScope.SALARY;
		}
	}

	static class DraftBonus extends ContractBonus {
		private String name;

		@Override
		@Transient
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		@Override
		@Transient
		public ExpressionScope getScope() {
			return ExpressionScope.SALARY;
		}
	}

	static class DraftEmbargo extends ContractEmbargo {

	}

	private static class DraftCompositePayments extends CompositePayments
			implements Predicate {
		
		

		public DraftCompositePayments(Collection<IContractPayment>... payments) {
			super(payments);
		}

		private Set<Integer> ids = new HashSet<Integer>();

		// --------------------------------------------------------- Collection

		@Override
		@SuppressWarnings("unchecked")
		public Iterator<IContractPayment> iterator() {
			return new FilterIterator(super.iterator(), this);
		}

		// ---------------------------------------------------------- Predicate

		@Override
		public boolean evaluate(Object obj) {
			IContractPayment payment = (IContractPayment) obj;
			
			Integer id = payment.getId();
			if ( id == null )
				return true;
			if ( id == Integer.MIN_VALUE) 
				return true;
//			if ( id <= Integer.MIN_VALUE + 1000) 
//				return true;
			if  ( payment instanceof IsDraft) // Draft
				return ids.add(payment.getId());
			return !ids.contains(id);
		}

		// -------------------------------------------------- CompositePayments
		
		protected static class DraftPeriodsContractPaymentIterator 
			extends PeriodsContractPaymentIterator implements IsDraft{

			public DraftPeriodsContractPaymentIterator(IContractPayment payment, Iterator<Period> periodsIt) {
				super(payment, periodsIt);
			}
			
		}
		
		@Override
		protected Iterator getIterator4(IContractPayment payment, Iterator periodsIt) {
			return new DraftPeriodsContractPaymentIterator(payment, periodsIt);
		}
	}

	static class DraftHierarchyEmbargos<T extends IContractEmbargo> extends
			HierarchyIterator<T> {

		private Set<Integer> ids = new HashSet<Integer>();

		public DraftHierarchyEmbargos(Iterator<T>... childs) {
			super(childs);
		}

		@Override
		protected T next(T e) {
			Integer id = e.getId();
			// Not it's not tricky. Remember we use Set, and Set's
			// add methos return true if this Set not already contain
			// the specified element ( id )
			if (ids.add(id))
				return e;
			else
				return null;
		}
	}

	static class DraftHierarchyDeductions<T extends IContractDeduction> extends
			HierarchyDeductions<T> {

		private Set<Integer> ids = new HashSet<Integer>();

		public DraftHierarchyDeductions(Iterator<T>... childs) {
			super(childs);
		}

		@Override
		protected T next(T e) {
			Integer id = e.getId();
			// Not it's not tricky. Remember we use Set, and Set's
			// add methos return true if this Set not already contain
			// the specified element ( id )
			if (ids.add(id))
				return super.next(e);
			else
				return null;
		}
	}

	static class DraftHierarchyBonus<T extends IContractBonus> extends
			HierarchyIterator<T> {

		private Set<Integer> ids = new HashSet<Integer>();

		public DraftHierarchyBonus(Iterator<T>... childs) {
			super(childs);
		}

		@Override
		protected T next(T e) {
			Integer id = e.getId();
			// Not it's not tricky. Remember we use Set, and Set's
			// add methos return true if this Set not already contain
			// the specified element ( id )
			if (ids.add(id))
				return e;
			else
				return null;
		}
	}

	

	class DelegateListener implements IListener {
		
		private IListener listener;
		
		
		
		public DelegateListener(IListener listener) {
			super();
			this.listener = listener;
		}
		
		public IListener getListener() {
			return listener;
		}

		public void setListener(IListener listener) {
			this.listener = listener;
		}
		
		
		@Override
		public void onIrpf(IrpfOutcome irpfOutcome) {
			if ( listener != null )
				listener.onIrpf(irpfOutcome);
		}
		
		@Override
		public void onLiquid(ISalary salary) {
			if ( listener != null )
				listener.onLiquid(salary);
		}

		public <U> U onConstantParameter(String func, U constant, ExpressionContext ctx) {
			if ( listener == null )
				return constant;
			return listener.onConstantParameter(func, constant, ctx);
		}
		
		@Override
		public void onInvalidLeave(Date startDate, Date endDate) {
			if ( listener == null )
				return ;
			listener.onInvalidLeave(startDate, endDate);
		}

		@Override
		public void onMistakenPartialFactor(double monthHours, double workedHours, double factor) {
			if ( listener != null )
				listener.onMistakenPartialFactor(monthHours, workedHours, factor);
		}

		@Override
		public void onUndefinedData(IExpression expression, String variableName, String message, Date start,
				Date end) {
			if ( listener != null )
				listener.onUndefinedData(expression, variableName, message, start, end);
		}

		@Override
		public void onRedefinedImplicit(String name, ITimedVariable<?> redefined, ITimedVariable<?> implicit) {
			if ( listener != null && !hasDraftVariable(name, redefined) )
				listener.onRedefinedImplicit(name, redefined, implicit);
		}
	}
	
	private SalaryDraft draft;
	private DelegateListener delegateListener;


	public SalaryDraftCalculatorContext(SalaryDraft draft, T ctx)
			throws ExpressionException {
		super(ctx);
		this.draft = draft;
		ctx.setListener(this.delegateListener = new DelegateListener(ctx.getListener()));
	}
	
	
	

	@Override
	public boolean next() throws SQLException, ExpressionException {
		
//		return ctx.next(ctx -> {
//			loadDraftContext(ctx);
//			loadDraftLeaves(ctx);
//		});
		
		return ctx.next( new NextHook() {
			
			@Override
			public void beforeLoadLeaves(ExpressionContext ctx) throws ExpressionException {
				loadLeaveVars(ctx);
			}
			
			@Override
			public void beforeLoadDaysContextVariables(ExpressionContext ctx) throws ExpressionException {
				loadDraftContext(ctx);
				loadDraftLeaves(ctx);
			}
		});
	}
	
	@Override
	public IListener getListener() {
		return delegateListener.getListener();
	}

	@Override
	public void setListener(IListener listener) {
		delegateListener.setListener(listener);
	}
	
	protected void loadDraftContext(ExpressionContext exprCtx)
			throws ExpressionException {

		// Date startDate = resetTime(draft.getStartDate());
		// Date endDate = resetTime(draft.getEndDate());

		Date ctxStartDate = resetTime(ctx.getStartDate());
		Date ctxEndDate = resetTime(ctx.getEndDate());

		List<Variable> draftData = draft.getDraftContext();
		for (Variable variable : draftData) {
			String name = variable.getName();
			

			Date varStartDate = resetTime(variable.getStartDate());
			Date varEndDate = resetTime(variable.getEndDate());

			Date startDate = Period.max(ctxStartDate, varStartDate);
			Date endDate = 
					name.equals(ContextVariable.NO_HOLIDAYS.getName()) ? 
					varEndDate : Period.min(ctxEndDate, varEndDate);
			if ( Period.compare(startDate, endDate) > 0 )
				continue;
			
			ITimedVariable<?> prev = exprCtx.getVariable(name, startDate, endDate);

			List<ITimedVariable<Object>> redefined = addVariable(variable, startDate, endDate, exprCtx);
			
			onRedefinedImplicit(exprCtx, name, variable.getExpression(), prev, redefined);
		}

	}

	protected void loadDraftLeaves(ExpressionContext exprCtx)
			throws ExpressionException {

		Date ctxStartDate = resetTime(ctx.getStartDate());
		Date ctxEndDate = resetTime(ctx.getEndDate());

		List<ITDataPerson> drafts = draft.getDraftLeaveIts();

		for (ITDataPerson dataPerson : drafts) {

			Date leaveStartDate = resetTime(dataPerson.getLeaveStartDate());
			Date leaveEndDate = resetTime(dataPerson.getLeaveEndDate());

			Date start = Period.max(ctxStartDate, leaveStartDate);
			Date end = Period.min(ctxEndDate, leaveEndDate);

			if (dataPerson.getContractId() == draft.getEmployee().getId()
					&& DateUtils.compare(leaveStartDate, ctxEndDate) < 0
					&& DateUtils.compare(leaveEndDate, ctxStartDate) > 0) {

				long days = leaveStartDate.before(ctxStartDate) ? DateUtils
						.getDaysBetween(leaveStartDate, ctxStartDate) : 0;

				LeaveType type = getLeaveType(dataPerson.getType());

				Integer id = dataPerson.getContractLeaveId();
				cleanDBLeave(exprCtx, id);

				loadContractLeave(id, start, end, days, type, dataPerson,
						exprCtx);
			}
		}
	}

	private void loadLeaveVars(ExpressionContext exprCtx)
			throws ExpressionException {

		Date ctxStartDate = resetTime(ctx.getStartDate());
		Date ctxEndDate = resetTime(ctx.getEndDate());

		List<Variable> draftData = draft.getDraftContext();
		draftData.stream()
		.filter(v-> v.getName().equals(ContextVariable.PATERNITY_FACTOR.getName() ) 
				|| v.getName().equals(ContextVariable.MATERNITY_FACTOR.getName())
				|| v.getName().equals(ContextVariable.DIRECT_PAY_START.getName())
				)
		.forEach(v -> {
			Date varStartDate = resetTime(v.getStartDate());
			Date varEndDate = resetTime(v.getEndDate());
			Date startDate = Period.max(ctxStartDate, varStartDate);
			Date endDate = Period.min(ctxEndDate, varEndDate);
			try {
				addVariable(v, startDate, endDate, exprCtx);
			} catch (ExpressionException e) {
				// TODO Auto-generated catch block
			}
		});
		
	}


	private void loadContractLeave(Integer id, Date start, Date end, long days,
			LeaveType type, ITDataPerson dataPerson, ExpressionContext exprCtx)
			throws ExpressionException {

		try {

			if (dataPerson.getRegBase().compareTo("REMOVE_VARIABLE()") != 0) {
				getCtx().loadContractLeave(id, start, end, days, type,
						dataPerson.getRegBase(), exprCtx);
			}
		} catch (Exception ex) {
			// is null
			getCtx().loadContractLeave(id, start, end, days, type,
					dataPerson.getRegBase(), exprCtx);
		}

	}

	protected void cleanDBLeave(ExpressionContext exprCtx, Integer id)
			throws ExpressionException {
		if (id <= 0)
			return;

		SortedSet<Leave> dbLeaves = getCtx().getLeaves();
		for (Leave dbLeave : dbLeaves) {
			if (dbLeave.getId().equals(id)) {
				getCtx().clean(exprCtx, dbLeave);
			}
		}
	}

	@Override
	public Collection<IContractDeduction> getContractDeductions()
			throws AonException {
		return new DraftHierarchyDeductions<IContractDeduction>(
				getDraftDeductions().iterator(), super.getContractDeductions()
						.iterator());
	}

	@Override
	public Collection<IContractEmbargo> getContractEmbargos()
			throws AonException {
		return new DraftHierarchyEmbargos<IContractEmbargo>(getDraftEmbargos()
				.iterator(), super.getContractEmbargos().iterator());
	}

	@Override
	public Collection<IContractPayment> getContractPayments()
			throws AonException {
		return new DraftCompositePayments(getDraftPayments(),
				getSuperContractPayments());
	}

	@Override
	public Collection<IContractBonus> getContractBonus() throws AonException {
		return new DraftHierarchyBonus(getDraftBonuses().iterator(), 
				super.getContractBonus().iterator());
	}
	
	public ISQLContractSalaryCalculatorContext getNoItContractSalaryCalculatorContext() {
		return ctx.getNoItContractSalaryCalculatorContext();
	}
	

	public SalaryDraft getDraft() {
		return draft;
	}

	protected IIrpfCalculatorContext getIrpfCalculatorContext() {
		return null;
	}

	protected void onUndefinedData(IExpression expression, String message,
			Date start, Date end, String... variables) {
		if (ctx.getListener() != null)
			for (String variable : variables)
				ctx.getListener().onUndefinedData(expression, variable,
						message, start, end);

	}

	protected Collection<IContractPayment> getDraftPayments() {
		Collection<IContractPayment> draftPayments = new HashSet<IContractPayment>();
		List<IContractPayment> agreemenPayments = new ArrayList<IContractPayment>(getAgreementPayments());
		for (Payment payment : draft.getDraftPayments()) {
			draftPayments.add(getDraftPayment(payment, agreemenPayments));
		}
		return draftPayments;
	}
	
	protected Collection<IContractPayment> getSuperContractPayments()
			throws AonException {
		return new FilterCollection<IContractPayment>(
				p -> inTime(p, ctx.getStartDate(), ctx.getEndDate())/*&& (!hasDraftPayments()  || !isDefault(p))*/ , 
				super.getContractPayments());
	}

	// ---------------------------------------------------------------- Private

	private Collection<IContractDeduction> getDraftDeductions() {
		Collection<IContractDeduction> deductions = new LinkedList<IContractDeduction>();

		for (Deduction deduction : draft.getDraftDeductions()) {

			DraftDeduction draftDeduction = new DraftDeduction();

			draftDeduction.setId(deduction.getId());
			draftDeduction.setName(deduction.getName());
			draftDeduction.setEndDate(resetTime(deduction.getEndDate()));
			draftDeduction.setStartDate(resetTime(deduction.getStartDate()));
			draftDeduction.setMonth(getMonth(deduction.getMonth()));
			draftDeduction.setExpression(deduction.getExpression());
			//draftDeduction.setDescription(deduction.getDescription());
			draftDeduction.setType(getDeductionType(deduction.getType()));
			draftDeduction.setDescription(deduction.getDescriptionTemplate());

			deductions.add(draftDeduction);
		}

		return deductions;
	}

	private Collection<IContractEmbargo> getDraftEmbargos() {
		Collection<IContractEmbargo> embargos = new LinkedList<IContractEmbargo>();

		for (Deduction embargo : draft.getDraftEmbargos()) {

			DraftEmbargo draftEmbargo = new DraftEmbargo();

			draftEmbargo.setId(embargo.getId());
			draftEmbargo.setEndDate(resetTime(embargo.getEndDate()));
			draftEmbargo.setStartDate(resetTime(embargo.getStartDate()));
			draftEmbargo.setExpression(embargo.getExpression());
			//draftEmbargo.setDescription(embargo.getDescription());
			draftEmbargo.setDescription(embargo.getDescriptionTemplate());

			embargos.add(draftEmbargo);
		}

		return embargos;
	}

	private Collection<IContractBonus> getDraftBonuses() {
		Collection<IContractBonus> bonuses = new LinkedList<IContractBonus>();

		for (Bonus bonus : draft.getDraftBonuses()) {

			DraftBonus draftBonus = new DraftBonus();

			draftBonus.setId(bonus.getId());
			draftBonus.setEndDate(resetTime(bonus.getEndDate()));
			draftBonus.setStartDate(resetTime(bonus.getStartDate()));
			draftBonus.setExpression(bonus.getExpression());
			//draftBonus.setDescription(bonus.getDescription());
			draftBonus.setDescription(bonus.getDescriptionTemplate());

			bonuses.add(draftBonus);
		}

		return bonuses;
	}

	private IContractPayment getDraftPayment(Payment payment, Collection<IContractPayment> agreementPayments) {
		if (AonStringUtils.containsIgnoreCase(payment.getExpression(),"CONVENIO()")){
			
			for (IContractPayment agreementPayment : agreementPayments) {
				if (payment.getId().equals(agreementPayment.getId())) {
					DraftPayment draftPayment = newDraftPayment(agreementPayment, AgreementDraftPayment::new);
					draftPayment.setId(payment.getId());
					agreementPayments.remove(agreementPayment);
					return draftPayment;
				}
			}
			
			for (IContractPayment agreementPayment : agreementPayments) {
				if (AonStringUtils.equals(payment.getName(),agreementPayment.getName())) {
					DraftPayment draftPayment = newDraftPayment(agreementPayment, AgreementDraftPayment::new);
					draftPayment.setId(payment.getId());
					agreementPayments.remove(agreementPayment);
					return draftPayment;
				}
			}
		}

		return newDraftPayment(payment);
	}

	private List<ITimedVariable<Object>> addVariable(Variable var, Date start, Date end,
			ExpressionContext ctx) throws ExpressionException {

		if (Variable.isAgreementVariable(var)) {
			ExpressionContext agreementCtx = getAgreementExpressionContext();
			ITimedVariable<?> agreementVar = agreementCtx.getVariable(
					var.getName(), start, end);
			if (agreementVar != null) {
				ctx.putVariable(var.getName(), agreementVar);
				return Collections.emptyList();
			}
		}

		ExpressionImpl expr = newExpressionImpl(var);
		return new ArrayList<ITimedVariable<Object>>(ctx.addLazyExpression(expr, start, end));
	}

	protected void onRedefinedImplicit(ExpressionContext ctx, String name, String expr,
			ITimedVariable<?> implicit, List<ITimedVariable<Object>> redefined) {
		if (getListener() == null)
			return;
		if (redefined == null)
			return;
		if (redefined.isEmpty())
			return;
		if (implicit == null)
			return;
		// TODO: What hell is this
		if (implicit instanceof IExpressionVariable<?> )
			if ( ((IExpressionVariable<?>) implicit).getExpression() != null )
				if (((IExpressionVariable<?>) implicit).getExpression().getScope() != null )
					if (((IExpressionVariable<?>) implicit).getExpression()
						.getScope().compareTo(ExpressionScope.AGREEMENT) >= 0)
						return;
		
		if ( isUndefined(implicit))
			return;
		
		if ( isSystem(name, expr) ) 
			return ;
		
		
		delegateListener.onRedefinedImplicit(name, redefined.get(0), implicit);
	}

	protected boolean isUndefined (ITimedVariable<?> var) {
		try {
			var.getValue(var.getPeriod());
		} catch ( ExpressionExceptionWrapper wrapper){
			try {
				throw wrapper.getCause();
			} catch ( DeferredExpressionException deferred){
				try {
					getExpressionContext()
					.dryEval(deferred.getExpression().getExpression(), 
					var.getPeriod().getStart(), 
					var.getPeriod().getEnd());
					//deferred.eval( getExpressionContext(), Object.class);
				} catch ( UndefinedVariablesException e){
					return true;
				} catch (ExpressionException e) {
					// TODO: return true ? Really it's undefined
				}
			} catch (Throwable e) { 
				// TODO: return true ? Really it's undefined
			} 
		}
		return false;
	}
	
	protected boolean hasDraftVariable(String name, ITimedVariable<?> sqlVar) {
		for ( Variable draftVar: draft.getDraftContext() ) 
			if ( name.equals(draftVar.getName() ) && sqlVar.getPeriod().contains(new Period(draftVar.getStartDate(), draftVar.getEndDate())))
					return true;
		return false;
	}
	
	
	protected boolean hasDraftPayments() {
		return draft.getDraftPayments().size() > 0;
	}


	// ------------------------------------------------------------------------

	private static Date resetTime(Date date) {
		if (date == null)
			return null;

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);

		// Set time fields to zero
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		// Put iterator back in the Date object
		return cal.getTime();
	}

	private static Month getMonth(Short month) {
		return month != null ? Month.getMonthByValue(month) : null;
	}

	private static ExpressionImpl newExpressionImpl(Variable var) {
		ExpressionImpl expr = new ExpressionImpl();
		expr.setName(var.getName());
		expr.setScope(ExpressionScope.SALARY);
		String expression = var.getExpression();
		if (AonStringUtils.isBlank(expression))
			expr.setExpression(String.valueOf(var.getValue()));
		else
			expr.setExpression(var.getExpression());
		return expr;
	}

	private static DraftPayment newDraftPayment(Payment payment) {
		DraftPayment draftPayment = new DraftPayment();

		draftPayment.setId(payment.getId());
		draftPayment.setName(payment.getName());
		draftPayment.setType(getPaymentType(payment.getType()));
		draftPayment.setSalaryType(getSalaryType(payment.getSalaryType()));

		draftPayment.setStartDate(resetTime(payment.getStartDate()));
		draftPayment.setEndDate(resetTime(payment.getEndDate()));
		Short month = payment.getMonth();
		if (month != null) {
			draftPayment.setMonth(Month.getMonthByValue(month));
		}

		//draftPayment.setDescription(payment.getDescription());
		draftPayment.setDescription(payment.getDescriptionTemplate());

		draftPayment.setExpression(payment.getExpression());
		draftPayment.setIrpfExpression(payment.getIrpfExpression());
		draftPayment.setQuoteExpression(payment.getQuoteExpression());

		PaymentConcept concept = new PaymentConcept();
		concept.setId(payment.getConceptId());
		draftPayment.setPaymentConcept(concept);

		return draftPayment;
	}

	private static DraftPayment newDraftPayment(IContractPayment payment) {
		DraftPayment draftPayment = new DraftPayment();
		return newDraftPayment(draftPayment, DraftPayment::new);
	}

	private static DraftPayment newDraftPayment(IContractPayment payment, Supplier<DraftPayment> supplier) {
		DraftPayment draftPayment = supplier.get();

		draftPayment.setId(payment.getId());
		draftPayment.setName(payment.getName());
		draftPayment.setType(payment.getType());
		draftPayment.setSalaryType(payment.getSalaryType());

		draftPayment.setStartDate(resetTime(payment.getStartDate()));
		draftPayment.setEndDate(resetTime(payment.getEndDate()));
		draftPayment.setMonth(payment.getMonth());
		draftPayment.setDescription(payment.getDescription());

		draftPayment.setExpression(payment.getExpression());
		draftPayment.setIrpfExpression(payment.getIrpfExpression());
		draftPayment.setQuoteExpression(payment.getQuoteExpression());

		PaymentConcept concept = new PaymentConcept();
		concept.setId(payment.getConceptId());
		draftPayment.setPaymentConcept(concept);

		return draftPayment;
	}

	private static boolean inTime( IContractPayment p, Date startDate, Date endDate) {
		return 
		Period.compare(p.getEndDate(),  startDate ) >= 0 
		&& Period.compare(p.getStartDate(),  endDate ) <= 0 ;
	}
	
	private static SalaryType getSalaryType(Salary.Type type) {
		return type != null ? SalaryType.values()[type.ordinal()] : null;
	}

	private static PaymentType getPaymentType(Payment.Type type) {
		return type != null ? PaymentType.values()[type.ordinal()] : null;
	}

	private static DeductionType getDeductionType(Deduction.Type type) {
		return type != null ? DeductionType.values()[type.ordinal()] : null;
	}

	private static LeaveType getLeaveType(ITDataPerson.Type type) {
		return type != null ? LeaveType.values()[type.ordinal()] : null;
	}

	private static boolean isSystem(String name, String expr) {
		return AonStringUtils.isNotEmpty(expr) && expr.matches("\\s*SISTEMA\\s*\\(\\s*['\"]"+ name +"['\"]\\s*\\)\\s*;*\\s*");
	}
	
	private static boolean isDefault(IContractPayment payment) {
		return AonStringUtils.startsWith(payment.getExpression(), "/*default*/" );
	}
	

}
