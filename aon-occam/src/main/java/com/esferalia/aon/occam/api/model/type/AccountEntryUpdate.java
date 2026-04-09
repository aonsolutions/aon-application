package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.IAccountEntryWrapper;


public enum AccountEntryUpdate  implements Serializable {
	
	MANUAL_TYPE ("Marcar como asiento manual") {
		@Override public <T> T visit(AccountEntryUpdateVisitor<T> visitor, IAccountEntryWrapper wrapper) { return visitor.visitManualType(wrapper);}},
	OPENING_TYPE ("Marcar como asiento de apertura") {
		@Override public <T> T visit(AccountEntryUpdateVisitor<T> visitor, IAccountEntryWrapper wrapper) { return visitor.visitOpeningType(wrapper);}},
	SECURITY_LEVEL ("Modificar confidencialidad") {
		@Override public <T> T visit(AccountEntryUpdateVisitor<T> visitor, IAccountEntryWrapper wrapper) { return visitor.visitSecurityLevel(wrapper);}},
	INVESTMENT ("Modificar marca de inversi\u00F3n") {
		@Override public <T> T visit(AccountEntryUpdateVisitor<T> visitor, IAccountEntryWrapper wrapper) { return visitor.visitInvestment(wrapper);}},
	SERVICE ("Modificar Servicio") {
		@Override public <T> T visit(AccountEntryUpdateVisitor<T> visitor, IAccountEntryWrapper wrapper) { return visitor.visitService(wrapper);}},
	VAT_ACCRUAL_PAYMENT ("Modificar Criterio de caja") {
		@Override public <T> T visit(AccountEntryUpdateVisitor<T> visitor, IAccountEntryWrapper wrapper) { return visitor.visitVatAccrualPayment(wrapper);}},
	TAX_DATE ("Modificar Fecha IVA") {
		@Override public <T> T visit(AccountEntryUpdateVisitor<T> visitor, IAccountEntryWrapper wrapper) { return visitor.visitTaxDate(wrapper);}},
	ACTIVITY ("Modificar Actividad") {
		@Override public <T> T visit(AccountEntryUpdateVisitor<T> visitor, IAccountEntryWrapper wrapper) { return visitor.visitActivity(wrapper);}},
	WITHHOLDING_TYPE ("Modificar tipo de retenci\u00F3n") {
		@Override public <T> T visit(AccountEntryUpdateVisitor<T> visitor, IAccountEntryWrapper wrapper) { return visitor.visitWithholdingType(wrapper);}},
	OPERATING_ACCOUNT ("Modificar cuenta de explotaci\u00F3n") {
		@Override public <T> T visit(AccountEntryUpdateVisitor<T> visitor, IAccountEntryWrapper wrapper) { return visitor.visitOperatingAccount(wrapper);}},
	;

	private String description;
	
	private AccountEntryUpdate( String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
	
	public Byte getValue() {
		return (byte) ordinal();
	}
	
	public static AccountEntryUpdate safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	public static AccountEntryUpdate safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= AccountEntryUpdate.values().length) return null;
		return AccountEntryUpdate.values()[i];
	}

	public abstract <T> T visit(AccountEntryUpdateVisitor<T> visitor, IAccountEntryWrapper wrapper);

	public static interface AccountEntryUpdateVisitor<T> {
		T visitManualType(IAccountEntryWrapper wrapper);
		T visitOpeningType(IAccountEntryWrapper wrapper);
		T visitSecurityLevel(IAccountEntryWrapper wrapper);
		T visitInvestment(IAccountEntryWrapper wrapper);
		T visitTaxDate(IAccountEntryWrapper wrapper);
		T visitActivity(IAccountEntryWrapper wrapper);
		T visitService(IAccountEntryWrapper wrapper);
		T visitVatAccrualPayment(IAccountEntryWrapper wrapper);
		T visitWithholdingType(IAccountEntryWrapper wrapper);
		T visitOperatingAccount(IAccountEntryWrapper wrapper);
	}
	
}