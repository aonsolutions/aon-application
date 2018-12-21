package com.esferalia.aon.gwt.fiscal.server;

import java.text.SimpleDateFormat;

import org.jooq.tools.json.JSONObject;
import org.jooq.tools.json.JSONParser;
import org.jooq.tools.json.ParseException;

import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.occam.api.model.AccountEntryParams;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.accounting.BalanceType;
import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.watson.util.AonStringUtils;

public class JsonParser {
	private static SimpleDateFormat FORMATTER = new SimpleDateFormat("dd/MM/yyyy");

	public static AccountEntryParams parse(String accountEntryParams) throws ParseException, java.text.ParseException {
		AccountEntryParams params = new AccountEntryParams();
		JSONParser parser = new JSONParser();
		JSONObject jsonParams =  (JSONObject) parser.parse(accountEntryParams);
		
		Long domain = (Long) jsonParams.get(IRequestParamsNames.DOMAIN);
		params.setDomain(domain.intValue());
		
		Long period = (Long) jsonParams.get(IRequestParamsNames.PERIOD);
		if (period != null) {
			params.setPeriod(period.intValue());	
		}
		String fromDate = (String) jsonParams.get(IRequestParamsNames.FROM_DATE);
		if (AonStringUtils.isNotBlank(fromDate)) {
			params.setFrom( FORMATTER.parse(fromDate));			
		}
		String toDate = (String) jsonParams.get(IRequestParamsNames.TO_DATE);
		if (AonStringUtils.isNotBlank(toDate)) {
			params.setTo( FORMATTER.parse(toDate));			
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
		return params;
	}

	public static AccountingReportParams parseAccountingParams(String accountReportParams) throws ParseException, java.text.ParseException {
		AccountingReportParams params = new AccountingReportParams();
		JSONParser parser = new JSONParser();
		JSONObject jsonParams =  (JSONObject) parser.parse(accountReportParams);
		
		// ******************* DOMAIN ******************* 
		Long domain = (Long) jsonParams.get(IRequestParamsNames.DOMAIN);
		params.setDomain(domain.intValue());

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
		
//		IRequestParamsNames.ACCOUNT,params.getAccount().getId() == null? JSON_NULL : new JSONNumber( params.getAccount().getId()));
//		IRequestParamsNames.ACCOUNT_CODE,AonStringUtils.isBlank(params.getAccount().getCode())? JSON_NULL : new JSONString( params.getAccount().getCode()));
//		IRequestParamsNames.ACCOUNT_DESCRIPTION,AonStringUtils.isBlank(params.getAccount().getDescription())? JSON_NULL : new JSONString( params.getAccount().getDescription()));
//		IRequestParamsNames.DOCUMENT,AonStringUtils.isBlank(params.getDocumentNumber())? JSON_NULL : new JSONString( params.getDocumentNumber()));
//		IRequestParamsNames.COST_CENTERS,JSON_NULL);
		return params;
	}
}
