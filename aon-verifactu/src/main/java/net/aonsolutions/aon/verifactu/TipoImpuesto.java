package net.aonsolutions.aon.verifactu;

enum TipoImpuesto {
	IVA("01"), 	// Impuesto sobre el Valor Añadido (IVA).
	IPSI("02"), // Impuesto sobre la Producción, los Servicios y la Importación (IPSI) de Ceuta y Melilla.
	IGIC("03"), // Impuesto General Indirecto Canario (IGIC).
	OTRO("05")  // Otros.
	;

	private String value;
	private TipoImpuesto(String value) {
		this.value = value; 
	}
	
	public String getValue() {
		return value;
	}
}
