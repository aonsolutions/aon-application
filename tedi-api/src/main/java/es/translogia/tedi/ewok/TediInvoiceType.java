package es.translogia.tedi.ewok;

import java.io.Serializable;

import es.translogia.tedi.ewok.TediVisitors.TediInvoiceTypeVisitor;

public enum TediInvoiceType implements Serializable {

	EMITIDA {
		@Override
		public TediInvoiceTypeVisitor visit(TediInvoiceTypeVisitor visitor) {
			visitor.visitEMITIDA();
			return visitor;
		}
	},
	RECIBIDA {
		@Override
		public TediInvoiceTypeVisitor visit(TediInvoiceTypeVisitor visitor) {
			visitor.visitRECIBIDA();
			return visitor;
		}
	},
	TICKET {
		@Override
		public TediInvoiceTypeVisitor visit(TediInvoiceTypeVisitor visitor) {
			visitor.visitTICKET();
			return visitor;
		}
	};

	private TediInvoiceType() {
	}

	public abstract TediInvoiceTypeVisitor visit(TediInvoiceTypeVisitor visitor);

	public static TediInvoiceType safeValueof(String type) {
		if (type == null || "".equals(type.trim()) ) return null;
		type = type.toUpperCase();  
		return valueOf(type); 
	}
}
