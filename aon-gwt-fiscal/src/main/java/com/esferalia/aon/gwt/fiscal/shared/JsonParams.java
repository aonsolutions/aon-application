package com.esferalia.aon.gwt.fiscal.shared;

import com.esferalia.aon.occam.api.model.AccountEntryParams;
import com.esferalia.aon.occam.api.model.AccountStatementParams;
import com.esferalia.aon.occam.api.model.fiscal.IRPFParams;
import com.esferalia.aon.occam.api.model.fiscal.VatParams;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONNull;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;

public class JsonParams extends JSONObject {
	private static final DateTimeFormat FORMATTER = DateTimeFormat.getFormat("dd/MM/yyyy");
	 
	public static String convert(VatParams params) {
		JSONObject json = new JSONObject();
		JSONNull JSON_NULL = JSONNull.getInstance();
		json.put(IRequestParamsNames.DOMAIN   		,new JSONNumber( params.getDomain()));
		json.put(IRequestParamsNames.REGISTRY 		,params.getRegistry() 			== null? JSON_NULL : new JSONNumber( params.getRegistry()));
		json.put(IRequestParamsNames.ACTIVITY 		,params.getActivity() 			== null? JSON_NULL : new JSONNumber( params.getActivity()));
		json.put(IRequestParamsNames.FROM_DATE 		,params.getFromDate() 			== null? JSON_NULL : new JSONString( FORMATTER.format(params.getFromDate())));
		json.put(IRequestParamsNames.TO_DATE  	 	,params.getToDate()   			== null? JSON_NULL : new JSONString( FORMATTER.format(params.getToDate())));
		json.put(IRequestParamsNames.PERCENT  		,params.getPercent()  			== null? JSON_NULL : new JSONNumber( params.getPercent()));
		json.put(IRequestParamsNames.TYPE   	  	,params.getVatSummaryType() 	== null? JSON_NULL : new JSONNumber( params.getVatSummaryType().ordinal()));
		json.put(IRequestParamsNames.OUTPUT   		,params.getOutput() 			== null? JSON_NULL : new JSONNumber( params.getOutput()?1:0));
		json.put(IRequestParamsNames.SURCHARGE		,params.getSurcharge() 			== null? JSON_NULL : new JSONNumber( params.getSurcharge()?1:0));
		json.put(IRequestParamsNames.FARMER_REGIME	,params.getFarmerRegime() 		== null? JSON_NULL : new JSONNumber( params.getFarmerRegime()?1:0));
		json.put(IRequestParamsNames.ACCRUAL_REGIME	,params.getAccrualRegime() 		== null? JSON_NULL : new JSONNumber( params.getAccrualRegime()?1:0));
		json.put(IRequestParamsNames.INVESTMENT		,params.getInvestment() 		== null? JSON_NULL : new JSONNumber( params.getInvestment()?1:0));
		json.put(IRequestParamsNames.SERVICE		,params.getService() 			== null? JSON_NULL : new JSONNumber( params.getService()?1:0));
		json.put(IRequestParamsNames.RECTIFICATION	,params.getRectificationType()	== null? JSON_NULL : new JSONNumber( params.getRectificationType().ordinal()));
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
		json.put(IRequestParamsNames.DOMAIN   		,new JSONNumber( params.getDomain()));
		json.put(IRequestParamsNames.PERIOD   		,params.getPeriod() 		== null? JSON_NULL : new JSONNumber( params.getPeriod()));
		json.put(IRequestParamsNames.FROM_DATE 		,params.getFrom() 			== null? JSON_NULL : new JSONString( FORMATTER.format(params.getFrom())));
		json.put(IRequestParamsNames.TO_DATE  	 	,params.getTo()   			== null? JSON_NULL : new JSONString( FORMATTER.format(params.getTo())));
		json.put(IRequestParamsNames.TYPE   	  	,params.getType()	== null? JSON_NULL : new JSONNumber( params.getType().ordinal()));
		json.put(IRequestParamsNames.JOURNAL   		,params.getJournal() 			== null? JSON_NULL : new JSONNumber( params.getJournal()));
		json.put(IRequestParamsNames.ACTIVITY 		,params.getActivity() 			== null? JSON_NULL : new JSONNumber( params.getActivity()));
		json.put(IRequestParamsNames.CONFIDENTIAL	,new JSONNumber( params.isConfidential()?1:0));
		json.put(IRequestParamsNames.ACCOUNT   		,params.getAccount()			== null? JSON_NULL : new JSONNumber( params.getAccount()));
		json.put(IRequestParamsNames.DEBIT 			,params.getDebit()  			== null? JSON_NULL : new JSONNumber( params.getDebit()));
		json.put(IRequestParamsNames.CREDIT			,params.getDebit()  			== null? JSON_NULL : new JSONNumber( params.getCredit()));
		json.put(IRequestParamsNames.CONCEPT  	 	,AonStringUtils.isBlank(params.getConcept())? JSON_NULL : new JSONString( params.getConcept()));
		json.put(IRequestParamsNames.DOCUMENT 	 	,AonStringUtils.isBlank(params.getDocument())? JSON_NULL : new JSONString( params.getDocument()));
		json.put(IRequestParamsNames.BALANCING_ACCOUNT,params.getBalancingAccount() == null? JSON_NULL : new JSONNumber( params.getBalancingAccount()));
		json.put(IRequestParamsNames.COMMENTS		,AonStringUtils.isBlank(params.getComments())? JSON_NULL : new JSONString( params.getComments()));
		json.put(IRequestParamsNames.ORDER 			,new JSONNumber( params.getOrder()));
		return json.toString();
	}
	
	public static String convert(AccountStatementParams params) {
		JSONObject json = new JSONObject();
		JSONNull JSON_NULL = JSONNull.getInstance();
		json.put(IRequestParamsNames.DOMAIN   		,new JSONNumber( params.getDomain()));
		json.put(IRequestParamsNames.PERIOD   		,params.getPeriod() 		== null? JSON_NULL : new JSONNumber( params.getPeriod()));
		json.put(IRequestParamsNames.ACCOUNT   		,params.getAccount()			== null? JSON_NULL : new JSONNumber( params.getAccount()));
		json.put(IRequestParamsNames.FROM_DATE 		,params.getFromDate()		== null? JSON_NULL : new JSONString( FORMATTER.format(params.getFromDate())));
		json.put(IRequestParamsNames.TO_DATE  	 	,params.getToDate()   			== null? JSON_NULL : new JSONString( FORMATTER.format(params.getToDate())));
		json.put(IRequestParamsNames.ACTIVITY 		,params.getActivity() 			== null? JSON_NULL : new JSONNumber( params.getActivity()));
		json.put(IRequestParamsNames.CONFIDENTIAL	,params.getSecurityLevel() == null?JSON_NULL :new JSONNumber( params.getSecurityLevel().value()));
		json.put(IRequestParamsNames.DOCUMENT 	 	,AonStringUtils.isBlank(params.getDocumentNumber())? JSON_NULL : new JSONString( params.getDocumentNumber()));
		return json.toString();
	}
}
