package com.esferalia.aon.gwt.fiscal.server;

import java.text.SimpleDateFormat;

import org.jooq.tools.json.JSONObject;
import org.jooq.tools.json.JSONParser;
import org.jooq.tools.json.ParseException;

import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.occam.api.model.AccountEntryParams;
import com.esferalia.aon.occam.api.model.type.AccountEntryType;
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
		if (confidential!= null) {
			params.setConfidential(confidential.intValue() == 1);	
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

}
