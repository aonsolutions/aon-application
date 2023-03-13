package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Account.ACCOUNT;

import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
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
import com.esferalia.aon.occam.server.accounting.BOEBalanceAsocAbbreviateScript;
import com.esferalia.aon.occam.server.accounting.BOEBalanceCoopNormalScript;
import com.esferalia.aon.occam.server.accounting.BOEBalanceNormalScript;
import com.esferalia.aon.occam.server.accounting.BOEBalancePYMESScript;
import com.esferalia.aon.occam.server.accounting.BOEPyGAbbreviateScript;
import com.esferalia.aon.occam.server.accounting.BOEPyGAsocAbbreviateScript;
import com.esferalia.aon.occam.server.accounting.BOEPyGCoopAbbreviateScript;
import com.esferalia.aon.occam.server.accounting.BOEPyGCoopNormalScript;
import com.esferalia.aon.occam.server.accounting.BOEPyGNormalScript;
import com.esferalia.aon.occam.server.accounting.BOEPyGPYMESScript;
import com.esferalia.aon.occam.server.accounting.BalanceScript;
import com.esferalia.aon.occam.server.accounting.IBalanceKey;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class AccountBalanceDAO {
	
	
	private AccountBalanceDAO() {
		
	}

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
		@Override public void visitBalanceCoopAbbreviate() { 
			// Cuando se haga activar el test en BalanceScriptTest
		}
		@Override public void visitBalanceAsocAbbreviate(){ this.script = new BOEBalanceAsocAbbreviateScript(); }
		
		@Override public void visitPygNormal()			{ this.script = new BOEPyGNormalScript(); }
		@Override public void visitPygAbbreviate() 		{ this.script = new BOEPyGAbbreviateScript(); }
		@Override public void visitPygPymes() 			{ this.script = new BOEPyGPYMESScript(); }
		@Override public void visitPygCoopNormal() 		{ this.script = new BOEPyGCoopNormalScript(); }
		@Override public void visitPygCoopAbbreviate() 	{ this.script = new BOEPyGCoopAbbreviateScript(); }
		@Override public void visitPygAsocAbbreviate() 	{ this.script = new BOEPyGAsocAbbreviateScript(); }

	}

	public static AccountBalanceReport balanceReport(AONContext ctx, AccountingReportParams params) {
		BalanceTypeVisitor visitor = new BalanceTypeVisitor();
		params.getBalanceType().visit(visitor);
		BalanceScript script = visitor.getScript();
		if (script == null)
			throw new AonCoreException("No se ha indicado un tipo de balance adecuado");
		return balanceReport(ctx, params, script);
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
		intervals.entrySet()
			.stream()
			.forEach(entry -> fillReport(ctx, entry.getValue(), script, report, entry.getKey().getName()));
		return report;
	}

	private static LinkedHashMap<DateInterval, AccountingReportParams> getDateIntervals(AONContext ctx, AccountingReportParams params) {
		LinkedList<AccountPeriod> periods = AccountPeriodDAO
				.getPeriods(ctx, p -> p.getDomainProperty().eq(ctx.getDomainId()))
				.collect(Collectors.toCollection(LinkedList::new));
		LinkedHashMap<DateInterval, AccountingReportParams> map = new LinkedHashMap<>();
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
	
	private static AccMiningParameters getAccMiningParameters(AONContext ctx, AccountingReportParams params) {
		AccMiningParameters mParams = new AccMiningParameters();
		mParams.setDomain(ctx.getDomainId());
		mParams.setStartDate(params.getFromDate());
		mParams.setEndDate(params.getToDate());
		mParams.setSecurityLevel(params.getSecurityLevel());
		if (params.isConsolidation()) {
			Integer[] domains = new Integer[params.getDomains().size()];
			for (int i = 0; i < params.getDomains().size();i++) {
				domains[i] = params.getDomains().get(i).getId();
			}
			mParams.setDomains(domains);
		}
		return mParams;
	}
	
	private static void fillReport(AONContext ctx, AccountingReportParams params, BalanceScript script, AccountBalanceReport report, String bal) {
		AccMiningMVELContext mvelCtx = new AccMiningMVELContext(script.getAccepter());
		LinkedHashMap<String, String> initialMap = new LinkedHashMap<>();
		LinkedHashMap<String, String> computeMap = new LinkedHashMap<>();

		mvelCtx.setAccounts( ACCOUNTING.getAccountBalances(ctx, getAccMiningParameters(ctx, params), params.getBalanceType().isPyG()) );
		for (IBalanceKey key : script.getKeyList() ) {
			if (!report.getBalances().containsKey(key.getCode())) {
				String expressionParsed = parseExpression(key.getInitialExpression());
				report.getBalances().put(key.getCode(), new BalanceLine()
					.setLevel(key.getLevel())
					.setPrefix(key.getPrefix())
					.setCode(key.getCode())
					.setDescription(key.getName())
					.setType(key.getType())
					.setAccounts(expressionParsed));
				
				if (params.isBreakdownEnabled()) {
					fillBreakDownIfRequested(ctx,report,mvelCtx,key,expressionParsed);
				}
				
				mvelCtx.put(key.getCode(), 0.0);
			}
			if (AonStringUtils.isNotBlank(key.getInitialExpression())) {
				initialMap.put(key.getCode(), key.getInitialExpression());
			}
			if (AonStringUtils.isNotBlank(key.getComputeExpression())) {
				computeMap.put(key.getCode(), key.getComputeExpression());
			}
		}
		resolveInitialMap( report, bal, initialMap, mvelCtx);
		report.getUnreadAccounts().put(bal, getUnreadAccounts( ctx, mvelCtx ));
		report.setHelpLink(params.getBalanceType().getHelpLink());
		resolveComputeMap( report, bal, computeMap, mvelCtx);
	}

	private static void resolveInitialMap(AccountBalanceReport report, String bal, LinkedHashMap<String, String> initialMap, AccMiningMVELContext mvelCtx) {
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
	}

	private static void resolveComputeMap(AccountBalanceReport report, String bal, LinkedHashMap<String, String> computeMap, AccMiningMVELContext mvelCtx) {
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

	private static void fillBreakDownIfRequested(AONContext ctx, AccountBalanceReport report,  AccMiningMVELContext mvelCtx, IBalanceKey key, String expressionParsed) {
		if (AonStringUtils.isBlank( expressionParsed )) return;

		String[] tokens = AonStringUtils.split(expressionParsed,'|');
		if (tokens != null && tokens.length > 0) {
			Condition c = null;
			List<String> saPositivoTokens = new LinkedList<>(); 
			List<String> sdPositivoTokens = new LinkedList<>();
			for (String token : tokens ) {
				if (isSaPositivo(token, key.getInitialExpression() )) {
					saPositivoTokens.add(token);		
				}
				if (isSdPositivo(token, key.getInitialExpression() )) {
					sdPositivoTokens.add(token);		
				}
				Condition c1 = ACCOUNT.CODE.like(token + "%");
				c = c==null?c1:c.or(c1);
			}
			Condition c2 = DSL.length(ACCOUNT.CODE).eq(4);
			c = c==null?c2:c.and(c2);
			AccountDAO.getAccounts(ctx, c)
				.forEach(account -> {
					AccountBalance b = mvelCtx.getAccounts().get(account.getCode());
					if (b != null) {
						boolean saPositivo = saPositivoTokens.stream().anyMatch(token -> AonStringUtils.startsWith(account.getCode(),token));
						boolean sdPositivo = sdPositivoTokens.stream().anyMatch(token -> AonStringUtils.startsWith(account.getCode(),token));
						boolean mustAdd = !saPositivo && !sdPositivo;
						if (!mustAdd && saPositivo && AonMathUtils.isGreatherThanZero( b.getCreditBalance() )) {
							mustAdd = true;
						}
						if (!mustAdd && sdPositivo && AonMathUtils.isGreatherThanZero( b.getDebitBalance() )) {
							mustAdd = true;
						}
						if (mustAdd) {
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
					}
				});
		}
	}

	private static LinkedList<AccountBalance> getUnreadAccounts(AONContext ctx, AccMiningMVELContext mvelCtx) {
		// Se chequean las cuentas que no se han tenido en cuenta, para facilitar al
		// cliene la búsqueda del descuadre.
		LinkedList<AccountBalance> unreadBalances = new LinkedList<>();
		for (String code : mvelCtx.getAccounts().keySet()) {
			AccountBalance accountBalance = mvelCtx.getAccounts().get(code);
			if (!accountBalance.isChecked() &&  (AonStringUtils.length(code) == 4
				&& !mvelCtx.getAccounts().get(AonStringUtils.substring(code, 0, 3)).isChecked()
				&& !mvelCtx.getAccounts().get(AonStringUtils.substring(code, 0, 2)).isChecked()
				&& !mvelCtx.getAccounts().get(AonStringUtils.substring(code, 0, 1)).isChecked())) {
				
				Account account = AccountDAO.get(ctx, code);
				accountBalance.setAccountDescription(account != null ? account.getDescription() : null);
				accountBalance.setAccountCode(code);
				unreadBalances.add(accountBalance);
			}
		}
		return unreadBalances;
	}

	private static String parseExpression(String initialExpression) {
		if (AonStringUtils.isBlank(initialExpression)) return null;
		Pattern p = Pattern.compile("-?\\d+");
		Matcher m = p.matcher(initialExpression);
		StringBuilder buf = new StringBuilder();
		while (m.find()) {
			if (buf.length() > 0) {
				buf.append('|');
			}
			buf.append(m.group());
		}
		return buf.toString();
	}
	
	private static boolean isSaPositivo( String account, String expression ) {
		String pat = ".sa[b|p]Positivo\\(\\[?."+ account +".\\]?\\).";
		Pattern pattern = Pattern.compile( pat, Pattern.MULTILINE|Pattern.CASE_INSENSITIVE);
		return pattern.matcher(expression).find();
	}
	private static boolean isSdPositivo( String account, String expression ) {
		String pat = ".sd[b|p]Positivo\\(\\[?."+ account +".\\]?\\).";
		Pattern pattern = Pattern.compile( pat, Pattern.MULTILINE|Pattern.CASE_INSENSITIVE);
		return pattern.matcher(expression).find();
	}
}
