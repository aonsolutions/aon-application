package com.esferalia.aon.gwt.payroll.server;

import static com.esferalia.aon.payroll.sql.SQLConstants.CONTRACT;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.function.Supplier;

import org.apache.commons.lang.StringUtils;
import org.mvel2.CompileException;

import com.code.aon.common.AonException;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.gwt.payroll.shared.AgreementDraft;
import com.esferalia.aon.gwt.payroll.shared.Agreement.Level;
import com.esferalia.aon.gwt.payroll.shared.AgreementDraft.SalaryTable;
import com.esferalia.aon.gwt.payroll.shared.Bonus;
import com.esferalia.aon.gwt.payroll.shared.Event;
import com.esferalia.aon.gwt.payroll.shared.Event.Type;
import com.esferalia.aon.gwt.payroll.shared.Extra;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.esferalia.aon.gwt.payroll.shared.PaymentEvent;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft.Scope;
import com.esferalia.aon.gwt.payroll.shared.StringVariable;
import com.esferalia.aon.gwt.payroll.shared.Variable;
import com.esferalia.aon.gwt.payroll.sql.SQLAgreementDraft;
import com.esferalia.aon.gwt.payroll.sql.SQLSalaryDraftCalculatorContext;
import com.esferalia.aon.gwt.payroll.sql.SQLSettleDraftCalculatorContext;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Salary;
import com.esferalia.aon.payroll.calculator.IContractBonus;
import com.esferalia.aon.payroll.calculator.IContractCost;
import com.esferalia.aon.payroll.calculator.IContractDeduction;
import com.esferalia.aon.payroll.calculator.IContractEmbargo;
import com.esferalia.aon.payroll.calculator.IContractPayment;
import com.esferalia.aon.payroll.calculator.IContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.sql.ISQLContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.sql.SQLAgreementContextFactory;
import com.esferalia.aon.payroll.calculator.sql.SQLContractExtraCalculatorContext;
import com.esferalia.aon.payroll.calculator.sql.SQLContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.sql.SQLContractSalaryCalculatorContext.AgreementContextKey;
import com.esferalia.aon.payroll.calculator.sql.SQLContractSalaryCalculatorContext.CCCContextKey;
import com.esferalia.aon.payroll.calculator.sql.SQLSystemExpressionContextFactory;
import com.esferalia.aon.payroll.enumeration.CCCType;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;
import com.esferalia.aon.payroll.irpf.IIrpfCalculatorContext;
import com.esferalia.aon.payroll.irpf.sql.SQLIrpfCalculatorContext;
import com.esferalia.aon.payroll.sql.SQLConstants;
import com.esferalia.aon.payroll.sql.SQLConstants.ContractColumns;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.calculator.ISalaryCalculatorContext;
import com.esferalia.aon.salary.expression.CheckException;
import com.esferalia.aon.salary.expression.ExpressionContext;
import com.esferalia.aon.salary.expression.ExpressionContext.ExpressionExceptionWrapper;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ExpressionScope;
import com.esferalia.aon.salary.expression.FullHideException;
import com.esferalia.aon.salary.expression.IExpressionVariable;
import com.esferalia.aon.salary.expression.ITimedResult;
import com.esferalia.aon.salary.expression.ITimedVariable;
import com.esferalia.aon.salary.expression.InterruptedException;
import com.esferalia.aon.salary.expression.UndefinedVariablesException;
import com.esferalia.aon.watson.util.AonDateUtils;
import com.esferalia.aon.watson.util.AonUtils;

public class EmployeesServiceHelper {

	public static final String REMOVE = "REMOVE()";

	/**
	 * 
	 * @param connection
	 * @param draft
	 * @param domainId
	 * @param parentDomainId
	 * @throws SQLException
	 */
	public static void calculate(Connection connection, AgreementDraft draft,
			Integer domainId, Integer parentDomainId) throws SQLException {
		SortedSet<Date> datesWithChanges = parentDomainId == null
				? SQLAgreementDraft.getDatesWithChanges(connection,
						draft.getId(), domainId)
				: SQLAgreementDraft.getDatesWithChanges(connection,
						draft.getId(), domainId, parentDomainId);

		Set<Payment> dbPayments = SQLAgreementDraft.getPayments(connection,
				draft.getId(), draft.getStartDate(), draft.getEndDate(),
				domainId, parentDomainId);

		@SuppressWarnings("unchecked")
		Collection<Payment> payments = new CompositeItems<Payment>(
				draft.getDraftPayments(), dbPayments);

		Set<String> variables = new HashSet<String>();
		Set<String> paymentsNames = new HashSet<String>();

		Set<Payment> allPayments = new HashSet<Payment>();
		for (Payment payment : payments) {

			if (hide(payment, dbPayments))
				continue;

			
			String paymentName = payment.getName();
			
			try {
				String expression = 
				getUserScript(payment.getExpression());
				Set<String> exprVariables =
				ExpressionContext.getVariableSet(
						expression);				
				variables.addAll(exprVariables);
				if ( !exprVariables.contains(paymentName) )
					paymentsNames.add(paymentName);

			} catch (Exception e) {
				paymentsNames.add(paymentName);
			}

			try {
				String irpfExpression = 
				getUserScript(payment.getIrpfExpression());
				Set<String> irpfVariables =
				ExpressionContext.getVariableSet(
						irpfExpression);				
				variables.addAll(irpfVariables);
			} catch (Exception e) {
			}

			try {
				String quoteExpression = 
				getUserScript(payment.getQuoteExpression());
				Set<String> quoteVariables =
				ExpressionContext.getVariableSet(
						quoteExpression);				
				variables.addAll(quoteVariables);
			} catch (Exception e) {
			}
			
			allPayments.add(payment);
		}

		variables.removeAll(paymentsNames);

		// Filter ContextVariable
		List<String> contextVariables = new LinkedList<String>();
		for (ContextVariable ctxVar : ContextVariable.values())
			if (ctxVar.isInternal())
				contextVariables.add(ctxVar.getName());
		variables.removeAll(contextVariables);

		// This is awfull ... very awful
		List<String> privateVariables = new LinkedList<String>();
		for (String var : variables) {
			if (var.endsWith("_ACTUAL"))
				privateVariables.add(var);
			if (var.endsWith("_HELP"))
				privateVariables.add(var);
		}
		variables.removeAll(privateVariables);

		/*
		 * Clean system variables. Set<String> systemVars =
		 * getSystemVariables(connection, draft.getStartDate(),
		 * draft.getEndDate()); variables.removeAll(systemVars);
		 */

		Set<Level> dbLevels = SQLAgreementDraft.getLevels(connection,
				draft.getId(),domainId, parentDomainId);

		Set<Level> allLevels = new HashSet<Level>(dbLevels);

		for (Level draftLevel : draft.getDraftLevels()) {
			allLevels.remove(draftLevel);
			if (!StringUtils.equals(REMOVE, draftLevel.getDescription()))
				allLevels.add(draftLevel);
		}

		Set<Extra> dbExtras = SQLAgreementDraft.getExtras(connection,
				draft.getId());

		Set<Extra> allExtras = new HashSet<Extra>(dbExtras);

		for (Extra draftExtra : draft.getDraftExtras()) {
			allExtras.remove(draftExtra);
			if (!StringUtils.equals(REMOVE, draftExtra.getIssueDate()))
				allExtras.add(draftExtra);
		}

		Map<Integer, Set<String>> dbCategories = SQLAgreementDraft
				.getCategories(connection, draft.getId(), domainId, parentDomainId);

		Map<Integer, Set<String>> allCategories = new HashMap<Integer, Set<String>>(
				dbCategories);

		Map<Integer, Set<String>> draftCategories = draft.getDraftCategories();
		allCategories.putAll(draftCategories);

		Level agreementData = new Level();
		agreementData.setId(0);
		allLevels.add(agreementData);

		SalaryTable dbSalaryTable = SQLAgreementDraft.getSalaryTable(connection,
				draft.getId(), draft.getStartDate(), draft.getEndDate(), domainId, parentDomainId);
		SalaryTable allSalaryTable = new SalaryTable(dbSalaryTable);
		allSalaryTable.putAll(draft.getDraftSalaryTable());
		
		for ( Variable var: allSalaryTable.getAllVariables() ) {
			String expression = 
			getUserScript(var.getExpression());
			Set<String> exprVariables =
			ExpressionContext.getVariableSet(
					expression);				
			variables.addAll(exprVariables);
		}

		draft.setLevels(allLevels);
		draft.setExtras(allExtras);
		draft.setVariables(variables); // * No draft
		draft.setPayments(allPayments);
		draft.setSalaryTable(allSalaryTable);
		draft.setCategoriesMap(allCategories);
		draft.setDatesWithChanges(datesWithChanges);

		List<Integer> domainIds = new ArrayList<Integer>();
		if (parentDomainId != null)
			domainIds.add(parentDomainId);
		domainIds.add(domainId);
		SQLAgreementContextFactory  agreementCtxFactory = null;
		
		try {
			agreementCtxFactory = 
					newSQLAgreementContextFactory(connection, draft.getStartDate(), draft.getEndDate());
		} catch (Throwable e) {
			return;
			// TODO: 
		}
		
		try {
			eval(agreementCtxFactory, draft.getId(), allLevels, allSalaryTable,
					draft.getStartDate(), draft.getEndDate(),
					domainIds.toArray(new Integer[] {}));
		} catch ( Throwable t) {
			// TODO: 
		}
		
		try {
			Set<Event> allEvents = eval(agreementCtxFactory, draft.getId(), allPayments, draft.getStartDate(), draft.getEndDate());
			draft.setEvents(allEvents);
		} catch ( Throwable t) {
			// TODO: 
		}
		
	}

	public static SalaryDraftCalculatorContext<SQLContractSalaryCalculatorContext> getSalaryCalculatorContext(
			final Connection conn, final SalaryDraft draft,
			IContractSalaryCalculatorContext.IListener listener)
					throws ExpressionException, SQLException {
		return getSalaryCalculatorContextImpl(conn, draft, listener);
	}

	public static List<Bonus> getAvailableBonuses(Connection conn,
			int employeeId, Integer... domains)
					throws IllegalArgumentException {
		try {
			List<Bonus> availableBonuses = new ArrayList<Bonus>();
			AON.getAvailableBonuses(new AONContext(conn), props -> {
				return props.getDomainProperty().in(domains);
				// .and(props.getIsUnknowProperty().eq(true));
			}).map(b -> {
				Bonus bonus = new Bonus();
				bonus.setId(b.getId());
				bonus.setExpression(b.getExpression());
				bonus.setDescription(b.getDescription());
				if (b.getType() != null)
					bonus.setType(Bonus.Type.values()[b.getType().ordinal()]);
				return bonus;
			}).forEach(bonus -> availableBonuses.add(bonus));
			;

			return availableBonuses;

		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException logOrIgnrore) {
				}
			}
		}
	}

	// ------------------------------------------------------------------------

	protected static Map<String, boolean[]> getDefinedMap(
			IContractSalaryCalculatorContext ctx) {
		Map<String, boolean[]> definedMap = new HashMap<String, boolean[]>();

		Date startDate = ctx.getStartDate();
		Date endDate = ctx.getEndDate();

		for (String name : ctx.getSystemExpressionContext().variablesSet()) {
			boolean defined[] = new boolean[Scope.NUM_VALUES];
			defined[Scope.SYSTEM.ordinal()] = true;
			definedMap.put(name, defined);
		}
		// Agreement
		ExpressionContext agreementCtx = ctx.getAgreementExpressionContext();
		for (String name : agreementCtx.variablesSet()) {
			ITimedVariable<?> var = agreementCtx.getVariable(name, startDate,
					endDate);
			if (!(var instanceof IExpressionVariable<?>))
				continue;
			ExpressionScope scope = ((IExpressionVariable<?>) var)
					.getExpression().getScope();
			if (scope != ExpressionScope.AGREEMENT)
				continue;

			boolean defined[] = definedMap.get(name);
			if (defined == null) {
				defined = new boolean[Scope.NUM_VALUES];
				definedMap.put(name, defined);
			}
			defined[Scope.AGREEMENT.ordinal()] = true;
		}

		for (IContractPayment p : ctx.getAgreementPayments()) {
			if (StringUtils.isBlank(p.getName()))
				continue;

			boolean defined[] = new boolean[Scope.NUM_VALUES];
			defined[Scope.AGREEMENT.ordinal()] = true;
			definedMap.put(p.getName(), defined);

		}

		ExpressionContext implicitCtx = ctx.getImplicitExpressionContext();
		for (String name : ctx.getImplicitExpressionContext().variablesSet()) {

			ITimedVariable<?> var = agreementCtx.getVariable(name, startDate,
					endDate);
			if (var instanceof IExpressionVariable<?>)
				continue;
			// Implicit variables don't come from expression

			boolean defined[] = definedMap.get(name);
			if (defined == null) {
				defined = new boolean[Scope.NUM_VALUES];
				definedMap.put(name, defined);
			}
			defined[Scope.APPLICATION.ordinal()] = true;
		}

		return definedMap;
	}

	// ------------------------------------------------------------------------

	private static Variable copy(Variable var) {
		Variable copy = new StringVariable();
		copy.setImplicit(true);
		copy.setName(var.getName());
		copy.setScope(var.getScope());
		copy.setEndDate(var.getEndDate());
		copy.setStartDate(var.getStartDate());
		copy.setExpression(var.getExpression());
		return copy;
	}

	private static boolean hide(Payment payment, Set<Payment> parents) {
		if (payment.getConceptId() == null)
			return false;
		if (!StringUtils.equals(REMOVE, payment.getExpression()))
			return false;

		if (payment.getId() < 0)
			return true;

		return parents.stream()
				.filter(parent -> AonUtils.equals(parent.getConceptId(),payment.getConceptId())
						&& parent.getDomain().equals(payment.getDomain()))
				.findAny().isPresent();
	}

	private static void eval(SQLAgreementContextFactory agreementCtxFactory, int agreementId,
			Set<Level> levels, SalaryTable salaryTable, Date start, Date end,
			Integer... domainIds) {
			// try to resolve some variables. Here we go.

			LinkedList<Variable> defVars = new LinkedList<Variable>(
					salaryTable.getVariables(0));

			for (Level level : levels) {
				ExpressionContext levelCtx = new ExpressionContext();

				for (Integer domainId : domainIds) {
					AgreementContextKey levelKey = new AgreementContextKey(
							domainId, agreementId, level.getId());
					levelCtx.add(agreementCtxFactory.create(levelKey));
				}

				for (Variable defVar : defVars)
					if (!salaryTable.contains(level.getId(), defVar.getName()))
						salaryTable.put(level.getId(), copy(defVar));

				LinkedList<Variable> levelVars = new LinkedList<Variable>(
						salaryTable.getVariables(level.getId()));

				eval(levelCtx, levelVars, start, end);
			}
	}

	private static Set<Event> eval(SQLAgreementContextFactory agreementCtxFactory, int agreementId,
			Collection<Payment> payments, Date start, Date end) {
		
		Set<Event> events = new HashSet<Event>();
		
		List<Payment> hide = new ArrayList<Payment>();

		ExpressionContext agreementDataCtx = agreementCtxFactory.getSystemExpressionContext();
		for ( Payment payment: payments ) {
			try {
				agreementDataCtx.eval(payment.getExpression(), start, end);
			}catch ( CheckException e ) {
				events.add(
				new PaymentEvent()
				.setPayment(payment)
				.setType(Type.WARNING)
				.setMessage(e.getMessage()));
			}catch (CompileException e){
				events.add(
				new PaymentEvent()
				.setPayment(payment)
				.setType(Type.ERROR)
				.setMessage(e.getMessage()));
			}catch ( FullHideException e ) {
				events.add(
				new Event()
				.setType(Type.INFO)
				.setMessage(e.getMessage()));
				hide.add(payment);
			}
			catch (ExpressionException e) {
			}
		}
		
		// TODO: Make this outside please
		for ( Payment payment: hide )
			payments.remove(payment);
		
		return events;
	}
	

	private static SQLAgreementContextFactory newSQLAgreementContextFactory(Connection conn, Date start, Date end)
			throws SQLException, ExpressionException {
		SQLSystemExpressionContextFactory systemCtxFactory = new SQLSystemExpressionContextFactory(
				conn, start, end,
				ISQLContractSalaryCalculatorContext.NEWER);

		Supplier<ExpressionContext> systemCtxSupplier = () -> systemCtxFactory
				.create(new CCCContextKey(CCCType.PRINCIPAL, SSRegimeType.GENERAL));

		SQLAgreementContextFactory agreementCtxFactory = new SQLAgreementContextFactory(
				conn, systemCtxSupplier, start, end,
				ISQLContractSalaryCalculatorContext.NEWER);
		return agreementCtxFactory;
	}

	private static void eval(ExpressionContext ctx, LinkedList<Variable> vars,
			Date start, Date end) {
		int errors = 0;
		while (errors < vars.size()) {
			Variable var = vars.pop();
			try {
				List<ITimedResult<Object>> results = ctx
						.eval(var.getExpression(), start, end);
				errors = 0;
				for (ITimedResult<Object> result : results) {
					var.setValue(result.getValue());
					ctx.putVariable(var.getName(), result);
					// System.out.println(var.getName() + " = " +
					// var.getValue());
				}
			} catch (UndefinedVariablesException e) {
				vars.add(var);
				errors++;
			} catch (CompileException e) {
				var.setValue(generateErrorMessage(e));
				errors++;
			} catch (ExpressionException e) {
				errors++;
				// Nothing to do... Only report this error. This will be
				// very hepfull.
			}
		}
	}

	
	private static String generateErrorMessage(CompileException e) {
		char expr[] = e.getExpr();
		int cursor = e.getCursor();
		return String.format("Error sintactico cerca de '%s'",
				showCodeNearError(expr, cursor));
	}

	private static CharSequence showCodeNearError(char[] expr, int cursor) {
		if (expr == null)
			return "???";

		int end = Math.min(cursor + 10, expr.length - 1);
		int start = Math.max(0, end - 20);

		while (start < end && Character.isWhitespace(expr[start]))
			start++;

		CharSequence cs = null;

		try {
			cs = String.copyValueOf(expr, start, end - start);
		} catch (StringIndexOutOfBoundsException e) {
			throw e;
		}

		return cs;
	}

	private static SalaryDraftCalculatorContext<SQLContractSalaryCalculatorContext> getSalaryCalculatorContextImpl(
			final Connection conn, final SalaryDraft draft,
			IContractSalaryCalculatorContext.IListener listener)
					throws ExpressionException, SQLException {

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(tableCol(CONTRACT, ContractColumns.ID),
				draft.getEmployee().getId());

		class SalaryCalculatorContextImpl
				extends SQLContractSalaryCalculatorContext {

			public SalaryCalculatorContextImpl(Connection connection,
					Date startDate, Date endDate, Date issueDate,
					Criteria criteria)
							throws SQLException, ExpressionException {
				super(connection, startDate, endDate, issueDate, criteria);
			}

			@Override
			public Object br(Date date) throws ExpressionException, SQLException, SalaryException {
				
				Date today = resetTime(Calendar.getInstance().getTime());
				Date endDate = AonDateUtils.add(getEndDate(), Calendar.DAY_OF_MONTH,1); // TODO: +1?
				if ( endDate.after(today) )
					return super.br(date);
				
				// delay
				int contractId = getId();
				Optional<Salary> salary = 
						AON.getSalaries(new AONContext(connection),
						p -> p.getIsSalaryProperty().eq(true)
						.and(p.getContractProperty().eq(contractId))
						.and(p.getStartDateProperty().le(getEnd()))
						.and(p.getEndDateProperty().ge(getStart())))
				.findAny();
				
				if ( !salary.isPresent() )
					return super.br(date);
				
				Date contractStart = super.getDate(CONTRACT, ContractColumns.START_DATE);
				if ( contractStart.before(getFirstDayOfMonth(date)))
					date = AonDateUtils.add(date, Calendar.MONTH, -1);
				
				return super.calculateBr(date);
				
			}
			
			@Override
			protected IIrpfCalculatorContext getIrpfCalculatorContext(
					Connection conn, Date startDate, Date endDate,
					Criteria criteria) {
				try {
//					SQLContractSalaryCalculatorContext sqlContractSalaryCalculatorCtx = new SQLContractSalaryCalculatorContext(
//							conn, startDate, endDate, endDate, criteria) {
//
//						@Override
//						public double getIrpf() {
//							return 0.00;
//						}
//
//					};
					
					SQLContractSalaryCalculatorContext sqlContractSalaryCalculatorCtx = getUnderlyingIrpfSQLCalculatorContext(conn, startDate, endDate, criteria);
					
					SQLSalaryDraftCalculatorContext sqlDraftSalaryCalculatorCtx = new SQLSalaryDraftCalculatorContext(
							draft, sqlContractSalaryCalculatorCtx);

					return new SQLIrpfCalculatorContext(conn, startDate,
							endDate, sqlDraftSalaryCalculatorCtx) {
						@Override
						public String getNif() {
							return "87449445H";
						}

						@Override
						public String getApellidosNombre() {
							return "TORVALDS BENEDICT LINUS";
						}

						@Override
						public String getRetenedorNif() {
							return "Z7896423E";
						}

						@Override
						public String getRetenedorApellidosNombre() {
							return "LINUX FOUNDATION";
						}

						@Override
						public int getAñoNacimiento() {
							Date birthDate = SalaryCalculatorContextImpl.this.getDate(SQLConstants.PERSON, SQLConstants.PersonColumns.BIRTH_DATE);
							return birthDate != null ? AonDateUtils.get(birthDate, Calendar.YEAR) : 1969;
						}

					};
				} catch (SQLException e) {
					throw new ExpressionExceptionWrapper(
							new ExpressionException(e));
				} catch (ExpressionException e) {
					throw new ExpressionExceptionWrapper(e);
				}
			}

			@Override
			protected IContractSalaryCalculatorContext getPaymentCalculatorContext(
					Connection conn, Date startDate, Date endDate,
					Date issueDate, Criteria criteria, final double x) {
				try {
					SQLContractSalaryCalculatorContext sqlContractSalaryCalculatorCtx = new SQLContractSalaryCalculatorContext(
							conn, startDate, endDate, issueDate, criteria) {

						@Override
						public double getIrpf() {
							return 0.00;
						}

						@Override
						public Object gross(double liquid, Date start, Date end)
								throws ExpressionException, SQLException {
							return x;
						}

						@Override
						public Object liquid(double liquid, Date start,
								Date end) throws ExpressionException,
										SQLException, SalaryException {
							throw new InterruptedException(String.format(
									"Lo sentimos. La funci\u00F3n BRUTO es incompatible con la funci\u00F3n NETO. Elija una de las dos. :-("));
						}

						@Override
						protected ISQLContractSalaryCalculatorContext getNoItCalculatorContext(
								Connection conn, Date startDate, Date endDate,
								Date issueDate, Criteria criteria, int start,
								int end) {
							ISQLContractSalaryCalculatorContext draftCtx;
							try {
								SQLNoItContractSalaryCalculatorContext sqlCtx = new SQLNoItContractSalaryCalculatorContext(
										conn, startDate, endDate, issueDate,
										criteria, start, end);
								draftCtx = new SQLSalaryDraftCalculatorContext(
										draft, sqlCtx);
								draftCtx.next();
								return draftCtx;
							} catch (ExpressionException e) {
								throw new ExpressionExceptionWrapper(e);
							} catch (SQLException e) {
								throw new ExpressionExceptionWrapper(
										new ExpressionException(e));
							}
						}

						@Override
						public Collection<IContractDeduction> getContractDeductions()
								throws AonException {
							return Collections.emptyList();
						}

						@Override
						public Collection<IContractEmbargo> getContractEmbargos()
								throws AonException {
							return Collections.emptyList();
						}

						@Override
						public Collection<IContractBonus> getContractBonus()
								throws AonException {
							return Collections.emptyList();
						}

						@Override
						public Collection<IContractCost> getContractCosts()
								throws AonException {
							return Collections.emptyList();
						}

						@Override
						protected void onRedefinedImplicit(String name,
								ITimedVariable<?> redefined,
								ITimedVariable<?> implicit) {
						}
					};
					SQLSalaryDraftCalculatorContext sqlDraftSalaryCalculatorCtx = new SQLSalaryDraftCalculatorContext(
							draft, sqlContractSalaryCalculatorCtx);

					sqlDraftSalaryCalculatorCtx.setListener(
							SalaryCalculatorContextImpl.this.getListener());

					sqlDraftSalaryCalculatorCtx.next();
					return sqlDraftSalaryCalculatorCtx;

				} catch (ExpressionException e) {
					throw new ExpressionExceptionWrapper(e);
				} catch (SQLException e) {
					throw new ExpressionExceptionWrapper(
							new ExpressionException(e));
				}
			}

			@Override
			protected IContractSalaryCalculatorContext getLiquidCalculatorContext(
					Connection conn, Date startDate, Date endDate,
					Date issueDate, Criteria criteria, final double solve,
					final double liquid) {
				try {
					SQLContractSalaryCalculatorContext sqlContractSalaryCalculatorCtx = new SQLContractSalaryCalculatorContext(
							conn, startDate, endDate, issueDate, criteria) {

						@Override
						public Object liquid(double liquid, Date start,
								Date end) throws ExpressionException,
										SQLException {
							return solve;
						}

						@Override
						public Object gross(double gross, Date start, Date end)
								throws ExpressionException, SQLException,
								SalaryException {
							throw new InterruptedException(String.format(
									"Lo sentimos. La funci\u00F3n NETO es incompatible con la funci\u00F3n BRUTO. Elija una de las dos. :-("));
						}

						@Override
						protected ISQLContractSalaryCalculatorContext getNoItCalculatorContext(
								Connection conn, Date startDate, Date endDate,
								Date issueDate, Criteria criteria, int start,
								int end) {
							ISQLContractSalaryCalculatorContext draftCtx;
							try {
								SQLNoItContractSalaryCalculatorContext sqlCtx = new SQLNoItContractSalaryCalculatorContext(
										conn, startDate, endDate, issueDate,
										criteria, start, end);
								draftCtx = new SQLSalaryDraftCalculatorContext(
										draft, sqlCtx);
								draftCtx.next();
								return draftCtx;
							} catch (ExpressionException e) {
								throw new ExpressionExceptionWrapper(e);
							} catch (SQLException e) {
								throw new ExpressionExceptionWrapper(
										new ExpressionException(e));
							}
						}

						@Override
						protected IIrpfCalculatorContext getIrpfCalculatorContext(
								Connection conn, Date startDate, Date endDate,
								Criteria criteria) {
							try {
								SQLContractSalaryCalculatorContext sqlContractSalaryCalculatorCtx = new SQLContractSalaryCalculatorContext(
										conn, startDate, endDate, endDate,
										criteria) {

									@Override
									public double getIrpf() {
										return 0.00;
									}

									@Override
									public Object liquid(double _liquid,
											Date start, Date end)
													throws ExpressionException,
													SQLException {
										return solve * (_liquid / liquid);
									}

								};

								SQLSalaryDraftCalculatorContext sqlDraftSalaryCalculatorCtx = new SQLSalaryDraftCalculatorContext(
										draft, sqlContractSalaryCalculatorCtx);

								return new SQLIrpfCalculatorContext(conn,
										startDate, endDate,
										sqlDraftSalaryCalculatorCtx) {
									@Override
									public String getNif() {
										return "87449445H";
									}

									@Override
									public String getApellidosNombre() {
										return "TORVALDS BENEDICT LINUS";
									}

									@Override
									public String getRetenedorNif() {
										return "Z7896423E";
									}

									@Override
									public String getRetenedorApellidosNombre() {
										return "LINUX FOUNDATION";
									}

									@Override
									public int getAñoNacimiento() {
										return 1969;
									};

								};
							} catch (SQLException e) {
								throw new ExpressionExceptionWrapper(
										new ExpressionException(e));
							} catch (ExpressionException e) {
								throw new ExpressionExceptionWrapper(e);
							}
						}

						@Override
						protected void onRedefinedImplicit(String name,
								ITimedVariable<?> redefined,
								ITimedVariable<?> implicit) {
						}

					};
					SQLSalaryDraftCalculatorContext sqlDraftSalaryCalculatorCtx = new SQLSalaryDraftCalculatorContext(
							draft, sqlContractSalaryCalculatorCtx);

					sqlDraftSalaryCalculatorCtx.setListener(
							SalaryCalculatorContextImpl.this.getListener());

					sqlDraftSalaryCalculatorCtx.next();
					return sqlDraftSalaryCalculatorCtx;

				} catch (ExpressionException e) {
					throw new ExpressionExceptionWrapper(e);
				} catch (SQLException e) {
					throw new ExpressionExceptionWrapper(
							new ExpressionException(e));
				}
			}

			@Override
			protected ISQLContractSalaryCalculatorContext getNoItCalculatorContext(
					Connection conn, Date startDate, Date endDate,
					Date issueDate, Criteria criteria, int start, int end) {

				ISQLContractSalaryCalculatorContext draftCtx;
				try {
					SQLNoItContractSalaryCalculatorContext sqlCtx = new SQLNoItContractSalaryCalculatorContext(
							conn, startDate, endDate, issueDate, criteria,
							start, end);
					draftCtx = new SQLSalaryDraftCalculatorContext(draft,
							sqlCtx);
					draftCtx.next();
					return draftCtx;
				} catch (ExpressionException e) {
					throw new ExpressionExceptionWrapper(e);
				} catch (SQLException e) {
					throw new ExpressionExceptionWrapper(
							new ExpressionException(e));
				}

			}

		}

		SalaryCalculatorContextImpl ctx = new SalaryCalculatorContextImpl(conn,
				draft.getStartDate(), draft.getEndDate(), draft.getIssueDate(),
				criteria);

		SalaryDraftCalculatorContext<SQLContractSalaryCalculatorContext> draftCtx = new SalaryDraftCalculatorContext<SQLContractSalaryCalculatorContext>(
				draft, ctx);

		draftCtx.setListener(listener);
		draftCtx.next();

		return draftCtx;
	}

	private static String tableCol(String table, String col) {
		return String.format("%1$s.%2$s", table, col);
	}

	public static SalaryDraftCalculatorContext<SQLContractSalaryCalculatorContext> getExtraCalculatorContextImpl(
			final Connection conn, final SalaryDraft draft,
			IContractSalaryCalculatorContext.IListener listener)
					throws ExpressionException, SQLException {

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				EmployeesServiceImpl.tableCol(CONTRACT, ContractColumns.ID),
				draft.getEmployee().getId());

		SQLContractSalaryCalculatorContext ctx = new SQLContractExtraCalculatorContext(
				conn, draft.getStartDate(), draft.getEndDate(),
				draft.getIssueDate(), draft.getIssueDate(), criteria);


		SalaryDraftCalculatorContext<SQLContractSalaryCalculatorContext> draftCtx = new SalaryDraftCalculatorContext<SQLContractSalaryCalculatorContext>(
				draft, ctx) {
			@Override
			protected Collection<IContractPayment> getDraftPayments() {
				return Collections.emptyList();
			}
			
		};
		

		draftCtx.setListener(listener);
		draftCtx.next();
		
		return draftCtx;
	}

	public static SalaryDraftCalculatorContext<SQLContractSalaryCalculatorContext> getSettleCalculatorContextImpl(
			final Connection conn, final SalaryDraft draft,
			IContractSalaryCalculatorContext.IListener listener)
			throws ExpressionException, SQLException {

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(tableCol(CONTRACT, ContractColumns.ID),
				draft.getEmployee().getId());

		SQLSettleDraftCalculatorContext draftCtx = new SQLSettleDraftCalculatorContext(
				draft, conn, draft.getStartDate(), draft.getEndDate(),
				draft.getIssueDate(), criteria);
		draftCtx.next();
		draftCtx.setListener(listener);
		return draftCtx;
	}

	public static String getUserScript(String script) {
		
		if (StringUtils.isBlank(script))
			return script;
		
		return script.replaceAll("\"/\\*user\\*/(.*)/\\*\\*/\"", "$1");
		
	}

	
}
