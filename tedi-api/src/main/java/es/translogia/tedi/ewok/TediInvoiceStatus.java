package es.translogia.tedi.ewok;

public enum TediInvoiceStatus {

	 PENDING("Pendiente")
	,SCORED("Contabilizada")
	,REFUSED("Rechazada")
	,TRASH("Papelera")
	;
	
	private String name;
	
	private TediInvoiceStatus(String name) {
		this.name = name;
	}
	
	public Byte value() {
		return (byte) ordinal();
	}
	
	public String getName() {
		return name;
	}
	
	public static TediInvoiceStatus safeValueOf( Byte i ) {
		if (i == null) return null;
		if (i < 0 || i >= TediInvoiceStatus.values().length) return null;
		return TediInvoiceStatus.values()[i];
	}
	
	public static TediInvoiceStatus safeValueOf(String value) {
		if(value != null) {
			try {
				return valueOf(value);
			} catch (Exception e) {}
		}
		return null;
	}

}
