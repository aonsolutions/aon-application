package com.esferalia.aon.occam.api;

import java.util.List;

import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Agreement;
import com.esferalia.aon.watson.error.AonCoreException;

public interface IAgreement {
	
	void save (AONContext ctx, Agreement ...agreements) throws AonCoreException;

	List<Agreement> getAgreements(CloseableAONContext ctx, Integer domainId) throws AonCoreException;

}
