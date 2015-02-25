package com.esferalia.aon.occam.api;

import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.FiscalParameters;
import com.esferalia.aon.occam.api.model.type.AppParam;

public interface ICommon {

	// 				   		  APPLICATION PARATEMER
	public ApplicationParameter fetchOne(AONContext ctx,AppParam param);

	public FiscalParameters getFiscalParameters(AONContext ctx);		
	
}
