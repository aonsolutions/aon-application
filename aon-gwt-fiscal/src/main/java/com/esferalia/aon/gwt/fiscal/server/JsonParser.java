package com.esferalia.aon.gwt.fiscal.server;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;

import org.jooq.tools.json.JSONArray;
import org.jooq.tools.json.JSONObject;
import org.jooq.tools.json.JSONParser;
import org.jooq.tools.json.ParseException;

import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountEntryParams;
import com.esferalia.aon.occam.api.model.AccountParams;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainParams;
import com.esferalia.aon.occam.api.model.FinanceParams;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.accounting.BalanceType;
import com.esferalia.aon.occam.api.model.fiscal.IRPFParams;
import com.esferalia.aon.occam.api.model.fiscal.IRPFParamsGroupedBy;
import com.esferalia.aon.occam.api.model.fiscal.IRPFParamsOrderBy;
import com.esferalia.aon.occam.api.model.fiscal.OperationParams;
import com.esferalia.aon.occam.api.model.fiscal.VatSummaryType;
import com.esferalia.aon.occam.api.model.fiscal.aeat.AEATParams;
import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.esferalia.aon.occam.api.model.type.RectificationType;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.occam.api.model.type.WithholdingType;
import com.esferalia.aon.occam.api.model.type.WithholdingTypeGroup;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class JsonParser {
	
	private static final String ENCODING = "utf-8";
	private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("dd/MM/yyyy");
	
	private JsonParser() {
		
	}
	
	public static AEATParams parseAEATParams(String aeatParams) throws ParseException {
		AEATParams params = new AEATParams();
		JSONParser parser = new JSONParser();
		JSONObject jsonParams =  (JSONObject) parser.parse(aeatParams);

		String domainName = (String) jsonParams.get(IRequestParamsNames.DOMAIN_NAME);
		params.setDomainName(domainName);
		
		Long domain = (Long) jsonParams.get(IRequestParamsNames.DOMAIN_ID);
		params.setDomainId( domain.intValue());
		
		String user = (String) jsonParams.get(IRequestParamsNames.USER);
		params.setUser(user);
		
		Long mod = (Long) jsonParams.get(IRequestParamsNames.MOD);
		params.setMod(mod==null?null:mod.intValue());

		Long certificateId = (Long) jsonParams.get(IRequestParamsNames.CERTIFICATE_ID);
		params.setCertificateId(certificateId==null?null:certificateId.intValue());

		String pass = (String) jsonParams.get(IRequestParamsNames.PASS);
		if (AonStringUtils.isNotBlank(pass)) {
			params.setPass(pass);			
		}
		
		String name = (String) jsonParams.get(IRequestParamsNames.NAME);
		if (AonStringUtils.isNotBlank(name)) {
			params.setName(name);			
		}

		String document = (String) jsonParams.get(IRequestParamsNames.DOCUMENT);
		if (AonStringUtils.isNotBlank(document)) {
			params.setDocument(document);			
		}

		String nrc = (String) jsonParams.get(IRequestParamsNames.NRC);
		if (AonStringUtils.isNotBlank(nrc)) {
			params.setNrc(nrc);			
		}
		
		Long test = (Long) jsonParams.get(IRequestParamsNames.TEST);
		if (test != null) {
			params.setTest(test==1);
		}
		
		JSONArray jsonSelected = (JSONArray) jsonParams.get(IRequestParamsNames.SELECTED);
		if (jsonSelected != null && jsonSelected.size() > 0) {
			ArrayList<String> selected = new ArrayList<>();
			for (int i = 0; i < jsonSelected.size(); i++) {
				selected.add(jsonSelected.get(i).toString());
			}
			params.setSelected(selected);
		}
		
		return params;
	}

	public static AccountParams parseAccountParams(String accountParams) throws ParseException {
		AccountParams params = new AccountParams();
		JSONParser parser = new JSONParser();
		JSONObject jsonParams =  (JSONObject) parser.parse(accountParams);
		
		String domainName = (String) jsonParams.get(IRequestParamsNames.DOMAIN_NAME);
		params.setDomainName(domainName);
		
		Long domain = (Long) jsonParams.get(IRequestParamsNames.DOMAIN);
		params.setDomain(domain.intValue());
		
		String user = (String) jsonParams.get(IRequestParamsNames.USER);
		params.setUser(user);
		// *******************  ACCOUNT ******************* 
		Long account = (Long) jsonParams.get(IRequestParamsNames.ACCOUNT);
		if (account != null) {
			params.setId(account.intValue());	
		}
		// *******************  CODE ******************* 
		String code = (String) jsonParams.get(IRequestParamsNames.ACCOUNT_CODE);
		if (AonStringUtils.isNotBlank(code)) {
			params.setCode(code);			
		}
		// *******************  DESCRIPTION ******************* 
		String description = (String) jsonParams.get(IRequestParamsNames.ACCOUNT_DESCRIPTION);
		if (AonStringUtils.isNotBlank(description)) {
			params.setDescription(description);			
		}
		// *******************  ALIAS ******************* 
		String alias = (String) jsonParams.get(IRequestParamsNames.ACCOUNT_ALIAS);
		if (AonStringUtils.isNotBlank(alias)) {
			params.setDescription(alias);			
		}
		// *******************  ACTIVE ******************* 
		Long active = (Long) jsonParams.get(IRequestParamsNames.ACCOUNT_ACTIVE);
		if (active != null) {
			params.setActive(active==1);
		}
		// ******************* LEVEL ******************* 
		Long level = (Long) jsonParams.get(IRequestParamsNames.LEVEL);
		if (level!= null) {
			params.setLevel(level.byteValue());	
		}
		// *******************  DESCRIPTION ******************* 
		String costCenter = (String) jsonParams.get(IRequestParamsNames.COST_CENTER);
		if (AonStringUtils.isNotBlank(costCenter)) {
			params.setCostCenter(costCenter);			
		}
		// ******************* PAGE OFFSET ******************* 
		Long pageOffset = (Long) jsonParams.get(IRequestParamsNames.OFFSET);
		if (pageOffset!= null) {
			params.setOffset(pageOffset.intValue());	
		}
		// ******************* LIMIT ******************* 
		Long limit = (Long) jsonParams.get(IRequestParamsNames.LIMIT);
		if (limit!= null) {
			params.setLimit(limit.intValue());	
		}
		return params;
	}

	public static AccountEntryParams parse(String accountEntryParams) throws ParseException, java.text.ParseException {
		AccountEntryParams params = new AccountEntryParams();
		JSONParser parser = new JSONParser();
		JSONObject jsonParams =  (JSONObject) parser.parse(accountEntryParams);
		
		Object dom = jsonParams.get(IRequestParamsNames.DOMAIN);
		if (dom == null) {
			throw new IllegalArgumentException("NULL DOMAIN!");
		}
		if (dom instanceof Long) {
			Long domain = (Long) dom; 
			params.setDomain(domain.intValue());
		} else if (dom instanceof String) {
			Integer domain = AonNumberUtils.toInteger((String) dom);
			if (domain == null) {
				throw new IllegalArgumentException("NULL DOMAIN!");
			}
			params.setDomain(domain);
		}
		
		Long accountEntryId = (Long) jsonParams.get(IRequestParamsNames.ACCOUNT_ENTRY_ID);
		if (accountEntryId!= null) {
			params.setAccountEntryId(accountEntryId.intValue());	
		}
		Long period = (Long) jsonParams.get(IRequestParamsNames.PERIOD);
		if (period != null) {
			params.setPeriod(period.intValue());	
		}
		String fromDate = (String) jsonParams.get(IRequestParamsNames.FROM_DATE);
		if (AonStringUtils.isNotBlank(fromDate)) {
			params.setFromDate( FORMATTER.parse(fromDate));			
		}
		String toDate = (String) jsonParams.get(IRequestParamsNames.TO_DATE);
		if (AonStringUtils.isNotBlank(toDate)) {
			params.setToDate( FORMATTER.parse(toDate));			
		}
		Long type = (Long) jsonParams.get(IRequestParamsNames.TYPE);
		if (type!= null) {
			params.setType( AccountEntryType.safeValueOf(type.intValue()));
		}
		Long journal = (Long) jsonParams.get(IRequestParamsNames.JOURNAL);
		if (journal != null) {
			params.setJournal(journal.intValue());	
		}
		Long activity = (Long) jsonParams.get(IRequestParamsNames.ACTIVITY);
		if (activity != null) {
			params.setActivity(activity.intValue());	
		}
		Long confidential = (Long) jsonParams.get(IRequestParamsNames.CONFIDENTIAL);
		if (confidential != null) {
			params.setSecurityLevel( SecurityLevel.safeValueOf( confidential.intValue() ));
		}
		Long account = (Long) jsonParams.get(IRequestParamsNames.ACCOUNT);
		if (account != null) {
			params.setAccount(account.intValue());	
		}
		Number debit = (Number) jsonParams.get(IRequestParamsNames.DEBIT);
		if (debit != null) {
			params.setDebit(debit.doubleValue());	
		}
		Number credit = (Number) jsonParams.get(IRequestParamsNames.CREDIT);
		if (credit != null) {
			params.setCredit(credit.doubleValue());	
		}
		String concept = (String) jsonParams.get(IRequestParamsNames.CONCEPT);
		if (AonStringUtils.isNotBlank(concept)) {
			params.setConcept(concept);			
		}
		String document = (String) jsonParams.get(IRequestParamsNames.DOCUMENT);
		if (AonStringUtils.isNotBlank(document)) {
			params.setDocument(document);			
		}
		Long balAccount = (Long) jsonParams.get(IRequestParamsNames.BALANCING_ACCOUNT);
		if (balAccount != null) {
			params.setBalancingAccount(balAccount.intValue());	
		}
		String comments = (String) jsonParams.get(IRequestParamsNames.COMMENTS);
		if (AonStringUtils.isNotBlank(comments)) {
			params.setComments(comments);			
		}
		Long order = (Long) jsonParams.get(IRequestParamsNames.ORDER);
		if (order != null) {
			params.setOrder(order.intValue());	
		}
		// ******************* TITLE ******************* 
		String title = (String) jsonParams.get(IRequestParamsNames.TITLE);
		if (AonStringUtils.isNotBlank(title)) {
			try {
				params.setTitle(URLDecoder.decode( title , ENCODING ));
			} catch (UnsupportedEncodingException e) {
				params.setTitle(title);
			}			
		}
		// ******************* SUBJECT ******************* 
		String subject = (String) jsonParams.get(IRequestParamsNames.SUBJECT);
		if (AonStringUtils.isNotBlank(subject)) {
			try {
				params.setSubject(URLDecoder.decode( subject, ENCODING ));
			} catch (UnsupportedEncodingException e) {
				params.setSubject(subject);			
			}			
		}
		// *******************  SHOW COVER ******************* 
		Long showCover = (Long) jsonParams.get(IRequestParamsNames.SHOW_COVER);
		if (showCover != null) {
			params.setShowCover(showCover==1);
		}
		// ******************* PAGE OFFSET ******************* 
		Long pageOffset = (Long) jsonParams.get(IRequestParamsNames.PAGE_OFFSET);
		if (pageOffset!= null) {
			params.setPageOffset(pageOffset.intValue());	
		}
		// ******************* PAGE OFFSET TEXT ******************* 
		String pageOffsetText = (String) jsonParams.get(IRequestParamsNames.PAGE_OFFSET_TEXT);
		if (AonStringUtils.isNotBlank(pageOffsetText)) {
			try {
				params.setPageOffsetText(URLDecoder.decode( pageOffsetText, ENCODING ));
			} catch (UnsupportedEncodingException e) {
				params.setPageOffsetText(pageOffsetText);			
			}			
		}
		// *******************  HIDE FILTER ******************* 
		Long hideFilter = (Long) jsonParams.get(IRequestParamsNames.HIDE_FILTER);
		if (hideFilter != null) {
			params.setHideFilter(hideFilter==1);
		}
		// ******************* HEADER TEXT ******************* 
		String headerText = (String) jsonParams.get(IRequestParamsNames.HEADER_TEXT);
		if (AonStringUtils.isNotBlank(headerText)) {
			try {
				params.setHeaderText(URLDecoder.decode( headerText, ENCODING ));
			} catch (UnsupportedEncodingException e) {
				params.setHeaderText(headerText);			
			}			
		}
		// *******************  HIDE DATETIME ON FOOTER ******************* 
		Long hideDateTimeOnFooter = (Long) jsonParams.get(IRequestParamsNames.HIDE_DATETIME_ON_FOOTER);
		if (hideDateTimeOnFooter != null) {
			params.setHideDateTimeOnFooter(hideDateTimeOnFooter==1);
		}
		// ******************* PAGE OFFSET TEXT ******************* 
		String footerText = (String) jsonParams.get(IRequestParamsNames.FOOTER_TEXT);
		if (AonStringUtils.isNotBlank(footerText)) {
			try {
				params.setFooterText(URLDecoder.decode( footerText, ENCODING ));
			} catch (UnsupportedEncodingException e) {
				params.setFooterText(footerText);			
			}			
		}
		return params;
	}

	public static AccountingReportParams parseAccountingParams(String accountReportParams) throws ParseException, java.text.ParseException {
		AccountingReportParams params = new AccountingReportParams();
		JSONParser parser = new JSONParser();
		JSONObject jsonParams =  (JSONObject) parser.parse(accountReportParams);
		
		// ******************* DOMAIN ******************* 
		Object dom = jsonParams.get(IRequestParamsNames.DOMAIN);
		if (dom == null) {
			throw new IllegalArgumentException("NULL DOMAIN!");
		}
		if (dom instanceof Long) {
			Long domain = (Long) dom; 
			params.setDomain(domain.intValue());
		} else if (dom instanceof String) {
			Integer domain = AonNumberUtils.toInteger((String) dom);
			if (domain == null) {
				throw new IllegalArgumentException("NULL DOMAIN!");
			}
			params.setDomain(domain);
		}
		

		// ******************* PERIOD ******************* 
		Long period = (Long) jsonParams.get(IRequestParamsNames.PERIOD);
		if (period != null) {
			params.setPeriod(period.intValue());	
		}
		// ******************* FROMDATE ******************* 
		String fromDate = (String) jsonParams.get(IRequestParamsNames.FROM_DATE);
		if (AonStringUtils.isNotBlank(fromDate)) {
			params.setFromDate( FORMATTER.parse(fromDate));			
		}
		// ******************* TODATE ******************* 
		String toDate = (String) jsonParams.get(IRequestParamsNames.TO_DATE);
		if (AonStringUtils.isNotBlank(toDate)) {
			params.setToDate( FORMATTER.parse(toDate));			
		}
		// ******************* ACCOUNT CODE ******************* 
		String accountCode = (String) jsonParams.get(IRequestParamsNames.ACCOUNT_CODE);
		if (AonStringUtils.isNotBlank(accountCode)) {
			Account account = new Account();
			account.setCode(accountCode);
			params.setAccount(account);			
		}
		// ******************* ACTIVITY ******************* 
		Long activity = (Long) jsonParams.get(IRequestParamsNames.ACTIVITY);
		if (activity != null) {
			params.setActivity(activity.intValue());	
		}
		// ******************* SECURITYLEVEL ******************* 
		Long confidential = (Long) jsonParams.get(IRequestParamsNames.CONFIDENTIAL);
		if (confidential != null) {
			params.setSecurityLevel( SecurityLevel.safeValueOf( confidential.intValue() ));
		}
		// ******************* PREVIOUSPERIODS ******************* 
		Long previousPeriods = (Long) jsonParams.get(IRequestParamsNames.PREVIOUS_PERIODS);
		if (previousPeriods != null) {
			params.setPreviousPeriods(previousPeriods.intValue());	
		}
		// *******************  BALANCE TYPE ******************* 
		Long balanceType = (Long) jsonParams.get(IRequestParamsNames.BALANCE_TYPE);
		if (balanceType != null) {
			params.setBalanceType( BalanceType.safeValueOf( balanceType.intValue() ));
		}
		// *******************  ACCOUNT LEVEL ******************* 
		Long accountLevel = (Long) jsonParams.get(IRequestParamsNames.LEVEL);
		if (accountLevel != null) {
			params.setLevel( accountLevel.intValue() );
		}
		// *******************  PERCENTS ENABLED ******************* 
		Long percentsEnabled  = (Long) jsonParams.get(IRequestParamsNames.PERCENTS_ENABLED);
		if (percentsEnabled != null) {
			params.setPercentsEnabled(percentsEnabled==1);
		}
		// *******************  BY_MONTH ******************* 
		Long byMonth  = (Long) jsonParams.get(IRequestParamsNames.BY_MONTH);
		if (byMonth != null) {
			params.setByMonth(byMonth==1);
		}
		// *******************  LOW_LEVEL_ACCOUNT_VISIBLE ******************* 
		Long lowLwvelVisible = (Long) jsonParams.get(IRequestParamsNames.LOW_LEVEL_ACCOUNT_VISIBLE);
		if (lowLwvelVisible != null) {
			params.setLowLevelAccountVisible(lowLwvelVisible==1);
		}
		// *******************  NO_ACTIVITY_ACCOUNT_VISIBLE ******************* 
		Long noActivityAccountVisible = (Long) jsonParams.get(IRequestParamsNames.NO_ACTIVITY_ACCOUNT_VISIBLE);
		if (noActivityAccountVisible != null) {
			params.setNoActivityAccountVisible(noActivityAccountVisible==1);
		}
		// *******************  NO_BALANCE_ACCOUNT_EXCLUDED ******************* 
		Long noBalanceAccountExcluded = (Long) jsonParams.get(IRequestParamsNames.NO_BALANCE_ACCOUNT_EXCLUDED);
		if (noBalanceAccountExcluded != null) {
			params.setNoBalanceAccountExcluded(noBalanceAccountExcluded==1);
		}
		
		// ******************* OPERATING_ENTRIES_EXCLUDED ******************* 
		Long operatingEntriesExcluded = (Long) jsonParams.get(IRequestParamsNames.OPERATING_ENTRIES_EXCLUDED);
		if (operatingEntriesExcluded != null) {
			params.setOperatingEntriesExcluded(operatingEntriesExcluded.intValue() == 1);	
		}
		// ******************* NOACTIVITYACCOUNTVISIBLE ******************* 
		Long closingEntriesExcluded = (Long) jsonParams.get(IRequestParamsNames.CLOSING_ENTRIES_EXCLUDED);
		if (closingEntriesExcluded != null) {
			params.setClosingEntriesExcluded(closingEntriesExcluded.intValue() == 1);	
		}
		// ******************* BREAKDOWN_ENABLED ******************* 
		Long breakdownEnabled = (Long) jsonParams.get(IRequestParamsNames.BREAKDOWN_ENABLED);
		if (breakdownEnabled != null) {
			params.setBreakdownEnabled(breakdownEnabled.intValue() == 1);	
		}

		// ******************* TITLE ******************* 
		String title = (String) jsonParams.get(IRequestParamsNames.TITLE);
		if (AonStringUtils.isNotBlank(title)) {
			try {
				params.setTitle(URLDecoder.decode( title , ENCODING ));
			} catch (UnsupportedEncodingException e) {
				params.setTitle(title);
			}			
		}
		// ******************* SUBJECT ******************* 
		String subject = (String) jsonParams.get(IRequestParamsNames.SUBJECT);
		if (AonStringUtils.isNotBlank(subject)) {
			try {
				params.setSubject(URLDecoder.decode( subject, ENCODING ));
			} catch (UnsupportedEncodingException e) {
				params.setSubject(subject);			
			}			
		}
		// *******************  SHOW COVER ******************* 
		Long showCover = (Long) jsonParams.get(IRequestParamsNames.SHOW_COVER);
		if (showCover != null) {
			params.setShowCover(showCover==1);
		}
		// ******************* PAGE OFFSET ******************* 
		Long pageOffset = (Long) jsonParams.get(IRequestParamsNames.PAGE_OFFSET);
		if (pageOffset!= null) {
			params.setPageOffset(pageOffset.intValue());	
		}
		// ******************* PAGE OFFSET TEXT ******************* 
		String pageOffsetText = (String) jsonParams.get(IRequestParamsNames.PAGE_OFFSET_TEXT);
		if (AonStringUtils.isNotBlank(pageOffsetText)) {
			try {
				params.setPageOffsetText(URLDecoder.decode( pageOffsetText, ENCODING ));
			} catch (UnsupportedEncodingException e) {
				params.setPageOffsetText(pageOffsetText);			
			}			
		}
		// *******************  HIDE FILTER ******************* 
		Long hideFilter = (Long) jsonParams.get(IRequestParamsNames.HIDE_FILTER);
		if (hideFilter != null) {
			params.setHideFilter(hideFilter==1);
		}
		// ******************* HEADER TEXT ******************* 
		String headerText = (String) jsonParams.get(IRequestParamsNames.HEADER_TEXT);
		if (AonStringUtils.isNotBlank(headerText)) {
			try {
				params.setHeaderText(URLDecoder.decode( headerText, ENCODING ));
			} catch (UnsupportedEncodingException e) {
				params.setHeaderText(headerText);			
			}			
		}
		// *******************  HIDE DATETIME ON FOOTER ******************* 
		Long hideDateTimeOnFooter = (Long) jsonParams.get(IRequestParamsNames.HIDE_DATETIME_ON_FOOTER);
		if (hideDateTimeOnFooter != null) {
			params.setHideDateTimeOnFooter(hideDateTimeOnFooter==1);
		}
		// ******************* PAGE OFFSET TEXT ******************* 
		String footerText = (String) jsonParams.get(IRequestParamsNames.FOOTER_TEXT);
		if (AonStringUtils.isNotBlank(footerText)) {
			try {
				params.setFooterText(URLDecoder.decode( footerText, ENCODING ));
			} catch (UnsupportedEncodingException e) {
				params.setFooterText(footerText);			
			}			
		}
		
		// ******************* LEDGER ACCOUNT *******************
		Integer la = (Integer) jsonParams.get(IRequestParamsNames.LEDGER_ACCOUNT);
		if (la != null) {
			params.setLedgerAccount(la.intValue());
		}
		
		// ******************* LEDGER DEBIT BALANCE *******************
		Double ldb = (Double) jsonParams.get(IRequestParamsNames.LEDGER_DEBIT_BALANCE);
		if (ldb != null) {
			params.setLedgerDebitBalance(ldb.doubleValue());
		}
		
		// ******************* LEDGER UNPAID BALANCE ******************* 
		Double lub = (Double) jsonParams.get(IRequestParamsNames.LEDGER_UNPAID_BALANCE);
		if (lub!= null) {
			params.setLedgerUnpaidBalance(lub.doubleValue());
		}
		
		// ******************* REGISTRY ******************* 
		Long registry = (Long) jsonParams.get(IRequestParamsNames.REGISTRY);
		if (registry != null) {
			params.setRegistry(registry.intValue());	
		}
		// ******************* PERCENT *******************
		Number percent = (Number) jsonParams.get(IRequestParamsNames.PERCENT);
		if (percent != null) {
			params.setPercent(percent.doubleValue());	
		}
		// ******************* PERCENT *******************
		Number surchargePercent = (Number) jsonParams.get(IRequestParamsNames.SURCHARGE_PERCENT);
		if (surchargePercent != null) {
			params.setSurchargePercent(surchargePercent.doubleValue());	
		}
		// ******************* VAT_SUMMARY_TYPE ******************* 
		Long type = (Long) jsonParams.get(IRequestParamsNames.VAT_SUMMARY_TYPE);
		if (type != null) {
			params.setVatSummaryType( VatSummaryType.safeValueOf(type.intValue()));
		}
		// ******************* OUTPUT ******************* 
		Long output = (Long) jsonParams.get(IRequestParamsNames.OUTPUT);
		if (output != null) {
			params.setOutput(output==1);
		}
		// ******************* SURCHARGE ******************* 
		Long surcharge = (Long) jsonParams.get(IRequestParamsNames.SURCHARGE);
		if (surcharge != null) {
			params.setSurcharge(surcharge==1);
		}
		// ******************* FARMER_REGIME ******************* 
		Long farmerRegime = (Long) jsonParams.get(IRequestParamsNames.FARMER_REGIME);
		if (farmerRegime != null) {
			params.setFarmerRegime(farmerRegime==1);
		}
		// ******************* ACCRUAL_REGIME ******************* 
		Long accrualRegime = (Long) jsonParams.get(IRequestParamsNames.ACCRUAL_REGIME);
		if (accrualRegime != null) {
			params.setAccrualRegime(accrualRegime==1);
		}
		// ******************* INVESTMENT ******************* 
		Long investment = (Long) jsonParams.get(IRequestParamsNames.INVESTMENT);
		if (investment != null) {
			params.setInvestment(investment==1);
		}
		// ******************* SERVICE ******************* 
		Long service = (Long) jsonParams.get(IRequestParamsNames.SERVICE);
		if (service != null) {
			params.setService(service==1);
		}
		// ******************* RECTIFICATION ******************* 
		Long rectification = (Long) jsonParams.get(IRequestParamsNames.RECTIFICATION);
		if (rectification != null) {
			params.setRectificationType(RectificationType.safeValueOf( rectification.intValue() ));
		}

		// ******************* DOMAINS ******************* 
		JSONArray domains = (JSONArray) jsonParams.get(IRequestParamsNames.DOMAINS);
		if (domains != null && domains.size() > 0) {
			LinkedList<Domain> list = new LinkedList<Domain>();
			for ( int i = 0; i < domains.size(); i++) {
				Object v = domains.get(i);
				list.add( new Domain().setId(  ((Long) v).intValue() ));
			}
			params.setDomains(list);
		}
		// ******************* SERVICE ******************* 
		Long consolidation = (Long) jsonParams.get(IRequestParamsNames.CONSOLIDATION);
		if (consolidation != null) {
			params.setConsolidation(consolidation==1);
		}

		return params;
	}
	
	public static FinanceParams parseFinanceParams(String financeParams) {
		FinanceParams params = new FinanceParams();
		org.json.JSONObject json = new org.json.JSONObject(financeParams);
		
		params.setDomainName(JsonUtils.getString(json, IJsonNames.DOMAIN_NAME));
		params.setDomain(JsonUtils.getInteger(json, IJsonNames.DOMAIN));
		params.setFromInvoiceDate(JsonUtils.getDate(json, IJsonNames.FROM_DATE));
		params.setToInvoiceDate(JsonUtils.getDate(json, IJsonNames.TO_DATE));
		params.setFromDueDate(JsonUtils.getDate(json, IJsonNames.FROM_DUE_DATE));
		params.setToInvoiceDate(JsonUtils.getDate(json, IJsonNames.TO_DUE_DATE));
		params.setSecurityLevel(SecurityLevel.safeValueOf(JsonUtils.getInteger(json, IJsonNames.SECURITY_LEVEL)));
		params.setHasConfidentialityRole(JsonUtils.getInt(json, IJsonNames.SECURITY_LEVEL) == 1);		
		params.setPayment(JsonUtils.getInt(json, IJsonNames.PAYMENT) == 1);
		params.setPending(JsonUtils.getInt(json, IJsonNames.PENDING) == 1);
		params.setBatched(JsonUtils.getInt(json, IJsonNames.BATCHED) == 1);
		params.setReturned(JsonUtils.getInt(json, IJsonNames.RETURNED) == 1);
		params.setPaid(JsonUtils.getInt(json, IJsonNames.PAID) == 1);
		params.setSettled(JsonUtils.getInt(json, IJsonNames.SETTLED) == 1);
		params.setRegistry(JsonUtils.getInteger(json, IJsonNames.REGISTRY));
		params.setAmount(JsonUtils.getDouble(json, IJsonNames.AMOUNT));
		params.setNearbyNumbers(JsonUtils.getInt(json, IJsonNames.NEARBY_NUMBERS) ==1);	
		params.setConcept(JsonUtils.getString(json, IJsonNames.CONCEPT));
		params.setReferenceCode(JsonUtils.getString(json, IJsonNames.REFERENCE_CODE));	
		params.setPayMethod(JsonUtils.getInteger(json, IJsonNames.PAY_METHOD));
		params.setOrder(JsonUtils.getInteger(json, IJsonNames.ORDER_BY));
		params.setIsPayroll(JsonUtils.getInt(json, IJsonNames.IS_PAYROLL) == 1);

		return params;
	}
	
	public static OperationParams parseOperationParams(String operationParams) throws ParseException, java.text.ParseException {
		OperationParams params = new OperationParams();
		JSONParser parser = new JSONParser();
		JSONObject jsonParams =  (JSONObject) parser.parse(operationParams);
		
		Object dom = jsonParams.get(IRequestParamsNames.DOMAIN);
		if (dom == null) {
			throw new IllegalArgumentException("NULL DOMAIN!");
		}
		if (dom instanceof Long) {
			Long domain = (Long) dom; 
			params.setDomain(domain.intValue());
		} else if (dom instanceof String) {
			Integer domain = AonNumberUtils.toInteger((String) dom);
			if (domain == null) {
				throw new IllegalArgumentException("NULL DOMAIN!");
			}
			params.setDomain(domain);
		}
		
		Long activity = (Long) jsonParams.get(IRequestParamsNames.ACTIVITY);
		if (activity != null) {
			params.setActivity(activity.intValue());	
		}
		
		String activityDescription = (String) jsonParams.get(IRequestParamsNames.ACTIVITY_DESCRIPTION);
		if (activityDescription != null) {
			params.setActivityDescription(activityDescription);	
		}			

		String fromDate = (String) jsonParams.get(IRequestParamsNames.FROM_DATE);
		if (AonStringUtils.isNotBlank(fromDate)) {
			params.setFromDate( FORMATTER.parse(fromDate));
		}
		String toDate = (String) jsonParams.get(IRequestParamsNames.TO_DATE);
		if (AonStringUtils.isNotBlank(toDate)) {
			params.setToDate( FORMATTER.parse(toDate));			
		}
		Long expenses = (Long) jsonParams.get(IRequestParamsNames.EXPENSES);
		params.setExpenses(expenses == null || expenses==1);
		
		Long irpf = (Long) jsonParams.get(IRequestParamsNames.IRPF);
		if (irpf != null) {
			params.setIrpf(irpf==1);
		}
		
		Long unified = (Long) jsonParams.get(IRequestParamsNames.UNIFIED_BOOK);
		if (unified != null) {
			params.setUnifiedBook(unified==1);
		}
		return params;
	}

	public static IRPFParams parseIRPFParams(String irpfParams) throws ParseException, java.text.ParseException {
		IRPFParams params = new IRPFParams();
		JSONParser parser = new JSONParser();
		JSONObject jsonParams =  (JSONObject) parser.parse(irpfParams);
		

		// ******************* DOMAIN ******************* 
		Object dom = jsonParams.get(IRequestParamsNames.DOMAIN);
		if (dom == null) {
			throw new IllegalArgumentException("NULL DOMAIN!");
		}
		if (dom instanceof Long) {
			Long domain = (Long) dom; 
			params.setDomain(domain.intValue());
		} else if (dom instanceof String) {
			Integer domain = AonNumberUtils.toInteger((String) dom);
			if (domain == null) {
				throw new IllegalArgumentException("NULL DOMAIN!");
			}
			params.setDomain(domain);
		}

		// ******************* ACTIVITY ******************* 
		Long registry = (Long) jsonParams.get(IRequestParamsNames.REGISTRY);
		if (registry != null) {
			params.setRegistry(registry.intValue());	
		}
		// ******************* ACTIVITY ******************* 
		Long activity = (Long) jsonParams.get(IRequestParamsNames.ACTIVITY);
		if (activity != null) {
			params.setActivity(activity.intValue());	
		}
		// ******************* FROMDATE ******************* 
		String fromDate = (String) jsonParams.get(IRequestParamsNames.FROM_DATE);
		if (AonStringUtils.isNotBlank(fromDate)) {
			params.setFromDate( FORMATTER.parse(fromDate));			
		}
		// ******************* TODATE ******************* 
		String toDate = (String) jsonParams.get(IRequestParamsNames.TO_DATE);
		if (AonStringUtils.isNotBlank(toDate)) {
			params.setToDate( FORMATTER.parse(toDate));			
		}
		// *******************  OUTPUT ******************* 
		Long outputEnabled  = (Long) jsonParams.get(IRequestParamsNames.OUTPUT);
		if (outputEnabled != null) {
			params.setOutput(outputEnabled==1);
		}
		// ******************* WithholdingType ******************* 
		Long withholdingTypeGroup = (Long) jsonParams.get(IRequestParamsNames.WITHHOLDING_TYPE_GROUP);
		if (withholdingTypeGroup != null) {
			params.setWithholdingTypeGroup(WithholdingTypeGroup.safeValueOf( withholdingTypeGroup.intValue() ));
		}
		// ******************* WithholdingType ******************* 
		Long withholdingType = (Long) jsonParams.get(IRequestParamsNames.WITHHOLDING_TYPE);
		if (withholdingType != null) {
			params.setWithholdingType(WithholdingType.safeValueOf( withholdingType.intValue() ));
		}
		// ******************* PERCENT *******************
		Number percent = (Number) jsonParams.get(IRequestParamsNames.PERCENT);
		if (percent != null) {
			params.setPercent(percent.doubleValue());	
		}
		// ******************* RECTIFICATION ******************* 
		Long rectification = (Long) jsonParams.get(IRequestParamsNames.RECTIFICATION);
		if (rectification != null) {
			params.setRectificationType(RectificationType.safeValueOf( rectification.intValue() ));
		}
		
		// ******************* ACCRUAL_REGIME ******************* 
		Long accrualRegime = (Long) jsonParams.get(IRequestParamsNames.ACCRUAL_REGIME);
		if (accrualRegime != null) {
			params.setAccrualRegime(accrualRegime==1);
		}
		// ******************* INVESTMENT ******************* 
		Long investment = (Long) jsonParams.get(IRequestParamsNames.INVESTMENT);
		if (investment != null) {
			params.setInvestment(investment==1);
		}
		// ******************* SERVICE ******************* 
		Long service = (Long) jsonParams.get(IRequestParamsNames.SERVICE);
		if (service != null) {
			params.setService(service==1);
		}
		// ******************* ACTIVITY ******************* 
		Long orderBy = (Long) jsonParams.get(IRequestParamsNames.ORDER_BY);
		if (orderBy != null) {
			params.setOrderBy( IRPFParamsOrderBy.safeValueOf(orderBy.intValue()));	
		}
		// ******************* ACTIVITY ******************* 
		Long groupedBy = (Long) jsonParams.get(IRequestParamsNames.GROUPED_BY);
		if (groupedBy != null) {
			params.setGroupedBy( IRPFParamsGroupedBy.safeValueOf(groupedBy.intValue()) );	
		}
		return params;
	}
	
	public static DomainParams parseDomainParams(String domainParams) throws ParseException, java.text.ParseException {
		DomainParams params = new DomainParams();
		JSONParser parser = new JSONParser();
		JSONObject jsonParams =  (JSONObject) parser.parse(domainParams);
		String schema = (String) jsonParams.get(IRequestParamsNames.SCHEMA);
		params.setSchema(schema);
		
		Long id = (Long) jsonParams.get(IRequestParamsNames.ID);
		if (id!= null) {
			params.setId(id.intValue());	
		}
		
		String query = (String) jsonParams.get(IRequestParamsNames.QUERY);
		if (AonStringUtils.isNotBlank(query)) {
			params.setQuery(query);
		}

		String name = (String) jsonParams.get(IRequestParamsNames.NAME);
		if (AonStringUtils.isNotBlank(name)) {
			params.setName(name);
		}

		String description = (String) jsonParams.get(IRequestParamsNames.DESCRIPTION);
		if (AonStringUtils.isNotBlank(description)) {
			params.setDescription(description);
		}

		Long type = (Long) jsonParams.get(IRequestParamsNames.TYPE);
		if (type!= null) {
			params.setType(type.intValue());	
		}

		Long parent = (Long) jsonParams.get(IRequestParamsNames.PARENT);
		if (parent!= null) {
			params.setParent(parent.intValue());	
		}
		
		Long orphan = (Long) jsonParams.get(IRequestParamsNames.ORPHAN);
		if (orphan != null) {
			params.setOrphan(orphan == 1);
		}

		Long active = (Long) jsonParams.get(IRequestParamsNames.ACTIVE);
		if (active != null) {
			params.setActive(active==1);
		}

		Long enableHeredity = (Long) jsonParams.get(IRequestParamsNames.ENABLE_HEREDITY);
		if (enableHeredity != null) {
			params.setEnableHeredity(enableHeredity==1);
		}
		
		Long domainManagement = (Long) jsonParams.get(IRequestParamsNames.DOMAIN_MANAGEMENT);
		if (domainManagement != null) {
			params.setDomainManagement(domainManagement==1);
		}

		String fromLastAccess = (String) jsonParams.get(IRequestParamsNames.FROM_LAST_ACCESS_DATE);
		if (AonStringUtils.isNotBlank(fromLastAccess)) {
			params.setFromLastAccess(FORMATTER.parse(fromLastAccess));			
		}
		
		String toLastAccess = (String) jsonParams.get(IRequestParamsNames.TO_LAST_ACCESS_DATE);
		if (AonStringUtils.isNotBlank(toLastAccess)) {
			params.setToLastAccess(FORMATTER.parse(toLastAccess));	
		}

		String fromExpirationDate = (String) jsonParams.get(IRequestParamsNames.FROM_EXPIRATION_DATE);
		if (AonStringUtils.isNotBlank(fromExpirationDate)) {
			params.setFromExpirationDate(FORMATTER.parse(fromExpirationDate));			
		}

		String toExpirationDate = (String) jsonParams.get(IRequestParamsNames.TO_EXPIRATION_DATE);
		if (AonStringUtils.isNotBlank(toExpirationDate)) {
			params.setToExpirationDate(FORMATTER.parse(toExpirationDate));			
		}
		
		Long offset = (Long) jsonParams.get(IRequestParamsNames.OFFSET);
		if (offset!= null) {
			params.setOffset(offset.intValue());	
		}

		Long limit = (Long) jsonParams.get(IRequestParamsNames.LIMIT);
		if (parent!= null) {
			params.setLimit(limit.intValue());
		}

		Long validate = (Long) jsonParams.get(IRequestParamsNames.VALIDATE);
		if (validate != null) {
			params.setValidate(validate == 1);
		}

		Long mustFlatten = (Long) jsonParams.get(IRequestParamsNames.MUST_FLATTEN);
		if (mustFlatten != null) {
			params.setMustFlatten(mustFlatten==1);
		}

		return params;
		
	}
	
}
