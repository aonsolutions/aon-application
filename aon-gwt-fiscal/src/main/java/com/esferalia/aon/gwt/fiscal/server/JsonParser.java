package com.esferalia.aon.gwt.fiscal.server;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;

import org.jooq.tools.json.JSONObject;
import org.jooq.tools.json.JSONParser;
import org.jooq.tools.json.ParseException;

import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountEntryParams;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.accounting.BalanceType;
import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.watson.util.AonStringUtils;

public class JsonParser {
	private static final String ENCODING = "utf-8";
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
}
