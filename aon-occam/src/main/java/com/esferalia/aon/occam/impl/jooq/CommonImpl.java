package com.esferalia.aon.occam.impl.jooq;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.ICommon;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainGserviceaccount;
import com.esferalia.aon.occam.api.model.DomainGserviceaccountFilter;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.esferalia.aon.occam.api.model.Filter.DomainFilter;
import com.esferalia.aon.occam.api.model.Filter.TagFilter;
import com.esferalia.aon.occam.api.model.Filter.TaxFilter;
import com.esferalia.aon.occam.api.model.FiscalParameters;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.WorkplaceFilter;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.product.Tax;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.impl.jooq.dao.AppParamDAO;
import com.esferalia.aon.occam.impl.jooq.dao.CompanyDAO;
import com.esferalia.aon.occam.impl.jooq.dao.ConfigurationDAO;
import com.esferalia.aon.occam.impl.jooq.dao.DomainDAO;
import com.esferalia.aon.occam.impl.jooq.dao.ProductDAO;
import com.esferalia.aon.occam.impl.jooq.dao.TagDAO;
import com.esferalia.aon.occam.impl.jooq.dao.TaxDAO;
import com.esferalia.aon.occam.impl.jooq.dao.WorkplaceDAO;

public class CommonImpl implements ICommon {

	// --------------------------- CONFIGURATION
	@Override
	public AonConfiguration getConfiguration(AONContext ctx, Date atDate) {
		return ConfigurationDAO.getConfiguration(ctx, atDate);
	}
	
	// ------------------ APPLICATION PARAMETERS
	@Override
	public ApplicationParameter fetchOne(AONContext ctx, AppParam param) {
		return AppParamDAO.fetchOne(ctx, param);
	}

	// ------------------ FISCAL PARAMETERS

	@Override
	public FiscalParameters getFiscalParameters(AONContext ctx) {
		return AppParamDAO.getFiscalParameters(ctx);
	}

	// ------------------ ENTERPRISE
	@Override
	public Enterprise getEnterprise(AONContext ctx, int id) {
		return CompanyDAO.getEnterprise(ctx, id);
	}

	@Override
	public LinkedList<Enterprise> getParentEnterprises(AONContext ctx, String query) {
		return CompanyDAO.getParentEnterprises(ctx, p -> (p.getDomainProperty()
				.eq(ctx.getDomainId()).or(p.getParentDomainProperty().eq(
				ctx.getDomainId()))).and(p.getNameProperty().like(query)
				.or(p.getAliasProperty().like(query))
				.or(p.getDocumentProperty().like(query))));
	}

	@Override
	public LinkedList<CompanyBank> getCompanyBanks(AONContext ctx, int enterprise) {
		return CompanyDAO.getBanks(ctx, enterprise);
	}

	@Override
	public LinkedList<CompanyBank> getCompanyBanks(AONContext ctx) {
		Company company = CompanyDAO.getCompany(ctx, ctx.getDomainId());
		return CompanyDAO.getBanks(ctx, company.getId());
	}

	@Override
	public Company getCompany(AONContext ctx, int domain) {
		return CompanyDAO.getCompany(ctx, domain);
	}

	// ------------------ WORKPLACE
	
	public Workplace getWorkplace(AONContext ctx, WorkplaceFilter filter) {
		return ctx.getDslContext().transactionResult(configuration -> 
			WorkplaceDAO.getWorkplace(ctx, filter));
	}
	
	public LinkedList<Workplace> getWorkplaceList(AONContext ctx, WorkplaceFilter filter) {
		return ctx.getDslContext().transactionResult(configuration -> 
			WorkplaceDAO.getWorkplaceList(ctx, filter));
	}
	
	// ------------------ PRODUCT
	@Override
	public List<String> getProductTags(AONContext ctx) {
		return ProductDAO.getProductTags(ctx);
	}

	@Override
	public Map<Integer, String[]> getProductTagMap(AONContext ctx) {
		return ProductDAO.getProductTagMap(ctx);
	}

	// ------------------ DOMAIN

	@Override
	public Domain insertDomain(AONContext ctx, Integer parentDomain,
			String document, String name, List<String> messages) {
		return DomainDAO.insertDomain(ctx, parentDomain, document, name, messages);

	}

	@Override
	public Domain getDomain(AONContext ctx, Integer domainId) {
		return DomainDAO.getDomain(ctx, domainId);
	}
	
	@Override
	public LinkedList<Domain> getDomainList(AONContext ctx, DomainFilter filter) {
		return ctx.getDslContext().transactionResult(
				configuration -> DomainDAO.getDomainList(ctx, filter));
	}

	@Override
	public DomainGserviceaccount getDomainGserviceaccount(AONContext ctx) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> DomainDAO.getDomainGserviceaccount(ctx));
	}
	
	@Override
	public HashMap<Integer,DomainGserviceaccount> getDomainGserviceaccountMap(AONContext ctx, Integer parent) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> DomainDAO.getDomainGserviceaccountMap(ctx, parent));
	}
	
	@Override
	public LinkedList<DomainGserviceaccount> getDomainGserviceaccountList(AONContext ctx) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> DomainDAO.getDomainGserviceaccountList(ctx));
	}
	
	@Override
	public DomainGserviceaccount getDomainGserviceaccount(AONContext ctx, DomainGserviceaccountFilter filter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> DomainDAO.getDomainGserviceaccount(ctx, filter));
	}
	
	@Override
	public void updateDomainGserviceaccount(AONContext ctx, String googleAccount) {
		ctx.getDslContext().transaction(
			configuration -> DomainDAO.updateDomainGserviceaccount(ctx, googleAccount));
	}
	
	@Override
	public void updateDomainGserviceaccount(AONContext ctx, DomainGserviceaccount dgsa) {
		ctx.getDslContext().transaction(
			configuration -> DomainDAO.updateDomainGserviceaccount(ctx, dgsa));
	}
	
	@Override
	public void deleteDomainGserviceaccount(AONContext ctx) {
		ctx.getDslContext().transaction(
			configuration -> DomainDAO.deleteDomainGserviceaccount(ctx));
	}
	@Override
	public void insertDomainGserviceaccount(AONContext ctx, DomainGserviceaccount dgsa){
		ctx.getDslContext().transaction(
			configuration -> DomainDAO.insertDomainGserviceaccount(ctx, dgsa));
	}
	
	@Override
	public Integer[] getSonsDomains(AONContext ctx){
		return ctx.getDslContext().transactionResult( 
				configuration -> DomainDAO.getSonsDomains(ctx));
	}
	
	// ------------------ TAG
	
	@Override
	public Tag getTag(AONContext ctx, Integer tagId){
		return ctx.getDslContext().transactionResult(
				configuration -> TagDAO.getTag(ctx, tagId));
	}
	
	@Override
	public Tag getTag(AONContext ctx, TagFilter filter){
		return ctx.getDslContext().transactionResult(
				configuration -> TagDAO.getTag(ctx, filter));
	}
	
	@Override
	public LinkedList<Tag> getTagList(AONContext ctx, TagFilter filter){
		return ctx.getDslContext().transactionResult(
				configuration -> TagDAO.getTagList(ctx, filter));
	}
	
	@Override
	public Stream<Tag> getTagStream(AONContext ctx, TagFilter filter){
		return ctx.getDslContext().transactionResult(
				configuration -> TagDAO.getTagStream(ctx, filter));
	}
	
	@Override
	public void updateTag(AONContext ctx, Tag tag){
		 ctx.getDslContext().transaction(configuration -> 
		 	TagDAO.updateTag(ctx, tag));
	}
	
	@Override
	public void deleteTag(AONContext ctx, Tag tag){
		 ctx.getDslContext().transaction(configuration -> 
		 	TagDAO.deleteTag(ctx, tag));
	}

	// ------------------ TAX
	
	@Override
	public Tax getTax(AONContext ctx, TaxFilter filter){
		return ctx.getDslContext().transactionResult(
				configuration -> TaxDAO.getTax(ctx, filter));
	}
}
