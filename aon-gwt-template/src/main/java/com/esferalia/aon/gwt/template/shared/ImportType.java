package com.esferalia.aon.gwt.template.shared;

public enum ImportType {

	PRODUCT,
	FEE,
	PROPOSAL,
	STOCK,
	DELIVERY,
	PROJECT_COMMERCIAL,
	CUSTOMER_IBAN,
	INVOICE,
	REGISTRY,
	DIARY,
	PGC;
	
	
	public String getName() {
		switch (this) {
		case INVOICE:
			return "Facturas";
		case DIARY:
			return "Diario";
		case PGC:
			return "Plan General Contable";
		case REGISTRY:
			return "Clientes, Proveedores y Acreedores";
		default:
			return this.toString();
		}
	}
}
