package com.esferalia.aon.gwt.payroll.server;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.function.Supplier;

import org.apache.commons.lang.StringUtils;
import org.mvel2.CompileException;

import com.esferalia.aon.gwt.payroll.shared.AgreementDraft;
import com.esferalia.aon.gwt.payroll.shared.Extra;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.esferalia.aon.gwt.payroll.shared.StringVariable;
import com.esferalia.aon.gwt.payroll.shared.Variable;
import com.esferalia.aon.gwt.payroll.shared.AgreementDraft.Level;
import com.esferalia.aon.gwt.payroll.shared.AgreementDraft.SalaryTable;
import com.esferalia.aon.gwt.payroll.sql.SQLAgreementDraft;
import com.esferalia.aon.payroll.calculator.sql.ISQLContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.sql.SQLAgreementContextFactory;
import com.esferalia.aon.payroll.calculator.sql.SQLContractSalaryCalculatorContext.AgreementContextKey;
import com.esferalia.aon.payroll.calculator.sql.SQLContractSalaryCalculatorContext.CCCContextKey;
import com.esferalia.aon.payroll.calculator.sql.SQLSystemExpressionContextFactory;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.salary.expression.ExpressionContext;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ITimedResult;
import com.esferalia.aon.salary.expression.UndefinedVariablesException;

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
		SortedSet<Date> datesWithChanges = parentDomainId == null ? SQLAgreementDraft
				.getDatesWithChanges(connection, draft.getId(), domainId)
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

			try {
				variables.addAll(ExpressionContext.getVariableSet(
						payment.getExpression(), payment.getIrpfExpression(),
						payment.getQuoteExpression()));
			} catch (Exception e) {
				// TODO:

			}

			paymentsNames.add(payment.getName());

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
		}
		variables.removeAll(privateVariables);

		/*
		 * Clean system variables. Set<String> systemVars =
		 * getSystemVariables(connection, draft.getStartDate(),
		 * draft.getEndDate()); variables.removeAll(systemVars);
		 */

		Set<Level> dbLevels = SQLAgreementDraft.getLevels(connection,
				draft.getId());

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
				.getCategories(connection, draft.getId());

		Map<Integer, Set<String>> allCategories = new HashMap<Integer, Set<String>>(
				dbCategories);

		Map<Integer, Set<String>> draftCategories = draft.getDraftCategories();
		allCategories.putAll(draftCategories);

		Level agreementData = new Level();
		agreementData.setId(0);
		allLevels.add(agreementData);

		SalaryTable dbSalaryTable = SQLAgreementDraft.getSalaryTable(
				connection, draft.getId(), draft.getStartDate(),
				draft.getEndDate());
		SalaryTable allSalaryTable = new SalaryTable(dbSalaryTable);
		allSalaryTable.putAll(draft.getDraftSalaryTable());

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

		eval(connection, draft.getId(), allLevels, allSalaryTable,
				draft.getStartDate(), draft.getEndDate(),
				domainIds.toArray(new Integer[] {}));
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

		return parents
				.stream()
				.filter(parent -> parent.getConceptId().equals(
						payment.getConceptId())
						&& parent.getDomain().equals(payment.getDomain()))
				.findAny().isPresent();
	}

	private static void eval(Connection conn, int agreementId,
			Set<Level> levels, SalaryTable salaryTable, Date start, Date end,
			Integer... domainIds) {
		try {
			// try to resolve some variables. Here we go.
			SQLSystemExpressionContextFactory systemCtxFactory = new SQLSystemExpressionContextFactory(
					conn, start, end, ISQLContractSalaryCalculatorContext.NEWER);

			Supplier<ExpressionContext> systemCtxSupplier = () -> systemCtxFactory
					.create(new CCCContextKey(null, null));

			SQLAgreementContextFactory agreementCtxFactory = new SQLAgreementContextFactory(
					conn, systemCtxSupplier, start, end,
					ISQLContractSalaryCalculatorContext.NEWER);

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
		} catch (Throwable e) {
			// e.printStackTrace();
		} 
	}

	private static void eval(ExpressionContext ctx, LinkedList<Variable> vars,
			Date start, Date end) {
		int errors = 0;
		while (errors < vars.size()) {
			Variable var = vars.pop();
			try {
				List<ITimedResult<Object>> results = ctx.eval(
						var.getExpression(), start, end);
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

}
