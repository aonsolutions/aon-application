package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.json.JsonFunctionalInterfaces.IAonAccountingReportParamsFromJSON;
import com.esferalia.aon.occam.api.json.JsonFunctionalInterfaces.IAonAccountingReportParamsToJSON;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.accounting.BalanceType;
import com.esferalia.aon.occam.api.model.fiscal.VatSummaryType;
import com.esferalia.aon.occam.api.model.type.RectificationType;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;

public enum AccountingReportParamsJSON {

	DOMAIN(
		(params, json) -> params.setDomain( JsonUtils.getInteger(json,IJsonNames.DOMAIN)),
		(params, json) -> json.put(IJsonNames.DOMAIN, params.getDomain())
	),
	DOMAIN_NAME(
		(params, json) -> params.setDomainName( json.optString(IJsonNames.DOMAIN_NAME)),
		(params, json) -> json.put(IJsonNames.DOMAIN_NAME, params.getDomainName())
	),
	USER(
		(params, json) -> params.setUser( JsonUtils.getString(json,IJsonNames.USER)),
		(params, json) -> json.putOpt(IJsonNames.USER, params.getUser())
	),
	PERIOD(
		(params, json) -> params.setPeriod(JsonUtils.getInteger(json,IJsonNames.PERIOD)),
		(params, json) -> json.put(IJsonNames.PERIOD, params.getPeriod())
	),
	FROM_DATE(
		(params, json) -> params.setFromDate(JsonUtils.getDate(json, IJsonNames.FROM_DATE)),
		(params, json) -> JsonUtils.putDate(json, IJsonNames.FROM_DATE, params.getFromDate())
	),
	TO_DATE(
		(params, json) -> params.setToDate(JsonUtils.getDate(json, IJsonNames.TO_DATE)),
		(params, json) -> JsonUtils.putDate(json, IJsonNames.TO_DATE, params.getToDate())
	),
	ACCOUNT(
		(params, json) -> params.setAccount( AccountJSON.fromJSON( json.optJSONObject(IJsonNames.ACCOUNT))),
		(params, json) -> json.putOpt(IJsonNames.ACCOUNT, AccountJSON.toJSON(  params.getAccount()))
	),
//	ACCOUNT_CODE(
//		(params, json) -> {
//			String accountCode = json.optString(IJsonNames.ACCOUNT_CODE, null);
//			if (AonStringUtils.isNotBlank(accountCode)) {
//				Account account = new Account();
//				account.setCode(accountCode);
//				params.setAccount(account);
//			}
//			return params;
//		},
//		(params, json) -> {
//			if ( params.getAccount() != null) {
//				json.put(IJsonNames.ACCOUNT_CODE, params.getAccount().getCode());			
//			}
//			return json;
//		}),
	ACTIVITY(
		(params, json) -> params.setActivity(JsonUtils.getInteger(json,IJsonNames.ACTIVITY)),
		(params, json) -> json.put(IJsonNames.ACTIVITY, params.getActivity())
	),
	SECURTIY_LEVEL(
		(params, json) -> params.setSecurityLevel( SecurityLevel.safeValueOf( JsonUtils.getInteger(json,IJsonNames.SECURITY_LEVEL) )),
		(params, json) -> JsonUtils.putEnum(json, IJsonNames.SECURITY_LEVEL, params.getSecurityLevel())
	),
	DOCUMENT_NUMBER(
		(params, json) -> params.setDocumentNumber( JsonUtils.getString(json,IJsonNames.DOCUMENT_NUMBER)), 
		(params, json) -> json.put(IJsonNames.DOCUMENT_NUMBER, params.getDocumentNumber())
	),
	PREVIOUS_PERIODS(
		(params, json) -> params.setPreviousPeriods(JsonUtils.getInteger(json,IJsonNames.PREVIOUS_PERIODS)),
		(params, json) -> json.put(IJsonNames.PREVIOUS_PERIODS, params.getPreviousPeriods())
	),
	BALANCE_TYPE(
		(params, json) -> params.setBalanceType( BalanceType.safeValueOf( JsonUtils.getInteger(json,IJsonNames.BALANCE_TYPE) )),
		(params, json) -> JsonUtils.putEnum(json, IJsonNames.BALANCE_TYPE, params.getBalanceType())
	),
	SELECTED_ACCOUNT(
		(params, json) -> params.setSelectedAccount( AccountJSON.fromJSON( json.optJSONObject(IJsonNames.SELECTED_ACCOUNT))),
		(params, json) -> json.putOpt(IJsonNames.SELECTED_ACCOUNT, AccountJSON.toJSON(  params.getSelectedAccount()))
	),
	SELECTED_PERIOD(
		(params, json) -> params.setSelectedPeriod( AccountPeriodJSON.fromJSON( json.optJSONObject(IJsonNames.SELECTED_PERIOD))),
		(params, json) -> json.putOpt(IJsonNames.SELECTED_PERIOD, AccountPeriodJSON.toJSON(  params.getSelectedPeriod()))
	),
	LEVEL(
		(params, json) -> params.setLevel(JsonUtils.getInteger(json,IJsonNames.LEVEL)),
		(params, json) -> json.put(IJsonNames.LEVEL, params.getLevel())
	),
	PERCENTS_ENABLED(
		(params, json) -> params.setPercentsEnabled(json.optBoolean(IJsonNames.PERCENTS_ENABLED)),
		(params, json) -> json.put(IJsonNames.PERCENTS_ENABLED, params.isPercentsEnabled())
	),
	BY_MONTH(
		(params, json) -> params.setByMonth(json.optBoolean(IJsonNames.BY_MONTH)),
		(params, json) -> json.put(IJsonNames.BY_MONTH, params.isByMonth())
	),
	LOW_LEVEL_ACCOUNT_VISIBLE(
		(params, json) -> params.setLowLevelAccountVisible(json.optBoolean(IJsonNames.LOW_LEVEL_ACCOUNT_VISIBLE)),
		(params, json) -> json.put(IJsonNames.LOW_LEVEL_ACCOUNT_VISIBLE, params.isLowLevelAccountVisible())
	),
	NO_ACTIVITY_ACCOUNT_VISIBLE(
		(params, json) -> params.setNoActivityAccountVisible(json.optBoolean(IJsonNames.NO_ACTIVITY_ACCOUNT_VISIBLE)),
		(params, json) -> json.put(IJsonNames.NO_ACTIVITY_ACCOUNT_VISIBLE, params.isNoActivityAccountVisible())
	),
	OPENING_ENTRIES_EXCLUDED(
		(params, json) -> params.setOperatingEntriesExcluded(json.optBoolean(IJsonNames.OPENING_ENTRIES_EXCLUDED)),
		(params, json) -> json.put(IJsonNames.OPENING_ENTRIES_EXCLUDED, params.areOperatingEntriesExcluded())
	),
	OPERATING_ENTRIES_EXCLUDED(
		(params, json) -> params.setOpeningEntriesExcluded(json.optBoolean(IJsonNames.OPERATING_ENTRIES_EXCLUDED)),
		(params, json) -> json.put(IJsonNames.OPERATING_ENTRIES_EXCLUDED, params.areOpeningEntriesExcluded())
	),
	CLOSING_ENTRIES_EXCLUDED(
		(params, json) -> params.setClosingEntriesExcluded(json.optBoolean(IJsonNames.CLOSING_ENTRIES_EXCLUDED)),
		(params, json) -> json.put(IJsonNames.CLOSING_ENTRIES_EXCLUDED, params.areClosingEntriesExcluded())
	),
	REVERSE_ORDER(
		(params, json) -> params.setReverseOrder(json.optBoolean(IJsonNames.REVERSE_ORDER)),
		(params, json) -> json.put(IJsonNames.REVERSE_ORDER, params.isReverseOrder())
	),
	BREAKDOWN_ENABLED(
		(params, json) -> params.setBreakdownEnabled(json.optBoolean(IJsonNames.BREAKDOWN_ENABLED)),
		(params, json) -> json.put(IJsonNames.BREAKDOWN_ENABLED, params.isBreakdownEnabled())
	),
	OUTPUT(
		(params, json) -> params.setOutput(json.optBoolean(IJsonNames.OUTPUT)),
		(params, json) -> json.put(IJsonNames.OUTPUT, params.getOutput())
	),
	TITLE(
		(params, json) -> params.setTitle( JsonUtils.decode(json,IJsonNames.TITLE) ),
		(params, json) -> json.put(IJsonNames.TITLE, params.getTitle())
	),
	SUBJECT(
		(params, json) -> params.setSubject( JsonUtils.decode(json,IJsonNames.SUBJECT) ),
		(params, json) -> json.put(IJsonNames.SUBJECT, params.getSubject())
	),
	SHOW_COVER(
		(params, json) -> params.setShowCover(json.optBoolean(IJsonNames.SHOW_COVER)),
		(params, json) -> json.put(IJsonNames.SHOW_COVER, params.isShowCover())
	),
	PAGE_OFFSET(
		(params, json) -> params.setPageOffset(JsonUtils.getInteger(json,IJsonNames.PAGE_OFFSET)),
		(params, json) -> json.put(IJsonNames.PAGE_OFFSET, params.getPageOffset())
	),
	PAGE_OFFSET_TEXT(
		(params, json) -> params.setPageOffsetText( JsonUtils.decode(json,IJsonNames.PAGE_OFFSET_TEXT) ),
		(params, json) -> json.put(IJsonNames.PAGE_OFFSET_TEXT, params.getPageOffsetText())
	),
	HIDE_FILTER(
		(params, json) -> params.setHideFilter(json.optBoolean(IJsonNames.HIDE_FILTER)),
		(params, json) -> json.put(IJsonNames.HIDE_FILTER, params.isHideFilter())
	),
	HEADER_TEXT(
		(params, json) -> params.setHeaderText( JsonUtils.decode(json,IJsonNames.HEADER_TEXT) ),
		(params, json) -> json.put(IJsonNames.HEADER_TEXT, params.getHeaderText())
	),
	HIDE_DATETIME_ON_FOOTER(
		(params, json) -> params.setHideDateTimeOnFooter(json.optBoolean(IJsonNames.HIDE_DATETIME_ON_FOOTER)),
		(params, json) -> json.put(IJsonNames.HIDE_DATETIME_ON_FOOTER, params.isHideDateTimeOnFooter())
	),
	FOOTER_TEXT(
		(params, json) -> params.setFooterText( JsonUtils.decode(json,IJsonNames.FOOTER_TEXT) ),
		(params, json) -> json.put(IJsonNames.FOOTER_TEXT, params.getFooterText())
	),
	LEDGER_ACCOUNT(
		(params, json) -> params.setLedgerAccount(JsonUtils.getInteger(json,IJsonNames.LEDGER_ACCOUNT)),
		(params, json) -> json.put(IJsonNames.LEDGER_ACCOUNT, params.getLedgerAccount())
	),
	LEDGER_DEBIT_BALANCE(
		(params, json) -> params.setLedgerDebitBalance(JsonUtils.getDouble(json,IJsonNames.LEDGER_DEBIT_BALANCE)),
		(params, json) -> json.put(IJsonNames.LEDGER_DEBIT_BALANCE, params.getLedgerDebitBalance())
	),
	LEDGER_UNPAID_BALANCE(
		(params, json) -> params.setLedgerUnpaidBalance(JsonUtils.getDouble(json,IJsonNames.LEDGER_UNPAID_BALANCE)),
		(params, json) -> json.put(IJsonNames.LEDGER_UNPAID_BALANCE, params.getLedgerUnpaidBalance())
	),
	REGISTRY(
		(params, json) -> params.setRegistry(JsonUtils.getInteger(json,IJsonNames.REGISTRY)),
		(params, json) -> json.put(IJsonNames.REGISTRY, params.getRegistry())
	),
	PERCENT(
		(params, json) -> params.setPercent(JsonUtils.getDouble(json,IJsonNames.PERCENT)),
		(params, json) -> json.put(IJsonNames.PERCENT, params.getPercent())
	),
	VAT_SUMMARY_TYPE(
		(params, json) -> params.setVatSummaryType( VatSummaryType.safeValueOf( JsonUtils.getInteger(json,IJsonNames.VAT_SUMMARY_TYPE) )),
		(params, json) -> JsonUtils.putEnum(json, IJsonNames.VAT_SUMMARY_TYPE, params.getVatSummaryType())
	),
	SURCHARGE(
		(params, json) -> params.setSurcharge(json.optBoolean(IJsonNames.SURCHARGE)),
		(params, json) -> json.put(IJsonNames.SURCHARGE, params.getSurcharge())
	),
	FARMER_REGIME(
		(params, json) -> params.setFarmerRegime(json.optBoolean(IJsonNames.FARMER_REGIME)),
		(params, json) -> json.put(IJsonNames.FARMER_REGIME, params.getFarmerRegime())
	),
	ACCRUAL_REGIME(
		(params, json) -> params.setAccrualRegime(json.optBoolean(IJsonNames.ACCRUAL_REGIME)),
		(params, json) -> json.put(IJsonNames.ACCRUAL_REGIME, params.getAccrualRegime())
	),
	INVESTMENT(
		(params, json) -> params.setInvestment(json.optBoolean(IJsonNames.INVESTMENT)),
		(params, json) -> json.put(IJsonNames.INVESTMENT, params.getInvestment())
	),
	SERVICE(
		(params, json) -> params.setService(json.optBoolean(IJsonNames.SERVICE)),
		(params, json) -> json.put(IJsonNames.SERVICE, params.getService())
	),
	RECTIFICATION_TYPE(
		(params, json) -> params.setRectificationType( RectificationType.safeValueOf( JsonUtils.getInteger(json,IJsonNames.RECTIFICATION) )),
		(params, json) -> JsonUtils.putEnum(json, IJsonNames.RECTIFICATION, params.getRectificationType())
	),
	CONSOLIDATION(
		(params, json) -> params.setConsolidation(json.optBoolean(IJsonNames.CONSOLIDATION)),
		(params, json) -> json.put(IJsonNames.CONSOLIDATION, params.isConsolidation())
	),
	DOMAINS(
		(params, json) -> {
			JSONArray domains = (JSONArray) json.opt(IJsonNames.DOMAINS);
			if (domains != null && domains.length() > 0) {
				LinkedList<Domain> list = new LinkedList<Domain>();
				for ( int i = 0; i < domains.length(); i++) {
					Object v = domains.get(i);
					list.add( new Domain().setId(  ((Long) v).intValue() ));
				}
				params.setDomains(list);
			}
			return params;
		},
		(params, json) -> {
			if (params.getDomains() != null && !params.getDomains().isEmpty()) {
				JSONArray domains = new JSONArray();
				int i = 0;
				for (Domain domain : params.getDomains()) {
					domains.put(i, DomainJSON.toJSON(domain));
					i++;
				}
				json.put(IJsonNames.DOMAINS, domains);
			}
			return json;
		}
	),
	;
	
	private IAonAccountingReportParamsFromJSON fromJSON;
	private IAonAccountingReportParamsToJSON toJSON;

	private AccountingReportParamsJSON(IAonAccountingReportParamsFromJSON fromJSON, IAonAccountingReportParamsToJSON toJSON) {
		this.fromJSON = fromJSON;
		this.toJSON = toJSON;
	}
	
	public static JSONObject toJSON(AccountingReportParams params) {
		JSONObject json = new JSONObject();
		for (AccountingReportParamsJSON p : AccountingReportParamsJSON.values()) {
			p.toJSON.to(params, json);
		}
		return json;
	}
	
	public static AccountingReportParams fromString(String text) {
		JSONObject json = new JSONObject(text);
		return fromJSON(json); 
	}

	public static AccountingReportParams fromJSON(JSONObject json) {
		AccountingReportParams params = new AccountingReportParams();
		if (json != null) {
			for (AccountingReportParamsJSON p : AccountingReportParamsJSON.values()) {
				p.fromJSON.from(params, json);
			}
		}
		return params;
	}

}
