package com.esferalia.aon.occam.api.model.finance;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IVATTaxRegimeVisitor;

public enum VATTaxRegime implements Serializable {
	
	VAT_GENERAL("R\u00E9gimen General") {
		@Override
		public void visit(IVATTaxRegimeVisitor visitor) {
			visitor.visitVatGeneral();
		}
	},
	VAT_SURCHARGE("R\u00E9gimen especial recargo de equivalencia"){
		@Override
		public void visit(IVATTaxRegimeVisitor visitor) {
			visitor.visitVatSurcharge();
		}
	},
	VAT_SIMPLIFIED("R\u00E9gimen especial simplificado"){
		@Override
		public void visit(IVATTaxRegimeVisitor visitor) {
			visitor.visitVatSimplified();
		}
	},
	VAT_ACCRUAL_PAYMENT("R\u00E9gimen especial del criterio de caja"){
		@Override
		public void visit(IVATTaxRegimeVisitor visitor) {
			visitor.visitVatAccrualPayment();
		}
	},
	VAT_REBU_OPERATION("R\u00E9gimen especial bienes usados, objetos de arte, antig\u00fcedades y objetos de colecci\u00F3n, determinaci\u00F3n base imponible operaci\u00F3n por operaci\u00F3n"){
		@Override
		public void visit(IVATTaxRegimeVisitor visitor) {
			visitor.visitVatRebuOperation();
		}
	},
	VAT_REBU_PROFIT("R\u00E9gimen especial bienes usados, objetos de arte, antig\u00fcedades y objetos de colecci\u00F3n, determinaci\u00F3n base imponible mediante margen de beneficio global"){
		@Override
		public void visit(IVATTaxRegimeVisitor visitor) {
			visitor.visitVatRebuProfit();
		}
	},
	VAT_TRAVEL_AGENCY("R\u00E9gimen especial agencias de viajes"){
		@Override
		public void visit(IVATTaxRegimeVisitor visitor) {
			visitor.visitVatTravelAgency();
		}
	},
	VAT_AGRICULTURE("R\u00E9gimen especial agricultura, ganader\u00EDa y pesca"){
		@Override
		public void visit(IVATTaxRegimeVisitor visitor) {
			visitor.visitVatAgriculture();
		}
	},
	VAT_GOLD("R\u00E9gimen especial oro de inversi\u00F3n, realizaci\u00F3n de operaciones que puedan tributar por este r\u00E9gimen"){
		@Override
		public void visit(IVATTaxRegimeVisitor visitor) {
			visitor.visitVatGold();
		}
	},
	VAT_UNION_EXTERNAL("R\u00E9gimen exterior a la Uni\u00F3n"){
		@Override
		public void visit(IVATTaxRegimeVisitor visitor) {
			visitor.visitVatUnionExternal();
		}
	},
	VAT_UNION("R\u00E9gimen de la Uni\u00F3n"){
		@Override
		public void visit(IVATTaxRegimeVisitor visitor) {
			visitor.visitVatUnion();
		}
	},
	VAT_IMPORTATION("R\u00E9gimen de importaci\u00F3n"){
		@Override
		public void visit(IVATTaxRegimeVisitor visitor) {
			visitor.visitVatImportation();
		}
	},
	VAT_EXEMPT("Exenta"){
		@Override
		public void visit(IVATTaxRegimeVisitor visitor) {
			visitor.visitVatExempt();
		}
	}
	;
	
	private String name;

	private VATTaxRegime(String name){
		this.name = name;
	}
	public String getName() {
		return name;
	}
	
	public void visit(IVATTaxRegimeVisitor visitor) {
		// Implemented on each type
	};
	
}