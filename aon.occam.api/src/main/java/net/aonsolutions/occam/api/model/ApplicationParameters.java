package net.aonsolutions.occam.api.model;

import java.util.Date;
import java.util.EnumMap;
import java.util.Optional;

import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.occam.api.model.type.Administration;
import net.aonsolutions.occam.api.model.type.AppParam;

public class ApplicationParameters {
	
	private EnumMap<AppParam,ApplicationParameter> params;
	
	public Optional<EnumMap<AppParam, ApplicationParameter>> getParams() {
		return Optional.ofNullable(params);
	}
	
	public ApplicationParameters setParams( EnumMap<AppParam,ApplicationParameter> params ) {
		this.params = params;
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
	private Optional<Date> date(AppParam param, String pattern) {
		return getParams()
			.map(p -> p.get(param))
			.map(ap -> ap.getValue())
			.map(AonStringUtils::trim )
			.map(v ->  AonDateUtils.parse( v , pattern))
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
	
	// ---------------------------------------------------------
	// -------------------------------------------- [GETTERS] --
	// ---------------------------------------------------------
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
	
	public Optional<Date> getTbaiIncludeDate() {
		return date(AppParam.TBAI_INCLUDE_DATE, "yyyy-MM-dd");
	}

	public Optional<String> getTbaiRegistryDate() {
		return string(AppParam.TBAI_REGISTRY_DATE);
	}

	public Optional<Date> getOperationsDeadline() {
		return date(AppParam.ACC_OPERATIONS_DEADLINE, "dd/MM/yyyy");
	}

	public Optional<Integer> getAccountingDefaultPeriod() {
		return integer(AppParam.ACC_DEFAULT_PERIOD);
	}
	
}
