package com.esferalia.aon.occam.api.model.tedi;

import java.io.Serializable;

public enum TediContextKey implements Serializable {
	// Á --> \u00C1 á --> \u00E1 
	// É --> \u00C9 é --> \u00E9 
	// Í --> \u00CD í --> \u00ED 
	// Ó --> \u00D3 ó --> \u00F3 
	// Ú --> \u00DA ú --> \u00FA ... acento
	// Ü --> \u00DC ü --> \u00fc ... diéresis
	// Ñ --> \u00D1 ñ --> \u00F1
	// º --> \u00BA ª --> \u00AA 
	// ¿ --> \u00BF
	
	DOMAIN("Dominio") {
		@Override
		public void visit(TediResult result,ITediContextVisitor visitor) {
			visitor.visitDomain(result);
		}
	},
	TYPE("Tipo de factura") {
		@Override
		public void visit(TediResult result,ITediContextVisitor visitor) {
			visitor.visitType(result);
		}
	}, 
	SERIES("Serie") {
		@Override
		public void visit(TediResult result,ITediContextVisitor visitor) {
			visitor.visitSeries(result);
		}
	},
	NUMBER("N\u00FAmero") {
		@Override
		public void visit(TediResult result,ITediContextVisitor visitor) {
			visitor.visitNumber(result);
		}
	},
	REFERENCE_CODE("N\u00BA factura") {
		@Override
		public void visit(TediResult result,ITediContextVisitor visitor) {
			visitor.visitReferenceCode(result);
		}
	},
	TRANSACTION("Tipo de transacci\u00F3n") {
		@Override
		public void visit(TediResult result,ITediContextVisitor visitor) {
			visitor.visitTransaction(result);
		}
	},
	ISSUE_DATE ("Fecha de factura") {
		@Override
		public void visit(TediResult result,ITediContextVisitor visitor) {
			visitor.visitIssueDate(result);
		}
	},
	TAX_DATE ("Fecha de IVA") {
		@Override
		public void visit(TediResult result,ITediContextVisitor visitor) {
			visitor.visitTaxDate(result);
		}
	},
	SCOPE("\u00C1mbito") {
		@Override
		public void visit(TediResult result,ITediContextVisitor visitor) {
			visitor.visitScope(result);
		}
	},
	REGISTRY ("Titular de la factura") {
		@Override
		public void visit(TediResult result,ITediContextVisitor visitor) {
			visitor.visitRegistry(result);
		}
	},
	RDOCUMENT ("N\u00BA documento del titular") {
		@Override
		public void visit(TediResult result,ITediContextVisitor visitor) {
			visitor.visitRdocument(result);
		}
	},
	RDOCUMENT_COUNTRY("Pa\u00CDs del documento del titular") {
		@Override
		public void visit(TediResult result,ITediContextVisitor visitor) {
			visitor.visitRdocumentCountry(result);
		}
	},
	RNAME("Raz\u00F3n social del titular") {
		@Override
		public void visit(TediResult result,ITediContextVisitor visitor) {
			visitor.visitRname(result);
		}
	},
	ADDRESS("Direcci\u00F3n del titular") {
		@Override
		public void visit(TediResult result,ITediContextVisitor visitor) {
			visitor.visitAddress(result);
		}
	},
	DETAIL_DESCRIPTION("Descripci\u00F3n de la l\u00EDnea") {
		@Override
		public void visit(TediResult result,ITediContextVisitor visitor) {
			visitor.visitDetailDescription(result);
		}
	}
	
	;

	private String description;

	private TediContextKey() {
	}

	private TediContextKey(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
	public abstract void visit(TediResult result , ITediContextVisitor visitor);
}