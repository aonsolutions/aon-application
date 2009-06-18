package com.code.aon.registry;

/**
 * Interface that must implement all the entities which are susceptible to pay taxes
 */
public interface ITaxInfo {
	
    /**
     * Checks if is tax free or not.
     * 
     * @return true, if is tax free
     */
    public boolean isTaxFree();

    /**
     * Checks if a surcharge has to be applied.
     * 
     * @return true, if a surcharge has to be applied
     */
    public boolean isSurcharge();
    
    
    /**
     * Checks if a withholding is applied.
     * 
     * @return true, if a withholding is applied
     */
    public boolean isWithholding();

}
