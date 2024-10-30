package net.aonsolutions.occam.api.model.type;

import java.io.Serializable;

public enum VATTaxRegime implements Serializable {
	
	VAT_GENERAL("R\u00E9gimen General") {
		@Override
		public <T> T visit(VATTaxRegimeVisitor<T> visitor) {
			return visitor.visitVatGeneral();
		}
	},
	VAT_SURCHARGE("R\u00E9gimen especial recargo de equivalencia"){
		@Override
		public <T> T visit(VATTaxRegimeVisitor<T> visitor) {
			return visitor.visitVatSurcharge();
		}
	},
	VAT_SIMPLIFIED("R\u00E9gimen especial simplificado"){
		@Override
		public <T> T visit(VATTaxRegimeVisitor<T> visitor) {
			return visitor.visitVatSimplified();
		}
	},
	VAT_ACCRUAL_PAYMENT("R\u00E9gimen especial del criterio de caja"){
		@Override
		public <T> T visit(VATTaxRegimeVisitor<T> visitor) {
			return visitor.visitVatAccrualPayment();
		}
	},
	VAT_REBU_OPERATION("R\u00E9gimen especial bienes usados, objetos de arte, antig\u00fcedades y objetos de colecci\u00F3n, determinaci\u00F3n base imponible operaci\u00F3n por operaci\u00F3n"){
		@Override
		public <T> T visit(VATTaxRegimeVisitor<T> visitor) {
			return visitor.visitVatRebuOperation();
		}
	},
	VAT_REBU_PROFIT("R\u00E9gimen especial bienes usados, objetos de arte, antig\u00fcedades y objetos de colecci\u00F3n, determinaci\u00F3n base imponible mediante margen de beneficio global"){
		@Override
		public <T> T visit(VATTaxRegimeVisitor<T> visitor) {
			return visitor.visitVatRebuProfit();
		}
	},
	VAT_TRAVEL_AGENCY("R\u00E9gimen especial agencias de viajes"){
		@Override
		public <T> T visit(VATTaxRegimeVisitor<T> visitor) {
			return visitor.visitVatTravelAgency();
		}
	},
	VAT_AGRICULTURE("R\u00E9gimen especial agricultura, ganader\u00EDa y pesca"){
		@Override
		public <T> T visit(VATTaxRegimeVisitor<T> visitor) {
			return visitor.visitVatAgriculture();
		}
	},
	VAT_GOLD("R\u00E9gimen especial oro de inversi\u00F3n, realizaci\u00F3n de operaciones que puedan tributar por este r\u00E9gimen"){
		@Override
		public <T> T visit(VATTaxRegimeVisitor<T> visitor) {
			return visitor.visitVatGold();
		}
	},
	VAT_UNION_EXTERNAL("R\u00E9gimen exterior a la Uni\u00F3n"){
		@Override
		public <T> T visit(VATTaxRegimeVisitor<T> visitor) {
			return visitor.visitVatUnionExternal();
		}
	},
	VAT_UNION("R\u00E9gimen de la Uni\u00F3n"){
		@Override
		public <T> T visit(VATTaxRegimeVisitor<T> visitor) {
			return visitor.visitVatUnion();
		}
	},
	VAT_IMPORTATION("R\u00E9gimen de importaci\u00F3n"){
		@Override
		public <T> T visit(VATTaxRegimeVisitor<T> visitor) {
			return visitor.visitVatImportation();
		}
	},
	VAT_EXEMPT("Exenta"){
		@Override
		public <T> T visit(VATTaxRegimeVisitor<T> visitor) {
			return visitor.visitVatExempt();
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
	
	public abstract <T> T visit(VATTaxRegimeVisitor<T> visitor);
	public static interface VATTaxRegimeVisitor<T> {
		T visitVatGeneral();
		T visitVatSurcharge();
		T visitVatSimplified();
		T visitVatAccrualPayment();
		T visitVatRebuOperation();
		T visitVatRebuProfit();
		T visitVatTravelAgency();
		T visitVatAgriculture();
		T visitVatGold();
		T visitVatUnionExternal();
		T visitVatUnion();
		T visitVatImportation();
		T visitVatExempt();
	}

}
