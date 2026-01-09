package com.esferalia.aon.occam.api.model.type;

public enum PrepaymentCollect  {

    FEE
    ,INVOICE_DETAIL
    ;
	
	public byte value(){
		return (byte) this.ordinal();
	}

}