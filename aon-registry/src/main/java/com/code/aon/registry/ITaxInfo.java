package com.code.aon.registry;

import com.code.aon.config.enumeration.InvoiceTransactionType;

/**
 * Interface that must implement all the entities which are susceptible to pay taxes
 */
public interface ITaxInfo {
	
    /**
     * Checks if a surcharge has to be applied.
     * 
     * @return true, if a surcharge has to be applied
     */
    public boolean isSurcharge();

    /**
     * Checks if is tax free or not.
     * 
     * @return true, if is tax free
     */
    public boolean isTaxFree();

    /**
     * Checks if a withholding is applied.
     * 
     * @return true, if a withholding is applied
     */
    public boolean isWithholding();

    /**
     * Return the transaction type for this ITaxInfo
     * 
     * @return InvoiceTransactionType the transaction type
     */
    public InvoiceTransactionType getTransaction();

}
