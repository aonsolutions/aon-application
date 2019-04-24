package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Account.ACCOUNT;

import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.jooq.Condition;
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountBalanceLineStyle;
import com.esferalia.aon.occam.api.model.AccountBalanceReport;
import com.esferalia.aon.occam.api.model.AccountBalanceReport.BalanceLine;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.DateInterval;
import com.esferalia.aon.occam.api.model.accounting.AccMiningParameters;
import com.esferalia.aon.occam.api.model.accounting.AccountBalance;
import com.esferalia.aon.occam.api.model.accounting.BalanceType.IBalanceTypeVisitor;
import com.esferalia.aon.occam.server.accounting.AccMiningMVELContext;
import com.esferalia.aon.occam.server.accounting.BOEBalanceAbbreviateScript;
import com.esferalia.aon.occam.server.accounting.BOEBalanceCoopNormalScript;
import com.esferalia.aon.occam.server.accounting.BOEBalanceNormalScript;
import com.esferalia.aon.occam.server.accounting.BOEBalancePYMESScript;
import com.esferalia.aon.occam.server.accounting.BOEPyGAbbreviateScript;
import com.esferalia.aon.occam.server.accounting.BOEPyGCoopAbbreviateScript;
import com.esferalia.aon.occam.server.accounting.BOEPyGCoopNormalScript;
import com.esferalia.aon.occam.server.accounting.BOEPyGNormalScript;
import com.esferalia.aon.occam.server.accounting.BOEPyGPYMESScript;
import com.esferalia.aon.occam.server.accounting.BalanceScript;
import com.esferalia.aon.occam.server.accounting.IBalanceKey;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class AccountBalanceDAO {

	private static class BalanceTypeVisitor implements IBalanceTypeVisitor {

		private static final long serialVersionUID = 1L;

		private BalanceScript script;
		
		public BalanceScript getScript() {
			return script;
		}
		
		@Override public void visitBalanceNormal() 		{ this.script = new BOEBalanceNormalScript();}
		@Override public void visitBalanceAbbreviate() 	{ this.script = new BOEBalanceAbbreviateScript();}
		@Override public void visitBalancePymes() 		{ this.script = new BOEBalancePYMESScript(); }
		
		@Override public void visitBalanceCoopNormal() 	{ this.script = new BOEBalanceCoopNormalScript(); }
		@Override public void visitPygCoopNormal() 		{ this.script = new BOEPyGCoopNormalScript(); }

		@Override public void visitPygNormal()			{ this.script = new BOEPyGNormalScript(); }
		@Override public void visitPygAbbreviate() 		{ this.script = new BOEPyGAbbreviateScript(); }
		@Override public void visitPygPymes() 			{ this.script = new BOEPyGPYMESScript(); }

		@Override public void visitPygCoopAbbreviate() 	{ this.script = new BOEPyGCoopAbbreviateScript(); }
		@Override public void visitBalanceCoopAbbreviate() { }

	}

	public static AccountBalanceReport balanceReport(AONContext ctx, AccountingReportParams params) {
		BalanceTypeVisitor visitor = new BalanceTypeVisitor();
		params.getBalanceType().visit(visitor);
		BalanceScript script = visitor.getScript();
		if (script == null)
			throw new AonCoreException("No se ha indicado un tipo de balance adecuado");
		AccountBalanceReport report = balanceReport(ctx, params, script);
		return report;
	}

	private static AccountBalanceReport balanceReport(AONContext ctx, AccountingReportParams params, BalanceScript script) {
		AccountBalanceReport report = new AccountBalanceReport();
		report.setParams(params);
		report.setSelectedPeriod(AccountPeriodDAO.getPeriod(ctx, params.getPeriod()));
		if (report.getSelectedPeriod() == null) {
			throw new AonCoreException("No se ha indicado ejercicio contable");
		}
		if (params.getFromDate() != null
				&& params.getFromDate().before(report.getSelectedPeriod().getInitiationDate())) {
			throw new AonCoreException("La fecha desde indicada es anterior al inicio del ejercicio");
		}
		if (params.getToDate() != null && params.getToDate().after(report.getSelectedPeriod().getDeadline())) {
			throw new AonCoreException("La fecha hasta indicada es posterior al final del ejercicio");
		}
		if (params.getActivity() != null) {
			report.setSelectedActivity(CompanyDAO.getEnterpriseActivity(ctx, params.getActivity()));
		}
		LinkedHashMap<DateInterval, AccountingReportParams> intervals = getDateIntervals(ctx, params);
		for (DateInterval inter : intervals.keySet()) {
			fillReport(ctx, intervals.get(inter), script, report, inter.getName());
		}

		return report;
	}

	private static LinkedHashMap<DateInterval, AccountingReportParams> getDateIntervals(AONContext ctx,
			AccountingReportParams params) {
		LinkedList<AccountPeriod> periods = AccountPeriodDAO
				.getPeriods(ctx, p -> p.getDomainProperty().eq(ctx.getDomainId()))
				.collect(Collectors.toCollection(LinkedList::new));
		LinkedHashMap<DateInterval, AccountingReportParams> map = new LinkedHashMap<DateInterval, AccountingReportParams>();
		for (AccountPeriod ap : periods) {
			if (AonNumberUtils.equals(ap.getId(), params.getPeriod())) {
				DateInterval inter = new DateInterval().setStart(params.getFromDate()).setEnd(params.getToDate())
						.setName(ap.getName());
				map.put(inter, params);
			} else if (map.size() > 0 && params.getPreviousPeriods() >= map.size()) {
				AccountingReportParams cloned = params.clone();
				cloned.setPeriod(ap.getId());
				cloned.setFromDate(AonDateUtils.add(params.getFromDate(), Calendar.YEAR, (map.size() * (-1))));
				cloned.setToDate(AonDateUtils.add(params.getToDate(), Calendar.YEAR, (map.size() * (-1))));
				DateInterval inter = new DateInterval().setStart(cloned.getFromDate()).setEnd(cloned.getToDate())
						.setName(ap.getName());
				map.put(inter, cloned);
			}
		}
		if (map.size() == 0) {
			throw new AonCoreException("Ejercicio contable no encontrado");
		}
		return map;
	}

	private static void fillReport(AONContext ctx, AccountingReportParams params, BalanceScript script, AccountBalanceReport report, String bal) {
		AccMiningMVELContext mvelCtx = new AccMiningMVELContext(script.getAccepter());
		LinkedHashMap<String, String> initialMap = new LinkedHashMap<String, String>();
		LinkedHashMap<String, String> computeMap = new LinkedHashMap<String, String>();

		AccMiningParameters mParams = new AccMiningParameters();
		mParams.setDomain(ctx.getDomainId());
		mParams.setStartDate(params.getFromDate());
		mParams.setEndDate(params.getToDate());
		if (params.isConsolidation()) {
			Integer[] domains = new Integer[params.getDomains().size()];
			for (int i = 0; i < params.getDomains().size();i++) {
				domains[i] = params.getDomains().get(i).getId();
			}
			mParams.setDomains(domains);
		}
		
		Map<String, AccountBalance> accounts = ACCOUNTING.getAccountBalances(ctx, mParams, params.getBalanceType().isPyG());
		mvelCtx.setAccounts(accounts);
		for (IBalanceKey key : script.getKeyList() ) {
			if (!report.getBalances().containsKey(key.getCode())) {
				String expressionParsed = parseExpression(key.getInitialExpression());
				report.getBalances().put(key.getCode(),
						new BalanceLine()
							.setLevel(key.getLevel())
							.setPrefix(key.getPrefix())
							.setCode(key.getCode())
							.setDescription(key.getName())
							.setType(key.getType())
							.setAccounts(expressionParsed))
				;
				if (params.isBreakdownEnabled() && AonStringUtils.isNotBlank( expressionParsed )) {
					String[] tokens = AonStringUtils.split(expressionParsed,'|');
					if (tokens != null && tokens.length > 0) {
						Condition c = null;
						for (String token : tokens ) {
							Condition c1 = ACCOUNT.CODE.like(token + "%");
							c = c==null?c1:c.or(c1);
						}
						c = c.and(DSL.length(ACCOUNT.CODE).eq(4));
						AccountDAO.getAccounts(ctx, c)
							.forEach(account -> {
								AccountBalance b = accounts.get(account.getCode());
								if (b != null) {
									report.getBalances().put( ("*"+account.getCode()) ,
											new BalanceLine().setLevel(5)
											.setPrefix(account.getCode())
											.setCode(account.getCode())
											.setDescription(account.getDescription())
											.setType(AccountBalanceLineStyle.BREAKDOWN)
											.setAccounts(account.getCode())
											.setBreakdown( b )
											)
									;
								}
							});
					}
				}
				mvelCtx.put(key.getCode(), 0.0);
			}
			String exp = key.getInitialExpression();
			if (AonStringUtils.isNotBlank(exp)) {
				initialMap.put(key.getCode(), exp);
			}
			String computeExp = key.getComputeExpression();
			if (AonStringUtils.isNotBlank(computeExp)) {
				computeMap.put(key.getCode(), computeExp);
			}
			
		}
		mvelCtx.setExpressionMap(initialMap);
		for (String keyCode : mvelCtx.getExpressionMap().keySet()) {
			String initialExp = mvelCtx.getExpressionMap().get(keyCode);
			mvelCtx.put(keyCode, 0.0);
			if (AonStringUtils.isNotBlank(initialExp)) {
				Object ret = mvelCtx.evaluateExpression(keyCode, initialExp);
				mvelCtx.put(keyCode, ret);
				report.setAmount(keyCode, bal, AonNumberUtils.todouble(ret));
			}
		}

		// Se chequean las cuentas que no se han tenido en cuenta, para facilitar al
		// cliene la búsqueda del descuadre.
		LinkedList<AccountBalance> unreadBalances = new LinkedList<AccountBalance>();
		for (String code : mvelCtx.getAccounts().keySet()) {
			AccountBalance accountBalance = mvelCtx.getAccounts().get(code);
			if (!accountBalance.isChecked()) {
				if (AonStringUtils.length(code) == 4
						&& !mvelCtx.getAccounts().get(AonStringUtils.substring(code, 0, 3)).isChecked()
						&& !mvelCtx.getAccounts().get(AonStringUtils.substring(code, 0, 2)).isChecked()
						&& !mvelCtx.getAccounts().get(AonStringUtils.substring(code, 0, 1)).isChecked()) {
					Account account = AccountDAO.get(ctx, code);
					accountBalance.setAccountDescription(account != null ? account.getDescription() : null);
					accountBalance.setAccountCode(code);
					unreadBalances.add(accountBalance);
				}
			}
		}
		report.getUnreadAccounts().put(bal, unreadBalances);
		/// [fin chequeo]

		mvelCtx.getExpressionMap().clear();
		mvelCtx.setExpressionMap(computeMap);
		for (String keyCode : mvelCtx.getExpressionMap().keySet()) {
			String exp = mvelCtx.getExpressionMap().get(keyCode);
			if (AonStringUtils.isNotBlank(exp)) {
				Object ret = mvelCtx.evaluateExpression(keyCode, exp);
				mvelCtx.put(keyCode, ret);
				report.setAmount(keyCode, bal, AonNumberUtils.todouble(ret));
			}
		}
	}

	private static String parseExpression(String initialExpression) {
		if (AonStringUtils.isBlank(initialExpression))
			return null;
		Pattern p = Pattern.compile("-?\\d+");
		Matcher m = p.matcher(initialExpression);
		StringBuffer buf = new StringBuffer();
		while (m.find()) {
			if (buf.length() > 0) {
				buf.append('|');
			}
			buf.append(m.group());
		}
		return buf.toString();
	}
}
