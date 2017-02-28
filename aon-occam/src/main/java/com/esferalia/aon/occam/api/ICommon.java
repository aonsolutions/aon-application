package com.esferalia.aon.occam.api;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainGserviceaccount;
import com.esferalia.aon.occam.api.model.DomainGserviceaccountFilter;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.esferalia.aon.occam.api.model.Filter.ApplicationParameterFilter;
import com.esferalia.aon.occam.api.model.Filter.DomainFilter;
import com.esferalia.aon.occam.api.model.Filter.ProductTagFilter;
import com.esferalia.aon.occam.api.model.Filter.TagFilter;
import com.esferalia.aon.occam.api.model.Filter.TaxFilter;
import com.esferalia.aon.occam.api.model.FiscalParameters;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.WorkplaceFilter;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.product.ProductTag;
import com.esferalia.aon.occam.api.model.product.Tax;
import com.esferalia.aon.occam.api.model.type.AppParam;

public interface ICommon {

	// --------------------------------------------
	// CONFIGURATION
	// --------------------------------------------
	public AonConfiguration getConfiguration(AONContext ctx, Date atDate);

	// --------------------------------------------
	// APPLICATION PARATEMER
	// --------------------------------------------
	public Stream<ApplicationParameter> getApplicationParameterStream(AONContext ctx, ApplicationParameterFilter filter);
	public void deleteApplicationParameter(AONContext ctx, ApplicationParameterFilter filter);
	
	public ApplicationParameter fetchOne(AONContext ctx, AppParam param);
	public FiscalParameters getFiscalParameters(AONContext ctx);	
	public ApplicationParameter insertApplicationParameter(AONContext ctx, String param, String value);

	// --------------------------------------------
	// ENTERPRISE
	// --------------------------------------------
	public Enterprise getEnterprise(AONContext ctx, int id);
	public LinkedList<Enterprise> getParentEnterprises(AONContext ctx,String query);
	public LinkedList<CompanyBank> getCompanyBanks(AONContext ctx, int enterprise);
	public Company getCompany(AONContext ctx, int domain);
	public LinkedList<CompanyBank> getCompanyBanks(AONContext ctx);

	// --------------------------------------------
	// WORKPLACE
	// --------------------------------------------
	
	public Workplace getWorkplace(AONContext ctx, WorkplaceFilter filter);
	public LinkedList<Workplace> getWorkplaceList(AONContext ctx, WorkplaceFilter filter);
	
	// --------------------------------------------
	// PRODUCT
	// --------------------------------------------
	public Stream<ProductTag> getProductTagStream(AONContext ctx, ProductTagFilter filter);
	public List<String> getProductTags(AONContext ctx);

	public Map<Integer, String[]> getProductTagMap(AONContext ctx);

	// --------------------------------------------
	// DOMAIN
	// --------------------------------------------
	
	public LinkedList<Domain> getDriveDomainList(AONContext ctx);
	
	public Domain getDomain(AONContext ctx, Integer domainId);
	public Domain getDomain(AONContext ctx, DomainFilter filter);
	public LinkedList<Domain> getDomainList(AONContext ctx, DomainFilter filter);
	
	public Domain insertDomain(AONContext ctx, Integer parentDomain,
			String document, String name, List<String> messages);
	

	public DomainGserviceaccount getDomainGserviceaccount(AONContext ctx);
	public DomainGserviceaccount getGeneralDomainGserviceaccount(AONContext ctx);
	public HashMap<Integer, DomainGserviceaccount> getDomainGserviceaccountMap(AONContext ctx, Integer parent);
	public LinkedList<DomainGserviceaccount> getDomainGserviceaccountList(AONContext ctx);
	public DomainGserviceaccount getDomainGserviceaccount(AONContext ctx, DomainGserviceaccountFilter filter);
	public void updateDomainGserviceaccount(AONContext ctx, String googleAccount);
	public void updateDomainGserviceaccount(AONContext ctx, DomainGserviceaccount dgsa);
	public void deleteDomainGserviceaccount(AONContext ctx);
	public void insertDomainGserviceaccount(AONContext ctx, DomainGserviceaccount dgsa);	

	public Integer[] getSonsDomains(AONContext ctx);
	
	// TAG
	public Stream<Tag> getTagStream(AONContext ctx, TagFilter filter);
	public Tag insertTag(AONContext ctx, Tag tag);
	public void updateTag(AONContext ctx, Tag tag);
	public void deleteTag(AONContext ctx, Tag tag);
	
	//TAX
	public Stream<Tax> getTaxStream(AONContext ctx, TaxFilter filter);

}
