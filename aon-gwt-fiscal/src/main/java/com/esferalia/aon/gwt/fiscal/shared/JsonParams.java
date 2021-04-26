package com.esferalia.aon.gwt.fiscal.shared;

import com.esferalia.aon.occam.api.model.AccountEntryParams;
import com.esferalia.aon.occam.api.model.AccountParams;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.FinanceParams;
import com.esferalia.aon.occam.api.model.RegistryParams;
import com.esferalia.aon.occam.api.model.fiscal.IRPFParams;
import com.esferalia.aon.occam.api.model.fiscal.OperationParams;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.http.client.URL;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNull;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;

public class JsonParams extends JSONObject {
	private static final DateTimeFormat FORMATTER = DateTimeFormat.getFormat("dd/MM/yyyy");
	 
	public static String convert(RegistryParams params) {
		JSONObject json = new JSONObject();
		JSONNull JSON_NULL = JSONNull.getInstance();
		json.put(IRequestParamsNames.DOMAIN_NAME	,new JSONString( params.getDomainName()));
		json.put(IRequestParamsNames.DOMAIN   		,new JSONNumber( params.getDomain()));
		json.put(IRequestParamsNames.USER   		,new JSONString( params.getUser()));
		json.put(IRequestParamsNames.SECURITY_LEVEL ,params.getSecurityLevel() == null?JSON_NULL :new JSONNumber( params.getSecurityLevel().value()));
		json.put(IRequestParamsNames.HAS_CONFIDENTIALITY_ROLE,new JSONNumber( params.hasConfidentialityRole()?1:0));
		json.put(IRequestParamsNames.ID 			,params.getId() 				== null? JSON_NULL : new JSONNumber( params.getId()));
		json.put(IRequestParamsNames.DOCUMENT_TYPE	,params.getDocumentType()	== null? JSON_NULL : new JSONNumber( params.getDocumentType().ordinal()));
		json.put(IRequestParamsNames.DOCUMENT_COUNTRY,params.getDocumentCountry()	== null? JSON_NULL : new JSONString( params.getDocumentCountry().getIso2()));
		json.put(IRequestParamsNames.DOCUMENT		,params.getDocument() 			== null? JSON_NULL : new JSONString( params.getDocument()));
		json.put(IRequestParamsNames.NAME		,params.getName() 			== null? JSON_NULL : new JSONString( params.getName()));
		json.put(IRequestParamsNames.ALIAS		,params.getAlias() 			== null? JSON_NULL : new JSONString( params.getAlias()));
		json.put(IRequestParamsNames.ACTIVE	,new JSONNumber( params.isActive()?1:0));
		json.put(IRequestParamsNames.INACTIVE	,new JSONNumber( params.isInactive()?1:0));
		json.put(IRequestParamsNames.BLOCKED	,new JSONNumber( params.isBlocked()?1:0));
		json.put(IRequestParamsNames.ORDER_BY		,new JSONNumber( params.getOrder()));		
		return json.toString();
	}

	public static String convert(AccountParams params) {
		JSONObject json = new JSONObject();
		JSONNull JSON_NULL = JSONNull.getInstance();
		json.put(IRequestParamsNames.DOMAIN_NAME	,new JSONString( params.getDomainName()));
		json.put(IRequestParamsNames.DOMAIN   		,new JSONNumber( params.getDomain()));
		json.put(IRequestParamsNames.USER   		,new JSONString( params.getUser()));
		json.put(IRequestParamsNames.ID 			,params.getId() 				== null? JSON_NULL : new JSONNumber( params.getId()));
		json.put(IRequestParamsNames.ACCOUNT_CODE	,params.getCode()	 			== null? JSON_NULL : new JSONString( params.getCode()));
		json.put(IRequestParamsNames.ACCOUNT_DESCRIPTION,params.getDescription()	 			== null? JSON_NULL : new JSONString( params.getDescription()));
		json.put(IRequestParamsNames.ACCOUNT_ALIAS,params.getAlias()	 			== null? JSON_NULL : new JSONString( params.getAlias()));
		json.put(IRequestParamsNames.LEVEL  		,params.getLevel()  			== null? JSON_NULL : new JSONNumber( params.getLevel()));
		json.put(IRequestParamsNames.ACCOUNT_ACTIVE ,params.getActive()				== null? JSON_NULL : new JSONNumber( params.getActive()?1:0));
		json.put(IRequestParamsNames.COST_CENTER    ,params.getCostCenter()			== null? JSON_NULL : new JSONString( params.getCostCenter()));
		json.put(IRequestParamsNames.OFFSET			,new JSONNumber( params.getOffset()));
		json.put(IRequestParamsNames.LIMIT			,new JSONNumber( params.getLimit()));
		return json.toString();
	}

	public static String convert(IRPFParams params) {
		JSONObject json = new JSONObject();
		JSONNull JSON_NULL = JSONNull.getInstance();
		json.put(IRequestParamsNames.DOMAIN   		,new JSONNumber( params.getDomain()));
		json.put(IRequestParamsNames.REGISTRY 		,params.getRegistry() 			== null? JSON_NULL : new JSONNumber( params.getRegistry()));
		json.put(IRequestParamsNames.ACTIVITY 		,params.getActivity() 			== null? JSON_NULL : new JSONNumber( params.getActivity()));
		json.put(IRequestParamsNames.FROM_DATE 		,params.getFromDate() 			== null? JSON_NULL : new JSONString( FORMATTER.format(params.getFromDate())));
		json.put(IRequestParamsNames.TO_DATE  	 	,params.getToDate()   			== null? JSON_NULL : new JSONString( FORMATTER.format(params.getToDate())));
		json.put(IRequestParamsNames.PERCENT  		,params.getPercent()  			== null? JSON_NULL : new JSONNumber( params.getPercent()));
		json.put(IRequestParamsNames.TYPE   	  	,params.getWithholdingType()	== null? JSON_NULL : new JSONNumber( params.getWithholdingType().ordinal()));
		json.put(IRequestParamsNames.OUTPUT   		,params.getOutput() 			== null? JSON_NULL : new JSONNumber( params.getOutput()?1:0));
		json.put(IRequestParamsNames.ACCRUAL_REGIME	,params.getAccrualRegime() 		== null? JSON_NULL : new JSONNumber( params.getAccrualRegime()?1:0));
		json.put(IRequestParamsNames.INVESTMENT		,params.getInvestment() 		== null? JSON_NULL : new JSONNumber( params.getInvestment()?1:0));
		json.put(IRequestParamsNames.SERVICE		,params.getService() 			== null? JSON_NULL : new JSONNumber( params.getService()?1:0));
		json.put(IRequestParamsNames.RECTIFICATION	,params.getRectificationType()	== null? JSON_NULL : new JSONNumber( params.getRectificationType().ordinal()));
		json.put(IRequestParamsNames.ORDER_BY		,params.getOrderBy()			== null? JSON_NULL : new JSONNumber( params.getOrderBy()));
		json.put(IRequestParamsNames.GROUP_BY_NIF	,params.getGroupByNif()			== null? JSON_NULL : new JSONNumber( params.getGroupByNif()));
		return json.toString();
	}
	
	public static String convert(AccountEntryParams params) {
		JSONObject json = new JSONObject();
		JSONNull JSON_NULL = JSONNull.getInstance();
		json.put(IRequestParamsNames.DOMAIN   		 ,new JSONNumber( params.getDomain()));
		json.put(IRequestParamsNames.PERIOD   		 ,params.getPeriod() 		== null? JSON_NULL : new JSONNumber( params.getPeriod()));
		json.put(IRequestParamsNames.FROM_DATE 		 ,params.getFromDate() 		== null? JSON_NULL : new JSONString( FORMATTER.format(params.getFromDate())));
		json.put(IRequestParamsNames.TO_DATE  	 	 ,params.getToDate()   		== null? JSON_NULL : new JSONString( FORMATTER.format(params.getToDate())));
		json.put(IRequestParamsNames.TYPE   	  	 ,params.getType()			== null? JSON_NULL : new JSONNumber( params.getType().ordinal()));
		json.put(IRequestParamsNames.ACCOUNT_ENTRY_ID,params.getAccountEntryId()== null? JSON_NULL : new JSONNumber( params.getAccountEntryId()));
		json.put(IRequestParamsNames.JOURNAL   		 ,params.getJournal() 		== null? JSON_NULL : new JSONNumber( params.getJournal()));
		json.put(IRequestParamsNames.ACTIVITY 		 ,params.getActivity() 		== null? JSON_NULL : new JSONNumber( params.getActivity()));
		json.put(IRequestParamsNames.CONFIDENTIAL	 ,params.getSecurityLevel() 	== null?JSON_NULL :new JSONNumber( params.getSecurityLevel().value()));
		json.put(IRequestParamsNames.ACCOUNT   		 ,params.getAccount()		== null? JSON_NULL : new JSONNumber( params.getAccount()));
		json.put(IRequestParamsNames.DEBIT 			 ,params.getDebit()  		== null? JSON_NULL : new JSONNumber( params.getDebit()));
		json.put(IRequestParamsNames.CREDIT			 ,params.getCredit()  		== null? JSON_NULL : new JSONNumber( params.getCredit()));
		json.put(IRequestParamsNames.CONCEPT  	 	 ,AonStringUtils.isBlank(params.getConcept())? JSON_NULL : new JSONString( params.getConcept()));
		json.put(IRequestParamsNames.DOCUMENT 	 	 ,AonStringUtils.isBlank(params.getDocument())? JSON_NULL : new JSONString( params.getDocument()));
		json.put(IRequestParamsNames.BALANCING_ACCOUNT,params.getBalancingAccount() == null? JSON_NULL : new JSONNumber( params.getBalancingAccount()));
		json.put(IRequestParamsNames.COMMENTS		 ,AonStringUtils.isBlank(params.getComments())? JSON_NULL : new JSONString( params.getComments()));
		json.put(IRequestParamsNames.ORDER 			 ,new JSONNumber( params.getOrder()));
		json.put(IRequestParamsNames.TITLE			 ,AonStringUtils.isBlank(params.getTitle())? JSON_NULL : new JSONString( URL.encode( params.getTitle() )));
		json.put(IRequestParamsNames.SUBJECT		 ,AonStringUtils.isBlank(params.getSubject())? JSON_NULL : new JSONString( URL.encode( params.getSubject())));
		json.put(IRequestParamsNames.SHOW_COVER		 ,new JSONNumber( params.isShowCover()?1:0));
		json.put(IRequestParamsNames.PAGE_OFFSET	 ,new JSONNumber( params.getPageOffset()));
		json.put(IRequestParamsNames.PAGE_OFFSET_TEXT,AonStringUtils.isBlank(params.getPageOffsetText())? JSON_NULL : new JSONString( URL.encode( params.getPageOffsetText())));
		json.put(IRequestParamsNames.HIDE_FILTER	 ,new JSONNumber( params.isHideFilter()?1:0));
		json.put(IRequestParamsNames.HEADER_TEXT	 ,AonStringUtils.isBlank(params.getHeaderText())? JSON_NULL : new JSONString( URL.encode( params.getHeaderText())));
		json.put(IRequestParamsNames.HIDE_DATETIME_ON_FOOTER,new JSONNumber( params.isHideDateTimeOnFooter()?1:0));
		json.put(IRequestParamsNames.FOOTER_TEXT	 ,AonStringUtils.isBlank(params.getFooterText())? JSON_NULL : new JSONString( URL.encode( params.getFooterText())));
		return json.toString();
	}
	
	public static String convert(AccountingReportParams params) {
		JSONObject json = new JSONObject();
		JSONNull JSON_NULL = JSONNull.getInstance();
		json.put(IRequestParamsNames.DOMAIN,new JSONNumber( params.getDomain()));
		if (params.getDomains() != null && params.getDomains().size() > 0) {
			JSONArray domains = new JSONArray();
			int i = 0;
			for (Domain d : params.getDomains()) {
				domains.set(i, new JSONNumber( d.getId() ));  
				i++;
			}
			json.put(IRequestParamsNames.DOMAINS,domains);
		}
		json.put(IRequestParamsNames.CONSOLIDATION	 ,new JSONNumber(params.isConsolidation()?1:0));
		
		json.put(IRequestParamsNames.PERIOD,params.getPeriod() 		== null? JSON_NULL : new JSONNumber( params.getPeriod()));
		if (params.getAccount() != null) {
			json.put(IRequestParamsNames.ACCOUNT,params.getAccount().getId() == null? JSON_NULL : new JSONNumber( params.getAccount().getId()));
			json.put(IRequestParamsNames.ACCOUNT_CODE,AonStringUtils.isBlank(params.getAccount().getCode())? JSON_NULL : new JSONString( params.getAccount().getCode()));
			json.put(IRequestParamsNames.ACCOUNT_DESCRIPTION,AonStringUtils.isBlank(params.getAccount().getDescription())? JSON_NULL : new JSONString( params.getAccount().getDescription()));
		} else {
			json.put(IRequestParamsNames.ACCOUNT,JSON_NULL);
			json.put(IRequestParamsNames.ACCOUNT_CODE,JSON_NULL);
			json.put(IRequestParamsNames.ACCOUNT_DESCRIPTION,JSON_NULL);
		}
		json.put(IRequestParamsNames.FROM_DATE,params.getFromDate() == null? JSON_NULL : new JSONString( FORMATTER.format(params.getFromDate())));
		json.put(IRequestParamsNames.TO_DATE,params.getToDate() == null? JSON_NULL : new JSONString( FORMATTER.format(params.getToDate())));
		json.put(IRequestParamsNames.ACTIVITY,params.getActivity() == null? JSON_NULL : new JSONNumber( params.getActivity()));
		json.put(IRequestParamsNames.CONFIDENTIAL,params.getSecurityLevel() == null?JSON_NULL :new JSONNumber( params.getSecurityLevel().value()));
		json.put(IRequestParamsNames.DOCUMENT,AonStringUtils.isBlank(params.getDocumentNumber())? JSON_NULL : new JSONString( params.getDocumentNumber()));
		
		json.put(IRequestParamsNames.LEVEL,new JSONNumber( params.getLevel()));
		json.put(IRequestParamsNames.PREVIOUS_PERIODS,new JSONNumber( params.getPreviousPeriods()));
		json.put(IRequestParamsNames.LOW_LEVEL_ACCOUNT_VISIBLE,new JSONNumber( params.isLowLevelAccountVisible()?1:0));
		json.put(IRequestParamsNames.NO_ACTIVITY_ACCOUNT_VISIBLE,new JSONNumber( params.isNoActivityAccountVisible()?1:0));
		json.put(IRequestParamsNames.OPERATING_ENTRIES_EXCLUDED,new JSONNumber( params.areOperatingEntriesExcluded()?1:0));
		json.put(IRequestParamsNames.CLOSING_ENTRIES_EXCLUDED,new JSONNumber( params.areClosingEntriesExcluded()?1:0));
		json.put(IRequestParamsNames.BREAKDOWN_ENABLED,new JSONNumber( params.isBreakdownEnabled()?1:0));
		
		json.put(IRequestParamsNames.PERCENTS_ENABLED,new JSONNumber( params.isPercentsEnabled()?1:0));
		json.put(IRequestParamsNames.BY_MONTH,new JSONNumber( params.isByMonth()?1:0));
		json.put(IRequestParamsNames.BALANCE_TYPE,params.getBalanceType()==null?JSON_NULL:new JSONNumber( params.getBalanceType().ordinal()));
		json.put(IRequestParamsNames.TITLE,AonStringUtils.isBlank(params.getTitle())? JSON_NULL : new JSONString( URL.encode( params.getTitle() )));
		json.put(IRequestParamsNames.SUBJECT,AonStringUtils.isBlank(params.getSubject())? JSON_NULL : new JSONString( URL.encode( params.getSubject())));
		json.put(IRequestParamsNames.SHOW_COVER,new JSONNumber( params.isShowCover()?1:0));
		json.put(IRequestParamsNames.PAGE_OFFSET,new JSONNumber( params.getPageOffset()));
		json.put(IRequestParamsNames.PAGE_OFFSET_TEXT,AonStringUtils.isBlank(params.getPageOffsetText())? JSON_NULL : new JSONString( URL.encode( params.getPageOffsetText())));
		json.put(IRequestParamsNames.HIDE_FILTER,new JSONNumber( params.isHideFilter()?1:0));
		json.put(IRequestParamsNames.HEADER_TEXT,AonStringUtils.isBlank(params.getHeaderText())? JSON_NULL : new JSONString( URL.encode( params.getHeaderText())));
		json.put(IRequestParamsNames.HIDE_DATETIME_ON_FOOTER,new JSONNumber( params.isHideDateTimeOnFooter()?1:0));
		json.put(IRequestParamsNames.FOOTER_TEXT,AonStringUtils.isBlank(params.getFooterText())? JSON_NULL : new JSONString( URL.encode( params.getFooterText())));
		
		if (params.getCostCenters() == null || params.getCostCenters().size() == 0) {
			json.put(IRequestParamsNames.COST_CENTERS,JSON_NULL);
		} else {
			JSONArray costCenters = new JSONArray();
			int i = 0;
			for (String cc : params.getCostCenters()) {
				if (AonStringUtils.isNotBlank(cc)) {
					costCenters.set(i, new JSONString( cc ));  
					i++;
				}
			}
			json.put(IRequestParamsNames.COST_CENTERS,costCenters);
		}
		
		json.put(IRequestParamsNames.REGISTRY 		 ,params.getRegistry() 			== null? JSON_NULL : new JSONNumber( params.getRegistry()));
		json.put(IRequestParamsNames.PERCENT  		 ,params.getPercent()  			== null? JSON_NULL : new JSONNumber( params.getPercent()));
		json.put(IRequestParamsNames.VAT_SUMMARY_TYPE,params.getVatSummaryType() 	== null? JSON_NULL : new JSONNumber( params.getVatSummaryType().ordinal()));
		json.put(IRequestParamsNames.OUTPUT   		 ,params.getOutput() 			== null? JSON_NULL : new JSONNumber( params.getOutput()?1:0));
		json.put(IRequestParamsNames.SURCHARGE		 ,params.getSurcharge() 			== null? JSON_NULL : new JSONNumber( params.getSurcharge()?1:0));
		json.put(IRequestParamsNames.FARMER_REGIME	 ,params.getFarmerRegime() 		== null? JSON_NULL : new JSONNumber( params.getFarmerRegime()?1:0));
		json.put(IRequestParamsNames.ACCRUAL_REGIME	 ,params.getAccrualRegime() 		== null? JSON_NULL : new JSONNumber( params.getAccrualRegime()?1:0));
		json.put(IRequestParamsNames.INVESTMENT		 ,params.getInvestment() 		== null? JSON_NULL : new JSONNumber( params.getInvestment()?1:0));
		json.put(IRequestParamsNames.SERVICE		 ,params.getService() 			== null? JSON_NULL : new JSONNumber( params.getService()?1:0));
		json.put(IRequestParamsNames.RECTIFICATION	 ,params.getRectificationType()	== null? JSON_NULL : new JSONNumber( params.getRectificationType().ordinal()));
		
		return json.toString();
	}

	public static String convert(OperationParams params) {		
		JSONObject json = new JSONObject();
		JSONNull JSON_NULL = JSONNull.getInstance();
		json.put(IRequestParamsNames.DOMAIN   		,new JSONNumber( params.getDomain()));
		json.put(IRequestParamsNames.ACTIVITY 		,params.getActivity() 	== null? JSON_NULL : new JSONNumber( params.getActivity()));
		json.put(IRequestParamsNames.ACTIVITY_DESCRIPTION, params.getActivityDescription()== null? JSON_NULL : new JSONString( params.getActivityDescription()));		
		json.put(IRequestParamsNames.FROM_DATE 		,params.getFromDate() 	== null? JSON_NULL : new JSONString( FORMATTER.format(params.getFromDate())));
		json.put(IRequestParamsNames.TO_DATE  	 	,params.getToDate()   	== null? JSON_NULL : new JSONString( FORMATTER.format(params.getToDate())));
		json.put(IRequestParamsNames.EXPENSES		,params.getExpenses()	== null? JSON_NULL : new JSONNumber( params.getExpenses()?1:0));
		json.put(IRequestParamsNames.IRPF			,params.getIrpf()		== null? JSON_NULL : new JSONNumber( params.getIrpf()?1:0));
		json.put(IRequestParamsNames.AEAT_BOOK		,params.getAeatBook()	== null? JSON_NULL : new JSONNumber( params.getAeatBook()?1:0));
		json.put(IRequestParamsNames.UNIFIED_BOOK	,params.getUnifiedBook()== null? JSON_NULL : new JSONNumber( params.getUnifiedBook()?1:0));
		return json.toString();
	}
	
	public static String convert(FinanceParams params) {		
		JSONObject json = new JSONObject();
		JSONNull JSON_NULL = JSONNull.getInstance();
		json.put(IRequestParamsNames.DOMAIN   		,new JSONNumber( params.getDomain()));
		json.put(IRequestParamsNames.FROM_DATE 		,params.getFromInvoiceDate() == null? JSON_NULL : new JSONString( FORMATTER.format(params.getFromInvoiceDate())));
		json.put(IRequestParamsNames.TO_DATE  	 	,params.getToInvoiceDate()   == null? JSON_NULL : new JSONString( FORMATTER.format(params.getToInvoiceDate())));
		json.put(IRequestParamsNames.FROM_DUE_DATE 		,params.getFromDueDate() == null? JSON_NULL : new JSONString( FORMATTER.format(params.getFromDueDate())));
		json.put(IRequestParamsNames.TO_DUE_DATE  	 	,params.getToDueDate()   == null? JSON_NULL : new JSONString( FORMATTER.format(params.getToDueDate())));
		json.put(IRequestParamsNames.SECURITY_LEVEL ,params.getSecurityLevel() == null?JSON_NULL :new JSONNumber( params.getSecurityLevel().value()));
		json.put(IRequestParamsNames.HAS_CONFIDENTIALITY_ROLE,new JSONNumber( params.hasConfidentialityRole()?1:0));
		json.put(IRequestParamsNames.PAYMENT		,params.getPayment()   == null? JSON_NULL : new JSONNumber( params.getPayment()?1:0));
		json.put(IRequestParamsNames.PENDING,new JSONNumber( params.isPending()?1:0));
		json.put(IRequestParamsNames.BATCHED,new JSONNumber( params.isBatched()?1:0));
		json.put(IRequestParamsNames.RETURNED,new JSONNumber( params.isReturned()?1:0));
		json.put(IRequestParamsNames.PAID,new JSONNumber( params.isPaid()?1:0));
		json.put(IRequestParamsNames.SETTLED,new JSONNumber( params.isSettled()?1:0));
		json.put(IRequestParamsNames.REGISTRY 		,params.getRegistry() == null? JSON_NULL : new JSONNumber( params.getRegistry()));
		json.put(IRequestParamsNames.AMOUNT			,params.getAmount()   == null? JSON_NULL : new JSONNumber( params.getAmount()));		
		json.put(IRequestParamsNames.NEARBY_NUMBERS,new JSONNumber( params.isNearbyNumbers()?1:0));
		json.put(IRequestParamsNames.CONCEPT  	 	 ,AonStringUtils.isBlank(params.getConcept())? JSON_NULL : new JSONString( params.getConcept()));
		json.put(IRequestParamsNames.REFERENCE_CODE,AonStringUtils.isBlank(params.getReferenceCode())? JSON_NULL : new JSONString( params.getReferenceCode()));
		json.put(IRequestParamsNames.PAY_METHOD	,params.getPayMethod() 		== null? JSON_NULL : new JSONNumber( params.getPayMethod()));		
		json.put(IRequestParamsNames.ORDER_BY		,new JSONNumber( params.getOrder()));		
		return json.toString();
	}
}
