package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IRPFParamsGroupedByVisitor;

public enum IRPFParamsGroupedBy implements Serializable {

	INVOICE("Factura"){

		@Override
		public <T,P> T visit(IRPFParamsGroupedByVisitor<T,P> visitor, P p) {
			return visitor.visitInvoice(p);
		}
		
	},
	REGISTRY("NIF/Raz\u00F3n social"){

		@Override
		public <T,P> T visit(IRPFParamsGroupedByVisitor<T,P> visitor, P p) {
			return visitor.visitRegistry(p);
		}
		
	},
	INVOICE_DETAIL("L\u00EDneas de factura"){

		@Override
		public <T,P> T visit(IRPFParamsGroupedByVisitor<T,P> visitor, P p) {
			return visitor.visitInvoiceDetail(p);
		}
		
	},
	;
	private String description;
	private IRPFParamsGroupedBy(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
	
	public static IRPFParamsGroupedBy safeValueOf(Integer i) {
		if (i == null) return null;
		if (i < 0 || i >= IRPFParamsGroupedBy.values().length) return null;
		return IRPFParamsGroupedBy.values()[i];
	}
	
	public abstract <T,P> T visit(IRPFParamsGroupedByVisitor<T,P> visitor, P p);

}
