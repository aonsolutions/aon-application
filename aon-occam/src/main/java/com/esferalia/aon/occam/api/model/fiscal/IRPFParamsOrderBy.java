package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IRPFParamsOrderByVisitor;

public enum IRPFParamsOrderBy implements Serializable {

	INVOICE_ISSUE_DATE("Fecha de Factura"){

		@Override
		public <T,P> T visit(IRPFParamsOrderByVisitor<T,P> visitor, P p) {
			return visitor.visitInvoiceIssueDate(p);
		}
		
	},
	INVOICE_NUMBER("N\u00famero de Factura"){

		@Override
		public <T,P> T visit(IRPFParamsOrderByVisitor<T,P> visitor, P p) {
			return visitor.visitInvoiceNumber(p);
		}
		
	},
	INVOICE_REGISTRY_NAME("Nombre de Cliente/Proveedor/Acreedor"){

		@Override
		public <T,P> T visit(IRPFParamsOrderByVisitor<T,P> visitor, P p) {
			return visitor.visitInvoiceRegistryName(p);
		}
		
	},
	INVOICE_REGISTRY_DOCUMENT("NIF/DNI de Cliente/Proveedor/Acreedor"){

		@Override
		public <T,P> T visit(IRPFParamsOrderByVisitor<T,P> visitor, P p) {
			return visitor.visitInvoiceRegistryDocument(p);
		}
	},

	;
	
	private String description;
	private IRPFParamsOrderBy(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
	
	public static IRPFParamsOrderBy safeValueOf(Integer i) {
		if (i == null) return null;
		if (i < 0 || i >= IRPFParamsOrderBy.values().length) return null;
		return IRPFParamsOrderBy.values()[i];
	}

	public abstract <T,P> T visit(IRPFParamsOrderByVisitor<T,P> visitor, P p);

}
