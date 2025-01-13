package net.aonsolutions.occam.api.model;

import java.io.Serializable;
import java.util.EnumMap;
import java.util.Optional;

import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.occam.api.model.type.Administration;
import net.aonsolutions.occam.api.model.type.AppParam;

public class ApplicationParameters implements Serializable {
	
	private static final long serialVersionUID = 2837662236575674360L;
	private EnumMap<AppParam,ApplicationParameter> params;
	private EnumMap<AppParam,Account> accounts;
	
	public Optional<EnumMap<AppParam, ApplicationParameter>> getParams() {
		return Optional.ofNullable(params);
	}
	public ApplicationParameters setParams( EnumMap<AppParam,ApplicationParameter> params ) {
		this.params = params;
		return this;
	}
	
	public Optional<EnumMap<AppParam,Account>> getAccounts() {
		return Optional.ofNullable(accounts);
	}
	public ApplicationParameters putAccount( AppParam param, Account account) {
		if ( accounts == null) accounts = new EnumMap<>(AppParam.class);
		accounts.put(param, account);
		return this;
	}
	public ApplicationParameters setAccounts( EnumMap<AppParam,Account> accounts) {
		this.accounts = accounts;
		return this;
	}

	
	private Optional<String> string(AppParam param) {
		return getParams()
			.map(p -> p.get(param))
			.map(ap -> ap.getValue());
	}
	private boolean bool(AppParam param) {
		return getParams()
			.map(p -> p.get(param))
			.map(ap -> ap.getValue())
			.map(AonStringUtils::trim )
			.map(v ->  v != null && ("true".equalsIgnoreCase(v) || "1".equals(v)))
			.orElse(false)
		;
	}
	private Optional<Integer> integer(AppParam param) {
		return getParams()
			.map(p -> p.get(param))
			.map(ap -> ap.getValue())
			.map(AonStringUtils::trim )
			.map(AonNumberUtils::toInteger)
		;
	}
	private Optional<Account> account(AppParam param) {
		return getAccounts()
			.map(p -> p.get(param))
		;
	}
	
	// ---------------------------------------------------------
	// -------------------------------------------- [GETTERS] --
	// ---------------------------------------------------------
	public boolean isBetaEnabled() {
		return bool(AppParam.AON_BETA_ENABLED);
	}
	public boolean isAlphaEnabled() {
		return bool(AppParam.AON_ALPHA_ENABLED);
	}
	public Optional<String> getDefaultInvoiceSeries() {
		return string(AppParam.ACC_DEFAULT_INVOICE_SERIES);
	}

	public Optional<Administration> getDefaultAdministration() {
		return string(AppParam.FS_DEFAULT_ADMINISTRATION)
			.flatMap(Administration::value);
	}
	
	public boolean isTbaiActive() {
		return bool(AppParam.TBAI_ACTIVE);
	}
	
	public boolean isTbaiTest() {
		return bool(AppParam.TBAI_TEST);
	}
	
	public Optional<String> getTbaiIncludeDate() {
		//date(AppParam.TBAI_INCLUDE_DATE, "yyyy-MM-dd");
		return string(AppParam.TBAI_INCLUDE_DATE);
	}

	public Optional<String> getTbaiRegistryDate() {
		return string(AppParam.TBAI_REGISTRY_DATE);
	}

	public Optional<String> getOperationsDeadline() {
		return string(AppParam.ACC_OPERATIONS_DEADLINE);
	}

	public Optional<Integer> getAccountingDefaultPeriod() {
		return integer(AppParam.ACC_DEFAULT_PERIOD);
	}

	// ---------------------------- [CUENTAS CONTABLE]
	
	public Optional<Account> getOutputVatDefaultAccount() {
		return account(AppParam.ACC_DEFAULT_CHARGED_VAT_ACC);
	}
	public Optional<Account> getInputVatDefaultAccount() {
		return account(AppParam.ACC_DEFAULT_PAID_VAT_ACC);
	}
	public Optional<Account> getSalesDefaultAccount() {
		return account(AppParam.ACC_DEFAULT_SALES_ACC);
	}
	public Optional<Account> getPurchaseDefaultAccount() {
		return account(AppParam.ACC_DEFAULT_PURCHASE_ACC);
	}
	public Optional<Account> getPrepaymentDefaultAccount() {
		return account(AppParam.ACC_DEFAULT_PREPAYMENT_ACC);
	}

	public Optional<Integer> getDirectTaxAdjustAccount() {
		return integer(AppParam.ACC_DIRECT_TAX_ADJUST_ACC);
	}
	public Optional<Account> getVatNegativeAdjustAccount() {
		return account(AppParam.ACC_VAT_NEGATIVE_ADJUST_ACC);
	}
	public Optional<Account> getWitholdingCreditorDefaultAccount() {
		return account(AppParam.ACC_DEFAULT_CHARGED_RET_ACC);
	}
}
