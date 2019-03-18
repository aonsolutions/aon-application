package es.translogia.tedi;

public enum TediTransaction {
	NACIONAL("NAC"),
	INTRACOMUNITARIA("INTR"),
	EXTRACOMUNITARIA("EXTR"),
	ISP("ISP"),
	CCM("CCM");

	String code;
	
	private TediTransaction(String code) {
		this.code = code;
	}
	
	public String getCode() {
		return code;
	}
	
	public static TediTransaction getTediInvoiceType(String code){
		if(NACIONAL.getCode().equals(code)) return  NACIONAL;
		else if(INTRACOMUNITARIA.getCode().equals(code)) return INTRACOMUNITARIA;
		else if(EXTRACOMUNITARIA.getCode().equals(code)) return EXTRACOMUNITARIA;
		else if(ISP.getCode().equals(code)) return ISP;
		else return CCM;
	}
}

