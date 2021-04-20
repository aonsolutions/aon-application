package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.FiscalModelDeclarationType;
import com.esferalia.aon.occam.api.model.type.Period;

public interface IFiscalModel extends Serializable {

	Integer getId();
	int getDomain();
	String getDomainName();
	FiscalModelType getModel();
	
	IFiscalModelKey getDeclarationTypeKey();
	double getResult();
	
	int getYear();
	Period getPeriod();
	Administration getAdministration();
	FiscalStatus getStatus();
	
	boolean isReplacement();
	boolean isComplementary();
	
	String getDocument();
	String getName();
	String getSurname();
	String getFullName();
	
	public default Finance getFinance() {
		return null;
	}
	public default FiscalModelDeclarationType getDeclarationType() {
		return null;
	}
	
	public default boolean isFirstPeriod() {
		return getPeriod() != null && getPeriod().isFirstPeriod();
	}
	public default boolean isLastPeriod() {
		return getPeriod() != null && getPeriod().isLastPeriod();
	}
	public default boolean isQuarterPeriod() {
		return getPeriod() != null && getPeriod().isQuarterPeriod();
	}
	public default boolean isMonthPeriod() {
		return getPeriod() != null && getPeriod().isMonthPeriod();
	}
	public default boolean isAraba() {
		return (getAdministration() == Administration.ALAVA);
	}
	public default boolean isBizkaia() {
		return (getAdministration() == Administration.BIZKAIA);
	}
	public default boolean isGipuzkoa() {
		return (getAdministration() == Administration.GIPUZKOA);
	}
	public default boolean isNavarra() {
		return (getAdministration() == Administration.NAVARRA);
	}
	public default boolean isAEAT() {
		return (getAdministration() == Administration.COMMON_TERRITORY);
	}
	public default boolean isFinished() {
		return getStatus() == FiscalStatus.FINISHED;
	}
	public default boolean isSent() {
		return getStatus() == FiscalStatus.SENT;
	}
	public default boolean isNotFinished() {
		return getStatus() != FiscalStatus.FINISHED;
	}
	public default boolean isBlocked() {
		return getStatus() == FiscalStatus.BLOCKED;
	}
	
}
