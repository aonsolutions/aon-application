package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.FiscalModelDeclarationType;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.watson.util.AonStringUtils;

public interface IFiscalModel extends Serializable {

	Integer getId();
	int getDomain();
	String getDomainName();
	FiscalModelType getModel();
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
	Double getDeclarationResult();
	FiscalModelDeclarationType getDeclarationResultType();
	
	@Deprecated
	IFiscalModelKey getDeclarationTypeKey();
	@Deprecated
	double getResult();
	@Deprecated
	public default FiscalModelDeclarationType getDeclarationType() {
		return null;
	}
	
	public default Finance getFinance() {
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
	public default boolean isStrictToDeposit() {
		return false;
	};
	
	
	public default boolean canBeSent() {
		return isFinished() || isCustomerAccepted(); 
	}
	
	// El modelo puede ser enviado al "Servicio de validación y prueba de impresión de la AEAT"
	// si "Puede ser Enviado" o "es Informativa y no está Presentado"
	public default boolean canBeValidated() {
		return canBeSent() || (getModel().isInformative() && !isSent()) ; 
	}
	
	public default boolean isNotEditable() {
		return !isEditable(); 
	}
	public default boolean isEditable() {
		return isPending() || isCustomerRejected(); 
	}
	public default boolean isPending() {
		return getStatus() == FiscalStatus.PENDING;
	}
	public default boolean isFinished() {
		return getStatus() == FiscalStatus.FINISHED;
	}
	public default boolean isSent() {
		return getStatus() == FiscalStatus.SENT;
	}
	public default boolean isBlocked() {
		return getStatus() == FiscalStatus.BLOCKED;
	}
	public default boolean isCustomerCheck() {
		return getStatus() == FiscalStatus.CUSTOMER_CHECK;
	}
	public default boolean isCustomerAccepted() {
		return getStatus() == FiscalStatus.CUSTOMER_ACCEPTED;
	}
	public default boolean isCustomerRejected() {
		return getStatus() == FiscalStatus.CUSTOMER_REJECTED;
	}
	
	public default boolean isGenerateFromYearStart() {
		return false;
	}
	
	@Deprecated
	public default boolean isNotFinished() {
		return isEditable();
	}
	
	public default String getModelFullName() {
		return AonStringUtils.defaultIfBlank(FiscalModelUtils.getModelName(this),
				(getModel() != null?getModel().getName():"???") ) 
			+ " "
			+ getYear()
			+ " "
			+ (getPeriod() != null?getPeriod().getDescription() :"???")
			+ (isComplementary()?" (C)":"")
			+ (isReplacement()?" (S)":"")
			;
	}
	
	public default String getNrc() {
		return "";
	}
	public default IFiscalModel setNrc(String nrc) {
		return this;
	}
	
	// FALTA - PARA LOS APLAZAMIENTOS
	
	public default int getPlazos() {
		return 0;
	}
	public default IFiscalModel setPlazos(int p) {
		return this;
	}
	
	// FALTA - LO PONGO COMO STRING PORQUE IGUAL ES MAS FACIL PARA EL MANEJO, ASI SOLO HABRA QUE CONVERTIRLA CUANDO SE EDITE EN ALGUN COMPONENTE VISUAL
//	public default Date getFechaPlazo() {
//		return null;
//	}
//	public default IFiscalModel setFechaPlazo(Date d) {
//		return this;
//	}
	public default String getFechaPlazo() {
		return "";
	}
	public default IFiscalModel setFechaPlazo(String d) {
		return this;
	}
	
}
