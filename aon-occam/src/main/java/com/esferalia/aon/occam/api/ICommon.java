package com.esferalia.aon.occam.api;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainGserviceaccount;
import com.esferalia.aon.occam.api.model.DomainGserviceaccountFilter;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.esferalia.aon.occam.api.model.FiscalParameters;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.WorkplaceFilter;
import com.esferalia.aon.occam.api.model.Filter.DomainFilter;
import com.esferalia.aon.occam.api.model.office.Tag;
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
	// WORKPLACE
	// --------------------------------------------
	
	public Workplace getWorkplace(AONContext ctx, WorkplaceFilter filter);
	public LinkedList<Workplace> getWorkplaceList(AONContext ctx, WorkplaceFilter filter);
	
	// --------------------------------------------
	// PRODUCT
	// --------------------------------------------
	public List<String> getProductTags(AONContext ctx);

	public Map<Integer, String[]> getProductTagMap(AONContext ctx);

	// --------------------------------------------
	// DOMAIN
	// --------------------------------------------
	
	public Domain getDomain(AONContext ctx, Integer domainId);
	public LinkedList<Domain> getDomainList(AONContext ctx, DomainFilter filter);
	
	public Domain insertDomain(AONContext ctx, Integer parentDomain,
			String document, String name, List<String> messages);
	
	public DomainGserviceaccount getDomainGserviceaccount(AONContext ctx);
	public LinkedList<DomainGserviceaccount> getDomainGserviceaccountList(AONContext ctx);
	public DomainGserviceaccount getDomainGserviceaccount(AONContext ctx, DomainGserviceaccountFilter filter);
	public void updateDomainGserviceaccount(AONContext ctx, String googleAccount);
	public void updateDomainGserviceaccount(AONContext ctx, DomainGserviceaccount dgsa);
	public void deleteDomainGserviceaccount(AONContext ctx);
	public void insertDomainGserviceaccount(AONContext ctx, DomainGserviceaccount dgsa);	

	public Integer[] getSonsDomains(AONContext ctx);
	
	// TAG
	public void updateTag(AONContext ctx, Tag tag);
	public void deleteTag(AONContext ctx, Tag tag);
}
