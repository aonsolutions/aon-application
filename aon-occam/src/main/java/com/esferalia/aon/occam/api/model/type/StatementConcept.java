package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

public enum StatementConcept implements Serializable{
	
	UNKNOWN,
    WITHDRAWAL,
    DEPOSIT,
    PAYMENT,
    COLLECTION, 
    LOAN,
    COLLECTION_BATCH,
    SUBSCRIPTION,
    AMORTIZATION,
    STOCK_EXCHANGE,
    GAS_CHEQUE,
    CASH_POINT,
    CREDIT_CARD,
    FOREIGN_OPERATION,
    RETURNED,
    SALARY,
    FISCAL_STAMP,
    INTEREST_COMMISSION,
    CANCELLATION,
    OTHER;
	
	public byte value() {
		return (byte) this.ordinal();
	}
	
	public String getName() {
		return this.toString();
	}
}
