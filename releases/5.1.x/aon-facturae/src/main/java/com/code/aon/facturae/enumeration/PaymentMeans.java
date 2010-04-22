package com.code.aon.facturae.enumeration;

import com.code.aon.config.enumeration.PayMethodType;

public enum PaymentMeans {

	AL_CONTADO("01", PayMethodType.CASH_BASIS),
	RECIBO_DOMICILIADO("02"),
	RECIBO("03", PayMethodType.NEGOTIABLE_DOCUMENT),
	TRANSFERENCIA("04", PayMethodType.BANK_TRANSFER),
	LETRA_ACEPTADA("05"),
	CREDITO_DOCUMENTARIO("06"),
	CONTRATO_ADJUDICACION("07"),
	LETRA_DE_CAMBIO("08"),
	PAGARE_A_LA_ORDEN("09"),
	PAGARE_NO_A_LA_ORDEN("10"),
	CHEQUE("11", PayMethodType.CHEQUE),
	REPOSICION("12"),
	ESPECIALES("13"),
	COMPENSACION("14"),
	GIRO_POSTAL("15"),
	CHEQUE_CONFORMADO("16"),
	CHEQUE_BANCARIO("17"),
	PAGO_CONTRA_REEMBOLSO("18"),
	PAGO_MEDIANTE_TARJETA("19", PayMethodType.CREDIT_CARD);
	
	private String value;
	
	private PayMethodType payMethodType;
	
	PaymentMeans( String value ) {
		this( value, null );
	}

	PaymentMeans( String value, PayMethodType payMethodType ) {
		this.value = value;
		this.payMethodType = payMethodType;
	}
	
	public String getValue() {
		return value;
	}

	public PayMethodType getPayMethodType() {
		return payMethodType;
	}
	
	public static PaymentMeans getPaymentMeans( PayMethodType payMethodType ) {
		for( PaymentMeans pm : values() ) {
			if ( payMethodType == pm.getPayMethodType() ) {
				return pm;
			}
		}
		return null;
	}
	
}
