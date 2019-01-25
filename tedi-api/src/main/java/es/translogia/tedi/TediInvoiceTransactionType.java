package es.translogia.tedi;

public enum TediInvoiceTransactionType {

	NATIONAL ("Nacional"),
	INTRACOMMUNITY("Intracomunitaria"),
	EXTRACOMMUNITY("Extracomunitaria"),
	CAN_CEU_MEL("Canarias, Ceuta y Melilla"),
	OTHER_ISP("I.S.P.");
	
	String type;
	
	private TediInvoiceTransactionType(String type) {
		this.type = type;
	}
	
	public String getType() {
		return type;
	}
	
	public TediInvoiceTransactionType getTediInvoiceTransactionType(String type) {
		if(INTRACOMMUNITY.toString().equalsIgnoreCase(type) || INTRACOMMUNITY.getType().equals(type)) return INTRACOMMUNITY;
		if(EXTRACOMMUNITY.toString().equalsIgnoreCase(type) || EXTRACOMMUNITY.getType().equals(type)) return EXTRACOMMUNITY;
		if(CAN_CEU_MEL.toString().equalsIgnoreCase(type) || CAN_CEU_MEL.getType().equals(type)) return CAN_CEU_MEL;
		if(OTHER_ISP.toString().equalsIgnoreCase(type) || OTHER_ISP.getType().equals(type)) return OTHER_ISP;
		else return NATIONAL;
	}
}
