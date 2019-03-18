package es.translogia.tedi;

public enum TediInvoiceType {
	ISSUED("EMITIDA"),
	RECEIVED("RECIBIDA"),
	TICKET("TICKET");
	
	String type;
	
	private TediInvoiceType(String type) {
		this.type = type;
	}
	
	public String getType() {
		return type;
	}
	
	public static TediInvoiceType getTediInvoiceType(String type){
		if(ISSUED.getType().equals(type)) return  ISSUED;
		else if(RECEIVED.getType().equals(type)) return RECEIVED;
		else return TICKET;
	}
}

