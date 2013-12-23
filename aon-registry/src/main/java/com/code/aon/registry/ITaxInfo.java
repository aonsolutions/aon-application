package com.code.aon.registry;

import com.code.aon.config.enumeration.InvoiceTransactionType;

public interface ITaxInfo {
	
    public boolean isSurcharge();

    public boolean isWithholding();

    public boolean isWithholdingFarmer();

    public boolean isVatAccrualPayment();

    public InvoiceTransactionType getTransaction();

    public boolean isVatFree();

    public boolean isRetentionFree();

}
