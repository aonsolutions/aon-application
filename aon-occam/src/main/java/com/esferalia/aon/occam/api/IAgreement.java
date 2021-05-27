package com.esferalia.aon.occam.api;

import com.esferalia.aon.occam.api.model.Agreement;
import com.esferalia.aon.watson.error.AonCoreException;

public interface IAgreement {
	
	void save (AONContext ctx, Agreement ...agreements) throws AonCoreException;

}
