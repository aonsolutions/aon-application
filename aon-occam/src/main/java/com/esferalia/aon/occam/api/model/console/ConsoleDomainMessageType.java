package com.esferalia.aon.occam.api.model.console;

import com.esferalia.aon.watson.util.AonStringUtils;

public enum ConsoleDomainMessageType {
	
	INTEGRITY 				{ @Override public <T> T visit(ConsoleDomainMessage cdm, ConsoleDomainMessageTypeVisitor<T> visitor) { return visitor.visitIntegrity(cdm); }},
	SCOPE_INTEGRITY 		{ @Override public <T> T visit(ConsoleDomainMessage cdm, ConsoleDomainMessageTypeVisitor<T> visitor) { return visitor.visitScopeIntegrity(cdm); }},
	PRODUCT 				{ @Override public <T> T visit(ConsoleDomainMessage cdm, ConsoleDomainMessageTypeVisitor<T> visitor) { return visitor.visitProduct(cdm); }},
	AGREEMENT 				{ @Override public <T> T visit(ConsoleDomainMessage cdm, ConsoleDomainMessageTypeVisitor<T> visitor) { return visitor.visitAgreement(cdm); }},
	INVOICE_PROCESS_OUTPUT  { @Override public <T> T visit(ConsoleDomainMessage cdm, ConsoleDomainMessageTypeVisitor<T> visitor) { return visitor.visitInvoiceProcessOutput(cdm); }}
	;

	public static ConsoleDomainMessageType safeValueOf( String i ) {
		if(AonStringUtils.isBlank(i)) return null;
		for (ConsoleDomainMessageType rs : values()) {
			if(i.equalsIgnoreCase(rs.name()))
				return rs;
		}
		return null;
	}

	public abstract <T> T visit(ConsoleDomainMessage cdm, ConsoleDomainMessageTypeVisitor<T> visitor);
	
	public static interface ConsoleDomainMessageTypeVisitor<T> {
		T visitIntegrity(ConsoleDomainMessage cdm);
		T visitScopeIntegrity(ConsoleDomainMessage cdm);
		T visitProduct(ConsoleDomainMessage cdm);
		T visitAgreement(ConsoleDomainMessage cdm);
		T visitInvoiceProcessOutput(ConsoleDomainMessage cdm);
	}
}
