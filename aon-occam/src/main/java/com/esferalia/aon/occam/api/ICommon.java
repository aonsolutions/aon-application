package com.esferalia.aon.occam.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.esferalia.aon.occam.api.model.FiscalParameters;
import com.esferalia.aon.occam.api.model.type.AppParam;

public interface ICommon {

	// --------------------------------------------
	// APPLICATION PARATEMER
	// --------------------------------------------

	public ApplicationParameter fetchOne(AONContext ctx, AppParam param);

	public FiscalParameters getFiscalParameters(AONContext ctx);

	// --------------------------------------------
	// ENTERPRISE
	// --------------------------------------------
	public Enterprise getEnterprise(AONContext ctx, int id);

	public ArrayList<Enterprise> getParentEnterprises(AONContext ctx,
			String query);

	public ArrayList<CompanyBank> getCompanyBanks(AONContext ctx, int enterprise);

	public Company getCompany(AONContext ctx, int domain);

	public ArrayList<CompanyBank> getCompanyBanks(AONContext ctx);

	// --------------------------------------------
	// PRODUCT
	// --------------------------------------------
	public List<String> getProductTags(AONContext ctx);

	public Map<Integer, String[]> getProductTagMap(AONContext ctx);

	// --------------------------------------------
	// DOMAIN
	// --------------------------------------------
	
	public Domain insertDomain(AONContext ctx, Integer parentDomain,
			String document, String name, List<String> messages);

}
