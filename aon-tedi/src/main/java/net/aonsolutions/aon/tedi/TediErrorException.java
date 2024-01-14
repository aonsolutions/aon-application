package net.aonsolutions.aon.tedi;

import com.esferalia.aon.occam.api.model.tedi.TediError;

public class TediErrorException extends Exception {
    
    private TediError tediError;
    
    public TediErrorException(TediError tediError) {
	this.tediError = tediError;
    }
    
    public TediError getTediError() {
	return tediError;
    }

}
