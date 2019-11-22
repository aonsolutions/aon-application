package com.esferalia.aon.occam.api.model.tedi;

import java.io.Serializable;

public enum TediContextKey  implements Serializable {
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
		public void visit(ITediContextVisitor visitor,ICallback callback) {
			visitor.visitDomain(callback);
		}
	},
	TYPE("Tipo de factura") {
		@Override
		public void visit(ITediContextVisitor visitor,ICallback callback) {
			visitor.visitType(callback);
		}
	}, 
	SERIES("Serie") {
		@Override
		public void visit(ITediContextVisitor visitor,ICallback callback) {
			visitor.visitSeries(callback);
		}
	},
	NUMBER("N\u00FAmero") {
		@Override
		public void visit(ITediContextVisitor visitor,ICallback callback) {
			visitor.visitNumber(callback);
		}
	},
	DUPLICATED_SERIES_NUMBER("Serie/N\u00FAmero") {
		@Override
		public void visit(ITediContextVisitor visitor,ICallback callback) {
			visitor.visitDuplicatedSeriesNumber(callback);
		}
	},
	REFERENCE_CODE("N\u00BA factura") {
		@Override
		public void visit(ITediContextVisitor visitor,ICallback callback) {
			visitor.visitReferenceCode(callback);
		}
	},
	DUPLICATED_REFERENCE_CODE("N\u00FAmero") {
		@Override
		public void visit(ITediContextVisitor visitor,ICallback callback) {
			visitor.visitDuplicatedReferenceCode(callback);
		}
	},
	TRANSACTION("Tipo de transacci\u00F3n") {
		@Override
		public void visit(ITediContextVisitor visitor,ICallback callback) {
			visitor.visitTransaction(callback);
		}
	},
	ISSUE_DATE ("Fecha de factura") {
		@Override
		public void visit(ITediContextVisitor visitor,ICallback callback) {
			visitor.visitIssueDate(callback);
		}
	},
	TAX_DATE ("Fecha de IVA") {
		@Override
		public void visit(ITediContextVisitor visitor,ICallback callback) {
			visitor.visitTaxDate(callback);
		}
	},
	SCOPE("\u00C1mbito") {
		@Override
		public void visit(ITediContextVisitor visitor,ICallback callback) {
			visitor.visitScope(callback);
		}
	},
	REGISTRY ("Titular de la factura") {
		@Override
		public void visit(ITediContextVisitor visitor,ICallback callback) {
			visitor.visitRegistry(callback);
		}
	},
	AMBIGUOUS_REGISTRY ("Titular de la factura") {
		@Override
		public void visit(ITediContextVisitor visitor,ICallback callback) {
			visitor.visitAmbiguousRegistry(callback);
		}
	},
	RDOCUMENT ("N\u00BA documento del titular") {
		@Override
		public void visit(ITediContextVisitor visitor,ICallback callback) {
			visitor.visitRdocument(callback);
		}
	},
	RDOCUMENT_COUNTRY("Pa\u00CDs del documento del titular") {
		@Override
		public void visit(ITediContextVisitor visitor,ICallback callback) {
			visitor.visitRdocumentCountry(callback);
		}
	},
	RNAME("Raz\u00F3n social del titular") {
		@Override
		public void visit(ITediContextVisitor visitor,ICallback callback) {
			visitor.visitRname(callback);
		}
	},
	ADDRESS("Direcci\u00F3n del titular") {
		@Override
		public void visit(ITediContextVisitor visitor,ICallback callback) {
			visitor.visitAddress(callback);
		}
	},
	DETAIL_DESCRIPTION("Descripci\u00F3n de la l\u00EDnea") {
		@Override
		public void visit(ITediContextVisitor visitor,ICallback callback) {
			visitor.visitDetailDescription(callback);
		}
	}, 
	DETAILS ("Detalles de la factura") {
		@Override
		public void visit(ITediContextVisitor visitor,ICallback callback) {
			visitor.visitDetails(callback);
		}
	},
	ACCOUNT_ENTRY ("Apunte contable") {
		@Override
		public void visit(ITediContextVisitor visitor,ICallback callback) {
			visitor.visitAccountEntry(callback);
		}
	},
	FINANCE_AMOUNT_ZERO("importe del vencimiento no puede ser cero") {
		@Override
		public void visit(ITediContextVisitor visitor,ICallback callback) {
			visitor.visitFinanceAmountZero(callback);
		}
	},
	FINANCE_WRONG_ACCOUNT_BANK("Cuenta bancaria incorrecta.") {
		@Override
		public void visit(ITediContextVisitor visitor,ICallback callback) {
			visitor.visitFinanceAccountBank(callback);
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
	public abstract void visit(ITediContextVisitor visitor,ICallback callback);
}