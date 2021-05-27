package net.aonsolutions.aon.gwt.seres.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public enum CommunicationTarget implements IsSerializable{

	OUTCOME_DELIVERY ("outcome_delivery"),
	OUTCOME_INVOICE ("outcome_invoice"),
	INCOME_SALES ("income_sales"),
	INCOME_INVOICE ("income_invoice"),
	INGENET_DELIVERY ("ingenet_delivery"),
	;

	private String value;
	
	private CommunicationTarget(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}

	public static CommunicationTarget getEnumByValue(String value) {
		for(CommunicationTarget target: values())
			if(target.getValue().equals(value)) return target;
		return null;
	}
	
}
