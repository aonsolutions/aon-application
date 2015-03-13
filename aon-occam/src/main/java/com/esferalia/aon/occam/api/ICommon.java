package com.esferalia.aon.occam.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.esferalia.aon.occam.api.model.FiscalParameters;
import com.esferalia.aon.occam.api.model.type.AppParam;

public interface ICommon {

	// --------------------------------------------
	// 				   		  APPLICATION PARATEMER
	// --------------------------------------------
	
	public ApplicationParameter fetchOne(AONContext ctx,AppParam param);
	public FiscalParameters getFiscalParameters(AONContext ctx);
	
	
	// --------------------------------------------
	//                                   ENTERPRISE
	// --------------------------------------------
	public Enterprise getEnterprise(AONContext ctx, int id);
	public ArrayList<Enterprise> getParentEnterprises(AONContext ctx,String query);
	
	// --------------------------------------------
	//                                      PRODUCT
	// --------------------------------------------
	public List<String> getProductTags(AONContext ctx);
	public Map<Integer,String[]> getProductTagMap(AONContext ctx);
	
}
